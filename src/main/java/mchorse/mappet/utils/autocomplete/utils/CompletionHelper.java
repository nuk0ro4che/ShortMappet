package mchorse.mappet.utils.autocomplete.utils;

public class CompletionHelper {
   public static String[] extractContext(String line, int cursorOffset) {
      if (line != null && cursorOffset > 0) {
         int safeOffset = Math.max(0, Math.min(cursorOffset, line.length()));
         String sub = line.substring(0, safeOffset);

         int wordStart;
         for(wordStart = sub.length(); wordStart > 0 && (Character.isLetterOrDigit(sub.charAt(wordStart - 1)) || sub.charAt(wordStart - 1) == '_'); --wordStart) {
         }

         String word = sub.substring(wordStart);
         if (wordStart > 0 && sub.charAt(wordStart - 1) == '.') {
            int chainEnd = wordStart - 1;
            String chain = extractChain(sub, chainEnd);
            return new String[]{".", word, chain};
         } else if (!word.isEmpty()) {
            return new String[]{"", word, ""};
         } else {
            if (sub.length() > 0) {
               char last = sub.charAt(sub.length() - 1);
               if (last == '(' || last == ',') {
                  return new String[]{"", "", ""};
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   private static String extractChain(String sub, int end) {
      int i = Math.min(end, sub.length()) - 1;
      StringBuilder reversed = new StringBuilder();
      int depth = 0;

      while(i >= 0) {
         char c = sub.charAt(i);
         if (c == ')') {
            ++depth;
            reversed.append(c);
            --i;
         } else if (c == '(' && depth > 0) {
            --depth;
            reversed.append(c);
            --i;
         } else if (depth > 0) {
            reversed.append(c);
            --i;
         } else {
            if (!Character.isLetterOrDigit(c) && c != '_' && c != '.') {
               break;
            }

            reversed.append(c);
            --i;
         }
      }

      return reversed.reverse().toString();
   }

   public static String extractMethodPrefix(String line, int cursorOffset) {
      String[] ctx = extractContext(line, cursorOffset);
      return ctx != null && ".".equals(ctx[0]) ? ctx[1] : null;
   }

   public static String applyCompletion(String line, int cursorOffset, String completion, boolean isVariable, int[] newCursorOffset) {
      int safeOffset = Math.max(0, Math.min(cursorOffset, line == null ? 0 : line.length()));
      if (line == null) {
         line = "";
      }

      String before = line.substring(0, safeOffset);
      String tail = line.substring(safeOffset);

      int wordStart;
      for(wordStart = before.length(); wordStart > 0 && (Character.isLetterOrDigit(before.charAt(wordStart - 1)) || before.charAt(wordStart - 1) == '_'); --wordStart) {
      }

      int wordEnd = 0;
      while(wordEnd < tail.length() && (Character.isLetterOrDigit(tail.charAt(wordEnd)) || tail.charAt(wordEnd) == '_')) {
         ++wordEnd;
      }

      String remainder = tail.substring(wordEnd);
      String left = before.substring(0, wordStart);
      String newLine;
      int newOffset;
      if (isVariable) {
         newLine = left + completion + remainder;
         newOffset = left.length() + completion.length();
      } else {
         boolean hasOpenParen = !remainder.isEmpty() && remainder.charAt(0) == '(';
         String suffix = hasOpenParen ? "" : "()";
         newLine = left + completion + suffix + remainder;
         newOffset = left.length() + completion.length() + (hasOpenParen ? 0 : 1);
      }

      if (newCursorOffset != null) {
         newCursorOffset[0] = Math.max(0, Math.min(newOffset, newLine.length()));
      }

      return newLine;
   }

   public static String applyCompletion(String line, int cursorOffset, String selectedMethod, int[] newCursorOffset) {
      return applyCompletion(line, cursorOffset, selectedMethod, false, newCursorOffset);
   }

   public static String applyCompletionKeyword(String line, int cursorOffset, String keyword, String icon, int[] newCursorOffset) {
      int safeOffset = Math.max(0, Math.min(cursorOffset, line == null ? 0 : line.length()));
      if (line == null) {
         line = "";
      }

      String before = line.substring(0, safeOffset);
      String tail = line.substring(safeOffset);

      int wordStart;
      for(wordStart = before.length(); wordStart > 0 && (Character.isLetterOrDigit(before.charAt(wordStart - 1)) || before.charAt(wordStart - 1) == '_'); --wordStart) {
      }

      String left = before.substring(0, wordStart);
      if (keyword == null) {
         keyword = "";
      }

      String inserted;
      int cursorPos;
      switch (keyword) {
         case "function":
            inserted = "function () {}";
            cursorPos = left.length() + "function ".length();
            break;
         case "if":
            inserted = "if () {}";
            cursorPos = left.length() + "if (".length();
            break;
         case "for":
            inserted = "for (var i = 0; i < ; i++) {}";
            cursorPos = left.length() + "for (var i = 0; i < ".length();
            break;
         case "while":
            inserted = "while () {}";
            cursorPos = left.length() + "while (".length();
            break;
         case "switch":
            inserted = "switch () {}";
            cursorPos = left.length() + "switch (".length();
            break;
         case "try":
            inserted = "try {}";
            cursorPos = left.length() + "try {".length();
            break;
         case "catch":
            inserted = "catch (e) {}";
            cursorPos = left.length() + "catch (e) {".length();
            break;
         case "case":
            inserted = "case : break;";
            cursorPos = left.length() + "case ".length();
            break;
         case "return":
            inserted = "return ;";
            cursorPos = left.length() + "return ".length();
            break;
         case "var":
         case "let":
         case "const":
            inserted = keyword + " = ";
            cursorPos = left.length() + keyword.length() + 1;
            break;
         case "Java":
            inserted = "Java.";
            cursorPos = left.length() + "Java.".length();
            break;
         default:
            inserted = keyword;
            cursorPos = left.length() + keyword.length();
      }

      int wordEnd = 0;
      while(wordEnd < tail.length() && (Character.isLetterOrDigit(tail.charAt(wordEnd)) || tail.charAt(wordEnd) == '_')) {
         ++wordEnd;
      }

      String newLine = left + inserted + tail.substring(wordEnd);
      if (newCursorOffset != null) {
         newCursorOffset[0] = Math.max(0, Math.min(cursorPos, newLine.length()));
      }

      return newLine;
   }

   public static String applyCompletionRaw(String line, int cursorOffset, String completion, int[] newCursorOffset) {
      int safeOffset = Math.max(0, Math.min(cursorOffset, line == null ? 0 : line.length()));
      if (line == null) {
         line = "";
      }

      String before = line.substring(0, safeOffset);
      String tail = line.substring(safeOffset);
      int wordStart;
      for(wordStart = before.length(); wordStart > 0 && (Character.isLetterOrDigit(before.charAt(wordStart - 1)) || before.charAt(wordStart - 1) == '_'); --wordStart) {
      }

      int wordEnd = 0;
      while(wordEnd < tail.length() && (Character.isLetterOrDigit(tail.charAt(wordEnd)) || tail.charAt(wordEnd) == '_')) {
         ++wordEnd;
      }

      String left = before.substring(0, wordStart);
      String newLine = left + completion + tail.substring(wordEnd);
      int newOffset = left.length() + completion.length();

      if (newCursorOffset != null) {
         newCursorOffset[0] = Math.max(0, Math.min(newOffset, newLine.length()));
      }

      return newLine;
   }

   public static String stripColors(String s) {
      return s == null ? "" : s.replaceAll("§.", "");
   }
}
