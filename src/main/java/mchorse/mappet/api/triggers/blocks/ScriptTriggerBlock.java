package mchorse.mappet.api.triggers.blocks;

import javax.script.ScriptException;
import mchorse.mappet.Mappet;
import mchorse.mappet.MappetClient;
import mchorse.mappet.api.scripts.Script;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.utils.ScriptUtils;
import net.minecraft.class_124;
import net.minecraft.class_2487;

public class ScriptTriggerBlock extends DataTriggerBlock {
   private static final long MISSING_FUNCTION_LOG_INTERVAL_MS = 5000L;
   public String function = "";
   public boolean inline = false;
   public String code = "";
   public boolean clientScript = false;
   private transient long lastMissingFunctionLogAt;
   private transient String lastMissingFunctionMessage = "";

   public ScriptTriggerBlock() {
   }

   public ScriptTriggerBlock(String string, String function) {
      super(string);
      this.function = function;
   }

   public boolean isEmpty() {
      return this.inline ? this.code.isEmpty() : this.string.isEmpty();
   }

   public String stringify() {
      if (!this.string.isEmpty() && !this.function.isEmpty()) {
         String prefix = this.isClientScript() ? "[CLIENT] " : "";
         String var10000 = this.string;
         return prefix + var10000 + " (" + String.valueOf(class_124.field_1080) + this.function + String.valueOf(class_124.field_1070) + ")";
      } else {
         return super.stringify();
      }
   }

   private boolean isClientScript() {
      if (this.inline) {
         return this.clientScript;
      }
      if (!this.string.isEmpty() && Mappet.scripts != null) {
         Script script = Mappet.scripts.load(this.string);
         if (script != null) {
            return script.client;
         }
      }
      return this.clientScript;
   }

   public void trigger(DataContext context) {
      if (this.isClientScript()) {
         this.triggerClient(context);
         return;
      }

      if (this.inline) {
         try {
            Mappet.scripts.eval(ScriptUtils.sanitize(ScriptUtils.getEngineByExtension("js")), this.code, context);
         } catch (ScriptException scriptException) {
            Mappet.logger.error(scriptException.getMessage());
         }
      }

      if (!this.string.isEmpty()) {
         try {
            DataContext data = this.apply(context);
            Mappet.scripts.execute(this.string, this.function.trim(), data);
            if (!context.isCanceled()) {
               context.cancel(data.isCanceled());
            }
         } catch (NoSuchMethodException e) {
            this.reportMissingFunction(e);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

   }

   private void triggerClient(DataContext context) {
      if (MappetClient.clientScriptRuntime == null) {
         return;
      }

      if (this.inline) {
         try {
            MappetClient.clientScriptRuntime.eval(ScriptUtils.sanitize(ScriptUtils.getEngineByExtension("js")), this.code, context);
         } catch (ScriptException scriptException) {
            Mappet.logger.error(scriptException.getMessage());
         }
      }

      if (!this.string.isEmpty()) {
         try {
            DataContext data = this.apply(context);
            MappetClient.clientScriptRuntime.execute(this.string, this.function.trim(), data);
            if (!context.isCanceled()) {
               context.cancel(data.isCanceled());
            }
         } catch (NoSuchMethodException e) {
            this.reportMissingFunction(e);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   





   private void reportMissingFunction(NoSuchMethodException error) {
      String message = error.getMessage() == null ? "No configured script function" : error.getMessage();
      long now = System.currentTimeMillis();
      if (!message.equals(this.lastMissingFunctionMessage) || now - this.lastMissingFunctionLogAt >= MISSING_FUNCTION_LOG_INTERVAL_MS) {
         this.lastMissingFunctionMessage = message;
         this.lastMissingFunctionLogAt = now;
         if (Mappet.logger != null) {
            Mappet.logger.error("Script trigger '" + this.string + "' skipped: " + message);
         }
      }
   }

   protected String getKey() {
      return "Script";
   }

   protected void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10582("Function", this.function);
      tag.method_10556("Inline", this.inline);
      tag.method_10582("Code", this.code);
      tag.method_10556("ClientScript", this.clientScript);
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.function = tag.method_10558("Function");
      if (tag.method_10545("Inline")) {
         this.inline = tag.method_10577("Inline");
      }

      if (tag.method_10545("Code")) {
         this.code = tag.method_10558("Code");
      }

      if (tag.method_10545("ClientScript")) {
         this.clientScript = tag.method_10577("ClientScript");
      }

   }
}
