package mchorse.mappet.utils;

import mchorse.mappet.api.utils.IExecutable;

public class RunnableExecutionFork implements IExecutable {
   public int timer;
   public Runnable runnable;

   public RunnableExecutionFork(int timer, Runnable runnable) {
      this.timer = timer;
      this.runnable = runnable;
   }

   public String getId() {
      return "";
   }

   public boolean update() {
      if (this.timer <= 0) {
         if (this.runnable != null) {
            this.runnable.run();
         }

         return true;
      } else {
         --this.timer;
         return false;
      }
   }
}
