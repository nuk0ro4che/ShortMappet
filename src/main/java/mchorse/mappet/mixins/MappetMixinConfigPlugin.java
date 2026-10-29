package mchorse.mappet.mixins;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Mappet mixin config plugin.
 *
 * <p>Its only job is to opt out of Pehkui's per-entity synchronized scale map
 * before Pehkui reads its own configuration.</p>
 *
 * <p>Pehkui stores every {@code ScaleData} in a {@code synchronized} map and its
 * scale getters perform a guarded lookup on <em>every</em> access (step height,
 * eye height, hitbox, motion, ... several times per entity tick). On a
 * single-threaded server that monitor is pure overhead &mdash; a spark profile
 * of this pack attributes ~2% of the whole server thread to
 * {@code Object2ObjectFunctions$SynchronizedFunction.containsKey/get} coming
 * from {@code Entity.pehkui_getScaleData}.</p>
 *
 * <p>Pehkui itself ships a switch for this: reading
 * {@code -Dpehkui.disableThreadSafety=true} makes its mixin plugin pick the
 * unsynchronized {@code ThreadUnsafeScaledEntityMixin} instead of
 * {@code ThreadSafeScaledEntityMixin}. That flag is evaluated while the mixin
 * configs are prepared, i.e. before any mod is constructed, which is why it is
 * set from this plugin's static initializer rather than from an entrypoint. An
 * explicit JVM argument always takes precedence.</p>
 */
public class MappetMixinConfigPlugin implements IMixinConfigPlugin
{
    private static final String PEHKUI_THREAD_SAFETY = "pehkui.disableThreadSafety";

    static
    {
        if (System.getProperty(PEHKUI_THREAD_SAFETY) == null)
        {
            System.setProperty(PEHKUI_THREAD_SAFETY, "true");
        }
    }

    @Override
    public void onLoad(String mixinPackage)
    {
    }

    @Override
    public String getRefMapperConfig()
    {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
    {
    }

    @Override
    public List<String> getMixins()
    {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {
    }
}
