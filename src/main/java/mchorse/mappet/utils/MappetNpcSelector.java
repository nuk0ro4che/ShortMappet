package mchorse.mappet.utils;

import com.mojang.brigadier.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import mchorse.mappet.api.states.States;
import mchorse.mappet.entities.EntityNpc;
import mchorse.mappet.mixins.EntitySelectorOptionsAccessor;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.MathBuilder;
import mchorse.mclib.math.Variable;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.class_243;
import net.minecraft.class_2561;

public class MappetNpcSelector {
   public static final String ARGUMENT_MAPPET_NPC_ID = "mpid";
   public static final String ARGUMENT_MAPPET_STATES = "mpe";
   public static final SelectorMathBuilder BUILDER = new SelectorMathBuilder();

   public static void register() {
      EntitySelectorOptionsAccessor.mappet$putOption("mpid", (reader) -> {
         List<Predicate<class_1297>> predicates = new ArrayList();
         (new MappetNpcSelector()).addNpcIdPredicate(predicates, reader.method_9835().readString());
         Objects.requireNonNull(reader);
         predicates.forEach(reader::method_9916);
      }, (reader) -> true, class_2561.method_43470("Mappet NPC id"));
      EntitySelectorOptionsAccessor.mappet$putOption("mpe", (reader) -> {
         List<Predicate<class_1297>> predicates = new ArrayList();
         (new MappetNpcSelector()).addStatesPredicate(predicates, readStatesExpression(reader.method_9835()));
         Objects.requireNonNull(reader);
         predicates.forEach(reader::method_9916);
      }, (reader) -> true, class_2561.method_43470("Mappet entity states expression"));
   }

   




   private static String readStatesExpression(StringReader reader) {
      int start = reader.getCursor();
      int parentheses = 0;
      boolean quoted = false;
      boolean escaped = false;
      char quote = '\u0000';

      while(reader.canRead()) {
         char character = reader.peek();
         if (escaped) {
            escaped = false;
         } else if (character == '\\') {
            escaped = true;
         } else if (quoted) {
            if (character == quote) {
               quoted = false;
            }
         } else if (character == '\"' || character == '\'') {
            quoted = true;
            quote = character;
         } else if (character == '(') {
            ++parentheses;
         } else if (character == ')' && parentheses > 0) {
            --parentheses;
         } else if (parentheses == 0 && (character == ',' || character == ']')) {
            break;
         }

         reader.skip();
      }

      return reader.getString().substring(start, reader.getCursor()).trim().replace("\\\"", "\"").replace("\\'", "'");
   }

   @Nonnull
   public List<Predicate<class_1297>> createPredicates(Map<String, String> arguments, String mainSelector, class_2168 sender, class_243 position) {
      List<Predicate<class_1297>> list = new ArrayList();
      if (arguments.containsKey("mpid")) {
         this.addNpcIdPredicate(list, (String)arguments.get("mpid"));
      }

      if (arguments.containsKey("mpe")) {
         this.addStatesPredicate(list, (String)arguments.get("mpe"));
      }

      return list;
   }

   private void addNpcIdPredicate(List<Predicate<class_1297>> list, String id) {
      boolean negative = id.startsWith("!");
      if (negative) {
         id = id.substring(1);
      }

      final String finalId = id;
      final boolean finalNegative = negative;

      list.add((Predicate<class_1297>)(e) -> {
         if (e instanceof EntityNpc) {
            String npcId = ((EntityNpc)e).getNpcId();
            return finalNegative != npcId.equals(finalId);
         } else {
            return false;
         }
      });
   }

   private void addStatesPredicate(List<Predicate<class_1297>> list, String expression) {
      BUILDER.reset();

      try {
         IValue value = BUILDER.parse(expression);
         list.add((Predicate<class_1297>)(e) -> {
            States states = EntityUtils.getStates(e);
            if (states == null) {
               return false;
            } else {
               for(Variable variable : BUILDER.variables.values()) {
                  Object v = states.values.get(variable.getName());
                  if (v != null) {
                     if (v instanceof String) {
                        variable.set((String)v);
                     } else if (v instanceof Number) {
                        variable.set(((Number)v).doubleValue());
                     }
                  } else {
                     variable.set((double)0.0F);
                  }
               }

               return value.booleanValue();
            }
         });
      } catch (Exception var4) {
      }

   }

   public static class SelectorMathBuilder extends MathBuilder {
      public void reset() {
         this.variables.clear();
      }

      protected Variable getVariable(String name) {
         Variable variable = super.getVariable(name);
         if (variable == null) {
            variable = new Variable(name, (double)0.0F);
            this.variables.put(name, variable);
         }

         return variable;
      }
   }
}