package mchorse.mappet.client.gui.panels;

public enum TriggerCategory {
    SERVER("mappet.gui.settings.categories.server"),
    PLAYER("mappet.gui.settings.categories.player"),
    LIVING("mappet.gui.settings.categories.living"),
    ENTITY("mappet.gui.settings.categories.entity"),
    OTHER("mappet.gui.settings.categories.other");

    public final String translationKey;

    private TriggerCategory(String translationKey) {
        this.translationKey = translationKey;
    }

    public boolean matches(String key) {
        if (key == null) {
            return false;
        }

        switch (this) {
            case SERVER:
                return key.startsWith("server_");
            case PLAYER:
                return key.startsWith("player_") || key.startsWith("block_") || key.equals("mouse_input") || key.equals("client_tick");
            case LIVING:
                return key.startsWith("living_");
            case ENTITY:
                return key.startsWith("entity_") || key.startsWith("projectile_");
            default:
                return key.equals("state_changed") || key.equals("sound_ended");
        }
    }

    public boolean matchesMod(String key) {
        switch (this) {
            case SERVER:
                return key.startsWith("server_");
            case PLAYER:
                return key.startsWith("player_") || key.startsWith("block_") || key.equals("mouse_input") || key.startsWith("voicechat_");
            case LIVING:
                return key.startsWith("living_");
            case ENTITY:
                return key.startsWith("entity_") || key.startsWith("projectile_");
            default:
                return !key.startsWith("server_") && !key.toLowerCase().contains("player") && !key.startsWith("block_") && !key.startsWith("living_") && !key.startsWith("entity_") && !key.startsWith("projectile_") && !key.startsWith("voicechat_") && !key.equals("mouse_input") && !key.equals("sound_ended");
        }
    }

    public static boolean isBuiltin(String key) {
        return key.equals("block_break") || key.equals("block_place") || key.equals("block_interact") || key.equals("block_click") || key.equals("entity_damaged") || key.equals("entity_attacked") || key.equals("entity_death") || key.equals("entity_landed") || key.equals("server_load") || key.equals("server_tick") || key.equals("player_tick") || key.equals("player_chat") || key.equals("player_login") || key.equals("player_logout") || key.equals("player_lmb") || key.equals("player_rmb") || key.equals("player_respawn") || key.equals("player_item_pickup") || key.equals("player_item_toss") || key.equals("player_item_interact") || key.equals("player_entity_interact") || key.equals("player_close_container") || key.equals("player_open_container") || key.equals("player_journal") || key.equals("living_knockback") || key.equals("projectile_impact") || key.equals("living_equipment_change") || key.equals("player_entity_leash") || key.equals("mouse_input") || key.equals("player_keyboard") || key.equals("state_changed") || key.equals("sound_ended") || key.equals("client_tick");
    }
}