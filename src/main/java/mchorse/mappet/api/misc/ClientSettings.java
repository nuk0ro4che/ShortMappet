package mchorse.mappet.api.misc;

import java.io.File;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mappet.utils.NBTToJsonLike;
import net.minecraft.class_2487;

public class ClientSettings implements INBTSerializable<class_2487> {
    private File file;

    public final Trigger blockInteract = new Trigger();
    public final Trigger blockLeftClick = new Trigger();
    public final Trigger playerTick = new Trigger();

    public ClientSettings(File file) {
        this.file = file;
    }

    public void load() {
        if (this.file != null && this.file.isFile()) {
            try {
                class_2487 tag = NBTToJsonLike.read(this.file);
                this.deserializeNBT(tag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    




    public void reload() {
        this.blockInteract.blocks.clear();
        this.blockInteract.recalculateEmpty();
        this.blockLeftClick.blocks.clear();
        this.blockLeftClick.recalculateEmpty();
        this.playerTick.blocks.clear();
        this.playerTick.recalculateEmpty();

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

        if (!this.blockInteract.isEmpty()) {
            tag.method_10566("BlockInteract", this.blockInteract.serializeNBT());
        }

        if (!this.blockLeftClick.isEmpty()) {
            tag.method_10566("BlockLeftClick", this.blockLeftClick.serializeNBT());
        }

        if (!this.playerTick.isEmpty()) {
            tag.method_10566("ClientTick", this.playerTick.serializeNBT());
        }

        return tag;
    }

    @Override
    public void deserializeNBT(class_2487 tag) {
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
