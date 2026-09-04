package mchorse.mappet.api.utils.nodes;

import net.minecraft.class_2487;

public class NodeUtils {
   public static <T extends Node> T nodeFromNBT(NodeSystem<T> system, class_2487 tag) {
      String type = tag.method_10558("Type");
      T node = system.getFactory().create(type);
      node.deserializeNBT(tag);
      return node;
   }

   public static <T extends Node> class_2487 nodeToNBT(NodeSystem<T> system, T node) {
      class_2487 tag = node.serializeNBT();
      tag.method_10582("Type", system.getFactory().getType(node));
      return tag;
   }
}
