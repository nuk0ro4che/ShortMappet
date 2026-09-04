package mchorse.mappet.utils;

public class EnumUtils {
   public static <T> T getValue(int ordinal, T[] values, T defaultValue) {
      return (T)(ordinal >= 0 && ordinal < values.length ? values[ordinal] : defaultValue);
   }
}
