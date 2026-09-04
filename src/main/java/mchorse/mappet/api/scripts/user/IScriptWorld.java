package mchorse.mappet.api.scripts.user;

import java.util.List;
import javax.vecmath.Vector3d;
import mchorse.mappet.api.scripts.code.mappet.MappetSchematic;
import mchorse.mappet.api.scripts.user.blocks.IScriptBlockState;
import mchorse.mappet.api.scripts.user.blocks.IScriptTileEntity;
import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import mchorse.mappet.api.scripts.user.entities.IScriptEntityItem;
import mchorse.mappet.api.scripts.user.entities.IScriptNpc;
import mchorse.mappet.api.scripts.user.entities.IScriptPlayer;
import mchorse.mappet.api.scripts.user.items.IScriptInventory;
import mchorse.mappet.api.scripts.user.items.IScriptItemStack;
import mchorse.mappet.api.scripts.user.lights.IScriptLight;
import mchorse.mappet.api.scripts.user.nbt.INBTCompound;
import mchorse.mappet.api.scripts.user.sounds.IScriptManagedSound;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.class_1937;
import net.minecraft.class_2396;

public interface IScriptWorld {
   class_1937 getMinecraftWorld();

   void setGameRule(String var1, Object var2);

   Object getGameRule(String var1);

   void setBlock(IScriptBlockState var1, int var2, int var3, int var4);

   void removeBlock(int var1, int var2, int var3);

   IScriptBlockState getBlock(int var1, int var2, int var3);

   IScriptBlockState getBlock(ScriptVector var1);

   boolean hasBlockEntity(int var1, int var2, int var3);
   @Deprecated
   default boolean hasTileEntity(int x, int y, int z) {
      return this.hasBlockEntity(x, y, z);
   }

   void replaceBlocks(IScriptBlockState var1, IScriptBlockState var2, Vector3d var3, Vector3d var4);

   void replaceBlocks(IScriptBlockState var1, IScriptBlockState var2, INBTCompound var3, Vector3d var4, Vector3d var5);

   IScriptTileEntity getBlockEntity(int var1, int var2, int var3);
   @Deprecated
   default IScriptTileEntity getTileEntity(int x, int y, int z) {
      return this.getBlockEntity(x, y, z);
   }

   boolean hasInventory(int var1, int var2, int var3);

   IScriptInventory getInventory(int var1, int var2, int var3);

   boolean isRaining();

   void setRaining(boolean var1);

   long getTime();

   void setTime(long var1);

   long getTotalTime();

   int getDimensionId();

   void spawnParticles(class_2396 var1, boolean var2, double var3, double var5, double var7, int var9, double var10, double var12, double var14, double var16, int... var18);

   void spawnParticles(IScriptPlayer var1, class_2396 var2, boolean var3, double var4, double var6, double var8, int var10, double var11, double var13, double var15, double var17, int... var19);

   default IScriptEntity spawnEntity(String id, double x, double y, double z) {
      return this.spawnEntity(id, x, y, z, (INBTCompound)null);
   }

   IScriptEntity spawnEntity(String var1, double var2, double var4, double var6, INBTCompound var8);

   default IScriptNpc spawnNpc(String id, double x, double y, double z) {
      return this.spawnNpc(id, "default", x, y, z);
   }

   IScriptNpc spawnNpc(String var1, String var2, double var3, double var5, double var7);

   IScriptNpc spawnNpc(String var1, String var2, double var3, double var5, double var7, float var9, float var10, float var11);

   List<IScriptEntity> getEntities(double var1, double var3, double var5, double var7, double var9, double var11);

   List<IScriptEntity> getEntities(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13);

   List<IScriptEntity> getEntities(double var1, double var3, double var5, double var7);

   default void playSound(String event, double x, double y, double z) {
      this.playSound(event, x, y, z, 1.0F, 1.0F);
   }

   void playSound(String var1, double var2, double var4, double var6, float var8, float var9);

   default IScriptManagedSound playLoopSound(String id, String event, double x, double y, double z) {
      return this.playLoopSound(id, event, "master", x, y, z, 1.0F, 1.0F);
   }

   default IScriptManagedSound playLoopSound(String id, String event, double x, double y, double z, float volume, float pitch) {
      return this.playLoopSound(id, event, "master", x, y, z, volume, pitch);
   }

   IScriptManagedSound playLoopSound(String id, String event, String category, double x, double y, double z, float volume, float pitch);

   
   IScriptManagedSound playManagedStaticSound(String id, String event, String category, float volume, float pitch);

   
   IScriptManagedSound getManagedSound(String id);

