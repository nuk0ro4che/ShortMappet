package mchorse.mappet.utils;

import java.io.File;
import java.io.IOException;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2520;
import net.minecraft.class_2522;
import org.apache.commons.io.FileUtils;

public class NBTToJsonLike {
   public static String toJson(class_2520 base) {
      return toJson(base, new StringBuilder(), "").toString();
   }

   public static StringBuilder toJson(class_2520 base, StringBuilder builder, String indent) {
      if (base instanceof class_2499 list) {
         builder.append("[\n");

         for(int i = 0; i < list.size(); ++i) {
            builder.append(indent);
            builder.append("    ");
            toJson(list.method_10534(i), builder, indent + "    ");
            if (i < list.size() - 1) {
               builder.append(",");
            }

            builder.append("\n");
         }

         builder.append(indent);
         builder.append("]");
      } else if (base instanceof class_2487 tag) {
         builder.append("{\n");
         int i = 0;

         for(String key : tag.method_10541()) {
            builder.append(indent);
            builder.append("    \"");
            builder.append(key);
            builder.append("\": ");
            toJson(tag.method_10580(key), builder, indent + "    ");
            if (i < tag.method_10546() - 1) {
               builder.append(",");
            }

            builder.append("\n");
            ++i;
         }

         builder.append(indent);
         builder.append("}");
      } else {
         builder.append(base.toString());
      }

      return builder;
   }

   public static class_2487 fromJson(String json) {
      try {
         return class_2522.method_10718(json);
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public static void write(File file, class_2487 tag) throws IOException {
      FileUtils.writeStringToFile(file, toJson(tag), Utils.getCharset());
   }

   public static class_2487 read(File file) throws IOException {
      String json = FileUtils.readFileToString(file, Utils.getCharset());
      return fromJson(json);
   }
}
