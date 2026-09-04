package mchorse.mappet.api.ui.components;

import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mappet.api.ui.utils.LayoutType;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.utils.ScrollDirection;
import mchorse.mclib.client.gui.utils.resizers.layout.ColumnResizer;
import mchorse.mclib.client.gui.utils.resizers.layout.GridResizer;
import mchorse.mclib.client.gui.utils.resizers.layout.RowResizer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_310;

public class UILayoutComponent extends UIParentComponent {
   public boolean scroll;
   public Integer scrollSize;
   public boolean horizontal;
   public LayoutType layoutType;
   public int margin;
   public int padding;
   public Integer width;
   public Integer items;

   public UILayoutComponent scroll() {
      this.scroll = true;
      return this;
   }

   public UILayoutComponent scrollSize(int scrollSize) {
      this.change(new String[]{"ScrollSize"});
      this.scrollSize = scrollSize;
      return this;
   }

   public UILayoutComponent horizontal() {
      this.horizontal = true;
      return this;
   }

   public UILayoutComponent width(int width) {
      this.width = width;
      return this;
   }

   public UILayoutComponent items(int items) {
      this.items = items;
      return this;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public GuiElement create(class_310 mc, UIContext context) {
      GuiElement element;
      if (this.scroll) {
         GuiScrollElement scroll = new GuiScrollElement(mc, this.horizontal ? ScrollDirection.HORIZONTAL : ScrollDirection.VERTICAL);
         if (this.scrollSize != null) {
            scroll.scroll.scrollSize = this.scrollSize;
         }

         element = scroll;
      } else {
         element = new GuiElement(mc);
      }

      for(UIComponent component : this.getChildComponents()) {
         GuiElement created = component.create(mc, context);
         if (this.layoutType == null) {
            created.flex().relative(element);
         }

         element.add(created);
      }

      if (this.layoutType != null) {
         this.applyLayout(element, this.layoutType);
      }

      return this.apply(element, context);
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   private void applyLayout(GuiElement element, LayoutType type) {
      if (type == LayoutType.COLUMN) {
         ColumnResizer column = element.flex().column(this.margin);
         if (this.scroll) {
            column.scroll();
         }

         if (!this.horizontal) {
            column.vertical();
         }

         if (this.width != null) {
            column.width(this.width);
         } else {
            column.stretch();
         }

         column.padding(this.padding);
      } else if (type == LayoutType.ROW) {
         RowResizer row = element.flex().row(this.margin);
         if (this.width != null) {
            row.width(this.width);
         }

         row.padding(this.padding);
      } else if (type == LayoutType.GRID) {
         GridResizer grid = element.flex().grid(this.margin);
         if (this.width != null) {
            grid.width(this.width);
         }

         if (this.items != null) {
            grid.items(this.items);
         }

         grid.padding(this.padding);
      }

   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected void applyProperty(UIContext context, String key, GuiElement element) {
      super.applyProperty(context, key, element);
      if (key.equals("ScrollSize") && element instanceof GuiScrollElement) {
         ((GuiScrollElement)element).scroll.scrollSize = this.scrollSize;
      }

   }

   @DiscardMethod
   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10556("Scroll", this.scroll);
      if (this.scrollSize != null) {
         tag.method_10569("ScrollSize", this.scrollSize);
      }

      tag.method_10556("Horizontal", this.horizontal);
      if (this.layoutType != null) {
         tag.method_10569("LayoutType", this.layoutType.ordinal());
      }

      tag.method_10569("Margin", this.margin);
      tag.method_10569("Padding", this.padding);
      if (this.width != null) {
         tag.method_10569("Width", this.width);
      }

      if (this.items != null) {
         tag.method_10569("Items", this.items);
      }

   }

   @DiscardMethod
   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Scroll")) {
         this.scroll = tag.method_10577("Scroll");
      }

      if (tag.method_10545("ScrollSize")) {
         this.scrollSize = tag.method_10550("ScrollSize");
      }

      if (tag.method_10545("Horizontal")) {
         this.horizontal = tag.method_10577("Horizontal");
      }

      if (tag.method_10545("LayoutType")) {
         int layoutType = tag.method_10550("LayoutType");
         if (layoutType >= 0 && layoutType < LayoutType.values().length) {
            this.layoutType = LayoutType.values()[layoutType];
         }
      }

      if (tag.method_10545("Margin")) {
         this.margin = tag.method_10550("Margin");
      }

      if (tag.method_10545("Padding")) {
         this.padding = tag.method_10550("Padding");
      }

      if (tag.method_10545("Width")) {
         this.width = tag.method_10550("Width");
      }

      if (tag.method_10545("Items")) {
         this.items = tag.method_10550("Items");
      }

   }
}
