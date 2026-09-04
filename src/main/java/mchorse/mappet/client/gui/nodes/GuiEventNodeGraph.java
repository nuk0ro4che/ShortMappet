package mchorse.mappet.client.gui.nodes;

import java.util.function.Consumer;
import mchorse.mappet.api.events.nodes.EventBaseNode;
import mchorse.mappet.api.utils.factory.IFactory;
import net.minecraft.class_310;

public class GuiEventNodeGraph extends GuiNodeGraph<EventBaseNode> {
   public GuiEventNodeGraph(class_310 mc, IFactory<EventBaseNode> factory, Consumer<EventBaseNode> callback) {
      super(mc, factory, callback);
   }

   protected int getIndexLabelColor(EventBaseNode lastSelected, int i) {
      return lastSelected.binary && i >= 2 ? 6710886 : 16777215;
   }

   protected int getNodeActiveColor(EventBaseNode output, int r) {
      if (output.binary) {
         return r == 0 ? 'ｄ' : (r == 1 ? 16711731 : 16759552);
      } else {
         return super.getNodeActiveColor(output, r);
      }
   }

   protected float getNodeActiveColorOpacity(EventBaseNode output, int r) {
      return output.binary && r >= 2 ? 0.25F : super.getNodeActiveColorOpacity(output, r);
   }
}
