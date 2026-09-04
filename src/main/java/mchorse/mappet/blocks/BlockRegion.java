package mchorse.mappet.blocks;

import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.blocks.PacketEditRegion;
import mchorse.mappet.tile.TileRegion;
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
import net.minecraft.class_2464;
import net.minecraft.class_2561;
import net.minecraft.class_2586;
import net.minecraft.class_259;
import net.minecraft.class_2591;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3222;
import net.minecraft.class_3726;
import net.minecraft.class_3965;
import net.minecraft.class_5558;
import net.minecraft.class_4970.class_2251;
import org.jetbrains.annotations.Nullable;

public class BlockRegion extends class_2248 implements class_2343 {
   public BlockRegion() {
      super(class_2251.method_9637().method_9629(-1.0F, 3600000.0F).method_22488().method_9634());
   }

   public void method_9568(class_1799 stack, @Nullable class_1922 world, List<class_2561> tooltip, class_1836 options) {
      tooltip.add(class_2561.method_43471("tile.mappet.region.tooltip"));
   }

   public class_1269 method_9534(class_2680 state, class_1937 world, class_2338 pos, class_1657 player, class_1268 hand, class_3965 hit) {
      if (!world.field_9236 && player.method_7337()) {
         class_2586 var8 = world.method_8321(pos);
         if (var8 instanceof TileRegion) {
            TileRegion tile = (TileRegion)var8;
            Dispatcher.sendTo((new PacketEditRegion(tile)).open(), (class_3222)player);
         }
      }

      return class_1269.field_5812;
   }

   public class_265 method_9530(class_2680 state, class_1922 world, class_2338 pos, class_3726 context) {
      return class_259.method_1077();
   }

   public class_2464 method_9604(class_2680 state) {
      return class_2464.field_11456;
   }

   public class_2586 method_10123(class_2338 pos, class_2680 state) {
      return new TileRegion(pos, state);
   }

   public <T extends class_2586> class_5558<T> method_31645(class_1937 world, class_2680 state, class_2591<T> type) {
      if (type != Mappet.regionTile) {
         return null;
      }

      return new class_5558<T>() {
         public void tick(class_1937 level, class_2338 pos, class_2680 blockState, T blockEntity) {
            if (blockEntity instanceof TileRegion region) {
               region.tick();
            }
         }
      };
   }
}
