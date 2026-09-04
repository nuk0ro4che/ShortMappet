package mchorse.mappet.api.scripts.user.mappet;

import java.util.Set;

public interface IMappetStates {
   double add(String var1, double var2);

   void setNumber(String var1, double var2);

   void setString(String var1, String var2);

   double getNumber(String var1);

   boolean isNumber(String var1);

   String getString(String var1);

   boolean isString(String var1);

   void reset(String var1);

   void resetMasked(String var1);

   void clear();

   boolean has(String var1);

   Set<String> keys();
}
