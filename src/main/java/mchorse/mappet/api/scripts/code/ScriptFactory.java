package mchorse.mappet.api.scripts.code;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.scripts.code.blocks.ScriptBlockState;
import mchorse.mappet.api.scripts.code.entities.ScriptEntity;
import mchorse.mappet.api.scripts.code.items.ScriptItemStack;
import mchorse.mappet.api.scripts.code.mappet.MappetUIBuilder;
import mchorse.mappet.api.scripts.code.nbt.ScriptNBTCompound;
import mchorse.mappet.api.scripts.code.nbt.ScriptNBTList;
import mchorse.mappet.api.scripts.user.IScriptFactory;
import mchorse.mappet.api.scripts.user.blocks.IScriptBlockState;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import mchorse.mappet.api.scripts.user.items.IScriptItemStack;
import mchorse.mappet.api.scripts.user.mappet.IMappetUIBuilder;
import mchorse.mappet.api.scripts.user.nbt.INBTCompound;
import mchorse.mappet.api.scripts.user.nbt.INBTList;
import mchorse.mappet.api.ui.UI;
import mchorse.mappet.api.ui.UIFile;
import mchorse.mappet.api.utils.logs.MappetLogger;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.class_1291;
import net.minecraft.class_1297;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2248;
import net.minecraft.class_2396;
import net.minecraft.class_2481;
import net.minecraft.class_2487;
import net.minecraft.class_2489;
import net.minecraft.class_2497;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2522;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_7923;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

public class ScriptFactory implements IScriptFactory {
   private static final Map<String, String> formattingCodes = new HashMap();
   private Random random = new Random();

   public IScriptBlockState createBlockState(String blockId, int meta) {
      class_2960 location = new class_2960(blockId);
      class_2248 block = (class_2248)class_7923.field_41175.method_10223(location);
      if (block == null) {
         return ScriptBlockState.create((class_2680)null);
      } else {
         List<class_2680> states = block.method_9595().method_11662();
         class_2680 state = meta >= 0 && meta < states.size() ? (class_2680)states.get(meta) : block.method_9564();
         return ScriptBlockState.create(state);
      }
   }

   public IScriptBlockState createBlockState(String blockId) {
      return this.createBlockState(blockId, 0);
   }

   public INBTCompound createCompound(String nbt) {
      class_2487 tag = new class_2487();
      if (nbt != null) {
         try {
            tag = class_2522.method_10718(nbt);
         } catch (Exception var4) {
         }
      }

      return new ScriptNBTCompound(tag);
   }

   public INBTCompound createCompoundFromJS(Object jsObject) {
      class_2520 base = this.convertToNBT(jsObject);
      return base instanceof class_2487 ? new ScriptNBTCompound((class_2487)base) : null;
   }

   public INBTList createList(String nbt) {
      class_2499 list = new class_2499();
      if (nbt != null) {
         try {
            list = (class_2499)class_2522.method_10718("{List:" + nbt + "}").method_10580("List");
         } catch (Exception var4) {
         }
      }

      return new ScriptNBTList(list);
   }

   public INBTList createListFromJS(Object jsObject) {
      class_2520 base = this.convertToNBT(jsObject);
      return base instanceof class_2499 ? new ScriptNBTList((class_2499)base) : null;
   }

   private class_2520 convertToNBT(Object object) {
      if (object instanceof String) {
         return class_2519.method_23256((String)object);
      } else if (object instanceof Double) {
         return class_2489.method_23241((Double)object);
      } else if (object instanceof Integer) {
         return class_2497.method_23247((Integer)object);
      } else if (object instanceof Boolean) {
         return class_2481.method_23233((Boolean)object ? Byte.valueOf("1") : Byte.valueOf("0"));
      } else if (object instanceof ScriptObjectMirror) {
         ScriptObjectMirror mirror = (ScriptObjectMirror)object;
         if (mirror.isArray()) {
            class_2499 list = new class_2499();
            int i = 0;

            for(int c = mirror.size(); i < c; ++i) {
               class_2520 base = this.convertToNBT(mirror.getSlot(i));
               if (base != null) {
                  list.add(base);
               }
            }

            return list;
         } else {
            class_2487 tag = new class_2487();

            for(String key : mirror.keySet()) {
               class_2520 base = this.convertToNBT(mirror.get(key));
               if (base != null) {
                  tag.method_10566(key, base);
               }
            }

            return tag;
         }
      } else {
         return null;
      }
   }

