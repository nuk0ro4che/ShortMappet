package mchorse.mappet.client.gui.scripts.utils.documentation;

import java.util.ArrayList;
import java.util.List;

public class DocList extends DocEntry {
   public List<DocEntry> entries = new ArrayList();

   public String getName() {
      return this.name;
   }

   public List<DocEntry> getEntries() {
      return this.entries;
   }
}
