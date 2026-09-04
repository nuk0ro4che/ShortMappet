package mchorse.mappet.api.misc;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.misc.hotkeys.TriggerHotkeys;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mappet.events.RegisterServerTriggerEvent;
import mchorse.mappet.utils.NBTToJsonLike;
import net.minecraft.class_2487;

public class ServerSettings implements INBTSerializable<class_2487> {
   private File file;
   private Map<String, String> keyToAlias = new HashMap();
   public final Map<String, Trigger> registered = new LinkedHashMap();
   public final Map<String, Trigger> registeredForgeTriggers = new LinkedHashMap();
   public final TriggerHotkeys hotkeys = new TriggerHotkeys();
   public final Trigger blockBreak;
   public final Trigger blockPlace;
   public final Trigger blockInteract;
   public final Trigger blockClick;
   public final Trigger entityDamaged;
   public final Trigger entityAttacked;
   public final Trigger entityDeath;
   public final Trigger entityLanded;
   public final Trigger serverLoad;
   public final Trigger serverTick;
   public final Trigger playerChat;
   public final Trigger playerLogIn;
   public final Trigger playerLogOut;
   public final Trigger playerLeftClick;
   public final Trigger playerRightClick;
public final Trigger playerRespawn;
    public final Trigger playerItemPickup;
   public final Trigger playerItemToss;
   public final Trigger playerItemInteract;
   public final Trigger playerEntityInteract;
   public final Trigger playerCloseContainer;
   public final Trigger playerOpenContainer;
   public final Trigger playerJournal;
   public final Trigger livingKnockBack;
   public final Trigger projectileImpact;
   public final Trigger onLivingEquipmentChange;
   public final Trigger playerEntityLeash;
   public final Trigger playerKeyboard;
   public final Trigger mouseInput;
   public final Trigger stateChanged;
   public final Trigger soundEnded;
   public final Trigger playerTick;

   public Trigger register(String key, Trigger trigger) {
      return this.register(key, (String)null, trigger);
   }

   public Trigger register(String key, String alias, Trigger trigger) {
      if (this.registered.containsKey(key)) {
         throw new IllegalStateException("Server trigger '" + key + "' is already registered!");
      } else {
         if (alias != null) {
            this.keyToAlias.put(key, alias);
         }

         this.registered.put(key, trigger);
         return trigger;
      }
   }

   public ServerSettings(File file) {
      this.file = file;
      this.blockBreak = this.register("block_break", "break_block", new Trigger());
      this.blockPlace = this.register("block_place", "place_block", new Trigger());
      this.blockInteract = this.register("block_interact", "interact_block", new Trigger());
      this.blockClick = this.register("block_click", new Trigger());
      this.entityDamaged = this.register("entity_damaged", "damage_entity", new Trigger());
      this.entityAttacked = this.register("entity_attacked", "attack_entity", new Trigger());
      this.entityDeath = this.register("entity_death", new Trigger());
      this.entityLanded = this.register("entity_landed", new Trigger());
      this.serverLoad = this.register("server_load", new Trigger());
      this.serverTick = this.register("server_tick", new Trigger());
      this.playerTick = this.register("player_tick", new Trigger());
      this.playerChat = this.register("player_chat", "chat", new Trigger());
      this.playerLogIn = this.register("player_login", new Trigger());
      this.playerLogOut = this.register("player_logout", new Trigger());
      this.playerLeftClick = this.register("player_lmb", new Trigger());
      this.playerRightClick = this.register("player_rmb", new Trigger());
this.playerRespawn = this.register("player_respawn", new Trigger());
       this.playerItemPickup = this.register("player_item_pickup", new Trigger());
      this.playerItemToss = this.register("player_item_toss", new Trigger());
      this.playerItemInteract = this.register("player_item_interact", new Trigger());
      this.playerEntityInteract = this.register("player_entity_interact", new Trigger());
      this.playerCloseContainer = this.register("player_close_container", new Trigger());
      this.playerOpenContainer = this.register("player_open_container", new Trigger());
      this.playerJournal = this.register("player_journal", new Trigger());
      this.livingKnockBack = this.register("living_knockback", new Trigger());
      this.projectileImpact = this.register("projectile_impact", new Trigger());
      this.onLivingEquipmentChange = this.register("living_equipment_change", new Trigger());
      this.playerEntityLeash = this.register("player_entity_leash", new Trigger());
      this.mouseInput = this.register("mouse_input", "mouse_input", new Trigger());
      this.playerKeyboard = this.register("player_keyboard", "player_keyboard", new Trigger());
      this.stateChanged = this.register("state_changed", new Trigger());
      this.soundEnded = this.register("sound_ended", new Trigger());
      
      Mappet.EVENT_BUS.post(new RegisterServerTriggerEvent(this));
   }

