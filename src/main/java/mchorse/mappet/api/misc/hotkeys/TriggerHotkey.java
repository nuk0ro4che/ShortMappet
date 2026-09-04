package mchorse.mappet.api.misc.hotkeys;

import mchorse.mappet.api.conditions.Checker;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.compat.INBTSerializable;
import net.minecraft.class_2487;

public class TriggerHotkey implements INBTSerializable<class_2487> {
   public static final int INPUT_KEYBOARD = 0;
   public static final int INPUT_MOUSE = 1;
   public int keycode;
   public int input = INPUT_KEYBOARD;
   public boolean toggle;
   public Trigger trigger = new Trigger();
   public Checker enabled = new Checker(true);

   public TriggerHotkey() {
      this.trigger = new Trigger();
      this.enabled = new Checker(true);
   }

   public TriggerHotkey(int keycode, boolean toggle) {
      this(keycode, toggle, INPUT_KEYBOARD);
   }

   public TriggerHotkey(int keycode, boolean toggle, int input) {
      this.keycode = keycode;
      this.toggle = toggle;
      this.input = input == INPUT_MOUSE ? INPUT_MOUSE : INPUT_KEYBOARD;
   }

   public void execute(DataContext context) {
      if (this.isEnabled(context)) {
         this.trigger.trigger(context);
      }

   }

   private boolean isEnabled(DataContext context) {
      return this.enabled.check(context);
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10569("Keycode", this.keycode);
      tag.method_10569("Input", this.input);
      tag.method_10556("Toggle", this.toggle);
      tag.method_10566("Trigger", this.trigger.serializeNBT());
      tag.method_10566("Enabled", this.enabled.serializeNBT());
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      this.keycode = tag.method_10550("Keycode");
      this.input = tag.method_10573("Input", 3) && tag.method_10550("Input") == INPUT_MOUSE ? INPUT_MOUSE : INPUT_KEYBOARD;
      this.toggle = tag.method_10577("Toggle");
      this.trigger.deserializeNBT(tag.method_10562("Trigger"));
      this.enabled.deserializeNBT(tag.method_10580("Enabled"));
   }
}
