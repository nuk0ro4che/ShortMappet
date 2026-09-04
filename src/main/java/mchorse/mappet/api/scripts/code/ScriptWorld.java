package mchorse.mappet.api.scripts.code;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.vecmath.Vector3d;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.entity.EntityGunProjectile;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.npcs.Npc;
import mchorse.mappet.api.npcs.NpcState;
import mchorse.mappet.api.scripts.code.blocks.ScriptBlockState;
import mchorse.mappet.api.scripts.code.blocks.ScriptTileEntity;
import mchorse.mappet.api.scripts.code.entities.ScriptEntity;
import mchorse.mappet.api.scripts.code.entities.ScriptNpc;
import mchorse.mappet.api.scripts.code.items.ScriptInventory;
import mchorse.mappet.api.scripts.code.items.ScriptItemStack;
import mchorse.mappet.api.scripts.code.mappet.MappetSchematic;
import mchorse.mappet.api.scripts.code.nbt.ScriptNBTCompound;
import mchorse.mappet.api.scripts.code.sounds.ManagedSoundRegistry;
import mchorse.mappet.api.scripts.code.sounds.ScriptWorldManagedSound;
import mchorse.mappet.api.scripts.user.IScriptRayTrace;
import mchorse.mappet.api.scripts.user.IScriptWorld;
import mchorse.mappet.api.scripts.user.blocks.IScriptBlockState;
import mchorse.mappet.api.scripts.user.blocks.IScriptTileEntity;
import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import mchorse.mappet.api.scripts.user.entities.IScriptEntityItem;
import mchorse.mappet.api.scripts.user.entities.IScriptNpc;
import mchorse.mappet.api.scripts.user.entities.IScriptPlayer;
import mchorse.mappet.api.scripts.user.items.IScriptInventory;
import mchorse.mappet.api.scripts.user.items.IScriptItemStack;
import mchorse.mappet.api.scripts.user.nbt.INBTCompound;
import mchorse.mappet.api.scripts.user.sounds.IScriptManagedSound;
import mchorse.mappet.api.utils.RayTracing;
import mchorse.mappet.client.morphs.WorldMorph;
import mchorse.mappet.entities.EntityNpc;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketWorldMorph;
import mchorse.mappet.api.scripts.lights.VanillaWorldLightManager;
import mchorse.mappet.api.scripts.user.lights.IScriptLight;
import mchorse.mappet.network.common.scripts.PacketManagedSound;
import mchorse.mappet.utils.WorldUtils;
import mchorse.mclib.utils.MathUtils;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1263;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1542;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2394;
import net.minecraft.class_2396;
import net.minecraft.class_2487;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_2769;
import net.minecraft.class_2770;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_638;
import net.minecraft.class_3222;
import net.minecraft.class_3419;
import net.minecraft.class_7923;
import net.minecraft.class_1937.class_7867;

public class ScriptWorld implements IScriptWorld {
   public static final int MAX_VOLUME = 100;
   private class_1937 world;
   private class_2338.class_2339 pos = new class_2338.class_2339();

   public ScriptWorld(class_1937 world) {
      this.world = world;
   }

   public class_1937 getMinecraftWorld() {
      return this.world;
   }

   public void setGameRule(String gameRule, Object value) {
      if (!(this.world instanceof class_3218)) {
         return;
      }
      if (!(value instanceof Boolean) && !(value instanceof String) && !(value instanceof Integer)) {
         throw new IllegalArgumentException("Unsupported game rule value type: " + String.valueOf(value.getClass()));
      } else {
         this.world.method_8503().method_3734().method_44252(this.world.method_8503().method_3739(), "gamerule " + gameRule + " " + String.valueOf(value));
      }
   }

   public Object getGameRule(String gameRule) {
      if (this.world.method_8450().method_8358().method_10545(gameRule)) {
         String value = this.world.method_8450().method_8358().method_10558(gameRule);
         if (!value.equals("true") && !value.equals("false")) {
            try {
               return Integer.valueOf(value);
            } catch (NumberFormatException var4) {
               return value;
            }
         } else {
            return Boolean.valueOf(value);
         }
      } else {
         throw new IllegalArgumentException("No such game rule: " + gameRule);
      }
   }

