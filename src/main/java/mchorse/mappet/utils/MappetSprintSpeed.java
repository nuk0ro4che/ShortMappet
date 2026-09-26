package mchorse.mappet.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1322;
import net.minecraft.class_1324;
import net.minecraft.class_5134;

/**
 * Holds per-player sprint speed overrides used by {@link
 * mchorse.mappet.mixins.LivingEntitySprintSpeedMixin}.
 */
public class MappetSprintSpeed {
    private static final Map<UUID, Float> OVERRIDES = new HashMap<>();

    /**
     * Override UUID for the custom sprint speed modifier. Free to use: it is
     * not one of the vanilla attribute modifier UUIDs.
     */
    private static final UUID OVERRIDE_ID = UUID.fromString("0f398c84-2a36-4c3f-9a71-8f6f1a4c5d01");

    /**
     * Vanilla sprint speed boost UUID ({@code Attributes.SPRINTING_SPEED_BOOST}).
     */
    private static final UUID SPRINTING_BOOST_ID = UUID.fromString("662a6b8d-da3e-4c1c-8813-96ea6097278d");

    public static void set(UUID player, float speed) {
        OVERRIDES.put(player, Math.max(0.0F, speed));
    }

    public static Float get(UUID player) {
        return OVERRIDES.get(player);
    }

    public static void remove(UUID player) {
        OVERRIDES.remove(player);
    }

    /**
     * Applies (or removes) the custom sprint speed boost depending on the
     * current override value. Safe to call from the script API to re-sync an
     * already-sprinting player.
     */
    public static void sync(class_1309 living) {
        Float speed = OVERRIDES.get(((class_1297) living).method_5667());
        class_1324 attribute = living.method_5996(class_5134.field_23719);

        if (attribute == null) {
            return;
        }

        attribute.method_6200(OVERRIDE_ID);

        if (speed == null) {
            return;
        }

        if (attribute.method_6199(SPRINTING_BOOST_ID) != null) {
            attribute.method_6200(SPRINTING_BOOST_ID);
        }

        if (((class_1297) living).method_5624() && speed > 0.0F) {
            double current = attribute.method_6194();

            if (current <= 0.0) {
                current = 0.1;
            }

            double amount = (double) speed / current - 1.0;
            attribute.method_26835(new class_1322(OVERRIDE_ID, "Mappet sprint speed override", amount, class_1322.class_1323.field_6331));
        }
    }
}