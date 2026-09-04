package mchorse.mappet.api.scripts.user.nbt;

public interface INBT {
   boolean isCompound();

   boolean isList();

   String stringify();

   boolean isEmpty();

   int size();

   INBT copy();

   void combine(INBT var1);

   boolean isSame(INBT var1);
}
