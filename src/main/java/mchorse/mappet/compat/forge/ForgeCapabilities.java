package mchorse.mappet.compat.forge;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import mchorse.mappet.Mappet;
import net.minecraft.class_2960;


/**
 * Forge capabilities for scripts. The whole bridge is reflective, so nothing here depends on Forge
 * being present, and the lazy optional every Forge capability is wrapped in is resolved on the Java
 * side, where its API is known.
 */
public final class ForgeCapabilities {
   private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();

   private ForgeCapabilities() {
   }

   /**
    * @param entity Minecraft entity
    * @param capability Forge {@code Capability} instance, usually a static field like
    *                   {@code TemperatureProvider.TEMPERATURE_CAPABILITY}
    *
    * @return the capability value or {@code null} when it's absent
    */
   public static Object get(Object entity, Object capability) {
      if (capability == null || mappet$isUndefined(capability)) {
         throw new RuntimeException("Forge bridge: capability is "
                 + (capability == null ? "null" : "undefined")
                 + " — the static field wasn't resolved (check its name, e.g. ThirstProvider.THIRST_CAPABILITY)");
      }

      if (entity == null) {
         mappet$warn("null:entity", "Forge bridge: entity is null, capability lookup skipped");

         return null;
      }

      /* Тип берём из самого объекта: под Connector'ом один и тот же класс capability может
         оказаться загруженным дважды, и статичный net.minecraftforge...Capability не совпадёт
         с тем, чего ждёт getCapability() у этой сущности */
      Method getCapability = Forge.mappet$method(entity.getClass(), "getCapability", capability.getClass(), null);

      if (getCapability == null) {
         /* Fall back to any overload that takes something and a side */
         getCapability = mappet$findGetCapability(entity.getClass());
      }

      if (getCapability == null) {
         mappet$warn("method:" + entity.getClass().getName(),
                 "Forge bridge: getCapability() not found on " + entity.getClass().getName());

         return null;
      }

      try {
         Object resolved = mappet$resolve(getCapability.invoke(entity, capability, null));

         if (resolved == null) {
            mappet$warn("absent:" + entity.getClass().getName() + "#" + capability,
                    "Forge bridge: capability " + capability + " is not attached to " + entity.getClass().getName());
         }

         return resolved;
      } catch (Throwable e) {
         mappet$warn("error:" + entity.getClass().getName() + "#" + capability.getClass().getName(),
                 "Forge bridge: getCapability(" + capability + ") failed on " + entity.getClass().getName() + ": " + e);

         return null;
      }
   }

   public static void mappet$warn(String key, String message) {
      if (WARNED.add(key)) {
         Mappet.LOGGER.warn(message);
      }
   }

   /** Nashorn и Rhino отдают непрочитанное поле не как null, а как свой синглтон {@code undefined} */
   public static boolean mappet$isUndefined(Object value) {
      return value != null && value.getClass().getName().endsWith("Undefined");
   }

   public static boolean has(Object entity, Object capability) {
      return get(entity, capability) != null;
   }

   /** Полный разбор lookup'а capability — печатается из скрипта, чтобы понять, где именно обрыв */
   public static String diagnose(Object entity, Object capability) {
      StringBuilder out = new StringBuilder();

      if (entity == null) {
         return "minecraft entity = null\n";
      }

      out.append("entity = ").append(entity.getClass().getName())
              .append(" (loader ").append(mappet$loader(entity.getClass().getClassLoader())).append(")\n");

      if (capability == null || mappet$isUndefined(capability)) {
         return out.append("capability = ").append(capability == null ? "null" : capability.getClass().getName())
                 .append(" — статическое поле не разрешилось (проверь имя поля на классе из forge.type)\n").toString();
      }

      out.append("capability = ").append(capability.getClass().getName())
              .append(" (loader ").append(mappet$loader(capability.getClass().getClassLoader())).append(")\n");

      Class<?> forgeCapability = Forge.mappet$class("net.minecraftforge.common.capabilities.Capability");

      out.append("net.minecraftforge Capability = ")
              .append(forgeCapability == null ? "NOT VISIBLE" : forgeCapability.getName() + " (loader " + mappet$loader(forgeCapability.getClassLoader()) + ")")
              .append('\n');

      Method exact = Forge.mappet$method(entity.getClass(), "getCapability", capability.getClass(), null);

      out.append("getCapability(capability class, side *) = ").append(mappet$describe(exact)).append('\n');

      Method byName = exact == null ? mappet$findGetCapability(entity.getClass()) : null;

      if (byName != null) {
         out.append("getCapability по имени        = ").append(mappet$describe(byName)).append('\n');
      }

      boolean found = false;

      for (Method method : entity.getClass().getMethods()) {
         if (method.getName().equals("getCapability")) {
            found = true;
            out.append("  объявлен: ").append(method).append(" @ ").append(method.getDeclaringClass().getName()).append('\n');
         }
      }

      if (!found) {
         out.append("  методов getCapability на классе вообще нет\n");
      }

      Method use = exact == null ? byName : exact;

      if (use != null) {
         try {
            Object lazy = use.invoke(entity, capability, null);

            out.append("invoke -> ").append(lazy == null ? "null" : lazy.getClass().getName()).append('\n');

            if (lazy != null && lazy.getClass().getName().contains("LazyOptional")) {
               Object resolved = mappet$resolve(lazy);

               out.append("resolve -> ").append(resolved == null ? "null" : resolved.getClass().getName()).append('\n');
            }
         } catch (Throwable e) {
            out.append("invoke бросил -> ").append(e).append('\n');
         }
      }

      return out.toString();
   }

