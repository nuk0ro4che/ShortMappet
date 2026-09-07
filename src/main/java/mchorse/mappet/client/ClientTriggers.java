package mchorse.mappet.client;

import mchorse.mappet.MappetClient;
import mchorse.mappet.api.utils.DataContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ClientTriggers {
    private ClientTriggers() {
    }

    public static void trigger(String name, DataContext context) {
        if (name == null || name.isEmpty() || context == null) {
            return;
        }

        if (MappetClient.clientSettings != null) {
            MappetClient.clientSettings.trigger(name, context);
        }
    }
}
