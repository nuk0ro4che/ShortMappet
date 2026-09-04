package mchorse.mappet.api.utils.factory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MapFactory<T> implements IFactory<T> {
   private BiMap<String, Class<? extends T>> factory = HashBiMap.create();
   private Map<String, String> aliases = new HashMap();
   private Map<Class<? extends T>, Integer> colors = new HashMap();

   public MapFactory<T> copy() {
      MapFactory<T> factory = new MapFactory<T>();

      for(Map.Entry<String, Class<? extends T>> entry : this.factory.entrySet()) {
         factory.register((String)entry.getKey(), (Class)entry.getValue(), (Integer)this.colors.get(entry.getValue()));
      }

      factory.aliases.putAll(this.aliases);
      return factory;
   }

   public MapFactory<T> register(String type, Class<? extends T> clazz, int color) {
      this.factory.put(type, clazz);
      this.colors.put(clazz, color);
      return this;
   }

   public MapFactory<T> alias(String type, String alias) {
      this.aliases.put(alias, type);
      return this;
   }

   public MapFactory<T> unregister(String key) {
      Class<? extends T> clazz = (Class)this.factory.remove(key);
      this.colors.remove(clazz);
      return this;
   }

   public String getType(T node) {
      String type = (String)this.factory.inverse().get(node.getClass());
      if (type != null) {
         return type;
      } else {
         throw new IllegalStateException("Node " + String.valueOf(node.getClass()) + " is not part of event node system!");
      }
   }

   public T create(String type) {
      Class<? extends T> clazz = (Class)this.factory.get(type);
      if (clazz == null) {
         clazz = (Class)this.factory.get(this.aliases.get(type));
      }

      if (clazz != null) {
         try {
            return (T)clazz.getConstructor().newInstance();
         } catch (Exception var4) {
         }
      }

      throw new IllegalStateException("Node type " + type + " is not part of event node system!");
   }

   public int getColor(T object) {
      Integer color = (Integer)this.colors.get(object.getClass());
      return color == null ? 0 : color;
   }

   public int getColor(String type) {
      Integer color = (Integer)this.colors.get(this.factory.get(type));
      return color == null ? 0 : color;
   }

   public Collection<String> getKeys() {
      return this.factory.keySet();
   }
}
