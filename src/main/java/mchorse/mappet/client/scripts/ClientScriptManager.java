package mchorse.mappet.client.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import mchorse.mappet.api.scripts.Script;
import mchorse.mappet.api.scripts.ScriptManager;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.utils.ScriptUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;





@Environment(EnvType.CLIENT)
public class ClientScriptManager extends ScriptManager {
   private long lastCacheCheck = 0;

   public ClientScriptManager(File folder) {
      super(folder);
   }

   
   public void receiveScripts(Map<String, net.minecraft.class_2487> scripts) {
      if (scripts == null) {
         return;
      }

      Set<String> incoming = new HashSet(scripts.keySet());
      for (String id : new ArrayList<String>(this.getKeys())) {
         if (id.endsWith("/") || incoming.contains(id)) {
            continue;
         }
         this.delete(id);
      }

      for (Map.Entry<String, net.minecraft.class_2487> entry : scripts.entrySet()) {
         String id = entry.getKey();
         net.minecraft.class_2487 tag = entry.getValue();
         if (id != null && !id.isEmpty() && tag != null) {
            this.save(id, (net.minecraft.class_2487)tag.method_10707());
         }
      }
   }

   public Object executeClient(String id, String function) throws ScriptException, NoSuchMethodException {
      return this.executeClient(id, function, (Object[])null);
   }

   public Object executeClient(String id, String function, Object... args) throws ScriptException, NoSuchMethodException {
      class_310 client = class_310.method_1551();
      if (client.field_1724 == null) {
         throw new ScriptException("Клиентский скрипт нельзя запустить до входа в мир.");
      }

      this.refreshGlobalLibraries();
      return args == null || args.length == 0 ? super.execute(id, function == null || function.isEmpty() ? "main" : function, DataContext.client(client.field_1724)) : super.execute(id, function == null || function.isEmpty() ? "main" : function, DataContext.client(client.field_1724), args);
   }

   public Object executeInline(String code, Object... args) throws ScriptException {
      class_310 client = class_310.method_1551();
      if (client.field_1724 == null) {
         throw new ScriptException("Клиентский скрипт нельзя запустить до входа в мир.");
      }

      this.refreshGlobalLibraries();
      String trimmed = code == null ? "" : code.trim();
      if (trimmed.isEmpty()) {
         return null;
      }

      DataContext context = DataContext.client(client.field_1724);
      ScriptEngine engine = ScriptUtils.sanitize(new NashornScriptEngineFactory().getScriptEngine(new String[]{"--language=es6", "-scripting"}));
      engine.put("context", context);

      StringBuilder source = new StringBuilder();
      for (String id : this.getKeys()) {
         if (id.endsWith("/")) {
            continue;
         }

         Script library = this.load(id);
         if (library != null && library.client && library.code != null && !library.code.trim().isEmpty()) {
            source.append(library.code).append("\n");
         }
      }

      if (trimmed.startsWith("function") || trimmed.startsWith("(")) {
         source.append("(").append(trimmed).append(")");
         Object result = this.eval(engine, source.toString(), context);
         if (result instanceof ScriptObjectMirror) {
            ScriptObjectMirror mirror = (ScriptObjectMirror)result;
            if (mirror.isFunction()) {
               return mirror.call(null, args == null ? new Object[0] : args);
            }
         }
         return result;
      }

      source.append(trimmed);
      return this.eval(engine, source.toString(), context);
   }

   




   public void checkAndInvalidateCache() {
      long now = System.currentTimeMillis();
      if (now - this.lastCacheCheck < 5000) {
         return;
      }
      this.lastCacheCheck = now;

      for (String id : this.getKeys()) {
         if (id.endsWith("/")) {
            continue;
         }
         File scriptFile = this.getScriptFile(id);
         if (scriptFile != null && scriptFile.isFile()) {
            Script cached = this.uniqueScripts.get(id);
            if (cached != null && scriptFile.lastModified() > cached.lastLoadedAt) {
               this.uniqueScripts.remove(id);
            }
         }
      }
   }

   private void refreshGlobalLibraries() {
      this.globalLibraries.clear();
      for (String id : this.getKeys()) {
         if (id.endsWith("/")) {
            continue;
         }
         Script script = this.load(id);
         if (script != null && script.globalLibrary) {
            this.globalLibraries.put(id, script);
         }
      }
   }
}
