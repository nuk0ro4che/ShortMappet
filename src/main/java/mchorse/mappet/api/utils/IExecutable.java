package mchorse.mappet.api.utils;

public interface IExecutable {
   String getId();

   default String getTaskId() {
      return this.getId();
   }

   boolean update();
}
