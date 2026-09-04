package mchorse.mappet.api.data;

import java.io.File;
import java.time.Instant;
import mchorse.mappet.api.utils.manager.BaseManager;
import mchorse.mappet.utils.Utils;
import net.minecraft.class_2487;
import org.apache.commons.io.FileUtils;

public class DataManager extends BaseManager<Data> {
   private File date;
   private Instant lastClear;
   private boolean lastInventory;

   public DataManager(File folder) {
      super(folder);
      if (folder != null) {
         this.date = new File(folder.getParentFile(), "date.txt");
      }

   }

   public Instant getLastClear() {
      if (this.lastClear == null) {
         try {
            String text = FileUtils.readFileToString(this.date, Utils.getCharset()).trim();
            String[] splits = text.split("\n");
            this.lastClear = Instant.parse(splits[0]);
            if (splits.length > 1) {
               this.lastInventory = splits[1].trim().equals("1");
            }
         } catch (Exception var3) {
            this.lastClear = Instant.EPOCH;
         }
      }

      return this.lastClear;
   }

   public boolean getLastInventory() {
      if (this.lastClear == null) {
         this.getLastClear();
      }

      return this.lastInventory;
   }

   public void updateLastClear(boolean inventory) {
      this.lastClear = Instant.now();

      try {
         FileUtils.writeStringToFile(this.date, this.lastClear.toString() + (inventory ? "\n1" : "\n0"), Utils.getCharset());
      } catch (Exception var3) {
      }

   }

   protected Data createData(String id, class_2487 tag) {
      Data data = new Data();
      if (tag != null) {
         data.deserializeNBT(tag);
      }

      return data;
   }
}
