package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;


public class PacketVirtualWorldLight implements IMessage {
   public static final byte SET = 0;
   public static final byte REMOVE = 1;
   private static final int MAX_POSITIONS = 96;

   public byte action;
   public String id = "";
   public int duration;
   public long[] positions = new long[0];
   public byte[] levels = new byte[0];

   public PacketVirtualWorldLight() {
   }

   private PacketVirtualWorldLight(byte action, String id, int duration, long[] positions, byte[] levels) {
      this.action = action;
      this.id = PacketClipboard.sanitize(id);
      this.duration = duration;
      this.positions = positions == null ? new long[0] : positions;
      this.levels = levels == null ? new byte[0] : levels;
   }

   public static PacketVirtualWorldLight set(String id, int duration, long[] positions, byte[] levels) {
      int size = Math.min(MAX_POSITIONS, Math.min(positions == null ? 0 : positions.length, levels == null ? 0 : levels.length));
      long[] safePositions = new long[size];
      byte[] safeLevels = new byte[size];
      if (size > 0) {
         System.arraycopy(positions, 0, safePositions, 0, size);
         System.arraycopy(levels, 0, safeLevels, 0, size);
      }

      return new PacketVirtualWorldLight(SET, id, duration, safePositions, safeLevels);
   }

   public static PacketVirtualWorldLight remove(String id) {
      return new PacketVirtualWorldLight(REMOVE, id, 0, null, null);
   }

   public void fromBytes(ByteBuf buf) {
      this.id = PacketClipboard.sanitize(ForgeByteBufUtils.readUTF8String(buf));
      this.action = buf.readByte();
      this.duration = buf.readInt();
      int size = Math.max(0, Math.min(MAX_POSITIONS, buf.readUnsignedShort()));
      this.positions = new long[size];
      this.levels = new byte[size];

      for(int index = 0; index < size; ++index) {
         this.positions[index] = buf.readLong();
         this.levels[index] = buf.readByte();
      }
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, PacketClipboard.sanitize(this.id));
      buf.writeByte(this.action);
      buf.writeInt(this.duration);
      int size = Math.min(MAX_POSITIONS, Math.min(this.positions.length, this.levels.length));
      buf.writeShort(size);

      for(int index = 0; index < size; ++index) {
         buf.writeLong(this.positions[index]);
         buf.writeByte(this.levels[index]);
      }
   }
}
