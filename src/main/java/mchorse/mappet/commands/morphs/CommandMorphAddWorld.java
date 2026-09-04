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
import net.minecraft.class_1309;
import net.minecraft.class_2168;
import net.minecraft.class_243;
import net.minecraft.class_2522;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class CommandMorphAddWorld extends MappetCommandBase {
   public String getName() {
      return "world";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.morph.add.world";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}morph add world{r} {7}<expiration> <x> <y> <z> <yaw> <pitch> <morph>{r}";
   }

   public int getRequiredArgs() {
      return 7;
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      class_243 pos = sender.method_9222();
      double baseYaw = (double)0.0F;
      double basePitch = (double)0.0F;
      if (sender.method_9228() instanceof class_1309) {
         class_1309 entity = (class_1309)sender.method_9228();
         baseYaw = (double)entity.method_36454();
         basePitch = (double)entity.method_36455();
      }

      int expiration = CommandBase.parseInt(args[0]);
      double x = CommandBase.parseDouble(pos.field_1352, args[1], false);
      double y = CommandBase.parseDouble(pos.field_1351, args[2], false);
      double z = CommandBase.parseDouble(pos.field_1350, args[3], false);
      float yaw = (float)CommandBase.parseDouble(baseYaw, args[4], false);
      float pitch = (float)CommandBase.parseDouble(basePitch, args[5], false);
      String nbt = Strings.join(SubCommandBase.dropFirstArguments(args, 6), " ");

      AbstractMorph morph;
      try {
         morph = MorphManager.INSTANCE.morphFromNBT(class_2522.method_10718(nbt));
      } catch (Exception var24) {
         throw new CommandException("morph.nbt", new Object[0]);
      }

      if (morph == null) {
         throw new CommandException("morph.unrecognized", new Object[]{nbt});
      } else {
         WorldMorph worldMorph = new WorldMorph();
         worldMorph.expiration = expiration;
         worldMorph.x = x;
         worldMorph.y = y;
         worldMorph.z = z;
         worldMorph.yaw = yaw;
         worldMorph.pitch = pitch;
         worldMorph.morph = morph;
         PacketWorldMorph message = new PacketWorldMorph(worldMorph);

         for(class_3222 player : server.method_3760().method_14571()) {
            if (player.method_37908() == sender.method_9225() && player.method_5649(x, y, z) <= (double)4096.0F) {
               Dispatcher.sendTo(message, player);
            }
         }

      }
   }
}