   public void setBlock(IScriptBlockState state, int x, int y, int z) {
      if (state == null || !this.world.method_22340(this.pos.method_10103(x, y, z))) {
         return;
      }
      if (this.world instanceof class_3218) {
         this.world.method_8652(this.pos, state.getMinecraftBlockState(), 6);
      } else if (this.world instanceof class_638) {
         

         ((class_638)this.world).method_30092(this.pos, state.getMinecraftBlockState(), 3, 512);
      }
   }

   public void removeBlock(int x, int y, int z) {
      if (!this.world.method_22340(this.pos.method_10103(x, y, z))) {
         return;
      }
      if (this.world instanceof class_3218) {
         this.world.method_8650(this.pos, false);
      } else if (this.world instanceof class_638) {
         ((class_638)this.world).method_30092(this.pos, class_2246.field_10124.method_9564(), 3, 512);
      }
   }

   public IScriptBlockState getBlock(int x, int y, int z) {
      if (!this.world.method_22340(this.pos.method_10103(x, y, z))) {
         return ScriptBlockState.AIR;
      } else {
         class_2680 blockState = this.world.method_8320(this.pos);
         return ScriptBlockState.create(blockState);
      }
   }

   public IScriptBlockState getBlock(ScriptVector pos) {
      return this.getBlock((int)pos.x, (int)pos.y, (int)pos.z);
   }

   public boolean hasBlockEntity(int x, int y, int z) {
      if (!this.world.method_22340(this.pos.method_10103(x, y, z))) {
         return false;
      } else {
         return this.world.method_8321(this.pos) != null;
      }
   }

   public void replaceBlocks(IScriptBlockState blockToBeReplaced, IScriptBlockState newBlock, Vector3d pos, int radius) {
      this.processBlocksInRegion(pos, radius, (x, y, z) -> {
         IScriptBlockState currentBlock = this.getBlock(x, y, z);
         if (currentBlock.isSame(blockToBeReplaced)) {
            this.setBlock(newBlock, x, y, z);
         }

      });
   }

   public void replaceBlocks(IScriptBlockState blockToBeReplaced, IScriptBlockState newBlock, INBTCompound tileData, Vector3d pos, int radius) {
      this.processBlocksInRegion(pos, radius, (x, y, z) -> {
         IScriptBlockState currentBlock = this.getBlock(x, y, z);
         if (currentBlock.isSame(blockToBeReplaced)) {
            this.setBlockEntity(x, y, z, newBlock, tileData);
         }

      });
   }

   private void processBlocksInRegion(Vector3d pos, int radius, BlockPosConsumer consumer) {
      int minX = (int)Math.floor(pos.x - (double)radius);
      int maxX = (int)Math.ceil(pos.x + (double)radius);
      int minY = (int)Math.floor(pos.y - (double)radius);
      int maxY = (int)Math.ceil(pos.y + (double)radius);
      int minZ = (int)Math.floor(pos.z - (double)radius);
      int maxZ = (int)Math.ceil(pos.z + (double)radius);

      for(int x = minX; x <= maxX; ++x) {
         for(int y = minY; y <= maxY; ++y) {
            for(int z = minZ; z <= maxZ; ++z) {
               if (this.world.method_22340(this.pos.method_10103(x, y, z))) {
                  double dx = pos.x - ((double)x + (double)0.5F);
                  double dy = pos.y - ((double)y + (double)0.5F);
                  double dz = pos.z - ((double)z + (double)0.5F);
                  double distanceSquared = dx * dx + dy * dy + dz * dz;
                  if (distanceSquared <= (double)(radius * radius)) {
                     consumer.accept(x, y, z);
                  }
               }
            }
         }
      }

   }

   public void replaceBlocks(IScriptBlockState blockToBeReplaced, IScriptBlockState newBlock, Vector3d pos1, Vector3d pos2) {
      this.processBlocksInRegion(pos1, pos2, (x, y, z) -> {
         IScriptBlockState currentBlock = this.getBlock(x, y, z);
         if (currentBlock.isSame(blockToBeReplaced)) {
            this.setBlock(newBlock, x, y, z);
         }

      });
   }

