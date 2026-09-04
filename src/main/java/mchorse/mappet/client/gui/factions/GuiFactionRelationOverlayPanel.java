package mchorse.mappet.client.gui.factions;

import java.util.List;
import java.util.function.Consumer;
import mchorse.mappet.api.factions.FactionRelation;
import mchorse.mappet.client.gui.panels.GuiFactionPanel;
import mchorse.mappet.client.gui.utils.overlays.GuiEditorOverlayPanel;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiFactionRelationOverlayPanel extends GuiEditorOverlayPanel<FactionRelation.Threshold> {
   public GuiTextElement title;
   public GuiColorElement color;
   public GuiTrackpadElement score;
   public GuiCirculateElement attitude;

   public GuiFactionRelationOverlayPanel(class_310 mc, FactionRelation relation) {
      super(mc, IKey.lang("mappet.gui.factions.overlay.main"));
      this.list.sorting().setList(relation.thresholds);
      this.title = new GuiTextElement(mc, 1000, (t) -> (this.item).title = t);
      this.color = new GuiColorElement(mc, (c) -> (this.item).color = c);
      this.score = (new GuiTrackpadElement(mc, (v) -> (this.item).score = v.intValue())).integer();
      this.attitude = GuiFactionPanel.createButton(mc, (a) -> (this.item).attitude = a);
      this.editor.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.factions.overlay.title")), this.title});
      this.editor.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.factions.overlay.color")).marginTop(12), this.color});
      this.editor.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.factions.overlay.score")).marginTop(12), this.score});
      this.editor.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.factions.overlay.attitude")).marginTop(12), this.attitude});
      this.pickItem(relation.thresholds.isEmpty() ? null : (FactionRelation.Threshold)relation.thresholds.get(0), true);
   }

   protected GuiListElement<FactionRelation.Threshold> createList(class_310 mc) {
      return new GuiThresholdListElement(mc, (l) -> this.pickItem((FactionRelation.Threshold)l.get(0), false));
   }

   protected IKey getAddLabel() {
      return IKey.lang("mappet.gui.factions.overlay.context.add");
   }

   protected IKey getRemoveLabel() {
      return IKey.lang("mappet.gui.factions.overlay.context.remove");
   }

   protected void addNewItem() {
      this.list.add(new FactionRelation.Threshold());
   }

   protected void fillData(FactionRelation.Threshold item) {
      this.title.setText(item.title);
      this.color.picker.setColor(item.color);
      this.score.setValue((double)item.score);
      GuiFactionPanel.setValue(this.attitude, item.attitude);
   }

   public static class GuiThresholdListElement extends GuiListElement<FactionRelation.Threshold> {
      public GuiThresholdListElement(class_310 mc, Consumer<List<FactionRelation.Threshold>> callback) {
         super(mc, callback);
      }

      protected void drawElementPart(FactionRelation.Threshold element, int i, int x, int y, boolean hover, boolean selected) {
         GuiDraw.drawRect(x, y, x + 4, y + this.scroll.scrollItemSize, -16777216 + element.color);
         GuiDraw.drawHorizontalGradientRect(x + 4, y, x + 24, y + this.scroll.scrollItemSize, 1140850688 + element.color, element.color);
         super.drawElementPart(element, i, x + 4, y, hover, selected);
      }

      protected String elementToString(FactionRelation.Threshold element) {
         return element.score + " " + element.title;
      }
   }
}
