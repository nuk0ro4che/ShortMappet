package mchorse.mappet.network;

import mchorse.mappet.network.client.blocks.ClientHandlerEditConditionModel;
import mchorse.mappet.network.client.blocks.ClientHandlerEditEmitter;
import mchorse.mappet.network.client.blocks.ClientHandlerEditRegion;
import mchorse.mappet.network.client.blocks.ClientHandlerEditTrigger;
import mchorse.mappet.network.client.content.ClientHandlerContentData;
import mchorse.mappet.network.client.content.ClientHandlerContentNames;
import mchorse.mappet.network.client.content.ClientHandlerServerSettings;
import mchorse.mappet.network.client.content.ClientHandlerStates;
import mchorse.mappet.network.client.crafting.ClientHandlerCraft;
import mchorse.mappet.network.client.crafting.ClientHandlerCraftingTable;
import mchorse.mappet.network.client.dialogue.ClientHandlerDialogueFragment;
import mchorse.mappet.network.client.events.ClientHandlerEventPlayerHotkeys;
import mchorse.mappet.network.client.events.ClientHandlerPlayerJournal;
import mchorse.mappet.network.client.factions.ClientHandlerFactions;
import mchorse.mappet.network.client.huds.ClientHandlerHUDMorph;
import mchorse.mappet.network.client.huds.ClientHandlerHUDScene;
import mchorse.mappet.network.client.items.ClientHandlerScriptedItemInfo;
import mchorse.mappet.network.client.logs.ClientHandlerLogs;
import mchorse.mappet.network.client.npc.ClientHandlerNpcList;
import mchorse.mappet.network.client.npc.ClientHandlerNpcState;
import mchorse.mappet.network.client.npc.ClientHandlerNpcStateChange;
import mchorse.mappet.network.client.quests.ClientHandlerQuest;
import mchorse.mappet.network.client.quests.ClientHandlerQuests;
import mchorse.mappet.network.client.scripts.ClientHandlerCameraShake;
import mchorse.mappet.network.client.scripts.ClientHandlerClientSetting;
import mchorse.mappet.network.client.scripts.ClientHandlerCancelDeath;
import mchorse.mappet.network.client.scripts.ClientHandlerClientScriptExecute;
import mchorse.mappet.network.client.scripts.ClientHandlerMouseSensitivity;
import mchorse.mappet.network.client.scripts.ClientHandlerMousePosition;
import mchorse.mappet.network.client.scripts.ClientHandlerMovementLock;
import mchorse.mappet.network.client.scripts.ClientHandlerClipboard;
import mchorse.mappet.network.client.scripts.ClientHandlerEntityRotations;
import mchorse.mappet.network.client.scripts.ClientHandlerEntityTransition;
import mchorse.mappet.network.client.scripts.ClientHandlerFirstPersonBody;
import mchorse.mappet.network.client.scripts.ClientHandlerHudVisibility;
import mchorse.mappet.network.client.scripts.ClientHandlerHudPosition;
import mchorse.mappet.network.client.scripts.ClientHandlerHandState;
import mchorse.mappet.network.client.scripts.ClientHandlerHandMorphAnimation;
import mchorse.mappet.network.client.scripts.ClientHandlerKeyBinding;
import mchorse.mappet.network.client.scripts.ClientHandlerOpenWeb;
import mchorse.mappet.network.client.scripts.ClientHandlerPlayModelAnimation;
import mchorse.mappet.network.client.scripts.ClientHandlerRepl;
import mchorse.mappet.network.client.scripts.ClientHandlerScriptSearchResults;
import mchorse.mappet.network.client.scripts.ClientHandlerScriptDiagnosticCode;
import mchorse.mappet.network.client.scripts.ClientHandlerShader;
import mchorse.mappet.network.client.scripts.ClientHandlerSound;
import mchorse.mappet.network.client.scripts.ClientHandlerManagedSound;
import mchorse.mappet.network.client.scripts.ClientHandlerWorldMorph;
import mchorse.mappet.network.client.scripts.ClientHandlerVoicechatMute;
import mchorse.mappet.network.client.scripts.ClientHandlerVirtualWorldLight;
import mchorse.mappet.network.client.ui.ClientHandlerCloseUI;
import mchorse.mappet.network.client.ui.ClientHandlerUI;
import mchorse.mappet.network.client.ui.ClientHandlerUIData;
import mchorse.mappet.network.client.utils.ClientHandlerChangedBoundingBox;
import mchorse.mappet.network.common.blocks.PacketEditConditionModel;
import mchorse.mappet.network.common.blocks.PacketEditEmitter;
import mchorse.mappet.network.common.blocks.PacketEditRegion;
import mchorse.mappet.network.common.blocks.PacketEditTrigger;
import mchorse.mappet.network.common.content.PacketContentData;
import mchorse.mappet.network.common.content.PacketContentExit;
import mchorse.mappet.network.common.content.PacketContentFolder;
import mchorse.mappet.network.common.content.PacketContentNames;
import mchorse.mappet.network.common.content.PacketContentRequestData;
import mchorse.mappet.network.common.content.PacketContentRequestNames;
import mchorse.mappet.network.common.content.PacketRequestServerSettings;
import mchorse.mappet.network.common.content.PacketRequestStates;
import mchorse.mappet.network.common.content.PacketServerSettings;
import mchorse.mappet.network.common.content.PacketStates;
import mchorse.mappet.network.common.crafting.PacketCraft;
import mchorse.mappet.network.common.crafting.PacketCraftingTable;
import mchorse.mappet.network.common.dialogue.PacketDialogueFragment;
import mchorse.mappet.network.common.dialogue.PacketFinishDialogue;
import mchorse.mappet.network.common.dialogue.PacketPickReply;
import mchorse.mappet.network.common.events.PacketEventHotkey;
import mchorse.mappet.network.common.events.PacketEventHotkeys;
import mchorse.mappet.network.common.events.PacketPlayerJournal;
import mchorse.mappet.network.common.factions.PacketFactions;
import mchorse.mappet.network.common.factions.PacketRequestFactions;
import mchorse.mappet.network.common.huds.PacketHUDMorph;
import mchorse.mappet.network.common.huds.PacketHUDScene;
import mchorse.mappet.network.common.items.PacketScriptedItemInfo;
import mchorse.mappet.network.common.logs.PacketLogs;
import mchorse.mappet.network.common.logs.PacketRequestLogs;
import mchorse.mappet.network.common.npc.PacketNpcJump;
import mchorse.mappet.network.common.npc.PacketNpcList;
import mchorse.mappet.network.common.npc.PacketNpcState;
import mchorse.mappet.network.common.npc.PacketNpcStateChange;
import mchorse.mappet.network.common.npc.PacketNpcTool;
import mchorse.mappet.network.common.quests.PacketQuest;
import mchorse.mappet.network.common.quests.PacketQuestAction;
import mchorse.mappet.network.common.quests.PacketQuestVisibility;
import mchorse.mappet.network.common.quests.PacketQuests;
import mchorse.mappet.network.common.scripts.PacketCameraShake;
import mchorse.mappet.network.common.scripts.PacketClick;
import mchorse.mappet.network.common.scripts.PacketClientSetting;
import mchorse.mappet.network.common.scripts.PacketClientScriptExecute;
import mchorse.mappet.network.common.scripts.PacketMouseSensitivity;
import mchorse.mappet.network.common.scripts.PacketMousePosition;
import mchorse.mappet.network.common.scripts.PacketCancelDeath;
import mchorse.mappet.network.common.scripts.PacketMovementLock;
import mchorse.mappet.network.common.scripts.PacketClipboard;
import mchorse.mappet.network.common.scripts.PacketEntityRotations;
import mchorse.mappet.network.common.scripts.PacketEntityTransition;
import mchorse.mappet.network.common.scripts.PacketFirstPersonBody;
import mchorse.mappet.network.common.scripts.PacketHudVisibility;
import mchorse.mappet.network.common.scripts.PacketHudPosition;
import mchorse.mappet.network.common.scripts.PacketHandState;
import mchorse.mappet.network.common.scripts.PacketHandMorphAnimation;
import mchorse.mappet.network.common.scripts.PacketKeyBinding;
import mchorse.mappet.network.common.scripts.PacketOpenWeb;
import mchorse.mappet.network.common.scripts.PacketPlayModelAnimation;
import mchorse.mappet.network.common.scripts.PacketRepl;
import mchorse.mappet.network.common.scripts.PacketRequestScriptSearch;
import mchorse.mappet.network.common.scripts.PacketRequestScriptDiagnostic;
import mchorse.mappet.network.common.scripts.PacketScriptDiagnosticCode;
import mchorse.mappet.network.common.scripts.PacketScriptSearchResults;
import mchorse.mappet.network.common.scripts.PacketShader;
import mchorse.mappet.network.common.scripts.PacketSound;
import mchorse.mappet.network.common.scripts.PacketManagedSound;
import mchorse.mappet.network.common.scripts.PacketWorldMorph;
import mchorse.mappet.network.common.scripts.PacketVoicechatMute;
import mchorse.mappet.network.common.scripts.PacketVirtualWorldLight;
import mchorse.mappet.network.common.ui.PacketCloseUI;
import mchorse.mappet.network.common.ui.PacketUI;
import mchorse.mappet.network.common.ui.PacketUIData;
import mchorse.mappet.network.common.ui.PacketUIPreview;
import mchorse.mappet.network.common.utils.PacketChangedBoundingBox;
import mchorse.mappet.network.server.blocks.ServerHandlerEditConditionModel;
import mchorse.mappet.network.server.blocks.ServerHandlerEditEmitter;
import mchorse.mappet.network.server.blocks.ServerHandlerEditRegion;
import mchorse.mappet.network.server.blocks.ServerHandlerEditTrigger;
import mchorse.mappet.network.server.content.ServerHandlerContentData;
import mchorse.mappet.network.server.content.ServerHandlerContentExit;
import mchorse.mappet.network.server.content.ServerHandlerContentFolder;
import mchorse.mappet.network.server.content.ServerHandlerContentRequestData;
import mchorse.mappet.network.server.content.ServerHandlerContentRequestNames;
import mchorse.mappet.network.server.content.ServerHandlerRequestServerSettings;
import mchorse.mappet.network.server.content.ServerHandlerRequestStates;
import mchorse.mappet.network.server.content.ServerHandlerServerSettings;
import mchorse.mappet.network.server.content.ServerHandlerStates;
import mchorse.mappet.network.server.crafting.ServerHandlerCraft;
import mchorse.mappet.network.server.crafting.ServerHandlerCraftingTable;
import mchorse.mappet.network.server.dialogue.ServerHandlerFinishDialogue;
import mchorse.mappet.network.server.dialogue.ServerHandlerPickReply;
import mchorse.mappet.network.server.events.ServerHandlerEventHotkey;
import mchorse.mappet.network.server.events.ServerHandlerPlayerJournal;
import mchorse.mappet.network.server.factions.ServerHandlerRequestFactions;
import mchorse.mappet.network.server.items.ServerHandlerScriptedItemInfo;
import mchorse.mappet.network.server.logs.ServerHandlerLogs;
import mchorse.mappet.network.server.npc.ServerHandlerNpcJump;
import mchorse.mappet.network.server.npc.ServerHandlerNpcList;
import mchorse.mappet.network.server.npc.ServerHandlerNpcState;
import mchorse.mappet.network.server.npc.ServerHandlerNpcTool;
import mchorse.mappet.network.server.quests.ServerHandlerQuestAction;
import mchorse.mappet.network.server.quests.ServerHandlerQuestVisibility;
import mchorse.mappet.network.server.scripts.ServerHandlerClick;
import mchorse.mappet.network.server.scripts.ServerHandlerClipboard;
import mchorse.mappet.network.server.scripts.ServerHandlerKeyBinding;
import mchorse.mappet.network.server.scripts.ServerHandlerMouseSensitivity;
import mchorse.mappet.network.server.scripts.ServerHandlerManagedSound;
import mchorse.mappet.network.server.scripts.ServerHandlerRepl;
import mchorse.mappet.network.server.scripts.ServerHandlerRequestScriptSearch;
import mchorse.mappet.network.server.scripts.ServerHandlerRequestScriptDiagnostic;
import mchorse.mappet.network.server.ui.ServerHandlerUI;
import mchorse.mappet.network.server.ui.ServerHandlerUIData;
import mchorse.mappet.network.server.ui.ServerHandlerUIPreview;
import mchorse.mclib.network.AbstractDispatcher;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.network.Side;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1297;
import net.minecraft.class_3222;

