package mchorse.mappet.client.gui.scripts.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import mchorse.mappet.api.scripts.user.IScriptEvent;
import mchorse.mappet.api.scripts.user.IScriptRayTrace;
import mchorse.mappet.api.scripts.user.IScriptServer;
import mchorse.mappet.api.scripts.user.IScriptWorld;
import mchorse.mappet.api.scripts.user.client.ISimpleVoiceChat;
import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import mchorse.mappet.api.scripts.user.entities.IScriptNpc;
import mchorse.mappet.api.scripts.user.entities.IScriptPlayer;
import mchorse.mappet.api.scripts.user.items.IScriptItemStack;
import mchorse.mappet.api.scripts.user.mappet.IMappetStates;
import mchorse.mappet.api.scripts.user.IScriptFactory;
import mchorse.mappet.api.scripts.user.sounds.IScriptManagedSound;
import mchorse.mappet.client.gui.scripts.GuiTextEditor.SourceDiagnostic;
import mchorse.mappet.utils.autocomplete.AutoCompleteConfig;
import mchorse.mappet.utils.autocomplete.AutoCompleteEngine;
import mchorse.mappet.utils.autocomplete.utils.DocResolver;
import mchorse.mappet.utils.autocomplete.utils.ScopeAnalyzer;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;





public class JavaScriptDiagnostics {
   private static final Pattern MEMBER_ACCESS = Pattern.compile("\\b([A-Za-z_$][\\w$]*)\\s*\\.\\s*([A-Za-z_$][\\w$]*)");
   private static final Pattern IDENTIFIER = Pattern.compile("\\b[A-Za-z_$][\\w$]*\\b");
   private static final Pattern VARIABLE_DECLARATION = Pattern.compile("\\b(?:var|let|const)\\s+([A-Za-z_$][\\w$]*)");
   private static final Pattern ASSIGNMENT_DECLARATION = Pattern.compile("(?m)(?<![=!<>])\\b([A-Za-z_$][\\w$]*)\\s*=(?!=)");
   private static final Pattern FUNCTION_DECLARATION = Pattern.compile("\\bfunction\\s+([A-Za-z_$][\\w$]*)\\s*\\(([^)]*)\\)");
   private static final Pattern FUNCTION_PARAMETERS = Pattern.compile("\\bfunction\\s*(?:[A-Za-z_$][\\w$]*)?\\s*\\(([^)]*)\\)");
   private static final Pattern FOR_IN_DECLARATION = Pattern.compile("\\bfor\\s*\\(\\s*(?:(?:var|let|const)\\s+)?([A-Za-z_$][\\w$]*)\\s+(?:in|of)\\b");
   private static final Pattern MAPPET_IMPORT = Pattern.compile("(?m)^[ \\t]*import[ \\t]+(?:([\\\"'])([^\\\"']+)\\1|([^;\\s]+))[ \\t]*;?[ \\t]*(?=\\r?$)");
   


   private static final Pattern MALFORMED_VARIABLE_DECLARATION = Pattern.compile("\\b(?:var|let|const)\\s+([A-Za-z_$][\\w$]*)(?=\\s+(?!(?:in|of)\\b)[A-Za-z_$][\\w$]*)");
   private static final Pattern UI_COMPONENT_ASSIGNMENT = Pattern.compile("\\b([A-Za-z_$][\\w$]*)\\s*=\\s*layout\\s*\\.\\s*(?:getCurrent|graphics|button|icon|label|text|textbox|textarea|toggle|trackpad|stringList|item|morph|click)\\s*\\(");
   private static final Pattern UI_BUILDER_ASSIGNMENT = Pattern.compile("\\b([A-Za-z_$][\\w$]*)\\s*=\\s*mappet\\s*\\.\\s*createUI\\s*\\(");
   private static final Pattern UI_CONTEXT_ASSIGNMENT = Pattern.compile("\\b([A-Za-z_$][\\w$]*)\\s*=\\s*[^;{}]*?\\.\\s*getUIContext\\s*\\(");
   


