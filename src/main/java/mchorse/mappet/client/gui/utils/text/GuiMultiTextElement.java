package mchorse.mappet.client.gui.utils.text;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.vecmath.Vector2d;
import mchorse.mappet.Mappet;
import mchorse.mappet.client.gui.utils.GuiMappetUtils;
import mchorse.mappet.client.gui.utils.text.undo.TextEditUndo;
import mchorse.mappet.client.gui.utils.text.utils.Cursor;
import mchorse.mappet.client.gui.utils.text.utils.StringGroup;
import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IFocusedGuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.elements.utils.ITextColoring;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.mclib.client.gui.utils.ScrollDirection;
import mchorse.mclib.utils.MathUtils;
import mchorse.mclib.utils.undo.UndoManager;
import net.minecraft.class_155;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import org.lwjgl.input.Keyboard;

public class GuiMultiTextElement<T extends TextLine> extends GuiElement implements IFocusedGuiElement, ITextColoring {
   public ScrollArea horizontal = new ScrollArea();
   public ScrollArea vertical = new ScrollArea();
   public Consumer<String> callback;
   private boolean background;
   protected int padding = 10;
   protected int lineHeight = 12;
   protected int textColor = 16777215;
   protected boolean textShadow;
   protected boolean wrapping;
   private boolean focused;
   private int dragging;
   protected List<T> text = new ArrayList();
   public final Cursor cursor = new Cursor();
   public final Cursor selection = new Cursor(-1, 0);
   private int lastMX;
   private int lastMY;
   private long lastClick;
   private long update;
   private long lastUpdate;
   private StringGroup lastGroup;
   private UndoManager<GuiMultiTextElement> undo;
   private int lastW;

   public static List<String> splitNewlineString(String string) {
      List<String> splits = new ArrayList();
      StringBuilder builder = new StringBuilder();
      int i = 0;

      for(int c = string.length(); i < c; ++i) {
         char character = string.charAt(i);
         if (character == '\n') {
            splits.add(builder.toString());
            builder = new StringBuilder();
         } else {
            builder.append(character);
         }
      }

      splits.add(builder.toString());
      return splits;
   }

   public GuiMultiTextElement(class_310 mc, Consumer<String> callback) {
      super(mc);
      this.callback = callback;
      this.horizontal.direction = ScrollDirection.HORIZONTAL;
      this.horizontal.cancelScrollEdge = true;
      this.horizontal.scrollSpeed = this.lineHeight * 2;
      this.vertical.cancelScrollEdge = true;
      this.vertical.scrollSpeed = this.lineHeight * 2;
      this.clear();
   }

   public GuiMultiTextElement<T> background() {
      return this.background(true);
   }

   public GuiMultiTextElement<T> background(boolean background) {
      this.background = background;
      return this;
   }

   public GuiMultiTextElement<T> padding(int padding) {
      this.padding = padding;
      return this;
   }

   public GuiMultiTextElement<T> lineHeight(int lineHeight) {
      this.lineHeight = lineHeight;
      return this;
   }

   public GuiMultiTextElement<T> wrap() {
      return this.wrap(!this.wrapping);
   }

   public GuiMultiTextElement<T> wrap(boolean wrapping) {
      this.wrapping = wrapping;
      return this;
   }

   public void setColor(int textColor, boolean textShadow) {
      this.textColor = textColor;
      this.textShadow = textShadow;
   }

   public void setText(String text) {
      this.text.clear();

      for(String line : text.split("\n")) {
         this.text.add(this.createTextLine(line));
      }

      this.cursor.set(0, 0);
      this.selection.set(-1, 0);
      this.horizontal.scroll = 0;
      this.vertical.scroll = 0;
      this.undo = (new UndoManager(100)).simpleMerge();
      if (this.area.w > 0) {
         this.recalculateWrapping();
         this.recalculateSizes();
      }

   }

   protected T createTextLine(String line) {
      return (T)(new TextLine(line));
   }

   public String getText() {
      return (String)this.text.stream().map((t) -> t.text).collect(Collectors.joining("\n"));
   }

   public List<T> getLines() {
      return this.text;
   }

   public int getWrappedWidth() {
      return this.area.w - this.padding * 3 - this.getShiftX();
   }

   public boolean isSelected() {
      return !this.selection.isEmpty();
   }

   public void startSelecting() {
      this.selection.copy(this.cursor);
   }

   public void deselect() {
      this.selection.set(-1, 0);
   }

   public void swapSelection() {
      if (this.isSelected()) {
         Cursor temp = new Cursor();
         temp.copy(this.selection);
         this.selection.copy(this.cursor);
         this.cursor.copy(temp);
      }

   }

   public void selectAll() {
      this.cursor.set(0, 0);
      this.startSelecting();
      this.cursor.line = this.text.size() - 1;
      this.moveCursorToLineEnd();
   }

   public String getSelectedText() {
      return !this.isSelected() ? "" : this.getText(this.cursor, this.selection);
   }

