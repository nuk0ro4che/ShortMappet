package mchorse.mappet.utils.husk;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

/**
 * JSR-223 factory that makes Mappet pick Husk for ".hk" script files (selected by extension in
 * ScriptUtils.getEngineByExtension). The engine itself is HuskScriptEngine, which compiles Husk
 * to JavaScript with the external huskc compiler and evaluates the result on Nashorn, the same
 * engine Mappet uses for plain ".js" scripts.
 */
public class HuskScriptEngineFactory implements ScriptEngineFactory {
   private static final List<String> NAMES = Arrays.asList("husk", "hk");
   private static final List<String> EXTENSIONS = Collections.singletonList("hk");
   private static final List<String> MIME_TYPES = Collections.singletonList("text/x-husk");
   private static final List<String> SYNTAXES = Collections.singletonList("rust");

   @Override
   public String getEngineName() {
      return "Husk";
   }

   @Override
   public String getEngineVersion() {
      return "0.1";
   }

   @Override
   public List<String> getExtensions() {
      return EXTENSIONS;
   }

   @Override
   public List<String> getMimeTypes() {
      return MIME_TYPES;
   }

   @Override
   public List<String> getNames() {
      return NAMES;
   }

   @Override
   public String getLanguageName() {
      return "husk";
   }

   @Override
   public String getLanguageVersion() {
      return "0.1";
   }

   @Override
   public Object getParameter(String key) {
      if (ScriptEngine.NAME.equals(key)) {
         return this.getLanguageName();
      }

      if (ScriptEngine.ENGINE.equals(key)) {
         return this.getEngineName();
      }

      if (ScriptEngine.ENGINE_VERSION.equals(key)) {
         return this.getEngineVersion();
      }

      if (ScriptEngine.LANGUAGE.equals(key)) {
         return this.getLanguageName();
      }

      if (ScriptEngine.LANGUAGE_VERSION.equals(key)) {
         return this.getLanguageVersion();
      }

      if ("THREADING".equals(key)) {
         return "UNKNOWN";
      }

      return null;
   }

   @Override
   public String getMethodCallSyntax(String obj, String method, String... args) {
      StringBuilder call = new StringBuilder(obj).append('.').append(method).append('(');

      for (int i = 0; i < args.length; i++) {
         if (i > 0) {
            call.append(", ");
         }

         call.append(args[i]);
      }

      return call.append(')').toString();
   }

   @Override
   public String getOutputStatement(String toDisplay) {
      return "print(" + toDisplay.replace("\\", "\\\\").replace("\"", "\\\"") + ")";
   }

   @Override
   public String getProgram(String... statements) {
      return String.join(";\n", statements);
   }

   @Override
   public ScriptEngine getScriptEngine() {
      return new HuskScriptEngine(this);
   }
}