   private static final Pattern ASSIGNED_API_CHAIN = Pattern.compile("(?:\\b(?:var|let|const)\\s+)?([A-Za-z_$][\\w$]*)\\s*=\\s*([A-Za-z_$][\\w$]*(?:\\s*\\.\\s*[A-Za-z_$][\\w$]*\\s*\\([^;{}]*?\\))+)");
   private static final Map<String, Set<String>> API_MEMBER_CACHE = new HashMap();
   private static final Map<String, Set<String>> DIRECT_MAPPET_API_MEMBERS = createDirectMappetApiMembers();
   private static final Set<String> UI_COMPONENT_MEMBERS = new HashSet(Arrays.asList(
      "id", "tooltip", "visible", "enabled", "margin", "marginTop", "marginBottom", "marginLeft", "marginRight",
      "keybind", "context", "x", "y", "xy", "rx", "ry", "rxy", "w", "h", "wh", "rw", "rh", "rwh",
      "anchor", "anchorX", "anchorY", "labelAnchor", "rect", "background", "closable", "color", "alpha",
      "text", "label", "value", "state", "icon", "item", "morph", "tooltip", "enterHover", "exitHover",
      "scaleTo", "colorTo", "moveTo", "rotateTo"
   ));
   private static final Set<String> UI_CONTEXT_MEMBERS = new HashSet(Arrays.asList(
      "getLast", "getHotkey", "getContext", "getHovered", "getUnhovered", "getData", "get", "isClosed"
   ));
   private static final Set<String> UI_LAYOUT_MEMBERS = new HashSet(Arrays.asList(
      "getCurrent", "background", "notClosable", "closable", "paused", "create", "graphics", "button", "icon",
      "label", "text", "textbox", "textarea", "toggle", "trackpad", "stringList", "item", "morph", "click",
      "layout", "column", "row", "grid"
   ));
   private static final Set<String> VECTOR_COMPONENTS = new HashSet(Arrays.asList("x", "y", "z"));
   private static final Set<String> RAY_TRACE_MEMBERS = new HashSet(Arrays.asList(
      "getMinecraftRayTraceResult", "isMissed", "isBlock", "isEntity", "getEntity", "getBlock", "getHitPosition"
   ));
   private static final Set<String> COLLECTION_MEMBERS = new HashSet(Arrays.asList("length", "size"));
   private static final Set<String> JAVASCRIPT_GLOBALS = new HashSet(Arrays.asList(
      "e", "c", "event", "mappet", "factory", "context", "Java", "Math", "JSON", "console", "print", "printStackTrace",
      "Packages", "exports", "module", "require", "arguments", "Array", "Object", "String", "Number", "Boolean", "Date",
      "RegExp", "Error", "Promise", "setTimeout", "setInterval", "clearTimeout", "clearInterval", "Infinity", "NaN",
      
      "layout"
   ));

   public static List<SourceDiagnostic> diagnose(String source) {
      return createSnapshot(source, -1, (Set)null).getDiagnostics();
   }

   
   public static List<SourceDiagnostic> diagnose(String source, Set<String> libraryFunctions) {
      return createSnapshot(source, -1, libraryFunctions).getDiagnostics();
   }

   




   public static DiagnosticSnapshot createSnapshot(String source, int revision) {
      return createSnapshot(source, revision, (Set)null);
   }

   public static DiagnosticSnapshot createSnapshot(String source, int revision, Set<String> libraryFunctions) {
      String text = source == null ? "" : source;
      String diagnosticText = stripMappetImports(text);
      List<SourceDiagnostic> diagnostics = new ArrayList(diagnoseSyntax(diagnosticText));

      replaceMalformedDeclarationDiagnostics(diagnostics, diagnoseMalformedVariableDeclarations(diagnosticText));

      if (!diagnostics.isEmpty()) {
         addMissingRecoveryDiagnostics(diagnostics, RhinoRecoveryDiagnostics.diagnose(diagnosticText));
      }

      diagnostics.addAll(diagnoseWarnings(diagnosticText, libraryFunctions));
      return new DiagnosticSnapshot(revision, diagnostics);
   }

