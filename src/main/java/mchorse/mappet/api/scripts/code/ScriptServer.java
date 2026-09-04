package mchorse.mappet.api.scripts.code;

import com.mojang.brigadier.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.script.ScriptException;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.scripts.code.entities.ScriptEntity;
import mchorse.mappet.api.scripts.code.entities.ScriptPlayer;
import mchorse.mappet.api.scripts.code.mappet.MappetStates;
import mchorse.mappet.api.scripts.user.IScriptServer;
import mchorse.mappet.api.scripts.user.IScriptWorld;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import mchorse.mappet.api.scripts.user.entities.IScriptPlayer;
import mchorse.mappet.api.scripts.user.mappet.IMappetStates;
import mchorse.mappet.api.utils.DataContext;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2300;
import net.minecraft.class_2303;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class ScriptServer implements IScriptServer {
   private MinecraftServer server;
   private IMappetStates states;

   public ScriptServer(MinecraftServer server) {
      this.server = server;
   }

   public MinecraftServer getMinecraftServer() {
      return this.server;
   }

   public IScriptWorld getWorld(int dimension) {
      return new ScriptWorld(this.server.method_3847(dimension == -1 ? class_1937.field_25180 : (dimension == 1 ? class_1937.field_25181 : class_1937.field_25179)));
   }

   public List<IScriptEntity> getEntities(String targetSelector) {
      List<IScriptEntity> entities = new ArrayList();

      try {
         class_2300 selector = (new class_2303(new StringReader(targetSelector), true)).method_9882();

         for(class_1297 entity : selector.method_9816(this.server.method_3739())) {
            entities.add(ScriptEntity.create(entity));
         }
      } catch (Exception var6) {
      }

      return entities;
   }

   public IScriptEntity getEntity(String uuid) {
      return ScriptEntity.create(this.findEntity(UUID.fromString(uuid)));
   }

   public List<IScriptPlayer> getAllPlayers() {
      List<IScriptPlayer> entities = new ArrayList();

      for(class_3222 player : this.server.method_3760().method_14571()) {
         entities.add(new ScriptPlayer(player));
      }

      return entities;
   }

   public List<IScriptPlayer> getOP() {
      List<IScriptPlayer> operators = new ArrayList();

      for (class_3222 player : this.server.method_3760().method_14571()) {
         if (this.server.method_3760().method_14569(player.method_7334())) {
            operators.add(new ScriptPlayer(player));
         }
      }

      return operators;
   }

   public IScriptPlayer getRandomPlayer() {
      List<class_3222> players = this.server.method_3760().method_14571();
      if (players.isEmpty()) {
         return null;
      }

      class_3222 player = players.get(ThreadLocalRandom.current().nextInt(players.size()));
      return new ScriptPlayer(player);
   }

   public IScriptPlayer getPlayer(String username) {
      class_3222 player = this.server.method_3760().method_14566(username);
      return player != null ? new ScriptPlayer(player) : null;
   }

   public IMappetStates getStates() {
      if (this.states == null) {
         this.states = new MappetStates(Mappet.states);
      }

      return this.states;
   }

   public boolean entityExists(String uuid) throws IllegalArgumentException {
      try {
         UUID parsedUuid = UUID.fromString(uuid);
         return this.findEntity(parsedUuid) != null;
      } catch (IllegalArgumentException ex) {
         throw new IllegalArgumentException("Invalid UUID string: " + uuid, ex);
      }
   }

   private class_1297 findEntity(UUID uuid) {
      for(class_3218 world : this.server.method_3738()) {
         class_1297 entity = world.method_14190(uuid);
         if (entity != null) {
            return entity;
         }
      }

      return null;
   }

   public void executeScript(String scriptName) {
      this.executeScript(scriptName, "main");
   }

   public void executeScript(String scriptName, String function) {
      DataContext context = new DataContext(this.server);

      try {
         Mappet.scripts.execute(scriptName, function, context);
      } catch (ScriptException var6) {
         String fileName = var6.getFileName() == null ? scriptName : var6.getFileName();
         var6.printStackTrace();
         throw new RuntimeException("Script Error: " + fileName + " - Line: " + var6.getLineNumber() + " - Column: " + var6.getColumnNumber() + " - Message: " + var6.getMessage(), var6);
      } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException("Script Empty: " + scriptName + " - Error: " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
      }
   }

   public void executeScript(String scriptName, String function, Object... args) {
      DataContext context = new DataContext(this.server);

      try {
         Mappet.scripts.execute(scriptName, function, context, args);
      } catch (ScriptException var7) {
         String fileName = var7.getFileName() == null ? scriptName : var7.getFileName();
         var7.printStackTrace();
         throw new RuntimeException("Script Error: " + fileName + " - Line: " + var7.getLineNumber() + " - Column: " + var7.getColumnNumber() + " - Message: " + var7.getMessage(), var7);
      } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException("Script Empty: " + scriptName + " - Error: " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
      }
   }
}
