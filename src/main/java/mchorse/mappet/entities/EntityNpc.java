package mchorse.mappet.entities;

import java.util.List;
import java.util.UUID;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.factions.Faction;
import mchorse.mappet.api.factions.FactionAttitude;
import mchorse.mappet.api.npcs.Npc;
import mchorse.mappet.api.npcs.NpcDrop;
import mchorse.mappet.api.npcs.NpcState;
import mchorse.mappet.api.scripts.code.nbt.ScriptNBTCompound;
import mchorse.mappet.api.scripts.user.nbt.INBTCompound;
import mchorse.mappet.api.states.States;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.entities.ai.EntityAIAlwaysWander;
import mchorse.mappet.entities.ai.EntityAIAttackNpcMelee;
import mchorse.mappet.entities.ai.EntityAIFollowTarget;
import mchorse.mappet.entities.ai.EntityAIHurtByTargetNpc;
import mchorse.mappet.entities.ai.EntityAIReturnToPost;
import mchorse.mappet.entities.ai.NpcPatrolController;
import mchorse.mappet.entities.ai.fly.EntityAINpcFly;
import mchorse.mappet.entities.ai.fly.FlyingMoveHelper;
import mchorse.mappet.entities.utils.MappetNpcRespawnManager;
import mchorse.mappet.entities.utils.NpcDamageSource;
import mchorse.mappet.items.ItemNpcTool;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.npc.PacketNpcStateChange;
import mchorse.mappet.utils.EntityUtils;
import mchorse.mclib.commands.utils.EntitySelectorUtils;
import mchorse.mclib.utils.Interpolations;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1313;
import net.minecraft.class_1314;
import net.minecraft.class_1347;
import net.minecraft.class_1361;
import net.minecraft.class_1376;
import net.minecraft.class_1394;
import net.minecraft.class_1400;
import net.minecraft.class_1407;
import net.minecraft.class_1408;
import net.minecraft.class_1409;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2168;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_2540;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3532;
import net.minecraft.class_5132;
import net.minecraft.class_5134;
import net.minecraft.class_8111;

public class EntityNpc extends class_1314 implements IMorphProvider {
   public static final int RENDER_DISTANCE = 160;
   private Morph morph = new Morph();
   private NpcState state = new NpcState();
   private int lastDamageTime;
   private boolean unkillableFailsafe = true;
   private Faction faction;
   private EntityAIHurtByTargetNpc targetAI;
   private final NpcPatrolController patrolController = new NpcPatrolController();
   public float smoothYawHead;
   public float prevSmoothYawHead;
   public float smoothBodyYawHead;
   public float prevSmoothBodyYawHead;
   private class_1297 lastTarget;
   public boolean dieOnLoad = false;

   public EntityNpc(class_1299<? extends EntityNpc> type, class_1937 worldIn) {
      super(type, worldIn);
   }

   public static class_5132.class_5133 createNpcAttributes() {
      return class_1314.method_26828().method_26868(class_5134.field_23719, (double)0.3125F).method_26868(class_5134.field_23721, (double)2.0F);
   }

   public void method_5697(class_1297 entityIn) {
      if (!(Boolean)this.state.immovable.get()) {
         super.method_5697(entityIn);
      }

      if (!this.method_37908().field_9236) {
         this.state.triggerEntityCollision.trigger(new DataContext(this, entityIn));
      }
   }

   public boolean method_5740() {
      return (Boolean)this.state.hasNoGravity.get();
   }

   public boolean method_5936() {
      return (Boolean)this.state.canPickUpLoot.get();
   }

   public boolean canBeSteered() {
      return (Boolean)this.state.canBeSteered.get();
   }

