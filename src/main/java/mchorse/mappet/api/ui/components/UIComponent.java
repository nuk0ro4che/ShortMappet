package mchorse.mappet.api.ui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;
import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mappet.api.ui.utils.UIContextItem;
import mchorse.mappet.api.ui.utils.UIKeybind;
import mchorse.mappet.api.ui.utils.UIUnit;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.utils.IconRegistry;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.Keybind;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.Interpolation;
import mchorse.mclib.utils.TextUtils;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.mappet.client.gui.utils.AnimatedUIComponentElement;
import mchorse.mappet.client.gui.utils.UIMorphTooltip;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_2497;
import net.minecraft.class_2499;
import net.minecraft.class_310;

public abstract class UIComponent implements INBTSerializable<class_2487> {
   public static final int DELAY = 200;
   public String id = "";
   public String tooltip = "";
   public class_2487 tooltipMorph;
   public boolean tooltipMorphEnabled;
   public String tooltipMorphText = "";
   public int tooltipMorphWidth = 150;
   public int tooltipMorphHeight = 142;
   public float tooltipMorphScale = 2.0F;
   public float tooltipMorphYaw;
   public float tooltipMorphPitch;
   public float tooltipMorphOffsetX;
   public float tooltipMorphOffsetY;
   public float tooltipMorphOffsetZ;
   public boolean visible = true;
   public boolean enabled = true;
   public int tooltipDirection;
   public int marginTop;
   public int marginBottom;
   public int marginLeft;
   public int marginRight;
   public UIUnit x = new UIUnit();
   public UIUnit y = new UIUnit();
   public UIUnit w = new UIUnit();
   public UIUnit h = new UIUnit();
   public int updateDelay = this.getDefaultUpdateDelay();
   
   public String callbackEvent = "";
   public String hoverEvent = "";
   public String hoverEnterEvent = "";
   public String hoverExitEvent = "";
   private boolean hasMoveTo;
   private float moveToX;
   private float moveToY;
   private int moveToDuration;
   private String moveToInterpolation = "sine_inout";
   private boolean hasRotateTo;
   private float rotateTo;
   private int rotateToDuration;
   private String rotateToInterpolation = "sine_inout";
   public List<UIKeybind> keybinds = new ArrayList();
   public List<UIContextItem> context = new ArrayList();
   protected Set<String> changedProperties = new HashSet();

   public UIComponent id(String id) {
      this.id = id;
      return this;
   }

   public UIComponent tooltip(String tooltip) {
      return this.tooltip(tooltip, 0);
   }

   public UIComponent tooltip(String tooltip, int direction) {
      this.change("Tooltip");
      this.tooltip = tooltip;
      this.tooltipDirection = direction;
      this.tooltipMorph = null;
      return this;
   }

   public UIComponent tooltip(AbstractMorph morph) {
      return this.tooltip(morph, "");
   }

   public UIComponent tooltip(AbstractMorph morph, String text) {
      this.change("TooltipMorph");
      this.tooltipMorph = morph == null ? null : MorphUtils.toNBT(morph);
      this.tooltipMorphEnabled = morph != null;
      this.tooltipMorphText = text == null ? "" : text;
      return this;
   }

   public UIComponent tooltipMorphEnabled(boolean enabled) {
      this.change("TooltipMorph");
      this.tooltipMorphEnabled = enabled;
      return this;
   }

   public UIComponent tooltipMorphSize(int width, int height) {
      this.change("TooltipMorph");
      this.tooltipMorphWidth = Math.max(32, width);
      this.tooltipMorphHeight = Math.max(32, height);
      return this;
   }

