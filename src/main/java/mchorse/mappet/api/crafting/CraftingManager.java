package mchorse.mappet.api.crafting;

import java.io.File;
import mchorse.mappet.api.utils.manager.BaseManager;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.crafting.PacketCraftingTable;
import net.minecraft.class_2487;
import net.minecraft.class_3222;

public class CraftingManager extends BaseManager<CraftingTable> {
   public CraftingManager(File folder) {
      super(folder);
   }

   protected CraftingTable createData(String id, class_2487 tag) {
      CraftingTable table = new CraftingTable();
      if (tag != null) {
         table.deserializeNBT(tag);
      }

      return table;
   }

   public void open(class_3222 player, CraftingTable table) {
      ICharacter character = Character.get(player);
      if (character != null) {
         character.setCraftingTable(table);
         Dispatcher.sendTo(new PacketCraftingTable(table), player);
      }

   }
}