   protected void method_5865(class_1297 passenger, class_1297.class_4738 positionUpdater) {
      if (this.method_5626(passenger)) {
         int index = this.method_5685().indexOf(passenger);
         class_2338 offsetPos;
         if (!this.state.steeringOffset.isEmpty() && index < this.state.steeringOffset.size()) {
            offsetPos = (class_2338)this.state.steeringOffset.get(index);
         } else {
            offsetPos = new class_2338(0, 0, 0);
         }

         double offsetX = (double)offsetPos.method_10263();
         double offsetY = this.method_23318() - (double)0.5F + (double)EntityUtils.getHeight(this) + (double)offsetPos.method_10264();
         double offsetZ = (double)offsetPos.method_10260();
         double bodyYaw = Math.toRadians((double)this.field_6283);
         double rotatedOffsetX = offsetX * Math.cos(bodyYaw) - offsetZ * Math.sin(bodyYaw);
         double rotatedOffsetZ = offsetX * Math.sin(bodyYaw) + offsetZ * Math.cos(bodyYaw);
         double finalPosX = this.method_23317() + rotatedOffsetX;
         double finalPosZ = this.method_23321() + rotatedOffsetZ;
         positionUpdater.accept(passenger, finalPosX, offsetY, finalPosZ);
         this.method_36456(passenger.method_36454());
         this.method_36457(passenger.method_36455());
      }

      if (passenger instanceof class_1657 && this.canBeSteered() && this.method_5685().indexOf(passenger) == this.method_5685().size() - 1) {
         this.handleSteering((class_1657)passenger);
      }

   }

   private void handleSteering(class_1657 player) {
      if (!this.method_37908().field_9236) {
         float forward = player.field_6250;
         float strafe = player.field_6212;
         this.method_36456(player.method_36454());
         this.method_5847(player.method_5791());
         if (forward != 0.0F || strafe != 0.0F) {
            float baseSpeed = (Float)this.state.speed.get() / 15.0F;
            double motionX = -Math.sin(Math.toRadians((double)this.method_36454())) * (double)forward + Math.cos(Math.toRadians((double)this.method_36454())) * (double)strafe;
            double motionZ = Math.cos(Math.toRadians((double)this.method_36454())) * (double)forward + Math.sin(Math.toRadians((double)this.method_36454())) * (double)strafe;
            double motionMagnitude = Math.sqrt(motionX * motionX + motionZ * motionZ);
            motionX /= motionMagnitude;
            motionZ /= motionMagnitude;
            class_243 velocity = this.method_18798();
            this.method_18800(motionX * (double)baseSpeed, velocity.field_1351, motionZ * (double)baseSpeed);
            this.method_5784(class_1313.field_6308, this.method_18798());
            this.method_5808(this.method_23317(), this.method_23318(), this.method_23321(), this.method_36454(), this.method_36455());
         }
      }

   }

   protected void method_5623(double heightDifference, boolean onGround, class_2680 state, class_2338 landedPosition) {
      if (!this.method_5782()) {
         super.method_5623(heightDifference, onGround, state, landedPosition);
      }

   }

   protected class_1408 method_5965(class_1937 world) {
      return (class_1408)(this.state != null && (Boolean)this.state.canFly.get() ? new class_1407(this, world) : new class_1409(this, world));
   }

   protected void method_5959() {
      super.method_5959();
      this.field_6201.method_35113((goal) -> true);
      this.field_6185.method_35113((goal) -> true);
      double speed = (double)1.0F;
      if (this.state != null) {
         speed = (double)(Float)this.state.speed.get();
         if ((Boolean)this.state.canSwim.get()) {
            this.field_6201.method_6277(0, new class_1347(this));
         }

         if (!((String)this.state.follow.get()).isEmpty()) {
            this.field_6201.method_6277(6, new EntityAIFollowTarget(this, speed, 2.0F, 10.0F));
         } else if ((Boolean)this.state.hasPost.get() && this.state.postPosition != null) {
            this.field_6201.method_6277(6, new EntityAIReturnToPost(this, this.state.postPosition, speed, (Float)this.state.postRadius.get()));
         }

         if ((Boolean)this.state.lookAround.get()) {
            this.field_6201.method_6277(8, new class_1376(this));
         }

         if ((Boolean)this.state.lookAtPlayer.get()) {
            this.field_6201.method_6277(9, new class_1361(this, class_1657.class, (Float)this.state.pathDistance.get(), 1.0F));
         }

         if ((Boolean)this.state.wander.get()) {
            this.field_6201.method_6277(9, new class_1394(this, speed / (double)2.0F));
         }

         if ((Boolean)this.state.canFly.get()) {
            this.field_6207 = new FlyingMoveHelper(this);
            this.field_6201.method_6277(10, new EntityAINpcFly(this));
         } else if ((Boolean)this.state.alwaysWander.get()) {
            this.field_6201.method_6277(10, new EntityAIAlwaysWander(this, speed / (double)2.0F));
         }
      }

      this.field_6185.method_6277(1, this.targetAI = new EntityAIHurtByTargetNpc(this, false, new Class[0]));
      this.field_6185.method_6277(2, new class_1400(this, class_1309.class, 10, true, false, (entity) -> this.targetCheck((class_1309) entity)));
      if (this.state != null) {
         this.field_6201.method_6277(4, new EntityAIAttackNpcMelee(this, speed, false, (Integer)this.state.damageDelay.get()));
      }

   }

