package mchorse.mappet.client.gui.utils;

import java.util.List;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_2338;
import net.minecraft.class_310;

public class GuiBlockPosAndTriggerList extends GuiElement {
   private List<class_2338> posList;
   private List<Trigger> triggerList;

   public GuiBlockPosAndTriggerList(class_310 mc) {
      super(mc);
      this.flex().column(5).stretch().vertical();
   }

   public void addBlockPos() {
      class_2338 pos = this.mc.field_1724.method_24515();
      Trigger trigger = new Trigger();
      this.posList.add(pos);
      this.triggerList.add(trigger);
      this.add(this.create(pos, trigger));
      this.getParentContainer().resize();
   }

   public void set(List<class_2338> posList, List<Trigger> triggerList) {
      this.posList = posList;
      this.triggerList = triggerList;
      this.removeAll();

      for(int i = 0; i < posList.size(); ++i) {
         class_2338 pos = (class_2338)posList.get(i);
         if (i >= triggerList.size()) {
            triggerList.add(new Trigger());
         }

         Trigger trigger = (Trigger)triggerList.get(i);
         GuiPatrolPointElement posElement = this.create(pos, trigger);
         this.add(posElement);
      }

      this.getParentContainer().resize();
   }

   private GuiPatrolPointElement create(class_2338 pos, Trigger trigger) {
      GuiPatrolPointElement element = new GuiPatrolPointElement(this.mc);
      element.position.set(pos);
      element.trigger.set(trigger);
      element.position.callback = (blockPos) -> this.posList.set(this.getChildren().indexOf(element), blockPos);
      element.position.context(() -> element.position.createDefaultContextMenu().action(Icons.REMOVE, IKey.lang("mappet.gui.block_pos.context.remove"), () -> this.removeBlock(element), 16711731));
      return element;
   }

   private void removeBlock(GuiPatrolPointElement element) {
      int index = this.getChildren().indexOf(element);
      this.remove(element);
      this.posList.remove(index);
      this.triggerList.remove(index);
      this.getParentContainer().resize();
   }
}
