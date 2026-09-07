package mchorse.mappet.client;

import mchorse.mappet.api.misc.hotkeys.TriggerHotkey;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.events.PacketEventHotkey;
import mchorse.mclib.utils.KeyCodes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class InputTriggerHandler {
   private static boolean keyboardActive;
   private static boolean mouseActive;

   private InputTriggerHandler() {
   }

   public static void setActive(boolean keyboard, boolean mouse) {
      keyboardActive = keyboard;
      mouseActive = mouse;
   }

   public static void onKeyboard(int glfwKey, int action) {
      if (!isGameplayInput() || action != GLFW.GLFW_PRESS && action != GLFW.GLFW_RELEASE) {
         return;
      }

      int keycode = KeyCodes.glfwToLwjgl2(glfwKey);
      if (keycode != 0) {
         boolean down = action == GLFW.GLFW_PRESS;
         class_310 mc = class_310.method_1551();

         if (mc.field_1724 != null) {
            ClientTriggers.trigger("player_keyboard", DataContext.client(mc.field_1724).set("keyCode", String.valueOf(keycode)).set("keyState", down));
         }

         if (keyboardActive) {
            send(TriggerHotkey.INPUT_KEYBOARD, keycode, down);
         }
      }
   }

   public static void onMouse(int button, int action) {
      if (isGameplayInput() && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_RELEASE)) {
         boolean down = action == GLFW.GLFW_PRESS;
         class_310 mc = class_310.method_1551();

         if (mc.field_1724 != null) {
            ClientTriggers.trigger("mouse_input", DataContext.client(mc.field_1724).set("button", (double)button).set("buttonState", down).set("DWhell", 0.0D));
         }

         if (mouseActive) {
            send(TriggerHotkey.INPUT_MOUSE, button, down, 0);
         }
      }
   }

   public static void onScroll(double vertical) {
      if (isGameplayInput() && vertical != 0.0D) {
         int wheel = (int)Math.round(vertical * 120.0D);
         class_310 mc = class_310.method_1551();

         if (mc.field_1724 != null) {
            ClientTriggers.trigger("mouse_input", DataContext.client(mc.field_1724).set("button", -1.0D).set("buttonState", false).set("DWhell", (double)wheel));
         }

         if (mouseActive) {
            send(TriggerHotkey.INPUT_MOUSE, -1, false, wheel);
         }
      }
   }

   private static boolean isGameplayInput() {
      class_310 mc = class_310.method_1551();

      return mc.field_1724 != null && mc.field_1687 != null && mc.field_1755 == null;
   }

   private static void send(int input, int keycode, boolean down) {
      send(input, keycode, down, 0);
   }

   private static void send(int input, int keycode, boolean down, int wheel) {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         Dispatcher.sendToServer(new PacketEventHotkey(input, keycode, down, wheel));
      }
   }
}
