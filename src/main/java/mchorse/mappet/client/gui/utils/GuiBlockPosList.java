package mchorse.mappet.client.gui.utils;

import java.util.List;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_2338;
import net.minecraft.class_310;

public class GuiBlockPosList extends GuiElement {
   private List<class_2338> posList;

   public GuiBlockPosList(class_310 mc) {
      super(mc);
      this.flex().column(5).stretch().vertical();
   }

   public void addBlockPos() {
      this.posList.add(new class_2338(0, 0, 0));
      this.add(this.create());
      this.getParentContainer().resize();
   }

   public void set(List<class_2338> posList) {
      this.posList = posList;
      this.removeAll();

      for(class_2338 pos : posList) {
         GuiSteeringOffsetElement posElement = this.create();
         posElement.position.set(pos);
         this.add(posElement);
      }

      this.getParentContainer().resize();
   }

   private GuiSteeringOffsetElement create() {
      GuiSteeringOffsetElement element = new GuiSteeringOffsetElement(this.mc);
      element.position.callback = (blockPos) -> this.posList.set(this.getChildren().indexOf(element), blockPos);
      element.position.context(() -> element.position.createDefaultContextMenu(false).action(Icons.REMOVE, IKey.lang("mappet.gui.block_pos.context.remove"), () -> this.removeBlock(element), 16711731));
      return element;
   }

   private void removeBlock(GuiSteeringOffsetElement element) {
      int index = this.getChildren().indexOf(element);
      this.remove(element);
      this.posList.remove(index);
      this.getParentContainer().resize();
   }
}
