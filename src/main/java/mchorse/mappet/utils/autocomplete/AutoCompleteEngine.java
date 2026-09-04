package mchorse.mappet.utils.autocomplete;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mchorse.mappet.Mappet;
import mchorse.mappet.utils.autocomplete.utils.CollectionResolver;
import mchorse.mappet.utils.autocomplete.utils.CompletionHelper;
import mchorse.mappet.utils.autocomplete.utils.DocResolver;
import mchorse.mappet.utils.autocomplete.utils.JavaResolver;
import mchorse.mappet.utils.autocomplete.utils.ScopeAnalyzer;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import net.minecraft.class_310;

public class AutoCompleteEngine {
   private static final Map<String, String> KNOWN_VAR_TYPES = new LinkedHashMap();
   private static final Map<String, String> ALIAS_METHODS;
   private static Map<String, Icon> iconCache;
   private static final String[] BUILTIN_VALUE_KEYS;
   private static final String[] KEY_BINDING_KEYS;
   private static final String[] KEY_BINDING_ACTIONS;
   private static final String[] KEY_BINDING_METHODS = new String[]{"activateKeyBinding", "getKeyBinding", "resetKeyBinding", "setKeyBinding"};
   private static volatile List<String> shaderIds = Collections.emptyList();
   private static final String[] SHADER_ID_METHODS = new String[]{"setShader", "applyShader", "applyUIShader", "applyHUDShader"};

   public static String resolveChainType(String chain, String fullText) {
      if (chain != null && !chain.isEmpty()) {
         if (chain.endsWith(".closeUI()") || chain.endsWith(".closeMappetUI()")) {
            return "IScriptPlayer";
         }

         if (chain.matches(".*\\.getHand\\([^)]*\\)")) {
            return "IHandSettings";
         }
         if (!chain.contains(".")) {
            String direct = (String)KNOWN_VAR_TYPES.get(chain);
            if (direct != null) {
               return direct;
            } else {
               String t = resolveVarReturnClass(chain, fullText);
               if (t == null || t.isEmpty()) {
                  t = resolveVarReturnClassFromAlias(chain, fullText);
               }
               if (t == null || t.isEmpty()) {
               }

               return t != null && !t.isEmpty() ? t : null;
            }
         } else {
            List<String> segments = splitChain(chain);
            if (segments.isEmpty()) {
               return null;
            } else {
               String head = (String)segments.get(0);
               String currentType = (String)KNOWN_VAR_TYPES.get(head);
               if (currentType == null) {
                  currentType = resolveVarReturnClass(head, fullText);
                  if (currentType == null || currentType.isEmpty()) {
                     currentType = resolveVarReturnClassFromAlias(head, fullText);
                  }
               }

               if (currentType != null && !currentType.isEmpty()) {
                  for(int i = 1; i < segments.size(); ++i) {
                     String seg = (String)segments.get(i);
                     int parenIdx = seg.indexOf(40);
                     String methodName = parenIdx >= 0 ? seg.substring(0, parenIdx) : seg;
                     if (methodName.isEmpty()) {
                        return null;
                     }

                     if (methodName.equals("getAllHud")) {
                        currentType = "IHudElement";
                        continue;
                     }

                     if (currentType.contains("IScriptPlayer") && methodName.equals("getSettings")) {
                        currentType = "IGameSettings";
                        continue;
                     }

                     if (currentType.contains("IHudElement") && isHudElementName(methodName)) {
                        currentType = "IHudElement";
                        continue;
                     }

                     String next = DocResolver.findReturnTypeByMethodInClass(currentType, methodName);
                     if (next == null || next.isEmpty()) {
                        String getter = (String)ALIAS_METHODS.get(methodName);
                        if (getter != null) {
                           next = DocResolver.findReturnTypeByMethodInClass(currentType, getter);
                        }
                     }

                     if (next == null || next.isEmpty()) {
                        next = DocResolver.findReturnTypeByMethodName(methodName);
                     }

                     if (next == null || next.isEmpty()) {
                        return null;
                     }

                     currentType = next;
                  }

                  return currentType;
               } else {
                  return null;
               }
            }
         }
      } else {
         return null;
      }
   }

   private static boolean isHudElementName(String name) {
      return name.equals("hotbar") || name.equals("health") || name.equals("hunger") || name.equals("experience")
         || name.equals("crosshair") || name.equals("statusEffects") || name.equals("mountHealth")
         || name.equals("vignette") || name.equals("spyglass") || name.equals("itemTooltip");
   }

   private static List<String> splitChain(String chain) {
      List<String> result = new ArrayList();
      int depth = 0;
      int start = 0;

      for(int i = 0; i < chain.length(); ++i) {
         char c = chain.charAt(i);
         if (c == '(') {
            ++depth;
         } else if (c == ')') {
            --depth;
         } else if (c == '.' && depth == 0) {
            String seg = chain.substring(start, i).trim();
            if (!seg.isEmpty()) {
               result.add(seg);
            }

            start = i + 1;
         }
      }

      String last = chain.substring(start).trim();
      if (!last.isEmpty()) {
         result.add(last);
      }

      return result;
   }

   public static List<AutoCompleteConfig.Suggestion> findMatchingInScope(String prefix, List<String> allLines) {
      return ScopeAnalyzer.findMatchingInScope(prefix, allLines, false);
   }

   public static List<AutoCompleteConfig.Suggestion> findMatchingInScope(String prefix, List<String> allLines, boolean insideSwitch) {
      return ScopeAnalyzer.findMatchingInScope(prefix, allLines, insideSwitch);
   }

   public static boolean isInsideSwitch(List<String> allLines, int cursorLineIdx) {
      return ScopeAnalyzer.isInsideSwitch(allLines, cursorLineIdx);
   }

   public static List<AutoCompleteConfig.Suggestion> findMatchingMethods(String prefix) {
      return DocResolver.findMatchingMethods(prefix);
   }

   public static boolean isHudElementChain(String chain) {
      if (chain == null || chain.isEmpty()) {
         return false;
      }

      List<String> parts = splitChain(chain);
      if (parts.isEmpty()) {
         return false;
      }

      String last = parts.get(parts.size() - 1);
      return isHudElementName(last);
   }

   public static List<AutoCompleteConfig.Suggestion> findHudElementMethods(String prefix) {
      List<AutoCompleteConfig.Suggestion> result = new ArrayList();
      String lower = prefix == null ? "" : prefix.toLowerCase();

      if (lower.isEmpty() || "render".startsWith(lower)) {
         result.add(new AutoCompleteConfig.Suggestion("render", "boolean", "fn"));
      }

      if (lower.isEmpty() || "pos".startsWith(lower)) {
         result.add(new AutoCompleteConfig.Suggestion("pos", "int, int", "fn"));
      }

      return result;
   }

   public static List<AutoCompleteConfig.Suggestion> findMethodsOfClass(String className, String prefix) {
      return findMethodsOfClass(className, prefix, false);
   }

