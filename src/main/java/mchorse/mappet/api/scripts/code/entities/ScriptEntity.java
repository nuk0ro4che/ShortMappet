package mchorse.mappet.api.scripts.code.entities;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.script.ScriptException;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.entity.EntityGunProjectile;
import mchorse.blockbuster.network.common.PacketModifyActor;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.scripts.code.ScriptRayTrace;
import mchorse.mappet.api.scripts.code.ScriptWorld;
import mchorse.mappet.api.scripts.code.client.ScriptSimpleVoiceChat;
import mchorse.mappet.api.scripts.code.entities.ai.EntitiesAIPatrol;
import mchorse.mappet.api.scripts.code.entities.ai.EntityAILookAtTarget;
import mchorse.mappet.api.scripts.code.entities.ai.repeatingCommand.EntityAIRepeatingCommand;
import mchorse.mappet.api.scripts.code.entities.ai.repeatingCommand.RepeatingCommandDataStorage;
import mchorse.mappet.api.scripts.code.entities.ai.rotations.EntityAIRotations;
import mchorse.mappet.api.scripts.code.entities.ai.rotations.RotationDataStorage;
import mchorse.mappet.api.scripts.code.items.ScriptItemStack;
import mchorse.mappet.api.scripts.code.mappet.MappetStates;
import mchorse.mappet.api.scripts.code.nbt.ScriptNBTCompound;
import mchorse.mappet.api.scripts.user.IScriptRayTrace;
import mchorse.mappet.api.scripts.user.client.ISimpleVoiceChat;
import mchorse.mappet.api.scripts.user.IScriptWorld;
import mchorse.mappet.api.scripts.user.data.ScriptBox;
import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import mchorse.mappet.api.scripts.user.entities.IScriptPlayer;
import mchorse.mappet.api.scripts.user.items.IScriptItemStack;
import mchorse.mappet.api.scripts.user.mappet.IMappetStates;
import mchorse.mappet.api.scripts.user.nbt.INBTCompound;
import mchorse.mappet.api.states.States;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.client.morphs.WorldMorph;
import mchorse.mappet.compat.EntityData;
import mchorse.mappet.compat.EntityGoals;
import mchorse.mappet.entities.EntityNpc;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketEntityRotations;
import mchorse.mappet.network.common.scripts.PacketEntityTransition;
import mchorse.mappet.network.common.scripts.PacketWorldMorph;
import mchorse.mappet.utils.EntityUtils;
import mchorse.mappet.utils.RunnableExecutionFork;
import mchorse.mclib.utils.Interpolation;
import mchorse.mclib.utils.RayTracing;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1268;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1320;
import net.minecraft.class_1322;
import net.minecraft.class_1324;
import net.minecraft.class_1361;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_1944;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2487;
import net.minecraft.class_2522;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_3532;
import net.minecraft.class_4135;
import net.minecraft.class_5134;
import net.minecraft.class_5321;
import net.minecraft.class_7923;
import net.minecraft.class_1322.class_1323;
import net.minecraft.server.MinecraftServer;

public class ScriptEntity<T extends class_1297> implements IScriptEntity {
   protected T entity;
   protected IMappetStates states;

   public static IScriptEntity create(class_1297 entity) {
      if (entity instanceof class_1657) {
         return new ScriptPlayer((class_1657)entity);
      } else if (entity instanceof EntityNpc) {
         return new ScriptNpc((EntityNpc)entity);
      } else if (entity instanceof class_1542) {
         return new ScriptEntityItem((class_1542)entity);
      } else {
         return entity != null ? new ScriptEntity(entity) : null;
      }
   }

   protected ScriptEntity(T entity) {
      this.entity = entity;
   }

   public class_1297 getMinecraftEntity() {
      return this.entity;
   }

   public ISimpleVoiceChat getSVC() {
      return new ScriptSimpleVoiceChat(this.entity);
   }

   public IScriptWorld getWorld() {
      return new ScriptWorld(this.entity.method_37908());
   }

   public ScriptVector getPosition() {
      return new ScriptVector(this.entity.method_23317(), this.entity.method_23318(), this.entity.method_23321());
   }

