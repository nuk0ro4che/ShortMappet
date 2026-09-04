package mchorse.mappet.compat.events.legacy;

import mchorse.mappet.compat.events.Event;
import net.minecraft.class_1263;
import net.minecraft.class_1268;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_2680;
import net.minecraft.class_2960;

public final class LegacyEvents {
   private LegacyEvents() {
   }

   public static enum Side {
      CLIENT,
      SERVER;
      private static Side[] $values() {
         return new Side[]{CLIENT, SERVER};
      }
   }

   public static class TickEvent extends Event {
      public final Side side;
      public final Phase phase;

      protected TickEvent(Side side, Phase phase) {
         this.side = side;
         this.phase = phase;
      }

      public static enum Phase {
         START,
         END;
         private static Phase[] $values() {
            return new Phase[]{START, END};
         }
      }

      public static class PlayerTickEvent extends TickEvent {
         public final class_1657 player;

         public PlayerTickEvent(class_1657 p, Side s, Phase ph) {
            super(s, ph);
            this.player = p;
         }
      }

      public static class ServerTickEvent extends TickEvent {
         public ServerTickEvent(Phase ph) {
            super(LegacyEvents.Side.SERVER, ph);
         }
      }

      public static class RenderTickEvent extends TickEvent {
         public RenderTickEvent(Phase ph) {
            super(LegacyEvents.Side.CLIENT, ph);
         }
      }

      public static class WorldTickEvent extends TickEvent {
         public final class_1937 world;

         public WorldTickEvent(class_1937 w, Phase ph) {
            super(w.field_9236 ? LegacyEvents.Side.CLIENT : LegacyEvents.Side.SERVER, ph);
            this.world = w;
         }
      }
   }

   public static class PlayerEvent extends Event {
      public final class_1657 player;

      protected PlayerEvent(class_1657 player) {
         this.player = player;
      }

      public class_1657 getPlayer() {
         return this.player;
      }

      public class_1657 getPlayerEntity() {
         return this.player;
      }

      public static class PlayerLoggedInEvent extends PlayerEvent {
         public PlayerLoggedInEvent(class_1657 p) {
            super(p);
         }
      }

      public static class PlayerLoggedOutEvent extends PlayerEvent {
         public PlayerLoggedOutEvent(class_1657 p) {
            super(p);
         }
      }

      public static class PlayerRespawnEvent extends PlayerEvent {
         public PlayerRespawnEvent(class_1657 p) {
            super(p);
         }
      }

      public static class Clone extends PlayerEvent {
         private final class_1657 original;

         public Clone(class_1657 p, class_1657 o) {
            super(p);
            this.original = o;
         }

         public class_1657 getOriginal() {
            return this.original;
         }
      }
   }

   public static class PlayerInteractEvent extends Event {
      protected final class_1657 player;
      protected final class_1937 world;
      protected final class_1268 hand;
      protected final class_2338 pos;

      protected PlayerInteractEvent(class_1657 p, class_1937 w, class_1268 h, class_2338 pos) {
         this.player = p;
         this.world = w;
         this.hand = h;
         this.pos = pos;
      }

      public class_1657 getPlayerEntity() {
         return this.player;
      }

      public class_1657 getPlayer() {
         return this.player;
      }

      public class_1937 getWorld() {
         return this.world;
      }

      public class_1268 getHand() {
         return this.hand;
      }

      public class_2338 getPos() {
         return this.pos;
      }

      public class_1799 getItem() {
         return this.player.method_5998(this.hand);
      }

      public static class LeftClickEmpty extends PlayerInteractEvent {
         public LeftClickEmpty(class_1657 p, class_1268 h) {
            super(p, p.method_37908(), h, p.method_24515());
         }
      }

      public static class RightClickEmpty extends PlayerInteractEvent {
         public RightClickEmpty(class_1657 p, class_1268 h) {
            super(p, p.method_37908(), h, p.method_24515());
         }
      }

      public static class RightClickItem extends PlayerInteractEvent {
         public RightClickItem(class_1657 p, class_1268 h) {
            super(p, p.method_37908(), h, p.method_24515());
         }
      }

      public static class LeftClickBlock extends PlayerInteractEvent {
         public LeftClickBlock(class_1657 p, class_1268 h, class_2338 pos) {
            super(p, p.method_37908(), h, pos);
         }
      }

