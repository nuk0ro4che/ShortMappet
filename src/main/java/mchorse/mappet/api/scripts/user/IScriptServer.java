package mchorse.mappet.api.scripts.user;

import java.util.List;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import mchorse.mappet.api.scripts.user.entities.IScriptPlayer;
import mchorse.mappet.api.scripts.user.mappet.IMappetStates;
import net.minecraft.server.MinecraftServer;

public interface IScriptServer {
   MinecraftServer getMinecraftServer();

   IScriptWorld getWorld(int var1);

   List<IScriptEntity> getEntities(String var1);

   IScriptEntity getEntity(String var1);

   List<IScriptPlayer> getAllPlayers();

   List<IScriptPlayer> getOP();

   IScriptPlayer getRandomPlayer();

   IScriptPlayer getPlayer(String var1);

   default boolean isOnline(String username) {
      return this.getPlayer(username) != null;
   }

   IMappetStates getStates();

   boolean entityExists(String var1);

   void executeScript(String var1);

   void executeScript(String var1, String var2);

   void executeScript(String var1, String var2, Object... var3);
}