   public String getText(Cursor a, Cursor b) {
      StringJoiner joiner = new StringJoiner("\n");
      Cursor min = a.isThisLessTo(b) ? a : b;
      Cursor max = a.isThisLessTo(b) ? b : a;

      for(int i = min.line; i <= Math.min(max.line, this.text.size() - 1); ++i) {
         String line = ((TextLine)this.text.get(i)).text;
         if (i == min.line && i == max.line) {
            joiner.add(line.substring(min.getOffset(line), max.getOffset(line)));
         } else if (i == min.line) {
            joiner.add(min.end(line));
         } else if (i == max.line) {
            joiner.add(max.start(line));
         } else {
            joiner.add(line);
         }
      }

      return joiner.toString();
   }

   public boolean selectGroup(int direction, boolean select) {
      List<Cursor> groups = this.findGroup(direction, this.cursor);
      if (groups.isEmpty()) {
         return false;
      } else {
         Cursor min = (Cursor)groups.get(0);
         Cursor max = (Cursor)groups.get(1);
         if (select) {
            if (direction == 0) {
               this.cursor.offset = max.offset;
               this.selection.set(this.cursor.line, min.offset);
            } else {
               if (!this.isSelected()) {
                  this.selection.copy(this.cursor);
               }

               this.cursor.offset = direction < 0 ? min.offset : max.offset;
            }
         } else {
            this.deselect();
            this.cursor.offset = direction < 0 ? min.offset : max.offset;
         }

         return true;
      }
   }

   public int measureGroup(int direction, Cursor cursor) {
      if (direction == 0) {
         return 0;
      } else {
         List<Cursor> group = this.findGroup(direction, cursor);
         if (group.isEmpty()) {
            return 0;
         } else {
            Cursor other = (Cursor)group.get(direction < 0 ? 0 : 1);
            return other.offset - cursor.offset;
         }
      }
   }

   public List<Cursor> findGroup(int direction, Cursor cursor) {
      String line = ((TextLine)this.text.get(cursor.line)).text;
      if (!line.isEmpty() && this.cursor.offset < line.length() - 1) {
         int offset = cursor.offset;
         int first = direction < 0 && offset > 0 ? offset - 1 : offset;
         String character = String.valueOf(line.charAt(first));
         StringGroup group = StringGroup.get(character);
         int min = offset;
         int max = offset;
         this.lastGroup = null;
         if (direction <= 0) {
            while(min > 0 && this.matchSelectGroup(group, String.valueOf(line.charAt(min - 1)))) {
               --min;
            }
         }

         this.lastGroup = null;
         if (direction >= 0) {
            while(max < line.length() && this.matchSelectGroup(group, String.valueOf(line.charAt(max)))) {
               ++max;
            }
         }

         return ImmutableList.of(new Cursor(cursor.line, min), new Cursor(cursor.line, max));
      } else {
         return Collections.emptyList();
      }
   }

   private boolean matchSelectGroup(StringGroup group, String character) {
      if (group.match(character)) {
         return this.lastGroup == null;
      } else if (group == StringGroup.SPACE) {
         if (this.lastGroup == null) {
            this.lastGroup = StringGroup.get(character);
         }

         return StringGroup.get(character) == this.lastGroup;
      } else {
         return false;
      }
   }

   public boolean selectTextful(String text, boolean reverse) {
      this.deselect();
      List<String> splits = splitNewlineString(text);
      this.selection.copy(this.cursor);

      for(int i = 0; i < splits.size(); ++i) {
         String line = ((TextLine)this.text.get(this.selection.line)).text;
         int l = ((String)splits.get(reverse ? splits.size() - (i + 1) : i)).length();
         Cursor var10000 = this.selection;
         var10000.offset += reverse ? -l : l;
         if (i < splits.size() - 1) {
            if (reverse && this.selection.offset < 0) {
               return false;
            }

            if (!reverse && this.selection.offset + l < line.length()) {
               return false;
            }

            var10000 = this.selection;
            var10000.line += reverse ? -1 : 1;
            this.selection.offset = reverse ? ((TextLine)this.text.get(this.selection.line)).text.length() : 0;
         }
      }

      return true;
   }

   public void checkSelection(boolean selecting) {
      if (selecting && !this.isSelected()) {
         this.startSelecting();
      } else if (!selecting && this.isSelected()) {
         this.deselect();
      }

   }

   public void clear() {
      this.setText("");
   }

   protected void changedLine(int i) {
      this.calculateWrappedLine((T)this.text.get(i));
      this.recalculateSizes();
   }

   protected void changedLineAfter(int i) {
      while(i < this.text.size()) {
         this.calculateWrappedLine((T)this.text.get(i));
         ++i;
      }

      this.recalculateSizes();
   }

