package mchorse.mappet;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import mchorse.mappet.api.misc.ClientSettings;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.client.ClientEventHandler;
import mchorse.mappet.client.ClientTriggers;
import mchorse.mappet.client.KeyboardHandler;
import mchorse.mappet.client.scripts.ClientScriptManager;
import mchorse.mappet.api.scripts.lights.VanillaWorldLightManager;
import mchorse.mappet.client.RenderingHandler;
import mchorse.mappet.client.SoundPack;
import mchorse.mappet.client.gui.scripts.GuiScriptTemplateEditorOverlayPanel;
import mchorse.mappet.client.gui.scripts.highlights.Highlighters;
import mchorse.mappet.client.gui.scripts.themes.GuiThemeEditorOverlayPanel;
import mchorse.mappet.client.gui.scripts.themes.Themes;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlay;
import mchorse.mappet.client.gui.utils.text.GuiText;
import mchorse.mappet.client.renders.entity.RenderNpc;
import mchorse.mappet.client.renders.tile.TileConditionModelRenderer;
import mchorse.mappet.client.renders.tile.TileRegionRenderer;
import mchorse.mappet.client.renders.tile.TileTriggerRenderer;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.utils.MPIcons;
import mchorse.mappet.utils.ValueButtons;
import mchorse.mappet.utils.ValueSyntaxStyle;
import mchorse.mappet.utils.autocomplete.ValueScriptTemplate;
import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.utils.DummyEntity;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.gui.ConfigGuiProviders;
import mchorse.mclib.config.values.Value;
import mchorse.mclib.network.ClientDispatcherHooks;
import mchorse.mappet.client.DiscordRPC;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.class_1074;
import net.minecraft.class_1299;
import net.minecraft.class_2561;
import net.minecraft.class_3264;
import net.minecraft.class_3283;
import net.minecraft.class_3285;
import net.minecraft.class_3288;
import net.minecraft.class_5352;
import net.minecraft.class_7699;
import net.minecraft.class_3288.class_3289;

public class MappetClient implements ClientModInitializer {
   
   public static ClientScriptManager clientScriptRuntime;
   
   public static ClientSettings clientSettings;
   private static boolean soundsPackLoaded;
   private static boolean soundsPackWarningShown;
   private static Field soundPackProviders;
   private static boolean customSoundsChecked;
   private static boolean customSoundsPresent;

   private static boolean hasCustomSounds() {
      if (!customSoundsChecked) {
         customSoundsChecked = true;
         customSoundsPresent = !SoundPack.getCustomSoundEvents().isEmpty();
      }
      return customSoundsPresent;
   }

   private static boolean addSoundPackProvider(class_3283 packs) {
      try {
         if (soundPackProviders == null) {
            for(Field field : class_3283.class.getDeclaredFields()) {
               if (Set.class.isAssignableFrom(field.getType())) {
                  field.setAccessible(true);
                  Object value = field.get(packs);
                  if (value instanceof Set) {
                     soundPackProviders = field;
                     break;
                  }
               }
            }
         }

         if (soundPackProviders == null) {
            throw new IllegalStateException("Resource pack providers set was not found");
         }

         Set<class_3285> providers = (Set)soundPackProviders.get(packs);
         providers.add((class_3285)(consumer) -> consumer.accept(class_3288.method_14456("mappet_custom_sounds", class_2561.method_43470("Mappet custom sounds"), true, (name) -> new SoundPack(ClientProxy.sounds), new class_3288.class_7679(class_2561.method_43470("Mappet custom OGG files"), 15, class_7699.method_45397()), class_3264.field_14188, class_3289.field_14280, true, class_5352.field_25348)));
         return true;
      } catch (Exception exception) {
         if (!soundsPackWarningShown) {
            soundsPackWarningShown = true;
            Mappet.LOGGER.warn("Unable to register the custom sounds pack after the client finished loading", exception);
         }

         return false;
      }
   }

