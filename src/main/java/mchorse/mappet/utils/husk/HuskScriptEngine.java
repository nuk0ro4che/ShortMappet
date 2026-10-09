package mchorse.mappet.utils.husk;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import mchorse.mappet.Mappet;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

/**
 * Script engine for Husk ".hk" scripts: every eval first compiles the source to JavaScript with
 * the external huskc compiler (library mode, so main() is never auto-called) and runs the result
 * on Nashorn with es6 enabled, shimming the module/console globals Husk's runtime expects.
 *
 * <p>When huskc is missing or rejects the input, the source is evaluated as plain JavaScript
 * instead. That keeps raw JS snippets (UI event handlers) working on the same engine, lets
 * ScriptUtils.initiateScriptEngines() probe the engine with "true", and turns a broken .hk
 * script into an error that carries huskc's diagnostics.
 */
public class HuskScriptEngine extends AbstractScriptEngine implements Invocable {
   private static final String[] BINARY_NAMES = {"huskc", "huskc.exe"};

   private static final String SHIMS =
      "var module = { exports: {} };\n" +
      "var console = {\n" +
      "   log: function() { print(__husk_console_line(arguments)); },\n" +
      "   info: function() { print(__husk_console_line(arguments)); },\n" +
      "   warn: function() { print(__husk_console_line(arguments)); },\n" +
      "   error: function() { print(__husk_console_line(arguments)); }\n" +
      "};\n" +
      "function __husk_console_line(args) {\n" +
      "   var parts = [];\n" +
      "   for (var i = 0; i < args.length; i++) parts.push(String(args[i]));\n" +
      "   return parts.join(\" \");\n" +
      "}\n";

   private static final long COMPILE_TIMEOUT_MS = 20000L;
   private static boolean logged;
   private static boolean warned;

   private final HuskScriptEngineFactory factory;
   private final ScriptEngine nashorn;

   public HuskScriptEngine(HuskScriptEngineFactory factory) {
      this.factory = factory;
      this.nashorn = new NashornScriptEngineFactory().getScriptEngine(new String[]{"--language=es6", "-scripting"});

      /* AbstractScriptEngine keeps its own ScriptContext field that eval/put/get use; share Nashorn's
         context with it, otherwise functions land in one scope and invokeFunction looks in another */
      this.setContext(this.nashorn.getContext());
   }

   @Override
   public Object eval(String code, ScriptContext context) throws ScriptException {
      Compiled compiled = this.compile(code);

      if (compiled.javascript != null) {
         return this.nashorn.eval(SHIMS + compiled.javascript, context);
      }

      try {
         return this.nashorn.eval(code, context);
      } catch (ScriptException e) {
         throw this.compileFailure(compiled, e);
      }
   }

   @Override
   public Object eval(Reader reader, ScriptContext context) throws ScriptException {
      StringBuilder code = new StringBuilder();
      char[] buffer = new char[8192];

      try {
         int read = reader.read(buffer);

         while (read != -1) {
            code.append(buffer, 0, read);
            read = reader.read(buffer);
         }
      } catch (IOException e) {
         throw new ScriptException(e);
      }

      return this.eval(code.toString(), context);
   }

   @Override
   public void setContext(ScriptContext context) {
      super.setContext(context);

      if (this.nashorn != null) {
         this.nashorn.setContext(context);
      }
   }

   @Override
   public Bindings createBindings() {
      return this.nashorn.createBindings();
   }

   @Override
   public HuskScriptEngineFactory getFactory() {
      return this.factory;
   }