   private static String mappet$describe(Method method) {
      if (method == null) {
         return "NOT FOUND";
      }

      return method.toString() + " @ " + method.getDeclaringClass().getName();
   }

   private static String mappet$loader(ClassLoader loader) {
      return loader == null ? "bootstrap" : String.valueOf(loader);
   }

   /** Looks a capability up in Forge's registry, e.g. by "lsо:temperature" */
   public static Object byId(String id) {
      Object registry = mappet$registry();
      Method getValue = registry == null ? null : Forge.mappet$method(registry.getClass(), "getValue", class_2960.class);

      if (getValue == null && registry != null) {
         getValue = Forge.mappet$method(registry.getClass(), "get", class_2960.class);
      }

      if (getValue == null) {
         return null;
      }

      try {
         return getValue.invoke(registry, new class_2960(id));
      } catch (Throwable e) {
         return null;
      }
   }

   public static Object getById(Object entity, String id) {
      Object capability = byId(id);

      return capability == null ? null : get(entity, capability);
   }

   /** Resource location of a capability, e.g. "lsо:temperature" */
   public static String id(Object capability) {
      Object registry = mappet$registry();
      Method getKey = registry == null ? null : Forge.mappet$method(registry.getClass(), "getKey", Object.class);

      if (getKey == null) {
         return null;
      }

      try {
         Object key = getKey.invoke(registry, capability);

         return key == null ? null : key.toString();
      } catch (Throwable e) {
         return null;
      }
   }

   /** All capability values an entity currently provides */
   public static List<Object> values(Object entity) {
      List<Object> values = new ArrayList<>();

      if (entity == null) {
         return values;
      }

      Method getCapabilities = Forge.mappet$method(entity.getClass(), "getCapabilities");

      if (getCapabilities == null) {
         return values;
      }

      try {
         Iterable<?> capabilities = (Iterable<?>) getCapabilities.invoke(entity);

         if (capabilities == null) {
            return values;
         }

         for (Object capability : capabilities) {
            Object value = get(entity, capability);

            if (value != null) {
               values.add(value);
            }
         }
      } catch (Throwable e) {
         return values;
      }

      return values;
   }

   private static Object mappet$registry() {
      try {
         return Forge.mappet$field(Forge.mappet$class("net.minecraftforge.registries.ForgeRegistries"), "CAPABILITIES").get(null);
      } catch (Throwable e) {
         return null;
      }
   }

   private static Method mappet$findGetCapability(Class<?> entity) {
      for (Method method : entity.getMethods()) {
         if (!method.getName().equals("getCapability") || method.getParameterCount() != 2) {
            continue;
         }

         /* В Forge Capability — обычный класс, а не интерфейс, поэтому важен только тип первого аргумента */
         if (method.getParameterTypes()[0].getSimpleName().equals("Capability")) {
            return method;
         }
      }

      return null;
   }

   /** Modern Forge resolves a {@code LazyOptional} through {@code resolve()}, older versions had {@code get()} */
   private static Object mappet$resolve(Object lazy) {
      if (lazy == null) {
         return null;
      }

      if (lazy instanceof Optional) {
         return ((Optional) lazy).orElse(null);
      }

      /* Это не обёртка, а готовое значение — отдавать его как есть */
      if (!lazy.getClass().getName().contains("LazyOptional")) {
         return lazy;
      }

      Object resolved = mappet$invoke(lazy, "resolve");

      if (resolved != null) {
         return resolved instanceof Optional ? ((Optional) resolved).orElse(null) : resolved;
      }

      Object value = mappet$invoke(lazy, "get");

      if (value != null) {
         return value;
      }

      mappet$warn("unwrap:" + lazy.getClass().getName(),
              "Forge bridge: couldn't unwrap " + lazy.getClass().getName() + " (no resolve()/get())");

      return null;
   }

   /**
    * {@code LazyOptional} держит реализацию в приватном вложении, поэтому метод ищем на
    * публичном предке и зовём через setAccessible — иначе вызов даёт IllegalAccessException
    */
   private static Object mappet$invoke(Object target, String name) {
      for (Class<?> type = target.getClass(); type != null; type = type.getSuperclass()) {
         if (!Modifier.isPublic(type.getModifiers())) {
            continue;
         }

         try {
            Method method = type.getDeclaredMethod(name);

            if (!Modifier.isPublic(method.getModifiers())) {
               continue;
            }

            method.setAccessible(true);

            return method.invoke(target);
         } catch (NoSuchMethodException e) {
            /* Метода нет на этом уровне — поднимаемся по иерархии выше */
         } catch (Throwable e) {
            return null;
         }
      }

      return null;
   }
}