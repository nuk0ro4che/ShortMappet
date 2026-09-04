package mchorse.mappet.client.gui.scripts;

import mchorse.mappet.client.gui.utils.overlays.GuiOverlayPanel;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiScriptSearchOverlayPanel extends GuiOverlayPanel {
   private final GuiTextEditor editor;
   private final GuiTextElement search;
   private final GuiTextElement replacement;
   private String lastNeedle = "";
   private String lastText = "";
   private int nextSearchOffset;
   private boolean open;

   public GuiScriptSearchOverlayPanel(class_310 mc, GuiTextEditor editor) {
      super(mc, IKey.str("Поиск и замена"));
      this.editor = editor;
      this.search = new GuiTextElement(mc, 1000, (text) -> this.resetSearch());
      this.replacement = new GuiTextElement(mc, 1000, (text) -> {
      });
      this.search.setText("");
      this.replacement.setText("");
      this.search.tooltip(IKey.str("Что найти в текущем скрипте"));
      this.replacement.tooltip(IKey.str("На что заменить найденный текст"));
      this.search.flex().relative(this.content).xy(0, 0).w(1.0F).h(18);
      this.replacement.flex().relative(this.content).xy(0, 30).w(1.0F).h(18);
      GuiButtonElement find = new GuiButtonElement(mc, IKey.str("Найти"), (button) -> this.findNext());
      GuiButtonElement replaceAll = new GuiButtonElement(mc, IKey.str("Заменить всё"), (button) -> this.replaceAll());
      find.tooltip(IKey.str("Найти следующее совпадение"));
      replaceAll.tooltip(IKey.str("Заменить все найденные совпадения"));
      GuiElement buttons = Elements.row(mc, 5, new GuiElement[]{find, replaceAll});
      buttons.flex().relative(this.content).xy(0, 58).w(1.0F).h(18);
      this.content.add(new IGuiElement[]{this.search, this.replacement, buttons});
   }

   public void setOpen(boolean open) {
      this.open = open;
   }

   public boolean isOpen() {
      return this.open;
   }

   @Override
   public boolean shouldCloseOnOutsideClick() {
      return false;
   }

   @Override
   public void onClose() {
      this.open = false;
   }

   private void findNext() {
      String needle = this.search.field.getText();
      if (needle == null || needle.isEmpty()) {
         return;
      }

      String text = this.editor.getText();
      if (!needle.equals(this.lastNeedle) || !text.equals(this.lastText)) {
         this.lastNeedle = needle;
         this.lastText = text;
         this.nextSearchOffset = 0;
      }

      int found = text.indexOf(needle, this.nextSearchOffset);
      if (found < 0) {
         found = text.indexOf(needle);
      }

      if (found >= 0) {
         this.editor.selectRange(found, found + needle.length());
         this.editor.moveViewportToCursor();
         this.nextSearchOffset = found + needle.length();
      }
   }

   private void replaceAll() {
      String needle = this.search.field.getText();
      if (needle == null || needle.isEmpty()) {
         return;
      }

      String replacement = this.replacement.field.getText();
      this.editor.setText(this.editor.getText().replace(needle, replacement == null ? "" : replacement));
      this.resetSearch();
   }

   private void resetSearch() {
      this.lastNeedle = "";
      this.lastText = "";
      this.nextSearchOffset = 0;
   }
}
