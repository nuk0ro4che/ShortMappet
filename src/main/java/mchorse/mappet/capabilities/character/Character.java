package mchorse.mappet.capabilities.character;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.crafting.CraftingTable;
import mchorse.mappet.camera.CameraShake;
import mchorse.mappet.api.dialogues.Dialogue;
import mchorse.mappet.api.dialogues.DialogueContext;
import mchorse.mappet.api.huds.HUDMorph;
import mchorse.mappet.api.huds.HUDScene;
import mchorse.mappet.api.quests.Quests;
import mchorse.mappet.api.states.States;
import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.huds.PacketHUDMorph;
import mchorse.mappet.network.common.huds.PacketHUDScene;
import mchorse.mappet.utils.CurrentSession;
import mchorse.mappet.utils.PositionCache;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.api.MorphManager;
import net.minecraft.class_1657;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_3222;

public class Character implements ICharacter {
   private class_1657 player;
   private Quests quests = new Quests();
   private States states = new States();
   private CraftingTable table;
   private Dialogue dialogue;
   private DialogueContext dialogueContext;
   private Instant lastClear = Instant.now();
   private PositionCache positionCache = new PositionCache();
   private CurrentSession session = new CurrentSession();
   private CameraShake cameraShake = new CameraShake();
   private UIContext uiContext;
   private Map<String, List<HUDScene>> displayedHUDs = new HashMap();

   public static Character get(class_1657 player) {
      if (player instanceof CharacterHolder holder) {
         Character character = holder.mappet$getCharacter();
         character.player = player;
         return character;
      } else {
         return null;
      }
   }

   public States getStates() {
      return this.states;
   }

   public Quests getQuests() {
      return this.quests;
   }

   public void setCraftingTable(CraftingTable table) {
      this.table = table;
   }

   public CraftingTable getCraftingTable() {
      return this.table;
   }

   public void setDialogue(Dialogue dialogue, DialogueContext context) {
      if (dialogue == null && this.dialogue != null) {
         this.dialogue.onClose.trigger(this.dialogueContext.data);
      }

      this.dialogue = dialogue;
      this.dialogueContext = context;
   }

   public Dialogue getDialogue() {
      return this.dialogue;
   }

   public DialogueContext getDialogueContext() {
      return this.dialogueContext;
   }

   public Instant getLastClear() {
      return this.lastClear;
   }

   public void updateLastClear(Instant instant) {
      this.lastClear = instant;
   }

   public PositionCache getPositionCache() {
      return this.positionCache;
   }

   public CurrentSession getCurrentSession() {
      return this.session;
   }

   public CameraShake getCameraShake() {
      return this.cameraShake;
   }

   public void copy(ICharacter character, class_1657 player) {
      this.quests.copy(character.getQuests());
      this.states.copy(character.getStates());
      this.lastClear = character.getLastClear();
      this.displayedHUDs = character.getDisplayedHUDs();
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10566("Quests", this.quests.serializeNBT());
      tag.method_10566("States", this.states.serializeNBT());
      tag.method_10582("LastClear", this.lastClear.toString());
      tag.method_10566("DisplayedHUDs", this.serializeDisplayedHUDs());
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10545("Quests")) {
         this.quests.deserializeNBT(tag.method_10562("Quests"));
      }

      if (tag.method_10545("States")) {
         this.states.deserializeNBT(tag.method_10562("States"));
      }

      if (tag.method_10545("LastClear")) {
         try {
            this.lastClear = Instant.parse(tag.method_10558("LastClear"));
         } catch (Exception var3) {
         }
      }