   private static String stripMappetImports(String source) {
      Matcher imports = MAPPET_IMPORT.matcher(source == null ? "" : source);
      return imports.replaceAll("");
   }

   
   private static void replaceMalformedDeclarationDiagnostics(List<SourceDiagnostic> diagnostics, List<SourceDiagnostic> replacements) {
      for(SourceDiagnostic replacement : replacements) {
         diagnostics.removeIf((diagnostic) -> !diagnostic.warning && diagnostic.line == replacement.line);
         diagnostics.add(replacement);
      }
   }

   private static List<SourceDiagnostic> diagnoseMalformedVariableDeclarations(String source) {
      String code = maskStringsAndComments(source);
      List<SourceDiagnostic> diagnostics = new ArrayList();
      int[] lineStarts = getLineStarts(code);
      Matcher declaration = MALFORMED_VARIABLE_DECLARATION.matcher(code);

      while(declaration.find()) {
         int[] position = offsetToLineColumn(lineStarts, declaration.start(1));
         diagnostics.add(new SourceDiagnostic(position[0], position[1], declaration.group(1).length(), "Ожидался знак = после объявления переменной", false));
      }

      return diagnostics;
   }

   
   private static void addMissingRecoveryDiagnostics(List<SourceDiagnostic> target, List<SourceDiagnostic> recovered) {
      for(SourceDiagnostic candidate : recovered) {
         boolean duplicate = false;

         for(SourceDiagnostic current : target) {
            if (!current.warning && current.line == candidate.line) {
               duplicate = true;
               break;
            }
         }

         if (!duplicate) {
            target.add(candidate);
         }
      }
   }

   public static class DiagnosticSnapshot {
      private final int revision;
      private final List<SourceDiagnostic> diagnostics;
      private final int status;

      public DiagnosticSnapshot(int revision, List<SourceDiagnostic> diagnostics) {
         this.revision = revision;
         this.diagnostics = Collections.unmodifiableList(new ArrayList(diagnostics));
         int currentStatus = 0;

         for(SourceDiagnostic diagnostic : this.diagnostics) {
            if (!diagnostic.warning) {
               currentStatus = 2;
               break;
            }

            currentStatus = 1;
         }

         this.status = currentStatus;
      }

      public int getRevision() {
         return this.revision;
      }

      public List<SourceDiagnostic> getDiagnostics() {
         return this.diagnostics;
      }

      public int getStatus() {
         return this.status;
      }
   }

   public static List<SourceDiagnostic> diagnoseSyntax(String source) {
      try {
         NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
         ScriptEngine engine = factory.getScriptEngine(new String[]{"--language=es6", "-scripting"});

         if (engine instanceof Compilable compilable) {
            compilable.compile(source);
         }
      } catch (ScriptException error) {
         int line = Math.max(0, error.getLineNumber() - 1);
         int column = Math.max(0, error.getColumnNumber() - 1);
         return single(line, column, 1, compactMessage(error.getMessage()), false);
      } catch (Exception error) {
         

      }

      return new ArrayList();
   }

   public static List<SourceDiagnostic> diagnoseWarnings(String source) {
      return diagnoseWarnings(source, (Set)null);
   }

   