      public static class RightClickBlock extends PlayerInteractEvent {
         public RightClickBlock(class_1657 p, class_1268 h, class_2338 pos) {
            super(p, p.method_37908(), h, pos);
         }
      }

      public static class EntityInteract extends PlayerInteractEvent {
         private final class_1297 target;

         public EntityInteract(class_1657 p, class_1268 h, class_1297 target) {
            super(p, p.method_37908(), h, target.method_24515());
            this.target = target;
         }

         public class_1297 getTarget() {
            return this.target;
         }
      }
   }

   public static class BlockEvent extends Event {
      protected final class_1937 world;
      protected final class_2338 pos;
      protected final class_2680 state;
      protected final class_1657 player;

      protected BlockEvent(class_1937 w, class_2338 p, class_2680 s, class_1657 player) {
         this.world = w;
         this.pos = p;
         this.state = s;
         this.player = player;
      }

      public class_1937 getWorld() {
         return this.world;
      }

      public class_2338 getPos() {
         return this.pos;
      }

      public class_2680 getState() {
         return this.state;
      }

      public class_1657 getPlayer() {
         return this.player;
      }

      public static class BreakEvent extends BlockEvent {
         public BreakEvent(class_1937 w, class_2338 p, class_2680 s, class_1657 player) {
            super(w, p, s, player);
         }
      }

      public static class PlaceEvent extends BlockEvent {
         public PlaceEvent(class_1937 w, class_2338 p, class_2680 s, class_1657 player) {
            super(w, p, s, player);
         }

         public class_2680 getPlacedBlock() {
            return this.state;
         }
      }
   }

   public static class LivingAttackEvent extends Event {
      private final class_1309 entity;
      private final class_1282 source;
      private final float amount;

      public LivingAttackEvent(class_1309 e, class_1282 s, float a) {
         this.entity = e;
         this.source = s;
         this.amount = a;
      }

      public class_1309 getEntity() {
         return this.entity;
      }

      public class_1309 getMobEntity() {
         return this.entity;
      }

      public class_1282 getSource() {
         return this.source;
      }

      public float getAmount() {
         return this.amount;
      }
   }

   public static class LivingDamageEvent extends LivingAttackEvent {
      public LivingDamageEvent(class_1309 e, class_1282 s, float a) {
         super(e, s, a);
      }
   }

   public static class LivingDeathEvent extends LivingAttackEvent {
      public LivingDeathEvent(class_1309 e, class_1282 s) {
         super(e, s, 0.0F);
      }
   }

   public static class LivingEquipmentChangeEvent extends Event {
      private final class_1309 entity;
      private final class_1304 slot;
      private final class_1799 from;
      private final class_1799 to;

      public LivingEquipmentChangeEvent(class_1309 e, class_1304 s, class_1799 f, class_1799 t) {
         this.entity = e;
         this.slot = s;
         this.from = f;
         this.to = t;
      }

      public class_1309 getEntity() {
         return this.entity;
      }

      public class_1309 getMobEntity() {
         return this.entity;
      }

      public class_1304 getSlot() {
         return this.slot;
      }

      public class_1799 getFrom() {
         return this.from;
      }

      public class_1799 getTo() {
         return this.to;
      }
   }

   public static class LivingKnockBackEvent extends Event {
      private final class_1309 entity;
      private final float strength;
      private final float ratioX;
      private final float ratioZ;

      public LivingKnockBackEvent(class_1309 e, float s, float x, float z) {
         this.entity = e;
         this.strength = s;
         this.ratioX = x;
         this.ratioZ = z;
      }

      public class_1309 getEntity() {
         return this.entity;
      }

      public class_1309 getMobEntity() {
         return this.entity;
      }

      public float getStrength() {
         return this.strength;
      }

      public float getRatioX() {
         return this.ratioX;
      }

      public float getRatioZ() {
         return this.ratioZ;
      }
   }

   public static class LivingEntityUseItemEvent extends Event {
      protected final class_1309 entity;
      protected final class_1799 item;
      protected final int duration;

      protected LivingEntityUseItemEvent(class_1309 e, class_1799 i, int d) {
         this.entity = e;
         this.item = i;
         this.duration = d;
      }

      public class_1309 getEntity() {
         return this.entity;
      }

      public class_1309 getMobEntity() {
         return this.entity;
      }

