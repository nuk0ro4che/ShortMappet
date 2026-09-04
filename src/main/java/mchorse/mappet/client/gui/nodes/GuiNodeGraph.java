package mchorse.mappet.client.gui.nodes;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.vecmath.Vector2d;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.utils.factory.IFactory;
import mchorse.mappet.api.utils.nodes.Node;
import mchorse.mappet.api.utils.nodes.NodeRelation;
import mchorse.mappet.api.utils.nodes.NodeSystem;
import mchorse.mappet.api.utils.nodes.NodeUtils;
import mchorse.mappet.compat.client.LegacyGlStateManager;
import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.utils.GuiCanvas;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.Keybind;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.Interpolations;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.class_1074;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2522;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_310;
import net.minecraft.class_757;
import org.lwjgl.input.Keyboard;

public class GuiNodeGraph<T extends Node> extends GuiCanvas {
   public static final IKey KEYS_CATEGORY = IKey.lang("mappet.gui.nodes.keys.editor");
   public static final IKey ADD_CATEGORY = IKey.lang("mappet.gui.nodes.keys.add");
   public NodeSystem<T> system;
   private List<T> selected = new ArrayList();
   private boolean lastSelected;
   private boolean selecting;
   private int lastNodeX;
   private int lastNodeY;
   private T output;
   private T input;
   private Color a = new Color();
   private Color b = new Color();
   private boolean notifyAboutMain;
   private long tick;
   private int average;
   private int prevAverage;
   private Consumer<T> callback;

   public GuiNodeGraph(class_310 mc, IFactory<T> factory, Consumer<T> callback) {
      super(mc);
      this.callback = callback;
      this.context(() -> {
         GuiSimpleContextMenu menu = new GuiSimpleContextMenu(this.mc);
         int x = (int)this.fromX(GuiBase.getCurrent().mouseX);
         int y = (int)this.fromY(GuiBase.getCurrent().mouseY);
         menu.action(Icons.ADD, IKey.lang("mappet.gui.nodes.context.add"), () -> {
            GuiSimpleContextMenu adds = new GuiSimpleContextMenu(this.mc);

            for(String key : this.system.getFactory().getKeys()) {
               IKey label = IKey.format("mappet.gui.nodes.context.add_node", new Object[]{IKey.lang("mappet.gui.node_types." + key)});
               int color = this.system.getFactory().getColor(key);
               adds.action(Icons.ADD, label, () -> this.addNode(key, x, y), color);
            }

            GuiBase.getCurrent().replaceContextMenu(adds);
         });
         if (!this.selected.isEmpty()) {
            menu.action(Icons.COPY, IKey.lang("mappet.gui.nodes.context.copy"), this::copyNodes);
         }

         try {
            this.addPaste(menu, x, y);
         } catch (Exception var5) {
         }

         if (!this.selected.isEmpty()) {
            menu.action(Icons.DOWNLOAD, IKey.lang("mappet.gui.nodes.context.main"), this::markMain);
            menu.action(Icons.REVERSE, IKey.lang("mappet.gui.nodes.context.sort"), this::sortInputs);
            menu.action(Icons.MINIMIZE, IKey.lang("mappet.gui.nodes.context.tie"), this::tieSelected);
            menu.action(Icons.MAXIMIZE, IKey.lang("mappet.gui.nodes.context.untie"), this::untieSelected);
            menu.action(Icons.REMOVE, IKey.lang("mappet.gui.nodes.context.remove"), this::removeSelected, 16711731);
         }

         return menu;
      });
      this.keys().register(IKey.lang("mappet.gui.nodes.context.tie"), 33, this::tieSelected).inside().category(KEYS_CATEGORY);
      this.keys().register(IKey.lang("mappet.gui.nodes.context.untie"), 22, this::untieSelected).inside().category(KEYS_CATEGORY);
      this.keys().register(IKey.lang("mappet.gui.nodes.context.main"), 50, this::markMain).inside().category(KEYS_CATEGORY);
      this.keys().register(IKey.lang("mappet.gui.nodes.context.sort"), 46, this::sortInputs).inside().category(KEYS_CATEGORY);
      int keycode = 2;

      for(String key : factory.getKeys()) {
         Keybind keybind = this.keys().register(IKey.format("mappet.gui.nodes.context.add_node", new Object[]{IKey.lang("mappet.gui.node_types." + key)}), keycode, () -> {
            GuiContext context = GuiBase.getCurrent();
            this.addNode(key, (int)this.fromX(context.mouseX), (int)this.fromY(context.mouseY));
         });
         keybind.inside().held(new int[]{29}).category(ADD_CATEGORY);
         ++keycode;
      }

   }