   public static List<AutoCompleteConfig.Suggestion> findMethodsOfClass(String className, String prefix, boolean clientScript) {
      List<AutoCompleteConfig.Suggestion> result = DocResolver.findMethodsOfClass(className, prefix);
            if (className.contains("IHudElement")) {
         result.removeIf((s) -> s.methodName.equals("Hotbar")
            || s.methodName.equals("Health")
            || s.methodName.equals("Hunger")
            || s.methodName.equals("Experience")
            || s.methodName.equals("Crosshair")
            || s.methodName.equals("StatusEffects")
            || s.methodName.equals("MountHealth")
            || s.methodName.equals("Vignette")
            || s.methodName.equals("Spyglass")
            || s.methodName.equals("ItemTooltip"));
      }

      LinkedHashSet<String> existing = new LinkedHashSet();

      for(AutoCompleteConfig.Suggestion s : result) {
         existing.add(s.methodName);
      }

      if (className.contains("IHudElement")) {
         if (!existing.contains("render")) {
            result.add(new AutoCompleteConfig.Suggestion("render", "boolean", "fn"));
            existing.add("render");
         }

         if (!existing.contains("pos")) {
            result.add(new AutoCompleteConfig.Suggestion("pos", "int, int", "fn"));
            existing.add("pos");
         }
      }

      String[] classesArray = className.split(",");

      for(String singleClass : classesArray) {
         singleClass = singleClass.trim();
         if (!singleClass.isEmpty()) {
            List<String> subTypes = DocResolver.collectSubTypes(singleClass);
            if (!subTypes.isEmpty()) {
               for(String subType : subTypes) {
                  for(AutoCompleteConfig.Suggestion s : DocResolver.findMethodsOfClass(subType, prefix)) {
                     if (!existing.contains(s.methodName)) {
                        result.add(s);
                        existing.add(s.methodName);
                     }
                  }
               }
            }
         }
      }

      String lower = prefix != null ? prefix.toLowerCase() : "";
      LinkedHashSet<String> classMethods = new LinkedHashSet();

      for(AutoCompleteConfig.Suggestion s : DocResolver.findMethodsOfClass(className, "")) {
         classMethods.add(s.methodName);
      }

      List<AutoCompleteConfig.Suggestion> aliasesToAdd = new ArrayList();

      for(String[] alias : AutoCompleteConfig.PROPERTY_ALIASES) {
         if (alias.length >= 3) {
            String aliasName = alias[0];
            String getterName = alias[1];
            String returnTypeHint = alias[2];
            if ((lower.isEmpty() || aliasName.toLowerCase().startsWith(lower)) && !existing.contains(aliasName) && classMethods.contains(getterName)) {
               aliasesToAdd.add(new AutoCompleteConfig.Suggestion(aliasName, returnTypeHint, "alias"));
            }
         }
      }

      if (!aliasesToAdd.isEmpty()) {
         aliasesToAdd.addAll(result);
         result = aliasesToAdd;
      }

      addRequiredApiSuggestions(className, prefix, result, clientScript);
      if (clientScript) {
         result.removeIf((suggestion) -> isServerOnlyClientScriptMethod(suggestion.methodName));
      }
      return result;
   }

   private static boolean isServerOnlyClientScriptMethod(String method) {
      return method.equals("executeCommand")
            || method.equals("send")
            || method.equals("sendRaw")
            || method.equals("sendTitleDurations")
            || method.equals("sendTitle")
            || method.equals("sendSubtitle")
            || method.equals("sendActionBar");
   }

   





   private static void addRequiredApiSuggestions(String className, String prefix, List<AutoCompleteConfig.Suggestion> suggestions) {
      addRequiredApiSuggestions(className, prefix, suggestions, false);
   }

   private static void addRequiredApiSuggestions(String className, String prefix, List<AutoCompleteConfig.Suggestion> suggestions, boolean clientScript) {
      String lowerPrefix = prefix == null ? "" : prefix.toLowerCase();
      if (className == null) {
         return;
      }

      if (className.contains("IScriptFactory")) {
         addApiSuggestion(suggestions, lowerPrefix, "createBlockState", "String, int", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "createItem", "String, int, int", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "createUIFromFile", "String id, String callbackScript, String callbackFunction", "fn");
      }

      if (className.contains("IScriptPlayer") || className.contains("IScriptWorld")) {
         addApiSuggestion(suggestions, lowerPrefix, "playSound", "String, String, double, double, double, float, float", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "playStaticSound", "String, String, float, float", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "getManagedSound", "String id", "fn");
      }

      if (className.contains("IScriptEntity")) {
         addApiSuggestion(suggestions, lowerPrefix, "rotateBy", "String interpolation, int durationTicks, float pitch, float yaw, float yawHead, boolean disableAI", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "rayTrace", "double maxDistance", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "rayTraceBlock", "double maxDistance", "fn");
      }

      if (className.contains("IScriptPlayer")) {
         addApiSuggestion(suggestions, lowerPrefix, "playManagedSound", "String id, String event, String category, double x, double y, double z, float volume, float pitch", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "playManagedSound", "String id, String event, String category, IScriptEntity entity, float volume, float pitch", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "playManagedStaticSound", "String id, String event, String category, float volume, float pitch", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "setMousePosition", "double x, double y (0.0..1.0)", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "moveMouseTo", "String interpolation, int durationTicks, double x, double y (0.0..1.0)", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "moveMouseBy", "String interpolation, int durationTicks, double dx, double dy (-1.0..1.0)", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "setSolidHitbox", "boolean solid", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "executeClientScript", "String script", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "executeClientScript", "String script, String function", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "disableJump", "boolean disabled", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "disableSprint", "boolean disabled", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "openUI", "String id, boolean defaultData", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "closeUI", "", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "closeMappetUI", "", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "setHUDWorldLighting", "String id, boolean enabled", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "setHUDWorldLighting", "String id, boolean enabled, float intensity (0.0..4.0)", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "applyShader", "String id, boolean ui, boolean hud", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "removeShader", "boolean ui, boolean hud", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "applyUIShader", "String id", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "removeUIShader", "", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "applyHUDShader", "String id", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "removeHUDShader", "", "fn");
      }

      if (className.contains("IScriptWorld")) {
         addApiSuggestion(suggestions, lowerPrefix, "playManagedStaticSound", "String id, String event, String category, float volume, float pitch", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "displayLight", "String id, int duration, double x, double y, double z, int level", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "displayRayTraceLight", "String id, int duration, double x, double y, double z, IScriptRayTrace rayTrace, int level, float spacing", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "displayFlashlight", "String id, int duration, double x, double y, double z, IScriptRayTrace rayTrace, int startLevel, int endLevel, float spacing", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "removeLight", "String id", "fn");
      }

      if (className.contains("IScriptRayTrace")) {
         addApiSuggestion(suggestions, lowerPrefix, "displayLight", "String id, int duration, int level, float spacing", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "displayFlashlight", "String id, int duration, int startLevel, int endLevel, float spacing", "fn");
      }

      if (className.contains("IScriptLight")) {
         addApiSuggestion(suggestions, lowerPrefix, "nonSolidIgnore", "", "fn");
      }

      if (className.contains("IScriptManagedSound")) {
         addApiSuggestion(suggestions, lowerPrefix, "setPosition", "double x, double y, double z", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "setVolume", "float volume", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "getTimeCode", "", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "setTimeCode", "double seconds", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "stop", "", "fn");
      }

      if (className.contains("IGameSettings")) {
         addApiSuggestion(suggestions, lowerPrefix, "activateKeyBinding", "String id (например key_key.jump)", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "getKeyBinding", "String id (например key_key.jump)", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "resetKeyBinding", "String id (например key_key.jump)", "fn");
         addApiSuggestion(suggestions, lowerPrefix, "setKeyBinding", "String id, String key", "fn");
      }
   }

   private static void addApiSuggestion(List<AutoCompleteConfig.Suggestion> suggestions, String lowerPrefix, String method, String args, String icon) {
      if (!lowerPrefix.isEmpty() && !method.toLowerCase().startsWith(lowerPrefix)) {
         return;
      }

      for(AutoCompleteConfig.Suggestion suggestion : suggestions) {
         if (method.equals(suggestion.methodName)) {
            return;
         }
      }

      suggestions.add(new AutoCompleteConfig.Suggestion(method, args, icon));
   }

