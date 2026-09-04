package mchorse.mappet.events;

import javax.script.ScriptEngine;
import mchorse.mappet.compat.events.Event;

public class RegisterScriptVariablesEvent extends Event {
   private ScriptEngine engine;

   public RegisterScriptVariablesEvent(ScriptEngine engine) {
      this.engine = engine;
   }

   public ScriptEngine getEngine() {
      return this.engine;
   }

   public void register(String key, Object value) {
      this.engine.put(key, value);
   }
}
