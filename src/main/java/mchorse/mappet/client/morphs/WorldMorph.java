package mchorse.mappet.client.morphs;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.DummyEntity;
import mchorse.mclib.utils.Interpolations;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.render.MorphRenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1944;
import net.minecraft.class_2540;
import net.minecraft.class_310;
import net.minecraft.class_4050;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_765;

public class WorldMorph implements IMessage {
   public AbstractMorph morph;
   public double x;
   public double y;
   public double z;
   public float yaw;
   public float pitch;
   public class_1297 entity;
   public int expiration;
   public boolean rotate;
   public String id = "";
   public boolean seeThrough;
   public boolean scale;
   public boolean remove;
   private int entityId = -1;
   private DummyEntity dummy;
   private double lastAttachedY;
   private boolean hasAttachedY;

   @Environment(EnvType.CLIENT)
   public void render(class_4587 matrices, class_4597 consumers, float tickDelta) {
      class_1297 camera = class_310.method_1551().method_1560();
      class_1309 dummy = this.getDummy();
      if (camera != null && dummy != null && this.morph != null) {
         dummy.field_6283 = 0.0F;
         double rx = Interpolations.lerp(dummy.field_6014, dummy.method_23317(), (double)tickDelta) - Interpolations.lerp(camera.field_6014, camera.method_23317(), (double)tickDelta);
         double ry = Interpolations.lerp(dummy.field_6036, dummy.method_23318(), (double)tickDelta) - Interpolations.lerp(camera.field_6036, camera.method_23318(), (double)tickDelta);
         double rz = Interpolations.lerp(dummy.field_5969, dummy.method_23321(), (double)tickDelta) - Interpolations.lerp(camera.field_5969, camera.method_23321(), (double)tickDelta);
         int block = dummy.method_37908().method_8314(class_1944.field_9282, dummy.method_24515());
         int sky = dummy.method_37908().method_8314(class_1944.field_9284, dummy.method_24515());
                     matrices.method_22903();
            matrices.method_22904(rx, ry, rz);
                        double distance = Math.sqrt(rx * rx + ry * ry + rz * rz);
            if (this.scale) {
               float scale = (float)Math.max(1.0D, distance / 12.0D);
               matrices.method_22905(scale, scale, scale);
            }
            this.morph.getSettings().shadowOption = 0;
            this.morph.getSettings().hasShadowOption = true;
            MorphRenderPipeline.drawEntity(this.morph, dummy, matrices, consumers, 15728880, tickDelta);
            matrices.method_22909();

      }
   }

   public double distanceTo(class_1297 target) {
      if (target == null) {
         return Double.MAX_VALUE;
      }
      double px = this.entity == null ? this.x : this.entity.method_23317() + this.x;
      double py = this.entity == null ? this.y : this.entity.method_23318() + this.y;
      double pz = this.entity == null ? this.z : this.entity.method_23321() + this.z;
      double dx = target.method_23317() - px;
      double dy = target.method_23318() - py;
      double dz = target.method_23321() - pz;
      return Math.sqrt(dx * dx + dy * dy + dz * dz);
   }

   @Environment(EnvType.CLIENT)
   public boolean update() {
      class_1309 dummy = this.getDummy();
      if (dummy != null && this.morph != null) {
         this.morph.update(dummy);
         ++dummy.field_6012;
         return dummy.field_6012 > this.expiration;
      } else {
         return true;
      }
   }

   @Environment(EnvType.CLIENT)
   private class_1309 getDummy() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1687 == null) {
         return null;
      } else {
         if (this.dummy == null) {
            this.dummy = new DummyEntity(class_1299.field_6097, mc.field_1687);
         }

         this.dummy.method_5728(false);
         this.dummy.method_5796(false);
         this.dummy.method_18380(class_4050.field_18076);
         class_1297 attached = this.getEntity();
         double px = attached == null ? this.x : attached.method_23317() + this.x;
         double py = attached == null ? this.y : attached.method_23318() + this.y;
         if (attached != null) {
            if (attached.method_5715() && this.hasAttachedY) {
               py = this.lastAttachedY + this.y;
            } else if (!attached.method_5715()) {
               this.lastAttachedY = attached.method_23318();
               this.hasAttachedY = true;
            }
         }
         double pz = attached == null ? this.z : attached.method_23321() + this.z;
         this.dummy.method_5814(px, py, pz);
         this.dummy.field_6014 = px;
         this.dummy.field_6036 = py;
         this.dummy.field_5969 = pz;
         float currentYaw = this.yaw + (attached != null && this.rotate ? attached.method_36454() : 0.0F);
         float currentPitch = this.pitch + (attached != null && this.rotate ? attached.method_36455() : 0.0F);
         this.dummy.method_36456(currentYaw);
         this.dummy.method_36457(currentPitch);
         this.dummy.field_5982 = this.yaw + (attached != null && this.rotate ? attached.field_5982 : 0.0F);
         this.dummy.field_6004 = this.pitch + (attached != null && this.rotate ? attached.field_6004 : 0.0F);
         if (attached instanceof class_1309) {
            class_1309 living = (class_1309)attached;
            if (this.rotate) {
               this.dummy.method_5847(living.method_5791() + this.yaw);
               this.dummy.field_6259 = living.field_6259 + this.yaw;
               this.dummy.field_6283 = living.field_6283 + this.yaw;
               this.dummy.field_6220 = living.field_6220 + this.yaw;
               return this.dummy;
            }
         }

         this.dummy.method_5847(currentYaw);
         this.dummy.field_6259 = this.dummy.field_5982;
         this.dummy.field_6283 = currentYaw;
         this.dummy.field_6220 = this.dummy.field_5982;
         return this.dummy;
      }
   }

   @Environment(EnvType.CLIENT)
   private class_1297 getEntity() {
      if (this.entityId >= 0 && this.entity == null && class_310.method_1551().field_1687 != null) {
         this.entity = class_310.method_1551().field_1687.method_8469(this.entityId);
      }

      return this.entity;
   }

   public void fromBytes(ByteBuf buf) {
      this.morph = MorphUtils.morphFromBuf(new class_2540(buf));
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      this.yaw = buf.readFloat();
      this.pitch = buf.readFloat();
      this.expiration = buf.readInt();
      this.rotate = buf.readBoolean();
      this.entityId = buf.readInt();
      int idLength = buf.readInt();
      byte[] idBytes = new byte[idLength];
      buf.readBytes(idBytes);
      this.id = new String(idBytes, StandardCharsets.UTF_8);
      this.seeThrough = buf.readBoolean();
      this.scale = buf.readBoolean();
      this.remove = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      MorphUtils.morphToBuf(new class_2540(buf), this.morph);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeFloat(this.yaw);
      buf.writeFloat(this.pitch);
      buf.writeInt(this.expiration);
      buf.writeBoolean(this.rotate);
      buf.writeInt(this.entity == null ? -1 : this.entity.method_5628());
      byte[] idBytes = (this.id == null ? "" : this.id).getBytes(StandardCharsets.UTF_8);
      buf.writeInt(idBytes.length);
      buf.writeBytes(idBytes);
      buf.writeBoolean(this.seeThrough);
      buf.writeBoolean(this.scale);
      buf.writeBoolean(this.remove);
   }
}
