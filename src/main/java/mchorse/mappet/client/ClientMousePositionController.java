package mchorse.mappet.client;

import org.lwjgl.glfw.GLFW;
import mchorse.mappet.mixins.MousePositionAccessor;
import net.minecraft.class_310;

public final class ClientMousePositionController {
   private static double currentX;
   private static double currentY;
   private static double startX;
   private static double startY;
   private static double targetX;
   private static double targetY;
   private static int elapsed;
   private static int duration;
   private static String interpolation = "sine_inout";
   private static volatile boolean suppressNextCursorEvent;
   private static boolean additive;
   private static double lastAutomaticX;
   private static double lastAutomaticY;

   private ClientMousePositionController() {}

   public static void set(double x, double y) {
      class_310 client = class_310.method_1551();
      if (client == null || client.method_22683() == null) return;
      x = clamp01(x);
      y = clamp01(y);
      currentX = x * windowWidth(client);
      currentY = y * windowHeight(client);
      duration = 0;
      suppressNextCursorEvent = true;
      GLFW.glfwSetCursorPos(client.method_22683().method_4490(), currentX, currentY);
      MousePositionAccessor mouse = (MousePositionAccessor)client.field_1729;
      mouse.mappet$setX(currentX);
      mouse.mappet$setY(currentY);
   }

   public static void moveTo(double x, double y, int ticks, String easing) {
      class_310 client = class_310.method_1551();
      if (client == null || client.method_22683() == null) return;
      double[] px = new double[1];
      double[] py = new double[1];
      GLFW.glfwGetCursorPos(client.method_22683().method_4490(), px, py);
      startX = px[0];
      startY = py[0];
      currentX = startX;
      currentY = startY;
      targetX = clamp01(x) * windowWidth(client);
      targetY = clamp01(y) * windowHeight(client);
      elapsed = 0;
      duration = Math.max(0, ticks);
      additive = false;
      interpolation = easing == null ? "sine_inout" : easing.toLowerCase(java.util.Locale.ROOT);
      if (duration == 0) set(x, y);
   }

   public static void moveBy(double dx, double dy, int ticks, String easing) {
      class_310 client = class_310.method_1551();
      if (client == null || client.method_22683() == null) return;
      double[] px = new double[1];
      double[] py = new double[1];
      GLFW.glfwGetCursorPos(client.method_22683().method_4490(), px, py);
      startX = px[0];
      startY = py[0];
      currentX = px[0];
      currentY = py[0];
      targetX = Math.max(0.0D, Math.min(windowWidth(client), px[0] + dx * windowWidth(client)));
      targetY = Math.max(0.0D, Math.min(windowHeight(client), py[0] + dy * windowHeight(client)));
      lastAutomaticX = startX;
      lastAutomaticY = startY;
      elapsed = 0;
      duration = Math.max(0, ticks);
      additive = true;
      interpolation = easing == null ? "sine_inout" : easing.toLowerCase(java.util.Locale.ROOT);
      if (duration == 0) duration = 1;
   }

   public static void tick() {
      if (duration <= 0) return;
      ++elapsed;
      class_310 client = class_310.method_1551();
      double progress = Math.min(1.0D, (double)elapsed / (double)duration);
      if (interpolation.contains("sine")) progress = 0.5D - 0.5D * Math.cos(Math.PI * progress);
      double automaticX = startX + (targetX - startX) * progress;
      double automaticY = startY + (targetY - startY) * progress;
      if (additive && client != null && client.method_22683() != null) {
         double[] actualX = new double[1];
         double[] actualY = new double[1];
         GLFW.glfwGetCursorPos(client.method_22683().method_4490(), actualX, actualY);
         currentX = Math.max(0.0D, Math.min(windowWidth(client), actualX[0] + automaticX - lastAutomaticX));
         currentY = Math.max(0.0D, Math.min(windowHeight(client), actualY[0] + automaticY - lastAutomaticY));
      } else {
         currentX = automaticX;
         currentY = automaticY;
      }
      lastAutomaticX = automaticX;
      lastAutomaticY = automaticY;
      if (client != null && client.method_22683() != null) {
         suppressNextCursorEvent = true;
         GLFW.glfwSetCursorPos(client.method_22683().method_4490(), currentX, currentY);
         MousePositionAccessor mouse = (MousePositionAccessor)client.field_1729;
         mouse.mappet$setX(currentX);
         mouse.mappet$setY(currentY);
      }
      if (elapsed >= duration) duration = 0;
   }

   private static double clamp01(double value) {
      return Math.max(0.0D, Math.min(1.0D, value));
   }

   private static double windowWidth(class_310 client) {
      return Math.max(1, client.method_22683().method_4489());
   }

   private static double windowHeight(class_310 client) {
      return Math.max(1, client.method_22683().method_4506());
   }

   public static boolean consumeSyntheticCursorEvent() {
      if (suppressNextCursorEvent) {
         suppressNextCursorEvent = false;
         return true;
      }
      return false;
   }
}
