package mchorse.mappet.api.ui;

import java.util.UUID;
import mchorse.mappet.api.ui.utils.UIRootComponent;
import mchorse.mappet.api.utils.AbstractData;
import net.minecraft.class_2487;

public class UI extends AbstractData {
   private UUID id;
   public UIRootComponent root;
   public boolean background;
   public boolean closable;
   public boolean paused;
   public int enterDuration;
   public String enterInterpolation = "sine_inout";
   public int exitDuration;
   public String exitInterpolation = "sine_inout";

   public UI() {
      this(UUID.randomUUID());
   }

   public UI(UUID id) {
      this.root = new UIRootComponent();
      this.closable = true;
      this.id = id;
   }

   public UUID getUIId() {
      return this.id;
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_25927("ID", this.id);
      tag.method_10566("Root", this.root.serializeNBT());
      tag.method_10556("Background", this.background);
      tag.method_10556("Closeable", this.closable);
      tag.method_10556("Paused", this.paused);
      tag.method_10569("EnterDuration", this.enterDuration);
      tag.method_10582("EnterInterpolation", this.enterInterpolation);
      tag.method_10569("ExitDuration", this.exitDuration);
      tag.method_10582("ExitInterpolation", this.exitInterpolation);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      this.root.deserializeNBT(tag.method_10562("Root"));
      if (tag.method_25928("ID")) {
         this.id = tag.method_25926("ID");
      }

      if (tag.method_10545("Background")) {
         this.background = tag.method_10577("Background");
      }

      if (tag.method_10545("Closeable")) {
         this.closable = tag.method_10577("Closeable");
      }

      if (tag.method_10545("Paused")) {
         this.paused = tag.method_10577("Paused");
      }

      if (tag.method_10545("EnterDuration")) {
         this.enterDuration = tag.method_10550("EnterDuration");
      }

      if (tag.method_10545("EnterInterpolation")) {
         this.enterInterpolation = tag.method_10558("EnterInterpolation");
      }

      if (tag.method_10545("ExitDuration")) {
         this.exitDuration = tag.method_10550("ExitDuration");
      }

      if (tag.method_10545("ExitInterpolation")) {
         this.exitInterpolation = tag.method_10558("ExitInterpolation");
      }

   }
}
