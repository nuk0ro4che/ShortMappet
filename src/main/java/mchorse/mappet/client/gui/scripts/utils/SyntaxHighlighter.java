package mchorse.mappet.client.gui.scripts.utils;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mchorse.mappet.utils.NBTUtils;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_327;

public class SyntaxHighlighter {
   private SyntaxStyle style;
   public Set<String> operators;
   public Set<String> primaryKeywords;
   public Set<String> secondaryKeywords;
   public Set<String> special;
   public Set<String> typeKeywords;
   public Pattern functionName;
   private String buffer;
   private char string;
   private int last;

   public SyntaxHighlighter() {
      this.style = new SyntaxStyle();
   }

   public SyntaxHighlighter(class_2487 tag) {
      this.operators = ImmutableSet.copyOf(NBTUtils.getStringArray(tag.method_10554("operators", 8)));
      this.primaryKeywords = ImmutableSet.copyOf(NBTUtils.getStringArray(tag.method_10554("primaryKeywords", 8)));
      this.secondaryKeywords = ImmutableSet.copyOf(NBTUtils.getStringArray(tag.method_10554("secondaryKeywords", 8)));
      this.special = ImmutableSet.copyOf(NBTUtils.getStringArray(tag.method_10554("special", 8)));
      this.typeKeywords = ImmutableSet.copyOf(NBTUtils.getStringArray(tag.method_10554("typeKeywords", 8)));
      this.functionName = Pattern.compile(tag.method_10558("functionName"), 2);
      this.style = new SyntaxStyle();
   }

   public SyntaxStyle getStyle() {
      return this.style;
   }

   public void setStyle(SyntaxStyle style) {
      this.style = style == null ? this.style : style;
   }

   public List<TextSegment> parse(class_327 font, List<HighlightedTextLine> textLines, String line, int lineIndex) {
      List<TextSegment> list = new ArrayList();
      List<TextSegment> prevLine = lineIndex > 0 ? ((HighlightedTextLine)textLines.get(lineIndex - 1)).segments : null;
      if (prevLine != null && !prevLine.isEmpty()) {
         TextSegment last = (TextSegment)prevLine.get(prevLine.size() - 1);
         if (last.color == this.style.comments && !last.text.startsWith("//") && !last.text.trim().endsWith("*/")) {
            list.add(new TextSegment(line, this.style.comments, 0));
            return list;
         }
      }

      this.buffer = "";
      this.string = 0;
      this.last = 0;
      boolean importStatement = line.trim().startsWith("import ") || line.trim().equals("import");
      int i = 0;

      label173:
      for(int c = line.length(); i < c; ++i) {
         char character = line.charAt(i);
         char next = i < c - 1 ? line.charAt(i + 1) : 0;
         if (character == '\'' || character == '"') {
            if (this.string == 0) {
               list.add(new TextSegment(this.buffer, this.style.other, font.method_1727(this.buffer)));
               this.buffer = "";
               this.string = character;
            } else if (this.string == character) {
               char prev = i > 0 ? line.charAt(i - 1) : 0;
               if (prev != '\\') {
                  this.string = 0;
                  this.buffer = this.buffer + character;
                  list.add(new TextSegment(this.buffer, importStatement ? this.style.special : this.style.strings, font.method_1727(this.buffer)));
                  this.buffer = "";
                  continue;
               }
            }
         }

         boolean isString = this.string != 0;
         if (!isString && character == '/' && i < c - 1 && line.charAt(i + 1) == '*') {
            int lastI = i;

            for(i += 2; i < c; ++i) {
               character = line.charAt(i);
               if (character == '*' && i < c - 1 && line.charAt(i + 1) == '/') {
                  String comment = line.substring(lastI, i + 2);
                  list.add(new TextSegment(this.buffer, this.style.other, font.method_1727(this.buffer)));
                  list.add(new TextSegment(comment, this.style.comments, font.method_1727(comment)));
                  ++i;
                  this.buffer = "";
                  continue label173;
               }
            }

            String comment = line.substring(lastI);
            list.add(new TextSegment(this.buffer, this.style.other, font.method_1727(this.buffer)));
            list.add(new TextSegment(comment, this.style.comments, 0));
            return list;
         } else {
            if (!isString && character == '/' && i < c - 1 && line.charAt(i + 1) == '/') {
               String comment = line.substring(i);
               list.add(new TextSegment(this.buffer, this.style.other, font.method_1727(this.buffer)));
               list.add(new TextSegment(comment, this.style.comments, 0));
               return list;
            }

            if (!isString && this.operators.contains(String.valueOf(character))) {
               boolean isNumericalMinus = character == '-' && Character.isDigit(next);
               if (!isNumericalMinus) {
                  String sign = String.valueOf(character);
                  list.add(new TextSegment(this.buffer, this.style.other, font.method_1727(this.buffer)));
                  list.add(new TextSegment(sign, this.style.primary, font.method_1727(sign)));
                  this.buffer = "";
                  this.last = i;
                  continue;
               }
            }

            this.buffer = this.buffer + character;
            if (!isString && (next != 0 && !this.isLegalName(next) || i == c - 1)) {
               if (this.last < i) {
                  char last = line.charAt(this.last);
                  boolean predicateForNumbers = (last == '-' || last == '.') && Character.isDigit(line.charAt(this.last + 1));
                  if (!this.isLegalName(last) && !predicateForNumbers) {
                     ++this.last;
                  }
               }

               String keyword = line.substring(this.last, i + 1);
               if (importStatement && !keyword.equals("import") && !keyword.trim().isEmpty() && !keyword.equals(";")) {
                  this.pushKeyword(list, keyword, this.style.special, i, font);
               } else if (this.primaryKeywords.contains(keyword)) {
                  this.pushKeyword(list, keyword, this.style.primary, i, font);
               } else if (this.special.contains(keyword)) {
                  this.pushKeyword(list, keyword, this.style.special, i, font);
               } else if (!this.secondaryKeywords.contains(keyword) && !this.isFunctionCall(list, keyword, next)) {
                  if (this.isNumberOrConstant(keyword)) {
                     this.pushKeyword(list, keyword, this.colorForLiteral(keyword), i, font);
                  } else if (this.isIdentifier(list)) {
                     this.pushKeyword(list, keyword, this.style.identifier, i, font);
                  }
               } else {
                  this.pushKeyword(list, keyword, this.style.secondary, i, font);
               }
            }

            if (!this.isLegalName(character)) {
               this.last = i;
            }
         }
      }

      if (!this.buffer.trim().isEmpty()) {
         list.add(new TextSegment(this.buffer, this.style.other, 0));
      }

      return list;
   }

