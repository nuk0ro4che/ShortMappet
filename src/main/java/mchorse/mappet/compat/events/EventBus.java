package mchorse.mappet.compat.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class EventBus {
   private final List<Listener> listeners = new ArrayList();

   public synchronized void register(Object owner) {
      this.unregister(owner);

      for(Method method : owner.getClass().getMethods()) {
         SubscribeEvent annotation = (SubscribeEvent)method.getAnnotation(SubscribeEvent.class);
         if (annotation != null && method.getParameterCount() == 1) {
            method.setAccessible(true);
            this.listeners.add(new Listener(owner, method, method.getParameterTypes()[0], annotation.priority()));
         }
      }

      this.listeners.sort(Comparator.<Listener, EventPriority>comparing((listener) -> listener.priority()).reversed());
   }

   public synchronized void unregister(Object owner) {
      this.listeners.removeIf((listener) -> listener.owner == owner);
   }

   public boolean post(Object event) {
      List<Listener> snapshot;
      synchronized(this) {
         snapshot = List.copyOf(this.listeners);
      }

      for(Listener listener : snapshot) {
         if (listener.type.isInstance(event)) {
            try {
               listener.method.invoke(listener.owner, event);
            } catch (ReflectiveOperationException exception) {
               throw new RuntimeException("Mappet event listener failed: " + String.valueOf(listener.method), exception);
            }
         }
      }

      return event instanceof Event && ((Event)event).isCanceled();
   }

   private static record Listener(Object owner, Method method, Class<?> type, EventPriority priority) {
   }
}