   private static List<SourceDiagnostic> diagnoseWarnings(String source, Set<String> libraryFunctions) {
      String code = maskStringsAndComments(source);
      List<SourceDiagnostic> diagnostics = new ArrayList();
      Deque<Set<String>> scopes = new ArrayDeque();
      scopes.push(collectBaseKnownNames(code, libraryFunctions));
      Set<Integer> malformedDeclarations = collectMalformedDeclarationOffsets(code);
      Map<Integer, String> memberOwners = collectMemberOwners(code);
      Map<String, Set<String>> availableMembers = new HashMap();
      Map<String, String> ownerTypes = new HashMap();
      int[] lineStarts = getLineStarts(code);
      Matcher identifier = IDENTIFIER.matcher(code);
      int cursor = 0;
      String pendingDeclaration = null;

      while(identifier.find()) {
         advanceScopes(code, cursor, identifier.start(), scopes);
         cursor = identifier.end();
         String name = identifier.group();
         int start = identifier.start();

         if (isDeclarationKeyword(name)) {
            pendingDeclaration = name;
            continue;
         }

         if (pendingDeclaration != null) {
            if (!malformedDeclarations.contains(start)) {
               declareName(scopes, name, pendingDeclaration);
            }

            pendingDeclaration = null;
            continue;
         }

         if (isFunctionParameterName(code, start)) {
            continue;
         }

         if (isAssignmentTarget(code, identifier.end())) {
            


            declareName(scopes, name, "var");
            continue;
         }

         String owner = (String)memberOwners.get(start);
         if (owner != null) {
            if (isUiBuilderVariable(owner, code) && UI_LAYOUT_MEMBERS.contains(name)) {
               continue;
            }
            if (isVisible(scopes, owner)) {
               String type = (String)ownerTypes.get(owner);
               if (type == null) {
                  type = resolveOwnerType(owner, code);
                  ownerTypes.put(owner, type);
               }

               if (type != null && !type.isEmpty() && !isKnownMember(type, name, availableMembers)) {
                  int[] position = offsetToLineColumn(lineStarts, start);
                  diagnostics.add(new SourceDiagnostic(position[0], position[1], name.length(), "Неизвестное свойство или метод API: " + name, true));
               }
            }

            continue;
         }

         


         if (!isVisible(scopes, name) && !isPropertyAccess(code, start) && !isObjectKey(code, identifier.end())) {
            int[] position = offsetToLineColumn(lineStarts, start);
            diagnostics.add(new SourceDiagnostic(position[0], position[1], name.length(), "Неизвестная переменная или функция: " + name, true));
         }
      }

      return diagnostics;
   }

   private static Set<String> collectBaseKnownNames(String code, Set<String> libraryFunctions) {
      Set<String> known = new HashSet(JAVASCRIPT_GLOBALS);
      known.addAll(AutoCompleteConfig.JS_KEYWORDS);
      known.add("in");
      known.add("of");
      if (libraryFunctions == null) {
         known.addAll(ScopeAnalyzer.getCurrentLibraryFunctionNames());
      } else {
         known.addAll(libraryFunctions);
      }

      


      Matcher functions = FUNCTION_DECLARATION.matcher(code);
      while(functions.find()) {
         known.add(functions.group(1));
      }

      Matcher functionParameters = FUNCTION_PARAMETERS.matcher(code);
      while(functionParameters.find()) {
         String parameters = functionParameters.group(1).trim();
         if (!parameters.isEmpty()) {
            for(String parameter : parameters.split(",")) {
               String name = parameter.trim();
               if (name.matches("[A-Za-z_$][\\w$]*")) {
                  known.add(name);
               }
            }
         }
      }

      return known;
   }

   private static Set<Integer> collectMalformedDeclarationOffsets(String code) {
      Set<Integer> offsets = new HashSet();
      Matcher declaration = MALFORMED_VARIABLE_DECLARATION.matcher(code);
      while(declaration.find()) {
         offsets.add(declaration.start(1));
      }

      return offsets;
   }

   private static Map<Integer, String> collectMemberOwners(String code) {
      Map<Integer, String> owners = new HashMap();
      Matcher member = MEMBER_ACCESS.matcher(code);
      while(member.find()) {
         owners.put(member.start(2), member.group(1));
      }

      return owners;
   }

   private static void advanceScopes(String code, int from, int to, Deque<Set<String>> scopes) {
      for(int index = from; index < to; ++index) {
         char character = code.charAt(index);
         if (character == '{') {
            scopes.push(new HashSet());
         } else if (character == '}' && scopes.size() > 1) {
            scopes.pop();
         }
      }
   }

   private static boolean isDeclarationKeyword(String name) {
      return name.equals("var") || name.equals("let") || name.equals("const");
   }

   private static boolean isFunctionParameterName(String code, int start) {
      int index = start - 1;
      while(index >= 0 && Character.isWhitespace(code.charAt(index))) {
         --index;
      }

      return index >= 0 && (code.charAt(index) == '(' || code.charAt(index) == ',')
         && code.lastIndexOf("function", start) >= 0 && code.lastIndexOf("function", start) > code.lastIndexOf('{', start);
   }

