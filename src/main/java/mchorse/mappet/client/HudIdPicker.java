package mchorse.mappet.client;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.scripts.GuiTextEditor;
import mchorse.mappet.mixins.MousePositionAccessor;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import org.lwjgl.glfw.GLFW;

public final class HudIdPicker {
   public static final List<HudVisibilityState.Element> PICKABLE = new ArrayList();
   private static final int OUTLINE_COLOR = 1728053247;
   private static final int CUSTOM_COLOR = -65281;
   private static final int HIDDEN_COLOR = 0x99C0C0C0;
   private static final int HIGHLIGHT_FILL = 1728169472;
   private static final int TEXT_COLOR = 16777215;
   private static final int MENU_BG = -520093696;
   private static final int MENU_HOVER = 1006632960;
   private static final int MENU_BORDER = -1342177280;
   private static GuiTextEditor editor;
   private static boolean active;
   private static double scaleFactor = 1.0D;
   private static int lastWidth = 0;
   private static int lastHeight = 0;
   private static List<String> choices;
   private static int menuX;
   private static int menuY;

   static {
      PICKABLE.add(HudVisibilityState.Element.HOTBAR);
      PICKABLE.add(HudVisibilityState.Element.HEALTH);
      PICKABLE.add(HudVisibilityState.Element.HUNGER);
      PICKABLE.add(HudVisibilityState.Element.EXPERIENCE);
      PICKABLE.add(HudVisibilityState.Element.CROSSHAIR);
      PICKABLE.add(HudVisibilityState.Element.STATUS_EFFECTS);
      PICKABLE.add(HudVisibilityState.Element.MOUNT_HEALTH);
      PICKABLE.add(HudVisibilityState.Element.CHAT);
      PICKABLE.add(HudVisibilityState.Element.PLAYER_LIST);
      PICKABLE.add(HudVisibilityState.Element.SCOREBOARD);
      PICKABLE.add(HudVisibilityState.Element.BOSS_BAR);
   }

   private HudIdPicker() {
   }

   private static final class ChoiceRow {
      final int x;
      final int y;
      final int w;
      final int h;
      final String id;

      ChoiceRow(int x, int y, int w, int h, String id) {
         this.x = x;
         this.y = y;
         this.w = w;
         this.h = h;
         this.id = id;
      }

      public boolean contains(int px, int py) {
         return px >= this.x && py >= this.y && px <= this.x + this.w && py <= this.y + this.h;
      }
   }

   private static List<ChoiceRow> menuRows() {
      List<ChoiceRow> rows = new ArrayList();
      if (choices == null || choices.isEmpty()) {
         return rows;
      }

      class_310 client = class_310.method_1551();
      class_327 font = client.field_1772;
      int rowH = 14;
      int textWidth = 0;

      for (String id : choices) {
         textWidth = Math.max(textWidth, font.method_1727(id));
      }

      int rowsH = choices.size() * (rowH + 1) - 1;
      int pad = 8;
      int x = Math.max(pad, Math.min(menuX, Math.max(pad, lastWidth - textWidth - pad * 2)));
      int y = menuY + pad;

      if (y + rowsH > lastHeight - pad) {
         y = Math.max(pad, menuY - pad - rowsH);
      }

      if (y + rowsH > lastHeight - pad) {
         y = Math.max(pad, lastHeight - pad - rowsH);
      }

      for (String id : choices) {
         rows.add(new ChoiceRow(x, y, textWidth + pad * 2, rowH, id));
         y += rowH + 1;
      }

      return rows;
   }

   public static void start(GuiTextEditor target) {
      editor = target;
      active = true;
      choices = null;
      class_310 client = class_310.method_1551();
      if (client != null && client.field_1755 != null) {
         client.method_1507(null);
      }
   }

   public static void stop() {
      active = false;
      editor = null;
      choices = null;
      restoreCursor();
   }

