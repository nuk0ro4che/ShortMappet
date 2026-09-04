package mchorse.mappet.api.scripts.code.mappet;

import java.util.Locale;
import mchorse.mappet.api.scripts.code.nbt.ScriptNBTCompound;
import mchorse.mappet.api.scripts.user.mappet.IMappetUIContext;
import mchorse.mappet.api.scripts.user.nbt.INBTCompound;
import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.components.UIComponent;

public class MappetUIContext implements IMappetUIContext {
   private UIContext context;
   private INBTCompound data;

   public MappetUIContext(UIContext context) {
      this.context = context;
   }

   public INBTCompound getData() {
      if (this.data == null) {
         this.data = new ScriptNBTCompound(this.context.data);
      }

      return this.data;
   }

   public String getData(String id) {
      return this.getColor(id);
   }

   public String getColor(String id) {
      int color = id == null || id.isEmpty() ? 0 : this.context.data.method_10550(id);
      return String.format(Locale.ROOT, "0x%08x", color);
   }

   public boolean isClosed() {
      return this.context.isClosed();
   }

   public String getLast() {
      return this.context.getLast();
   }

   public String getHotkey() {
      return this.context.getHotkey();
   }

   public String getContext() {
      return this.context.getContext();
   }

   public String getHovered() {
      return this.context.getHovered();
   }

   public String getUnhovered() {
      return this.context.getUnhovered();
   }

   public UIComponent get(String id) {
      return this.context.getById(id);
   }

   public void sendToPlayer() {
      this.context.sendToPlayer();
   }
}
