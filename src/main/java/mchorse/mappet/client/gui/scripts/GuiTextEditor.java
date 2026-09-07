package mchorse.mappet.client.gui.scripts;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.vecmath.Vector2d;
import mchorse.mappet.ClientProxy;
import mchorse.mappet.api.utils.ContentType;
import mchorse.mappet.client.gui.scripts.highlights.Highlighters;
import mchorse.mappet.client.gui.scripts.utils.JavaScriptDiagnostics;
import mchorse.mappet.client.gui.scripts.utils.JavaScriptDiagnostics.DiagnosticSnapshot;
import mchorse.mappet.client.gui.scripts.utils.JavaScriptNavigation;
import mchorse.mappet.client.gui.scripts.utils.HighlightedTextLine;
import mchorse.mappet.client.gui.scripts.utils.SyntaxHighlighter;
import mchorse.mappet.client.gui.scripts.utils.TextLineNumber;
import mchorse.mappet.client.gui.scripts.utils.TextSegment;
import mchorse.mappet.client.gui.utils.text.GuiMultiTextElement;
import mchorse.mappet.client.gui.utils.text.undo.TextEditUndo;
import mchorse.mappet.client.gui.utils.text.utils.Cursor;
import mchorse.mappet.utils.autocomplete.AutoCompleteConfig;
import mchorse.mappet.utils.autocomplete.AutoCompleteEngine;
import mchorse.mappet.utils.autocomplete.AutoCompleteMenu;
import mchorse.mappet.utils.autocomplete.Config;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.class_310;
import net.minecraft.class_3417;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.input.Keyboard;

public class GuiTextEditor extends GuiMultiTextElement<HighlightedTextLine> {
   private static final Pattern HEX_COLOR_LITERAL = Pattern.compile("(?i)(?<![a-z0-9_])0x(?:[0-9a-f]{8}|[0-9a-f]{6})(?![a-z0-9_])");
   private static long lastShaderNamesRequest;
   private static final long SHADER_NAMES_REQUEST_DELAY_MS = 500L;
   private SyntaxHighlighter highlighter = Highlighters.readHighlighter(Highlighters.highlighterFile("js.json"));
   private int placements;
   private boolean lines = true;
   private List<TextLineNumber> numbers = new ArrayList(40);
   private int lineNumber = 0;
   private AutoCompleteMenu autoCompleteMenu;
   private List<SourceDiagnostic> syntaxDiagnostics = new ArrayList();
   private volatile boolean syntaxDirty = true;
   private boolean javaScriptDiagnostics;
   private boolean clientScriptMode;
   private volatile int diagnosticRevision;
   private volatile int requestedJavaScriptRevision = -1;
   private volatile DiagnosticSnapshot javaScriptSnapshot = new DiagnosticSnapshot(-1, new ArrayList());
   

   private volatile Set<String> javaScriptLibraryFunctions = Collections.emptySet();
   private static final int WARNING_COLOR = -16128;
   private static final long JAVASCRIPT_DIAGNOSTIC_DELAY_MS = 0L;
   private Runnable findReplaceHandler;
   private Consumer<AutoCompleteConfig.Suggestion> apiDocumentationHandler;
   
   private Map<String, String> localAutoCompleteTypes = Collections.emptyMap();
   

   private int hyperlinkLine = -1;
   private int hyperlinkStart = -1;
   private int hyperlinkEnd = -1;
   private int hyperlinkCursorLine = -1;
   private int hyperlinkCursorOffset = -1;
   private int hyperlinkRevision = -1;
   private boolean hyperlinkCtrl;
   private boolean hyperlinkPointer;
   private long handCursor;
   

   private Map<Integer, Integer> foldRanges;
   private Set<Integer> foldedBlocks;
   

   private Set<Integer> foldSpacerLines = Collections.emptySet();
   private boolean foldRangesDirty = true;

   public GuiTextEditor(class_310 mc, Consumer<String> callback) {
      super(mc, callback);
      this.ensureFoldState();
      this.foldRangesDirty = true;
      requestShaderNames();
   }

   private static void requestShaderNames() {
      long now = System.currentTimeMillis();
      if (now - lastShaderNamesRequest < SHADER_NAMES_REQUEST_DELAY_MS) {
         return;
      }

      


      lastShaderNamesRequest = now;
      ClientProxy.requestNames(ContentType.SHADERS, AutoCompleteEngine::setShaderIds);
   }

   
   public void setFindReplaceHandler(Runnable handler) {
      this.findReplaceHandler = handler;
   }

   
   public void setApiDocumentationHandler(Consumer<AutoCompleteConfig.Suggestion> handler) {
      this.apiDocumentationHandler = handler;
   }

   
   public void setLocalAutoCompleteTypes(Map<String, String> types) {
      this.localAutoCompleteTypes = types == null || types.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(new HashMap(types));
   }

   
   public void setClientScriptMode(boolean clientScriptMode) {
      this.clientScriptMode = clientScriptMode;
   }

   protected HighlightedTextLine createTextLine(String line) {
      return new HighlightedTextLine(line);
   }

   public GuiTextEditor disableLines() {
      this.lines = false;
      return this;
   }

   public SyntaxHighlighter getHighlighter() {
      return this.highlighter;
   }

   public void setHighlighter(SyntaxHighlighter highlighter) {
      this.highlighter = highlighter;
   }

   
   public void setJavaScriptDiagnostics(boolean enabled) {
      if (this.javaScriptDiagnostics != enabled) {
         this.javaScriptDiagnostics = enabled;
         this.markSyntaxDirty();
      }
   }

   
   public void setJavaScriptLibraryFunctions(Set<String> libraryFunctions) {
      Set<String> functions = libraryFunctions == null || libraryFunctions.isEmpty()
         ? Collections.emptySet()
         : Collections.unmodifiableSet(new HashSet(libraryFunctions));

      if (!functions.equals(this.javaScriptLibraryFunctions)) {
         this.javaScriptLibraryFunctions = functions;
         this.markSyntaxDirty();
      }
   }

   
   public int getJavaScriptDiagnosticStatus() {
      if (!this.javaScriptDiagnostics) {
         return 0;
      }

      boolean warning = false;

      for(SourceDiagnostic diagnostic : this.getSyntaxDiagnostics()) {
         if (!diagnostic.warning) {
            return 2;
         }

         warning = true;
      }

      return warning ? 1 : 0;
   }

   
   public boolean isJavaScriptDiagnosticReady() {
      return !this.javaScriptDiagnostics || this.javaScriptSnapshot.getRevision() == this.diagnosticRevision;
   }

   public void resetHighlight() {
      for(HighlightedTextLine textLine : this.text) {
         textLine.resetSegments();
      }

   }

   public void setText(String text) {
      super.setText(text);
      this.ensureFoldState();
      this.foldedBlocks.clear();
      this.foldRangesDirty = true;
      this.clearHyperlinkHover();
      this.clearJavaScriptDiagnostics();
      this.markSyntaxDirty();
      this.resetHighlight();
   }

   

   private void clearJavaScriptDiagnostics() {
      this.syntaxDiagnostics = new ArrayList();
      this.javaScriptSnapshot = new DiagnosticSnapshot(-1, new ArrayList());
      this.requestedJavaScriptRevision = -1;
   }

