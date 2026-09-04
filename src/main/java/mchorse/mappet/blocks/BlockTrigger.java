package mchorse.mappet.blocks;

import java.util.List;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.blocks.PacketEditTrigger;
import mchorse.mappet.tile.TileTrigger;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1836;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2343;
import net.minecraft.class_243;
import net.minecraft.class_2464;
import net.minecraft.class_2561;
import net.minecraft.class_2586;
import net.minecraft.class_259;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2689;
import net.minecraft.class_2746;
import net.minecraft.class_2769;
import net.minecraft.class_3222;
import net.minecraft.class_3726;
import net.minecraft.class_3965;
import net.minecraft.class_4970.class_2251;
import org.jetbrains.annotations.Nullable;

public class BlockTrigger extends class_2248 implements class_2343 {
   public static final class_2746 COLLIDABLE = class_2746.method_11825("collidable");

   public BlockTrigger() {
      super(class_2251.method_9637().method_9629(-1.0F, 3600000.0F).method_22488());
      this.method_9590((class_2680)((class_2680)this.field_10647.method_11664()).method_11657(COLLIDABLE, false));
   }

   public void method_9568(class_1799 stack, @Nullable class_1922 world, List<class_2561> tooltip, class_1836 options) {
      tooltip.add(class_2561.method_43471("tile.mappet.trigger.tooltip"));
   }

   public void method_9606(class_2680 state, class_1937 world, class_2338 pos, class_1657 player) {
      if (!world.field_9236 && !player.method_7337()) {
         class_2586 var6 = world.method_8321(pos);
         if (var6 instanceof TileTrigger) {
            TileTrigger tile = (TileTrigger)var6;
            tile.leftClick.trigger((new DataContext(player)).set("x", (double)pos.method_10263()).set("y", (double)pos.method_10264()).set("z", (double)pos.method_10260()));
         }
      }

   }

   public class_1269 method_9534(class_2680 state, class_1937 world, class_2338 pos, class_1657 player, class_1268 hand, class_3965 hit) {
      if (!world.field_9236) {
         class_2586 var8 = world.method_8321(pos);
         if (var8 instanceof TileTrigger) {
            TileTrigger tile = (TileTrigger)var8;
            if (player.method_7337() && !player.method_5715()) {
               Dispatcher.sendTo(new PacketEditTrigger(tile), (class_3222)player);
            } else {
               tile.rightClick.trigger((new DataContext(player)).set("x", (double)pos.method_10263()).set("y", (double)pos.method_10264()).set("z", (double)pos.method_10260()));
            }
         }
      }

      return class_1269.field_5812;
   }

   private class_265 shape(class_1922 world, class_2338 pos) {
      class_2586 var4 = world.method_8321(pos);
      if (var4 instanceof TileTrigger tile) {
         class_243 var6 = tile.boundingBoxPos1;
         class_243 b = tile.boundingBoxPos2;
         return var6.equals(b) ? class_259.method_1077() : class_259.method_1081(var6.field_1352, var6.field_1351, var6.field_1350, b.field_1352, b.field_1351, b.field_1350);
      } else {
         return class_259.method_1077();
      }
   }

   public class_265 method_9549(class_2680 state, class_1922 world, class_2338 pos, class_3726 context) {
      return (Boolean)state.method_11654(COLLIDABLE) ? this.shape(world, pos) : class_259.method_1073();
   }

   public class_265 method_9530(class_2680 state, class_1922 world, class_2338 pos, class_3726 context) {
      return this.shape(world, pos);
   }

   public class_2464 method_9604(class_2680 state) {
      return class_2464.field_11456;
   }

   protected void method_9515(class_2689.class_2690<class_2248, class_2680> builder) {
      builder.method_11667(new class_2769[]{COLLIDABLE});
   }

   public class_2586 method_10123(class_2338 pos, class_2680 state) {
      return new TileTrigger(pos, state);
   }
}
