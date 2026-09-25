package mchorse.mappet.client;

import mchorse.mappet.MappetClient;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.api.utils.DataContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_746;
import net.minecraft.class_1268;
import net.minecraft.class_7923;

@Environment(EnvType.CLIENT)
public class ClientBlockInteractHandler {
    public static void onRightClickBlock(class_2338 pos, class_2680 state, class_1268 hand) {
        if (MappetClient.clientSettings == null) {
            return;
        }

        fireTrigger(MappetClient.clientSettings.blockInteract, pos, state, hand);
    }

    public static void onLeftClickBlock(class_2338 pos, class_2680 state) {
        if (MappetClient.clientSettings == null) {
            return;
        }

        fireTrigger(MappetClient.clientSettings.blockLeftClick, pos, state, class_1268.field_5808);
    }

    public static DataContext onBreakBlock(class_2338 pos, class_2680 state) {
        if (MappetClient.clientSettings == null) {
            return null;
        }

        Trigger trigger = MappetClient.clientSettings.getTrigger("block_break");
        return trigger != null && !trigger.isEmpty() ? fireTrigger(trigger, pos, state, class_1268.field_5808) : null;
    }

    public static DataContext onPlaceBlock(class_2338 pos, class_2680 state) {
        if (MappetClient.clientSettings == null) {
            return null;
        }

        Trigger trigger = MappetClient.clientSettings.getTrigger("block_place");
        DataContext context = trigger != null && !trigger.isEmpty() ? fireTrigger(trigger, pos, state, class_1268.field_5810) : null;

        if (context != null) {
            Object block = context.getValue("block");
            Object meta = context.getValue("meta");

            if (block instanceof String && meta instanceof Number) {
                String id = ((String) block).contains(":") ? (String) block : "minecraft:" + block;
                class_2680 override = mchorse.mappet.EventHandler.resolveBlockStateId(id, ((Number) meta).intValue());

                if (override != null && override != state) {
                    ClientVisualBlocks.override(pos, state.method_26204(), override);
                }
            }
        }

        return context;
    }

    private static DataContext fireTrigger(Trigger trigger, class_2338 pos, class_2680 state, class_1268 hand) {
        if (trigger == null || trigger.isEmpty()) {
            return null;
        }

        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;

        if (player == null || mc.field_1687 == null) {
            return null;
        }

        DataContext context = DataContext.client(player);
        context.set("block", class_7923.field_41175.method_10221(state.method_26204()).toString());
        context.set("meta", (double) state.method_26204().method_9595().method_11662().indexOf(state));
        context.set("x", (double) pos.method_10263());
        context.set("y", (double) pos.method_10264());
        context.set("z", (double) pos.method_10260());
        context.set("hand", hand == class_1268.field_5808 ? "main" : "off");

        trigger.trigger(context);

        return context;
    }
}