   public void writeNewLine() {
      if (this.hasLine(this.cursor.line)) {
         String line = ((TextLine)this.text.get(this.cursor.line)).text;
         if (this.cursor.offset != 0 && !line.isEmpty()) {
            if (this.cursor.offset >= line.length()) {
               this.text.add(this.cursor.line + 1, this.createTextLine(""));
            } else {
               ((TextLine)this.text.get(this.cursor.line)).set(this.cursor.start(line));
               this.text.add(this.cursor.line + 1, this.createTextLine(this.cursor.end(line)));
               this.moveCursorToLineStart();
            }
         } else {
            this.text.add(this.cursor.line, this.createTextLine(""));
         }

         this.changedLineAfter(this.cursor.line);
         ++this.cursor.line;
         this.cursor.offset = 0;
      }
   }

   public void writeCharacter(String character) {
      if (this.hasLine(this.cursor.line)) {
         String line = ((TextLine)this.text.get(this.cursor.line)).text;
         int index = this.cursor.offset;
         if (index >= line.length()) {
            line = line + character;
         } else if (index == 0) {
            line = character + line;
         } else {
            line = this.cursor.start(line) + character + this.cursor.end(line);
         }

         ((TextLine)this.text.get(this.cursor.line)).set(line);
         this.changedLine(this.cursor.line);
      }

   }

   public void writeString(String string) {
      List<String> splits = splitNewlineString(string);
      int size = splits.size();
      if (size == 1) {
         this.writeCharacter(string);
         Cursor var10000 = this.cursor;
         var10000.offset += string.length();
      } else {
         int line = this.cursor.line;
         String remainder = this.cursor.end(((TextLine)this.text.get(line)).text);
         ((TextLine)this.text.get(line)).set(this.cursor.start(((TextLine)this.text.get(line)).text));

         for(int i = 0; i < size; ++i) {
            if (i != 0 && i <= size - 1) {
               ++this.cursor.line;
               this.moveCursorToLineStart();
               this.text.add(this.cursor.line, this.createTextLine(""));
            }

            this.writeCharacter((String)splits.get(i));
         }

         this.cursor.offset = ((String)splits.get(size - 1)).length();
         this.writeCharacter(remainder);
         this.changedLineAfter(line);
      }

   }

   public void pasteText(String text) {
      TextEditUndo undo = new TextEditUndo(this);
      this.deleteSelection();
      this.writeString(text);
      undo.ready().post(text, this.cursor, this.selection);
      this.undo.pushUndo(undo);
   }

   public String deleteCharacter() {
      if (this.hasLine(this.cursor.line)) {
         String line = ((TextLine)this.text.get(this.cursor.line)).text;
         int index = Math.min(this.cursor.offset, line.length());
         if (line.isEmpty()) {
            if (this.cursor.line > 0) {
               this.text.remove(this.cursor.line);
               --this.cursor.line;
               this.moveCursorToLineEnd();
               this.changedLineAfter(this.cursor.line);
               return "\n";
            }
         } else {
            if (index >= line.length()) {
               String deleted = line.substring(line.length() - 1);
               line = line.substring(0, line.length() - 1);
               ((TextLine)this.text.get(this.cursor.line)).set(line);
               this.moveCursorToLineEnd();
               this.changedLine(this.cursor.line);
               return deleted;
            }

            if (index != 0) {
               String deleted = line.substring(this.cursor.getOffset(line, -1), this.cursor.getOffset(line));
               String var10000 = this.cursor.start(line, -1);
               line = var10000 + this.cursor.end(line);
               ((TextLine)this.text.get(this.cursor.line)).text = line;
               this.moveCursor(-1, 0);
               this.changedLine(this.cursor.line);
               return deleted;
            }

            if (this.cursor.line > 0) {
               String text = ((TextLine)this.text.remove(this.cursor.line)).text;
               --this.cursor.line;
               this.moveCursorToLineEnd();
               ((TextLine)this.text.get(this.cursor.line)).text = ((TextLine)this.text.get(this.cursor.line)).text + text;
               this.changedLineAfter(this.cursor.line);
               return "\n";
            }
         }
      }

      return "";
   }

   public void deleteSelection() {
      if (this.isSelected()) {
         Cursor min = this.getMin();
         Cursor max = this.getMax();
         if (min.line == max.line) {
            String line = ((TextLine)this.text.get(min.line)).text;
            if (min.offset <= 0 && max.offset >= line.length()) {
               ((TextLine)this.text.get(min.line)).set("");
            } else {
               TextLine var10000 = (TextLine)this.text.get(min.line);
               String var10001 = min.start(line);
               var10000.set(var10001 + max.end(line));
            }
         } else {
            String end = "";

            for(int i = max.line; i >= min.line; --i) {
               String line = ((TextLine)this.text.get(i)).text;
               if (i == max.line) {
                  end = max.end(line);
                  this.text.remove(i);
               } else if (i == min.line) {
                  TextLine var7 = (TextLine)this.text.get(i);
                  String var8 = min.start(line);
                  var7.set(var8 + end);
               } else {
                  this.text.remove(i);
               }
            }
         }

         this.changedLineAfter(min.line);
         this.cursor.copy(min);
         this.deselect();
      }
   }

