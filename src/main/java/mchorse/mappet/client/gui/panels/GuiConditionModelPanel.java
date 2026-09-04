package mchorse.mappet.client.gui.panels;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import mchorse.mappet.api.conditions.Checker;
import mchorse.mappet.api.conditions.blocks.AbstractConditionBlock;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.conditionModel.GuiConditionModelBasicSettingsElement;
import mchorse.mappet.client.gui.conditionModel.GuiConditionModelElement;
import mchorse.mappet.compat.NbtCompat;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.blocks.PacketEditConditionModel;
import mchorse.mappet.tile.TileConditionModel;
import mchorse.mappet.utils.ConditionModel;
import mchorse.mappet.utils.ReflectionUtils;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTransformations;
import mchorse.mclib.client.gui.framework.elements.input.GuiTransformations.GuiStaticTransformOrientation;
import mchorse.mclib.client.gui.framework.elements.input.GuiTransformations.TransformOrientation;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDrawable;
import mchorse.mclib.client.gui.mclib.GuiDashboardPanel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.MatrixUtils;
import mchorse.mclib.utils.MatrixUtils.RotationOrder;
import mchorse.mclib.utils.MatrixUtils.Transformation;
import net.minecraft.class_1074;
import net.minecraft.class_2338;
import net.minecraft.class_2586;
import net.minecraft.class_310;

public class GuiConditionModelPanel extends GuiDashboardPanel<GuiMappetDashboard> {
   public static final IKey EMPTY = IKey.lang("mappet.gui.conditionModel.info.empty");
   public GuiIconElement toggleSidebar;
   public GuiElement sidebar;
   public GuiTileConditionModelListElement tiles;
   public GuiScrollElement editor;
   public GuiConditionModelBasicSettingsElement basicSettings;
   protected TileConditionModel tile;
   protected boolean wasOpened;
   public GuiModelBlockTransformations trans;
   public GuiConditionModelElementsListElement conditionModelList;
   public GuiConditionModelElement conditionModelElement;

