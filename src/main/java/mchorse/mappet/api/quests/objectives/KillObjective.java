package mchorse.mappet.api.quests.objectives;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1074;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1657;
import net.minecraft.class_2487;
import net.minecraft.class_2520;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class KillObjective extends AbstractObjective {
   public static final class_2960 PLAYER_ID = new class_2960("minecraft:player");
   public class_2960 entity = new class_2960("");
   public class_2487 tag;
   public int count;
   public int killed;

   public KillObjective() {
   }

   public KillObjective(class_2960 entity, int quantity) {
      this.entity = entity;
      this.count = quantity;
   }

   public void playerKilled(class_1657 player, class_1297 mob) {
      boolean isPlayer = mob instanceof class_1657 && this.entity.equals(PLAYER_ID);
      if (this.entity.equals(class_7923.field_41177.method_10221(mob.method_5864())) || isPlayer) {
         if (!this.compareTag(mob)) {
            return;
         }

         ++this.killed;
      }

   }

   private boolean compareTag(class_1297 mob) {
      if (this.tag == null) {
         return true;
      } else {
         class_2487 tag = new class_2487();
         mob.method_5647(tag);
         return this.compareTagPartial(this.tag, tag);
      }
   }

   private boolean compareTagPartial(class_2520 a, class_2520 b) {
      if (a instanceof class_2487 tagA && b instanceof class_2487) {
         class_2487 tagB = (class_2487)b;

         for(String key : tagA.method_10541()) {
            class_2520 tagBase = tagB.method_10580(key);
            if (!this.compareTagPartial(tagA.method_10580(key), tagBase)) {
               return false;
            }
         }

         return true;
      } else {
         return a.equals(b);
      }
   }

   public boolean isComplete(class_1657 player) {
      return this.killed >= this.count;
   }

   public void complete(class_1657 player) {
   }

   @Environment(EnvType.CLIENT)
   public String stringifyObjective(class_1657 player) {
      class_1299<?> type = (class_1299)class_7923.field_41177.method_10223(this.entity);
      String entity = type == null ? null : type.method_5882();
      int count = Math.min(this.killed, this.count);
      if (entity != null) {
         entity = class_1074.method_4662(entity, new Object[0]);
      } else {
         entity = this.entity.toString();
      }

      return !this.message.isEmpty() ? this.message.replace("${entity}", entity).replace("${count}", String.valueOf(count)).replace("${total}", String.valueOf(this.count)) : class_1074.method_4662("mappet.gui.quests.objective_kill.string", new Object[]{entity, count, this.count});
   }

   public String getType() {
      return "kill";
   }

   public class_2487 partialSerializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10569("Killed", this.killed);
      return tag;
   }

   public void partialDeserializeNBT(class_2487 tag) {
      if (tag.method_10545("Killed")) {
         this.killed = tag.method_10550("Killed");
      }

   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      tag.method_10582("Entity", this.entity.toString());
      if (this.tag != null) {
         tag.method_10566("Tag", this.tag);
      }

      tag.method_10569("Count", this.count);
      tag.method_10569("Killed", this.killed);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.entity = new class_2960(tag.method_10558("Entity"));
      if (tag.method_10573("Tag", 10)) {
         this.tag = tag.method_10562("Tag");
      }

      this.count = tag.method_10550("Count");
      this.killed = tag.method_10550("Killed");
   }
}
