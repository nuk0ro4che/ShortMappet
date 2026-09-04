package mchorse.mappet.api.scripts.code.render;

import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.mappet.api.scripts.user.render.IScriptHand;
import mchorse.mappet.hand.HandState;
import mchorse.mappet.hand.Hands;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketHandState;
import mchorse.mappet.network.common.scripts.PacketHandMorphAnimation;
import net.minecraft.class_1657;
import net.minecraft.class_3222;

public class ScriptHand implements IScriptHand {
   private final HandState.Side hand;
   private final class_1657 player;
   private final boolean main;

   public ScriptHand(class_1657 player, int side) {
      this.player = player;
      this.main = side <= 0;
      this.hand = Hands.get(player).get(side);
   }

   public void resetAll() {
      this.hand.reset(this.main);
      this.sync();
   }

   public void setRotations(double x, double y, double z) {
      this.hand.pitch = x;
      this.hand.yaw = y;
      this.hand.roll = z;
      this.sync();
   }

   public void setRotations(ScriptVector rotations) {
      this.setRotations(rotations.x, rotations.y, rotations.z);
   }

   public ScriptVector getRotations() {
      return new ScriptVector(this.hand.pitch, this.hand.yaw, this.hand.roll);
   }

   public void setPosition(double x, double y, double z) {
      this.hand.x = x;
      this.hand.y = y;
      this.hand.z = z;
      this.sync();
   }

   public void setPosition(ScriptVector position) {
      this.setPosition(position.x, position.y, position.z);
   }

   public ScriptVector getPosition() {
      return new ScriptVector(this.hand.x, this.hand.y, this.hand.z);
   }

   public void moveTo(String interpolation, int ticks, double x, double y, double z) {
      this.hand.moveTo(interpolation, ticks, x, y, z, System.currentTimeMillis());
      this.sync();
   }

   public void moveTo(String interpolation, int ticks, ScriptVector position) {
      this.moveTo(interpolation, ticks, position.x, position.y, position.z);
   }

   public void rotateTo(String interpolation, int ticks, double pitch, double yaw, double roll) {
      this.hand.rotateTo(interpolation, ticks, pitch, yaw, roll, System.currentTimeMillis());
      this.sync();
   }

   public void rotateTo(String interpolation, int ticks, ScriptVector rotations) {
      this.rotateTo(interpolation, ticks, rotations.x, rotations.y, rotations.z);
   }

   public boolean isRender() {
      return this.hand.renderArm && this.hand.renderItem;
   }

   public boolean isRender(boolean item) {
      return item ? this.hand.renderItem : this.hand.renderArm;
   }

   public void setRender(boolean render) {
      this.hand.renderArm = render;
      this.hand.renderItem = render;
      this.sync();
   }

   public void setRender(boolean render, boolean item) {
      if (item) {
         this.hand.renderItem = render;
      } else {
         this.hand.renderArm = render;
      }
      this.sync();
   }

   public void setMorph(AbstractMorph morph) {
      this.hand.morph = morph;
      this.hand.renderArm = morph == null;
      this.hand.renderItem = morph == null;
      this.sync();
   }

   public void playAnimation(String animation) {
      if (animation == null || animation.trim().isEmpty() || this.hand.morph == null) {
         return;
      }
      if (this.player instanceof class_3222) {
         Dispatcher.sendTo(new PacketHandMorphAnimation(animation.trim(), this.main ? 0 : 1), (class_3222)this.player);
      }
   }

   private void sync() {
      if (this.player instanceof class_3222) {
         Dispatcher.sendTo(new PacketHandState(this.player.method_5667(), Hands.get(this.player)), (class_3222)this.player);
      }
   }
}