   public void replaceBlocks(IScriptBlockState blockToBeReplaced, IScriptBlockState newBlock, INBTCompound tileData, Vector3d pos1, Vector3d pos2) {
      this.processBlocksInRegion(pos1, pos2, (x, y, z) -> {
         IScriptBlockState currentBlock = this.getBlock(x, y, z);
         if (currentBlock.isSame(blockToBeReplaced)) {
            this.setBlockEntity(x, y, z, newBlock, tileData);
         }

      });
   }

   private void processBlocksInRegion(Vector3d pos1, Vector3d pos2, BlockPosConsumer consumer) {
      for(int x = (int)Math.min(pos1.x, pos2.x); (double)x <= Math.max(pos1.x, pos2.x); ++x) {
         for(int y = (int)Math.min(pos1.y, pos2.y); (double)y <= Math.max(pos1.y, pos2.y); ++y) {
            for(int z = (int)Math.min(pos1.z, pos2.z); (double)z <= Math.max(pos1.z, pos2.z); ++z) {
               if (this.world.method_22340(this.pos.method_10103(x, y, z))) {
                  consumer.accept(x, y, z);
               }
            }
         }
      }

   }

   public IScriptTileEntity getBlockEntity(int x, int y, int z) {
      return !this.hasBlockEntity(x, y, z) ? null : new ScriptTileEntity(this.world.method_8321(this.pos.method_10103(x, y, z)));
   }

   public boolean hasInventory(int x, int y, int z) {
      this.pos.method_10103(x, y, z);
      return this.world.method_22340(this.pos) && this.world.method_8321(this.pos) instanceof class_1263;
   }

   public IScriptInventory getInventory(int x, int y, int z) {
      if (this.world.method_22340(this.pos.method_10103(x, y, z))) {
         class_2586 tile = this.world.method_8321(this.pos);
         if (tile instanceof class_1263) {
            return new ScriptInventory((class_1263)tile);
         }
      }

      return null;
   }

   public boolean isRaining() {
      return this.world.method_8419();
   }

   public void setRaining(boolean raining) {
      if (this.world instanceof class_3218) {
         ((class_3218)this.world).method_27910(0, raining ? 6000 : 0, raining, raining);
      } else if (this.world instanceof class_638) {
         
         this.world.method_8519(raining ? 1.0F : 0.0F);
      }
   }

   public long getTime() {
      return this.world.method_8532();
   }

   public void setTime(long time) {
      if (this.world instanceof class_3218) {
         ((class_3218)this.world).method_29199(time);
      } else if (this.world instanceof class_638) {
         
         ((class_638)this.world).method_8435(time);
      }
   }

   public long getTotalTime() {
      return this.world.method_8510();
   }

   public int getDimensionId() {
      return this.world.method_27983() == class_1937.field_25180 ? -1 : (this.world.method_27983() == class_1937.field_25181 ? 1 : 0);
   }

   public void spawnParticles(class_2396 type, boolean longDistance, double x, double y, double z, int n, double dx, double dy, double dz, double speed, int... args) {
      if (this.world instanceof class_3218) {
         ((class_3218)this.world).method_14199((class_2394)type, x, y, z, n, dx, dy, dz, speed);
      } else if (this.world instanceof class_638) {
         
         this.world.method_8406((class_2394)type, x, y, z, dx, dy, dz);
      }
   }

   public void spawnParticles(IScriptPlayer entity, class_2396 type, boolean longDistance, double x, double y, double z, int n, double dx, double dy, double dz, double speed, int... args) {
      if (entity != null && this.world instanceof class_3218 && entity.getMinecraftPlayer() != null) {
         ((class_3218)this.world).method_14166(entity.getMinecraftPlayer(), (class_2394)type, longDistance, x, y, z, n, dx, dy, dz, speed);
      } else if (this.world instanceof class_638) {
         
         this.world.method_8406((class_2394)type, x, y, z, dx, dy, dz);
      }
   }

