package mchorse.mappet.client.gui.panels;

import mchorse.mappet.Mappet;
import mchorse.mappet.MappetClient;
import mchorse.mappet.api.misc.ClientSettings;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.triggers.GuiTriggerElement;
import mchorse.mappet.client.gui.utils.text.GuiText;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.content.PacketClientSettings;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiLabelListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiLabelSearchListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiConfirmModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.mclib.GuiDashboardPanel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import net.minecraft.class_2487;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import org.lwjgl.input.Keyboard;

public class GuiClientSettingsPanel extends GuiDashboardPanel<GuiMappetDashboard> {
   private static final String[] TRIGGERS = ClientSettings.GLOBAL_TRIGGERS;

   public GuiElement triggerCategoryTabs;
   public GuiButtonElement playerTriggerCategory;
   public GuiButtonElement livingTriggerCategory;
   public GuiButtonElement entityTriggerCategory;
   public GuiButtonElement otherTriggerCategory;
   public GuiToggleElement modTriggerToggle;
   public GuiLabelSearchListElement<String> triggers;
   public GuiTriggerElement trigger;
   public GuiScrollElement editor;
   public GuiIconElement applyChanges;
   public GuiIconElement resetChanges;
   private final ClientSettings settings;
   private String lastTrigger = TRIGGERS[0];
   private TriggerCategory triggerCategory = TriggerCategory.PLAYER;
   private boolean modTriggersOnly;
   private class_2487 snapshot;

   public GuiClientSettingsPanel(class_310 mc, GuiMappetDashboard dashboard) {
      super(mc, dashboard);
      this.settings = MappetClient.clientSettings;

      GuiLabel title = Elements.label(IKey.lang("mappet.gui.client_settings.title")).anchor(0.0F, 0.5F).background();
      title.flex().relative(this).xy(10, 10).wh(160, 20);

      this.triggerCategoryTabs = new GuiElement(mc);
      this.playerTriggerCategory = this.createTriggerCategoryButton(TriggerCategory.PLAYER);
      this.livingTriggerCategory = this.createTriggerCategoryButton(TriggerCategory.LIVING);
      this.entityTriggerCategory = this.createTriggerCategoryButton(TriggerCategory.ENTITY);
      this.otherTriggerCategory = this.createTriggerCategoryButton(TriggerCategory.OTHER);
      this.modTriggerToggle = new GuiToggleElement(mc, IKey.lang("mappet.gui.settings.categories.mods"), false, this::setModTriggersOnly);
      this.triggerCategoryTabs.flex().relative(this).xy(10, 35).w(1.0F, -110).h(20);
      this.playerTriggerCategory.flex().relative(this.triggerCategoryTabs).x(0.0F).w(0.25F).h(20);
      this.livingTriggerCategory.flex().relative(this.triggerCategoryTabs).x(0.25F).w(0.25F).h(20);
      this.entityTriggerCategory.flex().relative(this.triggerCategoryTabs).x(0.5F).w(0.25F).h(20);
      this.otherTriggerCategory.flex().relative(this.triggerCategoryTabs).x(0.75F).w(0.25F).h(20);
      this.modTriggerToggle.flex().relative(this).x(1.0F, -96).y(35).wh(90, 20);
      this.triggerCategoryTabs.add(new IGuiElement[]{this.playerTriggerCategory, this.livingTriggerCategory, this.entityTriggerCategory, this.otherTriggerCategory});

      this.triggers = new GuiLabelSearchListElement<String>(mc, (l) -> this.fillTrigger((Label)l.get(0), false));
      this.triggers.label(IKey.lang("mappet.gui.search"));
      this.triggers.list.background().flex().relative(this.triggers).y(20).w(1.0F).h(1.0F, -20);
      this.triggers.flex().relative(this).xy(10, 60).w(1.0F, -20).h(221);

      this.trigger = (new GuiTriggerElement(mc)).onClose(this::updateCurrentTrigger);
      this.trigger.flex().relative(this).x(1.0F, -10).y(1.0F, -10).wh(120, 20).anchor(1.0F, 1.0F);

      this.editor = new GuiScrollElement(mc);
      this.editor.flex().relative(this).xy(10, 281).w(1.0F, -20).h(1.0F, -311).column(5).scroll().stretch().padding(10);

      this.applyChanges = new GuiIconElement(mc, Icons.SAVE, (b) -> this.apply());
      this.applyChanges.tooltip(IKey.lang("mappet.gui.client_settings.apply"), Direction.LEFT);
      this.applyChanges.flex().relative(this).x(1.0F, -42).y(20).wh(20, 20).anchor(0.5F, 0.5F);

      this.resetChanges = new GuiIconElement(mc, Icons.REFRESH, (b) -> this.reset());
      this.resetChanges.tooltip(IKey.lang("mappet.gui.client_settings.reset"), Direction.LEFT);
      this.resetChanges.flex().relative(this).x(1.0F, -68).y(20).wh(20, 20).anchor(0.5F, 0.5F);

      this.add(new IGuiElement[]{title, this.triggerCategoryTabs, this.modTriggerToggle, this.triggers, this.editor, this.trigger, this.applyChanges, this.resetChanges});
   }

