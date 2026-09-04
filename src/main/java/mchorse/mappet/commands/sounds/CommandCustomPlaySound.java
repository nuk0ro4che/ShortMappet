package mchorse.mappet.commands.sounds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mchorse.mappet.client.SoundPack;
import mchorse.mappet.commands.MappetSubCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandCustomPlaySound extends MappetSubCommandBase {
   public String getName() {
      return "playsound";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.playsound";
   }

   public int getRequiredArgs() {
      return 3;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      server.method_3734().method_44252(sender, "playsound " + String.join(" ", args));
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      return args.length == 1 ? new ArrayList<>(SoundPack.getCustomSoundEvents()) : Collections.emptyList();
   }
}
