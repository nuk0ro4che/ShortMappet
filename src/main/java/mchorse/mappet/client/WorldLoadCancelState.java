package mchorse.mappet.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Shared state used to cancel loading a singleplayer world.
 *
 * <p>The integrated server loading is performed synchronously on the render
 * thread inside {@code Minecraft.method_29610}, so simply stopping the server
 * from the cancel button leaves the render thread stuck in its waiting loop
 * (the game freezes and the "Quit Game" button stops working). To cancel
 * safely we only flag the request here and let the render-thread mixins abort
 * the load at the right moment.</p>
 */
@Environment(EnvType.CLIENT)
public final class WorldLoadCancelState
{
    private static volatile boolean requested;

    private WorldLoadCancelState()
    {}

    public static void request()
    {
        requested = true;
    }

    public static boolean isRequested()
    {
        return requested;
    }

    public static void reset()
    {
        requested = false;
    }

    /**
     * Returns {@code true} once after a cancel was requested.
     */
    public static boolean consume()
    {
        if (!requested)
        {
            return false;
        }

        requested = false;

        return true;
    }
}
