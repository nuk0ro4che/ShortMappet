package mchorse.mappet.common;

import java.util.HashMap;
import java.util.LinkedHashMap;
import mchorse.mappet.api.triggers.Trigger;
import net.minecraft.class_2487;

public class ScriptedItemProps {
   private HashMap<Trigger, String> initialState = new HashMap();
   public HashMap<String, Trigger> registered = new LinkedHashMap();
   public Trigger interactWithAir = new Trigger();
   public Trigger interactWithEntity = new Trigger();
   public Trigger interactWithBlock = new Trigger();
   public Trigger attackEntity = new Trigger();
   public Trigger breakBlock = new Trigger();
   public Trigger placeBlock = new Trigger();
   public Trigger hitBlock = new Trigger();
   public Trigger onHolderTick = new Trigger();
   public Trigger pickup = new Trigger();
   public Trigger toss = new Trigger();
   public Trigger firstPickup = new Trigger();
   public boolean pickedUp = false;
   public Trigger startedHolding = new Trigger();
   public Trigger stoppedHolding = new Trigger();
   public Trigger useStart = new Trigger();
   public Trigger useStop = new Trigger();
   public Trigger onUseTick = new Trigger();
   public Trigger finishedUsing = new Trigger();

   public ScriptedItemProps() {
      this.reset();
   }

   public ScriptedItemProps(class_2487 tag) {
      this.reset();
      this.fromNBT(tag);
   }

   public void reset() {
      this.interactWithAir = this.register("interact_with_air", new Trigger());
      this.interactWithEntity = this.register("interact_with_entity", new Trigger());
      this.interactWithBlock = this.register("interact_with_block", new Trigger());
      this.attackEntity = this.register("attack_entity", new Trigger());
      this.breakBlock = this.register("break_block", new Trigger());
      this.placeBlock = this.register("place_block", new Trigger());
      this.hitBlock = this.register("hit_block", new Trigger());
      this.onHolderTick = this.register("on_holder_tick", new Trigger());
      this.pickup = this.register("pickup", new Trigger());
      this.toss = this.register("toss", new Trigger());
      this.firstPickup = this.register("first_pickup", new Trigger());
      this.pickedUp = false;
      this.startedHolding = this.register("started_holding", new Trigger());
      this.stoppedHolding = this.register("stopped_holding", new Trigger());
      this.useStart = this.register("use_start", new Trigger());
      this.useStop = this.register("use_stop", new Trigger());
      this.onUseTick = this.register("on_use_tick", new Trigger());
      this.finishedUsing = this.register("finished_using", new Trigger());
   }

   public Trigger register(String key, Trigger trigger) {
      this.registered.put(key, trigger);
      return trigger;
   }

   public void fromNBT(class_2487 tag) {
      this.reset();
      if (tag.method_10545("InteractWithAir")) {
         this.interactWithAir.deserializeNBT(tag.method_10562("InteractWithAir"));
      }

      if (tag.method_10545("InteractWithEntity")) {
         this.interactWithEntity.deserializeNBT(tag.method_10562("InteractWithEntity"));
      }

      if (tag.method_10545("InteractWithBlock")) {
         this.interactWithBlock.deserializeNBT(tag.method_10562("InteractWithBlock"));
      }

      if (tag.method_10545("AttackEntity")) {
         this.attackEntity.deserializeNBT(tag.method_10562("AttackEntity"));
      }

      if (tag.method_10545("BreakBlock")) {
         this.breakBlock.deserializeNBT(tag.method_10562("BreakBlock"));
      }

      if (tag.method_10545("PlaceBlock")) {
         this.placeBlock.deserializeNBT(tag.method_10562("PlaceBlock"));
      }

      if (tag.method_10545("HitBlock")) {
         this.hitBlock.deserializeNBT(tag.method_10562("HitBlock"));
      }

      if (tag.method_10545("OnHolderTick")) {
         this.onHolderTick.deserializeNBT(tag.method_10562("OnHolderTick"));
      }

      if (tag.method_10545("Pickup")) {
         this.pickup.deserializeNBT(tag.method_10562("Pickup"));
      }

      if (tag.method_10545("Toss")) {
         this.toss.deserializeNBT(tag.method_10562("Toss"));
      }

      if (tag.method_10545("FirstPickup")) {
         this.firstPickup.deserializeNBT(tag.method_10562("FirstPickup"));
      }

      if (tag.method_10545("PickedUp")) {
         this.pickedUp = tag.method_10577("PickedUp");
      }

      if (tag.method_10545("StartedHolding")) {
         this.startedHolding.deserializeNBT(tag.method_10562("StartedHolding"));
      }

      if (tag.method_10545("StoppedHolding")) {
         this.stoppedHolding.deserializeNBT(tag.method_10562("StoppedHolding"));
      }

      if (tag.method_10545("UseStart")) {
         this.useStart.deserializeNBT(tag.method_10562("UseStart"));
      }

      if (tag.method_10545("UseStop")) {
         this.useStop.deserializeNBT(tag.method_10562("UseStop"));
      }

      if (tag.method_10545("OnUseTick")) {
         this.onUseTick.deserializeNBT(tag.method_10562("OnUseTick"));
      }

      if (tag.method_10545("FinishedUsing")) {
         this.finishedUsing.deserializeNBT(tag.method_10562("FinishedUsing"));
      }

   }

   public class_2487 toNBT() {
      class_2487 tag = new class_2487();
      tag.method_10566("InteractWithAir", this.interactWithAir.serializeNBT());
      tag.method_10566("InteractWithEntity", this.interactWithEntity.serializeNBT());
      tag.method_10566("InteractWithBlock", this.interactWithBlock.serializeNBT());
      tag.method_10566("AttackEntity", this.attackEntity.serializeNBT());
      tag.method_10566("BreakBlock", this.breakBlock.serializeNBT());
      tag.method_10566("PlaceBlock", this.placeBlock.serializeNBT());
      tag.method_10566("HitBlock", this.hitBlock.serializeNBT());
      tag.method_10566("OnHolderTick", this.onHolderTick.serializeNBT());
      tag.method_10566("Pickup", this.pickup.serializeNBT());
      tag.method_10566("Toss", this.toss.serializeNBT());
      tag.method_10566("FirstPickup", this.firstPickup.serializeNBT());
      tag.method_10556("PickedUp", this.pickedUp);
      tag.method_10566("StartedHolding", this.startedHolding.serializeNBT());
      tag.method_10566("StoppedHolding", this.stoppedHolding.serializeNBT());
      tag.method_10566("UseStart", this.useStart.serializeNBT());
      tag.method_10566("UseStop", this.useStop.serializeNBT());
      tag.method_10566("OnUseTick", this.onUseTick.serializeNBT());
      tag.method_10566("FinishedUsing", this.finishedUsing.serializeNBT());
      return tag;
   }
}
