package mchorse.mappet.client.gui.scripts.utils;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.client.gui.utils.text.TextLine;
import net.minecraft.class_327;

public class HighlightedTextLine extends TextLine {
   public List<TextSegment> segments;
   public List<List<TextSegment>> wrappedSegments;

   public HighlightedTextLine(String text) {
      super(text);
   }

   public void resetSegments() {
      this.segments = null;
      this.wrappedSegments = null;
   }

   public void setSegments(List<TextSegment> segments) {
      this.segments = segments;
   }

   public void resetWrapping() {
      super.resetWrapping();
      this.wrappedSegments = null;
   }

   public void calculateWrappedLines(class_327 font, int w) {
      Object wrappedLines = this.wrappedLines;
      super.calculateWrappedLines(font, w);
      if (wrappedLines != this.wrappedLines) {
         this.resetSegments();
      }

   }

   public void calculateWrappedSegments(class_327 font) {
      if (this.wrappedLines == null) {
         this.wrappedSegments = null;
      } else {
         List<TextSegment> segments = new ArrayList();
         int w = 0;
         int i = 0;
         String line = (String)this.wrappedLines.get(i);
         this.wrappedSegments = new ArrayList();

         for(TextSegment segment : this.segments) {
            int sw = segment.text.length();

            for(int total = w + sw; total > line.length(); total = w + sw) {
               int endIndex = line.length() - w;
               TextSegment cutOff = new TextSegment(segment.text.substring(0, endIndex), segment.color, segment.width);
               TextSegment remainder = new TextSegment(segment.text.substring(endIndex), segment.color, segment.width);
               if (!cutOff.text.isEmpty()) {
                  cutOff.width = font.method_1727(cutOff.text);
                  segments.add(cutOff);
               }

               this.wrappedSegments.add(segments);
               segments = new ArrayList();
               segment = remainder;
               remainder.width = font.method_1727(remainder.text);
               sw = remainder.text.length();
               w = 0;
               ++i;
               if (i >= this.wrappedLines.size()) {
                  break;
               }

               line = (String)this.wrappedLines.get(i);
               if (remainder.text.isEmpty()) {
                  break;
               }
            }

            w += sw;
            segments.add(segment);
         }

         if (!segments.isEmpty()) {
            this.wrappedSegments.add(segments);
         }

      }
   }
}