   public boolean hasLine(int line) {
      return line >= 0 && line < this.text.size();
   }

   public Cursor getMin() {
      return this.selection.isThisLessTo(this.cursor) ? this.selection : this.cursor;
   }

   public Cursor getMax() {
      return this.selection.isThisLessTo(this.cursor) ? this.cursor : this.selection;
   }

   public void moveCursor(int x, int y) {
      this.moveCursor(x, y, true);
   }

   public void moveCursor(int x, int y, boolean jumpLine) {
      if (this.hasLine(this.cursor.line)) {
         String line = ((TextLine)this.text.get(this.cursor.line)).text;
         if (x != 0) {
            int nx = this.cursor.offset + (x > 0 ? 1 : -1);
            if (nx < 0) {
               if (jumpLine) {
                  if (this.hasLine(this.cursor.line - 1)) {
                     --this.cursor.line;
                     this.moveCursorToLineEnd();
                  }
               } else {
                  this.moveCursorToLineStart();
               }
            } else if (nx > line.length()) {
               if (jumpLine) {
                  if (this.hasLine(this.cursor.line + 1)) {
                     ++this.cursor.line;
                     this.moveCursorToLineStart();
                  }
               } else {
                  this.moveCursorToLineEnd();
               }
            } else {
               this.cursor.offset = nx;
            }
         }

         if (y != 0) {
            int ny = this.cursor.line + (y > 0 ? 1 : -1);
            if (this.hasLine(ny)) {
               this.cursor.line = ny;
               this.cursor.offset = MathUtils.clamp(this.cursor.offset, 0, ((TextLine)this.text.get(this.cursor.line)).text.length());
            }
         }

      }
   }

   public void moveCursorToLineStart() {
      this.cursor.offset = 0;
   }

   public void moveCursorToLineEnd() {
      if (this.hasLine(this.cursor.line)) {
         this.cursor.offset = ((TextLine)this.text.get(this.cursor.line)).text.length();
      }

   }

   public void moveCursorTo(Cursor cursor, int x, int y) {
      x -= this.area.x + this.padding;
      y -= this.area.y + this.padding;
      x += this.horizontal.scroll - this.getShiftX();
      y += this.vertical.scroll;
      if (this.wrapping) {
         this.moveToCursorWrapped(cursor, x, y);
      } else {
         this.moveCursorToUnwrapped(cursor, x, y);
      }

   }

   private void moveToCursorWrapped(Cursor cursor, int x, int y) {
      if (!this.text.isEmpty()) {
         T current = null;
         int line = y < 0 ? 0 : y / this.lineHeight;
         int l = 0;
         int s = 0;
         int i = 0;

         for(int c = this.text.size(); i < c; ++i) {
            T textLine = (T)(this.text.get(i));
            if (line >= l && line < l + textLine.getLines()) {
               current = textLine;
               cursor.line = i;
               s = line - l;
               break;
            }

            l += textLine.getLines();
         }

         if (current == null) {
            current = (T)(this.text.get(this.text.size() - 1));
            cursor.line = this.text.size() - 1;
            s = current.getLines() - 1;
         }

         cursor.offset = 0;
         String lineText = current.text;
         if (current.wrappedLines != null) {
            for(int j = 0; j < s; ++j) {
               cursor.offset += ((String)current.wrappedLines.get(j)).length();
            }

            lineText = (String)current.wrappedLines.get(s);
         }

         int w = 0;
         if (x > this.font.method_1727(lineText)) {
            cursor.offset += lineText.length();
         } else if (x >= 0) {
            for(int j = 0; x > w; ++j) {
               w = this.font.method_1727(lineText.substring(0, j));
               ++cursor.offset;
            }

            if (cursor.offset > 0) {
               cursor.offset -= 2;
            }

         }
      }
   }

   private void moveCursorToUnwrapped(Cursor cursor, int x, int y) {
      cursor.line = MathUtils.clamp(y / this.lineHeight, 0, this.text.size() - 1);
      String line = ((TextLine)this.text.get(cursor.line)).text;
      int w = this.font.method_1727(line);
      if (x <= 0) {
         this.moveCursorToLineStart();
      } else if (x > w) {
         this.moveCursorToLineEnd();
      } else {
         cursor.offset = 0;

         for(int var6 = this.font.method_1727(cursor.start(line)); x > var6; ++cursor.offset) {
            var6 = this.font.method_1727(cursor.start(line, 1));
         }

         if (cursor.offset > 0) {
            --cursor.offset;
         }
      }

   }

   public void moveViewportToCursor() {
      if (this.hasLine(this.cursor.line)) {
         Vector2d pos = this.getCursorPosition(this.cursor);
         pos.x += (double)this.horizontal.scroll;
         pos.y += (double)this.vertical.scroll;
         int w = 4;
         int h = this.lineHeight;
         this.horizontal.scrollIntoView((int)pos.x, w + this.padding * 2, this.getShiftX());
         this.vertical.scrollIntoView((int)pos.y, h + this.padding * 2, this.getShiftX());
      }
   }

