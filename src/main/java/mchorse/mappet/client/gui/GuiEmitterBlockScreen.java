package mchorse.mappet.client.gui;

import java.util.function.Consumer;
import mchorse.mappet.client.gui.conditions.GuiCheckerElement;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.blocks.PacketEditEmitter;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_2338;
import net.minecraft.class_310;
import net.minecraft.class_332;

public class GuiEmitterBlockScreen extends GuiBase {
   public GuiCheckerElement checker;
   public GuiTrackpadElement radius;
   public GuiTrackpadElement update;
   public GuiToggleElement disable;
   private class_2338 pos;

   public GuiEmitterBlockScreen(PacketEditEmitter message) {
      this.pos = message.pos;
      class_310 mc = class_310.method_1551();
      this.checker = new GuiCheckerElement(mc, message.createChecker());
      this.radius = new GuiTrackpadElement(mc, (Consumer)null);
      this.radius.limit((double)0.0F).setValue((double)message.radius);
      this.update = new GuiTrackpadElement(mc, (Consumer)null);
      this.update.limit((double)1.0F).integer().setValue((double)message.update);
      this.disable = new GuiToggleElement(mc, IKey.lang("mappet.gui.emitter_block.disable"), (Consumer)null);
      this.disable.toggled(message.disable);
      this.disable.tooltip(IKey.lang("mappet.gui.emitter_block.disable_tootlip"));
      GuiElement frame = Elements.column(mc, 5, new GuiElement[]{Elements.label(IKey.lang("mappet.gui.emitter_block.condition")), this.checker, Elements.row(mc, 5, new GuiElement[]{Elements.column(mc, 5, new GuiElement[]{Elements.label(IKey.lang("mappet.gui.emitter_block.radius")), this.radius}), Elements.column(mc, 5, new GuiElement[]{Elements.label(IKey.lang("mappet.gui.emitter_block.update")), this.update})}).marginTop(12), this.disable});
      frame.flex().relative(this.viewport).xy(0.5F, 0.5F).w(0.5F).anchor(0.5F, 0.5F);
      this.root.add(frame);
   }

   public boolean method_25421() {
      return false;
   }

   protected void closeScreen() {
      super.closeScreen();
      Dispatcher.sendToServer(new PacketEditEmitter(this.pos, this.checker.get().toNBT(), (float)this.radius.value, (int)this.update.value, this.disable.isToggled()));
   }

   public void method_25394(class_332 drawContext, int mouseX, int mouseY, float partialTicks) {
      this.method_25420(drawContext);
      super.method_25394(drawContext, mouseX, mouseY, partialTicks);
   }
}
