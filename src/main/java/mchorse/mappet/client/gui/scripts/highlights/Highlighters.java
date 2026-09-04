package mchorse.mappet.client.gui.scripts.highlights;

import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import mchorse.mappet.ClientProxy;
import mchorse.mappet.client.gui.scripts.utils.SyntaxHighlighter;
import mchorse.mappet.utils.NBTToJsonLike;

public class Highlighters {
   private static File editorHighlighters;
   private static SyntaxHighlighter defaultHighlighter;

   public static List<File> highlighters() {
      List<File> highlighters = new ArrayList();
      File[] files = editorHighlighters.listFiles();
      if (files != null) {
         for(File file : files) {
            if (file.isFile() && file.getName().endsWith(".json")) {
               highlighters.add(file);
            }
         }
      }

      return highlighters;
   }

   public static File highlighterFile(String name) {
      if (!name.endsWith(".json")) {
         name = name + ".json";
      }

      return new File(editorHighlighters, name);
   }

   public static SyntaxHighlighter readHighlighter(File file) {
      try {
         SyntaxHighlighter highlighter = new SyntaxHighlighter(NBTToJsonLike.read(file));
         Set<String> keywords = new HashSet(highlighter.primaryKeywords);
         keywords.add("import");
         highlighter.primaryKeywords = keywords;
         return highlighter;
      } catch (Exception var2) {
         return defaultHighlighter;
      }
   }

   public static void writeHighlighter(File file, SyntaxHighlighter highlighter) {
      try {
         NBTToJsonLike.write(file, highlighter.toNBT());
      } catch (Exception var3) {
      }

   }

   public static void initiate() {
      if (editorHighlighters == null) {
         editorHighlighters = new File(ClientProxy.configFolder, "highlights");
         editorHighlighters.mkdirs();
         File js = new File(editorHighlighters, "js.json");
         File kts = new File(editorHighlighters, "kts.json");
         if (!js.isFile()) {
            SyntaxHighlighter jsHighlighter = new SyntaxHighlighter();
            jsHighlighter.operators = ImmutableSet.of("+", "-", "=", "/", "*", "<", new String[]{">", "~", "&", "|", "!"});
            jsHighlighter.primaryKeywords = ImmutableSet.of("break", "continue", "switch", "case", "default", "try", new String[]{"catch", "delete", "do", "while", "else", "finally", "if", "else", "for", "each", "in", "instanceof", "new", "throw", "typeof", "with", "yield", "return", "import"});
            jsHighlighter.secondaryKeywords = ImmutableSet.of("const", "function", "var", "let", "prototype", "Math", new String[]{"JSON", "mappet"});
            jsHighlighter.special = ImmutableSet.of("this", "arguments");
            jsHighlighter.typeKeywords = ImmutableSet.of("true", "false", "null", "undefined");
            jsHighlighter.functionName = Pattern.compile("[\\w_][\\d\\w_]*", 2);
            writeHighlighter(js, jsHighlighter);
            defaultHighlighter = jsHighlighter;
         } else {
            defaultHighlighter = readHighlighter(highlighterFile("js.json"));
         }

         if (!kts.isFile()) {
            SyntaxHighlighter ktsHighlighter = new SyntaxHighlighter();
            ktsHighlighter.operators = ImmutableSet.of("+", "-", "=", "/", "*", "<", new String[]{">", "~", "&", "|", "!", "..", "->"});
            ktsHighlighter.primaryKeywords = ImmutableSet.of("break", "continue", "switch", "case", "try", "catch", new String[]{"delete", "do", "while", "else", "finally", "if", "else", "for", "is", "as", "in", "instanceof", "new", "throw", "typeof", "with", "yield", "when", "return", "by", "constructor", "delegate", "dynamic", "field", "get", "set", "init", "value", "where", "actual", "annotation", "companion", "field", "external", "infix", "inline", "inner", "internal", "open", "operator", "out", "override", "suspend", "vararg"});
            ktsHighlighter.secondaryKeywords = ImmutableSet.of("abstract", "extends", "final", "implements", "interface", "super", new String[]{"throws", "data", "class", "fun", "var", "val", "import", "Java", "JSON", "mappet"});
            ktsHighlighter.special = ImmutableSet.of("this", "it");
            ktsHighlighter.typeKeywords = ImmutableSet.of("true", "false", "null", "undefined", "enum");
            ktsHighlighter.functionName = Pattern.compile("[\\w_][\\d\\w_]*", 2);
            writeHighlighter(kts, ktsHighlighter);
         }

      }
   }
}
