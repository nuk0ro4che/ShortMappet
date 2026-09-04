package mchorse.mappet.client.gui.scripts.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mchorse.mappet.client.gui.scripts.GuiTextEditor.SourceDiagnostic;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.IdeErrorReporter;





final class RhinoRecoveryDiagnostics {
   private static final int MAX_RECOVERY_DIAGNOSTICS = 24;

   private RhinoRecoveryDiagnostics() {
   }

   public static List<SourceDiagnostic> diagnose(String source) {
      if (source == null || source.isEmpty()) {
         return Collections.emptyList();
      }

      List<SourceDiagnostic> diagnostics = new ArrayList();

      try {
         RhinoIdeErrorReporter reporter = new RhinoIdeErrorReporter(source, diagnostics);
         CompilerEnvirons environs = new CompilerEnvirons();
         environs.setLanguageVersion(Context.VERSION_ES6);
         environs.setRecoverFromErrors(true);
         environs.setIdeMode(true);
         environs.setErrorReporter(reporter);
         new Parser(environs, reporter).parse(source, "<mappet-editor>", 1);
      } catch(Exception | LinkageError ignored) {
         

      }

      return diagnostics;
   }

   private static class RhinoIdeErrorReporter implements IdeErrorReporter {
      private final String source;
      private final int[] lineStarts;
      private final List<SourceDiagnostic> diagnostics;

      public RhinoIdeErrorReporter(String source, List<SourceDiagnostic> diagnostics) {
         this.source = source;
         this.lineStarts = getLineStarts(source);
         this.diagnostics = diagnostics;
      }

      public void warning(String message, String sourceName, int offset, int length) {
      }

      public void error(String message, String sourceName, int offset, int length) {
         this.add(message, offset, length);
      }

      public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
      }

      public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
         int safeLine = Math.max(0, line - 1);
         int lineStart = safeLine < this.lineStarts.length ? this.lineStarts[safeLine] : this.source.length();
         this.add(message, lineStart + Math.max(0, lineOffset), 1);
      }

      public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
         return new EvaluatorException(message, sourceName, line, lineSource, lineOffset);
      }

      private void add(String message, int offset, int length) {
         if (this.diagnostics.size() >= MAX_RECOVERY_DIAGNOSTICS) {
            return;
         }

         int safeOffset = Math.min(this.source.length(), Math.max(0, offset));
         int[] position = offsetToLineColumn(this.lineStarts, safeOffset);
         int safeLength = Math.max(1, Math.min(Math.max(1, length), Math.max(1, this.source.length() - safeOffset)));
         String compact = compactMessage(message);
         boolean lineMarker = this.isEquivalentPrimaryConditionError(compact, position[0]);

         if (this.isCascadeOnClosingBrace(compact, position[0])) {
            return;
         }

         for(int index = 0; index < this.diagnostics.size(); ++index) {
            SourceDiagnostic existing = (SourceDiagnostic)this.diagnostics.get(index);

            if (existing.line == position[0]) {
               if (isGeneric(existing.message) && !isGeneric(compact)) {
                  this.diagnostics.set(index, new SourceDiagnostic(position[0], position[1], safeLength, "Дополнительная синтаксическая ошибка: " + compact, false, lineMarker));
               }

               return;
            }
         }

         this.diagnostics.add(new SourceDiagnostic(position[0], position[1], safeLength, "Дополнительная синтаксическая ошибка: " + compact, false, lineMarker));
      }

      




      private boolean isEquivalentPrimaryConditionError(String message, int line) {
         return message != null && message.contains("missing ) after condition") && this.getLineText(line).matches(".*\\bif\\s*\\(\\s*\\).*" );
      }

      private boolean isCascadeOnClosingBrace(String message, int line) {
         if (message == null) {
            return false;
         }

         if (message.contains("missing } after function body")) {
            return true;
         }

         if (!this.isClosingBraceLine(line)) {
            return false;
         }

         if (message.contains("missing ) after condition")) {
            return true;
         }

         return isGeneric(message) && this.hasRecentDiagnosticBefore(line);
      }

      private boolean isClosingBraceLine(int line) {
         String text = this.getLineText(line).trim();
         return text.equals("}") || text.equals("};");
      }

      private String getLineText(int line) {
         int start = line < this.lineStarts.length ? this.lineStarts[line] : this.source.length();
         int end = this.source.indexOf('\n', start);

         if (end < 0) {
            end = this.source.length();
         }

         return this.source.substring(start, end);
      }

      private boolean hasRecentDiagnosticBefore(int line) {
         for(SourceDiagnostic diagnostic : this.diagnostics) {
            if (diagnostic.line < line && diagnostic.line >= line - 3) {
               return true;
            }
         }

         return false;
      }

      private static boolean isGeneric(String message) {
         return message == null || message.trim().endsWith("syntax error");
      }
   }

   private static int[] getLineStarts(String source) {
      List<Integer> starts = new ArrayList();
      starts.add(0);

      for(int index = 0; index < source.length(); ++index) {
         if (source.charAt(index) == '\n') {
            starts.add(index + 1);
         }
      }

      int[] result = new int[starts.size()];

      for(int index = 0; index < result.length; ++index) {
         result[index] = (Integer)starts.get(index);
      }

      return result;
   }

   private static int[] offsetToLineColumn(int[] lineStarts, int offset) {
      int low = 0;
      int high = lineStarts.length - 1;

      while(low <= high) {
         int middle = low + high >>> 1;

         if (lineStarts[middle] <= offset) {
            low = middle + 1;
         } else {
            high = middle - 1;
         }
      }

      int line = Math.max(0, high);
      return new int[]{line, Math.max(0, offset - lineStarts[line])};
   }

   private static String compactMessage(String message) {
      if (message == null) {
         return "";
      }

      int newline = message.indexOf('\n');
      return newline < 0 ? message : message.substring(0, newline);
   }
}
