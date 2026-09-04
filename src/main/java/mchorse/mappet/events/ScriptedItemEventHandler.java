package mchorse.mappet.events;

import java.util.List;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.api.scripts.code.entities.ScriptEntityItem;
import mchorse.mappet.api.scripts.code.items.ScriptItemStack;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.common.ScriptedItemProps;
import mchorse.mappet.compat.events.SubscribeEvent;
import mchorse.mappet.compat.events.legacy.LegacyEvents;
import mchorse.mappet.utils.NBTUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1301;
import net.minecraft.class_1657;
import net.minecraft.class_1675;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3959;
import net.minecraft.class_3966;
import net.minecraft.class_7923;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class ScriptedItemEventHandler {
   @SubscribeEvent
   public void onScriptedItemRightClick(LegacyEvents.PlayerInteractEvent.RightClickItem event) {
      class_1657 playerIn = event.getPlayerEntity();
      class_1937 worldIn = event.getWorld();
      class_1268 handIn = event.getHand();
      if (!worldIn.field_9236) {
         class_1799 item = playerIn.method_5998(handIn);
         ScriptedItemProps props = NBTUtils.getScriptedItemProps(item);
         if (props == null) {
            return;
         }

         double reachDistance = playerIn.method_31549().field_7477 ? (double)5.0F : (double)4.5F;
         class_239 rayTraceResult = this.rayTraceEntityAndBlocks(worldIn, playerIn, reachDistance);
         List<class_1297> entityList = this.getEntitiesInPlayerReach(worldIn, playerIn, reachDistance);
         this.triggerInteractWithAir(event, props.interactWithAir, rayTraceResult, entityList, playerIn);
      }

   }

   public class_239 rayTraceEntityAndBlocks(class_1937 worldIn, class_1657 playerIn, double reachDistance) {
      class_243 eyePosition = playerIn.method_33571();
      class_243 lookVec = playerIn.method_5828(1.0F);
      class_243 reachVec = eyePosition.method_1019(lookVec.method_1021(reachDistance));
      class_239 blockResult = worldIn.method_17742(new class_3959(eyePosition, reachVec, class_3960.field_17559, class_242.field_1348, playerIn));
      class_3966 entityResult = class_1675.method_37226(worldIn, playerIn, eyePosition, reachVec, playerIn.method_5829().method_18804(lookVec.method_1021(reachDistance)).method_1014((double)1.0F), (entity) -> !entity.method_7325() && entity.method_5863(), (float)(reachDistance * reachDistance));
      if (entityResult == null) {
         return blockResult;
      } else {
         return (class_239)(blockResult.method_17783() != class_240.field_1333 && !(eyePosition.method_1025(entityResult.method_17784()) < eyePosition.method_1025(blockResult.method_17784())) ? blockResult : entityResult);
      }
   }

   @SubscribeEvent
   public void onPlayerWithScriptedItemInteractWithEntity(LegacyEvents.PlayerInteractEvent.EntityInteract event) {
      if (!event.getWorld().field_9236) {
         class_1799 item = event.getPlayerEntity().method_5998(event.getHand());
         ScriptedItemProps props = NBTUtils.getScriptedItemProps(item);
         if (props != null && props.interactWithEntity != null && !props.interactWithEntity.blocks.isEmpty()) {
            DataContext context = new DataContext(event.getPlayerEntity(), event.getTarget());
            context.set("hand", event.getHand() == class_1268.field_5808 ? "main" : "off");
            CommonProxy.eventHandler.trigger(event, props.interactWithEntity, context);
         }
      }

   }

   @SubscribeEvent
   public void onEntityAttackedWithScriptedItem(LegacyEvents.LivingAttackEvent event) {
      if (!event.getEntity().method_37908().field_9236 && event.getSource().method_5529() instanceof class_1657) {
         class_1657 player = (class_1657)event.getSource().method_5529();
         class_1799 item = player.method_5998(class_1268.field_5808);
         ScriptedItemProps props = NBTUtils.getScriptedItemProps(item);
         if (props != null && props.attackEntity != null && !props.attackEntity.blocks.isEmpty()) {
            class_1282 source = event.getSource();
            DataContext context = (new DataContext(event.getMobEntity(), source.method_5529())).set("damage", (double)event.getAmount());
            CommonProxy.eventHandler.trigger(event, props.attackEntity, context);
         }
      }

   }

   @SubscribeEvent
   public void onPlayerWithScriptedItemRightClickBlock(LegacyEvents.PlayerInteractEvent.RightClickBlock event) {
      if (!event.getWorld().field_9236) {
         class_1799 item = event.getPlayerEntity().method_5998(event.getHand());
         ScriptedItemProps props = NBTUtils.getScriptedItemProps(item);
         if (props != null && props.interactWithBlock != null && !props.interactWithBlock.blocks.isEmpty()) {
            class_2680 state = event.getWorld().method_8320(event.getPos());
            DataContext context = (new DataContext(event.getPlayerEntity())).set("block", class_7923.field_41175.method_10221(state.method_26204()).toString()).set("meta", (double)state.method_26204().method_9595().method_11662().indexOf(state)).set("x", (double)event.getPos().method_10263()).set("y", (double)event.getPos().method_10264()).set("z", (double)event.getPos().method_10260()).set("hand", event.getHand() == class_1268.field_5808 ? "main" : "off");
            CommonProxy.eventHandler.trigger(event, props.interactWithBlock, context);
         }
      }

   }

   @SubscribeEvent
   public void onPlayerHoldingScriptedItemTick(LegacyEvents.TickEvent.PlayerTickEvent event) {
      if (!event.player.method_37908().field_9236) {
         for(class_1268 hand : class_1268.values()) {
            class_1799 item = event.player.method_5998(hand);
            ScriptedItemProps props = NBTUtils.getScriptedItemProps(item);
            if (props != null && props.onHolderTick != null && !props.onHolderTick.blocks.isEmpty()) {
               DataContext context = new DataContext(event.player);
               context.set("hand", hand == class_1268.field_5808 ? "main" : "off");
               CommonProxy.eventHandler.trigger(event, props.onHolderTick, context);
            }
         }
      }

   }

   @SubscribeEvent
   public void onPlayerWithScriptedItemLeftClick(LegacyEvents.PlayerInteractEvent.LeftClickBlock event) {
      if (!event.getWorld().field_9236) {
         class_1799 item = event.getPlayerEntity().method_5998(event.getHand());
         ScriptedItemProps props = NBTUtils.getScriptedItemProps(item);
         if (props != null && props.hitBlock != null && !props.hitBlock.blocks.isEmpty()) {
            DataContext context = (new DataContext(event.getPlayerEntity())).set("x", (double)event.getPos().method_10263()).set("y", (double)event.getPos().method_10264()).set("z", (double)event.getPos().method_10260()).set("hand", event.getHand() == class_1268.field_5808 ? "main" : "off");
            CommonProxy.eventHandler.trigger(event, props.hitBlock, context);
         }
      }

   }

   @SubscribeEvent
   public void onPlayerWithScriptedItemPlaceBlock(LegacyEvents.BlockEvent.PlaceEvent event) {
      if (!event.getWorld().field_9236) {
         class_1657 player = event.getPlayer();
         class_1268 handIn = class_1268.field_5808;
         class_1799 item = player.method_5998(handIn);
         if (item.method_7960()) {
            handIn = class_1268.field_5810;
            item = player.method_5998(handIn);
         }

         ScriptedItemProps props = NBTUtils.getScriptedItemProps(item);
         if (props != null && props.placeBlock != null && !props.placeBlock.blocks.isEmpty()) {
            class_2680 state = event.getPlacedBlock();
            DataContext context = (new DataContext(player)).set("block", class_7923.field_41175.method_10221(state.method_26204()).toString()).set("meta", (double)state.method_26204().method_9595().method_11662().indexOf(state)).set("x", (double)event.getPos().method_10263()).set("y", (double)event.getPos().method_10264()).set("z", (double)event.getPos().method_10260()).set("hand", handIn == class_1268.field_5808 ? "main" : "off");
            CommonProxy.eventHandler.trigger(event, props.placeBlock, context);
         }
      }

   }

   @SubscribeEvent
   public void onPlayerPickUpScriptedItem(LegacyEvents.ItemEntityPickupEvent event) {
      if (!event.getItem().method_37908().field_9236) {
         class_1657 player = event.getPlayerEntity();
         class_1799 item = event.getItem().method_6983();
         ScriptedItemProps props = NBTUtils.getScriptedItemProps(item);
         if (props != null && props.pickup != null && !props.pickup.blocks.isEmpty()) {
            DataContext context = new DataContext(player);
            context.getValues().put("item", ScriptItemStack.create(event.getItem().method_6983()));
            context.getValues().put("entityItem", ScriptEntityItem.create(event.getItem()));
            CommonProxy.eventHandler.trigger(event, props.pickup, context);
         }
      }

   }

   @SubscribeEvent
   public void onPlayerTossScriptedItem(LegacyEvents.ItemTossEvent event) {
      if (!event.getItemEntity().method_37908().field_9236) {
         class_1657 player = event.getPlayer();
         class_1799 item = event.getItemEntity().method_6983();
         ScriptedItemProps props = NBTUtils.getScriptedItemProps(item);
         if (props != null && props.toss != null && !props.toss.blocks.isEmpty()) {
            DataContext context = new DataContext(player);
            context.getValues().put("item", ScriptItemStack.create(event.getItemEntity().method_6983()));
            context.getValues().put("entityItem", ScriptEntityItem.create(event.getItemEntity()));
            CommonProxy.eventHandler.trigger(event, props.toss, context);
         }
      }

   }

   @SubscribeEvent
   public void onPlayerWithScriptedItemBreakBlock(LegacyEvents.BlockEvent.BreakEvent event) {
      if (!event.getWorld().field_9236) {
         class_1657 player = event.getPlayer();
         class_1799 item = player.method_5998(class_1268.field_5808);
         ScriptedItemProps props = NBTUtils.getScriptedItemProps(item);
         if (props != null && props.breakBlock != null && !props.breakBlock.blocks.isEmpty()) {
            class_2680 state = event.getState();
            DataContext context = (new DataContext(player)).set("block", class_7923.field_41175.method_10221(state.method_26204()).toString()).set("meta", (double)state.method_26204().method_9595().method_11662().indexOf(state)).set("x", (double)event.getPos().method_10263()).set("y", (double)event.getPos().method_10264()).set("z", (double)event.getPos().method_10260());
            CommonProxy.eventHandler.trigger(event, props.breakBlock, context);
         }
      }

   }

   @SubscribeEvent
   public void onPlayerWithScriptedItemTick(LegacyEvents.TickEvent.PlayerTickEvent event) {
      if (!event.player.method_37908().field_9236 && event.side == LegacyEvents.Side.SERVER && event.phase == LegacyEvents.TickEvent.Phase.START) {
         for(class_1799 item : event.player.method_5877()) {
            ScriptedItemProps props = NBTUtils.getScriptedItemProps(item);
            if (props != null && props.onHolderTick != null && !props.onHolderTick.blocks.isEmpty()) {
               DataContext context = new DataContext(event.player);
               CommonProxy.eventHandler.trigger(event, props.onHolderTick, context);
            }
         }
      }

   }

   private void triggerInteractWithAir(LegacyEvents.PlayerInteractEvent.RightClickItem event, Trigger interactWithAirTrigger, class_239 rayTraceResult, List<class_1297> entityList, class_1657 playerIn) {
      if (interactWithAirTrigger != null && !interactWithAirTrigger.blocks.isEmpty() && (rayTraceResult == null || rayTraceResult.method_17783() != class_240.field_1332) && entityList.isEmpty()) {
         DataContext context = (new DataContext(playerIn)).set("x", (double)event.getPos().method_10263()).set("y", (double)event.getPos().method_10264()).set("z", (double)event.getPos().method_10260()).set("hand", event.getHand() == class_1268.field_5808 ? "main" : "off");
         CommonProxy.eventHandler.trigger(event, interactWithAirTrigger, context);
      }

   }

   private List<class_1297> getEntitiesInPlayerReach(class_1937 worldIn, class_1657 playerIn, double reachDistance) {
      class_243 eyePos = playerIn.method_33571();
      class_243 lookVec = playerIn.method_5828(1.0F);
      class_243 reachVec = eyePos.method_1019(new class_243(lookVec.field_1352 * reachDistance, lookVec.field_1351 * reachDistance, lookVec.field_1350 * reachDistance));
      class_238 playerReach = new class_238(eyePos.field_1352, eyePos.field_1351, eyePos.field_1350, reachVec.field_1352, reachVec.field_1351, reachVec.field_1350);
      List<class_1297> list = worldIn.method_8335(playerIn, playerReach.method_1014((double)1.0F));
      list.removeIf((entity) -> !entity.method_5863() || !class_1301.field_6155.test(entity));
      list.removeIf((entity) -> class_238.method_1010(List.of(entity.method_5829()), eyePos, reachVec, class_2338.field_10980) == null);
      return list;
   }

   @SubscribeEvent
   public void onFirstItemPickup(LegacyEvents.ItemEntityPickupEvent event) {
      if (!event.getItem().method_37908().field_9236) {
         class_1657 player = event.getPlayerEntity();
         class_1799 itemStack = event.getItem().method_6983();
         ScriptedItemProps props = NBTUtils.getScriptedItemProps(itemStack);
         if (props != null && props.pickedUp) {
            return;
         }

         if (props != null && props.firstPickup != null && !props.firstPickup.blocks.isEmpty()) {
            props.pickedUp = true;
            NBTUtils.setScriptedItemProps(itemStack, props);
            DataContext context = new DataContext(player);
            context.getValues().put("item", ScriptItemStack.create(itemStack));
            context.getValues().put("entityItem", ScriptEntityItem.create(event.getItem()));
            CommonProxy.eventHandler.trigger(event, props.firstPickup, context);
         }
      }

   }

   @SubscribeEvent
   public void onLivingEquipmentChange(LegacyEvents.LivingEquipmentChangeEvent event) {
      if (!event.getMobEntity().method_37908().field_9236) {
         class_1799 previousItem = event.getFrom();
         class_1799 newItem = event.getTo();
         int slotIndex = event.getSlot().method_5927();
         if (previousItem.method_7960() && !newItem.method_7960()) {
            ScriptedItemProps props = NBTUtils.getScriptedItemProps(newItem);
            if (props != null && props.startedHolding != null && !props.startedHolding.blocks.isEmpty()) {
               DataContext context = new DataContext(event.getMobEntity());
               context.getValues().put("item", ScriptItemStack.create(newItem));
               CommonProxy.eventHandler.trigger(event, props.startedHolding, context);
            }
         } else if (!previousItem.method_7960() && newItem.method_7960()) {
            ScriptedItemProps props = NBTUtils.getScriptedItemProps(previousItem);
            if (props != null && props.stoppedHolding != null && !props.stoppedHolding.blocks.isEmpty()) {
               DataContext context = new DataContext(event.getMobEntity());
               context.getValues().put("item", ScriptItemStack.create(previousItem));
               CommonProxy.eventHandler.trigger(event, props.stoppedHolding, context);
            }
         }

      }
   }

   @SubscribeEvent
   public void onScriptedItemUseStart(LegacyEvents.LivingEntityUseItemEvent.Start event) {
      if (!event.getMobEntity().method_37908().field_9236 && event.getMobEntity() instanceof class_1657) {
         class_1657 player = (class_1657)event.getMobEntity();
         class_1799 item = event.getItem();
         ScriptedItemProps props = NBTUtils.getScriptedItemProps(item);
         if (props != null && props.useStart != null && !props.useStart.blocks.isEmpty()) {
            DataContext context = new DataContext(player);
            context.getValues().put("item", ScriptItemStack.create(item));
            context.getValues().put("duration", event.getDuration());
            CommonProxy.eventHandler.trigger(event, props.useStart, context);
         }
      }

   }

   @SubscribeEvent
   public void onScriptedItemUseStop(LegacyEvents.LivingEntityUseItemEvent.Stop event) {
      if (!event.getMobEntity().method_37908().field_9236 && event.getMobEntity() instanceof class_1657) {
         class_1657 player = (class_1657)event.getMobEntity();
         class_1799 item = event.getItem();
         ScriptedItemProps props = NBTUtils.getScriptedItemProps(item);
         if (props != null && props.useStop != null && !props.useStop.blocks.isEmpty()) {
            DataContext context = new DataContext(player);
            context.getValues().put("item", ScriptItemStack.create(item));
            context.getValues().put("duration", event.getDuration());
            CommonProxy.eventHandler.trigger(event, props.useStop, context);
         }
      }

   }

   @SubscribeEvent
   public void onScriptedItemUseTick(LegacyEvents.LivingEntityUseItemEvent.Tick event) {
      if (!event.getMobEntity().method_37908().field_9236 && event.getMobEntity() instanceof class_1657) {
         class_1657 player = (class_1657)event.getMobEntity();
         class_1799 item = event.getItem();
         ScriptedItemProps props = NBTUtils.getScriptedItemProps(item);
         if (props != null && props.onUseTick != null && !props.onUseTick.blocks.isEmpty()) {
            DataContext context = new DataContext(player);
            context.getValues().put("item", ScriptItemStack.create(item));
            context.getValues().put("duration", event.getDuration());
            CommonProxy.eventHandler.trigger(event, props.onUseTick, context);
         }
      }

   }

   @SubscribeEvent
   public void onScriptedItemUseFinish(LegacyEvents.LivingEntityUseItemEvent.Finish event) {
      if (!event.getMobEntity().method_37908().field_9236 && event.getMobEntity() instanceof class_1657) {
         class_1657 player = (class_1657)event.getMobEntity();
         class_1799 item = event.getItem();
         ScriptedItemProps props = NBTUtils.getScriptedItemProps(item);
         if (props != null && props.finishedUsing != null && !props.finishedUsing.blocks.isEmpty()) {
            DataContext context = new DataContext(player);
            context.getValues().put("item", ScriptItemStack.create(item));
            context.getValues().put("duration", event.getDuration());
            CommonProxy.eventHandler.trigger(event, props.finishedUsing, context);
         }
      }

   }
}
