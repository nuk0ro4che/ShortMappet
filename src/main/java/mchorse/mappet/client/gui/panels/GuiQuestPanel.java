package mchorse.mappet.client.gui.panels;

import mchorse.mappet.api.quests.Quest;
import mchorse.mappet.api.utils.ContentType;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.quests.GuiObjectives;
import mchorse.mappet.client.gui.quests.GuiRewards;
import mchorse.mappet.client.gui.triggers.GuiTriggerElement;
import mchorse.mappet.client.gui.utils.text.GuiMultiTextElement;
import mchorse.mappet.client.gui.utils.text.TextLine;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_746;

public class GuiQuestPanel extends GuiMappetRunPanel<Quest> {
   public static final IKey EMPTY = IKey.lang("mappet.gui.quests.info.empty");
   public GuiTextElement title;
   public GuiMultiTextElement<TextLine> story;
   public GuiToggleElement cancelable;
   public GuiToggleElement instant;
   public GuiTriggerElement accept;
   public GuiTriggerElement decline;
   public GuiTriggerElement complete;
   public GuiObjectives objectives;
   public GuiRewards rewards;

   public GuiQuestPanel(class_310 mc, GuiMappetDashboard dashboard) {
      super(mc, dashboard);
      this.namesList.setFileIcon(Icons.EXCLAMATION);
      this.title = new GuiTextElement(mc, 1000, (text) -> (this.data).title = text);
      this.story = new GuiMultiTextElement<TextLine>(mc, (text) -> (this.data).story = text);
      this.story.wrap().background().padding(6).flex().h(120);
      this.cancelable = new GuiToggleElement(mc, IKey.lang("mappet.gui.quests.cancelable"), (b) -> (this.data).cancelable = b.isToggled());
      this.instant = new GuiToggleElement(mc, IKey.lang("mappet.gui.quests.instant"), (b) -> (this.data).instant = b.isToggled());
      this.instant.tooltip(IKey.lang("mappet.gui.quests.instant_tooltip"));
      this.accept = new GuiTriggerElement(mc);
      this.decline = new GuiTriggerElement(mc);
      this.complete = new GuiTriggerElement(mc);
      this.objectives = new GuiObjectives(mc);
      this.objectives.marginBottom(20);
      this.rewards = new GuiRewards(mc);
      GuiLabel objectiveLabel = Elements.label(IKey.lang("mappet.gui.quests.objectives.title")).background();
      GuiLabel rewardLabel = Elements.label(IKey.lang("mappet.gui.quests.rewards.title")).background();
      GuiIconElement addObjective = new GuiIconElement(mc, Icons.ADD, (b) -> GuiBase.getCurrent().replaceContextMenu(this.objectives.getAdds()));
      GuiIconElement addReward = new GuiIconElement(mc, Icons.ADD, (b) -> GuiBase.getCurrent().replaceContextMenu(this.rewards.getAdds()));
      addObjective.flex().relative(objectiveLabel).xy(1.0F, 0.5F).w(10).anchor(1.0F, 0.5F);
      addReward.flex().relative(rewardLabel).xy(1.0F, 0.5F).w(10).anchor(1.0F, 0.5F);
      objectiveLabel.marginTop(12).marginBottom(5).add(addObjective);
      rewardLabel.marginBottom(5).add(addReward);
      GuiScrollElement scrollEditor = this.createScrollEditor();
      scrollEditor.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.quests.title")), this.title});
      scrollEditor.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.quests.description")).marginTop(12), this.story});
      scrollEditor.add(Elements.row(mc, 5, new GuiElement[]{this.cancelable, this.instant}).marginTop(6));
      scrollEditor.add(new IGuiElement[]{objectiveLabel, this.objectives});
      scrollEditor.add(new IGuiElement[]{rewardLabel, this.rewards});
      scrollEditor.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.quests.accept")).background().marginTop(20).marginBottom(4), this.accept});
      scrollEditor.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.quests.decline")).background().marginTop(12).marginBottom(4), this.decline});
      scrollEditor.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.quests.complete")).background().marginTop(12).marginBottom(4), this.complete});
      this.editor.add(scrollEditor);
      this.fill(null);
   }

   protected void run(class_746 player) {
      this.save();
      this.save = false;
      class_634 var10000 = player.field_3944;
      String var10001 = String.valueOf(player.method_5667());
      var10000.method_45731("mp quest accept " + var10001 + " " + ((Quest)this.data).getId());
   }

   public ContentType getType() {
      return ContentType.QUEST;
   }

   public String getTitle() {
      return "mappet.gui.panels.quests";
   }

   public void fill(Quest data, boolean allowed) {
      super.fill(data, allowed);
      this.editor.setVisible(data != null);
      if (data != null) {
         this.title.setText(data.title);
         this.story.setText(data.story);
         this.cancelable.toggled(data.cancelable);
         this.instant.toggled(data.instant);
         this.accept.set(data.accept);
         this.decline.set(data.decline);
         this.complete.set(data.complete);
         this.objectives.set(data.objectives);
         this.rewards.set(data.rewards);
      }

      this.resize();
      this.resize();
   }

   public void draw(GuiContext context) {
      super.draw(context);
      if (!this.editor.isVisible()) {
         int w = this.editor.area.w / 2;
         int x = this.editor.area.mx() - w / 2;
         GuiDraw.drawMultiText(this.font, EMPTY.get(), x, this.area.my(), 16777215, w, 12, 0.5F, 0.5F);
      }

   }
}
