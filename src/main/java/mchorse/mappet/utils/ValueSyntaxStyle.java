package mchorse.mappet.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.mappet.client.gui.scripts.themes.Themes;
import mchorse.mappet.client.gui.scripts.utils.SyntaxStyle;
import mchorse.mclib.config.values.Value;

public class ValueSyntaxStyle extends Value {
   private SyntaxStyle style = new SyntaxStyle();
   private String file = "k0ro4che.json";

   public ValueSyntaxStyle(String id) {
      super(id);
      this.clientSide();
      this.loadDefaultTheme();
   }

   public SyntaxStyle get() {
      return this.style;
   }

   public String getFile() {
      return this.file;
   }

   public void set(String file, SyntaxStyle style) {
      this.file = file;
      this.style = new SyntaxStyle(style.toNBT());
      this.saveLater();
   }

   public void valueFromJSON(JsonElement element) {
      String file = element == null || !element.isJsonPrimitive() ? "k0ro4che.json" : element.getAsString();
      SyntaxStyle style = Themes.readTheme(Themes.themeFile(file));
      if (style == null) {
         file = "k0ro4che.json";
         style = Themes.readTheme(Themes.themeFile(file));
      }

      if (style != null) {
         this.style = style;
         this.file = file;
      } else {
         this.loadDefaultTheme();
      }

   }

   private void loadDefaultTheme() {
      String defaultFile = "k0ro4che.json";
      SyntaxStyle defaultStyle = Themes.readTheme(Themes.themeFile(defaultFile));
      if (defaultStyle != null) {
         this.style = defaultStyle;
         this.file = defaultFile;
      }
   }

   public JsonElement valueToJSON() {
      return new JsonPrimitive(this.file);
   }

   public void copy(Value value) {
      if (value instanceof ValueSyntaxStyle config) {
         this.file = config.file;
         this.style = new SyntaxStyle(config.style.toNBT());
      }

   }
}
