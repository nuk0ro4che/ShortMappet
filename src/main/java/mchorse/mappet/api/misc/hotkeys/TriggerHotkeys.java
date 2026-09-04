package mchorse.mappet.api.misc.hotkeys;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.compat.INBTSerializable;
import net.minecraft.class_1657;
import net.minecraft.class_2487;
import net.minecraft.class_2499;

public class TriggerHotkeys implements INBTSerializable<class_2487> {
   public List<TriggerHotkey> hotkeys = new ArrayList();

   public void execute(class_1657 player, int input, int keycode, boolean down) {
      for(TriggerHotkey hotkey : this.hotkeys) {
         if (hotkey.input == input && hotkey.keycode == keycode) {
            DataContext context = (new DataContext(player)).set("key", (double)keycode).set("down", down ? 1.0D : 0.0D).set("input", (double)input).set("mouse", input == TriggerHotkey.INPUT_MOUSE ? 1.0D : 0.0D);
            hotkey.execute(context);
            break;
         }
      }

   }

   public void execute(class_1657 player, int keycode, boolean down) {
      this.execute(player, TriggerHotkey.INPUT_KEYBOARD, keycode, down);
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      class_2499 hotkeys = new class_2499();

      for(TriggerHotkey hotkey : this.hotkeys) {
         hotkeys.add(hotkey.serializeNBT());
      }

      tag.method_10566("Hotkeys", hotkeys);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      this.hotkeys.clear();
      if (tag.method_10573("Hotkeys", 9)) {
         class_2499 hotkeys = tag.method_10554("Hotkeys", 10);

         for(int i = 0; i < hotkeys.size(); ++i) {
            TriggerHotkey hotkey = new TriggerHotkey();
            hotkey.deserializeNBT(hotkeys.method_10602(i));
            this.hotkeys.add(hotkey);
         }
      }

   }
}