   public IScriptEntity spawnEntity(String id, double x, double y, double z, INBTCompound compound) {
      if (!(this.world instanceof class_3218) || !this.world.method_22340(this.pos.method_10102(x, y, z))) {
         return null;
      } else {
         class_2487 tag = new class_2487();
         if (compound != null) {
            tag.method_10543(compound.getNbtCompound());
         }

         tag.method_10582("id", id);
         class_1297 entity = class_1299.method_17842(tag, this.world, (loaded) -> {
            loaded.method_5808(x, y, z, loaded.method_36454(), loaded.method_36455());
            this.world.method_8649(loaded);
            return loaded;
         });
         return entity == null ? null : ScriptEntity.create(entity);
      }
   }

   public IScriptNpc spawnNpc(String id, String state, double x, double y, double z) {
      if (!(this.world instanceof class_3218)) {
         return null;
      }
      Npc npc = (Npc)Mappet.npcs.load(id);
      if (npc == null) {
         return null;
      } else {
         NpcState npcState = (NpcState)npc.states.get(state);
         if (npcState == null) {
            return null;
         } else {
            EntityNpc entity = new EntityNpc(Mappet.npcEntity, this.world);
            entity.method_5814(x, y, z);
            entity.setNpc(npc, npcState);
            entity.method_37908().method_8649(entity);
            entity.initialize();
            if (!npc.serializeNBT().method_10558("StateName").equals("default")) {
               entity.setStringInData("StateName", state);
            }

            return new ScriptNpc(entity);
         }
      }
   }

   public IScriptNpc spawnNpc(String id, String state, double x, double y, double z, float yaw, float pitch, float headYaw) {
      if (!(this.world instanceof class_3218)) {
         return null;
      }
      Npc npc = (Npc)Mappet.npcs.load(id);
      if (npc == null) {
         return null;
      } else {
         NpcState npcState = (NpcState)npc.states.get(state);
         if (npcState == null) {
            return null;
         } else {
            EntityNpc entity = new EntityNpc(Mappet.npcEntity, this.world);
            entity.method_5808(x, y, z, yaw, pitch);
            entity.method_5847(headYaw);
            entity.setNpc(npc, npcState);
            entity.method_37908().method_8649(entity);
            entity.initialize();
            if (!npc.serializeNBT().method_10558("StateName").equals("default")) {
               entity.setStringInData("StateName", state);
            }

            return new ScriptNpc(entity);
         }
      }
   }

   public List<IScriptEntity> getEntities(double x1, double y1, double z1, double x2, double y2, double z2) {
      return this.getEntities(x1, y1, z1, x2, y2, z2, false);
   }

   public List<IScriptEntity> getEntities(double x1, double y1, double z1, double x2, double y2, double z2, boolean ignoreVolumeLimit) {
      List<IScriptEntity> entities = new ArrayList();
      double minX = Math.min(x1, x2);
      double minY = Math.min(y1, y2);
      double minZ = Math.min(z1, z2);
      double maxX = Math.max(x1, x2);
      double maxY = Math.max(y1, y2);
      double maxZ = Math.max(z1, z2);
      if (ignoreVolumeLimit || !(maxX - minX > (double)100.0F) && !(maxY - minY > (double)100.0F) && !(maxZ - minZ > (double)100.0F)) {
         for(class_1297 entity : this.world.method_8335((class_1297)null, new class_238(minX, minY, minZ, maxX, maxY, maxZ))) {
            entities.add(ScriptEntity.create(entity));
         }

         return entities;
      } else {
         return entities;
      }
   }

   public List<IScriptEntity> getEntities(double x, double y, double z, double radius) {
      radius = Math.abs(radius);
      List<IScriptEntity> entities = new ArrayList();
      if (radius > (double)50.0F) {
         return entities;
      } else {
         double minX = x - radius;
         double minY = y - radius;
         double minZ = z - radius;
         double maxX = x + radius;
         double maxY = y + radius;
         double maxZ = z + radius;
         if (this.world.method_22340(this.pos.method_10102(minX, minY, minZ)) && this.world.method_22340(this.pos.method_10102(maxX, maxY, maxZ))) {
            for(class_1297 entity : this.world.method_8335((class_1297)null, new class_238(minX, minY, minZ, maxX, maxY, maxZ))) {
               class_238 box = entity.method_5829();
               double eX = (box.field_1323 + box.field_1320) / (double)2.0F;
               double eY = (box.field_1322 + box.field_1325) / (double)2.0F;
               double eZ = (box.field_1321 + box.field_1324) / (double)2.0F;
               double dX = x - eX;
               double dY = y - eY;
               double dZ = z - eZ;
               if (dX * dX + dY * dY + dZ * dZ < radius * radius) {
                  entities.add(ScriptEntity.create(entity));
               }
            }

            return entities;
         } else {
            return entities;
         }
      }
   }

