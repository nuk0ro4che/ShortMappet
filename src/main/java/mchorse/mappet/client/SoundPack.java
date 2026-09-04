package mchorse.mappet.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mchorse.mappet.CommonProxy;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import net.minecraft.class_3262;
import net.minecraft.class_3264;
import net.minecraft.class_3270;
import net.minecraft.class_7367;

@Environment(EnvType.CLIENT)
public class SoundPack implements class_3262 {
   public static final String NAMESPACE = "mp.sounds";
   private final File folder;

   public SoundPack(File folder) {
      this.folder = folder;
      this.folder.mkdirs();
   }

   private InputStream jsonStream(JsonObject object) {
      return new ByteArrayInputStream(object.toString().getBytes(StandardCharsets.UTF_8));
   }

   public class_7367<InputStream> method_14410(String... segments) {
      return segments.length == 1 && "pack.mcmeta".equals(segments[0]) ? () -> new ByteArrayInputStream("{\"pack\":{\"pack_format\":15,\"description\":\"Mappet custom sounds\"}}".getBytes(StandardCharsets.UTF_8)) : null;
   }

   public class_7367<InputStream> method_14405(class_3264 type, class_2960 id) {
      if (type == class_3264.field_14188 && "mp.sounds".equals(id.method_12836())) {
         if ("sounds.json".equals(id.method_12832())) {
            return () -> this.jsonStream(this.generateJson(this.folder, "", new JsonObject()));
         } else {
            File file = this.getFile(id.method_12832());
            return file.isFile() ? () -> new FileInputStream(file) : null;
         }
      } else {
         return null;
      }
   }

   public void method_14408(class_3264 type, String namespace, String prefix, class_3262.class_7664 consumer) {
      if (type == class_3264.field_14188 && "mp.sounds".equals(namespace)) {
         if ("sounds.json".startsWith(prefix)) {
            consumer.accept(new class_2960("mp.sounds", "sounds.json"), (class_7367)() -> this.jsonStream(this.generateJson(this.folder, "", new JsonObject())));
         }

         if (this.folder.isDirectory()) {
            try {
               Files.walk(this.folder.toPath()).filter((x$0) -> Files.isRegularFile(x$0, new LinkOption[0])).filter((path) -> path.toString().toLowerCase().endsWith(".ogg")).forEach((path) -> {
                  String relative = this.folder.toPath().relativize(path).toString().replace('\\', '/');
                  String resourcePath = "sounds/" + relative;
                  if (resourcePath.startsWith(prefix)) {
                     consumer.accept(new class_2960("mp.sounds", resourcePath), (class_7367)() -> Files.newInputStream(path));
                  }

               });
            } catch (IOException var6) {
            }

         }
      }
   }

   public Set<String> method_14406(class_3264 type) {
      return type == class_3264.field_14188 ? Collections.singleton("mp.sounds") : Collections.emptySet();
   }

   public <T> T method_14407(class_3270<T> reader) {
      return null;
   }

   public String method_14409() {
      return "Mappet custom sounds";
   }

   public boolean method_45178() {
      return true;
   }

   public void close() {
   }

   private JsonObject generateJson(File directory, String prefix, JsonObject object) {
      File[] files = directory.listFiles();
      if (files == null) {
         return object;
      } else {
         for(File file : files) {
            String name = file.getName();
            if (file.isDirectory()) {
               this.generateJson(file, prefix + name + "/", object);
            } else if (name.toLowerCase().endsWith(".ogg")) {
               String id = name.substring(0, name.length() - 4);
               JsonObject sound = new JsonObject();
               JsonArray sounds = new JsonArray();
               sounds.add("mp.sounds:" + prefix + id);
               sound.add("sounds", sounds);
               object.add(prefix.replace('/', '.') + id, sound);
            }
         }

         return object;
      }
   }

   private File getFile(String path) {
      return !path.startsWith("sounds/") ? new File(this.folder, "__missing__") : new File(this.folder, path.substring("sounds/".length()));
   }

   public static List<String> getCustomSoundEvents() {
      File soundsFolder = new File(CommonProxy.configFolder, "sounds");
      JsonObject json = (new SoundPack(soundsFolder)).generateJson(soundsFolder, "", new JsonObject());
      List<String> events = new ArrayList();

      for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
         events.add("mp.sounds:" + (String)entry.getKey());
      }

      return events;
   }
}
