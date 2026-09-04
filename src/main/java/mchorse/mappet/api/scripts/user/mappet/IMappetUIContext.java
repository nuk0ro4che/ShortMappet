package mchorse.mappet.api.scripts.user.mappet;

import mchorse.mappet.api.scripts.user.nbt.INBTCompound;
import mchorse.mappet.api.ui.components.UIComponent;

public interface IMappetUIContext {
   INBTCompound getData();

   
   String getData(String var1);

   
   String getColor(String var1);

   boolean isClosed();

   String getLast();

   String getHotkey();

   String getContext();

   String getHovered();

   String getUnhovered();

   UIComponent get(String var1);

   void sendToPlayer();
}
