package mchorse.mappet;

import java.io.File;
import mchorse.mappet.api.conditions.blocks.AbstractConditionBlock;
import mchorse.mappet.api.conditions.blocks.ConditionConditionBlock;
import mchorse.mappet.api.conditions.blocks.DialogueConditionBlock;
import mchorse.mappet.api.conditions.blocks.EntityConditionBlock;
import mchorse.mappet.api.conditions.blocks.ExpressionConditionBlock;
import mchorse.mappet.api.conditions.blocks.FactionConditionBlock;
import mchorse.mappet.api.conditions.blocks.ItemConditionBlock;
import mchorse.mappet.api.conditions.blocks.MorphConditionBlock;
import mchorse.mappet.api.conditions.blocks.QuestConditionBlock;
import mchorse.mappet.api.conditions.blocks.StateConditionBlock;
import mchorse.mappet.api.conditions.blocks.WorldTimeConditionBlock;
import mchorse.mappet.api.dialogues.nodes.CommentNode;
import mchorse.mappet.api.dialogues.nodes.CraftingNode;
import mchorse.mappet.api.dialogues.nodes.QuestChainNode;
import mchorse.mappet.api.dialogues.nodes.QuestDialogueNode;
import mchorse.mappet.api.dialogues.nodes.ReactionNode;
import mchorse.mappet.api.dialogues.nodes.ReplyNode;
import mchorse.mappet.api.events.nodes.CancelNode;
import mchorse.mappet.api.events.nodes.CommandNode;
import mchorse.mappet.api.events.nodes.ConditionNode;
import mchorse.mappet.api.events.nodes.EventBaseNode;
import mchorse.mappet.api.events.nodes.SwitchNode;
import mchorse.mappet.api.events.nodes.TimerNode;
import mchorse.mappet.api.events.nodes.TriggerNode;
import mchorse.mappet.api.quests.chains.QuestNode;
import mchorse.mappet.api.triggers.blocks.AbstractTriggerBlock;
import mchorse.mappet.api.triggers.blocks.CommandTriggerBlock;
import mchorse.mappet.api.triggers.blocks.DialogueTriggerBlock;
import mchorse.mappet.api.triggers.blocks.EventTriggerBlock;
import mchorse.mappet.api.triggers.blocks.ItemTriggerBlock;
import mchorse.mappet.api.triggers.blocks.MorphTriggerBlock;
import mchorse.mappet.api.triggers.blocks.ScriptTriggerBlock;
import mchorse.mappet.api.triggers.blocks.SoundTriggerBlock;
import mchorse.mappet.api.triggers.blocks.StateTriggerBlock;
import mchorse.mappet.api.ui.components.UIButtonComponent;
import mchorse.mappet.api.ui.components.UIClickComponent;
import mchorse.mappet.api.ui.components.UIColorComponent;
import mchorse.mappet.api.ui.components.UIComponent;
import mchorse.mappet.api.ui.components.UIGraphicsComponent;
import mchorse.mappet.api.ui.components.UIIconComponent;
import mchorse.mappet.api.ui.components.UILabelComponent;
import mchorse.mappet.api.ui.components.UILayoutComponent;
import mchorse.mappet.api.ui.components.UIMorphComponent;
import mchorse.mappet.api.ui.components.UIStackComponent;
import mchorse.mappet.api.ui.components.UIStringListComponent;
import mchorse.mappet.api.ui.components.UITextComponent;
import mchorse.mappet.api.ui.components.UITextareaComponent;
import mchorse.mappet.api.ui.components.UITextboxComponent;
import mchorse.mappet.api.ui.components.UIToggleComponent;
import mchorse.mappet.api.ui.components.UITrackpadComponent;
import mchorse.mappet.api.utils.factory.IFactory;
import mchorse.mappet.api.utils.factory.MapFactory;
import mchorse.mappet.compat.events.FabricEventBridge;
import mchorse.mappet.events.RegisterConditionBlockEvent;
import mchorse.mappet.events.RegisterDialogueNodeEvent;
import mchorse.mappet.events.RegisterEventNodeEvent;
import mchorse.mappet.events.RegisterQuestChainNodeEvent;
import mchorse.mappet.events.RegisterTriggerBlockEvent;
import mchorse.mappet.events.RegisterUIComponentEvent;
import mchorse.mappet.events.ScriptedItemEventHandler;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.utils.ScriptUtils;
import net.fabricmc.loader.api.FabricLoader;

public class CommonProxy {
   private static IFactory<EventBaseNode> events;
   private static IFactory<EventBaseNode> dialogues;
   private static IFactory<QuestNode> chains;
   private static IFactory<AbstractConditionBlock> conditionBlocks;
   private static IFactory<AbstractTriggerBlock> triggerBlocks;
   private static IFactory<UIComponent> uiComponents;
   public static File configFolder;
   public static EventHandler eventHandler;
   public static ScriptedItemEventHandler scriptedItemEventHandler;

