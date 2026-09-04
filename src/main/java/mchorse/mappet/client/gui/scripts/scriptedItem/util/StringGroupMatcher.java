package mchorse.mappet.client.gui.scripts.scriptedItem.util;

import mchorse.mclib.utils.MathUtils;

public class StringGroupMatcher {
   private StringGroup lastGroup;

   public Pair<Integer, Integer> findGroup(int direction, String line, int offset) {
      if (line.isEmpty()) {
         return null;
      } else {
         int first = direction < 0 && offset > 0 ? offset - 1 : offset;
         first = MathUtils.clamp(first, 0, line.length() - 1);
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

         return new Pair<Integer, Integer>(min, max);
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
}
