package mchorse.mappet.client.gui.scripts.vim;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mchorse.mappet.client.gui.scripts.GuiTextEditor;
import mchorse.mappet.client.gui.utils.text.utils.Cursor;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.GuiUtils;
import net.minecraft.class_327;
import org.lwjgl.input.Keyboard;

public class VimEngine {
   public static enum Mode {
      NORMAL("NORMAL", 0x87AF5F),
      INSERT("INSERT", 0x87D787),
      VISUAL("VISUAL", 0xAF87FF),
      VISUAL_LINE("VISUAL LINE", 0xAF87FF),
      COMMAND("COMMAND", 0x5FAFFF);

      public final String label;
      public final int color;

      Mode(String label, int color) {
         this.label = label;
         this.color = color;
      }
   }

   private static final class Change {
      final String before;
      final String after;
      final Cursor cursor;

      Change(String before, String after, Cursor cursor) {
         this.before = before;
         this.after = after;
         this.cursor = cursor;
      }
   }

   private static final class Motion {
      final Cursor cursor;
      final boolean linewise;
      final boolean inclusive;

      Motion(Cursor cursor, boolean linewise, boolean inclusive) {
         this.cursor = cursor;
         this.linewise = linewise;
         this.inclusive = inclusive;
      }
   }

   private static final class Range {
      final int line1;
      final int offset1;
      final int line2;
      final int offset2;

      Range(int line1, int offset1, int line2, int offset2) {
         this.line1 = line1;
         this.offset1 = offset1;
         this.line2 = line2;
         this.offset2 = offset2;
      }
   }

   private static final int MAX_HISTORY = 200;
   private static final int MESSAGE_TICKS = 60;
   private static final int INDENT = 4;
   private static final int MAX_COMMAND_LENGTH = 120;
   private static final char ESCAPE = '\u001b';
   private static final String OPEN = "([{";
   private static final String CLOSE = ")]}";

   private final GuiTextEditor editor;

   private Mode mode = Mode.NORMAL;
   private boolean enabled = true;

   private int count = -1;
   private char operator = 0;
   private char prefix = 0;

   private final StringBuilder commandLine = new StringBuilder();
   private int commandOffset = 0;
   private char commandPrefix = ':';

   private String unnamed = "";
   private boolean unnamedLinewise = false;
   private String named = null;
   private String namedText = "";
   private boolean namedLinewise = false;
   private String search = "";

   private final List<Change> history = new ArrayList<Change>();
   private int historyIndex = -1;
   private String pending = null;
   private boolean restoring;

   private final StringBuilder sequence = new StringBuilder();
   private String lastSequence = "";
   private boolean replaying;

   private boolean inserting;
   private String insertText = null;
   private Cursor insertCursor = null;
   private String insertCommand = "";

   private String message = "";
   private int messageTicks = 0;

   private Runnable saveAction;

   public VimEngine(GuiTextEditor editor) {
      this.editor = editor;
   }

