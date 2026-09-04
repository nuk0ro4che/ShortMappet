package mchorse.mappet.client.gui.scripts.utils.documentation;

public class DocParameter extends DocEntry {
   private String type = "";

   public String getType() {
      int index = this.type.lastIndexOf(".");
      return index < 0 ? this.type : this.type.substring(index + 1);
   }
}
