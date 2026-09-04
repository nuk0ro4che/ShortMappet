package mchorse.mappet.commands.morphs;

import joptsimple.internal.Strings;
import mchorse.mappet.client.morphs.WorldMorph;
import mchorse.mappet.commands.MappetCommandBase;
import mchorse.mappet.compat.CommandBase;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketWorldMorph;
import mchorse.mclib.commands.SubCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.class_2522;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class CommandMorphAddEntity extends MappetCommandBase {
   public String getName() {
      return "entity";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.morph.add.entity";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}morph add entity{r} {7}<target> <expiration> <rotate> <x> <y> <z> <yaw> <pitch> <morph>{r}";
   }

   public int getRequiredArgs() {
      return 9;
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      int expiration = CommandBase.parseInt(args[1]);
      boolean rotate = CommandBase.parseBoolean(args[2]);
      double x = CommandBase.parseDouble(args[3]);
      double y = CommandBase.parseDouble(args[4]);
      double z = CommandBase.parseDouble(args[5]);
      float yaw = (float)CommandBase.parseDouble(args[6]);
      float pitch = (float)CommandBase.parseDouble(args[7]);
      String nbt = Strings.join(SubCommandBase.dropFirstArguments(args, 8), " ");

      AbstractMorph morph;
      try {
         morph = MorphManager.INSTANCE.morphFromNBT(class_2522.method_10718(nbt));
      } catch (Exception var19) {
         throw new CommandException("morph.nbt", new Object[0]);
      }

      if (morph == null) {
         throw new CommandException("morph.unrecognized", new Object[]{nbt});
      } else {
         for(class_1297 entity : getEntities(server, sender, args[0])) {
            WorldMorph worldMorph = new WorldMorph();
            worldMorph.expiration = expiration;
            worldMorph.rotate = rotate;
            worldMorph.x = x;
            worldMorph.y = y;
            worldMorph.z = z;
            worldMorph.yaw = yaw;
            worldMorph.pitch = pitch;
            worldMorph.morph = morph;
            worldMorph.entity = entity;
            PacketWorldMorph message = new PacketWorldMorph(worldMorph);
            Dispatcher.sendToTracked(entity, message);
            if (entity instanceof class_3222) {
               Dispatcher.sendTo(message, (class_3222)entity);
            }
         }
      }
   }
}
