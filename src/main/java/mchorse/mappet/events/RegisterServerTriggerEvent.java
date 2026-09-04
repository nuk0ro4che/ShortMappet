package mchorse.mappet.events;

import mchorse.mappet.api.misc.ServerSettings;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.compat.events.Event;

public class RegisterServerTriggerEvent extends Event {
   private final ServerSettings settings;

   public RegisterServerTriggerEvent(ServerSettings settings) {
      this.settings = settings;
   }

   public void register(String key, Trigger trigger) {
      this.settings.register(key, trigger);
   }
}