   public IScriptItemStack createItem(INBTCompound compound) {
      return (IScriptItemStack)(compound != null ? ScriptItemStack.create(class_1799.method_7915(compound.getNbtCompound())) : ScriptItemStack.EMPTY);
   }

   public IScriptItemStack createItem(String itemId, int count, int meta) {
      class_1792 item = (class_1792)class_7923.field_41178.method_10223(new class_2960(itemId));
      class_1799 stack = new class_1799(item, count);
      if (stack.method_7963()) {
         stack.method_7974(meta);
      }

      return ScriptItemStack.create(stack);
   }

   public IScriptItemStack createBlockItem(String blockId, int count, int meta) {
      class_2248 item = (class_2248)class_7923.field_41175.method_10223(new class_2960(blockId));
      class_1799 stack = new class_1799(item.method_8389(), count);
      if (stack.method_7963()) {
         stack.method_7974(meta);
      }

      return ScriptItemStack.create(stack);
   }

   public class_2396 getParticleType(String type) {
      return (class_2396)class_7923.field_41180.method_10223(class_2960.method_12829(type));
   }

   public class_1291 getPotion(String type) {
      return (class_1291)class_7923.field_41174.method_10223(class_2960.method_12829(type));
   }

   public AbstractMorph createMorph(INBTCompound compound) {
      return compound == null ? null : MorphManager.INSTANCE.morphFromNBT(compound.getNbtCompound());
   }

   public IMappetUIBuilder createUI(String script, String function) {
      script = script == null ? "" : script;
      function = function == null ? "" : function;
      return new MappetUIBuilder(new UI(), script, function);
   }

   



   public IMappetUIBuilder createUIFromFile(String id) {
      return this.createUIFromFile(id, null, null);
   }

   public IMappetUIBuilder createUIFromFile(String id, String script, String function) {
      if (Mappet.uis == null || id == null || id.trim().isEmpty()) {
         return null;
      }

      UIFile ui = (UIFile)Mappet.uis.load(id.trim());
      if (ui == null) {
         return null;
      }

      String callbackScript = script == null || script.trim().isEmpty() ? ui.script : script.trim();
      String callbackFunction = function == null || function.trim().isEmpty() ? ui.function : function.trim();
      if (callbackFunction == null || callbackFunction.isEmpty()) {
         callbackFunction = "main";
      }

      return new MappetUIBuilder(ui, callbackScript, callbackFunction);
   }

   public Object get(String key) {
      return Mappet.scripts.objects.get(key);
   }

   public void set(String key, Object object) {
      Mappet.scripts.objects.put(key, object);
   }

   public String dump(Object object, boolean simple) {
      if (object instanceof ScriptObjectMirror) {
         return object.toString();
      } else {
         Class<?> clazz = object.getClass();
         StringBuilder output = new StringBuilder(simple ? clazz.getSimpleName() : clazz.getTypeName());
         output.append(" {\n");

         for(Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
               output.append("    ");
               if (!simple) {
                  output.append(this.getModifier(field.getModifiers()));
               }

               output.append(field.getName());
               if (!simple) {
                  output.append(" (");
                  output.append(simple ? field.getType().getSimpleName() : field.getType().getTypeName());
                  output.append(")");
               }

               String value = "";

               try {
                  field.setAccessible(true);
                  Object o = field.get(object);
                  value = o == null ? "null" : o.toString();
               } catch (Exception var12) {
               }

               output.append(": ").append(value).append("\n");
            }
         }

         output.append("\n");

         for(Method method : clazz.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) {
               output.append("    ");
               if (!simple) {
                  output.append(this.getModifier(method.getModifiers()));
               }

               output.append(simple ? method.getReturnType().getSimpleName() : method.getReturnType().getTypeName());
               output.append(" ");
               output.append(method.getName()).append("(");
               int size = method.getParameterCount();

               for(int i = 0; i < size; ++i) {
                  Class<?> arg = method.getParameterTypes()[i];
                  output.append(simple ? arg.getSimpleName() : arg.getTypeName());
                  if (i < size - 1) {
                     output.append(", ");
                  }
               }

               output.append(")").append("\n");
            }
         }