   public void selectRange(int start, int end) {
      String source = this.getText();
      Cursor from = this.offsetToCursor(source, Math.max(0, Math.min(start, source.length())));
      Cursor to = this.offsetToCursor(source, Math.max(0, Math.min(end, source.length())));
      this.cursor.copy(from);
      this.selection.copy(to);
      this.moveViewportToCursor();
   }

   private Cursor offsetToCursor(String source, int offset) {
      int line = 0;
      int lineStart = 0;
      while (lineStart < source.length() && line < this.text.size()) {
         int next = source.indexOf('\n', lineStart);
         if (next < 0 || offset <= next) {
            return new Cursor(line, Math.max(0, offset - lineStart));
         }
         lineStart = next + 1;
         line++;
      }
      return new Cursor(Math.max(0, this.text.size() - 1), this.text.isEmpty() ? 0 : ((HighlightedTextLine)this.text.get(this.text.size() - 1)).text.length());
   }

   protected void recalculateSizes() {
      double power = Math.ceil(Math.log10((double)(this.text.size() + 1)));
      this.placements = (int)power * 6;
      super.recalculateSizes();
   }

   protected void changedLine(int i) {
      this.resetFoldsForTextChange();
      this.markSyntaxDirty();
      String line = ((HighlightedTextLine)this.text.get(i)).text;
      if (!line.contains("/*") && !line.contains("*/")) {
         super.changedLine(i);
         ((HighlightedTextLine)this.text.get(i)).resetSegments();
      } else {
         this.changedLineAfter(i);
      }

   }

   protected void changedLineAfter(int i) {
      this.resetFoldsForTextChange();
      this.markSyntaxDirty();
      super.changedLineAfter(i);

      while(i < this.text.size()) {
         ((HighlightedTextLine)this.text.get(i)).resetSegments();
         ++i;
      }

   }

   protected String getFromChar(char typedChar) {
      if (!this.wasDoubleInsert(typedChar, ')', '(') && !this.wasDoubleInsert(typedChar, ']', '[') && !this.wasDoubleInsert(typedChar, '}', '{') && !this.wasDoubleInsert(typedChar, '"', '"') && !this.wasDoubleInsert(typedChar, '\'', '\'')) {
         if (typedChar == '(') {
            return "()";
         } else if (typedChar == '[') {
            return "[]";
         } else if (typedChar == '{') {
            return "{}";
         } else if (typedChar == '"') {
            return "\"\"";
         } else {
            return typedChar == '\'' ? "''" : super.getFromChar(typedChar);
         }
      } else {
         this.moveCursor(1, 0);
         this.playSound(class_3417.field_14574);
         return "";
      }
   }

   private boolean wasDoubleInsert(char input, char target, char supplementary) {
      if (input != target) {
         return false;
      } else {
         String line = ((HighlightedTextLine)this.text.get(this.cursor.line)).text;
         return line.length() >= 2 && this.cursor.offset > 0 && this.cursor.offset < line.length() && line.charAt(this.cursor.offset) == target && line.charAt(this.cursor.offset - 1) == supplementary;
      }
   }

   protected void keyNewLine(TextEditUndo undo) {
      String line = ((HighlightedTextLine)this.text.get(this.cursor.line)).text;
      boolean unwrap = line.length() >= 2 && this.cursor.offset > 0 && this.cursor.offset < line.length() && line.charAt(this.cursor.offset) == '}' && line.charAt(this.cursor.offset - 1) == '{';
      int indent = this.getIndent(line) + (unwrap ? 4 : 0);
      super.keyNewLine(undo);
      String margin = this.createIndent(indent);
      this.writeString(margin);
      this.cursor.offset = indent;
      undo.postText = undo.postText + margin;
      if (unwrap) {
         super.keyNewLine(undo);
         margin = this.createIndent(indent - 4);
         this.writeString(margin);
         --this.cursor.line;
         this.cursor.offset = indent;
         undo.postText = undo.postText + margin;
      }

   }

   protected void keyBackspace(TextEditUndo undo, boolean ctrl) {
      String line = ((HighlightedTextLine)this.text.get(this.cursor.line)).text;
      line = this.cursor.start(line);
      if (!line.isEmpty() && line.trim().isEmpty()) {
         int offset = 4 - line.length() % 4;
         this.startSelecting();
         Cursor var10000 = this.cursor;
         var10000.offset -= offset;
         String deleted = this.getSelectedText();
         this.deleteSelection();
         this.deselect();
         undo.text = deleted;
      } else {
         super.keyBackspace(undo, ctrl);
      }

   }

   protected void keyTab(TextEditUndo undo) {
      if (this.isSelected()) {
         boolean shift = GuiUtils.isShiftKeyDown();
         Cursor min = this.getMin();
         if (shift) {
            min.offset = Math.max(min.offset - 4, 0);
         }

         Cursor temp = new Cursor();
         List<String> splits = GuiMultiTextElement.splitNewlineString(this.getSelectedText());

         for(int i = 0; i < splits.size(); ++i) {
            if (shift) {
               int indent = this.getIndent((String)splits.get(i));
               splits.set(i, ((String)splits.get(i)).substring(Math.min(indent, 4)));
            } else {
               Object var10002 = splits.get(i);
               splits.set(i, "    " + (String)var10002);
            }
         }

         String result = String.join("\n", splits);
         temp.copy(min);
         this.deleteSelection();
         this.writeString(result);
         this.getMin().set(min.line, ((String)splits.get(splits.size() - 1)).length());
         min.copy(temp);
         if (!shift) {
            min.offset += 4;
         }

         undo.postText = result;
      } else {
         super.keyTab(undo);
      }

   }

   
   public void draw(GuiContext context) {
      this.refreshFoldsIfDirty();
      this.updateHyperlinkHover(context);
      super.draw(context);
   }

   public void unfocus(GuiContext context) {
      super.unfocus(context);
      this.clearHyperlinkHover();
      this.applyHyperlinkCursor(false);
   }

   private void updateHyperlinkHover(GuiContext context) {
      boolean ctrl = GuiUtils.isCtrlKeyDown();
      boolean inside = this.area.isInside(context);
      if (!ctrl || !inside || this.text.isEmpty()) {
         this.clearHyperlinkHover();
         this.applyHyperlinkCursor(false);
         return;
      }

      Cursor hovered = new Cursor();
      Cursor current = new Cursor();
      current.copy(this.cursor);
      this.moveCursorTo(hovered, context.mouseX, context.mouseY);
      this.cursor.copy(current);

      if (this.hyperlinkCtrl != ctrl || this.hyperlinkRevision != this.diagnosticRevision
         || this.hyperlinkCursorLine != hovered.line || this.hyperlinkCursorOffset != hovered.offset) {
         this.clearHyperlinkHover();
         this.hyperlinkCtrl = ctrl;
         this.hyperlinkRevision = this.diagnosticRevision;
         this.hyperlinkCursorLine = hovered.line;
         this.hyperlinkCursorOffset = hovered.offset;

         JavaScriptNavigation.Target target = JavaScriptNavigation.resolve(this.getText(), this.cursorToOffset(hovered));
         if (target != null) {
            Cursor start = this.offsetToCursor(this.getText(), target.start);
            Cursor end = this.offsetToCursor(this.getText(), target.end);
            if (start.line == end.line) {
               this.hyperlinkLine = start.line;
               this.hyperlinkStart = start.offset;
               this.hyperlinkEnd = end.offset;
            }
         }
      }

      this.applyHyperlinkCursor(this.hyperlinkLine >= 0);
   }

