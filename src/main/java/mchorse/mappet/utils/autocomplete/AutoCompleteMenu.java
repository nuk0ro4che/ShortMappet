package mchorse.mappet.utils.autocomplete;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import mchorse.mappet.utils.MPIcons;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.utils.Icons;
import net.minecraft.class_310;
import net.minecraft.class_327;

public class AutoCompleteMenu extends GuiContextMenu {
   private static final int ITEM_HEIGHT = 20;
   private static final int MENU_BACKGROUND = 0xff161719;
   private static final int MENU_BORDER = 0xff34363a;
   private static final int MENU_SHADOW = 0x99000000;
   private static final int MENU_OUTLINE = 0xff0a0b0d;
   private static final int MENU_ACCENT = 0xff9f3338;
   private static final int ROW_HOVER = 0xff222428;
   private static final int ROW_SELECTED = 0xff302022;
   private static final int ROW_SELECTED_ACCENT = 0xffd14b50;
   private static final int TEXT_NORMAL = 0xffd6d7d9;
   private static final int TEXT_SELECTED = 0xFFFFFFFF;
   private static final int TEXT_METADATA = 0xff8f8f8f;
   public final SuggestionListElement list;
   private final GuiElement docContainer;
   private final GuiScrollElement docScroll;
   private AutoCompleteConfig.Suggestion lastDocSuggestion = null;
   private boolean docContainerAdded = false;
   private int docX;
   private int docY;
   private int docW;
   private int docH;

   public AutoCompleteMenu(class_310 mc, List<AutoCompleteConfig.Suggestion> suggestions, Consumer<AutoCompleteConfig.Suggestion> onSelect) {
      super(mc);
      this.list = new SuggestionListElement(mc, (picked) -> {
         if (picked != null && !picked.isEmpty()) {
            onSelect.accept(picked.get(0));
            this.removeFromParent();
         }

      });
      this.list.scroll.scrollItemSize = ITEM_HEIGHT;
      this.list.setList(suggestions);
      this.list.background(MENU_BACKGROUND);
      this.list.flex().relative(this).w(1.0F).h(1.0F);
      this.add(this.list);
      this.docContainer = new GuiElement(mc) {
         public void draw(GuiContext ctx) {
            GuiDraw.drawRect(this.area.x - 3, this.area.y - 3, this.area.x + this.area.w + 3, this.area.y + this.area.h + 3, MENU_OUTLINE);
            GuiDraw.drawRect(this.area.x - 2, this.area.y - 2, this.area.x + this.area.w + 2, this.area.y + this.area.h + 2, MENU_ACCENT);
            GuiDraw.drawRect(this.area.x - 1, this.area.y - 1, this.area.x + this.area.w + 1, this.area.y + this.area.h + 1, MENU_SHADOW);
            GuiDraw.drawRect(this.area.x - 1, this.area.y - 1, this.area.x + this.area.w + 1, this.area.y + this.area.h + 1, MENU_BORDER);
            GuiDraw.drawRect(this.area.x, this.area.y, this.area.x + this.area.w, this.area.y + this.area.h, MENU_BACKGROUND);
            super.draw(ctx);
         }
      };
      this.docScroll = new GuiScrollElement(mc);
      this.docScroll.flex().relative(this.docContainer).w(1.0F).h(1.0F).column(4).vertical().stretch().scroll().padding(7);
      this.docContainer.add(this.docScroll);
      this.docContainer.flex().set(0.0F, 0.0F, 0.0F, 0.0F);
   }

   public void refreshList(List<AutoCompleteConfig.Suggestion> suggestions) {
      this.list.setList(suggestions);
      this.clearDoc();
   }

   public void placeAt(GuiContext context, int cursorX, int cursorY) {
      class_327 font = this.mc.field_1772;
      int menuW = Config.getMenuWidth();
      if (menuW <= 0) {
         menuW = 180;

         for(AutoCompleteConfig.Suggestion s : this.list.getList()) {
            int iconOffset = "icon".equals(s.className) ? 20 : 24;
            int sw = font.method_1727(getSuggestionDisplay(s)) + font.method_1727(getSuggestionTail(s)) + iconOffset + 20;
            if (sw > menuW) {
               menuW = sw;
            }
         }
      }

      int listSize = this.list.getList().size();
      int maxVisible = Math.min(listSize, Config.getContextMenuMaxItems());
      int menuH = Config.getMenuHeight();
      if (menuH <= 0) {
         menuH = maxVisible * ITEM_HEIGHT;
      }

      int screenW = context.screen.root.area.w;
      int screenH = context.screen.root.area.h;
      int offsetX = Config.getMenuOffsetX();
      int offsetY = Config.getMenuOffsetY();
      int x = cursorX + 2 + offsetX;
      int y = cursorY + 2 + offsetY;
      if (y + menuH > screenH - 5) {
         y = cursorY - menuH - 2 + offsetY;
      }

      if (x + menuW > screenW - 5) {
         x = screenW - 5 - menuW;
      }

      if (x < 5) {
         x = 5;
      }

      if (y < 5) {
         y = 5;
      }

      this.flex().set((float)x, (float)y, (float)menuW, (float)menuH);
      this.resize();
      if (!this.list.getList().isEmpty()) {
         this.list.setIndex(0);
      }

   }

