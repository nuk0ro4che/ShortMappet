package mchorse.mappet.client.gui.nodes.dialogues;

import mchorse.mappet.api.dialogues.nodes.QuestDialogueNode;
import mchorse.mappet.api.utils.ContentType;
import mchorse.mappet.client.gui.nodes.GuiEventBaseNodePanel;
import mchorse.mappet.client.gui.utils.GuiMappetUtils;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiQuestDialogueNodePanel extends GuiEventBaseNodePanel<QuestDialogueNode> {
   public GuiButtonElement quest;
   public GuiToggleElement skipIfCompleted;

   public GuiQuestDialogueNodePanel(class_310 mc) {
      super(mc);
      this.quest = new GuiButtonElement(mc, IKey.lang("mappet.gui.overlays.quest"), (b) -> this.openQuests());
      this.skipIfCompleted = new GuiToggleElement(mc, IKey.lang("mappet.gui.nodes.dialogue.skip_if_completed"), (b) -> (this.node).skipIfCompleted = b.isToggled());
      this.skipIfCompleted.tooltip(IKey.lang("mappet.gui.nodes.dialogue.skip_if_completed_tooltip"));
      this.add(new IGuiElement[]{this.quest, this.skipIfCompleted.marginTop(12)});
   }

   private void openQuests() {
      GuiMappetUtils.openPicker(ContentType.QUEST, (this.node).quest, (name) -> (this.node).quest = name);
   }
}
