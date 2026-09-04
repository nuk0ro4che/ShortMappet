package mchorse.mappet.client.gui.scripts.utils.documentation;

public class DocDelegate extends DocEntry {
   public DocEntry delegate;

   public DocDelegate(DocEntry delegate) {
      this.delegate = delegate;
   }

   public String getName() {
      return "../";
   }

   public DocEntry getEntry() {
      return this.delegate;
   }
}
