package mchorse.mappet.api.triggers.blocks;

import mchorse.mappet.api.states.States;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.Target;
import mchorse.mappet.api.utils.TargetMode;
import mchorse.mappet.utils.EnumUtils;
import net.minecraft.class_2487;

public class StateTriggerBlock extends StringTriggerBlock {
   public Target target;
   public StateMode mode;
   public Object value;

   public StateTriggerBlock() {
      this.target = new Target(TargetMode.GLOBAL);
      this.mode = StateTriggerBlock.StateMode.SET;
      this.value = (double)0.0F;
   }

   public void trigger(DataContext context) {
      States states = this.target.getStates(context);
      if (states != null) {
         if (this.mode == StateTriggerBlock.StateMode.ADD && this.value instanceof Number) {
            states.add(this.string, ((Number)this.value).doubleValue());
         } else if (this.mode == StateTriggerBlock.StateMode.SET) {
            if (this.value instanceof Number) {
               states.setNumber(this.string, ((Number)this.value).doubleValue());
            } else if (this.value instanceof String) {
               states.setString(this.string, (String)this.value);
            }
         } else {
            states.resetMasked(this.string);
         }

      }
   }

   protected String getKey() {
      return "State";
   }

   protected void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10566("Target", this.target.serializeNBT());
      tag.method_10569("Mode", this.mode.ordinal());
      if (this.value instanceof Number) {
         tag.method_10549("Value", ((Number)this.value).doubleValue());
      } else if (this.value instanceof String) {
         tag.method_10582("Value", (String)this.value);
      }

   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.target.deserializeNBT(tag.method_10562("Target"));
      this.mode = (StateMode)EnumUtils.getValue(tag.method_10550("Mode"), StateTriggerBlock.StateMode.values(), StateTriggerBlock.StateMode.SET);
      if (tag.method_10573("Value", 99)) {
         this.value = tag.method_10574("Value");
      } else if (tag.method_10573("Value", 8)) {
         this.value = tag.method_10558("Value");
      }

   }

   public static enum StateMode {
      ADD,
      SET,
      REMOVE;
      private static StateMode[] $values() {
         return new StateMode[]{ADD, SET, REMOVE};
      }
   }
}
