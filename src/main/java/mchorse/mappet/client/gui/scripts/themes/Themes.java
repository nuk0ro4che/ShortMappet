package mchorse.mappet.client.gui.scripts.themes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.client.gui.scripts.utils.SyntaxStyle;
import mchorse.mappet.utils.NBTToJsonLike;
import mchorse.mclib.client.gui.utils.GuiUtils;
import net.fabricmc.loader.api.FabricLoader;

public class Themes {
   private static File editorThemes;

   public static void open() {
      initiate();
      GuiUtils.openWebLink(editorThemes.toURI());
   }

   public static List<File> themes() {
      initiate();
      List<File> themes = new ArrayList();
      File[] files = editorThemes.listFiles();
      if (files != null) {
         for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".json")) {
               themes.add(file);
            }
         }
      }

      return themes;
   }

   public static File themeFile(String name) {
      initiate();
      if (!name.endsWith(".json")) {
         name = name + ".json";
      }

      return new File(editorThemes, name);
   }

   public static SyntaxStyle readTheme(File file) {
      try {
         return new SyntaxStyle(NBTToJsonLike.read(file));
      } catch (Exception var2) {
         return null;
      }
   }

   public static void writeTheme(File file, SyntaxStyle style) {
      try {
         NBTToJsonLike.write(file, style.toNBT());
      } catch (Exception var3) {
      }

   }

   public static void initiate() {
      if (editorThemes == null) {
         File configFolder = CommonProxy.configFolder;
         if (configFolder == null) {
            configFolder = FabricLoader.getInstance().getConfigDir().resolve("mappet").toFile();
            configFolder.mkdirs();
         }

         editorThemes = new File(configFolder, "themes");
         editorThemes.mkdirs();
         File nukoroche = new File(editorThemes, "themakoroche.json");
         File oldtheme = new File(editorThemes, "k0ro4che.json"); 

         if(oldtheme.exists()){
            oldtheme.delete();
         }

         if (!nukoroche.isFile()) {
            SyntaxStyle thema = new SyntaxStyle();
            writeTheme(nukoroche, thema);
         }
      }
   }
}
