package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketHandMorphAnimation implements IMessage
{
    private static final int MAX_ANIMATION_LENGTH = 256;

    public String animation = "";
    public int side;

    public PacketHandMorphAnimation()
    {
    }

    public PacketHandMorphAnimation(String animation, int side)
    {
        this.animation = sanitize(animation);
        this.side = side <= 0 ? 0 : 1;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.animation = sanitize(ForgeByteBufUtils.readUTF8String(buf));
        this.side = buf.readBoolean() ? 1 : 0;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ForgeByteBufUtils.writeUTF8String(buf, sanitize(this.animation));
        buf.writeBoolean(this.side > 0);
    }

    private static String sanitize(String animation)
    {
        if (animation == null)
        {
            return "";
        }

        return animation.length() > MAX_ANIMATION_LENGTH ? animation.substring(0, MAX_ANIMATION_LENGTH) : animation;
    }
}
