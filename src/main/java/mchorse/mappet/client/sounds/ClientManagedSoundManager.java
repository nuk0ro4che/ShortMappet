package mchorse.mappet.client.sounds;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import mchorse.mappet.Mappet;
import mchorse.mappet.client.ClientTriggers;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.scripts.user.data.ScriptVector;
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
   private static final Map<String, ClientManagedSoundInstance> SOUNDS = new ConcurrentHashMap();
   private static Field soundSystemField;
   private static Field sourceMapField;
   private static Method sourceManagerRun;
   private static Field sourceHandleField;
   private static Field sourceChannelField;

   private static final boolean DEBUG_SOUND = System.getProperty("mappet.sound.debug") != null;

   private static void debugSound(String message, Object... args) {
      if (DEBUG_SOUND) {
         Mappet.LOGGER.warn(message, args);
      }
   }

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
      } else if (packet.action == PacketManagedSound.PAUSE) {
         ClientManagedSoundManager.applyPause(packet.id, true);
      } else if (packet.action == PacketManagedSound.RESUME) {
         ClientManagedSoundManager.applyPause(packet.id, false);
      } else if (packet.action == PacketManagedSound.LOOP) {
         ClientManagedSoundInstance sound = (ClientManagedSoundInstance)SOUNDS.get(packet.id);
         if (sound != null) {
            sound.setLooping(packet.loop);
            withSource(class_310.method_1551().method_1483(), sound, (source) -> {
               try {
                  AL10.alSourcei(getOpenAlSourceHandle(source), 4103, packet.loop ? 1 : 0);
               } catch (Exception ignored) {
               }
            });
         }
      }
   }

   public static boolean has(String id) {
      return id != null && SOUNDS.containsKey(id);
   }

   public static boolean isPaused(String id) {
      ClientManagedSoundInstance sound = id == null ? null : (ClientManagedSoundInstance)SOUNDS.get(id);
      return sound != null && sound.isPaused();
   }

   public static boolean isLooping(String id) {
      ClientManagedSoundInstance sound = id == null ? null : (ClientManagedSoundInstance)SOUNDS.get(id);
      return sound != null && sound.isLooping();
   }

   public static String getName(String id) {
      ClientManagedSoundInstance sound = id == null ? null : (ClientManagedSoundInstance)SOUNDS.get(id);
      return sound == null ? "" : sound.name;
   }

   public static double getTimeCode(String id) {
      ClientManagedSoundInstance sound = id == null ? null : (ClientManagedSoundInstance)SOUNDS.get(id);
      return sound == null ? 0.0D : sound.getFallbackTimeCode();
   }

   public static float getVolume(String id) {
      ClientManagedSoundInstance sound = id == null ? null : (ClientManagedSoundInstance)SOUNDS.get(id);
      return sound == null ? 0.0F : sound.getLiveVolume();
   }

   public static double getX(String id) {
      ClientManagedSoundInstance sound = id == null ? null : (ClientManagedSoundInstance)SOUNDS.get(id);
      return sound == null ? 0.0D : sound.getLiveX();
   }

   public static double getY(String id) {
      ClientManagedSoundInstance sound = id == null ? null : (ClientManagedSoundInstance)SOUNDS.get(id);
      return sound == null ? 0.0D : sound.getLiveY();
   }

   public static double getZ(String id) {
      ClientManagedSoundInstance sound = id == null ? null : (ClientManagedSoundInstance)SOUNDS.get(id);
      return sound == null ? 0.0D : sound.getLiveZ();
   }

   public static void tick() {
      class_310 client = class_310.method_1551();
      if (client == null) {
         return;
      }

      class_1144 soundManager = client.method_1483();
      boolean gamePaused = client.method_1493();
      Iterator<Map.Entry<String, ClientManagedSoundInstance>> iterator = SOUNDS.entrySet().iterator();

      while(iterator.hasNext()) {
         Map.Entry<String, ClientManagedSoundInstance> entry = (Map.Entry)iterator.next();
         ClientManagedSoundInstance sound = (ClientManagedSoundInstance)entry.getValue();
         if (sound.entityBound && sound.entityId >= 0 && client.field_1724 != null && client.field_1724.method_37908() != null) {
             net.minecraft.class_1297 entity = client.field_1724.method_37908().method_8469(sound.entityId);
             if (entity != null) {
                sound.update(entity.method_23317(), entity.method_23318(), entity.method_23321(), sound.getLiveVolume());
             }
          }

          applyLiveProperties(soundManager, sound);

          float categoryVolume = class_310.method_1551().field_1690.method_1630(sound.category);
          boolean muted = categoryVolume <= 0.0F || sound.getLiveVolume() <= 0.0F;
          if (muted && !sound.isVolumeMuted()) {
             sound.setFallbackTimeCode(sound.getFallbackTimeCode());
             sound.setVolumeMuted(true);
             withSource(soundManager, sound, (source) -> {
                try {
                   AL10.alSourceStop(getOpenAlSourceHandle(source));
                } catch (Exception ignored) {
                }
             });
             debugSound("[mute] id={} volume={} category={}", sound.id, sound.getLiveVolume(), categoryVolume);
          } else if (!muted && sound.isVolumeMuted()) {
             sound.setVolumeMuted(false);
             debugSound("[unmute] id={} volume={} category={}", sound.id, sound.getLiveVolume(), categoryVolume);
             if (!sound.isPaused() && !sound.isGamePaused()) {
                resumeManagedSound(soundManager, sound);
             }
          }

         if (!sound.isPaused() && !sound.isVolumeMuted()) {
            if (gamePaused && !sound.isGamePaused()) {
               Integer alState = getOpenAlSourceState(soundManager, sound);
               Double alOffset = readOpenAlOffset(soundManager, sound);
               double captured = sound.getFallbackTimeCode();
               sound.setFallbackTimeCode(captured);
               sound.setGamePaused(true);
               withSource(soundManager, sound, (source) -> {
                  try {
                     AL10.alSourceStop(getOpenAlSourceHandle(source));
                  } catch (Exception ignored) {
                  }
               });
               debugSound("[game-pause] id={} alState={} alOffset={} captured={}", sound.id, alState, alOffset, captured);
            } else if (!gamePaused && sound.isGamePaused()) {
               resumeManagedSound(soundManager, sound);
            }
         }

Double timeCode = sound.consumePendingTimeCode();
          if (timeCode != null) {
             applyTimeCode(soundManager, sound, timeCode);
          } else if (!sound.isPaused() && !sound.isGamePaused() && !sound.isVolumeMuted()) {
             syncFallbackToOpenAl(soundManager, sound);
          }
       }

       pollFinished();
    }

    public static void pollFinished() {
       class_310 client = class_310.method_1551();
       if (client == null) {
          return;
       }

       class_1144 soundManager = client.method_1483();
       Iterator<Map.Entry<String, ClientManagedSoundInstance>> iterator = SOUNDS.entrySet().iterator();

       while (iterator.hasNext()) {
          Map.Entry<String, ClientManagedSoundInstance> entry = (Map.Entry)iterator.next();
          ClientManagedSoundInstance sound = (ClientManagedSoundInstance)entry.getValue();
          boolean finished;
          if (!sound.isPaused() && !sound.isVolumeMuted() && !sound.isGamePaused()) {
             Integer alState = getOpenAlSourceState(soundManager, sound);
             if (alState != null) {
                if (alState != AL10.AL_INITIAL) {
                   sound.markStarted();
                }

                finished = (alState == AL10.AL_STOPPED);
             } else {
                if (soundManager.method_4877(sound)) {
                   sound.markStarted();
                }

                finished = (sound.isStarted() && !soundManager.method_4877(sound));
             }
          } else {
             finished = false;
          }

          if (finished) {
             iterator.remove();
             sound.finish();
Dispatcher.sendToServer(PacketManagedSound.finished(sound.id, sound.name));
              fireSoundEnded(client, sound);
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
             fireSoundEnded(class_310.method_1551(), sound);
         }
      }
   }

   private static void applyPause(String id, boolean paused) {
      ClientManagedSoundInstance sound = (ClientManagedSoundInstance)SOUNDS.get(id);
      if (sound == null) {
         return;
      }

      class_310 client = class_310.method_1551();
      if (client == null) {
         return;
      }

      if (paused) {
         double captured = sound.getFallbackTimeCode();
         sound.setFallbackTimeCode(captured);
         sound.setPaused(true);
         withSource(client.method_1483(), sound, (source) -> {
            try {
               AL10.alSourceStop(getOpenAlSourceHandle(source));
            } catch (Exception ignored) {
            }
         });
      } else {
         resumeManagedSound(client.method_1483(), sound);
      }
   }

   private static void fireSoundEnded(class_310 client, ClientManagedSoundInstance sound) {
      if (client != null && client.field_1724 != null) {
         DataContext context = DataContext.client(client.field_1724);
         context.getValues().put("volume", (double) sound.getLiveVolume());
         context.getValues().put("pitch", (double) sound.getLivePitch());
         context.getValues().put("position", new ScriptVector(sound.getLiveX(), sound.getLiveY(), sound.getLiveZ()));
         ClientTriggers.trigger("sound_ended", context);
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

   private static void resumeManagedSound(class_1144 soundManager, ClientManagedSoundInstance sound) {
      try {
         double timeCode = sound.getFallbackTimeCode();
         sound.setPaused(false);
         sound.setGamePaused(false);
         sound.setFallbackTimeCode(timeCode);
         sound.setTimeCode(timeCode);
         soundManager.method_4873(sound);
         applyTimeCode(soundManager, sound, timeCode);
         debugSound("[resume] id={} branch=replay timeCode={}", sound.id, timeCode);
      } catch (Exception ignored) {
      }
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
         AL10.alSourcef(handle, AL10.AL_GAIN, sound.getLiveVolume() * class_310.method_1551().field_1690.method_1630(sound.category));
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

   private static Integer getOpenAlSourceState(class_1144 soundManager, ClientManagedSoundInstance sound) {
      try {
         if (soundSystemField == null) {
            soundSystemField = class_1144.class.getDeclaredField("field_5590");
            soundSystemField.setAccessible(true);
         }
         Object soundSystem = soundSystemField.get(soundManager);
         if (soundSystem == null) {
            return null;
         }

         if (sourceMapField == null) {
            sourceMapField = soundSystem.getClass().getDeclaredField("field_18950");
            sourceMapField.setAccessible(true);
         }
         Map<?, ?> sources = (Map<?, ?>)sourceMapField.get(soundSystem);
         Object sourceManager = sources.get(sound);
         if (sourceManager == null) {
            return null;
         }

         return AL10.alGetSourcei(getOpenAlSourceHandleFromManager(sourceManager), AL10.AL_SOURCE_STATE);
      } catch (Exception e) {
         return null;
      }
   }

   private static Double readOpenAlOffset(class_1144 soundManager, ClientManagedSoundInstance sound) {
      try {
         if (soundSystemField == null) {
            soundSystemField = class_1144.class.getDeclaredField("field_5590");
            soundSystemField.setAccessible(true);
         }
         Object soundSystem = soundSystemField.get(soundManager);
         if (soundSystem == null) {
            return null;
         }

         if (sourceMapField == null) {
            sourceMapField = soundSystem.getClass().getDeclaredField("field_18950");
            sourceMapField.setAccessible(true);
         }
         Map<?, ?> sources = (Map<?, ?>)sourceMapField.get(soundSystem);
         Object sourceManager = sources.get(sound);
         if (sourceManager == null) {
            return null;
         }

         return (double)AL10.alGetSourcef(getOpenAlSourceHandleFromManager(sourceManager), AL11.AL_SEC_OFFSET);
      } catch (Exception e) {
         return null;
      }
   }

   private static int getOpenAlSourceHandleFromManager(Object sourceManager) throws IllegalAccessException, NoSuchFieldException {
      if (sourceChannelField == null) {
         sourceChannelField = sourceManager.getClass().getDeclaredField("field_18941");
         sourceChannelField.setAccessible(true);
      }

      return getOpenAlSourceHandle(sourceChannelField.get(sourceManager));
   }

   private static void syncFallbackToOpenAl(class_1144 soundManager, ClientManagedSoundInstance sound) {
      withSource(soundManager, sound, (source) -> {
         try {
            int handle = getOpenAlSourceHandle(source);
            if (AL10.alGetSourcei(handle, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING) {
               sound.setFallbackTimeCode((double)AL10.alGetSourcef(handle, AL11.AL_SEC_OFFSET));
            }
         } catch (Exception ignored) {
         }
      });
   }
}
