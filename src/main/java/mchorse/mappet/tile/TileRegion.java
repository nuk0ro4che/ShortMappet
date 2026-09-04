package mchorse.mappet.tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.regions.Region;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.utils.PositionCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_2586;
import net.minecraft.class_2596;
import net.minecraft.class_2602;
import net.minecraft.class_2622;
import net.minecraft.class_2680;
import net.minecraft.class_2743;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import org.apache.commons.lang3.mutable.MutableInt;

public class TileRegion extends class_2586 {
   public Region region = new Region();
   private Set<UUID> entities = new HashSet(10);
   private Map<UUID, MutableInt> delays = new HashMap();
   private int tick;

   public TileRegion(class_2338 pos, class_2680 state) {
      super(Mappet.regionTile, pos, state);
   }

   public void set(class_2487 tag) {
      this.region = new Region();
      this.region.deserializeNBT(tag);
      this.method_5431();
   }

   public void tick() {
      if (!this.field_11863.field_9236) {
         if (!this.delays.isEmpty()) {
            this.checkDelays();
         }

         int frequency = Math.max(this.region.update, 1);
         if (this.tick % frequency == 0) {
            this.checkRegion();
         }

         ++this.tick;
      }
   }

   private void checkDelays() {
      Map.Entry<UUID, MutableInt> trigger;
      int delay;
      for(Iterator<Map.Entry<UUID, MutableInt>> it = this.delays.entrySet().iterator(); it.hasNext(); ((MutableInt)trigger.getValue()).setValue(delay - 1)) {
         trigger = (Map.Entry)it.next();
         delay = ((MutableInt)trigger.getValue()).intValue();
         if (delay <= 0) {
            UUID id = (UUID)trigger.getKey();
            class_1657 player = this.field_11863.method_18470(id);
            if (player != null) {
               this.region.triggerEnter(player, this.method_11016());
            }

            it.remove();
         }
      }

   }

   private void checkRegion() {
      List<class_1297> list;
      label51: {
         list = new ArrayList();
         if (this.region.checkEntities) {
            class_1937 var3 = this.field_11863;
            if (var3 instanceof class_3218) {
               class_3218 serverWorld = (class_3218)var3;
               Iterable<class_1297> var10000 = serverWorld.method_27909();
               Objects.requireNonNull(list);
               var10000.forEach(list::add);
               break label51;
            }
         }

         list.addAll(this.field_11863.method_18456());
      }

      for(class_1297 entity : list) {
         boolean enabled = this.region.isEnabled(entity);
         if (!(entity instanceof class_1657) || !((class_1657)entity).method_7325()) {
            UUID id = entity.method_5667();
            boolean wasInside = this.entities.contains(id);
            if (this.region.isPlayerInside(entity, this.method_11016())) {
               if (!enabled) {
                  if (!this.region.passable && entity instanceof class_1657) {
                     this.handlePassing((class_1657)entity);
                  }
               } else {
                  this.region.triggerTick(entity, this.method_11016());
                  if (!wasInside) {
                     if (this.region.delay > 0) {
                        this.delays.put(id, new MutableInt(this.region.delay));
                     } else {
                        this.region.triggerEnter(entity, this.method_11016());
                     }

                     this.entities.add(id);
                  }
               }
            } else if (wasInside) {
               if (this.delays.containsKey(id)) {
                  this.delays.remove(id);
               } else {
                  this.region.triggerExit(entity, this.method_11016());
               }

               this.entities.remove(id);
            }
         }
      }

   }

   private void handlePassing(class_1657 player) {
      ICharacter character = Character.get(player);
      class_243 last = player.method_19538();
      if (character != null) {
         PositionCache cache = character.getPositionCache();
         class_243 vec = cache.lastPosition;
         if (vec != null && !this.region.isPlayerInside(vec.field_1352, vec.field_1351 + (double)(player.method_17682() / 2.0F), vec.field_1350, this.method_11016())) {
            this.teleportEntity(player, vec, last);
            cache.resetLastPositionTimer();
         } else {
            vec = cache.lastLastPosition;
            if (vec != null && !this.region.isPlayerInside(vec.field_1352, vec.field_1351 + (double)(player.method_17682() / 2.0F), vec.field_1350, this.method_11016())) {
               this.teleportEntity(player, vec, last);
               cache.resetLastPositionTimer();
            } else if (vec != null) {
               vec = vec.method_1023(player.method_23317(), player.method_23318(), player.method_23321());
               if (vec.method_1027() > (double)0.0F) {
                  vec = vec.method_1029();
                  vec = vec.method_1021((double)-0.5F);
                  double x = player.method_23317();
                  double y = player.method_23318();
                  double z = player.method_23321();

                  while(this.region.isPlayerInside(player, this.method_11016())) {
                     player.method_5814(player.method_23317() + vec.field_1352, player.method_23318() + vec.field_1351, player.method_23321() + vec.field_1350);
                  }

                  vec = new class_243(x, y, z);
                  player.method_5814(x, y, z);
                  this.teleportEntity(player, vec, last);
               }

               cache.resetLastPositionTimer();
            }
         }
      }
   }

   private void teleportEntity(class_1297 entity, class_243 vec, class_243 last) {
      entity.method_20620(vec.field_1352, vec.field_1351, vec.field_1350);
      class_243 motion = last.method_1020(entity.method_19538()).method_1021((double)-0.5F);
      if (motion.method_1022(class_243.field_1353) < (double)0.25F) {
         motion = motion.method_1029();
      }

      double y = Math.abs(motion.field_1351) < 0.01 ? 0.2 : motion.field_1351;
      entity.method_18800(motion.field_1352, y, motion.field_1350);
      ((class_3222)entity).field_13987.method_14364(new class_2743(entity));
   }

   @Environment(EnvType.CLIENT)
   public class_238 getRenderBoundingBox() {
      return (new class_238(this.field_11867)).method_1014((double)128.0F);
   }

   @Environment(EnvType.CLIENT)
   public double getMaxRenderDistanceSquared() {
      float range = 128.0F;
      return (double)(range * range);
   }

   public class_2487 getUpdateTag() {
      return this.method_38244();
   }

   public class_2596<class_2602> method_38235() {
      return class_2622.method_38585(this);
   }

   public class_2487 writeToNBT(class_2487 tag) {
      tag.method_10566("Region", this.region.serializeNBT());
      return tag;
   }

   public void readFromNBT(class_2487 tag) {
      if (tag.method_10545("Region")) {
         this.region.deserializeNBT(tag.method_10562("Region"));
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
