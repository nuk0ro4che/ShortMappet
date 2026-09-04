package mchorse.mappet.network.common.crafting;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.api.crafting.CraftingTable;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.class_2487;

public class PacketCraftingTable implements IMessage {
   public CraftingTable table;

   public PacketCraftingTable() {
   }

   public PacketCraftingTable(CraftingTable table) {
      this.table = table;
   }

   public void fromBytes(ByteBuf buf) {
      if (buf.readBoolean()) {
         class_2487 tag = NBTUtils.readInfiniteTag(buf);
         this.table = new CraftingTable();
         this.table.deserializeNBT(tag);
         this.table.setId(ForgeByteBufUtils.readUTF8String(buf));
      }

   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.table != null);
      if (this.table != null) {
         ForgeByteBufUtils.writeTag(buf, this.table.serializeNBT());
         ForgeByteBufUtils.writeUTF8String(buf, this.table.getId());
      }

   }
}
