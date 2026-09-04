package mchorse.mappet.client.gui.panels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mchorse.mappet.EventHandler;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.misc.ServerSettings;
import mchorse.mappet.api.states.States;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.events.GuiTriggerHotkeysOverlayPanel;
import mchorse.mappet.client.gui.states.GuiStatesEditor;
import mchorse.mappet.client.gui.triggers.GuiTriggerElement;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlay;
import mchorse.mappet.client.gui.utils.overlays.GuiStringOverlayPanel;
import mchorse.mappet.client.gui.utils.text.GuiText;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.content.PacketRequestServerSettings;
import mchorse.mappet.network.common.content.PacketRequestStates;
import mchorse.mappet.network.common.content.PacketServerSettings;
import mchorse.mappet.network.common.content.PacketStates;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.list.GuiLabelListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiLabelSearchListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiConfirmModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.mclib.GuiDashboardPanel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import net.minecraft.class_1657;
import net.minecraft.class_2487;
import net.minecraft.class_310;
import org.lwjgl.input.Keyboard;

public class GuiServerSettingsPanel extends GuiDashboardPanel<GuiMappetDashboard> {
   public GuiElement states;
   public GuiStatesEditor statesEditor;
   public GuiLabel statesTitle;
   public GuiIconElement statesSwitch;
   public GuiIconElement statesAdd;
   public GuiLabelSearchListElement<String> triggers;
   public GuiTriggerElement trigger;
   public GuiElement triggerCategoryTabs;
   public GuiButtonElement serverTriggerCategory;
   public GuiButtonElement playerTriggerCategory;
   public GuiButtonElement livingTriggerCategory;
   public GuiButtonElement entityTriggerCategory;
   public GuiButtonElement otherTriggerCategory;
   public GuiToggleElement modTriggerToggle;
   public GuiIconElement hotkeys;
   public GuiIconElement layoutToggleIcon;
   public GuiIconElement applyChanges;
   public GuiIconElement resetChanges;
   public GuiScrollElement editor;
   public GuiLabelListElement<String> forgeTriggers;
   public GuiTriggerElement forgeTrigger;
   public GuiElement globalTriggersLayout;
   public GuiElement forgeTriggersLayout;
   private ServerSettings settings;
   private String lastTarget;
   private String lastTrigger = "server_load";
   private TriggerCategory triggerCategory = TriggerCategory.SERVER;
   private boolean modTriggersOnly;
   private String lastForgeTrigger = "";
   private String lastStates = "~";
   private class_2487 settingsSnapshot;
   private class_2487 statesSnapshot;
   private String statesSnapshotTarget;

