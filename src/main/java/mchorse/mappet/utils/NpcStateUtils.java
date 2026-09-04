package mchorse.mappet.utils;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.api.npcs.NpcState;
import mchorse.mclib.network.ForgeByteBufUtils;
import net.minecraft.class_2487;

public class NpcStateUtils {
   public static void stateToBuf(ByteBuf buffer, NpcState state) {
      class_2487 tag = state.serializeNBT();
      ForgeByteBufUtils.writeTag(buffer, tag);
   }

   public static NpcState stateFromBuf(ByteBuf buffer) {
      class_2487 tag = mchorse.mclib.utils.NBTUtils.readInfiniteTag(buffer);
      NpcState state = new NpcState();
      state.deserializeNBT(tag);
      return state;
   }
}