   public static String resolveVarReturnClass(String varName, String fullText) {
      return DocResolver.resolveVarReturnClass(varName, fullText);
   }

   public static String resolveVarClassFromJavaType(String varName, String fullText) {
      return DocResolver.resolveVarClassFromJavaType(varName, fullText);
   }

   public static String resolveVarReturnClassFromAlias(String varName, String fullText) {
      String direct = (String)KNOWN_VAR_TYPES.get(varName);
      if (direct != null) {
         return direct;
      }

      Pattern hudMethod = Pattern.compile("(?:var|let|const)\\s+" + Pattern.quote(varName) + "\\s*=\\s*[\\w.]+\\.getAllHud\\s*\\(", 8);
      if (hudMethod.matcher(fullText).find()) {
         return "IHudElement";
      }

      Pattern p = Pattern.compile("(?:var|let|const)\\s+" + Pattern.quote(varName) + "\\s*=(?!=)\\s*(\\w+)\\.(\\w+)(?:\\([^)]*\\))?", 8);
         Matcher m = p.matcher(fullText);
         if (m.find()) {
            String ownerVar = m.group(1);
            String prop = m.group(2);
            String ownerType = (String)KNOWN_VAR_TYPES.get(ownerVar);
            if (ownerType == null) {
               ownerType = resolveVarReturnClass(ownerVar, fullText);
            }

            if (ownerType != null && !ownerType.isEmpty()) {
               String rt = DocResolver.findReturnTypeByMethodInClass(ownerType, prop);
               if (rt != null && !rt.isEmpty()) {
                  return rt;
               }

               String methodName = (String)ALIAS_METHODS.getOrDefault(prop, prop);
               if (!methodName.equals(prop)) {
                  rt = DocResolver.findReturnTypeByMethodInClass(ownerType, methodName);
                  if (rt != null && !rt.isEmpty()) {
                     return rt;
                  }
               }

               rt = DocResolver.findReturnTypeByMethodName(methodName);
               if (rt != null && !rt.isEmpty()) {
                  return rt;
               }
            } else {
               String methodName = (String)ALIAS_METHODS.getOrDefault(prop, prop);
               String rt = DocResolver.findReturnTypeByMethodName(methodName);
               if (rt != null && !rt.isEmpty()) {
                  return rt;
               }
            }
         }

      return null;
   }

   public static List<AutoCompleteConfig.Suggestion> findMethodsForKnownVar(String varName, String prefix, List<String> allLines) {
      return findMethodsForKnownVar(varName, prefix, allLines, false);
   }

   public static List<AutoCompleteConfig.Suggestion> findMethodsForKnownVar(String varName, String prefix, List<String> allLines, boolean clientScript) {
      String type = (String)KNOWN_VAR_TYPES.get(varName);
      if (type != null) {
         return findMethodsOfClass(type, prefix, clientScript);
      } else {
         String fullText = ScopeAnalyzer.joinLines(allLines);
         type = resolveVarReturnClassFromAlias(varName, fullText);
         return (List<AutoCompleteConfig.Suggestion>)(type != null && !type.isEmpty() ? findMethodsOfClass(type, prefix, clientScript) : new ArrayList());
      }
   }

   public static List<AutoCompleteConfig.Suggestion> injectMappetGlobal(String prefix, List<AutoCompleteConfig.Suggestion> existing) {
      if (prefix != null && !prefix.isEmpty() && !"mappet".startsWith(prefix.toLowerCase())) {
         return existing;
      } else {
         for(AutoCompleteConfig.Suggestion s : existing) {
            if ("mappet".equals(s.methodName)) {
               return existing;
            }
         }

         List<AutoCompleteConfig.Suggestion> result = new ArrayList(existing);
         result.add(0, new AutoCompleteConfig.Suggestion("mappet", "IScriptFactory", "var"));
         return result;
      }
   }

   public static List<AutoCompleteConfig.Suggestion> findMatchingJavaClasses(String prefix) {
      return JavaResolver.findMatchingJavaClasses(prefix);
   }

   public static String extractJavaTypePrefix(String line, int cursorOffset) {
      return JavaResolver.extractJavaTypePrefix(line, cursorOffset);
   }

   public static List<AutoCompleteConfig.Suggestion> findMatchingJavaNashorn(String prefix) {
      return JavaResolver.findMatchingJavaNashorn(prefix);
   }

   public static List<AutoCompleteConfig.Suggestion> findMatchingFromCollection(String varName, String prefix, List<String> allLines) {
      return CollectionResolver.findMatchingFromCollection(varName, prefix, allLines);
   }

   private static Map<String, Icon> getIconMap() {
      if (iconCache != null) {
         return iconCache;
      } else {
         iconCache = new HashMap();

         try {
            Class<?> registry = Class.forName("mchorse.mclib.client.gui.utils.IconRegistry");
            String[] fieldNames = new String[]{"ICONS", "icons", "REGISTRY"};

            for(String name : fieldNames) {
               try {
                  Field field = registry.getDeclaredField(name);
                  field.setAccessible(true);
                  Object obj = field.get((Object)null);
                  if (obj instanceof Map) {
                     iconCache = (Map)obj;
                     break;
                  }
               } catch (NoSuchFieldException var8) {
               }
            }
         } catch (Exception var9) {
            iconCache = new HashMap();
         }

         return iconCache;
      }
   }

   public static String extractValueKeyPrefix(String line, int cursorOffset) {
      if (line != null && cursorOffset > 0) {
         int safeOffset = Math.min(cursorOffset, line.length());
         String before = line.substring(0, safeOffset);
         int idx = -1;
         char quoteChar = 0;

         for(int i = before.length() - 1; i >= 0; --i) {
            char c = before.charAt(i);
            if ((c == '"' || c == '\'') && i >= 9 && (before.substring(i - 9, i).equals("getValue(") || before.substring(i - 9, i).equals("setValue("))) {
               idx = i - 9;
               quoteChar = c;
               break;
            }
         }

         if (idx == -1) {
            return null;
         } else {
            int startQuote = idx + 10;
            if (startQuote > before.length()) {
               return null;
            } else {
               int closeQuote = before.indexOf(quoteChar, startQuote);
               return closeQuote != -1 && closeQuote < cursorOffset ? null : before.substring(startQuote);
            }
         }
      } else {
         return null;
      }
   }

   private static String findKeyBindingMethod(String before) {
      int best = -1;
      String result = null;
      for (String method : KEY_BINDING_METHODS) {
         int marker = before.lastIndexOf(method + "(");
         if (marker > best) {
            best = marker;
            result = method;
         }
      }
      return result;
   }

   public static String extractKeyBindingActionPrefix(String line, int cursorOffset) {
      if (line == null || cursorOffset <= 0) {
         return null;
      }

      int safeOffset = Math.min(cursorOffset, line.length());
      String before = line.substring(0, safeOffset);
      String method = findKeyBindingMethod(before);
      int marker = method == null ? -1 : before.lastIndexOf(method + "(");
      if (marker < 0 || before.indexOf(')', marker) >= 0) {
         return null;
      }

      int start = marker + method.length() + 1;
      for(int i = start; i < before.length(); ++i) {
         char c = before.charAt(i);
         if (c == ',') {
            return null;
         }

         if (c == 34 || c == 39) {
            int close = before.indexOf(c, i + 1);
            return close != -1 && close < safeOffset ? null : before.substring(i + 1);
         }

         if (!Character.isWhitespace(c)) {
            return null;
         }
      }

      return null;
   }

