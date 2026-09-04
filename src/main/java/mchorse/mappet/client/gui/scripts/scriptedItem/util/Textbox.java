package mchorse.mappet.client.gui.scripts.scriptedItem.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.class_155;
import net.minecraft.class_327;

public class Textbox {
   private String text = "";
   private Consumer<String> callback;
   private int cursor;
   private int selection = -1;
   private int left;
   private int right;
   private Predicate<String> validator;
   private int length = 100;
   private boolean focused;
   private boolean enabled = true;
   private boolean visible = true;
   private IKey placeholder;
   private boolean background;
   private int color;
   private boolean border;
   private boolean holding;
   private int lastX;
   public Area area;
   public class_327 font;
   private int lastW;
   private long lastClick;

   public Textbox(Consumer<String> callback) {
      this.placeholder = IKey.EMPTY;
      this.background = true;
      this.color = -1;
      this.area = new Area();
      this.callback = callback;
   }

   public void setFont(class_327 font) {
      this.font = font;
      this.updateBounds(false);
   }

   public class_327 getFont() {
      return this.font;
   }

   public void setPlaceholder(IKey placeholder) {
      this.placeholder = placeholder;
   }

   public void setBorder(boolean border) {
      this.border = border;
   }

   public String getSelectedText() {
      if (this.isSelected()) {
         int min = Math.min(this.cursor, this.selection);
         int max = Math.max(this.cursor, this.selection);
         return this.text.substring(min, max);
      } else {
         return "";
      }
   }

   public String getText() {
      return this.text;
   }

   public void setText(String text) {
      if (text.length() > this.length) {
         text = text.substring(0, this.length);
      }

      this.text = text;
      this.moveCursorToStart();
      this.deselect();
      this.updateBounds(false);
   }

   public void acceptText() {
      if (this.callback != null) {
         this.callback.accept(this.text);
      }

   }

   public void insert(String text) {
      this.deleteSelection();
      text = text.replaceAll("\n", "");
      int i = this.text.length() + text.length();
      if (i >= this.length) {
         text = text.substring(0, this.length - this.text.length());
      }

      if (!text.isEmpty()) {
         String newText = this.text;
         if (this.cursor == 0) {
            newText = text + newText;
         } else if (this.cursor >= newText.length()) {
            newText = newText + text;
         } else {
            newText = newText.substring(0, this.cursor) + text + newText.substring(this.cursor);
         }

         this.text = newText;
         this.moveCursorTo(this.cursor + text.length());
         this.updateBounds(false);
      }
   }

   public void deleteCharacter() {
      if (this.cursor > 0) {
         String var10001 = this.text.substring(0, this.cursor - 1);
         this.text = var10001 + this.text.substring(this.cursor);
         this.moveCursorBy(-1);
      }

   }

   public void setValidator(Predicate<String> validator) {
      this.validator = validator;
   }

   public int getLength() {
      return this.length;
   }

   public void setLength(int length) {
      if (this.text.length() > length) {
         this.text = this.text.substring(0, length);
         this.updateBounds(false);
      }

      this.length = length;
   }

   public int getCursor() {
      return this.cursor;
   }

   public int getSelection() {
      return this.selection;
   }

   public boolean selectGroup(int direction, boolean select) {
      Pair<Integer, Integer> groups = this.findGroup(direction, this.cursor);
      if (groups == null) {
         return false;
      } else {
         int min = (Integer)groups.a;
         int max = (Integer)groups.b;
         if (select) {
            if (direction == 0) {
               this.cursor = max;
               this.selection = min;
            } else {
               if (!this.isSelected()) {
                  this.selection = this.cursor;
               }

               this.cursor = direction < 0 ? min : max;
            }
         } else {
            this.deselect();
            this.cursor = direction < 0 ? min : max;
         }

         this.updateBounds(false);
         return true;
      }
   }

   public Pair<Integer, Integer> findGroup(int direction, int offset) {
      StringGroupMatcher matcher = new StringGroupMatcher();
      return matcher.findGroup(direction, this.text, offset);
   }

   public void moveCursorTo(int cursor) {
      this.cursor = cursor;
      this.cursor = MathUtils.clamp(this.cursor, 0, this.text.length());
      this.updateBounds(false);
   }

   public void moveCursorToStart() {
      this.moveCursorTo(0);
   }

   public void moveCursorToEnd() {
      this.moveCursorTo(this.text.length());
   }

   private void moveCursorBy(int i) {
      this.moveCursorTo(this.cursor + (int)Math.copySign(1.0F, (float)i));
   }

   public boolean isSelected() {
      return this.selection != this.cursor && this.selection >= 0;
   }

