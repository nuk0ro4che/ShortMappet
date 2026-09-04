package mchorse.mappet.api.scripts;

import java.util.UUID;
import java.util.function.Consumer;
import mchorse.mappet.Mappet;
import mchorse.mappet.MappetClient;
import mchorse.mappet.api.scripts.code.ScriptEvent;
import mchorse.mappet.api.scripts.user.IScriptEvent;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.IExecutable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

public class ScriptExecutionFork implements IExecutable {
   public DataContext context;
   public ScriptObjectMirror object;
   public Consumer<IScriptEvent> consumer;
   public String script;
   public String function;
   public int timer;
   public boolean clientContext;
   private final String taskId = UUID.randomUUID().toString();

   public ScriptExecutionFork(DataContext context, String script, String function, int timer) {
      this.context = context;
      this.script = script;
      this.function = function;
      this.timer = timer;
      this.clientContext = context != null && context.isClient();
   }

   public ScriptExecutionFork(DataContext context, ScriptObjectMirror object, int timer) {
      this.context = context;
      this.object = object;
      this.timer = timer;
      this.clientContext = context != null && context.isClient();
   }

   public ScriptExecutionFork(DataContext context, Consumer<IScriptEvent> consumer, int timer) {
      this.context = context;
      this.consumer = consumer;
      this.timer = timer;
      this.clientContext = context != null && context.isClient();
   }

   public String getId() {
      return this.script;
   }

   public String getTaskId() {
      return this.taskId;
   }

   @Environment(EnvType.CLIENT)
   private void executeClientScript() throws Exception {
      if (MappetClient.clientScriptRuntime != null) {
         MappetClient.clientScriptRuntime.execute(this.script, this.function, this.context);
      }
   }

   public boolean update() {
      if (this.timer <= 0) {
         try {
            if (this.object != null) {
               this.object.call((Object)null, new Object[]{new ScriptEvent(this.context, (String)null, (String)null)});
            } else if (this.consumer != null) {
               this.consumer.accept(new ScriptEvent(this.context, (String)null, (String)null));
            } else if (this.clientContext) {
               this.executeClientScript();
            } else {
               Mappet.scripts.execute(this.script, this.function, this.context);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

         return true;
      } else {
         --this.timer;
         return false;
      }
   }
}
