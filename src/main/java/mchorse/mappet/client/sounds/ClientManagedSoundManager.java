package mchorse.mappet.client.sounds;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketManagedSound;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1102;
import net.minecraft.class_1113;
import net.minecraft.class_1144;
import net.minecraft.class_310;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;


@Environment(EnvType.CLIENT)
public final class ClientManagedSoundManager {
   private static final Map<String, ClientManagedSoundInstance> SOUNDS = new HashMap();
   private static Field soundSystemField;
   private static Field sourceMapField;
   private static Method sourceManagerRun;
   private static Field sourceHandleField;

   private ClientManagedSoundManager() {
   }

   public static void handle(PacketManagedSound packet) {
      if (packet.id == null || packet.id.isEmpty()) {
         return;
      }

      if (packet.action == PacketManagedSound.PLAY) {
         play(packet);
      } else if (packet.action == PacketManagedSound.UPDATE) {
         ClientManagedSoundInstance sound = (ClientManagedSoundInstance)SOUNDS.get(packet.id);
         if (sound != null) {
            sound.update(packet.x, packet.y, packet.z, packet.volume);
            applyLiveProperties(class_310.method_1551().method_1483(), sound);
         }
      } else if (packet.action == PacketManagedSound.STOP) {
         stop(packet.id, false);
      } else if (packet.action == PacketManagedSound.SET_TIME_CODE) {
         ClientManagedSoundInstance sound = (ClientManagedSoundInstance)SOUNDS.get(packet.id);
         if (sound != null) {
            sound.setTimeCode(packet.timeCode);
         }
      } else if (packet.action == PacketManagedSound.REQUEST_TIME_CODE) {
         ClientManagedSoundInstance sound = (ClientManagedSoundInstance)SOUNDS.get(packet.id);
         Dispatcher.sendToServer(PacketManagedSound.timeCode(packet.id, sound == null ? 0.0D : sound.getFallbackTimeCode()));
      }
   }

   public static boolean has(String id) {
      return id != null && SOUNDS.containsKey(id);
   }

   public static String getName(String id) {
      ClientManagedSoundInstance sound = id == null ? null : (ClientManagedSoundInstance)SOUNDS.get(id);
      return sound == null ? "" : sound.name;
   }

   public static double getTimeCode(String id) {
      ClientManagedSoundInstance sound = id == null ? null : (ClientManagedSoundInstance)SOUNDS.get(id);
      return sound == null ? 0.0D : sound.getFallbackTimeCode();
   }

   public static void tick() {
      class_310 client = class_310.method_1551();
      if (client == null) {
         return;
      }

      class_1144 soundManager = client.method_1483();
      Iterator<Map.Entry<String, ClientManagedSoundInstance>> iterator = SOUNDS.entrySet().iterator();

      while(iterator.hasNext()) {
         Map.Entry<String, ClientManagedSoundInstance> entry = (Map.Entry)iterator.next();
         ClientManagedSoundInstance sound = (ClientManagedSoundInstance)entry.getValue();
         if (sound.entityBound && sound.entityId >= 0 && client.field_1724 != null && client.field_1724.method_37908() != null) {
            net.minecraft.class_1297 entity = client.field_1724.method_37908().method_8469(sound.entityId);
            if (entity != null) {
               sound.update(entity.method_23317(), entity.method_23318(), entity.method_23321(), sound.getLiveVolume());
               applyLiveProperties(soundManager, sound);
            }
         }
         Double timeCode = sound.consumePendingTimeCode();
         if (timeCode != null) {
            applyTimeCode(soundManager, sound, (Double)timeCode);
         }

         
         if (sound.getAge() > 2 && !soundManager.method_4877(sound)) {
            iterator.remove();
            sound.finish();
            Dispatcher.sendToServer(PacketManagedSound.finished(sound.id, sound.name));
         }
      }
   }

   public static void clear() {
      for(ClientManagedSoundInstance sound : SOUNDS.values()) {
         class_310.method_1551().method_1483().method_4870(sound);
         sound.finish();
      }

      SOUNDS.clear();
   }

   private static void play(PacketManagedSound packet) {
      stop(packet.id, false);
      ClientManagedSoundInstance sound = new ClientManagedSoundInstance(packet);
      SOUNDS.put(packet.id, sound);
      class_310.method_1551().method_1483().method_4873(sound);
   }

   private static void stop(String id, boolean notifyServer) {
      ClientManagedSoundInstance sound = (ClientManagedSoundInstance)SOUNDS.remove(id);
      if (sound != null) {
         class_310.method_1551().method_1483().method_4870(sound);
         sound.finish();
         if (notifyServer) {
            Dispatcher.sendToServer(PacketManagedSound.finished(sound.id, sound.name));
         }
      }
   }

   



   private static void applyTimeCode(class_1144 soundManager, ClientManagedSoundInstance sound, double seconds) {
      if (!withSource(soundManager, sound, (source) -> setOpenAlTimeCode(source, seconds))) {
         
         sound.setTimeCode(seconds);
      }
   }

   private static void applyLiveProperties(class_1144 soundManager, ClientManagedSoundInstance sound) {
      withSource(soundManager, sound, (source) -> setOpenAlLiveProperties(source, sound));
   }

   private static boolean withSource(class_1144 soundManager, ClientManagedSoundInstance sound, Consumer<Object> action) {
      try {
         if (soundSystemField == null) {
            soundSystemField = class_1144.class.getDeclaredField("field_5590");
            soundSystemField.setAccessible(true);
         }

         Object soundSystem = soundSystemField.get(soundManager);
         if (sourceMapField == null) {
            sourceMapField = soundSystem.getClass().getDeclaredField("field_18950");
            sourceMapField.setAccessible(true);
         }

         Map<?, ?> sources = (Map<?, ?>)sourceMapField.get(soundSystem);
         Object sourceManager = sources.get(sound);
         if (sourceManager == null) {
            return false;
         }

         if (sourceManagerRun == null) {
            sourceManagerRun = sourceManager.getClass().getMethod("method_19735", Consumer.class);
         }

         sourceManagerRun.invoke(sourceManager, action);
         return true;
      } catch (Exception ignored) {
         return false;
      }
   }

   private static void setOpenAlTimeCode(Object source, double seconds) {
      try {
         AL10.alSourcef(getOpenAlSourceHandle(source), AL11.AL_SEC_OFFSET, (float)Math.max(0.0D, seconds));
      } catch (Exception ignored) {
      }
   }

   private static void setOpenAlLiveProperties(Object source, ClientManagedSoundInstance sound) {
      try {
         int handle = getOpenAlSourceHandle(source);
         AL10.alSourcef(handle, AL10.AL_GAIN, sound.getLiveVolume());
         AL10.alSource3f(handle, AL10.AL_POSITION, (float)sound.getLiveX(), (float)sound.getLiveY(), (float)sound.getLiveZ());
      } catch (Exception ignored) {
      }
   }

   private static int getOpenAlSourceHandle(Object source) throws IllegalAccessException, NoSuchFieldException {
      if (sourceHandleField == null) {
         sourceHandleField = source.getClass().getDeclaredField("field_18893");
         sourceHandleField.setAccessible(true);
      }

      return sourceHandleField.getInt(source);
   }
}