   public static List<AutoCompleteConfig.Suggestion> findMatchingKeyBindingActions(String prefix) {
      List<AutoCompleteConfig.Suggestion> result = new ArrayList();
      String lowerPrefix = prefix == null ? "" : prefix.toLowerCase();

      for(String action : KEY_BINDING_ACTIONS) {
         if (lowerPrefix.isEmpty() || action.toLowerCase().contains(lowerPrefix)) {
            result.add(new AutoCompleteConfig.Suggestion(action, "KeyBinding action", "action"));
            if (result.size() >= AutoCompleteConfig.MAX_SUGGESTIONS) {
               break;
            }
         }
      }

      result.sort(Comparator.comparing((a) -> a.methodName));
      return result;
   }

   public static String applyKeyBindingActionCompletion(String line, int cursorOffset, String action, int[] newCursorOffset) {
      int safe = Math.max(0, Math.min(cursorOffset, line == null ? 0 : line.length()));
      if (line == null) {
         line = "";
      }

      String before = line.substring(0, safe);
      String tail = line.substring(safe);
      String method = findKeyBindingMethod(before);
      int marker = method == null ? -1 : before.lastIndexOf(method + "(");
      if (marker < 0) {
         if (newCursorOffset != null) {
            newCursorOffset[0] = safe;
         }

         return line;
      }

      int quoteIdx = -1;
      char quoteChar = '"';
      for(int i = marker + method.length() + 1; i < before.length(); ++i) {
         char c = before.charAt(i);
         if (c == ',') {
            break;
         }

         if (c == 34 || c == 39) {
            quoteIdx = i;
            quoteChar = c;
            break;
         }
      }

      String newLine;
      int newOffset;
      if (quoteIdx < 0) {
         newLine = before + "\"" + action + "\"" + tail;
         newOffset = before.length() + action.length() + 2;
      } else {
         String left = line.substring(0, quoteIdx + 1);
         String rest = line.substring(quoteIdx + 1);
         int end = rest.length();
         for(int i = 0; i < rest.length(); ++i) {
            char c = rest.charAt(i);
            if (c == quoteChar || Character.isWhitespace(c) || c == ')' || c == ',') {
               end = i;
               break;
            }
         }

         newLine = left + action + rest.substring(end);
         newOffset = left.length() + action.length();
      }

      if (newCursorOffset != null) {
         newCursorOffset[0] = Math.max(0, Math.min(newOffset, newLine.length()));
      }

      return newLine;
   }

   public static String extractKeyBindingPrefix(String line, int cursorOffset) {
      if (line == null || cursorOffset <= 0) {
         return null;
      }

      int safeOffset = Math.min(cursorOffset, line.length());
      String before = line.substring(0, safeOffset);
      int marker = before.lastIndexOf("setKeyBinding(");
      if (marker < 0 || before.indexOf(')', marker) >= 0) {
         return null;
      }

      int comma = before.indexOf(',', marker + "setKeyBinding(".length());
      if (comma < 0) {
         return null;
      }

      for(int i = comma + 1; i < before.length(); ++i) {
         char c = before.charAt(i);
         if (c == 34 || c == 39) {
            int close = before.indexOf(c, i + 1);
            return close != -1 && close < safeOffset ? null : before.substring(i + 1);
         }

         if (!Character.isWhitespace(c)) {
            return null;
         }
      }

      return null;
   }

   public static List<AutoCompleteConfig.Suggestion> findMatchingKeyBindingKeys(String prefix) {
      List<AutoCompleteConfig.Suggestion> result = new ArrayList();
      String lowerPrefix = prefix == null ? "" : prefix.toLowerCase();

      for(String key : KEY_BINDING_KEYS) {
         if (lowerPrefix.isEmpty() || key.toLowerCase().startsWith(lowerPrefix)) {
            result.add(new AutoCompleteConfig.Suggestion(key, "KeyBinding", "key"));
            if (result.size() >= AutoCompleteConfig.MAX_SUGGESTIONS) {
               break;
            }
         }
      }

      result.sort(Comparator.comparing((a) -> a.methodName));
      return result;
   }

   public static String applyKeyBindingCompletion(String line, int cursorOffset, String key, int[] newCursorOffset) {
      int safe = Math.max(0, Math.min(cursorOffset, line == null ? 0 : line.length()));
      if (line == null) {
         line = "";
      }

      String before = line.substring(0, safe);
      String tail = line.substring(safe);
      int marker = before.lastIndexOf("setKeyBinding(");
      int comma = marker < 0 ? -1 : before.indexOf(',', marker + "setKeyBinding(".length());
      if (comma < 0) {
         if (newCursorOffset != null) {
            newCursorOffset[0] = safe;
         }

         return line;
      }

      int quoteIdx = -1;
      char quoteChar = '\"';
      for(int i = comma + 1; i < before.length(); ++i) {
         char c = before.charAt(i);
         if (c == 34 || c == 39) {
            quoteIdx = i;
            quoteChar = c;
            break;
         }
      }

      String newLine;
      int newOffset;
      if (quoteIdx < 0) {
         newLine = before + " \"" + key + "\"" + tail;
         newOffset = before.length() + key.length() + 3;
      } else {
         String left = line.substring(0, quoteIdx + 1);
         String rest = line.substring(quoteIdx + 1);
         int end = rest.length();
         for(int i = 0; i < rest.length(); ++i) {
            char c = rest.charAt(i);
            if (c == quoteChar || Character.isWhitespace(c) || c == ')' || c == ',') {
               end = i;
               break;
            }
         }

         newLine = left + key + rest.substring(end);
         newOffset = left.length() + key.length();
      }

      if (newCursorOffset != null) {
         newCursorOffset[0] = Math.max(0, Math.min(newOffset, newLine.length()));
      }

      return newLine;
   }

   public static String extractHudElementNamePrefix(String line, int cursorOffset)
   {
      if (line == null || cursorOffset <= 0)
      {
         return null;
      }

      int safeOffset = Math.min(cursorOffset, line.length());
      String before = line.substring(0, safeOffset);
      int marker = before.lastIndexOf("getAllHud(");

      if (marker < 0 || before.indexOf(')', marker) >= 0)
      {
         return null;
      }

      int quote = -1;
      char quoteChar = 0;

      for (int i = marker + "getAllHud(".length(); i < before.length(); i++)
      {
         char c = before.charAt(i);

         if (c == ',' || c == ')')
         {
            return null;
         }

         if (c == '\"' || c == '\'')
         {
            quote = i;
            quoteChar = c;
            break;
         }

         if (!Character.isWhitespace(c))
         {
            return null;
         }
      }

      if (quote < 0)
      {
         return null;
      }

      int close = before.indexOf(quoteChar, quote + 1);
      return close >= 0 && close < safeOffset ? null : before.substring(quote + 1);
   }

   public static List<AutoCompleteConfig.Suggestion> findMatchingHudElementNames(String prefix)
   {
      String[] names = {"Crosshair", "Hotbar", "Health", "Hunger", "Experience", "StatusEffects", "MountHealth", "Vignette", "Spyglass", "ItemTooltip"};
      String lower = prefix == null ? "" : prefix.toLowerCase();
      List<AutoCompleteConfig.Suggestion> result = new ArrayList();

      for (String name : names)
      {
         if (lower.isEmpty() || name.toLowerCase().startsWith(lower))
         {
            result.add(new AutoCompleteConfig.Suggestion(name, "HUD element", "hud"));
         }
      }

      return result;
   }

