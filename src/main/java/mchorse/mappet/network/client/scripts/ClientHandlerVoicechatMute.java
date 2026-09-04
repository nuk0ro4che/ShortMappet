package mchorse.mappet.network.client.scripts;

import mchorse.mappet.compat.voicechat.SimpleVoiceChatBridge;
import mchorse.mappet.network.common.scripts.PacketVoicechatMute;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerVoicechatMute extends ClientMessageHandler<PacketVoicechatMute> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketVoicechatMute message) {
      SimpleVoiceChatBridge.setClientPlayerMuted(message.player, message.muted);
   }
}
