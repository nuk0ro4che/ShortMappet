package mchorse.mappet.api.shaders;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_2481;
import net.minecraft.class_2487;
import net.minecraft.class_2489;
import net.minecraft.class_2494;
import net.minecraft.class_2497;
import net.minecraft.class_2499;
import net.minecraft.class_2503;
import net.minecraft.class_2514;
import net.minecraft.class_2516;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * Helpers for shader parameters supplied from scripts.
 *
 * <p>Parameters are split into two groups:</p>
 *
 * <ul>
 *     <li><b>Uniforms</b> &mdash; every key that is not a reserved config key
 *     is applied as a GLSL uniform with the same name.</li>
 *     <li><b>Config overrides</b> &mdash; reserved keys such as
 *     {@code renderOnHand}, {@code blendMode} or {@code passes} temporarily
 *     override the shader file settings. Both the bare key
 *     ({@code renderOnHand}) and the prefixed form ({@code $renderOnHand}) are
 *     accepted. Use {@code uniform:key} to force a uniform whose name collides
 *     with a config key.</li>
 * </ul>
 */
public final class ShaderParameters {
   public static final String CONFIG_PREFIX = "$";
   public static final String UNIFORM_PREFIX = "uniform:";

   public static final String ENABLED = "enabled";
   public static final String GLOBAL = "global";
   public static final String DEPTH_BUFFER = "depthBuffer";
   public static final String WORLD = "world";
   public static final String HIDE_IN_FIRST_PERSON = "hideInFirstPerson";
   public static final String RENDER_ON_HAND = "renderOnHand";
   public static final String RENDER_STAGE = "renderStage";
   public static final String BLEND_MODE = "blendMode";
   public static final String PASSES = "passes";
   public static final String SCALE = "scale";
   public static final String DURATION = "duration";
   public static final String SHADOW_MAP = "shadowMap";
   public static final String SHADOW_RESOLUTION = "shadowResolution";
   public static final String SHADOW_DISTANCE = "shadowDistance";

