package mchorse.mappet.tile;

import java.util.ArrayList;
import java.util.List;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.TileEntityModelSettings;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.conditions.Checker;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.blocks.PacketEditConditionModel;
import mchorse.mappet.utils.ConditionModel;
import mchorse.mclib.math.Constant;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2586;
import net.minecraft.class_2596;
import net.minecraft.class_2602;
import net.minecraft.class_2622;
import net.minecraft.class_2680;
import net.minecraft.class_3222;

public class TileConditionModel extends class_2586 {
   public EntityActor entity;
   public int frequency = 1;
   public boolean isGlobal = true;
   public boolean isShadow = false;
   private TileEntityModelSettings settings = new TileEntityModelSettings();
   public List<ConditionModel> list = new ArrayList();
   private int tick;

   public TileConditionModel(class_2338 pos, class_2680 state) {
      super(Mappet.conditionModelTile, pos, state);
   }

   @Environment(EnvType.CLIENT)
   public double getMaxRenderDistanceSquared() {
      float range = (float)(Integer)Blockbuster.actorRenderingRange.get();
      return (double)(range * range);
   }

   public void tick() {
      if (this.field_11863.field_9236) {
         this.updateMorph();
      } else {
         int frequency = Math.max(this.frequency, 1);
         Constant constantFalse = new Constant((double)0.0F);
         if (this.tick % frequency == 0) {
            for(class_1657 playerEntity : this.field_11863.method_18456()) {
               AbstractMorph morph = null;

               for(ConditionModel conditionModel : this.list) {
                  boolean result = false;
                  DataContext context = new DataContext(playerEntity);
                  Checker checker = conditionModel.checker;
                  if (checker.mode == Checker.Mode.CONDITION) {
                     result = checker.condition.execute(context);
                  } else {
                     String expression = checker.expression;
                     result = expression != null && !expression.trim().isEmpty() && Mappet.expressions.set(context).parse(expression, constantFalse).booleanValue();
                  }

                  if (result && !conditionModel.morph.equals(morph)) {
                     morph = conditionModel.morph;
                  }
               }

               class_2487 tag = new class_2487();
               class_2487 tagMorph = new class_2487();
               if (morph != null) {
                  morph.toNBT(tagMorph);
               }

               class_2487 settings = new class_2487();
               this.settings.toNBT(settings);
               tag.method_10566("settings", settings);
               tag.method_10566("morph", tagMorph);
               tag.method_10556("shadow", this.isShadow);
               tag.method_10556("global", this.isGlobal);
               Dispatcher.sendTo((new PacketEditConditionModel(this.method_11016(), tag)).setIsEdit(false), (class_3222)playerEntity);
            }
         }

         ++this.tick;
      }
   }

   @Environment(EnvType.CLIENT)
   public void updateMorph() {
      if (this.entity == null) {
         this.createEntity(this.field_11863);
      }

      if (this.entity.morph.get() != null) {
         this.entity.morph.get().update(this.entity);
      }

      ++this.entity.field_6012;
   }

   public TileEntityModelSettings getSettings() {
      return this.settings;
   }

   public void createEntity(class_1937 world) {
      if (world != null) {
         this.entity = new EntityActor(world);
         this.entity.method_24830(true);
      }

   }

   public void fill(class_2487 tag) {
      this.list.clear();
      class_2499 list = tag.method_10554("list", 10);

      for(int i = 0; i < list.size(); ++i) {
         class_2487 element = list.method_10602(i);
         ConditionModel conditionModel = new ConditionModel();
         conditionModel.deserializeNBT(element);
         this.list.add(conditionModel);
      }

      this.frequency = tag.method_10550("frequency");
      this.isGlobal = tag.method_10577("global");
      this.isShadow = tag.method_10577("shadow");
   }

   public class_2487 toNBT(class_2487 tag) {
      class_2499 list = new class_2499();

      for(ConditionModel element : this.list) {
         list.add(element.serializeNBT());
      }

      tag.method_10566("list", list);
      tag.method_10569("frequency", this.frequency);
      tag.method_10556("global", this.isGlobal);
      tag.method_10556("shadow", this.isShadow);
      return tag;
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      this.method_11007(tag);
      return tag;
   }

   public class_2487 getUpdateTag() {
      return this.method_38244();
   }

   public class_2596<class_2602> method_38235() {
      return class_2622.method_38585(this);
   }

   public class_2487 writeToNBT(class_2487 tag) {
      class_2487 settings = new class_2487();
      this.settings.toNBT(settings);
      tag.method_10566("settings", settings);
      this.toNBT(tag);
      return tag;
   }

   public void readFromNBT(class_2487 tag) {
      this.settings = new TileEntityModelSettings();
      this.settings.fromNBT((class_2487)tag.method_10580("settings"));
      this.fill(tag);
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