   public boolean isFocused() {
      return this.focused;
   }

   public void focus(GuiContext context) {
      this.focused = true;
      Keyboard.enableRepeatEvents(true);
   }

   public void unfocus(GuiContext context) {
      this.focused = false;
      Keyboard.enableRepeatEvents(false);
   }

   public void selectAll(GuiContext context) {
      this.selectAll();
   }

   public void unselect(GuiContext context) {
      this.deselect();
   }

   public void resize() {
      super.resize();
      if (this.lastW != this.area.w) {
         this.lastW = this.area.w;
         this.recalculateWrapping();
      }

      this.recalculateSizes();
      this.horizontal.clamp();
      this.vertical.clamp();
   }

   public void recalculate() {
      for(T textLine : this.text) {
         this.calculateWrappedLine(textLine);
      }

      this.recalculateSizes();
   }

   protected void recalculateWrapping() {
      if (this.wrapping) {
         for(T textLine : this.text) {
            this.calculateWrappedLine(textLine);
         }
      }

   }

   protected void calculateWrappedLine(T textLine) {
      if (this.wrapping) {
         textLine.calculateWrappedLines(this.font, this.getWrappedWidth());
      } else {
         textLine.resetWrapping();
      }

   }

   protected void recalculateSizes() {
      int w = 0;
      int h = 0;

      for(T textLine : this.text) {
         if (!this.wrapping) {
            w = Math.max(this.font.method_1727(textLine.text), w);
         }

         h += textLine.getLines() * this.lineHeight;
      }

      int offset = this.getShiftX();
      this.horizontal.copy(this.area);
      ScrollArea var10000 = this.horizontal;
      var10000.x += offset;
      var10000 = this.horizontal;
      var10000.w -= offset;
      this.horizontal.scrollSize = this.wrapping ? w : this.getHorizontalSize(w);
      this.vertical.copy(this.area);
      var10000 = this.vertical;
      int var10002 = this.lineHeight;
      Objects.requireNonNull(this.font);
      var10000.scrollSize = h - (var10002 - 9) + this.padding * 2;
   }

   public boolean mouseClicked(GuiContext context) {
      if (super.mouseClicked(context)) {
         return true;
      } else if (!this.horizontal.mouseClicked(context) && !this.vertical.mouseClicked(context)) {
         boolean wasFocused = this.focused;
         boolean shift = GuiUtils.isShiftKeyDown();
         this.focused = this.area.isInside(context);
         if (this.focused) {
            if (context.mouseButton == 0) {
               if (System.currentTimeMillis() < this.lastClick) {
                  this.selectGroup(0, true);
                  this.lastClick -= 500L;
               } else {
                  if (!shift) {
                     this.deselect();
                     this.dragging = 1;
                  } else if (!this.isSelected()) {
                     this.startSelecting();
                  }

                  this.moveCursorTo(this.cursor, context.mouseX, context.mouseY);
                  this.lastClick = System.currentTimeMillis() + 200L;
               }
            } else if (context.mouseButton == 2) {
               this.dragging = 3;
            }

            this.lastMX = context.mouseX;
            this.lastMY = context.mouseY;
         }

         if (wasFocused != this.focused) {
            context.focus(wasFocused ? null : this);
         }

         return this.focused;
      } else {
         return true;
      }
   }

   public boolean mouseScrolled(GuiContext context) {
      if (super.mouseScrolled(context)) {
         return true;
      } else if (GuiUtils.isShiftKeyDown()) {
         return this.horizontal.mouseScroll(context);
      } else {
         return this.vertical.scrollSize < this.area.h ? false : this.vertical.mouseScroll(context);
      }
   }

   public void mouseReleased(GuiContext context) {
      super.mouseReleased(context);
      this.horizontal.mouseReleased(context);
      this.vertical.mouseReleased(context);
      this.dragging = 0;
   }

   public boolean keyTyped(GuiContext context) {
      if (super.keyTyped(context)) {
         return true;
      } else if (!this.focused) {
         return false;
      } else if (context.keyCode == 1) {
         context.unfocus();
         return false;
      } else {
         boolean ctrl = GuiUtils.isCtrlKeyDown();
         boolean shift = GuiUtils.isShiftKeyDown();
         TextEditUndo undo = new TextEditUndo(this);
         if (this.handleKeys(context, undo, ctrl, shift)) {
            this.moveViewportToCursor();
         }

         if (undo.ready) {
            this.undo.pushUndo(undo);
         }

         this.update = context.tick + 20L;
         this.horizontal.clamp();
         this.vertical.clamp();
         return false;
      }
   }