   private static boolean isAssignmentTarget(String code, int end) {
      int index = end;
      while(index < code.length() && Character.isWhitespace(code.charAt(index))) {
         ++index;
      }

      return index < code.length() && code.charAt(index) == '='
         && (index + 1 >= code.length() || code.charAt(index + 1) != '=');
   }

   private static void declareName(Deque<Set<String>> scopes, String name, String declarationKind) {
      if (declarationKind.equals("var")) {
         scopes.peekLast().add(name);
      } else {
         scopes.peek().add(name);
      }
   }

   private static boolean isVisible(Deque<Set<String>> scopes, String name) {
      for(Set<String> scope : scopes) {
         if (scope.contains(name)) {
            return true;
         }
      }

      return false;
   }

   private static boolean isUiBuilderVariable(String owner, String source) {
      Matcher assignment = UI_BUILDER_ASSIGNMENT.matcher(source);
      while(assignment.find()) {
         if (owner.equals(assignment.group(1))) {
            return true;
         }
      }
      return owner.equals("layout");
   }

   private static String resolveOwnerType(String owner, String source) {
      if (owner.equals("e")) {
         return "IScriptEvent";
      }

      

      if (owner.equals("mappet")) {
         return "IScriptFactory";
      }

      Matcher uiContextAssignment = UI_CONTEXT_ASSIGNMENT.matcher(source);
      while(uiContextAssignment.find()) {
         if (owner.equals(uiContextAssignment.group(1))) {
            return "IMappetUIContext";
         }
      }

      


      Matcher uiBuilderAssignment = UI_BUILDER_ASSIGNMENT.matcher(source);
      while(uiBuilderAssignment.find()) {
         if (owner.equals(uiBuilderAssignment.group(1))) {
            return "IMappetUIBuilder";
         }
      }

      String assignedType = resolveAssignedApiChainType(owner, source);
      if (assignedType != null && !assignedType.isEmpty()) {
         return assignedType;
      }

      if (owner.equals("layout")) {
         return "IMappetUIBuilder";
      }

      Matcher uiAssignment = UI_COMPONENT_ASSIGNMENT.matcher(source);
      while(uiAssignment.find()) {
         if (owner.equals(uiAssignment.group(1))) {
            return "UIComponent";
         }
      }

      return AutoCompleteEngine.resolveChainType(owner, source);
   }

   private static String resolveAssignedApiChainType(String owner, String source) {
      return resolveAssignedApiChainType(owner, source, new HashSet());
   }

   private static String resolveAssignedApiChainType(String owner, String source, Set<String> resolving) {
      if (!resolving.add(owner)) {
         return null;
      }

      Matcher assignment = ASSIGNED_API_CHAIN.matcher(source);
      while(assignment.find()) {
         if (owner.equals(assignment.group(1))) {
            String chain = assignment.group(2).replaceAll("\\s+", "");
            String type = resolveApiChainType(chain, source, resolving);
            if (type != null && !type.isEmpty()) {
               return type;
            }
         }
      }

      return null;
   }

   private static String resolveApiChainType(String chain, String source, Set<String> resolving) {
      String direct = AutoCompleteEngine.resolveChainType(chain, source);
      if (direct != null && !direct.isEmpty()) {
         return direct;
      }

      int separator = chain.indexOf('.');
      if (separator <= 0 || separator >= chain.length() - 1) {
         return null;
      }

      String head = chain.substring(0, separator);
      String currentType = resolveAssignedApiChainType(head, source, resolving);
      if ((currentType == null || currentType.isEmpty()) && head.equals("layout")) {
         currentType = "IMappetUIBuilder";
      }
      if (currentType == null || currentType.isEmpty()) {
         return null;
      }

      String tail = chain.substring(separator + 1);
      while(!tail.isEmpty()) {
         int call = tail.indexOf('(');
         if (call <= 0) {
            return null;
         }

         String method = tail.substring(0, call);
         String next = DocResolver.findReturnTypeByMethodInClass(currentType, method);
         if (next == null || next.isEmpty()) {
            next = DocResolver.findReturnTypeByMethodName(method);
         }
        if (next == null || next.isEmpty()) {
            return null;
         }

         currentType = next;
         int depth = 0;
         int end = -1;
         for(int index = call; index < tail.length(); ++index) {
            char character = tail.charAt(index);
            if (character == '(') {
               ++depth;
            } else if (character == ')' && --depth == 0) {
               end = index;
               break;
            }
         }
         if (end < 0) {
            return null;
         }

         tail = end + 1 < tail.length() && tail.charAt(end + 1) == '.' ? tail.substring(end + 2) : tail.substring(end + 1);
      }

      return currentType;
   }

