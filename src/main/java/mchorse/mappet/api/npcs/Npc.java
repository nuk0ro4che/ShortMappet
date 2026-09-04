package mchorse.mappet.api.npcs;

import java.util.HashMap;
import java.util.Map;
import mchorse.mappet.api.utils.AbstractData;
import net.minecraft.class_2487;

public class Npc extends AbstractData {
   public Map<String, NpcState> states = new HashMap();

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      class_2487 states = new class_2487();

      for(Map.Entry<String, NpcState> entry : this.states.entrySet()) {
         class_2487 state = ((NpcState)entry.getValue()).serializeNBT();
         if (state.method_10546() > 0) {
            states.method_10566((String)entry.getKey(), state);
         }
      }

      if (states.method_10546() > 0) {
         tag.method_10566("States", states);
      }

      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10573("States", 10)) {
         class_2487 states = tag.method_10562("States");

         for(String key : states.method_10541()) {
            NpcState state = new NpcState();
            state.deserializeNBT(states.method_10562(key));
            this.states.put(key, state);
         }
      }

   }
}
