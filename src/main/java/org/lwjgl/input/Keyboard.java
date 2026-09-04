package org.lwjgl.input;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.utils.KeyCodes;
import net.minecraft.class_310;
import net.minecraft.class_437;
import org.lwjgl.glfw.GLFW;

public final class Keyboard {
   public static final int KEY_ESCAPE = 1;
   public static final int KEY_1 = 2;
   public static final int KEY_A = 30;
   public static final int KEY_BACK = 14;
   public static final int KEY_C = 46;
   public static final int KEY_D = 32;
   public static final int KEY_DELETE = 211;
   public static final int KEY_DOWN = 208;
   public static final int KEY_END = 207;
   public static final int KEY_EQUALS = 13;
   public static final int KEY_F = 33;
   public static final int KEY_F6 = 64;
   public static final int KEY_HOME = 199;
   public static final int KEY_J = 36;
   public static final int KEY_LCONTROL = 29;
   public static final int KEY_LEFT = 203;
   public static final int KEY_LMENU = 56;
   public static final int KEY_LSHIFT = 42;
   public static final int KEY_M = 50;
   public static final int KEY_N = 49;
   public static final int KEY_P = 25;
   public static final int KEY_R = 19;
   public static final int KEY_RETURN = 28;
   public static final int KEY_RIGHT = 205;
   public static final int KEY_SLASH = 53;
   public static final int KEY_TAB = 15;
   public static final int KEY_U = 22;
   public static final int KEY_UP = 200;
   public static final int KEY_V = 47;
   public static final int KEY_X = 45;
   public static final int KEY_Y = 21;
   public static final int KEY_Z = 44;

   private Keyboard() {
   }

   public static boolean isKeyDown(int key) {
      long window = class_310.method_1551().method_22683().method_4490();
      int glfw = KeyCodes.lwjgl2ToGlfw(key);
      return glfw >= 0 && GLFW.glfwGetKey(window, glfw) == 1;
   }

   public static void enableRepeatEvents(boolean enabled) {
      class_437 var2 = class_310.method_1551().field_1755;
      if (var2 instanceof GuiBase gui) {
         gui.context.repeatEvents = enabled;
      }

   }

   public static boolean getEventKeyState() {
      return false;
   }

   public static int getEventKey() {
      return 0;
   }

   public static char getEventCharacter() {
      return '\u0000';
   }
}
