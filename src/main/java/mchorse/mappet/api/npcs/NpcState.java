package mchorse.mappet.api.npcs;

import com.google.common.base.CaseFormat;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mchorse.mappet.api.states.States;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mappet.utils.NBTUtils;
import mchorse.mappet.utils.NpcStateUtils;
import mchorse.mclib.config.values.GenericValue;
import mchorse.mclib.config.values.Value;
import mchorse.mclib.config.values.ValueBoolean;
import mchorse.mclib.config.values.ValueDouble;
import mchorse.mclib.config.values.ValueFloat;
import mchorse.mclib.config.values.ValueInt;
import mchorse.mclib.config.values.ValueString;
import mchorse.mclib.utils.ValueSerializer;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2520;
import net.minecraft.class_2522;

public class NpcState implements INBTSerializable<class_2487> {
   public ValueString stateName = new ValueString("StateName", "");
   public ValueString id = new ValueString("Id", "");
   public States states = new States();
   public ValueBoolean unique = new ValueBoolean("Unique");
   public ValueFloat pathDistance = new ValueFloat("PathDistance", 32.0F);
   public ValueFloat maxHealth = new ValueFloat("MaxHealth", 20.0F);
   public ValueFloat health = new ValueFloat("Health", 20.0F);
   public ValueInt regenDelay = new ValueInt("RegenDelay", 80);
   public ValueInt regenFrequency = new ValueInt("RegenFrequency", 20);
   public ValueFloat damage = new ValueFloat("Damage", 2.0F);
   public ValueInt damageDelay = new ValueInt("DamageDelay", 10);
   public ValueBoolean canRanged = new ValueBoolean("CanRanged");
   public ValueBoolean canFallDamage = new ValueBoolean("CanFallDamage", true);
   public ValueBoolean canGetBurned = new ValueBoolean("CanGetBurned", true);
   public ValueBoolean invincible = new ValueBoolean("Invincible");
   public ValueBoolean killable = new ValueBoolean("Killable", true);
   public ValueFloat speed = new ValueFloat("Speed", 1.0F);
   public ValueFloat jumpPower = new ValueFloat("JumpPower", 0.6F);
   public ValueBoolean canSwim = new ValueBoolean("CanSwim", true);
   public ValueBoolean immovable = new ValueBoolean("Immovable");
   public ValueBoolean hasPost = new ValueBoolean("HasPost");
   public class_2338 postPosition;
   public ValueFloat postRadius = new ValueFloat("PostRadius", 1.0F);
   public ValueFloat fallback = new ValueFloat("Fallback", 15.0F);
   public ValueBoolean patrolCirculate = new ValueBoolean("PatrolCirculate");
   public List<class_2338> patrol = new ArrayList();
   public List<Trigger> patrolTriggers = new ArrayList();
   public List<class_2338> steeringOffset = new ArrayList();
   public ValueString follow = new ValueString("Follow", "");
   public ValueString faction = new ValueString("Faction", "");
   public AbstractMorph morph;
   public ValueFloat sightDistance = new ValueFloat("SightDistance", 25.0F);
   public ValueFloat sightRadius = new ValueFloat("SightRadius", 120.0F);
   public List<NpcDrop> drops = new ArrayList();
   public ValueInt xp = new ValueInt("Xp", 0);
   public ValueFloat shadowSize = new ValueFloat("ShadowSize", 0.6F);
   public ValueBoolean lookAtPlayer = new ValueBoolean("LookAtPlayer");
   public ValueBoolean lookAround = new ValueBoolean("LookAround");
   public ValueBoolean wander = new ValueBoolean("Wander");
   public ValueBoolean alwaysWander = new ValueBoolean("AlwaysWander");
   public ValueBoolean canFly = new ValueBoolean("CanFly");
   public ValueDouble flightMaxHeight = new ValueDouble("FlightMaxHeight", (double)6.0F);
   public ValueDouble flightMinHeight = new ValueDouble("FlightMinHeight", (double)4.0F);
   public ValueFloat flee = new ValueFloat("Flee", 4.0F);
   public ValueBoolean canPickUpLoot = new ValueBoolean("CanPickUpLoot");
   public ValueBoolean hasNoGravity = new ValueBoolean("HasNoGravity", false);
   public ValueBoolean canBeSteered = new ValueBoolean("CanBeSteered", false);
   public Trigger triggerDied = new Trigger();
   public Trigger triggerDamaged = new Trigger();
   public Trigger triggerInteract = new Trigger();
   public Trigger triggerTick = new Trigger();
   public Trigger triggerTarget = new Trigger();
   public Trigger triggerInitialize = new Trigger();
   public Trigger triggerRespawn = new Trigger();
   public Trigger triggerEntityCollision = new Trigger();
   public ValueBoolean respawn = new ValueBoolean("Respawn");
   public ValueInt respawnDelay = new ValueInt("RespawnDelay");
   public ValueBoolean respawnOnCoordinates = new ValueBoolean("RespawnOnCoordinates");
   public ValueDouble respawnPosX = new ValueDouble("RespawnPosX");
   public ValueDouble respawnPosY = new ValueDouble("RespawnPosY");
   public ValueDouble respawnPosZ = new ValueDouble("RespawnPosZ");
   public ValueBoolean respawnSaveUUID = new ValueBoolean("RespawnSaveUUID");
   public ValueSerializer serializer = new ValueSerializer();
   public Map<String, GenericValue> serealizableValues = new HashMap();