   public static String applyHudElementNameCompletion(String line, int cursorOffset, String name, int[] newCursorOffset)
   {
      int safe = Math.max(0, Math.min(cursorOffset, line == null ? 0 : line.length()));
      String source = line == null ? "" : line;
      String before = source.substring(0, safe);
      String tail = source.substring(safe);
      int marker = before.lastIndexOf("getAllHud(");
      int quote = -1;
      char quoteChar = '\"';

      for (int i = marker + "getAllHud(".length(); i < before.length(); i++)
      {
         char c = before.charAt(i);
         if (c == '\"' || c == '\'')
         {
            quote = i;
            quoteChar = c;
            break;
         }
      }

      String result;
      int offset;

      if (quote < 0)
      {
         result = before + "\"" + name + "\"" + tail;
         offset = before.length() + name.length() + 2;
      }
      else
      {
         String left = source.substring(0, quote + 1);
         String rest = source.substring(quote + 1);
         int end = rest.length();

         for (int i = 0; i < rest.length(); i++)
         {
            char c = rest.charAt(i);
            if (c == quoteChar || c == ')' || c == ',')
            {
               end = i;
               break;
            }
         }

         result = left + name + rest.substring(end);
         offset = left.length() + name.length();
      }

      if (newCursorOffset != null)
      {
         newCursorOffset[0] = Math.min(offset, result.length());
      }

      return result;
   }

   public static List<AutoCompleteConfig.Suggestion> findMatchingValueKeys(String prefix, String fullText) {
      List<AutoCompleteConfig.Suggestion> result = new ArrayList();
      String lowerPrefix = prefix == null ? "" : prefix.toLowerCase();
      LinkedHashSet<String> keys = new LinkedHashSet();

      for(String key : BUILTIN_VALUE_KEYS) {
         keys.add(key);
      }

      if (fullText != null) {
         Matcher m = Pattern.compile("(?:setValue|getValue)\\(\\s*[\"']([^\"']*)[\"']").matcher(fullText);

         while(m.find()) {
            String key = m.group(1);
            if (!key.isEmpty()) {
               keys.add(key);
            }
         }
      }

      for(String key : keys) {
         if (lowerPrefix.isEmpty() || key.toLowerCase().startsWith(lowerPrefix)) {
            result.add(new AutoCompleteConfig.Suggestion(key, "value", "value"));
            if (result.size() >= AutoCompleteConfig.MAX_SUGGESTIONS) {
               break;
            }
         }
      }

      result.sort(Comparator.comparing((a) -> a.methodName));
      return result;
   }

   public static String applyValueKeyCompletion(String line, int cursorOffset, String key, int[] newCursorOffset) {
      int safe = Math.max(0, Math.min(cursorOffset, line == null ? 0 : line.length()));
      if (line == null) {
         line = "";
      }

      String before = line.substring(0, safe);
      String tail = line.substring(safe);
      int marker = Math.max(before.lastIndexOf("getValue("), before.lastIndexOf("setValue("));
      if (marker < 0) {
         if (newCursorOffset != null) {
            newCursorOffset[0] = safe;
         }

         return line;
      } else {
         int start = marker + 9;
         int quoteIdx = -1;
         char quoteChar = '"';

         for(int i = start; i < before.length(); ++i) {
            char c = before.charAt(i);
            if (c == '"' || c == '\'') {
               quoteIdx = i;
               quoteChar = c;
               break;
            }
         }

         String newLine;
         int newOffset;
         if (quoteIdx < 0) {
            newLine = before + "\"" + key + "\"" + tail;
            newOffset = before.length() + 1 + key.length();
         } else {
            String left = line.substring(0, quoteIdx + 1);
            newLine = left + key + quoteChar + tail;
            newOffset = left.length() + key.length() + 1;
         }

         if (newCursorOffset != null) {
            newCursorOffset[0] = Math.max(0, Math.min(newOffset, newLine.length()));
         }

         return newLine;
      }
   }

   public static List<AutoCompleteConfig.Suggestion> findMatchingIcons(String prefix) {
      List<AutoCompleteConfig.Suggestion> result = new ArrayList();
      Map<String, Icon> icons = getIconMap();
      if (icons.isEmpty()) {
         return result;
      } else {
         String lowerPrefix = prefix == null ? "" : prefix.toLowerCase();

         for(String id : icons.keySet()) {
            if (lowerPrefix.isEmpty() || id.toLowerCase().startsWith(lowerPrefix)) {
               result.add(new AutoCompleteConfig.Suggestion(id, "Icon", "icon"));
               if (result.size() >= AutoCompleteConfig.MAX_SUGGESTIONS) {
                  break;
               }
            }
         }

         result.sort(Comparator.comparing((a) -> a.methodName));
         return result;
      }
   }

   public static String extractIconPrefix(String line, int cursorOffset) {
      if (line == null || cursorOffset <= 0) {
         return null;
      }

      int safeOffset = Math.min(cursorOffset, line.length());
      String before = line.substring(0, safeOffset);
      int iconCall = before.lastIndexOf("icon(");
      int contextCall = before.lastIndexOf("context(");
      int callStart = Math.max(iconCall, contextCall);
      int callLength = contextCall > iconCall ? "context(".length() : "icon(".length();

      if (callStart < 0) {
         return null;
      }

      int quoteIndex = -1;
      char quoteChar = 0;
      for (int i = callStart + callLength; i < before.length(); i++) {
         char c = before.charAt(i);
         if (c == '"' || c == '\'') {
            quoteIndex = i;
            quoteChar = c;
            break;
         }
         if (!Character.isWhitespace(c)) {
            return null;
         }
      }

      if (quoteIndex < 0) {
         return null;
      }

      int start = quoteIndex + 1;
      int closeQuote = before.indexOf(quoteChar, start);
      return closeQuote != -1 && closeQuote < safeOffset ? null : before.substring(start);
   }

   public static String extractShaderPrefix(String line, int cursorOffset) {
      if (line != null && cursorOffset > 0) {
         int safeOffset = Math.min(cursorOffset, line.length());
         String before = line.substring(0, safeOffset);
         int lastParen = findShaderCallStart(before);
         if (lastParen < 0) {
            return null;
         } else {
            int start = lastParen + getShaderCallLength(before, lastParen);
            int quoteIdx = -1;
            char quoteChar = 0;

            for(int i = start; i < safeOffset; ++i) {
               char c = before.charAt(i);
               if (c == '"' || c == '\'') {
                  quoteIdx = i;
                  quoteChar = c;
                  break;
               }

               if (!Character.isWhitespace(c)) {
                  break;
               }
            }

            if (quoteIdx < 0) {
               return null;
            } else {
               int startIdx = quoteIdx + 1;
               if (startIdx > before.length()) {
                  return "";
               } else {
                  int closeQuote = before.indexOf(quoteChar, startIdx);
                  return closeQuote != -1 && closeQuote < safeOffset ? null : before.substring(startIdx);
               }
            }
         }
      } else {
         return null;
      }
   }

   public static List<AutoCompleteConfig.Suggestion> findMatchingShaders(String prefix) {
      List<AutoCompleteConfig.Suggestion> result = new ArrayList();
      String lower = prefix == null ? "" : prefix.toLowerCase();
      for(String id : shaderIds) {
         if (lower.isEmpty() || id.toLowerCase().startsWith(lower)) {
            result.add(new AutoCompleteConfig.Suggestion(id, "shader", "shader"));
         }
      }
      return result;
   }

   
   public static void setShaderIds(List<String> ids) {
      LinkedHashSet<String> cleaned = new LinkedHashSet();
      if (ids != null) {
         for(String id : ids) {
            if (id != null && !id.trim().isEmpty()) {
               cleaned.add(id.trim());
            }
         }
      }
      List<String> sorted = new ArrayList(cleaned);
      sorted.sort(String::compareToIgnoreCase);
      shaderIds = Collections.unmodifiableList(sorted);
   }

   private static int findShaderCallStart(String before) {
      int best = -1;
      for(String method : SHADER_ID_METHODS) {
         int index = before.lastIndexOf(method + "(");
         if (index > best) {
            best = index;
         }
      }
      return best;
   }

