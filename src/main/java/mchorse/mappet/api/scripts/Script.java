package mchorse.mappet.api.scripts;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.scripts.code.ScriptEvent;
import mchorse.mappet.api.scripts.code.ScriptFactory;
import mchorse.mappet.api.utils.AbstractData;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.events.RegisterScriptVariablesEvent;
import mchorse.mappet.utils.ScriptUtils;
import mchorse.mappet.utils.Utils;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

public class Script extends AbstractData {
   public static final ThreadLocal<String> CURRENT_SCRIPT = new ThreadLocal();
   private static final String DEFAULT_IMPORT_REPOSITORY = "nuk0ro4che/myLib";
   private static final String DEFAULT_IMPORT_BRANCH = "main";
   private static final Pattern REMOTE_IMPORT = Pattern.compile("(?m)^\\s*import\\s+(?:([\\\"'])([^\\\"']+)\\1|([^;\\s]+))\\s*;?\\s*$");
   private static final Pattern FUNCTION_DECLARATION = Pattern.compile("(?m)^\\s*function\\s+([A-Za-z_$][\\w$]*)\\s*\\(([^)]*)\\)");
   private static final int MAX_REMOTE_LIBRARY_SIZE = 1024 * 1024;
   private static final Map<String, RemoteLibraryCacheEntry> REMOTE_LIBRARY_CACHE = new HashMap();
   private static class RemoteLibraryCacheEntry {
      private final String code;
      private final String etag;
      private final long lastModified;

      private RemoteLibraryCacheEntry(String code, String etag, long lastModified) {
         this.code = code;
         this.etag = etag;
         this.lastModified = lastModified;
      }
   }

   public String code = "";
   public boolean unique = true;
   public boolean globalLibrary = false;
   public boolean client = false;
   public List<String> libraries = new ArrayList();
   public long lastLoadedAt = 0;
   private ScriptEngine engine;
   private List<ScriptRange> ranges;
   private String DEFAULT_KOTLIN_IMPORTS = "import mchorse.metamorph.api.morphs.AbstractMorph\nimport mchorse.mappet.entities.EntityNpc\nimport net.minecraft.entity.effect.StatusEffect\nimport net.minecraft.entity.Entity\nimport net.minecraft.inventory.Inventory\nimport net.minecraft.server.network.ServerPlayerEntity\nimport net.minecraft.item.Item\nimport net.minecraft.item.ItemStack\nimport net.minecraft.block.entity.BlockEntity\nimport net.minecraft.block.BlockState\nimport net.minecraft.particle.ParticleType\nimport javax.vecmath.*\nimport java.lang.Math.*\nimport mchorse.mappet.api.scripts.user.*\nimport mchorse.mappet.api.scripts.user.blocks.*\nimport mchorse.mappet.api.scripts.user.data.*\nimport mchorse.mappet.api.scripts.user.entities.*\nimport mchorse.mappet.api.scripts.user.items.*\nimport mchorse.mappet.api.scripts.user.mappet.*\nimport mchorse.mappet.api.scripts.user.nbt.*\nimport mchorse.mappet.api.scripts.code.*\nimport mchorse.mappet.api.scripts.code.blocks.*\nimport mchorse.mappet.api.scripts.code.entities.*\nimport mchorse.mappet.api.scripts.code.items.*\nimport mchorse.mappet.api.scripts.code.mappet.*\nimport mchorse.mappet.api.scripts.code.nbt.*\n";

   public void start(ScriptManager manager) throws ScriptException {
      if (this.engine != null && this.hasRemoteImports() && this.refreshRemoteImports(manager)) {
         this.engine = null;
         this.ranges = null;
      }
      if (this.engine == null) {
         this.initializeEngine();
         this.configureEngineContext();
         this.registerScriptVariables();
         Set<String> uniqueImports = new HashSet();
         StringBuilder finalCode = new StringBuilder();
         Set<String> alreadyLoaded = new HashSet();
         int total = 0;
         boolean isKotlin = this.isKotlinEngine();
         List<String> allLibraries = new ArrayList();
         allLibraries.addAll(manager.globalLibraries.keySet());
         allLibraries.addAll(this.libraries);

         for(String library : allLibraries) {
            if (!this.shouldSkipLibrary(library, alreadyLoaded)) {
               total = this.processLibrary(manager, library, isKotlin, uniqueImports, finalCode, total);
               alreadyLoaded.add(library);
            }
         }

         total = this.processScriptCode(manager, isKotlin, uniqueImports, finalCode, alreadyLoaded, total);
         if (this.ranges != null) {
            this.ranges.add(new ScriptRange(total, this.getId()));
         }

         this.engine.put("mappet", new ScriptFactory());
         this.evalEngineCode(isKotlin, uniqueImports, finalCode);
      }

   }

