package mchorse.mappet.api.scripts.user.nbt;

import java.util.Set;
import net.minecraft.class_2487;

public interface INBTCompound extends INBT {
   class_2487 getNbtCompound();
   @Deprecated
   default class_2487 getNBTTagCompound() {
      return this.getNbtCompound();
   }
   @Deprecated
   default class_2487 getNBTTagComound() {
      return this.getNBTTagCompound();
   }

   boolean has(String var1);

   void remove(String var1);

   Set<String> keys();

   byte getByte(String var1);

   void setByte(String var1, byte var2);

   short getShort(String var1);

   void setShort(String var1, short var2);

   int getInt(String var1);

   void setInt(String var1, int var2);

   long getLong(String var1);

   void setLong(String var1, long var2);

   float getFloat(String var1);

   void setFloat(String var1, float var2);

   double getDouble(String var1);

   void setDouble(String var1, double var2);

   String getString(String var1);

   void setString(String var1, String var2);

   boolean getBoolean(String var1);

   void setBoolean(String var1, boolean var2);

   INBTCompound getCompound(String var1);

   void setCompound(String var1, INBTCompound var2);

   INBTList getList(String var1);

   void setList(String var1, INBTList var2);

   boolean setNBT(String var1, String var2);

   Object get(String var1);

   boolean equals(INBTCompound var1);

   void addCompound(String var1);

   String dumpJSON();
}
