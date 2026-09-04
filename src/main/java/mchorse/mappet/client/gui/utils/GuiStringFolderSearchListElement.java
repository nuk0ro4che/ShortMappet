package mchorse.mappet.client.gui.utils;

import java.util.List;
import java.util.function.Consumer;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringSearchListElement;
import net.minecraft.class_310;

public class GuiStringFolderSearchListElement extends GuiStringSearchListElement {
   public GuiStringFolderSearchListElement(class_310 mc, Consumer<List<String>> callback) {
      super(mc, callback);
   }

   protected GuiListElement<String> createList(class_310 mc, Consumer<List<String>> callback) {
      return new GuiStringFolderList(mc, callback);
   }
}
