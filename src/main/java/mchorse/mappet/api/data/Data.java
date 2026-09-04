package mchorse.mappet.api.data;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.states.States;
import mchorse.mappet.api.utils.AbstractData;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.compat.NbtCompat;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2371;
import net.minecraft.class_2487;
import net.minecraft.class_2499;

public class Data extends AbstractData {
   public States global = new States();
   public States player = new States();
   public class_2371<class_1799> inventory = class_2371.method_10211();

   public void save(class_1657 player) {
      ICharacter character = Character.get(player);
      if (character != null) {
         this.global.copy(Mappet.states);
         this.player.copy(character.getStates());
         int i = 0;

         for(int c = player.method_31548().method_5439(); i < c; ++i) {
            this.inventory.add(player.method_31548().method_5438(i).method_7972());
         }
      }

   }

   public void apply(class_1657 player, boolean global) {
      ICharacter character = Character.get(player);
      if (character != null) {
         if (global) {
            Mappet.states.copy(this.global);
         }

         character.getStates().copy(this.player);
         player.method_31548().method_5448();
         int i = 0;

         for(int c = Math.min(this.inventory.size(), player.method_31548().method_5439()); i < c; ++i) {
            player.method_31548().method_5447(i, ((class_1799)this.inventory.get(i)).method_7972());
         }
      }

   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10566("Global", this.global.serializeNBT());
      tag.method_10566("Player", this.player.serializeNBT());
      class_2499 inventory = new class_2499();

      for(class_1799 stack : this.inventory) {
         inventory.add(NbtCompat.write(stack));
      }

      tag.method_10566("Inventory", inventory);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10545("Global")) {
         this.global.deserializeNBT(tag.method_10562("Global"));
      }

      if (tag.method_10545("Player")) {
         this.player.deserializeNBT(tag.method_10562("Player"));
      }

      class_2499 inventory = tag.method_10554("Inventory", 10);

      for(int i = 0; i < inventory.size(); ++i) {
         this.inventory.add(class_1799.method_7915(inventory.method_10602(i)));
      }

   }
}
