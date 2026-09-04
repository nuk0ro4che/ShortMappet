package mchorse.mappet.network.common.ui;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.api.ui.UIFile;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;


public class PacketUIPreview implements IMessage {
   public String id = "";
   
   public UIFile ui;

   public PacketUIPreview() {
   }

   public PacketUIPreview(String id) {
      this.id = id == null ? "" : id;
   }

   public PacketUIPreview(UIFile ui) {
      this(ui == null ? "" : ui.getId());
      this.ui = ui;
   }

   public void fromBytes(ByteBuf buf) {
      this.id = ForgeByteBufUtils.readUTF8String(buf);
      if (buf.readBoolean()) {
         this.ui = new UIFile();
         this.ui.setId(this.id);
         this.ui.deserializeNBT(ForgeByteBufUtils.readTag(buf));
      }
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, this.id);
      buf.writeBoolean(this.ui != null);
      if (this.ui != null) {
         ForgeByteBufUtils.writeTag(buf, this.ui.serializeNBT());
      }
   }
}