   public void moveSelection(int delta) {
      int next = this.list.getIndex() + delta;
      int size = this.list.getList().size();
      if (next < 0) {
         next = 0;
      }

      if (next >= size) {
         next = size - 1;
      }

      if (next >= 0) {
         this.list.setIndex(next);
         this.list.scroll.scrollIntoView(next * ITEM_HEIGHT);
      }

      this.lastDocSuggestion = null;
   }

   public AutoCompleteConfig.Suggestion getSelected() {
      return (AutoCompleteConfig.Suggestion)this.list.getCurrentFirst();
   }

   public boolean isInsideDocTooltip(int mx, int my) {
      if (this.docContainer.area.w <= 0) {
         return false;
      } else {
         return mx >= this.docX && mx <= this.docX + this.docW && my >= this.docY && my <= this.docY + this.docH;
      }
   }

   public boolean scrollTooltip(int direction) {
      if (this.docContainer.area.w <= 0) {
         return false;
      } else {
         int step = 14;
         int cur = this.docScroll.scroll.scroll;
         int maxVal = Math.max(0, this.docScroll.scroll.scrollSize - this.docScroll.area.h);
         this.docScroll.scroll.scroll = Math.max(0, Math.min(cur + direction * step, maxVal));
         return true;
      }
   }

   public void removeFromParent() {
      if (this.docContainer.hasParent()) {
         this.docContainer.removeFromParent();
      }

      this.docContainerAdded = false;
      this.clearDoc();
      super.removeFromParent();
   }

   public void draw(GuiContext context) {
      GuiDraw.drawRect(this.area.x - 3, this.area.y - 3, this.area.x + this.area.w + 3, this.area.y + this.area.h + 3, MENU_OUTLINE);
      GuiDraw.drawRect(this.area.x - 2, this.area.y - 2, this.area.x + this.area.w + 2, this.area.y + this.area.h + 2, MENU_ACCENT);
      GuiDraw.drawRect(this.area.x - 1, this.area.y - 1, this.area.x + this.area.w + 1, this.area.y + this.area.h + 1, MENU_SHADOW);
      GuiDraw.drawRect(this.area.x - 1, this.area.y - 1, this.area.x + this.area.w + 1, this.area.y + this.area.h + 1, MENU_BORDER);
      GuiDraw.drawRect(this.area.x, this.area.y, this.area.x + this.area.w, this.area.y + this.area.h, MENU_BACKGROUND);
      super.draw(context);
      this.updateDoc(context);
   }

   private void clearDoc() {
      this.docScroll.removeAll();
      this.docContainer.flex().set(0.0F, 0.0F, 0.0F, 0.0F);
      this.docContainer.resize();
      this.lastDocSuggestion = null;
   }

   private void updateDoc(GuiContext context) {
      if (!Config.isDocPanelEnabled()) {
         if (this.docContainer.area.w > 0) {
            this.docContainer.flex().set(0.0F, 0.0F, 0.0F, 0.0F);
            this.docContainer.resize();
         }

      } else if (!this.docContainerAdded) {
         this.scheduleDocContainerAdd(context);
      } else {
         AutoCompleteConfig.Suggestion sel = this.getSelected();
         if (sel == null) {
            this.clearDoc();
         } else if (sel != this.lastDocSuggestion) {
            this.lastDocSuggestion = sel;
            int tipW = Config.getDocPanelWidth();
            int tipH = Config.getDocPanelHeight();
            int screenW = context.screen.root.area.w;
            int screenH = context.screen.root.area.h;
            this.docScroll.scroll.scrollTo(0);
            this.docScroll.removeAll();
            this.docScroll.area.set(0, 0, tipW, tipH);
            AutoCompleteEngine.populateDocPanel(this.mc, sel, this.docScroll);
            int contentH = this.docScroll.scroll.scrollSize;
            if (contentH <= 0) {
               this.docContainer.flex().set(0.0F, 0.0F, 0.0F, 0.0F);
               this.docContainer.resize();
            } else {
               int actualH = Math.min(contentH + 14, tipH);
               int tx = this.area.x + this.area.w + 6;
               int ty;
               if (tx + tipW <= screenW - 2) {
                  ty = this.area.y;
                  if (ty + actualH > screenH - 2) {
                     ty = Math.max(2, screenH - 2 - actualH);
                  }
               } else {
                  tx = this.area.x;
                  if (tx + tipW > screenW - 2) {
                     tx = Math.max(2, screenW - 2 - tipW);
                  }

                  ty = this.area.y + this.area.h + 4;
                  if (ty + actualH > screenH - 2) {
                     ty = this.area.y - actualH - 4;
                  }

                  if (ty < 2) {
                     ty = 2;
                  }
               }

               this.docX = tx;
               this.docY = ty;
               this.docW = tipW;
               this.docH = actualH;
               this.docContainer.flex().set((float)tx, (float)ty, (float)tipW, (float)actualH);
               this.docContainer.resize();
            }
         }
      }
   }

