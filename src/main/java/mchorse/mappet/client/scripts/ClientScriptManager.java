package mchorse.mappet.client.scripts;

import java.io.File;
import java.util.Map;
import javax.script.ScriptException;
import mchorse.mappet.api.scripts.Script;
import mchorse.mappet.api.scripts.ScriptManager;
import mchorse.mappet.api.utils.DataContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;





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
      for (Map.Entry<String, net.minecraft.class_2487> entry : scripts.entrySet()) {
         String id = entry.getKey();
         net.minecraft.class_2487 tag = entry.getValue();
         if (id != null && !id.isEmpty() && tag != null) {
            this.save(id, (net.minecraft.class_2487)tag.method_10707());
         }
      }
   }

   public Object executeClient(String id, String function) throws ScriptException, NoSuchMethodException {
      class_310 client = class_310.method_1551();
      if (client.field_1724 == null) {
         throw new ScriptException("Клиентский скрипт нельзя запустить до входа в мир.");
      }

      this.refreshGlobalLibraries();
      return super.execute(id, function == null || function.isEmpty() ? "main" : function, DataContext.client(client.field_1724));
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