   public Mode getMode() {
      return this.mode;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public VimEngine enabled(boolean enabled) {
      this.enabled = enabled;

      if (!enabled) {
         this.reset();
      }

      return this;
   }

   public VimEngine setSaveAction(Runnable saveAction) {
      this.saveAction = saveAction;

      return this;
   }

   public void reset() {
      this.mode = Mode.NORMAL;
      this.count = -1;
      this.operator = 0;
      this.prefix = 0;
      this.commandLine.setLength(0);
      this.commandOffset = 0;
      this.sequence.setLength(0);
      this.lastSequence = "";
      this.pending = null;
      this.inserting = false;
      this.insertText = null;
      this.insertCursor = null;
      this.editor.deselect();
      this.clampCursor();
   }

   public void onFocusLost() {
      if (this.mode == Mode.NORMAL) {
         return;
      }

      if (this.mode == Mode.INSERT) {
         this.leaveInsertMode();

         return;
      }

      this.sequence.setLength(0);
      this.count = -1;
      this.operator = 0;
      this.prefix = 0;
      this.commandLine.setLength(0);
      this.commandOffset = 0;
      this.resetMode();
   }

   /* ------------------------------------------------------------- keys */

   public boolean handleKey(GuiContext context) {
      if (!this.enabled) {
         return false;
      }

      int keyCode = context.keyCode;
      char key = context.typedChar;

      if (this.mode == Mode.COMMAND) {
         return this.handleCommandKey(keyCode, key);
      }

      if (this.mode == Mode.INSERT) {
         return false;
      }

      if (keyCode == 1) {
         this.sequence.setLength(0);
         this.count = -1;
         this.operator = 0;
         this.prefix = 0;
         this.resetMode();

         return true;
      }

      if (GuiUtils.isCtrlKeyDown()) {
         switch (keyCode) {
            case Keyboard.KEY_Z: this.undo(); return true;
            case Keyboard.KEY_Y: this.redo(); return true;
            case Keyboard.KEY_R: this.redo(); return true;
            case Keyboard.KEY_V: case Keyboard.KEY_C: case Keyboard.KEY_X: case 30: case 32: case 53: return true;
            case 14: {
               Motion word = this.motion('b', 1);

               return word != null && this.applyMotion(word);
            }
            default: return true;
         }
      }

      switch (keyCode) {
         case 14: return this.moveViewport(-1, 0);
         case 211: return this.moveViewport(1, 0);
         case 15: return true;
         case 28: {
            int n = this.count > 0 ? this.count : 1;

            this.finishCommand();

            int line = this.cursor().line + n;

            this.moveTo(new Cursor(line, this.firstNonBlank(line)));

            return true;
         }
         case 200: return this.moveViewport(0, -1);
         case 208: return this.moveViewport(0, 1);
         case 203: return this.moveViewport(-1, 0);
         case 205: return this.moveViewport(1, 0);
         case 199: this.moveTo(new Cursor(this.cursor().line, 0)); return true;
         case 207: this.moveTo(new Cursor(this.cursor().line, this.maxOffset(this.cursor().line))); return true;
         default:
      }

      if (key == 0 || key == 65533) {
         return false;
      }

      return this.handleNormalChar(key);
   }

   private boolean handleCommandKey(int keyCode, char key) {
      switch (keyCode) {
         case 1:
            this.commandLine.setLength(0);
            this.resetMode();

            return true;
         case 28:
            this.execute(this.commandPrefix + this.commandLine.toString());
            this.commandLine.setLength(0);
            this.resetMode();

            return true;
         case 259:
         case 262:
            if (this.commandOffset > 0) {
               this.commandLine.deleteCharAt(this.commandOffset - 1);
               this.commandOffset -= 1;
            }

            return true;
         case 261:
            if (this.commandOffset > 0) {
               this.commandOffset -= 1;
            }

            return true;
         case 263:
            if (this.commandOffset < this.commandLine.length()) {
               this.commandOffset += 1;
            }

            return true;
      }

      if (key >= ' ' && this.commandLine.length() < MAX_COMMAND_LENGTH) {
         this.commandLine.insert(this.commandOffset, key);
         this.commandOffset += 1;
      }

      return true;
   }

   private boolean handleNormalChar(char key) {
      if (this.prefix != 0) {
         return this.handlePrefixTarget(key);
      }

      if (this.mode == Mode.VISUAL || this.mode == Mode.VISUAL_LINE) {
         return this.handleVisualChar(key);
      }

      boolean hasCount = this.count > 0;
      int n = hasCount ? this.count : 1;

      if (this.operator != 0) {
         char op = this.operator;

         if (key == op || (op == 'c' && (key == 's' || key == 'S'))) {
            this.finishCommand();

            return this.linewise(op, n);
         }

         if (key == 'i' || key == 'a' || key == 'g' || key == 'f' || key == 'F' || key == 't' || key == 'T') {
            this.prefix = key;

            return true;
         }

         if (key >= '1' && key <= '9') {
            this.count = (hasCount ? this.count : 0) * 10 + (key - '0');

            return true;
         }

         if (key == '0' && hasCount) {
            this.count = this.count * 10;

            return true;
         }

         Motion operatorMotion = this.motion(key, n);
         this.finishCommand();

         return operatorMotion == null ? false : this.operate(op, operatorMotion);
      }

      if (key >= '0' && key <= '9') {
         if (key == '0' && !hasCount) {
            this.finishCommand();

            return this.applyMotion(new Motion(new Cursor(this.cursor().line, 0), false, true));
         }

         this.count = (hasCount ? this.count : 0) * 10 + (key - '0');

         return true;
      }

      switch (key) {
         case 'i': this.finishCommand(); this.enterInsertMode(this.cursor().offset); return true;
         case 'I': this.finishCommand(); this.enterInsertMode(this.firstNonBlank(this.cursor().line)); return true;
         case 'a': this.finishCommand(); this.enterInsertMode(Math.min(this.length(this.cursor().line), this.cursor().offset + 1)); return true;
         case 'A': this.finishCommand(); this.enterInsertMode(this.length(this.cursor().line)); return true;
         case 'o': this.finishCommand(); this.openLine(true); this.enterInsertMode(0); return true;
         case 'O': this.finishCommand(); this.openLine(false); this.enterInsertMode(0); return true;
         case 'R': this.finishCommand(); this.enterInsertMode(this.cursor().offset); return true;
         case 'v': this.finishCommand(); this.toggleVisual(Mode.VISUAL); return true;
         case 'V': this.finishCommand(); this.toggleVisual(Mode.VISUAL_LINE); return true;
         case 'x': this.finishCommand(); this.deleteChars(n); return true;
         case 'X': this.finishCommand(); this.deleteCharsBackwards(n); return true;
         case 'D': this.finishCommand(); return this.deleteToLineEnd();
         case 'C': this.finishCommand(); this.deleteToLineEnd(); this.enterInsertMode(this.cursor().offset); return true;
         case 's': this.finishCommand(); this.deleteChars(n); this.enterInsertMode(this.cursor().offset); return true;
         case 'S': this.finishCommand(); this.changeLine(); return true;
         case 'Y': this.finishCommand(); this.setRegister(this.yankLines(this.cursor().line, this.cursor().line), true); return true;
         case 'J': this.finishCommand(); this.joinLines(n); return true;
         case 'p': this.finishCommand(); this.put(true); return true;
         case 'P': this.finishCommand(); this.put(false); return true;
         case 'u': this.finishCommand(); this.undo(); return true;
         case '.': this.finishCommand(); this.replay(); return true;
         case '~': this.finishCommand(); this.toggleCase(n); return true;
         case ':': this.finishCommand(); this.openCommandLine(':'); return true;
         case '/':
            this.finishCommand();
            this.openCommandLine('/');
            this.commandLine.append(this.search);
            this.commandOffset = this.commandLine.length();

            return true;
         case 'n': this.finishCommand(); return this.applyMotion(new Motion(this.searchCursor(false, n), false, false));
         case 'N': this.finishCommand(); return this.applyMotion(new Motion(this.searchCursor(true, n), false, false));
         case 'd': case 'y': case 'c': case '>': case '<': case '=': this.operator = key; return true;
         case 'g': case 'f': case 'F': case 't': case 'T': case 'r': case '"': case 'z': this.prefix = key; return true;
      }

      Motion motion = this.motion(key, n);

      if (motion == null) {
         return false;
      }

      this.finishCommand();

      return this.applyMotion(motion);
   }

   private boolean handleVisualChar(char key) {
      switch (key) {
         case 'v': this.finishCommand(); this.toggleVisual(Mode.VISUAL); return true;
         case 'V': this.finishCommand(); this.toggleVisual(Mode.VISUAL_LINE); return true;
         case 'o': this.finishCommand(); this.editor.swapSelection(); return true;
         case 'd': case 'x': this.visualOperate('d'); return true;
         case 'c': case 's': this.visualOperate('c'); return true;
         case 'y': this.visualOperate('y'); return true;
         case 'p': this.visualOperate('p'); return true;
         case '>': this.visualOperate('>'); return true;
         case '<': this.visualOperate('<'); return true;
         case 'U': case 'u': case '~': this.visualOperate(key); return true;
         case 'i': case 'a': case 'g': case 'f': case 'F': case 't': case 'T': case 'r': case '"': this.prefix = key; return true;
         case '0': this.finishCommand(); return this.applyMotion(new Motion(new Cursor(this.cursor().line, 0), false, true));
         default:
      }

      if (key >= '1' && key <= '9') {
         return true;
      }

      Motion motion = this.motion(key, 1);

      if (motion == null) {
         return false;
      }

      this.finishCommand();

      return this.applyMotion(motion);
   }

   private boolean handlePrefixTarget(char key) {
      char p = this.prefix;
      int n = this.count > 0 ? this.count : 1;
      this.prefix = 0;

      if (p == 'g') {
         char op = this.operator;

         if (key == 'g') {
            int line = n > 1 ? Math.min(n - 1, this.lines() - 1) : 0;
            Motion motion = new Motion(new Cursor(line, 0), true, false);
            this.finishCommand();

            return op == 0 ? this.applyMotion(motion) : this.operate(op, motion);
         }

         if (key == 'U' || key == 'u' || key == '~') {
            Motion motion = this.motion('e', n);
            this.finishCommand();

            if (motion == null) {
               return true;
            }

            return this.operate(key, motion);
         }

         this.finishCommand();

         return true;
      }

      if (p == 'f' || p == 'F' || p == 't' || p == 'T') {
         char op = this.operator;
         boolean backward = p == 'F' || p == 'T';
         boolean till = p == 't' || p == 'T';
         Cursor c = this.cursor();
         String line = this.editor.getLineText(c.line);
         int o = c.offset;

         for (int i = 0; i < n; i++) {
            int f = backward ? line.lastIndexOf(key, Math.max(0, o - 1)) : line.indexOf(key, o + 1);

            if (f < 0) {
               break;
            }

            o = f;
         }

         if (till) {
            o = backward ? Math.max(0, o - 1) : Math.min(Math.max(0, line.length() - 1), o + 1);
         }

         Motion motion = new Motion(new Cursor(c.line, o), false, true);
         this.finishCommand();

         return op == 0 ? this.applyMotion(motion) : this.operate(op, motion);
      }

      if (p == 'r') {
         this.finishCommand();
         this.replaceChar(key, n);

         return true;
      }

      if (p == '"') {
         if (Character.isLetter(key)) {
            this.named = String.valueOf(key);
            this.namedText = "";
         }

         return true;
      }

      if (p == 'z') {
         this.finishCommand();

         return true;
      }

      if (p == 'i' || p == 'a') {
         boolean inner = p == 'i';
         Range range = this.textObject(key, inner);

         if (this.mode == Mode.VISUAL || this.mode == Mode.VISUAL_LINE) {
            char op = this.operator;
            this.finishCommand();

            if (range == null) {
               this.message("E348: No object under cursor");

               return true;
            }

            this.editor.cursor.set(range.line1, range.offset1);
            this.editor.selection.set(range.line2, Math.max(range.offset2 - 1, range.offset1));

            return op == 0;
         }

         char op = this.operator;
         this.finishCommand();

         if (range == null) {
            this.message("E348: No object under cursor");

            return true;
         }

         if (op == 0) {
            this.moveTo(new Cursor(range.line1, range.offset1));

            return true;
         }

         return this.operate(op, range);
      }

      this.finishCommand();

      return true;
   }

   /* ------------------------------------------------------------- modes */

   private void enterInsertMode(int offset) {
      this.beginChange();
      this.inserting = true;
      this.insertText = this.editor.getText();
      this.insertCursor = new Cursor(this.cursor().line, this.cursor().offset);
      this.insertCommand = this.sequence.length() > 0 ? this.sequence.toString() : this.lastSequence;
      this.cursor().offset = offset;
      this.editor.deselect();
      this.mode = Mode.INSERT;
   }

   public void leaveInsertMode() {
      if (this.mode != Mode.INSERT) {
         return;
      }

      String typed = this.insertText == null ? "" : this.diff(this.insertText, this.editor.getText());

      this.commitChange();
      this.inserting = false;

      if (!this.replaying) {
         this.lastSequence = this.insertCommand + typed + ESCAPE;
         this.sequence.setLength(0);
      }

      boolean moved = this.insertCursor != null && (this.cursor().line > this.insertCursor.line || this.cursor().offset > this.insertCursor.offset);
      int offset = this.cursor().offset;

      this.insertText = null;
      this.insertCursor = null;
      this.resetMode();

      if (moved && offset > 0) {
         this.cursor().offset = offset - 1;
      }

      this.clampCursor();
   }

   private void resetMode() {
      this.mode = Mode.NORMAL;
      this.count = -1;
      this.operator = 0;
      this.prefix = 0;
      this.named = null;
      this.editor.deselect();
   }

   private void openCommandLine(char prefix) {
      this.commandPrefix = prefix;
      this.commandLine.setLength(0);
      this.commandOffset = 0;
      this.mode = Mode.COMMAND;
      this.count = -1;
      this.operator = 0;
      this.prefix = 0;
   }

   private void toggleVisual(Mode mode) {
      if (this.mode == Mode.NORMAL) {
         this.editor.startSelecting();
         this.mode = mode;
      } else if (this.mode == mode) {
         this.resetMode();
      } else {
         this.mode = mode;
      }

      this.clampCursor();
   }

   /* ------------------------------------------------------------- motions */

   private Motion motion(char key, int count) {
      Cursor c = this.cursor();
      int lines = this.lines();

      switch (key) {
         case 'h': return new Motion(new Cursor(c.line, Math.max(0, c.offset - count)), false, false);
         case 'l': return new Motion(new Cursor(c.line, Math.min(this.maxOffset(c.line), c.offset + count)), false, false);
         case 'j': {
            int line = Math.min(lines - 1, c.line + count);
            return new Motion(new Cursor(line, Math.min(c.offset, this.maxOffset(line))), true, false);
         }
         case 'k': {
            int line = Math.max(0, c.line - count);
            return new Motion(new Cursor(line, Math.min(c.offset, this.maxOffset(line))), true, false);
         }
         case '0': return new Motion(new Cursor(c.line, 0), false, true);
         case '^': return new Motion(new Cursor(c.line, this.firstNonBlank(c.line)), false, true);
         case '$': return new Motion(new Cursor(c.line, this.maxOffset(c.line)), false, true);
         case 'w': return new Motion(this.wordForward(c, count, false), false, false);
         case 'W': return new Motion(this.wordForward(c, count, true), false, false);
         case 'b': return new Motion(this.wordBackward(c, count, false), false, false);
         case 'B': return new Motion(this.wordBackward(c, count, true), false, false);
         case 'e': return new Motion(this.wordEnd(c, count, false), false, true);
         case 'E': return new Motion(this.wordEnd(c, count, true), false, true);
         case 'G': {
            int line = count > 1 ? Math.min(count - 1, lines - 1) : lines - 1;
            return new Motion(new Cursor(line, Math.min(c.offset, this.maxOffset(line))), true, false);
         }
         case '{': return new Motion(new Cursor(this.blankBackward(c.line, count), 0), true, false);
         case '}': return new Motion(new Cursor(this.blankForward(c.line, count), 0), true, false);
         case '(': return new Motion(new Cursor(this.sentenceBackward(c.line, count), 0), true, false);
         case ')': return new Motion(new Cursor(this.sentenceForward(c.line, count), 0), true, false);
         case '%': return new Motion(this.matchBracket(c), false, false);
         case '|': return new Motion(new Cursor(c.line, Math.min(this.maxOffset(c.line), Math.max(0, count - 1))), false, false);
      }

      return null;
   }

   private boolean applyMotion(Motion motion) {
      if (this.mode == Mode.VISUAL_LINE) {
         int min = Math.min(this.editor.selection.line, motion.cursor.line);
         int max = Math.max(this.editor.selection.line, motion.cursor.line);

         for (int i = min; i <= max; i++) {
            this.moveTo(new Cursor(i, i == max ? this.maxOffset(i) : 0));
         }

         return true;
      }

      this.moveTo(motion.cursor);

      return true;
   }

   private boolean moveViewport(int x, int y) {
      Cursor c = this.cursor();

      if (x != 0) {
         this.moveTo(new Cursor(c.line, c.offset + x));
      } else {
         this.moveTo(new Cursor(c.line + y, c.offset));
      }

      return true;
   }

   private Cursor wordForward(Cursor from, int count, boolean big) {
      Cursor c = new Cursor(from.line, from.offset);

      for (int i = 0; i < count; i++) {
         String line = this.editor.getLineText(c.line);
         int o = Math.min(c.offset, line.length());

         if (o < line.length() && this.isWordChar(line.charAt(o), big)) {
            while (o < line.length() && this.isWordChar(line.charAt(o), big)) {
               o++;
            }
         }

         boolean found = false;

         while (o < line.length()) {
            if (this.isWordChar(line.charAt(o), big)) {
               found = true;
               break;
            }

            o++;
         }

         if (found) {
            c.offset = o;
            continue;
         }

         int l = c.line + 1;

         while (l < this.lines()) {
            String next = this.editor.getLineText(l);
            int p = 0;

            while (p < next.length() && !this.isWordChar(next.charAt(p), big)) {
               p++;
            }

            if (p < next.length()) {
               c.line = l;
               c.offset = p;
               found = true;
               break;
            }

            l++;
         }

         if (!found) {
            c.line = this.lines() - 1;
            c.offset = Math.max(0, this.length(c.line) - 1);
         }
      }

      c.offset = Math.min(c.offset, this.maxOffset(c.line));

      return c;
   }

   private Cursor wordBackward(Cursor from, int count, boolean big) {
      Cursor c = new Cursor(from.line, from.offset);

      for (int i = 0; i < count; i++) {
         String line = this.editor.getLineText(c.line);
         int o = Math.min(c.offset, line.length());

         if (o > 0 && this.isWordChar(line.charAt(o - 1), big)) {
            while (o > 0 && this.isWordChar(line.charAt(o - 1), big)) {
               o--;
            }
         }

         while (o > 0 && !this.isWordChar(line.charAt(o - 1), big)) {
            o--;
         }

         if (o > 0) {
            c.offset = o;
            continue;
         }

         int l = c.line - 1;
         boolean found = false;

         while (l >= 0) {
            String previous = this.editor.getLineText(l);
            int p = previous.length();

            while (p > 0 && !this.isWordChar(previous.charAt(p - 1), big)) {
               p--;
            }

            if (p > 0) {
               c.line = l;
               c.offset = p;
               found = true;
               break;
            }

            l--;
         }

         if (!found) {
            c.offset = 0;
         }
      }

      return c;
   }

   private Cursor wordEnd(Cursor from, int count, boolean big) {
      Cursor c = new Cursor(from.line, from.offset);

      for (int i = 0; i < count; i++) {
         String line = this.editor.getLineText(c.line);
         int o = Math.min(c.offset + 1, line.length());

         while (o < line.length() && !this.isWordChar(line.charAt(o), big)) {
            o++;
         }

         if (o >= line.length()) {
            int l = c.line + 1;
            boolean found = false;

            while (l < this.lines()) {
               String next = this.editor.getLineText(l);
               int p = 0;

               while (p < next.length() && !this.isWordChar(next.charAt(p), big)) {
                  p++;
               }

               if (p < next.length()) {
                  c.line = l;
                  c.offset = p;
                  found = true;
                  break;
               }

               l++;
            }

            if (!found) {
               c.line = this.lines() - 1;
               c.offset = Math.max(0, this.length(c.line) - 1);
            }

            continue;
         }

         while (o + 1 < line.length() && this.isWordChar(line.charAt(o + 1), big)) {
            o++;
         }

         c.offset = o;
      }

      c.offset = Math.min(c.offset, this.maxOffset(c.line));

      return c;
   }

   private Cursor matchBracket(Cursor from) {
      String line = this.editor.getLineText(from.line);

      if (line.isEmpty()) {
         return new Cursor(from.line, 0);
      }

      int start = Math.min(from.offset, line.length() - 1);
      int i = start;
      int direction = 0;

      for (; i >= 0 && i < line.length(); i--) {
         char c = line.charAt(i);

         if (OPEN.indexOf(c) >= 0) {
            direction = 1;
            break;
         }

         if (CLOSE.indexOf(c) >= 0) {
            direction = -1;
            break;
         }
      }

      if (direction == 0) {
         return new Cursor(from.line, from.offset);
      }

      char bracket = line.charAt(i);
      int index = OPEN.indexOf(bracket);

      if (index < 0) {
         index = CLOSE.indexOf(bracket);
      }

      char open = OPEN.charAt(index);
      char close = CLOSE.charAt(index);
      int depth = 0;

      for (int j = i; j >= 0 && j < line.length(); j += direction) {
         char c = line.charAt(j);

         if (c == open) {
            depth++;
         } else if (c == close) {
            depth--;

            if (depth == 0) {
               return new Cursor(from.line, j);
            }
         }
      }

      return new Cursor(from.line, from.offset);
   }

   /* ------------------------------------------------------------- operators */

   private boolean linewise(char op, int count) {
      int line = this.cursor().line;
      this.linewiseOperate(op, line, Math.min(this.lines() - 1, line + count - 1));

      return true;
   }

   private boolean operate(char op, Motion motion) {
      Cursor start = this.cursor();
      Cursor end = motion.cursor;

      if (motion.linewise || op == '>' || op == '<') {
         this.linewiseOperate(op, Math.min(start.line, end.line), Math.max(start.line, end.line));

         return true;
      }

      boolean forward = !end.isThisLessTo(start);
      int offset1 = forward ? start.offset : (motion.inclusive ? start.offset + 1 : start.offset);
      int offset2 = forward ? (motion.inclusive ? end.offset + 1 : end.offset) : end.offset;

      if (offset2 <= offset1) {
         if (forward) {
            offset2 = offset1 + 1;
         } else {
            offset1 = offset2 + 1;
         }
      }

      return this.operate(op, new Range(Math.min(start.line, end.line), offset1, Math.max(start.line, end.line), offset2));
   }

   private boolean operate(char op, Range range) {
      if (op == 'y') {
         this.setRegister(this.getRange(range), false);
         this.editor.cursor.set(range.line1, range.offset1);
         this.clampCursor();

         return true;
      }

      if (op == 'U' || op == 'u' || op == '~') {
         this.changeCase(op, range);

         return true;
      }

      if (op == '>') {
         this.indent(new Range(range.line1, 0, range.line2, 0), true);

         return true;
      }

      if (op == '<') {
         this.indent(new Range(range.line1, 0, range.line2, 0), false);

         return true;
      }

      if (op == '=') {
         return true;
      }

      this.beginChange();
      String text = this.getRange(range);
      this.deleteRange(range);
      this.setRegister(text, false);
      this.commitChange();
      this.editor.cursor.set(range.line1, range.offset1);
      this.clampCursor();

      if (op == 'c') {
         this.enterInsertMode(this.cursor().offset);
      }

      return true;
   }

   private void linewiseOperate(char op, int line1, int line2) {
      if (op == 'y') {
         this.setRegister(this.yankLines(line1, line2), true);
         this.editor.cursor.line = line1;
         this.editor.cursor.offset = this.firstNonBlank(line1);
         this.clampCursor();

         return;
      }

      if (op == '>' || op == '<') {
         this.indent(new Range(line1, 0, line2, 0), op == '>');

         return;
      }

      if (op == 'U' || op == 'u' || op == '~') {
         this.beginChange();

         for (int i = line1; i <= line2 && i < this.lines(); i++) {
            String line = this.editor.getLineText(i);
            this.editor.setLineText(i, op == 'U' ? line.toUpperCase() : op == 'u' ? line.toLowerCase() : this.swapCase(line));
         }

         this.commitChange();

         return;
      }

      if (op == '=') {
         return;
      }

      this.beginChange();
      String text = this.yankLines(line1, line2);
      this.deleteLines(line1, line2);
      this.setRegister(text, true);
      this.commitChange();

      if (op == 'c') {
         this.enterInsertMode(this.firstNonBlank(this.cursor().line));
      }
   }

   private void indent(Range range, boolean add) {
      this.beginChange();

      for (int i = range.line1; i <= range.line2 && i < this.lines(); i++) {
         String line = this.editor.getLineText(i);

         if (add) {
            this.editor.setLineText(i, this.spaces(INDENT) + line);
         } else {
            int n = 0;

            while (n < INDENT && n < line.length() && line.charAt(n) == ' ') {
               n++;
            }

            if (n == 0 && !line.isEmpty() && line.charAt(0) == '\t') {
               n = 1;
            }

            this.editor.setLineText(i, line.substring(n));
         }
      }

      this.commitChange();
   }

   private void changeCase(char kind, Range range) {
      this.beginChange();
      String text = this.getRange(range);
      String changed;

      if (kind == 'U') {
         changed = text.toUpperCase();
      } else if (kind == 'u') {
         changed = text.toLowerCase();
      } else {
         changed = this.swapCase(text);
      }

      this.replaceRange(range, changed);
      this.setRegister(text, false);
      this.editor.cursor.set(range.line1, range.offset1);
      this.commitChange();
      this.clampCursor();
   }

   private String swapCase(String text) {
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         builder.append(Character.isUpperCase(c) ? Character.toLowerCase(c) : Character.isLowerCase(c) ? Character.toUpperCase(c) : c);
      }

      return builder.toString();
   }

