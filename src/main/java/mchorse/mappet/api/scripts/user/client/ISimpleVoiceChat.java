package mchorse.mappet.api.scripts.user.client;

import mchorse.mappet.api.scripts.user.entities.IScriptEntity;


public interface ISimpleVoiceChat {
   




   void setMute(boolean muted);

   
   boolean isMuted();

   



   void setPlayerMuted(IScriptEntity player, boolean muted);

   
   boolean isPlayerMuted(IScriptEntity player);
}
