package mchorse.mappet.client.gui.triggers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.api.triggers.blocks.AbstractTriggerBlock;
import mchorse.mappet.api.triggers.blocks.CommandTriggerBlock;
import mchorse.mappet.api.triggers.blocks.DialogueTriggerBlock;
import mchorse.mappet.api.triggers.blocks.EventTriggerBlock;
import mchorse.mappet.api.triggers.blocks.ItemTriggerBlock;
import mchorse.mappet.api.triggers.blocks.MorphTriggerBlock;
import mchorse.mappet.api.triggers.blocks.ScriptTriggerBlock;
import mchorse.mappet.api.triggers.blocks.SoundTriggerBlock;
import mchorse.mappet.api.triggers.blocks.StateTriggerBlock;
import mchorse.mappet.client.gui.triggers.panels.GuiAbstractTriggerBlockPanel;
import mchorse.mappet.client.gui.triggers.panels.GuiCommandTriggerBlockPanel;
import mchorse.mappet.client.gui.triggers.panels.GuiDialogueTriggerBlockPanel;
import mchorse.mappet.client.gui.triggers.panels.GuiEventTriggerBlockPanel;
import mchorse.mappet.client.gui.triggers.panels.GuiItemTriggerBlockPanel;
import mchorse.mappet.client.gui.triggers.panels.GuiMorphTriggerBlockPanel;
import mchorse.mappet.client.gui.triggers.panels.GuiScriptTriggerBlockPanel;
import mchorse.mappet.client.gui.triggers.panels.GuiSoundTriggerBlockPanel;
import mchorse.mappet.client.gui.triggers.panels.GuiStateTriggerBlockPanel;
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
import net.minecraft.class_2487;
import net.minecraft.class_2522;
import net.minecraft.class_310;

public class GuiTriggerOverlayPanel extends GuiEditorOverlayPanel<AbstractTriggerBlock> {
   public static final Map<Class<? extends AbstractTriggerBlock>, Class<? extends GuiAbstractTriggerBlockPanel<? extends AbstractTriggerBlock>>> PANELS = new HashMap();
   private Trigger trigger;
   private Runnable onClose;

   public GuiTriggerOverlayPanel(class_310 mc, Trigger trigger) {
      this(mc, trigger, (Runnable)null);
   }

   public GuiTriggerOverlayPanel(class_310 mc, Trigger trigger, Runnable onClose) {
      super(mc, IKey.lang("mappet.gui.triggers.title"));
      this.trigger = trigger;
      this.onClose = onClose;
      this.list.sorting().setList(trigger.blocks);
      this.list.context(() -> {
         GuiSimpleContextMenu menu = (new GuiSimpleContextMenu(this.mc)).shadow();
         menu.action(Icons.ADD, IKey.lang("mappet.gui.triggers.context.add"), () -> {
            GuiSimpleContextMenu adds = (new GuiSimpleContextMenu(this.mc)).shadow();

            for(String key : CommonProxy.getTriggerBlocks().getKeys()) {
               IKey label = IKey.format("mappet.gui.triggers.context.add_trigger", new Object[]{IKey.lang("mappet.gui.trigger_types." + key)});
               int color = CommonProxy.getTriggerBlocks().getColor(key);
               adds.action(Icons.ADD, label, () -> this.addBlock(key), color);
            }

            GuiBase.getCurrent().replaceContextMenu(adds);
         });
         if (!this.list.isDeselected()) {
            menu.action(Icons.COPY, IKey.lang("mappet.gui.triggers.context.copy"), this::copyTrigger);
         }

         try {
            class_2487 tag = class_2522.method_10718(GuiUtils.getClipboardString());
            if (tag.method_10545("_TriggerType")) {
               menu.action(Icons.PASTE, IKey.lang("mappet.gui.triggers.context.paste"), () -> this.pasteTrigger(tag));
            }
         } catch (Exception var3) {
         }

         if (!this.list.isDeselected()) {
            menu.action(Icons.REMOVE, IKey.lang("mappet.gui.triggers.context.remove"), () -> this.removeItem(), 16711731);
         }

         return menu;
      });
      this.pickItem(this.trigger.blocks.isEmpty() ? null : (AbstractTriggerBlock)this.trigger.blocks.get(0), true);
   }