   public GuiNodeGraph<T> notifyAboutMain() {
      this.notifyAboutMain = true;
      return this;
   }

   private void copyNodes() {
      class_2487 tag = new class_2487();
      class_2499 list = new class_2499();
      class_2487 relations = new class_2487();

      for(T node : this.selected) {
         class_2487 nodeTag = NodeUtils.nodeToNBT(this.system, node);
         list.add(nodeTag);

         for(T child : this.system.getChildren(node)) {
            if (this.selected.contains(child)) {
               String key = node.getId().toString();
               class_2499 relation;
               if (relations.method_10545(key)) {
                  relation = relations.method_10554(key, 8);
               } else {
                  relation = new class_2499();
                  relations.method_10566(key, relation);
               }

               relation.add(class_2519.method_23256(child.getId().toString()));
            }
         }
      }

      tag.method_10556("_CopyNodes", true);
      tag.method_10566("Nodes", list);
      tag.method_10566("Relations", relations);
      GuiUtils.setClipboardString(tag.toString());
   }

   private void addPaste(GuiSimpleContextMenu menu, int x, int y) throws CommandSyntaxException {
      String json = GuiUtils.getClipboardString();
      class_2487 tag = class_2522.method_10718(json);
      if (tag.method_10577("_CopyNodes")) {
         class_2499 nodesTag = tag.method_10554("Nodes", 10);
         class_2487 relationsTag = tag.method_10562("Relations");
         List<T> nodes = new ArrayList();
         Map<String, T> mapping = new HashMap();

         for(int i = 0; i < nodesTag.size(); ++i) {
            class_2487 nodeTag = nodesTag.method_10602(i);
            String id = nodeTag.method_10558("Id");
            nodeTag.method_10551("Id");
            T node = NodeUtils.nodeFromNBT(this.system, nodeTag);
            mapping.put(id, node);
            nodes.add(node);
         }

         int nx = ((Node)nodes.get(0)).x;
         int ny = ((Node)nodes.get(0)).y;
         menu.action(Icons.PASTE, IKey.lang("mappet.gui.nodes.context.paste"), () -> {
            this.selected.clear();

            for(T node : nodes) {
               this.system.add(node);
               node.x = node.x - nx + x;
               node.y = node.y - ny + y;
               this.select(node, true);
            }

            for(String key : relationsTag.method_10541()) {
               class_2499 relations = relationsTag.method_10554(key, 8);
               T output = (T)(mapping.get(key));

               for(class_2520 base : relations) {
                  T input = (T)(mapping.get(((class_2519)base).method_10714()));
                  if (output != null && input != null) {
                     this.system.tie(output, input);
                  }
               }
            }

         });
      }
   }

   private void addNode(String key, int x, int y) {
      T node = this.system.getFactory().create(key);
      if (node != null) {
         node.x = x;
         node.y = y;
         this.system.add(node);
         this.select(node);
      }

   }

   private void removeSelected() {
      for(T selected : this.selected) {
         this.system.remove(selected);
      }

      if (this.system.main != null && this.selected.contains(this.system.main)) {
         this.system.main = null;
      }

      this.select(null);
   }

