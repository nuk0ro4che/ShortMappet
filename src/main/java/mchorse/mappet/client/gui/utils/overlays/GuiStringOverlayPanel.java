package mchorse.mappet.client.gui.utils.overlays;

import java.util.Collection;
import java.util.function.Consumer;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringSearchListElement;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_1074;
import net.minecraft.class_310;

public class GuiStringOverlayPanel extends GuiOverlayPanel {
   public GuiStringSearchListElement strings;
   private Consumer<String> callback;
   private boolean none;

   public GuiStringOverlayPanel(class_310 mc, IKey title, Collection<String> strings, Consumer<String> callback) {
      this(mc, title, true, strings, callback);
   }

   public GuiStringOverlayPanel(class_310 mc, IKey title, boolean none, Collection<String> strings, Consumer<String> callback) {
      super(mc, title);
      this.none = none;
      this.callback = callback;
      this.strings = new GuiStringSearchListElement(mc, (list) -> this.accept((String)list.get(0)));
      this.strings.label = IKey.lang("mappet.gui.search");
      this.strings.flex().relative(this.content).wh(1.0F, 1.0F);
      this.strings.list.add(strings);
      this.strings.list.sort();
      ScrollArea var10000 = this.strings.list.scroll;
      var10000.scrollSpeed *= 2;
      if (this.none) {
         this.strings.list.getList().add(0, class_1074.method_4662("mappet.gui.none", new Object[0]));
         this.strings.list.update();
      }

      this.content.add(this.strings);
   }

   public GuiStringOverlayPanel set(String string) {
      this.strings.filter("", true);
      this.strings.list.setCurrentScroll(string);
      if (this.none && this.strings.list.isDeselected()) {
         this.strings.list.setIndex(0);
      }

      return this;
   }

   protected void accept(String string) {
      if (this.callback != null) {
         this.callback.accept(this.getValue(string));
      }

   }

   protected String getValue() {
      return this.getValue((String)this.strings.list.getCurrentFirst());
   }

   protected String getValue(String string) {
      if (!this.none) {
         return string;
      } else {
         return this.strings.list.getIndex() == 0 ? "" : string;
      }
   }
}