   protected boolean handleKeys(GuiContext context, TextEditUndo undo, boolean ctrl, boolean shift) {
      if (ctrl && context.keyCode == 44) {
         boolean result = this.undo.undo(this);
         if (result) {
            this.playSound(class_3417.field_14823);
         }

         return result;
      } else if (ctrl && context.keyCode == 21) {
         boolean result = this.undo.redo(this);
         if (result) {
            this.playSound(class_3417.field_14982);
         }

         return result;
      } else if (ctrl && context.keyCode == 30) {
         this.selectAll();
         return false;
      } else if (context.keyCode != 200 && context.keyCode != 208 && context.keyCode != 205 && context.keyCode != 203) {
         if (context.keyCode == 199) {
            this.checkSelection(shift);
            this.moveCursorToLineStart();
            this.playSound(class_3417.field_14600);
            return true;
         } else if (context.keyCode == 207) {
            this.checkSelection(shift);
            this.moveCursorToLineEnd();
            this.playSound(class_3417.field_15151);
            return true;
         } else if (ctrl && (context.keyCode == 46 || context.keyCode == 45) && this.isSelected()) {
            GuiUtils.setClipboardString(this.getSelectedText());
            if (context.keyCode == 45) {
               this.deleteSelection();
               this.deselect();
               undo.ready().post("", this.cursor, this.selection);
               this.playSound(class_3417.field_14559);
            } else {
               this.playSound(class_3417.field_15197);
            }

            return context.keyCode == 45;
         } else if (ctrl && context.keyCode == 47) {
            String pasted = GuiUtils.getClipboardString();
            this.deleteSelection();
            this.deselect();
            this.writeString(pasted);
            undo.ready().post(pasted, this.cursor, this.selection);
            this.playSound(class_3417.field_15152);
            return true;
         } else if (ctrl && context.keyCode == 32) {
            this.deselect();
            String copy = ((TextLine)this.text.get(this.cursor.line)).text;
            this.moveCursorToLineEnd();
            this.writeNewLine();
            this.moveCursorToLineStart();
            this.writeString(copy);
            undo.ready().post(copy + "\n" + copy, this.cursor, this.selection);
            this.playSound(class_3417.field_15152);
            return true;
         } else if (context.keyCode == 15) {
            this.keyTab(undo.ready());
            undo.post(undo.postText, this.cursor, this.selection);
            this.playSound(GuiUtils.isShiftKeyDown() ? class_3417.field_15228 : class_3417.field_15134);
            return true;
         } else if (ctrl && context.keyCode == 53) {
            Cursor min = new Cursor();
            Cursor max = new Cursor();
            if (this.isSelected()) {
               min.copy(this.getMin());
               max.copy(this.getMax());
            } else {
               min.copy(this.cursor);
               max.copy(this.cursor);
            }

            int numCommentedLines = 0;
            int numUncommentedLines = 0;

            for(int i = min.line; i <= max.line; ++i) {
               String line = ((TextLine)this.text.get(i)).text;
               if (line.startsWith("//")) {
                  ++numCommentedLines;
               } else {
                  ++numUncommentedLines;
               }
            }

            for(int i = min.line; i <= max.line; ++i) {
               String line = ((TextLine)this.text.get(i)).text;
               if (numUncommentedLines == 0 && numCommentedLines > 0) {
                  if (line.startsWith("//")) {
                     ((TextLine)this.text.get(i)).set(line.substring(2));
                  }
               } else if (numUncommentedLines > 0 && !line.startsWith("//")) {
                  ((TextLine)this.text.get(i)).set("//" + line);
               }
            }

            if (this.isSelected()) {
               String selected = this.getSelectedText();
               this.deleteSelection();
               this.writeString(selected);
            } else {
               String currentLine = ((TextLine)this.text.get(this.cursor.line)).text;
               ((TextLine)this.text.get(this.cursor.line)).set("");
               this.writeString(currentLine);
            }

            undo.ready().post("", this.cursor, this.selection);
            this.changedLineAfter(min.line);
            this.playSound(class_3417.field_14731);
            return true;
         } else if (context.keyCode == 28) {
            this.keyNewLine(undo.ready());
            undo.post(undo.postText, this.cursor, this.selection);
            this.playSound(class_3417.field_14879);
            return true;
         } else if (context.keyCode != 14 && context.keyCode != 211) {
            if (class_155.method_643(context.typedChar)) {
               String character = this.getFromChar(context.typedChar);
               if (!character.isEmpty()) {
                  this.deleteSelection();
                  this.deselect();
                  this.writeCharacter(character);
                  this.moveCursor(1, 0);
                  undo.ready().post(character, this.cursor, this.selection);
                  this.playSound(class_3417.field_14574);
               }

               return true;
            } else {
               return false;
            }
         } else {
            boolean delete = context.keyCode == 211;
            if (this.isSelected()) {
               this.deleteSelection();
               this.deselect();
               this.playSound(class_3417.field_15152);
            } else {
               if (delete) {
                  int measure = ctrl ? Math.max(this.measureGroup(1, this.cursor), 1) : 1;

                  for(int i = 0; i < measure; ++i) {
                     this.moveCursor(1, 0);
                     String var10001 = undo.text;
                     undo.text = var10001 + this.deleteCharacter();
                  }
               } else {
                  this.keyBackspace(undo, ctrl);
               }

               this.playSound(class_3417.field_15026);
            }

            undo.ready().post("", this.cursor, this.selection);
            return true;
         }
      } else {
         int x = context.keyCode == 205 ? 1 : (context.keyCode == 203 ? -1 : 0);
         int y = context.keyCode == 200 ? -1 : (context.keyCode == 208 ? 1 : 0);
         if (x != 0 && ctrl) {
            if (!this.selectGroup(x, shift)) {
               this.checkSelection(shift);
               this.moveCursor(x, 0);
            }
         } else {
            this.checkSelection(shift);
            this.moveCursor(x, y);
         }

         this.playSound(class_3417.field_15181);
         return true;
      }
   }

