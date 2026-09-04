package mchorse.mappet.api.scripts.user.entities;

public interface ILocalMorph {
   ILocalMorph seeThrough(boolean value);

   ILocalMorph scale(boolean value);

   
   boolean isFar(double distance);

   
   boolean isNear(double distance);

   void remove();
}
