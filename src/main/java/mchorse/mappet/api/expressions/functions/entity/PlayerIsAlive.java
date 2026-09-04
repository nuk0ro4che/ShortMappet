package mchorse.mappet.api.expressions.functions.entity;

import mchorse.mappet.Mappet;
import mchorse.mappet.compat.CommandBase;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.SNFunction;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class PlayerIsAlive extends SNFunction {
   public PlayerIsAlive(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   public int getRequiredArguments() {
      return 1;
   }

   public double doubleValue() {
      try {
         String target = this.getArg(0).stringValue();
         MinecraftServer server = Mappet.expressions.getServer();

         for(class_3222 player : CommandBase.getPlayers(server, server, target)) {
            if (!player.method_5805()) {
               return (double)0.0F;
            }
         }

         return (double)1.0F;
      } catch (Exception var6) {
         return (double)0.0F;
      }
   }
}