      public class_1799 getItem() {
         return this.item;
      }

      public int getDuration() {
         return this.duration;
      }

      public static class Finish extends LivingEntityUseItemEvent {
         public Finish(class_1309 e, class_1799 i, int d) {
            super(e, i, d);
         }
      }

      public static class Start extends LivingEntityUseItemEvent {
         public Start(class_1309 e, class_1799 i, int d) {
            super(e, i, d);
         }
      }

      public static class Stop extends LivingEntityUseItemEvent {
         public Stop(class_1309 e, class_1799 i, int d) {
            super(e, i, d);
         }
      }

      public static class Tick extends LivingEntityUseItemEvent {
         public Tick(class_1309 e, class_1799 i, int d) {
            super(e, i, d);
         }
      }
   }

   public static class ItemEntityPickupEvent extends Event {
      private final class_1657 player;
      private final class_1542 item;

      public ItemEntityPickupEvent(class_1657 p, class_1542 i) {
         this.player = p;
         this.item = i;
      }

      public class_1657 getPlayer() {
         return this.player;
      }

      public class_1657 getPlayerEntity() {
         return this.player;
      }

      public class_1542 getItem() {
         return this.item;
      }

      public class_1542 getItemEntity() {
         return this.item;
      }
   }

   public static class ItemTossEvent extends Event {
      private final class_1657 player;
      private final class_1542 item;

      public ItemTossEvent(class_1657 p, class_1542 i) {
         this.player = p;
         this.item = i;
      }

      public class_1657 getPlayer() {
         return this.player;
      }

      public class_1657 getPlayerEntity() {
         return this.player;
      }

      public class_1542 getItem() {
         return this.item;
      }

      public class_1542 getItemEntity() {
         return this.item;
      }
   }

   public static class PlayerContainerEvent extends Event {
      protected final class_1657 player;
      protected final class_1263 container;

      protected PlayerContainerEvent(class_1657 p, class_1263 c) {
         this.player = p;
         this.container = c;
      }

      public class_1657 getPlayer() {
         return this.player;
      }

      public class_1657 getPlayerEntity() {
         return this.player;
      }

      public class_1263 getContainer() {
         return this.container;
      }

      public static class Close extends PlayerContainerEvent {
         public Close(class_1657 p, class_1263 c) {
            super(p, c);
         }
      }

      public static class Open extends PlayerContainerEvent {
         public Open(class_1657 p, class_1263 c) {
            super(p, c);
         }
      }
   }

   public static class EntityEvent extends Event {
      protected final class_1297 entity;

      public EntityEvent(class_1297 e) {
         this.entity = e;
      }

      public class_1297 getEntity() {
         return this.entity;
      }
   }

   public static class EntityJoinWorldEvent extends EntityEvent {
      private final class_1937 world;

      public EntityJoinWorldEvent(class_1297 e, class_1937 w) {
         super(e);
         this.world = w;
      }

      public class_1937 getWorld() {
         return this.world;
      }
   }

   public static class ProjectileImpactEvent extends EntityEvent {
      private final class_239 result;

      public ProjectileImpactEvent(class_1297 e, class_239 r) {
         super(e);
         this.result = r;
      }

      public class_239 getRayTraceResult() {
         return this.result;
      }
   }

   public static class WorldEvent extends Event {
      private final class_1937 world;

      public WorldEvent(class_1937 w) {
         this.world = w;
      }

      public class_1937 getWorld() {
         return this.world;
      }
   }

   public static class ServerChatEvent extends Event {
      private final class_1657 player;
      private final String message;

      public ServerChatEvent(class_1657 p, String m) {
         this.player = p;
         this.message = m;
      }

      public class_1657 getPlayer() {
         return this.player;
      }

      public String getMessage() {
         return this.message;
      }
   }

   public static class AttachCapabilitiesEvent<T> extends Event {
      private final T object;

      public AttachCapabilitiesEvent(T o) {
         this.object = o;
      }

      public T getObject() {
         return this.object;
      }

      public void addCapability(class_2960 id, Object provider) {
      }
   }

   public static class CommandEvent extends Event {
   }

   public static class TextureStitchEvent extends Event {
   }

   public static class FMLNetworkEvent extends Event {
   }

   public static class InputEvent extends Event {
      public static class KeyInputEvent extends InputEvent {
      }
   }
}
