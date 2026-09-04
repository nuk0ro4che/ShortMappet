package mchorse.mappet.api.scripts.code.client;

import mchorse.mappet.api.scripts.user.client.IGameSettings;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.client.scripts.ClientHandlerClientSetting;
import mchorse.mappet.network.client.scripts.ClientHandlerKeyBinding;
import mchorse.mappet.network.common.scripts.PacketClientSetting;
import mchorse.mappet.network.common.scripts.PacketKeyBinding;
import mchorse.mappet.network.common.scripts.PacketMouseSensitivity;
import net.minecraft.class_1657;
import net.minecraft.class_3222;

public class ScriptGameSettings implements IGameSettings {
   private final class_1657 player;

   public ScriptGameSettings(class_1657 player) {
      this.player = player;
   }

   private void set(String setting, double value) {
      if (this.player instanceof class_3222) {
         Dispatcher.sendTo(new PacketClientSetting(setting, value), (class_3222)this.player);
      } else {
         ClientHandlerClientSetting.apply(setting, value);
      }
   }

   public void setFov(int fov) {
      this.set("fov", (double)Math.max(30, Math.min(110, fov)));
   }

   public void setGamma(double gamma) {
      this.set("gamma", Math.max(0.0D, Math.min(10.0D, gamma)));
   }

   public void setMouseSensitivity(double sensitivity) {
      double value = Math.max(0.0D, Math.min(1.0D, sensitivity));
      if (this.player instanceof class_3222) {
         ClientMouseSensitivityCache.set(this.player.method_5667(), value);
      }
      this.set("mouseSensitivity", value);
   }

   public double getMouseSensitivity() {
      if (this.player instanceof class_3222) {
         Dispatcher.sendTo(new PacketMouseSensitivity(PacketMouseSensitivity.REQUEST), (class_3222)this.player);
         return ClientMouseSensitivityCache.get(this.player.method_5667());
      }
      return ClientMouseSensitivityCache.get(this.player.method_5667());
   }

   public void setHudHidden(boolean hidden) {
      this.set("hudHidden", hidden ? 1.0D : 0.0D);
   }

   public void setPerspective(int perspective) {
      this.set("perspective", (double)Math.max(0, Math.min(2, perspective)));
   }

   public void setKeyBinding(String id, String key) {
      PacketKeyBinding packet = new PacketKeyBinding(PacketKeyBinding.SET, id, key);
      if (this.player instanceof class_3222) {
         Dispatcher.sendTo(packet, (class_3222)this.player);
      } else {
         ClientHandlerKeyBinding.apply(packet);
      }
   }

   public void resetKeyBinding(String id) {
      PacketKeyBinding packet = new PacketKeyBinding(PacketKeyBinding.RESET, id, "key.keyboard.unknown");
      if (this.player instanceof class_3222) {
         Dispatcher.sendTo(packet, (class_3222)this.player);
      } else {
         ClientHandlerKeyBinding.apply(packet);
      }
   }

   public String getKeyBinding(String id) {
      if (this.player instanceof class_3222) {
         Dispatcher.sendTo(new PacketKeyBinding(PacketKeyBinding.REQUEST, id, ""), (class_3222)this.player);
         return ClientKeyBindingCache.get(this.player.method_5667(), id);
      }
      return ClientHandlerKeyBinding.getBindingKey(id);
   }

   public void activateKeyBinding(String id) {
      PacketKeyBinding packet = new PacketKeyBinding(PacketKeyBinding.ACTIVATE, id, "");
      if (this.player instanceof class_3222) {
         Dispatcher.sendTo(packet, (class_3222)this.player);
      } else {
         ClientHandlerKeyBinding.apply(packet);
      }
   }
}
