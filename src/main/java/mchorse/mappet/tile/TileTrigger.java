package mchorse.mappet.tile;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.blocks.BlockTrigger;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.utils.PacketChangedBoundingBox;
import mchorse.mappet.utils.NBTUtils;
import net.minecraft.class_1657;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_2586;
import net.minecraft.class_2596;
import net.minecraft.class_2602;
import net.minecraft.class_2622;
import net.minecraft.class_2680;
import net.minecraft.class_3222;

public class TileTrigger extends class_2586 {
   public Trigger leftClick = new Trigger();
   public Trigger rightClick = new Trigger();
   public class_243 boundingBoxPos1 = new class_243((double)0.0F, (double)0.0F, (double)0.0F);
   public class_243 boundingBoxPos2 = new class_243((double)1.0F, (double)1.0F, (double)1.0F);

   public TileTrigger(class_2338 pos, class_2680 state) {
      super(Mappet.triggerTile, pos, state);
   }

   public void set(class_2487 left, class_2487 right, boolean collidable, class_243 boundingBoxPos1, class_243 boundingBoxPos2) {
      this.leftClick = new Trigger();
      this.leftClick.deserializeNBT(left);
      this.rightClick = new Trigger();
      this.rightClick.deserializeNBT(right);
      this.field_11863.method_8501(this.field_11867, (class_2680)this.field_11863.method_8320(this.field_11867).method_11657(BlockTrigger.COLLIDABLE, collidable));
      this.boundingBoxPos1 = boundingBoxPos1;
      this.boundingBoxPos2 = boundingBoxPos2;
      this.sendChangesToAll();
      this.method_5431();
   }

   public void sendChangesToAll() {
      if (!this.field_11863.field_9236) {
         PacketChangedBoundingBox message = new PacketChangedBoundingBox(this.field_11867, this.boundingBoxPos1, this.boundingBoxPos2);

         for(class_1657 player : this.field_11863.method_18456()) {
            Dispatcher.sendTo(message, (class_3222)player);
         }

      }
   }

   public class_2487 writeToNBT(class_2487 tag) {
      tag.method_10566("Left", this.leftClick.serializeNBT());
      tag.method_10566("Right", this.rightClick.serializeNBT());
      tag.method_10566("BoundingBoxPos1", NBTUtils.vec3dTo(this.boundingBoxPos1));
      tag.method_10566("BoundingBoxPos2", NBTUtils.vec3dTo(this.boundingBoxPos2));
      return tag;
   }

   public void readFromNBT(class_2487 tag) {
      if (tag.method_10545("Left")) {
         this.leftClick.deserializeNBT(tag.method_10562("Left"));
      }

      if (tag.method_10545("Right")) {
         this.rightClick.deserializeNBT(tag.method_10562("Right"));
      }

      if (tag.method_10545("BoundingBoxPos1")) {
         this.boundingBoxPos1 = NBTUtils.vec3dFrom(tag.method_10580("BoundingBoxPos1"));
      }

      if (tag.method_10545("BoundingBoxPos2")) {
         this.boundingBoxPos2 = NBTUtils.vec3dFrom(tag.method_10580("BoundingBoxPos2"));
      }

   }

   public class_2487 getUpdateTag() {
      return this.method_38244();
   }

   public class_2596<class_2602> method_38235() {
      return class_2622.method_38585(this);
   }

   protected void method_11007(class_2487 tag) {
      super.method_11007(tag);
      this.writeToNBT(tag);
   }

   public void method_11014(class_2487 tag) {
      super.method_11014(tag);
      this.readFromNBT(tag);
   }
}