   public GuiServerSettingsPanel(class_310 mc, GuiMappetDashboard dashboard) {
      super(mc, dashboard);
      this.states = new GuiElement(mc);
      this.states.flex().relative(this).wh(0.5F, 1.0F);
      this.statesEditor = new GuiStatesEditor(mc);
      this.statesEditor.flex().relative(this.states).y(35).w(1.0F).h(1.0F, -35);
      this.statesTitle = Elements.label(IKey.str("")).anchor(0.0F, 0.5F).background();
      this.statesTitle.flex().relative(this.states).xy(10, 10).wh(120, 20);
      this.statesSwitch = new GuiIconElement(mc, Icons.SEARCH, this::openSearch);
      this.statesSwitch.flex().relative(this.states).x(1.0F, -50).y(10);
      this.statesAdd = new GuiIconElement(mc, Icons.ADD, this::addState);
      this.statesAdd.flex().relative(this.states).x(1.0F, -30).y(10);
      this.globalTriggersLayout = new GuiElement(mc);
      this.globalTriggersLayout.flex().relative(this).wh(0.5F, 0.5F);
      this.triggerCategoryTabs = new GuiElement(mc);
      this.serverTriggerCategory = this.createTriggerCategoryButton(TriggerCategory.SERVER);
      this.playerTriggerCategory = this.createTriggerCategoryButton(TriggerCategory.PLAYER);
      this.livingTriggerCategory = this.createTriggerCategoryButton(TriggerCategory.LIVING);
      this.entityTriggerCategory = this.createTriggerCategoryButton(TriggerCategory.ENTITY);
      this.otherTriggerCategory = this.createTriggerCategoryButton(TriggerCategory.OTHER);
      this.modTriggerToggle = new GuiToggleElement(mc, IKey.lang("mappet.gui.settings.categories.mods"), false, this::setModTriggersOnly);
      this.triggerCategoryTabs.flex().relative(this).x(0.5F, 10).y(35).w(0.5F, -110).h(20);
      this.serverTriggerCategory.flex().relative(this.triggerCategoryTabs).x(0.0F).w(0.2F).h(20);
      this.playerTriggerCategory.flex().relative(this.triggerCategoryTabs).x(0.2F).w(0.2F).h(20);
      this.livingTriggerCategory.flex().relative(this.triggerCategoryTabs).x(0.4F).w(0.2F).h(20);
      this.entityTriggerCategory.flex().relative(this.triggerCategoryTabs).x(0.6F).w(0.2F).h(20);
      this.otherTriggerCategory.flex().relative(this.triggerCategoryTabs).x(0.8F).w(0.2F).h(20);
      this.modTriggerToggle.flex().relative(this).x(1.0F, -96).y(35).wh(90, 20);
      this.triggerCategoryTabs.add(new IGuiElement[]{this.serverTriggerCategory, this.playerTriggerCategory, this.livingTriggerCategory, this.entityTriggerCategory, this.otherTriggerCategory});
      this.triggers = new GuiLabelSearchListElement<String>(mc, (l) -> this.fillTrigger((Label)l.get(0), false));
      this.triggers.label(IKey.lang("mappet.gui.search"));
      this.triggers.list.background().flex().relative(this.triggers).y(20).w(1.0F).h(1.0F, -20);
      this.triggers.flex().relative(this).x(0.5F, 10).y(60).w(0.5F, -20).h(221);
      this.trigger = (new GuiTriggerElement(mc)).onClose(this::updateCurrentTrigger);
      this.trigger.flex().relative(this).x(1.0F, -10).y(1.0F, -10).wh(120, 20).anchor(1.0F, 1.0F);
      this.editor = new GuiScrollElement(mc);
      this.editor.flex().relative(this).x(0.5F).y(281).w(0.5F).h(1.0F, -311).column(5).scroll().stretch().padding(10);
      GuiLabel triggersLabel = Elements.label(IKey.lang("mappet.gui.settings.title")).anchor(0.0F, 0.5F).background();
      triggersLabel.flex().relative(this).x(0.5F, 10).y(10).wh(120, 20);
      this.forgeTriggersLayout = new GuiElement(mc);
      this.forgeTriggersLayout.flex().relative(this).wh(0.5F, 0.5F);
      this.forgeTriggersLayout.setVisible(false);
      this.forgeTriggers = new GuiLabelListElement<String>(mc, (l) -> this.fillForgeTrigger((Label)l.get(0), false));
      this.forgeTriggers.background().flex().relative(this).x(0.5F, 10).y(35).w(0.5F, -20).h(246);
      this.forgeTriggers.context(() -> (new GuiSimpleContextMenu(mc)).action(Icons.ADD, IKey.lang("mappet.gui.settings.forge.add"), this::addForgeTrigger).action(Icons.ADD, IKey.lang("mappet.gui.settings.forge.add_from_list"), this::addForgeTriggerFromList).action(Icons.REMOVE, IKey.lang("mappet.gui.settings.forge.remove"), this::removeCurrentForgeTrigger));
      this.forgeTrigger = (new GuiTriggerElement(mc)).onClose(this::updateCurrentForgeTrigger);
      this.forgeTrigger.flex().relative(this).x(1.0F, -10).y(1.0F, -10).wh(120, 20).anchor(1.0F, 1.0F);
      GuiText forgeAttention = (new GuiText(this.mc)).text(IKey.lang("mappet.gui.settings.forge.attention"));
      forgeAttention.flex().relative(this).x(0.5F).y(281).w(0.5F).h(1.0F, -311);
      forgeAttention.padding(10);
      GuiLabel forgeTriggersLabel = Elements.label(IKey.lang("mappet.gui.settings.forge_title")).anchor(0.0F, 0.5F).background();
      forgeTriggersLabel.flex().relative(this).x(0.5F, 10).y(10).wh(120, 20);
      this.hotkeys = new GuiIconElement(mc, Icons.DOWNLOAD, (b) -> this.openHotkeysEditor());
      this.hotkeys.tooltip(IKey.lang("mappet.gui.settings.hotkeys"), Direction.LEFT);
      this.hotkeys.flex().relative(this).x(1.0F, -16).y(20).wh(20, 20).anchor(0.5F, 0.5F);
      this.applyChanges = new GuiIconElement(mc, Icons.SAVE, (b) -> this.applyChanges());
      this.applyChanges.tooltip(IKey.str("Применить изменения"), Direction.LEFT);
      this.applyChanges.flex().relative(this).x(1.0F, -42).y(20).wh(20, 20).anchor(0.5F, 0.5F);
      this.resetChanges = new GuiIconElement(mc, Icons.REFRESH, (b) -> this.resetChanges());
      this.resetChanges.tooltip(IKey.str("Сбросить изменения"), Direction.LEFT);
      this.resetChanges.flex().relative(this).x(1.0F, -68).y(20).wh(20, 20).anchor(0.5F, 0.5F);
      this.states.add(new IGuiElement[]{this.statesTitle, this.statesSwitch, this.statesAdd, this.statesEditor});
      this.globalTriggersLayout.add(new IGuiElement[]{this.triggerCategoryTabs, this.modTriggerToggle, this.triggers, this.editor, this.trigger, triggersLabel});
      this.forgeTriggersLayout.add(new IGuiElement[]{this.forgeTriggers, this.forgeTrigger, forgeTriggersLabel, forgeAttention});
      this.add(new IGuiElement[]{this.states, this.hotkeys, this.applyChanges, this.resetChanges, this.globalTriggersLayout});
   }