      if (tag.method_10545("DisplayedHUDs")) {
         this.deserializeDisplayedHUDs(tag.method_10562("DisplayedHUDs"));
      }

   }

   public UIContext getUIContext() {
      return this.uiContext;
   }

   public void setUIContext(UIContext context) {
      this.uiContext = context;
   }

   public boolean setupHUD(String id, boolean addToDisplayedList) {
      HUDScene scene = (HUDScene)Mappet.huds.load(id);
      if (scene == null) {
         return false;
      } else {
         Dispatcher.sendTo(new PacketHUDScene(id, scene.serializeNBT()), (class_3222)this.player);
         if (scene.global) {
            for(class_1657 player : this.player.method_37908().method_18456()) {
               if (player != this.player) {
                  Dispatcher.sendTo(new PacketHUDScene(id, scene.serializeNBT()), (class_3222)player);
               }
            }
         }

         if (addToDisplayedList) {
            this.getDisplayedHUDs().put(id, Arrays.asList(scene));
         }

         return true;
      }
   }

   public void changeHUDMorph(String id, int index, class_2487 tag) {
      Dispatcher.sendTo(new PacketHUDMorph(id, index, tag), (class_3222)this.player);
      HUDScene scene = (HUDScene)Mappet.huds.load(id);
      if (scene.global) {
         for(class_1657 player : this.player.method_37908().method_18456()) {
            if (player != this.player) {
               Dispatcher.sendTo(new PacketHUDMorph(id, index, tag), (class_3222)player);
            }
         }
      }

      for(Map.Entry<String, List<HUDScene>> entry : this.getDisplayedHUDs().entrySet()) {
         if (((String)entry.getKey()).equals(id)) {
            List<HUDScene> scenes = (List)entry.getValue();
            if (!scenes.isEmpty()) {
               scene = (HUDScene)scenes.get(0);
               if (scene.morphs.size() > index) {
                  HUDMorph newMorph = ((HUDMorph)scene.morphs.get(index)).copy();
                  newMorph.morph = new Morph(MorphManager.INSTANCE.morphFromNBT(tag));
                  scene.morphs.set(index, newMorph);
               }
            }
         }
      }

   }

   
   public boolean setHUDWorldLighting(String id, boolean worldLighting) {
      return this.setHUDWorldLighting(id, worldLighting, 1.0F);
   }

   public boolean setHUDWorldLighting(String id, boolean worldLighting, float intensity) {
      List<HUDScene> scenes = (List)this.getDisplayedHUDs().get(id);
      if (scenes == null || scenes.isEmpty()) {
         return false;
      }

      HUDScene scene = (HUDScene)scenes.get(0);
      scene.worldLighting = worldLighting;
      scene.worldLightingIntensity = Math.max(0.0F, Math.min(4.0F, intensity));
      Dispatcher.sendTo(new PacketHUDScene(id, scene.serializeNBT()), (class_3222)this.player);
      if (scene.global) {
         for(class_1657 player : this.player.method_37908().method_18456()) {
            if (player != this.player) {
               Dispatcher.sendTo(new PacketHUDScene(id, scene.serializeNBT()), (class_3222)player);
            }
         }
      }

      return true;
   }

   public void closeHUD(String id) {
      Dispatcher.sendTo(new PacketHUDScene(id == null ? "" : id, (class_2487)null), (class_3222)this.player);
      HUDScene scene = (HUDScene)Mappet.huds.load(id);
      if (scene.global) {
         for(class_1657 player : this.player.method_37908().method_18456()) {
            if (player != this.player) {
               Dispatcher.sendTo(new PacketHUDScene(id == null ? "" : id, (class_2487)null), (class_3222)player);
            }
         }
      }

      this.getDisplayedHUDs().remove(id);
   }

   public void closeAllHUD() {
      this.closeHUD((String)null);

      for(Map.Entry<String, List<HUDScene>> entry : this.getDisplayedHUDs().entrySet()) {
         if (((HUDScene)entry.getValue().get(0)).global) {
            for(class_1657 player : this.player.method_37908().method_18456()) {
               if (player != this.player) {
                  Dispatcher.sendTo(new PacketHUDScene((String)entry.getKey(), (class_2487)null), (class_3222)player);
               }
            }
         }
      }

      this.getDisplayedHUDs().clear();
   }

   public Map<String, List<HUDScene>> getDisplayedHUDs() {
      return this.displayedHUDs;
   }

   private class_2487 serializeDisplayedHUDs() {
      return this.getDisplayedHUDsTag();
   }

   private void deserializeDisplayedHUDs(class_2487 tag) {
      this.displayedHUDs.clear();

      for(String key : tag.method_10541()) {
         class_2499 sceneList = tag.method_10554(key, 10);
         List<HUDScene> scenes = new ArrayList();

         for(int i = 0; i < sceneList.size(); ++i) {
            class_2487 sceneTag = sceneList.method_10602(i);
            HUDScene scene = new HUDScene();
            scene.deserializeNBT(sceneTag);
            scenes.add(scene);
         }

         this.displayedHUDs.put(key, scenes);
      }

   }

   public class_2487 getDisplayedHUDsTag() {
      class_2487 tag = new class_2487();

      for(Map.Entry<String, List<HUDScene>> entry : this.displayedHUDs.entrySet()) {
         class_2499 sceneList = new class_2499();

         for(HUDScene scene : entry.getValue()) {
            sceneList.add(scene.serializeNBT());
         }

         tag.method_10566((String)entry.getKey(), sceneList);
      }

      return tag;
   }

   public class_2487 getGlobalDisplayedHUDsTag() {
      class_2487 tag = new class_2487();

      for(Map.Entry<String, List<HUDScene>> entry : this.displayedHUDs.entrySet()) {
         if (((HUDScene)entry.getValue().get(0)).global) {
            class_2499 sceneList = new class_2499();

            for(HUDScene scene : entry.getValue()) {
               sceneList.add(scene.serializeNBT());
            }

            tag.method_10566((String)entry.getKey(), sceneList);
         }
      }

      return tag;
   }

   public void updateDisplayedHUDsList() {
      Iterator<Map.Entry<String, List<HUDScene>>> iterator = this.getDisplayedHUDs().entrySet().iterator();

      while(iterator.hasNext()) {
         Map.Entry<String, List<HUDScene>> entry = (Map.Entry)iterator.next();
         List<HUDScene> scenes = (List)entry.getValue();
         boolean removeScene = false;

         for(HUDScene scene : scenes) {
            List<HUDMorph> morphs = scene.morphs;
            boolean updated = false;

            for(int i = 0; i < morphs.size(); ++i) {
               HUDMorph morph = (HUDMorph)morphs.get(i);
               if (morph.expire > 0) {
                  --morph.expire;
                  if (morph.expire == 0) {
                     morphs.remove(i);
                     --i;
                     updated = true;
                  }
               }
            }

            if (updated && morphs.isEmpty()) {
               removeScene = true;
               break;
            }
         }

         if (removeScene) {
            iterator.remove();
         }
      }

   }
}
