package mchorse.mappet.client.gui.utils.overlays;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import mchorse.mappet.utils.Utils;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.client.gui.utils.keys.LangKey;
import net.minecraft.class_1109;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_3419;
import net.minecraft.class_5819;
import net.minecraft.class_7923;
import net.minecraft.class_1113.class_1114;
import org.apache.commons.io.IOUtils;

public class GuiSoundOverlayPanel extends GuiResourceLocationOverlayPanel {
   private static Set<class_2960> extraSounds = new HashSet();
   private static long lastUpdate;

   private static Set<class_2960> getSoundEvents() {
      Set<class_2960> locations = new HashSet();
      if (lastUpdate < LangKey.lastTime) {
         extraSounds.clear();
         updateSounds("b.a");
         updateSounds("mp.sounds");
         lastUpdate = LangKey.lastTime;
      }

      locations.addAll(class_7923.field_41172.method_10235());
      locations.addAll(extraSounds);
      return locations;
   }

   private static void updateSounds(String rp) {
      try {
         class_3298 resource = (class_3298)class_310.method_1551().method_1478().method_14486(new class_2960(rp, "sounds.json")).orElseThrow();
         JsonElement element = (new JsonParser()).parse(IOUtils.toString(resource.method_14482(), Utils.getCharset()));
         if (element.isJsonObject()) {
            for(Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
               extraSounds.add(new class_2960(rp, (String)entry.getKey()));
            }
         }
      } catch (Exception var5) {
      }

   }

   public GuiSoundOverlayPanel(class_310 mc, Consumer<class_2960> callback) {
      super(mc, IKey.lang("mappet.gui.overlays.sounds.main"), getSoundEvents(), callback);
      GuiIconElement edit = new GuiIconElement(mc, Icons.SOUND, (b) -> this.playSound());
      edit.flex().wh(16, 16);
      this.icons.add(edit);
   }

   private void playSound() {
      if (this.rls.list.getIndex() > 0) {
         class_2960 location = new class_2960((String)this.rls.list.getCurrentFirst());
         float x = (float)this.mc.field_1724.method_23317();
         float y = (float)this.mc.field_1724.method_23318();
         float z = (float)this.mc.field_1724.method_23321();
         this.mc.method_1483().method_4873(new class_1109(location, class_3419.field_15250, 1.0F, 1.0F, class_5819.method_43047(), false, 0, class_1114.field_5476, (double)x, (double)y, (double)z, false));
      }
   }
}