   private boolean targetCheck(class_1309 entity) {
      if (this.isEntityOutOfPostDistance(entity)) {
         return false;
      } else {
         Faction faction = this.getFaction();
         if (entity instanceof EntityNpc) {
            EntityNpc npc = (EntityNpc)entity;
            if (faction != null) {
               return faction.get((String)npc.getState().faction.get()) == FactionAttitude.AGGRESSIVE;
            }
         }

         if (entity instanceof class_3222) {
            class_3222 player = (class_3222)entity;
            if (player.method_7325() || player.method_7337()) {
               return false;
            }

            FactionAttitude attitude = this.getPlayerAttitude(faction, entity);
            if (attitude != null) {
               return attitude == FactionAttitude.AGGRESSIVE;
            }
         }

         return faction != null && faction.othersAttitude == FactionAttitude.AGGRESSIVE;
      }
   }

   private FactionAttitude getPlayerAttitude(Faction faction, class_1297 entity) {
      if (entity instanceof class_3222 player) {
         ICharacter character = Character.get(player);
         if (faction != null && character != null) {
            return faction.get(character.getStates());
         }
      }

      return null;
   }

   public void initialize() {
      this.state.triggerInitialize.trigger((class_1309)this);
   }

   public States getStates() {
      return this.state.states;
   }

   public Faction getFaction() {
      if (this.faction == null) {
         String faction = (String)this.state.faction.get();
         this.faction = faction.isEmpty() ? null : (Faction)Mappet.factions.load(faction);
      }

      return this.faction;
   }

   public void setNpc(Npc npc, NpcState state) {
      this.setState(state, false);
      if (((String)this.state.id.get()).isEmpty()) {
         this.state.id.set(npc.getId());
      }

   }

   public String getNpcId() {
      return (String)this.state.id.get();
   }

   public NpcState getState() {
      return this.state;
   }

   public void setState(NpcState state, boolean notify) {
      notify = true;
      this.state = new NpcState();
      this.state.deserializeNBT(state.serializeNBT());
      this.method_5996(class_5134.field_23717).method_6192((double)(Float)this.state.pathDistance.get());
      this.field_6189 = this.method_5965(this.method_37908());
      double max = (double)this.method_6063();
      double health = (double)this.method_6032();
      this.setMaxHealth((double)(Float)state.maxHealth.get());
      this.method_6033((float)class_3532.method_15350((double)(Float)state.maxHealth.get() * (health / max), (double)1.0F, (double)(Float)state.maxHealth.get()));
      this.morph.set(state.morph);
      if (notify) {
         this.sendNpcStateChangePacket();
      }

      this.faction = null;
      this.method_5959();
   }

   public void sendNpcStateChangePacket() {
      if (this.method_37908() instanceof class_3218) {
         Dispatcher.sendToTracked(this, new PacketNpcStateChange(this));
      }

   }

   public AbstractMorph getMorph() {
      return this.morph.get();
   }