   /* ------------------------------------------------------------- text objects */

   private Range textObject(char key, boolean inner) {
      Cursor c = this.cursor();
      String line = this.editor.getLineText(c.line);
      int length = line.length();

      if (key == 'w' || key == 'W') {
         boolean big = key == 'W';
         int o = Math.min(c.offset, Math.max(0, length - 1));
         int start = -1;
         int end = -1;

         if (length > 0 && this.isWordChar(line.charAt(o), big)) {
            start = o;
            end = o + 1;

            while (start > 0 && this.isWordChar(line.charAt(start - 1), big)) {
               start--;
            }

            while (end < length && this.isWordChar(line.charAt(end), big)) {
               end++;
            }
         } else if (o > 0 && this.isWordChar(line.charAt(o - 1), big)) {
            start = o - 1;
            end = o;

            while (start > 0 && this.isWordChar(line.charAt(start - 1), big)) {
               start--;
            }
         }

         if (start < 0) {
            return null;
         }

         if (inner) {
            return new Range(c.line, start, c.line, end);
         }

         int trailing = end;

         while (trailing < length && (line.charAt(trailing) == ' ' || line.charAt(trailing) == '\t')) {
            trailing++;
         }

         return new Range(c.line, start, c.line, trailing > end ? trailing : end);
      }

      if (key == '"' || key == '\'' || key == '`') {
         int quote = -1;

         for (int i = Math.min(c.offset, length - 1); i >= 0; i--) {
            if (line.charAt(i) == key) {
               quote = i;
               break;
            }
         }

         if (quote < 0) {
            return null;
         }

         int close = line.indexOf(key, quote + 1);

         if (close < 0) {
            return null;
         }

         return inner ? new Range(c.line, quote + 1, c.line, close) : new Range(c.line, quote, c.line, close + 1);
      }

      if (key == '(' || key == 'b') {
         return this.bracketObject(c.line, line, c.offset, '(');
      }

      if (key == ')') {
         return this.bracketObject(c.line, line, c.offset, '(');
      }

      if (key == '[' || key == ']') {
         return this.bracketObject(c.line, line, c.offset, '[');
      }

      if (key == '{' || key == '}' || key == 'B') {
         return this.bracketObject(c.line, line, c.offset, '{');
      }

      if (key == '<') {
         return this.bracketObject(c.line, line, c.offset, '<');
      }

      if (key == '>') {
         return this.bracketObject(c.line, line, c.offset, '>');
      }

      return null;
   }