   public NpcState() {
      this.registerValue(this.stateName);
      this.registerValue(this.id);
      this.registerValue(this.unique);
      this.registerValue(this.pathDistance);
      this.registerValue(this.maxHealth);
      this.registerValue(this.health);
      this.registerValue(this.regenDelay);
      this.registerValue(this.regenFrequency);
      this.registerValue(this.damage);
      this.registerValue(this.damageDelay);
      this.registerValue(this.canRanged);
      this.registerValue(this.canFallDamage);
      this.registerValue(this.canGetBurned);
      this.registerValue(this.invincible);
      this.registerValue(this.killable);
      this.registerValue(this.speed);
      this.registerValue(this.jumpPower);
      this.registerValue(this.canSwim);
      this.registerValue(this.immovable);
      this.registerValue(this.hasPost);
      this.registerValue(this.postRadius);
      this.registerValue(this.fallback);
      this.registerValue(this.patrolCirculate);
      this.registerValue(this.follow);
      this.registerValue(this.faction);
      this.registerValue(this.sightDistance);
      this.registerValue(this.sightRadius);
      this.registerValue(this.xp);
      this.registerValue(this.shadowSize);
      this.registerValue(this.lookAtPlayer);
      this.registerValue(this.lookAround);
      this.registerValue(this.wander);
      this.registerValue(this.alwaysWander);
      this.registerValue(this.canFly);
      this.registerValue(this.flightMaxHeight);
      this.registerValue(this.flightMinHeight);
      this.registerValue(this.flee);
      this.registerValue(this.canPickUpLoot);
      this.registerValue(this.hasNoGravity);
      this.registerValue(this.canBeSteered);
      this.registerValue(this.respawn);
      this.registerValue(this.respawnDelay);
      this.registerValue(this.respawnOnCoordinates);
      this.registerValue(this.respawnPosX);
      this.registerValue(this.respawnPosY);
      this.registerValue(this.respawnPosZ);
      this.registerValue(this.respawnSaveUUID);
   }

   private void registerValue(GenericValue value) {
      this.serializer.registerNBTValue(value.id, value, true);
      this.serealizableValues.put(value.id, value);
   }

