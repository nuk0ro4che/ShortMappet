package mchorse.mappet.network.server.logs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import mchorse.mappet.api.utils.logs.MappetLogger;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.logs.PacketLogs;
import mchorse.mappet.network.common.logs.PacketRequestLogs;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.class_3222;
import net.minecraft.class_5218;

public class ServerHandlerLogs extends ServerMessageHandler<PacketRequestLogs> {
   public void run(class_3222 player, PacketRequestLogs message) {
      if (OpHelper.isPlayerOp(player)) {
         LocalDateTime lastLogTime = LocalDateTime.parse(message.lastLogTime, MappetLogger.dtf);
         File mappetWorldFolder = player.method_5682().method_27050(class_5218.field_24188).resolve("mappet").toFile();
         File logFile = new File(mappetWorldFolder, "logs/latest.log");

         try {
            BufferedReader reader = new BufferedReader(new FileReader(logFile));
            int stringEncodingLimit = 16384;
            String stringToSend = "";
            boolean isPreviousLineNew = false;

            String line;
            while((line = reader.readLine()) != null) {
               if (!this.isNewLine(lastLogTime, line, isPreviousLineNew)) {
                  isPreviousLineNew = false;
               } else {
                  isPreviousLineNew = true;
                  if (stringToSend.getBytes().length + line.getBytes().length < stringEncodingLimit) {
                     stringToSend = stringToSend.concat(line + "\r");
                  } else {
                     Dispatcher.sendTo(new PacketLogs(stringToSend), player);
                     stringToSend = "";
                  }
               }
            }

            if (!stringToSend.equals("")) {
               Dispatcher.sendTo(new PacketLogs(stringToSend), player);
            }
         } catch (IOException var11) {
         }

      }
   }

   public boolean isNewLine(LocalDateTime date, String line, boolean isPreviousLineNew) {
      if (date.equals(LocalDateTime.of(1, 1, 1, 0, 0, 0))) {
         return true;
      } else {
         int bracketIndex = line.indexOf("]");
         if (bracketIndex == -1) {
            return isPreviousLineNew;
         } else {
            String logDateString = line.substring(1, bracketIndex);
            LocalDateTime logDate = LocalDateTime.parse(logDateString, MappetLogger.dtf);
            return logDate.isAfter(date);
         }
      }
   }
}