   protected GuiListElement<AbstractTriggerBlock> createList(class_310 mc) {
      return new GuiAbstractBlockListElement(mc, (l) -> this.pickItem((AbstractTriggerBlock)l.get(0), false));
   }

   private void addBlock(String type) {
      AbstractTriggerBlock block = (AbstractTriggerBlock)CommonProxy.getTriggerBlocks().create(type);
      this.trigger.blocks.add(block);
      this.pickItem(block, true);
      this.list.update();
   }

   private void copyTrigger() {
      AbstractTriggerBlock block = (AbstractTriggerBlock)this.list.getCurrentFirst();
      class_2487 tag = block.serializeNBT();
      tag.method_10582("_TriggerType", CommonProxy.getTriggerBlocks().getType(block));
      GuiUtils.setClipboardString(tag.toString());
   }

   private void pasteTrigger(class_2487 tag) {
      AbstractTriggerBlock block = (AbstractTriggerBlock)CommonProxy.getTriggerBlocks().create(tag.method_10558("_TriggerType"));
      block.deserializeNBT(tag);
      this.trigger.blocks.add(block);
      this.list.update();
      this.pickItem(block, true);
   }

   protected void fillData(AbstractTriggerBlock block) {
      this.editor.removeAll();

      try {
         this.editor.add((GuiAbstractTriggerBlockPanel)((Class)PANELS.get(block.getClass())).getConstructors()[0].newInstance(this.mc, this, block));
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public void onClose() {
      super.onClose();
      this.trigger.recalculateEmpty();
      if (this.onClose != null) {
         this.onClose.run();
      }

   }

   public void draw(GuiContext context) {
      super.draw(context);
      if (this.trigger.blocks.isEmpty()) {
         GuiMappetUtils.drawRightClickHere(context, this.list.area);
      }

   }

   public boolean keyTyped(GuiContext context) {
      if (super.keyTyped(context)) {
         return true;
      }

      if (GuiUtils.isCtrlKeyDown() && context.keyCode == 31) {
         return true;
      }

      return false;
   }

   static {
      PANELS.put(CommandTriggerBlock.class, GuiCommandTriggerBlockPanel.class);
      PANELS.put(SoundTriggerBlock.class, GuiSoundTriggerBlockPanel.class);
      PANELS.put(EventTriggerBlock.class, GuiEventTriggerBlockPanel.class);
      PANELS.put(DialogueTriggerBlock.class, GuiDialogueTriggerBlockPanel.class);
      PANELS.put(ScriptTriggerBlock.class, GuiScriptTriggerBlockPanel.class);
      PANELS.put(ItemTriggerBlock.class, GuiItemTriggerBlockPanel.class);
      PANELS.put(StateTriggerBlock.class, GuiStateTriggerBlockPanel.class);
      PANELS.put(MorphTriggerBlock.class, GuiMorphTriggerBlockPanel.class);
   }

   public static class GuiAbstractBlockListElement extends GuiListElement<AbstractTriggerBlock> {
      public GuiAbstractBlockListElement(class_310 mc, Consumer<List<AbstractTriggerBlock>> callback) {
         super(mc, callback);
      }

      protected void drawElementPart(AbstractTriggerBlock element, int i, int x, int y, boolean hover, boolean selected) {
         int color = CommonProxy.getTriggerBlocks().getColor(element);
         GuiDraw.drawRect(x, y, x + 4, y + this.scroll.scrollItemSize, -16777216 + color);
         GuiDraw.drawHorizontalGradientRect(x + 4, y, x + 24, y + this.scroll.scrollItemSize, 1140850688 + color, color);
         super.drawElementPart(element, i, x + 4, y, hover, selected);
      }

      protected String elementToString(AbstractTriggerBlock element) {
         return element.stringify();
      }
   }
}
