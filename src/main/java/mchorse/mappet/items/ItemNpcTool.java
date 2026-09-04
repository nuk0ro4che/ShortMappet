package mchorse.mappet.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.npcs.Npc;
import mchorse.mappet.api.npcs.NpcState;
import mchorse.mappet.entities.EntityNpc;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.npc.PacketNpcList;
import mchorse.mappet.network.common.npc.PacketNpcState;
import mchorse.mclib.utils.OpHelper;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1271;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1836;
import net.minecraft.class_1838;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2487;
import net.minecraft.class_2561;
import net.minecraft.class_3222;

public class ItemNpcTool extends class_1792 {
   public ItemNpcTool() {
      super((new class_1792.class_1793()).method_7889(1));
   }

   @Environment(EnvType.CLIENT)
   public void method_7851(class_1799 stack, @Nullable class_1937 worldIn, List<class_2561> tooltip, class_1836 flagIn) {
      tooltip.add(class_2561.method_43471("item.mappet.npc_tool.tooltip"));
   }

   public class_1269 method_7847(class_1799 stack, class_1657 player, class_1309 target, class_1268 hand) {
      if (!player.method_37908().field_9236 && target instanceof EntityNpc) {
         if ((Boolean)Mappet.npcsToolOnlyOP.get() && !OpHelper.isPlayerOp((class_3222)player)) {
            return super.method_7847(stack, player, target, hand);
         } else if ((Boolean)Mappet.npcsToolOnlyCreative.get() && !player.method_7337()) {
            return super.method_7847(stack, player, target, hand);
         } else {
            EntityNpc npc = (EntityNpc)target;
            if (player.method_5715()) {
               npc.method_31472();
            } else {
               Dispatcher.sendTo(new PacketNpcState(target.method_5628(), npc.getState().serializeNBT()), (class_3222)player);
            }

            return class_1269.field_5812;
         }
      } else {
         return super.method_7847(stack, player, target, hand);
      }
   }

   public class_1271<class_1799> method_7836(class_1937 worldIn, class_1657 playerIn, class_1268 handIn) {
      if (!worldIn.field_9236) {
         if ((Boolean)Mappet.npcsToolOnlyOP.get() && !OpHelper.isPlayerOp((class_3222)playerIn)) {
            return super.method_7836(worldIn, playerIn, handIn);
         }

         if ((Boolean)Mappet.npcsToolOnlyCreative.get() && !playerIn.method_7337()) {
            return super.method_7836(worldIn, playerIn, handIn);
         }

         if (this.openNpcTool(playerIn, playerIn.method_5998(handIn))) {
            return class_1271.method_22427(playerIn.method_5998(handIn));
         }
      }

      return super.method_7836(worldIn, playerIn, handIn);
   }

   private boolean openNpcTool(class_1657 player, class_1799 stack) {
      Collection<String> npcs = Mappet.npcs.getKeys();
      if (!npcs.isEmpty() && player instanceof class_3222) {
         List<String> states = new ArrayList();

         try {
            class_2487 tag = stack.method_7969();
            Npc npc = (Npc)Mappet.npcs.load(tag.method_10558("Npc"));
            states.addAll(npc.states.keySet());
         } catch (Exception var7) {
         }

         Dispatcher.sendTo(new PacketNpcList(npcs, states), (class_3222)player);
         return true;
      } else {
         return false;
      }
   }

   public class_1269 method_7884(class_1838 context) {
      class_1657 player = context.method_8036();
      class_1937 worldIn = context.method_8045();
      class_2338 pos = context.method_8037();
      class_1268 hand = context.method_20287();
      class_2350 facing = context.method_8038();
      double hitX = context.method_17698().field_1352 - (double)pos.method_10263();
      double hitY = context.method_17698().field_1351 - (double)pos.method_10264();
      double hitZ = context.method_17698().field_1350 - (double)pos.method_10260();
      class_1799 stack = player.method_5998(hand);
      if (!worldIn.field_9236) {
         if ((Boolean)Mappet.npcsToolOnlyOP.get() && !OpHelper.isPlayerOp((class_3222)player)) {
            return class_1269.field_5811;
         }

         if ((Boolean)Mappet.npcsToolOnlyCreative.get() && !player.method_7337()) {
            return class_1269.field_5811;
         }

         EntityNpc entity = new EntityNpc(Mappet.npcEntity, worldIn);
         class_2338 posOffset = pos.method_10093(facing);
         entity.method_5814((double)posOffset.method_10263() + hitX, (double)posOffset.method_10264() + hitY, (double)posOffset.method_10260() + hitZ);
         this.setupState(entity, stack);
         entity.method_37908().method_8649(entity);
         entity.initialize();
         if (!player.method_5715()) {
            Dispatcher.sendTo(new PacketNpcState(entity.method_5628(), entity.getState().serializeNBT()), (class_3222)player);
         }
      }

      return stack.method_7909() == Mappet.npcTool ? class_1269.field_5812 : super.method_7884(context);
   }

   private void setupState(EntityNpc entity, class_1799 stack) {
      class_2487 tag = stack.method_7969();
      if (tag != null) {
         String npcId = tag.method_10558("Npc");
         String stateId = tag.method_10558("State");
         Npc npc = (Npc)Mappet.npcs.load(npcId);
         NpcState state = npc == null ? null : (NpcState)npc.states.get(stateId);
         if (npc != null && state == null && npc.states.containsKey("default")) {
            state = (NpcState)npc.states.get("default");
         }

         if (state != null) {
            entity.setNpc(npc, state);
            if (!npc.serializeNBT().method_10558("StateName").equals("default")) {
               entity.setStringInData("StateName", stateId);
            }
         }
      } else {
         tag = new class_2487();
         tag.method_10582("Name", "blockbuster.fred");
         AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(tag);
         entity.getState().morph = morph;
         entity.setMorph(morph);
      }

   }
}
