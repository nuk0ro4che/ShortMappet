package mchorse.mappet.api.scripts.code.blocks;

import mchorse.mappet.api.scripts.user.IScriptWorld;
import mchorse.mappet.api.scripts.user.blocks.IScriptBlockState;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class ScriptBlockState implements IScriptBlockState {
   public static ScriptBlockState AIR;
   public static class_2338.class_2339 BLOCK_POS;
   private class_2680 state;

   public static IScriptBlockState create(class_2680 state) {
      return state != class_2246.field_10124.method_9564() && state != null ? new ScriptBlockState(state) : AIR;
   }

   private ScriptBlockState(class_2680 state) {
      this.state = state;
   }

   public class_2680 getMinecraftBlockState() {
      return this.state;
   }

   public int getMeta() {
      return this.state.method_26204().method_9595().method_11662().indexOf(this.state);
   }

   public String getBlockId() {
      class_2960 rl = class_7923.field_41175.method_10221(this.state.method_26204());
      return rl == null ? "" : rl.toString();
   }

   public boolean isSame(IScriptBlockState state) {
      ScriptBlockState otherState = (ScriptBlockState)state;
      return this.state.method_26204() == otherState.state.method_26204() && this.getMeta() == otherState.getMeta();
   }

   public boolean isSameBlock(IScriptBlockState state) {
      return this.state.method_26204() == ((ScriptBlockState)state).state.method_26204();
   }

   public boolean isOpaque() {
      return this.state.method_26225();
   }

   public boolean hasCollision(IScriptWorld world, int x, int y, int z) {
      return !this.state.method_26220(world.getMinecraftWorld(), BLOCK_POS.method_10103(x, y, z)).method_1110();
   }

   public boolean isAir() {
      return this.state.method_26204() == class_2246.field_10124;
   }

   static {
      AIR = new ScriptBlockState(class_2246.field_10124.method_9564());
      BLOCK_POS = new class_2338.class_2339();
   }
}
