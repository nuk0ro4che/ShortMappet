package mchorse.mappet.api.ui.utils;

public enum LayoutType {
   COLUMN,
   ROW,
   GRID;
   private static LayoutType[] $values() {
      return new LayoutType[]{COLUMN, ROW, GRID};
   }
}
