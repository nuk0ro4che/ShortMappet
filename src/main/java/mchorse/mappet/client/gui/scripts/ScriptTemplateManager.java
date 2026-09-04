package mchorse.mappet.client.gui.scripts;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.Mappet;

public final class ScriptTemplateManager {
   private ScriptTemplateManager() {
   }

   public static void loadCustomTemplates(Map<String, String> templates) {
      File root = CommonProxy.configFolder;
      if (root == null) {
         return;
      }

      File folder = new File(root, "scriptshabloni");
      if (!folder.exists() && !folder.mkdirs()) {
         return;
      }

      File[] files = folder.listFiles((file, name) -> name.toLowerCase().endsWith(".js"));
      if (files == null) {
         return;
      }

      for(File file : files) {
         try {
            String name = file.getName();
            name = name.substring(0, name.length() - 3);
            String template = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            if (name.trim().isEmpty() || template.trim().isEmpty()) {
               continue;
            }

            String displayName = name;
            int suffix = 2;

            while(templates.containsKey(displayName)) {
               displayName = name + " (custom " + suffix + ")";
               ++suffix;
            }

            templates.put(displayName, template);
         } catch (Exception error) {
            Mappet.LOGGER.warn("Couldn't load script template {}", file, error);
         }
      }
   }

   public static boolean saveTemplate(String name, String template) {
      File root = CommonProxy.configFolder;
      if (root == null || name == null || template == null || template.trim().isEmpty()) {
         return false;
      }

      name = name.trim().replaceAll("[^a-zA-Z0-9._-]", "_");
      if (name.isEmpty()) {
         return false;
      }

      if (!name.toLowerCase().endsWith(".js")) {
         name += ".js";
      }

      File folder = new File(root, "scriptshabloni");
      if (!folder.exists() && !folder.mkdirs()) {
         return false;
      }

      try {
         Files.writeString(new File(folder, name).toPath(), template, StandardCharsets.UTF_8);
         return true;
      } catch (Exception error) {
         Mappet.LOGGER.warn("Couldn't save script template {}", name, error);
         return false;
      }
   }
}