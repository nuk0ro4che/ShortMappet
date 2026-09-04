package mchorse.mappet.api.huds;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.api.utils.AbstractData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_2499;

public class HUDScene extends AbstractData {
   public List<HUDMorph> morphs = new ArrayList();
   public float fov = 70.0F;
   public boolean hide;
   public boolean global;
   
   public boolean worldLighting;
   
   public float worldLightingIntensity = 1.0F;

   @Environment(EnvType.CLIENT)
   public boolean update(boolean allowExpiring) {
      this.morphs.removeIf((morph) -> morph.update(allowExpiring));
      return allowExpiring && this.morphs.isEmpty();
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      class_2499 morphs = new class_2499();

      for(HUDMorph morph : this.morphs) {
         morphs.add(morph.serializeNBT());
      }

      tag.method_10566("Morphs", morphs);
      tag.method_10548("Fov", this.fov);
      tag.method_10556("Hide", this.hide);
      tag.method_10556("Global", this.global);
      tag.method_10556("WorldLighting", this.worldLighting);
      tag.method_10548("WorldLightingIntensity", this.worldLightingIntensity);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10573("Morphs", 9)) {
         class_2499 list = tag.method_10554("Morphs", 10);

         for(int i = 0; i < list.size(); ++i) {
            HUDMorph morph = new HUDMorph();
            morph.deserializeNBT(list.method_10602(i));
            this.morphs.add(morph);
         }
      }

      if (tag.method_10545("Fov")) {
         this.fov = tag.method_10583("Fov");
      }

      if (tag.method_10545("Hide")) {
         this.hide = tag.method_10577("Hide");
      }

      if (tag.method_10545("Global")) {
         this.global = tag.method_10577("Global");
      }

      if (tag.method_10545("WorldLighting")) {
         this.worldLighting = tag.method_10577("WorldLighting");
      }
      if (tag.method_10545("WorldLightingIntensity")) {
         this.worldLightingIntensity = Math.max(0.0F, Math.min(4.0F, tag.method_10583("WorldLightingIntensity")));
      }

   }
}