   private String processPropertyName(String property) {
      return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, property);
   }

   public boolean edit(String property, String value) {
      Value parameter = (Value)this.serealizableValues.get(this.processPropertyName(property));
      if (parameter == null) {
         if (property.equals("post")) {
            String[] splits = value.split(" ");
            if (splits.length < 3) {
               return false;
            }

            int x = Integer.parseInt(splits[0]);
            int y = Integer.parseInt(splits[1]);
            int z = Integer.parseInt(splits[2]);
            this.postPosition = new class_2338(x, y, z);
         } else {
            if (!property.equals("morph")) {
               return false;
            }

            try {
               this.morph = MorphManager.INSTANCE.morphFromNBT(class_2522.method_10718(value));
            } catch (Exception var8) {
               return false;
            }
         }
      } else if (parameter instanceof ValueString) {
         ((ValueString)parameter).set(value);
      } else if (parameter instanceof ValueBoolean) {
         ((ValueBoolean)parameter).set(Boolean.parseBoolean(value));
      } else if (parameter instanceof ValueInt) {
         ((ValueInt)parameter).set(Integer.parseInt(value));
      } else if (parameter instanceof ValueFloat) {
         ((ValueFloat)parameter).set(Float.parseFloat(value));
      } else if (parameter instanceof ValueDouble) {
         ((ValueDouble)parameter).set(Double.parseDouble(value));
      }

      return true;
   }

   public class_2487 serializeNBT() {
      return this.partialSerializeNBT((List)null);
   }

   public class_2487 partialSerializeNBT(List<String> options) {
      boolean all = options == null;
      class_2487 tag = new class_2487();
      if (!all && options.isEmpty()) {
         return tag;
      } else {
         this.serializer.toNBT(tag);
         if (all || options.contains("states")) {
            tag.method_10566("States", this.states.serializeNBT());
         }

         if (all || options.contains("steering_offset")) {
            class_2499 offsets = new class_2499();

            for(int i = 0; i < this.steeringOffset.size(); ++i) {
               offsets.add(NBTUtils.blockPosTo((class_2338)this.steeringOffset.get(i)));
            }

            tag.method_10566("SteeringOffsets", offsets);
         }

         if (all || options.contains("post")) {
            tag.method_10566("Post", (class_2520)(this.postPosition == null ? new class_2499() : NBTUtils.blockPosTo(this.postPosition)));
         }

         if (all || options.contains("patrol")) {
            class_2499 points = new class_2499();
            class_2499 triggers = new class_2499();

            for(int i = 0; i < this.patrol.size(); ++i) {
               points.add(NBTUtils.blockPosTo((class_2338)this.patrol.get(i)));
            }

            for(int i = 0; i < this.patrolTriggers.size(); ++i) {
               triggers.add(((Trigger)this.patrolTriggers.get(i)).serializeNBT());
            }

            tag.method_10566("Patrol", points);
            tag.method_10566("PatrolTriggers", triggers);
         }

         if (all || options.contains("morph")) {
            tag.method_10566("Morph", this.morph == null ? new class_2487() : this.morph.toNBT());
         }

         if (all || options.contains("drops")) {
            class_2499 drops = new class_2499();

            for(NpcDrop drop : this.drops) {
               drops.add(drop.serializeNBT());
            }

            tag.method_10566("Drops", drops);
         }

         if (all || options.contains("trigger_died")) {
            tag.method_10566("TriggerDied", this.triggerDied.serializeNBT());
         }

         if (all || options.contains("trigger_damaged")) {
            tag.method_10566("TriggerDamaged", this.triggerDamaged.serializeNBT());
         }

         if (all || options.contains("trigger_interact")) {
            tag.method_10566("TriggerInteract", this.triggerInteract.serializeNBT());
         }

         if (all || options.contains("trigger_tick")) {
            tag.method_10566("TriggerTick", this.triggerTick.serializeNBT());
         }

         if (all || options.contains("trigger_target")) {
            tag.method_10566("TriggerTarget", this.triggerTarget.serializeNBT());
         }

         if (all || options.contains("trigger_initialize")) {
            tag.method_10566("TriggerInitialize", this.triggerInitialize.serializeNBT());
         }

         if (all || options.contains("trigger_respawn")) {
            tag.method_10566("TriggerRespawn", this.triggerRespawn.serializeNBT());
         }

         if (all || options.contains("trigger_entity_collision")) {
            tag.method_10566("TriggerEntityCollision", this.triggerEntityCollision.serializeNBT());
         }

         return tag;
      }
   }

   public void deserializeNBT(class_2487 tag) {
      this.serializer.fromNBT(tag);
      if (tag.method_10545("States")) {
         this.states.deserializeNBT(tag.method_10562("States"));
      }

      if (tag.method_10573("SteeringOffsets", 9)) {
         class_2499 offsets = tag.method_10554("SteeringOffsets", 9);
         this.steeringOffset.clear();

         for(int i = 0; i < offsets.size(); ++i) {
            class_2338 pos = NBTUtils.blockPosFrom(offsets.method_10534(i));
            if (pos != null) {
               this.steeringOffset.add(pos);
            }
         }
      }

      if (tag.method_10573("Post", 9)) {
         this.postPosition = NBTUtils.blockPosFrom(tag.method_10580("Post"));
      }

      if (tag.method_10573("Patrol", 9)) {
         class_2499 points = tag.method_10554("Patrol", 9);
         this.patrol.clear();

         for(int i = 0; i < points.size(); ++i) {
            class_2338 pos = NBTUtils.blockPosFrom(points.method_10534(i));
            if (pos != null) {
               this.patrol.add(pos);
            }
         }

         class_2499 triggers = tag.method_10554("PatrolTriggers", 10);
         this.patrolTriggers.clear();

         for(int i = 0; i < triggers.size(); ++i) {
            Trigger trigger = new Trigger();
            trigger.deserializeNBT(triggers.method_10602(i));
            this.patrolTriggers.add(trigger);
         }
      }

      if (tag.method_10573("Morph", 10)) {
         this.morph = MorphManager.INSTANCE.morphFromNBT(tag.method_10562("Morph"));
      }

      if (tag.method_10545("Drops")) {
         class_2499 drops = tag.method_10554("Drops", 10);
         this.drops.clear();

         for(int i = 0; i < drops.size(); ++i) {
            class_2487 tagDrop = drops.method_10602(i);
            NpcDrop drop = new NpcDrop();
            drop.deserializeNBT(tagDrop);
            if (!(drop.chance <= 0.0F) && !drop.stack.method_7960()) {
               this.drops.add(drop);
            }
         }
      }

      if (tag.method_10545("TriggerDied")) {
         this.triggerDied.deserializeNBT(tag.method_10562("TriggerDied"));
      }

      if (tag.method_10545("TriggerDamaged")) {
         this.triggerDamaged.deserializeNBT(tag.method_10562("TriggerDamaged"));
      }

      if (tag.method_10545("TriggerInteract")) {
         this.triggerInteract.deserializeNBT(tag.method_10562("TriggerInteract"));
      }

      if (tag.method_10545("TriggerTick")) {
         this.triggerTick.deserializeNBT(tag.method_10562("TriggerTick"));
      }

      if (tag.method_10545("TriggerTarget")) {
         this.triggerTarget.deserializeNBT(tag.method_10562("TriggerTarget"));
      }

      if (tag.method_10545("TriggerInitialize")) {
         this.triggerInitialize.deserializeNBT(tag.method_10562("TriggerInitialize"));
      }

      if (tag.method_10545("TriggerRespawn")) {
         this.triggerRespawn.deserializeNBT(tag.method_10562("TriggerRespawn"));
      }

      if (tag.method_10545("TriggerEntityCollision")) {
         this.triggerEntityCollision.deserializeNBT(tag.method_10562("TriggerEntityCollision"));
      }

   }

   public void writeToBuf(ByteBuf buf) {
      buf.writeFloat((Float)this.shadowSize.get());
      buf.writeFloat((Float)this.jumpPower.get());
      NpcStateUtils.stateToBuf(buf, this);
   }

   public void readFromBuf(ByteBuf buf) {
      this.shadowSize.set(buf.readFloat());
      this.jumpPower.set(buf.readFloat());
      NpcStateUtils.stateFromBuf(buf);
   }
}
