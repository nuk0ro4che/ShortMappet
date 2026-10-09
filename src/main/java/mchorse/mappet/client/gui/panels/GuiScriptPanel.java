package mchorse.mappet.client.gui.panels;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.scripts.Script;
import mchorse.mappet.api.utils.ContentType;
import mchorse.mappet.api.utils.IContentType;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.HudIdPicker;
import mchorse.mappet.client.gui.scripts.GuiDocumentationOverlayPanel;
import mchorse.mappet.client.gui.scripts.GuiLibrariesOverlayPanel;
import mchorse.mappet.client.gui.scripts.GuiRepl;
import mchorse.mappet.client.gui.scripts.GuiScriptContextMenu;
import mchorse.mappet.client.gui.scripts.GuiScriptGlobalSearchOverlayPanel;
import mchorse.mappet.client.gui.scripts.GuiScriptSearchOverlayPanel;
import mchorse.mappet.client.gui.scripts.GuiScriptTemplateConfirmModal;
import mchorse.mappet.client.gui.scripts.GuiScriptTemplatesOverlayPanel;
import mchorse.mappet.client.gui.scripts.GuiTextEditor;
import mchorse.mappet.client.gui.scripts.GuiTextEditor.SourceDiagnostic;
import mchorse.mappet.client.gui.scripts.ScriptTemplateManager;
import mchorse.mappet.client.gui.scripts.highlights.Highlighters;
import mchorse.mappet.client.gui.scripts.utils.GuiItemStackOverlayPanel;
import mchorse.mappet.client.gui.scripts.utils.GuiMorphOverlayPanel;
import mchorse.mappet.client.gui.scripts.utils.GuiScriptSoundOverlayPanel;
import mchorse.mappet.client.gui.scripts.utils.JavaScriptDiagnostics;
import mchorse.mappet.client.gui.utils.text.utils.Cursor;
import mchorse.mappet.client.gui.scripts.utils.SyntaxStyle;
import mchorse.mappet.client.gui.scripts.utils.documentation.DocClass;
import mchorse.mappet.client.gui.scripts.utils.documentation.DocMethod;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlay;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlayPanel;
import mchorse.mappet.client.gui.utils.overlays.GuiSoundOverlayPanel;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketRequestScriptDiagnostic;
import mchorse.mappet.network.common.scripts.PacketRequestScriptStamp;
import mchorse.mappet.network.common.scripts.PacketRequestClientScriptFlags;
import mchorse.mappet.network.common.scripts.PacketRequestScriptSearch;
import mchorse.mappet.network.common.scripts.ScriptSearchResult;
import mchorse.mappet.utils.MPIcons;
import mchorse.mappet.utils.autocomplete.AutoCompleteConfig;
import mchorse.mappet.utils.autocomplete.Config;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.input.color.GuiColorPicker;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.utils.IconRegistry;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.values.ValueInt;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.RayTracing;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.util.MMIcons;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_2487;
import net.minecraft.class_2522;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_634;
import net.minecraft.class_746;

public class GuiScriptPanel extends GuiMappetDashboardPanel<Script> {
   private final IContentType contentType;
   public GuiIconElement toggleRepl;
   public GuiIconElement docs;
   public GuiIconElement libraries;
   public GuiIconElement run;
   public GuiIconElement saveTemplate;
   public GuiIconElement searchReplace;
   public GuiIconElement globalSearch;
   public GuiIconElement beautify;
   public GuiTextEditor code;
   public GuiRepl repl;
   public GuiScriptTabBar tabs;
   public GuiToggleElement unique;
   public GuiToggleElement globalLibrary;
   public GuiToggleElement client;
   private final Set<String> clientScriptIds = new HashSet();
   private final Map<String, EditorViewState> editorViewStates = new HashMap();
   private final List<String> openScriptTabs = new ArrayList();
   private final Map<String, String> tabInitialCode = new HashMap();
   private final Set<String> modifiedScriptTabs = new HashSet();
   private static final Map<String, Integer> CACHED_SCRIPT_DIAGNOSTIC_STATUSES = new HashMap();
   private Map<String, Integer> scriptDiagnosticStatuses = new HashMap();
   private static final long AUTOSAVE_DELAY = 2000L;
   private static final long TOAST_DURATION = 2500L;
   private static final long TOAST_FADE = 500L;
   private static final long STAMP_POLL_INTERVAL = 1000L;
   private long lastEditTime;
   private String toast;
   private long toastExpiration;
   /* Опрос метки времени файла, чтобы заметить правку скрипта снаружи игры */
   private long lastStampPoll;
   private String stampId;
   private long stampModified;
   private long stampLength;
   private long stampDataModified;
   /* Ответы, запросы которых ушли до нашей записи или обновления буфера, базовой меткой быть не могут */
   private int stampRequestPending;
   private int stampStaleDiscard;
   private boolean externalChange;
   private long externalModified;
   private long externalLength;
   private long externalDataModified;
   private boolean reloadFromServer;
   private boolean changeBarVisible;
   private int applyX0;
   private int applyY0;
   private int applyX1;
   private int applyY1;
   private int rejectX0;
   private int rejectY0;
   private int rejectX1;
   private int rejectY1;
   

