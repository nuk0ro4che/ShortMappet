package mchorse.mappet.api.regions;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.conditions.Checker;
import mchorse.mappet.api.regions.shapes.AbstractShape;
import mchorse.mappet.api.regions.shapes.BoxShape;
import mchorse.mappet.api.states.States;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.TargetMode;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mappet.utils.EntityUtils;
import mchorse.mappet.utils.EnumUtils;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2499;

public class Region implements INBTSerializable<class_2487> {
   public boolean passable = true;
   public boolean checkEntities = false;
   public Checker enabled = new Checker(true);
   public int delay;
   public int update = 3;
   public Trigger onEnter = new Trigger();
   public Trigger onExit = new Trigger();
   public Trigger onTick = new Trigger();
   public List<AbstractShape> shapes = new ArrayList();
   public States states = new States();
   public boolean writeState;
   public String state = "";
   public TargetMode target;
   public boolean additive;
   public boolean once;

   public Region() {
      this.target = TargetMode.GLOBAL;
      this.additive = true;
      this.shapes.add(new BoxShape());
   }

   public boolean isEnabled(class_1297 entity) {
      if (this.once) {
         States states = this.getStates(entity);
         if (states != null && states.values.containsKey(this.state)) {
            return false;
         }
      }

      return this.enabled.check(new DataContext(entity));
   }

   public boolean isPlayerInside(class_1297 entity, class_2338 pos) {
      for(AbstractShape shape : this.shapes) {
         if (shape.isEntityInside(entity, pos)) {
            return true;
         }
      }

      return false;
   }

   public boolean isPlayerInside(double x, double y, double z, class_2338 pos) {
      for(AbstractShape shape : this.shapes) {
         if (shape.isEntityInside(x, y, z, pos)) {
            return true;
         }
      }

      return false;
   }

   public void triggerEnter(class_1297 entity, class_2338 pos) {
      if (this.writeState && !this.state.isEmpty()) {
         States states = this.getStates(entity);
         if (this.additive) {
            states.add(this.state, (double)1.0F);
         } else {
            states.setNumber(this.state, (double)1.0F);
         }
      }

      this.onEnter.trigger((new DataContext(entity)).set("x", (double)pos.method_10263()).set("y", (double)pos.method_10264()).set("z", (double)pos.method_10260()));
   }

   public void triggerExit(class_1297 entity, class_2338 pos) {
      if (this.writeState && !this.state.isEmpty()) {
         States states = this.getStates(entity);
         if (!this.additive) {
            states.reset(this.state);
         }
      }

      this.onExit.trigger((new DataContext(entity)).set("x", (double)pos.method_10263()).set("y", (double)pos.method_10264()).set("z", (double)pos.method_10260()));
   }

   public void triggerTick(class_1297 entity, class_2338 pos) {
      this.onTick.trigger((new DataContext(entity)).set("x", (double)pos.method_10263()).set("y", (double)pos.method_10264()).set("z", (double)pos.method_10260()));
   }

   private States getStates(class_1297 entity) {
      return this.target == TargetMode.GLOBAL ? Mappet.states : EntityUtils.getStates(entity);
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10556("Passable", this.passable);
      tag.method_10566("Enabled", this.enabled.serializeNBT());
      tag.method_10569("Delay", this.delay);
      tag.method_10569("Update", this.update);
      tag.method_10556("CheckEntities", this.checkEntities);
      tag.method_10566("OnEnter", this.onEnter.serializeNBT());
      tag.method_10566("OnExit", this.onExit.serializeNBT());
      tag.method_10566("OnTick", this.onTick.serializeNBT());
      class_2499 shapes = new class_2499();

      for(AbstractShape shape : this.shapes) {
         class_2487 shapeTag = shape.serializeNBT();
         shapeTag.method_10582("Type", shape.getType());
         shapes.add(shapeTag);
      }

      tag.method_10566("Shapes", shapes);
      tag.method_10556("WriteState", this.writeState);
      tag.method_10582("State", this.state.trim());
      tag.method_10569("Target", this.target.ordinal());
      tag.method_10556("Additive", this.additive);
      tag.method_10556("Once", this.once);
      tag.method_10566("States", this.states.serializeNBT());
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10545("Passable")) {
         this.passable = tag.method_10577("Passable");
      }

      if (tag.method_10573("Enabled", 10)) {
         this.enabled.deserializeNBT(tag.method_10580("Enabled"));
      }

      if (tag.method_10573("Delay", 99)) {
         this.delay = tag.method_10550("Delay");
      }

      if (tag.method_10573("Update", 99)) {
         this.update = tag.method_10550("Update");
      }

      if (tag.method_10545("CheckEntities")) {
         this.checkEntities = tag.method_10577("CheckEntities");
      }

      if (tag.method_10573("OnEnter", 10)) {
         this.onEnter.deserializeNBT(tag.method_10562("OnEnter"));
      }

      if (tag.method_10573("OnExit", 10)) {
         this.onExit.deserializeNBT(tag.method_10562("OnExit"));
      }

      if (tag.method_10573("OnTick", 10)) {
         this.onTick.deserializeNBT(tag.method_10562("OnTick"));
      }

      if (tag.method_10545("States")) {
         this.states.deserializeNBT(tag.method_10562("States"));
      }

      this.shapes.clear();
      if (tag.method_10573("Shape", 10)) {
         AbstractShape shape = this.readShape(tag.method_10562("Shape"));
         if (shape != null) {
            this.shapes.add(shape);
         }
      } else if (tag.method_10573("Shapes", 9)) {
         class_2499 list = tag.method_10554("Shapes", 10);

         for(int i = 0; i < list.size(); ++i) {
            AbstractShape shape = this.readShape(list.method_10602(i));
            if (shape != null) {
               this.shapes.add(shape);
            }
         }
      }

      if (this.shapes.isEmpty()) {
         this.shapes.add(new BoxShape());
      }

      this.writeState = tag.method_10577("WriteState");
      this.state = tag.method_10558("State");
      this.target = (TargetMode)EnumUtils.getValue(tag.method_10550("Target"), TargetMode.values(), TargetMode.GLOBAL);
      this.additive = tag.method_10577("Additive");
      this.once = tag.method_10577("Once");
   }

   private AbstractShape readShape(class_2487 shapeTag) {
      if (shapeTag.method_10545("Type")) {
         AbstractShape shape = AbstractShape.fromString(shapeTag.method_10558("Type"));
         if (shape != null) {
            shape.deserializeNBT(shapeTag);
            return shape;
         }
      }

      return null;
   }
}