   public void playSound(String event, double x, double y, double z, float volume, float pitch) {
      if (this.world instanceof class_3218) {
         for(class_3222 player : this.world.method_8503().method_3760().method_14571()) {
            WorldUtils.playSound(player, event, x, y, z, volume, pitch);
         }
      } else if (this.world instanceof class_638) {
         
         WorldUtils.playSound(this.world, x, y, z, event, volume, pitch);
      }
   }

   public IScriptManagedSound playLoopSound(String id, String event, String category, double x, double y, double z, float volume, float pitch) {
      String specialId = this.requireManagedSoundId(id);
      String soundName = this.requireManagedSoundName(event);
      String soundCategory = category == null || category.isEmpty() ? "master" : category;
      float soundVolume = Math.max(0.0F, volume);

      for(class_3222 player : this.world.method_8503().method_3760().method_14571()) {
         ManagedSoundRegistry.play(player, specialId, soundName, false, x, y, z, soundVolume, pitch);
         Dispatcher.sendTo(PacketManagedSound.play(specialId, soundName, soundCategory, false, x, y, z, soundVolume, pitch), player);
      }

      return new ScriptWorldManagedSound(this.world, specialId);
   }

   public IScriptManagedSound playManagedStaticSound(String id, String event, String category, float volume, float pitch) {
      String specialId = this.requireManagedSoundId(id);
      String soundName = this.requireManagedSoundName(event);
      String soundCategory = category == null || category.isEmpty() ? "master" : category;
      float soundVolume = Math.max(0.0F, volume);

      for(class_3222 player : this.world.method_8503().method_3760().method_14571()) {
         ManagedSoundRegistry.play(player, specialId, soundName, true, 0.0D, 0.0D, 0.0D, soundVolume, pitch);
         Dispatcher.sendTo(PacketManagedSound.play(specialId, soundName, soundCategory, true, 0.0D, 0.0D, 0.0D, soundVolume, pitch), player);
      }

      return new ScriptWorldManagedSound(this.world, specialId);
   }

   public IScriptManagedSound getManagedSound(String id) {
      return new ScriptWorldManagedSound(this.world, this.requireManagedSoundId(id));
   }

   private String requireManagedSoundId(String id) {
      if (id == null || id.trim().isEmpty()) {
         throw new IllegalArgumentException("Special sound ID can't be empty");
      }

      return id.trim();
   }

   private String requireManagedSoundName(String event) {
      if (event == null || event.trim().isEmpty()) {
         throw new IllegalArgumentException("Sound event name can't be empty");
      }

      return event.trim();
   }

   public void stopSound(String event, String category) {
      class_2960 id = event != null && !event.isEmpty() ? class_2960.method_12829(event) : null;
      class_3419 soundCategory = category != null && !category.isEmpty() ? class_3419.valueOf(category.toUpperCase(Locale.ROOT)) : null;

      for(class_3222 player : this.world.method_8503().method_3760().method_14571()) {
         player.field_13987.method_14364(new class_2770(id, soundCategory));
      }

   }

   public IScriptEntityItem dropItemStack(IScriptItemStack stack, double x, double y, double z, double mx, double my, double mz) {
      if (stack != null && !stack.isEmpty()) {
         class_1542 item = new class_1542(this.world, x, y, z, stack.getMinecraftItemStack().method_7972());
         item.method_18800(mx, my, mz);
         this.world.method_8649(item);
         return (IScriptEntityItem)ScriptEntity.create(item);
      } else {
         return null;
      }
   }

   public void explode(IScriptEntity exploder, double x, double y, double z, float distance, boolean blazeGround, boolean destroyTerrain) {
      this.world.method_8537(exploder == null ? null : exploder.getMinecraftEntity(), x, y, z, distance, blazeGround, destroyTerrain ? class_7867.field_40891 : class_7867.field_40888);
   }

