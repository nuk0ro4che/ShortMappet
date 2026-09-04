package mchorse.mappet.client.gui.states;

import java.util.Comparator;
import mchorse.mappet.api.states.States;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import net.minecraft.class_1074;
import net.minecraft.class_310;

public class GuiStatesEditor extends GuiScrollElement {
   private States states;

   public GuiStatesEditor(class_310 mc) {
      super(mc);
      this.flex().column(5).vertical().stretch().scroll().padding(10);
   }

   public States get() {
      return this.states;
   }

   public GuiStatesEditor set(States states) {
      this.states = states;
      this.removeAll();
      if (states != null) {
         for(String key : states.values.keySet()) {
            this.add(new GuiState(this.mc, key, states));
         }
      }

      this.sortElements();
      this.resize();
      return this;
   }

   private void sortElements() {
      this.getChildren().sort(Comparator.comparing((a) -> ((GuiState)a).getKey()));
   }

   public void addNew() {
      if (this.states != null) {
         int index = this.states.values.size() + 1;

         String key;
         for(key = "state_" + index; this.states.values.containsKey(key); key = "state_" + index) {
            ++index;
         }

         this.states.values.put(key, 0);
         this.add(new GuiState(this.mc, key, this.states));
         this.sortElements();
         this.getParentContainer().resize();
      }
   }

   public void draw(GuiContext context) {
      super.draw(context);
      if (this.states != null && this.states.values.isEmpty()) {
         int w = this.area.w / 2;
         int x = this.area.mx(w);
         GuiDraw.drawMultiText(this.font, class_1074.method_4662("mappet.gui.states.empty", new Object[0]), x, this.area.my(), 16777215, w, 12, 0.5F, 0.5F);
      }

   }
}
