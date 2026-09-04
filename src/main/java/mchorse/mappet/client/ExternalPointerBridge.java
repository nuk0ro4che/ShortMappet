package mchorse.mappet.client;

import java.io.IOException;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import org.lwjgl.glfw.GLFW;
import net.minecraft.class_310;


public final class ExternalPointerBridge {
   private ExternalPointerBridge() {}

   public static boolean sendSet(double x, double y) {
      class_310 client = class_310.method_1551();
      if (client == null || client.method_22683() == null) return false;
      return send("SET " + Math.round(clamp01(x) * windowWidth(client)) + " "
         + Math.round(clamp01(y) * windowHeight(client)) + "\n");
   }

   public static boolean sendFree() {
      return send("FREE\n");
   }

   public static boolean sendMoveBy(double dx, double dy, int durationTicks, String interpolation) {
      class_310 client = class_310.method_1551();
      if (client == null || client.method_22683() == null) return false;
      double[] px = new double[1];
      double[] py = new double[1];
      GLFW.glfwGetCursorPos(client.method_22683().method_4490(), px, py);
      double x = Math.max(0.0D, Math.min(windowWidth(client), px[0] + dx * windowWidth(client)));
      double y = Math.max(0.0D, Math.min(windowHeight(client), py[0] + dy * windowHeight(client)));
      return send("NUDGE " + Math.round(dx * windowWidth(client)) + " "
         + Math.round(dy * windowHeight(client)) + " " + Math.max(0, durationTicks * 50) + " "
         + (interpolation == null ? "linear" : interpolation) + "\n");
   }

   public static boolean sendMove(double x, double y, int durationTicks, String interpolation) {
      class_310 client = class_310.method_1551();
      if (client == null || client.method_22683() == null) return false;
      double[] px = new double[1];
      double[] py = new double[1];
      GLFW.glfwGetCursorPos(client.method_22683().method_4490(), px, py);
      
      return send("MOVE " + Math.round(px[0]) + " " + Math.round(py[0]) + " "
         + Math.round(clamp01(x) * windowWidth(client)) + " "
         + Math.round(clamp01(y) * windowHeight(client)) + " " + Math.max(0, durationTicks * 50) + " "
         + (interpolation == null ? "linear" : interpolation) + "\n");
   }

   private static double clamp01(double value) {
      return Math.max(0.0D, Math.min(1.0D, value));
   }

   private static int windowWidth(class_310 client) {
      return Math.max(1, client.method_22683().method_4489());
   }

   private static int windowHeight(class_310 client) {
      return Math.max(1, client.method_22683().method_4506());
   }

   private static boolean send(String command) {
      String path = System.getProperty("mappet.pointerSocket", "/tmp/mappet-pointer.sock").trim();
      if (path.isEmpty() || path.length() > 100) return false;
      try (SocketChannel channel = SocketChannel.open(UnixDomainSocketAddress.of(path))) {
         byte[] bytes = command.getBytes(StandardCharsets.US_ASCII);
         channel.write(ByteBuffer.wrap(bytes));
         return true;
      } catch (IOException | IllegalArgumentException ignored) {
         return false;
      }
   }
}
