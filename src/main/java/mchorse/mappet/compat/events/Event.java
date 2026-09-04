package mchorse.mappet.compat.events;

public class Event {
   private boolean canceled;

   public boolean isCanceled() {
      return this.canceled;
   }

   public void setCanceled(boolean canceled) {
      this.canceled = canceled;
   }

   public boolean isCancelable() {
      return true;
   }
}
