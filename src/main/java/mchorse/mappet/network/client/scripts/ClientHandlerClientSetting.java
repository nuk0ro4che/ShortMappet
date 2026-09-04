package mchorse.mappet.network.client.scripts;

import mchorse.mappet.mixins.GameOptionsAccessor;
import mchorse.mappet.network.common.scripts.PacketClientSetting;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_315;
import net.minecraft.class_5498;
import net.minecraft.class_746;
import net.minecraft.class_310;

public class ClientHandlerClientSetting extends ClientMessageHandler<PacketClientSetting> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketClientSetting message) {
      apply(message.setting, message.value);
   }

   @Environment(EnvType.CLIENT)
   public static void apply(String setting, double value) {
      class_315 options = class_310.method_1551().field_1690;
      GameOptionsAccessor accessor = (GameOptionsAccessor)options;
      if ("fov".equals(setting)) {
         accessor.mappet$getFov().method_41748((int)value);
      } else if ("gamma".equals(setting)) {
         accessor.mappet$getGamma().method_41748(value);
      } else if ("mouseSensitivity".equals(setting)) {
         accessor.mappet$getMouseSensitivity().method_41748(Math.max(0.0D, Math.min(1.0D, value)));
      } else if ("hudHidden".equals(setting)) {
         options.field_1842 = value != 0.0D;
      } else if ("perspective".equals(setting)) {
         class_5498[] perspectives = class_5498.values();
         int index = Math.max(0, Math.min(perspectives.length - 1, (int)value));
         options.method_31043(perspectives[index]);
      }
   }
}
