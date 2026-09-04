package mchorse.mappet.api.ui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_310;

public class UIStringListComponent extends UIComponent {
   public List<String> values = new ArrayList();
   public Integer selected;
   public Integer background;

   public UIStringListComponent values(String... values) {
      this.change(new String[]{"Values"});
      this.values.clear();
      this.values.addAll(Arrays.asList(values));
      return this;
   }

   public UIStringListComponent values(List<String> values) {
      this.change(new String[]{"Values"});
      this.values.clear();
      this.values.addAll(values);
      return this;
   }

   public UIStringListComponent setValues(List<String> values) {
      return this.values(values);
   }

   public List<String> getValues() {
      return this.values;
   }

   public UIStringListComponent selected(int selected) {
      this.change(new String[]{"Selected"});
      this.selected = selected;
      return this;
   }

   public UIStringListComponent background() {
      return this.background(-2013265920);
   }

   public UIStringListComponent background(int background) {
      this.change(new String[]{"Background"});
      this.background = background;
      return this;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected void applyProperty(UIContext context, String key, GuiElement element) {
      super.applyProperty(context, key, element);
      GuiStringListElement list = (GuiStringListElement)element;
      if (key.equals("Values")) {
         list.clear();
         list.add(this.values);
      } else if (key.equals("Selected") && this.selected != null) {
         list.setIndex(this.selected);
      } else if (key.equals("Background") && this.background != null) {
         list.background(this.background);
      }

   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public GuiElement create(class_310 mc, UIContext context) {
      GuiStringListElement element = new GuiStringListElement(mc, (Consumer)null);
      element.callback = (v) -> {
         if (!this.id.isEmpty()) {
            context.data.method_10582(this.id, (String)v.get(0));
            context.data.method_10569(this.id + ".index", element.getIndex());
            context.dirty(this.id, (long)this.updateDelay);
         }

      };
      element.add(this.values);
      if (this.selected != null) {
         element.setIndex(this.selected);
      }

      if (this.background != null) {
         element.background(this.background);
      }

      return this.apply(element, context);
   }

   @DiscardMethod
   public void populateData(class_2487 tag) {
      super.populateData(tag);
      if (!this.id.isEmpty()) {
         String value = "";
         int index = 0;
         if (this.selected != null && this.selected >= 0 && this.selected < this.values.size()) {
            value = (String)this.values.get(this.selected);
            index = this.selected;
         }

         tag.method_10569(this.id + ".index", index);
         tag.method_10582(this.id, value);
      }

   }

   @DiscardMethod
   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      class_2499 list = new class_2499();

      for(String value : this.values) {
         list.add(class_2519.method_23256(value));
      }

      if (list.size() > 0 || this.changedProperties.contains("Values")) {
         tag.method_10566("Values", list);
      }

      if (this.selected != null) {
         tag.method_10569("Selected", this.selected);
      }

      if (this.background != null) {
         tag.method_10569("Background", this.background);
      }

   }

   @DiscardMethod
   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.values.clear();
      if (tag.method_10545("Values")) {
         class_2499 list = tag.method_10554("Values", 8);
         int i = 0;

         for(int c = list.size(); i < c; ++i) {
            this.values.add(list.method_10608(i));
         }
      }

      if (tag.method_10545("Selected")) {
         this.selected = tag.method_10550("Selected");
      }

      if (tag.method_10545("Background")) {
         this.background = tag.method_10550("Background");
      }

   }
}
