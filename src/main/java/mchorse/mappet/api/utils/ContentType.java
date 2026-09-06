package mchorse.mappet.api.utils;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.utils.manager.IManager;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.panels.GuiMappetDashboardPanel;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum ContentType implements IContentType {
   QUEST {
      public IManager<? extends AbstractData> getManager() {
         return Mappet.quests;
      }

      @Environment(EnvType.CLIENT)
      public GuiMappetDashboardPanel get(GuiMappetDashboard dashboard) {
         return dashboard.quest;
      }

      @Environment(EnvType.CLIENT)
      public IKey getPickLabel() {
         return IKey.lang("mappet.gui.overlays.quest");
      }

      public String getName() {
         return "QUEST";
      }
   },
   CRAFTING_TABLE {
      public IManager<? extends AbstractData> getManager() {
         return Mappet.crafting;
      }

      @Environment(EnvType.CLIENT)
      public GuiMappetDashboardPanel get(GuiMappetDashboard dashboard) {
         return dashboard.crafting;
      }

      @Environment(EnvType.CLIENT)
      public IKey getPickLabel() {
         return IKey.lang("mappet.gui.overlays.crafting");
      }

      public String getName() {
         return "CRAFTING_TABLE";
      }
   },
   EVENT {
      public IManager<? extends AbstractData> getManager() {
         return Mappet.events;
      }

      @Environment(EnvType.CLIENT)
      public GuiMappetDashboardPanel get(GuiMappetDashboard dashboard) {
         return dashboard.event;
      }

      @Environment(EnvType.CLIENT)
      public IKey getPickLabel() {
         return IKey.lang("mappet.gui.overlays.event");
      }

      public String getName() {
         return "EVENT";
      }
   },
   DIALOGUE {
      public IManager<? extends AbstractData> getManager() {
         return Mappet.dialogues;
      }

      @Environment(EnvType.CLIENT)
      public GuiMappetDashboardPanel get(GuiMappetDashboard dashboard) {
         return dashboard.dialogue;
      }

      @Environment(EnvType.CLIENT)
      public IKey getPickLabel() {
         return IKey.lang("mappet.gui.overlays.dialogue");
      }

      public String getName() {
         return "DIALOGUE";
      }
   },
   NPC {
      public IManager<? extends AbstractData> getManager() {
         return Mappet.npcs;
      }

      @Environment(EnvType.CLIENT)
      public GuiMappetDashboardPanel get(GuiMappetDashboard dashboard) {
         return dashboard.npc;
      }

      @Environment(EnvType.CLIENT)
      public IKey getPickLabel() {
         return IKey.lang("mappet.gui.overlays.npc");
      }

      public String getName() {
         return "NPC";
      }
   },
   FACTION {
      public IManager<? extends AbstractData> getManager() {
         return Mappet.factions;
      }

      @Environment(EnvType.CLIENT)
      public GuiMappetDashboardPanel get(GuiMappetDashboard dashboard) {
         return dashboard.faction;
      }

      @Environment(EnvType.CLIENT)
      public IKey getPickLabel() {
         return IKey.lang("mappet.gui.overlays.faction");
      }

      public String getName() {
         return "FACTION";
      }
   },
   CHAINS {
      public IManager<? extends AbstractData> getManager() {
         return Mappet.chains;
      }

      @Environment(EnvType.CLIENT)
      public GuiMappetDashboardPanel get(GuiMappetDashboard dashboard) {
         return dashboard.chain;
      }

      @Environment(EnvType.CLIENT)
      public IKey getPickLabel() {
         return IKey.lang("mappet.gui.overlays.chain");
      }

      public String getName() {
         return "CHAINS";
      }
   },
   SCRIPTS {
      public IManager<? extends AbstractData> getManager() {
         return Mappet.scripts;
      }

      @Environment(EnvType.CLIENT)
      public GuiMappetDashboardPanel get(GuiMappetDashboard dashboard) {
         return dashboard.script;
      }

      @Environment(EnvType.CLIENT)
      public IKey getPickLabel() {
         return IKey.lang("mappet.gui.overlays.script");
      }

      public String getName() {
         return "SCRIPTS";
      }
   },
   HUDS {
      public IManager<? extends AbstractData> getManager() {
         return Mappet.huds;
      }

      @Environment(EnvType.CLIENT)
      public GuiMappetDashboardPanel get(GuiMappetDashboard dashboard) {
         return dashboard.hud;
      }

      public IKey getPickLabel() {
         return IKey.lang("mappet.gui.overlays.hud");
      }

      public String getName() {
         return "HUDS";
      }
   },
   UIS {
      public IManager<? extends AbstractData> getManager() {
         return Mappet.uis;
      }

      @Environment(EnvType.CLIENT)
      public GuiMappetDashboardPanel get(GuiMappetDashboard dashboard) {
         return dashboard.ui;
      }

      public IKey getPickLabel() {
         return IKey.lang("mappet.gui.overlays.ui");
      }

      public String getName() {
         return "UIS";
      }
   },
   CLIENT_SCRIPTS {
      public IManager<? extends AbstractData> getManager() {
         return Mappet.clientScripts;
      }

      @Environment(EnvType.CLIENT)
      public GuiMappetDashboardPanel get(GuiMappetDashboard dashboard) {
         return dashboard.script;
      }

      @Environment(EnvType.CLIENT)
      public IKey getPickLabel() {
         return IKey.str("Клиентские скрипты");
      }

      public String getName() {
         return "CLIENT_SCRIPTS";
      }
   },
   SHADERS {
      public IManager<? extends AbstractData> getManager() {
         return Mappet.shaders;
      }

      @Environment(EnvType.CLIENT)
      public GuiMappetDashboardPanel get(GuiMappetDashboard dashboard) {
         return dashboard.shader;
      }

      public IKey getPickLabel() {
         return IKey.str("Шейдер");
      }

      public String getName() {
         return "SHADERS";
      }
   };
   private static ContentType[] $values() {
      return new ContentType[]{QUEST, CRAFTING_TABLE, EVENT, DIALOGUE, NPC, FACTION, CHAINS, SCRIPTS, HUDS, UIS, CLIENT_SCRIPTS, SHADERS};
   }
}