   private void scheduleDocContainerAdd(GuiContext context) {
      try {
         Field f = context.getClass().getDeclaredField("postRenderCallbacks");
         f.setAccessible(true);
         List<Consumer<GuiContext>> callbacks = (List)f.get(context);
         callbacks.add((GuiContext ctx) -> {
            if (!this.docContainer.hasParent()) {
               ctx.screen.root.add(this.docContainer);
            }

            this.docContainerAdded = true;
         });
      } catch (Exception var4) {
         if (!this.docContainer.hasParent()) {
            context.screen.root.add(this.docContainer);
         }

         this.docContainerAdded = true;
      }

   }

   static String getSuggestionDisplay(AutoCompleteConfig.Suggestion s) {
      if (s == null) {
         return "";
      }

      String type = s.className != null ? s.className : "";
      if ("fn".equals(type)) {
         String params = s.returnType != null && !s.returnType.isEmpty() ? s.returnType : "()";
         return s.methodName + (params.startsWith("(") ? params : "(" + params + ")");
      }

      if ("alias".equals(type) || "var".equals(type) || "[]".equals(type) || "{}".equals(type) || "kw".equals(type) || "java".equals(type) || "icon".equals(type) || "hud".equals(type) || "action".equals(type)) {
         if ("alias".equals(type)) {
            switch (s.methodName) {
               case "hotbar":
                  return "Hotbar";
               case "health":
                  return "Health";
               case "hunger":
                  return "Hunger";
               case "experience":
                  return "Experience";
               case "crosshair":
                  return "Crosshair";
               case "statusEffects":
                  return "StatusEffects";
               case "mountHealth":
                  return "MountHealth";
               case "vignette":
                  return "Vignette";
               case "spyglass":
                  return "Spyglass";
               case "itemTooltip":
                  return "ItemTooltip";
               default:
                  break;
            }
         }

         return s.methodName;
      }

      return s.methodName + "()";
   }

   static String getSuggestionTail(AutoCompleteConfig.Suggestion s) {
      if (s == null) {
         return "";
      }
      
      if (s.returnType != null && !s.returnType.isEmpty() && !"fn".equals(s.className)) {
         return s.returnType;
      }

      return "";
   }

   public void setMouse(GuiContext context) {
   }

   public static class SuggestionListElement extends GuiListElement<AutoCompleteConfig.Suggestion> {
      private static Map<String, Icon> iconCache = new HashMap();

      public SuggestionListElement(class_310 mc, Consumer<List<AutoCompleteConfig.Suggestion>> callback) {
         super(mc, callback);
         this.scroll.scrollItemSize = ITEM_HEIGHT;
      }

      public void drawListElement(AutoCompleteConfig.Suggestion s, int i, int x, int y, boolean hover, boolean selected) {
         int color = AutoCompleteConfig.getAccentForSuggestion(s) | 0xFF000000;
         int rowEnd = x + this.scroll.w;
         if (selected) {
            GuiDraw.drawRect(x, y, rowEnd, y + ITEM_HEIGHT, ROW_SELECTED);
            GuiDraw.drawRect(x, y, x + 2, y + ITEM_HEIGHT, ROW_SELECTED_ACCENT);
         } else if (hover) {
            GuiDraw.drawRect(x, y, rowEnd, y + ITEM_HEIGHT, ROW_HOVER);
         }

         int textOffsetX = 25;
         if (s != null && "icon".equals(s.className)) {
            this.setIconColor(color);
            Icon icon = this.getIconFromRegistry(s.methodName);
            if (icon != null) {
               int iconW = this.getIconDim(icon, "w", "width");
               int iconH = this.getIconDim(icon, "h", "height");
               if (iconW <= 16 && iconH <= 16) {
                  icon.render(x + 5, y + (ITEM_HEIGHT - iconH) / 2);
               } else {
                  float scale = 12.0F / (float)Math.max(iconW, iconH);
                  icon.render(x + 6, y + (ITEM_HEIGHT - (int)((float)iconH * scale)) / 2, scale, scale);
               }
            } else {
               Icons.ADD.render(x + 5, y + 2);
            }
         } else {
            this.drawFallbackIcon(s, x + 5, y + 2, color);
         }

         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         String tail = AutoCompleteMenu.getSuggestionTail(s);
         int tailW = this.font.method_1727(tail);
         int tailX = rowEnd - tailW - 6;
         int maxTextW = Math.max(12, tailX - (x + textOffsetX) - 8);
         String label = this.shorten(AutoCompleteMenu.getSuggestionDisplay(s), maxTextW);
         int textY = y + (ITEM_HEIGHT - GuiDraw.fontHeight(this.font)) / 2;
         GuiDraw.drawString(this.font, label, x + textOffsetX, textY, selected ? TEXT_SELECTED : TEXT_NORMAL);
         if (!tail.isEmpty()) {
            GuiDraw.drawString(this.font, tail, tailX, textY, selected ? TEXT_SELECTED : TEXT_METADATA);
         }
      }

