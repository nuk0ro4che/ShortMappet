package mchorse.mappet.api.scripts.code;

import mchorse.mappet.api.scripts.code.entities.ScriptEntity;
import mchorse.mappet.api.scripts.user.IScriptRayTrace;
import mchorse.mappet.api.scripts.user.lights.IScriptLight;
import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import net.minecraft.class_1937;
import net.minecraft.class_239;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_239.class_240;

public class ScriptRayTrace implements IScriptRayTrace {
   private class_239 result;
   private IScriptEntity entity;
   private class_1937 world;
   private ScriptVector start;

   public ScriptRayTrace(class_239 result) {
      this(null, null, result);
   }

   public ScriptRayTrace(class_1937 world, ScriptVector start, class_239 result) {
      this.world = world;
      this.start = start;
      this.result = result;
   }

   public class_239 getMinecraftRayTraceResult() {
      return this.result;
   }

   public boolean isMissed() {
      return this.result.method_17783() == class_240.field_1333;
   }

   public boolean isBlock() {
      return this.result.method_17783() == class_240.field_1332;
   }

   public boolean isEntity() {
      return this.result.method_17783() == class_240.field_1331;
   }

   public IScriptEntity getEntity() {
      class_239 var2 = this.result;
      if (var2 instanceof class_3966 hit) {
         if (this.entity == null) {
            this.entity = ScriptEntity.create(hit.method_17782());
         }

         return this.entity;
      } else {
         return null;
      }
   }

   public ScriptVector getBlock() {
      class_239 var2 = this.result;
      ScriptVector var10000;
      if (var2 instanceof class_3965 hit) {
         var10000 = new ScriptVector(hit.method_17777());
      } else {
         var10000 = null;
      }

      return var10000;
   }

   public ScriptVector getHitPosition() {
      return new ScriptVector(this.result.method_17784());
   }

   public void displayLight(String id, int duration, int level, float spacing) {
      if (this.world == null || this.start == null) {
         return;
      }

      (new ScriptWorld(this.world)).displayRayTraceLight(id, duration, this.start.x, this.start.y, this.start.z, this, level, spacing);
   }

   public IScriptLight displayFlashlight(String id, int duration, int startLevel, int endLevel, float spacing) {
      if (this.world == null || this.start == null) {
         return ScriptFlashlight.empty();
      }

      return (new ScriptWorld(this.world)).displayFlashlight(id, duration, this.start.x, this.start.y, this.start.z, this, startLevel, endLevel, spacing);
   }
}