   private void initializeEngine() throws ScriptException {
      String extension = this.getScriptExtension();
      if (extension.equals("kts")) {
         System.setProperty("kotlin.jsr223.experimental.resolve.dependencies.from.context.classloader", "true");
      }

      this.engine = ScriptUtils.getEngineByExtension(extension);
      if (this.engine == null) {
         String message = "Looks like Mappet can't find script engine for a \"" + this.getScriptExtension() + "\" file extension.";
         throw new ScriptException(message, this.getId(), -1);
      } else {
         this.engine = ScriptUtils.sanitize(this.engine);
      }
   }

   private void configureEngineContext() {
      String extension = this.getScriptExtension();
      if (extension.equals("js")) {
         NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
         this.engine = factory.getScriptEngine(new String[]{"--language=es6", "-scripting"});
      }

      this.engine.getContext().setAttribute("javax.script.filename", this.getId(), 100);
      this.engine.getContext().setAttribute("polyglot.js.allowHostAccess", true, 100);
   }

   private void registerScriptVariables() {
      Mappet.EVENT_BUS.post(new RegisterScriptVariablesEvent(this.engine));
   }

   private boolean isKotlinEngine() {
      return this.engine.getFactory().getLanguageName().equals("kotlin");
   }

   private boolean shouldSkipLibrary(String library, Set<String> alreadyLoaded) {
      return library.equals(this.getId()) || alreadyLoaded.contains(library);
   }

   private int processLibrary(ScriptManager manager, String library, boolean isKotlin, Set<String> uniqueImports, StringBuilder finalCode, int total) {
      try {
         File scriptFile = manager.getScriptFile(library);
         String code = FileUtils.readFileToString(scriptFile, Utils.getCharset());
         Script libraryScript = manager.load(library);
         boolean stubClientLibrary = libraryScript != null && libraryScript.client && !this.client && !isKotlin;
         if (stubClientLibrary) {
            finalCode.append(this.buildClientLibraryStubs(code));
            if (this.ranges == null) {
               this.ranges = new ArrayList();
            }

            this.ranges.add(new ScriptRange(total, library));
            total += StringUtils.countMatches(code, "\n") + 1;
            return total;
         } else if (isKotlin) {
            code = this.processKotlinCode(code, uniqueImports);
         }

         finalCode.append(code);
         finalCode.append("\n");
         if (this.ranges == null) {
            this.ranges = new ArrayList();
         }

         this.ranges.add(new ScriptRange(total, library));
         total += StringUtils.countMatches(code, "\n") + 1;
      } catch (Exception e) {
         System.err.println("[Mappet] Script library " + library + ".js failed to load...");
         e.printStackTrace();
      }

      return total;
   }

   private String processKotlinCode(String code, Set<String> uniqueImports) {
      String[] lines = code.split("\n");
      StringBuilder currentCode = new StringBuilder();

      for(String line : lines) {
         if (line.trim().startsWith("import")) {
            uniqueImports.add(line.trim());
         } else {
            currentCode.append(line);
            currentCode.append("\n");
         }
      }

      return currentCode.toString();
   }

   private String buildClientLibraryStubs(String code) {
      StringBuilder stubs = new StringBuilder();
      if (code == null || code.trim().isEmpty()) {
         return stubs.toString();
      }

      Matcher matcher = FUNCTION_DECLARATION.matcher(code);
      while (matcher.find()) {
         String name = matcher.group(1);
         List<String> params = this.getFunctionParams(matcher.group(2));
         if (params.isEmpty()) {
            continue;
         }

         stubs.append("function ").append(name).append("(__arg0");
         for(int i = 1; i < params.size(); i++) {
            stubs.append(", __arg").append(i);
         }

         stubs.append(") {\n    __arg0.getPlayer().executeClientScript(function(){ ").append(name).append("(c");
         for(int i = 1; i < params.size(); i++) {
            stubs.append(", arguments[").append(i - 1).append("]");
         }

         stubs.append("); }");
         for(int i = 1; i < params.size(); i++) {
            stubs.append(", __arg").append(i);
         }

         stubs.append(");\n}\n");
      }

      return stubs.toString();
   }