   public static IFactory<EventBaseNode> getEvents() {
      return events;
   }

   public static IFactory<EventBaseNode> getDialogues() {
      return dialogues;
   }

   public static IFactory<QuestNode> getChains() {
      return chains;
   }

   public static IFactory<AbstractConditionBlock> getConditionBlocks() {
      return conditionBlocks;
   }

   public static IFactory<AbstractTriggerBlock> getTriggerBlocks() {
      return triggerBlocks;
   }

   public static IFactory<UIComponent> getUiComponents() {
      return uiComponents;
   }

   public void preInit() {
      String path = FabricLoader.getInstance().getConfigDir().toFile().getAbsolutePath();
      configFolder = new File(path, "mappet");
      configFolder.mkdir();
      Dispatcher.register();
      eventHandler = new EventHandler();
      scriptedItemEventHandler = new ScriptedItemEventHandler();
      Mappet.EVENT_BUS.register(eventHandler);
      FabricEventBridge.register();
   }

   public void init() {
      ScriptUtils.initiateScriptEngines();
   }

   public void postInit() {
      MapFactory<EventBaseNode> eventNodes = (new MapFactory<EventBaseNode>()).register("command", CommandNode.class, 9710335).register("comment", CommentNode.class, 15858316).register("condition", ConditionNode.class, 16716947).register("switch", SwitchNode.class, 11796224).register("timer", TimerNode.class, 35071).register("trigger", TriggerNode.class, 16711731).alias("trigger", "event").alias("trigger", "dialogue").alias("trigger", "script").register("cancel", CancelNode.class, 15658734);
      events = eventNodes;
      Mappet.EVENT_BUS.post(new RegisterEventNodeEvent(eventNodes));
      MapFactory<EventBaseNode> dialogueNodes = eventNodes.copy().register("reply", ReplyNode.class, 41215).register("reaction", ReactionNode.class, 16711731).register("crafting", CraftingNode.class, 16737792).register("quest_chain", QuestChainNode.class, 16755200).register("quest", QuestDialogueNode.class, 16755200).unregister("timer");
      dialogues = dialogueNodes;
      Mappet.EVENT_BUS.post(new RegisterDialogueNodeEvent(dialogueNodes));
      MapFactory<QuestNode> questChainNodes = (new MapFactory<QuestNode>()).register("quest", QuestNode.class, 16755200);
      chains = questChainNodes;
      Mappet.EVENT_BUS.post(new RegisterQuestChainNodeEvent(questChainNodes));
      MapFactory<AbstractConditionBlock> conditions = (new MapFactory<AbstractConditionBlock>()).register("quest", QuestConditionBlock.class, 16755200).register("state", StateConditionBlock.class, 16711731).register("dialogue", DialogueConditionBlock.class, 1179443).register("faction", FactionConditionBlock.class, 11796224).register("item", ItemConditionBlock.class, 16737792).register("world_time", WorldTimeConditionBlock.class, 35071).register("entity", EntityConditionBlock.class, 2965859).register("condition", ConditionConditionBlock.class, 16716947).register("morph", MorphConditionBlock.class, 5177568).register("expression", ExpressionConditionBlock.class, 15658734);
      conditionBlocks = conditions;
      Mappet.EVENT_BUS.post(new RegisterConditionBlockEvent(conditions));
      MapFactory<AbstractTriggerBlock> triggers = (new MapFactory<AbstractTriggerBlock>()).register("command", CommandTriggerBlock.class, 9710335).register("sound", SoundTriggerBlock.class, 41215).register("event", EventTriggerBlock.class, 16711731).register("dialogue", DialogueTriggerBlock.class, 1179443).register("script", ScriptTriggerBlock.class, 2965859).register("item", ItemTriggerBlock.class, 16737792).register("state", StateTriggerBlock.class, 16711731).register("morph", MorphTriggerBlock.class, 5177568);
      triggerBlocks = triggers;
      Mappet.EVENT_BUS.post(new RegisterTriggerBlockEvent(triggers));
      MapFactory<UIComponent> ui = (new MapFactory<UIComponent>()).register("graphics", UIGraphicsComponent.class, 16777215).register("button", UIButtonComponent.class, 16777215).register("icon", UIIconComponent.class, 16777215).register("label", UILabelComponent.class, 16777215).register("text", UITextComponent.class, 16777215).register("textbox", UITextboxComponent.class, 16777215).register("textarea", UITextareaComponent.class, 16777215).register("toggle", UIToggleComponent.class, 16777215).register("trackpad", UITrackpadComponent.class, 16777215).register("strings", UIStringListComponent.class, 16777215).register("item", UIStackComponent.class, 16777215).register("layout", UILayoutComponent.class, 16777215).register("morph", UIMorphComponent.class, 16777215).register("clickarea", UIClickComponent.class, 16777215).register("color", UIColorComponent.class, 16777215);
      uiComponents = ui;
      Mappet.EVENT_BUS.post(new RegisterUIComponentEvent(ui));
   }
}