   private static final Set<String> CONFIG_KEYS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
         ENABLED, GLOBAL, DEPTH_BUFFER, WORLD, HIDE_IN_FIRST_PERSON, RENDER_ON_HAND,
         RENDER_STAGE, BLEND_MODE, PASSES, SCALE, DURATION, SHADOW_MAP,
         SHADOW_RESOLUTION, SHADOW_DISTANCE)));

   private ShaderParameters() {
   }

   /**
    * Creates a render-time copy of the shader with the supplied parameters and
    * config overrides applied. The original file is never mutated.
    */
   public static ShaderFile applyTo(ShaderFile base, Map<String, Object> parameters) {
      ShaderFile shader = base.copy();

      /* File level parameter defaults first, runtime values second. */
      applyConfig(shader, shader.parameters);

      Map<String, Object> runtime = normalize(parameters);

      if (!runtime.isEmpty()) {
         shader.parameters.putAll(runtime);
         applyConfig(shader, runtime);
      }

      return shader;
   }

   /**
    * Applies reserved config keys from the map onto the shader fields.
    */
   public static void applyConfig(ShaderFile shader, Map<String, Object> parameters) {
      if (shader == null || parameters == null || parameters.isEmpty()) {
         return;
      }

      Boolean bool = getBoolean(parameters, ENABLED);
      if (bool != null) shader.enabled = bool;

      bool = getBoolean(parameters, GLOBAL);
      if (bool != null) shader.global = bool;

      bool = getBoolean(parameters, DEPTH_BUFFER);
      if (bool != null) shader.depthBuffer = bool;

      bool = getBoolean(parameters, WORLD);
      if (bool != null) shader.world = bool;

      bool = getBoolean(parameters, HIDE_IN_FIRST_PERSON);
      if (bool != null) shader.hideInFirstPerson = bool;

      bool = getBoolean(parameters, RENDER_ON_HAND);
      if (bool != null) shader.renderOnHand = bool;

      Integer integer = getInteger(parameters, RENDER_STAGE);
      if (integer != null) shader.renderStage = Math.max(0, integer);

      String string = getString(parameters, BLEND_MODE);
      if (string != null && !string.trim().isEmpty()) shader.blendMode = string.trim();

      integer = getInteger(parameters, PASSES);
      if (integer != null) shader.passes = Math.max(1, integer);

      Double number = getDouble(parameters, SCALE);
      if (number != null) shader.scale = Math.max(0.05F, number.floatValue());

      integer = getInteger(parameters, DURATION);
      if (integer != null) shader.duration = Math.max(0, integer);

      bool = getBoolean(parameters, SHADOW_MAP);
      if (bool != null) shader.shadowMap = bool;

      integer = getInteger(parameters, SHADOW_RESOLUTION);
      if (integer != null) shader.shadowResolution = Math.max(16, integer);

      integer = getInteger(parameters, SHADOW_DISTANCE);
      if (integer != null) shader.shadowDistance = Math.max(1, integer);
   }

   /**
    * Whether the key is a reserved config key (bare or {@code $}-prefixed).
    * Such keys are not uploaded as uniforms.
    */
   public static boolean isConfigEntry(String key) {
      if (key == null || key.isEmpty()) {
         return true;
      }

      if (key.startsWith(CONFIG_PREFIX)) {
         return true;
      }

      return CONFIG_KEYS.contains(key);
   }

   /**
    * Resolves the uniform name for a parameter key.
    */
   public static String uniformName(String key) {
      if (key == null) {
         return "";
      }

      if (key.startsWith(UNIFORM_PREFIX)) {
         return key.substring(UNIFORM_PREFIX.length());
      }

      if (key.startsWith(CONFIG_PREFIX)) {
         return key.substring(CONFIG_PREFIX.length());
      }

      return key;
   }

   /**
    * Normalizes a script supplied map into plain Java values.
    */
   public static Map<String, Object> normalize(Map<String, Object> source) {
      Map<String, Object> result = new LinkedHashMap<String, Object>();

      if (source == null) {
         return result;
      }

      for (Map.Entry<String, Object> entry : source.entrySet()) {
         String key = entry.getKey();

         if (key == null || key.isEmpty()) {
            continue;
         }

         Object value = normalizeValue(entry.getValue());

         if (value != null) {
            result.put(key, value);
         }
      }

      return result;
   }

   /**
    * Deep copies a parameter map.
    */
   public static Map<String, Object> copy(Map<String, Object> source) {
      Map<String, Object> result = new LinkedHashMap<String, Object>();

      if (source == null) {
         return result;
      }

      for (Map.Entry<String, Object> entry : source.entrySet()) {
         if (entry.getKey() != null) {
            result.put(entry.getKey(), copyValue(entry.getValue()));
         }
      }

      return result;
   }

   public static class_2487 toNbt(Map<String, Object> parameters) {
      class_2487 tag = new class_2487();

      if (parameters == null) {
         return tag;
      }

      for (Map.Entry<String, Object> entry : parameters.entrySet()) {
         String key = entry.getKey();

         if (key == null || key.isEmpty()) {
            continue;
         }

         class_2520 value = toTag(entry.getValue());

         if (value != null) {
            tag.method_10566(key, value);
         }
      }

      return tag;
   }

   public static Map<String, Object> fromNbt(class_2487 tag) {
      Map<String, Object> result = new LinkedHashMap<String, Object>();

      if (tag == null) {
         return result;
      }

      for (String key : tag.method_10541()) {
         Object value = fromTag(tag.method_10580(key));

         if (value != null) {
            result.put(key, value);
         }
      }

      return result;
   }

   private static Object normalizeValue(Object value) {
      if (value == null) {
         return null;
      }

      if (value instanceof ScriptObjectMirror) {
         ScriptObjectMirror mirror = (ScriptObjectMirror) value;

         if (mirror.isFunction()) {
            return null;
         }

         if (mirror.isArray()) {
            List<Object> list = new ArrayList<Object>();
            int size = mirror.size();

            for (int i = 0; i < size; ++i) {
               Object element = normalizeValue(mirror.getSlot(i));

               if (element != null) {
                  list.add(element);
               }
            }

            return list;
         }

         Map<String, Object> map = new LinkedHashMap<String, Object>();

         for (Map.Entry<String, Object> entry : mirror.entrySet()) {
            Object element = normalizeValue(entry.getValue());

            if (element != null) {
               map.put(entry.getKey(), element);
            }
         }

         return map;
      }

      if (value instanceof Map) {
         Map<String, Object> map = new LinkedHashMap<String, Object>();

         for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
            if (entry.getKey() == null) {
               continue;
            }

            Object element = normalizeValue(entry.getValue());

            if (element != null) {
               map.put(entry.getKey().toString(), element);
            }
         }

         return map;
      }

      if (value instanceof List) {
         List<Object> list = new ArrayList<Object>();

         for (Object element : (List<?>) value) {
            Object normalized = normalizeValue(element);

            if (normalized != null) {
               list.add(normalized);
            }
         }

         return list;
      }

      if (value.getClass().isArray()) {
         List<Object> list = new ArrayList<Object>();
         int length = Array.getLength(value);

         for (int i = 0; i < length; ++i) {
            Object normalized = normalizeValue(Array.get(value, i));

            if (normalized != null) {
               list.add(normalized);
            }
         }

         return list;
      }

      if (value instanceof Boolean || value instanceof Number || value instanceof CharSequence) {
         return value;
      }

      return value.toString();
   }

   private static Object copyValue(Object value) {
      if (value instanceof Map) {
         return copy((Map<String, Object>) value);
      }

      if (value instanceof List) {
         List<Object> list = new ArrayList<Object>();

         for (Object element : (List<?>) value) {
            list.add(copyValue(element));
         }

         return list;
      }

      if (value != null && value.getClass().isArray()) {
         List<Object> list = new ArrayList<Object>();
         int length = Array.getLength(value);

         for (int i = 0; i < length; ++i) {
            list.add(copyValue(Array.get(value, i)));
         }

         return list;
      }

      return value;
   }

   private static class_2520 toTag(Object value) {
      if (value == null) {
         return null;
      }

      if (value instanceof Boolean) {
         return class_2481.method_23233((byte) ((Boolean) value ? 1 : 0));
      }

      if (value instanceof Byte) {
         return class_2481.method_23233((Byte) value);
      }

      if (value instanceof Short) {
         return class_2516.method_23254((Short) value);
      }

      if (value instanceof Integer) {
         return class_2497.method_23247((Integer) value);
      }

      if (value instanceof Long) {
         return class_2503.method_23251((Long) value);
      }

      if (value instanceof Float) {
         return class_2494.method_23244((Float) value);
      }

      if (value instanceof Double) {
         return class_2489.method_23241((Double) value);
      }

      if (value instanceof CharSequence) {
         return class_2519.method_23256(value.toString());
      }

      if (value instanceof Map) {
         Map<String, Object> map = new LinkedHashMap<String, Object>();

         for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
            if (entry.getKey() != null) {
               map.put(entry.getKey().toString(), entry.getValue());
            }
         }

         return toNbt(map);
      }

      if (value instanceof List) {
         return toListTag((List<?>) value);
      }

      if (value.getClass().isArray()) {
         List<Object> list = new ArrayList<Object>();
         int length = Array.getLength(value);

         for (int i = 0; i < length; ++i) {
            list.add(Array.get(value, i));
         }

         return toListTag(list);
      }

      return class_2519.method_23256(value.toString());
   }

   private static class_2499 toListTag(List<?> list) {
      class_2499 tag = new class_2499();

      if (list == null || list.isEmpty()) {
         return tag;
      }

      boolean allBoolean = true;
      boolean allIntegral = true;
      boolean allNumber = true;
      boolean allString = true;
      boolean allMap = true;
      boolean hasLong = false;

      for (Object value : list) {
         if (value == null) {
            continue;
         }

         if (!(value instanceof Boolean)) allBoolean = false;
         if (!(value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long)) allIntegral = false;
         if (!(value instanceof Number)) allNumber = false;
         if (!(value instanceof CharSequence)) allString = false;
         if (!(value instanceof Map)) allMap = false;
         if (value instanceof Long) hasLong = true;
      }

      for (Object value : list) {
         if (value == null) {
            continue;
         }

         if (allBoolean) {
            tag.add(class_2481.method_23233((byte) ((Boolean) value ? 1 : 0)));
         } else if (allIntegral && hasLong) {
            tag.add(class_2503.method_23251(((Number) value).longValue()));
         } else if (allIntegral) {
            tag.add(class_2497.method_23247(((Number) value).intValue()));
         } else if (allNumber) {
            tag.add(class_2489.method_23241(((Number) value).doubleValue()));
         } else if (allString) {
            tag.add(class_2519.method_23256(value.toString()));
         } else if (allMap) {
            tag.add(toTag(value));
         } else {
            tag.add(class_2519.method_23256(value.toString()));
         }
      }

      return tag;
   }

   private static Object fromTag(class_2520 tag) {
      if (tag == null) {
         return null;
      }

      switch (tag.method_10711()) {
         case 1:
            return ((class_2514) tag).method_10698() != 0;
         case 2:
            return (int) ((class_2514) tag).method_10696();
         case 3:
            return ((class_2514) tag).method_10701();
         case 4:
            return ((class_2514) tag).method_10699();
         case 5:
            return ((class_2514) tag).method_10700();
         case 6:
            return ((class_2514) tag).method_10697();
         case 8:
            return ((class_2519) tag).method_10714();
         case 9: {
            class_2499 list = (class_2499) tag;
            List<Object> values = new ArrayList<Object>(list.size());

            for (int i = 0; i < list.size(); ++i) {
               Object value = fromTag(list.method_10534(i));

               if (value != null) {
                  values.add(value);
               }
            }

            return values;
         }
         case 10: {
            class_2487 compound = (class_2487) tag;
            Map<String, Object> values = new LinkedHashMap<String, Object>();

            for (String key : compound.method_10541()) {
               Object value = fromTag(compound.method_10580(key));

               if (value != null) {
                  values.put(key, value);
               }
            }

            return values;
         }
         default:
            return tag.toString();
      }
   }

   private static Object getConfigValue(Map<String, Object> parameters, String key) {
      if (parameters == null) {
         return null;
      }

      Object value = parameters.get(CONFIG_PREFIX + key);

      return value != null ? value : parameters.get(key);
   }

   private static Boolean getBoolean(Map<String, Object> parameters, String key) {
      Object value = getConfigValue(parameters, key);

      if (value instanceof Boolean) {
         return (Boolean) value;
      }

      if (value instanceof Number) {
         return ((Number) value).doubleValue() != 0.0D;
      }

      if (value instanceof CharSequence) {
         String text = value.toString().trim();

         if ("true".equalsIgnoreCase(text)) {
            return true;
         }

         if ("false".equalsIgnoreCase(text)) {
            return false;
         }
      }

      return null;
   }

   private static Integer getInteger(Map<String, Object> parameters, String key) {
      Object value = getConfigValue(parameters, key);

      if (value instanceof Number) {
         return ((Number) value).intValue();
      }

      if (value instanceof Boolean) {
         return (Boolean) value ? 1 : 0;
      }

      if (value instanceof CharSequence) {
         try {
            return Integer.parseInt(value.toString().trim());
         } catch (NumberFormatException ignored) {
            return null;
         }
      }

      return null;
   }

   private static Double getDouble(Map<String, Object> parameters, String key) {
      Object value = getConfigValue(parameters, key);

      if (value instanceof Number) {
         return ((Number) value).doubleValue();
      }

      if (value instanceof Boolean) {
         return (Boolean) value ? 1.0D : 0.0D;
      }

      if (value instanceof CharSequence) {
         try {
            return Double.parseDouble(value.toString().trim());
         } catch (NumberFormatException ignored) {
            return null;
         }
      }

      return null;
   }

   private static String getString(Map<String, Object> parameters, String key) {
      Object value = getConfigValue(parameters, key);

      return value == null ? null : value.toString();
   }
}