   private List<String> getFunctionParams(String raw) {
      List<String> params = new ArrayList<>();
      if (raw == null) {
         return params;
      }

      for(String param : raw.split(",")) {
         String name = param.trim();
         int equal = name.indexOf('=');
         if (equal >= 0) {
            name = name.substring(0, equal).trim();
         }

         if (!name.isEmpty() && name.matches("[A-Za-z_$][\\w$]*")) {
            params.add(name);
         }
      }

      return params;
   }

   private int processScriptCode(ScriptManager manager, boolean isKotlin, Set<String> uniqueImports, StringBuilder finalCode, Set<String> alreadyLoaded, int total) throws ScriptException {
      if (isKotlin) {
         String processedCode = this.processKotlinCode(this.code, uniqueImports);
         finalCode.insert(0, processedCode);
         return total;
      }

      return this.processRemoteImports(manager, this.code, finalCode, alreadyLoaded, total);
   }

   private int processRemoteImports(ScriptManager manager, String source, StringBuilder finalCode, Set<String> alreadyLoaded, int total) throws ScriptException {
      Matcher imports = REMOTE_IMPORT.matcher(source);
      StringBuffer cleaned = new StringBuffer();
      while (imports.find()) {
         String importName = imports.group(2) != null ? imports.group(2) : imports.group(3);
         String url = this.resolveRemoteImport(importName);
         if (url == null) {
            throw new ScriptException("Invalid Mappet import: " + importName, this.getId(), -1);
         }
         total = this.appendRemoteLibrary(manager, importName, url, finalCode, alreadyLoaded, total);
         imports.appendReplacement(cleaned, "");
      }
      imports.appendTail(cleaned);
      finalCode.append(cleaned.toString());
      return total;
   }

   private int appendRemoteLibrary(ScriptManager manager, String importName, String url, StringBuilder finalCode, Set<String> alreadyLoaded, int total) throws ScriptException {
      String cacheKey = "remote:" + url;
      if (!alreadyLoaded.add(cacheKey)) {
         return total;
      }

      String source = this.loadRemoteLibrary(manager, url, true);
      int libraryStart = total;
      total = this.processRemoteImports(manager, source, finalCode, alreadyLoaded, total);
      if (this.ranges == null) {
         this.ranges = new ArrayList();
      }
      this.ranges.add(new ScriptRange(libraryStart, importName));
      return total;
   }

   private String resolveRemoteImport(String importName) {
      if (importName == null || importName.isEmpty()) {
         return null;
      }
      if (importName.startsWith("http://") || importName.startsWith("https://")) {
         return importName;
      }
      if (importName.startsWith("github:")) {
         String value = importName.substring("github:".length());
         int firstColon = value.indexOf(':');
         int lastColon = value.lastIndexOf(':');
         if (firstColon <= 0 || lastColon <= firstColon || lastColon >= value.length() - 1) {
            return null;
         }
         String user = value.substring(0, firstColon);
         String repositoryAndFolder = value.substring(firstColon + 1, lastColon);
         String file = value.substring(lastColon + 1);
         if (repositoryAndFolder.isEmpty() || file.isEmpty()) {
            return null;
         }
         String[] path = repositoryAndFolder.split("/", -1);
         String repository = path[0];
         StringBuilder filePath = new StringBuilder();
         for (int i = 1; i < path.length; ++i) {
            if (!path[i].isEmpty()) {
               if (filePath.length() > 0) filePath.append('/');
               filePath.append(path[i]);
            }
         }
         if (filePath.length() > 0) filePath.append('/');
         filePath.append(file);
         return "https://raw.githubusercontent.com/" + user + "/" + repository + "/main/" + filePath;
      }
      if (importName.contains("/") || importName.contains("\\")) {
         return null;
      }
      return "https://raw.githubusercontent.com/" + DEFAULT_IMPORT_REPOSITORY + "/" + DEFAULT_IMPORT_BRANCH + "/" + importName;
   }