   private static boolean isKnownMember(String type, String property, Map<String, Set<String>> availableMembers) {
      if (isDirectMappetApiMember(type, property) || isKnownLayoutMember(type, property) || isKnownUiMember(type, property) || isKnownUiContextMember(type, property) || isKnownVectorComponent(type, property) || isKnownRayTraceMember(type, property) || isKnownCollectionMember(type, property)) {
         return true;
      }

      Set<String> members = (Set)availableMembers.get(type);

      if (members == null) {
         members = getApiMembers(type);
         availableMembers.put(type, members);
      }

      return members.isEmpty() || members.contains(property);
   }

   private static Map<String, Set<String>> createDirectMappetApiMembers() {
      Map<String, Set<String>> members = new HashMap();
      addDirectMappetApiMembers(members, IScriptFactory.class, IScriptEvent.class, IScriptEntity.class, IScriptPlayer.class, IScriptNpc.class, IScriptServer.class, IScriptWorld.class, IMappetStates.class, IScriptItemStack.class, IScriptRayTrace.class, ISimpleVoiceChat.class, IScriptManagedSound.class, ScriptVector.class);
      return members;
   }

   private static void addDirectMappetApiMembers(Map<String, Set<String>> target, Class<?>... types) {
      for(Class<?> type : types) {
         Set<String> methods = new HashSet();
         for(java.lang.reflect.Method method : type.getMethods()) {
            methods.add(method.getName());
         }

         target.put(type.getSimpleName(), methods);
         target.put(type.getName(), methods);
      }
   }

   private static boolean isDirectMappetApiMember(String type, String property) {
      if (type == null || property == null) {
         return false;
      }

      for(String singleType : type.split(",")) {
         Set<String> members = (Set)DIRECT_MAPPET_API_MEMBERS.get(singleType.trim());
         if (members != null && members.contains(property)) {
            return true;
         }
      }

      return false;
   }

   private static boolean isKnownUiContextMember(String type, String property) {
      String lower = type == null ? "" : type.toLowerCase();
      return (lower.contains("imappetuicontext") || lower.contains("uicontext")) && UI_CONTEXT_MEMBERS.contains(property);
   }

   private static boolean isKnownLayoutMember(String type, String property) {
      String lower = type == null ? "" : type.toLowerCase();
      return (lower.contains("imappetuibuilder") || lower.contains("uibuilder")) && UI_LAYOUT_MEMBERS.contains(property);
   }

   private static boolean isKnownUiMember(String type, String property) {
      String lower = type == null ? "" : type.toLowerCase();
      return (lower.contains("uicomponent") || lower.contains("uielement") || lower.contains("uiwidget") || lower.contains("ui") && lower.contains("component")) && UI_COMPONENT_MEMBERS.contains(property);
   }

   private static boolean isKnownVectorComponent(String type, String property) {
      String lower = type == null ? "" : type.toLowerCase();
      return (lower.contains("vector") || lower.contains("position")) && VECTOR_COMPONENTS.contains(property);
   }

   private static boolean isKnownRayTraceMember(String type, String property) {
      return type != null && type.toLowerCase().contains("raytrace") && RAY_TRACE_MEMBERS.contains(property);
   }

   private static boolean isKnownCollectionMember(String type, String property) {
      String lower = type == null ? "" : type.toLowerCase();
      return (lower.contains("list") || lower.contains("array") || lower.contains("collection") || lower.contains("set")) && COLLECTION_MEMBERS.contains(property);
   }

