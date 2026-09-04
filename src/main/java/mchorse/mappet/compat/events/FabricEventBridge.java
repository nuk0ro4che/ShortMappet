package mchorse.mappet.compat.events;

import mchorse.mappet.CommonProxy;
import mchorse.mappet.compat.events.legacy.LegacyEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.class_1269;
import net.minecraft.class_1271;
import net.minecraft.class_3222;

public final class FabricEventBridge {
   private static boolean registered;

   private FabricEventBridge() {
   }

   public static void register() {
      if (!registered) {
         registered = true;
         ServerTickEvents.START_SERVER_TICK.register((ServerTickEvents.StartTick)(server) -> {
            CommonProxy.eventHandler.onServerTick(new LegacyEvents.TickEvent.ServerTickEvent(LegacyEvents.TickEvent.Phase.START));

            for(class_3222 player : server.method_3760().method_14571()) {
               LegacyEvents.TickEvent.PlayerTickEvent event = new LegacyEvents.TickEvent.PlayerTickEvent(player, LegacyEvents.Side.SERVER, LegacyEvents.TickEvent.Phase.START);
               CommonProxy.eventHandler.onPlayerTick(event);
               CommonProxy.scriptedItemEventHandler.onPlayerHoldingScriptedItemTick(event);
               CommonProxy.scriptedItemEventHandler.onPlayerWithScriptedItemTick(event);
            }

         });
         ServerTickEvents.END_SERVER_TICK.register((ServerTickEvents.EndTick)(server) -> {
            CommonProxy.eventHandler.onServerTick(new LegacyEvents.TickEvent.ServerTickEvent(LegacyEvents.TickEvent.Phase.END));

            for(class_3222 player : server.method_3760().method_14571()) {
               LegacyEvents.TickEvent.PlayerTickEvent event = new LegacyEvents.TickEvent.PlayerTickEvent(player, LegacyEvents.Side.SERVER, LegacyEvents.TickEvent.Phase.END);
               CommonProxy.eventHandler.onPlayerTick(event);
               CommonProxy.scriptedItemEventHandler.onPlayerHoldingScriptedItemTick(event);
            }

         });
         ServerTickEvents.START_WORLD_TICK.register((ServerTickEvents.StartWorldTick)(world) -> CommonProxy.eventHandler.onWorldTick(new LegacyEvents.TickEvent.WorldTickEvent(world, LegacyEvents.TickEvent.Phase.START)));
         ServerTickEvents.END_WORLD_TICK.register((ServerTickEvents.EndWorldTick)(world) -> CommonProxy.eventHandler.onWorldTick(new LegacyEvents.TickEvent.WorldTickEvent(world, LegacyEvents.TickEvent.Phase.END)));
         ServerEntityEvents.ENTITY_LOAD.register((ServerEntityEvents.Load)(entity, world) -> CommonProxy.eventHandler.onEntityJoinWorld(new LegacyEvents.EntityJoinWorldEvent(entity, world)));
         ServerPlayConnectionEvents.JOIN.register((ServerPlayConnectionEvents.Join)(handler, sender, server) -> {
            LegacyEvents.PlayerEvent.PlayerLoggedInEvent event = new LegacyEvents.PlayerEvent.PlayerLoggedInEvent(handler.field_14140);
            CommonProxy.eventHandler.onPlayerLogsIn(event);
         });
         ServerPlayConnectionEvents.DISCONNECT.register((ServerPlayConnectionEvents.Disconnect)(handler, server) -> CommonProxy.eventHandler.onPlayerLogsOut(new LegacyEvents.PlayerEvent.PlayerLoggedOutEvent(handler.field_14140)));
         ServerPlayerEvents.COPY_FROM.register((ServerPlayerEvents.CopyFrom)(oldPlayer, newPlayer, alive) -> CommonProxy.eventHandler.onPlayerClone(new LegacyEvents.PlayerEvent.Clone(newPlayer, oldPlayer)));
         ServerPlayerEvents.AFTER_RESPAWN.register((ServerPlayerEvents.AfterRespawn)(oldPlayer, newPlayer, alive) -> CommonProxy.eventHandler.onPlayerSpawn(new LegacyEvents.PlayerEvent.PlayerRespawnEvent(newPlayer)));
         PlayerBlockBreakEvents.BEFORE.register((PlayerBlockBreakEvents.Before)(world, player, pos, state, blockEntity) -> {
            LegacyEvents.BlockEvent.BreakEvent event = new LegacyEvents.BlockEvent.BreakEvent(world, pos, state, player);
            CommonProxy.eventHandler.onPlayerBreakBlock(event);
            CommonProxy.scriptedItemEventHandler.onPlayerWithScriptedItemBreakBlock(event);
            return !event.isCanceled();
         });
         AttackBlockCallback.EVENT.register((AttackBlockCallback)(player, world, hand, pos, direction) -> {
            LegacyEvents.PlayerInteractEvent.LeftClickBlock event = new LegacyEvents.PlayerInteractEvent.LeftClickBlock(player, hand, pos);
            CommonProxy.eventHandler.onPlayerLeftClick(event);
            CommonProxy.scriptedItemEventHandler.onPlayerWithScriptedItemLeftClick(event);
            return event.isCanceled() ? class_1269.field_5814 : class_1269.field_5811;
         });
         UseBlockCallback.EVENT.register((UseBlockCallback)(player, world, hand, hit) -> {
            LegacyEvents.PlayerInteractEvent.RightClickBlock event = new LegacyEvents.PlayerInteractEvent.RightClickBlock(player, hand, hit.method_17777());
            CommonProxy.eventHandler.onPlayerRightClickBlock(event);
            CommonProxy.scriptedItemEventHandler.onPlayerWithScriptedItemRightClickBlock(event);
            return event.isCanceled() ? class_1269.field_5814 : class_1269.field_5811;
         });
         UseItemCallback.EVENT.register((UseItemCallback)(player, world, hand) -> {
            LegacyEvents.PlayerInteractEvent.RightClickItem event = new LegacyEvents.PlayerInteractEvent.RightClickItem(player, hand);
            CommonProxy.eventHandler.onPlayerRightClickItem(event);
            CommonProxy.scriptedItemEventHandler.onScriptedItemRightClick(event);
            return event.isCanceled() ? class_1271.method_22431(player.method_5998(hand)) : class_1271.method_22430(player.method_5998(hand));
         });
         UseEntityCallback.EVENT.register((UseEntityCallback)(player, world, hand, entity, hit) -> {
            LegacyEvents.PlayerInteractEvent.EntityInteract event = new LegacyEvents.PlayerInteractEvent.EntityInteract(player, hand, entity);
            CommonProxy.eventHandler.onPlayerInteractWithEntity(event);
            CommonProxy.scriptedItemEventHandler.onPlayerWithScriptedItemInteractWithEntity(event);
            return event.isCanceled() ? class_1269.field_5814 : class_1269.field_5811;
         });
         ServerMessageEvents.CHAT_MESSAGE.register((ServerMessageEvents.ChatMessage)(message, sender, params) -> CommonProxy.eventHandler.onPlayerChat(new LegacyEvents.ServerChatEvent(sender, message.method_46291().getString())));
      }
   }
}