   private static int getShaderCallLength(String before, int start) {
      for(String method : SHADER_ID_METHODS) {
         if (before.startsWith(method + "(", start)) {
            return method.length() + 1;
         }
      }
      return 0;
   }

   public static String applyShaderCompletion(String line, int cursorOffset, String id, int[] newCursorOffset) {
      int safe = Math.max(0, Math.min(cursorOffset, line == null ? 0 : line.length()));
      if (line == null) {
         line = "";
      }

      String before = line.substring(0, safe);
      String tail = line.substring(safe);
      int lastParen = findShaderCallStart(before);
      if (lastParen < 0) {
         if (newCursorOffset != null) {
            newCursorOffset[0] = safe;
         }

         return line;
      } else {
         int start = lastParen + getShaderCallLength(before, lastParen);
         int quoteIdx = -1;
         char quoteChar = '"';

         for(int i = start; i < before.length(); ++i) {
            char c = before.charAt(i);
            if (c == '"' || c == '\'') {
               quoteIdx = i;
               quoteChar = c;
               break;
            }
         }

         String newLine;
         int newOffset;
         if (quoteIdx < 0) {
            newLine = before + '"' + id + '"' + tail;
            newOffset = before.length() + 1 + id.length();
         } else {
            String beforeQuote = line.substring(0, quoteIdx + 1);
            String afterQuote = line.substring(quoteIdx + 1);
            int end = afterQuote.length();

            for(int i = 0; i < afterQuote.length(); ++i) {
               char c = afterQuote.charAt(i);
               if (c == quoteChar || Character.isWhitespace(c) || c == ')' || c == ',') {
                  end = i;
                  break;
               }
            }

            newLine = beforeQuote + id + afterQuote.substring(end);
            newOffset = beforeQuote.length() + id.length();
         }

         if (newCursorOffset != null) {
            newCursorOffset[0] = Math.max(0, Math.min(newOffset, newLine.length()));
         }

         return newLine;
      }
   }

   public static String extractInterpolationPrefix(String line, int cursorOffset) {
      if (line == null || cursorOffset <= 0) return null;
      String before = line.substring(0, Math.min(cursorOffset, line.length()));
      int paren = before.lastIndexOf('(');
      if (paren < 0) return null;
      String head = before.substring(0, paren).replaceAll("\\s+", "");
      if (!head.matches(".*\\.(moveTo|moveMouseTo|moveMouseBy|rotateTo|rotateBy|scaleTo)$")) return null;
      String argument = before.substring(paren + 1);
      if (argument.indexOf(',') >= 0) return null;
      int quote = Math.max(argument.lastIndexOf('"'), argument.lastIndexOf('\''));
      return quote >= 0 ? argument.substring(quote + 1) : argument.trim();
   }

   public static List<AutoCompleteConfig.Suggestion> findMatchingInterpolations(String prefix) {
      String lower = prefix == null ? "" : prefix.toLowerCase();
      String[] values = {"linear", "sine_in", "sine_out", "sine_inout", "quad_in", "quad_out", "quad_inout", "cubic_in", "cubic_out", "cubic_inout", "smoothstep"};
      List<AutoCompleteConfig.Suggestion> result = new ArrayList();
      for (String value : values) {
         if (lower.isEmpty() || value.startsWith(lower)) result.add(new AutoCompleteConfig.Suggestion(value, "interpolation", "value"));
      }
      return result;
   }

   public static String applyInterpolationCompletion(String line, int cursorOffset, String value, int[] newCursorOffset) {
      return CompletionHelper.applyCompletionRaw(line, cursorOffset, value, newCursorOffset);
   }

   public static String extractHUDPrefix(String line, int cursorOffset) {
      if (line != null && cursorOffset > 0) {
         int safeOffset = Math.min(cursorOffset, line.length());
         String before = line.substring(0, safeOffset);
         String[] patterns = new String[]{"setupHUD(", "changeHUDMorph(", "changeHUD(", "closeHUD("};
         int last = -1;
         String found = null;

         for(String p : patterns) {
            int idx = before.lastIndexOf(p);
            if (idx > last) {
               last = idx;
               found = p;
            }
         }

         if (last < 0) {
            return null;
         } else {
            int start = last + found.length();
            int quoteIdx = -1;
            char quoteChar = 0;

            for(int i = start; i < safeOffset; ++i) {
               char c = before.charAt(i);
               if (c == '"' || c == '\'') {
                  quoteIdx = i;
                  quoteChar = c;
                  break;
               }

               if (!Character.isWhitespace(c)) {
                  break;
               }
            }

            if (quoteIdx < 0) {
               return null;
            } else {
               int startIdx = quoteIdx + 1;
               if (startIdx > before.length()) {
                  return "";
               } else {
                  int closeQuote = before.indexOf(quoteChar, startIdx);
                  return closeQuote != -1 && closeQuote < safeOffset ? null : before.substring(startIdx);
               }
            }
         }
      } else {
         return null;
      }
   }

   public static List<AutoCompleteConfig.Suggestion> findMatchingHuds(String prefix) {
      List<AutoCompleteConfig.Suggestion> result = new ArrayList();

      try {
         if (Mappet.huds == null) {
            return result;
         }

         String lower = prefix == null ? "" : prefix.toLowerCase();

         for(String id : Mappet.huds.getKeys()) {
            if (lower.isEmpty() || id.toLowerCase().startsWith(lower)) {
               result.add(new AutoCompleteConfig.Suggestion(id, "HUD", "hud"));
               if (result.size() >= AutoCompleteConfig.MAX_SUGGESTIONS) {
                  break;
               }
            }
         }
      } catch (Exception var5) {
      }

      result.sort(Comparator.comparing((a) -> a.methodName));
      return result;
   }

   public static String applyHUDCompletion(String line, int cursorOffset, String id, int[] newCursorOffset) {
      int safe = Math.max(0, Math.min(cursorOffset, line == null ? 0 : line.length()));
      if (line == null) {
         line = "";
      }

      String before = line.substring(0, safe);
      String tail = line.substring(safe);
      String[] patterns = new String[]{"setupHUD(", "changeHUDMorph(", "changeHUD("};
      int last = -1;
      String found = null;

      for(String p : patterns) {
         int idx = before.lastIndexOf(p);
         if (idx > last) {
            last = idx;
            found = p;
         }
      }

      if (last < 0) {
         if (newCursorOffset != null) {
            newCursorOffset[0] = safe;
         }

         return line;
      } else {
         int start = last + found.length();
         int quoteIdx = -1;
         char quoteChar = '"';

         for(int i = start; i < before.length(); ++i) {
            char c = before.charAt(i);
            if (c == '"' || c == '\'') {
               quoteIdx = i;
               quoteChar = c;
               break;
            }

            if (!Character.isWhitespace(c)) {
               break;
            }
         }

         String newLine;
         int newOffset;
         if (quoteIdx < 0) {
            newLine = before + '"' + id + '"' + tail;
            newOffset = before.length() + 1 + id.length();
         } else {
            String beforeQuote = line.substring(0, quoteIdx + 1);
            String afterQuote = line.substring(quoteIdx + 1);
            int end = afterQuote.length();

            for(int i = 0; i < afterQuote.length(); ++i) {
               char c = afterQuote.charAt(i);
               if (c == quoteChar || Character.isWhitespace(c) || c == ')' || c == ',') {
                  end = i;
                  break;
               }
            }

            newLine = beforeQuote + id + afterQuote.substring(end);
            newOffset = beforeQuote.length() + id.length();
         }

         if (newCursorOffset != null) {
            newCursorOffset[0] = Math.max(0, Math.min(newOffset, newLine.length()));
         }

         return newLine;
      }
   }

