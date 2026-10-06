package mchorse.mappet.api.scripts.user.forge;

import java.util.List;


/**
 * Forge bridge for scripts. Everything is optional: on loaders without Forge
 * {@link #isAvailable()} returns false and the other methods return null or empty results, so
 * scripts can use it without breaking on Fabric or Quilt.
 *
 * <pre>
 * var TemperatureProvider = s.forge.type("sfiomn.legendarysurvivaloverhaul.common.capabilities.temperature.TemperatureProvider");
 * var cap = s.forge.capability(player, TemperatureProvider.TEMPERATURE_CAPABILITY);
 *
 * if (cap) cap.setTemperatureLevel(15.0);
 * </pre>
 */
public interface IScriptForge {
   boolean isAvailable();

   /** Version of the Forge loader, or an empty string when Forge isn't loaded */
   String getVersion();

   /**
    * Resolves a class by name, the same as Java.type, but through the loader that can see Forge
    * and its mods
    */
   Object type(String className);

   /**
    * Resolves a Forge capability of an entity
    *
    * @param entity Minecraft entity or a Mappet entity wrapper
    * @param capability Forge {@code Capability} instance, usually a static field
    *
    * @return the capability value or {@code null} when it's absent
    */
   Object capability(Object entity, Object capability);

   boolean hasCapability(Object entity, Object capability);

   /** Looks a capability up in Forge's registry first, e.g. by "lsо:temperature" */
   Object capability(String id);

   Object capability(Object entity, String id);

   /** All capability values an entity currently provides */
   List<Object> capabilities(Object entity);

   /** Resource location of a capability, e.g. "lsо:temperature" */
   String capabilityId(Object capability);

   /** Forge's event bus, or null when Forge isn't loaded */
   Object getBus();

   /** Creates and posts a Forge event, e.g. {@code s.forge.post("net.minecraftforge.event.entity.player.PlayerEvent$Clone", player, false)} */
   Object post(String eventClass);

   Object post(String eventClass, Object arg);

   Object post(String eventClass, Object arg1, Object arg2);

   Object post(String eventClass, Object[] args);

   /** Creates an event instance without posting it */
   Object construct(String eventClass, Object[] args);

   /**
    * Registers a listener for a Forge event
    *
    * @param eventClass fully qualified event class name
    * @param callback Nashorn function or Java consumer, called with the event
    * @param priority Forge priority name (highest, high, normal, low, lowest) or null
    * @param receiveCanceled whether the listener should also receive canceled events
    *
    * @return the listener, which can be passed to {@link #removeListener(Object)}, or null on failure
    */
   Object addListener(String eventClass, Object callback, String priority, boolean receiveCanceled);

   Object addListener(String eventClass, Object callback);

   boolean removeListener(Object listener);
}