package mchorse.mappet.client.gui.scripts.utils.documentation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import mchorse.mappet.client.gui.utils.text.GuiText;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import net.minecraft.class_124;
import net.minecraft.class_310;

public class DocMethod extends DocEntry {
   public DocReturn returns;
   public List<DocParameter> arguments = new ArrayList<>();
   public List<String> annotations = new ArrayList<>();

   public String getName() {
      String arguments = this.arguments.stream()
         .map(DocParameter::getType)
         .collect(Collectors.joining(", "));

      return super.getName() + "(" + class_124.field_1080 + arguments + class_124.field_1070 + ")";
   }

   public void fillIn(class_310 mc, GuiScrollElement target) {
      super.fillIn(mc, target);
      boolean first = true;

      for (DocParameter parameter : this.arguments) {
         GuiText text = new GuiText(mc).text(
            class_124.field_1065 + parameter.getType() + class_124.field_1070 + " " + parameter.name
         );
         if (first) {
            text.marginTop(8);
         }

         target.add(text);
         if (parameter.doc != null && !parameter.doc.isEmpty()) {
            DocEntry.process(parameter.doc, mc, target);
            GuiElement last = (GuiElement)target.getChildren().get(target.getChildren().size() - 1);
            last.marginBottom(8);
         }

         first = false;
      }

      String returnType = this.returns == null ? "void" : this.returns.getType();
      target.add(new GuiText(mc)
         .text("Returns " + class_124.field_1065 + returnType)
         .marginTop(8));

      List<String> annotationNames = this.annotations.stream()
         .map(annotation -> "@" + annotation.substring(annotation.lastIndexOf('.') + 1))
         .filter(annotation -> !annotation.equals("@Override"))
         .collect(Collectors.toList());

      if (!annotationNames.isEmpty()) {
         target.add(new GuiText(mc)
            .text(String.valueOf(class_124.field_1080) + String.valueOf(class_124.field_1067) + String.join(", ", annotationNames))
            .marginTop(8));
      }

      if (this.returns != null && this.returns.doc != null && !this.returns.doc.isEmpty()) {
         DocEntry.process(this.returns.doc, mc, target);
      }

   }

   public List<DocEntry> getEntries() {
      return this.parent == null ? super.getEntries() : this.parent.getEntries();
   }
}
