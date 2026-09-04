package mchorse.mappet.client.gui.panels;

import mchorse.mappet.MappetClient;
import mchorse.mappet.api.misc.ClientSettings;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.triggers.GuiTriggerElement;
import mchorse.mappet.client.gui.utils.text.GuiText;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiLabelListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.mclib.GuiDashboardPanel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import net.minecraft.class_2561;
import net.minecraft.class_310;

public class GuiClientSettingsPanel extends GuiDashboardPanel<GuiMappetDashboard> {
   private static final String[] TRIGGERS = new String[]{"block_interact", "block_left_click", "client_tick"};

   public GuiLabelListElement<String> triggers;
   public GuiTriggerElement trigger;
   public GuiScrollElement editor;
   public GuiIconElement applyChanges;
   public GuiIconElement resetChanges;
   private final ClientSettings settings;
   private String lastTrigger = TRIGGERS[0];

   public GuiClientSettingsPanel(class_310 mc, GuiMappetDashboard dashboard) {
      super(mc, dashboard);
      this.settings = MappetClient.clientSettings;

      GuiLabel title = Elements.label(IKey.lang("mappet.gui.client_settings.title")).anchor(0.0F, 0.5F).background();
      title.flex().relative(this).xy(10, 10).wh(160, 20);

      this.applyChanges = new GuiIconElement(mc, Icons.SAVE, (b) -> this.apply());
      this.applyChanges.tooltip(IKey.lang("mappet.gui.client_settings.apply"), Direction.LEFT);
      this.applyChanges.flex().relative(this).x(1.0F, -16).y(20).wh(20, 20).anchor(0.5F, 0.5F);

      this.resetChanges = new GuiIconElement(mc, Icons.REFRESH, (b) -> this.reset());
      this.resetChanges.tooltip(IKey.lang("mappet.gui.client_settings.reset"), Direction.LEFT);
      this.resetChanges.flex().relative(this).x(1.0F, -42).y(20).wh(20, 20).anchor(0.5F, 0.5F);

      this.triggers = new GuiLabelListElement<String>(mc, (l) -> this.fillTrigger((Label)l.get(0), false));
      this.triggers.background().flex().relative(this).x(10).y(40).w(0.4F, -20).h(1.0F, -50);

      this.editor = new GuiScrollElement(mc);
      this.editor.flex().relative(this).x(0.4F).y(40).w(0.6F).h(1.0F, -80).column(5).scroll().stretch().padding(10);

      this.trigger = (new GuiTriggerElement(mc)).onClose(this::updateCurrentTrigger);
      this.trigger.flex().relative(this).x(1.0F, -10).y(1.0F, -10).wh(120, 20).anchor(1.0F, 1.0F);

      this.add(new IGuiElement[]{title, this.triggers, this.editor, this.trigger, this.applyChanges, this.resetChanges});

      this.fillTriggerList();
   }

   private void fillTriggerList() {
      this.triggers.clear();

      for (String key : TRIGGERS) {
         this.triggers.add(this.createTooltip(key, this.getTrigger(key)), key);
      }

      Label<String> current = null;

      for (Label<String> label : this.triggers.getList()) {
         if (label.value.equals(this.lastTrigger)) {
            current = label;
            break;
         }
      }

      if (current != null) {
         this.fillTrigger(current, true);
      }
   }

   private IKey createTooltip(String key, Trigger trigger) {
      IKey title = IKey.lang("mappet.gui.client_settings.triggers." + key);

      if (trigger.blocks.isEmpty()) {
         return title;
      }

      return IKey.comp(new IKey[]{title, IKey.str(" §7(§6" + trigger.blocks.size() + "§7)§r")});
   }

   private void fillTrigger(Label<String> label, boolean select) {
      String key = (String)label.value;

      this.editor.removeAll();
      this.editor.add((new GuiText(this.mc)).text(IKey.lang("mappet.gui.client_settings.triggers.descriptions." + key)));
      this.editor.add(Elements.label(IKey.lang("mappet.gui.settings.variables")).background().marginTop(16).marginBottom(8));
      this.editor.add((new GuiText(this.mc)).text(IKey.lang("mappet.gui.client_settings.triggers.variables." + key)));

      this.trigger.set(this.getTrigger(key));

      if (select) {
         this.triggers.setCurrentScroll(label);
      }

      this.lastTrigger = key;
      this.resize();
   }

   private Trigger getTrigger(String key) {
      switch (key) {
         case "block_left_click":
            return this.settings.blockLeftClick;
         case "client_tick":
            return this.settings.playerTick;
         default:
            return this.settings.blockInteract;
      }
   }

   



   private void updateCurrentTrigger() {
      Label<String> label = (Label)this.triggers.getCurrentFirst();

      if (label != null) {
         label.title = this.createTooltip(this.lastTrigger, this.getTrigger(this.lastTrigger));
      }
   }

   private void apply() {
      this.settings.save();

      if (this.mc.field_1724 != null) {
         this.mc.field_1724.method_7353(class_2561.method_43470(IKey.lang("mappet.gui.client_settings.saved").get()), false);
      }
   }

   private void reset() {
      this.settings.reload();
      this.fillTriggerList();
   }

   @Override
   public void close() {
      super.close();
      this.settings.save();
   }
}
