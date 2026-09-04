package mchorse.mappet.client.gui;

import mchorse.mappet.api.ui.UI;
import net.minecraft.class_310;






public class GuiUIEditorPreview extends GuiUserInterface {
   private final GuiMappetDashboard editor;

   public GuiUIEditorPreview(class_310 mc, UI ui, GuiMappetDashboard editor) {
      super(mc, ui);
      this.editor = editor;
   }

   protected void closeScreen() {
      this.returnToEditor();
   }

   public void requestClose() {
      this.returnToEditor();
   }


   private void returnToEditor() {
      this.field_22787.method_1507(this.editor);
   }
}
