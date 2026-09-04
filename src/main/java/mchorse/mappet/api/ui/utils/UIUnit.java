package mchorse.mappet.api.ui.utils;

import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.resizers.Flex;
import mchorse.mclib.client.gui.utils.resizers.Flex.Measure;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;

public class UIUnit implements INBTSerializable<class_2487> {
   public float value;
   public int offset;
   
   public boolean relative;
   public int max;
   public float anchor;
   public String target = "";
   public float targetAnchor;

   @Environment(EnvType.CLIENT)
   public void apply(Flex.Unit unit, UIContext context) {
      GuiElement target = context.getElement(this.target);
      if (this.relative || this.value != 0.0F) {
         unit.set(this.value, Measure.RELATIVE);
         unit.offset = this.offset;
      } else {
         unit.set((float)this.offset, Measure.PIXELS);
      }

      unit.max = this.max;
      unit.anchor = this.anchor;
      unit.target = target == null ? null : target.flex();
      unit.targetAnchor = this.targetAnchor;
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10548("Value", this.value);
      tag.method_10569("Offset", this.offset);
      tag.method_10556("Relative", this.relative);
      tag.method_10548("Anchor", this.anchor);
      tag.method_10582("Target", this.target);
      tag.method_10548("TagetAnchor", this.targetAnchor);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      this.value = tag.method_10583("Value");
      this.offset = tag.method_10550("Offset");
      this.relative = tag.method_10545("Relative") ? tag.method_10577("Relative") : this.value != 0.0F;
      this.anchor = tag.method_10583("Anchor");
      this.target = tag.method_10558("Target");
      this.targetAnchor = tag.method_10583("TagetAnchor");
   }
}
