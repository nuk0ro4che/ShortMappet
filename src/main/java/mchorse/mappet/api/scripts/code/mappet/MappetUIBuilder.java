package mchorse.mappet.api.scripts.code.mappet;

import java.util.List;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.api.scripts.user.items.IScriptItemStack;
import mchorse.mappet.api.scripts.user.mappet.IMappetUIBuilder;
import mchorse.mappet.api.ui.UI;
import mchorse.mappet.api.ui.components.UIButtonComponent;
import mchorse.mappet.api.ui.components.UIClickComponent;
import mchorse.mappet.api.ui.components.UIColorComponent;
import mchorse.mappet.api.ui.components.UIComponent;
import mchorse.mappet.api.ui.components.UIGraphicsComponent;
import mchorse.mappet.api.ui.components.UIIconComponent;
import mchorse.mappet.api.ui.components.UILabelComponent;
import mchorse.mappet.api.ui.components.UILayoutComponent;
import mchorse.mappet.api.ui.components.UIMorphComponent;
import mchorse.mappet.api.ui.components.UIStackComponent;
import mchorse.mappet.api.ui.components.UIStringListComponent;
import mchorse.mappet.api.ui.components.UITextComponent;
import mchorse.mappet.api.ui.components.UITextareaComponent;
import mchorse.mappet.api.ui.components.UITextboxComponent;
import mchorse.mappet.api.ui.components.UIToggleComponent;
import mchorse.mappet.api.ui.components.UITrackpadComponent;
import mchorse.mappet.api.ui.utils.LayoutType;
import mchorse.metamorph.api.morphs.AbstractMorph;

public class MappetUIBuilder implements IMappetUIBuilder {
   private UI ui;
   private UIComponent current;
   private String script;
   private String function;

   public MappetUIBuilder(UI ui, String script, String function) {
      this.ui = ui;
      this.current = ui.root;
      this.script = script;
      this.function = function;
   }

   public MappetUIBuilder(UIComponent component) {
      this.current = component;
   }

   public UIComponent getCurrent() {
      return this.current;
   }

   public UI getUI() {
      return this.ui;
   }

   public String getScript() {
      return this.script;
   }

   public String getFunction() {
      return this.function;
   }

   public IMappetUIBuilder background() {
      if (this.ui != null) {
         this.ui.background = true;
      }

      return this;
   }

   public IMappetUIBuilder closable(boolean closable) {
      if (this.ui != null) {
         this.ui.closable = closable;
      }

      return this;
   }

   public IMappetUIBuilder paused(boolean paused) {
      if (this.ui != null) {
         this.ui.paused = paused;
      }

      return this;
   }

   public UIComponent create(String id) {
      UIComponent component = (UIComponent)CommonProxy.getUiComponents().create(id);
      if (component == null) {
         return null;
      } else {
         this.current.getChildComponents().add(component);
         return component;
      }
   }

   public UIGraphicsComponent graphics() {
      UIGraphicsComponent component = new UIGraphicsComponent();
      this.current.getChildComponents().add(component);
      return component;
   }

   public UIButtonComponent button(String label) {
      UIButtonComponent component = new UIButtonComponent();
      this.current.getChildComponents().add(component);
      component.label(label);
      return component;
   }

   public UIIconComponent icon(String icon) {
      UIIconComponent component = new UIIconComponent();
      this.current.getChildComponents().add(component);
      component.icon(icon);
      return component;
   }

   public UILabelComponent label(String label) {
      UILabelComponent component = new UILabelComponent();
      this.current.getChildComponents().add(component);
      component.label(label);
      return component;
   }

   public UITextComponent text(String text) {
      UITextComponent component = new UITextComponent();
      this.current.getChildComponents().add(component);
      component.label(text);
      return component;
   }

   public UITextboxComponent textbox(String text, int maxLength) {
      UITextboxComponent component = new UITextboxComponent();
      this.current.getChildComponents().add(component);
      component.maxLength(maxLength).label(text);
      return component;
   }

   public UITextareaComponent textarea(String text) {
      UITextareaComponent component = new UITextareaComponent();
      this.current.getChildComponents().add(component);
      component.label(text);
      return component;
   }

   public UIToggleComponent toggle(String label, boolean state) {
      UIToggleComponent component = new UIToggleComponent();
      this.current.getChildComponents().add(component);
      component.state(state).label(label);
      return component;
   }

   public UITrackpadComponent trackpad(double value) {
      UITrackpadComponent component = new UITrackpadComponent();
      this.current.getChildComponents().add(component);
      component.value(value);
      return component;
   }

   public UIColorComponent color(int color) {
      UIColorComponent component = new UIColorComponent();
      this.current.getChildComponents().add(component);
      component.color(color);
      return component;
   }

   public UIStringListComponent stringList(List<String> values, int selected) {
      UIStringListComponent component = new UIStringListComponent();
      this.current.getChildComponents().add(component);
      component.values(values);
      if (selected >= 0) {
         component.selected(selected);
      }

      return component;
   }

   public UIStackComponent item(IScriptItemStack stack) {
      UIStackComponent component = new UIStackComponent();
      this.current.getChildComponents().add(component);
      if (stack != null && !stack.isEmpty()) {
         component.stack(stack.getMinecraftItemStack());
      }

      return component;
   }

   public UIMorphComponent morph(AbstractMorph morph, boolean editing) {
      UIMorphComponent component = new UIMorphComponent();
      this.current.getChildComponents().add(component);
      component.morph(morph);
      if (editing) {
         component.editing();
      }

      return component;
   }

   public UIClickComponent click() {
      UIClickComponent component = new UIClickComponent();
      this.current.getChildComponents().add(component);
      return component;
   }

   public IMappetUIBuilder layout() {
      return new MappetUIBuilder(this.layout(0, 0));
   }

   public UILayoutComponent layout(int margin, int padding) {
      UILayoutComponent layout = new UILayoutComponent();
      layout.margin = margin;
      layout.padding = padding;
      this.current.getChildComponents().add(layout);
      return layout;
   }

   public IMappetUIBuilder column(int margin, int padding) {
      UILayoutComponent layout = this.layout(margin, padding);
      layout.layoutType = LayoutType.COLUMN;
      return new MappetUIBuilder(layout);
   }

   public IMappetUIBuilder row(int margin, int padding) {
      UILayoutComponent layout = this.layout(margin, padding);
      layout.layoutType = LayoutType.ROW;
      return new MappetUIBuilder(layout);
   }

   public IMappetUIBuilder grid(int margin, int padding) {
      UILayoutComponent layout = this.layout(margin, padding);
      layout.layoutType = LayoutType.GRID;
      return new MappetUIBuilder(layout);
   }
}
