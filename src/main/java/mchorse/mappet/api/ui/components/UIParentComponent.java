package mchorse.mappet.api.ui.components;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import net.minecraft.class_2487;
import net.minecraft.class_2499;

public abstract class UIParentComponent extends UIComponent {
   public List<UIComponent> children = new ArrayList();

   @DiscardMethod
   public List<UIComponent> getChildComponents() {
      return this.children;
   }

   @DiscardMethod
   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      class_2499 list = new class_2499();

      for(UIComponent component : this.children) {
         class_2487 componentTag = component.serializeNBT();
         componentTag.method_10582("Type", CommonProxy.getUiComponents().getType(component));
         list.add(componentTag);
      }

      tag.method_10566("Components", list);
   }

   @DiscardMethod
   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      class_2499 list = tag.method_10554("Components", 10);
      int i = 0;

      for(int c = list.size(); i < c; ++i) {
         class_2487 componentTag = list.method_10602(i);
         UIComponent component = (UIComponent)CommonProxy.getUiComponents().create(componentTag.method_10558("Type"));
         if (component != null) {
            component.deserializeNBT(componentTag);
            this.children.add(component);
         }
      }

   }
}
