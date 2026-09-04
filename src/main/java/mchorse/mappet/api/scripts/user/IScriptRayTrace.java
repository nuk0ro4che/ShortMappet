package mchorse.mappet.api.scripts.user;

import mchorse.mappet.api.scripts.user.lights.IScriptLight;

import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import net.minecraft.class_239;

public interface IScriptRayTrace {
   class_239 getMinecraftRayTraceResult();

   boolean isMissed();

   boolean isBlock();

   boolean isEntity();

   IScriptEntity getEntity();

   ScriptVector getBlock();

   ScriptVector getHitPosition();

   
   default void displayLight(String id, int duration) {
      this.displayLight(id, duration, 15, 0.5F);
   }

   
   void displayLight(String id, int duration, int level, float spacing);

   
   default IScriptLight displayFlashlight(String id, int duration) {
      return this.displayFlashlight(id, duration, 1, 15, 0.5F);
   }

   
   IScriptLight displayFlashlight(String id, int duration, int startLevel, int endLevel, float spacing);
}