   private void clearHyperlinkHover() {
      this.hyperlinkLine = -1;
      this.hyperlinkStart = -1;
      this.hyperlinkEnd = -1;
      this.hyperlinkCursorLine = -1;
      this.hyperlinkCursorOffset = -1;
      this.hyperlinkRevision = -1;
      this.hyperlinkCtrl = false;
   }

   private void applyHyperlinkCursor(boolean pointer) {
      if (this.hyperlinkPointer == pointer) {
         return;
      }

      this.hyperlinkPointer = pointer;
      if (pointer && this.handCursor == 0L) {
         this.handCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
      }

      GLFW.glfwSetCursor(this.mc.method_22683().method_4490(), pointer ? this.handCursor : 0L);
   }

   




   public boolean mouseClicked(GuiContext context) {
      this.refreshFoldsIfDirty();
      if (context.mouseButton == 0 && this.isFoldControl(context)) {
         int line = this.getVisibleLineAt(context.mouseY);
         this.toggleFold(line);
         return true;
      }

      if (context.mouseButton == 0 && GuiUtils.isCtrlKeyDown() && this.area.isInside(context)) {
         Cursor clicked = new Cursor();
         this.moveCursorTo(clicked, context.mouseX, context.mouseY);
         JavaScriptNavigation.Target target = JavaScriptNavigation.resolve(this.getText(), this.cursorToOffset(clicked));

         if (target != null) {
            this.closeAutoComplete();
            if (target.isApi()) {
               this.selectRange(target.start, target.end);
               if (this.apiDocumentationHandler != null) {
                  this.apiDocumentationHandler.accept(target.suggestion);
               }
            } else {
               this.selectRange(target.declarationStart, target.declarationEnd);
            }

            return true;
         }
      }

      return super.mouseClicked(context);
   }

   
   public void moveCursorTo(Cursor cursor, int x, int y) {
      if (this.wrapping) {
         super.moveCursorTo(cursor, x, y);
         this.ensureVisibleCursor(cursor);
         return;
      }

      int line = this.getVisibleLineAt(y);
      cursor.line = line;
      String sourceLine = ((HighlightedTextLine)this.text.get(line)).text;
      int localX = x - this.area.x - this.padding + this.horizontal.scroll - this.getShiftX();
      if (localX <= 0) {
         cursor.offset = 0;
      } else if (localX >= this.font.method_1727(sourceLine)) {
         cursor.offset = sourceLine.length();
      } else {
         int offset = 0;
         while (offset < sourceLine.length() && this.font.method_1727(sourceLine.substring(0, offset + 1)) <= localX) {
            ++offset;
         }
         cursor.offset = offset;
      }
   }

   public void moveCursor(int x, int y, boolean jumpLine) {
      super.moveCursor(x, y, jumpLine);
      this.ensureVisibleCursor(this.cursor);
   }

   protected Vector2d getCursorPosition(Cursor cursor) {
      if (this.wrapping) {
         return super.getCursorPosition(cursor);
      }

      Vector2d position = new Vector2d();
      int rows = 0;
      for (int index = 0; index < cursor.line && index < this.text.size(); ++index) {
         rows += ((HighlightedTextLine)this.text.get(index)).getLines();
      }

      String sourceLine = this.hasLine(cursor.line) ? ((HighlightedTextLine)this.text.get(cursor.line)).text : "";
      position.x = this.font.method_1727(sourceLine.substring(0, Math.max(0, Math.min(cursor.offset, sourceLine.length()))));
      position.y = rows * this.lineHeight;
      position.x = position.x - this.horizontal.scroll + this.getShiftX();
      position.y -= this.vertical.scroll;
      return position;
   }

   private int cursorToOffset(Cursor cursor) {
      int offset = 0;
      int last = Math.min(cursor.line, this.text.size());

      for(int index = 0; index < last; ++index) {
         offset += ((HighlightedTextLine)this.text.get(index)).text.length() + 1;
      }

      if (cursor.line >= 0 && cursor.line < this.text.size()) {
         offset += Math.max(0, Math.min(cursor.offset, ((HighlightedTextLine)this.text.get(cursor.line)).text.length()));
      }

      return offset;
   }

   public boolean keyTyped(GuiContext context) {
      if (this.isFocused() && GuiUtils.isCtrlKeyDown() && context.keyCode == Keyboard.KEY_F) {
         if (this.findReplaceHandler != null) {
            this.findReplaceHandler.run();
         }

         return true;
      }

      if (this.handleAutoCompleteKey(context)) {
         return true;
      }

      boolean handled = super.keyTyped(context);
      if (this.isFocused()) {
         this.refreshAutoComplete(context);
      }

      return handled;
   }

   private boolean handleAutoCompleteKey(GuiContext context) {
      if (!this.hasAutoCompleteMenu()) {
         return false;
      }

      if (context.keyCode == 200) {
         this.autoCompleteMenu.moveSelection(-1);
         return true;
      }

      if (context.keyCode == 208) {
         this.autoCompleteMenu.moveSelection(1);
         return true;
      }

      if (context.keyCode == 15 || context.keyCode == 28) {
         AutoCompleteConfig.Suggestion suggestion = this.autoCompleteMenu.getSelected();
         this.applyAutoCompleteSuggestion(suggestion);
         return true;
      }

      if (context.keyCode == 1) {
         this.closeAutoComplete();
         return true;
      }

      return false;
   }

   private void refreshAutoComplete(GuiContext context) {
      if (!Config.isAutocompleteEnabled() || this.isSelected() || !this.hasLine(this.cursor.line)) {
         this.closeAutoComplete();
         return;
      }

      List<AutoCompleteConfig.Suggestion> suggestions = this.findAutoCompleteSuggestions();
      if (suggestions.isEmpty()) {
         this.closeAutoComplete();
         return;
      }

      AutoCompleteMenu menu = new AutoCompleteMenu(this.mc, suggestions, this::applyAutoCompleteSuggestion);
      Vector2d position = this.getCursorPosition(this.cursor);
      int x = this.area.x + this.padding + (int)position.x;
      int y = this.area.y + this.padding + (int)position.y + this.lineHeight;
      menu.placeAt(context, x, y);
      context.replaceContextMenu(menu);
      this.autoCompleteMenu = menu;
   }

