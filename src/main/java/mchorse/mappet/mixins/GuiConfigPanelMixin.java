package mchorse.mappet.mixins;

import java.util.ArrayList;
import java.util.List;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.gui.GuiConfigPanel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiConfigPanel.class)
public abstract class GuiConfigPanelMixin {
   @Shadow public GuiScrollElement options;
   @Shadow private Config config;

   @Inject(method = "refresh", at = @At("RETURN"))
   private void mappet$removeAutocompleteTooltips(CallbackInfo ci) {
      if (this.config == null || !"autocomplete".equals(this.config.id)) {
         return;
      }

      this.options.removeTooltip();
      List<GuiElement> elements = this.options.getChildren(GuiElement.class, new ArrayList<GuiElement>(), true);
      for(GuiElement element : elements) {
         element.removeTooltip();
      }
   }
}
