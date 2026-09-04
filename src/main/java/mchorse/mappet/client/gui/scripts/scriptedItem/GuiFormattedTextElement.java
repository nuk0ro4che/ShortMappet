package mchorse.mappet.client.gui.scripts.scriptedItem;

import java.util.Objects;
import java.util.function.Consumer;
import mchorse.mappet.client.gui.scripts.scriptedItem.util.Textbox;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IFocusedGuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.class_310;
import net.minecraft.class_327;
import org.lwjgl.input.Keyboard;

public class GuiFormattedTextElement extends GuiElement implements IFocusedGuiElement {
   private static String[] formattingCodes = new String[]{"§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f", "§k", "§l", "§m", "§n", "§o"};
   private static int[] colors = new int[]{0, 170, 43520, 43690, 11141120, 11141290, 16755200, 11184810, 5592405, 5592575, 5635925, 5636095, 16733525, 16733695, 16777045, 16777215};
   public Textbox text;
   private Consumer<String> callback;

   public GuiFormattedTextElement(class_310 mc, Consumer<String> consumer) {
      super(mc);
      this.callback = consumer;
      this.text = new Textbox(consumer);
      this.text.setFont(mc.field_1772);
      this.text.setBorder(true);
   }

   public void resize() {
      super.resize();
      this.text.area.set(this.area.x, this.area.ey() - 20, this.area.w, 20);
   }

   public boolean mouseClicked(GuiContext context) {
      int cell = this.area.h - 20;
      if (this.area.isInside(context) && context.mouseY < this.area.y + cell) {
         int index = (context.mouseX - this.area.x) / cell;
         if (index < formattingCodes.length && this.text.isSelected()) {
            String formattingCode = formattingCodes[index];
            String text = this.text.getText();
            int cursor = this.text.getCursor();
            int selection = this.text.getSelection();
            int beginning = Math.min(cursor, selection);
            int end = Math.max(cursor, selection);
            Textbox var10000 = this.text;
            String var10001 = text.substring(0, beginning);
            var10000.setText(var10001 + formattingCode + this.text.getSelectedText() + "§r" + text.substring(end));
            this.text.moveCursorTo(beginning);
            this.text.setSelection(end + 4);
            if (this.callback != null) {
               this.callback.accept(this.text.getText());
            }

            return true;
         } else {
            return super.mouseClicked(context);
         }
      } else if (super.mouseClicked(context)) {
         return true;
      } else {
         boolean wasFocused = this.text.isFocused();
         this.text.mouseClicked(context.mouseX, context.mouseY, context.mouseButton);
         if (wasFocused != this.text.isFocused()) {
            context.focus(wasFocused ? null : this);
         }

         return this.text.area.isInside(context);
      }
   }

   public void mouseReleased(GuiContext context) {
      this.text.mouseReleased(context.mouseX, context.mouseY, context.mouseButton);
      super.mouseReleased(context);
   }

   public boolean keyTyped(GuiContext context) {
      if (this.isFocused()) {
         if (context.keyCode == 15) {
            context.focus(this, -1, GuiUtils.isShiftKeyDown() ? -1 : 1);
            return true;
         }

         if (context.keyCode == 1) {
            context.unfocus();
            return false;
         }
      }

      return this.text.keyPressed(context) || this.text.textInput(context.typedChar) || super.keyTyped(context);
   }

   public void draw(GuiContext context) {
      this.area.draw(-15658735);
      int x = this.area.x;
      int y = this.area.y;
      int cell = this.area.h - 20;

      for(int i = 0; i < formattingCodes.length; ++i) {
         Area.SHARED.set(x, y, cell, cell);
         int a = Area.SHARED.isInside(context) ? -2013265920 : -16777216;
         float aa = Area.SHARED.isInside(context) ? 0.5F : 1.0F;
         if (i < colors.length) {
            GuiDraw.drawRect(x, y, x + cell, y + cell, colors[i] | a);
         } else if (i == 16) {
            class_327 var10000 = context.font;
            String var10001 = formattingCodes[i] + "W";
            int var10002 = x + cell / 2;
            int var10003 = y + cell / 2;
            Objects.requireNonNull(context.font);
            GuiDraw.drawCenteredString(var10000, var10001, var10002, var10003 - 9 / 2, ColorUtils.multiplyColor(16777215, aa));
         } else if (i == 17) {
            class_327 var8 = context.font;
            String var12 = formattingCodes[i] + "B";
            int var16 = x + cell / 2;
            int var20 = y + cell / 2;
            Objects.requireNonNull(context.font);
            GuiDraw.drawCenteredString(var8, var12, var16, var20 - 9 / 2, ColorUtils.multiplyColor(16777215, aa));
         } else if (i == 18) {
            class_327 var9 = context.font;
            String var13 = formattingCodes[i] + "S";
            int var17 = x + cell / 2;
            int var21 = y + cell / 2;
            Objects.requireNonNull(context.font);
            GuiDraw.drawCenteredString(var9, var13, var17, var21 - 9 / 2, ColorUtils.multiplyColor(16777215, aa));
         } else if (i == 19) {
            class_327 var10 = context.font;
            String var14 = formattingCodes[i] + "U";
            int var18 = x + cell / 2;
            int var22 = y + cell / 2;
            Objects.requireNonNull(context.font);
            GuiDraw.drawCenteredString(var10, var14, var18, var22 - 9 / 2, ColorUtils.multiplyColor(16777215, aa));
         } else if (i == 20) {
            class_327 var11 = context.font;
            String var15 = formattingCodes[i] + "I";
            int var19 = x + cell / 2;
            int var23 = y + cell / 2;
            Objects.requireNonNull(context.font);
            GuiDraw.drawCenteredString(var11, var15, var19, var23 - 9 / 2, ColorUtils.multiplyColor(16777215, aa));
         }

         x += cell;
      }

      this.text.render(context);
      super.draw(context);
   }

   public boolean isFocused() {
      return this.text.isFocused();
   }

   public void focus(GuiContext guiContext) {
      this.text.setFocused(true);
      Keyboard.enableRepeatEvents(true);
   }

   public void unfocus(GuiContext guiContext) {
      this.text.setFocused(false);
      Keyboard.enableRepeatEvents(false);
   }

   public void selectAll(GuiContext guiContext) {
      this.text.moveCursorToEnd();
      this.text.setSelection(0);
   }

   public void unselect(GuiContext guiContext) {
      this.text.deselect();
   }
}