   private List<AutoCompleteConfig.Suggestion> findAutoCompleteSuggestions() {
      List<String> lines = new ArrayList();
      for(HighlightedTextLine textLine : this.text) {
         lines.add(textLine.text);
      }

      String line = ((HighlightedTextLine)this.text.get(this.cursor.line)).text;
      int offset = this.cursor.offset;
      String fullText = AutoCompleteEngine.joinLines(lines);
      String interpolationPrefix = AutoCompleteEngine.extractInterpolationPrefix(line, offset);
      if (interpolationPrefix != null) {
         return AutoCompleteEngine.findMatchingInterpolations(interpolationPrefix);
      }

      String javaTypePrefix = AutoCompleteEngine.extractJavaTypePrefix(line, offset);
      if (javaTypePrefix != null) {
         return AutoCompleteEngine.findMatchingJavaClasses(javaTypePrefix);
      }

      String hudElementPrefix = AutoCompleteEngine.extractHudElementNamePrefix(line, offset);
      if (hudElementPrefix != null) {
         return AutoCompleteEngine.findMatchingHudElementNames(hudElementPrefix);
      }

      String valuePrefix = AutoCompleteEngine.extractValueKeyPrefix(line, offset);
      if (valuePrefix != null) {
         return AutoCompleteEngine.findMatchingValueKeys(valuePrefix, fullText);
      }

      String keyBindingActionPrefix = AutoCompleteEngine.extractKeyBindingActionPrefix(line, offset);
      if (keyBindingActionPrefix != null) {
         return AutoCompleteEngine.findMatchingKeyBindingActions(keyBindingActionPrefix);
      }

      String keyBindingPrefix = AutoCompleteEngine.extractKeyBindingPrefix(line, offset);
      if (keyBindingPrefix != null) {
         return AutoCompleteEngine.findMatchingKeyBindingKeys(keyBindingPrefix);
      }

      String iconPrefix = AutoCompleteEngine.extractIconPrefix(line, offset);
      if (iconPrefix != null) {
         return AutoCompleteEngine.findMatchingIcons(iconPrefix);
      }

      String shaderPrefix = AutoCompleteEngine.extractShaderPrefix(line, offset);
      if (shaderPrefix != null) {
         requestShaderNames();
         return AutoCompleteEngine.findMatchingShaders(shaderPrefix);
      }

      String hudPrefix = AutoCompleteEngine.extractHUDPrefix(line, offset);
      if (hudPrefix != null) {
         return AutoCompleteEngine.findMatchingHuds(hudPrefix);
      }

      String soundPrefix = AutoCompleteEngine.extractSoundPrefix(line, offset);
      if (soundPrefix != null) {
         return AutoCompleteEngine.findMatchingSounds(soundPrefix);
      }

      String[] completionContext = AutoCompleteEngine.extractContext(line, offset);
      if (completionContext == null) {
         return new ArrayList();
      }

      String prefix = completionContext[1];
      if (".".equals(completionContext[0])) {
         String chain = completionContext[2];
         if ("Java".equals(chain)) {
            return AutoCompleteEngine.findMatchingJavaNashorn(prefix);
         }

         if (AutoCompleteEngine.isHudElementChain(chain)) {
            return AutoCompleteEngine.findHudElementMethods(prefix);
         }

         String type = (String)this.localAutoCompleteTypes.get(chain);
         if (type == null || type.isEmpty()) {
            type = AutoCompleteEngine.resolveChainType(chain, fullText);
         }
         if (type == null || type.isEmpty()) {
            return AutoCompleteEngine.findMethodsForKnownVar(chain, prefix, lines, this.clientScriptMode);
         }

         return AutoCompleteEngine.findMethodsOfClass(type, prefix, this.clientScriptMode);
      }

      List<AutoCompleteConfig.Suggestion> scope = AutoCompleteEngine.findMatchingInScope(prefix, lines, AutoCompleteEngine.isInsideSwitch(lines, this.cursor.line));
      List<AutoCompleteConfig.Suggestion> result = new ArrayList(AutoCompleteEngine.injectMappetGlobal(prefix, scope));
      for(Map.Entry<String, String> entry : this.localAutoCompleteTypes.entrySet()) {
         String name = (String)entry.getKey();
         if ((prefix == null || prefix.isEmpty() || name.startsWith(prefix)) && !this.containsAutoCompleteSuggestion(result, name)) {
            result.add(new AutoCompleteConfig.Suggestion(name, (String)entry.getValue(), "var"));
         }
      }
      return result;
   }

   private boolean containsAutoCompleteSuggestion(List<AutoCompleteConfig.Suggestion> suggestions, String name) {
      for(AutoCompleteConfig.Suggestion suggestion : suggestions) {
         if (name.equals(suggestion.methodName)) {
            return true;
         }
      }
      return false;
   }

   private void applyAutoCompleteSuggestion(AutoCompleteConfig.Suggestion suggestion) {
      if (suggestion == null || !this.hasLine(this.cursor.line)) {
         this.closeAutoComplete();
         return;
      }

      String line = ((HighlightedTextLine)this.text.get(this.cursor.line)).text;
      int offset = this.cursor.offset;
      int[] newOffset = new int[]{offset};
      String replacement;
      if (AutoCompleteEngine.extractInterpolationPrefix(line, offset) != null) {
         replacement = AutoCompleteEngine.applyInterpolationCompletion(line, offset, suggestion.methodName, newOffset);
      } else if (AutoCompleteEngine.extractJavaTypePrefix(line, offset) != null) {
         replacement = AutoCompleteEngine.applyJavaTypeCompletion(line, offset, suggestion.methodName, newOffset);
      } else if (AutoCompleteEngine.extractHudElementNamePrefix(line, offset) != null) {
         replacement = AutoCompleteEngine.applyHudElementNameCompletion(line, offset, suggestion.methodName, newOffset);
      } else if (AutoCompleteEngine.extractValueKeyPrefix(line, offset) != null) {
         replacement = AutoCompleteEngine.applyValueKeyCompletion(line, offset, suggestion.methodName, newOffset);
      } else if (AutoCompleteEngine.extractKeyBindingActionPrefix(line, offset) != null) {
         replacement = AutoCompleteEngine.applyKeyBindingActionCompletion(line, offset, suggestion.methodName, newOffset);
      } else if (AutoCompleteEngine.extractKeyBindingPrefix(line, offset) != null) {
         replacement = AutoCompleteEngine.applyKeyBindingCompletion(line, offset, suggestion.methodName, newOffset);
      } else if (AutoCompleteEngine.extractIconPrefix(line, offset) != null) {
         replacement = AutoCompleteEngine.applyIconCompletion(line, offset, suggestion.methodName, newOffset);
      } else if (AutoCompleteEngine.extractShaderPrefix(line, offset) != null) {
         replacement = AutoCompleteEngine.applyShaderCompletion(line, offset, suggestion.methodName, newOffset);
      } else if (AutoCompleteEngine.extractHUDPrefix(line, offset) != null) {
         replacement = AutoCompleteEngine.applyHUDCompletion(line, offset, suggestion.methodName, newOffset);
      } else if (AutoCompleteEngine.extractSoundPrefix(line, offset) != null) {
         replacement = AutoCompleteEngine.applySoundCompletion(line, offset, suggestion.methodName, newOffset);
      } else if ("kw".equals(suggestion.className)) {
         replacement = AutoCompleteEngine.applyCompletionKeyword(line, offset, suggestion.methodName, suggestion.className, newOffset);
      } else if ("var".equals(suggestion.className) || "[]".equals(suggestion.className) || "{}".equals(suggestion.className) || "alias".equals(suggestion.className) || "value".equals(suggestion.className)) {
         replacement = AutoCompleteEngine.applyCompletionRaw(line, offset, suggestion.methodName, newOffset);
      } else {
         replacement = AutoCompleteEngine.applyCompletion(line, offset, suggestion.methodName, newOffset);
      }

      this.replaceCurrentLine(replacement, newOffset[0]);
      this.closeAutoComplete();
   }