   public void toggleTriggerLayouts() {
      boolean trigger = this.globalTriggersLayout.isVisible();
      this.layoutToggleIcon.both(trigger ? Icons.COPY : Icons.PROCESSOR);
      this.globalTriggersLayout.setVisible(!trigger);
      this.forgeTriggersLayout.setVisible(trigger);
   }

   public void addForgeTrigger() {
      GuiModal.addFullModal(this, () -> new GuiPromptModal(this.mc, IKey.lang("mappet.gui.panels.modals.add"), this::addForgeTrigger));
   }

   public void addForgeTrigger(String name) {
      if (!this.forgeTriggers.getList().contains(name)) {
         this.forgeTriggers.add(IKey.str(name), name);
         this.settings.registeredForgeTriggers.put(name, new Trigger());
      }
   }

   public void removeCurrentForgeTrigger() {
      Label<String> current = (Label)this.forgeTriggers.getCurrentFirst();
      if (current != null) {
         String name = (String)current.value;
         this.forgeTriggers.remove(current);
         this.settings.registeredForgeTriggers.remove(name);
         this.forgeTrigger.setVisible(false);
      }
   }

   public void addForgeTriggerFromList() {
      Set<String> events = (Set)EventHandler.getRegisteredEvents().stream().map(EventHandler::getEventClassName).collect(Collectors.toSet());
      GuiStringOverlayPanel overlay = new GuiStringOverlayPanel(this.mc, IKey.lang("mappet.gui.forge.pick"), false, events, this::addForgeTrigger);
      GuiOverlay.addOverlay(GuiBase.getCurrent(), overlay.set(this.lastTarget), 0.5F, 0.6F);
   }

   private void updateCurrentTrigger() {
      Trigger trigger = (Trigger)this.settings.registered.get(this.lastTrigger);
      Label<String> label = (Label)this.triggers.list.getCurrentFirst();
      if (label != null) {
         label.title = this.createTooltip(this.lastTrigger, trigger);
      }
   }

