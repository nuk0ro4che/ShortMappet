package mchorse.mappet.client.gui;

import java.util.Map;
import mchorse.mappet.api.factions.Faction;
import mchorse.mappet.api.quests.Quest;
import mchorse.mappet.api.quests.Quests;
import mchorse.mappet.api.quests.chains.QuestStatus;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.client.gui.factions.GuiFactionCard;
import mchorse.mappet.client.gui.quests.GuiQuestCard;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.factions.PacketRequestFactions;
import mchorse.mappet.network.common.quests.PacketQuestAction;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiLabelListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_1074;
import net.minecraft.class_310;
import net.minecraft.class_332;

public class GuiJournalScreen extends GuiBase {
   public GuiScrollElement factions;
   public GuiElement quests;
   public GuiLabelListElement<Quest> questList;
   public GuiScrollElement questArea;
   public GuiIconElement cancel;

   public GuiJournalScreen(class_310 mc) {
      ICharacter character = Character.get(mc.field_1724);
      Quests quests = character.getQuests();
      this.factions = new GuiScrollElement(mc);
      this.quests = new GuiElement(mc);
      this.factions.flex().relative(this.viewport).x(1.0F, -250).y(22).w(240).h(1.0F, -22).column(10).vertical().stretch().scroll().padding(10);
      this.quests.flex().relative(this.viewport).xy(10, 22).wTo(this.factions.area).h(1.0F, -32);
      this.questList = new GuiLabelListElement<Quest>(mc, (l) -> this.pickQuest((Quest)((Label)l.get(0)).value, false));
      this.questArea = new GuiScrollElement(mc);

      for(Map.Entry<String, Quest> entry : quests.quests.entrySet()) {
         this.questList.add(IKey.str(((Quest)entry.getValue()).getProcessedTitle()), (Quest)entry.getValue());
      }

      this.questList.background().sort();
      this.cancel = new GuiIconElement(mc, Icons.CLOSE, (b) -> this.cancelQuest());
      this.cancel.disabledColor(-1996488705);
      this.cancel.flex().relative(this.quests).x(1.0F, -14).y(14).anchor(0.5F, 0.5F);
      this.questList.flex().relative(this.quests).w(120).h(1.0F);
      this.questArea.flex().relative(this.quests).x(120).w(1.0F, -120).h(1.0F).column(5).vertical().stretch().scroll().padding(10);
      this.root.add(new IGuiElement[]{this.factions, this.quests});
      this.quests.add(new IGuiElement[]{this.questList, this.questArea, this.cancel});
      this.pickQuest(quests.quests.isEmpty() ? null : (Quest)quests.quests.values().iterator().next(), true);
      Dispatcher.sendToServer(new PacketRequestFactions());
   }

   private void cancelQuest() {
      Label<Quest> label = (Label)this.questList.getCurrentFirst();
      if (label != null) {
         Dispatcher.sendToServer(new PacketQuestAction(((Quest)label.value).getId(), QuestStatus.CANCELED));
         int index = this.questList.getIndex();
         this.questList.remove(label);
         this.questList.setIndex(Math.max(index - 1, 0));
         label = (Label)this.questList.getCurrentFirst();
         this.pickQuest(label == null ? null : (Quest)label.value, true);
      }

   }

   public void fillFactions(Map<String, Faction> factions, Map<String, Double> states) {
      this.factions.removeAll();

      for(String key : factions.keySet()) {
         this.factions.add(new GuiFactionCard(class_310.method_1551(), (Faction)factions.get(key), (Double)states.get(key)));
      }

      this.root.resize();
   }

   private void pickQuest(Quest value, boolean select) {
      this.questArea.removeAll();
      this.questArea.setVisible(value != null);
      this.cancel.setVisible(value != null);
      if (value != null) {
         GuiQuestCard.fillQuest(this.questArea, value, false, true);
         this.cancel.setEnabled(value.cancelable);
         this.root.resize();
         this.root.resize();
      }

      if (select) {
         this.questList.setCurrentScroll(null);

         for(Label<Quest> label : this.questList.getList()) {
            if (label.value == value) {
               this.questList.setCurrentScroll(label);
               break;
            }
         }

      }
   }

   public void method_25394(class_332 drawContext, int mouseX, int mouseY, float partialTicks) {
      this.method_25420(drawContext);
      GuiDraw.drawStringWithShadow(this.field_22793, class_1074.method_4662("mappet.gui.panels.factions", new Object[0]), this.factions.area.x + 10, 10, 16777215);
      GuiDraw.drawStringWithShadow(this.field_22793, class_1074.method_4662("mappet.gui.panels.quests", new Object[0]), this.quests.area.x + 4, 10, 16777215);
      GuiDraw.drawVerticalGradientRect(this.questArea.area.x, this.questArea.area.y(0.75F), this.questArea.area.ex(), this.questArea.area.ey(), 0, 1140850688);
      super.method_25394(drawContext, mouseX, mouseY, partialTicks);
   }
}