   @Override
   public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
      return ((Invocable) this.nashorn).invokeFunction(name, args);
   }

   @Override
   public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
      return ((Invocable) this.nashorn).invokeMethod(thiz, name, args);
   }

   @Override
   public <T> T getInterface(Class<T> clz) {
      return ((Invocable) this.nashorn).getInterface(clz);
   }

   @Override
   public <T> T getInterface(Object thiz, Class<T> clz) {
      return ((Invocable) this.nashorn).getInterface(thiz, clz);
   }

   private ScriptException compileFailure(Compiled compiled, ScriptException jsError) {
      StringBuilder message = new StringBuilder("Husk script failed");

      if (compiled.missing) {
         message.append(" to compile: huskc compiler not found (install Rust and run \"cargo install husk-lang\", or pass -Dmappet.huskc=<path>)");
      } else if (compiled.error != null && !compiled.error.isEmpty()) {
         message.append(" to compile:\n").append(compiled.error);
      } else {
         message.append(" to compile: ").append(compiled.error == null ? "no output from huskc" : compiled.error);
      }

      message.append("\nThe source is not valid JavaScript either: ").append(jsError.getMessage());

      return new ScriptException(message.toString());
   }

   private Compiled compile(String code) {
      String binary = this.findHuskc();

      if (binary == null) {
         if (!warned) {
            warned = true;
            Mappet.LOGGER.warn("[Husk] huskc compiler not found, .hk scripts can't compile (install Rust and run \"cargo install husk-lang\", or pass -Dmappet.huskc=<path>)");
         }

         return new Compiled(null, null, true);
      }

      File source = null;
      File output = null;
      File errors = null;

      try {
         source = File.createTempFile("mappet-husk", ".hk");
         output = File.createTempFile("mappet-husk", ".js");
         errors = File.createTempFile("mappet-husk", ".log");

         Files.write(source.toPath(), code.getBytes(StandardCharsets.UTF_8));

         Process process = new ProcessBuilder(binary, "compile", "--lib", source.getAbsolutePath())
            .redirectOutput(output)
            .redirectError(errors)
            .start();

         if (!process.waitFor(COMPILE_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
            process.destroyForcibly();

            return new Compiled(null, "huskc timed out after " + COMPILE_TIMEOUT_MS / 1000L + " seconds", false);
         }

         if (process.exitValue() != 0) {
            return new Compiled(null, Files.readString(errors.toPath(), StandardCharsets.UTF_8).trim(), false);
         }

         String javascript = stripBigIntLiterals(Files.readString(output.toPath(), StandardCharsets.UTF_8));

         if (!logged) {
            logged = true;
            Mappet.LOGGER.info("[Husk] engine ready, compiling .hk scripts with {}", binary);
         }

         return new Compiled(javascript, null, false);
      } catch (IOException e) {
         return new Compiled(null, "can't run huskc: " + e.getMessage(), false);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();

         return new Compiled(null, "interrupted while waiting for huskc", false);
      } finally {
         this.delete(source);
         this.delete(output);
         this.delete(errors);
      }
   }

   private String findHuskc() {
      String property = System.getProperty("mappet.huskc");

      if (property != null && !property.isEmpty()) {
         return property;
      }

      String home = System.getProperty("user.home");

      if (home != null) {
         for (String name : BINARY_NAMES) {
            File file = new File(new File(home, ".cargo/bin"), name);

            if (file.isFile()) {
               return file.getAbsolutePath();
            }
         }
      }

      String path = System.getenv("PATH");

      if (path != null) {
         for (String directory : path.split(File.pathSeparator)) {
            if (directory.isEmpty()) {
               continue;
            }

            for (String name : BINARY_NAMES) {
               File file = new File(directory, name);

               if (file.isFile()) {
                  return file.getAbsolutePath();
               }
            }
         }
      }

      return null;
   }

   /**
    * Husk's i32 range check emits BigInt literals (like -2147483648n) that Nashorn can't parse.
    * The values stay exact as plain doubles for any i32 magnitude, so dropping the "n" suffix
    * keeps the semantics and makes the output parseable.
    */
   private static String stripBigIntLiterals(String javascript) {
      return javascript.replaceAll("(?<![\\w.$])(-?\\d+)n\\b", "$1");
   }

   private void delete(File file) {
      if (file != null && !file.delete()) {
         file.deleteOnExit();
      }
   }

   private static final class Compiled {
      private final String javascript;
      private final String error;
      private final boolean missing;

      private Compiled(String javascript, String error, boolean missing) {
         this.javascript = javascript;
         this.error = error;
         this.missing = missing;
      }
   }
}
