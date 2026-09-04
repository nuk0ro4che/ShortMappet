package mchorse.mappet.api.scripts.code;

import mchorse.mappet.api.scripts.lights.VanillaWorldLightManager;
import mchorse.mappet.api.scripts.user.lights.IScriptLight;
import net.minecraft.class_1937;
import net.minecraft.class_3218;


public class ScriptFlashlight implements IScriptLight {
   private static final IScriptLight EMPTY = new IScriptLight() {
      public IScriptLight nonSolidIgnore() {
         return this;
      }
   };

   private final class_1937 world;
   private final String id;
   private final int duration;
   private final double x;
   private final double y;
   private final double z;
   private final double endX;
   private final double endY;
   private final double endZ;
   private final int startLevel;
   private final int endLevel;
   private final float spacing;
   private boolean nonSolidIgnore;

   public static IScriptLight empty() {
      return EMPTY;
   }

   public static IScriptLight create(class_1937 world, String id, int duration, double x, double y, double z, double endX, double endY, double endZ, int startLevel, int endLevel, float spacing) {
      if (world == null || id == null) {
         return EMPTY;
      }

      ScriptFlashlight light = new ScriptFlashlight(world, id, duration, x, y, z, endX, endY, endZ, startLevel, endLevel, spacing);
      light.place(false);
      return light;
   }

   private ScriptFlashlight(class_1937 world, String id, int duration, double x, double y, double z, double endX, double endY, double endZ, int startLevel, int endLevel, float spacing) {
      this.world = world;
      this.id = id;
      this.duration = duration;
      this.x = x;
      this.y = y;
      this.z = z;
      this.endX = endX;
      this.endY = endY;
      this.endZ = endZ;
      this.startLevel = startLevel;
      this.endLevel = endLevel;
      this.spacing = spacing;
   }

   public IScriptLight nonSolidIgnore() {
      this.nonSolidIgnore = true;
      this.place();
      return this;
   }

   private void place(boolean nonSolidIgnore) {
      this.nonSolidIgnore = nonSolidIgnore;
      this.place();
   }

   private void place() {
      if (this.world instanceof class_3218) {
         VanillaWorldLightManager.displayFlashlight((class_3218)this.world, this.id, this.duration, this.x, this.y, this.z, this.endX, this.endY, this.endZ, this.startLevel, this.endLevel, this.spacing, this.nonSolidIgnore);
      } else {
         VanillaWorldLightManager.displayFlashlightClient(this.world, this.id, this.duration, this.x, this.y, this.z, this.endX, this.endY, this.endZ, this.startLevel, this.endLevel, this.spacing, this.nonSolidIgnore);
      }
   }
}
