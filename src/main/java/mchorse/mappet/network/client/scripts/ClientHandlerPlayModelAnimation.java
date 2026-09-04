package mchorse.mappet.network.client.scripts;

import mchorse.chameleon.animation.ActionConfig;
import mchorse.chameleon.animation.Animator;
import mchorse.chameleon.metamorph.ChameleonMorph;
import mchorse.mappet.network.common.scripts.PacketPlayModelAnimation;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerPlayModelAnimation extends ClientMessageHandler<PacketPlayModelAnimation> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketPlayModelAnimation message) {
      if (message.animation.isEmpty() || class_310.method_1551().field_1687 == null) {
         return;
      }

      class_1297 entity = class_310.method_1551().field_1687.method_8469(message.entityId);
      AbstractMorph morph = this.getMorph(entity);
      if (!(morph instanceof ChameleonMorph)) {
         return;
      }

      ChameleonMorph chameleon = (ChameleonMorph)morph;
      ActionConfig config = chameleon.actions.getConfig(message.animation);
      if (config == null) {
         return;
      }

      Animator animator = chameleon.getAnimator();
      animator.addAction(animator.createAction(animator.animation, config, false));
   }

   @Environment(EnvType.CLIENT)
   private AbstractMorph getMorph(class_1297 entity) {
      if (entity == null) {
         return null;
      }


      if (entity instanceof IMorphProvider) {
         return ((IMorphProvider)entity).getMorph();
      }

      if (entity instanceof class_1657) {
         IMorphing morphing = Morphing.get((class_1657)entity);
         return morphing == null ? null : morphing.getCurrentMorph();
      }

      return null;
   }
}