   public GuiConditionModelPanel(class_310 mc, GuiMappetDashboard dashboard) {
      super(mc, dashboard);
      this.sidebar = new GuiElement(mc);
      this.sidebar.flex().relative(this).x(1.0F).w(200).h(1.0F).anchorX(1.0F);
      this.toggleSidebar = new GuiIconElement(mc, Icons.RIGHTLOAD, (element) -> this.toggleSidebar());
      this.toggleSidebar.flex().relative(this.sidebar).x(-20);
      GuiDrawable drawable = new GuiDrawable((context) -> GuiDraw.drawStringWithShadow(this.font, class_1074.method_4662(this.getTitle(), new Object[0]), this.tiles.area.x, this.area.y + 10, 16777215));
      this.tiles = new GuiTileConditionModelListElement(mc, (list) -> this.fill((TileConditionModel)list.get(0), false));
      this.tiles.flex().relative(this.sidebar).xy(10, 25).w(1.0F, -20).h(1.0F, -35);
      this.sidebar.add(new IGuiElement[]{drawable, this.tiles});
      this.editor = new GuiScrollElement(mc);
      this.editor.markContainer();
      this.editor.flex().relative(this).w(240).h(1.0F).column(5).vertical().stretch().scroll().padding(10);
      this.basicSettings = new GuiConditionModelBasicSettingsElement(mc);
      this.editor.scroll.opposite = true;
      this.editor.add(this.basicSettings);
      this.editor.add(Elements.label(IKey.lang("mappet.gui.conditionModel.list")).marginTop(12));
      GuiElement row = Elements.row(mc, 5, new GuiElement[]{(new GuiIconElement(mc, Icons.ADD, (b) -> {
         ConditionModel model = new ConditionModel();
         this.tile.list.add(model);
         this.fillConditionBlockList(this.tile.list);
         this.conditionModelElement.set(model);
         this.conditionModelList.setCurrent(model);
      })).tooltip(IKey.lang("mappet.gui.conditionModel.add")), (new GuiIconElement(mc, Icons.REMOVE, (b) -> {
         this.tile.list.remove(this.conditionModelList.getCurrentFirst());
         this.fillConditionBlockList(this.tile.list);
         this.conditionModelElement.setVisible(false);
      })).tooltip(IKey.lang("mappet.gui.conditionModel.remove")), (new GuiIconElement(mc, Icons.MOVE_UP, (b) -> {
         ConditionModel current = (ConditionModel)this.conditionModelList.getCurrentFirst();
         List<ConditionModel> list = this.tile.list;
         int index = list.indexOf(current);
         if (index - 1 >= 0) {
            Collections.swap(list, index, index - 1);
         }

         this.fillConditionBlockList(this.tile.list);
         this.conditionModelList.setCurrent(current);
      })).tooltip(IKey.lang("mappet.gui.conditionModel.move_up")), (new GuiIconElement(mc, Icons.MOVE_DOWN, (b) -> {
         ConditionModel current = (ConditionModel)this.conditionModelList.getCurrentFirst();
         List<ConditionModel> list = this.tile.list;
         int index = list.indexOf(current);
         if (index + 1 < list.size()) {
            Collections.swap(list, index, index + 1);
         }

         this.fillConditionBlockList(list);
         this.conditionModelList.setCurrent(current);
      })).tooltip(IKey.lang("mappet.gui.conditionModel.move_down"))});
      row.flex().row(5).width(50);
      this.editor.add(row);
      this.conditionModelList = new GuiConditionModelElementsListElement(mc, (list) -> this.setBasicSettings((ConditionModel)list.get(0)));
      this.conditionModelList.flex().relative(this).h(350);
      this.editor.add(this.conditionModelList);
      this.conditionModelElement = new GuiConditionModelElement(mc);
      this.conditionModelElement.flex().relative(this).anchorY(0.5F).anchorX(0.5F).x(0.5F).y(0.5F, -30).wh(290, 290);
      this.add(this.conditionModelElement);
      this.trans = new GuiModelBlockTransformations(mc);
      this.trans.flex().relative(this).x(0.5F, -32).y(1.0F, -10).wh(250, 70).anchor(0.5F, 1.0F);
      this.trans.model = this.tile;
      this.trans.set(this.tile);
      this.add(new IGuiElement[]{this.sidebar, this.editor, this.toggleSidebar, this.trans});
      this.keys().register(IKey.lang("mappet.gui.panels.keys.toggle_sidebar"), 49, () -> this.toggleSidebar.clickItself(GuiBase.getCurrent())).category(GuiMappetDashboardPanel.KEYS_CATEGORY);
      this.fill((TileConditionModel)null, true);
   }

   private void toggleSidebar() {
      this.sidebar.toggleVisible();
      this.toggleSidebar.both(this.sidebar.isVisible() ? Icons.RIGHTLOAD : Icons.LEFTLOAD);
      if (this.sidebar.isVisible()) {
         this.toggleSidebar.flex().relative(this.sidebar).x(-20);
      } else {
         this.toggleSidebar.flex().relative(this).x(1.0F, -20);
      }

      this.resize();
   }

   public TileConditionModel getTile() {
      return this.tile;
   }

   public String getTitle() {
      return "mappet.gui.panels.condition_models";
   }

   public boolean isOpened() {
      return this.wasOpened;
   }

   public boolean isSelected(TileConditionModel tileEntityModel) {
      return this.tile == tileEntityModel;
   }

   public void setBasicSettings(ConditionModel conditionModel) {
      if (conditionModel != null && !conditionModel.equals(this.conditionModelElement.conditionModel)) {
         this.conditionModelElement.set(conditionModel);
      }

      this.conditionModelElement.setVisible(conditionModel != null);
   }

   public void fill(TileConditionModel tile, boolean ignoreSave) {
      if (!ignoreSave) {
         this.save();
      }

      if (tile != null && tile.method_11015()) {
         tile = null;
      }

      this.tile = tile;
      this.editor.setVisible(tile != null);
      this.trans.setVisible(tile != null);
      this.conditionModelElement.setVisible(tile != null && this.conditionModelList.getCurrentFirst() != null);
      this.tiles.setCurrentScroll(tile);
      if (tile != null) {
         this.trans.set(tile);
         this.basicSettings.set(tile);
         this.fillConditionBlockList(tile.list);
      }

   }

