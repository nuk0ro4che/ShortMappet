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

        triggerBlockInteraction(MappetClient.clientSettings.blockInteract, pos, state, hand);
    }

    public static void onLeftClickBlock(class_2338 pos, class_2680 state) {
        if (MappetClient.clientSettings == null) {
            return;
        }

        triggerBlockInteraction(MappetClient.clientSettings.blockLeftClick, pos, state, class_1268.field_5808);
    }

    private static void triggerBlockInteraction(Trigger trigger, class_2338 pos, class_2680 state, class_1268 hand) {
        if (trigger == null || trigger.isEmpty()) {
            return;
        }

        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;

        if (player == null || mc.field_1687 == null) {
            return;
        }

        DataContext context = DataContext.client(player);
        context.set("block", class_7923.field_41175.method_10221(state.method_26204()).toString());
        context.set("meta", (double) state.method_26204().method_9595().method_11662().indexOf(state));
        context.set("x", (double) pos.method_10263());
        context.set("y", (double) pos.method_10264());
        context.set("z", (double) pos.method_10260());
        context.set("hand", hand == class_1268.field_5808 ? "main" : "off");

        trigger.trigger(context);
    }
}
