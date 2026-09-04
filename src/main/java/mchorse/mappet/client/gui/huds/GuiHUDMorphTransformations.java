package mchorse.mappet.client.gui.huds;

import mchorse.mappet.api.huds.HUDMorph;
import mchorse.mclib.client.gui.framework.elements.input.GuiTransformations;
import net.minecraft.class_310;

public class GuiHUDMorphTransformations extends GuiTransformations {
   public HUDMorph morph;

   public GuiHUDMorphTransformations(class_310 mc) {
      super(mc);
   }

   public void setMorph(HUDMorph morph) {
      this.morph = morph;
      if (morph != null) {
         this.fillT((double)morph.translate.x, (double)morph.translate.y, (double)morph.translate.z);
         this.fillS((double)morph.scale.x, (double)morph.scale.y, (double)morph.scale.z);
         this.fillR((double)morph.rotate.x, (double)morph.rotate.y, (double)morph.rotate.z);
      }

   }

   public void setT(double x, double y, double z) {
      this.morph.translate.x = (float)x;
      this.morph.translate.y = (float)y;
      this.morph.translate.z = (float)z;
   }

   public void setS(double x, double y, double z) {
      this.morph.scale.x = (float)x;
      this.morph.scale.y = (float)y;
      this.morph.scale.z = (float)z;
   }

   public void setR(double x, double y, double z) {
      this.morph.rotate.x = (float)x;
      this.morph.rotate.y = (float)y;
      this.morph.rotate.z = (float)z;
   }
}