   public IScriptRayTrace rayTrace(double x1, double y1, double z1, double x2, double y2, double z2) {
      return new ScriptRayTrace(this.world, new ScriptVector(x1, y1, z1), RayTracing.rayTraceWithEntity(this.world, x1, y1, z1, x2, y2, z2));
   }

   public IScriptRayTrace rayTraceBlock(double x1, double y1, double z1, double x2, double y2, double z2) {
      return new ScriptRayTrace(this.world, new ScriptVector(x1, y1, z1), RayTracing.rayTrace(this.world, x1, y1, z1, x2, y2, z2));
   }

   public void displayLight(String id, int duration, double x, double y, double z, int level) {
      String lightId = this.normalizeLightId(id);
      if (lightId == null) {
         return;
      }
      if (this.world instanceof class_3218) {
         VanillaWorldLightManager.displayPoint((class_3218)this.world, lightId, duration, x, y, z, level);
      } else {
         VanillaWorldLightManager.displayPointClient(this.world, lightId, duration, x, y, z, level);
      }
   }

   public void displayRayTraceLight(String id, int duration, double x, double y, double z, IScriptRayTrace rayTrace, int level, float spacing) {
      String lightId = this.normalizeLightId(id);
      ScriptVector hit = rayTrace == null ? null : rayTrace.getHitPosition();
      if (lightId == null || hit == null) {
         return;
      }
      if (this.world instanceof class_3218) {
         VanillaWorldLightManager.displayRay((class_3218)this.world, lightId, duration, x, y, z, hit.x, hit.y, hit.z, level, spacing);
      } else {
         VanillaWorldLightManager.displayRayClient(this.world, lightId, duration, x, y, z, hit.x, hit.y, hit.z, level, spacing);
      }
   }

   public IScriptLight displayFlashlight(String id, int duration, double x, double y, double z, IScriptRayTrace rayTrace, int startLevel, int endLevel, float spacing) {
      String lightId = this.normalizeLightId(id);
      ScriptVector hit = rayTrace == null ? null : rayTrace.getHitPosition();
      return lightId != null && hit != null ? ScriptFlashlight.create(this.world, lightId, duration, x, y, z, hit.x, hit.y, hit.z, startLevel, endLevel, spacing) : ScriptFlashlight.empty();
   }

   public void removeLight(String id) {
      String lightId = this.normalizeLightId(id);
      if (lightId == null) {
         return;
      }
      if (this.world instanceof class_3218) {
         VanillaWorldLightManager.remove((class_3218)this.world, lightId);
      } else {
         VanillaWorldLightManager.removeClient(this.world, lightId);
      }
   }

   private String normalizeLightId(String id) {
      if (id == null) {
         return null;
      }

      String value = id.trim();
      return value.isEmpty() || value.length() > 128 ? null : value;
   }

   public boolean isActive(int x, int y, int z) {
      return this.world.method_49808(new class_2338(x, y, z), class_2350.field_11036) > 0;
   }

   public boolean testForBlock(int x, int y, int z, String blockId, int meta) {
      class_2248 value = (class_2248)class_7923.field_41175.method_10223(new class_2960(blockId));
      List<class_2680> validStates = value.method_9595().method_11662();
      class_2680 state = meta >= 0 && meta < validStates.size() ? (class_2680)validStates.get(meta) : value.method_9564();
      return this.getBlock(x, y, z).isSame(ScriptBlockState.create(state));
   }

   public void fill(IScriptBlockState state, int x1, int y1, int z1, int x2, int y2, int z2) {
      int xMin = Math.min(x1, x2);
      int xMax = Math.max(x1, x2);
      int yMin = Math.min(y1, y2);
      int yMax = Math.max(y1, y2);
      int zMin = Math.min(z1, z2);
      int zMax = Math.max(z1, z2);

      for(int x = xMin; x <= xMax; ++x) {
         for(int y = yMin; y <= yMax; ++y) {
            for(int z = zMin; z <= zMax; ++z) {
               this.setBlock(state, x, y, z);
            }
         }
      }

   }

   public IScriptEntity summonFallingBlock(double x, double y, double z, String blockId, int meta) {
      class_2487 nbt = new class_2487();
      nbt.method_10582("Block", blockId);
      nbt.method_10569("Data", meta);
      nbt.method_10569("Time", 1);
      return this.spawnEntity("minecraft:falling_block", x, y, z, new ScriptNBTCompound(nbt));
   }

