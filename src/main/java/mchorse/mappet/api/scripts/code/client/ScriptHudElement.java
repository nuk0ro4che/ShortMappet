package mchorse.mappet.api.scripts.code.client;

import mchorse.mappet.api.scripts.user.client.IHudElement;
import mchorse.mappet.client.HudVisibilityState;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketHudPosition;
import mchorse.mappet.network.common.scripts.PacketHudVisibility;
import net.minecraft.class_1657;
import net.minecraft.class_3222;

public class ScriptHudElement implements IHudElement {
   private final class_1657 player;
   private final HudVisibilityState.Element element;

   public ScriptHudElement(class_1657 player, HudVisibilityState.Element element) {
      this.player = player;
      this.element = element;
   }

   public void render(boolean enabled) {
      if (this.player instanceof class_3222) {
         Dispatcher.sendTo(new PacketHudVisibility(this.element.ordinal(), enabled), (class_3222)this.player);
      } else {
         HudVisibilityState.set(this.element, enabled);
      }
   }

   public void pos(int x, int y) {
      if (this.player instanceof class_3222) {
         Dispatcher.sendTo(new PacketHudPosition(this.element.ordinal(), x, y), (class_3222)this.player);
      } else {
         HudVisibilityState.setPosition(this.element, x, y);
      }
   }

   public void reset() {
      this.render(true);
      this.pos(0, 0);
   }
}