   private void tieSelected() {
      if (this.selected.size() > 1) {
         T last = (T)(this.selected.get(this.selected.size() - 1));
         List<T> nodes = new ArrayList(this.selected);
         nodes.remove(last);
         nodes.sort(Comparator.comparingInt((a) -> a.x));

         for(T node : nodes) {
            this.system.tie(last, node);
         }

      }
   }

   private void untieSelected() {
      if (!this.selected.isEmpty()) {
         if (this.selected.size() == 1) {
            this.system.relations.remove(((Node)this.selected.get(0)).getId());
         } else if (this.selected.size() == 2) {
            T a = (T)(this.selected.get(0));
            T b = (T)(this.selected.get(1));
            this.system.untie(a, b);
            this.system.untie(b, a);
         } else {
            T last = (T)(this.selected.get(this.selected.size() - 1));

            for(int i = 0; i < this.selected.size() - 1; ++i) {
               this.system.untie(last, (T)this.selected.get(i));
            }
         }

      }
   }

   private void markMain() {
      if (!this.selected.isEmpty()) {
         this.system.main = (T)(this.selected.get(this.selected.size() - 1));
      }
   }

   private void sortInputs() {
      if (this.selected.size() == 1) {
         T node = (T)(this.selected.get(0));
         List<NodeRelation<T>> relations = (List)this.system.relations.get(node.getId());
         if (relations != null) {
            relations.sort(Comparator.comparingInt((a) -> a.input.x));
         }

      }
   }

   public void setNode(T node) {
      if (this.callback != null) {
         this.callback.accept(node);
      }

   }

   public void select(T node) {
      this.select(node, false);
   }

   public void select(T node, boolean add) {
      if (!add) {
         this.selected.clear();
      }

      if (node != null) {
         this.selected.add(node);
      }

      this.setNode(node);
   }

   public Area getNodeArea(T node) {
      int x1 = this.toX((double)(node.x - 60));
      int y1 = this.toY((double)(node.y - 35));
      int x2 = this.toX((double)(node.x + 60));
      int y2 = this.toY((double)(node.y + 35));
      Area.SHARED.setPoints(x1, y1, x2, y2);
      return Area.SHARED;
   }

   public Area getNodeOutletArea(Area nodeArea, boolean output) {
      int y = output ? 7 : -7;
      int x1 = nodeArea.mx() - 7;
      int y1 = nodeArea.y(output ? 1.0F : 0.0F) - 7 + y;
      int x2 = nodeArea.mx() + 7;
      int y2 = nodeArea.y(output ? 1.0F : 0.0F) + 7 + y;
      Area area = new Area();
      area.setPoints(x1, y1, x2, y2);
      return area;
   }

   public boolean isConnecting() {
      return this.output != null || this.input != null;
   }

   public boolean keyTyped(GuiContext context) {
      if (context.keyCode == 1 && this.isConnecting()) {
         this.output = this.input = null;
         this.dragging = false;
         this.lastSelected = false;
         this.selecting = false;
         return true;
      } else {
         return super.keyTyped(context);
      }
   }

   public void set(NodeSystem<T> system) {
      boolean same = this.system != null && system != null && this.system.getId().equals(system.getId());
      this.system = system;
      if (system != null && !same) {
         int x = system.main == null ? 0 : system.main.x;
         int y = system.main == null ? 0 : system.main.y;
         if (system.main == null && !system.nodes.isEmpty()) {
            for(T node : system.nodes.values()) {
               x += node.x;
               y += node.y;
            }

            x /= system.nodes.size();
            y /= system.nodes.size();
         }

         this.scaleX.setShift((double)x);
         this.scaleY.setShift((double)y);
         this.scaleX.setZoom((double)0.5F);
         this.scaleY.setZoom((double)0.5F);
      }

      if (same) {
         List<UUID> ids = (List)this.selected.stream().map(Node::getId).collect(Collectors.toList());
         this.selected.clear();

         for(UUID uuid : ids) {
            this.selected.add((T)this.system.nodes.get(uuid));
         }

         this.setNode(this.selected.isEmpty() ? null : (T)this.selected.get(this.selected.size() - 1));
      } else {
         this.selected.clear();
      }

   }