   public IScriptEntity setFallingBlock(int x, int y, int z) {
      IScriptBlockState state = this.getBlock(x, y, z);
      if (state.isAir()) {
         return null;
      } else {
         class_2487 nbt = new class_2487();
         nbt.method_10582("Block", state.getBlockId());
         nbt.method_10569("Data", state.getMeta());
         nbt.method_10569("Time", 1);
         class_2248 block = (class_2248)class_7923.field_41175.method_10223(new class_2960(state.getBlockId()));
         if (block != null) {
            List<class_2680> states = block.method_9595().method_11662();
            class_2680 blockState = state.getMeta() >= 0 && state.getMeta() < states.size() ? (class_2680)states.get(state.getMeta()) : block.method_9564();

            for(class_2769<?> property : blockState.method_28501()) {
               nbt.method_10582(property.method_11899(), blockState.method_11654(property).toString());
            }
         }

         if (this.hasBlockEntity(x, y, z)) {
            nbt.method_10566("BlockEntityData", this.getBlockEntity(x, y, z).getData().getNbtCompound());
         }

         this.world.method_8544(this.pos.method_10103(x, y, z));
         this.world.method_8652(this.pos.method_10103(x, y, z), class_2246.field_10124.method_9564(), 3);
         return this.spawnEntity("minecraft:falling_block", (double)x + (double)0.5F, (double)y + (double)0.5F, (double)z + (double)0.5F, new ScriptNBTCompound(nbt));
      }
   }

   public void setBlockEntity(int x, int y, int z, IScriptBlockState blockState, INBTCompound tileData) {
      if (blockState == null || tileData == null) {
         return;
      }
      this.setBlock(blockState, x, y, z);
      IScriptTileEntity tile = this.getBlockEntity(x, y, z);
      if (tile == null) {
         return;
      }
      tileData.setInt("x", x);
      tileData.setInt("y", y);
      tileData.setInt("z", z);
      tile.setData(tileData);
   }

   public void fillTileEntities(int x1, int y1, int z1, int x2, int y2, int z2, IScriptBlockState blockState, INBTCompound tileData) {
      int xMin = Math.min(x1, x2);
      int xMax = Math.max(x1, x2);
      int yMin = Math.min(y1, y2);
      int yMax = Math.max(y1, y2);
      int zMin = Math.min(z1, z2);
      int zMax = Math.max(z1, z2);

      for(int x = xMin; x <= xMax; ++x) {
         for(int y = yMin; y <= yMax; ++y) {
            for(int z = zMin; z <= zMax; ++z) {
               this.setBlockEntity(x, y, z, blockState, tileData);
            }
         }
      }

   }

   public void clone(int x, int y, int z, int xNew, int yNew, int zNew) {
      IScriptBlockState state = this.getBlock(x, y, z);
      if (!state.getBlockId().equals("minecraft:air")) {
         this.setBlock(state, xNew, yNew, zNew);
         if (this.getBlockEntity(x, y, z) != null) {
            INBTCompound tile = this.getBlockEntity(x, y, z).getData();
            tile.setInt("x", xNew);
            tile.setInt("y", yNew);
            tile.setInt("z", zNew);
            this.setBlock(state, x, y, z);
            this.getBlockEntity(xNew, yNew, zNew).setData(tile);
         }
      }

   }

   public void clone(int x1, int y1, int z1, int x2, int y2, int z2, int xNew, int yNew, int zNew) {
      int xMin = Math.min(x1, x2);
      int xMax = Math.max(x1, x2);
      int yMin = Math.min(y1, y2);
      int yMax = Math.max(y1, y2);
      int zMin = Math.min(z1, z2);
      int zMax = Math.max(z1, z2);
      int xCentre = (xMin + xMax) / 2;
      int yCentre = (yMin + yMax) / 2;
      int zCentre = (zMin + zMax) / 2;

      for(int x = xMin; x <= xMax; ++x) {
         for(int y = yMin; y <= yMax; ++y) {
            for(int z = zMin; z <= zMax; ++z) {
               this.clone(x, y, z, xNew + x - xCentre, yNew + y - yCentre, zNew + z - zCentre);
            }
         }
      }

   }