   private static void showCursor(class_310 client) {
      if (client != null && client.method_22683() != null && client.method_1569()) {
         GLFW.glfwSetInputMode(client.method_22683().method_4490(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
      }
   }

   public static void restoreCursor() {
      class_310 client = class_310.method_1551();
      if (client != null && client.field_1755 == null && client.method_22683() != null && client.method_1569()) {
         GLFW.glfwSetInputMode(client.method_22683().method_4490(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
      }
   }

   public static boolean isActive() {
      return active;
   }

   public static void mouseClicked(int button) {
      if (!active || lastWidth <= 0 || lastHeight <= 0) {
         return;
      }

      int[] cursor = cursor();
      if (cursor == null) {
         return;
      }

      if (button == 1) {
         if (choices != null && !choices.isEmpty()) {
            choices = null;
         }

         return;
      }

      if (choices != null && !choices.isEmpty()) {
         for (ChoiceRow row : menuRows()) {
            if (row.contains(cursor[0], cursor[1])) {
               insert(row.id);
               return;
            }
         }

         return;
      }

      List<String> ids = new ArrayList();

      for (HudVisibilityState.Element element : elementsAt(cursor[0], cursor[1], lastWidth, lastHeight)) {
         ids.add(element.name().toLowerCase(Locale.ROOT));
      }

      ids.addAll(customHitsAt(cursor[0], cursor[1]));

      if (ids.isEmpty()) {
         return;
      }

      Set<String> unique = new LinkedHashSet(ids);
      ids = new ArrayList(unique);

      if (ids.size() == 1) {
         insert(ids.get(0));
      } else {
         choices = ids;
         menuX = cursor[0];
         menuY = cursor[1];
      }
   }

   private static void insert(String id) {
      if (editor != null) {
         editor.pasteText("\"" + id + "\"");
      }

      stop();

      class_310 client = class_310.method_1551();
      if (client != null) {
         GuiMappetDashboard dashboard = GuiMappetDashboard.get(client);
         client.method_1507(dashboard);
         dashboard.panels.setPanel(dashboard.script);
      }
   }

   public static void render(class_332 context, float tickDelta) {
      if (!active) {
         return;
      }

      class_310 client = class_310.method_1551();
      if (client == null || client.method_22683() == null || client.field_1729 == null) {
         return;
      }

      if (client.field_1755 != null) {
         stop();
         return;
      }

      showCursor(client);

      int width = context.method_51421();
      int height = context.method_51443();
      int framebufferWidth = client.method_22683().method_4489();
      if (framebufferWidth <= 0) {
         return;
      }

      scaleFactor = framebufferWidth / (double)width;
      lastWidth = width;
      lastHeight = height;
      int[] cursor = cursor();
      if (cursor == null) {
         return;
      }

      List<HudVisibilityState.Element> hits = elementsAt(cursor[0], cursor[1], width, height);
      class_327 font = client.field_1772;

      for (HudVisibilityState.Element element : PICKABLE) {
         if (!HudVisibilityState.isVisible(element)) {
            continue;
         }

         int[] rect = measure(element, width, height);
         if (rect == null) {
            continue;
         }

         boolean hovered = cursor[0] >= rect[0] && cursor[1] >= rect[1] && cursor[0] <= rect[2] && cursor[1] <= rect[3];
         if (hovered) {
            context.method_25294(rect[0], rect[1], rect[2], rect[3], HIGHLIGHT_FILL);
         }

         outline(context, rect[0], rect[1], rect[2], rect[3], OUTLINE_COLOR, 1);
      }

      boolean hoveredHidden = false;

      for (HudCapture.CustomDraw draw : HudCapture.getCustomDraws()) {
         if (!HudCustomState.isVisible(draw.texture)) {
            continue;
         }

         int[] rect = applyTransform(draw.texture, draw.x, draw.y, draw.x2, draw.y2);
         if (rect == null) {
            continue;
         }

         boolean hovered = cursor[0] >= rect[0] && cursor[1] >= rect[1] && cursor[0] <= rect[2] && cursor[1] <= rect[3];
         if (hovered) {
            context.method_25294(rect[0], rect[1], rect[2], rect[3], HIGHLIGHT_FILL);
         }

         outline(context, rect[0], rect[1], rect[2], rect[3], CUSTOM_COLOR, 1);
      }

      for (Map.Entry<String, int[]> entry : HudCapture.getLastSeen().entrySet()) {
         String id = entry.getKey();
         if (HudCustomState.isVisible(id)) {
            continue;
         }

         int[] box = entry.getValue();
         int[] rect = applyTransform(id, box[0], box[1], box[2], box[3]);
         if (rect == null) {
            continue;
         }

         boolean hovered = cursor[0] >= rect[0] && cursor[1] >= rect[1] && cursor[0] <= rect[2] && cursor[1] <= rect[3];
         if (hovered) {
            hoveredHidden = true;
            context.method_25294(rect[0], rect[1], rect[2], rect[3], HIGHLIGHT_FILL);
         }

         outline(context, rect[0], rect[1], rect[2], rect[3], HIDDEN_COLOR, 1);
      }

      context.method_25294(cursor[0] - 2, cursor[1] - 1, cursor[0] + 2, cursor[1] + 1, TEXT_COLOR);
      context.method_25294(cursor[0] - 1, cursor[1] - 2, cursor[0] + 1, cursor[1] + 2, TEXT_COLOR);

      if (choices != null && !choices.isEmpty()) {
         drawMenu(context, font, cursor, hits);
         return;
      }

      StringBuilder hint = new StringBuilder(hoveredHidden ? "Скрытый — клик, чтобы вставить айди обратно" : "Клик — вставить айди, Esc — отмена");
      for (HudVisibilityState.Element element : hits) {
         hint.append("  ·  ").append(element.name().toLowerCase(Locale.ROOT));
      }

      Set<String> hinted = new LinkedHashSet();
      for (HudCapture.CustomDraw draw : HudCapture.getCustomDraws()) {
         if (!HudCustomState.isVisible(draw.texture)) {
            continue;
         }

         int[] rect = applyTransform(draw.texture, draw.x, draw.y, draw.x2, draw.y2);
         if (rect != null && cursor[0] >= rect[0] && cursor[1] >= rect[1] && cursor[0] <= rect[2] && cursor[1] <= rect[3]) {
            hinted.add(draw.texture);
         }
      }

      for (Map.Entry<String, int[]> entry : HudCapture.getLastSeen().entrySet()) {
         String id = entry.getKey();
         if (HudCustomState.isVisible(id)) {
            continue;
         }

         int[] box = entry.getValue();
         int[] rect = applyTransform(id, box[0], box[1], box[2], box[3]);
         if (rect != null && cursor[0] >= rect[0] && cursor[1] >= rect[1] && cursor[0] <= rect[2] && cursor[1] <= rect[3]) {
            hinted.add(id);
         }
      }

      for (String id : hinted) {
         hint.append("  ·  мод:").append(id);
      }

      drawCentered(context, font, hint.toString(), width / 2, 8);
   }

   private static void outline(class_332 context, int x, int y, int x2, int y2, int color, int thickness) {
      context.method_25294(x, y, x2, y + thickness, color);
      context.method_25294(x, y2, x2, y2 + thickness, color);
      context.method_25294(x, y, x + thickness, y2, color);
      context.method_25294(x2, y, x2 + thickness, y2, color);
   }

   private static void drawMenu(class_332 context, class_327 font, int[] cursor, List<HudVisibilityState.Element> hits) {
      List<ChoiceRow> rows = menuRows();
      boolean hoveredAny = false;

      for (ChoiceRow row : rows) {
         context.method_25294(row.x, row.y, row.x + row.w, row.y + row.h, MENU_BG);
         context.method_25294(row.x, row.y, row.x + row.w, row.y + 1, MENU_BORDER);
         context.method_25294(row.x, row.y + row.h, row.x + row.w, row.y + row.h + 1, MENU_BORDER);
      }

      for (ChoiceRow row : rows) {
         boolean hovered = row.contains(cursor[0], cursor[1]);
         if (hovered) {
            hoveredAny = true;
            context.method_25294(row.x, row.y, row.x + row.w, row.y + row.h, MENU_HOVER);
         }

         context.method_27535(font, class_2561.method_30163(row.id), row.x + 8, row.y + 3, TEXT_COLOR);
      }

      StringBuilder hint = new StringBuilder(hoveredAny ? "Клик — вставить этот айди" : "Выберите вариант");
      for (HudVisibilityState.Element element : hits) {
         hint.append("  ·  ").append(element.name().toLowerCase(Locale.ROOT));
      }

      drawCentered(context, font, hint.toString(), context.method_51421() / 2, 8);
   }

   private static void drawCentered(class_332 context, class_327 font, String text, int x, int y) {
      int width = font.method_1727(text);
      context.method_27535(font, class_2561.method_30163(text), x - width / 2, y, TEXT_COLOR);
   }

   private static int[] applyTransform(String id, int x, int y, int x2, int y2) {
      int ox = HudCustomState.getX(id);
      int oy = HudCustomState.getY(id);
      float scale = HudCustomState.getScale(id);
      int px = x + ox;
      int py = y + oy;
      int width = (int)((x2 - x) * scale);
      int height = (int)((y2 - y) * scale);
      return new int[]{px, py, px + width, py + height};
   }

   private static List<String> customHitsAt(int x, int y) {
      List<String> ids = new ArrayList();
      for (HudCapture.CustomDraw draw : HudCapture.getCustomDraws()) {
         if (!HudCustomState.isVisible(draw.texture)) {
            continue;
         }

         int[] rect = applyTransform(draw.texture, draw.x, draw.y, draw.x2, draw.y2);
         if (rect != null && x >= rect[0] && y >= rect[1] && x <= rect[2] && y <= rect[3]) {
            ids.add(draw.texture);
         }
      }

      for (Map.Entry<String, int[]> entry : HudCapture.getLastSeen().entrySet()) {
         String id = entry.getKey();
         if (HudCustomState.isVisible(id)) {
            continue;
         }

         int[] box = entry.getValue();
         int[] rect = applyTransform(id, box[0], box[1], box[2], box[3]);
         if (rect != null && x >= rect[0] && y >= rect[1] && x <= rect[2] && y <= rect[3]) {
            ids.add(id);
         }
      }

      return ids;
   }

   private static boolean overlapsCustom(int[] rect) {
      for (HudCapture.CustomBox box : HudCapture.getCustomBoxes()) {
         if (!HudCustomState.isVisible(box.texture)) {
            continue;
         }

         int[] r = applyTransform(box.texture, box.x, box.y, box.x2, box.y2);
         if (r != null && r[0] <= rect[2] && r[2] >= rect[0] && r[1] <= rect[3] && r[3] >= rect[1]) {
            return true;
         }
      }

      return false;
   }

   private static int[] measure(HudVisibilityState.Element element, int width, int height) {
      int[] box = HudCapture.box(element);
      return box != null ? box : rect(element, width, height);
   }

   private static int[] rect(HudVisibilityState.Element element, int width, int height) {
      int[] base;

      switch (element) {
         case HOTBAR:
            base = new int[]{width / 2 - 91, height - 22, width / 2 + 91, height};
            break;
         case HEALTH:
            base = new int[]{width / 2 - 91, height - 55, width / 2 - 10, height - 29};
            break;
         case HUNGER:
            base = new int[]{width / 2 - 10, height - 45, width / 2 + 91, height - 29};
            break;
         case EXPERIENCE:
            base = new int[]{width / 2 - 91, height - 29, width / 2 + 91, height - 24};
            break;
         case CROSSHAIR:
            base = new int[]{width / 2 - 8, height / 2 - 8, width / 2 + 8, height / 2 + 8};
            break;
         case STATUS_EFFECTS:
            base = new int[]{width - 130, 8, width - 8, 36};
            break;
         case MOUNT_HEALTH:
            base = new int[]{width / 2 - 91, 8, width / 2 + 91, 34};
            break;
         case CHAT:
            base = new int[]{2, height - 180, Math.max(40, width / 2), height - 20};
            break;
         case PLAYER_LIST:
            base = new int[]{width / 2 - 100, 25, width / 2 + 100, 225};
            break;
         case SCOREBOARD:
            base = new int[]{width - 135, 5, width - 5, Math.min(240, height - 15)};
            break;
         case BOSS_BAR:
            base = new int[]{width / 2 - 96, 8, width / 2 + 96, 30};
            break;
         default:
            return null;
      }

      int ox = HudVisibilityState.getX(element);
      int oy = HudVisibilityState.getY(element);
      if (ox == 0 && oy == 0) {
         return base;
      }

      return new int[]{base[0] + ox, base[1] + oy, base[2] + ox, base[3] + oy};
   }

   private static int[] cursor() {
      class_310 client = class_310.method_1551();
      if (client == null || client.field_1729 == null) {
         return null;
      }

      MousePositionAccessor mouse = (MousePositionAccessor)client.field_1729;
      return new int[]{(int)(mouse.mappet$getX() / scaleFactor), (int)(mouse.mappet$getY() / scaleFactor)};
   }

   private static List<HudVisibilityState.Element> elementsAt(int x, int y, int width, int height) {
      List<HudVisibilityState.Element> elements = new ArrayList();
      for (HudVisibilityState.Element element : PICKABLE) {
         if (!HudVisibilityState.isVisible(element)) {
            continue;
         }

         int[] rect = measure(element, width, height);
         if (rect == null || x < rect[0] || y < rect[1] || x > rect[2] || y > rect[3]) {
            continue;
         }

         if (HudCapture.box(element) == null && overlapsCustom(rect)) {
            continue;
         }

         elements.add(element);
      }

      return elements;
   }
}