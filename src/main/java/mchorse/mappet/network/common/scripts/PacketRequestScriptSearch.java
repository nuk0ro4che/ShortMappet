package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketRequestScriptSearch implements IMessage
{
    private static final int MAX_QUERY_LENGTH = 256;

    public String query = "";
    public boolean clientScript;

    public PacketRequestScriptSearch()
    {
    }

    public PacketRequestScriptSearch(String query)
    {
        this(query, false);
    }

    public PacketRequestScriptSearch(String query, boolean clientScript)
    {
        this.query = limit(query);
        this.clientScript = clientScript;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.query = limit(ForgeByteBufUtils.readUTF8String(buf));
        this.clientScript = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ForgeByteBufUtils.writeUTF8String(buf, limit(this.query));
        buf.writeBoolean(this.clientScript);
    }

    private static String limit(String value)
    {
        if (value == null)
        {
            return "";
        }

        return value.length() > MAX_QUERY_LENGTH ? value.substring(0, MAX_QUERY_LENGTH) : value;
    }
}
