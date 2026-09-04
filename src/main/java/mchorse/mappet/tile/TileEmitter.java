package mchorse.mappet.tile;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.conditions.Checker;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.blocks.BlockEmitter;
import mchorse.mappet.network.common.blocks.PacketEditEmitter;
import net.minecraft.class_1657;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2520;
import net.minecraft.class_2586;
import net.minecraft.class_2680;

public class TileEmitter extends class_2586 {
   private Checker checker = new Checker();
   private float radius;
   private int update = 5;
   private boolean disable;
   private int tick = 0;

   public TileEmitter(class_2338 pos, class_2680 state) {
      super(Mappet.emitterTile, pos, state);
   }

   public Checker getChecker() {
      return this.checker;
   }

   public float getRadius() {
      return this.radius;
   }

   public int getUpdate() {
      return this.update;
   }

   public boolean getDisable() {
      return this.disable;
   }

   public void setExpression(PacketEditEmitter message) {
      this.checker.deserializeNBT((class_2520)message.checker);
      this.radius = message.radius;
      this.update = Math.max(message.update, 1);
      this.disable = message.disable;
      this.updateExpression();
      this.method_5431();
   }

   public void tick() {
      if (!this.field_11863.field_9236) {
         if (this.tick % this.update == 0 && !this.checker.isEmpty()) {
            this.updateExpression();
         }

         ++this.tick;
      }
   }

   private void updateExpression() {
      List<class_1657> playersInside = new ArrayList();
      if (this.radius > 0.0F) {
         class_2338 pos = this.method_11016();
         boolean playerIn = false;

         for(class_1657 player : this.field_11863.method_18456()) {
            if (Math.sqrt(player.method_5649((double)pos.method_10263() + (double)0.5F, (double)pos.method_10264(), (double)pos.method_10260() + (double)0.5F)) <= (double)this.radius) {
               playerIn = true;
               playersInside.add(player);
               break;
            }
         }

         if (!playerIn) {
            if (this.disable) {
               this.updateState(false);
            }

            return;
         }
      }

      boolean result = this.checker.check(new DataContext(this.field_11863, this.method_11016()));
      if (!result) {
         for(class_1657 player : playersInside) {
            result = this.checker.check(new DataContext(player));
            if (result) {
               break;
            }
         }
      }

      this.updateState(result);
   }

   private void updateState(boolean result) {
      class_2680 state = this.field_11863.method_8320(this.field_11867);
      if ((Boolean)state.method_11654(BlockEmitter.POWERED) != result) {
         this.field_11863.method_8501(this.field_11867, (class_2680)state.method_11657(BlockEmitter.POWERED, result));
      }

   }

   public class_2487 writeToNBT(class_2487 tag) {
      tag.method_10566("Checker", this.checker.serializeNBT());
      if (this.radius > 0.0F) {
         tag.method_10548("Radius", this.radius);
      }

      if (this.update > 0) {
         tag.method_10569("Update", this.update);
      }

      if (this.disable) {
         tag.method_10556("Disable", this.disable);
      }

      return tag;
   }

   public void readFromNBT(class_2487 tag) {
      if (tag.method_10545("Checker")) {
         this.checker.deserializeNBT(tag.method_10580("Checker"));
      }

      if (tag.method_10545("Radius")) {
         this.radius = tag.method_10583("Radius");
      }

      if (tag.method_10545("Update")) {
         this.update = tag.method_10550("Update");
      }

      if (tag.method_10545("Disable")) {
         this.disable = tag.method_10577("Disable");
      }

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