   private Range bracketObject(int index, String line, int offset, char bracket) {
      char open = bracket;
      char close = bracket == '(' ? ')' : bracket == '[' ? ']' : bracket == '{' ? '}' : bracket == '<' ? '>' : open;
      int depth = 0;
      int start = -1;

      for (int i = Math.min(offset, line.length() - 1); i >= 0; i--) {
         char c = line.charAt(i);

         if (c == close) {
            depth++;
         } else if (c == open) {
            if (depth == 0) {
               start = i;
               break;
            }

            depth--;
         }
      }

      if (start < 0) {
         return null;
      }

      int end = -1;
      depth = 0;

      for (int i = start; i < line.length(); i++) {
         char c = line.charAt(i);

         if (c == open) {
            depth++;
         } else if (c == close) {
            depth--;

            if (depth == 0) {
               end = i;
               break;
            }
         }
      }

      if (end < 0) {
         return null;
      }

      return new Range(index, start + 1, index, end);
   }

   /* ------------------------------------------------------------- editing */

   private void deleteChars(int count) {
      int index = this.cursor().line;
      String line = this.editor.getLineText(index);
      int start = this.cursor().offset;
      int end = Math.min(line.length(), start + count);

      if (end <= start) {
         return;
      }

      this.beginChange();
      String text = line.substring(start, end);
      this.editor.setLineText(index, line.substring(0, start) + line.substring(end));
      this.setRegister(text, false);
      this.commitChange();
      this.clampCursor();
   }

