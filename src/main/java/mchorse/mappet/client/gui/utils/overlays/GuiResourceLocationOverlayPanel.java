package mchorse.mappet.client.gui.utils.overlays;

import java.util.Set;
import java.util.function.Consumer;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringSearchListElement;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_1074;
import net.minecraft.class_2960;
import net.minecraft.class_310;

public abstract class GuiResourceLocationOverlayPanel extends GuiOverlayPanel {
   public GuiStringSearchListElement rls;
   private Consumer<class_2960> callback;

   public GuiResourceLocationOverlayPanel(class_310 mc, IKey title, Set<class_2960> keys, Consumer<class_2960> callback) {
      super(mc, title);
      this.callback = callback;
      this.rls = new GuiStringSearchListElement(mc, (list) -> this.accept((String)list.get(0)));
      this.rls.label = IKey.lang("mappet.gui.search");
      this.rls.flex().relative(this.content).wh(1.0F, 1.0F);

      for(class_2960 location : keys) {
         this.rls.list.add(location.toString());
      }

      this.rls.list.sort();
      this.rls.list.getList().add(0, class_1074.method_4662("mappet.gui.none", new Object[0]));
      this.rls.list.update();
      ScrollArea var10000 = this.rls.list.scroll;
      var10000.scrollSpeed *= 3;
      this.content.add(this.rls);
   }

   public GuiResourceLocationOverlayPanel set(class_2960 rl) {
      return this.set(rl == null ? "" : rl.toString());
   }

   public GuiResourceLocationOverlayPanel set(String rl) {
      this.rls.filter("", true);
      this.rls.list.setCurrentScroll(rl);
      if (this.rls.list.isDeselected()) {
         this.rls.list.setIndex(0);
      }

      return this;
   }

   private void accept(String string) {
      if (this.callback != null) {
         this.callback.accept(this.rls.list.getIndex() == 0 ? null : new class_2960(string));
      }

   }
}