   private static Set<String> getApiMembers(String type) {
      Set<String> cached = (Set)API_MEMBER_CACHE.get(type);
      if (cached != null) {
         return cached;
      }

      Set<String> members = new HashSet();
      List<AutoCompleteConfig.Suggestion> methods = AutoCompleteEngine.findMethodsOfClass(type, "");

      for(AutoCompleteConfig.Suggestion method : methods) {
         members.add(method.methodName);
      }

      if (!members.isEmpty()) {
         for(String[] alias : AutoCompleteConfig.PROPERTY_ALIASES) {
            if (alias.length >= 2) {
               String returnType = DocResolver.findReturnTypeByMethodInClass(type, alias[1]);
               if (returnType != null && !returnType.isEmpty()) {
                  members.add(alias[0]);
               }
            }
         }
      }

      API_MEMBER_CACHE.put(type, members);
      return members;
   }

   private static boolean isPropertyAccess(String code, int start) {
      int index = start - 1;

      while(index >= 0 && Character.isWhitespace(code.charAt(index))) {
         --index;
      }

      return index >= 0 && code.charAt(index) == '.';
   }

   private static boolean isObjectKey(String code, int end) {
      int index = end;

      while(index < code.length() && Character.isWhitespace(code.charAt(index))) {
         ++index;
      }

      return index < code.length() && code.charAt(index) == ':';
   }

   private static String maskStringsAndComments(String source) {
      StringBuilder masked = new StringBuilder(source.length());
      boolean lineComment = false;
      boolean blockComment = false;
      boolean escaped = false;
      char quote = '\u0000';

      for(int index = 0; index < source.length(); ++index) {
         char character = source.charAt(index);
         char next = index + 1 < source.length() ? source.charAt(index + 1) : '\u0000';
         boolean mask = lineComment || blockComment || quote != '\u0000';

         if (lineComment) {
            if (character == '\n') {
               lineComment = false;
               mask = false;
            }
         } else if (blockComment) {
            if (character == '*' && next == '/') {
               blockComment = false;
            }
         } else if (quote != '\u0000') {
            if (escaped) {
               escaped = false;
            } else if (character == '\\') {
               escaped = true;
            } else if (character == quote) {
               quote = '\u0000';
            }
         } else if (character == '/' && next == '/') {
            lineComment = true;
            mask = true;
         } else if (character == '/' && next == '*') {
            blockComment = true;
            mask = true;
         } else if (character == '\'' || character == '\"' || character == '`') {
            quote = character;
            mask = true;
         }

         masked.append(mask && character != '\n' ? ' ' : character);
      }

      return masked.toString();
   }

   private static int[] getLineStarts(String source) {
      List<Integer> starts = new ArrayList();
      starts.add(0);

      for(int index = 0; index < source.length(); ++index) {
         if (source.charAt(index) == '\n') {
            starts.add(index + 1);
         }
      }

      int[] result = new int[starts.size()];

      for(int index = 0; index < result.length; ++index) {
         result[index] = (Integer)starts.get(index);
      }

      return result;
   }

   private static int[] offsetToLineColumn(int[] lineStarts, int offset) {
      int low = 0;
      int high = lineStarts.length - 1;

      while(low <= high) {
         int middle = low + high >>> 1;

         if (lineStarts[middle] <= offset) {
            low = middle + 1;
         } else {
            high = middle - 1;
         }
      }

      int line = Math.max(0, high);
      return new int[]{line, Math.max(0, offset - lineStarts[line])};
   }

   private static List<SourceDiagnostic> single(int line, int column, int length, String message, boolean warning) {
      List<SourceDiagnostic> diagnostics = new ArrayList();
      diagnostics.add(new SourceDiagnostic(line, column, length, message, warning));
      return diagnostics;
   }

   private static String compactMessage(String message) {
      if (message == null) {
         return "";
      }

      int newline = message.indexOf('\n');
      return newline < 0 ? message : message.substring(0, newline);
   }
}
