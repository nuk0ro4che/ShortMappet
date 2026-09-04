package mchorse.mappet.api.scripts;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.scripts.code.ScriptEvent;
import mchorse.mappet.api.scripts.code.ScriptFactory;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.manager.BaseManager;
import mchorse.mappet.utils.ScriptUtils;
import mchorse.mappet.utils.Utils;
import net.minecraft.class_124;
import net.minecraft.class_1308;
import net.minecraft.class_2487;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;

public class ScriptManager extends BaseManager<Script> {
   public final Map<String, Object> objects = new HashMap();
   protected Map<String, Script> uniqueScripts = new HashMap();
   public Map<String, Script> globalLibraries = new HashMap();
   private Map<Object, ScriptEngine> repls = new HashMap();
   private String replOutput = "";

   public ScriptManager(File folder) {
      super(folder);
      this.removeLegacyImportCaches(this.folder);
      ScriptUtils.getAllEngines();
   }

   private void removeLegacyImportCaches(File directory) {
      if (directory == null || !directory.isDirectory()) {
         return;
      }
      File[] files = directory.listFiles();
      if (files == null) {
         return;
      }
      for (File file : files) {
         if (file.isDirectory() && file.getName().equals(".imports")) {
            try {
               FileUtils.deleteDirectory(file);
            } catch (Exception ignored) {
            }
         } else if (file.isDirectory()) {
            this.removeLegacyImportCaches(file);
         }
      }
   }

   public String executeRepl(Object key, String code) throws ScriptException {
      ScriptEngine engine = (ScriptEngine)this.repls.get(key);
      this.replOutput = "";
      if (engine == null) {
         engine = ScriptUtils.sanitize(ScriptUtils.getEngineByExtension("js"));
         engine.put("____manager____", this);
         engine.put("mappet", new ScriptFactory());
         ScriptEvent event = new ScriptEvent(this.prepareContext(key), "", "");
         engine.put("c", event);
         engine.put("s", event.getSubject());
         engine.eval("var __p__ = print; print = function(message) { ____manager____.replPrint(message); __p__(message); };");
         this.repls.put(key, engine);
      }

      Object object = engine.eval(code);
      if (this.replOutput.isEmpty()) {
         this.replPrint(object);
      }

      return this.replOutput;
   }

   public Object eval(ScriptEngine engine, String code, DataContext context) throws ScriptException {
      ScriptEvent event = new ScriptEvent(context, "", "");
      engine.put("mappet", new ScriptFactory());
      engine.put("c", event);
      return engine.eval(code);
   }

   public DataContext prepareContext(Object key) {
      DataContext context;
      if (key instanceof class_3222) {
         context = new DataContext((class_3222)key);
      } else if (key instanceof MinecraftServer) {
         context = new DataContext((MinecraftServer)key);
      } else if (key instanceof class_1308) {
         context = new DataContext((class_1308)key);
      } else {
         MinecraftServer server = Mappet.server;
         context = new DataContext(server);
      }

      return context;
   }

   public void replPrint(Object object) {
      if (object == null) {
         object = String.valueOf(class_124.field_1080) + "undefined";
      }

      String var10001 = this.replOutput;
      this.replOutput = var10001 + object.toString() + "\n";
   }

   public Object execute(String id, String function, DataContext context) throws ScriptException, NoSuchMethodException {
      Script script = this.getScript(id);
      return script == null ? null : script.execute(function, context);
   }

   public Object execute(String id, String function, DataContext context, Object... args) throws ScriptException, NoSuchMethodException {
      Script script = this.getScript(id);
      return script == null ? null : script.execute(function, context, args);
   }

   public Object executeUIEvent(String id, String code, DataContext context, ScriptEvent event, Object component, Object uiContext, String componentId, int mouseX, int mouseY) throws ScriptException {
      Script script = this.getScript(id);
      return script == null ? null : script.executeUIEvent(code, context, event, component, uiContext, componentId, mouseX, mouseY);
   }

   private Script getScript(String id) throws ScriptException {
      Script script = (Script)this.uniqueScripts.get(id);
      if (script == null) {
         script = this.load(id);
         if (script != null && script.unique) {
            this.uniqueScripts.put(id, script);
         }

         if (script != null && script.globalLibrary) {
            this.globalLibraries.put(id, script);
         }
      }

      if (script == null) {
         return null;
      } else {
         script.start(this);
         return script;
      }
   }

   public Collection<String> getKeys() {
      if (this.folder == null) {
         return Collections.emptySet();
      } else {
         Set<String> set = new HashSet();
         this.recursiveFind(set, this.folder, "");

         for(File file : this.folder.listFiles()) {
            String name = file.getName();
            if (name.endsWith(".json") && file.isFile() && this.isData(file)) {
               set.add(name.replace(".json", ""));
            }
         }

         return set;
      }
   }

   protected Script createData(String id, class_2487 tag) {
      Script script = new Script();
      if (tag != null) {
         script.deserializeNBT(tag);
      }

      return script;
   }

   public Script load(String id) {
      Script script = (Script)super.load(id);
      File scriptFile = this.getScriptFile(id);
      if (scriptFile != null && scriptFile.isFile()) {
         try {
            String code = FileUtils.readFileToString(scriptFile, Utils.getCharset());
            if (script == null) {
               script = new Script();
            }

            script.code = code.replaceAll("\t", "    ").replaceAll("\r", "");
            script.lastLoadedAt = scriptFile.lastModified();
         } catch (Exception var5) {
         }
      }

      return script;
   }

   public boolean save(String id, class_2487 tag) {
      String code = new String(tag.method_10547("Code"), StandardCharsets.UTF_8);
      tag.method_10551("Code");
      boolean result = super.save(id, tag);
      if (!code.trim().isEmpty()) {
         try {
            FileUtils.writeStringToFile(this.getScriptFile(id), code, Utils.getCharset());
            result = true;
         } catch (Exception var6) {
         }
      }

      if (result) {
         this.uniqueScripts.remove(id);
         Script script = this.load(id);
         if (script != null && script.unique) {
            this.uniqueScripts.put(id, script);
         }

         if (script != null && script.globalLibrary) {
            this.globalLibraries.put(id, script);
         }
      }

      return result;
   }

   public boolean exists(String name) {
      File scriptFile = this.getScriptFile(name);
      return super.exists(name) || scriptFile != null && scriptFile.exists();
   }

   public boolean rename(String id, String newId) {
      File scriptFile = this.getScriptFile(id);
      File targetFile = this.getScriptFile(newId);
      if (targetFile != null && targetFile.getParentFile() != null) {
         targetFile.getParentFile().mkdirs();
      }
      boolean result = super.rename(id, newId);
      if (scriptFile != null && scriptFile.exists() && targetFile != null) {
         return scriptFile.renameTo(targetFile) || result;
      } else {
         return result;
      }
   }

   public boolean delete(String name) {
      boolean result = super.delete(name);
      File scriptFile = this.getScriptFile(name);
      return scriptFile != null && scriptFile.delete() || result;
   }

   protected boolean isData(File file) {
      return super.isData(file) || !file.getName().endsWith(".json");
   }

   public File getScriptFile(String id) {
      return this.folder == null ? null : new File(this.folder, id.lastIndexOf(".") != -1 ? id : id + ".js");
   }

   public void initiateAllScripts() {
      for(String id : this.getKeys()) {
         try {
            Script script = this.load(id);
            if (script != null && script.unique) {
               this.uniqueScripts.put(id, script);
               script.start(this);
            }

            if (script != null && script.globalLibrary) {
               this.globalLibraries.put(id, script);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

   }
}
