package mchorse.mappet.api.ui.utils;

import mchorse.mappet.compat.INBTSerializable;
import net.minecraft.class_2487;

public class UIContextItem implements INBTSerializable<class_2487> {
   public String icon = "";
   public String action = "";
   public String label = "";
   public int color;

   public UIContextItem() {
   }

   public UIContextItem(String icon, String action, String label, int color) {
      this.icon = icon;
      this.action = action;
      this.label = label;
      this.color = color;
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10582("Icon", this.icon);
      tag.method_10582("Action", this.action);
      tag.method_10582("Label", this.label);
      tag.method_10569("Color", this.color);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      this.icon = tag.method_10558("Icon");
      this.action = tag.method_10558("Action");
      this.label = tag.method_10558("Label");
      this.color = tag.method_10550("Color");
   }
}
