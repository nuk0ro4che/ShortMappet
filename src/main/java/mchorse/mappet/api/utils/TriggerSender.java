package mchorse.mappet.api.utils;

import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2165;
import net.minecraft.class_2168;
import net.minecraft.class_2338;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_3218;
import net.minecraft.server.MinecraftServer;

public class TriggerSender {
   private MinecraftServer server;
   private class_1937 world;
   private class_2338 pos;
   private class_1297 entity;

   public TriggerSender set(MinecraftServer server, class_1937 world, class_2338 pos) {
      this.server = server;
      this.world = world;
      this.pos = pos;
      this.entity = null;
      return this;
   }

   public TriggerSender set(class_1297 entity) {
      this.entity = entity;
      this.server = entity.method_5682();
      this.world = entity.method_37908();
      this.pos = entity.method_24515();
      return this;
   }

   public class_2168 getSource() {
      class_3218 commandWorld = this.world instanceof class_3218 ? (class_3218)this.world : this.server.method_30002();
      class_243 position = this.entity == null ? class_243.method_24955(this.pos == null ? class_2338.field_10980 : this.pos) : this.entity.method_19538();
      return new class_2168(class_2165.field_17395, position, class_241.field_1340, commandWorld, 4, "Mappet", class_2561.method_43470("Mappet"), this.server, this.entity);
   }
}
