package mchorse.mappet.network.common.quests;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.api.quests.chains.QuestStatus;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketQuestAction implements IMessage {
   public String id;
   public QuestStatus status;

   public PacketQuestAction() {
   }

   public PacketQuestAction(String id, QuestStatus status) {
      this.id = id;
      this.status = status;
   }

   public void fromBytes(ByteBuf buf) {
      this.id = ForgeByteBufUtils.readUTF8String(buf);
      this.status = QuestStatus.values()[buf.readInt()];
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, this.id);
      buf.writeInt(this.status.ordinal());
   }
}
