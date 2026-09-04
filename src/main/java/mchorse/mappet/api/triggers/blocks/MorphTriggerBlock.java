package mchorse.mappet.api.triggers.blocks;

import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.Target;
import mchorse.mappet.api.utils.TargetMode;
import mchorse.mappet.entities.EntityNpc;
import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.MorphManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1074;
import net.minecraft.class_1297;
import net.minecraft.class_2487;
import net.minecraft.class_3222;

public class MorphTriggerBlock extends AbstractTriggerBlock {
   public Target target;
   public class_2487 morph;

   public MorphTriggerBlock() {
      this.target = new Target(TargetMode.SUBJECT);
   }

   @Environment(EnvType.CLIENT)
   public String stringify() {
      return this.morph == null ? class_1074.method_4662("mappet.gui.triggers.morph.demorph", new Object[0]) : class_1074.method_4662("mappet.gui.triggers.morph.morph", new Object[]{this.morph.method_10558("Name")});
   }

   public void trigger(DataContext context) {
      class_1297 entity = this.target.getEntity(context);
      if (entity instanceof class_3222 player) {
         if (this.morph == null) {
            MorphAPI.demorph(player);
         } else {
            MorphAPI.morph(player, MorphManager.INSTANCE.morphFromNBT(this.morph), true);
         }
      } else if (entity instanceof EntityNpc npc) {
         npc.setMorph(MorphManager.INSTANCE.morphFromNBT(this.morph));
         npc.sendNpcStateChangePacket();
      }

   }

   public boolean isEmpty() {
      return this.morph == null;
   }

   protected void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10566("Target", this.target.serializeNBT());
      if (this.morph != null) {
         tag.method_10566("Morph", this.morph);
      }

   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Target")) {
         this.target.deserializeNBT(tag.method_10562("Target"));
      }

      this.morph = null;
      if (tag.method_10545("Morph")) {
         this.morph = tag.method_10562("Morph");
      }

   }
}
