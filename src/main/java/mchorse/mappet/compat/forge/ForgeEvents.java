package mchorse.mappet.compat.forge;

import java.lang.reflect.Constructor;
import java.util.function.Consumer;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;


/**
 * Forge event bus for scripts. Listeners are registered from Java, so scripts can react to any
 * Forge event without knowing its generic signature, and callbacks are accepted both as Java
 * consumers and as plain Nashorn functions.
 */
public final class ForgeEvents {
   private ForgeEvents() {
   }

   public static boolean isAvailable() {
      return Forge.mappet$class("net.minecraftforge.common.MinecraftForge") != null;
   }

   public static Object getBus() {
      try {
         return Forge.mappet$field(Forge.mappet$class("net.minecraftforge.common.MinecraftForge"), "EVENT_BUS").get(null);
      } catch (Throwable e) {
         return null;
      }
   }

   /** Creates an event instance by class name, matching the arguments against the constructors */
   public static Object construct(String className, Object[] args) {
      Class<?> event = Forge.mappet$class(className);

      if (event == null) {
         return null;
      }

      return mappet$construct(event, args);
   }

   /** Posts an event, constructing it from the class name and the given arguments */
   public static Object post(String className, Object[] args) {
      Object bus = getBus();
      Object event = construct(className, args);

      if (bus == null || event == null) {
         return null;
      }

      try {
         return Forge.mappet$methodLoose(bus.getClass(), "post", 1).invoke(bus, event);
      } catch (Throwable e) {
         return null;
      }
   }

   /**
    * Registers a listener for a Forge event.
    *
    * @param eventClass fully qualified event class name
    * @param callback Nashorn function or Java consumer, called with the event
    * @param priority Forge priority name (highest, high, normal, low, lowest) or null
    * @param receiveCanceled whether the listener should also receive canceled events
    *
    * @return the listener, which can be passed to {@link #removeListener(Object)}, or null on failure
    */
   public static Object addListener(String eventClass, Object callback, String priority, boolean receiveCanceled) {
      Object bus = getBus();
      Class<?> event = Forge.mappet$class(eventClass);

      if (bus == null || event == null || callback == null) {
         return null;
      }

      Consumer<Object> consumer = mappet$consumer(callback);
      Object priorityConstant = mappet$priority(priority);
      Object registered = null;

      try {
         if (priorityConstant == null) {
            registered = Forge.mappet$method(bus.getClass(), "addListener", Class.class, Consumer.class).invoke(bus, event, consumer);
         } else {
            registered = Forge.mappet$method(bus.getClass(), "addListener", priorityConstant.getClass(), boolean.class, Class.class, Consumer.class).invoke(bus, priorityConstant, receiveCanceled, event, consumer);
         }
      } catch (Throwable e) {
         return null;
      }

      return mappet$listener(consumer, registered);
   }

   public static boolean removeListener(Object listener) {
      Object bus = getBus();
      Consumer<Object> consumer = mappet$consumerOf(listener);

      if (bus == null || consumer == null) {
         return false;
      }

      try {
         Forge.mappet$method(bus.getClass(), "removeListener", Consumer.class).invoke(bus, consumer);

         return true;
      } catch (Throwable e) {
         return false;
      }
   }

   private static Consumer<Object> mappet$consumerOf(Object listener) {
      if (listener instanceof Consumer) {
         return (Consumer<Object>) listener;
      }

      if (listener instanceof Listener) {
         return ((Listener) listener).mappet$consumer;
      }

      return null;
   }

   /** Keeps the consumer alive, because Forge only removes listeners by the object it was given */
   private static Object mappet$listener(Consumer<Object> consumer, Object registered) {
      return registered == null ? consumer : new Listener(consumer);
   }

   private static Object mappet$priority(String priority) {
      if (priority == null) {
         return null;
      }

      return Forge.mappet$enum(Forge.mappet$class("net.minecraftforge.eventbus.api.EventPriority"), priority);
   }

   private static Consumer<Object> mappet$consumer(Object callback) {
      if (callback instanceof Consumer) {
         return (Consumer<Object>) callback;
      }

      if (callback instanceof ScriptObjectMirror) {
         ScriptObjectMirror mirror = (ScriptObjectMirror) callback;

         if (mirror.isFunction()) {
            return (event) -> mappet$call(mirror, event);
         }
      }

      if (callback instanceof java.util.function.Function) {
         return (event) -> ((java.util.function.Function<Object, Object>) callback).apply(event);
      }

      return null;
   }

   private static Object mappet$call(ScriptObjectMirror mirror, Object event) {
      try {
         return mirror.call("call", mirror, new Object[]{event});
      } catch (Throwable e) {
         return null;
      }
   }

   private static Object mappet$construct(Class<?> event, Object[] args) {
      Object[] values = args == null ? new Object[0] : args;
      int arity = values.length;

      for (Constructor<?> constructor : event.getConstructors()) {
         Class<?>[] types = constructor.getParameterTypes();

         if (types.length != arity) {
            continue;
         }

         Object[] call = new Object[arity];
         boolean matches = true;

         for (int i = 0; i < arity && matches; i++) {
            Object value = values[i];

            if (value == null) {
               matches = !types[i].isPrimitive();
            } else if (types[i].isPrimitive()) {
               matches = mappet$primitive(types[i], value);
            } else {
               matches = types[i].isInstance(value);
            }

            call[i] = value;
         }

         if (matches) {
            try {
               return constructor.newInstance(call);
            } catch (Throwable e) {
               /* Try the next constructor */
            }
         }
      }

      return null;
   }

   private static boolean mappet$primitive(Class<?> type, Object value) {
      if (type == boolean.class) {
         return value instanceof Boolean;
      }

      if (type == int.class) {
         return value instanceof Number;
      }

      if (type == long.class || type == double.class || type == float.class || type == short.class || type == byte.class) {
         return value instanceof Number;
      }

      if (type == char.class) {
         return value instanceof Character;
      }

      return false;
   }

   /** Holder that keeps the consumer reachable for {@link #removeListener(Object)} */
   public static final class Listener {
      private final Consumer<Object> mappet$consumer;

      private Listener(Consumer<Object> consumer) {
         this.mappet$consumer = consumer;
      }

      public Consumer<Object> getConsumer() {
         return this.mappet$consumer;
      }
   }
}