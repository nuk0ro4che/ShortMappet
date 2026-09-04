package mchorse.mappet.api.expressions;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.expressions.functions.State;
import mchorse.mappet.api.expressions.functions.dialogue.DialogueRead;
import mchorse.mappet.api.expressions.functions.entity.EntityFunction;
import mchorse.mappet.api.expressions.functions.entity.PlayerIsAlive;
import mchorse.mappet.api.expressions.functions.factions.FactionFriendly;
import mchorse.mappet.api.expressions.functions.factions.FactionHas;
import mchorse.mappet.api.expressions.functions.factions.FactionHostile;
import mchorse.mappet.api.expressions.functions.factions.FactionNeutral;
import mchorse.mappet.api.expressions.functions.factions.FactionScore;
import mchorse.mappet.api.expressions.functions.inventory.InventoryArmor;
import mchorse.mappet.api.expressions.functions.inventory.InventoryHas;
import mchorse.mappet.api.expressions.functions.inventory.InventoryHolds;
import mchorse.mappet.api.expressions.functions.quests.QuestCompleted;
import mchorse.mappet.api.expressions.functions.quests.QuestPresent;
import mchorse.mappet.api.expressions.functions.quests.QuestPresentCompleted;
import mchorse.mappet.api.expressions.functions.world.WorldIsDay;
import mchorse.mappet.api.expressions.functions.world.WorldIsNight;
import mchorse.mappet.api.expressions.functions.world.WorldTime;
import mchorse.mappet.api.expressions.functions.world.WorldTotalTime;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mclib.math.Constant;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.MathBuilder;
import mchorse.mclib.math.Variable;
import net.minecraft.class_1309;
import net.minecraft.class_1937;
import net.minecraft.server.MinecraftServer;

public class ExpressionManager {
   private final Set<String> reportedInvalidExpressions = ConcurrentHashMap.newKeySet();
   public static IValue ONE = new Constant((double)1.0F);
   public static IValue ZERO = new Constant((double)0.0F);
   public MathBuilder builder = (new MathBuilder()).lenient();
   public DataContext context;

   public ExpressionManager() {
      this.builder.functions.put("quest_present", QuestPresent.class);
      this.builder.functions.put("quest_completed", QuestCompleted.class);
      this.builder.functions.put("quest_present_or_completed", QuestPresentCompleted.class);
      this.builder.functions.put("faction_friendly", FactionFriendly.class);
      this.builder.functions.put("faction_neutral", FactionNeutral.class);
      this.builder.functions.put("faction_hostile", FactionHostile.class);
      this.builder.functions.put("faction_has", FactionHas.class);
      this.builder.functions.put("faction_score", FactionScore.class);
      this.builder.functions.put("state", State.class);
      this.builder.functions.put("inv_has", InventoryHas.class);
      this.builder.functions.put("inv_holds", InventoryHolds.class);
      this.builder.functions.put("inv_armor", InventoryArmor.class);
      this.builder.functions.put("entity", EntityFunction.class);
      this.builder.functions.put("player_is_alive", PlayerIsAlive.class);
      this.builder.functions.put("dialogue_read", DialogueRead.class);
      this.builder.functions.put("world_time", WorldTime.class);
      this.builder.functions.put("world_total_time", WorldTotalTime.class);
      this.builder.functions.put("world_is_day", WorldIsDay.class);
      this.builder.functions.put("world_is_night", WorldIsNight.class);
   }

   private void reset() {
      for(Map.Entry<String, Variable> entry : this.builder.variables.entrySet()) {
         String key = (String)entry.getKey();
         Variable variable = (Variable)entry.getValue();
         if (!key.equals("PI") && !key.equals("E")) {
            if (variable.isNumber()) {
               variable.set((double)0.0F);
            } else {
               variable.set("");
            }
         }
      }

      this.context = null;
   }

   public ExpressionManager set(class_1937 world) {
      return this.set(new DataContext(world));
   }

   public ExpressionManager set(class_1309 subject) {
      return this.set(new DataContext(subject));
   }

   public ExpressionManager set(DataContext context) {
      this.reset();
      this.context = context;

      for(Map.Entry<String, Object> entry : context.getValues().entrySet()) {
         String key = (String)entry.getKey();
         Variable variable = (Variable)this.builder.variables.get(key);
         if (variable == null) {
            variable = new Variable(key, (double)0.0F);
            this.builder.register(variable);
         }

         if (entry.getValue() instanceof Number) {
            variable.set(((Number)entry.getValue()).doubleValue());
         } else if (entry.getValue() instanceof String) {
            variable.set((String)entry.getValue());
         }
      }

      return this;
   }

   public IValue parse(String expression) {
      return this.parse(expression, ZERO);
   }

   public IValue parse(String expression, IValue defaultValue) {
      if (expression != null && !expression.trim().isEmpty()) {
         try {
            IValue value = this.builder.parse(expression);
            this.reportedInvalidExpressions.remove(expression);
            return value;
         } catch (Exception e) {
            if (this.reportedInvalidExpressions.add(expression)) {
               Mappet.LOGGER.warn("Invalid Mappet expression '{}': {}", expression, e.getMessage());
            }

            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   public class_1937 getWorld() {
      return this.context.world;
   }

   public MinecraftServer getServer() {
      return this.context.getSender().method_9211();
   }
}
