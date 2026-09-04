package mchorse.mappet.client.gui.scripts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import mchorse.mappet.client.gui.panels.GuiScriptPanel;
import mchorse.mappet.client.gui.scripts.utils.HighlightedTextLine;
import mchorse.mappet.client.gui.utils.text.GuiText;
import mchorse.mappet.client.gui.utils.text.utils.Cursor;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketRepl;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_1074;
import net.minecraft.class_310;

public class GuiRepl extends GuiElement {
   public GuiTextEditor repl;
   public GuiScrollElement log;
   private List<String> history = new ArrayList();
   private int index = 0;

   public GuiRepl(class_310 mc) {
      super(mc);
      this.repl = new GuiTextEditor(mc, (Consumer)null);
      this.repl.background().flex().relative(this).y(1.0F).w(1.0F).h(100).anchorY(1.0F);
      this.repl.context(() -> GuiScriptPanel.createScriptContextMenu(this.mc, this.repl));
      this.log = new GuiScrollElement(mc);
      this.log.flex().relative(this).w(1.0F).h(1.0F, -100).column(0).vertical().stretch().scroll();
      this.add(new IGuiElement[]{this.repl, this.log});
      this.repl.setText("\"" + class_1074.method_4662("mappet.gui.scripts.repl.hello_world", new Object[0]) + "\"");
      this.log(class_1074.method_4662("mappet.gui.scripts.repl.welcome", new Object[0]));
   }

   public boolean keyTyped(GuiContext context) {
      if (this.repl.isFocused() && context.keyCode == 28 && !GuiUtils.isShiftKeyDown()) {
         String text = this.repl.getText();
         if (text.trim().startsWith("clear()")) {
            this.repl.clear();
            this.log.removeAll();
            return true;
         } else {
            if (!text.isEmpty()) {
               Dispatcher.sendToServer(new PacketRepl(text));
               this.repl.clear();
               this.history.add(text);
               this.index = this.history.size();
            }

            return true;
         }
      } else {
         if (this.repl.isFocused() && !this.repl.isSelected() && !this.history.isEmpty() && GuiUtils.isCtrlKeyDown()) {
            Cursor cursor = this.repl.cursor;
            if (context.keyCode == 200 && this.index > 0) {
               --this.index;
               this.repl.setText((String)this.history.get(this.index));
               int lastLine = this.repl.getLines().size() - 1;
               cursor.set(lastLine, ((HighlightedTextLine)this.repl.getLines().get(lastLine)).text.length());
               this.repl.moveViewportToCursor();
               return true;
            }

            if (context.keyCode == 208 && this.index < this.history.size() - 1) {
               ++this.index;
               this.repl.setText((String)this.history.get(this.index));
               int lastLine = this.repl.getLines().size() - 1;
               cursor.set(lastLine, ((HighlightedTextLine)this.repl.getLines().get(lastLine)).text.length());
               this.repl.moveViewportToCursor();
               return true;
            }
         }

         return super.keyTyped(context);
      }
   }

   public void log(String code) {
      code = code.trim();
      if (!code.isEmpty()) {
         int size = this.log.getChildren().size();
         boolean odd = (size + 1) % 2 == 1;
         this.log.add((new GuiReplText(this.mc, odd, size == 0 ? 10 : 5)).text(code));
         this.resize();
         this.log.scroll.scrollTo(this.log.scroll.scrollSize);
      }
   }

   public static class GuiReplText extends GuiText {
      private boolean odd;

      public GuiReplText(class_310 mc, boolean odd, int vertical) {
         super(mc);
         this.odd = odd;
         this.padding(10, vertical);
         this.context(() -> (new GuiSimpleContextMenu(mc)).action(Icons.COPY, IKey.str("Copy text"), () -> GuiUtils.setClipboardString(this.getText().get())));
      }

      public void draw(GuiContext context) {
         if (this.odd) {
            this.area.draw(-2013265920);
         }

         super.draw(context);
      }
   }
}