   public boolean mouseClicked(GuiContext context) {
      if (super.mouseClicked(context) && context.mouseButton == 2) {
         return true;
      } else if (this.system == null) {
         return false;
      } else {
         if (context.mouseButton == 0) {
            this.lastNodeX = (int)this.fromX(context.mouseX);
            this.lastNodeY = (int)this.fromY(context.mouseY);
            boolean shift = GuiUtils.isShiftKeyDown();
            List<T> nodes = new ArrayList(this.system.nodes.values());
            Collections.reverse(nodes);

            for(T node : nodes) {
               Area nodeArea = this.getNodeArea(node);
               Area outputArea = this.getNodeOutletArea(nodeArea, true);
               Area inputArea = this.getNodeOutletArea(nodeArea, false);
               if (outputArea.isInside(context)) {
                  if (this.input != null) {
                     if (this.input != node) {
                        this.system.tie(node, this.input);
                     }

                     this.output = this.input = null;
                  } else if (this.output != null && this.output != node && this.system.main != node) {
                     this.system.tie(this.output, node);
                     this.output = this.input = null;
                  } else {
                     this.output = node;
                  }

                  return true;
               }

               if (inputArea.isInside(context) && this.system.main != node) {
                  if (this.output != null) {
                     if (this.output != node) {
                        this.system.tie(this.output, node);
                     }

                     this.output = this.input = null;
                  } else {
                     this.input = node;
                  }

                  return true;
               }

               if (nodeArea.isInside(context)) {
                  if (shift) {
                     if (!this.selected.contains(node)) {
                        this.select(node, true);
                     } else {
                        this.selected.remove(node);
                        this.select(node, true);
                     }
                  } else if (!this.selected.contains(node)) {
                     this.select(node);
                  }

                  this.lastSelected = true;
                  return true;
               }
            }

            if (shift) {
               this.selecting = true;
            } else {
               this.select(null);
            }
         }

         return false;
      }
   }

   protected void startDragging(GuiContext context) {
      if (context.mouseButton == 0 && GuiUtils.isCtrlKeyDown()) {
         this.mouse = 2;
      }

      super.startDragging(context);
   }

   public void mouseReleased(GuiContext context) {
      super.mouseReleased(context);
      boolean connected = false;
      if (this.isConnecting()) {
         boolean output = this.output != null;

         for(T node : this.system.nodes.values()) {
            Area nodeArea = this.getNodeArea(node);
            Area expectedOutlet = this.getNodeOutletArea(nodeArea, !output);
            Area otherOutlet = this.getNodeOutletArea(nodeArea, output);
            T source = output ? this.output : this.input;
            boolean validTarget = node != source && (!output || this.system.main != node);
            if (validTarget && (expectedOutlet.isInside(context) || otherOutlet.isInside(context) || nodeArea.isInside(context))) {
               if (output) {
                  this.input = node;
               } else {
                  this.output = node;
               }

               connected = this.output != null && this.input != null && this.input != this.output;
               break;
            }
         }
      }

      if (this.selecting) {
         Area area = new Area();
         boolean wasSelected = !this.selected.isEmpty();
         area.setPoints(this.lastX, this.lastY, context.mouseX, context.mouseY);

         for(T node : this.system.nodes.values()) {
            Area nodeArea = this.getNodeArea(node);
            if (nodeArea.intersects(area) && !this.selected.contains(node)) {
               this.selected.add(0, node);
            }
         }

         if (!wasSelected && !this.selected.isEmpty()) {
            this.setNode((T)this.selected.get(this.selected.size() - 1));
         }
      } else if (connected) {
         this.system.tie(this.output, this.input);
      }

      this.lastSelected = false;
      this.selecting = false;
      boolean plainClick = Math.abs(context.mouseX - this.lastX) <= 3 && Math.abs(context.mouseY - this.lastY) <= 3;
      if (connected || !plainClick) {
         this.output = this.input = null;
      }

   }

