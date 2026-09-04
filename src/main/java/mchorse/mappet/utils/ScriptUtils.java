package mchorse.mappet.utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.network.common.PacketModifyModelBlock;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.blocks.PacketEditConditionModel;
import mchorse.mappet.network.common.blocks.PacketEditRegion;
import mchorse.mappet.tile.TileConditionModel;
import mchorse.mappet.tile.TileRegion;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2586;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

public class ScriptUtils {
   private static ScriptEngineManager manager;

   public static List<ScriptEngine> getAllEngines() {
      return (List)getManager().getEngineFactories().stream().filter((factory) -> !factory.getExtensions().contains("scala")).map((factory) -> {
         try {
            return factory.getScriptEngine();
         } catch (NoClassDefFoundError | Exception var2) {
            return null;
         }
      }).filter(Objects::nonNull).collect(Collectors.toList());
   }

   public static ScriptEngine getEngineByExtension(String extension) {
      extension = extension.replace(".", "");
      ScriptEngine engine = getManager().getEngineByExtension(extension);
      if (extension.equals("py")) {
         try {
            Field fieldInterpreter = Class.forName("org.python.jsr223.PyScriptEngine").getDeclaredField("interp");
            fieldInterpreter.setAccessible(true);
            Object interpreter = fieldInterpreter.get(engine);
            Field fieldcFlags = Class.forName("org.python.util.PythonInterpreter").getDeclaredField("cflags");
            fieldcFlags.setAccessible(true);
            Object cFlags = fieldcFlags.get(interpreter);
            Class.forName("org.python.core.CompilerFlags").getDeclaredField("source_is_utf8").setBoolean(cFlags, true);
            return engine;
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return engine;
   }

   public static void initiateScriptEngines() {
      for(ScriptEngine engine : getAllEngines()) {
         try {
            if (!engine.eval(Objects.equals(engine.getFactory().getLanguageName(), "python") ? "True" : "true").equals(Boolean.TRUE)) {
               throw new Exception("Something went wrong with " + engine.getFactory().getEngineName());
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

   }

   public static ScriptEngineManager getManager() {
      try {
         if (manager == null) {
            manager = new ScriptEngineManager();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return manager;
   }

   public static ScriptEngine sanitize(ScriptEngine engine) {
      Bindings bindings = engine.getBindings(100);
      bindings.remove("load");
      bindings.remove("loadWithNewGlobal");
      bindings.remove("exit");
      bindings.remove("quit");
      return engine;
   }

   public static String getScriptContent(ScriptObjectMirror script) {
      String fullScript = script.toString();
      String scriptContent = "";
      Pattern pattern = Pattern.compile("\\{(.*)\\}", 32);
      Matcher matcher = pattern.matcher(fullScript);
      if (matcher.find()) {
         scriptContent = matcher.group(1).trim();
      }

      String[] lines = scriptContent.split("\\n");
      int commonSpaces = Integer.MAX_VALUE;

      for(int i = 1; i < lines.length; ++i) {
         int spaceCount;
         for(spaceCount = 0; lines[i].length() > spaceCount && lines[i].charAt(spaceCount) == ' '; ++spaceCount) {
         }

         commonSpaces = Math.min(commonSpaces, spaceCount);
      }

      StringBuilder adjustedScript = new StringBuilder(lines[0].trim() + "\n");

      for(int i = 1; i < lines.length; ++i) {
         adjustedScript.append(lines[i].substring(commonSpaces)).append("\n");
      }

      scriptContent = adjustedScript.toString().trim();
      return scriptContent;
   }

   public static <T> void sendTileUpdatePacket(T tile) {
      try {
         if (tile instanceof TileEntityModel bbModelBlock) {
            PacketModifyModelBlock message = new PacketModifyModelBlock(bbModelBlock.method_11016(), bbModelBlock, true);
            Dispatcher.DISPATCHER.sendToAll(message);
         } else if (tile instanceof TileConditionModel conditionModelBlock) {
            class_2487 tag = new class_2487();
            PacketEditConditionModel message = new PacketEditConditionModel(conditionModelBlock.method_11016(), conditionModelBlock.toNBT(tag));
            Dispatcher.DISPATCHER.sendToAll(message);
         } else {
            if (!(tile instanceof TileRegion)) {
               throw new IllegalArgumentException("Invalid tile type");
            }

            TileRegion region = (TileRegion)tile;
            new class_2487();
            PacketEditRegion message = new PacketEditRegion(region.method_11016(), region.region.serializeNBT());
            Dispatcher.DISPATCHER.sendToAll(message);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public static <T extends class_2248, U extends class_2586, V> V place(class_1937 mcWorld, class_2338 pos, T blockType, Class<U> tileEntityType, PostPlace<U> postPlace, Supplier<V> returnVal) {
      if (mcWorld.method_8320(pos).method_26204() != class_2246.field_10124) {
         mcWorld.method_8652(pos, class_2246.field_10124.method_9564(), 6);
      }

      mcWorld.method_8652(pos, blockType.method_9564(), 6);
      if (mcWorld.method_8320(pos).method_26204() == blockType) {
         U tileEntity = (U)(tileEntityType.cast(mcWorld.method_8321(pos)));
         if (tileEntity != null) {
            postPlace.apply(tileEntity);
            tileEntity.method_5431();
         }
      }

      return (V)returnVal.get();
   }

   public interface PostPlace<T extends class_2586> {
      void apply(T var1);
   }
}
