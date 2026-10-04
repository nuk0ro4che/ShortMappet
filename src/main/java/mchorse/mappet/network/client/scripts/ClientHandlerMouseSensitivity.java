package mchorse.mappet.network.client.scripts;

import mchorse.mappet.mixins.GameOptionsAccessor;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketMouseSensitivity;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_315;
import net.minecraft.class_746;
import net.minecraft.class_310;

public class ClientHandlerMouseSensitivity extends ClientMessageHandler<PacketMouseSensitivity> {
   private static double lastSent = Double.NaN;

   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketMouseSensitivity message) {
      class_315 options = class_310.method_1551().field_1690;
      GameOptionsAccessor accessor = (GameOptionsAccessor)options;

      if (message.action == PacketMouseSensitivity.REQUEST) {
         Dispatcher.sendToServer(new PacketMouseSensitivity(PacketMouseSensitivity.RESPONSE, accessor.mappet$getMouseSensitivity().method_41753()));
      }
   }

   @Environment(EnvType.CLIENT)
   public static void reset() {
      lastSent = Double.NaN;
   }

   @Environment(EnvType.CLIENT)
   public static double getLocalSensitivity() {
      class_310 client = class_310.method_1551();

      if (client == null || client.field_1690 == null) {
         return 0.5D;
      }

      return ((GameOptionsAccessor)client.field_1690).mappet$getMouseSensitivity().method_41753();
   }

   @Environment(EnvType.CLIENT)
   public static void syncSensitivity() {
      class_310 client = class_310.method_1551();

      if (client == null || client.field_1724 == null || client.field_1724.field_3944 == null) {
         return;
      }

      double value = getLocalSensitivity();

      if (value == lastSent) {
         return;
      }

      lastSent = value;
      Dispatcher.sendToServer(new PacketMouseSensitivity(PacketMouseSensitivity.RESPONSE, value));
   }
}
