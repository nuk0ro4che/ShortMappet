package mchorse.mappet.network.client.scripts;

import mchorse.chameleon.animation.ActionConfig;
import mchorse.chameleon.animation.Animator;
import mchorse.chameleon.metamorph.ChameleonMorph;
import mchorse.mappet.hand.Hands;
import mchorse.mappet.network.common.scripts.PacketHandMorphAnimation;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerHandMorphAnimation extends ClientMessageHandler<PacketHandMorphAnimation>
{
    @Override
    @Environment(EnvType.CLIENT)
    public void run(class_746 player, PacketHandMorphAnimation message)
    {
        if (message.animation.isEmpty() || class_310.method_1551().field_1724 == null)
        {
            return;
        }

        AbstractMorph morph = Hands.get(player).get(message.side).morph;
        if (!(morph instanceof ChameleonMorph chameleon))
        {
            return;
        }

        ActionConfig config = chameleon.actions.getConfig(message.animation);
        if (config == null)
        {
            return;
        }

        Animator animator = chameleon.getAnimator();
        animator.addAction(animator.createAction(animator.animation, config, false));
    }
}
