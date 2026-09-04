package mchorse.mappet.api.scripts.code.nbt;

import mchorse.mappet.api.scripts.user.nbt.INBT;
import mchorse.mappet.api.scripts.user.nbt.INBTCompound;
import mchorse.mappet.api.scripts.user.nbt.INBTList;
import net.minecraft.class_2481;
import net.minecraft.class_2489;
import net.minecraft.class_2494;
import net.minecraft.class_2497;
import net.minecraft.class_2499;
import net.minecraft.class_2503;
import net.minecraft.class_2514;
import net.minecraft.class_2516;
import net.minecraft.class_2519;
import net.minecraft.class_2520;

public class ScriptNBTList implements INBTList {
   private class_2499 list;

   public ScriptNBTList(class_2499 list) {
      this.list = list == null ? new class_2499() : list;
   }

   public class_2499 getNbtList() {
      return this.list;
   }

   public boolean isCompound() {
      return false;
   }

   public boolean isList() {
      return true;
   }

   public String stringify() {
      return this.list.toString();
   }

   public String toString() {
      return this.list.toString();
   }

   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   public int size() {
      return this.list.size();
   }

   public INBT copy() {
      return new ScriptNBTList(this.list.method_10612());
   }

   public void combine(INBT nbt) {
      if (nbt instanceof INBTList) {
         class_2499 list = ((INBTList)nbt).getNbtList();
         if (this.list.method_10601() == list.method_10601()) {
            for(int i = 0; i < list.size(); ++i) {
               this.list.add(list.method_10534(i).method_10707());
            }
         }
      }

   }

   public boolean isSame(INBT nbt) {
      return nbt instanceof INBTList ? this.list.equals(((INBTList)nbt).getNbtList()) : false;
   }

   public boolean has(int index) {
      return index >= 0 && index < this.size();
   }

   public void remove(int index) {
      this.list.method_10536(index);
   }

   public byte getByte(int index) {
      class_2520 base = this.list.method_10534(index);
      return base.method_10711() == 1 ? ((class_2514)base).method_10698() : 0;
   }

   public void setByte(int index, byte value) {
      this.list.method_10606(index, class_2481.method_23233(value));
   }

   public void addByte(byte value) {
      this.list.add(class_2481.method_23233(value));
   }

   public short getShort(int index) {
      class_2520 base = this.list.method_10534(index);
      return base.method_10711() == 2 ? ((class_2514)base).method_10696() : 0;
   }

   public void setShort(int index, short value) {
      this.list.method_10606(index, class_2516.method_23254(value));
   }

   public void addShort(short value) {
      this.list.add(class_2516.method_23254(value));
   }

   public int getInt(int index) {
      class_2520 base = this.list.method_10534(index);
      return base.method_10711() == 3 ? ((class_2514)base).method_10701() : 0;
   }

   public void setInt(int index, int value) {
      this.list.method_10606(index, class_2497.method_23247(value));
   }

   public void addInt(int value) {
      this.list.add(class_2497.method_23247(value));
   }

   public long getLong(int index) {
      class_2520 base = this.list.method_10534(index);
      return base.method_10711() == 4 ? ((class_2514)base).method_10699() : 0L;
   }

   public void setLong(int index, long value) {
      this.list.method_10606(index, class_2503.method_23251(value));
   }

   public void addLong(long value) {
      this.list.add(class_2503.method_23251(value));
   }

   public float getFloat(int index) {
      class_2520 base = this.list.method_10534(index);
      return base.method_10711() == 5 ? ((class_2514)base).method_10700() : 0.0F;
   }

   public void setFloat(int index, float value) {
      this.list.method_10606(index, class_2494.method_23244(value));
   }

   public void addFloat(float value) {
      this.list.add(class_2494.method_23244(value));
   }

   public double getDouble(int index) {
      class_2520 base = this.list.method_10534(index);
      return base.method_10711() == 6 ? ((class_2514)base).method_10697() : (double)0.0F;
   }

   public void setDouble(int index, double value) {
      this.list.method_10606(index, class_2489.method_23241(value));
   }

   public void addDouble(double value) {
      this.list.add(class_2489.method_23241(value));
   }

   public String getString(int index) {
      class_2520 base = this.list.method_10534(index);
      return base.method_10711() == 8 ? ((class_2519)base).method_10714() : "";
   }

   public void setString(int index, String value) {
      this.list.method_10606(index, class_2519.method_23256(value));
   }

   public void addString(String value) {
      this.list.add(class_2519.method_23256(value));
   }

   public boolean getBoolean(int index) {
      class_2520 base = this.list.method_10534(index);
      return base.method_10711() == 1 && ((class_2514)base).method_10698() != 0;
   }

   public void setBoolean(int index, boolean value) {
      this.list.method_10606(index, class_2481.method_23233((byte)(value ? 1 : 0)));
   }

   public void addBoolean(boolean value) {
      this.list.add(class_2481.method_23233((byte)(value ? 1 : 0)));
   }

   public INBTCompound getCompound(int index) {
      return new ScriptNBTCompound(this.list.method_10602(index));
   }

   public void setCompound(int index, INBTCompound value) {
      this.list.method_10606(index, value.getNbtCompound());
   }

   public void addCompound(INBTCompound value) {
      this.list.add(value.getNbtCompound());
   }

   public INBTList getList(int index) {
      class_2520 base = this.list.method_10534(index);
      return new ScriptNBTList(base.method_10711() == 9 ? (class_2499)base : null);
   }

   public void setList(int index, INBTList value) {
      this.list.method_10606(index, value.getNbtList());
   }

   public void addList(INBTList value) {
      this.list.add(value.getNbtList());
   }

   public Object[] toArray() {
      Object[] array = new Object[this.list.size()];

      for(int i = 0; i < this.list.size(); ++i) {
         array[i] = this.list.method_10534(i);
      }

      return array;
   }
}
