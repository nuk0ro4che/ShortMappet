package mchorse.mappet.client.gui.scripts.utils.documentation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import mchorse.mappet.ClientProxy;
import mchorse.mappet.client.gui.scripts.GuiDocumentationOverlayPanel;
import net.minecraft.class_310;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class DocMerger {
   public static Docs getMergedDocs() {
      class_310 mc = class_310.method_1551();
      String language = mc.method_1526().method_4669();
      Gson gson = (new GsonBuilder()).create();
      List<Docs> docsList = new ArrayList();
      File docsFolder = new File(ClientProxy.configFolder.getPath(), "documentation");
      docsFolder.mkdirs();
      addAddonsDocs(gson, docsList);
      List<File> files = (List)FileUtils.listFiles(docsFolder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).stream().filter(File::isFile).collect(Collectors.toList());
      List<File> translationFiles = (List)files.stream().filter((filex) -> filex.getParent().equals("translation")).collect(Collectors.toList());

      for(File file : files) {
         boolean isTranslation = file.getParent().equals("translation");
         if (!isTranslation) {
            addDocToList(gson, docsList, file);
         }
      }

      for(File file : translationFiles) {
         if (language.equals(file.getName())) {
            addDocToList(gson, docsList, file);
         }
      }

      InputStream stream = GuiDocumentationOverlayPanel.class.getResourceAsStream("/assets/mappet/docs.json");
      if (stream == null) {
         return null;
      } else {
         Scanner scanner = new Scanner(stream, "UTF-8");

         Docs var20;
         label75: {
            Docs mainDocs;
            try {
               if (!scanner.useDelimiter("\\A").hasNext()) {
                  var20 = null;
                  break label75;
               }

               mainDocs = (Docs)gson.fromJson(scanner.next(), Docs.class);
            } catch (Throwable var13) {
               try {
                  scanner.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }

               throw var13;
            }

            scanner.close();
            if (mainDocs == null) {
               return null;
            }

            for(Docs doc : docsList) {
               mergeDocs(mainDocs, doc);
            }

            return mainDocs;
         }

         scanner.close();
         return var20;
      }
   }

   private static void addAddonsDocs(Gson gson, List<Docs> docsList) {
   }

   private static void mergeDocs(Docs docsMain, Docs docsAdd) {
      for(DocClass classAdd : docsAdd.classes) {
         classAdd.source = docsAdd.source;
         DocClass classMain = docsMain.getClass(classAdd.name);
         if (classMain == null) {
            docsMain.classes.add(classAdd);
         } else {
            classMain.doc = classAdd.doc.trim().isEmpty() ? classMain.doc : classAdd.doc.trim();
            mergeClasses(classMain, classAdd);
         }
      }

      for(DocPackage packageAdd : docsAdd.packages) {
         packageAdd.source = docsAdd.source;
         DocPackage packageMain = docsMain.getPackage(packageAdd.name);
         if (packageMain == null) {
            docsMain.packages.add(packageAdd);
         } else {
            packageMain.doc = packageAdd.doc.trim().isEmpty() ? packageMain.doc : packageAdd.doc.trim();
            Collections.replaceAll(docsMain.packages, packageMain, packageAdd);
         }
      }

   }

   private static void mergeClasses(DocClass classMain, DocClass classAdd) {
      for(DocMethod methodAdd : classAdd.methods) {
         methodAdd.source = classAdd.source;
         DocMethod methodMain = classMain.getExactMethod(methodAdd.name);
         if (methodMain == null) {
            classMain.methods.add(methodAdd);
         } else {
            methodMain.doc = methodAdd.doc.trim().isEmpty() ? methodMain.doc : methodAdd.doc.trim();
            Collections.replaceAll(classMain.methods, methodMain, methodAdd);
         }
      }

   }

   private static void addDocToList(Gson gson, List<Docs> list, File file) {
      try {
         InputStream stream = new FileInputStream(file);
         Scanner scanner = new Scanner(stream, "UTF-8");
         Docs docs = (Docs)gson.fromJson(scanner.useDelimiter("\\A").next(), Docs.class);
         docs.source = (new File(ClientProxy.configFolder.getPath())).toPath().relativize(file.toPath()).toFile().getPath();
         docs.classes.forEach((clazz) -> {
            clazz.source = docs.source;
            clazz.methods.forEach((method) -> method.source = docs.source);
         });
         list.add(docs);
      } catch (FileNotFoundException e) {
         throw new RuntimeException(e);
      }
   }
}