   public void setSelection(int selection) {
      this.selection = selection;
      this.updateBounds(true);
   }

   public void deselect() {
      this.selection = -1;
   }

   public void deleteSelection() {
      if (this.cursor == this.selection) {
         this.deselect();
      }

      if (this.isSelected()) {
         int min = Math.min(this.cursor, this.selection);
         int max = Math.max(this.cursor, this.selection);
         String var10001 = this.text.substring(0, min);
         this.text = var10001 + this.text.substring(max);
         this.deselect();
         this.cursor = min;
         this.updateBounds(false);
         this.clamp();
      }
   }

   private void updateBounds(boolean selection) {
      int cursor = selection ? this.selection : this.cursor;
      int length = this.text.length();
      int offset = this.background ? 10 : 0;
      int max = this.area.w - offset;
      if (this.font.method_1727(this.text) < max) {
         this.left = 0;
         this.right = length;
      } else {
         if (cursor < this.left) {
            int bound = this.getBound(max, cursor, 1);
            if (bound == cursor) {
               bound = this.getBound(max, length - 1, -1);
               this.left = bound;
               this.right = length;
            } else {
               this.left = cursor;
               this.right = MathUtils.clamp(bound + 1, 0, length);
            }
         } else if (cursor >= this.right) {
            int bound = this.getBound(max, MathUtils.clamp(cursor, 0, length - 1), -1);
            if (bound == cursor) {
               bound = this.getBound(max, 0, 1);
               this.left = 0;
               this.right = bound;
            } else {
               this.left = bound;
               this.right = cursor;
            }
         }

         this.left = MathUtils.clamp(this.left, 0, length);
         this.right = MathUtils.clamp(this.right, 0, length);
      }
   }

   private int getBound(int max, int start, int direction) {
      int w = 0;

      for(int i = start; i >= 0 && i < this.text.length(); i += direction) {
         int sw = this.font.method_1727(String.valueOf(this.text.charAt(i)));
         if (w < max && w + sw >= max) {
            return i;
         }

         w += sw;
      }

      return start;
   }

   private void clamp() {
      this.cursor = MathUtils.clamp(this.cursor, 0, this.text.length());
      this.selection = MathUtils.clamp(this.selection, -1, this.text.length());
   }

   private String getWrappedText() {
      int length = this.text.length();
      return this.text.substring(MathUtils.clamp(this.left, 0, length), MathUtils.clamp(this.right, 0, length));
   }

   public boolean hasBackground() {
      return this.background;
   }

   public void setBackground(boolean background) {
      this.background = background;
   }

   public int getColor() {
      return this.color;
   }