   protected void dragging(GuiContext context) {
      super.dragging(context);
      if (this.dragging && this.mouse == 0 && this.lastSelected && !this.selected.isEmpty()) {
         int lastNodeX = (int)this.fromX(context.mouseX);
         int lastNodeY = (int)this.fromY(context.mouseY);

         for(T node : this.selected) {
            node.x += lastNodeX - this.lastNodeX;
            node.y += lastNodeY - this.lastNodeY;
         }

         this.lastNodeX = lastNodeX;
         this.lastNodeY = lastNodeY;
      }

   }

   public void draw(GuiContext context) {
      if (this.area.isInside(context) && !context.isFocused()) {
         float steps = this.prevAverage <= 0 ? 1.0F : (float)this.prevAverage;
         float step = 15.0F / steps;
         float x = Keyboard.isKeyDown(203) ? -step : (Keyboard.isKeyDown(205) ? step : 0.0F);
         float y = Keyboard.isKeyDown(200) ? -step : (Keyboard.isKeyDown(208) ? step : 0.0F);
         if (x != 0.0F) {
            this.scaleX.setShift((double)x / this.scaleX.getZoom() + this.scaleX.getShift());
         }

         if (y != 0.0F) {
            this.scaleY.setShift((double)y / this.scaleY.getZoom() + this.scaleY.getShift());
         }

         ++this.average;
         if (this.tick < context.tick) {
            this.tick = context.tick;
            this.prevAverage = this.average;
            this.average = 0;
         }
      }

      super.draw(context);
      if (this.system.nodes.isEmpty()) {
         int w = this.area.w / 2;
         LegacyGlStateManager.enableTexture2D();
         GuiDraw.drawMultiText(this.font, class_1074.method_4662("mappet.gui.nodes.info.empty_nodes", new Object[0]), this.area.mx(w), this.area.my(), 16777215, w, 12, 0.5F, 0.5F);
      } else if (this.notifyAboutMain && this.system.main == null) {
         String label = class_1074.method_4662("mappet.gui.nodes.info.empty_main", new Object[0]);
         int w = this.font.method_1727(label);
         GuiDraw.drawRect(this.area.x + 4, this.area.y + 4, this.area.x + 24 + w, this.area.y + 20, -2013265920);
         LegacyGlStateManager.color(1.0F, 0.0F, 0.1F, 1.0F);
         Icons.EXCLAMATION.render(this.area.x + 4, this.area.y + 4);
         GuiDraw.drawStringWithShadow(this.font, label, this.area.x + 20, this.area.y + 8, 16711696);
      }

   }