   public static String applyJavaTypeCompletion(String line, int cursorOffset, String className, int[] newCursorOffset) {
      int safe = Math.max(0, Math.min(cursorOffset, line == null ? 0 : line.length()));
      if (line == null) {
         line = "";
      }

      String before = line.substring(0, safe);
      line.substring(safe);
      int idxDq = before.lastIndexOf("Java.type(\"");
      int idxSq = before.lastIndexOf("Java.type('");
      int idx;
      char quoteChar;
      if (idxDq >= idxSq) {
         idx = idxDq;
         quoteChar = '"';
      } else {
         idx = idxSq;
         quoteChar = '\'';
      }

      if (idx < 0) {
         if (newCursorOffset != null) {
            newCursorOffset[0] = safe;
         }

         return line;
      } else {
         int start = idx + 11;
         String beforeQuote = line.substring(0, start);
         String rest = line.substring(start);
         int end = rest.length();

         for(int i = 0; i < rest.length(); ++i) {
            char c = rest.charAt(i);
            if (c == quoteChar || c == ')') {
               end = i;
               break;
            }
         }

         String newLine = beforeQuote + className + rest.substring(end);
         int newOffset = beforeQuote.length() + className.length();
         if (newCursorOffset != null) {
            newCursorOffset[0] = Math.max(0, Math.min(newOffset, newLine.length()));
         }

         return newLine;
      }
   }

   public static String applyIconCompletion(String line, int cursorOffset, String iconId, int[] newCursorOffset) {
      int safe = Math.max(0, Math.min(cursorOffset, line == null ? 0 : line.length()));
      if (line == null) {
         line = "";
      }

      String before = line.substring(0, safe);
      String tail = line.substring(safe);
      int iconCall = before.lastIndexOf("icon(");
      int contextCall = before.lastIndexOf("context(");
      int lastParen = Math.max(iconCall, contextCall);
      String call = contextCall > iconCall ? "context(" : "icon(";
      if (lastParen < 0) {
         if (newCursorOffset != null) {
            newCursorOffset[0] = Math.max(0, Math.min(safe, line.length()));
         }

         return line;
      } else {
         int start = lastParen + call.length();
         int quoteIdx = -1;
         char quoteChar = '"';

         for(int i = start; i < before.length(); ++i) {
            char c = before.charAt(i);
            if (c == '"' || c == '\'') {
               quoteIdx = i;
               quoteChar = c;
               break;
            }
         }

         String newLine;
         int newOffset;
         if (quoteIdx < 0) {
            newLine = before + "\"" + iconId + "\"" + tail;
            newOffset = before.length() + 1 + iconId.length();
         } else {
            String beforeQuote = line.substring(0, quoteIdx + 1);
            String afterQuote = line.substring(quoteIdx + 1);
            int end = afterQuote.length();

            for(int i = 0; i < afterQuote.length(); ++i) {
               char c = afterQuote.charAt(i);
               if (c == quoteChar || Character.isWhitespace(c) || c == ')' || c == ',') {
                  end = i;
                  break;
               }
            }

            newLine = beforeQuote + iconId + afterQuote.substring(end);
            newOffset = beforeQuote.length() + iconId.length();
         }

         if (newCursorOffset != null) {
            newCursorOffset[0] = Math.max(0, Math.min(newOffset, newLine.length()));
         }

         return newLine;
      }
   }

   public static String[] extractContext(String line, int cursorOffset) {
      return CompletionHelper.extractContext(line, cursorOffset);
   }

   public static String applyCompletion(String line, int cursorOffset, String completion, boolean isVariable, int[] newCursorOffset) {
      return CompletionHelper.applyCompletion(line, cursorOffset, completion, isVariable, newCursorOffset);
   }

   public static String applyCompletion(String line, int cursorOffset, String selectedMethod, int[] newCursorOffset) {
      return CompletionHelper.applyCompletion(line, cursorOffset, selectedMethod, false, newCursorOffset);
   }

   public static String applyCompletionKeyword(String line, int cursorOffset, String keyword, String icon, int[] newCursorOffset) {
      return CompletionHelper.applyCompletionKeyword(line, cursorOffset, keyword, icon, newCursorOffset);
   }

   public static String applyCompletionRaw(String line, int cursorOffset, String completion, int[] newCursorOffset) {
      return CompletionHelper.applyCompletionRaw(line, cursorOffset, completion, newCursorOffset);
   }

   public static String extractMethodPrefix(String line, int cursorOffset) {
      return CompletionHelper.extractMethodPrefix(line, cursorOffset);
   }

   public static List<String> getDocForSuggestion(AutoCompleteConfig.Suggestion s) {
      return DocResolver.getDocForSuggestion(s);
   }

   public static GuiScrollElement buildDocPanel(class_310 mc, AutoCompleteConfig.Suggestion s) {
      return DocResolver.buildDocPanel(mc, s);
   }

   public static void populateDocPanel(class_310 mc, AutoCompleteConfig.Suggestion s, GuiScrollElement panel) {
      DocResolver.populateDocPanel(mc, s, panel);
   }

   public static String stripColors(String s) {
      return CompletionHelper.stripColors(s);
   }

   public static String joinLines(List<String> lines) {
      return ScopeAnalyzer.joinLines(lines);
   }