   private void replaceCurrentLine(String replacement, int newOffset) {
      String current = ((HighlightedTextLine)this.text.get(this.cursor.line)).text;
      int lineIndex = this.cursor.line;
      this.cursor.offset = 0;
      this.startSelecting();
      this.cursor.offset = current.length();
      this.pasteText(replacement);
      this.cursor.line = lineIndex;
      this.cursor.offset = Math.max(0, Math.min(newOffset, replacement.length()));
      this.deselect();
      this.moveViewportToCursor();
   }

   private boolean hasAutoCompleteMenu() {
      return this.autoCompleteMenu != null && this.autoCompleteMenu.hasParent();
   }

   private void closeAutoComplete() {
      if (this.autoCompleteMenu != null && this.autoCompleteMenu.hasParent()) {
         this.autoCompleteMenu.removeFromParent();
      }

      this.autoCompleteMenu = null;
   }

   public int getIndent(int i) {
      return this.hasLine(i) ? this.getIndent(((HighlightedTextLine)this.text.get(i)).text) : 0;
   }

   public int getIndent(String line) {
      for(int j = 0; j < line.length(); ++j) {
         char c = line.charAt(j);
         if (c != ' ') {
            return j;
         }
      }

      return line.length();
   }

   public String createIndent(int i) {
      StringBuilder builder;
      for(builder = new StringBuilder(); i > 0; --i) {
         builder.append(' ');
      }

      return builder.toString();
   }

   protected void drawTextLine(String line, int i, int j, int nx, int ny) {
      List<SourceDiagnostic> diagnostics = this.getSyntaxDiagnostics();
      if (this.lines && j == 0 && !this.foldSpacerLines.contains(i)) {
         String label = String.valueOf(i + 1);
         int x = this.area.x + 17 + this.placements - this.font.method_1727(label);
         if (this.lineNumber >= this.numbers.size()) {
            this.numbers.add(new TextLineNumber());
         }

         ((TextLineNumber)this.numbers.get(this.lineNumber)).set(label, x, ny);
         ++this.lineNumber;
      }

      HighlightedTextLine textLine = (HighlightedTextLine)this.text.get(i);
      if (textLine.segments == null) {
         textLine.setSegments(this.highlighter.parse(this.font, this.text, textLine.text, i));
         if (textLine.wrappedLines != null) {
            textLine.calculateWrappedSegments(this.font);
         }
      }

      List<TextSegment> segments = textLine.segments;
      if (textLine.wrappedSegments != null) {
         segments = j < textLine.wrappedSegments.size() ? (List)textLine.wrappedSegments.get(j) : null;
      }

      if (segments != null) {
         for(TextSegment segment : segments) {
            GuiDraw.drawString(this.font, segment.text, nx, ny, segment.color, this.highlighter.getStyle().shadow);
            nx += segment.width;
         }
      }

      int lineX = nx - (segments == null ? 0 : this.getSegmentsWidth(segments));
      if (j == 0) {
         for(SourceDiagnostic diagnostic : diagnostics) {
            if (diagnostic.line == i) {
               this.drawSyntaxUnderline(line, lineX, ny, diagnostic);
            }
         }
      }

      this.drawHyperlinkUnderline(textLine, line, i, j, lineX, ny);
      if (j == 0 && this.foldedBlocks.contains(i)) {
         GuiDraw.drawString(this.font, "…", lineX + this.font.method_1727(line) + 4, ny, -8355712, this.highlighter.getStyle().shadow);
      }
   }

   
   private void drawColorLiteralSwatches(String line, int lineX, int y) {
      Matcher matcher = HEX_COLOR_LITERAL.matcher(line);

      while (matcher.find()) {
         int x = lineX + this.font.method_1727(line.substring(0, matcher.end())) + 3;
         int top = y + 1;
         if (x + 8 > this.area.ex() - 2 || top + 8 > this.area.ey()) {
            continue;
         }

         String digits = matcher.group().substring(2);
         int color;
         try {
            long value = Long.parseLong(digits, 16);
            color = digits.length() == 6 ? (int)(4278190080L | value) : (int)value;
         } catch (NumberFormatException ignored) {
            continue;
         }

         GuiDraw.drawRect(x, top, x + 8, top + 8, -13684945);
         GuiDraw.drawRect(x + 1, top + 1, x + 7, top + 7, -11184811);
         GuiDraw.drawRect(x + 1, top + 1, x + 4, top + 4, -7829368);
         GuiDraw.drawRect(x + 4, top + 4, x + 7, top + 7, -7829368);
         GuiDraw.drawRect(x + 1, top + 1, x + 7, top + 7, color);
      }
   }

   
   private void drawFoldOverlay() {
      if (!this.lines || this.foldRanges.isEmpty()) {
         return;
      }

      int y = this.area.y + this.padding - this.vertical.scroll;
      for (int line = 0; line < this.text.size(); ++line) {
         HighlightedTextLine textLine = (HighlightedTextLine)this.text.get(line);
         if (textLine.getLines() > 0 && this.foldRanges.containsKey(line) && y + this.lineHeight > this.area.y && y < this.area.ey()) {
            this.drawFoldArrow(this.area.x + 8, y + 4, this.foldedBlocks.contains(line));
         }
         y += textLine.getLines() * this.lineHeight;
      }
   }

   
   private void drawFoldArrow(int centerX, int centerY, boolean collapsed) {
      for(int offset = 0; offset <= 2; ++offset) {
         if (collapsed) {
            int x = centerX + 2 - offset;
            GuiDraw.drawRect(x, centerY - offset, x + 1, centerY - offset + 1, -1);
            GuiDraw.drawRect(x, centerY + offset, x + 1, centerY + offset + 1, -1);
         } else {
            int y = centerY + 2 - offset;
            GuiDraw.drawRect(centerX - offset, y, centerX - offset + 1, y + 1, -1);
            GuiDraw.drawRect(centerX + offset, y, centerX + offset + 1, y + 1, -1);
         }
      }
   }

   private void drawHyperlinkUnderline(HighlightedTextLine textLine, String visibleLine, int line, int wrappedLine, int x, int y) {
      if (this.hyperlinkLine != line || this.hyperlinkStart < 0 || this.hyperlinkEnd <= this.hyperlinkStart) {
         return;
      }

      int offset = 0;
      if (textLine.wrappedLines != null) {
         for (int index = 0; index < wrappedLine; ++index) {
            offset += ((String)textLine.wrappedLines.get(index)).length();
         }
      }

      int start = Math.max(this.hyperlinkStart, offset);
      int end = Math.min(this.hyperlinkEnd, offset + visibleLine.length());
      if (start >= end) {
         return;
      }

      int from = start - offset;
      int to = end - offset;
      int startX = x + this.font.method_1727(visibleLine.substring(0, from));
      int endX = x + this.font.method_1727(visibleLine.substring(0, to));
      int linkColor = -16777216 | this.highlighter.getStyle().primary & 16777215;
      GuiDraw.drawRect(startX, y + this.lineHeight - 1, Math.max(startX + 1, endX), y + this.lineHeight, linkColor);
   }

   private int getSegmentsWidth(List<TextSegment> segments) {
      int width = 0;

      for(TextSegment segment : segments) {
         width += segment.width;
      }

      return width;
   }

