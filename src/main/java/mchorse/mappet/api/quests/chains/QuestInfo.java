package mchorse.mappet.api.quests.chains;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.quests.Quest;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.class_2487;

public class QuestInfo implements IMessage {
   public Quest quest;
   public QuestStatus status;

   public QuestInfo() {
   }

   public QuestInfo(Quest quest, QuestStatus status) {
      this.quest = quest;
      this.status = status;
   }

   public void fromBytes(ByteBuf buf) {
      this.status = QuestStatus.values()[buf.readInt()];
      String id = ForgeByteBufUtils.readUTF8String(buf);
      class_2487 tag = NBTUtils.readInfiniteTag(buf);
      this.quest = (Quest)Mappet.quests.create(id, tag);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.status.ordinal());
      ForgeByteBufUtils.writeUTF8String(buf, this.quest.getId());
      ForgeByteBufUtils.writeTag(buf, this.quest.serializeNBT());
   }
}
