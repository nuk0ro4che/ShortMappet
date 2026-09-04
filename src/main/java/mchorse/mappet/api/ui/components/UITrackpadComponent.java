package mchorse.mappet.api.ui.components;

import java.util.function.Consumer;
import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_310;

public class UITrackpadComponent extends UIComponent {
   public Double value;
   public Double min;
   public Double max;
   public boolean integer;
   public Double normal;
   public Double weak;
   public Double strong;
   public Double increment;

   public UITrackpadComponent value(double value) {
      this.change(new String[]{"Value"});
      this.value = value;
      return this;
   }

   public UITrackpadComponent min(double min) {
      this.change(new String[]{"Min"});
      this.min = min;
      return this;
   }

   public UITrackpadComponent max(double max) {
      this.change(new String[]{"Max"});
      this.max = max;
      return this;
   }

   public UITrackpadComponent integer() {
      return this.integer(true);
   }

   public UITrackpadComponent integer(boolean integer) {
      this.change(new String[]{"Integer"});
      this.integer = integer;
      return this;
   }

   public UITrackpadComponent limit(double min, double max) {
      return this.min(min).max(max);
   }

   public UITrackpadComponent limit(double min, double max, boolean integer) {
      return this.min(min).max(max).integer(integer);
   }

   public UITrackpadComponent amplitudes(double normal) {
      return this.amplitudes(normal, normal / (double)5.0F, normal * (double)5.0F);
   }

   public UITrackpadComponent amplitudes(double normal, double weak, double strong) {
      this.change(new String[]{"Normal", "Weak", "Strong"});
      this.normal = normal;
      this.weak = weak;
      this.strong = strong;
      return this;
   }

   public UITrackpadComponent increment(double increment) {
      this.change(new String[]{"Increment"});
      this.increment = increment;
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
      GuiTrackpadElement trackpad = (GuiTrackpadElement)element;
      if (key.equals("Value") && this.value != null) {
         trackpad.setValue(this.value);
      } else if (key.equals("Min") && this.min != null) {
         trackpad.min = this.min;
      } else if (key.equals("Max") && this.max != null) {
         trackpad.max = this.max;
      } else if (key.equals("Integer")) {
         trackpad.integer = this.integer;
      } else if (key.equals("Normal") && this.normal != null) {
         trackpad.normal = this.normal;
      } else if (key.equals("Weak") && this.weak != null) {
         trackpad.weak = this.weak;
      } else if (key.equals("Strong") && this.strong != null) {
         trackpad.strong = this.strong;
      } else if (key.equals("Increment") && this.increment != null) {
         trackpad.increment = this.increment;
      }

   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public GuiElement create(class_310 mc, UIContext context) {
      GuiTrackpadElement element = new GuiTrackpadElement(mc, (Consumer)null);
      element.callback = (v) -> {
         if (!this.id.isEmpty()) {
            if (element.integer) {
               context.data.method_10569(this.id, v.intValue());
            } else {
               context.data.method_10549(this.id, v);
            }

            context.dirty(this.id, (long)this.updateDelay);
         }

      };
      if (this.value != null) {
         element.setValue(this.value);
      }

      if (this.min != null) {
         element.min = this.min;
      }

      if (this.max != null) {
         element.max = this.max;
      }

      element.integer = this.integer;
      if (this.normal != null) {
         element.normal = this.normal;
      }

      if (this.weak != null) {
         element.weak = this.weak;
      }

      if (this.strong != null) {
         element.strong = this.strong;
      }

      if (this.increment != null) {
         element.increment = this.increment;
      }

      return this.apply(element, context);
   }

   @DiscardMethod
   public void populateData(class_2487 tag) {
      super.populateData(tag);
      if (!this.id.isEmpty()) {
         if (this.integer) {
            tag.method_10569(this.id, this.value == null ? 0 : this.value.intValue());
         } else {
            tag.method_10549(this.id, this.value == null ? (double)0.0F : this.value);
         }
      }

   }

   @DiscardMethod
   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      if (this.value != null) {
         tag.method_10549("Value", this.value);
      }

      if (this.min != null) {
         tag.method_10549("Min", this.min);
      }

      if (this.max != null) {
         tag.method_10549("Max", this.max);
      }

      tag.method_10556("Integer", this.integer);
      if (this.normal != null) {
         tag.method_10549("Normal", this.normal);
      }

      if (this.weak != null) {
         tag.method_10549("Weak", this.weak);
      }

      if (this.strong != null) {
         tag.method_10549("Strong", this.strong);
      }

      if (this.increment != null) {
         tag.method_10549("Increment", this.increment);
      }

   }

   @DiscardMethod
   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Value")) {
         this.value = tag.method_10574("Value");
      }

      if (tag.method_10545("Min")) {
         this.min = tag.method_10574("Min");
      }

      if (tag.method_10545("Max")) {
         this.max = tag.method_10574("Max");
      }

      if (tag.method_10545("Integer")) {
         this.integer = tag.method_10577("Integer");
      }

      if (tag.method_10545("Normal")) {
         this.normal = tag.method_10574("Normal");
      }

      if (tag.method_10545("Weak")) {
         this.weak = tag.method_10574("Weak");
      }

      if (tag.method_10545("Strong")) {
         this.strong = tag.method_10574("Strong");
      }

      if (tag.method_10545("Increment")) {
         this.increment = tag.method_10574("Increment");
      }

   }
}
