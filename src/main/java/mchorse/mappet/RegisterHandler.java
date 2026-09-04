package mchorse.mappet;

import java.util.Calendar;
import mchorse.mappet.blocks.BlockConditionModel;
import mchorse.mappet.blocks.BlockEmitter;
import mchorse.mappet.blocks.BlockRegion;
import mchorse.mappet.blocks.BlockTrigger;
import mchorse.mappet.entities.EntityNpc;
import mchorse.mappet.items.ItemNpcTool;
import mchorse.mappet.tile.TileConditionModel;
import mchorse.mappet.tile.TileEmitter;
import mchorse.mappet.tile.TileRegion;
import mchorse.mappet.tile.TileTrigger;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.class_1299;
import net.minecraft.class_1311;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2248;
import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_2586;
import net.minecraft.class_2591;
import net.minecraft.class_2960;
import net.minecraft.class_7923;
import net.minecraft.class_1299.class_1300;

public final class RegisterHandler {
   private RegisterHandler() {
   }

   public static void registerContent() {
      Mappet.emitterBlock = (BlockEmitter)block("emitter", new BlockEmitter());
      Mappet.triggerBlock = (BlockTrigger)block("trigger", new BlockTrigger());
      Mappet.regionBlock = (BlockRegion)block("region", new BlockRegion());
      Mappet.conditionModelBlock = (BlockConditionModel)block("condition_model", new BlockConditionModel());
      Mappet.npcTool = (class_1792)class_2378.method_10230(class_7923.field_41178, id("npc_tool"), new ItemNpcTool());
      class_2378.method_10230(class_7923.field_41178, id("emitter"), new class_1747(Mappet.emitterBlock, new class_1792.class_1793()));
      class_2378.method_10230(class_7923.field_41178, id("trigger"), new class_1747(Mappet.triggerBlock, new class_1792.class_1793()));
      class_2378.method_10230(class_7923.field_41178, id("region"), new class_1747(Mappet.regionBlock, new class_1792.class_1793()));
      class_2378.method_10230(class_7923.field_41178, id("condition_model"), new class_1747(Mappet.conditionModelBlock, new class_1792.class_1793()));
      Mappet.npcEntity = (class_1299)class_2378.method_10230(class_7923.field_41177, id("npc"), class_1300.method_5903(EntityNpc::new, class_1311.field_6294).method_17687(0.6F, 1.8F).method_27299(160).method_27300(3).method_5905(id("npc").toString()));
      FabricDefaultAttributeRegistry.register(Mappet.npcEntity, EntityNpc.createNpcAttributes());
      Mappet.emitterTile = tile("emitter", FabricBlockEntityTypeBuilder.create(TileEmitter::new, new class_2248[]{Mappet.emitterBlock}));
      Mappet.triggerTile = tile("trigger", FabricBlockEntityTypeBuilder.create(TileTrigger::new, new class_2248[]{Mappet.triggerBlock}));
      Mappet.regionTile = tile("region", FabricBlockEntityTypeBuilder.create(TileRegion::new, new class_2248[]{Mappet.regionBlock}));
      Mappet.conditionModelTile = tile("condition_model", FabricBlockEntityTypeBuilder.create(TileConditionModel::new, new class_2248[]{Mappet.conditionModelBlock}));
      class_2378.method_10230(class_7923.field_44687, id("main"), FabricItemGroup.builder().method_47321(class_2561.method_43471("itemGroup.mappet")).method_47320(() -> new class_1799(Mappet.emitterBlock)).method_47317((context, entries) -> {
         entries.method_45421(Mappet.npcTool);
         entries.method_45421(Mappet.emitterBlock);
         entries.method_45421(Mappet.triggerBlock);
         entries.method_45421(Mappet.regionBlock);
         entries.method_45421(Mappet.conditionModelBlock);
      }).method_47324());
   }

   private static <T extends class_2248> T block(String name, T block) {
      return (T)(class_2378.method_10230(class_7923.field_41175, id(name), block));
   }

   private static <T extends class_2586> class_2591<T> tile(String name, FabricBlockEntityTypeBuilder<T> builder) {
      return (class_2591)class_2378.method_10230(class_7923.field_41181, id(name), builder.build());
   }

   private static class_2960 id(String path) {
      return new class_2960("mappet", path);
   }

   public static String getNpcToolModelPath(Calendar calendar) {
      String postfix = isWinter(calendar) ? "_winter" : "";
      if (isChristmas(calendar)) {
         postfix = "_christmas";
      } else if (isEaster(calendar)) {
         postfix = "_easter";
      } else if (isAprilFoolsDay(calendar)) {
         postfix = "_april";
      } else if (isHalloween(calendar)) {
         postfix = "_halloween";
      }

      return "npc_tool" + postfix;
   }

   public static boolean isChristmas(Calendar c) {
      return c.get(2) == 11 && c.get(5) >= 24 && c.get(5) <= 26;
   }

   public static boolean isAprilFoolsDay(Calendar c) {
      return c.get(2) == 3 && c.get(5) <= 2;
   }

   public static boolean isWinter(Calendar c) {
      int m = c.get(2);
      return m == 11 || m == 0 || m == 1;
   }

   public static boolean isHalloween(Calendar c) {
      return c.get(2) == 9 && c.get(5) >= 24;
   }

   public static boolean isEaster(Calendar c) {
      Calendar e = getEasterDate(c.get(1));
      return c.get(2) == e.get(2) && c.get(5) == e.get(5);
   }

   public static Calendar getEasterDate(int year) {
      int a = year % 19;
      int b = year / 100;
      int c = year % 100;
      int d = b / 4;
      int e = b % 4;
      int f = (b + 8) / 25;
      int g = (b - f + 1) / 3;
      int h = (19 * a + b - d - g + 15) % 30;
      int i = c / 4;
      int k = c % 4;
      int l = (32 + 2 * e + 2 * i - h - k) % 7;
      Calendar result = Calendar.getInstance();
      result.set(year, (h + l + 114) / 31 - 1, (h + l + 114) % 31 + 1);
      return result;
   }
}
