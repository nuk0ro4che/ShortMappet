package mchorse.mappet.client.gui.ui;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icon;
import net.minecraft.class_310;


public class GuiUIComponentHierarchyList extends GuiStringListElement {
   private final Consumer<List<String>> selectionCallback;
   private final Function<String, Icon> iconProvider;
   private final Function<String, Integer> colorProvider;

   public GuiUIComponentHierarchyList(class_310 mc, java.util.function.Consumer<java.util.List<String>> callback, Function<String, Icon> iconProvider, Function<String, Integer> colorProvider) {
      super(mc, callback);
      this.selectionCallback = callback;
      this.iconProvider = iconProvider;
      this.colorProvider = colorProvider;
   }

   public boolean mouseClicked(mchorse.mclib.client.gui.framework.elements.utils.GuiContext context) {
      if (context.mouseButton == 1 && this.area.isInside(context)) {
         int index = (context.mouseY - this.scroll.y + this.scroll.scroll) / Math.max(1, this.scroll.scrollItemSize);
         if (index >= 0 && index < this.getList().size()) {
            String value = (String)this.getList().get(index);
            this.setCurrent(value);
            this.selectionCallback.accept(Collections.singletonList(value));
         }
      }
      return super.mouseClicked(context);
   }

   protected String elementToString(String value) {
      int indent = 0;
      while(indent < value.length() && value.charAt(indent) == ' ') {
         ++indent;
      }
      return value.substring(0, indent) + "   " + value.substring(indent);
   }

   protected void drawElementPart(String value, int width, int x, int y, boolean hovered, boolean selected) {
      super.drawElementPart(value, width, x, y, hovered, selected);
      Icon icon = (Icon)this.iconProvider.apply(value);
      if (icon != null) {
         int indent = 0;
         while(indent < value.length() && value.charAt(indent) == ' ') {
            ++indent;
         }
         GuiDraw.bindColor(this.colorProvider == null ? -1 : (Integer)this.colorProvider.apply(value));
         icon.render(x + indent * 4 + 7, y + 7, 0.5F, 0.5F);
         GuiDraw.bindColor(-1);
      }
   }
}