   public void setPosition(double x, double y, double z) {
      if (!Double.isNaN(x) && !Double.isNaN(y) && !Double.isNaN(z)) {
         this.entity.method_20620(x, y, z);
         if (this.entity instanceof class_3222) {
            ((class_3222)this.entity).field_13987.method_14363(x, y, z, this.entity.method_36454(), this.entity.method_36455());
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getDimension() {
      class_5321<class_1937> key = this.entity.method_37908().method_27983();
      return key == class_1937.field_25180 ? -1 : (key == class_1937.field_25181 ? 1 : 0);
   }

   public void setDimension(int dimension) {
      if (this.getDimension() != dimension) {
         if (dimension >= -1 && dimension <= 1) {
            MinecraftServer minecraftServer = this.entity.method_5682();
            class_5321<class_1937> key = dimension == -1 ? class_1937.field_25180 : (dimension == 1 ? class_1937.field_25181 : class_1937.field_25179);
            class_3218 worldServer = minecraftServer.method_3847(key);
            if (worldServer != null) {
               if (this.entity instanceof class_3222) {
                  class_3222 player = (class_3222)this.entity;
                  player.method_14251(worldServer, player.method_23317(), player.method_23318(), player.method_23321(), player.method_36454(), player.method_36455());
               } else {
                  this.entity.method_5731(worldServer);
               }

            }
         } else {
            throw new IllegalArgumentException("Dimension must be -1 (Nether), 0 (Overworld), or 1 (End).");
         }
      }
   }

   public ScriptVector getMotion() {
      return new ScriptVector(this.entity.method_18798().field_1352, this.entity.method_18798().field_1351, this.entity.method_18798().field_1350);
   }

   public void setMotion(double x, double y, double z) {
      this.entity.method_18800(x, y, z);
   }

   public void addMotion(double x, double y, double z) {
      this.entity.field_6037 = true;
      this.entity.method_5762(x, y, z);
   }

   public ScriptVector getRotations() {
      return new ScriptVector((double)this.getPitch(), (double)this.getYaw(), (double)this.getYawHead());
   }

   public void setRotations(float pitch, float yaw, float yawHead) {
      if (!Float.isNaN(pitch) && !Float.isNaN(yaw) && !Float.isNaN(yawHead)) {
         this.entity.method_5808(this.entity.method_23317(), this.entity.method_23318(), this.entity.method_23321(), yaw, pitch);
         this.entity.method_5847(yawHead);
         class_1297 var5 = this.entity;
         if (var5 instanceof class_1309) {
            class_1309 living = (class_1309)var5;
            living.field_6283 = yawHead;
         }

         if (!this.isPlayer()) {
            for(class_3222 player : this.entity.method_5682().method_3760().method_14571()) {
               if (player.method_37908() == this.entity.method_37908() && player.method_5858(this.entity) <= (double)16384.0F) {
                  Dispatcher.sendTo(new PacketEntityRotations(this.entity.method_5628(), yaw, yawHead, pitch), player);
               }
            }
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   
   public void rotateTo(String interpolation, int durationTicks, float pitch, float yaw, float yawHead, boolean disableAI) {
      

      if (disableAI && this.entity instanceof class_1308) {
         class_1308 mob = (class_1308)this.entity;
         boolean wasAIEnabled = !mob.method_5987();
         this.setAIEnabled(false);
         this.rotateTo(interpolation, durationTicks, pitch, yaw, yawHead);
         if (wasAIEnabled) {
            CommonProxy.eventHandler.addExecutable(new RunnableExecutionFork(Math.max(0, durationTicks), () -> this.setAIEnabled(true)));
         }
      } else {
         this.rotateTo(interpolation, durationTicks, pitch, yaw, yawHead);
      }
   }

   private void rotateTo(String interpolation, int durationTicks, float pitch, float yaw, float yawHead) {
      int duration = Math.max(0, durationTicks);
      if (duration == 0) {
         this.setRotations(pitch, yaw, yawHead);
         return;
      }

      Interpolation interp = Interpolation.valueOf(interpolation.toUpperCase(java.util.Locale.ROOT));
      float startPitch = this.getPitch();
      float startYaw = this.getYaw();
      float startYawHead = this.getYawHead();
      float targetYaw = this.closestRotation(startYaw, yaw);
      float targetYawHead = this.closestRotation(startYawHead, yawHead);
      this.sendEntityTransition(false, true, interpolation, duration, this.entity.method_23317(), this.entity.method_23318(), this.entity.method_23321(), pitch, yaw, yawHead);

      

      for (int step = 1; step <= duration; ++step) {
         double progress = (double)step / (double)duration;
         float nextPitch = (float)interp.interpolate((double)startPitch, (double)pitch, progress);
         float nextYaw = (float)interp.interpolate((double)startYaw, (double)targetYaw, progress);
         float nextYawHead = (float)interp.interpolate((double)startYawHead, (double)targetYawHead, progress);
         CommonProxy.eventHandler.addExecutable(new RunnableExecutionFork(step - 1, () -> this.setInterpolatedRotations(nextPitch, nextYaw, nextYawHead)));
      }
   }

   private float closestRotation(float from, float target) {
      float delta = (target - from) % 360.0F;
      if (delta >= 180.0F) {
         delta -= 360.0F;
      } else if (delta < -180.0F) {
         delta += 360.0F;
      }

      return from + delta;
   }

   public float getPitch() {
      return this.entity.method_36455();
   }

   public float getYaw() {
      return this.entity.method_36454();
   }

   public float getYawHead() {
      return this.entity.method_5791();
   }

   public ScriptVector getLook() {
      float f1 = -(this.entity.method_36455() * ((float)Math.PI / 180F));
      float f2 = this.entity.method_5791() * ((float)Math.PI / 180F);
      float f3 = -class_3532.method_15374(f2);
      float f4 = class_3532.method_15362(f2);
      float f6 = class_3532.method_15362(f1);
      return new ScriptVector((double)(f3 * f6), this.entity.method_5828(1.0F).field_1351, (double)(f4 * f6));
   }

   public float getEyeHeight() {
      return EntityUtils.getEyeHeight(this.entity);
   }

   public float getWidth() {
      return this.entity.method_17681();
   }

   public float getHeight() {
      return this.entity.method_17682();
   }

   public float getHp() {
      return this.isLivingBase() ? ((class_1309)this.entity).method_6032() : 0.0F;
   }

   public void setHp(float hp) {
      if (this.isLivingBase()) {
         ((class_1309)this.entity).method_6033(hp);
      }

   }

   public float getMaxHp() {
      return this.isLivingBase() ? ((class_1309)this.entity).method_6063() : 0.0F;
   }

   public void setMaxHp(float hp) {
      if (this.isLivingBase() && hp > 0.0F) {
         ((class_1309)this.entity).method_5996(class_5134.field_23716).method_6192((double)hp);
      }

   }

   public boolean isInWater() {
      return this.entity.method_5799();
   }

   public boolean isInLava() {
      return this.entity.method_5771();
   }

   public boolean isBurning() {
      return this.entity.method_5809();
   }

   public void setBurning(int seconds) {
      if (seconds <= 0) {
         this.entity.method_5646();
      } else {
         this.entity.method_5639(seconds);
      }

   }

   public boolean isSneaking() {
      return this.entity.method_5715();
   }

   public boolean isSprinting() {
      return this.entity.method_5624();
   }

   public boolean isOnGround() {
      return this.entity.method_24828();
   }

   public IScriptRayTrace rayTrace(double maxDistance) {
      ScriptVector start = new ScriptVector(this.entity.method_23317(), this.entity.method_23318() + (double)this.getEyeHeight(), this.entity.method_23321());
      return new ScriptRayTrace(this.entity.method_37908(), start, RayTracing.rayTraceWithEntity(this.entity, maxDistance));
   }

   public IScriptRayTrace rayTraceBlock(double maxDistance) {
      ScriptVector start = new ScriptVector(this.entity.method_23317(), this.entity.method_23318() + (double)this.getEyeHeight(), this.entity.method_23321());
      return new ScriptRayTrace(this.entity.method_37908(), start, RayTracing.rayTrace(this.entity, maxDistance, 0.0F));
   }

   public IScriptItemStack getMainItem() {
      return (IScriptItemStack)(this.isLivingBase() ? ScriptItemStack.create(((class_1309)this.entity).method_6047()) : ScriptItemStack.EMPTY);
   }

   public void setMainItem(IScriptItemStack stack) {
      this.setItem(class_1268.field_5808, stack);
   }

   public IScriptItemStack getOffItem() {
      return (IScriptItemStack)(this.isLivingBase() ? ScriptItemStack.create(((class_1309)this.entity).method_6079()) : ScriptItemStack.EMPTY);
   }

   public void setOffItem(IScriptItemStack stack) {
      this.setItem(class_1268.field_5810, stack);
   }

   private void setItem(class_1268 hand, IScriptItemStack stack) {
      if (stack == null) {
         stack = ScriptItemStack.EMPTY;
      }

      if (this.isLivingBase()) {
         ((class_1309)this.entity).method_6122(hand, stack.getMinecraftItemStack().method_7972());
      }

   }

   public void giveItem(IScriptItemStack stack) {
      this.giveItem(stack, true, true);
   }

   public void giveItem(IScriptItemStack stack, boolean playSound, boolean dropIfInventoryFull) {
      if (stack != null && !stack.isEmpty()) {
         if (this.isPlayer()) {
            class_1657 player = (class_1657)this.entity;
            class_1799 itemStack = stack.getMinecraftItemStack().method_7972();
            boolean flag = player.method_31548().method_7394(itemStack);
            if (flag) {
               if (playSound) {
                  player.method_37908().method_43128((class_1657)null, player.method_23317(), player.method_23318(), player.method_23321(), class_3417.field_15197, class_3419.field_15248, 0.2F, ((player.method_6051().method_43057() - player.method_6051().method_43057()) * 0.7F + 1.0F) * 2.0F);
               }

               player.field_7512.method_7623();
            } else if (dropIfInventoryFull && !player.method_37908().field_9236) {
               class_1542 entityItem = new class_1542(player.method_37908(), player.method_23317(), player.method_23318(), player.method_23321(), itemStack);
               entityItem.method_6982(0);
               player.method_37908().method_8649(entityItem);
            }
         } else if (this.isLivingBase() && this.entity instanceof class_1309) {
            class_1309 living = (class_1309)this.entity;
            if (living.method_6047().method_7960()) {
               living.method_6122(class_1268.field_5808, stack.getMinecraftItemStack().method_7972());
            } else if (living.method_6079().method_7960()) {
               living.method_6122(class_1268.field_5810, stack.getMinecraftItemStack().method_7972());
            } else {
               living.method_5699(stack.getMinecraftItemStack().method_7972(), this.getEyeHeight());
            }
         }

      }
   }

   public IScriptItemStack getHelmet() {
      return this.entity instanceof class_1309 ? ScriptItemStack.create(((class_1309)this.entity).method_6118(class_1304.field_6169).method_7972()) : null;
   }

   public IScriptItemStack getChestplate() {
      return this.entity instanceof class_1309 ? ScriptItemStack.create(((class_1309)this.entity).method_6118(class_1304.field_6174).method_7972()) : null;
   }

   public IScriptItemStack getLeggings() {
      return this.entity instanceof class_1309 ? ScriptItemStack.create(((class_1309)this.entity).method_6118(class_1304.field_6172).method_7972()) : null;
   }

   public IScriptItemStack getBoots() {
      return this.entity instanceof class_1309 ? ScriptItemStack.create(((class_1309)this.entity).method_6118(class_1304.field_6166).method_7972()) : null;
   }

   public void setHelmet(IScriptItemStack itemStack) {
      this.entity.method_5673(class_1304.field_6169, itemStack.getMinecraftItemStack());
   }

   public void setChestplate(IScriptItemStack itemStack) {
      this.entity.method_5673(class_1304.field_6174, itemStack.getMinecraftItemStack());
   }

   public void setLeggings(IScriptItemStack itemStack) {
      this.entity.method_5673(class_1304.field_6172, itemStack.getMinecraftItemStack());
   }

   public void setBoots(IScriptItemStack itemStack) {
      this.entity.method_5673(class_1304.field_6166, itemStack.getMinecraftItemStack());
   }

   public void setSpeed(float speed) {
      if (this.isLivingBase()) {
         ((class_1309)this.entity).method_5996(class_5134.field_23719).method_6192((double)speed);
      }

   }

   public IScriptEntity getTarget() {
      return this.entity instanceof class_1308 ? create(((class_1308)this.entity).method_5968()) : null;
   }

   public void setTarget(IScriptEntity entity) {
      if (this.entity instanceof class_1308 && entity == null) {
         class_1308 livingBase = (class_1308)this.entity;
         livingBase.method_5980((class_1309)null);
         livingBase.method_6015((class_1309)null);
         String id = "minecraft:armor_stand";
         double x = (double)this.entity.method_24515().method_10263();
         double y = (double)(this.entity.method_24515().method_10264() - 1);
         double z = (double)this.entity.method_24515().method_10260();
         String nbt = "{Marker:1b,NoGravity:1,Invisible:1b,CustomName:\"target_canceler\"}";
         class_2487 tag = new class_2487();

         try {
            tag = class_2522.method_10718(nbt);
         } catch (Exception var15) {
         }

         INBTCompound compound = new ScriptNBTCompound(tag);
         ScriptWorld world = new ScriptWorld(this.entity.method_37908());
         IScriptEntity targetCanceller = world.spawnEntity(id, x, y, z, compound);
         livingBase.method_5980((class_1309)targetCanceller.getMinecraftEntity());
         livingBase.method_6015((class_1309)targetCanceller.getMinecraftEntity());
         targetCanceller.remove();
      } else if (this.entity instanceof class_1308 && entity.isLivingBase()) {
         class_1308 livingBase = (class_1308)this.entity;
         livingBase.method_5980((class_1309)entity.getMinecraftEntity());
      }

   }

   public boolean isAIEnabled() {
      if (this.isLivingBase()) {
         return !((class_1308)this.entity).method_5987();
      } else {
         return false;
      }
   }

   public void setAIEnabled(boolean enabled) {
      if (this.isLivingBase()) {
         ((class_1308)this.entity).method_5977(!enabled);
      }

   }

   public String getUniqueId() {
      return this.entity.method_5845();
   }

   public String getEntityId() {
      class_2960 rl = class_7923.field_41177.method_10221(this.entity.method_5864());
      return rl == null ? "" : rl.toString();
   }

   public int getTicks() {
      return this.entity.field_6012;
   }

   public int getCombinedLight() {
      class_2338.class_2339 pos = new class_2338.class_2339(class_3532.method_15357(this.entity.method_23317()), 0, class_3532.method_15357(this.entity.method_23321()));
      if (this.entity.method_37908().method_22340(pos)) {
         pos.method_33098(class_3532.method_15357(this.entity.method_23318() + (double)this.entity.method_5751()));
         int block = this.entity.method_37908().method_8314(class_1944.field_9282, pos);
         int sky = this.entity.method_37908().method_8314(class_1944.field_9284, pos);
         return block << 4 | sky << 20;
      } else {
         return 0;
      }
   }

   public String getName() {
      return this.entity.method_5477().getString();
   }

   public void setName(String name) {
      this.entity.method_5665(class_2561.method_43470(name));
      if (name.isEmpty()) {
         this.entity.method_5880(false);
      } else {
         this.entity.method_5880(true);
      }

   }

   public void setInvisible(boolean invisible) {
      this.entity.method_5648(invisible);
   }

   public INBTCompound getFullData() {
      return new ScriptNBTCompound(this.entity.method_5647(new class_2487()));
   }

   public void setFullData(INBTCompound data) {
      this.entity.method_5651(data.getNbtCompound());
   }

   public INBTCompound getEntityData() {
      return new ScriptNBTCompound(EntityData.get(this.entity));
   }

   public boolean isPlayer() {
      return this.entity instanceof class_1657;
   }

   public boolean isNPC() {
      return this.entity instanceof EntityNpc;
   }

   public boolean isItem() {
      return this.entity instanceof class_1542;
   }

   public boolean isLivingBase() {
      return this.entity instanceof class_1309;
   }

   public boolean isSame(IScriptEntity entity) {
      return this.entity == entity.getMinecraftEntity();
   }

   public boolean isEntityInRadius(IScriptEntity entity, double radius) {
      return this.entity.method_5858(entity.getMinecraftEntity()) <= radius * radius;
   }

   public boolean isInArea(double x1, double y1, double z1, double x2, double y2, double z2) {
      return (new class_238(x1, y1, z1, x2, y2, z2)).method_994(this.entity.method_5829());
   }

   public void damage(float health) {
      if (this.isLivingBase()) {
         this.entity.method_5643(this.entity.method_37908().method_48963().method_48829(), health);
      }

   }

   public void damageAs(IScriptEntity entity, float damage) {
      class_1297 target = this.entity;
      class_1297 attacker = entity.getMinecraftEntity();
      if (attacker instanceof class_1309 attackerLiving) {
         target.method_5643(target.method_37908().method_48963().method_48812(attackerLiving), damage);
      }

   }

   public void damageWithItemsAs(IScriptPlayer player) {
      player.getMinecraftPlayer().method_7324(this.entity);
   }

   public void mount(IScriptEntity entity) {
      this.entity.method_5873(entity.getMinecraftEntity(), true);
   }

   public void dismount() {
      this.entity.method_5848();
   }

   public IScriptEntity getMount() {
      return create(this.entity.method_5854());
   }

   public void setSolidHitbox(boolean solid) {
      if (this.entity instanceof mchorse.mappet.compat.EntityDataHolder holder) {
         holder.mappet$setSolidHitbox(solid);
      }
   }

   public ScriptBox getBoundingBox() {
      class_238 aabb = this.entity.method_5829();
      return new ScriptBox(aabb.field_1323, aabb.field_1322, aabb.field_1321, aabb.field_1320, aabb.field_1325, aabb.field_1324);
   }

   private ScriptEntityItem dropItemInternal(class_1799 itemStack) {
      if (itemStack.method_7960()) {
         return null;
      } else {
         class_1542 entityItem = new class_1542(this.entity.method_37908(), this.entity.method_23317(), this.entity.method_23318() + (double)this.entity.method_5751(), this.entity.method_23321(), itemStack);
         entityItem.method_6982(40);
         if (this.isNPC()) {
            entityItem.method_6981(this.entity.method_5667());
         } else {
            entityItem.method_6981(this.entity.method_5667());
         }

         entityItem.field_6037 = true;
         entityItem.method_5762(this.getLook().x / (double)3.0F, this.getLook().y / (double)3.0F, this.getLook().z / (double)3.0F);
         return this.entity.method_37908().method_8649(entityItem) ? new ScriptEntityItem(entityItem) : null;
      }
   }

   public ScriptEntityItem dropItem(int amount) {
      class_1799 heldItemStack = this.getMainItem().getMinecraftItemStack();
      if (heldItemStack.method_7960()) {
         return null;
      } else {
         int count = heldItemStack.method_7947();
         if (amount > count) {
            amount = count;
         }

         class_1799 droppedStack = heldItemStack.method_7972();
         droppedStack.method_7939(amount);
         heldItemStack.method_7934(amount);
         return this.dropItemInternal(droppedStack);
      }
   }

   public ScriptEntityItem dropItem() {
      return this.dropItem(1);
   }

   public ScriptEntityItem dropItem(IScriptItemStack scriptItemStack) {
      return this.dropItemInternal(scriptItemStack.getMinecraftItemStack());
   }

   public float getFallDistance() {
      return this.entity.field_6017;
   }

   public void setFallDistance(float distance) {
      this.entity.field_6017 = distance;
   }

   public void remove() {
      this.entity.method_31472();
   }

   public void kill() {
      this.entity.method_5768();
   }

   public void swingArm(int arm) {
      if (this.isLivingBase()) {
         ((class_1309)this.entity).method_6104(arm == 1 ? class_1268.field_5810 : class_1268.field_5808);
      }

   }

   public List<IScriptEntity> getLeashedEntities() {
      List<IScriptEntity> entities = new ArrayList();
      class_1937 world = this.entity.method_37908();

      for(class_1297 entity : world.method_8335((class_1297)null, this.entity.method_5829().method_1014((double)128.0F))) {
         if (entity instanceof class_1308 entityLiving) {
            if (entityLiving.method_5934() && entityLiving.method_5933() == this.entity) {
               entities.add(create(entityLiving));
            }
         }
      }

      return entities;
   }

   public boolean setLeashHolder(IScriptEntity leashHolder) {
      if (!(this.entity instanceof class_1308)) {
         return false;
      } else {
         class_1308 leashedEntity = (class_1308)this.entity;
         boolean wasLeashed = leashedEntity.method_5934();
         leashedEntity.method_5954(leashHolder.getMinecraftEntity(), true);
         return !wasLeashed && leashedEntity.method_5934();
      }
   }

   public IScriptEntity getLeashHolder() {
      if (!(this.entity instanceof class_1308)) {
         return null;
      } else {
         class_1308 leashedEntity = (class_1308)this.entity;
         class_1297 leashHolder = leashedEntity.method_5933();
         return leashHolder == null ? null : create(leashHolder);
      }
   }

   public boolean clearLeashHolder(boolean dropLead) {
      if (!(this.entity instanceof class_1308)) {
         return false;
      } else {
         class_1308 leashedEntity = (class_1308)this.entity;
         boolean wasLeashed = leashedEntity.method_5934();
         leashedEntity.method_5932(true, dropLead);
         return wasLeashed && !leashedEntity.method_5934();
      }
   }

   private static class_1324 getAttribute(class_1309 entity, String name) {
      class_2960 id = class_2960.method_12829(name);
      class_1320 type = id == null ? null : (class_1320)class_7923.field_41190.method_10223(id);
      return type == null ? null : entity.method_5996(type);
   }

   public void setModifier(String modifierName, double value) {
      if (this.entity instanceof class_1309) {
         class_1309 entityLivingBase = (class_1309)this.entity;
         UUID uuid = entityLivingBase.method_5667();
         class_1324 attribute = getAttribute(entityLivingBase, modifierName);
         if (attribute == null) {
            return;
         }

         class_1322 modifier = new class_1322(uuid, "script." + modifierName, value, class_1323.field_6328);
         if (attribute.method_6196(modifier)) {
            attribute.method_6202(modifier);
         }

         attribute.method_26835(modifier);
      }

   }

   public double getModifier(String modifierName) {
      if (this.entity instanceof class_1309) {
         class_1309 entityLivingBase = (class_1309)this.entity;
         class_1324 attribute = getAttribute(entityLivingBase, modifierName);
         if (attribute != null) {
            class_1322 modifier = attribute.method_6199(entityLivingBase.method_5667());
            return modifier == null ? (double)0.0F : modifier.method_6186();
         }
      }

      return (double)0.0F;
   }

   public void removeModifier(String modifierName) {
      if (this.entity instanceof class_1309) {
         class_1309 entityLivingBase = (class_1309)this.entity;
         class_1324 attribute = getAttribute(entityLivingBase, modifierName);
         if (attribute != null) {
            class_1322 modifier = attribute.method_6199(entityLivingBase.method_5667());
            if (modifier != null) {
               attribute.method_6202(modifier);
            }
         }
      }

   }

   public void removeAllModifiers() {
      if (this.entity instanceof class_1309) {
         class_1309 entityLivingBase = (class_1309)this.entity;

         for(class_1320 type : class_7923.field_41190) {
            class_1324 attribute = entityLivingBase.method_5996(type);
            if (attribute != null) {
               attribute.method_6200(entityLivingBase.method_5667());
            }
         }
      }

   }

   public void applyPotion(class_1291 potion, int duration, int amplifier, boolean particles) {
      if (this.isLivingBase()) {
         class_1293 effect = new class_1293(potion, duration, amplifier, false, particles);
         ((class_1309)this.entity).method_6092(effect);
      }

   }

   public boolean hasPotion(class_1291 potion) {
      return this.isLivingBase() ? ((class_1309)this.entity).method_6059(potion) : false;
   }

   public boolean removePotion(class_1291 potion) {
      if (this.isLivingBase()) {
         class_1309 entity = (class_1309)this.entity;
         int size = entity.method_6088().size();
         entity.method_6016(potion);
         return size != entity.method_6088().size();
      } else {
         return false;
      }
   }

   public void clearPotions() {
      if (this.isLivingBase()) {
         ((class_1309)this.entity).method_6012();
      }

   }

   public IMappetStates getStates() {
      if (this.states == null) {
         States states = EntityUtils.getStates(this.entity);
         if (states != null) {
            this.states = new MappetStates(states);
         }
      }

      return this.states;
   }

   public AbstractMorph getMorph() {
      return this.entity instanceof IMorphProvider ? ((IMorphProvider)this.entity).getMorph() : null;
   }

   public boolean setMorph(AbstractMorph morph) {
      return FabricLoader.getInstance().isModLoaded("blockbuster") ? this.setActorsMorph(morph) : false;
   }

   





   public boolean setMorph(String morphNbt) {
      if (morphNbt == null || morphNbt.trim().isEmpty()) {
         return false;
      }

      try {
         class_2487 tag = class_2522.method_10718(morphNbt);
         AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(tag);
         return morph != null && this.setMorph(morph);
      } catch (Exception error) {
         return false;
      }
   }

   private boolean setActorsMorph(AbstractMorph morph) {
      if (this.entity instanceof EntityActor) {
         EntityActor actor = (EntityActor)this.entity;
         actor.morph.setDirect(morph);
         PacketModifyActor message = new PacketModifyActor(actor);
         mchorse.blockbuster.network.Dispatcher.sendToTracked(actor, message);
         return true;
      } else {
         return false;
      }
   }

   public void displayMorph(AbstractMorph morph, int expiration, double x, double y, double z, float yaw, float pitch, boolean rotate, IScriptPlayer player) {
      if (morph != null) {
         WorldMorph worldMorph = new WorldMorph();
         worldMorph.morph = morph;
         worldMorph.expiration = expiration;
         worldMorph.rotate = rotate;
         worldMorph.x = x;
         worldMorph.y = y;
         worldMorph.z = z;
         worldMorph.yaw = yaw;
         worldMorph.pitch = pitch;
         worldMorph.entity = this.entity;
         PacketWorldMorph message = new PacketWorldMorph(worldMorph);
         if (player == null) {
            Dispatcher.sendToTracked(this.entity, message);
            if (this.isPlayer()) {
               Dispatcher.sendTo(message, (class_3222)this.entity);
            }
         } else {
            Dispatcher.sendTo(message, player.getMinecraftPlayer());
         }

      }
   }

   public IScriptEntity shootBBGunProjectile(String gunPropsNBT) {
      if (this.entity instanceof class_1309 && FabricLoader.getInstance().isModLoaded("blockbuster")) {
         try {
            return this.shootBBGunProjectileMethod(gunPropsNBT);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return null;
   }

   private IScriptEntity shootBBGunProjectileMethod(String gunPropsNBT) throws CommandSyntaxException {
      if (this.entity instanceof class_1309) {
         class_1309 entityLivingBase = (class_1309)this.entity;
         class_2487 gunPropsNBTCompound = class_2522.method_10718(gunPropsNBT).method_10562("Gun");
         GunProps gunProps = new GunProps(gunPropsNBTCompound.method_10562("Projectile"));
         gunProps.fromNBT(gunPropsNBTCompound);
         EntityGunProjectile projectile = new EntityGunProjectile(entityLivingBase.method_37908(), gunProps, MorphManager.INSTANCE.morphFromNBT(gunProps.projectileMorph));
         projectile.method_5814(entityLivingBase.method_23317(), entityLivingBase.method_23318() + 1.8, entityLivingBase.method_23321());
         projectile.shootFrom(entityLivingBase);
         projectile.setInitialMotion();
         entityLivingBase.method_37908().method_8649(projectile);
         return create(projectile);
      } else {
         return null;
      }
   }

   public void executeCommand(String command) {
      this.entity.method_37908().method_8503().method_3734().method_44252(this.entity.method_5671(), command);
   }

   public void executeScript(String scriptName) {
      this.executeScript(scriptName, "main");
   }

   public void executeScript(String scriptName, String function) {
      DataContext context = new DataContext(this.entity);

      try {
         Mappet.scripts.execute(scriptName, function, context);
      } catch (ScriptException var6) {
         String fileName = var6.getFileName() == null ? scriptName : var6.getFileName();
         var6.printStackTrace();
         throw new RuntimeException("Script Error: " + fileName + " - Line: " + var6.getLineNumber() + " - Column: " + var6.getColumnNumber() + " - Message: " + var6.getMessage(), var6);
      } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException("Script Empty: " + scriptName + " - Error: " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
      }
   }

   public void executeScript(String scriptName, String function, Object... args) {
      DataContext context = new DataContext(this.entity);

      try {
         Mappet.scripts.execute(scriptName, function, context, args);
      } catch (ScriptException var7) {
         String fileName = var7.getFileName() == null ? scriptName : var7.getFileName();
         var7.printStackTrace();
         throw new RuntimeException("Script Error: " + fileName + " - Line: " + var7.getLineNumber() + " - Column: " + var7.getColumnNumber() + " - Message: " + var7.getMessage(), var7);
      } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException("Script Empty: " + scriptName + " - Error: " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
      }
   }

   public void lockPosition(double x, double y, double z) {
      EntityData.get(this.entity).method_10556("positionLocked", true);
      EntityData.get(this.entity).method_10549("lockX", x);
      EntityData.get(this.entity).method_10549("lockY", y);
      EntityData.get(this.entity).method_10549("lockZ", z);
   }

   public void unlockPosition() {
      EntityData.get(this.entity).method_10556("positionLocked", false);
   }

   public boolean isPositionLocked() {
      return EntityData.get(this.entity).method_10577("positionLocked");
   }

   public void lockRotation(float yaw, float pitch, float yawHead) {
      EntityData.get(this.entity).method_10556("rotationLocked", true);
      EntityData.get(this.entity).method_10548("lockYaw", yaw);
      EntityData.get(this.entity).method_10548("lockPitch", pitch);
      EntityData.get(this.entity).method_10548("lockYawHead", yawHead);
   }

   public void unlockRotation() {
      EntityData.get(this.entity).method_10556("rotationLocked", false);
   }

   public boolean isRotationLocked() {
      return EntityData.get(this.entity).method_10577("rotationLocked");
   }

   public void moveTo(String interpolation, int durationTicks, double x, double y, double z, boolean disableAI) {
      if (disableAI) {
         this.setAIEnabled(false);
         this.moveTo(interpolation, durationTicks, x, y, z);
         CommonProxy.eventHandler.addExecutable(new RunnableExecutionFork(durationTicks, () -> this.setAIEnabled(true)));
      } else {
         this.moveTo(interpolation, durationTicks, x, y, z);
      }

   }

   private void moveTo(String interpolation, int durationTicks, double x, double y, double z) {
      int duration = Math.max(0, durationTicks);
      if (duration == 0) {
         this.setPosition(x, y, z);
         return;
      }

      Interpolation interp = Interpolation.valueOf(interpolation.toUpperCase(java.util.Locale.ROOT));
      double startX = this.entity.method_23317();
      double startY = this.entity.method_23318();
      double startZ = this.entity.method_23321();
      this.sendEntityTransition(true, false, interpolation, duration, x, y, z, this.getPitch(), this.getYaw(), this.getYawHead());

      

      for (int step = 1; step <= duration; ++step) {
         double progress = (double)step / (double)duration;
         double interpX = interp.interpolate(startX, x, progress);
         double interpY = interp.interpolate(startY, y, progress);
         double interpZ = interp.interpolate(startZ, z, progress);
         CommonProxy.eventHandler.addExecutable(new RunnableExecutionFork(step - 1, () -> this.setInterpolatedPosition(interpX, interpY, interpZ)));
      }

   }

   

   private void setInterpolatedPosition(double x, double y, double z) {
      this.entity.method_5814(x, y, z);
   }

   

   private void setInterpolatedRotations(float pitch, float yaw, float yawHead) {
      this.entity.method_5808(this.entity.method_23317(), this.entity.method_23318(), this.entity.method_23321(), yaw, pitch);
      this.entity.method_5847(yawHead);
      if (this.entity instanceof class_1309) {
         ((class_1309)this.entity).field_6283 = yawHead;
      }
   }

   protected void sendEntityTransition(boolean position, boolean rotation, String interpolation, int duration, double x, double y, double z, float pitch, float yaw, float yawHead) {
      this.sendEntityTransition(position, rotation, interpolation, duration, x, y, z, pitch, yaw, yawHead, false);
   }

   protected void sendEntityTransition(boolean position, boolean rotation, String interpolation, int duration, double x, double y, double z, float pitch, float yaw, float yawHead, boolean relativeRotation) {
      


      PacketEntityTransition packet = new PacketEntityTransition(this.entity.method_5628(), position, rotation, interpolation, duration, x, y, z, pitch, yaw, yawHead, relativeRotation);
      for (class_3222 player : this.entity.method_5682().method_3760().method_14571()) {
         if (player.method_37908() == this.entity.method_37908() && player.method_5858(this.entity) <= (double)16384.0F) {
            Dispatcher.sendTo(packet, player);
         }
      }
   }

   public void observe(IScriptEntity entity) {
      if (this.entity instanceof class_1308) {
         class_1308 entityLiving = (class_1308)this.entity;
         if (entity == null) {
            class_4135 taskToRemove = null;

            for(class_4135 task : EntityGoals.goals(entityLiving).method_35115()) {
               if (task.method_19058() instanceof EntityAILookAtTarget) {
                  taskToRemove = task;
                  break;
               }
            }

            if (taskToRemove != null) {
               EntityGoals.goals(entityLiving).method_6280(taskToRemove.method_19058());
            }
         } else {
            EntityGoals.goals(entityLiving).method_6277(8, new EntityAILookAtTarget(entityLiving, entity.getMinecraftEntity(), 1.0F));
         }
      }

   }

   public IScriptEntity getObservedEntity() {
      if (this.entity instanceof class_1308) {
         class_1308 entityLiving = (class_1308)this.entity;

         for(class_4135 task : EntityGoals.goals(entityLiving).method_35115()) {
            class_1297 target = null;
            if (task.method_19058() instanceof EntityAILookAtTarget) {
               EntityAILookAtTarget lookAtTask = (EntityAILookAtTarget)task.method_19058();
               target = lookAtTask.getTarget();
            } else if (task.method_19058() instanceof class_1361) {
               class_1361 watchClosestTask = (class_1361)task.method_19058();
               target = this.getEntityFromWatchClosest(watchClosestTask);
            }

            if (target != null) {
               return create(target);
            }
         }
      }

      return null;
   }

   private class_1297 getEntityFromWatchClosest(class_1361 watchClosestTask) {
      for(Field field : class_1361.class.getDeclaredFields()) {
         if (class_1297.class.isAssignableFrom(field.getType())) {
            try {
               field.setAccessible(true);
               return (class_1297)field.get(watchClosestTask);
            } catch (ReflectiveOperationException var7) {
            }
         }
      }

      return null;
   }

   public void addEntityPatrol(double x, double y, double z, double speed, boolean shouldCirculate, String executeCommandOnArrival) {
      if (this.entity instanceof class_1308) {
         class_1308 entityLiving = (class_1308)this.entity;
         class_4135 taskToRemove = this.findEntitiesAIPatrolTask(entityLiving);
         EntitiesAIPatrol patrolTask;
         if (taskToRemove != null) {
            patrolTask = (EntitiesAIPatrol)taskToRemove.method_19058();
            EntityGoals.goals(entityLiving).method_6280(patrolTask);
            patrolTask.addPatrolPoint(class_2338.method_49637(x, y, z), shouldCirculate, executeCommandOnArrival);
         } else {
            patrolTask = new EntitiesAIPatrol((class_1308)this.entity, speed, new class_2338[]{class_2338.method_49637(x, y, z)}, new boolean[]{shouldCirculate}, new String[]{executeCommandOnArrival});
         }

         EntityGoals.goals(entityLiving).method_6277(1, patrolTask);
      }

   }

   public void clearEntityPatrols() {
      if (this.entity instanceof class_1308) {
         class_1308 entityLiving = (class_1308)this.entity;
         class_4135 taskToRemove = this.findEntitiesAIPatrolTask(entityLiving);
         if (taskToRemove != null) {
            EntityGoals.goals(entityLiving).method_6280(taskToRemove.method_19058());
         }
      }

   }

   private class_4135 findEntitiesAIPatrolTask(class_1308 entityLiving) {
      for(class_4135 task : EntityGoals.goals(entityLiving).method_35115()) {
         if (task.method_19058() instanceof EntitiesAIPatrol) {
            return task;
         }
      }

      return null;
   }

   public void setRotationsAI(float yaw, float pitch, float yawHead) {
      if (this.entity instanceof class_1308) {
         class_1308 entityLiving = (class_1308)this.entity;
         this.removeTaskIfExists(entityLiving, EntityAIRotations.class);
         EntityGoals.goals(entityLiving).method_6277(9, new EntityAIRotations(entityLiving, yaw, pitch, yawHead, 1.0F));
         RotationDataStorage.getRotationDataStorage(this.entity.method_37908()).addRotationData(entityLiving.method_5667(), yaw, pitch, yawHead);
      }

   }

   public void clearRotationsAI() {
      if (this.entity instanceof class_1308) {
         class_1308 entityLiving = (class_1308)this.entity;
         this.removeTaskIfExists(entityLiving, EntityAIRotations.class);
         RotationDataStorage.getRotationDataStorage(this.entity.method_37908()).removeRotationData(entityLiving.method_5667());
      }

   }

   private class_4135 removeTaskIfExists(class_1308 entityLiving, Class<?> taskClass) {
      class_4135 taskToRemove = null;

      for(class_4135 task : EntityGoals.goals(entityLiving).method_35115()) {
         if (task.method_19058().getClass().equals(taskClass)) {
            taskToRemove = task;
            break;
         }
      }

      if (taskToRemove != null) {
         EntityGoals.goals(entityLiving).method_6280(taskToRemove.method_19058());
      }

      return taskToRemove;
   }

   public void executeRepeatingCommand(String command, int frequency) {
      if (this.entity instanceof class_1308) {
         class_1308 entityLiving = (class_1308)this.entity;
         EntityGoals.goals(entityLiving).method_6277(10, new EntityAIRepeatingCommand(entityLiving, command, frequency));
         RepeatingCommandDataStorage.getRepeatingCommandDataStorage(this.entity.method_37908()).addRepeatingCommandData(entityLiving.method_5667(), command, frequency);
      }

   }

   public void clearAllRepeatingCommands() {
      if (this.entity instanceof class_1308) {
         class_1308 entityLiving = (class_1308)this.entity;
         this.removeTaskIfExists(entityLiving, EntityAIRepeatingCommand.class);
         RepeatingCommandDataStorage.getRepeatingCommandDataStorage(this.entity.method_37908()).removeRepeatingCommandData(entityLiving.method_5667());
      }

   }

   public void removeRepeatingCommand(String command) {
      if (this.entity instanceof class_1308) {
         class_1308 entityLiving = (class_1308)this.entity;
         this.removeSpecificRepeatingCommandTaskIfExists(entityLiving, command);
         RepeatingCommandDataStorage.getRepeatingCommandDataStorage(this.entity.method_37908()).removeSpecificRepeatingCommandData(entityLiving.method_5667(), command);
      }

   }

   private void removeSpecificRepeatingCommandTaskIfExists(class_1308 entityLiving, String command) {
      List<class_4135> tasksToRemove = new ArrayList();

      for(class_4135 task : EntityGoals.goals(entityLiving).method_35115()) {
         if (task.method_19058() instanceof EntityAIRepeatingCommand && ((EntityAIRepeatingCommand)task.method_19058()).getCommand().equals(command)) {
            tasksToRemove.add(task);
         }
      }

      for(class_4135 taskToRemove : tasksToRemove) {
         EntityGoals.goals(entityLiving).method_6280(taskToRemove.method_19058());
      }

   }
}