   protected void playSound(class_3414 event) {
      if ((Boolean)Mappet.scriptEditorSounds.get()) {
         GuiMappetUtils.playSound(event);
      }

   }

   protected String getFromChar(char typedChar) {
      return String.valueOf(typedChar);
   }

   protected void keyNewLine(TextEditUndo undo) {
      this.deleteSelection();
      this.deselect();
      this.writeNewLine();
      undo.postText = undo.postText + "\n";
   }

   protected void keyBackspace(TextEditUndo undo, boolean ctrl) {
      int measure = ctrl ? Math.max(Math.abs(this.measureGroup(-1, this.cursor)), 1) : 1;

      for(int i = 0; i < measure; ++i) {
         String var10001 = this.deleteCharacter();
         undo.text = var10001 + undo.text;
      }

   }

   protected void keyTab(TextEditUndo undo) {
      undo.postText = "    ";
      this.deleteSelection();
      this.deselect();
      this.writeString(undo.postText);
   }

   public void draw(GuiContext context) {
      this.handleLogic(context);
      if (this.background) {
         this.drawBackground();
      }

      super.draw(context);
      GuiDraw.scissor(this.area.x, this.area.y, this.area.w, this.area.h, context);
      int x = this.area.x + this.padding;
      int y = this.area.y + this.padding;
      Cursor min = this.getMin();
      Cursor max = this.getMax();
      if (this.isSelected()) {
         this.drawSelectionBar(x, y, min, max);
      }

      int i = 0;

      for(int ci = this.text.size(); i < ci; ++i) {
         T textLine = (T)(this.text.get(i));
         String line = textLine.text;
         int newX = x - this.horizontal.scroll + this.getShiftX();
         int newY = y - this.vertical.scroll;
         if (newY > this.area.ey()) {
            break;
         }

         boolean drawCursor = this.cursor.line == i && this.focused;
         int lines = textLine.getLines() - 1;
         Objects.requireNonNull(this.font);
         if (newY + 9 + lines * this.lineHeight >= this.area.y) {
            int cursorW = 0;
            int cursorA = 0;
            if (drawCursor) {
               cursorW = line.isEmpty() ? 0 : this.font.method_1727(this.cursor.start(line));
               cursorA = (int)(Math.sin((double)((float)context.tick + context.partialTicks) / (double)2.0F) * (double)127.5F + (double)127.5F) << 24;
            }

            if (textLine.wrappedLines == null) {
               if (drawCursor) {
                  int var10000 = newX + cursorW;
                  int var10001 = newY - 1;
                  int var10002 = newX + cursorW + 1;
                  Objects.requireNonNull(this.font);
                  GuiDraw.drawRect(var10000, var10001, var10002, newY + 9 + 1, cursorA + 16777215);
               }

               this.drawTextLine(line, i, 0, newX, newY);
            } else {
               int wrappedW = 0;
               int j = 0;

               for(int cj = textLine.wrappedLines.size(); j < cj; ++j) {
                  String wrappedLine = (String)textLine.wrappedLines.get(j);
                  int lineW = this.font.method_1727(wrappedLine);
                  int lineY = newY + j * this.lineHeight;
                  if (cursorW >= wrappedW && cursorW < wrappedW + lineW || this.cursor.offset >= textLine.text.length()) {
                     int var22 = newX + cursorW - wrappedW;
                     int var23 = lineY - 1;
                     int var24 = newX + cursorW - wrappedW + 1;
                     Objects.requireNonNull(this.font);
                     GuiDraw.drawRect(var22, var23, var24, lineY + 9 + 1, cursorA + 16777215);
                  }

                  this.drawTextLine(wrappedLine, i, j, newX, lineY);
                  wrappedW += lineW;
               }
            }
         }

         y += textLine.getLines() * this.lineHeight;
      }

      this.horizontal.drawScrollbar();
      this.vertical.drawScrollbar();
      this.drawForeground(context);
      GuiDraw.unscissor(context);
   }