   default void stopAllSounds() {
      this.stopSound("", "");
   }

   default void stopSound(String event) {
      this.stopSound(event, "");
   }

   void stopSound(String var1, String var2);

   default IScriptEntityItem dropItemStack(IScriptItemStack stack, double x, double y, double z) {
      return this.dropItemStack(stack, x, y, z, (double)0.0F, (double)0.0F, (double)0.0F);
   }

   IScriptEntityItem dropItemStack(IScriptItemStack var1, double var2, double var4, double var6, double var8, double var10, double var12);

   default void explode(double x, double y, double z, float distance) {
      this.explode((IScriptEntity)null, x, y, z, distance, false, true);
   }

   default void explode(double x, double y, double z, float distance, boolean blazeGround, boolean destroyTerrain) {
      this.explode((IScriptEntity)null, x, y, z, distance, blazeGround, destroyTerrain);
   }

   void explode(IScriptEntity var1, double var2, double var4, double var6, float var8, boolean var9, boolean var10);

   IScriptRayTrace rayTrace(double var1, double var3, double var5, double var7, double var9, double var11);

   IScriptRayTrace rayTraceBlock(double var1, double var3, double var5, double var7, double var9, double var11);

   
   default void displayLight(String id, int duration, double x, double y, double z) {
      this.displayLight(id, duration, x, y, z, 15);
   }

   
   void displayLight(String id, int duration, double x, double y, double z, int level);

   
   default void displayRayTraceLight(String id, int duration, double x, double y, double z, IScriptRayTrace rayTrace) {
      this.displayRayTraceLight(id, duration, x, y, z, rayTrace, 15, 0.5F);
   }

   
   void displayRayTraceLight(String id, int duration, double x, double y, double z, IScriptRayTrace rayTrace, int level, float spacing);

   
   default IScriptLight displayFlashlight(String id, int duration, double x, double y, double z, IScriptRayTrace rayTrace) {
      return this.displayFlashlight(id, duration, x, y, z, rayTrace, 1, 15, 0.5F);
   }

   
   IScriptLight displayFlashlight(String id, int duration, double x, double y, double z, IScriptRayTrace rayTrace, int startLevel, int endLevel, float spacing);

   
   void removeLight(String id);

   boolean isActive(int var1, int var2, int var3);

   boolean testForBlock(int var1, int var2, int var3, String var4, int var5);

   void fill(IScriptBlockState var1, int var2, int var3, int var4, int var5, int var6, int var7);

   IScriptEntity summonFallingBlock(double var1, double var3, double var5, String var7, int var8);

   IScriptEntity setFallingBlock(int var1, int var2, int var3);

   void setBlockEntity(int var1, int var2, int var3, IScriptBlockState var4, INBTCompound var5);
   @Deprecated
   default void setTileEntity(int x, int y, int z, IScriptBlockState blockState, INBTCompound tileData) {
      this.setBlockEntity(x, y, z, blockState, tileData);
   }

   void fillTileEntities(int var1, int var2, int var3, int var4, int var5, int var6, IScriptBlockState var7, INBTCompound var8);

   void clone(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9);

   void clone(int var1, int var2, int var3, int var4, int var5, int var6);

   IScriptItemStack getBlockStackWithTile(int var1, int var2, int var3);

   MappetSchematic createSchematic();

   default void displayMorph(AbstractMorph morph, int expiration, double x, double y, double z) {
      this.displayMorph(morph, expiration, x, y, z, 0.0F, 0.0F);
   }

   default void displayMorph(AbstractMorph morph, int expiration, double x, double y, double z, int range) {
      this.displayMorph(morph, expiration, x, y, z, 0.0F, 0.0F, range);
   }

   default void displayMorph(AbstractMorph morph, int expiration, double x, double y, double z, float yaw, float pitch) {
      this.displayMorph(morph, expiration, x, y, z, yaw, pitch, 64);
   }

   default void displayMorph(AbstractMorph morph, int expiration, double x, double y, double z, float yaw, float pitch, int range) {
      this.displayMorph(morph, expiration, x, y, z, yaw, pitch, range, (IScriptPlayer)null);
   }

   void displayMorph(AbstractMorph var1, int var2, double var3, double var5, double var7, float var9, float var10, int var11, IScriptPlayer var12);

   IScriptEntity shootBBGunProjectile(IScriptEntity var1, double var2, double var4, double var6, double var8, double var10, String var12);
}
