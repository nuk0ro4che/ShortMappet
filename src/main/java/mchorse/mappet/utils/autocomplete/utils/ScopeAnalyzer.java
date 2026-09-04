package mchorse.mappet.utils.autocomplete.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mchorse.mappet.utils.autocomplete.AutoCompleteConfig;

public class ScopeAnalyzer {
   private static final Pattern VAR_DECL = Pattern.compile("(?:var|let|const)\\s+(\\w+)\\s*=(?!=)", 8);
   private static final Pattern FUNC_DECL = Pattern.compile("function\\s+(\\w+)\\s*\\(([^)]*?)\\)", 8);
   private static final Pattern SWITCH_OPEN = Pattern.compile("switch\\s*\\([^)]*\\)\\s*\\{");
   private static final Pattern LIB_FUNC_GLOBAL = Pattern.compile("function\\s+(\\w+)\\s*\\(([^)]*?)\\)", 8);
   private static final Pattern LIB_FUNC_VAR = Pattern.compile("(?:var|let|const)\\s+(\\w+)\\s*=\\s*function\\s*\\(([^)]*?)\\)", 8);
   private static final Pattern LIB_FUNC_EXPORT = Pattern.compile("(?:exports|module\\.exports)\\.(\\w+)\\s*=\\s*function\\s*\\(([^)]*?)\\)", 8);
   private static final Pattern LIB_FUNC_PROP = Pattern.compile("(?<!function\\s)(\\w+)\\.(\\w+)\\s*=\\s*function\\s*\\(([^)]*?)\\)", 8);
   
   private static final Pattern LIB_VAR = Pattern.compile("(?:var|let|const)\\s+([A-Za-z_$][\\w$]*)\\s*=", 8);
   private static final Map<String, String> KNOWN_PARAM_TYPES = new LinkedHashMap();

   public static List<AutoCompleteConfig.Suggestion> findMatchingInScope(String prefix, List<String> allLines) {
      return findMatchingInScope(prefix, allLines, false);
   }

   public static List<AutoCompleteConfig.Suggestion> findMatchingInScope(String prefix, List<String> allLines, boolean insideSwitch) {
      if (allLines != null && !allLines.isEmpty()) {
         String lower = prefix.toLowerCase();
         String fullText = joinLines(allLines);
         LinkedHashMap<String, AutoCompleteConfig.Suggestion> map = new LinkedHashMap();
         Matcher mVar = VAR_DECL.matcher(fullText);

         while(mVar.find()) {
            String varName = mVar.group(1);
            if (varName != null && !varName.isEmpty() && (prefix.isEmpty() || varName.toLowerCase().startsWith(lower)) && !map.containsKey(varName)) {
               String varType = resolveVarType(varName, fullText);
               String icon = resolveVarIcon(varName, fullText);
               map.put(varName, new AutoCompleteConfig.Suggestion(varName, varType, icon));
            }
         }

         Matcher mFuncParams = FUNC_DECL.matcher(fullText);

         while(mFuncParams.find()) {
            String rawParams = mFuncParams.group(2).trim();
            if (!rawParams.isEmpty()) {
               for(String param : rawParams.split(",")) {
                  String p = param.trim();
                  if (!p.isEmpty() && (prefix.isEmpty() || p.toLowerCase().startsWith(lower)) && !map.containsKey(p)) {
                     String knownType = (String)KNOWN_PARAM_TYPES.get(p);
                     if (knownType != null) {
                        map.put(p, new AutoCompleteConfig.Suggestion(p, knownType, "var"));
                     } else {
                        map.put(p, new AutoCompleteConfig.Suggestion(p, "param", "var"));
                     }
                  }
               }
            }
         }

         Matcher mFunc = FUNC_DECL.matcher(fullText);

         while(mFunc.find()) {
            String funcName = mFunc.group(1);
            String funcParams = mFunc.group(2).trim();
            if (funcName != null && !funcName.isEmpty() && (prefix.isEmpty() || funcName.toLowerCase().startsWith(lower)) && !map.containsKey(funcName)) {
               map.put(funcName, new AutoCompleteConfig.Suggestion(funcName, "(" + funcParams + ")", "fn"));
            }
         }

         collectLibraryFunctions(prefix, lower, map);
         if (insideSwitch) {
            String caseKw = "case";
            if ((prefix == null || prefix.isEmpty() || caseKw.startsWith(lower)) && !map.containsKey(caseKw)) {
               map.put(caseKw, new AutoCompleteConfig.Suggestion(caseKw, "", "kw"));
            }
         }

         for(String kw : AutoCompleteConfig.JS_KEYWORDS) {
            if ((prefix.isEmpty() || kw.toLowerCase().startsWith(lower)) && !map.containsKey(kw)) {
               map.put(kw, new AutoCompleteConfig.Suggestion(kw, "", "kw"));
            }
         }

         List<AutoCompleteConfig.Suggestion> result = new ArrayList(map.values());
         result.sort((a, b) -> {
            int pa = priority(a.className);
            int pb = priority(b.className);
            return pa != pb ? pa - pb : a.methodName.compareToIgnoreCase(b.methodName);
         });
         return result;
      } else {
         return new ArrayList();
      }
   }

   


