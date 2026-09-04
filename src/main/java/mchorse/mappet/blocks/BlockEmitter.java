package mchorse.mappet.blocks;

import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.blocks.PacketEditEmitter;
import mchorse.mappet.tile.TileEmitter;
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
import net.minecraft.class_2350;
import net.minecraft.class_2561;
import net.minecraft.class_2586;
import net.minecraft.class_2591;
import net.minecraft.class_2680;
import net.minecraft.class_2689;
import net.minecraft.class_2746;
import net.minecraft.class_2769;
import net.minecraft.class_3222;
import net.minecraft.class_3965;
import net.minecraft.class_5558;
import net.minecraft.class_4970.class_2251;
import org.jetbrains.annotations.Nullable;

public class BlockEmitter extends class_2248 implements class_2343 {
   public static final class_2746 POWERED = class_2746.method_11825("powered");

   public BlockEmitter() {
      super(class_2251.method_9637().method_9629(-1.0F, 3600000.0F));
      this.method_9590((class_2680)((class_2680)this.field_10647.method_11664()).method_11657(POWERED, false));
   }

   public void method_9568(class_1799 stack, @Nullable class_1922 world, List<class_2561> tooltip, class_1836 options) {
      tooltip.add(class_2561.method_43471("tile.mappet.emitter.tooltip"));
   }

   public class_1269 method_9534(class_2680 state, class_1937 world, class_2338 pos, class_1657 player, class_1268 hand, class_3965 hit) {
      if (!world.field_9236 && player.method_7337()) {
         class_2586 var8 = world.method_8321(pos);
         if (var8 instanceof TileEmitter) {
            TileEmitter tile = (TileEmitter)var8;
            Dispatcher.sendTo(new PacketEditEmitter(tile), (class_3222)player);
         }
      }

      return class_1269.field_5812;
   }

   protected void method_9515(class_2689.class_2690<class_2248, class_2680> builder) {
      builder.method_11667(new class_2769[]{POWERED});
   }

   public boolean method_9506(class_2680 state) {
      return true;
   }

   public int method_9524(class_2680 state, class_1922 world, class_2338 pos, class_2350 direction) {
      return (Boolean)state.method_11654(POWERED) ? 15 : 0;
   }

   public class_2586 method_10123(class_2338 pos, class_2680 state) {
      return new TileEmitter(pos, state);
   }

   public <T extends class_2586> class_5558<T> method_31645(class_1937 world, class_2680 state, class_2591<T> type) {
      return type == Mappet.emitterTile ? (w, p, s, tile) -> ((TileEmitter)tile).tick() : null;
   }
}
