package mchorse.mappet.api.scripts.code.entities.ai.repeatingCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.class_18;
import net.minecraft.class_1937;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_3218;

public class RepeatingCommandDataStorage extends class_18 {
   public static final String REPEATING_COMMAND_DATA_KEY = "mappet_repeating_command_data";
   private Map<UUID, List<RepeatingCommandData>> repeatingCommandDataMap = new HashMap();

   public static RepeatingCommandDataStorage getRepeatingCommandDataStorage(class_1937 world) {
      if (world instanceof class_3218 serverWorld) {
         return (RepeatingCommandDataStorage)serverWorld.method_17983().method_17924(RepeatingCommandDataStorage::fromNbt, RepeatingCommandDataStorage::new, "mappet_repeating_command_data");
      } else {
         throw new IllegalArgumentException("Repeating command data is server-side");
      }
   }

   public void readFromNBT(class_2487 nbt) {
      this.repeatingCommandDataMap.clear();
      class_2499 repeatingCommandDataList = nbt.method_10554("repeatingCommandDataList", 10);

      for(int i = 0; i < repeatingCommandDataList.size(); ++i) {
         class_2487 repeatingCommandDataCompound = repeatingCommandDataList.method_10602(i);
         UUID entityId = UUID.fromString(repeatingCommandDataCompound.method_10558("entityId"));
         String command = repeatingCommandDataCompound.method_10558("command");
         int frequency = repeatingCommandDataCompound.method_10550("frequency");
         ((List)this.repeatingCommandDataMap.computeIfAbsent(entityId, (k) -> new ArrayList())).add(new RepeatingCommandData(command, frequency));
      }

   }

   public class_2487 method_75(class_2487 nbt) {
      class_2499 repeatingCommandDataList = new class_2499();

      for(Map.Entry<UUID, List<RepeatingCommandData>> entry : this.repeatingCommandDataMap.entrySet()) {
         for(RepeatingCommandData data : entry.getValue()) {
            class_2487 repeatingCommandDataCompound = new class_2487();
            repeatingCommandDataCompound.method_10582("entityId", ((UUID)entry.getKey()).toString());
            repeatingCommandDataCompound.method_10582("command", data.command);
            repeatingCommandDataCompound.method_10569("frequency", data.frequency);
            repeatingCommandDataList.add(repeatingCommandDataCompound);
         }
      }

      nbt.method_10566("repeatingCommandDataList", repeatingCommandDataList);
      return nbt;
   }

   private static RepeatingCommandDataStorage fromNbt(class_2487 nbt) {
      RepeatingCommandDataStorage storage = new RepeatingCommandDataStorage();
      storage.readFromNBT(nbt);
      return storage;
   }

   public void addRepeatingCommandData(UUID entityId, String command, int frequency) {
      ((List)this.repeatingCommandDataMap.computeIfAbsent(entityId, (k) -> new ArrayList())).add(new RepeatingCommandData(command, frequency));
      this.method_80();
   }

   public void removeRepeatingCommandData(UUID entityId) {
      this.repeatingCommandDataMap.remove(entityId);
      this.method_80();
   }

   public List<RepeatingCommandData> getRepeatingCommandData(UUID entityId) {
      return (List)this.repeatingCommandDataMap.get(entityId);
   }

   public void removeSpecificRepeatingCommandData(UUID entityId, String command) {
      List<RepeatingCommandData> commandDataList = (List)this.repeatingCommandDataMap.get(entityId);
      if (commandDataList != null) {
         commandDataList.removeIf((data) -> data.command.equals(command));
         if (commandDataList.isEmpty()) {
            this.repeatingCommandDataMap.remove(entityId);
         }

         this.method_80();
      }

   }

   public static class RepeatingCommandData {
      public final String command;
      public final int frequency;

      public RepeatingCommandData(String command, int frequency) {
         this.command = command;
         this.frequency = frequency;
      }
   }
}
