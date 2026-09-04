package mchorse.mappet.api.ui.components;

import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_310;

public class UIToggleComponent extends UILabelBaseComponent {
   public boolean state;

   public UIToggleComponent state(boolean state) {
      this.change(new String[]{"State"});
      this.state = state;
      return this;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected void applyProperty(UIContext context, String key, GuiElement element) {
      super.applyProperty(context, key, element);
      GuiToggleElement toggle = (GuiToggleElement)element;
      if (key.equals("Label")) {
         toggle.label = IKey.str(this.getLabel());
      } else if (key.equals("State")) {
         toggle.toggled(this.state);
      }

   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public GuiElement create(class_310 mc, UIContext context) {
      GuiToggleElement toggle = new GuiToggleElement(mc, IKey.str(this.getLabel()), (b) -> {
         if (!this.id.isEmpty()) {
            context.data.method_10556(this.id, b.isToggled());
            context.dirty(this.id, (long)this.updateDelay);
         }

      });
      toggle.toggled(this.state);
      return this.apply(toggle, context);
   }

   @DiscardMethod
   public void populateData(class_2487 tag) {
      super.populateData(tag);
      if (!this.id.isEmpty()) {
         tag.method_10556(this.id, this.state);
      }

   }

   @DiscardMethod
   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10556("State", this.state);
   }

   @DiscardMethod
   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("State")) {
         this.state = tag.method_10577("State");
      }

   }
}