   private boolean hasRemoteImports() {
      return REMOTE_IMPORT.matcher(this.code == null ? "" : this.code).find();
   }

   private boolean refreshRemoteImports(ScriptManager manager) {
      Matcher imports = REMOTE_IMPORT.matcher(this.code == null ? "" : this.code);
      boolean changed = false;
      Set<String> checked = new HashSet();
      while (imports.find()) {
         String importName = imports.group(2) != null ? imports.group(2) : imports.group(3);
         String url = this.resolveRemoteImport(importName);
         if (url == null || !checked.add(url)) continue;
         try {
            String previous;
            synchronized (REMOTE_LIBRARY_CACHE) {
               RemoteLibraryCacheEntry cached = REMOTE_LIBRARY_CACHE.get(url);
               previous = cached == null ? null : cached.code;
            }
            String latest = this.loadRemoteLibrary(manager, url, true);
            if (previous != null && latest != null && !previous.equals(latest)) changed = true;
         } catch (ScriptException ignored) {
         }
      }
      return changed;
   }

   private String loadRemoteLibrary(ScriptManager manager, String url) throws ScriptException {
      return this.loadRemoteLibrary(manager, url, false);
   }

   private String loadRemoteLibrary(ScriptManager manager, String url, boolean forceRemote) throws ScriptException {
      RemoteLibraryCacheEntry cached;
      synchronized (REMOTE_LIBRARY_CACHE) {
         cached = REMOTE_LIBRARY_CACHE.get(url);
         if (!forceRemote && cached != null) return cached.code;
      }

      HttpURLConnection connection = null;
      try {
         String freshUrl = url + (url.contains("?") ? "&" : "?") + "mappet_cache=" + System.currentTimeMillis();
         connection = (HttpURLConnection)new URL(freshUrl).openConnection();
         connection.setConnectTimeout(5000);
         connection.setReadTimeout(8000);
         connection.setUseCaches(false);
         connection.setRequestProperty("Cache-Control", "no-cache, no-store");
         connection.setRequestProperty("Pragma", "no-cache");
         connection.setRequestProperty("User-Agent", "Mappet-Import/1.0");

         int status = connection.getResponseCode();
         if (status < 200 || status >= 300) {
            throw new ScriptException("Cannot import " + url + " (HTTP " + status + ")");
         }
         try (InputStream input = new BufferedInputStream(connection.getInputStream()); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int total = 0;
            int read;
            while ((read = input.read(buffer)) >= 0) {
               total += read;
               if (total > MAX_REMOTE_LIBRARY_SIZE) throw new ScriptException("Imported library is larger than 1 MB: " + url);
               output.write(buffer, 0, read);
            }
            String source = new String(output.toByteArray(), StandardCharsets.UTF_8);
            synchronized (REMOTE_LIBRARY_CACHE) {
               REMOTE_LIBRARY_CACHE.put(url, new RemoteLibraryCacheEntry(source, connection.getHeaderField("ETag"), connection.getLastModified()));
            }
            return source;
         }
      } catch (ScriptException e) {
         throw e;
      } catch (Exception e) {
         throw new ScriptException("Cannot import " + url + ": " + e.getMessage());
      } finally {
         if (connection != null) connection.disconnect();
      }
   }


   private void evalEngineCode(boolean isKotlin, Set<String> uniqueImports, StringBuilder finalCode) throws ScriptException {
      if (isKotlin) {
         String var10000 = this.DEFAULT_KOTLIN_IMPORTS;
         String allCode = var10000 + "\n" + String.join("\n", uniqueImports) + "\n" + finalCode.toString();
         this.engine.eval(allCode);
      } else {
         this.engine.eval(finalCode.toString());
      }

   }

   public String getScriptExtension() {
      String id = this.getId();
      int index = id.lastIndexOf(46);
      return index >= 0 ? id.substring(index + 1) : "js";
   }

