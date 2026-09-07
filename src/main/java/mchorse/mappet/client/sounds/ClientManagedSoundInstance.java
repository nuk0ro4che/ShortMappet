package mchorse.mappet.client.sounds;

import java.util.Locale;
import mchorse.mappet.network.common.scripts.PacketManagedSound;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1101;
import net.minecraft.class_1113;
import net.minecraft.class_1113.class_1114;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import net.minecraft.class_5819;


@Environment(EnvType.CLIENT)
public class ClientManagedSoundInstance extends class_1101 {
   public final String id;
   public final String name;
   public final boolean entityBound;
   public final int entityId;
   public final class_3419 category;
   private boolean finished;
   private double fallbackTimeCode;
   private long fallbackTimeAnchor;
   private Double pendingTimeCode;
   private int age;

   public ClientManagedSoundInstance(PacketManagedSound packet) {
      super(class_3414.method_47908(new class_2960(packet.name)), resolveCategory(packet.category), class_5819.method_43047());
      this.id = packet.id;
      this.name = packet.name;
      this.entityBound = packet.entityBound;
      this.entityId = packet.entityId;
      this.category = resolveCategory(packet.category);
      this.field_5442 = Math.max(0.0F, packet.volume);
      this.field_5441 = Math.max(0.01F, packet.pitch);
      this.field_5439 = packet.x;
      this.field_5450 = packet.y;
      this.field_5449 = packet.z;
      this.field_5446 = false;
      this.field_5451 = 0;
      this.field_5440 = packet.staticSound ? class_1114.field_5478 : class_1114.field_5476;
      this.field_18936 = packet.staticSound;
      this.setFallbackTimeCode(0.0D);
   }

   private static class_3419 resolveCategory(String category) {
      try {
         return class_3419.valueOf((category == null ? "master" : category).toUpperCase(Locale.ROOT));
      } catch (Exception ignored) {
         return class_3419.field_15250;
      }
   }

   public void update(double x, double y, double z, float volume) {
      this.field_5439 = x;
      this.field_5450 = y;
      this.field_5449 = z;
      this.field_5442 = Math.max(0.0F, volume);
   }

   public float getLiveVolume() {
      return this.field_5442;
   }

   public double getLiveX() {
      return this.field_5439;
   }

   public double getLiveY() {
      return this.field_5450;
   }

   public double getLiveZ() {
      return this.field_5449;
   }

   public void setTimeCode(double seconds) {
      this.pendingTimeCode = Math.max(0.0D, seconds);
      this.setFallbackTimeCode(this.pendingTimeCode);
   }

   public Double consumePendingTimeCode() {
      Double value = this.pendingTimeCode;
      this.pendingTimeCode = null;
      return value;
   }

   public double getFallbackTimeCode() {
      return this.fallbackTimeCode + (double)(System.nanoTime() - this.fallbackTimeAnchor) / 1.0E9D * (double)this.field_5441;
   }

   public void setFallbackTimeCode(double seconds) {
      this.fallbackTimeCode = Math.max(0.0D, seconds);
      this.fallbackTimeAnchor = System.nanoTime();
   }

   public int getAge() {
      return this.age;
   }

   public void finish() {
      if (!this.finished) {
         this.finished = true;
         this.method_24876();
      }
   }

   public boolean method_4793() {
      return this.finished;
   }

   public void method_16896() {
      ++this.age;
   }
}
