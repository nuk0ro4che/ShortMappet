package mchorse.mappet;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import mchorse.mappet.api.huds.HUDScene;
import mchorse.mappet.api.quests.Quest;
import mchorse.mappet.api.quests.Quests;
import mchorse.mappet.api.scripts.code.entities.ScriptEntity;
import mchorse.mappet.api.scripts.code.entities.ScriptEntityItem;
import mchorse.mappet.api.scripts.code.entities.ScriptPlayer;
import mchorse.mappet.api.scripts.code.entities.ai.repeatingCommand.EntityAIRepeatingCommand;
import mchorse.mappet.api.scripts.code.entities.ai.repeatingCommand.RepeatingCommandDataStorage;
import mchorse.mappet.api.scripts.code.entities.ai.rotations.EntityAIRotations;
import mchorse.mappet.api.scripts.code.entities.ai.rotations.RotationDataStorage;
import mchorse.mappet.api.scripts.code.items.ScriptInventory;
import mchorse.mappet.api.scripts.code.sounds.ManagedSoundRegistry;
import mchorse.mappet.api.scripts.code.items.ScriptItemStack;
import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import mchorse.mappet.api.scripts.user.entities.IScriptPlayer;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.IExecutable;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.CharacterProvider;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.client.KeyboardHandler;
import mchorse.mappet.client.RenderingHandler;
import mchorse.mappet.client.SoundPack;
import mchorse.mappet.commands.data.CommandDataClear;
import mchorse.mappet.compat.EntityData;
import mchorse.mappet.compat.EntityGoals;
import mchorse.mappet.compat.events.Event;
import mchorse.mappet.compat.voicechat.SimpleVoiceChatBridge;
import mchorse.mappet.compat.events.EventPriority;
import mchorse.mappet.compat.events.SubscribeEvent;
import mchorse.mappet.compat.events.legacy.LegacyEvents;
import mchorse.mappet.entities.EntityNpc;
import mchorse.mappet.entities.utils.MappetNpcRespawnManager;
import mchorse.mappet.events.StateChangedEvent;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.events.PacketEventHotkeys;
import mchorse.mappet.network.common.huds.PacketHUDScene;
import mchorse.mappet.network.common.npc.PacketNpcJump;
import mchorse.mappet.network.common.quests.PacketQuest;
import mchorse.mappet.network.common.quests.PacketQuests;
import mchorse.mappet.network.common.scripts.PacketClick;
import mchorse.mappet.network.common.scripts.PacketCancelDeath;
import mchorse.mappet.network.common.content.PacketClientSettings;
import mchorse.mappet.utils.RunnableExecutionFork;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1263;
import net.minecraft.class_1268;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1676;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3966;
import net.minecraft.class_7923;

public class EventHandler {
   public static final class_2960 CAPABILITY = new class_2960("mappet", "character");
   private static Boolean isMohist;
   private Set<class_1657> playersToCheck = new HashSet();
   private List<IExecutable> executables = new ArrayList();
   private List<IExecutable> secondList = new ArrayList();
   private Set<String> cancelledExecutableIds = new HashSet();
   private DataContext context;
   private Set<UUID> loggedInPlayers = new HashSet();
   private static Set<Class<? extends Event>> registeredEvents = new HashSet();

   private static boolean isMohist() {
      if (isMohist != null) {
         return isMohist;
      } else {
         try {
            Class.forName("com.mohistmc.MohistMC");
            isMohist = true;
         } catch (Exception var1) {
            isMohist = false;
         }

         return isMohist;
      }
   }

   public List<String> getIds() {
      List<String> ids = new ArrayList();

      for(IExecutable executable : this.executables) {
         ids.add(executable.getId());
      }

      return Lists.newArrayList(Sets.newLinkedHashSet(ids));
   }

   public void addExecutables(List<IExecutable> executionForks) {
      this.executables.addAll(executionForks);
   }

   public void addExecutable(IExecutable executable) {
      this.executables.add(executable);
   }

   public int removeExecutables(String id) {
      int size = this.executables.size();
      this.executables.removeIf((e) -> e.getId() != null && e.getId().equals(id));
      return size - this.executables.size();
   }

