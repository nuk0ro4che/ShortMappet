package mchorse.mappet.api.huds;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.vecmath.Vector3f;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mclib.utils.DummyEntity;
import mchorse.mclib.utils.NBTUtils;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.client.MorphRenderUtils;
import mchorse.metamorph.client.render.MorphRenderContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1041;
import net.minecraft.class_1299;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_310;
import net.minecraft.class_4597;
import net.minecraft.class_4587;
import org.joml.Quaternionf;

public class HUDMorph implements INBTSerializable<class_2487> {
   public Morph morph = new Morph();
   public boolean ortho;
   public float orthoX;
   public float orthoY;
   public int expire;
   public Vector3f translate = new Vector3f();
   public Vector3f scale = new Vector3f(1.0F, 1.0F, 1.0F);
   public Vector3f rotate = new Vector3f();
   @Environment(EnvType.CLIENT)
   private DummyEntity entity;
   private int tick;

   @Environment(EnvType.CLIENT)
   public DummyEntity getEntity() {
      if (this.entity == null) {
         this.entity = new DummyEntity(class_1299.field_6097, class_310.method_1551().field_1687);
         this.entity.method_36456(0.0F);
         this.entity.field_5982 = 0.0F;
         this.entity.method_36457(0.0F);
         this.entity.field_6004 = 0.0F;
         this.entity.method_5847(0.0F);
         this.entity.field_6259 = 0.0F;
         this.entity.field_6283 = 0.0F;
         this.entity.field_6220 = 0.0F;
         this.entity.method_24830(true);
      }

      return this.entity;
   }

   @Environment(EnvType.CLIENT)
   public void render(class_1041 resolution, float partialTicks, int light) {
      this.render(resolution, partialTicks, light, 1.0F, 1.0F, 1.0F);
   }

   
   public void render(class_1041 resolution, float partialTicks, int light, float red, float green, float blue) {
      if (!this.morph.isEmpty()) {
         RenderSystem.setShaderColor(red, green, blue, 1.0F);
         try {
         float tx = this.translate.x;
         float ty = this.translate.y;
         float tz = this.translate.z;
         float sx = this.scale.x;
         float sy = this.scale.y;
         float sz = this.scale.z;
         float rx = this.rotate.x;
         float ry = this.rotate.y;
         float rz = this.rotate.z;
         if (this.ortho) {
             


             tx += (float)resolution.method_4486() * this.orthoX;
             ty += (float)resolution.method_4502() * this.orthoY;
             String morphClass = this.morph.getClass().getSimpleName();
             if (morphClass.contains("Entity") || morphClass.contains("entity")) {
                float orthoScale = (float)resolution.method_4502() / 3.0F;
                sx *= orthoScale;
                sy *= orthoScale;
                sz *= orthoScale;
             }
          }

         class_4597.class_4598 consumers = class_310.method_1551().method_22940().method_23000();
         





         class_4587 matrices = new class_4587();
         float localZ = this.ortho ? tz - 2000.0F : tz;
         matrices.method_22904((double)tx, (double)ty, (double)localZ);
         matrices.method_22907((new Quaternionf()).rotationAxis((float)Math.toRadians((double)rz), 0.0F, 0.0F, 1.0F));
         matrices.method_22907((new Quaternionf()).rotationAxis((float)Math.toRadians((double)ry), 0.0F, 1.0F, 0.0F));
         matrices.method_22907((new Quaternionf()).rotationAxis((float)Math.toRadians((double)rx), 1.0F, 0.0F, 0.0F));
         matrices.method_22905(sx, sy, sz);
         MorphRenderContext.push(matrices, consumers, light, 0, partialTicks);

         try {
            MorphRenderUtils.render(this.morph.get(), this.getEntity(), 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
         } finally {
            MorphRenderContext.pop();
            consumers.method_22993();
         }
         } finally {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public boolean update(boolean allowExpiring) {
      DummyEntity entity = this.getEntity();
      if (!this.morph.isEmpty()) {
         this.morph.get().update(entity);
      }

      ++entity.field_6012;
      ++this.tick;
      if (!allowExpiring) {
         return false;
      } else {
         return this.expire > 0 && this.tick >= this.expire;
      }
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      class_2487 morph = this.morph.toNBT();
      if (morph != null) {
         tag.method_10566("Morph", morph);
      }

      tag.method_10556("Ortho", this.ortho);
      tag.method_10548("OrthoX", this.orthoX);
      tag.method_10548("OrthoY", this.orthoY);
      tag.method_10569("Expire", this.expire);
      if (this.translate.x != 0.0F || this.translate.y != 0.0F || this.translate.z != 0.0F) {
         tag.method_10566("Translate", NBTUtils.writeFloatList(new class_2499(), this.translate));
      }

      if (this.scale.x != 1.0F || this.scale.y != 1.0F || this.scale.z != 1.0F) {
         tag.method_10566("Scale", NBTUtils.writeFloatList(new class_2499(), this.scale));
      }

      if (this.rotate.x != 0.0F || this.rotate.y != 0.0F || this.rotate.z != 0.0F) {
         tag.method_10566("Rotate", NBTUtils.writeFloatList(new class_2499(), this.rotate));
      }

      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10545("Morph")) {
         this.morph.fromNBT(tag.method_10562("Morph"));
      }

      this.ortho = tag.method_10577("Ortho");
      this.orthoX = tag.method_10583("OrthoX");
      this.orthoY = tag.method_10583("OrthoY");
      this.expire = tag.method_10550("Expire");
      NBTUtils.readFloatList(tag.method_10554("Translate", 5), this.translate);
      NBTUtils.readFloatList(tag.method_10554("Scale", 5), this.scale);
      NBTUtils.readFloatList(tag.method_10554("Rotate", 5), this.rotate);
   }

   private void updateFrom(HUDMorph other) {
      this.morph = other.morph;
      this.ortho = other.ortho;
      this.orthoX = other.orthoX;
      this.orthoY = other.orthoY;
      this.expire = other.expire;
      this.translate.set(other.translate);
      this.scale.set(other.scale);
      this.rotate.set(other.rotate);
   }

   public HUDMorph copy() {
      HUDMorph copy = new HUDMorph();
      copy.updateFrom(this);
      return copy;
   }
}
