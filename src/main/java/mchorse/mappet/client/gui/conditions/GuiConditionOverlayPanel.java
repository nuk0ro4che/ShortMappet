package mchorse.mappet.client.gui.conditions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.api.conditions.Condition;
import mchorse.mappet.api.conditions.blocks.AbstractConditionBlock;
import mchorse.mappet.api.conditions.blocks.ConditionConditionBlock;
import mchorse.mappet.api.conditions.blocks.DialogueConditionBlock;
import mchorse.mappet.api.conditions.blocks.EntityConditionBlock;
import mchorse.mappet.api.conditions.blocks.ExpressionConditionBlock;
import mchorse.mappet.api.conditions.blocks.FactionConditionBlock;
import mchorse.mappet.api.conditions.blocks.ItemConditionBlock;
import mchorse.mappet.api.conditions.blocks.MorphConditionBlock;
import mchorse.mappet.api.conditions.blocks.QuestConditionBlock;
import mchorse.mappet.api.conditions.blocks.StateConditionBlock;
import mchorse.mappet.api.conditions.blocks.WorldTimeConditionBlock;
import mchorse.mappet.client.gui.conditions.blocks.GuiAbstractConditionBlockPanel;
import mchorse.mappet.client.gui.conditions.blocks.GuiConditionConditionBlockPanel;
import mchorse.mappet.client.gui.conditions.blocks.GuiDialogueConditionBlockPanel;
import mchorse.mappet.client.gui.conditions.blocks.GuiEntityConditionBlockPanel;
import mchorse.mappet.client.gui.conditions.blocks.GuiExpressionConditionBlockPanel;
import mchorse.mappet.client.gui.conditions.blocks.GuiFactionConditionBlockPanel;
import mchorse.mappet.client.gui.conditions.blocks.GuiItemConditionBlockPanel;
import mchorse.mappet.client.gui.conditions.blocks.GuiMorphConditionBlockPanel;
import mchorse.mappet.client.gui.conditions.blocks.GuiQuestConditionBlockPanel;
import mchorse.mappet.client.gui.conditions.blocks.GuiStateConditionBlockPanel;
import mchorse.mappet.client.gui.conditions.blocks.GuiWorldTimeConditionBlockPanel;
import mchorse.mappet.client.gui.utils.GuiMappetUtils;
import mchorse.mappet.client.gui.utils.overlays.GuiEditorOverlayPanel;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_1074;
import net.minecraft.class_2487;
import net.minecraft.class_2522;
import net.minecraft.class_310;
import net.minecraft.class_327;

public class GuiConditionOverlayPanel extends GuiEditorOverlayPanel<AbstractConditionBlock> {
   public static final Map<Class<? extends AbstractConditionBlock>, Class<? extends GuiAbstractConditionBlockPanel<? extends AbstractConditionBlock>>> PANELS = new HashMap();
   private Condition condition;

   public GuiConditionOverlayPanel(class_310 mc, Condition condition) {
      super(mc, IKey.lang("mappet.gui.conditions.title"));
      this.condition = condition;
      this.list.sorting().setList(condition.blocks);
      this.list.context(() -> {
         GuiSimpleContextMenu menu = (new GuiSimpleContextMenu(this.mc)).shadow();
         menu.action(Icons.ADD, IKey.lang("mappet.gui.conditions.context.add"), () -> {
            GuiSimpleContextMenu adds = (new GuiSimpleContextMenu(this.mc)).shadow();

            for(String key : CommonProxy.getConditionBlocks().getKeys()) {
               IKey label = IKey.format("mappet.gui.conditions.context.add_condition", new Object[]{IKey.lang("mappet.gui.condition_types." + key)});
               int color = CommonProxy.getConditionBlocks().getColor(key);
               adds.action(Icons.ADD, label, () -> this.addBlock(key), color);
            }

            GuiBase.getCurrent().replaceContextMenu(adds);
         });
         if (!this.list.isDeselected()) {
            menu.action(Icons.COPY, IKey.lang("mappet.gui.conditions.context.copy"), this::copyCondition);
         }

         try {
            class_2487 tag = class_2522.method_10718(GuiUtils.getClipboardString());
            if (tag.method_10577("_ConditionCopy")) {
               menu.action(Icons.PASTE, IKey.lang("mappet.gui.conditions.context.paste"), () -> this.pasteCondition(tag));
            }
         } catch (Exception var3) {
         }

         if (!this.list.isDeselected()) {
            menu.action(Icons.REMOVE, IKey.lang("mappet.gui.conditions.context.remove"), () -> this.removeItem(), 16711731);
         }

         return menu;
      });
      this.context(() -> this.list.createContextMenu(GuiBase.getCurrent()));
      this.pickItem(this.condition.blocks.isEmpty() ? null : (AbstractConditionBlock)this.condition.blocks.get(0), true);
   }