public class Dispatcher {
   public static final AbstractDispatcher DISPATCHER = new AbstractDispatcher("mappet") {
      public void register() {
         boolean client = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
         if (client) {
            this.register(PacketCraftingTable.class, ClientHandlerCraftingTable.class, Side.CLIENT);
         }

         this.register(PacketCraftingTable.class, ServerHandlerCraftingTable.class, Side.SERVER);
         if (client) {
            this.register(PacketCraft.class, ClientHandlerCraft.class, Side.CLIENT);
         }

         this.register(PacketCraft.class, ServerHandlerCraft.class, Side.SERVER);
         if (client) {
            this.register(PacketDialogueFragment.class, ClientHandlerDialogueFragment.class, Side.CLIENT);
         }

         this.register(PacketPickReply.class, ServerHandlerPickReply.class, Side.SERVER);
         this.register(PacketFinishDialogue.class, ServerHandlerFinishDialogue.class, Side.SERVER);
         if (client) {
            this.register(PacketEditEmitter.class, ClientHandlerEditEmitter.class, Side.CLIENT);
         }

         this.register(PacketEditEmitter.class, ServerHandlerEditEmitter.class, Side.SERVER);
         if (client) {
            this.register(PacketEditTrigger.class, ClientHandlerEditTrigger.class, Side.CLIENT);
         }

         this.register(PacketEditTrigger.class, ServerHandlerEditTrigger.class, Side.SERVER);
         if (client) {
            this.register(PacketEditRegion.class, ClientHandlerEditRegion.class, Side.CLIENT);
         }

         this.register(PacketEditRegion.class, ServerHandlerEditRegion.class, Side.SERVER);
         if (client) {
            this.register(PacketEditConditionModel.class, ClientHandlerEditConditionModel.class, Side.CLIENT);
         }

         this.register(PacketEditConditionModel.class, ServerHandlerEditConditionModel.class, Side.SERVER);
         if (client) {
            this.register(PacketScriptedItemInfo.class, ClientHandlerScriptedItemInfo.class, Side.CLIENT);
         }

         this.register(PacketScriptedItemInfo.class, ServerHandlerScriptedItemInfo.class, Side.SERVER);
         this.register(PacketContentRequestNames.class, ServerHandlerContentRequestNames.class, Side.SERVER);
         this.register(PacketContentRequestData.class, ServerHandlerContentRequestData.class, Side.SERVER);
         if (client) {
            this.register(PacketContentData.class, ClientHandlerContentData.class, Side.CLIENT);
         }

         this.register(PacketContentData.class, ServerHandlerContentData.class, Side.SERVER);
         this.register(PacketContentFolder.class, ServerHandlerContentFolder.class, Side.SERVER);
         if (client) {
            this.register(PacketContentNames.class, ClientHandlerContentNames.class, Side.CLIENT);
         }

         this.register(PacketContentExit.class, ServerHandlerContentExit.class, Side.SERVER);
         if (client) {
            this.register(PacketServerSettings.class, ClientHandlerServerSettings.class, Side.CLIENT);
         }

         this.register(PacketServerSettings.class, ServerHandlerServerSettings.class, Side.SERVER);
         this.register(PacketRequestServerSettings.class, ServerHandlerRequestServerSettings.class, Side.SERVER);
         if (client) {
            this.register(PacketStates.class, ClientHandlerStates.class, Side.CLIENT);
         }

         this.register(PacketStates.class, ServerHandlerStates.class, Side.SERVER);
         this.register(PacketRequestStates.class, ServerHandlerRequestStates.class, Side.SERVER);
         if (client) {
            this.register(PacketNpcStateChange.class, ClientHandlerNpcStateChange.class, Side.CLIENT);
         }

         if (client) {
            this.register(PacketNpcState.class, ClientHandlerNpcState.class, Side.CLIENT);
         }

         this.register(PacketNpcState.class, ServerHandlerNpcState.class, Side.SERVER);
         if (client) {
            this.register(PacketNpcList.class, ClientHandlerNpcList.class, Side.CLIENT);
         }

         this.register(PacketNpcList.class, ServerHandlerNpcList.class, Side.SERVER);
         this.register(PacketNpcTool.class, ServerHandlerNpcTool.class, Side.SERVER);
         this.register(PacketNpcJump.class, ServerHandlerNpcJump.class, Side.SERVER);
         if (client) {
            this.register(PacketQuest.class, ClientHandlerQuest.class, Side.CLIENT);
         }

         if (client) {
            this.register(PacketQuests.class, ClientHandlerQuests.class, Side.CLIENT);
         }

         this.register(PacketQuestAction.class, ServerHandlerQuestAction.class, Side.SERVER);
         this.register(PacketQuestVisibility.class, ServerHandlerQuestVisibility.class, Side.SERVER);
         if (client) {
            this.register(PacketFactions.class, ClientHandlerFactions.class, Side.CLIENT);
         }

         this.register(PacketRequestFactions.class, ServerHandlerRequestFactions.class, Side.SERVER);
         if (client) {
            this.register(PacketEventHotkeys.class, ClientHandlerEventPlayerHotkeys.class, Side.CLIENT);
         }

         this.register(PacketEventHotkey.class, ServerHandlerEventHotkey.class, Side.SERVER);
         if (client) {
            this.register(PacketPlayerJournal.class, ClientHandlerPlayerJournal.class, Side.CLIENT);
         }

         this.register(PacketPlayerJournal.class, ServerHandlerPlayerJournal.class, Side.SERVER);
         if (client) {
            this.register(PacketCameraShake.class, ClientHandlerCameraShake.class, Side.CLIENT);
            this.register(PacketEntityRotations.class, ClientHandlerEntityRotations.class, Side.CLIENT);
            this.register(PacketEntityTransition.class, ClientHandlerEntityTransition.class, Side.CLIENT);
            this.register(PacketFirstPersonBody.class, ClientHandlerFirstPersonBody.class, Side.CLIENT);
            this.register(PacketHudVisibility.class, ClientHandlerHudVisibility.class, Side.CLIENT);
            this.register(PacketHudPosition.class, ClientHandlerHudPosition.class, Side.CLIENT);
            this.register(PacketHandState.class, ClientHandlerHandState.class, Side.CLIENT);
            this.register(PacketHandMorphAnimation.class, ClientHandlerHandMorphAnimation.class, Side.CLIENT);
            this.register(PacketScriptSearchResults.class, ClientHandlerScriptSearchResults.class, Side.CLIENT);
            this.register(PacketScriptDiagnosticCode.class, ClientHandlerScriptDiagnosticCode.class, Side.CLIENT);
            this.register(PacketClipboard.class, ClientHandlerClipboard.class, Side.CLIENT);
            this.register(PacketClientSetting.class, ClientHandlerClientSetting.class, Side.CLIENT);
            this.register(PacketClientScriptExecute.class, ClientHandlerClientScriptExecute.class, Side.CLIENT);
            this.register(PacketMouseSensitivity.class, ClientHandlerMouseSensitivity.class, Side.CLIENT);
            this.register(PacketMousePosition.class, ClientHandlerMousePosition.class, Side.CLIENT);
            this.register(PacketCancelDeath.class, ClientHandlerCancelDeath.class, Side.CLIENT);
            this.register(PacketMovementLock.class, ClientHandlerMovementLock.class, Side.CLIENT);
            this.register(PacketKeyBinding.class, ClientHandlerKeyBinding.class, Side.CLIENT);
            this.register(PacketOpenWeb.class, ClientHandlerOpenWeb.class, Side.CLIENT);
            this.register(PacketPlayModelAnimation.class, ClientHandlerPlayModelAnimation.class, Side.CLIENT);
            this.register(PacketVoicechatMute.class, ClientHandlerVoicechatMute.class, Side.CLIENT);
         }

         this.register(PacketClipboard.class, ServerHandlerClipboard.class, Side.SERVER);
         this.register(PacketRequestScriptSearch.class, ServerHandlerRequestScriptSearch.class, Side.SERVER);
         this.register(PacketRequestScriptDiagnostic.class, ServerHandlerRequestScriptDiagnostic.class, Side.SERVER);
         this.register(PacketMouseSensitivity.class, ServerHandlerMouseSensitivity.class, Side.SERVER);
         this.register(PacketKeyBinding.class, ServerHandlerKeyBinding.class, Side.SERVER);

         this.register(PacketClick.class, ServerHandlerClick.class, Side.SERVER);
         if (client) {
            this.register(PacketRepl.class, ClientHandlerRepl.class, Side.CLIENT);
         }

         this.register(PacketRepl.class, ServerHandlerRepl.class, Side.SERVER);
         if (client) {
            this.register(PacketSound.class, ClientHandlerSound.class, Side.CLIENT);
            this.register(PacketManagedSound.class, ClientHandlerManagedSound.class, Side.CLIENT);
         }

         this.register(PacketManagedSound.class, ServerHandlerManagedSound.class, Side.SERVER);

         if (client) {
            this.register(PacketWorldMorph.class, ClientHandlerWorldMorph.class, Side.CLIENT);
            this.register(PacketShader.class, ClientHandlerShader.class, Side.CLIENT);
         }

         if (client) {
            this.register(PacketHUDScene.class, ClientHandlerHUDScene.class, Side.CLIENT);
         }

         if (client) {
            this.register(PacketHUDMorph.class, ClientHandlerHUDMorph.class, Side.CLIENT);
         }

         if (client) {
            this.register(PacketUI.class, ClientHandlerUI.class, Side.CLIENT);
         }

         this.register(PacketUI.class, ServerHandlerUI.class, Side.SERVER);
         if (client) {
            this.register(PacketUIData.class, ClientHandlerUIData.class, Side.CLIENT);
         }

         this.register(PacketUIData.class, ServerHandlerUIData.class, Side.SERVER);
         this.register(PacketUIPreview.class, ServerHandlerUIPreview.class, Side.SERVER);
         if (client) {
            this.register(PacketCloseUI.class, ClientHandlerCloseUI.class, Side.CLIENT);
         }

         this.register(PacketRequestLogs.class, ServerHandlerLogs.class, Side.SERVER);
         if (client) {
            this.register(PacketLogs.class, ClientHandlerLogs.class, Side.CLIENT);
         }

         if (client) {
            this.register(PacketChangedBoundingBox.class, ClientHandlerChangedBoundingBox.class, Side.CLIENT);
            this.register(PacketVirtualWorldLight.class, ClientHandlerVirtualWorldLight.class, Side.CLIENT);
         }

      }
   };
   private static boolean registered;

   public static void sendToTracked(class_1297 entity, IMessage message) {
      DISPATCHER.sendToTracked(entity, message);
   }

   public static void sendTo(IMessage message, class_3222 player) {
      DISPATCHER.sendTo(message, player);
   }

   public static void sendToServer(IMessage message) {
      DISPATCHER.sendToServer(message);
   }

   public static synchronized void register() {
      if (!registered) {
         registered = true;
         DISPATCHER.register();
      }

   }
}
