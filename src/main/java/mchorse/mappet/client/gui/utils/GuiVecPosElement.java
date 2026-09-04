package mchorse.mappet.client.gui.utils;

import java.util.function.Consumer;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_243;
import net.minecraft.class_310;

public class GuiVecPosElement extends GuiElement {
   public GuiTrackpadElement x;
   public GuiTrackpadElement y;
   public GuiTrackpadElement z;
   public Consumer<class_243> callback;

   public GuiVecPosElement(class_310 mc, Consumer<class_243> callback) {
      super(mc);
      this.callback = callback;
      this.x = new GuiTrackpadElement(mc, (v) -> this.callback());
      this.y = new GuiTrackpadElement(mc, (v) -> this.callback());
      this.z = new GuiTrackpadElement(mc, (v) -> this.callback());
      this.flex().row(5);
      this.add(new IGuiElement[]{this.x, this.y, this.z});
      this.context(this::createDefaultContextMenu);
   }

   public GuiVecPosElement clamp(class_243 min, class_243 max) {
      this.x.limit(min.field_1352, max.field_1352);
      this.y.limit(min.field_1351, max.field_1351);
      this.z.limit(min.field_1350, max.field_1350);
      return this;
   }

   public GuiSimpleContextMenu createDefaultContextMenu() {
      return (new GuiSimpleContextMenu(this.mc)).action(Icons.MOVE_TO, IKey.lang("mappet.gui.block_pos.context.paste"), this::pastePosition);
   }

   private void pastePosition() {
      this.set(new class_243(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318(), this.mc.field_1724.method_23321()));
      this.callback();
   }

   private void callback() {
      if (this.callback != null) {
         this.callback.accept(this.get());
      }

   }

   public class_243 get() {
      return new class_243(this.x.value, this.y.value, this.z.value);
   }

   public void set(class_243 pos) {
      this.x.setValue(pos.field_1352);
      this.y.setValue(pos.field_1351);
      this.z.setValue(pos.field_1350);
   }
}