   private void deleteCharsBackwards(int count) {
      int index = this.cursor().line;
      String line = this.editor.getLineText(index);
      int start = Math.max(0, this.cursor().offset - count);
      int end = this.cursor().offset;

      if (end <= start) {
         return;
      }

      this.beginChange();
      String text = line.substring(start, end);
      this.editor.setLineText(index, line.substring(0, start) + line.substring(end));
      this.setRegister(text, false);
      this.editor.cursor.offset = start;
      this.commitChange();
      this.clampCursor();
   }

   private boolean deleteToLineEnd() {
      int index = this.cursor().line;
      String line = this.editor.getLineText(index);
      int start = this.cursor().offset;

      if (start >= line.length()) {
         return false;
      }

      this.beginChange();
      String text = line.substring(start);
      this.editor.setLineText(index, line.substring(0, start));
      this.setRegister(text, false);
      this.commitChange();
      this.clampCursor();

      return true;
   }

   private void changeLine() {
      int index = this.cursor().line;
      String line = this.editor.getLineText(index);
      this.beginChange();
      this.setRegister(line + "\n", true);
      int indent = this.firstNonBlank(index);
      this.editor.setLineText(index, line.substring(0, indent));
      this.editor.cursor.line = index;
      this.editor.cursor.offset = indent;
      this.enterInsertMode(indent);
   }