   public Object execute(String function, DataContext context, Object... args) throws ScriptException, NoSuchMethodException {
      if (function.isEmpty()) {
         function = "main";
      }

      this.engine.put("context", context);
      String previousScript = (String)CURRENT_SCRIPT.get();
      CURRENT_SCRIPT.set(this.getId());

      try {
         return ((Invocable)this.engine).invokeFunction(function, args);
      } catch (ScriptException e) {
         ScriptException exception = this.processScriptException(e);
         Mappet.logger.error(e.getMessage());
         throw exception == null ? e : exception;
      } finally {
         if (previousScript == null) {
            CURRENT_SCRIPT.remove();
         } else {
            CURRENT_SCRIPT.set(previousScript);
         }
      }
   }

   public Object execute(String function, DataContext context) throws ScriptException, NoSuchMethodException {
      return this.execute(function, context, new ScriptEvent(context, this.getId(), function));
   }

   




   public Object executeUIEvent(String code, DataContext dataContext, ScriptEvent event, Object component, Object uiContext, String id, int mouseX, int mouseY) throws ScriptException {
      if (code == null || code.trim().isEmpty()) {
         return null;
      }

      this.engine.put("context", dataContext);
      this.engine.put("__mappetUIEventC", event);
      this.engine.put("__mappetUIEventComponent", component);
      this.engine.put("__mappetUIEventContext", uiContext);
      this.engine.put("__mappetUIEventId", id);
      this.engine.put("__mappetUIEventMouseX", mouseX);
      this.engine.put("__mappetUIEventMouseY", mouseY);
      String previousScript = (String)CURRENT_SCRIPT.get();
      CURRENT_SCRIPT.set(this.getId());

      try {
         return this.engine.eval("(function(c, component, context, id, mouseX, mouseY) {\n" + code + "\n})(__mappetUIEventC, __mappetUIEventComponent, __mappetUIEventContext, __mappetUIEventId, __mappetUIEventMouseX, __mappetUIEventMouseY);");
      } catch (ScriptException exception) {
         ScriptException processed = this.processScriptException(exception);
         Mappet.logger.error(exception.getMessage());
         throw processed == null ? exception : processed;
      } finally {
         if (previousScript == null) {
            CURRENT_SCRIPT.remove();
         } else {
            CURRENT_SCRIPT.set(previousScript);
         }
      }
   }

   private ScriptException processScriptException(ScriptException e) {
      if (this.ranges == null) {
         return null;
      } else {
         ScriptRange range = null;

         for(int i = this.ranges.size() - 1; i >= 0; --i) {
            ScriptRange possibleRange = (ScriptRange)this.ranges.get(i);
            if (possibleRange.lineOffset <= e.getLineNumber() - 1) {
               range = possibleRange;
               break;
            }
         }

         if (range != null) {
            String message = e.getMessage();
            int lineNumber = e.getLineNumber() - range.lineOffset;
            String var10001 = this.getId();
            String var10002 = range.script;
            message = message.replaceFirst(var10001, var10002 + " (in " + this.getId() + ")");
            message = message.replaceFirst("at line number [\\d]+", "at line number " + lineNumber);
            return new ScriptException(message, range.script, lineNumber, e.getColumnNumber());
         } else {
            return null;
         }
      }
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      class_2499 libraries = new class_2499();

      for(String library : this.libraries) {
         libraries.add(class_2519.method_23256(library));
      }

      tag.method_10556("Unique", this.unique);
      tag.method_10556("GlobalLibrary", this.globalLibrary);
      tag.method_10556("Client", this.client);
      tag.method_10566("Libraries", libraries);
      tag.method_10570("Code", this.code.getBytes(StandardCharsets.UTF_8));
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      this.unique = tag.method_10577("Unique");
      if (tag.method_10573("Libraries", 9)) {
         class_2499 libraries = tag.method_10554("Libraries", 8);
         this.libraries.clear();
         int i = 0;

         for(int c = libraries.size(); i < c; ++i) {
            this.libraries.add(libraries.method_10608(i));
         }
      }

      if (tag.method_10545("GlobalLibrary")) {
         this.globalLibrary = tag.method_10577("GlobalLibrary");
      }

      if (tag.method_10545("Client")) {
         this.client = tag.method_10577("Client");
      }

      this.code = new String(tag.method_10547("Code"), StandardCharsets.UTF_8);
   }
}
