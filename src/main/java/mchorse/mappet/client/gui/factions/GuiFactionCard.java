package mchorse.mappet.client.gui.factions;

import mchorse.mappet.api.factions.Faction;
import mchorse.mappet.api.factions.FactionRelation;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.Interpolations;
import net.minecraft.class_124;
import net.minecraft.class_310;

public class GuiFactionCard extends GuiElement {
   private Faction faction;
   private double score;

   public GuiFactionCard(class_310 mc, Faction faction, double score) {
      super(mc);
      this.faction = faction;
      this.score = score;
      this.flex().h(24);
   }

   public void draw(GuiContext context) {
      super.draw(context);
      String title = this.faction.title;
      if (this.mc.field_1724.method_7337()) {
         title = title + String.valueOf(class_124.field_1080) + " (" + this.faction.getId() + ")";
      }

      GuiDraw.drawStringWithShadow(this.font, title, this.area.x, this.area.y, this.faction.color);
      FactionRelation.Threshold current = this.faction.ownRelation.get((int)this.score);
      FactionRelation.Threshold previous = null;
      int w = this.font.method_1727(current.title);
      GuiDraw.drawStringWithShadow(this.font, current.title, this.area.ex() - w, this.area.y, 16777215);
      int i = 0;

      for(int c = this.faction.ownRelation.thresholds.size(); i < c; ++i) {
         FactionRelation.Threshold threshold = (FactionRelation.Threshold)this.faction.ownRelation.thresholds.get(i);
         float a = (float)i / (float)c;
         float b = (float)(i + 1) / (float)c;
         if (threshold == current) {
            double anchor = i != 0 && i < c - 1 ? (this.score - (double)previous.score) / (double)((float)(threshold.score - previous.score)) : (double)1.0F;
            int x = this.area.x(b);
            if (anchor < (double)1.0F) {
               x = this.area.x((float)Interpolations.lerp((double)a, (double)b, anchor));
               GuiDraw.drawVerticalGradientRect(this.area.x(a), this.area.y + 12, x, this.area.y + 24, -16777216 + threshold.color, -16777216 + ColorUtils.multiplyColor(threshold.color, 0.7F));
               GuiDraw.drawVerticalGradientRect(x, this.area.y + 14, this.area.x(b), this.area.y + 24, threshold.color, -2013265920 + threshold.color);
            } else {
               GuiDraw.drawVerticalGradientRect(this.area.x(a), this.area.y + 12, x, this.area.y + 24, -16777216 + threshold.color, -16777216 + ColorUtils.multiplyColor(threshold.color, 0.7F));
            }

            GuiDraw.drawRect(this.area.x(a) - 1, this.area.y + 24, this.area.x(b) + 1, this.area.y + 25, -1);
         } else {
            GuiDraw.drawVerticalGradientRect(this.area.x(a), this.area.y + 14, this.area.x(b), this.area.y + 24, threshold.color, -2013265920 + threshold.color);
         }

         previous = threshold;
      }

      String label = String.valueOf((int)this.score);
      w = this.font.method_1727(label);
      GuiDraw.drawTextBackground(this.font, label, this.area.mx() - w / 2, this.area.y, 16777215, -2013265920, 2);
   }
}