   public boolean removeExecutable(String taskId) {
      if (taskId == null || taskId.isEmpty()) {
         return false;
      }

      boolean found = false;
      for (IExecutable executable : this.executables) {
         found |= taskId.equals(executable.getTaskId());
      }
      for (IExecutable executable : this.secondList) {
         found |= taskId.equals(executable.getTaskId());
      }

      if (found) {
         this.cancelledExecutableIds.add(taskId);
      }

      return found;
   }

   public void reset() {
      this.playersToCheck.clear();
      this.executables.clear();
      this.secondList.clear();
      this.cancelledExecutableIds.clear();
      this.context = null;
   }

   public void trigger(Event event, Trigger trigger, DataContext context) {
      context.getValues().put("event", event);
      trigger.trigger(context);
      if (event.isCancelable() && context.isCanceled()) {
         if (event instanceof LegacyEvents.LivingEquipmentChangeEvent || event instanceof LegacyEvents.TickEvent.PlayerTickEvent) {
            return;
         }

         event.setCanceled(true);
      }

   }

   public static String getEventClassName(Class<? extends Event> clazz) {
      return clazz.getName().replace("$", ".");
   }

   public static Set<Class<? extends Event>> getRegisteredEvents() {
      return Collections.emptySet();
   }

   @SubscribeEvent
   public void onPlayerChat(LegacyEvents.ServerChatEvent event) {
      if (!Mappet.settings.playerChat.isEmpty()) {
         DataContext context = (new DataContext(event.getPlayer())).set("message", event.getMessage());
         this.trigger(event, Mappet.settings.playerChat, context);
      }

   }

   @SubscribeEvent
   public void onPlayerBreakBlock(LegacyEvents.BlockEvent.BreakEvent event) {
      if (!Mappet.settings.blockBreak.isEmpty()) {
         class_2680 state = event.getState();
         DataContext context = (new DataContext(event.getPlayer())).set("block", class_7923.field_41175.method_10221(state.method_26204()).toString()).set("meta", (double)state.method_26204().method_9595().method_11662().indexOf(state)).set("x", (double)event.getPos().method_10263()).set("y", (double)event.getPos().method_10264()).set("z", (double)event.getPos().method_10260());
         this.trigger(event, Mappet.settings.blockBreak, context);
      }

   }

   @SubscribeEvent
   public void onPlayerPlaceBlock(LegacyEvents.BlockEvent.PlaceEvent event) {
      if (!Mappet.settings.blockPlace.isEmpty()) {
         class_2680 state = event.getPlacedBlock();
         DataContext context = (new DataContext(event.getPlayer())).set("block", class_7923.field_41175.method_10221(state.method_26204()).toString()).set("meta", (double)state.method_26204().method_9595().method_11662().indexOf(state)).set("x", (double)event.getPos().method_10263()).set("y", (double)event.getPos().method_10264()).set("z", (double)event.getPos().method_10260());
         this.trigger(event, Mappet.settings.blockPlace, context);
      }

   }

   @SubscribeEvent
   public void onEntityHurt(LegacyEvents.LivingDamageEvent event) {
      class_1282 source = event.getSource();
      class_1309 attacker = source.method_5529() instanceof class_1309 ? (class_1309)source.method_5529() : null;
      if (Mappet.settings != null && Mappet.settings.entityDamaged != null && !Mappet.settings.entityDamaged.isEmpty()) {
         DataContext context = (new DataContext(event.getMobEntity(), source.method_5529())).set("damage", (double)event.getAmount());
         context.getValues().put("damageType", source.method_5525());
         context.getValues().put("attacker", ScriptEntity.create(attacker));
         this.trigger(event, Mappet.settings.entityDamaged, context);
      }
   }

   @SubscribeEvent
   public void onEntityAttacked(LegacyEvents.LivingAttackEvent event) {
      class_1282 source = event.getSource();
      if (event.getEntity() != null && !event.getEntity().method_37908().field_9236 && Mappet.settings != null && Mappet.settings.entityAttacked != null && !Mappet.settings.entityAttacked.isEmpty()) {
         DataContext context = (new DataContext(event.getMobEntity(), source.method_5529())).set("damage", (double)event.getAmount());
         context.getValues().put("damageType", source.method_5525());
         this.trigger(event, Mappet.settings.entityAttacked, context);
      }
   }