   private void updateCurrentForgeTrigger() {
      Trigger trigger = (Trigger)this.settings.registeredForgeTriggers.get(this.lastForgeTrigger);
      ((Label)this.forgeTriggers.getCurrentFirst()).title = this.createForgeTooltip(this.lastForgeTrigger, trigger);
   }

   public IKey createTooltip(String key, Trigger trigger) {
      IKey title = IKey.lang("mappet.gui.settings.triggers." + key);
      if (trigger.blocks.isEmpty()) {
         return title;
      } else {
         IKey count = IKey.str(" §7(§6" + trigger.blocks.size() + "§7)§r");
         return IKey.comp(new IKey[]{title, count});
      }
   }

   public IKey createForgeTooltip(String key, Trigger trigger) {
      IKey title = IKey.str(key);
      if (trigger.blocks.isEmpty()) {
         return title;
      } else {
         IKey count = IKey.str(" §7(§6" + trigger.blocks.size() + "§7)§r");
         return IKey.comp(new IKey[]{title, count});
      }
   }

   private GuiButtonElement createTriggerCategoryButton(TriggerCategory category) {
      return (new GuiButtonElement(this.mc, IKey.lang(category.translationKey), (button) -> this.selectTriggerCategory(category))).background(true);
   }

   private void setModTriggersOnly(GuiToggleElement toggle) {
      this.modTriggersOnly = toggle.isToggled();
      if (this.settings != null) {
         this.fillGlobalTriggerList(this.lastTrigger);
      } else {
         this.updateTriggerCategoryTabs();
      }
   }

   private void selectTriggerCategory(TriggerCategory category) {
      if (category == null) {
         return;
      }

      this.triggerCategory = category;
      if (this.settings != null) {
         this.fillGlobalTriggerList(this.lastTrigger);
      } else {
         this.updateTriggerCategoryTabs();
      }
   }