   public static Set<String> getCurrentLibraryFunctionNames() {
      Set<String> names = new HashSet();

      try {
         Set<String> libraries = new HashSet();
         List<String> attached = getCurrentScriptLibraries();
         if (attached != null) {
            libraries.addAll(attached);
         }

         libraries.addAll(getGlobalLibraryNames());

         for(String library : libraries) {
            String code = readLibraryCode(library);
            if (code != null && !code.isEmpty()) {
               LinkedHashMap<String, AutoCompleteConfig.Suggestion> functions = new LinkedHashMap();
               parseLibFunctions(code, "", "", functions);
               names.addAll(functions.keySet());
            }
         }
      } catch (Exception ignored) {
      }

      return names;
   }

   
   public static Set<String> getLibraryFunctionNames(List<String> libraryCodes) {
      Set<String> names = new HashSet();
      if (libraryCodes == null) {
         return names;
      }

      for(String code : libraryCodes) {
         if (code != null && !code.isEmpty()) {
            LinkedHashMap<String, AutoCompleteConfig.Suggestion> functions = new LinkedHashMap();
            parseLibFunctions(code, "", "", functions);
            names.addAll(functions.keySet());
         }
      }

      return names;
   }

   private static void collectLibraryFunctions(String prefix, String lower, LinkedHashMap<String, AutoCompleteConfig.Suggestion> map) {
      try {
         List<String> libraryNames = getCurrentScriptLibraries();
         if (libraryNames == null || libraryNames.isEmpty()) {
            return;
         }

         for(String libName : libraryNames) {
            String libCode = readLibraryCode(libName);
            if (libCode != null && !libCode.isEmpty()) {
               parseLibFunctions(libCode, prefix, lower, map);
            }
         }
      } catch (Exception var7) {
      }

   }

   private static List<String> getCurrentScriptLibraries() {
      try {
         Class<?> dashboardClass = Class.forName("mchorse.mappet.client.gui.GuiMappetDashboard");
         Field dashField = dashboardClass.getDeclaredField("dashboard");
         dashField.setAccessible(true);
         Object dashboard = dashField.get((Object)null);
         if (dashboard == null) {
            return null;
         }

         Field scriptPanelField = dashboardClass.getDeclaredField("script");
         scriptPanelField.setAccessible(true);
         Object scriptPanel = scriptPanelField.get(dashboard);
         if (scriptPanel == null) {
            return null;
         }

         Field dataField = findField(scriptPanel.getClass(), "data");
         if (dataField == null) {
            return null;
         }

         dataField.setAccessible(true);
         Object scriptObj = dataField.get(scriptPanel);
         if (scriptObj == null) {
            return null;
         }

         Field libField = scriptObj.getClass().getDeclaredField("libraries");
         libField.setAccessible(true);
         Object libs = libField.get(scriptObj);
         if (libs instanceof List) {
            return (List)libs;
         }
      } catch (Exception var9) {
      }

      return null;
   }

   private static Set<String> getGlobalLibraryNames() {
      Set<String> names = new HashSet();

      try {
         Class<?> mappetClass = Class.forName("mchorse.mappet.Mappet");
         Field scriptsField = mappetClass.getDeclaredField("scripts");
         scriptsField.setAccessible(true);
         Object scriptManager = scriptsField.get((Object)null);
         if (scriptManager == null) {
            return names;
         }

         Field librariesField = scriptManager.getClass().getDeclaredField("globalLibraries");
         librariesField.setAccessible(true);
         Object libraries = librariesField.get(scriptManager);
         if (libraries instanceof Map) {
            for(Object name : ((Map)libraries).keySet()) {
               if (name != null) {
                  names.add(String.valueOf(name));
               }
            }
         }
      } catch (Exception ignored) {
      }

      return names;
   }