   public UIComponent tooltipMorphTransform(float scale, float yaw, float pitch, float x, float y, float z) {
      this.change("TooltipMorph");
      this.tooltipMorphScale = Math.max(0.05F, scale);
      this.tooltipMorphYaw = yaw;
      this.tooltipMorphPitch = pitch;
      this.tooltipMorphOffsetX = x;
      this.tooltipMorphOffsetY = y;
      this.tooltipMorphOffsetZ = z;
      return this;
   }

   
   public UIComponent callback(String function) {
      this.change("Events");
      this.callbackEvent = function == null ? "" : function.trim();
      return this;
   }

   
   public UIComponent hover(String function) {
      this.change("Events");
      this.hoverEvent = function == null ? "" : function.trim();
      return this;
   }

   
   public UIComponent hoverEnter(String function) {
      this.change("Events");
      this.hoverEnterEvent = function == null ? "" : function.trim();
      return this;
   }

   
   public UIComponent hoverExit(String function) {
      this.change("Events");
      this.hoverExitEvent = function == null ? "" : function.trim();
      return this;
   }

   @DiscardMethod
   public boolean hasEventScripts() {
      return !this.callbackEvent.isEmpty() || !this.hoverEvent.isEmpty() || !this.hoverEnterEvent.isEmpty() || !this.hoverExitEvent.isEmpty();
   }

   @DiscardMethod
   public String getEventScript(String event) {
      if ("Callback".equals(event)) return this.callbackEvent;
      if ("Hover".equals(event)) return this.hoverEvent;
      if ("HoverEnter".equals(event)) return this.hoverEnterEvent;
      if ("HoverExit".equals(event)) return this.hoverExitEvent;
      return "";
   }

   public UIComponent visible(boolean visible) {
      this.change("Visible");
      this.visible = visible;
      return this;
   }

   public UIComponent enabled(boolean enabled) {
      this.change("Enabled");
      this.enabled = enabled;
      return this;
   }

   public UIComponent margin(int margin) {
      this.change("Margin");
      this.marginTop = margin;
      this.marginBottom = margin;
      this.marginLeft = margin;
      this.marginRight = margin;
      return this;
   }

   public UIComponent marginTop(int margin) {
      this.change("Margin");
      this.marginTop = margin;
      return this;
   }

   public UIComponent marginBottom(int margin) {
      this.change("Margin");
      this.marginBottom = margin;
      return this;
   }

   public UIComponent marginLeft(int margin) {
      this.change("Margin");
      this.marginLeft = margin;
      return this;
   }

   public UIComponent marginRight(int margin) {
      this.change("Margin");
      this.marginRight = margin;
      return this;
   }



   public UIComponent keybind(int keyCode, String action, String label) {
      return this.keybind(keyCode, action, label, false, false, false);
   }

   public UIComponent keybind(int keyCode, String action, String label, boolean ctrl) {
      return this.keybind(keyCode, action, label, ctrl, false, false);
   }

   public UIComponent keybind(int keyCode, String action, String label, boolean ctrl, boolean shift) {
      return this.keybind(keyCode, action, label, ctrl, shift, false);
   }

   public UIComponent keybind(int keyCode, String action, String label, boolean ctrl, boolean shift, boolean alt) {
      this.change("Keybinds");
      this.keybinds.add(new UIKeybind(keyCode, action, label, UIKeybind.createModifier(shift, ctrl, alt)));
      return this;
   }

   public UIComponent context(String icon, String action, String label) {
      return this.context(icon, action, label, 0);
   }

   public UIComponent context(String icon, String action, String label, int color) {
      this.change("Context");
      this.context.add(new UIContextItem(icon, action, label, color));
      return this;
   }

   public UIComponent x(int value) {
      this.change("X");
      this.x.value = 0.0F;
      this.x.offset = value;
      this.x.relative = false;
      return this;
   }

   public UIComponent rx(float value) {
      return this.rx(value, 0);
   }

   public UIComponent rx(float value, int offset) {
      this.change("X");
      this.x.value = value;
      this.x.offset = offset;
      this.x.relative = true;
      return this;
   }

   public UIComponent y(int value) {
      this.change("Y");
      this.y.value = 0.0F;
      this.y.offset = value;
      this.y.relative = false;
      return this;
   }

   public UIComponent ry(float value) {
      return this.ry(value, 0);
   }

   public UIComponent ry(float value, int offset) {
      this.change("Y");
      this.y.value = value;
      this.y.offset = offset;
      this.y.relative = true;
      return this;
   }

   public UIComponent w(int value) {
      this.change("W");
      this.w.value = 0.0F;
      this.w.offset = value;
      this.w.relative = false;
      return this;
   }

