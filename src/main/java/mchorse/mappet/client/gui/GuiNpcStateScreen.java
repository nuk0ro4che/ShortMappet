package mchorse.mappet.client.gui;

import mchorse.mappet.api.npcs.NpcState;
import mchorse.mappet.client.gui.npc.GuiNpcEditor;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.npc.PacketNpcState;
import mchorse.mclib.client.gui.framework.GuiBase;
import net.minecraft.class_310;
import net.minecraft.class_332;

public class GuiNpcStateScreen extends GuiBase {
   public GuiNpcEditor editor;
   private int entityId;

   public GuiNpcStateScreen(class_310 mc, int entityId, NpcState state) {
      this.entityId = entityId;
      this.editor = new GuiNpcEditor(mc, true);
      this.editor.flex().relative(this.root).w(1.0F).h(1.0F);
      this.root.add(this.editor);
      this.editor.set(state);
   }

   public boolean method_25421() {
      return false;
   }

   protected void closeScreen() {
      super.closeScreen();
      Dispatcher.sendToServer(new PacketNpcState(this.entityId, this.editor.get().serializeNBT()));
   }

   public void method_25394(class_332 drawContext, int mouseX, int mouseY, float partialTicks) {
      this.method_25420(drawContext);
      super.method_25394(drawContext, mouseX, mouseY, partialTicks);
   }
}
