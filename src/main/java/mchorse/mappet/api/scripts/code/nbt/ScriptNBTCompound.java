package mchorse.mappet.api.scripts.code.nbt;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mchorse.mappet.api.scripts.user.nbt.INBT;
import mchorse.mappet.api.scripts.user.nbt.INBTCompound;
import mchorse.mappet.api.scripts.user.nbt.INBTList;
import net.minecraft.class_2481;
import net.minecraft.class_2487;
import net.minecraft.class_2489;
import net.minecraft.class_2494;
import net.minecraft.class_2497;
import net.minecraft.class_2499;
import net.minecraft.class_2503;
import net.minecraft.class_2516;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2522;

public class ScriptNBTCompound implements INBTCompound {
   private class_2487 tag;

   public ScriptNBTCompound(class_2487 tag) {
      this.tag = tag == null ? new class_2487() : tag;
   }

   public class_2487 getNbtCompound() {
      return this.tag;
   }

   public boolean isCompound() {
      return true;
   }

   public boolean isList() {
      return false;
   }

   public String stringify() {
      return this.tag.toString();
   }

   public String toString() {
      return this.tag.toString();
   }

   public boolean isEmpty() {
      return this.tag.method_33133();
   }

   public int size() {
      return this.tag.method_10546();
   }

   public void combine(INBT nbt) {
      if (nbt instanceof ScriptNBTCompound) {
         this.tag.method_10543(((ScriptNBTCompound)nbt).tag);
      }

   }

   public boolean isSame(INBT nbt) {
      return nbt instanceof ScriptNBTCompound ? this.tag.equals(((ScriptNBTCompound)nbt).tag) : false;
   }

   public boolean has(String key) {
      return this.tag.method_10545(key);
   }

   public void remove(String key) {
      this.tag.method_10551(key);
   }

   public Set<String> keys() {
      return this.tag.method_10541();
   }

   public INBTCompound copy() {
      return new ScriptNBTCompound(this.tag.method_10553());
   }

   public byte getByte(String key) {
      return this.tag.method_10571(key);
   }

   public void setByte(String key, byte value) {
      this.tag.method_10567(key, value);
   }

   public short getShort(String key) {
      return this.tag.method_10568(key);
   }

   public void setShort(String key, short value) {
      this.tag.method_10575(key, value);
   }

   public int getInt(String key) {
      return this.tag.method_10550(key);
   }

   public void setInt(String key, int value) {
      this.tag.method_10569(key, value);
   }

   public long getLong(String key) {
      return this.tag.method_10537(key);
   }

   public void setLong(String key, long value) {
      this.tag.method_10544(key, value);
   }

   public float getFloat(String key) {
      return this.tag.method_10583(key);
   }

   public void setFloat(String key, float value) {
      this.tag.method_10548(key, value);
   }

   public double getDouble(String key) {
      return this.tag.method_10574(key);
   }

   public void setDouble(String key, double value) {
      this.tag.method_10549(key, value);
   }

   public String getString(String key) {
      return this.tag.method_10558(key);
   }

   public void setString(String key, String value) {
      this.tag.method_10582(key, value);
   }

   public boolean getBoolean(String key) {
      return this.tag.method_10577(key);
   }

   public void setBoolean(String key, boolean value) {
      this.tag.method_10556(key, value);
   }

   public INBTCompound getCompound(String key) {
      return new ScriptNBTCompound(this.tag.method_10562(key));
   }

   public void setCompound(String key, INBTCompound value) {
      this.tag.method_10566(key, ((ScriptNBTCompound)value).tag);
   }

   public INBTList getList(String key) {
      class_2520 tag = this.tag.method_10580(key);
      return new ScriptNBTList(tag instanceof class_2499 ? (class_2499)tag : new class_2499());
   }

   public void setList(String key, INBTList value) {
      this.tag.method_10566(key, value.getNbtList());
   }

   public boolean setNBT(String key, String nbt) {
      try {
         class_2487 tag = class_2522.method_10718("{data:" + nbt + "}");
         this.tag.method_10566(key, tag.method_10580("data"));
         return true;
      } catch (Exception var4) {
         return false;
      }
   }

   public Object get(String key) {
      class_2520 tag = this.tag.method_10580(key);
      if (tag instanceof class_2487) {
         return new ScriptNBTCompound((class_2487)tag);
      } else if (tag instanceof class_2499) {
         return new ScriptNBTList((class_2499)tag);
      } else if (tag instanceof class_2519) {
         return this.getString(key);
      } else if (tag instanceof class_2497) {
         return this.getInt(key);
      } else if (tag instanceof class_2489) {
         return this.getDouble(key);
      } else if (tag instanceof class_2494) {
         return this.getFloat(key);
      } else if (tag instanceof class_2503) {
         return this.getLong(key);
      } else if (tag instanceof class_2516) {
         return this.getShort(key);
      } else {
         return tag instanceof class_2481 ? this.getByte(key) : null;
      }
   }

   public boolean equals(INBTCompound compound) {
      return compound != null && this.tag.equals(compound.getNbtCompound());
   }

   public void addCompound(String key) {
      this.tag.method_10566(key, new class_2487());
   }

   public String dumpJSON() {
      String result = this.stringify().replaceAll("([a-zA-Z0-9_]+):", "\"$1\":");
      Pattern pattern = Pattern.compile("([0-9]+[bLsdf])|0b|1b");
      Matcher matcher = pattern.matcher(result);
      StringBuffer sb = new StringBuffer();

      while(matcher.find()) {
         if (matcher.group(0).equals("0b")) {
            matcher.appendReplacement(sb, "false");
         } else if (matcher.group(0).equals("1b")) {
            matcher.appendReplacement(sb, "true");
         } else {
            matcher.appendReplacement(sb, matcher.group(1).substring(0, matcher.group(1).length() - 1));
         }
      }

      matcher.appendTail(sb);
      return sb.toString();
   }
}
