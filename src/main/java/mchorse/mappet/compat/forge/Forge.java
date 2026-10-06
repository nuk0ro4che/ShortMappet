package mchorse.mappet.compat.forge;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Forge detection and class resolution. Forge isn't on the classpath of the build, so its classes
 * are looked up through the mod classloader (which can see Forge's own layer) with the thread
 * context classloader as a fallback. Everything returns null or false on loaders without Forge,
 * so scripts stay portable.
 */
public final class Forge {
   private static final Map<String, Class<?>> CLASSES = new ConcurrentHashMap<>();
   private static final Map<String, Method> METHODS = new ConcurrentHashMap<>();
   private static final Map<String, Field> FIELDS = new ConcurrentHashMap<>();

   private Forge() {
   }

   public static boolean isAvailable() {
      return mappet$class("net.minecraftforge.common.capabilities.Capability") != null;
   }

   /** Version of the Forge loader, or an empty string when Forge isn't loaded */
   public static String getVersion() {
      Class<?> modList = mappet$class("net.minecraftforge.fml.ModList");

      if (modList == null) {
         return "";
      }

      try {
         Object list = modList.getMethod("get").invoke(null);
         Object container = modList.getMethod("getModContainerById", String.class).invoke(list, "forge");

         if (container == null) {
            return "";
         }

         Object version = container.getClass().getMethod("getVersion").invoke(container);

         return version == null ? "" : String.valueOf(version);
      } catch (Throwable e) {
         return "";
      }
   }

   public static Class<?> mappet$class(String name) {
      Class<?> cached = CLASSES.get(name);

      if (cached != null) {
         return cached;
      }

      Class<?> resolved = null;

      try {
         resolved = Class.forName(name, false, Forge.class.getClassLoader());
      } catch (Throwable e) {
         ClassLoader context = Thread.currentThread().getContextClassLoader();

         if (context != null) {
            try {
               resolved = Class.forName(name, false, context);
            } catch (Throwable ignored) {
               /* Forge isn't installed */
            }
         }
      }

      if (resolved != null) {
         CLASSES.put(name, resolved);
      }

      return resolved;
   }

   /** Looks up a method by name and parameter types, returns {@code null} when there is none */
   public static Method mappet$method(Class<?> owner, String name, Class<?>... parameters) {
      String key = owner.getName() + "#" + name;

      for (int i = 0; i < parameters.length; i++) {
         key += "#" + parameters[i].getName();
      }

      Method cached = METHODS.get(key);

      if (cached != null) {
         return cached;
      }

      Method found = null;

      try {
         found = owner.getMethod(name, parameters);
      } catch (Throwable e) {
         for (Method method : owner.getMethods()) {
            if (!method.getName().equals(name) || method.getParameterCount() != parameters.length) {
               continue;
            }

            Class<?>[] types = method.getParameterTypes();
            boolean matches = true;

            for (int i = 0; i < types.length && matches; i++) {
               matches = types[i].isAssignableFrom(parameters[i]);
            }

            if (matches) {
               found = method;

               break;
            }
         }
      }

      if (found != null) {
         METHODS.put(key, found);
      }

      return found;
   }

   /**
    * Looks up a method by name and argument count, ignoring the parameter types. Needed for
    * interfaces with generics, like Forge's {@code EventBus.post(IEvent)}, where the erased
    * signature isn't known at compile time.
    */
   public static Method mappet$methodLoose(Class<?> owner, String name, int arity) {
      String key = owner.getName() + "#loose#" + name + "#" + arity;
      Method cached = METHODS.get(key);

      if (cached != null) {
         return cached;
      }

      Method found = null;

      for (Method method : owner.getMethods()) {
         if (!method.getName().equals(name) || method.getParameterCount() != arity) {
            continue;
         }

         boolean matches = true;

         for (Class<?> type : method.getParameterTypes()) {
            matches = matches && !type.isPrimitive();
         }

         if (matches) {
            found = method;

            break;
         }
      }

      if (found != null) {
         METHODS.put(key, found);
      }

      return found;
   }

   /** Looks up a static field by name, returns {@code null} when there is none */
   public static Field mappet$field(Class<?> owner, String name) {
      String key = owner.getName() + "#" + name;
      Field cached = FIELDS.get(key);

      if (cached != null) {
         return cached;
      }

      Field found = null;

      try {
         found = owner.getField(name);
      } catch (Throwable e) {
         /* Not a public field */
      }

      if (found != null) {
         FIELDS.put(key, found);
      }

      return found;
   }

   public static Object mappet$enum(Class<?> owner, String name) {
      if (owner == null || name == null || !owner.isEnum()) {
         return null;
      }

      for (Object constant : owner.getEnumConstants()) {
         if (((Enum<?>) constant).name().equalsIgnoreCase(name)) {
            return constant;
         }
      }

      return null;
   }
}