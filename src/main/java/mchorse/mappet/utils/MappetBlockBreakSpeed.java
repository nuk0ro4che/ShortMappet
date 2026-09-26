package mchorse.mappet.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Holds per-player block breaking speed multipliers used by {@link
 * mchorse.mappet.mixins.PlayerEntityBlockBreakSpeedMixin}. Runs on both server
 * (authoritative timing) and client (progress overlay via packets).
 */
public class MappetBlockBreakSpeed {
    private static final Map<UUID, Float> MULTIPLIERS = new HashMap<UUID, Float>();

    public static void set(UUID player, float multiplier) {
        MULTIPLIERS.put(player, Math.max(0.0F, multiplier));
    }

    public static Float get(UUID player) {
        return MULTIPLIERS.get(player);
    }

    public static void remove(UUID player) {
        MULTIPLIERS.remove(player);
    }
}