package mchorse.mappet.compat.forge;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_2960;


/**
 * Forge capabilities for scripts. The whole bridge is reflective, so nothing here depends on Forge
 * being present, and the lazy optional every Forge capability is wrapped in is resolved on the Java
 * side, where its API is known.
 */
public final class ForgeCapabilities {
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
      if (entity == null || capability == null) {
         return null;
      }

      Method getCapability = Forge.mappet$method(entity.getClass(), "getCapability", Forge.mappet$class("net.minecraftforge.common.capabilities.Capability"), null);

      if (getCapability == null) {
         /* Fall back to any overload that takes something and a side */
         getCapability = mappet$findGetCapability(entity.getClass());
      }

      if (getCapability == null) {
         return null;
      }

      try {
         return mappet$resolve(getCapability.invoke(entity, capability, null));
      } catch (Throwable e) {
         return null;
      }
   }

   public static boolean has(Object entity, Object capability) {
      return get(entity, capability) != null;
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

         Class<?> parameter = method.getParameterTypes()[0];

         if (parameter.isInterface() && parameter.getSimpleName().equals("Capability")) {
            return method;
         }
      }

      return null;
   }

   /** Modern Forge resolves a {@code LazyOptional} through {@code resolve()}, older versions had {@code get()} */
   private static Object mappet$resolve(Object lazy) throws Exception {
      if (lazy == null) {
         return null;
      }

      try {
         Object optional = lazy.getClass().getMethod("resolve").invoke(lazy);

         if (optional instanceof Optional) {
            return ((Optional) optional).orElse(null);
         }
      } catch (NoSuchMethodException e) {
         /* Not a lazy optional */
      }

      try {
         return lazy.getClass().getMethod("get").invoke(lazy);
      } catch (NoSuchMethodException e) {
         return lazy;
      }
   }
}