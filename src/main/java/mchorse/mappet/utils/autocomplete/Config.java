package mchorse.mappet.utils.autocomplete;

import mchorse.mclib.config.ConfigBuilder;
import mchorse.mclib.config.values.ValueBoolean;
import mchorse.mclib.config.values.ValueFloat;
import mchorse.mclib.config.values.ValueInt;
import mchorse.mclib.events.RegisterConfigEvent;

public class Config {
   public static final String MODID = "autocomplete";
   public static final String DEFAULT_NEW_SCRIPT_TEMPLATE = "function main(c) {\\n    let s = c.subject;\\n}";
   public static ValueBoolean autocompleteEnabled;
   public static ValueBoolean docPanelEnabled;
   public static ValueScriptTemplate newScriptTemplate;
   public static ValueInt contextMenuMaxItems;
   public static ValueInt menuWidth;
   public static ValueInt menuHeight;
   public static ValueInt menuOffsetX;
   public static ValueInt menuOffsetY;
   public static ValueFloat docPanelScale;
   public static ValueInt docPanelWidth;
   public static ValueInt docPanelHeight;
   public static ValueInt tabSelectMode;
   public static ValueBoolean lockArrows;
   public static ValueInt acVar;
   public static ValueInt acArray;
   public static ValueInt acObject;
   public static ValueInt acFunction;
   public static ValueInt acKeyword;
   public static ValueInt acIScript;
   public static ValueInt acJava;
   public static ValueInt acOther;
   public static ValueInt acLib;
   public static ValueInt acIcon;

   public void onConfigRegister(RegisterConfigEvent event) {
      ConfigBuilder builder = event.createBuilder("autocomplete");
      builder.category("features");
      builder.getCategory().markClientSide();
      autocompleteEnabled = (ValueBoolean)builder.getBoolean("autocomplete_enabled", true).clientSide();
      docPanelEnabled = (ValueBoolean)builder.getBoolean("doc_panel_enabled", true).clientSide();
      builder.category("script_template");
      builder.getCategory().markClientSide();
      newScriptTemplate = new ValueScriptTemplate("new_script_template", DEFAULT_NEW_SCRIPT_TEMPLATE);
      newScriptTemplate.clientSide();
      builder.register(newScriptTemplate);
      builder.category("autocomplete");
      builder.getCategory().markClientSide();
      contextMenuMaxItems = (ValueInt)builder.getInt("max_visible_items", 8, 2, 30).clientSide();
      menuWidth = (ValueInt)builder.getInt("menu_width", 0, 0, 800).clientSide();
      menuHeight = (ValueInt)builder.getInt("menu_height", 0, 0, 600).clientSide();
      menuOffsetX = (ValueInt)builder.getInt("menu_offset_x", 0, -500, 500).clientSide();
      menuOffsetY = (ValueInt)builder.getInt("menu_offset_y", 0, -500, 500).clientSide();
      builder.category("tabpad");
      builder.getCategory().markClientSide();
      tabSelectMode = (ValueInt)builder.getInt("tab_select_mode", 2, 1, 3).clientSide();
      lockArrows = (ValueBoolean)builder.getBoolean("lock_arrows", false).clientSide();
      builder.category("doc_panel");
      builder.getCategory().markClientSide();
      docPanelScale = (ValueFloat)builder.getFloat("doc_panel_scale", 1.0F, 0.0F, 2.0F).clientSide();
      docPanelWidth = (ValueInt)builder.getInt("doc_panel_width", 240, 80, 600).clientSide();
      docPanelHeight = (ValueInt)builder.getInt("doc_panel_height", 200, 60, 800).clientSide();
      builder.category("autocomplete_colors");
      builder.getCategory().markClientSide();
      acVar = (ValueInt)builder.getInt("var", AutoCompleteConfig.AutoCompleteColors.COLOR_VAR).clientSide();
      acArray = (ValueInt)builder.getInt("array", AutoCompleteConfig.AutoCompleteColors.COLOR_ARRAY).clientSide();
      acObject = (ValueInt)builder.getInt("object", AutoCompleteConfig.AutoCompleteColors.COLOR_OBJECT).clientSide();
      acFunction = (ValueInt)builder.getInt("function", AutoCompleteConfig.AutoCompleteColors.COLOR_FUNCTION).clientSide();
      acKeyword = (ValueInt)builder.getInt("keyword", AutoCompleteConfig.AutoCompleteColors.COLOR_KEYWORD).clientSide();
      acIScript = (ValueInt)builder.getInt("iscript", AutoCompleteConfig.AutoCompleteColors.COLOR_ISCRIPT).clientSide();
      acJava = (ValueInt)builder.getInt("java", AutoCompleteConfig.AutoCompleteColors.COLOR_JAVA).clientSide();
      acOther = (ValueInt)builder.getInt("other", AutoCompleteConfig.AutoCompleteColors.COLOR_OTHER).clientSide();
      acLib = (ValueInt)builder.getInt("lib", AutoCompleteConfig.AutoCompleteColors.COLOR_LIB).clientSide();
      acIcon = (ValueInt)builder.getInt("icon", AutoCompleteConfig.AutoCompleteColors.COLOR_ICON).clientSide();
      acVar.color();
      acArray.color();
      acObject.color();
      acFunction.color();
      acKeyword.color();
      acIScript.color();
      acJava.color();
      acOther.color();
      acLib.color();
      acIcon.color();
   }