   public void fillConditionBlockList(List<ConditionModel> list) {
      this.conditionModelList.clear();

      for(ConditionModel model : list) {
         this.conditionModelList.add(model);
      }

   }

   public void fillTiles(Collection<class_2586> tiles) {
      this.tiles.clear();
      if (tiles != null) {
         for(class_2586 tile : tiles) {
            if (tile instanceof TileConditionModel) {
               this.tiles.add((TileConditionModel)tile);
            }
         }

         this.tiles.setCurrentScroll(this.tile);
         this.setBasicSettings((ConditionModel)null);
      }
   }

   public boolean needsBackground() {
      return false;
   }

   public void open() {
      super.open();
      this.fillTiles(ReflectionUtils.getGlobalTiles(this.mc.field_1769));
   }

   public void appear() {
      super.appear();
      if (this.tile != null && this.tile.method_11015()) {
         this.fill((TileConditionModel)null, true);
      }

      this.wasOpened = true;
   }

   public void close() {
      super.close();
      this.save();
      this.wasOpened = false;
   }

   private void save() {
      if (this.tile != null && !this.tile.method_11015() && this.wasOpened) {
         this.tile.method_5431();
         Dispatcher.sendToServer(new PacketEditConditionModel(this.tile.method_11016(), NbtCompat.write((class_2586)this.tile)));
      }

   }

   public void draw(GuiContext context) {
      if (this.editor.isVisible()) {
         GuiDraw.drawRect(this.editor.area.x, this.editor.area.y, this.editor.area.mx(), this.editor.area.ey(), -1157627904);
         GuiDraw.drawHorizontalGradientRect(this.editor.area.mx(), this.editor.area.y, this.editor.area.x(1.25F), this.editor.area.ey(), -1157627904, 0);
         GuiDraw.drawRect(this.conditionModelList.area.x, this.conditionModelList.area.y, this.conditionModelList.area.ex(), this.conditionModelList.area.ey(), -1157627904);
      }

      if (this.conditionModelElement.isVisible()) {
         this.conditionModelElement.area.draw(-587202560);
      }

      if (this.sidebar.isVisible()) {
         this.sidebar.area.draw(-587202560);
      }

      super.draw(context);
      if (!this.editor.isVisible()) {
         int w = (this.sidebar.isVisible() ? this.sidebar.area.x - this.area.x : this.area.w) / 2;
         int x = this.area.x + w / 2;
         GuiDraw.drawMultiText(this.font, EMPTY.get(), x, this.area.my(), 16777215, w, 12, 0.5F, 0.5F);
      }

   }

   public static class GuiTileConditionModelListElement extends GuiListElement<TileConditionModel> {
      public GuiTileConditionModelListElement(class_310 mc, Consumer<List<TileConditionModel>> callback) {
         super(mc, callback);
      }

      protected String elementToString(TileConditionModel element) {
         class_2338 pos = element.method_11016();
         String first = "(" + element.list.size() + ") ";
         return first + "(" + pos.method_10263() + ", " + pos.method_10264() + ", " + pos.method_10260() + ")";
      }
   }

   public static class GuiConditionModelElementsListElement extends GuiListElement<ConditionModel> {
      public GuiConditionModelElementsListElement(class_310 mc, Consumer<List<ConditionModel>> callback) {
         super(mc, callback);
      }

      protected String elementToString(ConditionModel element) {
         Checker checker = element.checker;
         if (checker.isEmpty()) {
            return "-";
         } else {
            String string;
            if (checker.mode == Checker.Mode.CONDITION) {
               int size = checker.condition.blocks.size();
               String var10000 = ((AbstractConditionBlock)checker.condition.blocks.get(0)).stringify();
               string = var10000 + (size > 1 ? " (" + (size - 1) + "+)" : "");
            } else {
               string = checker.expression;
            }

            return string;
         }
      }
   }