   protected void drawCanvas(GuiContext context) {
      super.drawCanvas(context);
      if (this.system != null) {
         int thickness = (Integer)Mappet.nodeThickness.get();
         LegacyGlStateManager.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShader(class_757::method_34540);
         LegacyGlStateManager.disableTexture2D();
         LegacyGlStateManager.shadeModel(7425);
         LegacyGlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         LegacyGlStateManager.glLineWidth((float)thickness);
         class_287 builder = class_289.method_1348().method_1349();
         T lastSelected = this.selected.isEmpty() ? null : (T)this.selected.get(this.selected.size() - 1);
         List<Vector2d> positions = new ArrayList();
         if (thickness > 0) {
            this.renderVisibleConnections(context, positions, lastSelected, thickness);
         }

         Area main = null;

         for(T node : this.system.nodes.values()) {
            Area nodeArea = this.getNodeArea(node);
            if (nodeArea.w > 25) {
               this.renderOutlets(context, node, nodeArea);
            }

            boolean hover = Area.SHARED.isInside(context);
            int index = this.selected.indexOf(node);
            int colorBg = hover ? -16250872 : -16777216;
            int colorFg = -1442840576 + this.system.getFactory().getColor(node);
            if (index >= 0) {
               int colorSh = index == this.selected.size() - 1 ? '裿' : 8874;
               GuiDraw.drawDropShadow(nodeArea.x + 4, nodeArea.y + 4, nodeArea.ex() - 4, nodeArea.ey() - 4, 8, -16777216 + colorSh, colorSh);
            }

            GuiDraw.drawRect(nodeArea.x + 1, nodeArea.y, nodeArea.ex() - 1, nodeArea.ey(), colorBg);
            GuiDraw.drawRect(nodeArea.x, nodeArea.y + 1, nodeArea.ex(), nodeArea.ey() - 1, colorBg);
            GuiDraw.drawOutline(nodeArea.x + 3, nodeArea.y + 3, nodeArea.ex() - 3, nodeArea.ey() - 3, colorFg);
            if (node == this.system.main) {
               main = new Area();
               main.copy(nodeArea);
            }
         }

         for(T node : this.system.nodes.values()) {
            Area nodeArea = this.getNodeArea(node);
            String title = node.getTitle();
            if (!title.isEmpty() && nodeArea.w > 40) {
               if (title.length() > 37) {
                  title = title.substring(0, 37) + "§r...";
               }

               GuiDraw.drawTextBackground(this.font, title, nodeArea.mx() - this.font.method_1727(title) / 2, nodeArea.my() - 4, 16777215, -2013265920);
            }
         }

         for(int i = 0; i < positions.size(); ++i) {
            Vector2d pos = (Vector2d)positions.get(i);
            String label = String.valueOf(i);
            GuiDraw.drawStringWithShadow(this.font, label, (int)pos.x - this.font.method_1727(label) / 2, (int)pos.y - 4, this.getIndexLabelColor(lastSelected, i));
         }

         if (main != null) {
            LegacyGlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GuiDraw.drawOutlinedIcon(Icons.DOWNLOAD, main.mx(), main.y - 4, -1, 0.5F, 1.0F);
         }

         LegacyGlStateManager.glLineWidth(1.0F);
         if (this.selecting) {
            GuiDraw.drawRect(this.lastX, this.lastY, context.mouseX, context.mouseY, 1140885759);
         }

      }
   }

   private void renderOutlets(GuiContext context, T node, Area nodeArea) {
      Area output = this.getNodeOutletArea(nodeArea, true);
      Area input = this.getNodeOutletArea(nodeArea, false);
      boolean insideO = output.isInside(context);
      boolean insideI = input.isInside(context);
      int colorO = ColorUtils.multiplyColor(16777215, insideO ? 1.0F : 0.6F);
      int colorI = ColorUtils.multiplyColor(16777215, insideI ? 1.0F : 0.6F);
      if (this.output == node) {
         colorO = 35071;
         if (insideI) {
            colorI = 16711731;
         }
      } else if (this.output != null) {
         if (insideO) {
            colorO = 16711731;
         } else if (insideI) {
            colorI = 65348;
         }
      }

      if (this.input == node) {
         colorI = 35071;
         if (insideO) {
            colorO = 16711731;
         }
      } else if (this.input != null) {
         if (insideI) {
            colorI = 16711731;
         } else if (insideO) {
            colorO = 65348;
         }
      }

      GuiDraw.drawOutline(output.x, output.y, output.ex(), output.ey(), -16777216 + colorO);
      if (this.system.main != node) {
         GuiDraw.drawOutline(input.x, input.y, input.ex(), input.ey(), -16777216 + colorI);
      }

   }