   private void openLine(boolean below) {
      this.beginChange();
      int index = Math.min(this.lines(), this.cursor().line + (below ? 1 : 0));
      this.editor.insertLine(index, "");
      this.editor.cursor.line = index;
      this.editor.cursor.offset = 0;
   }

   private void joinLines(int count) {
      if (this.lines() < 2) {
         return;
      }

      this.beginChange();
      int joined = 0;

      for (int i = 0; i < count; i++) {
         int index = this.cursor().line;

         if (index + 1 >= this.lines()) {
            break;
         }

         String a = this.editor.getLineText(index);
         String b = this.editor.getLineText(index + 1);
         int start = 0;

         while (start < b.length() && (b.charAt(start) == ' ' || b.charAt(start) == '\t')) {
            start++;
         }

         b = b.substring(start);

         if (a.isEmpty()) {
            if (b.isEmpty()) {
               continue;
            }

            this.editor.setLineText(index, b);
            this.editor.removeLine(index + 1);
            this.editor.cursor.offset = 0;
         } else {
            String separator = (a.endsWith(" ") || a.endsWith("\t")) ? "" : " ";

            if (a.endsWith("{") || a.endsWith("(") || a.endsWith("[")) {
               separator = "";
            }

            this.editor.setLineText(index, a + separator + b);
            this.editor.removeLine(index + 1);
            this.editor.cursor.offset = a.length() + separator.length();
         }

         joined++;
      }

      if (joined == 0) {
         this.pending = null;
      } else {
         this.commitChange();
      }

      this.clampCursor();
   }

   private void replaceChar(char character, int count) {
      int index = this.cursor().line;
      String line = this.editor.getLineText(index);
      int start = this.cursor().offset;
      int end = Math.min(line.length(), start + count);

      if (end <= start) {
         return;
      }

      this.beginChange();
      StringBuilder builder = new StringBuilder(line);

      for (int i = start; i < end; i++) {
         builder.setCharAt(i, character);
      }

      this.editor.setLineText(index, builder.toString());
      this.commitChange();
      this.clampCursor();
   }

   private void toggleCase(int count) {
      int index = this.cursor().line;
      String line = this.editor.getLineText(index);
      int start = this.cursor().offset;
      int end = Math.min(line.length(), start + count);

      if (end <= start) {
         return;
      }

      this.beginChange();
      StringBuilder builder = new StringBuilder(line);

      for (int i = start; i < end; i++) {
         char c = builder.charAt(i);
         builder.setCharAt(i, Character.isUpperCase(c) ? Character.toLowerCase(c) : Character.isLowerCase(c) ? Character.toUpperCase(c) : c);
      }

      this.editor.setLineText(index, builder.toString());
      this.commitChange();
      this.clampCursor();
   }

   private void put(boolean after) {
      String register = this.getRegister();

      if (register == null || register.isEmpty()) {
         return;
      }

      boolean linewise = this.isRegisterLinewise();
      String text = linewise && register.endsWith("\n") ? register.substring(0, register.length() - 1) : register;
      String[] parts = text.split("\n", -1);

      this.beginChange();

      if (linewise) {
         int index = Math.min(this.lines(), this.cursor().line + (after ? 1 : 0));

         for (int i = 0; i < parts.length; i++) {
            this.editor.insertLine(index + i, parts[i]);
         }

         this.editor.cursor.line = index;
         this.editor.cursor.offset = 0;
      } else {
         int index = this.cursor().line;
         String line = this.editor.getLineText(index);
         int offset = Math.min(line.length(), Math.max(0, this.cursor().offset + (after ? 1 : 0)));
         String head = line.substring(0, offset);
         String tail = line.substring(offset);

         this.editor.setLineText(index, head + parts[0]);

         for (int i = 1; i < parts.length; i++) {
            this.editor.insertLine(index + i, parts[i]);
         }

         if (parts.length > 1) {
            this.editor.setLineText(index + parts.length - 1, parts[parts.length - 1] + tail);
         }

         this.editor.cursor.line = index + parts.length - 1;
         this.editor.cursor.offset = parts[parts.length - 1].length();
      }

      this.commitChange();
      this.clampCursor();
   }

   /* ------------------------------------------------------------- ranges */

   private String yankLines(int line1, int line2) {
      StringBuilder builder = new StringBuilder();

      for (int i = line1; i <= line2 && i < this.lines(); i++) {
         if (i > line1) {
            builder.append("\n");
         }

         builder.append(this.editor.getLineText(i));
      }

      return builder.toString() + "\n";
   }

   private void deleteLines(int line1, int line2) {
      for (int i = Math.min(line2, this.lines() - 1); i >= line1; i--) {
         this.editor.removeLine(i);
      }

      if (this.lines() == 0) {
         this.editor.insertLine(0, "");
      }

      this.editor.cursor.line = Math.max(0, Math.min(line1, this.lines() - 1));
      this.editor.cursor.offset = 0;
   }

   private String getRange(Range range) {
      String first = this.editor.getLineText(range.line1);
      String last = this.editor.getLineText(range.line2);

      if (range.line1 == range.line2) {
         int start = Math.max(0, Math.min(range.offset1, first.length()));
         int end = Math.max(start, Math.min(range.offset2, first.length()));

         return first.substring(start, end);
      }

      StringBuilder builder = new StringBuilder(first.substring(Math.max(0, Math.min(range.offset1, first.length()))));

      for (int i = range.line1 + 1; i < range.line2; i++) {
         builder.append("\n").append(this.editor.getLineText(i));
      }

      builder.append("\n").append(last, 0, Math.max(0, Math.min(range.offset2, last.length())));

      return builder.toString();
   }

   private void deleteRange(Range range) {
      String first = this.editor.getLineText(range.line1);
      String last = this.editor.getLineText(range.line2);

      if (range.line1 == range.line2) {
         int start = Math.max(0, Math.min(range.offset1, first.length()));
         int end = Math.max(start, Math.min(range.offset2, first.length()));
         this.editor.setLineText(range.line1, first.substring(0, start) + first.substring(end));

         return;
      }

      int start = Math.max(0, Math.min(range.offset1, first.length()));
      int end = Math.max(0, Math.min(range.offset2, last.length()));
      this.editor.setLineText(range.line1, first.substring(0, start) + last.substring(end));

      for (int i = range.line2; i > range.line1; i--) {
         this.editor.removeLine(i);
      }
   }

   private void replaceRange(Range range, String text) {
      this.deleteRange(range);

      String[] parts = text.split("\n", -1);

      if (parts.length == 1) {
         String line = this.editor.getLineText(range.line1);
         int offset = Math.min(line.length(), Math.max(0, range.offset1));
         this.editor.setLineText(range.line1, line.substring(0, offset) + parts[0] + line.substring(offset));

         return;
      }

      String line = this.editor.getLineText(range.line1);
      int offset = Math.min(line.length(), Math.max(0, range.offset1));
      String head = line.substring(0, offset);
      String tail = line.substring(offset);

      this.editor.setLineText(range.line1, head + parts[0]);

      for (int i = 1; i < parts.length; i++) {
         this.editor.insertLine(range.line1 + i, parts[i]);
      }

      this.editor.setLineText(range.line1 + parts.length - 1, parts[parts.length - 1] + tail);
   }

