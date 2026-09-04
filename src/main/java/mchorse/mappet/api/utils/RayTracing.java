package mchorse.mappet.api.utils;

import java.util.Optional;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3959;
import net.minecraft.class_3966;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class RayTracing {
   public static class_239 rayTraceWithEntity(class_1937 world, double x1, double y1, double z1, double x2, double y2, double z2) {
      class_243 origin = new class_243(x1, y1, z1);
      class_243 destination = new class_243(x2, y2, z2);
      class_239 block = rayTrace(world, x1, y1, z1, x2, y2, z2);
      double closest = block.method_17783() == class_240.field_1333 ? origin.method_1025(destination) : origin.method_1025(block.method_17784());
      class_1297 target = null;
      class_243 hit = null;

      for(class_1297 entity : world.method_8333((class_1297)null, (new class_238(origin, destination)).method_1014((double)1.0F), class_1297::method_5863)) {
         Optional<class_243> point = entity.method_5829().method_1014((double)entity.method_5871()).method_992(origin, destination);
         if (point.isPresent()) {
            double distance = origin.method_1025((class_243)point.get());
            if (distance < closest) {
               closest = distance;
               target = entity;
               hit = (class_243)point.get();
            }
         }
      }

      return (class_239)(target == null ? block : new class_3966(target, hit));
   }

   public static class_239 rayTrace(class_1937 world, double x1, double y1, double z1, double x2, double y2, double z2) {
      class_243 start = new class_243(x1, y1, z1);
      class_243 end = new class_243(x2, y2, z2);
      return world.method_17742(new class_3959(start, end, class_3960.field_17559, class_242.field_1348, (class_1297)null));
   }
}