   public IScriptItemStack getBlockStackWithTile(int x, int y, int z) {
      if (!this.world.method_22340(this.pos.method_10103(x, y, z))) {
         return ScriptItemStack.EMPTY;
      } else {
         class_2680 blockState = this.world.method_8320(this.pos);
         class_2248 block = blockState.method_26204();
         if (block == class_2246.field_10124) {
            return ScriptItemStack.EMPTY;
         } else {
            class_1792 itemFromBlock = block.method_8389();
            class_1799 itemStack;
            if (itemFromBlock == class_1802.field_8162) {
               itemStack = block.method_9574(this.world, this.pos, blockState);
               if (itemStack.method_7960()) {
                  return ScriptItemStack.EMPTY;
               }
            } else {
               itemStack = new class_1799(itemFromBlock);
            }

            class_2586 tileEntity = this.world.method_8321(this.pos);
            if (tileEntity != null) {
               class_2487 tileEntityNBT = tileEntity.method_38243();
               class_2487 itemStackNBT = new class_2487();
               itemStackNBT.method_10566("BlockEntityTag", tileEntityNBT);
               itemStack.method_7980(itemStackNBT);
            }

            return ScriptItemStack.create(itemStack);
         }
      }
   }

   public void displayMorph(AbstractMorph morph, int expiration, double x, double y, double z, float yaw, float pitch, int range, IScriptPlayer player) {
      if (morph != null) {
         WorldMorph worldMorph = new WorldMorph();
         worldMorph.morph = morph;
         worldMorph.expiration = expiration;
         worldMorph.x = x;
         worldMorph.y = y;
         worldMorph.z = z;
         worldMorph.yaw = yaw;
         worldMorph.pitch = pitch;
         if (player == null) {
            double distance = (double)MathUtils.clamp(range, 1, 256);

            for(class_3222 target : this.world.method_8503().method_3760().method_14571()) {
               if (target.method_37908() == this.world && target.method_5649(x, y, z) <= distance * distance) {
                  Dispatcher.sendTo(new PacketWorldMorph(worldMorph), target);
               }
            }
         } else {
            Dispatcher.sendTo(new PacketWorldMorph(worldMorph), player.getMinecraftPlayer());
         }

      }
   }

   public MappetSchematic createSchematic() {
      return MappetSchematic.create(this);
   }

   public IScriptEntity shootBBGunProjectile(IScriptEntity shooter, double x, double y, double z, double yaw, double pitch, String gunPropsNbtString) {
      if (shooter.getMinecraftEntity() instanceof class_1309 && FabricLoader.getInstance().isModLoaded("blockbuster")) {
         try {
            return this.shootBBGunProjectileMethod(shooter, x, y, z, yaw, pitch, gunPropsNbtString);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return null;
   }

   private IScriptEntity shootBBGunProjectileMethod(IScriptEntity shooter, double x, double y, double z, double yaw, double pitch, String gunPropsNbtString) throws CommandSyntaxException {
      ScriptFactory factory = new ScriptFactory();
      class_1309 entityLivingBase = (class_1309)shooter.getMinecraftEntity();
      GunProps gunProps = new GunProps(factory.createCompound(gunPropsNbtString).getCompound("Gun").getCompound("Projectile").getNbtCompound());
      gunProps.fromNBT(factory.createCompound(gunPropsNbtString).getCompound("Gun").getNbtCompound());
      EntityGunProjectile projectile = new EntityGunProjectile(entityLivingBase.method_37908(), gunProps, MorphManager.INSTANCE.morphFromNBT(gunProps.projectileMorph));
      projectile.method_5814(x, y, z);
      projectile.method_36457((float)pitch);
      projectile.method_36456((float)yaw);
      projectile.shootFrom(entityLivingBase);
      projectile.setInitialMotion();
      entityLivingBase.method_37908().method_8649(projectile);
      IScriptEntity spawnedEntity = ScriptEntity.create(projectile);
      return spawnedEntity;
   }

   @FunctionalInterface
   private interface BlockPosConsumer {
      void accept(int var1, int var2, int var3);
   }
}
