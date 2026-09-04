package mchorse.mappet.api.scripts.user.mappet;

import java.util.List;
import mchorse.mappet.api.scripts.user.items.IScriptItemStack;
import mchorse.mappet.api.ui.components.UIButtonComponent;
import mchorse.mappet.api.ui.components.UIClickComponent;
import mchorse.mappet.api.ui.components.UIColorComponent;
import mchorse.mappet.api.ui.components.UIComponent;
import mchorse.mappet.api.ui.components.UIGraphicsComponent;
import mchorse.mappet.api.ui.components.UIIconComponent;
import mchorse.mappet.api.ui.components.UILabelComponent;
import mchorse.mappet.api.ui.components.UIMorphComponent;
import mchorse.mappet.api.ui.components.UIStackComponent;
import mchorse.mappet.api.ui.components.UIStringListComponent;
import mchorse.mappet.api.ui.components.UITextComponent;
import mchorse.mappet.api.ui.components.UITextareaComponent;
import mchorse.mappet.api.ui.components.UITextboxComponent;
import mchorse.mappet.api.ui.components.UIToggleComponent;
import mchorse.mappet.api.ui.components.UITrackpadComponent;
import mchorse.metamorph.api.morphs.AbstractMorph;

public interface IMappetUIBuilder {
   UIComponent getCurrent();

   IMappetUIBuilder background();

   default IMappetUIBuilder notClosable() {
      return this.closable(false);
   }

   IMappetUIBuilder closable(boolean var1);

   IMappetUIBuilder paused(boolean var1);

   UIComponent create(String var1);

   UIGraphicsComponent graphics();

   UIButtonComponent button(String var1);

   UIIconComponent icon(String var1);

   UILabelComponent label(String var1);

   UITextComponent text(String var1);

   default UITextboxComponent textbox() {
      return this.textbox("");
   }

   default UITextboxComponent textbox(String text) {
      return this.textbox(text, 32);
   }

   UITextboxComponent textbox(String var1, int var2);

   default UITextareaComponent textarea() {
      return this.textarea("");
   }

   UITextareaComponent textarea(String var1);

   default UIToggleComponent toggle(String label) {
      return this.toggle(label, false);
   }

   UIToggleComponent toggle(String var1, boolean var2);

   default UITrackpadComponent trackpad() {
      return this.trackpad((double)0.0F);
   }

   UITrackpadComponent trackpad(double var1);

   default UIColorComponent color() {
      return this.color(-1);
   }

   UIColorComponent color(int var1);

   default UIStringListComponent stringList(List<String> values) {
      return this.stringList(values, -1);
   }

   UIStringListComponent stringList(List<String> var1, int var2);

   default UIStackComponent item() {
      return this.item((IScriptItemStack)null);
   }

   UIStackComponent item(IScriptItemStack var1);

   default UIMorphComponent morph(AbstractMorph morph) {
      return this.morph(morph, false);
   }

   UIMorphComponent morph(AbstractMorph var1, boolean var2);

   UIClickComponent click();

   IMappetUIBuilder layout();

   default IMappetUIBuilder column(int margin) {
      return this.column(margin, 0);
   }

   IMappetUIBuilder column(int var1, int var2);

   default IMappetUIBuilder row(int margin) {
      return this.row(margin, 0);
   }

   IMappetUIBuilder row(int var1, int var2);

   default IMappetUIBuilder grid(int margin) {
      return this.grid(margin, 0);
   }

   IMappetUIBuilder grid(int var1, int var2);
}