   public void setColor(int color) {
      this.color = color;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean isFocused() {
      return this.focused;
   }

   public void setFocused(boolean focused) {
      this.focused = focused;
   }

   public void mouseClicked(int x, int y, int button) {
      if (button == 0 && this.area.isInside(x, y)) {
         if (System.currentTimeMillis() < this.lastClick) {
            this.selectGroup(0, true);
            this.lastClick -= 500L;
         } else {
            int lastSelection = this.selection;
            this.deselect();
            if (GuiUtils.isShiftKeyDown()) {
               this.selection = lastSelection < 0 ? this.cursor : lastSelection;
            }

            this.focused = true;
            this.lastX = x;
            this.holding = true;
            this.moveCursorTo(this.getIndexAt(x));
            this.lastClick = System.currentTimeMillis() + 200L;
         }
      } else {
         this.focused = false;
      }

   }

   public void mouseReleased(int x, int y, int button) {
      if (button == 0) {
         this.holding = false;
      }

   }

   private int getIndexAt(int x) {
      x -= this.area.x;
      if (this.background) {
         x -= 4;
      }

      if (x >= 0) {
         String wrappedText = this.getWrappedText();
         int w = this.font.method_1727(wrappedText);
         if (x >= w) {
            return this.right;
         }

         w = 0;
         int i = 0;

         for(int c = wrappedText.length(); i < c; ++i) {
            char character = wrappedText.charAt(i);
            if (character == 167) {
               ++i;
            } else {
               int string = this.font.method_1727(String.valueOf(character));
               if (x >= w && x < w + string) {
                  return this.left + i;
               }

               w += string;
            }
         }
      }

      return this.left;
   }

   public boolean keyPressed(GuiContext context) {
      if (this.focused && this.enabled && this.visible) {
         boolean selecting = this.isSelected();
         boolean ctrl = GuiUtils.isCtrlKeyDown();
         boolean shift = GuiUtils.isShiftKeyDown();
         if (!ctrl || context.keyCode != 46 && context.keyCode != 45) {
            if (ctrl && context.keyCode == 47) {
               String clipboard = GuiUtils.getClipboardString();
               if (!clipboard.isEmpty()) {
                  this.insert(clipboard);
                  this.acceptText();
               }

               return true;
            }

            if (ctrl && context.keyCode == 30) {
               this.selection = 0;
               this.cursor = this.text.length();
               this.updateBounds(false);
               return true;
            }

            if (context.keyCode == 199) {
               this.handleShift(shift);
               this.moveCursorToStart();
            } else if (context.keyCode == 207) {
               this.handleShift(shift);
               this.moveCursorToEnd();
            } else if (context.keyCode != 203 && context.keyCode != 205) {
               if (context.keyCode == 14 || context.keyCode == 211) {
                  if (this.isSelected()) {
                     this.deleteSelection();
                     this.acceptText();
                     return true;
                  }

                  if (context.keyCode == 211 && this.cursor < this.text.length()) {
                     this.moveCursorBy(1);
                     this.deleteCharacter();
                     this.acceptText();
                     return true;
                  }

                  if (context.keyCode == 14) {
                     this.deleteCharacter();
                     this.acceptText();
                     return true;
                  }
               }
            } else {
               int offset = context.keyCode == 203 ? -1 : 1;
               if (ctrl) {
                  if (!this.selectGroup(offset, shift)) {
                     this.handleShift(shift);
                     this.moveCursorBy(offset);
                  }
               } else {
                  this.handleShift(shift);
                  this.moveCursorBy(offset);
               }
            }
         } else if (selecting) {
            GuiUtils.setClipboardString(this.getSelectedText());
            if (context.keyCode == 45) {
               this.deleteSelection();
            }

            return true;
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean textInput(char character) {
      if (this.focused && this.enabled && this.visible) {
         if (class_155.method_643(character)) {
            String text = String.valueOf(character);
            if (this.validator != null && !this.validator.test(text)) {
               return false;
            } else {
               this.insert(text);
               this.acceptText();
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void handleShift(boolean shift) {
      if (shift) {
         if (this.selection == -1) {
            this.selection = this.cursor;
         }
      } else {
         this.deselect();
      }

   }

   public void render(GuiContext context) {
      if (this.visible) {
         int mouseX = context.mouseX;
         int mouseY = context.mouseY;
         if (this.lastW != this.area.w) {
            this.lastW = this.area.w;
            this.updateBounds(false);
         }

         if (this.area.isInside(mouseX, mouseY) && this.holding && Math.abs(mouseX - this.lastX) > 2) {
            this.moveCursorTo(this.getIndexAt(mouseX));
         }

         int x = this.area.x;
         int y = this.area.y;
         if (this.background) {
            this.area.draw(-16777216);
            if (this.border) {
               int borderColor = this.focused ? -16777216 + (Integer)McLib.primaryColor.get() : -5592406;
               GuiDraw.drawOutline(this.area.x, this.area.y, this.area.ex(), this.area.ey(), borderColor);
            }

            x = this.area.x + 4;
            int var10000 = this.area.my();
            Objects.requireNonNull(this.font);
            y = var10000 - 9 / 2;
         }

         boolean empty = !this.focused && this.text.isEmpty();
         String text = empty ? this.placeholder.get() : this.getWrappedText();
         int length = text.length();
         int color = empty ? 11184810 : this.color;
         if (!empty && this.isSelected()) {
            int min = MathUtils.clamp(Math.min(this.cursor, this.selection) - this.left, 0, length);
            int max = MathUtils.clamp(Math.max(this.cursor, this.selection) - this.left, 0, length);
            int offset = this.font.method_1727(text.substring(0, min));
            int sx = x + offset;
            int sw = this.font.method_1727(text.substring(min, max));
            int var10001 = y - 2;
            int var10002 = sx + sw;
            Objects.requireNonNull(this.font);
            GuiDraw.drawRect(sx, var10001, var10002, y + 9 + 2, -2013265920 + (Integer)McLib.primaryColor.get());
         }

         GuiDraw.drawStringWithShadow(context.font, text, x, y, color);
         if (this.focused) {
            int relativeIndex = this.cursor - this.left;
            if (relativeIndex >= 0 && relativeIndex <= length) {
               x += this.font.method_1727(text.substring(0, relativeIndex));
               float alpha = (float)Math.sin((double)context.partialTicks / (double)2.0F);
               int c = ColorUtils.setAlpha(16777215, alpha * 0.5F + 0.5F);
               int var20 = y - 1;
               int var21 = x + 1;
               Objects.requireNonNull(this.font);
               GuiDraw.drawRect(x, var20, var21, y + 9 + 1, c);
            }
         }

      }
   }
}
