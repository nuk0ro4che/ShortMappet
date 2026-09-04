package mchorse.mappet.api.scripts.user.client;

public interface IGameSettings {
   void setFov(int fov);

   void setGamma(double gamma);

   void setMouseSensitivity(double sensitivity);

   double getMouseSensitivity();

   void setHudHidden(boolean hidden);

   void setPerspective(int perspective);

   void setKeyBinding(String id, String key);

   void resetKeyBinding(String id);

   




   String getKeyBinding(String id);

   
   void activateKeyBinding(String id);
}
