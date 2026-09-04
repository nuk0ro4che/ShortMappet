package mchorse.mappet.client.gui.quests.objectives;

import mchorse.mappet.api.quests.objectives.KillObjective;
import mchorse.mappet.client.gui.utils.overlays.GuiEntityOverlayPanel;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlay;
import mchorse.mappet.client.gui.utils.overlays.GuiResourceLocationOverlayPanel;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_1074;
import net.minecraft.class_2487;
import net.minecraft.class_2522;
import net.minecraft.class_2960;
import net.minecraft.class_310;

public class GuiKillObjective extends GuiObjective<KillObjective> {
   public GuiButtonElement entity;
   public GuiTrackpadElement count;
   public GuiTextElement tag;

   public GuiKillObjective(class_310 mc, KillObjective objective) {
      super(mc, objective);
      this.entity = new GuiButtonElement(mc, IKey.lang("mappet.gui.overlays.entities.main"), (b) -> this.openPickEntityOverlay());
      this.entity.flex().relative(this).y(12).w(0.5F, -3);
      this.count = new GuiTrackpadElement(mc, (value) -> (this.objective).count = value.intValue());
      this.count.integer().limit((double)0.0F).setValue((double)objective.count);
      this.count.flex().relative(this).x(1.0F).y(12).w(0.5F, -2).anchorX(1.0F);
      this.tag = new GuiTextElement(mc, 10000, this::parseTag);
      this.tag.flex().relative(this).y(49).w(1.0F);
      this.tag.setText(objective.tag == null ? "" : objective.tag.toString());
      this.message.flex().relative(this).y(1.0F).w(1.0F).anchorY(1.0F);
      this.flex().h(106);
      this.add(new IGuiElement[]{this.message, this.entity, this.count, this.tag});
   }

   private void parseTag(String tag) {
      class_2487 nbt = null;

      try {
         nbt = class_2522.method_10718(tag);
      } catch (Exception var4) {
      }

      (this.objective).tag = nbt;
   }

   private void openPickEntityOverlay() {
      GuiResourceLocationOverlayPanel overlay = (new GuiEntityOverlayPanel(this.mc, this::setEntity)).set((this.objective).entity);
      GuiOverlay.addOverlay(GuiBase.getCurrent(), overlay, 0.5F, 0.6F);
   }

   private void setEntity(class_2960 location) {
      (this.objective).entity = location == null ? new class_2960("") : location;
   }

   public IKey getMessageTooltip() {
      return IKey.lang("mappet.gui.quests.objective_kill.message_tooltip");
   }

   public void draw(GuiContext context) {
      super.draw(context);
      GuiDraw.drawStringWithShadow(this.font, class_1074.method_4662("mappet.gui.quests.objective_kill.entity", new Object[0]), this.entity.area.x, this.entity.area.y - 12, 16777215);
      GuiDraw.drawStringWithShadow(this.font, class_1074.method_4662("mappet.gui.quests.objective_kill.count", new Object[0]), this.count.area.x, this.count.area.y - 12, 16777215);
      GuiDraw.drawStringWithShadow(this.font, class_1074.method_4662("mappet.gui.quests.objective_kill.nbt", new Object[0]), this.tag.area.x, this.tag.area.y - 12, 16777215);
   }
}
