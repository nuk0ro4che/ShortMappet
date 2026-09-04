package mchorse.mappet.api.scripts.code.items;

import mchorse.mappet.api.scripts.user.items.IScriptItem;
import net.minecraft.class_1792;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class ScriptItem implements IScriptItem {
   private class_1792 item;

   public ScriptItem(class_1792 item) {
      this.item = item;
   }

   public class_1792 getMinecraftItem() {
      return this.item;
   }

   public String getId() {
      class_2960 location = this.item == null ? null : class_7923.field_41178.method_10221(this.item);
      return location == null ? "" : location.toString();
   }

   public boolean isSame(IScriptItem item) {
      return this.item == ((ScriptItem)item).getMinecraftItem();
   }
}
