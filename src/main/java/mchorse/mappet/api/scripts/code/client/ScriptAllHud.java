package mchorse.mappet.api.scripts.code.client;

import java.util.Locale;
import mchorse.mappet.api.scripts.user.client.IHudElement;
import mchorse.mappet.client.HudVisibilityState;
import net.minecraft.class_1657;

public class ScriptAllHud implements IHudElement {
   private final class_1657 player;

   public ScriptAllHud(class_1657 player) {
      this.player = player;
   }

   private IHudElement get(HudVisibilityState.Element element) {
      return new ScriptHudElement(this.player, element);
   }

   public IHudElement get(String name) {
      if (name == null) {
         throw new IllegalArgumentException("HUD element name cannot be null");
      }

      return this.get(HudVisibilityState.Element.valueOf(name.toUpperCase(Locale.ROOT)));
   }

   public IHudElement Hotbar() {
      return this.get(HudVisibilityState.Element.HOTBAR);
   }

   public IHudElement Health() {
      return this.get(HudVisibilityState.Element.HEALTH);
   }

   public IHudElement Hunger() {
      return this.get(HudVisibilityState.Element.HUNGER);
   }

   public IHudElement Experience() {
      return this.get(HudVisibilityState.Element.EXPERIENCE);
   }

   public IHudElement Crosshair() {
      return this.get(HudVisibilityState.Element.CROSSHAIR);
   }

   public IHudElement StatusEffects() {
      return this.get(HudVisibilityState.Element.STATUS_EFFECTS);
   }

   public IHudElement MountHealth() {
      return this.get(HudVisibilityState.Element.MOUNT_HEALTH);
   }

   public IHudElement Vignette() {
      return this.get(HudVisibilityState.Element.VIGNETTE);
   }

   public IHudElement Spyglass() {
      return this.get(HudVisibilityState.Element.SPYGLASS);
   }

   public IHudElement ItemTooltip() {
      return this.get(HudVisibilityState.Element.ITEM_TOOLTIP);
   }

   public void render(boolean enabled) {
   }

   public void pos(int x, int y) {
   }

   public void reset() {
      for (HudVisibilityState.Element element : HudVisibilityState.Element.values()) {
         ScriptHudElement hud = new ScriptHudElement(this.player, element);
         hud.render(true);
         hud.pos(0, 0);
      }
   }
}
