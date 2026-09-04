package mchorse.mappet.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mchorse.mappet.api.utils.IExecutable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;






@Environment(EnvType.CLIENT)
public class ClientEventHandler {
   private static final ClientEventHandler INSTANCE = new ClientEventHandler();

   private final List<IExecutable> executables = new ArrayList<>();
   private final List<IExecutable> secondList = new ArrayList<>();
   private final Set<String> cancelledIds = new HashSet<>();

   public static ClientEventHandler instance() {
      return INSTANCE;
   }

   public void addExecutable(IExecutable executable) {
      this.executables.add(executable);
   }

   public boolean removeExecutable(String taskId) {
      if (taskId == null || taskId.isEmpty()) {
         return false;
      }

      boolean found = false;
      for (IExecutable executable : this.executables) {
         found |= taskId.equals(executable.getTaskId());
      }
      for (IExecutable executable : this.secondList) {
         found |= taskId.equals(executable.getTaskId());
      }

      if (found) {
         this.cancelledIds.add(taskId);
      }

      return found;
   }

   


   public void tick() {
      if (this.executables.isEmpty()) {
         return;
      }

      this.secondList.addAll(this.executables);
      this.executables.clear();
      this.secondList.removeIf(e -> this.cancelledIds.contains(e.getTaskId()) || e.update());
      this.executables.addAll(this.secondList);
      this.secondList.clear();
      this.cancelledIds.clear();
   }

   public void reset() {
      this.executables.clear();
      this.secondList.clear();
      this.cancelledIds.clear();
   }
}
