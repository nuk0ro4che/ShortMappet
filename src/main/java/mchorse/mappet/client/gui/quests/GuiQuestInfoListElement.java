package mchorse.mappet.client.gui.quests;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import mchorse.mappet.api.quests.chains.QuestInfo;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import net.minecraft.class_124;
import net.minecraft.class_310;

public class GuiQuestInfoListElement extends GuiListElement<QuestInfo> {
   public GuiQuestInfoListElement(class_310 mc, Consumer<List<QuestInfo>> callback) {
      super(mc, callback);
      this.scroll.scrollItemSize = 16;
   }

   protected boolean sortElements() {
      this.list.sort(Comparator.comparing((a) -> a.quest.title));
      return true;
   }

   protected String elementToString(QuestInfo element) {
      String var10000 = String.valueOf(element.status.formatting);
      String string = var10000 + element.quest.getProcessedTitle();
      if (this.mc.field_1724.method_7337()) {
         string = string + String.valueOf(class_124.field_1080) + " (" + element.quest.getId() + ")";
      }

      return string;
   }
}