         output.append("}");
         return output.toString();
      }
   }

   private String getModifier(int m) {
      String modifier = Modifier.isFinal(m) ? "final " : "";
      if (Modifier.isPublic(m)) {
         modifier = modifier + "public ";
      } else if (Modifier.isProtected(m)) {
         modifier = modifier + "protected ";
      } else if (Modifier.isPrivate(m)) {
         modifier = modifier + "private ";
      }

      return modifier;
   }

   public double random(double max) {
      return Math.random() * max;
   }

   public double random(double min, double max) {
      return min + Math.random() * (max - min);
   }

   public double random(double min, double max, long seed) {
      this.random.setSeed(seed);
      return min + this.random.nextDouble() * (max - min);
   }

   public String style(String... styles) {
      StringBuilder builder = new StringBuilder();

      for(String style : styles) {
         String code = (String)formattingCodes.get(style);
         if (code != null) {
            builder.append('§');
            builder.append(code);
         }
      }

      return builder.toString();
   }

   public boolean isPointInBounds(Object point, Object bound1, Object bound2) {
      if (point instanceof Vector2d) {
         return this.isPointInBounds2D((Vector2d)point, (Vector2d)bound1, (Vector2d)bound2);
      } else if (point instanceof Vector3d) {
         return this.isPointInBounds3D((Vector3d)point, (Vector3d)bound1, (Vector3d)bound2);
      } else if (point instanceof Vector4d) {
         return this.isPointInBounds4D((Vector4d)point, (Vector4d)bound1, (Vector4d)bound2);
      } else {
         throw new IllegalArgumentException("Invalid vector type: " + point.getClass().getName());
      }
   }

   private boolean isPointInBounds2D(Vector2d point, Vector2d bound1, Vector2d bound2) {
      return point.x >= Math.min(bound1.x, bound2.x) && point.x <= Math.max(bound1.x, bound2.x) && point.y >= Math.min(bound1.y, bound2.y) && point.y <= Math.max(bound1.y, bound2.y);
   }

   private boolean isPointInBounds3D(Vector3d point, Vector3d bound1, Vector3d bound2) {
      return point.x >= Math.min(bound1.x, bound2.x) && point.x <= Math.max(bound1.x, bound2.x) && point.y >= Math.min(bound1.y, bound2.y) && point.y <= Math.max(bound1.y, bound2.y) && point.z >= Math.min(bound1.z, bound2.z) && point.z <= Math.max(bound1.z, bound2.z);
   }

   private boolean isPointInBounds4D(Vector4d point, Vector4d bound1, Vector4d bound2) {
      return point.x >= Math.min(bound1.x, bound2.x) && point.x <= Math.max(bound1.x, bound2.x) && point.y >= Math.min(bound1.y, bound2.y) && point.y <= Math.max(bound1.y, bound2.y) && point.z >= Math.min(bound1.z, bound2.z) && point.z <= Math.max(bound1.z, bound2.z) && point.w >= Math.min(bound1.w, bound2.w) && point.w <= Math.max(bound1.w, bound2.w);
   }

   public INBTCompound toNBT(Object object) {
      if (object instanceof INBTCompound) {
         return (INBTCompound)object;
      } else if (object instanceof class_2487) {
         return new ScriptNBTCompound((class_2487)object);
      } else {
         return object instanceof AbstractMorph ? new ScriptNBTCompound(((AbstractMorph)object).toNBT()) : null;
      }
   }

   public MappetLogger getLogger() {
      return Mappet.logger;
   }

   public IScriptEntity getMappetEntity(class_1297 minecraftEntity) {
      return ScriptEntity.create(minecraftEntity);
   }

   public String format(String format, Object... args) {
      return String.format(format, args);
   }

   static {
      formattingCodes.put("black", "0");
      formattingCodes.put("dark_blue", "1");
      formattingCodes.put("dark_green", "2");
      formattingCodes.put("dark_aqua", "3");
      formattingCodes.put("dark_red", "4");
      formattingCodes.put("dark_purple", "5");
      formattingCodes.put("gold", "6");
      formattingCodes.put("gray", "7");
      formattingCodes.put("dark_gray", "8");
      formattingCodes.put("blue", "9");
      formattingCodes.put("green", "a");
      formattingCodes.put("aqua", "b");
      formattingCodes.put("red", "c");
      formattingCodes.put("light_purple", "d");
      formattingCodes.put("yellow", "e");
      formattingCodes.put("white", "f");
      formattingCodes.put("obfuscated", "k");
      formattingCodes.put("bold", "l");
      formattingCodes.put("strikethrough", "m");
      formattingCodes.put("underline", "n");
      formattingCodes.put("italic", "o");
      formattingCodes.put("reset", "r");
   }
}
