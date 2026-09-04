package mchorse.mappet.client.gui.panels;

import mchorse.mappet.CommonProxy;
import mchorse.mappet.api.dialogues.Dialogue;
import mchorse.mappet.api.events.nodes.EventBaseNode;
import mchorse.mappet.api.utils.ContentType;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.nodes.GuiEventBaseNodePanel;
import mchorse.mappet.client.gui.nodes.GuiEventNodeGraph;
import mchorse.mappet.client.gui.triggers.GuiTriggerElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_746;

public class GuiDialoguePanel extends GuiMappetRunPanel<Dialogue> {
   public static final IKey EMPTY = IKey.lang("mappet.gui.nodes.info.empty_dialogue");
   public GuiEventNodeGraph graph;
   public GuiEventBaseNodePanel panel;
   public GuiElement bottom;
   public GuiToggleElement closable;
   public GuiTriggerElement onClose;

   public GuiDialoguePanel(class_310 mc, GuiMappetDashboard dashboard) {
      super(mc, dashboard);
      this.namesList.setFileIcon(Icons.BUBBLE);
      this.graph = new GuiEventNodeGraph(mc, CommonProxy.getDialogues(), this::pickNode);
      this.graph.notifyAboutMain().flex().relative(this.editor).wh(1.0F, 1.0F);
      this.bottom = new GuiElement(mc);
      this.bottom.flex().relative(this.sidebar).y(1.0F).w(1.0F).anchorY(1.0F).column(5).vertical().stretch().padding(10);
      this.closable = new GuiToggleElement(mc, IKey.lang("mappet.gui.nodes.dialogue.closable"), (b) -> (this.data).closable = b.isToggled());
      this.onClose = new GuiTriggerElement(mc);
      this.names.flex().hTo(this.bottom.area, -5);
      this.editor.add(this.graph);
      this.bottom.add(new IGuiElement[]{Elements.label(IKey.str("On close trigger")), this.onClose, this.closable});
      this.sidebar.prepend(this.bottom);
      this.fill(null);
   }

   private void pickNode(EventBaseNode node) {
      if (this.panel != null) {
         this.panel.removeFromParent();
         this.panel = null;
      }

      if (node != null) {
         GuiEventBaseNodePanel panel = null;

         try {
            panel = (GuiEventBaseNodePanel)((Class)GuiEventPanel.PANELS.get(node.getClass())).getConstructor(class_310.class).newInstance(this.mc);
            panel.set(node);
         } catch (Exception e) {
            e.printStackTrace();
         }

         if (panel != null) {
            panel.flex().relative(this).y(1.0F).w(220).anchorY(1.0F);
            this.panel = panel;
            this.panel.resize();
            this.editor.add(panel);
         }
      }

   }

   protected void run(class_746 player) {
      this.save();
      this.save = false;
      class_634 var10000 = player.field_3944;
      String var10001 = String.valueOf(player.method_5667());
      var10000.method_45731("mp dialogue open " + var10001 + " " + ((Dialogue)this.data).getId());
   }

   public ContentType getType() {
      return ContentType.DIALOGUE;
   }

   public String getTitle() {
      return "mappet.gui.panels.dialogues";
   }

   public void fill(Dialogue data, boolean allowed) {
      super.fill(data, allowed);
      this.graph.setVisible(data != null);
      this.bottom.setVisible(data != null && allowed);
      if (data != null) {
         this.graph.set(data);
         this.closable.toggled(data.closable);
         this.onClose.set(data.onClose);
      }

   }

   public void draw(GuiContext context) {
      super.draw(context);
      if (!this.graph.isVisible()) {
         int w = this.editor.area.w / 2;
         int x = this.editor.area.mx() - w / 2;
         GuiDraw.drawMultiText(this.font, EMPTY.get(), x, this.area.my(), 16777215, w, 12, 0.5F, 0.5F);
      }

   }
}
