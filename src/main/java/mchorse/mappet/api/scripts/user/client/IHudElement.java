package mchorse.mappet.api.scripts.user.client;

public interface IHudElement {
   default IHudElement Hotbar() {
      return this;
   }

   default IHudElement Health() {
      return this;
   }

   default IHudElement Hunger() {
      return this;
   }

   default IHudElement Experience() {
      return this;
   }

   default IHudElement Crosshair() {
      return this;
   }

   default IHudElement StatusEffects() {
      return this;
   }

   default IHudElement MountHealth() {
      return this;
   }

   default IHudElement Vignette() {
      return this;
   }

   default IHudElement Spyglass() {
      return this;
   }

   default IHudElement ItemTooltip() {
      return this;
   }

   void render(boolean enabled);
   void pos(int x, int y);

   default void reset() {
   }
}
