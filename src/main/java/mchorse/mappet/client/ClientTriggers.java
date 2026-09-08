package mchorse.mappet.client;

import mchorse.mappet.MappetClient;
import mchorse.mappet.api.utils.DataContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ClientTriggers {
    private ClientTriggers() {
    }

    public static void trigger(String key, DataContext context) {
        if (MappetClient.clientSettings != null) {
            MappetClient.clientSettings.trigger(key, context);
        }
    }
}