package mchorse.mappet.api.scripts.user.entities;

import java.util.List;
import mchorse.mappet.api.scripts.code.entities.ScriptEntityItem;
import mchorse.mappet.api.scripts.code.items.ScriptItemStack;
import mchorse.mappet.api.scripts.user.IScriptRayTrace;
import mchorse.mappet.api.scripts.user.IScriptWorld;
import mchorse.mappet.api.scripts.user.client.ISimpleVoiceChat;
import mchorse.mappet.api.scripts.user.data.ScriptBox;
import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.api.scripts.user.items.IScriptItemStack;
import mchorse.mappet.api.scripts.user.mappet.IMappetStates;
import mchorse.mappet.api.scripts.user.nbt.INBTCompound;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.class_1291;
import net.minecraft.class_1297;

public interface IScriptEntity {
   class_1297 getMinecraftEntity();

   
   ISimpleVoiceChat getSVC();

   IScriptWorld getWorld();

   ScriptVector getPosition();

   void setPosition(double var1, double var3, double var5);

   int getDimension();

   void setDimension(int var1);

   ScriptVector getMotion();

   void setMotion(double var1, double var3, double var5);

   void addMotion(double var1, double var3, double var5);

   ScriptVector getRotations();

   void setRotations(float var1, float var2, float var3);

   




   void rotateTo(String interpolation, int durationTicks, float pitch, float yaw, float yawHead, boolean disableAI);

   
   default void rotateBy(String interpolation, int durationTicks, float pitch, float yaw, float yawHead, boolean disableAI) {
      this.rotateTo(interpolation, durationTicks, this.getPitch() + pitch, this.getYaw() + yaw, this.getYawHead() + yawHead, disableAI);
   }

   float getPitch();

   float getYaw();

   float getYawHead();

   ScriptVector getLook();

   float getEyeHeight();

   float getWidth();

   float getHeight();

   float getHp();

   void setHp(float var1);

   float getMaxHp();

   void setMaxHp(float var1);

   boolean isInWater();

   boolean isInLava();

   boolean isBurning();

   void setBurning(int var1);

   boolean isSneaking();

   boolean isSprinting();

   boolean isOnGround();

   IScriptRayTrace rayTrace(double var1);

   IScriptRayTrace rayTraceBlock(double var1);

   IScriptItemStack getMainItem();

   void setMainItem(IScriptItemStack var1);

   IScriptItemStack getOffItem();

   void setOffItem(IScriptItemStack var1);

   void giveItem(IScriptItemStack var1);

   void giveItem(IScriptItemStack var1, boolean var2, boolean var3);

   IScriptItemStack getHelmet();

   IScriptItemStack getChestplate();

   IScriptItemStack getLeggings();

   IScriptItemStack getBoots();

   void setHelmet(IScriptItemStack var1);

   void setChestplate(IScriptItemStack var1);

   void setLeggings(IScriptItemStack var1);

   void setBoots(IScriptItemStack var1);

   default void setArmor(IScriptItemStack helmet, IScriptItemStack chestplate, IScriptItemStack leggings, IScriptItemStack boots) {
      this.setHelmet(helmet);
      this.setChestplate(chestplate);
      this.setLeggings(leggings);
      this.setBoots(boots);
   }

   default void clearArmor() {
      this.setArmor(ScriptItemStack.EMPTY, ScriptItemStack.EMPTY, ScriptItemStack.EMPTY, ScriptItemStack.EMPTY);
   }

   void setSpeed(float var1);

   IScriptEntity getTarget();

   void setTarget(IScriptEntity var1);

   boolean isAIEnabled();

   void setAIEnabled(boolean var1);

   String getUniqueId();

   String getEntityId();

   int getTicks();

   int getCombinedLight();

   String getName();

   void setName(String var1);

   void setInvisible(boolean var1);

   INBTCompound getFullData();

   void setFullData(INBTCompound var1);

   INBTCompound getEntityData();

   boolean isPlayer();

   boolean isNPC();

   boolean isItem();

   boolean isLivingBase();

   boolean isSame(IScriptEntity var1);

   boolean isEntityInRadius(IScriptEntity var1, double var2);

   default boolean isInBlock(int x, int y, int z) {
      return this.isInArea((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1));
   }

   boolean isInArea(double var1, double var3, double var5, double var7, double var9, double var11);

   void damage(float var1);

   void damageAs(IScriptEntity var1, float var2);

   void damageWithItemsAs(IScriptPlayer var1);

   void mount(IScriptEntity var1);

   void dismount();

   IScriptEntity getMount();

   ScriptBox getBoundingBox();

   
   void setSolidHitbox(boolean solid);

   ScriptEntityItem dropItem(int var1);

   ScriptEntityItem dropItem();

   ScriptEntityItem dropItem(IScriptItemStack var1);

   float getFallDistance();

   void setFallDistance(float var1);

   void remove();

   void kill();

   default void swingArm() {
      this.swingArm(0);
   }

   void swingArm(int var1);

   List<IScriptEntity> getLeashedEntities();

   boolean setLeashHolder(IScriptEntity var1);

   IScriptEntity getLeashHolder();

   boolean clearLeashHolder(boolean var1);

   void setModifier(String var1, double var2);

   double getModifier(String var1);

   void removeModifier(String var1);

   void removeAllModifiers();

   void applyPotion(class_1291 var1, int var2, int var3, boolean var4);

   boolean hasPotion(class_1291 var1);

   boolean removePotion(class_1291 var1);

   void clearPotions();

   IMappetStates getStates();

   AbstractMorph getMorph();

   boolean setMorph(AbstractMorph var1);

   



   boolean setMorph(String morphNbt);

   default void displayMorph(AbstractMorph morph, int expiration, double x, double y, double z) {
      this.displayMorph(morph, expiration, x, y, z, true);
   }

   default void displayMorph(AbstractMorph morph, int expiration, double x, double y, double z, boolean rotate) {
      this.displayMorph(morph, expiration, x, y, z, 0.0F, 0.0F, rotate);
   }

   default void displayMorph(AbstractMorph morph, int expiration, double x, double y, double z, float yaw, float pitch, boolean rotate) {
      this.displayMorph(morph, expiration, x, y, z, yaw, pitch, rotate, (IScriptPlayer)null);
   }

   void displayMorph(AbstractMorph var1, int var2, double var3, double var5, double var7, float var9, float var10, boolean var11, IScriptPlayer var12);

   IScriptEntity shootBBGunProjectile(String var1);

   void executeCommand(String var1);

   void executeScript(String var1);

   void executeScript(String var1, String var2);

   void executeScript(String var1, String var2, Object... var3);

   void lockPosition(double var1, double var3, double var5);

   void unlockPosition();

   boolean isPositionLocked();

   void lockRotation(float var1, float var2, float var3);

   void unlockRotation();

   boolean isRotationLocked();

   void moveTo(String var1, int var2, double var3, double var5, double var7, boolean var9);

   void observe(IScriptEntity var1);

   void addEntityPatrol(double var1, double var3, double var5, double var7, boolean var9, String var10);

   void clearEntityPatrols();

   void setRotationsAI(float var1, float var2, float var3);

   void clearRotationsAI();

   void executeRepeatingCommand(String var1, int var2);

   void removeRepeatingCommand(String var1);

   void clearAllRepeatingCommands();
}
