package mchorse.mappet.client.gui;

import java.util.function.Consumer;
import java.util.function.Supplier;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.client.gui.triggers.GuiTriggerElement;
import mchorse.mappet.client.gui.utils.GuiVecPosElement;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.blocks.PacketEditTrigger;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiCollapseSection;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_332;

public class GuiTriggerBlockScreen extends GuiBase {
   public GuiTriggerElement left;
   public GuiTriggerElement right;
   public GuiToggleElement collidable;
   public GuiVecPosElement boundingBoxPos1;
   public GuiVecPosElement boundingBoxPos2;
   private class_2338 pos;

   public GuiTriggerBlockScreen(class_2338 pos, Trigger left, Trigger right, boolean collidable, class_243 pos1, class_243 pos2) {
      this.pos = pos;
      class_310 mc = class_310.method_1551();
      GuiElement element = new GuiElement(mc);
      element.flex().relative(this.viewport).xy(0.5F, 0.5F).w(0.5F).anchor(0.5F, 0.5F).column(5).vertical().stretch();
      this.left = new GuiTriggerElement(mc);
      this.left.set(left);
      this.right = new GuiTriggerElement(mc);
      this.right.set(right);
      this.collidable = new GuiToggleElement(mc, IKey.lang("mappet.gui.trigger_block.collidable"), (Consumer)null);
      this.collidable.toggled(collidable);
      this.boundingBoxPos1 = new GuiVecPosElement(mc, (Consumer)null);
      this.boundingBoxPos1.set(pos1);
      this.sanitizeTrackpads(this.boundingBoxPos1);
      this.boundingBoxPos2 = new GuiVecPosElement(mc, (Consumer)null);
      this.boundingBoxPos2.set(pos2);
      this.sanitizeTrackpads(this.boundingBoxPos2);
      GuiCollapseSection collapseSection = new GuiCollapseSection(mc, IKey.lang("mappet.gui.trigger_block.bounding_box.title"), (Supplier)null, true);
      collapseSection.addFields(new GuiElement[]{Elements.row(mc, 2, new GuiElement[]{Elements.label(IKey.lang("mappet.gui.trigger_block.bounding_box.pos1")).background().marginTop(6), this.boundingBoxPos1})});
      collapseSection.addFields(new GuiElement[]{Elements.row(mc, 2, new GuiElement[]{Elements.label(IKey.lang("mappet.gui.trigger_block.bounding_box.pos2")).background().marginTop(6), this.boundingBoxPos2})});
      collapseSection.context(() -> {
         GuiSimpleContextMenu menu = (new GuiSimpleContextMenu(mc)).action(Icons.MINIMIZE, IKey.lang("mappet.gui.trigger_block.bounding_box.center"), () -> {
            double sizeX = this.boundingBoxPos2.x.value - this.boundingBoxPos1.x.value;
            double sizeY = this.boundingBoxPos2.y.value - this.boundingBoxPos1.y.value;
            double sizeZ = this.boundingBoxPos2.z.value - this.boundingBoxPos1.z.value;
            this.boundingBoxPos1.x.setValue((double)0.5F - sizeX / (double)2.0F);
            this.boundingBoxPos1.y.setValue((double)0.5F - sizeY / (double)2.0F);
            this.boundingBoxPos1.z.setValue((double)0.5F - sizeZ / (double)2.0F);
            this.boundingBoxPos2.x.setValue((double)0.5F + sizeX / (double)2.0F);
            this.boundingBoxPos2.y.setValue((double)0.5F + sizeY / (double)2.0F);
            this.boundingBoxPos2.z.setValue((double)0.5F + sizeZ / (double)2.0F);
         }).action(Icons.FULLSCREEN, IKey.lang("mappet.gui.trigger_block.bounding_box.reset"), () -> {
            this.boundingBoxPos1.set(class_243.field_1353);
            this.boundingBoxPos2.set(new class_243((double)1.0F, (double)1.0F, (double)1.0F));
         });
         return menu;
      });
      element.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.trigger_block.left")).background().marginBottom(5), this.left});
      element.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.trigger_block.right")).background().marginTop(12).marginBottom(5), this.right, this.collidable.marginTop(6)});
      element.add(collapseSection.marginTop(12));
      this.root.add(element);
   }

   public void sanitizeTrackpads(GuiVecPosElement guiVecPosElement) {
      guiVecPosElement.clamp(class_243.field_1353, new class_243((double)1.0F, (double)1.0F, (double)1.0F));
      guiVecPosElement.x.normal = 0.05;
      guiVecPosElement.x.increment = 0.05;
      guiVecPosElement.y.normal = 0.05;
      guiVecPosElement.y.increment = 0.05;
      guiVecPosElement.z.normal = 0.05;
      guiVecPosElement.z.increment = 0.05;
      guiVecPosElement.context((Supplier)null);
   }

   public boolean method_25421() {
      return false;
   }

   protected void closeScreen() {
      super.closeScreen();
      Dispatcher.sendToServer(new PacketEditTrigger(this.pos, this.left.get().serializeNBT(), this.right.get().serializeNBT(), this.collidable.isToggled(), this.boundingBoxPos1.get(), this.boundingBoxPos2.get()));
   }

   public void method_25394(class_332 drawContext, int mouseX, int mouseY, float partialTicks) {
      this.method_25420(drawContext);
      super.method_25394(drawContext, mouseX, mouseY, partialTicks);
   }
}
