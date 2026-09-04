package mchorse.mappet.api.scripts.user;

import java.util.Map;
import java.util.function.Consumer;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import mchorse.mappet.api.scripts.user.entities.IScriptNpc;
import mchorse.mappet.api.scripts.user.entities.IScriptPlayer;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

public interface IScriptEvent {
   String getScript();

   String getFunction();

   IScriptEntity getSubject();

   IScriptEntity getObject();

   IScriptPlayer getPlayer();

   IScriptNpc getNPC();

   IScriptWorld getWorld();

   IScriptServer getServer();

   Map<String, Object> getValues();

   Object getValue(String var1);

   void setValue(String var1, Object var2);

   void cancel();

   default String scheduleScript(int delay) {
      return this.scheduleScript(this.getFunction(), delay);
   }

   default String scheduleScript(String function, int delay) {
      return this.scheduleScript(this.getScript(), function, delay);
   }

   String scheduleScript(String var1, String var2, int var3);

   String scheduleScript(int var1, ScriptObjectMirror var2);

   String scheduleScript(int var1, Consumer<IScriptEvent> var2);

   boolean cancelScheduleScript(String var1);

   int executeCommand(String var1);

   void send(String var1);
}
