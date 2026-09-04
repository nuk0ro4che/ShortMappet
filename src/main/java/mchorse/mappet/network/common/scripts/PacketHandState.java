package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import mchorse.mappet.hand.HandState;
import mchorse.mclib.network.IMessage;
import mchorse.metamorph.api.MorphUtils;
import net.minecraft.class_2540;

public class PacketHandState implements IMessage {
   public UUID player;
   public HandState state = new HandState();

   public PacketHandState() {
   }

   public PacketHandState(UUID player, HandState state) {
      this.player = player;
      this.state = state;
   }

   public void fromBytes(ByteBuf buf) {
      this.player = new UUID(buf.readLong(), buf.readLong());
      read(buf, this.state.main);
      read(buf, this.state.off);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeLong(this.player.getMostSignificantBits());
      buf.writeLong(this.player.getLeastSignificantBits());
      write(buf, this.state.main);
      write(buf, this.state.off);
   }

   private static void write(ByteBuf buf, HandState.Side side) {
      buf.writeBoolean(side.renderArm);
      buf.writeBoolean(side.renderItem);
      buf.writeDouble(side.x); buf.writeDouble(side.y); buf.writeDouble(side.z);
      buf.writeDouble(side.pitch); buf.writeDouble(side.yaw); buf.writeDouble(side.roll);
      writeTransition(buf, side.position);
      writeTransition(buf, side.rotation);
      buf.writeBoolean(side.morph != null);
      if (side.morph != null) {
         MorphUtils.morphToBuf(new class_2540(buf), side.morph);
      }
   }

   private static void read(ByteBuf buf, HandState.Side side) {
      side.renderArm = buf.readBoolean();
      side.renderItem = buf.readBoolean();
      side.x = buf.readDouble(); side.y = buf.readDouble(); side.z = buf.readDouble();
      side.pitch = buf.readDouble(); side.yaw = buf.readDouble(); side.roll = buf.readDouble();
      readTransition(buf, side.position);
      readTransition(buf, side.rotation);
      side.morph = buf.readBoolean() ? MorphUtils.morphFromBuf(new class_2540(buf)) : null;
   }

   private static void writeTransition(ByteBuf buf, HandState.Transition transition) {
      buf.writeDouble(transition.fromX); buf.writeDouble(transition.fromY); buf.writeDouble(transition.fromZ);
      buf.writeDouble(transition.toX); buf.writeDouble(transition.toY); buf.writeDouble(transition.toZ);
      buf.writeLong(transition.started); buf.writeLong(transition.finished);
      byte[] bytes = (transition.interpolation == null ? "LINEAR" : transition.interpolation).getBytes(java.nio.charset.StandardCharsets.UTF_8);
      buf.writeInt(bytes.length); buf.writeBytes(bytes);
   }

   private static void readTransition(ByteBuf buf, HandState.Transition transition) {
      transition.fromX = buf.readDouble(); transition.fromY = buf.readDouble(); transition.fromZ = buf.readDouble();
      transition.toX = buf.readDouble(); transition.toY = buf.readDouble(); transition.toZ = buf.readDouble();
      long sentStarted = buf.readLong();
      long sentFinished = buf.readLong();
      int length = buf.readInt();
      byte[] bytes = new byte[length];
      buf.readBytes(bytes);
      transition.interpolation = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
      long remaining = Math.max(0L, sentFinished - sentStarted);
      transition.started = System.currentTimeMillis();
      transition.finished = transition.started + remaining;
   }
}
