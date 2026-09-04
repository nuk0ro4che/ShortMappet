package mchorse.mappet.api.utils.logs;

import com.google.common.io.Files;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import mchorse.mappet.api.scripts.user.logs.IMappetLogger;

public class MappetLogger extends Logger implements IMappetLogger {
   public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd HH:mm:ss");

   public MappetLogger(String name, File worldFile) {
      super(name, (String)null);
      FileHandler handler = null;

      try {
         File logsFolder = new File(worldFile, "logs");
         logsFolder.mkdirs();
         File file = new File(logsFolder, "latest.log");
         if (file.exists()) {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String date = LocalDate.now().format(dateFormat);
            File[] todayFiles = logsFolder.listFiles((fileToFilter) -> fileToFilter.getName().startsWith(date));
            int lastIndex = todayFiles.length == 0 ? 0 : (Integer)Arrays.stream(todayFiles).map((element) -> {
               String fileName = element.getName();
               return Integer.parseInt(fileName.substring(fileName.lastIndexOf("-") + 1, fileName.lastIndexOf(".")));
            }).max(Comparator.naturalOrder()).orElse(0);
            String var10004 = LocalDateTime.now().format(dateFormat);
            Files.move(file, new File(logsFolder, var10004 + "-" + (lastIndex + 1) + ".log"));
         }

         handler = new FileHandler(file.getPath(), true);
         handler.setEncoding("UTF-8");
         handler.setFormatter(new Formatter() {
            public String format(LogRecord record) {
               String var10000 = LocalDateTime.now().format(MappetLogger.dtf);
               return "[" + var10000 + "] [" + record.getLevel().getName() + "] " + this.formatMessage(record) + "\n";
            }
         });
         this.addHandler(handler);
      } catch (Exception e) {
         e.printStackTrace();
      }

      this.setUseParentHandlers(false);
      this.setLevel(Level.ALL);
   }

   public void error(String message) {
      this.log(LoggerLevel.ERROR.value, message);
   }

   public void debug(String message) {
      this.log(LoggerLevel.DEBUG.value, message);
   }
}
