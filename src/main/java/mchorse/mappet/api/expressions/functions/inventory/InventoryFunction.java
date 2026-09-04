package mchorse.mappet.api.expressions.functions.inventory;

import com.google.common.collect.ImmutableList;
import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.compat.CommandBase;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.SNFunction;
import net.minecraft.class_1657;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public abstract class InventoryFunction extends SNFunction {
   public InventoryFunction(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   protected void verifyArgument(int index, IValue value) {
      if (index != 2) {
         super.verifyArgument(index, value);
      }

   }

   public int getRequiredArguments() {
      return 1;
   }

   public double doubleValue() {
      String id = this.getArg(0).stringValue();
      String target = this.args.length > 1 ? this.getArg(1).stringValue() : null;
      boolean all = this.args.length > 2 && this.getArg(2).booleanValue();
      List<class_3222> players = null;

      try {
         if (target != null) {
            MinecraftServer server = Mappet.expressions.getServer();
            players = CommandBase.getPlayers(server, server, target);
         }
      } catch (Exception var9) {
      }

      if (players == null && Mappet.expressions.context.subject instanceof class_3222) {
         players = ImmutableList.of((class_3222)Mappet.expressions.context.subject);
      }

      if (players != null) {
         int i = 0;

         for(class_3222 player : players) {
            boolean has = this.isTrue(id, player);
            if (has) {
               if (!all) {
                  return (double)1.0F;
               }

               ++i;
            }
         }

         return i == players.size() ? (double)1.0F : (double)0.0F;
      } else {
         return (double)0.0F;
      }
   }

   protected abstract boolean isTrue(String var1, class_1657 var2);
}
