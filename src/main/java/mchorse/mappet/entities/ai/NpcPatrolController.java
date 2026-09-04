package mchorse.mappet.entities.ai;

import mchorse.mappet.api.npcs.NpcState;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.entities.EntityNpc;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.class_1408;
import net.minecraft.class_2338;
import net.minecraft.class_243;

public class NpcPatrolController {
   private int index;
   private int direction = 1;
   private int moveCooldown;
   private boolean arrived;
   private int routeHash;

   public void tick(EntityNpc npc) {
      if (npc.method_37908().field_9236) {
         return;
      }

      NpcState state = npc.getState();
      int currentHash = this.getRouteHash(state);
      if (currentHash != this.routeHash) {
         this.routeHash = currentHash;
         this.index = 0;
         this.direction = 1;
         this.moveCooldown = 0;
         this.arrived = false;
      }

      if (state.patrol.isEmpty() || (Boolean)state.immovable.get()) {
         this.stop(npc);
         return;
      }

      if (!((String)state.follow.get()).isEmpty() || (Boolean)state.hasPost.get() && state.postPosition != null || npc.method_5968() != null) {
         return;
      }

      this.clampIndex(state);
      class_2338 point = (class_2338)state.patrol.get(this.index);
      class_243 target = class_243.method_24953(point);
      double distance = npc.method_5707(target);
      if (distance <= 2.0D) {
         if (!this.arrived) {
            this.arrived = true;
            this.trigger(npc, state);
         }

         this.advance(state);
         point = (class_2338)state.patrol.get(this.index);
         target = class_243.method_24953(point);
         distance = npc.method_5707(target);
      } else {
         this.arrived = false;
      }

      npc.method_5988().method_6230(target.field_1352, target.field_1351 + npc.method_17682(), target.field_1350, 10.0F, (float)npc.method_5978());
      if (this.moveCooldown > 0) {
         --this.moveCooldown;
         return;
      }

      class_1408 navigation = npc.method_5942();
      navigation.method_6337(target.field_1352, target.field_1351, target.field_1350, (Float)state.speed.get());
      this.moveCooldown = 5;
   }

   private void advance(NpcState state) {
      int count = state.patrol.size();
      if (count <= 1) {
         this.index = 0;
         this.arrived = true;
         return;
      }

      int next = this.index + this.direction;
      if ((Boolean)state.patrolCirculate.get()) {
         this.index = MathUtils.cycler(next, 0, count - 1);
      } else {
         if (next < 0 || next >= count) {
            this.direction *= -1;
         }

         this.index += this.direction;
         this.index = Math.max(0, Math.min(count - 1, this.index));
      }

      this.arrived = false;
      this.moveCooldown = 0;
   }

   private void trigger(EntityNpc npc, NpcState state) {
      if (this.index < 0 || this.index >= state.patrolTriggers.size()) {
         return;
      }

      Trigger trigger = (Trigger)state.patrolTriggers.get(this.index);
      DataContext context = (new DataContext(npc)).set("last", this.index == state.patrol.size() - 1 ? 1 : 0).set("index", this.index).set("count", state.patrol.size());
      trigger.trigger(context);
   }

   private void clampIndex(NpcState state) {
      if (this.index < 0 || this.index >= state.patrol.size()) {
         this.index = 0;
         this.direction = 1;
         this.arrived = false;
      }

   }

   private int getRouteHash(NpcState state) {
      int hash = 1;

      for(Object object : state.patrol) {
         class_2338 point = (class_2338)object;
         hash = 31 * hash + point.hashCode();
      }

      hash = 31 * hash + ((Boolean)state.patrolCirculate.get() ? 1 : 0);
      return hash;
   }

   private void stop(EntityNpc npc) {
      npc.method_5942().method_6340();
      this.moveCooldown = 0;
      this.arrived = false;
   }
}
