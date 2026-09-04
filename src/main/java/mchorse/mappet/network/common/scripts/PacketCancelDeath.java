package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;

public class PacketCancelDeath implements IMessage {
    public boolean cancel;

    public PacketCancelDeath() {
    }

    public PacketCancelDeath(boolean cancel) {
        this.cancel = cancel;
    }

    public void fromBytes(ByteBuf buf) {
        this.cancel = buf.readBoolean();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.cancel);
    }
}
