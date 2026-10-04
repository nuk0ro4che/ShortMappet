package mchorse.mappet.events;

import java.util.function.Function;

import mchorse.mappet.api.scripts.user.ScriptExtensions;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import mchorse.mappet.compat.events.Event;

/**
 * Allows mods to register objects that scripts can get from any entity with
 * entity.get("id"), so addon API doesn't have to be global calls with the entity as an argument.
 */
public class RegisterScriptExtensionsEvent extends Event {
   /**
    * @param id name used in scripts, for example "changed" for player.get("changed")
    * @param provider creates the addon object for a given entity, called on every get
    * @param docType name of the class in the documentation, used by the script editor, for
    *                example "IChangedScript". Pass null if the addon has no documentation.
    */
   public void register(String id, Function<IScriptEntity, Object> provider, String docType) {
      ScriptExtensions.register(id, provider, docType);
   }

   public void unregister(String id) {
      ScriptExtensions.unregister(id);
   }
}