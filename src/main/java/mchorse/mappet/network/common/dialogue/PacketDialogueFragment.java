package mchorse.mappet.network.common.dialogue;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.crafting.CraftingTable;
import mchorse.mappet.api.dialogues.DialogueFragment;
import mchorse.mappet.api.quests.chains.QuestContext;
import mchorse.mappet.api.quests.chains.QuestInfo;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.class_2540;

public class PacketDialogueFragment implements IMessage {
   public String title = "";
   public boolean closable;
   public AbstractMorph morph;
   public DialogueFragment reaction = new DialogueFragment();
   public List<DialogueFragment> replies = new ArrayList();
   public CraftingTable table;
   public boolean hasQuests;
   public boolean singleQuest;
   public List<QuestInfo> quests = new ArrayList();

   public PacketDialogueFragment() {
   }

   public PacketDialogueFragment(boolean closable, DialogueFragment reaction, List<DialogueFragment> replies) {
      this.closable = closable;
      this.reaction = reaction;
      this.replies = replies;
   }

   public void addMorph(AbstractMorph morph) {
      this.morph = morph;
   }

   public void addCraftingTable(CraftingTable table) {
      this.table = table;
   }

   public void addQuests(QuestContext context) {
      this.hasQuests = true;
      this.quests.addAll(context.quests);
   }

   public void addQuest(QuestInfo questInfo) {
      this.hasQuests = true;
      this.singleQuest = true;
      this.quests.add(questInfo);
   }

   public void fromBytes(ByteBuf buf) {
      this.title = ForgeByteBufUtils.readUTF8String(buf);
      this.closable = buf.readBoolean();
      this.morph = MorphUtils.morphFromBuf(new class_2540(buf));
      this.reaction.deserializeNBT(NBTUtils.readInfiniteTag(buf));
      int i = 0;

      for(int c = buf.readInt(); i < c; ++i) {
         DialogueFragment fragment = new DialogueFragment();
         fragment.deserializeNBT(NBTUtils.readInfiniteTag(buf));
         this.replies.add(fragment);
      }

      if (buf.readBoolean()) {
         String id = ForgeByteBufUtils.readUTF8String(buf);
         this.table = (CraftingTable)Mappet.crafting.create(id, NBTUtils.readInfiniteTag(buf));
      }

      this.hasQuests = buf.readBoolean();
      this.singleQuest = buf.readBoolean();
      i = 0;

      for(int c = buf.readInt(); i < c; ++i) {
         QuestInfo info = new QuestInfo();
         info.fromBytes(buf);
         this.quests.add(info);
      }

   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, this.title);
      buf.writeBoolean(this.closable);
      MorphUtils.morphToBuf(new class_2540(buf), this.morph);
      ForgeByteBufUtils.writeTag(buf, this.reaction.serializeNBT());
      buf.writeInt(this.replies.size());

      for(DialogueFragment fragment : this.replies) {
         ForgeByteBufUtils.writeTag(buf, fragment.serializeNBT());
      }

      buf.writeBoolean(this.table != null);
      if (this.table != null) {
         ForgeByteBufUtils.writeUTF8String(buf, this.table.getId());
         ForgeByteBufUtils.writeTag(buf, this.table.serializeNBT());
      }

      buf.writeBoolean(this.hasQuests);
      buf.writeBoolean(this.singleQuest);
      buf.writeInt(this.quests.size());

      for(QuestInfo info : this.quests) {
         info.toBytes(buf);
      }

   }

   public boolean isEmpty() {
      return this.replies.isEmpty() && !this.hasQuests && this.table == null;
   }
}
