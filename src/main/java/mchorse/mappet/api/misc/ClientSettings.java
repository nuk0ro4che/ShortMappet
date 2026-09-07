package mchorse.mappet.api.misc;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mappet.utils.NBTToJsonLike;
import net.minecraft.class_2487;

public class ClientSettings implements INBTSerializable<class_2487> {
    public static final String[] GLOBAL_TRIGGERS = new String[]{
        "client_tick", "block_interact", "block_click", "block_break", "block_place",
        "player_lmb", "player_rmb", "player_item_interact", "player_entity_interact",
        "player_keyboard", "mouse_input", "player_chat", "player_login", "player_logout",
        "player_respawn", "player_item_pickup", "player_item_toss",
        "player_open_container", "player_close_container", "player_journal",
        "entity_damaged", "entity_attacked", "entity_death", "entity_landed",
        "living_knockback", "projectile_impact", "living_equipment_change",
        "player_entity_leash", "state_changed", "sound_ended"
    };

    private File file;
    public final Map<String, Trigger> triggers = new LinkedHashMap();

    public final Trigger blockInteract;
    public final Trigger blockLeftClick;
    public final Trigger playerTick;

    public ClientSettings(File file) {
        this.file = file;

        for (String key : GLOBAL_TRIGGERS) {
            this.triggers.put(key, new Trigger());
        }

        this.blockInteract = this.triggers.get("block_interact");
        this.blockLeftClick = this.triggers.get("block_click");
        this.playerTick = this.triggers.get("client_tick");
    }

    public Trigger getTrigger(String key) {
        return this.triggers.get(key);
    }

    public void trigger(String key, DataContext context) {
        Trigger trigger = this.getTrigger(key);
        if (trigger != null && !trigger.isEmpty()) {
            trigger.trigger(context);
        }
    }

    public void load() {
        if (this.file != null && this.file.isFile()) {
            try {
                this.deserializeNBT(NBTToJsonLike.read(this.file));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void reload() {
        for (Trigger trigger : this.triggers.values()) {
            trigger.blocks.clear();
            trigger.recalculateEmpty();
        }

        this.load();
    }

    public void save() {
        try {
            NBTToJsonLike.write(this.file, this.serializeNBT());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public class_2487 serializeNBT() {
        class_2487 tag = new class_2487();
        class_2487 triggersTag = new class_2487();

        for (Map.Entry<String, Trigger> entry : this.triggers.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                triggersTag.method_10566(entry.getKey(), entry.getValue().serializeNBT());
            }
        }

        if (!triggersTag.method_33133()) {
            tag.method_10566("Triggers", triggersTag);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(class_2487 tag) {
        if (tag.method_10545("Triggers")) {
            class_2487 triggersTag = tag.method_10562("Triggers");

            for (Map.Entry<String, Trigger> entry : this.triggers.entrySet()) {
                String key = entry.getKey();
                if (triggersTag.method_10573(key, 10)) {
                    class_2487 triggerTag = triggersTag.method_10562(key);
                    if (!triggerTag.method_33133()) {
                        entry.getValue().deserializeNBT(triggerTag);
                    }
                }
            }
        }

        if (tag.method_10545("BlockInteract")) {
            this.blockInteract.deserializeNBT(tag.method_10562("BlockInteract"));
        }

        if (tag.method_10545("BlockLeftClick")) {
            this.blockLeftClick.deserializeNBT(tag.method_10562("BlockLeftClick"));
        }

        if (tag.method_10545("ClientTick")) {
            this.playerTick.deserializeNBT(tag.method_10562("ClientTick"));
        }
    }
}