package mchorse.mappet.client.gui.npc;

import mchorse.mappet.api.npcs.NpcState;
import mchorse.mappet.client.gui.triggers.GuiTriggerElement;
import mchorse.mappet.client.gui.utils.GuiVecPosElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_243;
import net.minecraft.class_310;

public class GuiNpcRespawnPanel extends GuiNpcPanel {
   public GuiToggleElement respawn;
   public GuiTrackpadElement respawnDelay;
   public GuiToggleElement respawnOnCoordinates;
   public GuiVecPosElement respawnCoordinates;
   public GuiToggleElement respawnSaveUUID;
   public GuiTriggerElement triggerRespawn;

   public GuiNpcRespawnPanel(class_310 mc) {
      super(mc);
      this.respawn = new GuiToggleElement(mc, IKey.lang("mappet.gui.npcs.respawn.respawn"), (b) -> this.state.respawn.set(b.isToggled()));
      this.respawnDelay = new GuiTrackpadElement(mc, (v) -> this.state.respawnDelay.set(v.intValue()));
      this.respawnDelay.integer().limit((double)0.0F);
      this.respawnOnCoordinates = new GuiToggleElement(mc, IKey.lang("mappet.gui.npcs.respawn.respawn_on_coordinates"), (b) -> this.state.respawnOnCoordinates.set(b.isToggled()));
      this.respawnCoordinates = new GuiVecPosElement(mc, (pos) -> {
         this.state.respawnPosX.set(pos.field_1352);
         this.state.respawnPosY.set(pos.field_1351);
         this.state.respawnPosZ.set(pos.field_1350);
      });
      this.respawnSaveUUID = new GuiToggleElement(mc, IKey.lang("mappet.gui.npcs.respawn.respawn_save_uuid"), (b) -> {
         this.state.respawnSaveUUID.set(b.isToggled());
         if ((Boolean)this.state.respawnSaveUUID.get() && (Integer)this.state.respawnDelay.get() < 20) {
            this.respawnDelay.setValue((double)20.0F);
            this.state.respawnDelay.set(20);
         }

      });
      this.triggerRespawn = new GuiTriggerElement(mc);
      this.add(this.respawn);
      this.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.npcs.respawn.respawn_delay")), this.respawnDelay});
      this.add(new IGuiElement[]{this.respawnOnCoordinates, this.respawnCoordinates});
      this.add(this.respawnSaveUUID);
      this.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.npcs.respawn.respawn_trigger")).background().marginTop(12).marginBottom(5), this.triggerRespawn});
   }

   public void set(NpcState state) {
      super.set(state);
      this.respawn.toggled((Boolean)state.respawn.get());
      this.respawnDelay.setValue((double)(Integer)state.respawnDelay.get());
      this.respawnOnCoordinates.toggled((Boolean)state.respawnOnCoordinates.get());
      this.respawnCoordinates.set(new class_243((Double)state.respawnPosX.get(), (Double)state.respawnPosY.get(), (Double)state.respawnPosZ.get()));
      this.respawnSaveUUID.toggled((Boolean)state.respawnSaveUUID.get());
      this.triggerRespawn.set(state.triggerRespawn);
   }
}