   private void renderConnections(GuiContext context, class_287 builder, List<Vector2d> positions, T lastSelected) {
      for(List<NodeRelation<T>> relations : this.system.relations.values()) {
         for(int r = 0; r < relations.size(); ++r) {
            NodeRelation<T> relation = (NodeRelation)relations.get(r);
            Area output = this.getNodeOutletArea(this.getNodeArea(relation.output), true);
            Area input = this.getNodeOutletArea(this.getNodeArea(relation.input), false);
            int x1 = input.mx();
            int y1 = input.my();
            int x2 = output.mx();
            int y2 = output.my();
            this.drawConnection(builder, context, relation.output, r, x1, y1, x2, y2, false);
            if (relation.output == lastSelected) {
               positions.add(new Vector2d((double)((float)(x1 + x2) / 2.0F), (double)((float)(y1 + y2) / 2.0F)));
            }
         }
      }

      if (this.isConnecting()) {
         T node = this.output == null ? this.input : this.output;
         Area area = this.getNodeArea(node);
         Area outlet = this.getNodeOutletArea(area, node == this.output);
         int x1 = context.mouseX;
         int y1 = context.mouseY;
         int x2 = outlet.mx();
         int y2 = outlet.my();
         List<NodeRelation<T>> list = (List)this.system.relations.get(node.getId());
         this.drawConnection(builder, context, node, list == null ? 0 : list.size(), x1, y1, x2, y2, true);
      }

   }

   private void renderVisibleConnections(GuiContext context, List<Vector2d> positions, T lastSelected, int thickness) {
      for(List<NodeRelation<T>> relations : this.system.relations.values()) {
         for(int r = 0; r < relations.size(); ++r) {
            NodeRelation<T> relation = (NodeRelation)relations.get(r);
            Area output = this.getNodeOutletArea(this.getNodeArea(relation.output), true);
            Area input = this.getNodeOutletArea(this.getNodeArea(relation.input), false);
            int x1 = output.mx();
            int y1 = output.my();
            int x2 = input.mx();
            int y2 = input.my();
            int alpha = (int)(MathUtils.clamp(this.getNodeActiveColorOpacity(relation.output, r), 0.0F, 1.0F) * 255.0F);
            int color = alpha << 24 | this.getNodeActiveColor(relation.output, r) & 16777215;
            this.drawGuiLine(x1, y1, x2, y2, color, thickness);
            if (relation.output == lastSelected) {
               positions.add(new Vector2d((double)((float)(x1 + x2) / 2.0F), (double)((float)(y1 + y2) / 2.0F)));
            }
         }
      }

      if (this.isConnecting()) {
         T node = this.output == null ? this.input : this.output;
         Area outlet = this.getNodeOutletArea(this.getNodeArea(node), node == this.output);
         int color = -16777216 | this.getNodeActiveColor(node, 0) & 16777215;
         this.drawGuiLine(outlet.mx(), outlet.my(), context.mouseX, context.mouseY, color, thickness);
      }

   }

   private void drawGuiLine(int x1, int y1, int x2, int y2, int color, int thickness) {
      int dx = x2 - x1;
      int dy = y2 - y1;
      int steps = Math.max(Math.abs(dx), Math.abs(dy));
      int size = Math.max(1, thickness);
      int half = size / 2;
      if (steps == 0) {
         GuiDraw.drawRect(x1 - half, y1 - half, x1 - half + size, y1 - half + size, color);
      } else {
         for(int i = 0; i <= steps; ++i) {
            int x = x1 + Math.round((float)dx * ((float)i / (float)steps));
            int y = y1 + Math.round((float)dy * ((float)i / (float)steps));
            GuiDraw.drawRect(x - half, y - half, x - half + size, y - half + size, color);
         }

      }
   }

