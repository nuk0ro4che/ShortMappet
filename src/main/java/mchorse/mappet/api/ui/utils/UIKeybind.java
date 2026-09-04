package mchorse.mappet.api.ui.utils;

import mchorse.mappet.compat.INBTSerializable;
import net.minecraft.class_2487;

public class UIKeybind implements INBTSerializable<class_2487> {
   public int keyCode;
   public String action;
   public String label;
   public int modifier;

   public static int createModifier(boolean shift, boolean ctrl, boolean alt) {
      int modifier = shift ? 1 : 0;
      modifier += (ctrl ? 1 : 0) << 1;
      modifier += (alt ? 1 : 0) << 2;
      return modifier;
   }

   public UIKeybind() {
   }

   public UIKeybind(int keyCode, String action, String label, int modifier) {
      this.keyCode = keyCode;
      this.action = action;
      this.label = label;
      this.modifier = modifier;
   }

   public boolean isShift() {
      return (this.modifier & 1) == 1;
   }

   public boolean isCtrl() {
      return (this.modifier >> 1 & 1) == 1;
   }

   public boolean isAlt() {
      return (this.modifier >> 2 & 1) == 1;
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10569("KeyCode", this.keyCode);
      tag.method_10582("Action", this.action);
      tag.method_10582("Label", this.label);
      tag.method_10569("Modifier", this.modifier);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      this.keyCode = tag.method_10550("KeyCode");
      this.action = tag.method_10558("Action");
      this.label = tag.method_10558("Label");
      this.modifier = tag.method_10550("Modifier");
   }
}