   public UIComponent rw(float value) {
      return this.rw(value, 0);
   }

   public UIComponent rw(float value, int offset) {
      this.change("W");
      this.w.value = value;
      this.w.offset = offset;
      this.w.relative = true;
      return this;
   }

   public UIComponent h(int value) {
      this.change("H");
      this.h.value = 0.0F;
      this.h.offset = value;
      this.h.relative = false;
      return this;
   }

   public UIComponent rh(float value) {
      return this.rh(value, 0);
   }

   public UIComponent rh(float value, int offset) {
      this.change("H");
      this.h.value = value;
      this.h.offset = offset;
      this.h.relative = true;
      return this;
   }

   public UIComponent xy(int x, int y) {
      return this.x(x).y(y);
   }

   public UIComponent rxy(float x, float y) {
      return this.rx(x).ry(y);
   }

   public UIComponent wh(int w, int h) {
      return this.w(w).h(h);
   }

   public UIComponent rwh(float w, float h) {
      return this.rw(w).rh(h);
   }

   public UIComponent anchor(float anchor) {
      return this.anchor(anchor, anchor);
   }

   public UIComponent anchor(float anchorX, float anchorY) {
      return this.anchorX(anchorX).anchorY(anchorY);
   }

   public UIComponent anchorX(float anchor) {
      this.change("X");
      this.x.anchor = anchor;
      return this;
   }

   public UIComponent anchorY(float anchor) {
      this.change("Y");
      this.y.anchor = anchor;
      return this;
   }

   public UIComponent updateDelay(int updateDelay) {
      this.change("UpdateDelay");
      this.updateDelay = updateDelay;
      return this;
   }

   
   public UIComponent moveTo(float x, float y, int duration) {
      return this.moveTo(x, y, duration, "sine_inout");
   }

   public UIComponent moveTo(float x, float y, int duration, String interpolation) {
      this.change("MoveTo");
      this.hasMoveTo = true;
      this.moveToX = x;
      this.moveToY = y;
      this.moveToDuration = Math.max(0, duration);
      this.moveToInterpolation = this.normalizeInterpolation(interpolation);
      return this;
   }

   
   public UIComponent rotateTo(float rotation, int duration) {
      return this.rotateTo(rotation, duration, "sine_inout");
   }

   public UIComponent rotateTo(float rotation, int duration, String interpolation) {
      this.change("RotateTo");
      this.hasRotateTo = true;
      this.rotateTo = rotation;
      this.rotateToDuration = Math.max(0, duration);
      this.rotateToInterpolation = this.normalizeInterpolation(interpolation);
      return this;
   }

