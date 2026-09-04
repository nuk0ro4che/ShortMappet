package mchorse.mappet.network.common.npc;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketNpcList implements IMessage {
   public List<String> npcs = new ArrayList();
   public List<String> states = new ArrayList();
   public boolean isStates;

   public PacketNpcList() {
   }

   public PacketNpcList(Collection<String> npcs, Collection<String> states) {
      this.npcs.addAll(npcs);
      this.states.addAll(states);
   }

   public PacketNpcList states() {
      this.isStates = true;
      return this;
   }

   public void fromBytes(ByteBuf buf) {
      int i = 0;

      for(int c = buf.readInt(); i < c; ++i) {
         this.npcs.add(ForgeByteBufUtils.readUTF8String(buf));
      }

      i = 0;

      for(int c = buf.readInt(); i < c; ++i) {
         this.states.add(ForgeByteBufUtils.readUTF8String(buf));
      }

      this.isStates = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.npcs.size());

      for(String string : this.npcs) {
         ForgeByteBufUtils.writeUTF8String(buf, string);
      }

      buf.writeInt(this.states.size());

      for(String string : this.states) {
         ForgeByteBufUtils.writeUTF8String(buf, string);
      }

      buf.writeBoolean(this.isStates);
   }
}