   private GuiButtonElement createTriggerCategoryButton(TriggerCategory category) {
      return (new GuiButtonElement(this.mc, IKey.lang(category.translationKey), (button) -> this.selectTriggerCategory(category))).background(true);
   }

   private void setModTriggersOnly(GuiToggleElement toggle) {
      this.modTriggersOnly = toggle.isToggled();
      this.fillGlobalTriggerList(this.lastTrigger);
   }

   private void selectTriggerCategory(TriggerCategory category) {
      if (category == null) {
         return;
      }

      this.triggerCategory = category;
      this.fillGlobalTriggerList(this.lastTrigger);
   }

   private void fillGlobalTriggerList(String preferred) {
      GuiLabelListElement<String> list = (GuiLabelListElement)this.triggers.list;
      String query = this.triggers.search.field.getText();
      list.clear();

      for (String key : TRIGGERS) {
         Trigger trigger = this.settings.getTrigger(key);
         if (this.matchesTriggerFilter(key)) {
            list.add(this.createTooltip(key, trigger), key);
         }
      }

      list.sort();
      this.triggers.filter(query, false);
      Label<String> selected = null;
      for (Label<String> label : list.getList()) {
         if (label.value.equals(preferred)) {
            selected = label;
            break;
         }
      }

      if (selected == null && !list.getList().isEmpty()) {
         selected = (Label)list.getList().get(0);
      }

      if (selected != null) {
         list.setCurrentValue(selected.value);
         this.fillTrigger(selected, true);
      }

      this.updateTriggerCategoryTabs();
   }

   private void updateTriggerCategoryTabs() {
      this.styleTriggerCategoryButton(this.playerTriggerCategory, TriggerCategory.PLAYER);
      this.styleTriggerCategoryButton(this.livingTriggerCategory, TriggerCategory.LIVING);
      this.styleTriggerCategoryButton(this.entityTriggerCategory, TriggerCategory.ENTITY);
      this.styleTriggerCategoryButton(this.otherTriggerCategory, TriggerCategory.OTHER);
      this.modTriggerToggle.toggled(this.modTriggersOnly);
   }

   private boolean matchesTriggerFilter(String key) {
      return TriggerCategory.isBuiltin(key) ? !this.modTriggersOnly && this.triggerCategory.matches(key) : this.triggerCategory.matchesMod(key);
   }

   private void styleTriggerCategoryButton(GuiButtonElement button, TriggerCategory category) {
      boolean selected = category == this.triggerCategory;
      button.custom = selected;
      if (selected) {
         button.color((Integer)Mappet.globalTriggerCategoryColor.get()).textColor(-1, true);
      } else {
         button.textColor(-4144960, true);
      }
   }

   private IKey createTooltip(String key, Trigger trigger) {
      IKey title = IKey.lang("mappet.gui.settings.triggers." + key);

      if (trigger.blocks.isEmpty()) {
         return title;
      }

      return IKey.comp(new IKey[]{title, IKey.str(" §7(§6" + trigger.blocks.size() + "§7)§r")});
   }

