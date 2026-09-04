package mchorse.mappet.compat.voicechat;

import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import de.maxhenkel.voicechat.api.events.ClientReceiveSoundEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.compat.events.SubscribeEvent;
import mchorse.mappet.events.RegisterServerTriggerEvent;
import net.minecraft.class_3222;


@ForgeVoicechatPlugin
public class MappetSimpleVoiceChatPlugin implements VoicechatPlugin {
   private static final AtomicBoolean REGISTERED = new AtomicBoolean();
   private static final double SILENCE_DBFS = -96.0D;
   private final ConcurrentMap<UUID, OpusDecoder> volumeDecoders = new ConcurrentHashMap();
   private volatile VoicechatApi api;

   public String getPluginId() {
      return "mappet";
   }

   public void initialize(VoicechatApi api) {
      this.api = api;
      if (REGISTERED.compareAndSet(false, true)) {
         Mappet.EVENT_BUS.register(this);
      }

      if (Mappet.settings != null) {
         this.registerTriggers(Mappet.settings);
      }
   }

   public void registerEvents(EventRegistration registration) {
      registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacket);
      registration.registerEvent(ClientReceiveSoundEvent.class, this::onClientReceiveSound);
   }

   private void onMicrophonePacket(MicrophonePacketEvent event) {
      VoicechatConnection connection = event.getSenderConnection();
      if (connection == null || !(connection.getPlayer().getPlayer() instanceof class_3222)) {
         return;
      }

      class_3222 player = (class_3222)connection.getPlayer().getPlayer();
      double volumeDbfs = this.calculateVolumeDbfs(player.method_5667(), event.getPacket().getOpusEncodedData());
      if (SimpleVoiceChatBridge.receiveMicrophonePacket(player, volumeDbfs)) {
         event.cancel();
      }
   }

   



   private double calculateVolumeDbfs(UUID player, byte[] encodedAudio) {
      VoicechatApi currentApi = this.api;
      if (currentApi == null || player == null || encodedAudio == null || encodedAudio.length == 0) {
         return SILENCE_DBFS;
      }

      try {
         OpusDecoder decoder = this.volumeDecoders.computeIfAbsent(player, (id) -> currentApi.createDecoder());
         short[] samples;
         synchronized(decoder) {
            samples = decoder.decode(encodedAudio);
         }
         if (samples == null || samples.length == 0) {
            return SILENCE_DBFS;
         }

         double squareSum = 0.0D;
         for(short sample : samples) {
            double normalized = (double)sample / 32768.0D;
            squareSum += normalized * normalized;
         }

         double rms = Math.sqrt(squareSum / (double)samples.length);
         return rms <= 1.0E-5D ? SILENCE_DBFS : Math.max(SILENCE_DBFS, 20.0D * Math.log10(rms));
      } catch (Exception ignored) {
         
         return SILENCE_DBFS;
      }
   }

   private void onClientReceiveSound(ClientReceiveSoundEvent event) {
      if (SimpleVoiceChatBridge.isClientPlayerMuted(event.getId())) {
         short[] audio = event.getRawAudio();
         if (audio != null && audio.length > 0) {
            event.setRawAudio(new short[audio.length]);
         }
      }
   }

   @SubscribeEvent
   public void onRegisterServerTriggers(RegisterServerTriggerEvent event) {
      this.registerTriggers(event);
   }

   private void registerTriggers(RegisterServerTriggerEvent event) {
      this.registerTrigger(event, SimpleVoiceChatBridge.TALKING_TRIGGER);
      this.registerTrigger(event, SimpleVoiceChatBridge.STARTED_TRIGGER);
      this.registerTrigger(event, SimpleVoiceChatBridge.STOPPED_TRIGGER);
   }

   private void registerTriggers(mchorse.mappet.api.misc.ServerSettings settings) {
      this.registerTrigger(settings, SimpleVoiceChatBridge.TALKING_TRIGGER);
      this.registerTrigger(settings, SimpleVoiceChatBridge.STARTED_TRIGGER);
      this.registerTrigger(settings, SimpleVoiceChatBridge.STOPPED_TRIGGER);
   }

   private void registerTrigger(RegisterServerTriggerEvent event, String key) {
      event.register(key, new Trigger());
   }

   private void registerTrigger(mchorse.mappet.api.misc.ServerSettings settings, String key) {
      if (!settings.registered.containsKey(key)) {
         settings.register(key, new Trigger());
      }
   }
}