   private boolean isLegalName(char character) {
      return Character.isLetterOrDigit(character) || character == '_';
   }

   private void pushKeyword(List<TextSegment> list, String keyword, int color, int i, class_327 font) {
      if (this.buffer.length() > keyword.length()) {
         String other = this.buffer.substring(0, this.buffer.length() - keyword.length());
         list.add(new TextSegment(other, this.style.other, font.method_1727(other)));
      }

      list.add(new TextSegment(keyword, color, font.method_1727(keyword)));
      this.buffer = "";
      this.last = i + 1;
   }

   private boolean isFunctionCall(List<TextSegment> list, String keyword, char next) {
      if (!list.isEmpty()) {
         TextSegment previous = (TextSegment)list.get(list.size() - 1);
         boolean bufferIsKeyword = this.buffer.trim().equals(keyword);
         if (previous.color == this.style.strings) {
            return false;
         }

         if (bufferIsKeyword && previous.text.equals("function")) {
            return false;
         }

         if (bufferIsKeyword && previous.color != this.style.other) {
            return false;
         }

         if (previous.text.trim().equals(keyword.trim())) {
            return false;
         }
      }

      Matcher matcher = this.functionName.matcher(keyword);
      boolean matches = matcher.matches();
      return next == '(' && matches;
   }

   private int colorForLiteral(String keyword) {
      if (!keyword.startsWith("0x")) {
         return this.style.numbers;
      }

      String digits = keyword.substring(2);
      if (digits.length() != 6 && digits.length() != 8) {
         return this.style.numbers;
      }

      try {
         long value = Long.parseLong(digits, 16);
         return (int)(4278190080L | (value & 16777215L));
      } catch (NumberFormatException ignored) {
         return this.style.numbers;
      }
   }

   private boolean isNumberOrConstant(String keyword) {
      if (this.typeKeywords.contains(keyword)) {
         return true;
      } else {
         try {
            Double.parseDouble(keyword);
            return true;
         } catch (NumberFormatException var5) {
            int length = keyword.trim().length();
            if (keyword.startsWith("0x") && length >= 3 && length <= 10) {
               try {
                  Long.parseLong(keyword.substring(2), 16);
                  return true;
               } catch (Exception var4) {
               }
            }

            return false;
         }
      }
   }

   private boolean isIdentifier(List<TextSegment> list) {
      if (!list.isEmpty()) {
         TextSegment previous = (TextSegment)list.get(list.size() - 1);
         if (previous.text.trim().equals("function") && previous.color == this.getStyle().secondary) {
            return true;
         }
      }

      return false;
   }

   public class_2487 toNBT() {
      return this.toNBT(new class_2487());
   }

   public class_2487 toNBT(class_2487 tag) {
      class_2499 tagListOperators = new class_2499();
      NBTUtils.writeStringList(tagListOperators, this.operators);
      tag.method_10566("operators", tagListOperators);
      class_2499 tagListPrimaryKeywords = new class_2499();
      NBTUtils.writeStringList(tagListPrimaryKeywords, this.primaryKeywords);
      tag.method_10566("primaryKeywords", tagListPrimaryKeywords);
      class_2499 tagListSecondaryKeywords = new class_2499();
      NBTUtils.writeStringList(tagListSecondaryKeywords, this.secondaryKeywords);
      tag.method_10566("secondaryKeywords", tagListSecondaryKeywords);
      class_2499 tagListSpecial = new class_2499();
      NBTUtils.writeStringList(tagListSpecial, this.special);
      tag.method_10566("special", tagListSpecial);
      class_2499 tagListTypeKeywords = new class_2499();
      NBTUtils.writeStringList(tagListTypeKeywords, this.typeKeywords);
      tag.method_10566("typeKeywords", tagListTypeKeywords);
      tag.method_10582("functionName", this.functionName.toString());
      return tag;
   }
}
