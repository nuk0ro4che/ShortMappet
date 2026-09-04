package mchorse.mappet.client;

import mchorse.mappet.hand.HandState;
import mchorse.mappet.hand.Hands;
import mchorse.metamorph.api.MorphSettings;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.MorphRenderUtils;
import mchorse.metamorph.client.render.IMorphRenderer;
import mchorse.metamorph.client.render.MorphRenderContext;
import mchorse.metamorph.client.render.MorphRendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1309;
import net.minecraft.class_310;
import net.minecraft.class_742;
import net.minecraft.class_897;
import net.minecraft.class_1007;
import net.minecraft.class_1657;
import net.minecraft.class_4587;
import net.minecraft.class_4597;

import java.util.Map;
import java.util.WeakHashMap;

@Environment(EnvType.CLIENT)
public final class HandMorphRenderer
{
    private static final Map<AbstractMorph, Integer> UPDATED_TICKS = new WeakHashMap<>();
    private static final ThreadLocal<Boolean> UPDATING_HAND_MORPH = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<AbstractMorph> CURRENT_MORPH = new ThreadLocal<>();

    private HandMorphRenderer()
    {
    }

    public static boolean render(AbstractMorph morph, class_1657 player, class_1268 hand,
        float tickDelta, class_4587 matrices, class_4597 consumers, int light, HandState.Side state)
    {
        return render(morph, player, hand, tickDelta, matrices, consumers, light, state, false);
    }

    private static boolean render(AbstractMorph morph, class_1657 player, class_1268 hand,
        float tickDelta, class_4587 matrices, class_4597 consumers, int light, HandState.Side state,
        boolean forceActivePlayerMorphHand)
    {
        if (morph == null || player == null)
        {
            return false;
        }

        updateAnimationState(morph, player);

        boolean mainHand = hand == class_1268.field_5808;
        double side = mainHand ? 1.0D : -1.0D;

        matrices.method_22903();
        matrices.method_22904(side * (0.56D + state.x), -0.36D + state.y, -0.72D + state.z);
        MorphRenderContext.push(matrices, consumers, light, 0, tickDelta);

        try
        {
            CURRENT_MORPH.set(morph);

            if (morph instanceof EntityMorph entityMorph)
            {
                resetDamageState(entityMorph, player);
                resetMorphOrientation(entityMorph, player);
                return renderEntityMorphHand(entityMorph, morph, player, hand, matrices, consumers, light, forceActivePlayerMorphHand);
            }

            return renderFullMorphWithoutPlayerRotation(morph, player, tickDelta);
        }
        finally
        {
            CURRENT_MORPH.remove();
            MorphRenderContext.pop();
            matrices.method_22909();
        }
    }

    private static boolean renderEntityMorphHand(EntityMorph entityMorph, AbstractMorph morph,
        class_1657 player, class_1268 hand, class_4587 matrices, class_4597 consumers, int light,
        boolean forceActivePlayerMorphHand)
    {
        IMorphRenderer renderer = MorphRendererRegistry.get(morph);

        if (renderer == null)
        {
            return false;
        }

        



        if (!forceActivePlayerMorphHand || !entityMorph.isPlayer())
        {
            return renderer.renderHand(morph, player, hand);
        }

        if (renderVanillaPlayerMorphHand(entityMorph, player, hand, matrices, consumers, light))
        {
            return true;
        }

        
        MorphSettings settings = entityMorph.getSettings();
        boolean hands = settings.hands;
        settings.hands = true;

        try
        {
            return renderer.renderHand(morph, player, hand);
        }
        finally
        {
            settings.hands = hands;
        }
    }

    




    private static boolean renderVanillaPlayerMorphHand(EntityMorph morph, class_1657 player,
        class_1268 hand, class_4587 matrices, class_4597 consumers, int light)
    {
        class_1309 dummy = morph.getEntity(player.method_37908());

        if (!(dummy instanceof class_742 playerDummy))
        {
            return false;
        }

        class_897<?> rawRenderer = class_310.method_1551().method_1561().method_3953(playerDummy);

        if (!(rawRenderer instanceof class_1007 renderer))
        {
            return false;
        }

        if (hand == class_1268.field_5808)
        {
            renderer.method_4220(matrices, consumers, light, playerDummy);
        }
        else
        {
            renderer.method_4221(matrices, consumers, light, playerDummy);
        }

        return true;
    }


    private static boolean renderFullMorphWithoutPlayerRotation(AbstractMorph morph,
        class_1657 player, float tickDelta)
    {
        float yaw = player.method_36454();
        float pitch = player.method_36455();
        float previousYaw = player.field_5982;
        float previousPitch = player.field_6004;
        float bodyYaw = player.field_6283;
        float headYaw = player.field_6241;
        float previousBodyYaw = player.field_6220;
        float previousHeadYaw = player.field_6259;

        try
        {
            player.method_36456(0.0F);
            player.method_36457(0.0F);
            player.field_5982 = 0.0F;
            player.field_6004 = 0.0F;
            player.field_6283 = 0.0F;
            player.field_6241 = 0.0F;
            player.field_6220 = 0.0F;
            player.field_6259 = 0.0F;

            return MorphRenderUtils.render(morph, player, 0.0D, 0.0D, 0.0D, 0.0F, tickDelta);
        }
        finally
        {
            player.method_36456(yaw);
            player.method_36457(pitch);
            player.field_5982 = previousYaw;
            player.field_6004 = previousPitch;
            player.field_6283 = bodyYaw;
            player.field_6241 = headYaw;
            player.field_6220 = previousBodyYaw;
            player.field_6259 = previousHeadYaw;
        }
    }

