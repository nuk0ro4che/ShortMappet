package mchorse.mappet.network.common.quests;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.quests.Quest;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;

public class PacketQuestVisibility implements IMessage {
   public String id;
   public Quest quest;
   public boolean visible;

   public PacketQuestVisibility() {
   }

   public PacketQuestVisibility(String id, Quest quest, boolean visible) {
      this.id = id;
      this.quest = quest;
      this.visible = visible;
   }

   public void fromBytes(ByteBuf buf) {
      this.id = ForgeByteBufUtils.readUTF8String(buf);
      if (buf.readBoolean()) {
         this.quest = (Quest)Mappet.quests.create(this.id, NBTUtils.readInfiniteTag(buf));
      }

      this.visible = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, this.id);
      buf.writeBoolean(this.quest != null);
      if (this.quest != null) {
         ForgeByteBufUtils.writeTag(buf, this.quest.serializeNBT());
      }

      buf.writeBoolean(this.visible);
   }
}
