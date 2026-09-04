package mchorse.mappet.commands.states;

import net.minecraft.class_2168;

public class CommandStateSub extends CommandStateAdd {
   public String getName() {
      return "sub";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.state.sub";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}state sub{r} {7}<target> <id> <number>{r}";
   }

   protected double processValue(double value) {
      return -value;
   }
}
