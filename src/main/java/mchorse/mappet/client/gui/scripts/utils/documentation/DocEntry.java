package mchorse.mappet.client.gui.scripts.utils.documentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import joptsimple.internal.Strings;
import mchorse.mappet.Mappet;
import mchorse.mappet.client.gui.scripts.GuiTextEditor;
import mchorse.mappet.client.gui.utils.text.GuiText;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_124;
import net.minecraft.class_310;

public abstract class DocEntry {
   private static final Pattern CODE_BLOCK = Pattern.compile("<pre>\\s*\\{@code(?:[ \\t]*\\r?\\n)?(.*?)(?:\\s*}</pre>|\\s*}</pre(?=\\s*$)|\\s*\\z)", Pattern.DOTALL);
   public DocEntry parent;
   public String name = "";
   public String doc = "";
   public String source = "Mappet";

   private static String normalizeCode(String code) {
      StringBuilder result = new StringBuilder(code.length());
      boolean quoted = false;
      char quote = 0;
      boolean escaped = false;

      for (int i = 0; i < code.length(); i++) {
         char current = code.charAt(i);

         if (escaped) {
            result.append(current);
            escaped = false;
            continue;
         }

         if (current == '\\') {
            if (i + 1 < code.length() && code.charAt(i + 1) == 'n' && !quoted) {
               result.append('\n');
               i++;
               continue;
            }

            if (i + 1 < code.length() && code.charAt(i + 1) == 'r' && i + 2 < code.length() && code.charAt(i + 2) == '\\' && code.charAt(i + 3) == 'n' && !quoted) {
               result.append('\n');
               i += 3;
               continue;
            }

            result.append(current);
            escaped = quoted;
            continue;
         }

         if (current == '\"' && (!quoted || current == quote)) {
            quoted = !quoted;
            quote = quoted ? current : 0;
         }

         result.append(current);
      }

      return result.toString();
   }

   public static String processCode(String code) {
      if (code == null || code.trim().isEmpty()) {
         return "";
      }

      String normalized = normalizeCode(code)
         .replaceFirst("\\s*}</pre>\\s*$", "");
      String[] rawLines = normalized.split("\\n", -1);
      List<String> lines = new ArrayList<>();
      Collections.addAll(lines, rawLines);

      int first = 0;
      while (first < lines.size() && lines.get(first).trim().isEmpty()) {
         first++;
      }

      if (first >= lines.size()) {
         return "";
      }
      String firstLine = lines.get(first);
      int indent = 0;
      while (indent < firstLine.length() && (firstLine.charAt(indent) == ' ' || firstLine.charAt(indent) == '\t')) {
         indent++;
      }

      if (indent > 0) {
         for (int i = first; i < lines.size(); i++) {
            String line = lines.get(i);
            int remove = 0;

            while (remove < line.length() && remove < indent && (line.charAt(remove) == ' ' || line.charAt(remove) == '\t')) {
               remove++;
            }

            lines.set(i, line.substring(remove));
         }
      }

      int end = lines.size();
      while (end > first && lines.get(end - 1).trim().isEmpty()) {
         end--;
      }

      if (end <= first) {
         return "";
      }

      return Strings.join(lines.subList(first, end), "\n").trim();
   }

   private static void addText(String text, class_310 mc, GuiScrollElement target) {
      if (text.trim().isEmpty()) {
         return;
      }

      boolean p = text.trim().startsWith("<p>");
      String noNewLines = text.replaceAll("\\n", "").trim();
      String bold = noNewLines.replaceAll("<b>", class_124.field_1067.toString());
      String italic = bold.replaceAll("<i>", class_124.field_1056.toString());
      String strikethrough = italic.replaceAll("<s>", class_124.field_1055.toString());
      String code = strikethrough.replaceAll("<code>", class_124.field_1080.toString());
      String list = code.replaceAll("<li> *", "\n- ");
      String closing = list.replaceAll("</(b|i|s|code|ul|li)>", class_124.field_1070.toString());
      String paragraphs = closing.replaceAll("</?(p|ul|li)>", "");
      String classLink = paragraphs.replaceAll("\\{@link +[^}]+\\.([^}]+)}", String.valueOf(class_124.field_1065) + "$1" + String.valueOf(class_124.field_1070));
      String methodLink = classLink.replaceAll("\\{@link +([^}]*)#([^}]+)}", String.valueOf(class_124.field_1065) + "$1" + String.valueOf(class_124.field_1070) + "." + String.valueOf(class_124.field_1080) + "$2" + String.valueOf(class_124.field_1070));
      String plainLink = methodLink.replaceAll("\\{@link ([^}]+)}", String.valueOf(class_124.field_1065) + "$1" + String.valueOf(class_124.field_1070));
      String html = plainLink.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
      GuiText guiText = (new GuiText(mc)).text(html.trim().replaceAll(" {2,}", " "));

      if (p) {
         guiText.marginTop(12);
      }

      target.add(guiText);
   }

   private static void addCode(String code, class_310 mc, GuiScrollElement target) {
      GuiTextEditor editor = new GuiTextEditor(mc, (Consumer)null);
      String text = processCode(code).replaceAll("§", "\\\\u00A7");

      editor.setText(text);
      editor.background().flex().h(Math.max(1, editor.getLines().size()) * 12 + 20);
      editor.getHighlighter().setStyle(Mappet.scriptEditorSyntaxStyle.get());
      target.add(editor);
   }

   public static void process(String doc, class_310 mc, GuiScrollElement target) {
      if (doc == null || doc.isEmpty()) {
         return;
      }
      String normalized = doc.replace("\\r\\n", "\\n").replace("\\\\n", "\\n");
      Matcher matcher = CODE_BLOCK.matcher(normalized);
      int end = 0;

      while(matcher.find()) {
         addText(normalized.substring(end, matcher.start()), mc, target);
         addCode(matcher.group(1), mc, target);
         end = matcher.end();
      }

      addText(normalized.substring(end), mc, target);
   }

   public String getName() {
      int index = this.name.lastIndexOf(".");
      return index < 0 ? this.name : this.name.substring(index + 1);
   }

   public void fillIn(class_310 mc, GuiScrollElement target) {
      target.add((new GuiText(mc)).text(IKey.format("mappet.gui.scripts.documentation.source", new Object[]{this.source})));
      process(this.doc, mc, target);
   }

   public List<DocEntry> getEntries() {
      return Collections.emptyList();
   }

   public DocEntry getEntry() {
      return this;
   }
}
