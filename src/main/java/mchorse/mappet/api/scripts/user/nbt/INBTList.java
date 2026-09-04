package mchorse.mappet.api.scripts.user.nbt;

import net.minecraft.class_2499;

public interface INBTList extends INBT {
   class_2499 getNbtList();
   @Deprecated
   default class_2499 getNBTTagList() {
      return this.getNbtList();
   }

   boolean has(int var1);

   void remove(int var1);

   byte getByte(int var1);

   void setByte(int var1, byte var2);

   void addByte(byte var1);

   short getShort(int var1);

   void setShort(int var1, short var2);

   void addShort(short var1);

   int getInt(int var1);

   void setInt(int var1, int var2);

   void addInt(int var1);

   long getLong(int var1);

   void setLong(int var1, long var2);

   void addLong(long var1);

   float getFloat(int var1);

   void setFloat(int var1, float var2);

   void addFloat(float var1);

   double getDouble(int var1);

   void setDouble(int var1, double var2);

   void addDouble(double var1);

   String getString(int var1);

   void setString(int var1, String var2);

   void addString(String var1);

   boolean getBoolean(int var1);

   void setBoolean(int var1, boolean var2);

   void addBoolean(boolean var1);

   INBTCompound getCompound(int var1);

   void setCompound(int var1, INBTCompound var2);

   void addCompound(INBTCompound var1);

   INBTList getList(int var1);

   void setList(int var1, INBTList var2);

   void addList(INBTList var1);

   Object[] toArray();
}