   private void drawSyntaxUnderline(String line, int lineX, int lineY, SourceDiagnostic diagnostic) {
      if (line == null || line.isEmpty()) {
         return;
      }

      int start = Math.max(0, Math.min(diagnostic.column, line.length()));
      int end = Math.min(line.length(), start + Math.max(1, diagnostic.length));
      int x = lineX + this.font.method_1727(line.substring(0, start));
      int ex = lineX + this.font.method_1727(line.substring(0, end));

      if (ex <= x) {
         ex = x + 5;
      }

      int y = lineY + this.lineHeight - 2;
      int errorColor = this.getDiagnosticColor(diagnostic);

      for(int px = x; px < ex; px += 2) {
         int dy = (px - x) / 2 % 2;
         GuiDraw.drawRect(px, y + dy, Math.min(px + 2, ex), y + dy + 1, errorColor);
      }
   }

   private int getDiagnosticColor(SourceDiagnostic diagnostic) {
      return diagnostic.warning ? WARNING_COLOR : -16777216 | this.highlighter.getStyle().error & 16777215;
   }

   protected int getShiftX() {
      return this.lines ? 22 + this.placements : 0;
   }

   private boolean isFoldControl(GuiContext context) {
      return this.lines && this.area.isInside(context) && context.mouseX >= this.area.x && context.mouseX < this.area.x + 15
         && this.foldRanges.containsKey(this.getVisibleLineAt(context.mouseY));
   }

   private int getVisibleLineAt(int mouseY) {
      if (this.text.isEmpty()) {
         return 0;
      }

      int targetRow = Math.max(0, (mouseY - this.area.y - this.padding + this.vertical.scroll) / this.lineHeight);
      int row = 0;
      for (int index = 0; index < this.text.size(); ++index) {
         int lines = ((HighlightedTextLine)this.text.get(index)).getLines();
         if (targetRow < row + lines) {
            return index;
         }
         row += lines;
      }

      for (int index = this.text.size() - 1; index >= 0; --index) {
         if (((HighlightedTextLine)this.text.get(index)).getLines() > 0) {
            return index;
         }
      }

      return 0;
   }

   private void ensureFoldState() {
      if (this.foldRanges == null) {
         this.foldRanges = new HashMap();
      }
      if (this.foldedBlocks == null) {
         this.foldedBlocks = new HashSet();
      }
   }

   private void toggleFold(int start) {
      this.ensureFoldState();
      if (!this.foldRanges.containsKey(start)) {
         return;
      }

      if (!this.foldedBlocks.add(start)) {
         this.foldedBlocks.remove(start);
      }
      this.applyFoldVisibility();
   }

   private void resetFoldsForTextChange() {
      

      this.foldRangesDirty = true;
   }

   private void refreshFoldsIfDirty() {
      this.ensureFoldState();
      if (!this.foldRangesDirty) {
         return;
      }

            this.refreshFoldRanges();
      

      this.foldedBlocks.retainAll(this.foldRanges.keySet());
      if (!this.foldedBlocks.isEmpty()) {
         this.applyFoldVisibility();
      } else {
         this.foldSpacerLines = Collections.emptySet();
      }
      this.foldRangesDirty = false;

   }

   private void ensureVisibleCursor(Cursor cursor) {
      int containing = -1;
      for (Map.Entry<Integer, Integer> entry : this.foldRanges.entrySet()) {
         if (this.foldedBlocks.contains(entry.getKey()) && cursor.line > entry.getKey() && cursor.line <= entry.getValue()) {
            containing = Math.max(containing, entry.getKey());
         }
      }
      if (containing >= 0) {
         cursor.set(containing, Math.min(cursor.offset, ((HighlightedTextLine)this.text.get(containing)).text.length()));
      }
   }

   private void applyFoldVisibility() {
      this.ensureFoldState();
      boolean[] hidden = new boolean[this.text.size()];
      Set<Integer> spacers = new HashSet();
      for (Map.Entry<Integer, Integer> entry : this.foldRanges.entrySet()) {
         if (this.foldedBlocks.contains(entry.getKey())) {
            for (int line = entry.getKey() + 1; line <= entry.getValue() && line < hidden.length; ++line) {
               hidden[line] = true;
            }
         }
      }
      

      for (Map.Entry<Integer, Integer> entry : this.foldRanges.entrySet()) {
         if (!this.foldedBlocks.contains(entry.getKey())) {
            continue;
         }
         int closingLine = entry.getValue();
         boolean insideAnotherFold = false;
         for (Map.Entry<Integer, Integer> other : this.foldRanges.entrySet()) {
            if (other != entry && this.foldedBlocks.contains(other.getKey())
               && other.getKey() < closingLine && other.getValue() >= closingLine) {
               insideAnotherFold = true;
               break;
            }
         }
         if (!insideAnotherFold && closingLine >= 0 && closingLine < hidden.length) {
            spacers.add(closingLine);
         }
      }
      this.foldSpacerLines = spacers;

      for (int line = 0; line < this.text.size(); ++line) {
         HighlightedTextLine textLine = (HighlightedTextLine)this.text.get(line);
         if (hidden[line]) {
            if (spacers.contains(line)) {
               textLine.wrappedLines = Collections.singletonList("");
               textLine.wrappedSegments = Collections.singletonList(Collections.emptyList());
            } else {
               textLine.wrappedLines = Collections.emptyList();
               textLine.wrappedSegments = Collections.emptyList();
            }
         } else {
            textLine.resetWrapping();
            if (this.wrapping) {
               this.calculateWrappedLine(textLine);
            }
         }
      }

      this.ensureVisibleCursor(this.cursor);
      this.ensureVisibleCursor(this.selection);
      if (this.selection.line < 0) {
         this.deselect();
      }
      this.recalculateSizes();
      this.vertical.clamp();
      this.horizontal.clamp();
   }

   private void refreshFoldRanges() {
      this.ensureFoldState();
      this.foldRanges.clear();
      Deque<FoldStart> opens = new ArrayDeque();
      boolean lineComment = false;
      boolean blockComment = false;
      boolean escaped = false;
      char quote = '\u0000';
      int line = 0;
      int lineStart = 0;
      String source = this.getText();

      for (int index = 0; index < source.length(); ++index) {
         char character = source.charAt(index);
         char next = index + 1 < source.length() ? source.charAt(index + 1) : '\u0000';
         if (lineComment) {
            if (character == '\n') {
               lineComment = false;
            }
         } else if (blockComment) {
            if (character == '*' && next == '/') {
               blockComment = false;
               ++index;
            }
         } else if (quote != '\u0000') {
            if (escaped) {
               escaped = false;
            } else if (character == '\\') {
               escaped = true;
            } else if (character == quote) {
               quote = '\u0000';
            }
         } else if (character == '/' && next == '/') {
            lineComment = true;
            ++index;
         } else if (character == '/' && next == '*') {
            blockComment = true;
            ++index;
         } else if (character == '\'' || character == '"' || character == '`') {
            quote = character;
         } else if (character == '{') {
            opens.push(new FoldStart(line, this.isFoldableBlockOpener(source, lineStart, index)));
         } else if (character == '}' && !opens.isEmpty()) {
            FoldStart start = (FoldStart)opens.pop();
            if (start.foldable && start.line < line) {
               this.foldRanges.put(start.line, line);
            }
         }

         if (character == '\n') {
            ++line;
            lineStart = index + 1;
         }
      }

      this.mergeElseFoldChains();
   }

   
   private boolean isFoldableBlockOpener(String source, int lineStart, int braceIndex) {
      String header = this.getFoldHeader(source, lineStart, braceIndex);
      if (header.contains("=>")) {
         return true;
      }

      String[] keywords = new String[] {"function", "if", "else", "switch", "for", "while", "do", "try", "catch", "finally", "class", "with"};
      for (String keyword : keywords) {
         int offset = header.lastIndexOf(keyword);
         if (offset >= 0 && this.isJavaScriptWordBoundary(header, offset - 1) && this.isJavaScriptWordBoundary(header, offset + keyword.length())) {
            return true;
         }
      }

      return false;
   }

   

