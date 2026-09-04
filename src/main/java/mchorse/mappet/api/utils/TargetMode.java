package mchorse.mappet.api.utils;

public enum TargetMode {
   GLOBAL,
   SUBJECT,
   OBJECT,
   SELECTOR,
   PLAYER,
   NPC;
   private static TargetMode[] $values() {
      return new TargetMode[]{GLOBAL, SUBJECT, OBJECT, SELECTOR, PLAYER, NPC};
   }
}