   /* ------------------------------------------------------------- visual */

   private void visualOperate(char op) {
      Cursor min = this.editor.getMin();
      Cursor max = this.editor.getMax();
      int line1 = min.line;
      int offset1 = min.offset;
      int line2 = max.line;
      int offset2 = max.offset + 1;
      boolean linewise = this.mode == Mode.VISUAL_LINE || op == '>' || op == '<';
      String register = this.getRegister();

      this.finishCommand();
      this.resetMode();

      if (op == 'y') {
         this.setRegister(linewise ? this.yankLines(line1, line2) : this.getRange(new Range(line1, offset1, line2, offset2)), linewise);
         this.editor.cursor.set(line1, offset1);
         this.clampCursor();

         return;
      }

      if (op == 'p') {
         if (register == null || register.isEmpty()) {
            return;
         }

         Range range = new Range(line1, offset1, line2, offset2);
         this.beginChange();
         this.deleteRange(range);
         this.commitChange();
         this.editor.cursor.set(range.line1, range.offset1);
         this.clampCursor();
         this.put(false);

         return;
      }

      if (op == '>' || op == '<') {
         this.indent(new Range(line1, 0, line2, 0), op == '>');

         return;
      }

      if (op == 'U' || op == 'u' || op == '~') {
         this.changeCase(op, new Range(line1, offset1, line2, offset2));

         return;
      }

      Range range = linewise ? new Range(line1, 0, line2, 0) : new Range(line1, offset1, line2, offset2);
      this.beginChange();
      String text = this.getRange(range);
      this.deleteRange(range);
      this.setRegister(text, linewise);
      this.commitChange();
      this.editor.cursor.set(range.line1, range.offset1);
      this.clampCursor();

      if (op == 'c') {
         this.enterInsertMode(this.cursor().offset);
      }
   }

   /* ------------------------------------------------------------- search */

   private Cursor searchCursor(boolean backward, int count) {
      if (this.search.isEmpty()) {
         return this.cursor();
      }

      Cursor c = new Cursor(this.cursor().line, this.cursor().offset);

      for (int i = 0; i < count; i++) {
         Cursor found = this.findSearch(backward);

         if (found == null) {
            break;
         }

         c = found;
         this.editor.cursor.copy(c);
      }

      return c;
   }

   private Cursor findSearch(boolean backward) {
      if (this.search.isEmpty()) {
         return null;
      }

      int lines = this.lines();
      int fromLine = this.cursor().line;
      int fromOffset = this.cursor().offset;
      int start = backward ? fromOffset - this.search.length() : fromOffset + 1;

      for (int i = 0; i < lines; i++) {
         int l = backward ? fromLine - i : fromLine + i;

         if (l < 0) {
            l += lines;
         }

         if (l >= lines) {
            l -= lines;
         }

         String line = this.editor.getLineText(l);
         int p;

         if (i == 0) {
            p = backward ? line.lastIndexOf(this.search, start) : line.indexOf(this.search, Math.max(0, start));
         } else {
            p = backward ? line.lastIndexOf(this.search) : line.indexOf(this.search);
         }

         if (p >= 0) {
            return new Cursor(l, p);
         }
      }

      return null;
   }

   /* ------------------------------------------------------------- command line */

   private void execute(String input) {
      char prefix = input.isEmpty() ? ':' : input.charAt(0);
      String text = input.length() > 0 ? input.substring(1) : "";

      if (prefix == '/') {
         if (text.isEmpty()) {
            return;
         }

         this.search = text;
         Cursor found = this.findSearch(false);

         if (found == null) {
            this.message("E486: Pattern not found: " + text);

            return;
         }

         this.moveTo(found);

         return;
      }

      String[] parts = text.trim().split("\\s+");
      String name = parts.length == 0 ? "" : parts[0];

      if (name.equals("s") || name.equals("substitute")) {
         this.substitute(text);

         return;
      }

      switch (name) {
         case "": case "noh": case "nohlsearch": case "set": break;
         case "w": case "wq": case "x": this.save(); break;
         case "q": case "q!": case "quit": break;
         default: this.message("E492: Not an editor command: " + name);
      }
   }

   private void substitute(String text) {
      String[] parts = text.split("/", -1);

      if (parts.length < 3 || parts[1].isEmpty()) {
         this.message("E486: Invalid pattern");

         return;
      }

      String pattern = parts[1];
      String replacement = parts[2];
      boolean global = parts.length > 3 && parts[3].contains("g");

      try {
         int index = this.cursor().line;
         Matcher matcher = Pattern.compile(pattern).matcher(this.editor.getLineText(index));

         if (!matcher.find()) {
            this.message("E486: Pattern not found: " + pattern);

            return;
         }

         String quoted = Matcher.quoteReplacement(replacement);
         String result = global ? matcher.replaceAll(quoted) : matcher.replaceFirst(quoted);

         this.beginChange();
         this.setRegister(this.editor.getLineText(index), false);
         this.editor.setLineText(index, result);
         this.commitChange();
         this.clampCursor();
         this.message("");
      } catch (Exception e) {
         this.message("E486: Invalid pattern: " + pattern);
      }
   }

   private void save() {
      if (this.saveAction == null) {
         this.message("E32: No file name");

         return;
      }

      this.saveAction.run();
      this.message("");
   }

   private void message(String message) {
      this.message = message == null ? "" : message;
      this.messageTicks = MESSAGE_TICKS;
   }

   /* ------------------------------------------------------------- history */

   private void beginChange() {
      if (this.restoring || this.inserting || this.pending != null) {
         return;
      }

      this.pending = this.editor.getText();
   }

   private void commitChange() {
      if (this.pending == null) {
         return;
      }

      String before = this.pending;
      String after = this.editor.getText();
      this.pending = null;

      if (this.restoring || before.equals(after)) {
         return;
      }

      while (this.history.size() > this.historyIndex + 1) {
         this.history.remove(this.history.size() - 1);
      }

      this.history.add(new Change(before, after, new Cursor(this.cursor().line, this.cursor().offset)));

      while (this.history.size() > MAX_HISTORY) {
         this.history.remove(0);
      }

      this.historyIndex = this.history.size() - 1;
      this.editor.afterExternalEdit();
   }

   private void undo() {
      if (this.historyIndex < 0) {
         this.message("Already at oldest change");

         return;
      }

      Change change = this.history.get(this.historyIndex);
      this.historyIndex -= 1;
      this.restore(change.before, change.cursor);
   }

   private void redo() {
      if (this.historyIndex >= this.history.size() - 1) {
         this.message("Already at newest change");

         return;
      }

      this.historyIndex += 1;
      Change change = this.history.get(this.historyIndex);
      this.restore(change.after, change.cursor);
   }