   @SubscribeEvent
   public void onPlayerOpenOrCloseContainer(LegacyEvents.PlayerContainerEvent event) {
      Trigger trigger = event instanceof LegacyEvents.PlayerContainerEvent.Close ? Mappet.settings.playerCloseContainer : Mappet.settings.playerOpenContainer;
      this.playersToCheck.add(event.getPlayerEntity());
      if (!trigger.isEmpty()) {
         class_1263 container = event.getContainer();
         DataContext context = new DataContext(event.getPlayerEntity());
         if (container instanceof class_2586) {
            class_2586 blockEntity = (class_2586)container;
            class_2338 pos = blockEntity.method_11016();
            context.set("x", (double)pos.method_10263());
            context.set("y", (double)pos.method_10264());
            context.set("z", (double)pos.method_10260());
         }

         if (container != null) {
            context.getValues().put("inventory", new ScriptInventory(container));
         }

         trigger.trigger(context);
      }
   }

   @SubscribeEvent
   @Environment(EnvType.CLIENT)
   public void onPlayerLeftClickEmpty(LegacyEvents.PlayerInteractEvent.LeftClickEmpty event) {
      if (event.getPlayerEntity().method_37908().field_9236) {
         Dispatcher.sendToServer(new PacketClick(class_1268.field_5808));
      }
   }