   private String getFoldHeader(String source, int lineStart, int braceIndex) {
      String header = source.substring(Math.max(0, lineStart), braceIndex).trim();
      if (!header.isEmpty()) {
         return header;
      }

      int lineEnd = lineStart - 1;
      while (lineEnd >= 0) {
         int previousStart = source.lastIndexOf('\n', Math.max(0, lineEnd - 1)) + 1;
         String previous = source.substring(previousStart, lineEnd).trim();
         if (!previous.isEmpty()) {
            return previous;
         }
         lineEnd = previousStart - 1;
      }

      return "";
   }

   private boolean isJavaScriptWordBoundary(String value, int index) {
      return index < 0 || index >= value.length() || !Character.isJavaIdentifierPart(value.charAt(index));
   }

   private static class FoldStart {
      public final int line;
      public final boolean foldable;

      public FoldStart(int line, boolean foldable) {
         this.line = line;
         this.foldable = foldable;
      }
   }

   private void mergeElseFoldChains() {
      String[] lines = this.getText().split("\\n", -1);
      List<Integer> starts = new ArrayList(this.foldRanges.keySet());
      for (Integer start : starts) {
         Integer end = (Integer)this.foldRanges.get(start);
         if (end == null) {
            continue;
         }

         int mergedEnd = end;
         List<Integer> chainStarts = new ArrayList();
         while (this.isElseContinuation(lines, mergedEnd) && this.foldRanges.containsKey(mergedEnd)) {
            chainStarts.add(mergedEnd);
            mergedEnd = (Integer)this.foldRanges.get(mergedEnd);
         }

         if (mergedEnd != end) {
            this.foldRanges.put(start, mergedEnd);
            for (Integer chainStart : chainStarts) {
               this.foldRanges.remove(chainStart);
            }
         }
      }
   }

   private boolean isElseContinuation(String[] lines, int closingLine) {
      if (closingLine < 0 || closingLine >= lines.length) {
         return false;
      }

      String tail = lines[closingLine].trim();
      int close = tail.indexOf('}');
      return close >= 0 && tail.substring(close + 1).trim().startsWith("else");
   }

   protected void drawBackground() {
      this.area.draw(-16777216 + ColorUtils.multiplyColor(this.highlighter.getStyle().background, 0.8F));
   }

   private List<SourceDiagnostic> getSyntaxDiagnostics() {
      if (this.syntaxDirty) {
         String source = this.getText();
         if (this.javaScriptDiagnostics) {
            

            this.syntaxDiagnostics = this.javaScriptSnapshot.getDiagnostics();
            if (this.javaScriptSnapshot.getRevision() != this.diagnosticRevision) {
               this.requestJavaScriptDiagnostics(source, this.diagnosticRevision);
            }
         } else {
            this.syntaxDiagnostics = diagnose(source);
         }

         this.syntaxDirty = false;
      }

      return this.syntaxDiagnostics;
   }

   private void markSyntaxDirty() {
      ++this.diagnosticRevision;
      this.syntaxDirty = true;
   }