   private static String readLibraryCode(String libName) {
      try {
         Class<?> mappetClass = Class.forName("mchorse.mappet.Mappet");
         Field scriptsField = mappetClass.getDeclaredField("scripts");
         scriptsField.setAccessible(true);
         Object scriptManager = scriptsField.get((Object)null);
         if (scriptManager == null) {
            return null;
         } else {
            Method getFile = scriptManager.getClass().getDeclaredMethod("getScriptFile", String.class);
            getFile.setAccessible(true);
            File scriptFile = (File)getFile.invoke(scriptManager, libName);
            if (scriptFile != null && scriptFile.exists()) {
               byte[] bytes = Files.readAllBytes(scriptFile.toPath());
               return new String(bytes, StandardCharsets.UTF_8);
            } else {
               return null;
            }
         }
      } catch (Exception var7) {
         return null;
      }
   }

   private static void parseLibFunctions(String code, String prefix, String lower, LinkedHashMap<String, AutoCompleteConfig.Suggestion> map) {
      Matcher m1 = LIB_FUNC_GLOBAL.matcher(code);

      while(m1.find()) {
         addLibFunc(m1.group(1), m1.group(2).trim(), prefix, lower, map);
      }

      Matcher m2 = LIB_FUNC_VAR.matcher(code);

      while(m2.find()) {
         addLibFunc(m2.group(1), m2.group(2).trim(), prefix, lower, map);
      }

      Matcher m3 = LIB_FUNC_EXPORT.matcher(code);

      while(m3.find()) {
         addLibFunc(m3.group(1), m3.group(2).trim(), prefix, lower, map);
      }

      Matcher m4 = LIB_FUNC_PROP.matcher(code);

      while(m4.find()) {
         addLibFunc(m4.group(2), m4.group(3).trim(), prefix, lower, map);
      }

      Matcher variables = LIB_VAR.matcher(code);
      while (variables.find()) {
         addLibVariable(variables.group(1), prefix, lower, map);
      }

   }

   private static void addLibVariable(String name, String prefix, String lower, LinkedHashMap<String, AutoCompleteConfig.Suggestion> map) {
      if (name != null && !name.isEmpty() && (prefix.isEmpty() || name.toLowerCase().startsWith(lower)) && !map.containsKey(name)) {
         map.put(name, new AutoCompleteConfig.Suggestion(name, "", "var"));
      }
   }

   private static void addLibFunc(String name, String params, String prefix, String lower, LinkedHashMap<String, AutoCompleteConfig.Suggestion> map) {
      if (name != null && !name.isEmpty()) {
         if (prefix.isEmpty() || name.toLowerCase().startsWith(lower)) {
            if (!map.containsKey(name)) {
               map.put(name, new AutoCompleteConfig.Suggestion(name, "(" + params + ")", "lib"));
            }

         }
      }
   }

   private static int priority(String icon) {
      if (icon == null) {
         return 4;
      } else {
         switch (icon) {
            case "var":
            case "[]":
            case "{}":
               return 0;
            case "fn":
               return 1;
            case "lib":
               return 2;
            case "kw":
               return 3;
            default:
               return 4;
         }
      }
   }

   private static String resolveVarType(String varName, String fullText) {
      return resolveVarType(varName, fullText, new HashSet());
   }

