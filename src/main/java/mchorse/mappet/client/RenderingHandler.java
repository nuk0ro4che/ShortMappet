package mchorse.mappet.client;

import java.util.ArrayList;
import java.util.List;
import com.mojang.blaze3d.systems.RenderSystem;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.huds.HUDStage;
import mchorse.mappet.client.gui.GuiQuestTracker;
import mchorse.mappet.client.sounds.ClientManagedSoundManager;
import mchorse.mappet.client.morphs.WorldMorph;
import mchorse.mappet.client.shaders.ClientShaderRuntime;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.class_287;
import net.minecraft.class_4597;
import net.minecraft.class_310;

@Environment(EnvType.CLIENT)
public final class RenderingHandler {
   public static HUDStage stage = new HUDStage(false);
   public static HUDStage currentStage;
   public static final List<WorldMorph> worldMorphs = new ArrayList();
   public static boolean renderingSeeThrough;
   private static final class_4597.class_4598 localMorphBuffers = class_4597.method_22991(new class_287(1024));

   private RenderingHandler() {
   }

   public static void register() {
      ClientTickEvents.END_CLIENT_TICK.register((ClientTickEvents.EndTick)(client) -> update());
      WorldRenderEvents.BEFORE_ENTITIES.register((WorldRenderEvents.BeforeEntities)(context) -> ClientEntityTransitions.render(context.tickDelta()));
      WorldRenderEvents.AFTER_ENTITIES.register((WorldRenderEvents.AfterEntities)(context) -> {
         if (context.consumers() != null) {
            
            class_4597.class_4598 buffers = localMorphBuffers;
            for (WorldMorph morph : worldMorphs) {
               boolean through = morph.seeThrough;
               renderingSeeThrough = through;
               if (through) {
                  RenderSystem.disableDepthTest();
                  RenderSystem.depthMask(false);
               }
               try {
                  morph.render(context.matrixStack(), buffers, context.tickDelta());
                  buffers.method_22993();
               } finally {
                  if (through) {
                     renderingSeeThrough = false;
                     RenderSystem.depthMask(true);
                     RenderSystem.enableDepthTest();
                  }
               }
            }

         }
      });
   }

   public static void renderHud(float tickDelta) {
      class_310 client = class_310.method_1551();
      GuiQuestTracker.renderQuests(client.method_22683(), tickDelta);
      HUDStage active = currentStage == null ? stage : currentStage;
      active.render(client.method_22683(), tickDelta);
   }

   public static void addOrReplaceWorldMorph(WorldMorph incoming) {
      if (incoming.id == null || incoming.id.isEmpty()) {
         worldMorphs.add(incoming);
         return;
      }

      for (int i = 0; i < worldMorphs.size(); ++i) {
         if (incoming.id.equals(worldMorphs.get(i).id)) {
            worldMorphs.set(i, incoming);
            return;
         }
      }

      worldMorphs.add(incoming);
   }

   public static void removeWorldMorph(String id) {
      if (id != null && !id.isEmpty()) {
         worldMorphs.removeIf((morph) -> id.equals(morph.id));
      }
   }

   public static void update() {
      HUDStage active = currentStage == null ? stage : currentStage;
      active.update(active == stage);
      worldMorphs.removeIf(WorldMorph::update);
      ClientManagedSoundManager.tick();
      ClientMousePositionController.tick();
   }

   public static void reset() {
      stage.reset();
      currentStage = null;
      worldMorphs.clear();
      ClientEntityTransitions.clear();
      ClientManagedSoundManager.clear();
      ClientShaderRuntime.reset();
   }
}
