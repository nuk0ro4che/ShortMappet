package mchorse.mappet.api.scripts.code;

import java.util.Map;
import java.util.function.Consumer;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.api.scripts.ScriptExecutionFork;
import mchorse.mappet.api.scripts.code.entities.ScriptEntity;
import mchorse.mappet.api.scripts.user.IScriptEvent;
import mchorse.mappet.api.scripts.user.IScriptServer;
import mchorse.mappet.api.scripts.user.IScriptWorld;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import mchorse.mappet.api.scripts.user.entities.IScriptNpc;
import mchorse.mappet.api.scripts.user.entities.IScriptPlayer;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.client.ClientEventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

public class ScriptEvent implements IScriptEvent {
   private DataContext context;
   private String script;
   private String function;
   private IScriptEntity subject;
   private IScriptEntity object;
   private IScriptWorld world;
   private IScriptServer server;

   public ScriptEvent(DataContext context, String script, String function) {
      this.context = context;
      this.script = script;
      this.function = function;
   }

   public String getScript() {
      return this.script == null ? "" : this.script;
   }

   public String getFunction() {
      return this.function == null ? "" : this.function;
   }

   public IScriptEntity getSubject() {
      if (this.subject == null && this.context.subject != null) {
         this.subject = ScriptEntity.create(this.context.subject);
      }

      return this.subject;
   }

   public IScriptEntity getObject() {
      if (this.object == null && this.context.object != null) {
         this.object = ScriptEntity.create(this.context.object);
      }

      return this.object;
   }

   public IScriptPlayer getPlayer() {
      IScriptEntity subject = this.getSubject();
      IScriptEntity object = this.getObject();
      if (subject instanceof IScriptPlayer) {
         return (IScriptPlayer)subject;
      } else {
         return object instanceof IScriptPlayer ? (IScriptPlayer)object : null;
      }
   }

   public IScriptNpc getNPC() {
      IScriptEntity subject = this.getSubject();
      IScriptEntity object = this.getObject();
      if (subject instanceof IScriptNpc) {
         return (IScriptNpc)subject;
      } else {
         return object instanceof IScriptPlayer ? (IScriptNpc)object : null;
      }
   }

   public IScriptWorld getWorld() {
      if (this.world == null && this.context.world != null) {
         this.world = new ScriptWorld(this.context.world);
      }

      return this.world;
   }

   public IScriptServer getServer() {
      if (this.server == null && this.context.server != null) {
         this.server = new ScriptServer(this.context.server);
      }

      return this.server;
   }

   public Map<String, Object> getValues() {
      return this.context.getValues();
   }

   public Object getValue(String key) {
      return this.context.getValue(key);
   }

   public void setValue(String key, Object value) {
      this.context.getValues().put(key, value);
   }

   public void cancel() {
      this.context.cancel();
   }

   public String scheduleScript(String script, String function, int delay) {
      ScriptExecutionFork fork = new ScriptExecutionFork(this.context.copy(), script, function, delay);
      if (this.context.isClient()) {
         ClientEventHandler.instance().addExecutable(fork);
      } else {
         CommonProxy.eventHandler.addExecutable(fork);
      }
      return fork.getTaskId();
   }

   public String scheduleScript(int delay, ScriptObjectMirror function) {
      if (function != null && function.isFunction()) {
         ScriptExecutionFork fork = new ScriptExecutionFork(this.context.copy(), function, delay);
         if (this.context.isClient()) {
            ClientEventHandler.instance().addExecutable(fork);
         } else {
            CommonProxy.eventHandler.addExecutable(fork);
         }
         return fork.getTaskId();
      } else {
         throw new IllegalStateException("Given object is null in script " + this.script + " (" + this.function + " function)!");
      }
   }

   public String scheduleScript(int delay, Consumer<IScriptEvent> consumer) {
      if (consumer != null) {
         ScriptExecutionFork fork = new ScriptExecutionFork(this.context.copy(), consumer, delay);
         if (this.context.isClient()) {
            ClientEventHandler.instance().addExecutable(fork);
         } else {
            CommonProxy.eventHandler.addExecutable(fork);
         }
         return fork.getTaskId();
      } else {
         throw new IllegalStateException("Given object is null in script " + this.script + " (" + this.function + " function)!");
      }
   }

   public boolean cancelScheduleScript(String taskId) {
      if (this.context.isClient()) {
         return ClientEventHandler.instance().removeExecutable(taskId);
      }
      return CommonProxy.eventHandler.removeExecutable(taskId);
   }

   public int executeCommand(String command) {
      if (this.context.isClient()) {
         return 0;
      }
      return this.context.execute(command);
   }

   public void send(String message) {
      if (this.context.isClient()) {
         return;
      }
      class_2561 component = class_2561.method_43470(message == null ? "" : message);
      if (this.context.isClient()) {
         if (this.context.subject instanceof class_1657) {
            ((class_1657)this.context.subject).method_7353(component, false);
         }
         return;
      }
      for(class_1657 player : this.context.server.method_3760().method_14571()) {
         player.method_43496(component);
      }

   }
}