   private static String resolveVarType(String varName, String fullText, Set<String> visited) {
      if (visited.contains(varName)) {
         return "";
      } else {
         visited.add(varName);
         String known = (String)KNOWN_PARAM_TYPES.get(varName);
         if (known != null) {
            return known;
         } else {
            Pattern p = Pattern.compile("(?:var|let|const)\\s+" + Pattern.quote(varName) + "\\s*=(?!=)\\s*(\\w+)\\.(\\w+)\\s*\\(", 8);
            Matcher m = p.matcher(fullText);
            if (m.find()) {
               String ownerVar = m.group(1);
               String methodName = m.group(2);
               String rt = DocResolver.findReturnTypeByMethodName(methodName);
               if (rt != null && !rt.isEmpty()) {
                  return rt;
               }

               String ownerType = (String)KNOWN_PARAM_TYPES.get(ownerVar);
               if (ownerType == null) {
                  ownerType = resolveVarType(ownerVar, fullText, visited);
               }

               if (ownerType != null && !ownerType.isEmpty()) {
                  rt = DocResolver.findReturnTypeByMethodInClass(ownerType, methodName);
                  if (rt != null && !rt.isEmpty()) {
                     return rt;
                  }
               }
            }

            Pattern p2 = Pattern.compile("(?:var|let|const)\\s+" + Pattern.quote(varName) + "\\s*=(?!=)\\s*(\\w+)\\.(\\w+)\\s*(?:[^(]|$)", 8);
            Matcher m2 = p2.matcher(fullText);
            if (m2.find()) {
               String ownerVar = m2.group(1);
               String prop = m2.group(2);
               String ownerType = (String)KNOWN_PARAM_TYPES.get(ownerVar);
               if (ownerType == null) {
                  ownerType = resolveVarType(ownerVar, fullText, visited);
               }

               if (ownerType != null && !ownerType.isEmpty()) {
                  String rt = DocResolver.findReturnTypeByMethodInClass(ownerType, prop);
                  if (rt == null || rt.isEmpty()) {
                     String getter = "get" + Character.toUpperCase(prop.charAt(0)) + prop.substring(1);
                     rt = DocResolver.findReturnTypeByMethodInClass(ownerType, getter);
                  }

                  if (rt != null && !rt.isEmpty()) {
                     return rt;
                  }
               }

               String rt = DocResolver.findReturnTypeByMethodName(prop);
               if (rt != null && !rt.isEmpty()) {
                  return rt;
               }
            }

            if (isArrayDecl(varName, fullText)) {
               return "Array";
            } else {
               return isObjectDecl(varName, fullText) ? "Object" : "";
            }
         }
      }
   }

   private static String resolveVarIcon(String varName, String fullText) {
      if (isArrayDecl(varName, fullText)) {
         return "[]";
      } else {
         return isObjectDecl(varName, fullText) ? "{}" : "var";
      }
   }

   private static boolean isArrayDecl(String varName, String fullText) {
      return Pattern.compile("(?:var|let|const)\\s+" + Pattern.quote(varName) + "\\s*=\\s*\\[", 8).matcher(fullText).find();
   }

   private static boolean isObjectDecl(String varName, String fullText) {
      return Pattern.compile("(?:var|let|const)\\s+" + Pattern.quote(varName) + "\\s*=\\s*\\{", 8).matcher(fullText).find();
   }

   public static boolean isInsideSwitch(List<String> allLines, int cursorLineIdx) {
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i <= cursorLineIdx; ++i) {
         sb.append(allLines.get(i) != null ? (String)allLines.get(i) : "").append("\n");
      }

      String text = sb.toString();
      Matcher m = SWITCH_OPEN.matcher(text);

      int lastMatchEnd;
      for(lastMatchEnd = -1; m.find(); lastMatchEnd = m.end()) {
      }

      if (lastMatchEnd == -1) {
         return false;
      } else {
         String after = text.substring(lastMatchEnd);
         int depth = 1;
         boolean inString = false;
         char strChar = 0;

         for(int i = 0; i < after.length(); ++i) {
            char ch = after.charAt(i);
            if (inString) {
               if (ch == strChar && (i == 0 || after.charAt(i - 1) != '\\')) {
                  inString = false;
               }
            } else if (ch != '"' && ch != '\'') {
               if (ch == '{') {
                  ++depth;
               } else if (ch == '}') {
                  --depth;
                  if (depth == 0) {
                     return false;
                  }
               }
            } else {
               inString = true;
               strChar = ch;
            }
         }

         return depth == 1;
      }
   }

   public static String joinLines(List<String> lines) {
      StringBuilder sb = new StringBuilder();

      for(String l : lines) {
         sb.append(l != null ? l : "").append("\n");
      }

      return sb.toString();
   }

   private static Field findField(Class<?> c, String name) {
      while(c != null) {
         try {
            return c.getDeclaredField(name);
         } catch (NoSuchFieldException var3) {
            c = c.getSuperclass();
         }
      }

      return null;
   }

   static {
      KNOWN_PARAM_TYPES.put("c", "IScriptEvent");
      KNOWN_PARAM_TYPES.put("event", "IScriptEvent");
      KNOWN_PARAM_TYPES.put("mappet", "IScriptFactory");
      KNOWN_PARAM_TYPES.put("handler", "handler");
   }
}