   public void load() {
      if (this.file != null && this.file.isFile()) {
         try {
            class_2487 tag = NBTToJsonLike.read(this.file);
            if (!tag.method_10545("Hotkeys")) {
               File hotkeys = new File(this.file.getParentFile(), "hotkeys.json");
               if (hotkeys.isFile()) {
                  try {
                     class_2487 hotkeysTag = NBTToJsonLike.read(hotkeys);
                     tag.method_10566("Hotkeys", hotkeysTag);
                     hotkeys.delete();
                  } catch (Exception var4) {
                  }
               }
            }

            this.deserializeNBT(tag);
         } catch (Exception e) {
            e.printStackTrace();
         }

      }
   }

   public void save() {
      try {
         NBTToJsonLike.write(this.file, this.serializeNBT());
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      class_2487 triggers = new class_2487();

      for(Map.Entry<String, Trigger> entry : this.registered.entrySet()) {
         this.writeTrigger(triggers, (String)entry.getKey(), (Trigger)entry.getValue());
      }

      if (!triggers.method_33133()) {
         tag.method_10566("Triggers", triggers);
      }

      class_2487 forgeTriggers = new class_2487();

      for(Map.Entry<String, Trigger> entry : this.registeredForgeTriggers.entrySet()) {
         this.writeTrigger(forgeTriggers, (String)entry.getKey(), (Trigger)entry.getValue());
      }

      if (!forgeTriggers.method_33133()) {
         tag.method_10566("ForgeTriggers", forgeTriggers);
      }

      tag.method_10566("Hotkeys", this.hotkeys.serializeNBT());
      return tag;
   }

   private void writeTrigger(class_2487 tag, String key, Trigger trigger) {
      if (trigger != null) {
         class_2487 triggerTag = trigger.serializeNBT();
         if (!triggerTag.method_33133()) {
            tag.method_10566(key, triggerTag);
         }
      }

   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10545("Triggers")) {
         class_2487 triggers = tag.method_10562("Triggers");

         for(Map.Entry<String, Trigger> entry : this.registered.entrySet()) {
            String oldAlias = (String)this.keyToAlias.get(entry.getKey());
            if (triggers.method_10573(oldAlias, 10)) {
               this.readTrigger(triggers, oldAlias, (Trigger)entry.getValue());
            } else {
               this.readTrigger(triggers, (String)entry.getKey(), (Trigger)entry.getValue());
            }
         }
      }

      this.registeredForgeTriggers.clear();
      if (tag.method_10545("ForgeTriggers")) {
         class_2487 forgeTriggers = tag.method_10562("ForgeTriggers");

         for(String key : forgeTriggers.method_10541()) {
            Trigger trigger = new Trigger();
            trigger.deserializeNBT(forgeTriggers.method_10562(key));
            this.registeredForgeTriggers.put(key, trigger);
         }
      }

      if (tag.method_10545("Hotkeys")) {
         this.hotkeys.deserializeNBT(tag.method_10562("Hotkeys"));
      }

   }

   private void readTrigger(class_2487 tag, String key, Trigger trigger) {
      if (tag.method_10573(key, 10)) {
         class_2487 triggerTag = tag.method_10562(key);
         if (!triggerTag.method_33133()) {
            trigger.deserializeNBT(triggerTag);
         }
      }

   }
}