      private String shorten(String text, int width) {
         if (this.font.method_1727(text) <= width) {
            return text;
         }

         String suffix = "...";
         int end = text.length();
         while(end > 0 && this.font.method_1727(text.substring(0, end) + suffix) > width) {
            --end;
         }

         return end <= 0 ? suffix : text.substring(0, end) + suffix;
      }

      private int getIconDim(Icon icon, String name1, String name2) {
         try {
            Field f = this.findIconField(icon, name1);
            if (f == null) {
               f = this.findIconField(icon, name2);
            }

            if (f != null) {
               f.setAccessible(true);
               return f.getInt(icon);
            }
         } catch (Exception var5) {
         }

         return 16;
      }

      private void setIconColor(int color) {
         float red = (float)(color >> 16 & 255) / 255.0F;
         float green = (float)(color >> 8 & 255) / 255.0F;
         float blue = (float)(color & 255) / 255.0F;
         RenderSystem.setShaderColor(red, green, blue, 1.0F);
      }

      private Field findIconField(Icon icon, String name) {
         for(Class<?> cls = icon.getClass(); cls != null; cls = cls.getSuperclass()) {
            try {
               return cls.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
            }
         }

         return null;
      }

      private Icon getIconFromRegistry(String id) {
         if (iconCache.containsKey(id)) {
            return (Icon)iconCache.get(id);
         } else {
            try {
               Class<?> registry = Class.forName("mchorse.mclib.client.gui.utils.IconRegistry");
               String[] fieldNames = new String[]{"ICONS", "icons", "REGISTRY"};

               for(String name : fieldNames) {
                  try {
                     Field field = registry.getDeclaredField(name);
                     field.setAccessible(true);
                     Object obj = field.get((Object)null);
                     if (obj instanceof Map) {
                        Icon icon = (Icon)((Map)obj).get(id);
                        if (icon != null) {
                           iconCache.put(id, icon);
                           return icon;
                        }
                     }
                  } catch (NoSuchFieldException var11) {
                  }
               }
            } catch (Exception var12) {
            }

            return null;
         }
      }

      private void drawFallbackIcon(AutoCompleteConfig.Suggestion s, int x, int y, int color) {
         if (s != null) {
            float r = (float)(color >> 16 & 255) / 255.0F;
            float g = (float)(color >> 8 & 255) / 255.0F;
            float b = (float)(color & 255) / 255.0F;
            RenderSystem.setShaderColor(r, g, b, 1.0F);
            String type = s.className != null ? s.className : "";
            switch (type) {
               case "var":
                  MPIcons.OTHER_BANNER.render(x, y);
                  break;
               case "[]":
                  Icons.FULLSCREEN.render(x, y);
                  break;
               case "{}":
                  Icons.POSE.render(x, y);
                  break;
               case "fn":
                  Icons.PLAY.render(x, y);
                  break;
               case "java":
                  Icons.DOWNLOAD.render(x, y);
                  break;
               case "kw":
                  Icons.RIGHTLOAD.render(x, y);
                  break;
               case "lib":
                  Icons.PLAY.render(x, y);
                  break;
               case "alias":
                  MPIcons.COMPUTER_CURSOR.render(x, y);
                  break;
               case "hud":
                  Icons.POSE.render(x, y);
                  break;
               case "shader":
                  MPIcons.SCENE.render(x, y);
                  break;
               default:
                  if (!type.startsWith("IScript") && !type.startsWith("IMappet")) {
                     Icons.BLOCK.render(x, y);
                  } else {
                     Icons.SEARCH.render(x, y);
                  }
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

      protected String elementToString(AutoCompleteConfig.Suggestion s) {
         return s != null ? AutoCompleteMenu.getSuggestionDisplay(s) : "";
      }
   }
}