   public static class GuiModelBlockTransformations extends GuiTransformations {
      public TileConditionModel model;

      public GuiModelBlockTransformations(class_310 mc) {
         super(mc);
         this.one.callback = (toggle) -> {
            boolean one = toggle.isToggled();
            this.model.getSettings().setUniform(one);
            this.updateScaleFields();
            if (!one) {
               this.sy.setValueAndNotify(this.sx.value);
               this.sz.setValueAndNotify(this.sx.value);
            }

         };
      }

      public void set(TileConditionModel model) {
         this.model = model;
         if (model != null) {
            this.fillT((double)model.getSettings().getX(), (double)model.getSettings().getY(), (double)model.getSettings().getZ());
            this.fillS((double)model.getSettings().getSx(), (double)model.getSettings().getSy(), (double)model.getSettings().getSz());
            this.fillR((double)model.getSettings().getRx(), (double)model.getSettings().getRy(), (double)model.getSettings().getRz());
            this.one.toggled(model.getSettings().isUniform());
            this.updateScaleFields();
         }

      }

      public void setT(double x, double y, double z) {
         this.model.getSettings().setX((float)x);
         this.model.getSettings().setY((float)y);
         this.model.getSettings().setZ((float)z);
      }

      public void setS(double x, double y, double z) {
         this.model.getSettings().setSx((float)x);
         this.model.getSettings().setSy((float)y);
         this.model.getSettings().setSz((float)z);
      }

      public void setR(double x, double y, double z) {
         this.model.getSettings().setRx((float)x);
         this.model.getSettings().setRy((float)y);
         this.model.getSettings().setRz((float)z);
      }

      protected void localTranslate(double x, double y, double z) {
         Vector4f translation = new Vector4f((float)x, (float)y, (float)z, 1.0F);
         if (GuiStaticTransformOrientation.getOrientation() == TransformOrientation.LOCAL) {
            float rx = (float)Math.toRadians((double)this.model.getSettings().getRx());
            float ry = (float)Math.toRadians((double)this.model.getSettings().getRy());
            float rz = (float)Math.toRadians((double)this.model.getSettings().getRz());
            MatrixUtils.getRotationMatrix(rx, ry, rz, RotationOrder.valueOf(this.model.getSettings().getOrder().toString())).transform(translation);
         }

         this.model.getSettings().setX(this.model.getSettings().getX() + translation.x);
         this.model.getSettings().setY(this.model.getSettings().getY() + translation.y);
         this.model.getSettings().setZ(this.model.getSettings().getZ() + translation.z);
         this.fillT((double)this.model.getSettings().getX(), (double)this.model.getSettings().getY(), (double)this.model.getSettings().getZ());
      }

      protected void prepareRotation(Matrix4f mat) {
         MatrixUtils.RotationOrder order = RotationOrder.valueOf(this.model.getSettings().getOrder().toString());
         float[] rot = new float[]{(float)this.rx.value, (float)this.ry.value, (float)this.rz.value};
         Matrix4f trans = new Matrix4f();
         trans.setIdentity();
         trans.set(Transformation.getRotationMatrix(order.thirdIndex, (double)rot[order.thirdIndex]));
         mat.mul(trans);
         trans.set(Transformation.getRotationMatrix(order.secondIndex, (double)rot[order.secondIndex]));
         mat.mul(trans);
         trans.set(Transformation.getRotationMatrix(order.firstIndex, (double)rot[order.firstIndex]));
         mat.mul(trans);
      }

      protected void postRotation(MatrixUtils.Transformation transform) {
         Vector3f result = transform.getRotation(RotationOrder.valueOf(this.model.getSettings().getOrder().toString()), new Vector3f((float)this.rx.value, (float)this.ry.value, (float)this.rz.value));
         this.rx.setValueAndNotify((double)result.x);
         this.ry.setValueAndNotify((double)result.y);
         this.rz.setValueAndNotify((double)result.z);
      }
   }
}
