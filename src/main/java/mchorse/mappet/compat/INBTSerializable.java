package mchorse.mappet.compat;

public interface INBTSerializable<T> {
   T serializeNBT();

   void deserializeNBT(T var1);
}