   private void fillTrigger(Label<String> label, boolean select) {
      String key = (String)label.value;

      this.editor.removeAll();
      this.editor.add((new GuiText(this.mc)).text(IKey.lang("mappet.gui.settings.triggers.descriptions." + key)));
      this.editor.add(Elements.label(IKey.lang("mappet.gui.settings.variables")).background().marginTop(16).marginBottom(8));
      this.editor.add((new GuiText(this.mc)).text(IKey.lang("mappet.gui.settings.triggers.variables." + key)));

      this.trigger.set(this.settings.getTrigger(key));

      if (select) {
         this.triggers.list.setCurrentScroll(label);
      }

      this.lastTrigger = key;
      this.resize();
   }

   private void updateCurrentTrigger() {
      Label<String> label = (Label)this.triggers.list.getCurrentFirst();

      if (label != null) {
         label.title = this.createTooltip(this.lastTrigger, this.settings.getTrigger(this.lastTrigger));
      }
   }

   private void apply() {
      if (this.isSettingsDirty()) {
         class_2487 tag = this.settings.serializeNBT();
         this.settings.save();
         this.snapshot = this.copyTag(tag);
         Dispatcher.sendToServer(new PacketClientSettings(tag));
      }

      if (this.mc.field_1724 != null) {
         this.mc.field_1724.method_7353(class_2561.method_43470(IKey.lang("mappet.gui.client_settings.saved").get()), false);
      }
   }

   private void reset() {
      if (this.snapshot != null) {
         for (Trigger trigger : this.settings.triggers.values()) {
            trigger.blocks.clear();
            trigger.recalculateEmpty();
         }

         this.settings.deserializeNBT(this.copyTag(this.snapshot));
      }

      this.fillGlobalTriggerList(this.lastTrigger);
   }

   public boolean hasUnsavedChanges() {
      return this.isSettingsDirty();
   }

   private boolean isSettingsDirty() {
      return this.snapshot != null && !this.settings.serializeNBT().equals(this.snapshot);
   }

   private class_2487 copyTag(class_2487 tag) {
      return tag == null ? null : (class_2487)tag.method_10707();
   }

   public void requireChangeResolution(Runnable continuation) {
      if (!this.hasUnsavedChanges()) {
         if (continuation != null) {
            continuation.run();
         }
         return;
      }

      if (GuiModal.hasModal(this)) {
         return;
      }

      GuiModal.addFullModal(this, () -> {
         GuiConfirmModal modal = new GuiConfirmModal(this.mc, IKey.str("Есть неприменённые изменения"), (apply) -> {
            if (apply) {
               this.apply();
            } else {
               this.reset();
            }

            if (continuation != null) {
               continuation.run();
            }
         });
         modal.confirm.label = IKey.str("Применить");
         modal.cancel.label = IKey.str("Сбросить");
         return modal;
      });
   }

   @Override
   public boolean keyTyped(GuiContext context) {
      if (GuiUtils.isCtrlKeyDown()) {
         if (context.keyCode == 31) {
            this.apply();
            return true;
         }

         if (context.keyCode == Keyboard.KEY_R) {
            this.reset();
            return true;
         }
      }

      return super.keyTyped(context);
   }

   public void save() {
      this.apply();
   }

   @Override
   public void appear() {
      super.appear();
      this.settings.load();
      this.snapshot = this.copyTag(this.settings.serializeNBT());
      this.fillGlobalTriggerList(this.lastTrigger);
   }

   public void fill(class_2487 tag) {
      this.settings.fill(tag);
      this.snapshot = this.copyTag(this.settings.serializeNBT());
      this.fillGlobalTriggerList(this.lastTrigger);
   }

   @Override
   public void disappear() {
      super.disappear();
   }

   @Override
   public void close() {
      super.close();
      this.snapshot = null;
   }

   @Override
   public void draw(GuiContext context) {
      boolean dirty = this.isSettingsDirty();
      this.applyChanges.setEnabled(dirty);
      this.resetChanges.setEnabled(dirty);
      super.draw(context);
   }
}