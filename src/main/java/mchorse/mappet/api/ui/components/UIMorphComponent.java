package mchorse.mappet.api.ui.components;

import java.text.DecimalFormat;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.utils.GuiMorphRenderer;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.NBTUtils;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_310;

public class UIMorphComponent extends UIComponent {
   public class_2487 morph;
   public boolean editing;
   public Vector3f pos;
   public Vector2f rot;
   public float distance = 2.0F;
   public float fov = 70.0F;

   public UIMorphComponent morph(AbstractMorph morph) {
      this.change(new String[]{"Morph"});
      this.morph = MorphUtils.toNBT(morph);
      return this;
   }

   public UIMorphComponent editing() {
      return this.editing(true);
   }

   public UIMorphComponent editing(boolean editing) {
      this.change(new String[]{"Editing"});
      this.editing = editing;
      return this;
   }

   public UIMorphComponent position(float x, float y, float z) {
      this.change(new String[]{"Position"});
      this.pos = new Vector3f(x, y, z);
      return this;
   }

   public UIMorphComponent rotation(float pitch, float yaw) {
      this.change(new String[]{"Rotation"});
      this.rot = new Vector2f(pitch, yaw);
      return this;
   }

   public UIMorphComponent distance(float distance) {
      this.change(new String[]{"Distance"});
      this.distance = distance;
      return this;
   }

   public UIMorphComponent fov(float fov) {
      this.change(new String[]{"Fov"});
      this.fov = fov;
      return this;
   }

   @DiscardMethod
   protected int getDefaultUpdateDelay() {
      return 200;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected void applyProperty(UIContext context, String key, GuiElement element) {
      super.applyProperty(context, key, element);
      GuiMorphRenderer renderer = (GuiMorphRenderer)element;
      if (key.equals("Morph")) {
         renderer.morph.set(MorphManager.INSTANCE.morphFromNBT(this.morph));
      } else if (key.equals("Editing")) {
         ((GuiNestedEdit)renderer.getChildren(GuiNestedEdit.class).get(0)).setVisible(this.editing);
      } else if (key.equals("Position") && this.pos != null) {
         renderer.setPosition(this.pos.x, this.pos.y, this.pos.z);
      } else if (key.equals("Rotation") && this.rot != null) {
         renderer.setRotation(this.rot.y, this.rot.x);
      } else if (key.equals("Distance")) {
         renderer.scale = this.distance;
      } else if (key.equals("Fov")) {
         renderer.fov = this.fov;
      }

   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public GuiElement create(class_310 mc, UIContext context) {
      GuiMorphRenderer renderer = new GuiMorphRenderer(mc);
      if (this.morph != null) {
         renderer.morph.set(MorphManager.INSTANCE.morphFromNBT(this.morph));
      }

      GuiNestedEdit edit = new GuiNestedEdit(mc, (editing) -> GuiMappetDashboard.get(mc).openMorphMenu(renderer.getRoot(), editing, renderer.morph.copy(), (morph) -> {
            if (!this.id.isEmpty()) {
               AbstractMorph copy = MorphUtils.copy(morph);
               class_2487 copyTag = MorphUtils.toNBT(copy);
               renderer.morph.setDirect(copy);
               context.data.method_10566(this.id, copyTag == null ? new class_2487() : copyTag);
               context.dirty(this.id, (long)this.updateDelay);
            }
         }));
      edit.flex().relative(renderer).x(0.5F).y(1.0F, -30).wh(100, 20).anchorX(0.5F);
      edit.setVisible(this.editing);
      renderer.add(edit);
      if (this.pos != null) {
         renderer.setPosition(this.pos.x, this.pos.y, this.pos.z);
      }

      if (this.rot != null) {
         renderer.setRotation(this.rot.y, this.rot.x);
      }

      renderer.scale = this.distance;
      renderer.fov = this.fov;
      return this.apply(renderer, context);
   }

   @Environment(EnvType.CLIENT)
   protected void resetContext(GuiElement element, UIContext context) {
      GuiMorphRenderer renderer = (GuiMorphRenderer)element;
      renderer.context(() -> {
         GuiSimpleContextMenu menu = new GuiSimpleContextMenu(class_310.method_1551());
         if ((Boolean)Mappet.scriptUIDebug.get()) {
            menu.action(Icons.SEARCH, IKey.lang("mappet.gui.context.copy_camera"), () -> this.copyCameraProperties(renderer));
         }

         return menu;
      });
   }

   @Environment(EnvType.CLIENT)
   protected void createContext(GuiSimpleContextMenu menu, GuiElement element, UIContext context) {
      if ((Boolean)Mappet.scriptUIDebug.get()) {
         GuiMorphRenderer renderer = (GuiMorphRenderer)element;
         menu.action(Icons.SEARCH, IKey.lang("mappet.gui.context.copy_camera"), () -> this.copyCameraProperties(renderer));
      }

      super.createContext(menu, element, context);
   }

   @Environment(EnvType.CLIENT)
   private void copyCameraProperties(GuiMorphRenderer renderer) {
      DecimalFormat formatter = GuiTrackpadElement.FORMAT;
      String var10000 = formatter.format((double)renderer.pos.x);
      GuiUtils.setClipboardString(".position(" + var10000 + ", " + formatter.format((double)renderer.pos.y) + ", " + formatter.format((double)renderer.pos.z) + ").rotation(" + formatter.format((double)renderer.pitch) + ", " + formatter.format((double)renderer.yaw) + ").distance(" + formatter.format((double)renderer.scale) + ").fov(" + formatter.format((double)renderer.fov) + ")");
   }

   @DiscardMethod
   public void populateData(class_2487 tag) {
      super.populateData(tag);
      if (!this.id.isEmpty() && this.morph != null) {
         tag.method_10566(this.id, this.morph.method_10553());
      }

   }

   @DiscardMethod
   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      if (this.morph != null) {
         tag.method_10566("Morph", this.morph);
      }

      tag.method_10556("Editing", this.editing);
      if (this.pos != null) {
         class_2499 pos = new class_2499();
         NBTUtils.writeFloatList(pos, this.pos);
         tag.method_10566("Position", pos);
      }

      if (this.rot != null) {
         class_2499 rot = new class_2499();
         Vector3f rotation = new Vector3f(this.rot.x, this.rot.y, 0.0F);
         NBTUtils.writeFloatList(rot, rotation);
         tag.method_10566("Rotation", rot);
      }

      tag.method_10548("Distance", this.distance);
      tag.method_10548("Fov", this.fov);
   }

   @DiscardMethod
   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Morph")) {
         this.morph = tag.method_10562("Morph");
      }

      if (tag.method_10545("Editing")) {
         this.editing = tag.method_10577("Editing");
      }

      if (tag.method_10545("Position")) {
         Vector3f position = new Vector3f();
         NBTUtils.readFloatList(tag.method_10554("Position", 5), position);
         this.pos = position;
      }

      if (tag.method_10545("Rotation")) {
         Vector3f rotation = new Vector3f();
         NBTUtils.readFloatList(tag.method_10554("Rotation", 5), rotation);
         this.rot = new Vector2f(rotation.x, rotation.y);
      }

      if (tag.method_10545("Distance")) {
         this.distance = tag.method_10583("Distance");
      }

      if (tag.method_10545("Fov")) {
         this.fov = tag.method_10583("Fov");
      }

   }
}
