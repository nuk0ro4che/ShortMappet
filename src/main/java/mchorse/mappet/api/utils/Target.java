package mchorse.mappet.api.utils;

import com.mojang.brigadier.StringReader;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.states.States;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mappet.utils.EntityUtils;
import mchorse.mappet.utils.EnumUtils;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2303;
import net.minecraft.class_2487;

public class Target implements INBTSerializable<class_2487> {
   public TargetMode mode;
   public String selector = "";
   private TargetMode defaultMode;

   public Target(TargetMode mode) {
      this.mode = this.defaultMode = mode;
   }

   public class_1657 getPlayer(DataContext context) {
      if (this.mode == TargetMode.SUBJECT && context.subject instanceof class_1657) {
         return (class_1657)context.subject;
      } else if (this.mode == TargetMode.OBJECT && context.object instanceof class_1657) {
         return (class_1657)context.object;
      } else if (this.mode == TargetMode.PLAYER) {
         return context.getPlayer();
      } else {
         if (this.mode == TargetMode.SELECTOR) {
            try {
               return (new class_2303(new StringReader(this.selector))).method_9882().method_9811(context.getSender());
            } catch (Exception var3) {
            }
         }

         return null;
      }
   }

   public class_1297 getEntity(DataContext context) {
      if (this.mode == TargetMode.SUBJECT && context.subject != null) {
         return context.subject;
      } else if (this.mode == TargetMode.OBJECT && context.object != null) {
         return context.object;
      } else if (this.mode == TargetMode.PLAYER) {
         return context.getPlayer();
      } else if (this.mode == TargetMode.NPC) {
         return context.getNpc();
      } else {
         if (this.mode == TargetMode.SELECTOR) {
            try {
               return (new class_2303(new StringReader(this.selector))).method_9882().method_9809(context.getSender());
            } catch (Exception var3) {
            }
         }

         return null;
      }
   }

   public ICharacter getCharacter(DataContext context) {
      return Character.get(this.getPlayer(context));
   }

   public States getStates(DataContext context) {
      return this.mode != TargetMode.GLOBAL ? EntityUtils.getStates(this.getEntity(context)) : Mappet.states;
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10569("Target", this.mode.ordinal());
      tag.method_10582("Selector", this.selector);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      this.mode = (TargetMode)EnumUtils.getValue(tag.method_10550("Target"), TargetMode.values(), this.defaultMode);
      this.selector = tag.method_10558("Selector");
   }
}
