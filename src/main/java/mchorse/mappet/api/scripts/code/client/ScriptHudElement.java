package mchorse.mappet.api.scripts.code.client;

import mchorse.mappet.api.scripts.user.client.IHudElement;
import mchorse.mappet.client.HudCustomState;
import mchorse.mappet.client.HudVisibilityState;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketHudPosition;
import mchorse.mappet.network.common.scripts.PacketHudScale;
import mchorse.mappet.network.common.scripts.PacketHudVisibility;
import net.minecraft.class_1657;
import net.minecraft.class_3222;

public class ScriptHudElement implements IHudElement {
   private final class_1657 player;
   private final HudVisibilityState.Element element;
   private final String mod;

   public ScriptHudElement(class_1657 player, HudVisibilityState.Element element) {
      this.player = player;
      this.element = element;
      this.mod = null;
   }

   public ScriptHudElement(class_1657 player, String mod) {
      this.player = player;
      this.element = null;
      this.mod = mod;
   }

   public void render(boolean enabled) {
      if (this.player instanceof class_3222) {
         if (this.mod != null) {
            Dispatcher.sendTo(new PacketHudVisibility(this.mod, enabled), (class_3222)this.player);
         } else {
            Dispatcher.sendTo(new PacketHudVisibility(this.element.ordinal(), enabled), (class_3222)this.player);
         }
      } else if (this.mod != null) {
         HudCustomState.setVisible(this.mod, enabled);
      } else {
         HudVisibilityState.set(this.element, enabled);
      }
   }

   public void pos(int x, int y) {
      if (this.player instanceof class_3222) {
         if (this.mod != null) {
            Dispatcher.sendTo(new PacketHudPosition(this.mod, x, y), (class_3222)this.player);
         } else {
            Dispatcher.sendTo(new PacketHudPosition(this.element.ordinal(), x, y), (class_3222)this.player);
         }
      } else if (this.mod != null) {
         HudCustomState.setPosition(this.mod, x, y);
      } else {
         HudVisibilityState.setPosition(this.element, x, y);
      }
   }

   public void scale(float factor) {
      if (this.mod == null) {
         return;
      }

      if (this.player instanceof class_3222) {
         Dispatcher.sendTo(new PacketHudScale(this.mod, factor), (class_3222)this.player);
      } else {
         HudCustomState.setScale(this.mod, factor);
      }
   }

   public void reset() {
      if (this.mod != null) {
         this.render(true);
         this.pos(0, 0);
         this.scale(1.0F);
      } else {
         this.render(true);
         this.pos(0, 0);
      }
   }
}