   @DiscardMethod
   private String normalizeInterpolation(String interpolation) {
      if (interpolation == null || interpolation.trim().isEmpty()) {
         return "sine_inout";
      }
      try {
         return Interpolation.valueOf(interpolation.trim().toUpperCase(Locale.ROOT).replace('-', '_')).name().toLowerCase(Locale.ROOT);
      } catch (IllegalArgumentException exception) {
         return "sine_inout";
      }
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   private Interpolation getInterpolation(String interpolation) {
      try {
         return Interpolation.valueOf((interpolation == null ? "sine_inout" : interpolation).toUpperCase(Locale.ROOT).replace('-', '_'));
      } catch (IllegalArgumentException exception) {
         return Interpolation.SINE_INOUT;
      }
   }

   @DiscardMethod
   protected int getDefaultUpdateDelay() {
      return 0;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected GuiElement apply(GuiElement element, UIContext context) {
      GuiElement layoutElement = element;
      AnimatedUIComponentElement animation = null;
      if (this.hasBaseAnimation()) {
         animation = new AnimatedUIComponentElement(class_310.method_1551(), element);
         layoutElement = animation;
         this.applyBaseAnimations(animation);
      }

      if (this.tooltipMorphEnabled && this.tooltipMorph != null) {
         this.applyMorphTooltip(element);
      } else if (!this.tooltip.isEmpty()) {
         this.applyTooltip(element);
      }

      element.setVisible(this.visible);
      element.setEnabled(this.enabled);
      element.marginTop(this.marginTop);
      element.marginBottom(this.marginBottom);
      element.marginLeft(this.marginLeft);
      element.marginRight(this.marginRight);
      this.x.apply(layoutElement.flex().x, context);
      this.y.apply(layoutElement.flex().y, context);
      this.w.apply(layoutElement.flex().w, context);
      this.h.apply(layoutElement.flex().h, context);
      if (!this.id.isEmpty()) {
         context.registerElement(this.id, element, this.isDataReserved());
      }

      this.applyKeybinds(element, context);
      this.applyContext(element, context);
      this.applyEvents(element, context);
      return layoutElement;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   private boolean hasBaseAnimation() {
      return (this.hasMoveTo || this.hasRotateTo) && !(this instanceof UIButtonComponent) && !(this instanceof UIIconComponent);
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   private void applyBaseAnimations(AnimatedUIComponentElement element) {
      if (this.hasMoveTo) {
         element.moveTo(this.moveToX, this.moveToY, this.moveToDuration, this.getInterpolation(this.moveToInterpolation));
      }
      if (this.hasRotateTo) {
         element.rotateTo(this.rotateTo, this.rotateToDuration, this.getInterpolation(this.rotateToInterpolation));
      }
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   private AnimatedUIComponentElement getBaseAnimation(GuiElement element) {
      return element != null && element.getParent() instanceof AnimatedUIComponentElement ? (AnimatedUIComponentElement)element.getParent() : null;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected GuiElement applyKeybinds(GuiElement element, UIContext context) {
      element.keys().keybinds.clear();

      for(UIKeybind keybind : this.keybinds) {
         Keybind key = element.keys().register(IKey.str(keybind.label), keybind.keyCode, () -> context.sendKey(keybind.action));
         List<Integer> held = new ArrayList();
         if (keybind.isCtrl()) {
            held.add(29);
         }

         if (keybind.isShift()) {
            held.add(42);
         }

         if (keybind.isAlt()) {
            held.add(56);
         }

         if (!held.isEmpty()) {
            key.held(held.stream().mapToInt((i) -> i).toArray());
         }
      }

      return element;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected boolean isDataReserved() {
      return false;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   private void applyEvents(GuiElement element, UIContext context) {
      if (!this.id.isEmpty() && this.hasEventScripts()) {
         mchorse.mappet.client.gui.utils.UIComponentEventTracker tracker = new mchorse.mappet.client.gui.utils.UIComponentEventTracker(class_310.method_1551(), this, context);
         element.add(tracker);
         tracker.flex().relative(element).wh(1.0F, 1.0F);
      }
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   private void applyMorphTooltip(GuiElement element) {
      AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(this.tooltipMorph);
      element.tooltip(new UIMorphTooltip(class_310.method_1551(), morph, this.tooltipMorphText, this.tooltipMorphWidth, this.tooltipMorphHeight, this.tooltipMorphScale, this.tooltipMorphYaw, this.tooltipMorphPitch, this.tooltipMorphOffsetX, this.tooltipMorphOffsetY, this.tooltipMorphOffsetZ, this.tooltipDirection));
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   private void applyTooltip(GuiElement element) {
      Direction direction = Direction.BOTTOM;
      if (this.tooltipDirection == 1) {
         direction = Direction.TOP;
      } else if (this.tooltipDirection == 2) {
         direction = Direction.RIGHT;
      } else if (this.tooltipDirection == 3) {
         direction = Direction.LEFT;
      }

      if (this.tooltip.trim().isEmpty()) {
         element.tooltip = null;
      } else {
         element.tooltip(IKey.str(TextUtils.processColoredText(this.tooltip)), direction);
      }

   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   private void applyContext(GuiElement element, UIContext context) {
      if (this.context.isEmpty()) {
         this.resetContext(element, context);
      } else {
         element.context(() -> {
            GuiSimpleContextMenu menu = new GuiSimpleContextMenu(class_310.method_1551());
            this.createContext(menu, element, context);
            return menu;
         });
      }

   }

   @Environment(EnvType.CLIENT)
   protected void resetContext(GuiElement element, UIContext context) {
      element.context((Supplier)null);
   }

   @Environment(EnvType.CLIENT)
   protected void createContext(GuiSimpleContextMenu menu, GuiElement element, UIContext context) {
      for(UIContextItem item : this.context) {
         Runnable runnable = () -> context.sendContext(item.action);
         Icon icon = (Icon)IconRegistry.icons.get(item.icon);
         if (icon == null) {
            icon = Icons.NONE;
         }

         if (item.color > 0) {
            menu.action(icon, IKey.str(item.label), runnable, item.color);
         } else {
            menu.action(icon, IKey.str(item.label), runnable);
         }
      }

   }

   @DiscardMethod
   protected void change(String... properties) {
      this.changedProperties.addAll(Arrays.asList(properties));
   }

   @DiscardMethod
   public void clearChanges() {
      this.changedProperties.clear();
   }

   @DiscardMethod
   public Set<String> getChanges() {
      return Collections.unmodifiableSet(this.changedProperties);
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public void handleChanges(UIContext context, class_2487 changes, GuiElement element) {
      this.deserializeNBT(changes);

      for(String key : changes.method_10541()) {
         this.applyProperty(context, key, element);
      }

   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected void applyProperty(UIContext context, String key, GuiElement element) {
      if (key.equals("TooltipMorph")) {
         if (this.tooltipMorphEnabled && this.tooltipMorph != null) {
            this.applyMorphTooltip(element);
         } else {
            this.applyTooltip(element);
         }
      } else if (key.equals("Tooltip")) {
         this.applyTooltip(element);
      } else if (key.equals("Visible")) {
         element.setVisible(this.visible);
      } else if (key.equals("Enabled")) {
         element.setEnabled(this.enabled);
      } else if (key.equals("Margin")) {
         element.marginTop(this.marginTop);
         element.marginBottom(this.marginBottom);
         element.marginLeft(this.marginLeft);
         element.marginRight(this.marginRight);
      } else if (key.equals("X") || key.equals("Y") || key.equals("W") || key.equals("H")) {
         GuiElement layoutElement = this.getBaseAnimation(element);
         if (layoutElement == null) {
            layoutElement = element;
         }
         if (key.equals("X")) this.x.apply(layoutElement.flex().x, context);
         if (key.equals("Y")) this.y.apply(layoutElement.flex().y, context);
         if (key.equals("W")) this.w.apply(layoutElement.flex().w, context);
         if (key.equals("H")) this.h.apply(layoutElement.flex().h, context);
      } else if (key.equals("MoveTo") || key.equals("RotateTo")) {
         AnimatedUIComponentElement animation = this.getBaseAnimation(element);
         if (animation != null) {
            this.applyBaseAnimations(animation);
         }
      } else if (key.equals("Keybinds")) {
         this.applyKeybinds(element, context);
      } else if (key.equals("Context")) {
         this.applyContext(element, context);
      }

   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public abstract GuiElement create(class_310 var1, UIContext var2);

   @DiscardMethod
   public void populateData(class_2487 tag) {
   }

   @DiscardMethod
   public List<UIComponent> getChildComponents() {
      return Collections.emptyList();
   }

   @DiscardMethod
   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      this.serializeNBT(tag);
      return tag;
   }

   @DiscardMethod
   public void serializeNBT(class_2487 tag) {
      tag.method_10582("Id", this.id);
      class_2487 tooltip = new class_2487();
      tooltip.method_10582("Label", this.tooltip);
      tooltip.method_10569("Direction", this.tooltipDirection);
      tag.method_10566("Tooltip", tooltip);
      if (this.tooltipMorph != null) {
         tag.method_10566("TooltipMorph", this.tooltipMorph);
         tag.method_10582("TooltipMorphText", this.tooltipMorphText);
         class_2487 tooltipMorphSettings = new class_2487();
         tooltipMorphSettings.method_10556("Enabled", this.tooltipMorphEnabled);
         tooltipMorphSettings.method_10569("Width", this.tooltipMorphWidth);
         tooltipMorphSettings.method_10569("Height", this.tooltipMorphHeight);
         tooltipMorphSettings.method_10548("Scale", this.tooltipMorphScale);
         tooltipMorphSettings.method_10548("Yaw", this.tooltipMorphYaw);
         tooltipMorphSettings.method_10548("Pitch", this.tooltipMorphPitch);
         tooltipMorphSettings.method_10548("OffsetX", this.tooltipMorphOffsetX);
         tooltipMorphSettings.method_10548("OffsetY", this.tooltipMorphOffsetY);
         tooltipMorphSettings.method_10548("OffsetZ", this.tooltipMorphOffsetZ);
         tag.method_10566("TooltipMorphSettings", tooltipMorphSettings);
      }
      tag.method_10556("Visible", this.visible);
      tag.method_10556("Enabled", this.enabled);
      class_2499 margins = new class_2499();
      margins.add(class_2497.method_23247(this.marginTop));
      margins.add(class_2497.method_23247(this.marginBottom));
      margins.add(class_2497.method_23247(this.marginLeft));
      margins.add(class_2497.method_23247(this.marginRight));
      tag.method_10566("Margin", margins);
      tag.method_10566("X", this.x.serializeNBT());
      tag.method_10566("Y", this.y.serializeNBT());
      tag.method_10566("W", this.w.serializeNBT());
      tag.method_10566("H", this.h.serializeNBT());
      tag.method_10569("UpdateDelay", this.updateDelay);
      if (this.hasEventScripts()) {
         class_2487 events = new class_2487();
         events.method_10582("Callback", this.callbackEvent);
         events.method_10582("Hover", this.hoverEvent);
         events.method_10582("HoverEnter", this.hoverEnterEvent);
         events.method_10582("HoverExit", this.hoverExitEvent);
         tag.method_10566("EventScripts", events);
      }
      if (this.hasMoveTo) {
         class_2487 move = new class_2487();
         move.method_10548("X", this.moveToX);
         move.method_10548("Y", this.moveToY);
         move.method_10569("Duration", this.moveToDuration);
         move.method_10582("Interpolation", this.moveToInterpolation);
         tag.method_10566("MoveTo", move);
      }
      if (this.hasRotateTo) {
         class_2487 rotation = new class_2487();
         rotation.method_10548("Angle", this.rotateTo);
         rotation.method_10569("Duration", this.rotateToDuration);
         rotation.method_10582("Interpolation", this.rotateToInterpolation);
         tag.method_10566("RotateTo", rotation);
      }
      class_2499 keybinds = new class_2499();

      for(UIKeybind keybind : this.keybinds) {
         keybinds.add(keybind.serializeNBT());
      }

      tag.method_10566("Keybinds", keybinds);
      class_2499 context = new class_2499();

      for(UIContextItem contextItem : this.context) {
         context.add(contextItem.serializeNBT());
      }

      tag.method_10566("Context", context);
   }

   @DiscardMethod
   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10545("Id")) {
         this.id = tag.method_10558("Id");
      }

      if (tag.method_10573("Tooltip", 10)) {
         class_2487 tooltip = tag.method_10562("Tooltip");
         this.tooltip = tooltip.method_10558("Label");
         this.tooltipDirection = tooltip.method_10550("Direction");
      }

      if (tag.method_10573("TooltipMorph", 10)) {
         this.tooltipMorph = tag.method_10562("TooltipMorph");
         this.tooltipMorphText = tag.method_10545("TooltipMorphText") ? tag.method_10558("TooltipMorphText") : "";
         if (tag.method_10573("TooltipMorphSettings", 10)) {
            class_2487 tooltipMorphSettings = tag.method_10562("TooltipMorphSettings");
            this.tooltipMorphEnabled = tooltipMorphSettings.method_10545("Enabled") ? tooltipMorphSettings.method_10577("Enabled") : true;
            this.tooltipMorphWidth = tooltipMorphSettings.method_10545("Width") ? tooltipMorphSettings.method_10550("Width") : 150;
            this.tooltipMorphHeight = tooltipMorphSettings.method_10545("Height") ? tooltipMorphSettings.method_10550("Height") : 142;
            this.tooltipMorphScale = tooltipMorphSettings.method_10545("Scale") ? tooltipMorphSettings.method_10583("Scale") : 2.0F;
            this.tooltipMorphYaw = tooltipMorphSettings.method_10545("Yaw") ? tooltipMorphSettings.method_10583("Yaw") : 0.0F;
            this.tooltipMorphPitch = tooltipMorphSettings.method_10545("Pitch") ? tooltipMorphSettings.method_10583("Pitch") : 0.0F;
            this.tooltipMorphOffsetX = tooltipMorphSettings.method_10545("OffsetX") ? tooltipMorphSettings.method_10583("OffsetX") : 0.0F;
            this.tooltipMorphOffsetY = tooltipMorphSettings.method_10545("OffsetY") ? tooltipMorphSettings.method_10583("OffsetY") : 0.0F;
            this.tooltipMorphOffsetZ = tooltipMorphSettings.method_10545("OffsetZ") ? tooltipMorphSettings.method_10583("OffsetZ") : 0.0F;
         } else {
            this.tooltipMorphEnabled = true;
         }
      }

      if (tag.method_10545("Visible")) {
         this.visible = tag.method_10577("Visible");
      }

      if (tag.method_10545("Enabled")) {
         this.enabled = tag.method_10577("Enabled");
      }

      if (tag.method_10545("Margin")) {
         class_2499 margins = tag.method_10554("Margin", 3);
         if (margins.size() >= 4) {
            this.marginTop = margins.method_10600(0);
            this.marginBottom = margins.method_10600(1);
            this.marginLeft = margins.method_10600(2);
            this.marginRight = margins.method_10600(3);
         }
      }

      if (tag.method_10545("X")) {
         this.x.deserializeNBT(tag.method_10562("X"));
      }

      if (tag.method_10545("Y")) {
         this.y.deserializeNBT(tag.method_10562("Y"));
      }

      if (tag.method_10545("W")) {
         this.w.deserializeNBT(tag.method_10562("W"));
      }

      if (tag.method_10545("H")) {
         this.h.deserializeNBT(tag.method_10562("H"));
      }

            if (tag.method_10545("UpdateDelay")) {
         this.updateDelay = tag.method_10550("UpdateDelay");
      }

      if (tag.method_10573("EventScripts", 10)) {
         class_2487 events = tag.method_10562("EventScripts");
         this.callbackEvent = events.method_10558("Callback");
         this.hoverEvent = events.method_10558("Hover");
         this.hoverEnterEvent = events.method_10558("HoverEnter");
         this.hoverExitEvent = events.method_10558("HoverExit");
      }

      if (tag.method_10573("MoveTo", 10)) {
         class_2487 move = tag.method_10562("MoveTo");
         this.hasMoveTo = true;
         this.moveToX = move.method_10583("X");
         this.moveToY = move.method_10583("Y");
         this.moveToDuration = move.method_10550("Duration");
         this.moveToInterpolation = move.method_10545("Interpolation") ? this.normalizeInterpolation(move.method_10558("Interpolation")) : "sine_inout";
      }
      if (tag.method_10573("RotateTo", 10)) {
         class_2487 rotation = tag.method_10562("RotateTo");
         this.hasRotateTo = true;
         this.rotateTo = rotation.method_10583("Angle");
         this.rotateToDuration = rotation.method_10550("Duration");
         this.rotateToInterpolation = rotation.method_10545("Interpolation") ? this.normalizeInterpolation(rotation.method_10558("Interpolation")) : "sine_inout";
      }

      if (tag.method_10545("Keybinds")) {
         this.keybinds.clear();
         class_2499 keybinds = tag.method_10554("Keybinds", 10);
         int i = 0;

         for(int c = keybinds.size(); i < c; ++i) {
            UIKeybind keybind = new UIKeybind();
            keybind.deserializeNBT(keybinds.method_10602(i));
            this.keybinds.add(keybind);
         }
      }

      if (tag.method_10545("Context")) {
         this.context.clear();
         class_2499 context = tag.method_10554("Context", 10);
         int i = 0;

         for(int c = context.size(); i < c; ++i) {
            UIContextItem contextItem = new UIContextItem();
            contextItem.deserializeNBT(context.method_10602(i));
            this.context.add(contextItem);
         }
      }

   }
}
