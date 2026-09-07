package mchorse.mappet.client;

import java.util.HashSet;
import java.util.Set;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.misc.hotkeys.TriggerHotkey;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.client.gui.GuiJournalScreen;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.panels.GuiScriptPanel;
import mchorse.mappet.client.gui.scripts.scriptedItem.GuiScriptedItemScreen;
import mchorse.mappet.mixins.ScreenAccessor;
import mchorse.mappet.compat.events.legacy.LegacyEvents;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.events.PacketPlayerJournal;
import mchorse.mclib.utils.KeyCodes;
import mchorse.mclib.utils.OpHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_481;
import net.minecraft.class_490;
import net.minecraft.class_491;
import net.minecraft.class_7919;
import net.minecraft.class_3675.class_307;

@Environment(EnvType.CLIENT)
public final class KeyboardHandler {
   public static final Set<TriggerHotkey> hotkeys = new HashSet();
   public static boolean clientPlayerJournal;
   private static boolean jumpWasDown;
   private static class_304 dashboard;
   private static class_304 journal;
   private static class_304 runScript;
   private static class_304 scriptedItem;

   private KeyboardHandler() {
   }

   public static void register() {
      String category = "mappet.keys.category";
      dashboard = key("mappet.keys.dashboard", 13, category);
      journal = key("mappet.keys.journal", 36, category);
      runScript = key("mappet.keys.runCurrentScript", 64, category);
      scriptedItem = key("mappet.keys.scripted_item", 207, category);
      ClientTickEvents.END_CLIENT_TICK.register(KeyboardHandler::tick);
ScreenEvents.AFTER_INIT.register((ScreenEvents.AfterInit)(client, screen, width, height) -> {
          if (isContainer(screen) && client.field_1724 != null) {
             ClientTriggers.trigger("player_open_container", DataContext.client(client.field_1724));
          }

          if (screen instanceof class_490 || screen instanceof class_481) {
            int x = (Integer)Mappet.journalButtonX.get();
            int y = height - 20 - (Integer)Mappet.journalButtonY.get();
            class_4185 button = class_4185.method_46430(class_2561.method_43470("§6✎"), (ignored) -> openPlayerJournal()).method_46434(x, y, 20, 20).method_46436(class_7919.method_47407(class_2561.method_43471("mappet.gui.player_journal"))).method_46431();
            addDrawableChild(screen, button);
         }

      });
   }

   private static void addDrawableChild(class_437 screen, class_4185 button) {
      ((ScreenAccessor)screen).mappet$addDrawableChild(button);
   }

   private static class_304 key(String id, int code, String category) {
      return KeyBindingHelper.registerKeyBinding(new class_304(id, class_307.field_1668, KeyCodes.lwjgl2ToGlfw(code), category));
   }

   public static void updateHeldKeys() {
   }

   public static void openPlayerJournal() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         ClientTriggers.trigger("player_journal", DataContext.client(mc.field_1724));
      }

      if (clientPlayerJournal) {
         mc.method_1507(new GuiJournalScreen(mc));
      } else {
         Dispatcher.sendToServer(new PacketPlayerJournal());
      }

   }

   private static void tick(class_310 mc) {
      if (mc.field_1724 != null) {
         boolean jumpDown = mc.field_1690.field_1903.method_1434();
         if (jumpDown && !jumpWasDown) {
            CommonProxy.eventHandler.onKeyInput(new LegacyEvents.InputEvent.KeyInputEvent());
         }

         jumpWasDown = jumpDown;

         while(dashboard.method_1436()) {
            if (OpHelper.isPlayerOp() && (!(Boolean)Mappet.dashboardOnlyCreative.get() || mc.field_1724.method_31549().field_7477)) {
               mc.method_1507(GuiMappetDashboard.get(mc));
            }
         }

         while(journal.method_1436()) {
            openPlayerJournal();
         }

         while(runScript.method_1436()) {
            GuiMappetDashboard dashboard = GuiMappetDashboard.get(mc);
            GuiScriptPanel panel = dashboard == null ? null : dashboard.script;
            if (panel != null) {
               panel.runCurrentScript();
            }
         }

         while(scriptedItem.method_1436()) {
            class_1799 stack = mc.field_1724.method_6047();
            if (!stack.method_7960() && stack.method_7909() != class_1802.field_8162) {
               mc.method_1507(new GuiScriptedItemScreen(mc, stack));
            }
         }

      }
   }

   public static void onScreenClose(class_437 screen) {
      if (isContainer(screen)) {
         class_310 mc = class_310.method_1551();
         if (mc.field_1724 != null) {
            ClientTriggers.trigger("player_close_container", DataContext.client(mc.field_1724));
         }
      }
   }

   private static boolean isContainer(class_437 screen) {
      return screen instanceof class_491 && !(screen instanceof class_490) && !(screen instanceof class_481);
   }
}
