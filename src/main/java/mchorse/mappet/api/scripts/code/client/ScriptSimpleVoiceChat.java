package mchorse.mappet.api.scripts.code.client;

import java.util.UUID;
import mchorse.mappet.api.scripts.user.client.ISimpleVoiceChat;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import mchorse.mappet.compat.voicechat.SimpleVoiceChatBridge;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketVoicechatMute;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_3222;


public class ScriptSimpleVoiceChat implements ISimpleVoiceChat {
   private final class_1297 subject;

   public ScriptSimpleVoiceChat(class_1297 subject) {
      this.subject = subject;
   }

   public void setMute(boolean muted) {
      UUID id = this.getSubjectId();
      if (id != null) {
         SimpleVoiceChatBridge.setMicrophoneMuted(id, muted);
      }
   }

   public boolean isMuted() {
      UUID id = this.getSubjectId();
      return id != null && SimpleVoiceChatBridge.isMicrophoneMuted(id);
   }

   public void setPlayerMuted(IScriptEntity player, boolean muted) {
      if (!(this.subject instanceof class_3222)) {
         return;
      }

      UUID id = this.getId(player);
      if (id != null) {
         Dispatcher.sendTo(new PacketVoicechatMute(id, muted), (class_3222)this.subject);
      }
   }

   public boolean isPlayerMuted(IScriptEntity player) {
      UUID id = this.getId(player);
      return id != null && SimpleVoiceChatBridge.isClientPlayerMuted(id);
   }

   private UUID getSubjectId() {
      return this.subject instanceof class_1657 ? ((class_1657)this.subject).method_5667() : null;
   }

   private UUID getId(IScriptEntity player) {
      if (player == null) {
         return null;
      }

      class_1297 entity = player.getMinecraftEntity();
      return entity instanceof class_1657 ? ((class_1657)entity).method_5667() : null;
   }
}