   protected GuiListElement<AbstractConditionBlock> createList(class_310 mc) {
      return new GuiAbstractBlockListElement(mc, (l) -> this.pickItem((AbstractConditionBlock)l.get(0), false));
   }

   private void addBlock(String type) {
      AbstractConditionBlock block = (AbstractConditionBlock)CommonProxy.getConditionBlocks().create(type);
      this.condition.blocks.add(block);
      this.pickItem(block, true);
      this.list.update();
   }

   private void copyCondition() {
      AbstractConditionBlock block = (AbstractConditionBlock)this.list.getCurrentFirst();
      class_2487 tag = block.serializeNBT();
      tag.method_10556("_ConditionCopy", true);
      tag.method_10582("Type", CommonProxy.getConditionBlocks().getType(block));
      GuiUtils.setClipboardString(tag.toString());
   }

   private void pasteCondition(class_2487 tag) {
      AbstractConditionBlock block = (AbstractConditionBlock)CommonProxy.getConditionBlocks().create(tag.method_10558("Type"));
      block.deserializeNBT(tag);
      this.condition.blocks.add(block);
      this.list.update();
      this.pickItem(block, true);
   }

   protected void fillData(AbstractConditionBlock block) {
      this.editor.removeAll();

      try {
         this.editor.add((GuiAbstractConditionBlockPanel)((Class)PANELS.get(block.getClass())).getConstructors()[0].newInstance(this.mc, this, block));
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public void draw(GuiContext context) {
      super.draw(context);
      if (this.condition.blocks.isEmpty()) {
         GuiMappetUtils.drawRightClickHere(context, this.list.area);
      }

   }

   static {
      PANELS.put(QuestConditionBlock.class, GuiQuestConditionBlockPanel.class);
      PANELS.put(StateConditionBlock.class, GuiStateConditionBlockPanel.class);
      PANELS.put(DialogueConditionBlock.class, GuiDialogueConditionBlockPanel.class);
      PANELS.put(FactionConditionBlock.class, GuiFactionConditionBlockPanel.class);
      PANELS.put(ItemConditionBlock.class, GuiItemConditionBlockPanel.class);
      PANELS.put(WorldTimeConditionBlock.class, GuiWorldTimeConditionBlockPanel.class);
      PANELS.put(EntityConditionBlock.class, GuiEntityConditionBlockPanel.class);
      PANELS.put(ConditionConditionBlock.class, GuiConditionConditionBlockPanel.class);
      PANELS.put(MorphConditionBlock.class, GuiMorphConditionBlockPanel.class);
      PANELS.put(ExpressionConditionBlock.class, GuiExpressionConditionBlockPanel.class);
   }

   public static class GuiAbstractBlockListElement extends GuiListElement<AbstractConditionBlock> {
      public GuiAbstractBlockListElement(class_310 mc, Consumer<List<AbstractConditionBlock>> callback) {
         super(mc, callback);
         this.postDraw = true;
         this.scroll.scrollItemSize = 24;
      }

      public void drawPostListElement(AbstractConditionBlock element, int i, int x, int y, boolean hover, boolean selected) {
         if (i > 0) {
            String label = class_1074.method_4662(element.or ? "mappet.gui.conditions.label_or" : "mappet.gui.conditions.label_and", new Object[0]);
            y -= 4;
            int w = this.font.method_1727(label);
            GuiDraw.drawTextBackground(this.font, label, this.scroll.mx(w), y, 16777215, -2013265920, 2);
         }

      }

      protected void drawElementPart(AbstractConditionBlock element, int i, int x, int y, boolean hover, boolean selected) {
         int color = CommonProxy.getConditionBlocks().getColor(element);
         GuiDraw.drawRect(x, y, x + 4, y + this.scroll.scrollItemSize, -16777216 + color);
         GuiDraw.drawHorizontalGradientRect(x + 4, y, x + 24, y + this.scroll.scrollItemSize, 1140850688 + color, color);
         if (element.not) {
            class_327 var10000 = this.font;
            int var10002 = x + 6;
            int var10003 = y + this.scroll.scrollItemSize / 2;
            Objects.requireNonNull(this.font);
            GuiDraw.drawTextBackground(var10000, "!", var10002, var10003 - 9 / 2, 16777215, -2013265920, 2);
         }

         super.drawElementPart(element, i, x + 4, y, hover, selected);
      }

      protected String elementToString(AbstractConditionBlock element) {
         return element.stringify();
      }
   }
}
