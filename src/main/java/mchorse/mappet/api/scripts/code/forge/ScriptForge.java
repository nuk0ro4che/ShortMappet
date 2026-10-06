package mchorse.mappet.api.scripts.code.forge;

import java.util.List;
import jdk.dynalink.beans.StaticClass;
import mchorse.mappet.api.scripts.code.entities.ScriptEntity;
import mchorse.mappet.api.scripts.user.forge.IScriptForge;
import mchorse.mappet.compat.forge.Forge;
import mchorse.mappet.compat.forge.ForgeCapabilities;
import mchorse.mappet.compat.forge.ForgeEvents;
import net.minecraft.class_1297;


/** Script facing side of the Forge bridge, see {@link IScriptForge} */
public class ScriptForge implements IScriptForge {
   public boolean isAvailable() {
      return Forge.isAvailable();
   }

   public String getVersion() {
      return Forge.getVersion();
   }

   /**
    * Класс Java для скрипта. Возвращает {@code jdk.dynalink.beans.StaticClass} — то же, что возвращает
    * Nashorn'овский {@code Java.type}: с голого {@code java.lang.Class} скрипт не видит статических полей
    * (свойство возвращает {@code undefined}).
    */
   public Object type(String className) {
      Class<?> clazz = Forge.mappet$class(className);

      if (clazz == null) {
         ForgeCapabilities.mappet$warn("type:" + className, "Forge bridge: class not found: " + className);

         return null;
      }

      try {
         return StaticClass.forClass(clazz);
      } catch (Throwable t) {
         return clazz;
      }
   }

   public Object capability(Object entity, Object capability) {
      return ForgeCapabilities.get(mappet$entity(entity), capability);
   }

   /** Диагностика capability: почему lookup вернул null. Печатается через print() из скрипта */
   public String capabilityDebug(Object entity, Object capability) {
      class_1297 minecraftEntity = mappet$entity(entity);

      return "script entity = " + (entity == null ? "null" : entity.getClass().getName())
              + "\nminecraft entity = " + (minecraftEntity == null ? "null" : minecraftEntity.getClass().getName())
              + "\n" + ForgeCapabilities.diagnose(minecraftEntity, capability);
   }

   public boolean hasCapability(Object entity, Object capability) {
      return ForgeCapabilities.has(mappet$entity(entity), capability);
   }

   public Object capability(String id) {
      return ForgeCapabilities.byId(id);
   }

   public Object capability(Object entity, String id) {
      return ForgeCapabilities.getById(mappet$entity(entity), id);
   }

   public List<Object> capabilities(Object entity) {
      return ForgeCapabilities.values(mappet$entity(entity));
   }

   public String capabilityId(Object capability) {
      return ForgeCapabilities.id(capability);
   }

   public Object getBus() {
      return ForgeEvents.getBus();
   }

   public Object post(String eventClass) {
      return ForgeEvents.post(eventClass, new Object[0]);
   }

   public Object post(String eventClass, Object arg) {
      return ForgeEvents.post(eventClass, new Object[]{arg});
   }

   public Object post(String eventClass, Object arg1, Object arg2) {
      return ForgeEvents.post(eventClass, new Object[]{arg1, arg2});
   }

   public Object post(String eventClass, Object[] args) {
      return ForgeEvents.post(eventClass, args);
   }

   public Object construct(String eventClass, Object[] args) {
      return ForgeEvents.construct(eventClass, args);
   }

   public Object addListener(String eventClass, Object callback, String priority, boolean receiveCanceled) {
      return ForgeEvents.addListener(eventClass, callback, priority, receiveCanceled);
   }

   public Object addListener(String eventClass, Object callback) {
      return ForgeEvents.addListener(eventClass, callback, null, false);
   }

   public boolean removeListener(Object listener) {
      return ForgeEvents.removeListener(listener);
   }

   /** Accepts both raw Minecraft entities and Mappet's own entity wrappers */
   private static class_1297 mappet$entity(Object entity) {
      if (entity instanceof class_1297) {
         return (class_1297) entity;
      }

      if (entity instanceof ScriptEntity) {
         return ((ScriptEntity) entity).getMinecraftEntity();
      }

      if (entity instanceof mchorse.mappet.api.scripts.user.entities.IScriptEntity) {
         return ((mchorse.mappet.api.scripts.user.entities.IScriptEntity) entity).getMinecraftEntity();
      }

      return null;
   }
}