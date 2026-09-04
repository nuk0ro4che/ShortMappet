package mchorse.mappet.client.gui.utils.overlays;

import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public abstract class GuiEditorOverlayPanel<T> extends GuiOverlayPanel {
   public GuiListElement<T> list;
   public GuiScrollElement editor;
   protected T item;

   public GuiEditorOverlayPanel(class_310 mc, IKey title) {
      super(mc, title);
      this.list = this.createList(mc);
      this.list.context(() -> {
         GuiSimpleContextMenu menu = (new GuiSimpleContextMenu(this.mc)).action(Icons.ADD, this.getAddLabel(), this::addItem);
         if (!this.list.getList().isEmpty()) {
            menu.action(Icons.REMOVE, this.getRemoveLabel(), this::removeItem, 16711731);
         }

         return menu.shadow();
      });
      this.editor = new GuiScrollElement(mc);
      this.list.flex().relative(this.content).w(120).h(1.0F);
      this.editor.flex().relative(this.content).x(120).w(1.0F, -120).h(1.0F).column(5).vertical().stretch().scroll().padding(10);
      this.content.add(new IGuiElement[]{this.editor, this.list});
   }

   protected abstract GuiListElement<T> createList(class_310 var1);

   protected IKey getAddLabel() {
      return IKey.EMPTY;
   }

   protected IKey getRemoveLabel() {
      return IKey.EMPTY;
   }

   protected void addItem() {
      this.addNewItem();
      this.list.update();
   }

   protected void addNewItem() {
   }

   protected void removeItem() {
      int index = this.list.getIndex();
      this.list.getList().remove(index);
      index = Math.max(index - 1, 0);
      T item = (T)(this.list.getList().isEmpty() ? null : this.list.getList().get(index));
      this.pickItem(item, true);
      this.list.update();
   }

   protected void pickItem(T item, boolean select) {
      this.item = item;
      this.editor.setVisible(item != null);
      if (item != null) {
         this.fillData(item);
         if (select) {
            this.list.setCurrentScroll(item);
         }

         this.resize();
      }

   }

   protected abstract void fillData(T var1);
}