   public static int getContextMenuMaxItems() {
      return contextMenuMaxItems == null ? 8 : Math.max(2, (Integer)contextMenuMaxItems.get());
   }

   public static float getDocPanelScale() {
      if (docPanelScale == null) {
         return 1.0F;
      } else {
         float value = (Float)docPanelScale.get();
         return value < 0.1F ? 0.0F : Math.min(value, 2.0F);
      }
   }

   public static int getMenuWidth() {
      return menuWidth == null ? 0 : (Integer)menuWidth.get();
   }

   public static int getMenuHeight() {
      return menuHeight == null ? 0 : (Integer)menuHeight.get();
   }

   public static int getMenuOffsetX() {
      return menuOffsetX == null ? 0 : (Integer)menuOffsetX.get();
   }

   public static int getMenuOffsetY() {
      return menuOffsetY == null ? 0 : (Integer)menuOffsetY.get();
   }

   public static int getDocPanelWidth() {
      return docPanelWidth == null ? 240 : Math.max(80, (Integer)docPanelWidth.get());
   }

   public static int getDocPanelHeight() {
      return docPanelHeight == null ? 200 : Math.max(60, (Integer)docPanelHeight.get());
   }

   public static int getTabSelectMode() {
      return tabSelectMode == null ? 2 : Math.min(3, Math.max(1, (Integer)tabSelectMode.get()));
   }

   public static boolean isLockArrows() {
      return lockArrows != null && (Boolean)lockArrows.get();
   }

   public static boolean isAutocompleteEnabled() {
      return autocompleteEnabled == null || (Boolean)autocompleteEnabled.get();
   }

   public static boolean isDocPanelEnabled() {
      return docPanelEnabled == null || (Boolean)docPanelEnabled.get();
   }

   public static String getNewScriptTemplate() {
      String template = newScriptTemplate == null ? DEFAULT_NEW_SCRIPT_TEMPLATE : (String)newScriptTemplate.get();
      if (template == null || template.trim().isEmpty()) {
         return decodeScriptTemplate(DEFAULT_NEW_SCRIPT_TEMPLATE);
      }

      return decodeScriptTemplate(template);
   }

   public static String getDefaultNewScriptTemplate() {
      return decodeScriptTemplate(DEFAULT_NEW_SCRIPT_TEMPLATE);
   }

   public static String decodeScriptTemplate(String template) {
      if (template == null) {
         return "";
      }

      return template.replace("\\r\\n", "\n").replace("\\n", "\n").replace("\\t", "\t");
   }

   public static void syncColors() {
      if (acVar != null) {
         AutoCompleteConfig.AutoCompleteColors.COLOR_VAR = (Integer)acVar.get();
      }

      if (acArray != null) {
         AutoCompleteConfig.AutoCompleteColors.COLOR_ARRAY = (Integer)acArray.get();
      }

      if (acObject != null) {
         AutoCompleteConfig.AutoCompleteColors.COLOR_OBJECT = (Integer)acObject.get();
      }

      if (acFunction != null) {
         AutoCompleteConfig.AutoCompleteColors.COLOR_FUNCTION = (Integer)acFunction.get();
      }

      if (acKeyword != null) {
         AutoCompleteConfig.AutoCompleteColors.COLOR_KEYWORD = (Integer)acKeyword.get();
      }

      if (acIScript != null) {
         AutoCompleteConfig.AutoCompleteColors.COLOR_ISCRIPT = (Integer)acIScript.get();
      }

      if (acJava != null) {
         AutoCompleteConfig.AutoCompleteColors.COLOR_JAVA = (Integer)acJava.get();
      }

      if (acOther != null) {
         AutoCompleteConfig.AutoCompleteColors.COLOR_OTHER = (Integer)acOther.get();
      }

      if (acLib != null) {
         AutoCompleteConfig.AutoCompleteColors.COLOR_LIB = (Integer)acLib.get();
      }

      if (acIcon != null) {
         AutoCompleteConfig.AutoCompleteColors.COLOR_ICON = (Integer)acIcon.get();
      }

   }
}