   private void fillGlobalTriggerList(String preferred) {
      GuiLabelListElement<String> list = (GuiLabelListElement)this.triggers.list;
      String query = this.triggers.search.field.getText();
      list.clear();

      for(String key : this.settings.registered.keySet()) {
         if (this.matchesTriggerFilter(key)) {
            list.add(this.createTooltip(key, (Trigger)this.settings.registered.get(key)), key);
         }
      }

      list.sort();
      this.triggers.filter(query, false);
      Label<String> selected = null;
      for(Label<String> label : list.getList()) {
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
      this.styleTriggerCategoryButton(this.serverTriggerCategory, TriggerCategory.SERVER);
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

   private void openSearch(GuiIconElement element) {
      List<String> targets = new ArrayList();
      targets.add("~");

      for(class_1657 player : this.mc.field_1687.method_18456()) {
         targets.add(player.method_7334().getName());
      }

      GuiStringOverlayPanel overlay = new GuiStringOverlayPanel(this.mc, IKey.lang("mappet.gui.states.pick"), false, targets, (target) -> {
         if (!target.isEmpty()) {
            this.requestStates(target);
         }
      });
      GuiOverlay.addOverlay(GuiBase.getCurrent(), overlay.set(this.lastTarget), 0.4F, 0.6F);
   }

   private void addState(GuiIconElement element) {
      this.statesEditor.addNew();
   }

   private void openHotkeysEditor() {
      GuiTriggerHotkeysOverlayPanel overlay = new GuiTriggerHotkeysOverlayPanel(this.mc, this.settings.hotkeys);
      GuiOverlay.addOverlay(GuiBase.getCurrent(), overlay, 0.5F, 0.7F);
   }

   public void fill(class_2487 tag) {
      this.settings = new ServerSettings((File)null);
      this.settings.deserializeNBT(tag);
      this.settingsSnapshot = this.copyTag(tag);
      this.fillGlobalTriggerList(this.lastTrigger);
      this.forgeTriggers.clear();

      for(String key : this.settings.registeredForgeTriggers.keySet()) {
         this.forgeTriggers.add(this.createForgeTooltip(key, (Trigger)this.settings.registeredForgeTriggers.get(key)), key);
      }

      this.forgeTriggers.sort();
      this.forgeTrigger.setVisible(false);
      this.resize();
   }

   private void fillTrigger(Label<String> trigger, boolean select) {
      this.editor.removeAll();
      this.editor.add((new GuiText(this.mc)).text(IKey.lang("mappet.gui.settings.triggers.descriptions." + (String)trigger.value)));
      this.editor.add(Elements.label(IKey.lang("mappet.gui.settings.variables")).background().marginTop(16).marginBottom(8));
      this.editor.add((new GuiText(this.mc)).text(IKey.lang("mappet.gui.settings.triggers.variables." + (String)trigger.value)));
      this.trigger.set((Trigger)this.settings.registered.get(trigger.value));
      if (select) {
         this.triggers.list.setCurrentScroll(trigger);
      }

      this.lastTrigger = (String)trigger.value;
      this.resize();
   }

   private void fillForgeTrigger(Label<String> trigger, boolean select) {
      this.forgeTrigger.set((Trigger)this.settings.registeredForgeTriggers.get(trigger.value));
      this.forgeTrigger.setVisible(true);
      if (select) {
         this.forgeTriggers.setCurrentScroll(trigger);
      }

      this.lastForgeTrigger = (String)trigger.value;
      this.resize();
   }

   public void fillStates(String target, class_2487 data) {
      States states = new States();
      this.statesTitle.label = target.equals("~") ? IKey.lang("mappet.gui.states.server") : IKey.format("mappet.gui.states.player", new Object[]{target});
      states.deserializeNBT(data);
      this.statesEditor.set(states);
      this.lastTarget = target;
      this.lastStates = target;
      this.statesSnapshot = this.copyTag(data);
      this.statesSnapshotTarget = target;
   }

   
   public boolean hasUnsavedChanges() {
      return this.isSettingsDirty() || this.isStatesDirty();
   }

   private boolean isSettingsDirty() {
      return this.settings != null && this.settingsSnapshot != null
         && !this.settings.serializeNBT().equals(this.settingsSnapshot);
   }

   private boolean isStatesDirty() {
      return this.statesEditor.get() != null && this.statesSnapshot != null
         && !this.statesEditor.get().serializeNBT().equals(this.statesSnapshot);
   }

   private class_2487 copyTag(class_2487 tag) {
      return tag == null ? null : (class_2487)tag.method_10707();
   }

   
   public void applyChanges() {
      this.applySettingsChanges();
      this.applyStatesChanges();
   }

   private void applySettingsChanges() {
      if (this.settings != null && this.isSettingsDirty()) {
         class_2487 tag = this.settings.serializeNBT();
         Dispatcher.sendToServer(new PacketServerSettings(tag));
         this.settingsSnapshot = this.copyTag(tag);
      }
   }

   
   public void applyStatesChanges() {
      if (this.statesEditor.get() != null && this.isStatesDirty()) {
         class_2487 tag = this.statesEditor.get().serializeNBT();
         Dispatcher.sendToServer(new PacketStates(this.lastTarget, tag));
         this.statesSnapshot = this.copyTag(tag);
         this.statesSnapshotTarget = this.lastTarget;
      }
   }

   
   public void resetChanges() {
      this.resetSettingsChanges();
      this.resetStatesChanges();
   }

   private void resetSettingsChanges() {
      if (this.settingsSnapshot != null) {
         this.fill(this.copyTag(this.settingsSnapshot));
      }
   }

   
   public void resetStatesChanges() {
      if (this.statesSnapshot != null) {
         this.fillStates(this.statesSnapshotTarget == null ? this.lastTarget : this.statesSnapshotTarget, this.copyTag(this.statesSnapshot));
      }
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
               this.applyChanges();
            } else {
               this.resetChanges();
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

   private void requestStates(String target) {
      if (target.equals(this.lastTarget)) {
         return;
      }

      this.requireChangeResolution(() -> Dispatcher.sendToServer(new PacketRequestStates(target)));
   }

   
   public boolean keyTyped(mchorse.mclib.client.gui.framework.elements.utils.GuiContext context) {
      if (GuiUtils.isCtrlKeyDown()) {
         if (context.keyCode == 31) {
            this.applyStatesChanges();
            return true;
         }
         if (context.keyCode == Keyboard.KEY_R) {
            this.resetStatesChanges();
            return true;
         }
      }
      return super.keyTyped(context);
   }

   
   public void save() {
      this.applyChanges();
   }

   public void appear() {
      super.appear();
      Dispatcher.sendToServer(new PacketRequestServerSettings());
      Dispatcher.sendToServer(new PacketRequestStates(this.lastStates));
   }

   public void disappear() {
      super.disappear();
   }

   public void close() {
      super.close();
      this.statesEditor.set((States)null);
      this.statesSnapshot = null;
      this.statesSnapshotTarget = null;
   }

   public void draw(mchorse.mclib.client.gui.framework.elements.utils.GuiContext context) {
      boolean settingsDirty = this.isSettingsDirty();
      boolean statesDirty = this.isStatesDirty();
      this.applyChanges.setEnabled(settingsDirty || statesDirty);
      this.resetChanges.setEnabled(settingsDirty || statesDirty);
      super.draw(context);
   }

   private enum TriggerCategory {
      SERVER("mappet.gui.settings.categories.server"),
      PLAYER("mappet.gui.settings.categories.player"),
      LIVING("mappet.gui.settings.categories.living"),
      ENTITY("mappet.gui.settings.categories.entity"),
      OTHER("mappet.gui.settings.categories.other");

      public final String translationKey;

      private TriggerCategory(String translationKey) {
         this.translationKey = translationKey;
      }

      public boolean matches(String key) {
         if (key == null) {
            return false;
         }

         switch (this) {
            case SERVER:
               return key.startsWith("server_");
            case PLAYER:
               return key.startsWith("player_") || key.startsWith("block_") || key.equals("mouse_input");
            case LIVING:
               return key.startsWith("living_");
            case ENTITY:
               return key.startsWith("entity_") || key.startsWith("projectile_");
            default:
               return key.equals("state_changed") || key.equals("sound_ended");
         }
      }

      public boolean matchesMod(String key) {
         switch (this) {
            case SERVER:
               return key.startsWith("server_");
            case PLAYER:
               return key.startsWith("player_") || key.startsWith("block_") || key.equals("mouse_input") || key.startsWith("voicechat_");
            case LIVING:
               return key.startsWith("living_");
            case ENTITY:
               return key.startsWith("entity_") || key.startsWith("projectile_");
            default:
               return !key.startsWith("server_") && !key.toLowerCase().contains("player") && !key.startsWith("block_") && !key.startsWith("living_") && !key.startsWith("entity_") && !key.startsWith("projectile_") && !key.startsWith("voicechat_") && !key.equals("mouse_input") && !key.equals("sound_ended");
         }
      }

      public static boolean isBuiltin(String key) {
         return key.equals("block_break") || key.equals("block_place") || key.equals("block_interact") || key.equals("block_click") || key.equals("entity_damaged") || key.equals("entity_attacked") || key.equals("entity_death") || key.equals("entity_landed") || key.equals("server_load") || key.equals("server_tick") || key.equals("player_tick") || key.equals("player_chat") || key.equals("player_login") || key.equals("player_logout") || key.equals("player_lmb") || key.equals("player_rmb") || key.equals("player_respawn") || key.equals("player_item_pickup") || key.equals("player_item_toss") || key.equals("player_item_interact") || key.equals("player_entity_interact") || key.equals("player_close_container") || key.equals("player_open_container") || key.equals("player_journal") || key.equals("living_knockback") || key.equals("projectile_impact") || key.equals("living_equipment_change") || key.equals("player_entity_leash") || key.equals("mouse_input") || key.equals("player_keyboard") || key.equals("state_changed") || key.equals("sound_ended") || key.equals("client_tick");
      }
   }
}