   static {
      KNOWN_VAR_TYPES.put("c", "IScriptEvent");
      KNOWN_VAR_TYPES.put("mappet", "IScriptFactory");
      KNOWN_VAR_TYPES.put("event", "IScriptEvent");
      KNOWN_VAR_TYPES.put("factory", "IScriptFactory");
      ALIAS_METHODS = new LinkedHashMap();
      ALIAS_METHODS.put("subject", "getSubject");
      ALIAS_METHODS.put("object", "getObject");
      ALIAS_METHODS.put("entity", "getEntity");
      ALIAS_METHODS.put("player", "getPlayer");
      ALIAS_METHODS.put("npc", "getNPC");
      ALIAS_METHODS.put("world", "getWorld");
      ALIAS_METHODS.put("server", "getServer");
      ALIAS_METHODS.put("values", "getValues");
      ALIAS_METHODS.put("position", "getPosition");
      ALIAS_METHODS.put("motion", "getMotion");
      ALIAS_METHODS.put("rotations", "getRotations");
      ALIAS_METHODS.put("look", "getLook");
      ALIAS_METHODS.put("hp", "getHp");
      ALIAS_METHODS.put("maxHp", "getMaxHp");
      ALIAS_METHODS.put("name", "getName");
      ALIAS_METHODS.put("inventory", "getInventory");
      ALIAS_METHODS.put("states", "getStates");
      ALIAS_METHODS.put("morph", "getMorph");
      ALIAS_METHODS.put("mount", "getMount");
      ALIAS_METHODS.put("target", "getTarget");
      ALIAS_METHODS.put("fullData", "getFullData");
      ALIAS_METHODS.put("entityData", "getEntityData");
      ALIAS_METHODS.put("mainItem", "getMainItem");
      ALIAS_METHODS.put("offItem", "getOffItem");
      ALIAS_METHODS.put("quests", "getQuests");
      ALIAS_METHODS.put("context", "getContext");
      ALIAS_METHODS.put("data", "getData");
      ALIAS_METHODS.put("allHud", "getAllHud");
      ALIAS_METHODS.put("hotbar", "Hotbar");
      ALIAS_METHODS.put("health", "Health");
      ALIAS_METHODS.put("hunger", "Hunger");
      ALIAS_METHODS.put("experience", "Experience");
      ALIAS_METHODS.put("crosshair", "Crosshair");
      ALIAS_METHODS.put("statusEffects", "StatusEffects");
      ALIAS_METHODS.put("mountHealth", "MountHealth");
      ALIAS_METHODS.put("vignette", "Vignette");
      ALIAS_METHODS.put("spyglass", "Spyglass");
      ALIAS_METHODS.put("itemTooltip", "ItemTooltip");
      ALIAS_METHODS.put("item", "getItem");
      ALIAS_METHODS.put("uiContext", "getUIContext");
      ALIAS_METHODS.put("dimension", "getDimension");
      ALIAS_METHODS.put("hunger", "getHunger");
      ALIAS_METHODS.put("xpLevel", "getXpLevel");
      ALIAS_METHODS.put("hotbarIndex", "getHotbarIndex");
      iconCache = null;
      BUILTIN_VALUE_KEYS = new String[]{"block", "x", "y", "z", "meta", "item", "hand", "button", "buttonState", "DWhell", "keyCode", "keyState", "falling", "fromDim", "toDim", "distance", "damageMultiplier", "gui", "entityItem", "duration", "strength", "ratioX", "ratioZ", "slot", "previous", "entityItem", "inventory", "damageType", "message", "entity", "key", "current", "pos", "projectile", "thrower", "damage"};
      KEY_BINDING_ACTIONS = new String[]{"key_key.attack", "key_key.use", "key_key.forward", "key_key.left", "key_key.back", "key_key.right", "key_key.jump", "key_key.sneak", "key_key.sprint", "key_key.drop", "key_key.inventory", "key_key.chat", "key_key.playerlist", "key_key.pickItem", "key_key.command", "key_key.socialInteractions", "key_key.screenshot", "key_key.togglePerspective", "key_key.smoothCamera", "key_key.fullscreen", "key_key.spectatorOutlines", "key_key.swapOffhand", "key_key.saveToolbarActivator", "key_key.loadToolbarActivator", "key_key.advancements", "key_key.hotbar.1", "key_key.hotbar.2", "key_key.hotbar.3", "key_key.hotbar.4", "key_key.hotbar.5", "key_key.hotbar.6", "key_key.hotbar.7", "key_key.hotbar.8", "key_key.hotbar.9", "key_key.metamorph.action", "key_key.metamorph.creative_menu", "key_key.metamorph.selector_menu", "key_key.metamorph.survival_menu", "key_key.metamorph.demorph", "key_mclib.dashboard", "key_key.blockbuster.plause_director", "key_key.blockbuster.record_director", "key_key.blockbuster.pause_director", "key_key.blockbuster.open_gun", "key_key.blockbuster.zoom", "key_key.blockbuster.gun_reload", "key_key.blockbuster.gun_shoot", "key_key.blockbuster.screenshot_transparent", "key_key.blockbuster.capture", "key_key.aperture.profile.toggle", "key_key.aperture.profile.playback", "key_key.aperture.profile.point", "key_key.aperture.roll.add", "key_key.aperture.roll.reduce", "key_key.aperture.roll.reset", "key_key.aperture.fov.add", "key_key.aperture.fov.reduce", "key_key.aperture.fov.reset", "key_key.aperture.control.stepUp", "key_key.aperture.control.stepDown", "key_key.aperture.control.stepLeft", "key_key.aperture.control.stepRight", "key_key.aperture.control.stepFront", "key_key.aperture.control.stepBack", "key_key.aperture.control.rotateUp", "key_key.aperture.control.rotateDown", "key_key.aperture.control.rotateLeft", "key_key.aperture.control.rotateRight", "key_key.aperture.camera_editor", "key_key.aperture.smooth_camera", "key_mappet.keys.dashboard", "key_mappet.keys.journal", "key_mappet.keys.runCurrentScript", "key_mappet.keys.scripted_item", "key_key.modmenu.open_menu", "key_zoomify.key.zoom", "key_zoomify.key.zoom.secondary", "key_iris.keybind.reload", "key_iris.keybind.toggleShaders", "key_iris.keybind.shaderPackSelection", "key_iris.keybind.wireframe"};
      KEY_BINDING_KEYS = new String[]{"key.keyboard.unknown", "key.keyboard.space", "key.keyboard.apostrophe", "key.keyboard.comma", "key.keyboard.minus", "key.keyboard.period", "key.keyboard.slash", "key.keyboard.0", "key.keyboard.1", "key.keyboard.2", "key.keyboard.3", "key.keyboard.4", "key.keyboard.5", "key.keyboard.6", "key.keyboard.7", "key.keyboard.8", "key.keyboard.9", "key.keyboard.semicolon", "key.keyboard.equal", "key.keyboard.a", "key.keyboard.b", "key.keyboard.c", "key.keyboard.d", "key.keyboard.e", "key.keyboard.f", "key.keyboard.g", "key.keyboard.h", "key.keyboard.i", "key.keyboard.j", "key.keyboard.k", "key.keyboard.l", "key.keyboard.m", "key.keyboard.n", "key.keyboard.o", "key.keyboard.p", "key.keyboard.q", "key.keyboard.r", "key.keyboard.s", "key.keyboard.t", "key.keyboard.u", "key.keyboard.v", "key.keyboard.w", "key.keyboard.x", "key.keyboard.y", "key.keyboard.z", "key.keyboard.left.bracket", "key.keyboard.backslash", "key.keyboard.right.bracket", "key.keyboard.grave.accent", "key.keyboard.world.1", "key.keyboard.world.2", "key.keyboard.escape", "key.keyboard.enter", "key.keyboard.tab", "key.keyboard.backspace", "key.keyboard.insert", "key.keyboard.delete", "key.keyboard.right", "key.keyboard.left", "key.keyboard.down", "key.keyboard.up", "key.keyboard.page.up", "key.keyboard.page.down", "key.keyboard.home", "key.keyboard.end", "key.keyboard.caps.lock", "key.keyboard.scroll.lock", "key.keyboard.num.lock", "key.keyboard.print.screen", "key.keyboard.pause", "key.keyboard.f1", "key.keyboard.f2", "key.keyboard.f3", "key.keyboard.f4", "key.keyboard.f5", "key.keyboard.f6", "key.keyboard.f7", "key.keyboard.f8", "key.keyboard.f9", "key.keyboard.f10", "key.keyboard.f11", "key.keyboard.f12", "key.keyboard.f13", "key.keyboard.f14", "key.keyboard.f15", "key.keyboard.f16", "key.keyboard.f17", "key.keyboard.f18", "key.keyboard.f19", "key.keyboard.f20", "key.keyboard.f21", "key.keyboard.f22", "key.keyboard.f23", "key.keyboard.f24", "key.keyboard.f25", "key.keyboard.kp.0", "key.keyboard.kp.1", "key.keyboard.kp.2", "key.keyboard.kp.3", "key.keyboard.kp.4", "key.keyboard.kp.5", "key.keyboard.kp.6", "key.keyboard.kp.7", "key.keyboard.kp.8", "key.keyboard.kp.9", "key.keyboard.kp.decimal", "key.keyboard.kp.divide", "key.keyboard.kp.multiply", "key.keyboard.kp.subtract", "key.keyboard.kp.add", "key.keyboard.kp.enter", "key.keyboard.kp.equal", "key.keyboard.left.shift", "key.keyboard.left.control", "key.keyboard.left.alt", "key.keyboard.left.win", "key.keyboard.right.shift", "key.keyboard.right.control", "key.keyboard.right.alt", "key.keyboard.right.win", "key.keyboard.menu", "key.mouse.left", "key.mouse.right", "key.mouse.middle", "key.mouse.4", "key.mouse.5", "key.mouse.6", "key.mouse.7", "key.mouse.8"};
   }

   private interface Icon {
   }
}
