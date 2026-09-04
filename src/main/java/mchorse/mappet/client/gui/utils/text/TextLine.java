package mchorse.mappet.client.gui.utils.text;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_327;
import net.minecraft.class_3532;

public class TextLine {
   public String text;
   public List<String> wrappedLines;

   public TextLine(String text) {
      this.text = text;
   }

   public void set(String text) {
      this.text = text;
   }

   public int getLines() {
      return this.wrappedLines == null ? 1 : this.wrappedLines.size();
   }

   public void resetWrapping() {
      this.wrappedLines = null;
   }

   public void calculateWrappedLines(class_327 font, int w) {
      List<String> wrappedLines = this.splitIntoLines(font, w);
      if (wrappedLines.size() < 2) {
         this.wrappedLines = null;
      } else {
         this.wrappedLines = wrappedLines;
      }

   }

   private List<String> splitIntoLines(class_327 font, int w) {
      List<String> lines = new ArrayList();
      if (font.method_1727(this.text) < w) {
         lines.add(this.text);
         return lines;
      } else {
         int left = 0;
         int right = 0;
         int c = this.text.length();

         for(int increment = c > 5 ? 3 : 1; right < c; right += increment) {
            String string = this.text.substring(left, right);
            int sw = font.method_1727(string);
            if (sw > w) {
               int space = string.lastIndexOf(32, right);
               int diff = right - left - space;
               if (space != -1 && diff < 12) {
                  right -= diff - 1;
                  string = this.text.substring(left, right);
               }

               lines.add(string);
               left = right;
            }
         }

         if (left != right) {
            lines.add(this.text.substring(left, class_3532.method_15340(right, 0, c)));
         }

         return lines;
      }
   }
}
