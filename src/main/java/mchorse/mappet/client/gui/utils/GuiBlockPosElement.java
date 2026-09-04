package mchorse.mappet.client.gui.utils;

import java.util.function.Consumer;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_2338;
import net.minecraft.class_310;

public class GuiBlockPosElement extends GuiElement {
   public GuiTrackpadElement x;
   public GuiTrackpadElement y;
   public GuiTrackpadElement z;
   public Consumer<class_2338> callback;

   public GuiBlockPosElement(class_310 mc, Consumer<class_2338> callback) {
      super(mc);
      this.callback = callback;
      this.x = new GuiTrackpadElement(mc, (v) -> this.callback());
      this.x.integer();
      this.y = new GuiTrackpadElement(mc, (v) -> this.callback());
      this.y.integer();
      this.z = new GuiTrackpadElement(mc, (v) -> this.callback());
      this.z.integer();
      this.flex().row(5);
      this.add(new IGuiElement[]{this.x, this.y, this.z});
      this.context(this::createDefaultContextMenu);
   }

   public GuiSimpleContextMenu createDefaultContextMenu() {
      return (new GuiSimpleContextMenu(this.mc)).action(Icons.MOVE_TO, IKey.lang("mappet.gui.block_pos.context.paste"), this::pastePosition);
   }

   public GuiSimpleContextMenu createDefaultContextMenu(boolean shouldAddPasteCurrentLocationOption) {
      GuiSimpleContextMenu contextMenu = new GuiSimpleContextMenu(this.mc);
      if (shouldAddPasteCurrentLocationOption) {
         contextMenu.action(Icons.MOVE_TO, IKey.lang("mappet.gui.block_pos.context.paste"), this::pastePosition);
      }

      return contextMenu;
   }

   private void pastePosition() {
      this.set(this.mc.field_1724.method_24515());
      this.callback();
   }

   protected void callback() {
      if (this.callback != null) {
         this.callback.accept(this.get());
      }

   }

   private class_2338 get() {
      return new class_2338((int)this.x.value, (int)this.y.value, (int)this.z.value);
   }

   public void set(class_2338 pos) {
      if (pos == null) {
         pos = class_2338.field_10980;
      }

      this.x.setValue((double)pos.method_10263());
      this.y.setValue((double)pos.method_10264());
      this.z.setValue((double)pos.method_10260());
   }
}
