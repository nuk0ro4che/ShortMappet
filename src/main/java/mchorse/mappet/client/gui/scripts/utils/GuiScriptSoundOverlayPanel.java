package mchorse.mappet.client.gui.scripts.utils;

import java.util.function.Consumer;
import mchorse.mappet.client.gui.scripts.GuiTextEditor;
import mchorse.mappet.client.gui.utils.overlays.GuiSoundOverlayPanel;
import net.minecraft.class_310;

public class GuiScriptSoundOverlayPanel extends GuiSoundOverlayPanel {
   private GuiTextEditor editor;

   public GuiScriptSoundOverlayPanel(class_310 mc, GuiTextEditor editor) {
      super(mc, (Consumer)null);
      this.editor = editor;
      this.set(editor.getSelectedText().replaceAll("\"", ""));
   }

   @Override
   public void onClose() {
      if (this.rls != null && this.rls.list != null && !this.rls.list.isDeselected() && this.rls.list.getIndex() > 0) {
         String current = this.editor.getSelectedText().trim();
         String result = (String)this.rls.list.getCurrentFirst();

         if (current.startsWith("\"")) {
            result = "\"" + result;
         }

         if (current.endsWith("\"")) {
            result = result + "\"";
         }

         this.editor.pasteText(result);
      }
      super.onClose();
   }
}