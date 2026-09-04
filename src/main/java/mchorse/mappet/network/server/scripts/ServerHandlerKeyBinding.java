package mchorse.mappet.network.server.scripts;

import mchorse.mappet.api.scripts.code.client.ClientKeyBindingCache;
import mchorse.mappet.network.common.scripts.PacketKeyBinding;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_3222;


public class ServerHandlerKeyBinding extends ServerMessageHandler<PacketKeyBinding>
{
    @Override
    public void run(class_3222 player, PacketKeyBinding message)
    {
        if (message.action == PacketKeyBinding.RESPONSE)
        {
            ClientKeyBindingCache.set(player.method_5667(), message.id, message.key);
        }
    }
}
