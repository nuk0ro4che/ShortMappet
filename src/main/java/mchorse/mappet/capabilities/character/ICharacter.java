package mchorse.mappet.capabilities.character;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import mchorse.mappet.api.crafting.CraftingTable;
import mchorse.mappet.api.dialogues.Dialogue;
import mchorse.mappet.api.dialogues.DialogueContext;
import mchorse.mappet.api.huds.HUDScene;
import mchorse.mappet.api.quests.Quests;
import mchorse.mappet.api.states.States;
import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mappet.utils.CurrentSession;
import mchorse.mappet.utils.PositionCache;
import net.minecraft.class_1657;
import net.minecraft.class_2487;

public interface ICharacter extends INBTSerializable<class_2487> {
   States getStates();

   Quests getQuests();

   void setCraftingTable(CraftingTable var1);

   CraftingTable getCraftingTable();

   void setDialogue(Dialogue var1, DialogueContext var2);

   Dialogue getDialogue();

   DialogueContext getDialogueContext();

   Instant getLastClear();

   void updateLastClear(Instant var1);

   PositionCache getPositionCache();

   CurrentSession getCurrentSession();

   void copy(ICharacter var1, class_1657 var2);

   UIContext getUIContext();

   void setUIContext(UIContext var1);

   boolean setupHUD(String var1, boolean var2);

   void changeHUDMorph(String var1, int var2, class_2487 var3);

   void closeHUD(String var1);

   void closeAllHUD();

   Map<String, List<HUDScene>> getDisplayedHUDs();
}
