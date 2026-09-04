package mchorse.mappet.compat.events;

public enum EventPriority {
   LOWEST,
   LOW,
   NORMAL,
   HIGH,
   HIGHEST;
   private static EventPriority[] $values() {
      return new EventPriority[]{LOWEST, LOW, NORMAL, HIGH, HIGHEST};
   }
}