   @SubscribeEvent
   @Environment(EnvType.CLIENT)
   public void onPlayerRightClickEmpty(LegacyEvents.PlayerInteractEvent.RightClickEmpty event) {
      if (event.getPlayerEntity().method_37908().field_9236 && event.getHand() != class_1268.field_5810) {
         Dispatcher.sendToServer(new PacketClick(class_1268.field_5810));
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerRightClickItem(LegacyEvents.PlayerInteractEvent.RightClickItem event) {
      class_1657 player = event.getPlayerEntity();
      if (!player.method_37908().field_9236 && !Mappet.settings.playerItemInteract.isEmpty()) {
         DataContext context = (new DataContext(player)).set("x", (double)event.getPos().method_10263()).set("y", (double)event.getPos().method_10264()).set("z", (double)event.getPos().method_10260()).set("hand", event.getHand() == class_1268.field_5808 ? "main" : "off");
         this.trigger(event, Mappet.settings.playerItemInteract, context);
      }
   }

   @SubscribeEvent
   @Environment(EnvType.CLIENT)
   public void onPlayerLeftClick(LegacyEvents.PlayerInteractEvent.LeftClickBlock event) {
      class_1657 player = event.getPlayerEntity();
      if (!player.method_37908().field_9236 && !Mappet.settings.blockClick.isEmpty()) {
         DataContext context = (new DataContext(player)).set("x", (double)event.getPos().method_10263()).set("y", (double)event.getPos().method_10264()).set("z", (double)event.getPos().method_10260()).set("hand", event.getHand() == class_1268.field_5808 ? "main" : "off");
         this.trigger(event, Mappet.settings.blockClick, context);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerRightClickBlock(LegacyEvents.PlayerInteractEvent.RightClickBlock event) {
      class_1657 player = event.getPlayerEntity();
      if (!player.method_37908().field_9236 && !Mappet.settings.blockInteract.isEmpty()) {
         class_2680 state = event.getWorld().method_8320(event.getPos());
         DataContext context = (new DataContext(player)).set("block", class_7923.field_41175.method_10221(state.method_26204()).toString()).set("meta", (double)state.method_26204().method_9595().method_11662().indexOf(state)).set("x", (double)event.getPos().method_10263()).set("y", (double)event.getPos().method_10264()).set("z", (double)event.getPos().method_10260()).set("hand", event.getHand() == class_1268.field_5808 ? "main" : "off");
         this.trigger(event, Mappet.settings.blockInteract, context);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerInteractWithEntity(LegacyEvents.PlayerInteractEvent.EntityInteract event) {
      class_1657 player = event.getPlayerEntity();
      if (!player.method_37908().field_9236 && !Mappet.settings.playerEntityInteract.isEmpty()) {
         DataContext context = (new DataContext(player, event.getTarget())).set("hand", event.getHand() == class_1268.field_5808 ? "main" : "off");
         this.trigger(event, Mappet.settings.playerEntityInteract, context);
      }
   }

   @SubscribeEvent
   public void attachPlayerCapability(LegacyEvents.AttachCapabilitiesEvent<class_1297> event) {
      if (event.getObject() instanceof class_1657) {
         event.addCapability(CAPABILITY, new CharacterProvider());
      }

   }

   @SubscribeEvent
   @Environment(EnvType.CLIENT)
   public void onPlayerClientLogsIn(LegacyEvents.PlayerEvent.PlayerLoggedInEvent event) {
      class_3222 player = (class_3222)event.player;
      if ((Boolean)Mappet.loadCustomSoundsOnLogin.get()) {
         IScriptPlayer scriptPlayer = new ScriptPlayer(player);

         for(String sound : SoundPack.getCustomSoundEvents()) {
            scriptPlayer.playStaticSound(sound, 1.0E-9F, 1.0F);
            CommonProxy.eventHandler.addExecutable(new RunnableExecutionFork(1, () -> scriptPlayer.stopSound(sound)));
         }
      }

   }

   @SubscribeEvent
   public void onPlayerLogsIn(LegacyEvents.PlayerEvent.PlayerLoggedInEvent event) {
      class_3222 player = (class_3222)event.player;
      ICharacter character = Character.get(player);
      Instant lastClear = Mappet.data.getLastClear();
      if (character != null) {
         if (character.getLastClear().isBefore(lastClear)) {
            CommandDataClear.clear(player, Mappet.data.getLastInventory());
            character.updateLastClear(lastClear);
         }

         this.syncData(player, character);
      }

      if (Mappet.clientSettings != null) {
         Dispatcher.sendTo(new PacketClientSettings(Mappet.clientSettings.serializeNBT()), player);
      }

      Map<String, List<HUDScene>> displayedHUDs = character.getDisplayedHUDs();

      for(Map.Entry<String, List<HUDScene>> entry : displayedHUDs.entrySet()) {
         String id = (String)entry.getKey();

         for(HUDScene scene : entry.getValue()) {
            Dispatcher.sendTo(new PacketHUDScene(id, scene.serializeNBT()), player);
         }
      }

      for(class_3222 p : Mappet.server.method_3760().method_14571()) {
         ICharacter c = Character.get(p);
         if (c != null) {
            Map<String, List<HUDScene>> displayed = c.getDisplayedHUDs();

            for(Map.Entry<String, List<HUDScene>> entry : displayed.entrySet()) {
               String id = (String)entry.getKey();

               for(HUDScene scene : entry.getValue()) {
                  if (scene.global) {
                     Dispatcher.sendTo(new PacketHUDScene(id, scene.serializeNBT()), player);
                  }
               }
            }
         }
      }

      if (!Mappet.settings.playerLogIn.isEmpty()) {
         DataContext context = new DataContext(event.player);
         Mappet.settings.playerLogIn.trigger(context);
      }

      this.loggedInPlayers.add(player.method_5667());
   }

   @SubscribeEvent
   public void onPlayerLogsOut(LegacyEvents.PlayerEvent.PlayerLoggedOutEvent event) {
      if (!Mappet.settings.playerLogOut.isEmpty()) {
         DataContext context = new DataContext(event.player);
         Mappet.settings.playerLogOut.trigger(context);
      }

      if (event.player instanceof class_3222) {
         ManagedSoundRegistry.forget((class_3222)event.player);
      }

      this.loggedInPlayers.remove(event.player.method_5667());
   }

   @SubscribeEvent
   public void onPlayerClone(LegacyEvents.PlayerEvent.Clone event) {
      class_1657 player = event.getPlayerEntity();
      ICharacter character = Character.get(player);
      ICharacter oldCharacter = Character.get(event.getOriginal());
      if (!isMohist()) {
         character.copy(oldCharacter, player);
      }

   }

   @SubscribeEvent
   public void onPlayerSpawn(LegacyEvents.PlayerEvent.PlayerRespawnEvent event) {
      if (!event.player.method_37908().field_9236) {
         class_3222 player = (class_3222)event.player;
         ICharacter character = Character.get(player);
         this.syncData(player, character);
         if (this.loggedInPlayers.contains(player.method_5667()) && !Mappet.settings.playerRespawn.isEmpty()) {
            Mappet.settings.playerRespawn.trigger(new DataContext(player));
         }
      }

   }

   private void syncData(class_3222 player, ICharacter character) {
      if (!character.getQuests().quests.isEmpty()) {
         character.getQuests().initiate(player);
         Dispatcher.sendTo(new PacketQuests(character.getQuests()), player);
      }

      if (!Mappet.settings.hotkeys.hotkeys.isEmpty()) {
         Dispatcher.sendTo(new PacketEventHotkeys(Mappet.settings), player);
      }

   }

   @SubscribeEvent
   public void onPlayerPickUp(LegacyEvents.ItemEntityPickupEvent event) {
      this.playersToCheck.add(event.getPlayerEntity());
      if (!Mappet.settings.playerItemPickup.isEmpty()) {
         DataContext context = new DataContext(event.getPlayerEntity());
         context.getValues().put("item", ScriptItemStack.create(event.getItem().method_6983()));
         context.getValues().put("entityItem", ScriptEntityItem.create(event.getItem()));
         this.trigger(event, Mappet.settings.playerItemPickup, context);
      }

   }

   @SubscribeEvent
   public void onPlayerToss(LegacyEvents.ItemTossEvent event) {
      if (!event.getPlayer().method_37908().field_9236) {
         if (!Mappet.settings.playerItemToss.isEmpty()) {
            DataContext context = new DataContext(event.getPlayer());
            context.getValues().put("entityItem", ScriptEntityItem.create(event.getItemEntity()));
            this.trigger(event, Mappet.settings.playerItemToss, context);
         }

      }
   }

   @SubscribeEvent
   public void onMobKilled(LegacyEvents.LivingDeathEvent event) {
      if (!event.getEntity().method_37908().field_9236 && Mappet.settings != null) {
         class_1297 source = event.getSource().method_5529();
         String damageType = event.getSource().method_5525();
         Trigger trigger = Mappet.settings.entityDeath;
         DataContext context = null;
         if (!trigger.isEmpty()) {
            context = new DataContext(event.getMobEntity(), source);
            context.getValues().put("damageType", damageType);
            if (source != null) {
               context.getValues().put("killer", ScriptEntity.create(source));
            }

            class_1297 thrower = null;
            if (source instanceof class_1676) {
               thrower = ((class_1676)source).method_24921();
            }

            if (thrower != null) {
               context.getValues().put("thrower", ScriptEntity.create(thrower));
            }

            this.trigger(event, trigger, context);
         }

         
         if (context != null && context.isCanceled()) {
            class_1309 entity = event.getEntity();
            entity.method_6033(1.0F);
            if (entity instanceof class_1657) {
               Dispatcher.sendTo(new PacketCancelDeath(true), (class_3222) entity);
            }
         }

         if (source instanceof class_1657) {
            class_1657 killer = (class_1657)source;
            ICharacter character = Character.get(killer);
            if (character != null) {
               for(Quest quest : character.getQuests().quests.values()) {
                  quest.mobWasKilled(killer, event.getEntity());
               }

               this.playersToCheck.add(killer);
            }
         }

      }
   }

   @SubscribeEvent
   public void onEntityJoinWorld(LegacyEvents.EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof class_1308) {
         class_1308 entityLiving = (class_1308)event.getEntity();
         RotationDataStorage rotationDataStorage = RotationDataStorage.getRotationDataStorage(event.getWorld());
         RotationDataStorage.RotationData rotationData = rotationDataStorage.getRotationData(entityLiving.method_5667());
         if (rotationData != null) {
            float yaw = rotationData.yaw;
            float pitch = rotationData.pitch;
            float yawHead = rotationData.yawHead;
            EntityGoals.goals(entityLiving).method_6277(0, new EntityAIRotations(entityLiving, yaw, pitch, yawHead, 1.0F));
         }

         RepeatingCommandDataStorage repeatingCommandDataStorage = RepeatingCommandDataStorage.getRepeatingCommandDataStorage(event.getWorld());
         List<RepeatingCommandDataStorage.RepeatingCommandData> repeatingCommandDataList = repeatingCommandDataStorage.getRepeatingCommandData(entityLiving.method_5667());
         if (repeatingCommandDataList != null) {
            for(RepeatingCommandDataStorage.RepeatingCommandData repeatingCommandData : repeatingCommandDataList) {
               String command = repeatingCommandData.command;
               int frequency = repeatingCommandData.frequency;
               EntityGoals.goals(entityLiving).method_6277(10, new EntityAIRepeatingCommand(entityLiving, command, frequency));
            }
         }
      }

   }

   List<class_1297> getAllEntities() {
      List<class_1297> entities = new ArrayList();

      try {
         for(class_3218 world : Mappet.server.method_3738()) {
            Iterable<class_1297> var10000 = world.method_27909();
            Objects.requireNonNull(entities);
            var10000.forEach(entities::add);
         }
      } catch (Exception var4) {
      }

      return entities;
   }

   @SubscribeEvent
   public void onServerTick(LegacyEvents.TickEvent.ServerTickEvent event) {
      if (event.phase != LegacyEvents.TickEvent.Phase.START) {
         SimpleVoiceChatBridge.tick();
         for(class_1297 entity : this.getAllEntities()) {
            if (entity != null) {
               if (EntityData.get(entity).method_10577("positionLocked")) {
                  IScriptEntity scriptEntity = ScriptEntity.create(entity);
                  scriptEntity.setPosition(EntityData.get(entity).method_10574("lockX"), EntityData.get(entity).method_10574("lockY"), EntityData.get(entity).method_10574("lockZ"));
                  scriptEntity.setMotion((double)0.0F, (double)0.0F, (double)0.0F);
               }

               if (EntityData.get(entity).method_10577("rotationLocked")) {
                  IScriptEntity scriptEntity = ScriptEntity.create(entity);
                  scriptEntity.setRotations(EntityData.get(entity).method_10583("lockPitch"), EntityData.get(entity).method_10583("lockYaw"), EntityData.get(entity).method_10583("lockYawHead"));
               }
            }
         }

         for(class_1657 player : this.playersToCheck) {
            ICharacter character = Character.get(player);
            if (character != null) {
               Quests quests = character.getQuests();
               Iterator<Map.Entry<String, Quest>> it = quests.quests.entrySet().iterator();
               quests.iterating = true;

               while(it.hasNext()) {
                  Map.Entry<String, Quest> entry = (Map.Entry)it.next();
                  Quest quest = (Quest)entry.getValue();
                  if (quest.instant && quest.rewardIfComplete(player)) {
                     it.remove();
                     Dispatcher.sendTo(new PacketQuest((String)entry.getKey(), (Quest)null), (class_3222)player);
                  } else {
                     Dispatcher.sendTo(new PacketQuest((String)entry.getKey(), (Quest)entry.getValue()), (class_3222)player);
                  }
               }

               quests.flush(player);
            }
         }

         this.playersToCheck.clear();
         if (!this.executables.isEmpty()) {
            this.secondList.addAll(this.executables);
            this.executables.clear();
            this.secondList.removeIf((executable) -> this.cancelledExecutableIds.contains(executable.getTaskId()) || executable.update());
            this.secondList.addAll(this.executables);
            this.executables.clear();
            this.executables.addAll(this.secondList);
            this.secondList.clear();
            this.cancelledExecutableIds.clear();
         }

         if (!Mappet.settings.serverTick.isEmpty()) {
            if (this.context == null) {
               this.context = new DataContext(Mappet.server);
            }

            Mappet.settings.serverTick.trigger(this.context);
            this.context.cancel(false);
         }

      }
   }

   public void onEntityLanded(class_1297 entity, double distance) {
      if (entity.method_37908().field_9236 || Mappet.settings.entityLanded.isEmpty()) {
         return;
      }

      String value;
      if (entity instanceof class_1657) {
         value = "player";
      } else if (entity instanceof class_1308) {
         value = "mob";
      } else if (entity instanceof class_1542) {
         value = "item";
      } else {
         value = "other";
      }

      Mappet.settings.entityLanded.trigger(new DataContext(entity).set("falling", value).set("distance", Math.max(0.0F, distance)));
   }

   @SubscribeEvent
   public void onPlayerTick(LegacyEvents.TickEvent.PlayerTickEvent event) {
      if (event.phase != LegacyEvents.TickEvent.Phase.START) {
         if (!event.player.method_37908().field_9236 && !Mappet.settings.playerTick.isEmpty()) {
            Mappet.settings.playerTick.trigger(new DataContext(event.player));
         }

         ICharacter character = Character.get(event.player);
         if (character != null && !event.player.method_37908().field_9236) {
            character.getPositionCache().updatePlayer(event.player);
            ((Character)character).updateDisplayedHUDsList();
         }

         if (event.player.method_37908().field_9236 && event.player == class_310.method_1551().field_1724) {
            this.onPlayerTickClient(event);
         }

      }
   }

   @Environment(EnvType.CLIENT)
   private void onPlayerTickClient(LegacyEvents.TickEvent.PlayerTickEvent event) {
      RenderingHandler.update();
      KeyboardHandler.updateHeldKeys();
   }

   @SubscribeEvent
   public void onStateChange(StateChangedEvent event) {
      Trigger trigger = Mappet.settings.stateChanged;
      if (!trigger.isEmpty()) {
         this.handleStateChangedEvent(event, trigger);
      }

      for(class_1657 player : Mappet.server.method_3760().method_14571()) {
         ICharacter character = Character.get(player);
         if (character != null && (event.isGlobal() || character.getStates() == event.states)) {
            int i = 0;

            for(Quest quest : character.getQuests().quests.values()) {
               i += quest.stateWasUpdated(player) ? 1 : 0;
            }

            if (i > 0) {
               this.playersToCheck.add(player);
            }
         }
      }

   }

   private void handleStateChangedEvent(StateChangedEvent event, Trigger trigger) {
      if (event.isGlobal()) {
         this.handleGlobalStateChangedEvent(event, trigger);
      } else {
         this.handlePlayerStateChangedEvent(event, trigger);
         this.handleNpcStateChangedEvent(event, trigger);
      }

   }

   private void handleGlobalStateChangedEvent(StateChangedEvent event, Trigger trigger) {
      this.context = new DataContext(Mappet.server);
      this.setStateChangedEventValues(this.context, event);
      trigger.trigger(this.context);
   }

   private void handlePlayerStateChangedEvent(StateChangedEvent event, Trigger trigger) {
      for(class_1657 player : Mappet.server.method_3760().method_14571()) {
         ICharacter character = Character.get(player);
         if (character != null && character.getStates() == event.states) {
            this.context = new DataContext(player);
            this.setStateChangedEventValues(this.context, event);
            this.context.getValues().put("entity", ScriptEntity.create(player));
            trigger.trigger(this.context);
         }
      }

   }

   private void handleNpcStateChangedEvent(StateChangedEvent event, Trigger trigger) {
      for(EntityNpc npc : this.getAllNpcs()) {
         if (npc != null && npc.getStates() == event.states) {
            this.context = new DataContext(npc);
            this.setStateChangedEventValues(this.context, event);
            this.context.getValues().put("entity", ScriptEntity.create(npc));
            trigger.trigger(this.context);
         }
      }

   }

   private void setStateChangedEventValues(DataContext context, StateChangedEvent event) {
      context.getValues().put("key", event.key);
      context.getValues().put("current", event.current);
      context.getValues().put("previous", event.previous);
   }

   private List<EntityNpc> getAllNpcs() {
      List<EntityNpc> npcs = new ArrayList();

      try {
         for(class_1937 world : Mappet.server.method_3738()) {
            ((class_3218)world).method_27909().forEach((entity) -> {
               if (entity instanceof EntityNpc npc) {
                  npcs.add(npc);
               }

            });
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return npcs;
   }

   @SubscribeEvent
   public void onWorldTick(LegacyEvents.TickEvent.WorldTickEvent event) {
      MappetNpcRespawnManager respawnManager = MappetNpcRespawnManager.get(event.world);
      respawnManager.onTick();
   }

   @SubscribeEvent
   public void onLivingKnockBack(LegacyEvents.LivingKnockBackEvent event) {
      class_1309 target = event.getMobEntity();
      class_1297 attacker = event.getMobEntity().method_6065();
      if (target != null && EntityData.get(target).method_10577("positionLocked")) {
         event.setCanceled(true);
      }

      if (!target.method_37908().field_9236 && !Mappet.settings.livingKnockBack.isEmpty()) {
         DataContext context = (new DataContext(target, attacker)).set("strength", (double)event.getStrength()).set("ratioX", (double)event.getRatioX()).set("ratioZ", (double)event.getRatioZ());
         context.getValues().put("attacker", ScriptEntity.create(attacker));
         this.trigger(event, Mappet.settings.livingKnockBack, context);
      }
   }

   @SubscribeEvent
   public void onProjectileImpact(LegacyEvents.ProjectileImpactEvent event) {
      if (!event.getEntity().method_37908().field_9236) {
         Trigger trigger = Mappet.settings.projectileImpact;
         if (!trigger.isEmpty()) {
            class_239 var5 = event.getRayTraceResult();
            class_1297 var10000;
            if (var5 instanceof class_3966) {
               class_3966 hit = (class_3966)var5;
               var10000 = hit.method_17782();
            } else {
               var10000 = null;
            }

            class_1297 hitEntity = var10000;
            DataContext context = new DataContext(event.getEntity(), hitEntity);
            context.getValues().put("pos", new ScriptVector(event.getRayTraceResult().method_17784()));
            context.getValues().put("projectile", ScriptEntity.create(event.getEntity()));
            if (hitEntity != null && !context.getValues().containsKey("entity")) {
               context.getValues().put("entity", ScriptEntity.create(hitEntity));
            }

            class_1297 thrower = null;
            if (event.getEntity() instanceof class_1676) {
               thrower = ((class_1676)event.getEntity()).method_24921();
            }

            if (thrower != null) {
               context.getValues().put("thrower", ScriptEntity.create(thrower));
            }

            this.trigger(event, trigger, context);
         }

      }
   }

   @SubscribeEvent
   public void onLivingEquipmentChange(LegacyEvents.LivingEquipmentChangeEvent event) {
      if (!event.getEntity().method_37908().field_9236 && Mappet.settings != null) {
         Trigger trigger = Mappet.settings.onLivingEquipmentChange;
         if (!trigger.isEmpty()) {
            DataContext context = new DataContext(event.getEntity());
            context.getValues().put("item", ScriptItemStack.create(event.getTo()));
            context.getValues().put("previous", ScriptItemStack.create(event.getFrom()));
            if (event.getEntity() instanceof class_3222) {
               ScriptPlayer player = new ScriptPlayer((class_3222)event.getEntity());
               context.getValues().put("slot", player.getHotbarIndex());
            } else {
               context.getValues().put("slot", event.getSlot().method_5927());
            }

            this.trigger(event, trigger, context);
         }

      }
   }

   @SubscribeEvent
   @Environment(EnvType.CLIENT)
   public void onKeyInput(LegacyEvents.InputEvent.KeyInputEvent event) {
      if (class_310.method_1551().field_1690.field_1903.method_1434()) {
         class_1657 player = class_310.method_1551().field_1724;
         if (player.method_5765() && player.method_5854() instanceof EntityNpc && (Boolean)((EntityNpc)player.method_5854()).getState().canBeSteered.get()) {
            float jumpPower = (Float)((EntityNpc)player.method_5854()).getState().jumpPower.get();
            Dispatcher.sendToServer(new PacketNpcJump(player.method_5854().method_5628(), jumpPower));
         }
      }

   }

   
   public void onManagedSoundFinished(class_3222 player, String id, String name, ManagedSoundRegistry.State state) {
      if (!Mappet.settings.soundEnded.isEmpty()) {
         DataContext context = new DataContext(player);
         context.getValues().put("id", id);
         context.getValues().put("name", name);
         context.getValues().put("volume", (double) state.volume);
         context.getValues().put("pitch", (double) state.pitch);
         context.getValues().put("position", new ScriptVector(state.x, state.y, state.z));
         Mappet.settings.soundEnded.trigger(context);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerLeashEntity(LegacyEvents.PlayerInteractEvent.EntityInteract event) {
      class_1657 player = event.getPlayerEntity();
      class_1799 item = player.method_5998(event.getHand());
      if (!player.method_37908().field_9236 && item.method_7909() == class_1802.field_8719 && !Mappet.settings.playerEntityLeash.isEmpty()) {
         class_1297 target = event.getTarget();
         if (target instanceof class_1308 && !((class_1308)target).method_5934()) {
            DataContext context = (new DataContext(player, target)).set("hand", event.getHand() == class_1268.field_5808 ? "main" : "off");
            this.trigger(event, Mappet.settings.playerEntityLeash, context);
         }
      }
   }
}
