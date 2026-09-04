package mchorse.mappet.api.conditions.blocks;

import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.TargetMode;
import mchorse.mappet.utils.EntityUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1074;
import net.minecraft.class_1297;

public class EntityConditionBlock extends PropertyConditionBlock {
   protected TargetMode getDefaultTarget() {
      return TargetMode.SUBJECT;
   }

   protected boolean evaluateBlock(DataContext context) {
      class_1297 entity = this.target.getEntity(context);
      if (entity == null) {
         return false;
      } else {
         double value = EntityUtils.getProperty(entity, this.id);
         return this.comparison.comparison.isString ? this.compareString(String.valueOf(value)) : this.compare(value);
      }
   }

   @Environment(EnvType.CLIENT)
   public String stringify() {
      String id = "";
      if (EntityUtils.ENTITY_PROPERTIES.contains(this.id)) {
         id = class_1074.method_4662("mappet.gui.entity_property." + this.id, new Object[0]);
      }

      return this.comparison.stringify(id);
   }
}