   private final Map<String, Set<String>> scriptLibraryFunctions = new HashMap();
   private static final int MAX_CONCURRENT_DIAGNOSTIC_REQUESTS = 6;
   private List<String> scriptDiagnosticQueue = new ArrayList();
   private final Set<String> requestedDiagnosticScripts = java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap());
   private final java.util.concurrent.atomic.AtomicInteger diagnosticScanWorkers = new java.util.concurrent.atomic.AtomicInteger();
   private final java.util.Queue<DiagnosticScanResult> completedDiagnosticScans = new java.util.concurrent.ConcurrentLinkedQueue();
   private int diagnosticScanRevision;
   private String searchResultScriptId;
   private int searchResultLine;
   private int searchResultColumn;
   private int searchResultLength;
   private GuiScriptGlobalSearchOverlayPanel globalSearchOverlay;
   private GuiScriptSearchOverlayPanel searchReplaceOverlay;

   public static GuiContextMenu createScriptContextMenu(class_310 mc, GuiTextEditor editor) {
      GuiScriptContextMenu menu = new GuiScriptContextMenu(mc);
      menu.action(
                      Icons.POSE,
                      IKey.lang("mappet.gui.scripts.context.paste_morph"),
                      () -> openMorphPicker(editor))
              .action(
                      MMIcons.ITEM,
                      IKey.lang("mappet.gui.scripts.context.paste_item"),
                      () -> openItemPicker(editor))
              .action(
                      Icons.BLOCK,
                      IKey.lang("mappet.gui.scripts.context.paste_player_pos"),
                      () -> pastePlayerPosition(editor))
              .action(
                      Icons.LIMB,
                      IKey.lang("mappet.gui.scripts.context.paste_player_rot"),
                      () -> pastePlayerRotation(editor))
              .action(
                      Icons.VISIBLE,
                      IKey.lang("mappet.gui.scripts.context.paste_block_pos"),
                      () -> pasteBlockPosition(editor))
              .action(
                      Icons.SOUND,
                      IKey.lang("mappet.gui.scripts.context.paste_sound"),
                      () -> openSoundPicker(editor))
              .action(
                      Icons.MATERIAL,
                      IKey.lang("mappet.gui.scripts.context.paste_colorRGB"),
                      () -> openColorPicker(editor, false))
              .action(
                      Icons.MATERIAL,
                      IKey.lang("mappet.gui.scripts.context.paste_colorARGB"),
                      () -> openColorPicker(editor, true))
              .action(
                      Icons.CURSOR,
                      IKey.lang("mappet.gui.script_panel.insert_hud_id"),
                      () -> HudIdPicker.start(editor));

      if (editor.isSelected()) {
         setupDocumentation(editor, menu);
      }

      menu.action(
              Icons.FILE,
              IKey.lang("mappet.gui.scripts.context.use_template"),
              () -> openScriptTemplatesOverlay(editor));
      return menu;
   }

   private static void openScriptTemplatesOverlay(GuiTextEditor editor) {
      GuiScriptTemplatesOverlayPanel overlay =
              new GuiScriptTemplatesOverlayPanel(
                      class_310.method_1551(), (template) -> confirmTemplateOverwrite(editor, template));
      GuiOverlay.addOverlay(GuiBase.getCurrent(), overlay, 0.46F, 0.58F);
   }

   private static void confirmTemplateOverwrite(GuiTextEditor editor, String template) {
      GuiModal.addFullModal(
              GuiBase.getCurrent().screen.root,
              () ->
                      new GuiScriptTemplateConfirmModal(
                              class_310.method_1551(),
                              IKey.lang("mappet.gui.scripts.templates.confirm_overwrite"),
                              (confirmed) -> {
                                 if (confirmed) {
                                    editor.setText(template);
                                 }
                              }));
   }

   private static void setupDocumentation(GuiTextEditor editor, GuiScriptContextMenu menu) {
      String text = editor.getSelectedText().replaceAll("[^\\w\\d_]+", "");
      List<DocClass> searched = GuiDocumentationOverlayPanel.search(text);

      if (!searched.isEmpty()) {
         for (DocClass docClass : searched) {
            menu.action(
                    Icons.SEARCH,
                    IKey.format("mappet.gui.scripts.context.docs", new Object[] {docClass.getName()}),
                    () -> searchDocumentation(editor, docClass.getMethod(text)));
         }
      }
   }

   private static void openMorphPicker(GuiTextEditor editor) {
      AbstractMorph morph = null;
      class_2487 tag = readFromSelected(editor);

      if (tag != null) {
         try {
            morph = MorphManager.INSTANCE.morphFromNBT(tag);

         } catch (Exception e) {
            morph = null;
         }
      }

      GuiOverlay.addOverlay(
              GuiBase.getCurrent(),
              new GuiMorphOverlayPanel(
                      class_310.method_1551(),
                      IKey.lang("mappet.gui.scripts.overlay.title_morph"),
                      editor,
                      morph),
              240,
              54);
   }

   private static void openItemPicker(GuiTextEditor editor) {
      class_1799 stack = class_1799.field_8037;
      class_2487 tag = readFromSelected(editor);

      if (tag != null) {
         try {
            stack = class_1799.method_7915(tag);

         } catch (Exception e) {
            stack = class_1799.field_8037;
         }
      }

      GuiOverlay.addOverlay(
              GuiBase.getCurrent(),
              new GuiItemStackOverlayPanel(
                      class_310.method_1551(),
                      IKey.lang("mappet.gui.scripts.overlay.title_item"),
                      editor,
                      stack),
              240,
              54);
   }

   private static class_2487 readFromSelected(GuiTextEditor editor) {
      if (editor.isSelected()) {
         try {
            class_2487 wrapper = class_2522.method_10718("{String:" + editor.getSelectedText() + "}");

            return class_2522.method_10718(wrapper.method_10558("String"));

         } catch (Exception var3) {
            return null;
         }

      } else {
         return null;
      }
   }

   private static void pastePlayerPosition(GuiTextEditor editor) {
      class_1657 player = class_310.method_1551().field_1724;
      DecimalFormat format = GuiTrackpadElement.FORMAT;
      String var10001 = format.format(player.method_23317());
      editor.pasteText(
              var10001
                      + ", "
                      + format.format(player.method_23318())
                      + ", "
                      + format.format(player.method_23321()));
   }

   private static void pastePlayerRotation(GuiTextEditor editor) {
      class_1657 player = class_310.method_1551().field_1724;
      DecimalFormat format = GuiTrackpadElement.FORMAT;
      String var10001 = format.format((double) player.method_36455());
      editor.pasteText(
              var10001
                      + ",  "
                      + format.format((double) player.method_36454())
                      + ", "
                      + format.format((double) player.method_5791()));
   }

   private static void pasteBlockPosition(GuiTextEditor editor) {
      class_1657 player = class_310.method_1551().field_1724;
      DecimalFormat format = GuiTrackpadElement.FORMAT;
      class_239 result = RayTracing.rayTrace(player, (double) 128.0F, 0.0F);

      if (result instanceof class_3965 blockHit) {
         class_2338 pos = blockHit.method_17777();
         String var10001 = format.format((long) pos.method_10263());
         editor.pasteText(
                 var10001
                         + ", "
                         + format.format((long) pos.method_10264())
                         + ", "
                         + format.format((long) pos.method_10260()));
      }
   }

   private static void openSoundPicker(GuiTextEditor editor) {
      GuiSoundOverlayPanel panel = new GuiScriptSoundOverlayPanel(class_310.method_1551(), editor);
      GuiOverlay.addOverlay(GuiBase.getCurrent(), panel, 0.5F, 0.9F);
   }

   private static void openColorPicker(final GuiTextEditor editor, final boolean isArgb) {
      final ValueInt valueInt =
              isArgb
                      ? (new ValueInt("color_picker", 0)).colorAlpha()
                      : (new ValueInt("color_picker", 0)).color();
      GuiOverlayPanel panel =
              new GuiOverlayPanel(
                      class_310.method_1551(),
                      IKey.lang("mappet.gui.scripts.context.paste_color" + (isArgb ? "A" : "") + "RGB")) {
                 public void onClose() {
                    super.onClose();
                    editor.pasteText(
                            (new Color((Integer) valueInt.get(), isArgb))
                                    .stringify(isArgb)
                                    .replaceAll("#", "0x"));
                 }
              };
      GuiColorPicker picker =
              new GuiColorPicker(class_310.method_1551(), (color) -> valueInt.set(color));

      if (isArgb) {
         picker.editAlpha();
      }

      picker
              .markIgnored()
              .flex()
              .relative(panel.content)
              .xy(0.5F, 0.5F)
              .anchor(0.5F, 0.5F)
              .wh(200, 85)
              .bounds(panel.content, 2);
      panel.content.add(picker);
      GuiOverlay.addOverlay(GuiBase.getCurrent(), panel, 200, 160);
   }

   private static String beautifyJavaScript(String source) {
      if (source == null || source.isEmpty()) return source == null ? "" : source;
      StringBuilder result = new StringBuilder();
      int indent = 0;
      boolean inBlockComment = false;
      for (String raw : source.replace("\r", "").split("\\n", -1)) {
         String line = raw.trim();
         if (line.isEmpty()) {
            result.append('\n');
            continue;
         }
         if (line.startsWith("}")) indent = Math.max(0, indent - 1);
         for (int i = 0; i < indent; ++i) result.append("    ");
         result.append(line).append('\n');
         int opens = 0, closes = 0;
         boolean string = false, escaped = false;
         for (int i = 0; i < line.length(); ++i) {
            char c = line.charAt(i);
            if (c == '\\' && string) { escaped = !escaped; continue; }
            if ((c == '"' || c == '\'') && !escaped && !inBlockComment) string = !string;
            escaped = false;
            if (!string) {
               if (i + 1 < line.length() && line.charAt(i) == '/' && line.charAt(i + 1) == '*') inBlockComment = true;
               if (i > 0 && line.charAt(i - 1) == '*' && c == '/') inBlockComment = false;
               if (!inBlockComment && c == '{') ++opens;
               if (!inBlockComment && c == '}') ++closes;
            }
         }
         indent = Math.max(0, indent + opens - closes + (line.startsWith("}") ? 1 : 0));
      }
      return result.toString().replaceFirst("\\n$", "");
   }

   private static void searchDocumentation(GuiTextEditor editor, DocMethod method) {
      GuiDocumentationOverlayPanel panel =
              new GuiDocumentationOverlayPanel(class_310.method_1551(), method);
      GuiOverlay.addOverlay(GuiBase.getCurrent(), panel, 0.9F, 0.9F);
   }

   
   private void openApiDocumentation(AutoCompleteConfig.Suggestion suggestion) {
      if (suggestion == null || suggestion.methodName == null || suggestion.methodName.isEmpty()) {
         return;
      }

      DocMethod fallback = null;
      String expectedType = suggestion.className == null ? "" : suggestion.className;
      for (DocClass docClass : GuiDocumentationOverlayPanel.search(suggestion.methodName)) {
         DocMethod method = docClass.getMethod(suggestion.methodName);
         if (method == null) {
            continue;
         }

         if (fallback == null) {
            fallback = method;
         }

         String name = docClass.name == null ? "" : docClass.name;
         if (name.equals(expectedType) || name.endsWith("." + expectedType)) {
            searchDocumentation(this.code, method);
            return;
         }
      }

      if (fallback != null) {
         searchDocumentation(this.code, fallback);
      }
   }

   public GuiScriptPanel(class_310 mc, GuiMappetDashboard dashboard) {
      this(mc, dashboard, ContentType.SCRIPTS);
   }

   public GuiScriptPanel(class_310 mc, GuiMappetDashboard dashboard, IContentType contentType) {
      super(mc, dashboard);
      this.contentType = contentType == null ? ContentType.SCRIPTS : contentType;
      this.namesList.setFileIcon(MMIcons.PROPERTIES);
      this.namesList.setFileIconResolver(this::getScriptFileIcon);
      this.namesList.setDiagnosticStatusResolver(this::getScriptDiagnosticStatus);
      this.namesList.onMiddleClick(this::openBackgroundScriptTab);
      this.toggleRepl =
              new GuiIconElement(mc, MPIcons.REPL, (b) -> this.setRepl(!this.repl.isVisible()));
      this.toggleRepl.tooltip(IKey.lang("mappet.gui.scripts.repl.title"), Direction.LEFT);
      Icon documentationIcon = IconRegistry.icons.get("other_document");
      this.docs =
              new GuiIconElement(
                      mc,
                      documentationIcon != null ? documentationIcon : Icons.FILE,
                      this::openDocumentation);
      this.docs.tooltip(IKey.lang("mappet.gui.scripts.documentation.title"), Direction.LEFT);
      this.libraries = new GuiIconElement(mc, Icons.MORE, this::openLibraries);
      this.libraries.tooltip(IKey.lang("mappet.gui.scripts.libraries.tooltip"), Direction.LEFT);
      this.run = new GuiIconElement(mc, Icons.PLAY, this::runScript);
      this.run.tooltip(IKey.lang("mappet.gui.scripts.run"), Direction.LEFT);
      Icon saveTemplateIcon = IconRegistry.icons.get("add");
      this.saveTemplate =
              new GuiIconElement(
                      mc,
                      saveTemplateIcon != null ? saveTemplateIcon : Icons.FILE,
                      (button) -> this.askSaveTemplate());
      this.saveTemplate.tooltip(IKey.lang("mappet.gui.scripts.savetemp"), Direction.LEFT);
      this.searchReplace = new GuiIconElement(mc, Icons.SEARCH, (button) -> this.openSearchReplace());
      this.searchReplace.tooltip(IKey.lang("mappet.gui.scripts.findreplace"), Direction.LEFT);
      Icon globalSearchIcon = IconRegistry.icons.get("computer_folder");
      this.globalSearch =
              new GuiIconElement(
                      mc,
                      globalSearchIcon != null ? globalSearchIcon : Icons.SEARCH,
                      (button) -> this.openGlobalSearch());
      this.globalSearch.tooltip(IKey.lang("mappet.gui.scripts.globalsearch"), Direction.LEFT);
      this.beautify = new GuiIconElement(mc, Icons.FILE, (button) -> {
         if (this.code != null) this.code.setText(beautifyJavaScript(this.code.getText()));
      });
      this.beautify.tooltip(IKey.str("Форматировать код"), Direction.LEFT);
      this.iconBar.add(
              new IGuiElement[] {
                      this.toggleRepl,
                      this.docs,
                      this.libraries,
                      this.run,
                      this.saveTemplate,
                      this.searchReplace,
                      this.globalSearch,
                      this.beautify
              });
      this.code = new GuiTextEditor(mc, this::trackCurrentTabChange);
      this.code.setClientScriptMode(this.isClientOnlyScripts());
      this.code.setVimSaveAction(this::save);
      this.code.setFindReplaceHandler(this::openSearchReplace);
      this.code.setApiDocumentationHandler(this::openApiDocumentation);
      this.code.background().context(() -> createScriptContextMenu(this.mc, this.code));
      this.code
              .keys()
              .ignoreFocus()
              .register(IKey.lang("mappet.gui.scripts.keys.word_wrap"), 25, this::toggleWordWrap)
              .category(GuiMappetDashboardPanel.KEYS_CATEGORY)
              .held(new int[] {29});
      this.repl = new GuiRepl(mc);
      this.client =
               new GuiToggleElement(
                       mc,
                       IKey.lang("mappet.gui.scripts.client"),
                       (b) -> this.setClientToggle(b.isToggled()));
      this.unique =
               new GuiToggleElement(
                       mc,
                       IKey.lang("mappet.gui.npcs.meta.unique"),
                       (b) -> (this.data).unique = b.isToggled());
      this.globalLibrary =
               new GuiToggleElement(
                       mc,
                       IKey.lang("mappet.gui.scripts.global_library"),
                       (b) -> (this.data).globalLibrary = b.isToggled());
      GuiElement sideBarToggles =
               Elements.column(mc, 2, new GuiElement[] {this.client, this.unique, this.globalLibrary});
      sideBarToggles.flex().relative(this.sidebar).x(10).y(1.0F, -10).w(1.0F, -20).anchorY(1.0F);
      this.names.flex().hTo(sideBarToggles.area, -5);
      this.tabs = new GuiScriptTabBar(mc).onSelect(this::selectScriptTab).onClose(this::closeScriptTab).onReorder(this::reorderScriptTab);
      this.tabs.flex().relative(this.editor).xy(0, 0).w(1.0F).h(GuiScriptTabBar.getHeight());
      this.code.flex().relative(this.editor).y(GuiScriptTabBar.getHeight()).w(1.0F).h(1.0F, -GuiScriptTabBar.getHeight());
      this.repl.flex().relative(this.editor).wh(1.0F, 1.0F);
      this.editor.add(this.tabs);
      this.editor.add(this.code);
      this.sidebar.prepend(sideBarToggles);
      this.add(this.repl);
      this.fill(null);
   }

   public void fillNames(List<String> names) {
      if (names != null) {
         Set<String> available = new HashSet(names);
         this.openScriptTabs.removeIf((script) -> !available.contains(script));
         this.editorViewStates.keySet().removeIf((script) -> !available.contains(script));
         this.tabInitialCode.keySet().removeIf((script) -> !available.contains(script));
         this.modifiedScriptTabs.removeIf((script) -> !available.contains(script));
         this.scriptLibraryFunctions.keySet().removeIf((script) -> !available.contains(script));

         if (this.data != null && !available.contains(((Script)this.data).getId())) {
            this.fill(null);
         }
      }
      super.fillNames(names);
      this.refreshScriptTabs();
      this.beginScriptDiagnosticScan(names);
   }

   private void beginScriptDiagnosticScan(List<String> names) {
      if (this.isClientOnlyScripts()) {
         return;
      }
      ++this.diagnosticScanRevision;
      this.scriptDiagnosticStatuses.clear();
      this.scriptDiagnosticStatuses.putAll(CACHED_SCRIPT_DIAGNOSTIC_STATUSES);
      this.scriptDiagnosticQueue.clear();
      this.completedDiagnosticScans.clear();
      this.requestedDiagnosticScripts.clear();
      String current = this.data == null ? "" : ((Script) this.data).getId();

      for (String name : names) {
         if (name.endsWith(".js")
                 && !name.equals(current)
                 && !CACHED_SCRIPT_DIAGNOSTIC_STATUSES.containsKey(name)) {
            this.scriptDiagnosticQueue.add(name);
         }
      }
   }

   public void receiveScriptDiagnosticCode(
           String script, String source, List<String> libraryFunctions, boolean library) {
      if (script == null) {
         return;
      }

      Set<String> functions =
              new HashSet(libraryFunctions == null ? new ArrayList() : libraryFunctions);
      this.scriptLibraryFunctions.put(script, functions);

      if (this.data != null && script.equals(((Script) this.data).getId())) {
         this.code.setJavaScriptDiagnostics(!library && "js".equals(((Script) this.data).getScriptExtension()));
         this.code.setJavaScriptLibraryFunctions(functions);
      }

      if (!this.requestedDiagnosticScripts.remove(script)) {
         return;
      }

      int revision = this.diagnosticScanRevision;
      this.diagnosticScanWorkers.incrementAndGet();
      Thread worker =
              new Thread(
                      () -> {
                         try {
                            int status =
                                    library ? 0 : JavaScriptDiagnostics.createSnapshot(source, revision, functions).getStatus();

                            if (revision == this.diagnosticScanRevision) {
                               this.completedDiagnosticScans.add(new DiagnosticScanResult(script, status, revision));
                            }
                         } finally {
                            this.diagnosticScanWorkers.decrementAndGet();
                         }
                      },
                      "Mappet script list diagnostics");
      worker.setDaemon(true);
      worker.start();
   }

   private static int getDiagnosticStatus(List<SourceDiagnostic> diagnostics) {
      boolean warning = false;

      for (SourceDiagnostic diagnostic : diagnostics) {
         if (!diagnostic.warning) {
            return 2;
         }

         warning = true;
      }

      return warning ? 1 : 0;
   }

   private void updateScriptDiagnosticScan() {
      if (this.isClientOnlyScripts()) {
         return;
      }
      DiagnosticScanResult result;

      while ((result = this.completedDiagnosticScans.poll()) != null) {
         if (result.revision == this.diagnosticScanRevision
                 && (this.data == null || !result.script.equals(((Script) this.data).getId()))) {
            this.scriptDiagnosticStatuses.put(result.script, result.status);
            CACHED_SCRIPT_DIAGNOSTIC_STATUSES.put(result.script, result.status);
         }
      }

      


      int inFlight = this.requestedDiagnosticScripts.size() + this.diagnosticScanWorkers.get();

      while (inFlight < MAX_CONCURRENT_DIAGNOSTIC_REQUESTS && !this.scriptDiagnosticQueue.isEmpty()) {
         String script = (String) this.scriptDiagnosticQueue.remove(0);
         this.requestedDiagnosticScripts.add(script);
         Dispatcher.sendToServer(new PacketRequestScriptDiagnostic(script, this.getType() == ContentType.CLIENT_SCRIPTS));
         inFlight++;
      }
   }

   private int getScriptDiagnosticStatus(String id) {
      Integer status = (Integer) this.scriptDiagnosticStatuses.get(id);
      return status == null ? 0 : status;
   }

   private void updateCurrentScriptDiagnosticStatus() {
      if (this.data != null && "js".equals(((Script) this.data).getScriptExtension())) {
         String script = ((Script) this.data).getId();
         int status = this.code.getJavaScriptDiagnosticStatus();

         if (status > 0
                 || !this.scriptDiagnosticStatuses.containsKey(script)
                 || this.code.isJavaScriptDiagnosticReady()) {
            this.scriptDiagnosticStatuses.put(script, status);
            CACHED_SCRIPT_DIAGNOSTIC_STATUSES.put(script, status);
         }
      }
   }

   public void draw(GuiContext context) {
      this.updateCurrentScriptDiagnosticStatus();
      this.updateScriptDiagnosticScan();
      this.updateStampPoll();
      this.updateAutosave();
      super.draw(context);
      this.drawExternalChangeBar(context);
      this.drawToast();
   }

   /**
    * Раз в секунду спрашивает у сервера, не изменился ли файл скрипта на диске
    */
   private void updateStampPoll() {
      if (this.data == null || this.code == null || !this.code.isVisible()) {
         return;
      }

      long now = System.currentTimeMillis();

      if (now - this.lastStampPoll < STAMP_POLL_INTERVAL) {
         return;
      }

      this.lastStampPoll = now;

      String id = ((Script) this.data).getId();

      Dispatcher.sendToServer(new PacketRequestScriptStamp(id, this.isClientOnlyScripts()));
      this.stampRequestPending++;
   }

   /**
    * Метка времени файла с сервера: первая ответная метка становится базовой,
    * расхождение с ней означает, что файл правили вне редактора
    */
   public void receiveScriptStamp(String id, long modified, long length, long dataModified) {
      if (this.stampRequestPending > 0) {
         this.stampRequestPending--;
      }

      if (this.stampStaleDiscard > 0) {
         this.stampStaleDiscard--;
         return;
      }

      if (id == null || this.data == null || !id.equals(((Script) this.data).getId())) {
         return;
      }

      if (modified <= 0L && length < 0L) {
         /* Файла больше нет на диске — оставляем буфер редактора как есть */
         return;
      }

      if (!id.equals(this.stampId)) {
         this.stampId = id;
         this.stampModified = modified;
         this.stampLength = length;
         this.stampDataModified = dataModified;

         return;
      }

      if (modified == this.stampModified && length == this.stampLength && dataModified == this.stampDataModified) {
         /* Файл снова совпал с открытой версией — правку с диска отменили */
         this.externalChange = false;
         this.changeBarVisible = false;

         return;
      }

      this.externalChange = true;
      this.externalModified = modified;
      this.externalLength = length;
      this.externalDataModified = dataModified;
   }

   /**
    * Принимает правку с диска: просит содержимое у сервера и сбрасывает буфер
    */
   private void applyExternalChange() {
      if (this.data == null) {
         return;
      }

      String id = ((Script) this.data).getId();

      this.externalChange = false;
      this.changeBarVisible = false;
      this.stampId = id;
      this.stampModified = this.externalModified;
      this.stampLength = this.externalLength;
      this.stampDataModified = this.externalDataModified;
      this.reloadFromServer = true;

      this.requestData(id);
   }

   /**
    * Оставляет содержимое редактора и гасит уведомление, приняв текущий файл как базовый
    */
   private void rejectExternalChange() {
      if (this.data == null) {
         return;
      }

      this.externalChange = false;
      this.changeBarVisible = false;
      this.stampId = ((Script) this.data).getId();
      this.stampModified = this.externalModified;
      this.stampLength = this.externalLength;
      this.stampDataModified = this.externalDataModified;
   }

   @Override
   public boolean mouseClicked(GuiContext context) {
      if (this.externalChange && this.changeBarVisible && context.mouseButton == 0) {
         int x = context.mouseX;
         int y = context.mouseY;

         if (this.inside(this.applyX0, this.applyY0, this.applyX1, this.applyY1, x, y)) {
            this.applyExternalChange();

            return true;
         }

         if (this.inside(this.rejectX0, this.rejectY0, this.rejectX1, this.rejectY1, x, y)) {
            this.rejectExternalChange();

            return true;
         }
      }

      return super.mouseClicked(context);
   }

   private static boolean inside(int x0, int y0, int x1, int y1, int x, int y) {
      return x >= x0 && x < x1 && y >= y0 && y < y1;
   }

   /**
    * Табличка «файл изменён снаружи» с кнопками применения и отклонения правки
    */
   private void drawExternalChangeBar(GuiContext context) {
      if (!this.externalChange || this.font == null || this.editor == null
              || !this.editor.isVisible() || this.code == null || !this.code.isVisible()) {
         this.changeBarVisible = false;

         return;
      }

      String message = IKey.lang("mappet.gui.scripts.external_change").get();
      String apply = IKey.lang("mappet.gui.scripts.external_apply").get();
      String reject = IKey.lang("mappet.gui.scripts.external_reject").get();

      int x = this.editor.area.x + 8;
      int y = this.editor.area.y + GuiScriptTabBar.getHeight() + 6;
      int pad = 6;
      int gap = 6;
      int buttonPadding = 5;
      int applyWidth = this.font.method_1727(apply) + buttonPadding * 2;
      int rejectWidth = this.font.method_1727(reject) + buttonPadding * 2;

      int x0 = x - 4;
      int y0 = y - 4;
      int x1 = x + pad + this.font.method_1727(message) + gap + applyWidth + gap + rejectWidth + pad;
      int y1 = y + 14;

      GuiDraw.drawRect(x0, y0, x1, y1, 0xD8000000);

      GuiDraw.drawStringWithShadow(this.font, message, x + pad, y, 0xFFFFFFFF);

      this.applyX0 = x + pad + this.font.method_1727(message) + gap;
      this.applyY0 = y - 3;
      this.applyX1 = this.applyX0 + applyWidth;
      this.applyY1 = y + 13;

      this.rejectX0 = this.applyX1 + gap;
      this.rejectY0 = y - 3;
      this.rejectX1 = this.rejectX0 + rejectWidth;
      this.rejectY1 = y + 13;

      this.drawChangeBarButton(context, this.applyX0, this.applyY0, this.applyX1, this.applyY1, apply, buttonPadding);
      this.drawChangeBarButton(context, this.rejectX0, this.rejectY0, this.rejectX1, this.rejectY1, reject, buttonPadding);

      this.changeBarVisible = true;
   }

   private void drawChangeBarButton(GuiContext context, int x0, int y0, int x1, int y1, String label, int padding) {
      boolean hovered = this.inside(x0, y0, x1, y1, context.mouseX, context.mouseY);

      GuiDraw.drawRect(x0, y0, x1, y1, hovered ? 0x50FFFFFF : 0x28FFFFFF);
      GuiDraw.drawStringWithShadow(this.font, label, x0 + padding, y0 + 4, 0xFFFFFFFF);
   }

   /**
    * Пока файл правили снаружи, автосохранение молча затёрло бы эту правку
    */
   private void updateAutosave() {
      if (this.externalChange) {
         return;
      }

      if (this.lastEditTime == 0L || this.data == null || this.code == null || !this.code.isVisible()) {
         return;
      }

      long now = System.currentTimeMillis();

      if (now - this.lastEditTime < AUTOSAVE_DELAY) {
         return;
      }

      this.lastEditTime = 0L;
      this.saveAndSyncTab(true);
   }

   /**
    * Сбрасывает содержимое редактора на сервер и отмечает вкладку как сохранённую
    */
   private void saveAndSyncTab(boolean toast) {
      if (this.data == null) {
         return;
      }

      String script = ((Script)this.data).getId();

      this.save();
      this.tabInitialCode.put(script, this.code.getText());
      this.modifiedScriptTabs.remove(script);
      this.refreshScriptTabs();

      if (toast) {
         this.toast = IKey.lang("mappet.gui.scripts.saved").get();
         this.toastExpiration = System.currentTimeMillis() + TOAST_DURATION;
      }
   }

   /**
    * После своей записи метку времени файла заново снимет опрос, а уведомление уже неактуально
    */
   @Override
   public void save() {
      super.save();

      /* Ответ на опрос, ушедший до записи, вернёт старую метку — базовой он не станет */
      this.stampStaleDiscard += this.stampRequestPending;
      this.stampRequestPending = 0;
      this.stampId = null;
      this.externalChange = false;
      this.changeBarVisible = false;
      this.reloadFromServer = false;
   }

   private void drawToast() {
      if (this.toast == null || this.font == null || this.externalChange) {
         return;
      }

      long remaining = this.toastExpiration - System.currentTimeMillis();

      if (remaining <= 0L) {
         this.toast = null;
         return;
      }

      if (remaining <= 2L) {
         this.toast = null;
         return;
      }

      /* Плавное затухание вместо ступенек, иначе анимация выглядит дёргано */
      int alpha = remaining < TOAST_FADE ? (int) (255L * remaining / TOAST_FADE) : 255;

      int x = this.editor.area.x + 8;
      int y = this.editor.area.y + GuiScriptTabBar.getHeight() + 6;

      GuiDraw.drawRect(x - 4, y - 4, x + this.font.method_1727(this.toast) + 4, y + 10, alpha << 24);
      GuiDraw.drawStringWithShadow(this.font, this.toast, x, y, alpha << 24 | 0xFFFFFF);
   }

   private static class DiagnosticScanResult {
      public final String script;
      public final int status;
      public final int revision;

      public DiagnosticScanResult(String script, int status, int revision) {
         this.script = script;
         this.status = status;
         this.revision = revision;
      }
   }

   private void toggleWordWrap() {
      this.code.wrap();
      this.code.recalculate();
      this.code.horizontal.clamp();
      this.code.vertical.clamp();
   }

   private void openDocumentation(GuiIconElement element) {
      GuiDocumentationOverlayPanel panel = new GuiDocumentationOverlayPanel(this.mc);
      GuiOverlay.addOverlay(GuiBase.getCurrent(), panel, 0.9F, 0.9F);
   }

   protected void runScript(GuiIconElement element) {
      this.runCurrentScript();
   }

   public void runCurrentScript() {
      if (this.data == null || this.mc == null || this.mc.field_1724 == null) {
         return;
      }
      class_746 player = this.mc.field_1724;
      this.save();
      this.save = false;
      class_634 network = player.field_3944;
      String playerName = String.valueOf(player.method_5667());
      String command = this.isCurrentScriptClient() ? "mp clientscript exec " : "mp script exec " + playerName + " ";
      network.method_45731(command + ((Script)this.data).getId());
   }

   private void openLibraries(GuiIconElement element) {
      GuiLibrariesOverlayPanel overlay = new GuiLibrariesOverlayPanel(this.mc, this.data);
      GuiOverlay.addOverlay(GuiBase.getCurrent(), overlay, 0.4F, 0.6F);
   }

   private void openSearchReplace() {
      if (this.data != null && this.code.isVisible()) {
         if (this.searchReplaceOverlay != null && this.searchReplaceOverlay.isOpen()) {
            this.searchReplaceOverlay.close();
            return;
         }

         if (this.searchReplaceOverlay == null) {
            this.searchReplaceOverlay = new GuiScriptSearchOverlayPanel(this.mc, this.code);
         }
         this.searchReplaceOverlay.setOpen(true);
         GuiOverlay.addCard(
                 GuiBase.getCurrent(),
                 this.searchReplaceOverlay,
                 this.searchReplace,
                 250,
                 112);
      }
   }

   protected void openGlobalSearch() {
      if (this.data == null || !this.code.isVisible()) {
         return;
      }

      this.save();
      this.globalSearchOverlay =
              new GuiScriptGlobalSearchOverlayPanel(
                      this.mc,
                      this::requestGlobalSearch,
                      this::openSearchResult);
      GuiOverlay.addOverlay(GuiBase.getCurrent(), this.globalSearchOverlay, 0.7F, 0.75F);
   }

   protected void requestGlobalSearch(String query) {
      Dispatcher.sendToServer(new PacketRequestScriptSearch(query, this.getType() == ContentType.CLIENT_SCRIPTS));
   }

   
   protected boolean isClientOnlyScripts() {
      return false;
   }

   private boolean isCurrentScriptClient() {
      return this.data != null && this.data.client;
   }

   private Icon getScriptFileIcon(String path) {
      return this.clientScriptIds.contains(path) ? Icons.CODE : null;
   }

   private void setClientToggle(boolean value) {
      if (this.data == null) {
         return;
      }
      this.data.client = value;
      if (value) {
         this.clientScriptIds.add(this.data.getId());
      } else {
         this.clientScriptIds.remove(this.data.getId());
      }
      this.save();
   }

   public void setClientScriptIds(Collection<String> ids) {
      this.clientScriptIds.clear();
      if (ids != null) {
         this.clientScriptIds.addAll(ids);
      }
      if (this.data != null) {
         if (this.data.client) {
            this.clientScriptIds.add(this.data.getId());
         } else {
            this.clientScriptIds.remove(this.data.getId());
         }
      }
   }

   @Override
   public void requestDataNames() {
      super.requestDataNames();
      Dispatcher.sendToServer(new PacketRequestClientScriptFlags());
   }

   public void showSearchResults(List<ScriptSearchResult> results) {
      if (this.globalSearchOverlay != null) {
         this.globalSearchOverlay.showResults(results);
      }
   }

   private void openSearchResult(ScriptSearchResult result) {
      this.searchResultScriptId = result.script;
      this.searchResultLine = result.line;
      this.searchResultColumn = result.column;
      this.searchResultLength = result.length;
      this.namesList.setCurrentFile(result.script);
      this.pickData(result.script);
   }

   private void focusSearchResult() {
      if (this.searchResultScriptId == null
              || this.data == null
              || !this.searchResultScriptId.equals(this.data.getId())) {
         return;
      }

      String text = this.code.getText();
      int offset = this.getLineOffset(text, this.searchResultLine);
      int start = Math.min(text.length(), offset + Math.max(0, this.searchResultColumn - 1));
      int end = Math.min(text.length(), start + Math.max(1, this.searchResultLength));
      this.code.selectRange(start, end);
      this.code.moveViewportToCursor();
      this.searchResultScriptId = null;
      this.searchResultLine = 0;
      this.searchResultColumn = 0;
      this.searchResultLength = 0;
   }

   private int getLineOffset(String text, int line) {
      int offset = 0;

      for (int current = 1; current < line && offset < text.length(); current++) {
         int next = text.indexOf('\n', offset);
         offset = next < 0 ? text.length() : next + 1;
      }

      return offset;
   }

   private void askSaveTemplate() {
      if (this.data == null || !this.code.isVisible()) {
         return;
      }

      GuiModal.addFullModal(
              this.sidebar,
              () ->
                      new GuiPromptModal(this.mc, IKey.lang("mappet.gui.scripts.nametemp"), this::saveTemplate)
                              .filename());
   }

   private void saveTemplate(String name) {
      ScriptTemplateManager.saveTemplate(name, this.code.getText());
   }

   protected void addNewData(String name, Script data) {
      if (name.lastIndexOf(".") == -1) {
         name = name + ".js";
      }

      super.addNewData((String) name, data);
   }

   protected Script createClientData(String id, class_2487 tag) {
      Script script = new Script();

      if (tag != null) {
         script.deserializeNBT(tag);
      }

      script.setId(id);
      return script;
   }

   protected void dupeData(String name) {
      if (name.lastIndexOf(".") == -1) {
         name = name + ".js";
      }

      super.dupeData(name);
   }

   public IContentType getType() {
      return this.contentType;
   }

   public String getTitle() {
      return "mappet.gui.panels.scripts";
   }

   protected void fillDefaultData(Script data) {
      super.fillDefaultData(data);
      data.code = Config.getNewScriptTemplate();
   }

   public void fill(Script data, boolean allowed) {
      String last = this.data == null ? null : ((Script) this.data).getId();
      /* Ответ сервера на «применить»: содержимое перечитано с диска, буфер больше не считается изменённым */
      String reloadingId = this.reloadFromServer && last != null ? last : null;

      this.reloadFromServer = false;
      this.externalChange = false;
      this.changeBarVisible = false;

      /* Буфер показывает содержимое с диска: базовой станет первая метка после этого,
         ответы, запросы которых ушли раньше, описывают уже не актуальную ситуацию */
      this.stampStaleDiscard += this.stampRequestPending;
      this.stampRequestPending = 0;
      this.stampId = null;
      this.lastStampPoll = 0L;

      super.fill(data, allowed);
      this.editor.setVisible(data != null);
      this.beautify.setVisible(data != null && allowed);
      this.client.setVisible(data != null && allowed);
      this.unique.setVisible(data != null && allowed);
      this.globalLibrary.setVisible(data != null && allowed);
      this.updateButtons();

      if (data != null) {
         boolean reload = reloadingId != null && reloadingId.equals(data.getId());
         String previous = (String) this.tabInitialCode.get(data.getId());
         /* Экран показывает ровно то, что пришло с сервера — базовой становится эта версия */
         boolean fresh = reload
            || last != null && last.equals(data.getId())
            || previous == null
            || !previous.equals(data.code);

         this.openScriptTab(data.getId());

         if (fresh) {
            this.tabInitialCode.put(data.getId(), data.code);
            this.modifiedScriptTabs.remove(data.getId());
            this.lastEditTime = 0L;
         }
         this.setRepl(false);
         this.code.setJavaScriptDiagnostics("js".equals(data.getScriptExtension()));
         Set<String> libraryFunctions = this.scriptLibraryFunctions.get(data.getId());
         this.code.setJavaScriptLibraryFunctions(libraryFunctions);
         this.requestCurrentScriptDiagnostic(data);
         this.code.setHighlighter(
                 Highlighters.readHighlighter(Highlighters.highlighterFile(data.getScriptExtension())));
         this.updateStyle();

         if (last != null && !last.equals(data.getId())) {
            this.saveEditorView(last);
         }

         if (!this.code.getText().equals(data.code)) {
            this.code.setText(data.code);
         }

         this.restoreEditorView(data.getId());

         this.unique.toggled(data.unique);
         this.globalLibrary.toggled(data.globalLibrary);
         this.client.toggled(data.client);
         this.code.setClientScriptMode(data.client);
         if (data.client) {
            this.clientScriptIds.add(data.getId());
         } else {
            this.clientScriptIds.remove(data.getId());
         }
         this.focusSearchResult();
      } else {
         this.code.setJavaScriptLibraryFunctions((Set)null);
         this.refreshScriptTabs();
      }
   }

   private void saveEditorView(String script) {
      if (script == null || script.isEmpty()) {
         return;
      }

      this.editorViewStates.put(script, new EditorViewState(this.code.cursor, this.code.selection, this.code.vertical.scroll, this.code.horizontal.scroll));
   }

   private void restoreEditorView(String script) {
      EditorViewState state = this.editorViewStates.get(script);
      if (state == null || this.code.getLines().isEmpty()) {
         return;
      }

      this.code.cursor.set(Math.max(0, Math.min(state.cursorLine, this.code.getLines().size() - 1)), 0);
      String line = ((mchorse.mappet.client.gui.utils.text.TextLine)this.code.getLines().get(this.code.cursor.line)).text;
      this.code.cursor.offset = Math.max(0, Math.min(state.cursorOffset, line.length()));

      if (state.selectionLine < 0) {
         this.code.deselect();
      } else {
         this.code.selection.set(Math.max(0, Math.min(state.selectionLine, this.code.getLines().size() - 1)), 0);
         String selectionLine = ((mchorse.mappet.client.gui.utils.text.TextLine)this.code.getLines().get(this.code.selection.line)).text;
         this.code.selection.offset = Math.max(0, Math.min(state.selectionOffset, selectionLine.length()));
      }

      this.code.vertical.scroll = Math.max(0, state.verticalScroll);
      this.code.horizontal.scroll = Math.max(0, state.horizontalScroll);
      this.code.vertical.clamp();
      this.code.horizontal.clamp();
   }

   private static class EditorViewState {
      public final int cursorLine;
      public final int cursorOffset;
      public final int selectionLine;
      public final int selectionOffset;
      public final int verticalScroll;
      public final int horizontalScroll;

      public EditorViewState(Cursor cursor, Cursor selection, int verticalScroll, int horizontalScroll) {
         this.cursorLine = cursor.line;
         this.cursorOffset = cursor.offset;
         this.selectionLine = selection.line;
         this.selectionOffset = selection.offset;
         this.verticalScroll = verticalScroll;
         this.horizontalScroll = horizontalScroll;
      }
   }

   

   private void requestCurrentScriptDiagnostic(Script script) {
      if (!this.isClientOnlyScripts() && script != null && "js".equals(script.getScriptExtension()) && script.getId() != null && !script.getId().isEmpty()) {
         Dispatcher.sendToServer(new PacketRequestScriptDiagnostic(script.getId(), this.getType() == ContentType.CLIENT_SCRIPTS));
      }
   }

   private void updateButtons() {
      boolean visible = this.data != null && this.allowed && this.code.isVisible();
      this.run.setVisible(visible);
      this.libraries.setVisible(visible);
      this.saveTemplate.setVisible(visible);
      this.searchReplace.setVisible(visible);
      this.globalSearch.setVisible(visible);
   }

   private void setRepl(boolean showRepl) {
      this.repl.setVisible(showRepl);
      this.code.setVisible(!showRepl);
      if (this.tabs != null) {
         this.tabs.setVisible(!showRepl && this.data != null);
      }
      this.updateButtons();
   }

   
   private void openBackgroundScriptTab(String script) {
      if (script != null && !script.isEmpty()) {
         this.openScriptTab(script);
      }
   }

   private void openScriptTab(String script) {
      if (this.openScriptTabs == null || script == null || script.isEmpty()) {
         return;
      }
      if (!this.openScriptTabs.contains(script)) {
         this.openScriptTabs.add(script);
         if (this.openScriptTabs.size() > 10) {
            String removed = (String)this.openScriptTabs.remove(0);
            this.editorViewStates.remove(removed);
            this.tabInitialCode.remove(removed);
            this.modifiedScriptTabs.remove(removed);
         }
      }
      this.refreshScriptTabs();
   }

   private void refreshScriptTabs() {
      if (this.tabs == null || this.openScriptTabs == null) {
         return;
      }
      String active = this.data == null ? null : ((Script)this.data).getId();
      this.tabs.setTabs(this.openScriptTabs, active);
      this.tabs.setVisible(active != null && !this.repl.isVisible());
   }

   private void selectScriptTab(String script) {
      if (script == null || this.data != null && script.equals(((Script)this.data).getId())) {
         return;
      }
      this.namesList.setCurrentFile(script);
      

      this.pickData(script);
   }

   @Override
   protected void onDataCreated(String id) {
      this.saveAndSyncTab(false);
   }

   @Override
   protected void onDataRenamed(String oldId, String newId) {
      this.renameOpenScript(oldId, newId);

      if (this.data != null && newId.equals(((Script)this.data).getId())) {
         this.requestCurrentScriptDiagnostic((Script)this.data);
      }

      this.refreshScriptTabs();
   }

   @Override
   protected void onFolderRenamed(String oldFolder, String newFolder) {
      this.renameOpenScripts(oldFolder, newFolder);
      this.refreshScriptTabs();
   }

   @Override
   protected void onFolderMoved(String oldFolder, String newFolder) {
      this.renameOpenScripts(oldFolder, newFolder);
      this.refreshScriptTabs();
   }

   /** Переименовывает одну открытую вкладку, чтобы запись шла в существующий файл */
   private void renameOpenScript(String oldId, String newId) {
      if (oldId == null || newId == null || oldId.equals(newId)) {
         return;
      }

      int index = this.openScriptTabs.indexOf(oldId);

      if (index >= 0) {
         this.openScriptTabs.set(index, newId);
      }

      this.renameKey(this.editorViewStates, oldId, newId);
      this.renameKey(this.tabInitialCode, oldId, newId);
      this.renameKey(this.scriptLibraryFunctions, oldId, newId);
      this.renameKey(this.scriptDiagnosticStatuses, oldId, newId);
      this.renameKey(CACHED_SCRIPT_DIAGNOSTIC_STATUSES, oldId, newId);

      if (this.modifiedScriptTabs.remove(oldId)) {
         this.modifiedScriptTabs.add(newId);
      }
   }

   /** То же, но для всех скриптов внутри переименованной или перемещённой папки */
   private void renameOpenScripts(String oldFolder, String newFolder) {
      if (oldFolder == null || newFolder == null || oldFolder.equals(newFolder)) {
         return;
      }

      String prefix = oldFolder.endsWith("/") ? oldFolder : oldFolder + "/";

      for (String script : new ArrayList<>(this.openScriptTabs)) {
         if (script.startsWith(prefix)) {
            this.renameOpenScript(script, newFolder + script.substring(oldFolder.length()));
         }
      }
   }

   private static <V> void renameKey(Map<String, V> map, String oldId, String newId) {
      if (map.containsKey(oldId)) {
         V value = map.remove(oldId);
         map.put(newId, value);
      }
   }

   @Override
   protected void onDataRemoved(String id) {
      if (id == null || id.isEmpty()) {
         return;
      }

      this.openScriptTabs.remove(id);
      this.editorViewStates.remove(id);
      this.tabInitialCode.remove(id);
      this.modifiedScriptTabs.remove(id);
      this.scriptLibraryFunctions.remove(id);
      this.scriptDiagnosticStatuses.remove(id);
      CACHED_SCRIPT_DIAGNOSTIC_STATUSES.remove(id);
      this.refreshScriptTabs();
   }

   private void closeScriptTab(String script) {
      if (script == null || !this.openScriptTabs.remove(script)) {
         return;
      }
      this.editorViewStates.remove(script);
      this.tabInitialCode.remove(script);
      this.modifiedScriptTabs.remove(script);

      if (this.data != null && script.equals(((Script)this.data).getId())) {
         this.save();
         if (this.openScriptTabs.isEmpty()) {
            this.fill(null);
         } else {
            String next = (String)this.openScriptTabs.get(Math.max(0, this.openScriptTabs.size() - 1));
            this.namesList.setCurrentFile(next);
            this.pickData(next);
         }
      }
      this.refreshScriptTabs();
   }

   private void reorderScriptTab(String script, Integer targetIndex) {
      if (script == null || targetIndex == null) {
         return;
      }
      int from = this.openScriptTabs.indexOf(script);
      if (from < 0) {
         return;
      }
      this.openScriptTabs.remove(from);
      int target = Math.max(0, Math.min(targetIndex.intValue(), this.openScriptTabs.size()));
      this.openScriptTabs.add(target, script);
      this.refreshScriptTabs();
   }

   
   public void pickData(String id) {
      boolean selectingExistingTab = id != null && this.openScriptTabs != null && this.openScriptTabs.contains(id);
      if (id != null && this.data != null && !id.equals(((Script)this.data).getId()) && !selectingExistingTab) {
         String current = ((Script)this.data).getId();
         String original = (String)this.tabInitialCode.get(current);
         boolean changed = this.modifiedScriptTabs.contains(current)
            || original != null && !this.code.getText().equals(original);
         if (!changed) {
            this.openScriptTabs.remove(current);
            this.editorViewStates.remove(current);
            this.tabInitialCode.remove(current);
            this.modifiedScriptTabs.remove(current);
            this.refreshScriptTabs();
         }
      }
      super.pickData(id);
   }

   
   private void trackCurrentTabChange(String code) {
      if (this.data == null || this.tabInitialCode == null || this.modifiedScriptTabs == null) {
         return;
      }
      String script = ((Script)this.data).getId();
      String original = (String)this.tabInitialCode.get(script);

      if (original != null && !original.equals(code)) {
         this.modifiedScriptTabs.add(script);
         this.lastEditTime = System.currentTimeMillis();
         this.refreshScriptTabs();
      }
   }

   protected void preSave() {
      (this.data).code = this.code.getText();
      this.saveEditorView(this.data.getId());
   }

   public void open() {
      super.open();
      this.updateStyle();
   }

   public void updateStyle() {
      SyntaxStyle style = Mappet.scriptEditorSyntaxStyle.get();

      if (this.code.getHighlighter().getStyle() != style) {
         this.code.getHighlighter().setStyle(style);
         this.code.resetHighlight();
         this.repl.repl.getHighlighter().setStyle(style);
         this.repl.repl.resetHighlight();
      }
   }

   public Script getData() {
      return this.data;
   }
}