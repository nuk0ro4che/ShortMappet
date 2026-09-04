package mchorse.mappet.api.scripts.code.entities;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.npcs.Npc;
import mchorse.mappet.api.npcs.NpcState;
import mchorse.mappet.api.scripts.Script;
import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.api.triggers.blocks.ScriptTriggerBlock;
import mchorse.mappet.api.scripts.user.entities.IScriptNpc;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.entities.EntityNpc;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.class_2338;

public class ScriptNpc extends ScriptEntity<EntityNpc> implements IScriptNpc {
   public ScriptNpc(EntityNpc entity) {
      super(entity);
   }

   public EntityNpc getMappetNpc() {
      return this.entity;
   }

   public String getNpcId() {
      return ((EntityNpc)this.entity).getNpcId();
   }

   public boolean setMorph(AbstractMorph morph) {
      ((EntityNpc)this.entity).getState().morph = MorphUtils.copy(morph);
      ((EntityNpc)this.entity).setMorph(((EntityNpc)this.entity).getState().morph);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
      return true;
   }

   public String getNpcState() {
      return (String)((EntityNpc)this.entity).getState().stateName.get();
   }

   public void setNpcState(String stateId) {
      String npcId = ((EntityNpc)this.entity).getNpcId();
      Npc npc = (Npc)Mappet.npcs.load(npcId);
      NpcState state = npc == null ? null : (NpcState)npc.states.get(stateId);
      if (npc != null && state == null && npc.states.containsKey("default")) {
         state = (NpcState)npc.states.get("default");
      }

      if (state != null) {
         ((EntityNpc)this.entity).setNpc(npc, state);
         if (!npc.serializeNBT().method_10558("StateName").equals("default")) {
            ((EntityNpc)this.entity).setStringInData("StateName", stateId);
         }
      }

      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public void canPickUpLoot(boolean canPickUpLoot) {
      ((EntityNpc)this.entity).method_5952(canPickUpLoot);
   }

   public void follow(String target) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.follow.set(target);
      ((EntityNpc)this.entity).setState(state, false);
   }

   public String getFaction() {
      return (String)((EntityNpc)this.entity).getState().faction.get();
   }

   public void setCanBeSteered(boolean enabled) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.canBeSteered.set(enabled);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public boolean canBeSteered() {
      return (Boolean)((EntityNpc)this.entity).getState().canBeSteered.get();
   }

   public void setSteeringOffset(int index, float x, float y, float z) {
      NpcState state = ((EntityNpc)this.entity).getState();
      if (index >= 0 && index < state.steeringOffset.size()) {
         state.steeringOffset.set(index, class_2338.method_49637((double)x, (double)y, (double)z));
         ((EntityNpc)this.entity).sendNpcStateChangePacket();
      } else {
         throw new IndexOutOfBoundsException("Invalid index: " + index);
      }
   }

   public void addSteeringOffset(float x, float y, float z) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.steeringOffset.add(class_2338.method_49637((double)x, (double)y, (double)z));
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public List<ScriptVector> getSteeringOffsets() {
      NpcState state = ((EntityNpc)this.entity).getState();
      List<ScriptVector> steeringOffsets = new ArrayList();

      for(class_2338 pos : state.steeringOffset) {
         steeringOffsets.add(new ScriptVector((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260()));
      }

      return steeringOffsets;
   }

   public void setNpcSpeed(float speed) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.speed.set(speed);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public float getNpcSpeed() {
      return (Float)((EntityNpc)this.entity).getState().speed.get();
   }

   public void setJumpPower(float jumpHeight) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.jumpPower.set(jumpHeight);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public float getJumpPower() {
      return (Float)((EntityNpc)this.entity).getState().jumpPower.get();
   }

   public void setInvincible(boolean invincible) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.invincible.set(invincible);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public boolean isInvincible() {
      return (Boolean)((EntityNpc)this.entity).getState().invincible.get();
   }

   public void setCanSwim(boolean canSwim) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.canSwim.set(canSwim);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public boolean canSwim() {
      return (Boolean)((EntityNpc)this.entity).getState().canSwim.get();
   }

   public void setImmovable(boolean immovable) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.immovable.set(immovable);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public boolean isImmovable() {
      return (Boolean)((EntityNpc)this.entity).getState().immovable.get();
   }

   public void setShadowSize(float size) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.shadowSize.set(size);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public float getShadowSize() {
      return (Float)((EntityNpc)this.entity).getState().shadowSize.get();
   }

   public float setXpValue(int xp) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.xp.set(xp);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
      return (float)xp;
   }

   public int getXpValue() {
      return (Integer)((EntityNpc)this.entity).getState().xp.get();
   }

   public float getPathDistance() {
      NpcState state = ((EntityNpc)this.entity).getState();
      return (Float)state.pathDistance.get();
   }

   public void setPathDistance(float sightRadius) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.pathDistance.set(sightRadius);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public void setAttackRange(float sightDistance) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.sightDistance.set(sightDistance);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public float getAttackRange() {
      return (Float)((EntityNpc)this.entity).getState().sightDistance.get();
   }

