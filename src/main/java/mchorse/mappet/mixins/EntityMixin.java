package mchorse.mappet.mixins;

import java.util.ArrayList;
import java.util.List;

import mchorse.mappet.CommonProxy;
import mchorse.mappet.compat.EntityDataHolder;
import net.minecraft.class_1297;
import net.minecraft.class_1313;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_259;
import net.minecraft.class_265;
import net.minecraft.class_2487;
import net.minecraft.class_2940;
import net.minecraft.class_2945;
import net.minecraft.class_4050;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1297.class})
public abstract class EntityMixin implements EntityDataHolder {
   @Shadow
   protected abstract class_238 method_20343(class_4050 pose);

   @Shadow
   public abstract void method_5857(class_238 box);

   @Shadow
   public abstract void method_18382();

   @Unique
   private final class_2487 mappet$persistentData = new class_2487();

   @Unique
   private boolean mappet$wasOnGroundBeforeMove;

   @Unique
   private boolean mappet$trackingFall;

   @Unique
   private double mappet$fallStartY;

   @Unique
   private double mappet$maxFallDistance;

   public class_2487 mappet$getPersistentData() {
      return this.mappet$persistentData;
   }

   


   @Inject(method = {"<init>"}, at = {@At("RETURN")})
   private void mappet$startSolidHitboxTracking(net.minecraft.class_1299<?> type, class_1937 world, CallbackInfo ci) {
      ((class_1297)(Object)this).method_5841().method_12784(EntityDataHolder.MAPPET_SOLID_HITBOX, false);
   }

   @Inject(
      method = {"method_18376"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void mappet$forcedLayPose(CallbackInfoReturnable<class_4050> cir) {
      class_1297 entity = (class_1297)(Object)this;
      if (entity instanceof class_1657 player && ((EntityDataHolder)player).mappet$isLay()) {
         cir.setReturnValue(class_4050.field_18079);
      }
   }

   @ModifyVariable(method = {"method_20736"}, at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private static List<class_265> mappet$copyCollisionList(List<class_265> collisions) {
      return new ArrayList<>(collisions);
   }

   @Inject(
      method = {"method_20736"},
      at = {@At("HEAD")}
   )
   private static void mappet$addSolidEntityCollisions(class_1297 entity, class_243 movement, class_238 box, class_1937 world, List<class_265> collisions, CallbackInfoReturnable<class_243> cir) {
      class_238 swept = box.method_18804(movement);
      for (class_1297 other : world.method_8335(entity, swept)) {
         if (((EntityDataHolder)(Object)other).mappet$isSolidHitbox()) {
            class_238 otherBox = other.method_5829();
            collisions.add(class_259.method_1078(otherBox));
         }
      }
   }

   @Inject(
      method = {"method_30948", "method_5810"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void mappet$solidHitbox(CallbackInfoReturnable<Boolean> cir) {
      if (((EntityDataHolder)(Object)this).mappet$isSolidHitbox()) {
         cir.setReturnValue(true);
      }
   }

   @Inject(method = {"method_30949"}, at = {@At("RETURN")}, cancellable = true)
   private void mappet$solidHitboxPair(class_1297 other, CallbackInfoReturnable<Boolean> cir) {
      class_1297 entity = (class_1297)(Object)this;
      if (((EntityDataHolder)(Object)entity).mappet$isSolidHitbox()
         || ((EntityDataHolder)(Object)other).mappet$isSolidHitbox()) {
         cir.setReturnValue(true);
      }
   }

   @Inject(
      method = {"method_5674"},
      at = {@At("TAIL")}
   )
   private void mappet$refreshLayDimensions(class_2940<?> data, CallbackInfo ci) {
      class_1297 entity = (class_1297)(Object)this;
      if (data == EntityDataHolder.MAPPET_LAY && entity instanceof class_1657 player) {
         EntityDataHolder holder = (EntityDataHolder)player;
         boolean lay = holder.mappet$isLay();
         class_4050 pose = lay ? class_4050.field_18079 : class_4050.field_18076;

         if (!lay) {
            player.method_18380(class_4050.field_18076);
         }

         this.method_18382();
         this.method_5857(this.method_20343(pose));
      }

   }


   @Inject(
      method = {"method_5784"},
      at = {@At("HEAD")}
   )
   private void mappet$rememberGroundState(class_1313 movementType, class_243 movement, CallbackInfo ci) {
      class_1297 entity = (class_1297)(Object)this;
      this.mappet$wasOnGroundBeforeMove = entity.method_24828();

      if (this.mappet$wasOnGroundBeforeMove) {
         if (movement.field_1351 > 0.0D) {
            this.mappet$trackingFall = true;
            this.mappet$fallStartY = entity.method_23318();
            this.mappet$maxFallDistance = 0.0D;
         } else if (!this.mappet$trackingFall) {
            this.mappet$maxFallDistance = 0.0D;
         }
      } else {
         if (!this.mappet$trackingFall) {
            this.mappet$trackingFall = true;
            this.mappet$fallStartY = entity.method_23318();
            this.mappet$maxFallDistance = 0.0D;
         }

         this.mappet$maxFallDistance = Math.max(this.mappet$maxFallDistance, entity.field_6017);
      }
   }

   @Inject(
      method = {"method_5784"},
      at = {@At("TAIL")}
   )
   private void mappet$checkLanding(class_1313 movementType, class_243 movement, CallbackInfo ci) {
      class_1297 entity = (class_1297)(Object)this;
      if (!this.mappet$wasOnGroundBeforeMove && entity.method_24828()) {
         double distance = Math.max(this.mappet$maxFallDistance, entity.field_6017);
         distance = Math.max(distance, this.mappet$fallStartY - entity.method_23318());

         if (CommonProxy.eventHandler != null) {
            CommonProxy.eventHandler.onEntityLanded(entity, distance);
         }

         this.mappet$trackingFall = false;
         this.mappet$maxFallDistance = 0.0D;
      }
   }

   @Inject(
      method = {"method_5647"},
      at = {@At("RETURN")}
   )
   private void mappet$writePersistentData(class_2487 nbt, CallbackInfoReturnable<class_2487> cir) {
      if (!this.mappet$persistentData.method_33133()) {
         nbt.method_10566("MappetEntityData", this.mappet$persistentData.method_10553());
      }
   }

   @Inject(
      method = {"method_5651"},
      at = {@At("TAIL")}
   )
   private void mappet$readPersistentData(class_2487 nbt, CallbackInfo ci) {
      if (nbt.method_10545("MappetEntityData")) {
         this.mappet$persistentData.method_10543(nbt.method_10562("MappetEntityData"));

         class_1297 entity = (class_1297)(Object)this;
         if (entity instanceof class_1657 player) {
            if (this.mappet$persistentData.method_10545("MappetLay")) {
               player.method_5841().method_12778(EntityDataHolder.MAPPET_LAY, this.mappet$persistentData.method_10577("MappetLay"));
            }

         }
      }
   }
}