   protected int getShiftX() {
      return 0;
   }

   protected int getHorizontalSize(int w) {
      return w + this.padding * 2 + this.getShiftX();
   }

   protected void drawTextLine(String line, int i, int j, int nx, int ny) {
      GuiDraw.drawString(this.font, line, nx, ny, this.textColor, this.textShadow);
   }

   protected void drawBackground() {
      this.area.draw(-6250336);
      this.area.draw(-16777216, 1);
   }

   protected void drawForeground(GuiContext context) {
   }

   private void handleLogic(GuiContext context) {
      if (this.update > this.lastUpdate) {
         this.lastUpdate = this.update;
         if (this.callback != null) {
            this.callback.accept(this.getText());
         }
      }

      if (this.dragging == 1 && (Math.abs(context.mouseX - this.lastMX) > 4 || Math.abs(context.mouseY - this.lastMY) > 4)) {
         this.startSelecting();
         this.dragging = 2;
      }

      if (this.focused && this.dragging == 2) {
         this.moveCursorTo(this.cursor, context.mouseX, context.mouseY);
         this.moveViewportToCursor();
      }

      if (this.dragging == 3) {
         ScrollArea var10000 = this.horizontal;
         var10000.scroll += this.lastMX - context.mouseX;
         this.horizontal.clamp();
         var10000 = this.vertical;
         var10000.scroll += this.lastMY - context.mouseY;
         this.vertical.clamp();
         this.lastMX = context.mouseX;
         this.lastMY = context.mouseY;
      }

      this.horizontal.drag(context);
      this.vertical.drag(context);
   }

   private void drawSelectionBar(int x, int y, Cursor min, Cursor max) {
      Vector2d minPos = this.getCursorPosition(min);
      Vector2d maxPos = this.getCursorPosition(max);
      this.drawSelectionArea(x + (int)minPos.x, y + (int)minPos.y, x + (int)maxPos.x, y + (int)maxPos.y);
   }

   protected Vector2d getCursorPosition(Cursor cursor) {
      Vector2d pos = new Vector2d();
      if (this.wrapping) {
         this.getCusrorPositionWrapped(cursor, pos);
      } else {
         String line = ((TextLine)this.text.get(cursor.line)).text;
         pos.x = (double)this.font.method_1727(cursor.start(line));
         pos.y = (double)(cursor.line * this.lineHeight);
      }

      pos.x = pos.x - (double)this.horizontal.scroll + (double)this.getShiftX();
      pos.y -= (double)this.vertical.scroll;
      return pos;
   }

   private void getCusrorPositionWrapped(Cursor cursor, Vector2d pos) {
      int lines = 0;
      int offset = 0;
      int i = 0;

      for(int c = this.text.size(); i < c; ++i) {
         T textLine = (T)(this.text.get(i));
         int textLines = textLine.getLines();
         if (i == cursor.line) {
            if (textLine.wrappedLines == null) {
               offset = this.font.method_1727(cursor.start(textLine.text));
            } else {
               int textOffset = 0;

               for(int j = 0; j < textLine.wrappedLines.size(); ++j) {
                  String wrappedLine = (String)textLine.wrappedLines.get(j);
                  if (cursor.offset >= textOffset && cursor.offset < textOffset + wrappedLine.length()) {
                     offset = this.font.method_1727(wrappedLine.substring(0, cursor.offset - textOffset));
                     break;
                  }

                  ++lines;
                  textOffset += wrappedLine.length();
               }

               if (cursor.offset >= textLine.text.length()) {
                  --lines;
                  offset = this.font.method_1727((String)textLine.wrappedLines.get(textLine.wrappedLines.size() - 1));
               }
            }
            break;
         }

         lines += textLines;
      }

      pos.x = (double)offset;
      pos.y = (double)(lines * this.lineHeight);
   }

   private void drawSelectionArea(int x1, int y1, int x2, int y2) {
      int selectionPad = 2;
      int color = -2013265920 + (Integer)McLib.primaryColor.get();
      boolean middle = y2 > y1 + this.lineHeight;
      boolean bottom = y2 > y1;
      int endX = !bottom && !middle ? x2 + 2 : this.area.ex();
      int var10000;
      if (bottom && !middle) {
         var10000 = y2;
      } else {
         Objects.requireNonNull(this.font);
         var10000 = y1 + 9;
      }

      int endY = var10000;
      if (!bottom && !middle) {
         endY += 2;
      }

      GuiDraw.drawRect(x1 - 2, y1 - 2, endX, endY, color);
      if (middle) {
         var10000 = this.area.x;
         Objects.requireNonNull(this.font);
         GuiDraw.drawRect(var10000, y1 + 9, this.area.ex(), y2, color);
      }

      if (bottom) {
         var10000 = this.area.x;
         int var10002 = x2 + 2;
         Objects.requireNonNull(this.font);
         GuiDraw.drawRect(var10000, y2, var10002, y2 + 9 + 2, color);
      }

   }
}
