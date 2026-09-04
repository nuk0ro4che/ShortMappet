package mchorse.mappet.compat.voicechat;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.api.utils.DataContext;
import net.minecraft.class_3222;






public final class SimpleVoiceChatBridge {
   public static final String TALKING_TRIGGER = "voicechat_talking";
   public static final String STARTED_TRIGGER = "voicechat_started";
   public static final String STOPPED_TRIGGER = "voicechat_stopped";
   private static final long SPEAKING_TIMEOUT_MS = 350L;
   private static final Map<UUID, SpeakingState> SPEAKERS = new ConcurrentHashMap();
   private static final Set<UUID> SERVER_MUTED_MICROPHONES = ConcurrentHashMap.newKeySet();
   private static final Set<UUID> CLIENT_MUTED_PLAYERS = ConcurrentHashMap.newKeySet();

   private SimpleVoiceChatBridge() {
   }

   
   public static boolean receiveMicrophonePacket(class_3222 player, double volumeDbfs) {
      if (player == null) {
         return false;
      }

      UUID id = player.method_5667();
      if (SERVER_MUTED_MICROPHONES.contains(id)) {
         return true;
      }

      SpeakingState state = (SpeakingState)SPEAKERS.computeIfAbsent(id, (key) -> new SpeakingState(player));
      state.player = player;
      state.volumeDbfs = volumeDbfs;
      state.lastPacketAt = System.currentTimeMillis();
      return false;
   }

   
   public static void tick() {
      if (Mappet.settings == null) {
         return;
      }

      long now = System.currentTimeMillis();
      for(Map.Entry<UUID, SpeakingState> entry : SPEAKERS.entrySet()) {
         SpeakingState state = (SpeakingState)entry.getValue();
         class_3222 player = state.player;
         boolean speaking = player != null && now - state.lastPacketAt <= SPEAKING_TIMEOUT_MS;

         if (speaking) {
            if (!state.speaking) {
               state.speaking = true;
               trigger(STARTED_TRIGGER, player, Double.NaN);
            }

            trigger(TALKING_TRIGGER, player, state.volumeDbfs);
            continue;
         }

         if (state.speaking && player != null) {
            state.speaking = false;
            trigger(STOPPED_TRIGGER, player, Double.NaN);
         }

         SPEAKERS.remove(entry.getKey(), state);
      }
   }

   private static void trigger(String key, class_3222 player, double volumeDbfs) {
      Trigger trigger = (Trigger)Mappet.settings.registered.get(key);
      if (trigger != null && !trigger.isEmpty()) {
         DataContext context = new DataContext(player);
         if (TALKING_TRIGGER.equals(key) && !Double.isNaN(volumeDbfs)) {
            context.set("volume", volumeDbfs);
         }

         trigger.trigger(context);
      }
   }

   public static void setMicrophoneMuted(UUID player, boolean muted) {
      if (player == null) {
         return;
      }

      if (muted) {
         SERVER_MUTED_MICROPHONES.add(player);
      } else {
         SERVER_MUTED_MICROPHONES.remove(player);
      }
   }

   public static boolean isMicrophoneMuted(UUID player) {
      return player != null && SERVER_MUTED_MICROPHONES.contains(player);
   }

   
   public static void setClientPlayerMuted(UUID player, boolean muted) {
      if (player == null) {
         return;
      }

      if (muted) {
         CLIENT_MUTED_PLAYERS.add(player);
      } else {
         CLIENT_MUTED_PLAYERS.remove(player);
      }
   }

   public static boolean isClientPlayerMuted(UUID player) {
      return player != null && CLIENT_MUTED_PLAYERS.contains(player);
   }

   private static final class SpeakingState {
      public volatile class_3222 player;
      public volatile long lastPacketAt;
      public volatile boolean speaking;
      
      public volatile double volumeDbfs = -96.0D;

      public SpeakingState(class_3222 player) {
         this.player = player;
         this.lastPacketAt = System.currentTimeMillis();
      }
   }
}
