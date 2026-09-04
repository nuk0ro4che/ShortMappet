package mchorse.mappet.network.common.ui;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.api.ui.UI;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import net.minecraft.class_2487;

public class PacketUI implements IMessage {
   public UI ui;
   public boolean editorPreview;

   public PacketUI() {
   }

   public PacketUI(UI ui) {
      this.ui = ui;
   }

   public void fromBytes(ByteBuf buf) {
      this.ui = new UI();
      this.ui.deserializeNBT(ForgeByteBufUtils.readTag(buf));
      this.editorPreview = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      class_2487 tag = this.ui == null ? new class_2487() : this.ui.serializeNBT();
      ForgeByteBufUtils.writeTag(buf, tag);
      buf.writeBoolean(this.editorPreview);
   }
}
