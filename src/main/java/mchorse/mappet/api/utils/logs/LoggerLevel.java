package mchorse.mappet.api.utils.logs;

import java.util.logging.Level;

public enum LoggerLevel {
   ERROR(new MappetLoggerLevel("ERROR", 999), 16733525),
   WARNING(Level.WARNING, 16755200),
   INFO(Level.INFO, 16777215),
   DEBUG(new MappetLoggerLevel("DEBUG", 699), 11184810);

   public final Level value;
   public final int color;

   private LoggerLevel(Level level, int color) {
      this.value = level;
      this.color = color;
   }
   private static LoggerLevel[] $values() {
      return new LoggerLevel[]{ERROR, WARNING, INFO, DEBUG};
   }
}