   public void setKillable(boolean killable) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.killable.set(killable);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public boolean isKillable() {
      return (Boolean)((EntityNpc)this.entity).getState().killable.get();
   }

   public boolean canGetBurned() {
      return (Boolean)((EntityNpc)this.entity).getState().canGetBurned.get();
   }

   public void canGetBurned(boolean canGetBurned) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.canGetBurned.set(canGetBurned);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public boolean canFallDamage() {
      return (Boolean)((EntityNpc)this.entity).getState().canFallDamage.get();
   }

   public void canFallDamage(boolean canFallDamage) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.canFallDamage.set(canFallDamage);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public float getDamage() {
      return (Float)((EntityNpc)this.entity).getState().damage.get();
   }

   public void setDamage(float damage) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.damage.set(damage);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public int getDamageDelay() {
      return (Integer)((EntityNpc)this.entity).getState().damageDelay.get();
   }

   public void setDamageDelay(int damageDelay) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.damageDelay.set(damageDelay);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public boolean doesWander() {
      return (Boolean)((EntityNpc)this.entity).getState().wander.get();
   }

   public void setWander(boolean wander) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.wander.set(wander);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public boolean doesLookAround() {
      return (Boolean)((EntityNpc)this.entity).getState().lookAround.get();
   }

   public void setLookAround(boolean lookAround) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.lookAround.set(lookAround);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public boolean doesLookAtPlayer() {
      return (Boolean)((EntityNpc)this.entity).getState().lookAtPlayer.get();
   }

   public void setLookAtPlayer(boolean lookAtPlayer) {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.lookAtPlayer.set(lookAtPlayer);
      ((EntityNpc)this.entity).sendNpcStateChangePacket();
   }

   public void clearPatrolPoints() {
      NpcState state = ((EntityNpc)this.entity).getState();
      state.patrol.clear();
      state.patrolTriggers.clear();
      ((EntityNpc)this.entity).setState(state, false);
   }

   public void removePatrolPoint(int index) {
      NpcState state = ((EntityNpc)this.entity).getState();
      this.normalizePatrolTriggers(state);
      if (index >= 0 && index < state.patrol.size()) {
         state.patrol.remove(index);
         state.patrolTriggers.remove(index);
      }

      ((EntityNpc)this.entity).setState(state, false);
   }

   public void removePatrolPoint(int x, int y, int z) {
      NpcState state = ((EntityNpc)this.entity).getState();
      this.normalizePatrolTriggers(state);

      for(int i = state.patrol.size() - 1; i >= 0; --i) {
         class_2338 point = (class_2338)state.patrol.get(i);
         if (point.method_10263() == x && point.method_10264() == y && point.method_10260() == z) {
            state.patrol.remove(i);
            state.patrolTriggers.remove(i);
         }
      }

      ((EntityNpc)this.entity).setState(state, false);
   }

   public void addPatrol(int x, int y, int z) {
      this.addPatrol(x, y, z, null);
   }

   public void addPatrol(int x, int y, int z, String function) {
      NpcState state = ((EntityNpc)this.entity).getState();
      this.normalizePatrolTriggers(state);
      state.patrol.add(new class_2338(x, y, z));
      state.patrolTriggers.add(this.createPatrolTrigger(function));
      ((EntityNpc)this.entity).setState(state, true);
   }

   public void addPatrol(ScriptVector position) {
      this.addPatrol(position, null);
   }

   public void addPatrol(ScriptVector position, String function) {
      if (position != null) {
         this.addPatrol((int)position.x, (int)position.y, (int)position.z, function);
      }
   }

   public void setPatrol(int index, int x, int y, int z) {
      this.setPatrol(index, x, y, z, null);
   }

   public void setPatrol(int index, int x, int y, int z, String function) {
      NpcState state = ((EntityNpc)this.entity).getState();
      this.normalizePatrolTriggers(state);
      if (index >= 0 && index < state.patrol.size()) {
         state.patrol.set(index, new class_2338(x, y, z));
         if (function != null && !function.trim().isEmpty()) {
            state.patrolTriggers.set(index, this.createPatrolTrigger(function));
         }
         ((EntityNpc)this.entity).setState(state, true);
      }
   }

   public void setPatrol(int index, ScriptVector position) {
      this.setPatrol(index, position, null);
   }

   public void setPatrol(int index, ScriptVector position, String function) {
      if (position != null) {
         this.setPatrol(index, (int)position.x, (int)position.y, (int)position.z, function);
      }
   }

   private Trigger createPatrolTrigger(String function) {
      Trigger trigger = new Trigger();
      if (function != null && !function.trim().isEmpty()) {
         String script = (String)Script.CURRENT_SCRIPT.get();
         if (script != null && !script.isEmpty()) {
            trigger.blocks.add(new ScriptTriggerBlock(script, function.trim()));
            trigger.recalculateEmpty();
         }
      }
      return trigger;
   }

   private void normalizePatrolTriggers(NpcState state) {
      while(state.patrolTriggers.size() < state.patrol.size()) {
         state.patrolTriggers.add(new Trigger());
      }

      while(state.patrolTriggers.size() > state.patrol.size()) {
         state.patrolTriggers.remove(state.patrolTriggers.size() - 1);
      }
   }
}
