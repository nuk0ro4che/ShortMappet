package mchorse.mappet.api.utils.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import mchorse.mappet.api.utils.AbstractData;
import mchorse.mappet.api.utils.factory.IFactory;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;

public class NodeSystem<T extends Node> extends AbstractData {
   private IFactory<T> factory;
   public Map<UUID, T> nodes = new HashMap();
   public Map<UUID, List<NodeRelation<T>>> relations = new HashMap();
   public T main;

   public NodeSystem(IFactory<T> factory) {
      this.factory = factory;
   }

   public IFactory<T> getFactory() {
      return this.factory;
   }

   public void add(T node) {
      if (node.getId() == null) {
         while(true) {
            UUID id = UUID.randomUUID();
            if (!this.nodes.containsKey(id)) {
               node.setId(id);
               break;
            }
         }
      } else if (this.nodes.containsKey(node.getId())) {
         throw new IllegalStateException("Node by UUID " + String.valueOf(node.getId()) + " is already present in this node system!");
      }

      this.nodes.put(node.getId(), node);
   }

   public boolean tie(T output, T input) {
      if (output == input) {
         return false;
      } else if (this.nodes.containsKey(output.getId()) && this.nodes.containsKey(input.getId()) && !this.hasRelation(output, input)) {
         List<NodeRelation<T>> relations = (List)this.relations.get(output.getId());
         if (relations == null) {
            relations = new ArrayList();
            this.relations.put(output.getId(), relations);
         }

         relations.add(new NodeRelation(output, input));
         return true;
      } else {
         return false;
      }
   }

   public void untie(T output, T input) {
      List<NodeRelation<T>> relations = (List)this.relations.get(output.getId());
      if (relations != null) {
         relations.removeIf((relation) -> relation.input == input);
         if (relations.isEmpty()) {
            this.relations.remove(output.getId());
         }
      }

   }

   public void addTie(T output, T toAdd) {
      this.add(toAdd);
      this.tie(output, toAdd);
   }

   public void addMain(T node) {
      this.add(node);
      this.main = node;
   }

   public boolean remove(T node) {
      UUID key = node.getId();
      if (!this.nodes.containsKey(key)) {
         return false;
      } else {
         this.nodes.remove(key);
         this.relations.remove(node.getId());
         Iterator<Map.Entry<UUID, List<NodeRelation<T>>>> it = this.relations.entrySet().iterator();

         while(it.hasNext()) {
            Map.Entry<UUID, List<NodeRelation<T>>> entry = (Map.Entry)it.next();
            entry.getValue().removeIf((relation) -> relation.input == node);
            if (entry.getValue().isEmpty()) {
               it.remove();
            }
         }

         return true;
      }
   }

   public boolean hasRelation(T output, T input) {
      return this.getRelation(output, input) != null;
   }

   public NodeRelation<T> getRelation(T output, T input) {
      if (!this.relations.containsKey(output.getId())) {
         return null;
      } else {
         for(NodeRelation<T> relation : this.relations.get(output.getId())) {
            if (Objects.equals(relation.input.getId(), input.getId())) {
               return relation;
            }
         }

         return null;
      }
   }

   public List<T> getChildren(T node) {
      List<T> children = new ArrayList();
      if (this.relations.containsKey(node.getId())) {
         for(NodeRelation<T> relation : this.relations.get(node.getId())) {
            if (relation.output == node) {
               children.add(relation.input);
            }
         }
      }

      return children;
   }

   public List<T> getRoots() {
      List<T> roots = new ArrayList();

      label31:
      for(T node : this.nodes.values()) {
         for(List<NodeRelation<T>> relations : this.relations.values()) {
            for(NodeRelation<T> relation : relations) {
               if (relation.input == node) {
                  continue label31;
               }
            }
         }

         roots.add(node);
      }

      return roots;
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      class_2499 nodes = new class_2499();

      for(T node : this.nodes.values()) {
         class_2487 nodeTag = NodeUtils.nodeToNBT(this, node);
         if (this.relations.containsKey(node.getId())) {
            class_2499 relations = new class_2499();

            for(NodeRelation<T> relation : this.relations.get(node.getId())) {
               relations.add(class_2519.method_23256(relation.input.getId().toString()));
            }

            nodeTag.method_10566("Relations", relations);
         }

         nodes.add(nodeTag);
      }

      if (nodes.size() > 0) {
         if (this.main != null && this.nodes.containsKey(this.main.getId())) {
            tag.method_10582("Main", this.main.getId().toString());
         }

         tag.method_10566("Nodes", nodes);
      }

      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      Map<UUID, List<UUID>> map = new HashMap();
      if (tag.method_10573("Nodes", 9)) {
         class_2499 nodes = tag.method_10554("Nodes", 10);

         for(int i = 0; i < nodes.size(); ++i) {
            class_2487 nodeTag = nodes.method_10602(i);
            T node = NodeUtils.nodeFromNBT(this, nodeTag);
            if (nodeTag.method_10545("Relations")) {
               List<UUID> uuids = new ArrayList();
               class_2499 relations = nodeTag.method_10554("Relations", 8);
               map.put(node.getId(), uuids);

               for(int j = 0; j < relations.size(); ++j) {
                  uuids.add(UUID.fromString(relations.method_10608(j)));
               }
            }

            this.add(node);
         }
      }

      for(Map.Entry<UUID, List<UUID>> entry : map.entrySet()) {
         for(UUID input : entry.getValue()) {
            T nodeOutput = (T)(this.nodes.get(entry.getKey()));
            T nodeInput = (T)(this.nodes.get(input));
            if (nodeOutput != nodeInput && nodeInput != null && nodeOutput != null) {
               this.tie(nodeOutput, nodeInput);
            }
         }
      }

      if (tag.method_10545("Main")) {
         this.main = (T)(this.nodes.get(UUID.fromString(tag.method_10558("Main"))));
      }

   }
}