   public class_1309 getFollowTarget() {
      if (((String)this.state.follow.get()).isEmpty()) {
         return null;
      } else if (this.state.follow.equals("@r")) {
         List<? extends class_1657> players = this.method_37908().method_18456();
         int index = class_3532.method_15340((int)(Math.random() * (double)players.size() - (double)1.0F), 0, players.size() - 1);
         return players.isEmpty() ? null : (class_1309)players.get(index);
      } else {
         if (((String)this.state.follow.get()).startsWith("@")) {
            try {
               class_2168 sender = CommandNpc.getCommandSender(this);

               for(class_1297 entity : EntitySelectorUtils.getEntities(sender, (String)this.state.follow.get())) {
                  if (entity instanceof class_1309) {
                     return (class_1309)entity;
                  }
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
         } else {
            try {
               class_1657 player = Mappet.server.method_3760().method_14566((String)this.state.follow.get());
               return (class_1309)(player == null ? Mappet.server.method_3760().method_14602(UUID.fromString((String)this.state.follow.get())) : player);
            } catch (Exception var6) {
            }
         }

         return null;
      }
   }

   public void setMorph(AbstractMorph morph) {
      this.morph.set(morph);
      this.state.morph = morph;
   }

   public void setMaxHealth(double value) {
      this.method_5996(class_5134.field_23716).method_6192(value);
   }

   public void method_5773() {
      if (this.lastTarget != this.method_5968()) {
         this.lastTarget = this.method_5968();
         this.state.triggerTarget.trigger(new DataContext(this, this.lastTarget));
      }

      this.healthFailsafe();
      this.updateAttackTarget();
      super.method_5773();
      this.patrolController.tick(this);
      if (!this.morph.isEmpty()) {
         this.morph.get().update(this);
      }

      if ((Integer)this.state.regenDelay.get() > 0 && !this.method_37908().field_9236) {
         int regen = (Integer)this.state.regenFrequency.get() == 0 ? 1 : (Integer)this.state.regenFrequency.get();
         if (this.lastDamageTime >= (Integer)this.state.regenDelay.get() && this.field_6012 % regen == 0 && this.method_6032() > 0.0F && this.method_6032() < this.method_6063()) {
            this.method_6025(1.0F);
         }

         ++this.lastDamageTime;
      }

      if (this.method_37908().field_9236) {
         this.prevSmoothYawHead = this.smoothYawHead;
         this.smoothYawHead = Interpolations.lerpYaw(this.smoothYawHead, this.field_6241, 0.5F);
         this.prevSmoothBodyYawHead = this.smoothBodyYawHead;
         this.smoothBodyYawHead = Interpolations.lerpYaw(this.smoothBodyYawHead, this.field_6283, 0.5F);
      } else {
         this.state.triggerTick.trigger((class_1309)this);
      }

   }

   private boolean isEntityOutOfPostDistance(class_1297 entity) {
      if (this.state != null && this.state.postPosition != null && (Boolean)this.state.hasPost.get()) {
         class_2338 post = this.state.postPosition;
         class_2338 position = entity.method_24515();
         double distance = post.method_10262(position);
         return distance > (double)((Float)this.state.fallback.get() * (Float)this.state.fallback.get());
      } else {
         return false;
      }
   }

   private void updateAttackTarget() {
      if (this.state != null && this.method_5968() != null && this.state.postPosition != null && (Boolean)this.state.hasPost.get() && this.isEntityOutOfPostDistance(this.method_5968())) {
         this.targetAI.reset = true;
         this.method_5980((class_1309)null);
      }

      if (this.faction != null && this.field_6012 % 10 == 0) {
         class_1297 entity = this.method_5968();
         if (entity instanceof class_3222) {
            class_3222 player = (class_3222)entity;
            Faction faction = this.getFaction();
            FactionAttitude attitude = this.getPlayerAttitude(faction, player);
            if (attitude == FactionAttitude.FRIENDLY || player.method_7337()) {
               this.method_5980((class_1309)null);
            }
         }

      }
   }

   protected void method_6108() {
      if ((Boolean)this.state.killable.get() || !this.unkillableFailsafe) {
         super.method_6108();
      }

   }

   protected void method_16077(class_1282 source, boolean causedByPlayer) {
      super.method_16077(source, causedByPlayer);

      for(NpcDrop drop : this.state.drops) {
         if (this.field_5974.method_43057() < drop.chance) {
            this.method_5775(drop.stack.method_7972());
         }
      }

   }

   public boolean method_6121(class_1297 entityIn) {
      class_1282 source = (class_1282)((Boolean)Mappet.npcsPeacefulDamage.get() ? new NpcDamageSource(this) : this.method_48923().method_48812(this));
      return entityIn.method_5643(source, (Float)this.state.damage.get());
   }

   public boolean method_5643(class_1282 damage, float damageAmount) {
      boolean applied = super.method_5643(damage, damageAmount);
      if (applied && !this.method_5679(damage)) {
         this.lastDamageTime = 0;
      }

      this.healthFailsafe();
      



      if (!this.method_37908().field_9236) {
         this.state.triggerDamaged.trigger((class_1309)this);
      }

      return applied;
   }

   public boolean method_5679(class_1282 source) {
      if (this.method_37908().field_9236) {
         return true;
      } else {
         class_1297 entity = source.method_5529();
         if (entity instanceof class_3222) {
            class_3222 player = (class_3222)entity;
            Faction faction = this.getFaction();
            FactionAttitude attitude = this.getPlayerAttitude(faction, player);
            if (attitude == FactionAttitude.FRIENDLY && !player.method_7337()) {
               return true;
            }
         }

         if (!(Boolean)this.state.invincible.get()) {
            return !(Boolean)this.state.canFallDamage.get() && source.method_49708(class_8111.field_42345) ? true : super.method_5679(source);
         } else {
            return !source.method_5530() && !source.method_49708(class_8111.field_42347);
         }
      }
   }

   public void method_5768() {
      this.unkillableFailsafe = false;
      super.method_5768();
   }

   public void method_6078(class_1282 cause) {
      super.method_6078(cause);
      if ((Boolean)this.state.respawn.get() && !this.dieOnLoad) {
         MappetNpcRespawnManager respawnManager = MappetNpcRespawnManager.get(this.method_37908());
         respawnManager.addDiedNpc(this);
         this.dieOnLoad = true;
      }

      this.state.triggerDied.trigger(this, cause.method_5529());
   }

   public boolean method_5974(double distanceSquared) {
      return (Boolean)this.state.unique.get();
   }

   public void healthFailsafe() {
      if (!(Boolean)this.state.killable.get() && this.method_6032() <= 0.0F && this.unkillableFailsafe) {
         this.method_6033(0.001F);
      }

   }

   protected class_1269 method_5992(class_1657 player, class_1268 hand) {
      if (!this.method_37908().field_9236) {
         if (!player.method_5998(hand).method_7920(player, this, hand).method_23665()) {
            this.state.triggerInteract.trigger(new DataContext(player, this));
         }

         if ((this.method_5685().size() < this.state.steeringOffset.size() || this.state.steeringOffset.isEmpty()) && this.canBeSteered() && !(player.method_5998(hand).method_7909() instanceof ItemNpcTool)) {
            player.method_5873(this, true);
         }
      }

      return class_1269.field_5812;
   }

   public void method_5652(class_2487 tag) {
      tag.method_10556("DieOnLoad", this.dieOnLoad);
      if (!this.dieOnLoad) {
         tag.method_10566("State", this.state.serializeNBT());
      }

   }

   public void method_5749(class_2487 tag) {
      NpcState state = new NpcState();
      state.deserializeNBT(tag.method_10562("State"));
      if (tag.method_10545("States")) {
         state.states.deserializeNBT(tag.method_10562("States"));
      }

      this.setState(state, false);
      if (tag.method_10545("NpcId")) {
         state.id.set(tag.method_10558("NpcId"));
      }

      if (tag.method_10545("DieOnLoad") && tag.method_10577("DieOnLoad")) {
         this.method_31472();
      }

   }

   public void writeSpawnData(class_2540 buf) {
      MorphUtils.morphToBuf(buf, this.morph.get());
      this.state.writeToBuf(buf);
   }

   public void readSpawnData(class_2540 buf) {
      this.morph.setDirect(MorphUtils.morphFromBuf(buf));
      this.state.readFromBuf(buf);
      this.field_6259 = this.field_6241;
      this.smoothYawHead = this.field_6241;
   }

   @Environment(EnvType.CLIENT)
   public boolean method_5640(double distance) {
      double d0 = this.method_5829().method_995();
      if (Double.isNaN(d0)) {
         d0 = (double)1.0F;
      }

      d0 *= (double)160.0F;
      return distance < d0 * d0;
   }

   public void setStringInData(String key, String value) {
      INBTCompound fullData = new ScriptNBTCompound(this.method_5647(new class_2487()));
      fullData.getCompound("State").setString(key, value);
      this.method_5651(fullData.getNbtCompound());
   }

   public boolean method_5753() {
      return !(Boolean)this.state.canGetBurned.get() || super.method_5753();
   }

   public int method_6110() {
      return (Integer)this.state.xp.get();
   }

   public void method_5837(class_3222 player) {
      super.method_5837(player);
      Dispatcher.sendTo(new PacketNpcStateChange(this), player);
   }
}
