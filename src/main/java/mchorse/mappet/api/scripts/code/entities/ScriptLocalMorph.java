package mchorse.mappet.api.scripts.code.entities;

import mchorse.mappet.api.scripts.user.entities.ILocalMorph;
import mchorse.mappet.client.RenderingHandler;
import mchorse.mappet.client.morphs.WorldMorph;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketWorldMorph;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.class_1657;
import net.minecraft.class_3222;

public class ScriptLocalMorph implements ILocalMorph {
   private final class_1657 player;
   private final WorldMorph worldMorph;

   public ScriptLocalMorph(class_1657 player, String id, AbstractMorph morph, double x, double y, double z) {
      this.player = player;
      this.worldMorph = new WorldMorph();
      this.worldMorph.id = id == null ? "" : id;
      this.worldMorph.morph = morph;
      this.worldMorph.entity = player;
      this.worldMorph.expiration = Integer.MAX_VALUE;
      this.worldMorph.scale = true;
      this.worldMorph.x = x;
      this.worldMorph.y = y;
      this.worldMorph.z = z;
      this.send();
   }

   public ILocalMorph seeThrough(boolean value) {
      this.worldMorph.seeThrough = value;
      this.send();
      return this;
   }

   public ILocalMorph scale(boolean value) {
      this.worldMorph.scale = value;
      this.send();
      return this;
   }

   public boolean isFar(double distance) {
      return this.worldMorph.distanceTo(this.player) >= Math.max(0.0D, distance);
   }

   public boolean isNear(double distance) {
      return this.worldMorph.distanceTo(this.player) < Math.max(0.0D, distance);
   }

   public void remove() {
      this.worldMorph.remove = true;
      this.send();
   }

   private void send() {
      if (this.player instanceof class_3222) {
         Dispatcher.sendTo(new PacketWorldMorph(this.worldMorph), (class_3222)this.player);
      } else if (this.worldMorph.remove) {
         RenderingHandler.removeWorldMorph(this.worldMorph.id);
      } else {
         RenderingHandler.addOrReplaceWorldMorph(this.worldMorph);
      }
   }
}
