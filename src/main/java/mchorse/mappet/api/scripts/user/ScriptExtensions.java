package mchorse.mappet.api.scripts.user;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import mchorse.mappet.events.RegisterScriptExtensionsEvent;

/**
 * Addons can hang their own objects on script entities, so scripts can write
 * "player.get("changed").setForm("changed:latex")" instead of a global call with the entity as an
 * argument. Every registered object is created per entity, so it can keep the entity around and
 * expose methods without it.
 */
public class ScriptExtensions {
   private static Map<String, Function<IScriptEntity, Object>> providers;
   private static Map<String, String> docTypes;

   /** Called by mods, see {@link RegisterScriptExtensionsEvent}. */
   public static void register(String id, Function<IScriptEntity, Object> provider, String docType) {
      providers().put(id, provider);
      docTypes().put(id, docType);
   }

   /** Called by mods, see {@link RegisterScriptExtensionsEvent}. */
   public static void unregister(String id) {
      providers().remove(id);
      docTypes().remove(id);
   }

   public static Object get(IScriptEntity entity, String id) {
      Function<IScriptEntity, Object> provider = providers().get(id);

      return provider == null ? null : provider.apply(entity);
   }

   public static boolean has(String id) {
      return providers().containsKey(id);
   }

   public static Map<String, String> getDocTypes() {
      return docTypes();
   }

   private static Map<String, Function<IScriptEntity, Object>> providers() {
      if (providers == null) {
         providers = new HashMap<>();
         docTypes = new HashMap<>();
         Mappet.EVENT_BUS.post(new RegisterScriptExtensionsEvent());
      }

      return providers;
   }

   private static Map<String, String> docTypes() {
      if (docTypes == null) {
         providers();
      }

      return docTypes;
   }
}