   private void drawConnection(class_287 builder, GuiContext context, T node, int r, int x1, int y1, int x2, int y2, boolean forceLine) {
      float factor = ((float)context.tick + context.partialTicks) / 60.0F;
      float segments = 8.0F;
      float opacity = this.getNodeActiveColorOpacity(node, r);
      int c1 = (Boolean)Mappet.nodePulseBackgroundMcLibPrimary.get() ? (Integer)McLib.primaryColor.get() : (Integer)Mappet.nodePulseBackgroundColor.get();
      int c2 = this.getNodeActiveColor(node, r);

      for(int i = 0; (float)i < 8.0F; ++i) {
         float factor1 = (float)i / 8.0F;
         float factor2 = (float)(i + 1) / 8.0F;
         float color1 = 1.0F - MathUtils.clamp(Math.abs(1.0F - factor1 - factor % 1.0F) / 0.2F, 0.0F, 1.0F);
         float color2 = 1.0F - MathUtils.clamp(Math.abs(1.0F - factor2 - factor % 1.0F) / 0.2F, 0.0F, 1.0F);
         color1 = Math.max(color1, 1.0F - MathUtils.clamp(Math.abs(1.0F - factor1 + 1.0F - factor % 1.0F) / 0.2F, 0.0F, 1.0F));
         color2 = Math.max(color2, 1.0F - MathUtils.clamp(Math.abs(1.0F - factor2 + 1.0F - factor % 1.0F) / 0.2F, 0.0F, 1.0F));
         color1 = Math.max(color1, 1.0F - MathUtils.clamp(Math.abs(1.0F - factor1 - 1.0F - factor % 1.0F) / 0.2F, 0.0F, 1.0F));
         color2 = Math.max(color2, 1.0F - MathUtils.clamp(Math.abs(1.0F - factor2 - 1.0F - factor % 1.0F) / 0.2F, 0.0F, 1.0F));
         ColorUtils.interpolate(this.a, c1, c2, color1, false);
         ColorUtils.interpolate(this.b, c1, c2, color2, false);
         this.a.a = opacity;
         this.b.a = opacity;
         if (y2 > y1 && !forceLine) {
            if ((float)i == 4.0F) {
               builder.method_22912((double)Interpolations.lerp((float)x1, (float)x2, 0.5F), (double)y1, (double)0.0F).method_22915(this.a.r, this.a.g, this.a.b, this.a.a).method_1344();
               builder.method_22912((double)Interpolations.lerp((float)x1, (float)x2, 0.5F), (double)y2, (double)0.0F).method_22915(this.b.r, this.b.g, this.b.b, this.b.a).method_1344();
            } else {
               int y = (float)i < 4.0F ? y1 : y2;
               builder.method_22912((double)Interpolations.lerp((float)x1, (float)x2, (float)i == 5.0F ? 0.5F : factor1), (double)y, (double)0.0F).method_22915(this.a.r, this.a.g, this.a.b, this.a.a).method_1344();
               builder.method_22912((double)Interpolations.lerp((float)x1, (float)x2, (float)i == 3.0F ? 0.5F : factor2), (double)y, (double)0.0F).method_22915(this.b.r, this.b.g, this.b.b, this.b.a).method_1344();
            }
         } else {
            builder.method_22912((double)Interpolations.lerp((float)x1, (float)x2, factor1), (double)Interpolations.lerp((float)y1, (float)y2, factor1), (double)0.0F).method_22915(this.a.r, this.a.g, this.a.b, this.a.a).method_1344();
            builder.method_22912((double)Interpolations.lerp((float)x1, (float)x2, factor2), (double)Interpolations.lerp((float)y1, (float)y2, factor2), (double)0.0F).method_22915(this.b.r, this.b.g, this.b.b, this.b.a).method_1344();
         }
      }

   }

   protected int getIndexLabelColor(T lastSelected, int i) {
      return 16777215;
   }

   protected int getNodeActiveColor(T output, int r) {
      return 35071;
   }

   protected float getNodeActiveColorOpacity(T output, int r) {
      return 0.75F;
   }
}