   public void onInitializeClient() {
      clientScriptRuntime = new ClientScriptManager(new File(CommonProxy.configFolder, "client_script_cache"));
clientSettings = new ClientSettings(new File(CommonProxy.configFolder, "client_settings.json"));
       clientSettings.load();
       ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
          if (client.field_1724 != null) {
             ClientTriggers.trigger("player_login", DataContext.client(client.field_1724));
          }
       });
       ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
          if (client.field_1724 != null) {
             ClientTriggers.trigger("player_logout", DataContext.client(client.field_1724));
          }
       });
      GuiModelRenderer.dummyEntityFactory = (client) -> {
         if (client == null || client.field_1687 == null) {
            return null;
         }

         return new DummyEntity(class_1299.field_6131, client.field_1687);
      };
      ClientDispatcherHooks.install();
      ClientDispatcherHooks.registerClientReceivers(Dispatcher.DISPATCHER);
      EntityRendererRegistry.register(Mappet.npcEntity, RenderNpc::new);
      BlockEntityRendererRegistry.register(Mappet.conditionModelTile, (context) -> new TileConditionModelRenderer());
      BlockEntityRendererRegistry.register(Mappet.regionTile, (context) -> new TileRegionRenderer());
      BlockEntityRendererRegistry.register(Mappet.triggerTile, (context) -> new TileTriggerRenderer());
      Config mappetConfig = (Config)McLib.proxy.configs.modules.get("mappet");
      if (mappetConfig != null) {
         Value general = (Value)mappetConfig.values.get("general");
         if (general != null && general.getSubValue("buttons") == null) {
            ValueButtons buttons = new ValueButtons("buttons");
            buttons.clientSide();
            buttons.setConfig(mappetConfig);
            general.addSubValue(buttons);
         }
      }

      ConfigGuiProviders.register(ValueButtons.class, (mc, config, value) -> Arrays.asList(Elements.column(mc, 5, new GuiElement[]{(new GuiText(mc)).text(IKey.lang("mappet.translation.credit")), Elements.row(mc, 5, new GuiElement[]{new GuiButtonElement(mc, IKey.lang("mappet.translation.wiki"), (b) -> GuiUtils.openWebLink(class_1074.method_4662("mappet.translation.wiki_url", new Object[0]))), new GuiButtonElement(mc, IKey.lang("mappet.translation.community"), (b) -> GuiUtils.openWebLink(class_1074.method_4662("mappet.translation.community_url", new Object[0])))}), new GuiButtonElement(mc, IKey.lang("mappet.translation.sounds"), (b) -> GuiUtils.openWebLink(ClientProxy.sounds.toURI()))}).marginTop(6)));
      ConfigGuiProviders.register(ValueSyntaxStyle.class, (mc, config, value) -> Arrays.asList((new GuiButtonElement(mc, IKey.lang("mappet.gui.syntax_theme.edit"), (b) -> GuiOverlay.addOverlay(GuiBase.getCurrent(), new GuiThemeEditorOverlayPanel(mc), 0.6F, 0.95F))).tooltip(IKey.lang(value.getCommentKey()))));
      ConfigGuiProviders.register(ValueScriptTemplate.class, (mc, config, value) -> Arrays.asList(new GuiButtonElement(mc, IKey.lang("autocomplete.config.script_template.edit"), (b) -> GuiOverlay.addOverlay(GuiBase.getCurrent(), new GuiScriptTemplateEditorOverlayPanel(mc, value), 0.8F, 0.85F))));
      RenderingHandler.register();
      KeyboardHandler.register();
      DiscordRPC.initialize();
      ClientTickEvents.END_CLIENT_TICK.register(DiscordRPC::tick);
      ClientTickEvents.END_CLIENT_TICK.register((client) -> VanillaWorldLightManager.tick());
      ClientProxy.sounds = new File(CommonProxy.configFolder, "sounds");
      ClientTickEvents.END_CLIENT_TICK.register((client) -> {
         if (!hasCustomSounds() || soundsPackLoaded) {
            return;
         }

         class_3283 packs = client.method_1520();
         if (!addSoundPackProvider(packs)) {
            return;
         }

         soundsPackLoaded = true;
         packs.method_14445();
         packs.method_49427("mappet_custom_sounds");
         client.method_1513();
      });
       Themes.initiate();
       Highlighters.initiate();
       MPIcons.register();
       ClientTickEvents.END_CLIENT_TICK.register((client) -> {
          ClientEventHandler.instance().tick();
          if (MappetClient.clientScriptRuntime != null) {
             MappetClient.clientScriptRuntime.checkAndInvalidateCache();
          }
          if (MappetClient.clientSettings != null && !MappetClient.clientSettings.playerTick.isEmpty() && client.field_1724 != null) {
             MappetClient.clientSettings.playerTick.trigger(DataContext.client(client.field_1724));
          }
       });
    }
}
