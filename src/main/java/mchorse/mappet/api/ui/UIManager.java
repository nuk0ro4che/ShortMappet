package mchorse.mappet.api.ui;

import java.io.File;
import mchorse.mappet.api.utils.manager.BaseManager;
import net.minecraft.class_2487;

public class UIManager extends BaseManager<UIFile> {
   public UIManager(File folder) {
      super(folder);
   }

   protected UIFile createData(String id, class_2487 tag) {
      UIFile ui = new UIFile();
      if (tag != null) {
         ui.deserializeNBT(tag);
      }

      return ui;
   }
}
