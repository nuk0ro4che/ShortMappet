package mchorse.mappet.api.ui.components;

import java.util.function.Consumer;
import mchorse.mappet.api.scripts.user.items.IScriptItemStack;
import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mappet.client.gui.utils.GuiUserInterfaceSlotElement;
import mchorse.mappet.compat.NbtCompat;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1799;
import net.minecraft.class_2487;
import net.minecraft.class_310;

public class UIStackComponent extends UIComponent {
   public class_1799 stack;

   public UIStackComponent() {
      this.stack = class_1799.field_8037;
   }

   public UIStackComponent stack(IScriptItemStack stack) {
      return this.stack(stack == null ? null : stack.getMinecraftItemStack());
   }

   public UIStackComponent stack(class_1799 stack) {
      this.change(new String[]{"Stack"});
      this.stack = stack == null ? class_1799.field_8037 : stack.method_7972();
      return this;
   }

   @DiscardMethod
   protected int getDefaultUpdateDelay() {
      return 200;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected void applyProperty(UIContext context, String key, GuiElement element) {
      super.applyProperty(context, key, element);
      if (key.equals("Stack")) {
         ((GuiSlotElement)element).setStack(this.stack);
      }

   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public GuiElement create(class_310 mc, UIContext context) {
      GuiSlotElement element = new GuiUserInterfaceSlotElement(mc, 0, (Consumer)null);
      element.callback = this.id.isEmpty() ? null : (stack) -> {
         context.data.method_10566(this.id, NbtCompat.write(stack));
         context.data.method_10569(this.id + ".slot", element.lastSlot);
         context.dirty(this.id, (long)this.updateDelay);
      };
      element.setStack(this.stack);
      element.drawDisabled = false;
      return this.apply(element, context);
   }

   @DiscardMethod
   public void populateData(class_2487 tag) {
      super.populateData(tag);
      if (!this.id.isEmpty()) {
         tag.method_10566(this.id, NbtCompat.write(this.stack));
      }

   }

   @DiscardMethod
   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10566("Stack", NbtCompat.write(this.stack));
   }

   @DiscardMethod
   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Stack")) {
         this.stack = class_1799.method_7915(tag.method_10562("Stack"));
      }

   }
}
