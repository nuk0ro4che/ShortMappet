package mchorse.mappet.api.dialogues.nodes;

import mchorse.mappet.api.dialogues.DialogueContext;
import mchorse.mappet.api.events.EventContext;
import mchorse.mappet.api.events.nodes.EventBaseNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;

public class CraftingNode extends EventBaseNode {
   public String table = "";

   public int execute(EventContext context) {
      if (context instanceof DialogueContext) {
         ((DialogueContext)context).setCrafting(this);
      }

      return -1;
   }

   @Environment(EnvType.CLIENT)
   protected String getDisplayTitle() {
      return this.table;
   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      tag.method_10582("CraftingTable", this.table);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("CraftingTable")) {
         this.table = tag.method_10558("CraftingTable");
      }

   }
}
