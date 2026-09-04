package mchorse.mappet.api.scripts.code.client;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public final class ClientKeyBindingCache
{
    private static final ConcurrentHashMap<UUID, ConcurrentHashMap<String, String>> VALUES = new ConcurrentHashMap<>();

    private ClientKeyBindingCache()
    {}

    public static String get(UUID player, String id)
    {
        ConcurrentHashMap<String, String> bindings = VALUES.get(player);
        return bindings == null ? "" : bindings.getOrDefault(normalize(id), "");
    }

    public static void set(UUID player, String id, String key)
    {
        VALUES.computeIfAbsent(player, ignored -> new ConcurrentHashMap<>()).put(normalize(id), key == null ? "" : key);
    }

    public static void remove(UUID player)
    {
        VALUES.remove(player);
    }

    private static String normalize(String id)
    {
        if (id == null)
        {
            return "";
        }

        return id.startsWith("key_") ? id.substring(4) : id;
    }
}
