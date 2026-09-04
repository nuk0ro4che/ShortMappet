package mchorse.mappet.utils;

import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.class_1923;
import net.minecraft.class_2586;
import net.minecraft.class_310;
import net.minecraft.class_761;

public class ReflectionUtils {
   public static Set<class_2586> getGlobalTiles(class_761 ignored) {
      Set<class_2586> result = new LinkedHashSet();
      class_310 client = class_310.method_1551();
      if (client.field_1687 != null && client.field_1724 != null) {
         class_1923 center = client.field_1724.method_31476();
         int radius = (Integer)client.field_1690.method_42503().method_41753() + 1;

         for(int x = center.field_9181 - radius; x <= center.field_9181 + radius; ++x) {
            for(int z = center.field_9180 - radius; z <= center.field_9180 + radius; ++z) {
               if (client.field_1687.method_2935().method_12123(x, z)) {
                  result.addAll(client.field_1687.method_8497(x, z).method_12214().values());
               }
            }
         }

         return result;
      } else {
         return result;
      }
   }
}
