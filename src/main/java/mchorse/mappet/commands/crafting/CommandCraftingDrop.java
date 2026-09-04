package mchorse.mappet.commands.crafting;

import java.util.Collections;
import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.crafting.CraftingRecipe;
import mchorse.mappet.api.crafting.CraftingTable;
import mchorse.mappet.compat.CommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_1542;
import net.minecraft.class_1799;
import net.minecraft.class_2168;
import net.minecraft.class_243;
import net.minecraft.server.MinecraftServer;

public class CommandCraftingDrop extends CommandCraftingBase {
   public String getName() {
      return "drop";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.crafting.drop";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}crafting drop{r} {7}<id> [index] [x] [y] [z] [mx] [my] [mz]{r}";
   }

   public int getRequiredArgs() {
      return 1;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      String id = args[0];
      CraftingTable table = this.getCraftingTable(id);
      if (table.recipes.isEmpty()) {
         throw new CommandException("crafting.empty", new Object[]{id});
      } else {
         int index = (int)Math.min(Math.round(Math.random() * (double)table.recipes.size()), (long)(table.recipes.size() - 1));
         if (args.length > 1 && !args[1].equals("@r")) {
            index = CommandBase.parseInt(args[1], 0, table.recipes.size() - 1);
         }

         class_243 pos = sender.method_9222();
         double x = args.length > 2 ? CommandBase.parseDouble(pos.field_1352, args[2], false) : pos.field_1352;
         double y = args.length > 3 ? CommandBase.parseDouble(pos.field_1351, args[3], false) : pos.field_1351;
         double z = args.length > 4 ? CommandBase.parseDouble(pos.field_1350, args[4], false) : pos.field_1350;
         CraftingRecipe recipe = (CraftingRecipe)table.recipes.get(index);

         for(class_1799 stack : recipe.output) {
            if (stack == null || stack.method_7960()) {
               throw new CommandException("crafting.empty_output", new Object[]{id, index});
            }
         }

         double mx = args.length > 5 ? CommandBase.parseDouble(args[5]) : (double)0.0F;
         double my = args.length > 6 ? CommandBase.parseDouble(args[6]) : (double)0.0F;
         double mz = args.length > 7 ? CommandBase.parseDouble(args[7]) : (double)0.0F;

         for(int i = 0; i < recipe.output.size(); ++i) {
            class_1799 stack = (class_1799)recipe.output.get(i);
            class_1542 item = new class_1542(sender.method_9225(), x, y, z, stack.method_7972());
            item.method_18800(mx, item.method_18798().field_1351, item.method_18798().field_1350);
            item.method_18800(item.method_18798().field_1352, my, item.method_18798().field_1350);
            item.method_18800(item.method_18798().field_1352, item.method_18798().field_1351, mz);
            if (i > 0) {
               item.method_5762((Math.random() - (double)0.5F) * 0.1, (double)0.0F, (double)0.0F);
               item.method_5762((double)0.0F, (Math.random() - (double)0.5F) * 0.1, (double)0.0F);
               item.method_5762((double)0.0F, (double)0.0F, (Math.random() - (double)0.5F) * 0.1);
            }

            sender.method_9225().method_8649(item);
         }

      }
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      if (args.length == 1) {
         return getListOfStringsMatchingLastWord(args, Mappet.crafting.getKeys());
      } else {
         return args.length == 2 ? getListOfStringsMatchingLastWord(args, new String[]{"@r"}) : Collections.emptyList();
      }
   }
}
