package mchorse.mappet.client.gui.huds;

import java.util.function.Consumer;
import mchorse.mappet.api.huds.HUDMorph;
import mchorse.mappet.api.huds.HUDScene;
import mchorse.mappet.client.gui.utils.GuiMappetUtils;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlayPanel;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_2487;
import net.minecraft.class_2522;
import net.minecraft.class_310;

public class GuiHUDMorphsOverlayPanel extends GuiOverlayPanel {
   public GuiHUDMorphListElement morphs;
   private HUDScene scene;
   private Consumer<HUDMorph> callback;

   public GuiHUDMorphsOverlayPanel(class_310 mc, HUDScene scene, Consumer<HUDMorph> callback) {
      super(mc, IKey.lang("mappet.gui.huds.overlay.title"));
      this.scene = scene;
      this.callback = callback;
      this.morphs = new GuiHUDMorphListElement(mc, (list) -> this.accept((HUDMorph)list.get(0)));
      this.morphs.sorting().context(() -> {
         GuiSimpleContextMenu menu = new GuiSimpleContextMenu(mc);
         menu.action(Icons.ADD, IKey.lang("mappet.gui.huds.context.add"), this::addMorph);
         if (!this.morphs.isDeselected()) {
            menu.action(Icons.COPY, IKey.lang("mappet.gui.huds.context.copy"), this::copyMorph);

            try {
               class_2487 tag = class_2522.method_10718(GuiUtils.getClipboardString());
               if (tag.method_10545("_HUDMorphCopy")) {
                  HUDMorph morph = new HUDMorph();
                  morph.deserializeNBT(tag);
                  menu.action(Icons.PASTE, IKey.lang("mappet.gui.huds.context.paste"), () -> this.addMorph(morph));
               }
            } catch (Exception var5) {
            }

            menu.action(Icons.REMOVE, IKey.lang("mappet.gui.huds.context.remove"), this::removeMorph, 16711731);
         }

         return menu.shadow();
      });
      this.morphs.flex().relative(this.content).wh(1.0F, 1.0F);
      this.morphs.setList(this.scene.morphs);
      ScrollArea var10000 = this.morphs.scroll;
      var10000.scrollSpeed *= 2;
      this.content.add(this.morphs);
   }

   private void addMorph() {
      HUDMorph morph = new HUDMorph();
      

      morph.orthoX = 0.5F;
      morph.orthoY = 0.5F;
      this.addMorph(morph);
   }

   private void addMorph(HUDMorph morph) {
      this.scene.morphs.add(morph);
      this.morphs.update();
      this.morphs.setCurrentScroll(morph);
      this.accept(morph);
   }

   private void copyMorph() {
      class_2487 tag = ((HUDMorph)this.morphs.getCurrentFirst()).serializeNBT();
      tag.method_10556("_HUDMorphCopy", true);
      GuiUtils.setClipboardString(tag.toString());
   }

   private void removeMorph() {
      int index = this.morphs.getIndex();
      this.scene.morphs.remove(this.morphs.getIndex());
      this.morphs.update();
      this.morphs.setIndex(index < 1 ? 0 : index - 1);
      this.accept((HUDMorph)this.morphs.getCurrentFirst());
   }

   public GuiHUDMorphsOverlayPanel set(HUDMorph morph) {
      this.morphs.setCurrentScroll(morph);
      return this;
   }

   protected void accept(HUDMorph morph) {
      if (this.callback != null) {
         this.callback.accept(morph);
      }

   }

   public void draw(GuiContext context) {
      super.draw(context);
      if (this.scene.morphs.size() <= 1) {
         GuiMappetUtils.drawRightClickHere(context, this.area);
      }

   }
}