   private void restore(String text, Cursor cursor) {
      this.restoring = true;

      try {
         this.editor.replaceAllText(text);
         this.editor.afterExternalEdit();
      } finally {
         this.restoring = false;
      }

      this.editor.cursor.set(Math.max(0, Math.min(cursor.line, Math.max(0, this.lines() - 1))), cursor.offset);
      this.clampCursor();
      this.editor.deselect();
   }

   private void replay() {
      if (this.lastSequence.isEmpty() || this.replaying) {
         return;
      }

      String sequence = this.lastSequence;
      this.replaying = true;
      this.sequence.setLength(0);

      try {
         for (int i = 0; i < sequence.length(); i++) {
            char key = sequence.charAt(i);

            if (key == ESCAPE) {
               this.leaveInsertMode();

               continue;
            }

            if (this.mode == Mode.INSERT) {
               this.insertChar(key);

               continue;
            }

            this.handleNormalChar(key);
         }
      } finally {
         this.replaying = false;
         this.sequence.setLength(0);
         this.lastSequence = sequence;
      }
   }

   private void insertChar(char key) {
      if (!this.inserting) {
         this.beginChange();
         this.inserting = true;
         this.insertText = this.editor.getText();
         this.insertCursor = new Cursor(this.cursor().line, this.cursor().offset);
      }

      this.editor.writeString(String.valueOf(key));
   }

   /* ------------------------------------------------------------- helpers */

   private Cursor cursor() {
      return this.editor.cursor;
   }

   private int lines() {
      return this.editor.getLineCount();
   }

   private int length(int line) {
      return this.editor.getLineText(line).length();
   }

   private int maxOffset(int line) {
      if (this.mode == Mode.INSERT || this.mode == Mode.COMMAND) {
         return this.length(line);
      }

      return Math.max(0, this.length(line) - 1);
   }

   private int firstNonBlank(int line) {
      String text = this.editor.getLineText(line);
      int i = 0;

      while (i < text.length() && (text.charAt(i) == ' ' || text.charAt(i) == '\t')) {
         i++;
      }

      return Math.min(i, this.maxOffset(line));
   }

   private String spaces(int count) {
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < count; i++) {
         builder.append(" ");
      }

      return builder.toString();
   }

   private boolean isWordChar(char c, boolean big) {
      if (big) {
         return !Character.isWhitespace(c);
      }

      return Character.isLetterOrDigit(c) || c == '_' || c == '$';
   }

   private boolean isBlankLine(int line) {
      return this.editor.getLineText(line).trim().isEmpty();
   }

   private int blankBackward(int line, int count) {
      for (int i = 0; i < count; i++) {
         int l = line - 1;

         while (l >= 0 && !this.isBlankLine(l)) {
            l--;
         }

         if (l < 0) {
            return 0;
         }

         line = l;
      }

      return line;
   }

   private int blankForward(int line, int count) {
      for (int i = 0; i < count; i++) {
         int l = line + 1;

         while (l < this.lines() && !this.isBlankLine(l)) {
            l++;
         }

         if (l >= this.lines()) {
            return this.lines() - 1;
         }

         line = l;
      }

      return line;
   }

   private boolean isSentenceEnd(int line) {
      String text = this.editor.getLineText(line).trim();

      if (text.isEmpty()) {
         return true;
      }

      char c = text.charAt(text.length() - 1);

      return c == '.' || c == '!' || c == '?';
   }

   private int sentenceBackward(int line, int count) {
      for (int i = 0; i < count; i++) {
         int l = line - 1;

         while (l >= 0 && !this.isSentenceEnd(l)) {
            l--;
         }

         if (l < 0) {
            return 0;
         }

         line = l;
      }

      return line;
   }

   private int sentenceForward(int line, int count) {
      for (int i = 0; i < count; i++) {
         int l = line + 1;

         while (l < this.lines() && !this.isSentenceEnd(l)) {
            l++;
         }

         if (l >= this.lines()) {
            return this.lines() - 1;
         }

         line = l;
      }

      return line;
   }

   private void moveTo(Cursor target) {
      Cursor c = this.cursor();
      c.line = Math.max(0, Math.min(target.line, Math.max(0, this.lines() - 1)));
      c.offset = Math.max(0, Math.min(target.offset, this.maxOffset(c.line)));
   }

   private void clampCursor() {
      Cursor c = this.cursor();
      c.line = Math.max(0, Math.min(c.line, Math.max(0, this.lines() - 1)));
      c.offset = Math.max(0, Math.min(c.offset, this.maxOffset(c.line)));
   }

   private void finishCommand() {
      this.count = -1;
      this.operator = 0;
      this.prefix = 0;
      this.named = null;

      if (this.mode != Mode.COMMAND && !this.replaying && this.sequence.length() > 0) {
         this.lastSequence = this.sequence.toString();
         this.sequence.setLength(0);
      }
   }

   private String diff(String before, String after) {
      int max = Math.min(before.length(), after.length());
      int p = 0;

      while (p < max && before.charAt(p) == after.charAt(p)) {
         p++;
      }

      int s = 0;

      while (s < max - p && before.charAt(before.length() - 1 - s) == after.charAt(after.length() - 1 - s)) {
         s++;
      }

      return after.substring(p, after.length() - s);
   }

   private boolean isRegisterLinewise() {
      return this.named == null ? this.unnamedLinewise : this.namedLinewise;
   }

   private String getRegister() {
      return this.named == null ? this.unnamed : this.namedText;
   }

   private void setRegister(String text, boolean linewise) {
      if (this.named == null) {
         this.unnamed = text;
         this.unnamedLinewise = linewise;
      } else {
         this.namedText = text;
         this.namedLinewise = linewise;
      }
   }

   /* ------------------------------------------------------------- draw */

   public void draw(class_327 font, Area area, int lineHeight) {
      if (!this.enabled) {
         return;
      }

      if (this.messageTicks > 0) {
         this.messageTicks -= 1;
      }

      int y = area.ey() - lineHeight - 1;
      String label = "-- " + this.mode.label + " --";
      int x = area.ex() - font.method_1727(label) - 6;

      if (this.mode == Mode.COMMAND) {
         String line = this.commandPrefix + this.commandLine.toString();
         int width = font.method_1727(line);

         GuiDraw.drawRect(area.x + 3, y - 2, area.x + 9 + width, y + lineHeight, -16777216 + 0x1a1a1a);
         GuiDraw.drawString(font, line, area.x + 6, y, -16777216 + 0xdddddd);
      } else if (this.messageTicks > 0 && !this.message.isEmpty()) {
         int width = font.method_1727(this.message);

         GuiDraw.drawRect(area.x + 3, y - 2, area.x + 9 + width, y + lineHeight, -16777216 + 0x1a1a1a);
         GuiDraw.drawString(font, this.message, area.x + 6, y, -16777216 + 0xff6060);
      }

      GuiDraw.drawRect(x - 3, y - 2, area.ex() - 2, y + lineHeight, -16777216 + 0x1a1a1a);
      GuiDraw.drawString(font, label, x, y, -16777216 + this.mode.color);
   }
}
