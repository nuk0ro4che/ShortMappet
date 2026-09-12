package mchorse.mappet;

import java.io.File;
import java.util.logging.Handler;
import mchorse.mappet.api.crafting.CraftingManager;
import mchorse.mappet.api.data.DataManager;
import mchorse.mappet.api.dialogues.DialogueManager;
import mchorse.mappet.api.events.EventManager;
import mchorse.mappet.api.expressions.ExpressionManager;
import mchorse.mappet.api.factions.FactionManager;
import mchorse.mappet.api.huds.HUDManager;
import mchorse.mappet.api.shaders.ShaderManager;
import mchorse.mappet.api.ui.UIManager;
import mchorse.mappet.api.misc.ClientSettings;
import mchorse.mappet.api.misc.ServerSettings;
import mchorse.mappet.api.npcs.NpcManager;
import mchorse.mappet.api.quests.QuestManager;
import mchorse.mappet.api.quests.chains.QuestChainManager;
import mchorse.mappet.api.schematics.SchematicManager;
import mchorse.mappet.api.scripts.Script;
import mchorse.mappet.api.scripts.ScriptManager;
import mchorse.mappet.api.scripts.lights.VanillaWorldLightManager;
import mchorse.mappet.api.states.States;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.logs.MappetLogger;
import mchorse.mappet.blocks.BlockConditionModel;
import mchorse.mappet.blocks.BlockEmitter;
import mchorse.mappet.blocks.BlockRegion;
import mchorse.mappet.blocks.BlockTrigger;
import mchorse.mappet.commands.CommandMappet;
import mchorse.mappet.compat.events.EventBus;
import mchorse.mappet.entities.EntityNpc;
import mchorse.mappet.tile.TileConditionModel;
import mchorse.mappet.tile.TileEmitter;
import mchorse.mappet.tile.TileRegion;
import mchorse.mappet.tile.TileTrigger;
import mchorse.mappet.utils.MappetNpcSelector;
import mchorse.mappet.utils.ScriptUtils;
import mchorse.mappet.utils.ValueSyntaxStyle;
import mchorse.mclib.McLib;
import mchorse.mclib.commands.utils.L10n;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.ConfigBuilder;
import mchorse.mclib.config.values.ValueBoolean;
import mchorse.mclib.config.values.ValueInt;
import mchorse.mclib.config.values.ValueString;
import mchorse.mclib.events.RegisterConfigEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1299;
import net.minecraft.class_1792;
import net.minecraft.class_2591;
import net.minecraft.class_5218;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Mappet implements ModInitializer {
   public static final String MOD_ID = "mappet";
   public static final String VERSION = "0.9.0-1.20.1";
   public static final Logger LOGGER = LoggerFactory.getLogger("mappet");
   public static Mappet instance;
   public static CommonProxy proxy;
   public static final L10n l10n = new L10n("mappet");
   public static final EventBus EVENT_BUS = new EventBus();
   public static MappetLogger logger;
   public static MinecraftServer server;
   public static class_1792 npcTool;
   public static BlockEmitter emitterBlock;
   public static BlockTrigger triggerBlock;
   public static BlockRegion regionBlock;
   public static BlockConditionModel conditionModelBlock;
   public static class_1299<EntityNpc> npcEntity;
   public static class_2591<TileEmitter> emitterTile;
   public static class_2591<TileTrigger> triggerTile;
   public static class_2591<TileRegion> regionTile;
   public static class_2591<TileConditionModel> conditionModelTile;
   public static ServerSettings settings;
    public static ClientSettings clientSettings;
   public static States states;
   public static QuestManager quests;
   public static SchematicManager schematics;
   public static CraftingManager crafting;
   public static EventManager events;
   public static DialogueManager dialogues;
   public static ExpressionManager expressions;
   public static NpcManager npcs;
   public static FactionManager factions;
   public static DataManager data;
   public static QuestChainManager chains;
   public static ScriptManager scripts;
   
   public static ScriptManager clientScripts;
   public static HUDManager huds;
   public static ShaderManager shaders;
   public static UIManager uis;
   public static ValueBoolean generalDataCaching;
   public static ValueBoolean loadCustomSoundsOnLogin;
   public static ValueBoolean npcsPeacefulDamage;
   public static ValueBoolean npcsToolOnlyOP;
   public static ValueBoolean npcsToolOnlyCreative;
   public static ValueBoolean dashboardOnlyCreative;
   public static ValueInt eventMaxExecutions;
   public static ValueBoolean eventUseServerForCommands;
   public static ValueInt nodePulseBackgroundColor;
   public static ValueBoolean nodePulseBackgroundMcLibPrimary;
   public static ValueInt globalTriggerCategoryColor;
   public static ValueInt nodeThickness;
   public static ValueBoolean questsPreviewRewards;
   public static ValueInt journalButtonX;
   public static ValueInt journalButtonY;
   public static ValueSyntaxStyle scriptEditorSyntaxStyle;
   public static ValueBoolean scriptEditorSounds;
   public static ValueBoolean scriptUIDebug;
   public static ValueBoolean scriptDocsNewStructure;
   public static ValueBoolean discordRpcEnabled;
   public static ValueString discordRpcClientId;
   public static ValueString discordRpcDetails;
   public static ValueString discordRpcState;
   public static ValueBoolean discordRpcTimestamp;
   public static ValueString discordRpcLargeImage;
   public static ValueString discordRpcLargeText;
   public static ValueString discordRpcSmallImage;
   public static ValueString discordRpcSmallText;
   public static ValueString discordRpcButton1Text;
   public static ValueString discordRpcButton1Url;
   public static ValueString discordRpcButton2Text;
   public static ValueString discordRpcButton2Url;

   public static void onConfigRegister(RegisterConfigEvent event) {
      ConfigBuilder builder = event.createBuilder("mappet");
      builder.category("general");
      generalDataCaching = builder.getBoolean("data_caching", true);
      loadCustomSoundsOnLogin = builder.getBoolean("load_custom_sounds_on_login", false);
      npcsPeacefulDamage = builder.category("npc").getBoolean("peaceful_damage", true);
      npcsToolOnlyOP = builder.getBoolean("tool_only_op", true);
      npcsToolOnlyCreative = builder.getBoolean("tool_only_creative", false);
      dashboardOnlyCreative = builder.getBoolean("dashboard_only_creative", false);
      eventMaxExecutions = builder.category("events").getInt("max_executions", 10000, 100, 1000000);
      eventUseServerForCommands = builder.getBoolean("use_server_for_commands", false);
      nodePulseBackgroundColor = builder.category("gui").getInt("pulse_background_color", 0).color();
      nodePulseBackgroundMcLibPrimary = builder.getBoolean("pulse_background_mclib", false);
      nodeThickness = builder.getInt("node_thickness", 3, 0, 20);
      questsPreviewRewards = builder.getBoolean("quest_preview_rewards", true);
      journalButtonX = builder.getInt("journal_button_x", 0, 0, 300);
      journalButtonY = builder.getInt("journal_button_y", 0, 0, 300);
      builder.getCategory().markClientSide();
      builder.category("script_editor").register(scriptEditorSyntaxStyle = new ValueSyntaxStyle("syntax_style"));
      globalTriggerCategoryColor = builder.getInt("global_trigger_category_color", 0x6E1408).color();
      scriptEditorSounds = builder.getBoolean("sounds", true);
      scriptUIDebug = builder.getBoolean("ui_debug", false);
      scriptDocsNewStructure = builder.getBoolean("docs_new_structure", true);
      builder.getCategory().markClientSide();

      ConfigBuilder discord = event.createBuilder("discordrpc");
      discord.category("general");
      discordRpcEnabled = discord.getBoolean("enabled", true);
      discordRpcClientId = discord.getString("client_id", "1543082148561420310");
      discordRpcDetails = discord.getString("details", "");
      discordRpcState = discord.getString("state", "");
      discordRpcTimestamp = discord.getBoolean("show_timestamp", true);
      discordRpcLargeImage = discord.getString("large_image", "https://i.imgur.com/sKWvPqh.jpeg");
      discordRpcLargeText = discord.getString("large_text", "ShortMappet");
      discordRpcSmallImage = discord.getString("small_image", "");
      discordRpcSmallText = discord.getString("small_text", "");
      discordRpcButton1Text = discord.getString("button1_text", "links on me");
      discordRpcButton1Url = discord.getString("button1_url", "https://guns.lol/pok0roche");
      discordRpcButton2Text = discord.getString("button2_text", "discord shortmappet");
      discordRpcButton2Url = discord.getString("button2_url", "https://discord.gg/VrSw4wQm2G");
      discord.getCategory().markClientSide();
   }

   private static void registerConfig() {
      RegisterConfigEvent event = new RegisterConfigEvent(FabricLoader.getInstance().getConfigDir().toFile());
      onConfigRegister(event);
      new mchorse.mappet.utils.autocomplete.Config().onConfigRegister(event);

      for(Config module : event.modules) {
         if ("mappet".equals(module.id) || "autocomplete".equals(module.id) || "discordrpc".equals(module.id)) {
            McLib.proxy.configs.modules.put(module.id, module);
         }
      }

      McLib.proxy.configs.reload();
   }

   public void onInitialize() {
      instance = this;
      proxy = new CommonProxy();
      registerConfig();
      RegisterHandler.registerContent();
      proxy.preInit();
      proxy.init();
      proxy.postInit();
      MappetNpcSelector.register();
      CommandRegistrationCallback.EVENT.register((CommandRegistrationCallback)(dispatcher, registryAccess, environment) -> (new CommandMappet()).register((com.mojang.brigadier.CommandDispatcher)dispatcher));
      ServerLifecycleEvents.SERVER_STARTED.register(Mappet::serverStarted);
      ServerTickEvents.END_SERVER_TICK.register((server) -> VanillaWorldLightManager.tick());
      ServerLifecycleEvents.SERVER_STOPPED.register(Mappet::serverStopped);
      LOGGER.info("Mappet {} initialized", "0.9.0-1.20.1");
   }

   private static void serverStarted(MinecraftServer server) {
      Mappet.server = server;
      File root = server.method_27050(class_5218.field_24188).resolve("mappet").toFile();
      root.mkdirs();
      closeLogger();
      logger = new MappetLogger("mappet", root);
      settings = new ServerSettings(new File(root, "settings.json"));
      settings.load();
      clientSettings = new ClientSettings(new File(root, "client_settings.json"));
      clientSettings.load();
      states = new States(new File(root, "states.json"));
      states.load();
      quests = new QuestManager(new File(root, "quests"));
      schematics = new SchematicManager(new File(root, "schematics"));
      crafting = new CraftingManager(new File(root, "crafting"));
      events = new EventManager(new File(root, "events"));
      dialogues = new DialogueManager(new File(root, "dialogues"));
      expressions = new ExpressionManager();
      npcs = new NpcManager(new File(root, "npcs"));
      factions = new FactionManager(new File(root, "factions"));
      data = new DataManager(new File(root, "data"));
      chains = new QuestChainManager(new File(root, "chains"));
      scripts = new ScriptManager(new File(root, "scripts"));
      clientScripts = scripts;
      huds = new HUDManager(new File(root, "huds"));
      shaders = new ShaderManager(new File(root, "shaders"));
      uis = new UIManager(new File(root, "uis"));
      if (!settings.serverLoad.isEmpty()) {
         settings.serverLoad.trigger(new DataContext(server));
      }

      ScriptUtils.initiateScriptEngines();
      scripts.initiateAllScripts();
      migrateLegacyClientScripts(new File(root, "client_scripts"));
      

      EventHandler.getRegisteredEvents();
   }

   private static void serverStopped(MinecraftServer server) {
      VanillaWorldLightManager.clear();
      if (settings != null) {
         settings.save();
         states.save();
      }

      if (clientSettings != null) {
         clientSettings.save();
      }

      settings = null;
      clientSettings = null;
      states = null;
      quests = null;
      schematics = null;
      crafting = null;
      events = null;
      dialogues = null;
      expressions = null;
      npcs = null;
      factions = null;
      data = null;
      chains = null;
      scripts = null;
      clientScripts = null;
      huds = null;
      shaders = null;
      uis = null;
      if (CommonProxy.eventHandler != null) {
         CommonProxy.eventHandler.reset();
      }

      closeLogger();
      Mappet.server = null;
   }

   private static void migrateLegacyClientScripts(File folder) {
      if (folder == null || !folder.isDirectory() || scripts == null) {
         return;
      }

      boolean imported = false;
      ScriptManager legacy = new ScriptManager(folder);

      for (String id : legacy.getKeys()) {
         if (id.endsWith("/") || !id.endsWith(".js")) {
            continue;
         }

         try {
            Script script = legacy.load(id);
            if (script == null || script.code == null || script.code.trim().isEmpty() || scripts.exists(id)) {
               continue;
            }
            script.client = true;
            scripts.save(id, script.serializeNBT());
            imported = true;
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      if (imported) {
         LOGGER.info("Imported legacy client scripts from 'client_scripts' into 'scripts' (marked as client scripts).");
      }
   }

   private static void closeLogger() {
      if (logger != null) {
         for(Handler handler : logger.getHandlers()) {
            handler.close();
            logger.removeHandler(handler);
         }

         logger = null;
      }
   }
}
