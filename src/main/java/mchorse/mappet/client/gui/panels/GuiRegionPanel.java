package mchorse.mappet.client.gui.panels;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import mchorse.mappet.api.regions.shapes.AbstractShape;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.regions.GuiRegionEditor;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.blocks.PacketEditRegion;
import mchorse.mappet.tile.TileRegion;
import mchorse.mappet.utils.ReflectionUtils;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDrawable;
import mchorse.mclib.client.gui.mclib.GuiDashboardPanel;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_1074;
import net.minecraft.class_2338;
import net.minecraft.class_2586;
import net.minecraft.class_310;

public class GuiRegionPanel extends GuiDashboardPanel<GuiMappetDashboard> {
   public static final IKey EMPTY = IKey.lang("mappet.gui.region.info.empty");
   public GuiIconElement toggleSidebar;
   public GuiElement sidebar;
   public GuiTileRegionListElement tiles;
   public GuiScrollElement editor;
   public GuiRegionEditor region;
   protected TileRegion tile;
   protected boolean wasOpened;

   public GuiRegionPanel(class_310 mc, GuiMappetDashboard dashboard) {
      super(mc, dashboard);
      this.sidebar = new GuiElement(mc);
      this.sidebar.flex().relative(this).x(1.0F).w(200).h(1.0F).anchorX(1.0F);
      this.toggleSidebar = new GuiIconElement(mc, Icons.RIGHTLOAD, (element) -> this.toggleSidebar());
      this.toggleSidebar.flex().relative(this.sidebar).x(-20);
      GuiDrawable drawable = new GuiDrawable((context) -> GuiDraw.drawStringWithShadow(this.font, class_1074.method_4662(this.getTitle(), new Object[0]), this.tiles.area.x, this.area.y + 10, 16777215));
      this.tiles = new GuiTileRegionListElement(mc, (list) -> this.fill((TileRegion)list.get(0), false));
      this.tiles.flex().relative(this.sidebar).xy(10, 25).w(1.0F, -20).h(1.0F, -35);
      this.sidebar.add(new IGuiElement[]{drawable, this.tiles});
      this.editor = new GuiScrollElement(mc);
      this.editor.markContainer();
      this.editor.flex().relative(this).w(240).h(1.0F).column(5).vertical().stretch().scroll().padding(10);
      this.region = new GuiRegionEditor(mc);
      this.editor.scroll.opposite = true;
      this.editor.add(this.region);
      this.add(new IGuiElement[]{this.sidebar, this.editor, this.toggleSidebar});
      this.keys().register(IKey.lang("mappet.gui.panels.keys.toggle_sidebar"), 49, () -> this.toggleSidebar.clickItself(GuiBase.getCurrent())).category(GuiMappetDashboardPanel.KEYS_CATEGORY);
      this.fill((TileRegion)null, true);
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

   public TileRegion getTile() {
      return this.tile;
   }

   public String getTitle() {
      return "mappet.gui.panels.regions";
   }

   public void fill(TileRegion tile, boolean ignoreSave) {
      if (!ignoreSave) {
         this.save();
      }

      if (tile != null && tile.method_11015()) {
         tile = null;
      }

      this.tile = tile;
      this.editor.setVisible(tile != null);
      this.tiles.setCurrentScroll(tile);
      if (tile != null) {
         this.region.set(tile.region);
      }

   }

   public void fillTiles(Collection<class_2586> tiles) {
      this.tiles.clear();
      if (tiles != null) {
         for(class_2586 tile : tiles) {
            if (tile instanceof TileRegion) {
               this.tiles.add((TileRegion)tile);
            }
         }

         this.tiles.setCurrentScroll(this.tile);
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
         this.fill((TileRegion)null, true);
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
         Dispatcher.sendToServer(new PacketEditRegion(this.tile.method_11016(), this.tile.region.serializeNBT()));
      }

   }

   public void draw(GuiContext context) {
      if (this.editor.isVisible()) {
         GuiDraw.drawRect(this.editor.area.x, this.editor.area.y, this.editor.area.mx(), this.editor.area.ey(), -1157627904);
         GuiDraw.drawHorizontalGradientRect(this.editor.area.mx(), this.editor.area.y, this.editor.area.x(1.25F), this.editor.area.ey(), -1157627904, 0);
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

   public static class GuiTileRegionListElement extends GuiListElement<TileRegion> {
      public GuiTileRegionListElement(class_310 mc, Consumer<List<TileRegion>> callback) {
         super(mc, callback);
      }

      protected String elementToString(TileRegion element) {
         class_2338 pos = element.method_11016();
         String first = element.region.shapes.isEmpty() ? "" : class_1074.method_4662("mappet.gui.shapes." + ((AbstractShape)element.region.shapes.get(0)).getType(), new Object[0]) + " ";
         return first + "(" + pos.method_10263() + ", " + pos.method_10264() + ", " + pos.method_10260() + ")";
      }
   }
}