    private static void resetDamageState(EntityMorph morph, class_1657 player)
    {
        class_1309 entity = morph.getEntity(player.method_37908());

        if (entity != null)
        {
            entity.field_6213 = 0;
            entity.field_6235 = 0;
        }
    }

    private static void resetMorphOrientation(EntityMorph morph, class_1657 player)
    {
        class_1309 entity = morph.getEntity(player.method_37908());

        if (entity != null)
        {
            entity.method_36456(0.0F);
            entity.method_36457(0.0F);
            entity.field_5982 = 0.0F;
            entity.field_6004 = 0.0F;
            entity.field_6220 = 0.0F;
            entity.field_6259 = 0.0F;
            entity.field_6283 = 0.0F;
            entity.field_6241 = 0.0F;
        }
    }

       




       public static boolean hasActivePlayerEntityMorph(class_1657 player)
    {
        if (player == null)
        {
            return false;
        }

        IMorphing morphing = Morphing.get(player);
        AbstractMorph morph = morphing == null ? null : morphing.getCurrentMorph();

        return morph instanceof EntityMorph entityMorph && entityMorph.isPlayer();
    }

    public static boolean renderActivePlayerEntityMorph(class_1657 player, class_1268 hand,
      float tickDelta, float swingProgress, float equipProgress, class_4587 matrices,
      class_4597 consumers, int light, HandState.Side state)

   {
      if (player == null)
      {
         return false;
      }

      IMorphing morphing = Morphing.get(player);
      AbstractMorph morph = morphing == null ? null : morphing.getCurrentMorph();

      if (!(morph instanceof EntityMorph entityMorph) || !entityMorph.isPlayer())
      {
         return false;
      }

      

      float liveSwingProgress = player.method_6055(tickDelta);

      return renderPlayerMorphHandWithVanillaAnimation(morph, player, hand, tickDelta,
         liveSwingProgress, equipProgress, matrices, consumers, light, state);
   }

       private static boolean renderPlayerMorphHandWithVanillaAnimation(AbstractMorph morph, class_1657 player,
        class_1268 hand, float tickDelta, float swingProgress, float equipProgress,
        class_4587 matrices, class_4597 consumers, int light, HandState.Side state)
    {
        boolean mainHand = hand == class_1268.field_5808;
        float side = mainHand ? 1F : -1F;

        matrices.method_22903();

        try
        {
            


            applyVanillaProfileArmTransform(matrices, side, swingProgress, equipProgress);

            MorphRenderContext.push(matrices, consumers, light, 0, tickDelta);

            try
            {
                CURRENT_MORPH.set(morph);
                EntityMorph entityMorph = (EntityMorph)morph;
                resetDamageState(entityMorph, player);
                resetMorphOrientation(entityMorph, player);

                return renderEntityMorphHand(entityMorph, morph, player, hand, matrices, consumers, light, true);
            }
            finally
            {
                CURRENT_MORPH.remove();
                MorphRenderContext.pop();
            }
        }
        finally
        {
            matrices.method_22909();
        }
    }

    




    private static void applyVanillaProfileArmTransform(class_4587 matrices, float side,
        float swing, float equipProgress)
    {
        float rootSwing = (float)Math.sqrt(swing);
        float xSwing = -0.3F * (float)Math.sin(rootSwing * Math.PI);
        float ySwing = 0.4F * (float)Math.sin(rootSwing * Math.PI * 2F);
        float zSwing = -0.4F * (float)Math.sin(swing * Math.PI);

        matrices.method_46416(side * (xSwing + 0.64F), ySwing - 0.6F - equipProgress * 0.6F,
            zSwing - 0.72F);
        matrices.method_22907(net.minecraft.class_7833.field_40716.rotationDegrees(side * 45F));

        float squaredSwing = (float)Math.sin(swing * swing * Math.PI);
        float rootSwingSin = (float)Math.sin(rootSwing * Math.PI);
        matrices.method_22907(net.minecraft.class_7833.field_40716.rotationDegrees(side * rootSwingSin * 70F));
        matrices.method_22907(net.minecraft.class_7833.field_40718.rotationDegrees(side * squaredSwing * -20F));

        matrices.method_46416(-side, 3.6F, 3.5F);
        matrices.method_22907(net.minecraft.class_7833.field_40718.rotationDegrees(side * 120F));
        matrices.method_22907(net.minecraft.class_7833.field_40714.rotationDegrees(200F));
        matrices.method_22907(net.minecraft.class_7833.field_40716.rotationDegrees(side * -135F));
        matrices.method_46416(side * 5.6F, 0F, 0F);
    }

    public static boolean hasActiveMorph(class_1657 player)

   {

        return player != null && (Hands.get(player).get(0).morph != null || Hands.get(player).get(1).morph != null);
    }

    public static AbstractMorph getCurrentMorph()
    {
        return CURRENT_MORPH.get();
    }

    public static boolean isUpdatingHandMorph()
    {
        return UPDATING_HAND_MORPH.get();
    }

    private static void updateAnimationState(AbstractMorph morph, class_1309 player)
    {
        int tick = player.field_6012;
        Integer updatedTick = UPDATED_TICKS.get(morph);

        if (updatedTick != null && updatedTick == tick)
        {
            return;
        }

        UPDATING_HAND_MORPH.set(true);

        try
        {
            morph.update(player);
            UPDATED_TICKS.put(morph, tick);
        }
        finally
        {
            UPDATING_HAND_MORPH.set(false);
        }
    }
}