   private void requestJavaScriptDiagnostics(String source, int revision) {
      if (this.requestedJavaScriptRevision == revision) {
         return;
      }

      this.requestedJavaScriptRevision = revision;
      Thread worker = new Thread(() -> {
         try {
            Thread.sleep(JAVASCRIPT_DIAGNOSTIC_DELAY_MS);
            if (revision != this.diagnosticRevision) {
               return;
            }

            Set<String> libraryFunctions = this.javaScriptLibraryFunctions;
            DiagnosticSnapshot snapshot = JavaScriptDiagnostics.createSnapshot(source, revision, libraryFunctions);

            if (revision == this.diagnosticRevision && snapshot.getRevision() == revision) {
               this.javaScriptSnapshot = snapshot;
               this.syntaxDirty = true;
            }
         } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
         }
      }, "Mappet JS diagnostics");
      worker.setDaemon(true);
      worker.start();
   }

   private static List<SourceDiagnostic> diagnose(String source) {
      Deque<Bracket> brackets = new ArrayDeque();
      boolean lineComment = false;
      boolean blockComment = false;
      boolean escaped = false;
      char quote = '\u0000';
      int quoteLine = 0;
      int quoteColumn = 0;
      int commentLine = 0;
      int commentColumn = 0;
      int line = 0;
      int column = 0;

      for(int index = 0; index < source.length(); ++index) {
         char character = source.charAt(index);
         char next = index + 1 < source.length() ? source.charAt(index + 1) : '\u0000';

         if (lineComment) {
            if (character == '\n') {
               lineComment = false;
            }
         } else if (blockComment) {
            if (character == '*' && next == '/') {
               blockComment = false;
            }
         } else if (quote != '\u0000') {
            if (character == '\n' && quote != '`') {
               return singleDiagnostic(quoteLine, quoteColumn, 1, "Незавершённая строка");
            }

            if (escaped) {
               escaped = false;
            } else if (character == '\\') {
               escaped = true;
            } else if (character == quote) {
               quote = '\u0000';
            }
         } else if (character == '/' && next == '/') {
            lineComment = true;
         } else if (character == '/' && next == '*') {
            blockComment = true;
            commentLine = line;
            commentColumn = column;
         } else if (character == '\'' || character == '\"' || character == '`') {
            quote = character;
            quoteLine = line;
            quoteColumn = column;
         } else if (character == '(' || character == '[' || character == '{') {
            brackets.push(new Bracket(character, line, column, findControlKeywordColumn(source, index, line, column)));
         } else if (character == ')' || character == ']' || character == '}') {
            char expected = character == ')' ? '(' : (character == ']' ? '[' : '{');
            if (brackets.isEmpty() || ((Bracket)brackets.peek()).character != expected) {
               return singleDiagnostic(line, column, 1, "Лишняя или несовпадающая закрывающая скобка");
            }

            brackets.pop();
         }

         if (character == '\n') {
            ++line;
            column = 0;
         } else {
            ++column;
         }
      }

      if (quote != '\u0000') {
         return singleDiagnostic(quoteLine, quoteColumn, 1, "Незавершённая строка");
      }

      if (blockComment) {
         return singleDiagnostic(commentLine, commentColumn, 2, "Незавершённый комментарий");
      }

      List<SourceDiagnostic> diagnostics = diagnoseEmptyIfConditions(source);
      if (!brackets.isEmpty()) {
         

         Bracket bracket = (Bracket)brackets.peek();
         int markerLength = bracket.markerColumn == bracket.column ? 1 : 2;
         diagnostics.add(new SourceDiagnostic(bracket.line, bracket.markerColumn, markerLength, "Незакрытая скобка"));
      }

      return diagnostics;
   }

   


   private static List<SourceDiagnostic> diagnoseEmptyIfConditions(String source) {
      List<SourceDiagnostic> diagnostics = new ArrayList();
      String[] lines = source.split("\\n", -1);

      for(int line = 0; line < lines.length; ++line) {
         String text = lines[line];
         int from = 0;

         while(true) {
            int column = text.indexOf("if", from);
            if (column < 0) {
               break;
            }

            from = column + 2;
            if (!isWordBoundary(text, column - 1) || !isWordBoundary(text, column + 2)) {
               continue;
            }

            int opening = column + 2;
            while(opening < text.length() && Character.isWhitespace(text.charAt(opening))) {
               ++opening;
            }

            if (opening + 1 < text.length() && text.charAt(opening) == '(' && text.charAt(opening + 1) == ')') {
               diagnostics.add(new SourceDiagnostic(line, column, 2, "Пустое условие if"));
            }
         }
      }

      return diagnostics;
   }

   private static List<SourceDiagnostic> singleDiagnostic(int line, int column, int length, String message) {
      List<SourceDiagnostic> diagnostics = new ArrayList();
      diagnostics.add(new SourceDiagnostic(line, column, length, message));
      return diagnostics;
   }

   

   private static int findControlKeywordColumn(String source, int index, int line, int fallback) {
      int lineStart = source.lastIndexOf("\n", Math.max(0, index - 1)) + 1;
      int marker = -1;
      String[] keywords = new String[]{"if", "else", "while", "for", "switch", "catch", "finally", "do"};

      for(String keyword : keywords) {
         int candidate = source.lastIndexOf(keyword, index - 1);

         if (candidate >= lineStart && candidate > marker && isWordBoundary(source, candidate - 1) && isWordBoundary(source, candidate + keyword.length())) {
            marker = candidate;
         }
      }

      return marker < 0 ? fallback : marker - lineStart;
   }

   private static boolean isWordBoundary(String source, int index) {
      return index < 0 || index >= source.length() || !Character.isLetterOrDigit(source.charAt(index)) && source.charAt(index) != '_';
   }

   private static class Bracket {
      public final char character;
      public final int line;
      public final int column;
      public final int markerColumn;

      public Bracket(char character, int line, int column, int markerColumn) {
         this.character = character;
         this.line = line;
         this.column = column;
         this.markerColumn = markerColumn;
      }
   }

   public static class SourceDiagnostic {
      public final int line;
      public final int column;
      public final int length;
      public final String message;
      public final boolean warning;
      
      public final boolean lineMarker;

      public SourceDiagnostic(int line, int column, int length, String message) {
         this(line, column, length, message, false, true);
      }

      public SourceDiagnostic(int line, int column, int length, String message, boolean warning) {
         this(line, column, length, message, warning, true);
      }

      public SourceDiagnostic(int line, int column, int length, String message, boolean warning, boolean lineMarker) {
         this.line = line;
         this.column = column;
         this.length = length;
         this.message = message;
         this.warning = warning;
         this.lineMarker = lineMarker;
      }
   }

   private boolean hasDiagnosticAtLine(List<SourceDiagnostic> diagnostics, int line) {
      return this.getDiagnosticAtLine(diagnostics, line) != null;
   }

   private SourceDiagnostic getDiagnosticAtLine(List<SourceDiagnostic> diagnostics, int line) {
      SourceDiagnostic warning = null;

      for(SourceDiagnostic diagnostic : diagnostics) {
         if (diagnostic.line == line && diagnostic.lineMarker) {
            if (!diagnostic.warning) {
               return diagnostic;
            }

            warning = diagnostic;
         }
      }

      if (warning != null) {
         return warning;
      }

      return this.getFoldedDiagnosticAtLine(diagnostics, line, this.hasLine(line) ? ((HighlightedTextLine)this.text.get(line)).text : "");
   }

   

   private SourceDiagnostic getFoldedDiagnosticAtLine(List<SourceDiagnostic> diagnostics, int line, String header) {
      if (!this.foldedBlocks.contains(line)) {
         return null;
      }

      Integer end = (Integer)this.foldRanges.get(line);
      if (end == null) {
         return null;
      }

      SourceDiagnostic candidate = null;
      for (SourceDiagnostic diagnostic : diagnostics) {
         if (diagnostic.line > line && diagnostic.line <= end && diagnostic.lineMarker) {
            if (!diagnostic.warning) {
               candidate = diagnostic;
               break;
            }
            if (candidate == null) {
               candidate = diagnostic;
            }
         }
      }

      if (candidate == null) {
         return null;
      }

      int column = header.indexOf("function");
      if (column < 0) {
         column = 0;
      }
      return new SourceDiagnostic(line, column, Math.max(1, Math.min(8, Math.max(1, header.length() - column))), candidate.message, candidate.warning, true);
   }

   protected void drawForeground(GuiContext context) {
      if (this.lines) {
         int x = this.area.x + this.getShiftX();
         GuiDraw.drawRect(this.area.x, this.area.y, x, this.area.ey(), -16777216 + this.highlighter.getStyle().background);

         List<SourceDiagnostic> diagnostics = this.getSyntaxDiagnostics();

         for(TextLineNumber number : this.numbers) {
            if (!number.draw) {
               break;
            }

            if (this.hasDiagnosticAtLine(diagnostics, Integer.parseInt(number.line) - 1)) {
               SourceDiagnostic diagnostic = this.getDiagnosticAtLine(diagnostics, Integer.parseInt(number.line) - 1);
               int errorColor = this.getDiagnosticColor(diagnostic);
               int transparentError = errorColor & 16777215;
               GuiDraw.drawHorizontalGradientRect(this.area.x + 1, number.y - 1, x - 1, number.y + this.lineHeight - 1, errorColor, transparentError);
            }

            GuiDraw.drawString(this.font, number.line, number.x, number.y, this.highlighter.getStyle().lineNumbers);
            number.draw = false;
         }

         this.lineNumber = 0;
         int a = (int)(Math.min((float)this.horizontal.scroll / 10.0F, 1.0F) * 68.0F);
         if (a > 0) {
            GuiDraw.drawHorizontalGradientRect(x, this.area.y, x + 10, this.area.ey(), a << 24, 0);
         }
      }

      this.drawFoldOverlay();
      this.drawHelpHint(context);
   }

      private void drawHelpHint(GuiContext context) {
      if (!this.hyperlinkPointer || this.hyperlinkLine < 0 || !GuiUtils.isCtrlKeyDown() || !this.area.isInside(context)) {
         return;
      }

      int iconWidth = Icons.HELP.w;
      int iconHeight = Icons.HELP.h;
      

      int x = context.mouseX + 8;
      int y = context.mouseY + 8;
      x = Math.max(this.area.x, Math.min(x, this.area.ex() - iconWidth));
      y = Math.max(this.area.y, Math.min(y, this.area.ey() - iconHeight));
      Icons.HELP.render(x, y);
   }
}
