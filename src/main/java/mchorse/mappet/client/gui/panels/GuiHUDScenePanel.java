package mchorse.mappet.client.gui.panels;

import mchorse.mappet.api.huds.HUDMorph;
import mchorse.mappet.api.huds.HUDScene;
import mchorse.mappet.api.huds.HUDStage;
import mchorse.mappet.api.utils.ContentType;
import mchorse.mappet.client.RenderingHandler;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.huds.GuiHUDMorphTransformations;
import mchorse.mappet.client.gui.huds.GuiHUDMorphsOverlayPanel;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlay;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import net.minecraft.class_310;

public class GuiHUDScenePanel extends GuiMappetDashboardPanel<HUDScene> {
   public GuiIconElement morphs;
   public GuiElement column;
   public GuiNestedEdit morph;
   public GuiToggleElement ortho;
   public GuiTrackpadElement orthoX;
   public GuiTrackpadElement orthoY;
   public GuiTrackpadElement expire;
   public GuiHUDMorphTransformations transformations;
   public GuiTrackpadElement fov;
   public GuiToggleElement hide;
   public GuiToggleElement global;
   public GuiToggleElement worldLighting;
   private HUDStage stage = new HUDStage(true);
   private HUDMorph current;
   private long lastTick;

   public GuiHUDScenePanel(class_310 mc, GuiMappetDashboard dashboard) {
      super(mc, dashboard);
      this.namesList.setFileIcon(Icons.POSE);
      this.morphs = new GuiIconElement(mc, Icons.MORE, (b) -> this.openMorphs());
      this.morph = new GuiNestedEdit(mc, this::openMorphMenu);
      this.ortho = new GuiToggleElement(mc, IKey.lang("mappet.gui.huds.ortho"), (b) -> this.current.ortho = b.isToggled());
      this.orthoX = new GuiTrackpadElement(mc, (v) -> this.current.orthoX = v.floatValue());
      this.orthoX.limit((double)0.0F, (double)1.0F).metric().strong = (double)0.01F;
      this.orthoY = new GuiTrackpadElement(mc, (v) -> this.current.orthoY = v.floatValue());
      this.orthoY.limit((double)0.0F, (double)1.0F).metric().strong = (double)0.01F;
      this.expire = new GuiTrackpadElement(mc, (v) -> this.current.expire = v.intValue());
      this.expire.limit((double)0.0F).integer();
      this.transformations = new GuiHUDMorphTransformations(mc);
      this.column = Elements.column(mc, 5, 10, new GuiElement[0]);
      this.fov = new GuiTrackpadElement(mc, (v) -> (this.data).fov = v.floatValue());
      this.fov.limit((double)0.0F, (double)180.0F);
      this.hide = new GuiToggleElement(mc, IKey.lang("mappet.gui.huds.hide"), (b) -> (this.data).hide = b.isToggled());
      this.global = new GuiToggleElement(mc, IKey.lang("mappet.gui.huds.global"), (b) -> (this.data).global = b.isToggled());
      this.worldLighting = new GuiToggleElement(mc, IKey.lang("mappet.gui.huds.world_lighting"), (b) -> (this.data).worldLighting = b.isToggled());
      this.morphs.flex().relative(this.editor);
      this.column.flex().relative(this.editor).y(1.0F).w(130).anchorY(1.0F);
      this.transformations.flex().relative(dashboard.root).x(0.5F).y(1.0F, -10).wh(190, 70).anchor(0.5F, 1.0F);
            this.column.add(new IGuiElement[]{this.morph, this.ortho, this.orthoX, this.orthoY, Elements.label(IKey.lang("mappet.gui.huds.expire")).marginTop(12), this.expire});
      this.column.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.huds.fov")).marginTop(12), this.fov, this.hide, this.global, this.worldLighting});

      this.editor.add(new IGuiElement[]{this.column, this.transformations, this.morphs});
      this.fill(null);
   }

   private void openMorphs() {
      GuiHUDMorphsOverlayPanel overlay = new GuiHUDMorphsOverlayPanel(this.mc, this.data, this::pickMorph);
      GuiOverlay.addOverlay(GuiBase.getCurrent(), overlay.set(this.current), 0.4F, 0.6F);
   }

   private void openMorphMenu(boolean editing) {
      GuiMappetDashboard.get(this.mc).openMorphMenu(this.getParentContainer(), editing, this.current.morph.get(), this::setMorph);
   }

   private void setMorph(AbstractMorph morph) {
      morph = MorphUtils.copy(morph);
      this.current.morph.setDirect(morph);
      this.morph.setMorph(morph);
   }

   public boolean needsBackground() {
      return false;
   }

   public ContentType getType() {
      return ContentType.HUDS;
   }

   public String getTitle() {
      return "mappet.gui.panels.huds";
   }

   public void fill(HUDScene data, boolean allowed) {
      super.fill(data, allowed);
      this.editor.setVisible(data != null);
      if (data != null) {
         this.stage.reset();
         this.stage.scenes.put(data.getId(), data);
         this.fov.setValue((double)data.fov);
         this.hide.toggled(data.hide);
         this.global.toggled(data.global);
         this.worldLighting.toggled(data.worldLighting);
         this.pickMorph((this.data).morphs.isEmpty() ? null : (HUDMorph)(this.data).morphs.get(0));
      }

   }

   private void pickMorph(HUDMorph current) {
      this.current = current;
      this.column.setVisible(current != null);
      this.transformations.setVisible(current != null);
      if (current != null) {
         this.morph.setMorph(current.morph.get());
         this.ortho.toggled(current.ortho);
         this.orthoX.setValue((double)current.orthoX);
         this.orthoY.setValue((double)current.orthoY);
         this.expire.setValue((double)current.expire);
         this.transformations.setMorph(current);
      }

   }

   public void appear() {
      super.appear();
      RenderingHandler.currentStage = this.stage;
   }

   public void disappear() {
      super.disappear();
      RenderingHandler.currentStage = null;
   }

   public void draw(GuiContext context) {
      if (this.editor.isVisible() && this.current != null && !this.current.morph.isEmpty()) {
         GuiDraw.drawTextBackground(this.font, this.current.morph.get().getDisplayName(), this.morphs.area.ex() + 3, this.morphs.area.my() - 4, 16777215, -2013265920, 2);
      }

      super.draw(context);
   }
}
