package mchorse.mappet.utils.autocomplete;

import java.util.Arrays;
import java.util.List;

public class AutoCompleteConfig {
   public static int MAX_SUGGESTIONS = 5000;
   public static final List<String> JS_KEYWORDS = Arrays.asList("var", "let", "const", "function", "return", "if", "else", "for", "while", "do", "break", "continue", "switch", "default", "new", "delete", "typeof", "instanceof", "in", "of", "indexof", "try", "catch", "finally", "throw", "true", "false", "null", "undefined", "this", "import", "Java");
   public static final List<String> JAVA_NASHORN_MEMBERS = Arrays.asList("type", "from");
   public static final List<String[]> PROPERTY_ALIASES = Arrays.asList(new String[]{"subject", "getSubject", "IScriptPlayer,IScriptEntity"}, new String[]{"object", "getObject", "IScriptEntity"}, new String[]{"entity", "getEntity", "IScriptEntity"}, new String[]{"player", "getPlayer", "IScriptPlayer", "IScriptEntity"}, new String[]{"npc", "getNPC", "IScriptNpc"}, new String[]{"world", "getWorld", "IScriptWorld"}, new String[]{"server", "getServer", "IScriptServer"}, new String[]{"values", "getValues", "Map"}, new String[]{"states", "getStates", "IMappetStates"}, new String[]{"inventory", "getInventory", "IScriptInventory"}, new String[]{"quests", "getQuests", "IMappetQuests"}, new String[]{"position", "getPosition", "ScriptVector"}, new String[]{"motion", "getMotion", "ScriptVector"}, new String[]{"rotations", "getRotations", "ScriptVector"}, new String[]{"look", "getLook", "ScriptVector"}, new String[]{"morph", "getMorph", "AbstractMorph"}, new String[]{"mount", "getMount", "IScriptEntity"}, new String[]{"target", "getTarget", "IScriptEntity"}, new String[]{"mainItem", "getMainItem", "IScriptItemStack"}, new String[]{"offItem", "getOffItem", "IScriptItemStack"}, new String[]{"fullData", "getFullData", "INBTCompound"}, new String[]{"entityData", "getEntityData", "INBTCompound"}, new String[]{"UIContext", "getUIContext", "IMappetUIContext"}, new String[]{"data", "getData", "INBTCompound"});

   public static int getAccentForSuggestion(Suggestion s) {
      if (s == null) {
         return AutoCompleteConfig.AutoCompleteColors.COLOR_OTHER;
      } else {
         switch (s.className != null ? s.className : "") {
            case "var":
               return AutoCompleteConfig.AutoCompleteColors.COLOR_VAR;
            case "[]":
               return AutoCompleteConfig.AutoCompleteColors.COLOR_ARRAY;
            case "{}":
               return AutoCompleteConfig.AutoCompleteColors.COLOR_OBJECT;
            case "fn":
               return AutoCompleteConfig.AutoCompleteColors.COLOR_FUNCTION;
            case "kw":
               return AutoCompleteConfig.AutoCompleteColors.COLOR_KEYWORD;
            case "java":
               return AutoCompleteConfig.AutoCompleteColors.COLOR_JAVA;
            case "lib":
               return AutoCompleteConfig.AutoCompleteColors.COLOR_LIB;
            case "icon":
               return AutoCompleteConfig.AutoCompleteColors.COLOR_ICON;
            case "shader":
               return AutoCompleteConfig.AutoCompleteColors.COLOR_SHADER;
            case "hud":
               return AutoCompleteConfig.AutoCompleteColors.COLOR_HUD;
            case "alias":
               return AutoCompleteConfig.AutoCompleteColors.COLOR_ALIAS;
            default:
               return s.className == null || !s.className.startsWith("IScript") && !s.className.startsWith("IMappet") ? AutoCompleteConfig.AutoCompleteColors.COLOR_OTHER : AutoCompleteConfig.AutoCompleteColors.COLOR_ISCRIPT;
         }
      }
   }

   public static class AutoCompleteColors {
      public static int COLOR_VAR = 0xffd87878;
      public static int COLOR_ARRAY = 0xffb98a9e;
      public static int COLOR_OBJECT = 0xffd08a6a;
      public static int COLOR_FUNCTION = 0xffe06c75;
      public static int COLOR_KEYWORD = 0xffc586c0;
      public static int COLOR_ISCRIPT = 0xffc45b5b;
      public static int COLOR_JAVA = 0xffd19a66;
      public static int COLOR_SHADER = 0xffb76e79;
      public static int COLOR_HUD = 0xffc66d76;
      public static int COLOR_OTHER = 0xffb0a0a0;
      public static int COLOR_LIB = 0xffa85d68;
      public static int COLOR_ICON = 0xffd06b72;
      public static int COLOR_ALIAS = 0xffe08b8b;
   }

   public static class Suggestion {
      public final String methodName;
      public final String returnType;
      public final String className;

      public Suggestion(String methodName, String returnType, String className) {
         this.methodName = methodName;
         this.returnType = returnType != null ? returnType : "";
         this.className = className != null ? className : "";
      }
   }
}
