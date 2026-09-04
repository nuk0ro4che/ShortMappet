package mchorse.mappet.api.utils.nodes;

public class NodeRelation<T extends Node> {
   public T output;
   public T input;

   public NodeRelation(T output, T input) {
      this.output = output;
      this.input = input;
   }
}
