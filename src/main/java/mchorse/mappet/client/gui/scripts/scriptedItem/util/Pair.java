package mchorse.mappet.client.gui.scripts.scriptedItem.util;

import java.util.Objects;

public class Pair<A, B> {
   public A a;
   public B b;

   public Pair(A a, B b) {
      this.a = a;
      this.b = b;
   }

   public boolean equals(Object obj) {
      if (super.equals(obj)) {
         return true;
      } else if (!(obj instanceof Pair)) {
         return false;
      } else {
         Pair pair = (Pair)obj;
         return Objects.equals(this.a, pair.b) && Objects.equals(this.b, pair.b);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.a, this.b});
   }
}
