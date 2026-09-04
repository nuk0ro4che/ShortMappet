package mchorse.mappet.api.utils.nodes;

import java.util.UUID;
import mchorse.mappet.compat.INBTSerializable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;

public abstract class Node implements INBTSerializable<class_2487> {
   public String title = "";
   private UUID id;
   public int x;
   public int y;

   public UUID getId() {
      return this.id;
   }

   public void setId(UUID id) {
      if (this.id == null) {
         this.id = id;
      }

   }

   @Environment(EnvType.CLIENT)
   public String getTitle() {
      return this.title.isEmpty() ? this.getDisplayTitle() : this.title;
   }

   @Environment(EnvType.CLIENT)
   protected String getDisplayTitle() {
      return "";
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      if (this.id != null) {
         tag.method_10582("Id", this.id.toString());
      }

      tag.method_10582("Title", this.title);
      tag.method_10569("X", this.x);
      tag.method_10569("Y", this.y);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10545("Id")) {
         this.id = UUID.fromString(tag.method_10558("Id"));
      }

      this.title = tag.method_10558("Title");
      this.x = tag.method_10550("X");
      this.y = tag.method_10550("Y");
   }
}
