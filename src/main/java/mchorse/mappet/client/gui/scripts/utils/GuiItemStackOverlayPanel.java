package mchorse.mappet.client.gui.scripts.utils;

import mchorse.mappet.client.gui.scripts.GuiTextEditor;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlayPanel;
import mchorse.mappet.compat.NbtCompat;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_1799;
import net.minecraft.class_2519;
import net.minecraft.class_310;

public class GuiItemStackOverlayPanel extends GuiOverlayPanel {
   public GuiSlotElement pick;
   public GuiButtonElement insert;
   private GuiTextEditor editor;
   private class_1799 stack;

   public GuiItemStackOverlayPanel(class_310 mc, IKey title, GuiTextEditor editor, class_1799 stack) {
      super(mc, title);
      this.editor = editor;
      this.stack = stack;
      this.pick = new GuiSlotElement(mc, 0, this::pickItem);
      this.pick.flex().wh(20, 20);
      this.pick.setStack(stack);
      this.insert = new GuiButtonElement(mc, IKey.lang("mappet.gui.scripts.overlay.insert"), this::insert);
      GuiElement row = Elements.row(mc, 5, new GuiElement[]{this.pick, this.insert});
      row.flex().relative(this.content).y(1.0F, -30).w(1.0F).h(20).row(0).preferred(1);
      this.content.add(row);
   }

   private void pickItem(class_1799 stack) {
      this.stack = stack.method_7972();
   }

   private void insert(GuiButtonElement b) {
      this.close();
      if (!this.stack.method_7960()) {
         String nbt = NbtCompat.write(this.stack).toString();
         this.editor.pasteText(class_2519.method_10706(nbt));
      }

   }
}
