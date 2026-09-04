package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketScriptSearchResults implements IMessage
{
    private static final int MAX_RESULTS = 4096;
    private static final int MAX_TEXT_LENGTH = 1024;

    public boolean clientScript;
    public final List<ScriptSearchResult> results = new ArrayList<>();

    public PacketScriptSearchResults()
    {
    }

    public PacketScriptSearchResults(List<ScriptSearchResult> results)
    {
        this(results, false);
    }

    public PacketScriptSearchResults(List<ScriptSearchResult> results, boolean clientScript)
    {
        this.clientScript = clientScript;
        if (results != null)
        {
            this.results.addAll(results.subList(0, Math.min(results.size(), MAX_RESULTS)));
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.results.clear();
        this.clientScript = buf.readBoolean();
        int size = Math.min(Math.max(buf.readInt(), 0), MAX_RESULTS);

        for (int i = 0; i < size; i++)
        {
            String script = limit(ForgeByteBufUtils.readUTF8String(buf));
            int line = buf.readInt();
            int column = buf.readInt();
            int length = buf.readInt();
            String text = limit(ForgeByteBufUtils.readUTF8String(buf));
            this.results.add(new ScriptSearchResult(script, line, column, length, text));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        int size = Math.min(this.results.size(), MAX_RESULTS);
        buf.writeBoolean(this.clientScript);
        buf.writeInt(size);

        for (int i = 0; i < size; i++)
        {
            ScriptSearchResult result = this.results.get(i);
            ForgeByteBufUtils.writeUTF8String(buf, limit(result.script));
            buf.writeInt(result.line);
            buf.writeInt(result.column);
            buf.writeInt(result.length);
            ForgeByteBufUtils.writeUTF8String(buf, limit(result.text));
        }
    }

    private static String limit(String value)
    {
        if (value == null)
        {
            return "";
        }

        return value.length() > MAX_TEXT_LENGTH ? value.substring(0, MAX_TEXT_LENGTH) : value;
    }
}
