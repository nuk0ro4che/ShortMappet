package mchorse.mappet.client.gui;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import mchorse.mappet.api.quests.Quest;
import mchorse.mappet.api.quests.Quests;
import mchorse.mappet.api.quests.objectives.AbstractObjective;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.compat.client.LegacyGlStateManager;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import net.minecraft.class_1041;
import net.minecraft.class_124;
import net.minecraft.class_310;
import net.minecraft.class_327;

public class GuiQuestTracker {
   public static void renderQuests(class_1041 size, float partial) {
      class_310 mc = class_310.method_1551();
      ICharacter character = Character.get(mc.field_1724);
      if (character != null && !character.getQuests().quests.isEmpty()) {
         Quests quests = character.getQuests();
         int i = 0;
         int c = Math.min(quests.quests.size(), 3);
         int w = 160;
         int x = size.method_4486() - w;
         int y = 60;

         for(Map.Entry<String, Quest> entry : quests.quests.entrySet()) {
            if (i >= c) {
               break;
            }

            y += ((Quest)entry.getValue()).visible ? renderQuest(mc, (Quest)entry.getValue(), x, y, w) : 0;
            ++i;
         }
      }

   }

   private static int renderQuest(class_310 mc, Quest value, int x, int y, int w) {
      boolean questComplete = value.isComplete(mc.field_1724);
      String title = value.getProcessedTitle();
      if (questComplete) {
         String var10000 = String.valueOf(class_124.field_1065);
         title = var10000 + title;
      }

      LegacyGlStateManager.enableBlend();
      LegacyGlStateManager.enableAlpha();
      LegacyGlStateManager.blendFunc(LegacyGlStateManager.SourceFactor.SRC_ALPHA, LegacyGlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GuiDraw.drawHorizontalGradientRect(x, y, x + w, y + 16, 285212672, -1442840576);
      GuiDraw.drawStringWithShadow(mc.field_1772, title, x + 4, y + 4, 16777215);
      if (mc.field_1724.method_7337()) {
         int lw = mc.field_1772.method_1727(value.getId());
         GuiDraw.drawTextBackground(mc.field_1772, value.getId(), x - 4 - lw, y + 4, 11184810, -2013265920, 2);
      }

      int original = y;
      y += 16;

      for(AbstractObjective objective : value.objectives) {
         String var17 = objective.stringify(mc.field_1724);
         String description = "- " + var17;
         int var10001 = w - 6;
         class_327 var10002 = mc.field_1772;
         Objects.requireNonNull(var10002);
         List<String> lines = GuiDraw.listFormattedStringToWidth(description, var10001, var10002::method_1727);
         boolean complete = questComplete || objective.isComplete(mc.field_1724);

         for(String line : lines) {
            GuiDraw.drawStringWithShadow(mc.field_1772, line, x + 4, y + 2, complete ? 16777215 : 11184810);
            y += 12;
         }
      }

      return y - original + 6;
   }
}
