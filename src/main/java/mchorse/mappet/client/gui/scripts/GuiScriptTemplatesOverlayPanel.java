package mchorse.mappet.client.gui.scripts;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlayPanel;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringSearchListElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiScriptTemplatesOverlayPanel extends GuiOverlayPanel {
   private static final String KEYS = """
function getKeys() {
    return {
        'Left click': -100,
        'Right click': -99,
        'Scroll wheel': -98,
        'Mouse 4': -97,
        'Mouse 5': -96,
        'Mouse 6': -95,
        'Mouse 7': -94,
        'Mouse 8': -93,
        'Unknown': 0,
        'Number 1': 2,
        'Number 2': 3,
        'Number 3': 4,
        'Number 4': 5,
        'Number 5': 6,
        'Number 6': 7,
        'Number 7': 8,
        'Number 8': 9,
        'Number 9': 10,
        'Number 0': 11,
        'Minus': 12,
        'Equal': 13,
        'Backspace': 14,
        'Tab': 15,
        'Button Q': 16,
        'Button W': 17,
        'Button E': 18,
        'Button R': 19,
        'Button T': 20,
        'Button Y': 21,
        'Button U': 22,
        'Button I': 23,
        'Button O': 24,
        'Button P': 25,
        'Left bracket': 26,
        'Right bracket': 27,
        'Enter': 28,
        'Left CTRL': 29,
        'Button A': 30,
        'Button S': 31,
        'Button D': 32,
        'Button F': 33,
        'Button G': 34,
        'Button H': 35,
        'Button J': 36,
        'Button K': 37,
        'Button L': 38,
        'Semicolon': 39,
        'Apostrophe': 40,
        'Grave accent': 41,
        'Left shift': 42,
        'Backslash': 43,
        'Button Z': 44,
        'Button X': 45,
        'Button C': 46,
        'Button V': 47,
        'Button B': 48,
        'Button N': 49,
        'Button M': 50,
        'Comma': 51,
        'Period': 52,
        'Slash': 53,
        'Right shift': 54,
        'Numpad multiply': 55,
        'Left ALT': 56,
        'Space': 57,
        'Caps lock': 58,
        'Button F1': 59,
        'Button F2': 60,
        'Button F3': 61,
        'Button F4': 62,
        'Button F5': 63,
        'Button F6': 64,
        'Button F7': 65,
        'Button F8': 66,
        'Button F9': 67,
        'Button F10': 68,
        'Num lock': 69,
        'Scroll lock': 70,
        'Numpad 7': 71,
        'Numpad 8': 72,
        'Numpad 9': 73,
        'Numpad subtract': 74,
        'Numpad 4': 75,
        'Numpad 5': 76,
        'Numpad 6': 77,
        'Numpad add': 78,
        'Numpad 1': 79,
        'Numpad 2': 80,
        'Numpad 3': 81,
        'Numpad 0': 82,
        'Numpad decimal': 83,
        'Button F11': 87,
        'Button F12': 88,
        'Button F13': 100,
        'Button F14': 101,
        'Button F15': 102,
        'Button F16': 103,
        'Button F17': 104,
        'Button F18': 105,
        'Button F19': 113,
        'Equal': 141,
        'Center': 156,
        'Right CTRL': 157,
        'Divide': 181,
        'Print screen': 183,
        'Right ALT': 184,
        'Pause': 197,
        'Home': 199,
        'Up': 200,
        'Page up': 201,
        'Left': 203,
        'Right': 205,
        'End': 207,
        'Down': 208,
        'Page down': 209,
        'Insert': 210,
        'Delete': 211,
        'Left WIN': 219,
        'Right WIN': 220,
        'Menu': 221
    }
}
""";
   private final Map<String, String> templates = new LinkedHashMap();
   private final GuiStringSearchListElement list;
   private final GuiButtonElement select;
   private final Consumer<String> callback;

   public GuiScriptTemplatesOverlayPanel(class_310 mc, Consumer<String> callback) {
      super(mc, IKey.lang("mappet.gui.scripts.templates.title"));
      this.callback = callback;
      this.templates.put("keys", KEYS);
      ScriptTemplateManager.loadCustomTemplates(this.templates);
      this.list = new GuiStringSearchListElement(mc, (list) -> {
      });
      this.select = new GuiButtonElement(mc, IKey.lang("mappet.gui.scripts.templates.select"), (button) -> this.selectTemplate());
      this.list.label = IKey.lang("mappet.gui.search");
      this.list.list.add(this.templates.keySet());
      this.list.list.sort();
      this.list.flex().relative(this.content).w(1.0F).h(1.0F, -30);
      this.select.flex().relative(this.content).x(1.0F).y(1.0F, -4).w(0.48F).anchor(1.0F, 1.0F);
      this.content.add(new IGuiElement[]{this.list, this.select});
   }

   private void selectTemplate() {
      if (this.list.list.isDeselected()) {
         return;
      }

      String selected = (String)this.list.list.getCurrentFirst();
      String template = (String)this.templates.get(selected);

      this.close();
      if (template != null && this.callback != null) {
         this.callback.accept(template);
      }
   }
}
