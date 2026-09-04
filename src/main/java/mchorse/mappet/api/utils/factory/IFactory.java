package mchorse.mappet.api.utils.factory;

import java.util.Collection;

public interface IFactory<T> {
   String getType(T var1);

   T create(String var1);

   int getColor(T var1);

   int getColor(String var1);

   Collection<String> getKeys();
}
