package mchorse.mappet.client.gui.panels;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.Mappet;
import mchorse.mappet.compat.NbtCompat;
import mchorse.mappet.api.ui.UIFile;
import mchorse.mappet.api.ui.components.UIButtonComponent;
import mchorse.mappet.api.ui.components.UIComponent;
import mchorse.mappet.api.ui.components.UIClickComponent;
import mchorse.mappet.api.ui.components.UIColorComponent;
import mchorse.mappet.api.ui.components.UIGraphicsComponent;
import mchorse.mappet.api.ui.components.UIIconComponent;
import mchorse.mappet.api.ui.components.UILabelBaseComponent;
import mchorse.mappet.api.ui.components.UILabelComponent;
import mchorse.mappet.api.ui.components.UILayoutComponent;
import mchorse.mappet.api.ui.components.UIMorphComponent;
import mchorse.mappet.api.ui.components.UIParentComponent;
import mchorse.mappet.api.ui.components.UIStackComponent;
import mchorse.mappet.api.ui.components.UIStringListComponent;
import mchorse.mappet.api.ui.components.UITextComponent;
import mchorse.mappet.api.ui.components.UITextboxComponent;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mappet.api.ui.components.UITextareaComponent;
import mchorse.mappet.api.ui.components.UIToggleComponent;
import mchorse.mappet.api.ui.components.UITrackpadComponent;
import mchorse.mappet.api.ui.utils.LayoutType;
import mchorse.mappet.api.ui.utils.UIUnit;
import mchorse.mappet.api.utils.ContentType;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.scripts.GuiTextEditor;
import mchorse.mappet.client.gui.utils.graphics.GradientGraphic;
import mchorse.mappet.client.gui.utils.graphics.Graphic;
import mchorse.mappet.client.gui.utils.graphics.IconGraphic;
import mchorse.mappet.client.gui.utils.graphics.RectGraphic;
import mchorse.mappet.client.gui.utils.graphics.ShadowGraphic;
import mchorse.mappet.client.gui.utils.graphics.TextGraphic;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.ui.PacketUIPreview;
import mchorse.mappet.client.gui.ui.GuiUIEditorCanvas;
import mchorse.mappet.client.gui.ui.GuiUIEditorIconGrid;
import mchorse.mappet.client.gui.ui.GuiUIMorphTooltipPreview;
import mchorse.mappet.client.gui.ui.GuiUIComponentHierarchyList;
import mchorse.mappet.client.gui.ui.GuiUIComponentPaletteButton;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.IconRegistry;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.class_1799;
import net.minecraft.class_2487;
import net.minecraft.class_2522;
import net.minecraft.class_310;






public class GuiUIFilePanel extends GuiMappetDashboardPanel<UIFile> {
   private enum InspectorTab {
      FILE,
      GENERAL,
      CONTENT,
      ANIMATION,
      GRAPHICS
   }

   private final GuiElement palette;
   private final GuiScrollElement paletteScroll;
   private final GuiElement hierarchyPanel;
   private final GuiUIEditorCanvas preview;
   private final GuiElement properties;
   private final GuiElement propertyTabs;
   private final GuiButtonElement tabFile;
   private final GuiButtonElement tabGeneral;
   private final GuiButtonElement tabContent;
   private final GuiButtonElement tabAnimation;
   private final GuiButtonElement tabGraphics;
   private final GuiScrollElement propertyScroll;
   private final GuiUIComponentHierarchyList hierarchy;
   private final GuiTextElement script;
   private final GuiTextElement function;
   private final GuiTextElement componentId;
   private final GuiTextElement label;
   private final GuiTrackpadElement x;
   private final GuiTrackpadElement y;
   private final GuiTrackpadElement w;
   private final GuiTrackpadElement h;
   private final GuiElement positionPair;
   private final GuiToggleElement relativePosition;
   private final GuiElement moveToPair;
   private final GuiElement scaleToPair;
   private final GuiElement sizePair;
   private final GuiToggleElement relativeSize;
   private final GuiElement anchorPair;
   private final GuiToggleElement background;
   private final GuiToggleElement closable;
   private final GuiToggleElement paused;
   private final GuiToggleElement visible;
   private final GuiToggleElement enabled;
   private final GuiToggleElement relativeX;
   private final GuiToggleElement relativeY;
   private final GuiToggleElement relativeW;
   private final GuiToggleElement relativeH;
   private final GuiTrackpadElement anchorX;
   private final GuiTrackpadElement anchorY;
   private final GuiTextElement tooltip;
   private final GuiTrackpadElement tooltipDirection;
   private final GuiTrackpadElement marginTop;
   private final GuiTrackpadElement marginBottom;
   private final GuiTrackpadElement marginLeft;
   private final GuiTrackpadElement marginRight;
   private final GuiTrackpadElement updateDelay;
   private final GuiTrackpadElement moveToX;
   private final GuiTrackpadElement moveToY;
   private final GuiTrackpadElement moveToDuration;
   private final GuiTextElement moveToInterpolation;
   private final GuiTrackpadElement rotateToAngle;
   private final GuiTrackpadElement rotateToDuration;
   private final GuiTextElement rotateToInterpolation;
   private String moveInterpolationValue = "sine_inout";
   private String rotateInterpolationValue = "sine_inout";
   private final GuiButtonElement moveUp;
   private final GuiButtonElement moveDown;
   private final GuiButtonElement indent;
   private final GuiButtonElement outdent;
   private final GuiButtonElement removeComponent;
   private final GuiIconElement previewInGame;
   private final List<GuiElement> fileProperties = new ArrayList();
   private final List<GuiElement> baseComponentProperties = new ArrayList();
   private final List<GuiElement> animationProperties = new ArrayList();
   private final List<GuiElement> componentProperties = new ArrayList();
   private final List<GuiElement> labelProperties = new ArrayList();
   private final List<GuiElement> labelStyleProperties = new ArrayList();
   private final List<GuiElement> buttonProperties = new ArrayList();
   private final List<GuiElement> iconProperties = new ArrayList();
   private final List<GuiElement> textProperties = new ArrayList();
   private final List<GuiElement> inputProperties = new ArrayList();
   private final List<GuiElement> toggleProperties = new ArrayList();
   private final List<GuiElement> trackpadProperties = new ArrayList();
   private final List<GuiElement> colorProperties = new ArrayList();
   private final List<GuiElement> stringListProperties = new ArrayList();
   private final List<GuiElement> layoutProperties = new ArrayList();
   private final List<GuiElement> morphProperties = new ArrayList();
   private final List<GuiElement> morphTooltipProperties = new ArrayList();
   private final List<GuiElement> eventScriptProperties = new ArrayList();
   private final List<GuiElement> stackProperties = new ArrayList();
   private final List<GuiElement> visualAnimationProperties = new ArrayList();
   private final List<GuiElement> graphicsProperties = new ArrayList();
   private final GuiTrackpadElement textColor;
   private final GuiToggleElement textShadow;
   private final GuiToggleElement textBackground;
   private final GuiTrackpadElement labelAnchorX;
   private final GuiTrackpadElement labelAnchorY;
   private final GuiTrackpadElement textAnchor;
   private final GuiTrackpadElement buttonBackground;
   private final GuiTrackpadElement scaleToX;
   private final GuiTrackpadElement scaleToY;
   private final GuiTrackpadElement scaleToDuration;
   private final GuiTextElement scaleToInterpolation;
   private final GuiTrackpadElement colorToColor;
   private final GuiTrackpadElement colorToDuration;
   private final GuiTextElement colorToInterpolation;
   private String scaleInterpolationValue = "sine_inout";
   private String colorInterpolationValue = "sine_inout";
   private final GuiToggleElement buttonHover;
   private final GuiToggleElement buttonUnhover;
   private final GuiTextElement iconName;
   private final GuiTrackpadElement iconBackground;
   private final GuiTrackpadElement iconColor;
   private final GuiTrackpadElement inputMaxLength;
   private final GuiToggleElement toggleState;
   private final GuiTrackpadElement trackValue;
   private final GuiTrackpadElement trackMin;
   private final GuiTrackpadElement trackMax;
   private final GuiToggleElement trackInteger;
   private final GuiTrackpadElement trackNormal;
   private final GuiTrackpadElement trackWeak;
   private final GuiTrackpadElement trackStrong;
   private final GuiTrackpadElement trackIncrement;
   private final GuiColorElement colorValue;
   private final GuiTextEditor stringsValues;
   private final GuiTextEditor callbackEvent;
   private final GuiTextEditor hoverEvent;
   private final GuiTextEditor hoverEnterEvent;
   private final GuiTextEditor hoverExitEvent;
   private final GuiTrackpadElement stringsSelected;
   private final GuiTrackpadElement stringsBackground;
   private final GuiToggleElement layoutScroll;
   private final GuiTrackpadElement layoutScrollSize;
   private final GuiToggleElement layoutHorizontal;
   private final GuiTrackpadElement layoutType;
   private final GuiTrackpadElement layoutMargin;
   private final GuiTrackpadElement layoutPadding;
   private final GuiTrackpadElement layoutWidth;
   private final GuiTrackpadElement layoutItems;
   private final GuiTextElement morphNbt;
   private final GuiButtonElement morphSelect;
   private final GuiButtonElement morphEdit;
   private final GuiToggleElement morphEditing;
   private final GuiTrackpadElement morphPosX;
   private final GuiTrackpadElement morphPosY;
   private final GuiTrackpadElement morphPosZ;
   private final GuiTrackpadElement morphPitch;
   private final GuiTrackpadElement morphYaw;
   private final GuiTrackpadElement morphDistance;
   private final GuiTrackpadElement morphFov;
   private final GuiTextElement morphTooltip;
   private final GuiToggleElement morphUseTooltip;
   private final GuiButtonElement morphTooltipSelect;
   private final GuiButtonElement morphTooltipEdit;
   private final GuiButtonElement morphTooltipClear;
   private final GuiUIMorphTooltipPreview morphTooltipPreview;
   private final GuiTrackpadElement morphTooltipWidth;
   private final GuiTrackpadElement morphTooltipHeight;
   private final GuiTrackpadElement morphTooltipScale;
   private final GuiTrackpadElement morphTooltipYaw;
   private final GuiTrackpadElement morphTooltipPitch;
   private final GuiTrackpadElement morphTooltipOffsetX;
   private final GuiTrackpadElement morphTooltipOffsetY;
   private final GuiTrackpadElement morphTooltipOffsetZ;
   private final GuiTrackpadElement morphTooltipDirection;
   private final GuiElement morphTooltipSizePair;
   private final GuiElement morphTooltipRotationPair;
   private final GuiElement morphTooltipOffsetTriple;
   private final GuiElement morphPositionPair;
   private final GuiElement morphRotationPair;
   private final GuiTextElement stackNbt;
   private final GuiTextElement graphicsNbt;
   private final GuiUIEditorIconGrid iconPicker;
   private final GuiStringListElement graphicsList;
   private final GuiButtonElement graphicRect;
   private final GuiButtonElement graphicGradient;
   private final GuiButtonElement graphicText;
   private final GuiButtonElement graphicIcon;
   private final GuiButtonElement graphicShadow;
   private final GuiButtonElement graphicRemove;
   private final GuiTrackpadElement graphicX;
   private final GuiTrackpadElement graphicY;
   private final GuiTrackpadElement graphicW;
   private final GuiTrackpadElement graphicH;
   private final GuiTrackpadElement graphicRX;
   private final GuiTrackpadElement graphicRY;
   private final GuiTrackpadElement graphicRW;
   private final GuiTrackpadElement graphicRH;
   private final GuiTrackpadElement graphicAnchorX;
   private final GuiTrackpadElement graphicAnchorY;
   private final GuiTrackpadElement graphicPrimary;
   private final GuiToggleElement graphicHover;
   private final GuiTrackpadElement graphicSecondary;
   private final GuiToggleElement graphicHorizontal;
   private final GuiTextElement graphicTextValue;
   private final GuiUIEditorIconGrid graphicIconPicker;
   private final GuiTextElement graphicIconValue;
   private final GuiTrackpadElement graphicOffset;
   private final GuiElement graphicPositionPair;
   private final GuiElement graphicRelativePair;
   private final GuiElement graphicAnchorPair;
   private final Map<String, Graphic> graphicEntries = new LinkedHashMap();
   private Graphic selectedGraphic;
   private final Map<String, UIComponent> hierarchyEntries = new LinkedHashMap();
   private final GuiLabel hierarchyCaption;
   private UIComponent selected;
   private List<Integer> selectionPathBeforePreview;
   private InspectorTab inspectorTabBeforePreview;
   private InspectorTab inspectorTab = InspectorTab.FILE;
   private boolean fillingProperties;
   private boolean isDragging = false;
   private final Deque<class_2487> undoStack = new ArrayDeque<>(50);
   private final Deque<class_2487> redoStack = new ArrayDeque<>(50);

   public GuiUIFilePanel(class_310 mc, GuiMappetDashboard dashboard) {
      super(mc, dashboard);
      this.namesList.setFileIcon(Icons.DISABLED);
      this.palette = new GuiElement(mc);
      this.paletteScroll = new GuiScrollElement(mc);
      this.hierarchyPanel = new GuiElement(mc);
      this.preview = new GuiUIEditorCanvas(mc, this::pickCanvasComponent, this::canvasChanged, (component) -> this.createComponentContext());
      this.properties = new GuiElement(mc);
      this.propertyTabs = new GuiElement(mc);
      this.tabFile = this.inspectorTab(InspectorTab.FILE, "Файл");
      this.tabGeneral = this.inspectorTab(InspectorTab.GENERAL, "Общее");
      this.tabContent = this.inspectorTab(InspectorTab.CONTENT, "Тип");
      this.tabAnimation = this.inspectorTab(InspectorTab.ANIMATION, "Анимац.");
      this.tabGraphics = this.inspectorTab(InspectorTab.GRAPHICS, "Графика");
      

      this.propertyTabs.add(new IGuiElement[]{this.tabFile, this.tabGeneral, this.tabContent, this.tabAnimation});
      this.propertyScroll = new GuiScrollElement(mc);
      this.hierarchy = new GuiUIComponentHierarchyList(mc, (list) -> this.pickComponent((String)list.get(0)), (key) -> this.componentIcon((UIComponent)this.hierarchyEntries.get(key)), (key) -> this.componentColor((UIComponent)this.hierarchyEntries.get(key)));
      this.hierarchy.context(() -> this.createComponentContext());
      this.script = new GuiTextElement(mc, 1000, (value) -> {
         if (this.data != null && !this.fillingProperties) {
            this.data.script = value;
         }
      });
      this.function = new GuiTextElement(mc, 1000, (value) -> {
         if (this.data != null && !this.fillingProperties) {
            this.data.function = value.isEmpty() ? "main" : value;
         }
      });
      this.componentId = new GuiTextElement(mc, 1000, (value) -> {
         if (this.selected != null && !this.fillingProperties) {
            this.selected.id = value.trim();
            this.refreshEditor();
         }
      });
      this.label = new GuiTextElement(mc, 1000, (value) -> {
         if (this.selected instanceof UILabelBaseComponent && !this.fillingProperties) {
            ((UILabelBaseComponent)this.selected).label(value);
            this.rebuildPreview();
         }
      });
      this.x = this.coordinate(0);
      this.y = this.coordinate(1);
      this.w = this.coordinate(2);
      this.h = this.coordinate(3);
      this.positionPair = new GuiElement(mc).markContainer();
      this.positionPair.flex().h(20).row(2);
      this.positionPair.add(new IGuiElement[]{this.x, this.y});
      this.sizePair = this.axisPair(this.w, this.h);
      this.background = new GuiToggleElement(mc, IKey.lang("mappet.gui.ui_editor.background"), (button) -> {
         if (this.data != null) {
            this.data.background = button.isToggled();
         }
      });
      this.closable = new GuiToggleElement(mc, IKey.lang("mappet.gui.ui_editor.closable"), (button) -> {
         if (this.data != null) {
            this.data.closable = button.isToggled();
         }
      });
      this.paused = new GuiToggleElement(mc, IKey.lang("mappet.gui.ui_editor.paused"), (button) -> {
         if (this.data != null) {
            this.data.paused = button.isToggled();
         }
      });
      this.visible = new GuiToggleElement(mc, IKey.lang("mappet.gui.ui_editor.visible"), (button) -> {
         if (this.selected != null && !this.fillingProperties) {
            this.selected.visible(button.isToggled());
            this.rebuildPreview();
         }
      });
      this.enabled = new GuiToggleElement(mc, IKey.lang("mappet.gui.ui_editor.enabled"), (button) -> {
         if (this.selected != null && !this.fillingProperties) {
            this.selected.enabled(button.isToggled());
            this.rebuildPreview();
         }
      });
      this.relativeX = this.relativeToggle(mc, IKey.lang("mappet.gui.ui_editor.relative_x"), 0);
      this.relativeY = this.relativeToggle(mc, IKey.lang("mappet.gui.ui_editor.relative_y"), 1);
      this.relativePosition = new GuiToggleElement(mc, IKey.str("Относительно"), (button) -> this.setPositionRelative(button.isToggled()));
      this.relativeW = this.relativeToggle(mc, IKey.lang("mappet.gui.ui_editor.relative_w"), 2);
      this.relativeH = this.relativeToggle(mc, IKey.lang("mappet.gui.ui_editor.relative_h"), 3);
      this.relativeSize = new GuiToggleElement(mc, IKey.str("Относительно"), (button) -> this.setSizeRelative(button.isToggled()));
      this.anchorX = this.anchorTrackpad(mc, true);
      this.anchorY = this.anchorTrackpad(mc, false);
      this.anchorPair = this.axisPair(this.anchorX, this.anchorY);
      this.tooltip = new GuiTextElement(mc, 1000, (value) -> {
         if (this.selected != null && !this.fillingProperties) {
            this.selected.tooltip(value);
            this.rebuildPreview();
         }
      });
      this.tooltipDirection = this.componentNumber((value) -> this.selected.tooltipDirection = value.intValue()).limit(0.0D, 3.0D, true);
      this.marginTop = this.componentNumber((value) -> this.selected.marginTop(value.intValue())).integer();
      this.marginBottom = this.componentNumber((value) -> this.selected.marginBottom(value.intValue())).integer();
      this.marginLeft = this.componentNumber((value) -> this.selected.marginLeft(value.intValue())).integer();
      this.marginRight = this.componentNumber((value) -> this.selected.marginRight(value.intValue())).integer();
      this.updateDelay = this.componentNumber((value) -> this.selected.updateDelay(value.intValue())).limit(0.0D, 1000.0D, true);
      this.moveToX = this.componentNumber((value) -> this.applyMoveTo());
      this.moveToY = this.componentNumber((value) -> this.applyMoveTo());
      this.moveToDuration = this.componentNumber((value) -> this.applyMoveTo()).limit(0.0D, 1200.0D, true);
      this.moveToInterpolation = new GuiTextElement(mc, 100, (value) -> {
         if (!this.fillingProperties) {
            this.moveInterpolationValue = value.trim().isEmpty() ? "sine_inout" : value.trim();
            this.applyMoveTo();
         }
      });
      this.moveToPair = this.axisPair(this.moveToX, this.moveToY);
      this.rotateToAngle = this.componentNumber((value) -> this.applyRotateTo());
      this.rotateToDuration = this.componentNumber((value) -> this.applyRotateTo()).limit(0.0D, 1200.0D, true);
      this.rotateToInterpolation = new GuiTextElement(mc, 100, (value) -> {
         if (!this.fillingProperties) {
            this.rotateInterpolationValue = value.trim().isEmpty() ? "sine_inout" : value.trim();
            this.applyRotateTo();
         }
      });
      this.textColor = this.typedNumber((component, value) -> ((UILabelBaseComponent)component).color(value.intValue(), this.isTextShadowEnabled()));
      this.textShadow = this.typedToggle("Тень текста", (component, value) -> ((UILabelBaseComponent)component).color(this.textColorValue(), value));
      this.textBackground = this.typedToggle("Фон текста", (component, value) -> this.setTextBackground((UILabelBaseComponent)component, value));
      this.labelAnchorX = this.typedNumber((component, value) -> ((UILabelComponent)component).labelAnchor(value.floatValue(), this.labelAnchorYValue())).limit(0.0D, 1.0D);
      this.labelAnchorY = this.typedNumber((component, value) -> ((UILabelComponent)component).labelAnchor(this.labelAnchorXValue(), value.floatValue())).limit(0.0D, 1.0D);
      this.textAnchor = this.typedNumber((component, value) -> ((UITextComponent)component).textAnchor(value.floatValue())).limit(0.0D, 1.0D);
      this.buttonBackground = this.typedNumber((component, value) -> ((UIButtonComponent)component).background(value.intValue()));
      this.scaleToX = this.typedNumber((component, value) -> this.applyScaleTo(component));
      this.scaleToY = this.typedNumber((component, value) -> this.applyScaleTo(component));
      this.scaleToDuration = this.typedNumber((component, value) -> this.applyScaleTo(component)).limit(0.0D, 1200.0D, true);
      this.scaleToInterpolation = new GuiTextElement(mc, 100, (value) -> {
         if (!this.fillingProperties) {
            this.scaleInterpolationValue = value.trim().isEmpty() ? "sine_inout" : value.trim();
            if (this.selected != null) this.applyScaleTo(this.selected);
         }
      });
      this.scaleToPair = this.axisPair(this.scaleToX, this.scaleToY);
      this.colorToColor = this.typedNumber((component, value) -> this.applyColorTo(component));
      this.colorToDuration = this.typedNumber((component, value) -> this.applyColorTo(component)).limit(0.0D, 1200.0D, true);
      this.colorToInterpolation = new GuiTextElement(mc, 100, (value) -> {
         if (!this.fillingProperties) {
            this.colorInterpolationValue = value.trim().isEmpty() ? "sine_inout" : value.trim();
            if (this.selected != null) this.applyColorTo(this.selected);
         }
      });
      this.buttonHover = this.typedToggle("Событие hover", (component, value) -> ((UIButtonComponent)component).hoverEvents = value);
      this.buttonUnhover = this.typedToggle("Событие ухода", (component, value) -> ((UIButtonComponent)component).unhoverEvents = value);
      this.iconName = this.typedText((component, value) -> ((UIIconComponent)component).icon(value));
      this.iconPicker = new GuiUIEditorIconGrid(mc, (id) -> {
         if (this.selected instanceof UIIconComponent && !this.fillingProperties) {
            ((UIIconComponent)this.selected).icon(id);
            this.iconName.setText(id);
            this.rebuildPreview();
         }
      });
      this.iconPicker.flex().h(108);
      this.iconBackground = this.typedNumber((component, value) -> ((UIIconComponent)component).background(value.intValue()));
      this.iconColor = this.typedNumber((component, value) -> ((UIIconComponent)component).color(value.intValue()));
      this.inputMaxLength = this.typedNumber((component, value) -> ((UITextboxComponent)component).maxLength(value.intValue())).limit(1.0D, 10000.0D, true);
      this.toggleState = this.typedToggle("Начальное состояние", (component, value) -> ((UIToggleComponent)component).state(value));
      this.trackValue = this.typedNumber((component, value) -> ((UITrackpadComponent)component).value(value));
      this.trackMin = this.typedNumber((component, value) -> ((UITrackpadComponent)component).min(value));
      this.trackMax = this.typedNumber((component, value) -> ((UITrackpadComponent)component).max(value));
      this.trackInteger = this.typedToggle("Целые числа", (component, value) -> ((UITrackpadComponent)component).integer(value));
      this.trackNormal = this.typedNumber((component, value) -> ((UITrackpadComponent)component).amplitudes(value, ((UITrackpadComponent)component).weak == null ? value / 5.0D : ((UITrackpadComponent)component).weak, ((UITrackpadComponent)component).strong == null ? value * 5.0D : ((UITrackpadComponent)component).strong));
      this.trackWeak = this.typedNumber((component, value) -> ((UITrackpadComponent)component).amplitudes(((UITrackpadComponent)component).normal == null ? 1.0D : ((UITrackpadComponent)component).normal, value, ((UITrackpadComponent)component).strong == null ? 5.0D : ((UITrackpadComponent)component).strong));
      this.trackStrong = this.typedNumber((component, value) -> ((UITrackpadComponent)component).amplitudes(((UITrackpadComponent)component).normal == null ? 1.0D : ((UITrackpadComponent)component).normal, ((UITrackpadComponent)component).weak == null ? 0.2D : ((UITrackpadComponent)component).weak, value));
      this.trackIncrement = this.typedNumber((component, value) -> ((UITrackpadComponent)component).increment(value));
      this.colorValue = new GuiColorElement(mc, (color) -> {
         if (this.selected instanceof UIColorComponent && !this.fillingProperties) {
            ((UIColorComponent)this.selected).color(color);
            this.rebuildPreview();
         }
      });
      this.colorValue.picker.editAlpha();
      this.stringsValues = new GuiTextEditor(mc, (value) -> {
         if (this.selected instanceof UIStringListComponent && !this.fillingProperties) {
            ((UIStringListComponent)this.selected).values(java.util.Arrays.asList(value.split("\\n", -1)));
            this.rebuildPreview();
         }
      });
      this.stringsValues.setJavaScriptDiagnostics(false);
      this.stringsValues.flex().h(112);
      this.callbackEvent = this.eventScriptEditor((component, value) -> component.callbackEvent = value.trim());
      this.hoverEvent = this.eventScriptEditor((component, value) -> component.hoverEvent = value.trim());
      this.hoverEnterEvent = this.eventScriptEditor((component, value) -> component.hoverEnterEvent = value.trim());
      this.hoverExitEvent = this.eventScriptEditor((component, value) -> component.hoverExitEvent = value.trim());
      this.stringsSelected = this.typedNumber((component, value) -> ((UIStringListComponent)component).selected(value.intValue())).integer();
      this.stringsBackground = this.typedNumber((component, value) -> ((UIStringListComponent)component).background(value.intValue()));
      this.layoutScroll = this.typedToggle("Прокрутка", (component, value) -> ((UILayoutComponent)component).scroll = value);
      this.layoutScrollSize = this.typedNumber((component, value) -> ((UILayoutComponent)component).scrollSize(value.intValue())).integer();
      this.layoutHorizontal = this.typedToggle("Горизонтально", (component, value) -> ((UILayoutComponent)component).horizontal = value);
      this.layoutType = this.typedNumber((component, value) -> ((UILayoutComponent)component).layoutType = value.intValue() < 0 ? null : LayoutType.values()[Math.min(LayoutType.values().length - 1, value.intValue())]).limit(-1.0D, (double)(LayoutType.values().length - 1), true);
      this.layoutMargin = this.typedNumber((component, value) -> ((UILayoutComponent)component).margin = value.intValue()).integer();
      this.layoutPadding = this.typedNumber((component, value) -> ((UILayoutComponent)component).padding = value.intValue()).integer();
      this.layoutWidth = this.typedNumber((component, value) -> ((UILayoutComponent)component).width(value.intValue())).integer();
      this.layoutItems = this.typedNumber((component, value) -> ((UILayoutComponent)component).items(value.intValue())).integer();
      this.morphNbt = this.typedText((component, value) -> {
         try {
            ((UIMorphComponent)component).morph = class_2522.method_10718(value);
         } catch (Exception ignored) {
         }
      });
      this.morphNbt.tooltip(IKey.str("Данные выбранного морфа. Обычно изменять их вручную не требуется."));
      this.morphSelect = new GuiButtonElement(mc, IKey.str("Выбрать морф"), (button) -> this.openMorphSelector(false)).background(true);
      this.morphSelect.tooltip(IKey.str("Выбрать внешний вид морфа для компонента"));
      this.morphEdit = new GuiButtonElement(mc, IKey.str("Редактировать морф"), (button) -> this.openMorphSelector(true)).background(true);
      this.morphEdit.tooltip(IKey.str("Открыть выбранный морф в редакторе"));
      this.morphEditing = this.typedToggle("Разрешить редактирование", (component, value) -> ((UIMorphComponent)component).editing(value));
      this.morphEditing.tooltip(IKey.str("Разрешить изменение морфа игроком в открытом интерфейсе"));
      this.morphPosX = this.typedNumber((component, value) -> this.applyMorphPosition((UIMorphComponent)component));
      this.morphPosX.tooltip(IKey.str("Горизонтальное смещение камеры морфа"));
      this.morphPosY = this.typedNumber((component, value) -> this.applyMorphPosition((UIMorphComponent)component));
      this.morphPosY.tooltip(IKey.str("Вертикальное смещение камеры морфа"));
      this.morphPosZ = this.typedNumber((component, value) -> this.applyMorphPosition((UIMorphComponent)component));
      this.morphPosZ.tooltip(IKey.str("Смещение камеры морфа вперёд или назад"));
      this.morphPitch = this.typedNumber((component, value) -> this.applyMorphRotation((UIMorphComponent)component));
      this.morphPitch.tooltip(IKey.str("Наклон камеры вверх или вниз"));
      this.morphYaw = this.typedNumber((component, value) -> this.applyMorphRotation((UIMorphComponent)component));
      this.morphYaw.tooltip(IKey.str("Поворот камеры влево или вправо"));
      this.morphDistance = this.typedNumber((component, value) -> ((UIMorphComponent)component).distance(value.floatValue()));
      this.morphDistance.tooltip(IKey.str("Расстояние от камеры до морфа"));
      this.morphFov = this.typedNumber((component, value) -> ((UIMorphComponent)component).fov(value.floatValue()));
      this.morphFov.tooltip(IKey.str("Угол обзора камеры морфа"));
      this.morphTooltip = this.typedText((component, value) -> this.applyMorphTooltip(component, value));
      this.morphTooltip.tooltip(IKey.str("Текст, который показывается под моделью в подсказке морфа"));
      this.morphTooltip.flex().h(20);
      this.morphUseTooltip = new GuiToggleElement(mc, IKey.str("С морфом"), (button) -> this.toggleMorphTooltip(button.isToggled()));
      this.morphUseTooltip.tooltip(IKey.str("Показать в подсказке модель и текст в подсказке этого элемента"));
      this.morphTooltipSelect = new GuiButtonElement(mc, IKey.str("Выбрать морф подсказки"), (button) -> this.openMorphTooltipSelector(false)).background(true);
      this.morphTooltipSelect.tooltip(IKey.str("Выбрать модель, которая будет показана в подсказке"));
      this.morphTooltipEdit = new GuiButtonElement(mc, IKey.str("Редактировать морф подсказки"), (button) -> this.openMorphTooltipSelector(true)).background(true);
      this.morphTooltipEdit.tooltip(IKey.str("Открыть морф подсказки в редакторе"));
      this.morphTooltipClear = new GuiButtonElement(mc, IKey.str("Убрать морф подсказки"), (button) -> this.clearMorphTooltip()).background(true);
      this.morphTooltipClear.tooltip(IKey.str("Отключить Morph Tooltip и удалить выбранный морф"));
      this.morphTooltipPreview = new GuiUIMorphTooltipPreview(mc);
      this.morphTooltipPreview.flex().h(150);
      this.morphTooltipWidth = this.morphTooltipNumber().integer().limit(32.0D, 512.0D, true);
      this.morphTooltipHeight = this.morphTooltipNumber().integer().limit(48.0D, 512.0D, true);
      this.morphTooltipScale = this.morphTooltipNumber().limit(0.05D, 10.0D);
      this.morphTooltipYaw = this.morphTooltipNumber().limit(-180.0D, 180.0D);
      this.morphTooltipPitch = this.morphTooltipNumber().limit(-180.0D, 180.0D);
      this.morphTooltipOffsetX = this.morphTooltipNumber().limit(-10.0D, 10.0D);
      this.morphTooltipOffsetY = this.morphTooltipNumber().limit(-10.0D, 10.0D);
      this.morphTooltipOffsetZ = this.morphTooltipNumber().limit(-10.0D, 10.0D);
      this.morphTooltipDirection = this.morphTooltipNumber().integer().limit(0.0D, 3.0D, true);
      this.morphTooltipSizePair = this.axisPair(this.morphTooltipWidth, this.morphTooltipHeight);
      this.morphTooltipRotationPair = this.axisPair(this.morphTooltipYaw, this.morphTooltipPitch);
      this.morphTooltipOffsetTriple = this.axisTriple(this.morphTooltipOffsetX, this.morphTooltipOffsetY, this.morphTooltipOffsetZ);
      this.morphPositionPair = this.axisPair(this.morphPosX, this.morphPosY);
      this.morphRotationPair = this.axisPair(this.morphPitch, this.morphYaw);
      this.stackNbt = this.typedText((component, value) -> {
         try {
            ((UIStackComponent)component).stack(class_1799.method_7915(class_2522.method_10718(value)));
         } catch (Exception ignored) {
         }
      });
      this.graphicsNbt = this.typedText((component, value) -> {
         try {
            ((UIGraphicsComponent)component).deserializeNBT(class_2522.method_10718(value));
            this.refreshGraphicsList();
         } catch (Exception ignored) {
         }
      });
      this.graphicsList = new GuiStringListElement(mc, (values) -> {
         if (this.selected instanceof UIGraphicsComponent && !values.isEmpty()) {
            int index = this.graphicsListIndex();
            List<Graphic> graphics = ((UIGraphicsComponent)this.selected).graphics;
            this.selectedGraphic = index >= 0 && index < graphics.size() ? graphics.get(index) : null;
            this.fillGraphicProperties();
         }
      }) {
         public void mouseReleased(GuiContext context) {
            super.mouseReleased(context);
            GuiUIFilePanel.this.applyGraphicsListOrder();
         }
      };
      this.graphicsList.flex().h(92);
      this.graphicsList.sorting();
      this.graphicRect = new GuiButtonElement(mc, IKey.str("+ Прямоугольник"), (button) -> this.addGraphic(new RectGraphic(0, 0, 100, 20, -1))).background(true);
      this.graphicGradient = new GuiButtonElement(mc, IKey.str("+ Градиент"), (button) -> this.addGraphic(new GradientGraphic(0, 0, 100, 20, -1, -16777216, false))).background(true);
      this.graphicText = new GuiButtonElement(mc, IKey.str("+ Текст"), (button) -> this.addGraphic(new TextGraphic("Текст", 0, 0, 0, 0, -1, 0.0F, 0.0F))).background(true);
      this.graphicIcon = new GuiButtonElement(mc, IKey.str("+ Иконка"), (button) -> this.addGraphic(new IconGraphic("none", 0, 0, -1, 0.0F, 0.0F))).background(true);
      this.graphicShadow = new GuiButtonElement(mc, IKey.str("+ Тень"), (button) -> this.addGraphic(new ShadowGraphic(0, 0, 100, 20, -1, -16777216, 4))).background(true);
      this.graphicRemove = new GuiButtonElement(mc, IKey.str("Удалить графику"), (button) -> this.removeGraphic()).background(true);
      this.graphicX = this.graphicNumber((graphic, value) -> graphic.pixels.x = value.intValue()).integer();
      this.graphicY = this.graphicNumber((graphic, value) -> graphic.pixels.y = value.intValue()).integer();
      this.graphicW = this.graphicNumber((graphic, value) -> graphic.pixels.w = value.intValue()).integer();
      this.graphicH = this.graphicNumber((graphic, value) -> graphic.pixels.h = value.intValue()).integer();
      this.graphicRX = this.graphicNumber((graphic, value) -> graphic.relativeX = value.floatValue()).limit(0.0D, 1.0D);
      this.graphicRY = this.graphicNumber((graphic, value) -> graphic.relativeY = value.floatValue()).limit(0.0D, 1.0D);
      this.graphicRW = this.graphicNumber((graphic, value) -> graphic.relativeW = value.floatValue()).limit(0.0D, 1.0D);
      this.graphicRH = this.graphicNumber((graphic, value) -> graphic.relativeH = value.floatValue()).limit(0.0D, 1.0D);
      this.graphicAnchorX = this.graphicNumber((graphic, value) -> this.setGraphicAnchorX(graphic, value.floatValue())).limit(0.0D, 1.0D);
      this.graphicAnchorY = this.graphicNumber((graphic, value) -> this.setGraphicAnchorY(graphic, value.floatValue())).limit(0.0D, 1.0D);
      this.graphicPrimary = this.graphicNumber((graphic, value) -> graphic.primary = value.intValue());
      this.graphicHover = new GuiToggleElement(mc, IKey.str("Только при hover"), (button) -> {
         if (this.selectedGraphic != null && !this.fillingProperties) {
            this.selectedGraphic.hover = button.isToggled();
            this.refreshGraphicsPreview();
         }
      });
      this.graphicSecondary = this.graphicNumber((graphic, value) -> this.setGraphicSecondary(graphic, value.intValue()));
      this.graphicHorizontal = new GuiToggleElement(mc, IKey.str("Горизонтальный"), (button) -> {
         if (this.selectedGraphic instanceof GradientGraphic && !this.fillingProperties) {
            ((GradientGraphic)this.selectedGraphic).horizontal = button.isToggled();
            this.refreshGraphicsPreview();
         }
      });
      this.graphicTextValue = this.graphicText((graphic, value) -> {
         if (graphic instanceof TextGraphic) ((TextGraphic)graphic).text = value;
      });
      this.graphicIconValue = this.graphicText((graphic, value) -> {
         if (graphic instanceof IconGraphic) ((IconGraphic)graphic).icon(value);
      });
      this.graphicIconPicker = new GuiUIEditorIconGrid(mc, (id) -> {
         if (this.selectedGraphic instanceof IconGraphic && !this.fillingProperties) {
            ((IconGraphic)this.selectedGraphic).icon(id);
            this.graphicIconValue.setText(id);
            this.refreshGraphicsPreview();
         }
      });
      this.graphicIconPicker.flex().h(108);
      this.graphicOffset = this.graphicNumber((graphic, value) -> {
         if (graphic instanceof ShadowGraphic) ((ShadowGraphic)graphic).offset = value.intValue();
      }).integer();
      this.graphicPositionPair = this.axisPair(this.graphicX, this.graphicY);
      this.graphicRelativePair = this.axisPair(this.graphicRX, this.graphicRY);
      this.graphicAnchorPair = this.axisPair(this.graphicAnchorX, this.graphicAnchorY);
      this.moveUp = new GuiButtonElement(mc, IKey.lang("mappet.gui.ui_editor.move_up"), (button) -> this.moveSelected(-1)).background(true);
      this.moveDown = new GuiButtonElement(mc, IKey.lang("mappet.gui.ui_editor.move_down"), (button) -> this.moveSelected(1)).background(true);
      this.indent = new GuiButtonElement(mc, IKey.lang("mappet.gui.ui_editor.indent"), (button) -> this.indentSelected()).background(true);
      this.outdent = new GuiButtonElement(mc, IKey.lang("mappet.gui.ui_editor.outdent"), (button) -> this.outdentSelected()).background(true);
      this.removeComponent = new GuiButtonElement(mc, IKey.lang("mappet.gui.ui_editor.remove"), (button) -> this.removeSelected()).background(true);
      this.previewInGame = new GuiIconElement(mc, Icons.VISIBLE, (button) -> this.previewInGame());
      this.previewInGame.tooltip(IKey.lang("mappet.gui.ui_editor.preview_in_game"));

      
      


      this.palette.flex().relative(this.editor).xy(8, 30).w(150).h(1.0F, -176);
      this.paletteScroll.flex().relative(this.palette).xy(0, 0).w(1.0F).h(1.0F).column(3).stretch().vertical().scroll().padding(2);
      


      this.preview.flex().relative(this.editor).xy(168, 30).w(1.0F, -396).h(1.0F, -38);
      this.properties.flex().relative(this.editor).x(1.0F, -8).y(30).w(220).h(1.0F, -38).anchorX(1.0F);
      this.propertyTabs.flex().relative(this.properties).xy(0, 0).w(1.0F, -8).h(20).row(1);
      this.propertyScroll.flex().relative(this.properties).xy(0, 23).w(1.0F, -8).h(1.0F, -23).column(1).stretch().vertical().scroll().padding(8);
      this.hierarchy.flex().relative(this.hierarchyPanel).xy(0, 18).w(1.0F).h(1.0F, -18);
      this.hierarchyCaption = Elements.label(IKey.str("Иерархия (0)"));
      this.hierarchyCaption.flex().relative(this.hierarchyPanel).xy(0, 0).w(1.0F).h(16);

      this.paletteScroll.add(new IGuiElement[]{
         this.paletteCategory("Разметка"), this.componentButton("layout", "Макет"),
         this.paletteCategory("Текст и ввод"), this.componentButton("label", "Надпись"),
         this.componentButton("text", "Текст"), this.componentButton("textbox", "Текстовое поле"),
         this.componentButton("textarea", "Текстовая область"),
         this.paletteCategory("Управление"), this.componentButton("button", "Кнопка"),
                  this.componentButton("icon", "Иконка"), this.componentButton("toggle", "Переключатель"), this.componentButton("trackpad", "Трекпад"), this.componentButton("color", "Выбор цветов"), this.componentButton("strings", "Список строк"),
         this.paletteCategory("Продвинутые"), this.componentButton("graphics", "Графика"),
         this.componentButton("clickarea", "Область клика"), this.componentButton("item", "Предмет"),
         this.componentButton("morph", "Морф")
      });
      this.palette.add(this.paletteScroll);
      this.hierarchyPanel.add(new IGuiElement[]{this.hierarchyCaption, this.hierarchy});
      GuiLabel idCaption = Elements.label(IKey.lang("mappet.gui.ui_editor.id"));
      GuiLabel labelCaption = Elements.label(IKey.lang("mappet.gui.ui_editor.label"));
      GuiLabel xCaption = Elements.label(IKey.lang("mappet.gui.ui_editor.x"));
      GuiLabel yCaption = Elements.label(IKey.lang("mappet.gui.ui_editor.y"));
      GuiLabel widthCaption = Elements.label(IKey.lang("mappet.gui.ui_editor.width"));
      GuiLabel heightCaption = Elements.label(IKey.lang("mappet.gui.ui_editor.height"));
      GuiLabel anchorXCaption = Elements.label(IKey.lang("mappet.gui.ui_editor.anchor_x"));
      this.keys().register(IKey.str("Отменить (Ctrl+Z)"), 26, () -> this.undo()).category(IKey.str("UI редактор"));
      this.keys().register(IKey.str("Повторить (Ctrl+Y)"), 21, () -> this.redo()).category(IKey.str("UI редактор"));
      GuiLabel anchorYCaption = Elements.label(IKey.lang("mappet.gui.ui_editor.anchor_y"));
      GuiLabel tooltipCaption = Elements.label(IKey.lang("mappet.gui.ui_editor.tooltip"));

      Collections.addAll(this.baseComponentProperties, idCaption, this.componentId, Elements.label(IKey.str("Координаты X/Y")).marginTop(6), this.positionPair, this.relativePosition, Elements.label(IKey.str("Размер W/H")).marginTop(6), this.sizePair, this.relativeSize, Elements.label(IKey.str("Anchor X/Y")).marginTop(6), this.anchorPair, tooltipCaption.marginTop(6), this.tooltip, Elements.label(IKey.str("Направление подсказки")).marginTop(6), this.tooltipDirection, this.morphUseTooltip, Elements.label(IKey.str("Отступ сверху")).marginTop(6), this.marginTop, Elements.label(IKey.str("Отступ снизу")).marginTop(6), this.marginBottom, Elements.label(IKey.str("Отступ слева")).marginTop(6), this.marginLeft, Elements.label(IKey.str("Отступ справа")).marginTop(6), this.marginRight, Elements.label(IKey.str("Задержка обновления")).marginTop(6), this.updateDelay, this.visible, this.enabled);
      Collections.addAll(this.animationProperties, Elements.label(IKey.str("moveTo X/Y")), this.moveToPair, Elements.label(IKey.str("moveTo длительность")), this.moveToDuration, Elements.label(IKey.str("moveTo интерполяция")), this.moveToInterpolation, Elements.label(IKey.str("rotateTo угол")), this.rotateToAngle, Elements.label(IKey.str("rotateTo длительность")), this.rotateToDuration, Elements.label(IKey.str("rotateTo интерполяция")), this.rotateToInterpolation);
      this.componentProperties.addAll(this.baseComponentProperties);
      this.componentProperties.addAll(this.animationProperties);
      Collections.addAll(this.labelProperties, labelCaption, this.label);
      Collections.addAll(this.labelStyleProperties, Elements.label(IKey.str("Цвет текста")), this.textColor, this.textShadow, this.textBackground);
      Collections.addAll(this.buttonProperties, Elements.label(IKey.str("Фон кнопки")), this.buttonBackground, this.buttonHover, this.buttonUnhover);
      Collections.addAll(this.iconProperties, Elements.label(IKey.str("Выбор иконки")), this.iconPicker, Elements.label(IKey.str("Фон иконки")), this.iconBackground, Elements.label(IKey.str("Цвет иконки")), this.iconColor);
      Collections.addAll(this.textProperties, Elements.label(IKey.str("Anchor надписи X")), this.labelAnchorX, Elements.label(IKey.str("Anchor надписи Y")), this.labelAnchorY, Elements.label(IKey.str("Anchor текста")), this.textAnchor);
      Collections.addAll(this.inputProperties, Elements.label(IKey.str("Максимум символов")), this.inputMaxLength);
      Collections.addAll(this.toggleProperties, this.toggleState);
      Collections.addAll(this.trackpadProperties, Elements.label(IKey.str("Значение")), this.trackValue, Elements.label(IKey.str("Минимум")), this.trackMin, Elements.label(IKey.str("Максимум")), this.trackMax, this.trackInteger, Elements.label(IKey.str("Шаг обычный")), this.trackNormal, Elements.label(IKey.str("Шаг слабый")), this.trackWeak, Elements.label(IKey.str("Шаг сильный")), this.trackStrong, Elements.label(IKey.str("Приращение")), this.trackIncrement);
      Collections.addAll(this.colorProperties, Elements.label(IKey.str("Начальный цвет (ARGB)")), this.colorValue);
      Collections.addAll(this.stringListProperties, Elements.label(IKey.str("Строки (через |)")), this.stringsValues, Elements.label(IKey.str("Выбранный индекс")), this.stringsSelected, Elements.label(IKey.str("Фон списка")), this.stringsBackground);
      Collections.addAll(this.layoutProperties, this.layoutScroll, Elements.label(IKey.str("Размер прокрутки")), this.layoutScrollSize, this.layoutHorizontal, Elements.label(IKey.str("Тип layout: -1 нет, 0 column, 1 row, 2 grid")), this.layoutType, Elements.label(IKey.str("Отступ layout")), this.layoutMargin, Elements.label(IKey.str("Padding layout")), this.layoutPadding, Elements.label(IKey.str("Ширина ячейки")), this.layoutWidth, Elements.label(IKey.str("Кол-во ячеек grid")), this.layoutItems);
      Collections.addAll(this.morphTooltipProperties, Elements.label(IKey.str("Свойства морф-подсказки")).marginTop(6), this.morphTooltipPreview, this.morphTooltipSelect, this.morphTooltipEdit, this.morphTooltipClear, Elements.label(IKey.str("Размер W/H")), this.morphTooltipSizePair, Elements.label(IKey.str("Масштаб")), this.morphTooltipScale, Elements.label(IKey.str("Поворот Yaw/Pitch")), this.morphTooltipRotationPair, Elements.label(IKey.str("Смещение X/Y/Z")), this.morphTooltipOffsetTriple, Elements.label(IKey.str("Текст подсказки морфа")).marginTop(6), this.morphTooltip);
      Collections.addAll(this.eventScriptProperties, Elements.label(IKey.str("События")).marginTop(10), this.wrappedInspectorLabel("Callback: function(c, component, context, id)"), this.callbackEvent, this.wrappedInspectorLabel("Hover: function(c, component, context, id, mouseX, mouseY)"), this.hoverEvent, this.wrappedInspectorLabel("Hover Enter: function(c, component, context, id)"), this.hoverEnterEvent, this.wrappedInspectorLabel("Hover Exit: function(c, component, context, id)"), this.hoverExitEvent);
      Collections.addAll(this.morphProperties, this.morphSelect, this.morphEdit, Elements.label(IKey.str("NBT морфа")), this.morphNbt, this.morphEditing, Elements.label(IKey.str("Позиция камеры X/Y")), this.morphPositionPair, Elements.label(IKey.str("Позиция камеры Z")), this.morphPosZ, Elements.label(IKey.str("Наклон/поворот камеры")), this.morphRotationPair, Elements.label(IKey.str("Дистанция камеры")), this.morphDistance, Elements.label(IKey.str("Угол обзора (FOV)")), this.morphFov);
      Collections.addAll(this.stackProperties, Elements.label(IKey.str("NBT предмета")), this.stackNbt);
      Collections.addAll(this.visualAnimationProperties, Elements.label(IKey.str("scaleTo X/Y")), this.scaleToPair, Elements.label(IKey.str("scaleTo длительность")), this.scaleToDuration, Elements.label(IKey.str("scaleTo интерполяция")), this.scaleToInterpolation, Elements.label(IKey.str("colorTo цвет")), this.colorToColor, Elements.label(IKey.str("colorTo длительность")), this.colorToDuration, Elements.label(IKey.str("colorTo интерполяция")), this.colorToInterpolation);
      Collections.addAll(this.graphicsProperties, Elements.label(IKey.str("Графические примитивы")), this.graphicsList, this.graphicRect, this.graphicGradient, this.graphicText, this.graphicIcon, this.graphicShadow, this.graphicRemove, Elements.label(IKey.str("Координаты X/Y")), this.graphicPositionPair, Elements.label(IKey.str("Ширина")), this.graphicW, Elements.label(IKey.str("Высота")), this.graphicH, Elements.label(IKey.str("Относительные X/Y")), this.graphicRelativePair, Elements.label(IKey.str("Relative ширина")), this.graphicRW, Elements.label(IKey.str("Relative высота")), this.graphicRH, Elements.label(IKey.str("Anchor X/Y")), this.graphicAnchorPair, Elements.label(IKey.str("Основной цвет")), this.graphicPrimary, this.graphicHover, Elements.label(IKey.str("Второй цвет / тень")), this.graphicSecondary, this.graphicHorizontal, Elements.label(IKey.str("Текст примитива")), this.graphicTextValue, Elements.label(IKey.str("Выбор иконки примитива")), this.graphicIconPicker, Elements.label(IKey.str("ID иконки")), this.graphicIconValue, Elements.label(IKey.str("Смещение тени")), this.graphicOffset, Elements.label(IKey.str("Расширенный NBT")), this.graphicsNbt);
      this.componentProperties.addAll(this.labelProperties);
      this.componentProperties.addAll(this.labelStyleProperties);
      this.componentProperties.addAll(this.buttonProperties);
      this.componentProperties.addAll(this.iconProperties);
      this.componentProperties.addAll(this.textProperties);
      this.componentProperties.addAll(this.inputProperties);
      this.componentProperties.addAll(this.toggleProperties);
      this.componentProperties.addAll(this.trackpadProperties);
      this.componentProperties.addAll(this.colorProperties);
      this.componentProperties.addAll(this.stringListProperties);
      this.componentProperties.addAll(this.layoutProperties);
      this.componentProperties.addAll(this.morphTooltipProperties);
      this.componentProperties.addAll(this.morphProperties);
      this.componentProperties.addAll(this.stackProperties);
      this.animationProperties.addAll(this.visualAnimationProperties);
      this.componentProperties.addAll(this.visualAnimationProperties);
      this.componentProperties.addAll(this.graphicsProperties);

      Collections.addAll(this.fileProperties, Elements.label(IKey.lang("mappet.gui.ui_editor.script")), this.script, Elements.label(IKey.lang("mappet.gui.ui_editor.function")), this.function, this.background, this.closable, this.paused);
      this.rebuildInspectorElements();
      this.properties.add(new IGuiElement[]{this.propertyTabs, this.propertyScroll});
      this.iconBar.add(this.previewInGame);
      this.editor.add(new IGuiElement[]{this.palette, this.hierarchyPanel, this.preview, this.properties});
      this.fill(null);
   }

   private GuiButtonElement inspectorTab(InspectorTab tab, String label) {
      GuiButtonElement button = new GuiButtonElement(this.mc, IKey.str(label), (element) -> {
         this.inspectorTab = tab;
         this.rebuildInspectorElements();
      });
      button.background(true);
      return button;
   }

   private void refreshInspectorTabs() {
      boolean hasFile = this.data != null;
      boolean hasComponent = this.selected != null;
      this.propertyTabs.setVisible(hasFile);
      this.tabFile.setEnabled(hasFile);
      this.tabGeneral.setEnabled(hasComponent);
      this.tabContent.setEnabled(hasComponent);
      this.tabAnimation.setEnabled(hasComponent);
      this.tabGraphics.setVisible(false);
      this.tabGraphics.setEnabled(false);
      this.styleInspectorTab(this.tabFile, InspectorTab.FILE);
      this.styleInspectorTab(this.tabGeneral, InspectorTab.GENERAL);
      this.styleInspectorTab(this.tabContent, InspectorTab.CONTENT);
      this.styleInspectorTab(this.tabAnimation, InspectorTab.ANIMATION);
   }

   private void styleInspectorTab(GuiButtonElement button, InspectorTab tab) {
      boolean selected = this.inspectorTab == tab;
      button.custom = selected;
      if (selected) {
         button.color((Integer)Mappet.globalTriggerCategoryColor.get()).textColor(-1, true);
      } else {
         button.textColor(-4144960, true);
      }
   }

   private GuiSimpleContextMenu createComponentContext() {
      GuiSimpleContextMenu menu = new GuiSimpleContextMenu(this.mc);
      if (this.selected != null) {
         menu.action(Icons.MOVE_UP, IKey.lang("mappet.gui.ui_editor.move_up"), () -> this.moveSelected(-1));
         menu.action(Icons.MOVE_DOWN, IKey.lang("mappet.gui.ui_editor.move_down"), () -> this.moveSelected(1));
         menu.action(Icons.MOVE_RIGHT, IKey.lang("mappet.gui.ui_editor.indent"), this::indentSelected);
         menu.action(Icons.MOVE_LEFT, IKey.lang("mappet.gui.ui_editor.outdent"), this::outdentSelected);
         menu.action(Icons.REMOVE, IKey.lang("mappet.gui.ui_editor.remove"), this::removeSelected);
      }
      return menu;
   }

   private boolean isTextShadowEnabled() {
      return this.textShadow != null && this.textShadow.isToggled();
   }

   private int textColorValue() {
      return this.textColor == null || this.textColor.value == 0.0D ? 16777215 : (int)this.textColor.value;
   }

   private float labelAnchorXValue() {
      return this.labelAnchorX == null ? 0.0F : (float)this.labelAnchorX.value;
   }

   private float labelAnchorYValue() {
      return this.labelAnchorY == null ? 0.0F : (float)this.labelAnchorY.value;
   }

   private void setTextBackground(UILabelBaseComponent component, boolean background) {
      class_2487 tag = component.serializeNBT();
      tag.method_10556("HasBackground", background);
      component.deserializeNBT(tag);
   }

   private void applyScaleTo(UIComponent component) {
      if (component instanceof UIButtonComponent) {
         ((UIButtonComponent)component).scaleTo((float)this.scaleToX.value, (float)this.scaleToY.value, (int)this.scaleToDuration.value, this.scaleInterpolationValue);
      } else if (component instanceof UIIconComponent) {
         ((UIIconComponent)component).scaleTo((float)this.scaleToX.value, (float)this.scaleToY.value, (int)this.scaleToDuration.value, this.scaleInterpolationValue);
      }
      this.rebuildPreview();
   }

   private void applyColorTo(UIComponent component) {
      if (component instanceof UIButtonComponent) {
         ((UIButtonComponent)component).colorTo((int)this.colorToColor.value, (int)this.colorToDuration.value, this.colorInterpolationValue);
      } else if (component instanceof UIIconComponent) {
         ((UIIconComponent)component).colorTo((int)this.colorToColor.value, (int)this.colorToDuration.value, this.colorInterpolationValue);
      }
      this.rebuildPreview();
   }

   private void openMorphSelector(boolean editing) {
      if (!(this.selected instanceof UIMorphComponent)) {
         return;
      }

      UIMorphComponent component = (UIMorphComponent)this.selected;
      AbstractMorph current = component.morph == null ? null : MorphManager.INSTANCE.morphFromNBT(component.morph);
      this.dashboard.openMorphMenu(this.editor, editing, current, (morph) -> {
         if (morph == null) {
            return;
         }

         component.morph = MorphUtils.toNBT(MorphUtils.copy(morph));
         this.morphNbt.setText(component.morph == null ? "" : component.morph.toString());
         this.fillProperties();
         this.rebuildPreview();
      });
   }

   private void toggleMorphTooltip(boolean enabled) {
      if (this.selected == null) {
         return;
      }

      UIComponent component = this.selected;
      component.tooltipMorphEnabled(enabled);
      if (enabled && component.tooltipMorph == null && component instanceof UIMorphComponent) {
         class_2487 morphNbt = ((UIMorphComponent)component).morph;
         AbstractMorph morph = morphNbt == null ? null : MorphManager.INSTANCE.morphFromNBT(morphNbt);
         if (morph != null) {
            component.tooltip(MorphUtils.copy(morph), this.morphTooltip.field.getText());
            component.tooltipMorphEnabled(true);
         }
      }
      this.refreshMorphTooltipSettings();
      this.rebuildPreview();
   }

   private class_2487 getMorphTooltipNBT(UIComponent component) {
      if (component == null) {
         return null;
      }
      if (component.tooltipMorph != null) {
         return component.tooltipMorph;
      }
      return component instanceof UIMorphComponent ? ((UIMorphComponent)component).morph : null;
   }

   private void openMorphTooltipSelector(boolean editing) {
      if (this.selected == null) {
         return;
      }

      UIComponent component = this.selected;
      class_2487 currentNbt = this.getMorphTooltipNBT(component);
      AbstractMorph current = currentNbt == null ? null : MorphManager.INSTANCE.morphFromNBT(currentNbt);
      this.dashboard.openMorphMenu(this.editor, editing, current, (morph) -> {
         if (morph == null) {
            this.morphUseTooltip.toggled(component.tooltipMorph != null);
            return;
         }

         component.tooltip(MorphUtils.copy(morph), this.morphTooltip.field.getText());
         this.morphUseTooltip.toggled(true);
         this.morphTooltip.setText(component.tooltipMorphText);
         this.refreshMorphTooltipSettings();
         this.rebuildPreview();
      });
   }

   private void refreshMorphTooltipSettings() {
      boolean visible = this.selected != null && this.selected.tooltipMorphEnabled;
      this.tooltip.setEnabled(!visible);
      this.setVisible(this.morphTooltipProperties, visible);
      if (visible) {
         this.fillMorphTooltipSettings(this.selected);
      } else {
         this.morphTooltipPreview.setMorph(null);
      }
      this.rebuildInspectorElements();
   }

   private GuiTrackpadElement morphTooltipNumber() {
      return new GuiTrackpadElement(this.mc, (value) -> {
         if (this.selected != null && this.selected.tooltipMorph != null && !this.fillingProperties) {
            this.applyMorphTooltipSettings();
         }
      });
   }

   private void applyMorphTooltipSettings() {
      if (this.selected == null || this.selected.tooltipMorph == null) {
         return;
      }

      this.selected.tooltipMorphSize((int)this.morphTooltipWidth.value, (int)this.morphTooltipHeight.value);
      this.selected.tooltipMorphTransform((float)this.morphTooltipScale.value, (float)this.morphTooltipYaw.value, (float)this.morphTooltipPitch.value, (float)this.morphTooltipOffsetX.value, (float)this.morphTooltipOffsetY.value, (float)this.morphTooltipOffsetZ.value);
      this.selected.tooltipDirection = (int)this.morphTooltipDirection.value;
      this.fillMorphTooltipSettings(this.selected);
      this.rebuildPreview();
   }

   private void fillMorphTooltipSettings(UIComponent component) {
      if (component == null) {
         this.morphTooltipPreview.setMorph(null);
         return;
      }

      this.morphTooltipPreview.setMorph(component.tooltipMorph == null ? null : MorphManager.INSTANCE.morphFromNBT(component.tooltipMorph), component.tooltipMorphWidth, component.tooltipMorphHeight, component.tooltipMorphScale, component.tooltipMorphYaw, component.tooltipMorphPitch, component.tooltipMorphOffsetX, component.tooltipMorphOffsetY, component.tooltipMorphOffsetZ);
      this.morphTooltipWidth.setValue((double)component.tooltipMorphWidth);
      this.morphTooltipHeight.setValue((double)component.tooltipMorphHeight);
      this.morphTooltipScale.setValue((double)component.tooltipMorphScale);
      this.morphTooltipYaw.setValue((double)component.tooltipMorphYaw);
      this.morphTooltipPitch.setValue((double)component.tooltipMorphPitch);
      this.morphTooltipOffsetX.setValue((double)component.tooltipMorphOffsetX);
      this.morphTooltipOffsetY.setValue((double)component.tooltipMorphOffsetY);
      this.morphTooltipOffsetZ.setValue((double)component.tooltipMorphOffsetZ);
      this.morphTooltipDirection.setValue((double)component.tooltipDirection);
      this.morphTooltipEdit.setEnabled(MorphManager.INSTANCE.morphFromNBT(component.tooltipMorph) != null);
   }

   private void clearMorphTooltip() {
      if (this.selected == null) {
         return;
      }
      this.selected.tooltip((AbstractMorph)null, "");
      this.selected.tooltipMorphEnabled(false);
      this.morphUseTooltip.toggled(false);
      this.refreshMorphTooltipSettings();
      this.rebuildPreview();
   }

   private void applyMorphTooltip(UIComponent component, String text) {
      this.applyMorphTooltip(component, text, this.morphUseTooltip.isToggled());
   }

   private void applyMorphTooltip(UIComponent component, String text, boolean enabled) {
      if (component == null) {
         return;
      }

      component.tooltipMorphEnabled(enabled);
      if (enabled && component.tooltipMorph != null) {
         component.tooltipMorphText = text == null ? "" : text;
         component.tooltip = "";
      }

      this.rebuildPreview();
   }

   private void applyMorphPosition(UIMorphComponent component) {
      component.position((float)this.morphPosX.value, (float)this.morphPosY.value, (float)this.morphPosZ.value);
   }

   private void applyMorphRotation(UIMorphComponent component) {
      component.rotation((float)this.morphPitch.value, (float)this.morphYaw.value);
   }

   private GuiTrackpadElement typedNumber(BiConsumer<UIComponent, Double> callback) {
      return this.componentNumber((value) -> callback.accept(this.selected, value));
   }

   private GuiToggleElement typedToggle(String label, BiConsumer<UIComponent, Boolean> callback) {
      return new GuiToggleElement(this.mc, IKey.str(label), (button) -> {
         if (this.selected != null && !this.fillingProperties) {
            callback.accept(this.selected, button.isToggled());
            this.rebuildPreview();
         }
      });
   }

   private GuiTextEditor eventScriptEditor(BiConsumer<UIComponent, String> callback) {
      GuiTextEditor editor = new GuiTextEditor(this.mc, (value) -> {
         if (this.selected != null && !this.fillingProperties) {
            callback.accept(this.selected, value);
            this.rebuildPreview();
         }
      });
      editor.setJavaScriptDiagnostics(false);
      Map<String, String> eventTypes = new LinkedHashMap();
      eventTypes.put("c", "IScriptEvent");
      eventTypes.put("component", "UIComponent");
      eventTypes.put("context", "IMappetUIContext");
      eventTypes.put("id", "String");
      eventTypes.put("mouseX", "int");
      eventTypes.put("mouseY", "int");
      editor.setLocalAutoCompleteTypes(eventTypes);
      editor.flex().h(64);
      return editor;
   }

   private GuiTextElement typedText(BiConsumer<UIComponent, String> callback) {
      return new GuiTextElement(this.mc, 1000, (value) -> {
         if (this.selected != null && !this.fillingProperties) {
            callback.accept(this.selected, value);
            this.rebuildPreview();
         }
      });
   }

   private int graphicsListIndex() {
      return this.graphicsList == null ? -1 : this.graphicsList.getIndex();
   }

   private GuiTrackpadElement graphicNumber(BiConsumer<Graphic, Double> callback) {
      return new GuiTrackpadElement(this.mc, (value) -> {
         if (this.selectedGraphic != null && !this.fillingProperties) {
            callback.accept(this.selectedGraphic, value);
            this.refreshGraphicsPreview();
         }
      });
   }

   private GuiTextElement graphicText(BiConsumer<Graphic, String> callback) {
      return new GuiTextElement(this.mc, 1000, (value) -> {
         if (this.selectedGraphic != null && !this.fillingProperties) {
            callback.accept(this.selectedGraphic, value);
            this.refreshGraphicsPreview();
         }
      });
   }

   private void setGraphicAnchorX(Graphic graphic, float value) {
      graphic.anchorX = value;
      if (graphic instanceof TextGraphic) ((TextGraphic)graphic).anchorX = value;
      if (graphic instanceof IconGraphic) ((IconGraphic)graphic).anchorX = value;
   }

   private void setGraphicAnchorY(Graphic graphic, float value) {
      graphic.anchorY = value;
      if (graphic instanceof TextGraphic) ((TextGraphic)graphic).anchorY = value;
      if (graphic instanceof IconGraphic) ((IconGraphic)graphic).anchorY = value;
   }

   private void setGraphicSecondary(Graphic graphic, int color) {
      if (graphic instanceof GradientGraphic) {
         ((GradientGraphic)graphic).secondary = color;
      } else if (graphic instanceof ShadowGraphic) {
         ((ShadowGraphic)graphic).secondary = color;
      }
   }

   private void addGraphic(Graphic graphic) {
      if (this.selected instanceof UIGraphicsComponent) {
         ((UIGraphicsComponent)this.selected).graphics.add(graphic);
         this.selectedGraphic = graphic;
         this.refreshGraphicsList();
         this.refreshGraphicsPreview();
      }
   }

   private void removeGraphic() {
      if (this.selected instanceof UIGraphicsComponent && this.selectedGraphic != null) {
         ((UIGraphicsComponent)this.selected).graphics.remove(this.selectedGraphic);
         this.selectedGraphic = null;
         this.refreshGraphicsList();
         this.refreshGraphicsPreview();
      }
   }

   private String graphicTitle(Graphic graphic, int index) {
      if (graphic instanceof GradientGraphic) return "Градиент " + index;
      if (graphic instanceof TextGraphic) return "Текст " + index;
      if (graphic instanceof IconGraphic) return "Иконка " + index;
      if (graphic instanceof ShadowGraphic) return "Тень " + index;
      return "Прямоугольник " + index;
   }

   private void applyGraphicsListOrder() {
      if (!(this.selected instanceof UIGraphicsComponent)) {
         return;
      }

      List<Graphic> ordered = new ArrayList();
      for (String title : this.graphicsList.getList()) {
         Graphic graphic = (Graphic)this.graphicEntries.get(title);
         if (graphic != null) {
            ordered.add(graphic);
         }
      }

      UIGraphicsComponent component = (UIGraphicsComponent)this.selected;
      if (ordered.size() == component.graphics.size() && !ordered.equals(component.graphics)) {
         component.graphics.clear();
         component.graphics.addAll(ordered);
         this.refreshGraphicsPreview();
      }
   }

   private void refreshGraphicsList() {
      this.graphicsList.clear();
      this.graphicEntries.clear();
      if (!(this.selected instanceof UIGraphicsComponent)) {
         return;
      }

      List<Graphic> graphics = ((UIGraphicsComponent)this.selected).graphics;
      int index = 0;
      for(Graphic graphic : graphics) {
         String title = this.graphicTitle(graphic, index++);
         this.graphicEntries.put(title, graphic);
         this.graphicsList.add(title);
      }
      if (this.selectedGraphic != null) {
         int selectedIndex = graphics.indexOf(this.selectedGraphic);
         if (selectedIndex >= 0) {
            this.graphicsList.setIndex(selectedIndex);
         }
      }
      this.fillGraphicProperties();
   }

   private void refreshGraphicsPreview() {
      if (this.selected instanceof UIGraphicsComponent) {
         this.graphicsNbt.setText(this.selected.serializeNBT().toString());
         this.rebuildPreview();
      }
   }

   private void fillGraphicProperties() {
      this.fillingProperties = true;
      if (this.selectedGraphic != null) {
         Graphic graphic = this.selectedGraphic;
         this.graphicX.setValue((double)graphic.pixels.x);
         this.graphicY.setValue((double)graphic.pixels.y);
         this.graphicW.setValue((double)graphic.pixels.w);
         this.graphicH.setValue((double)graphic.pixels.h);
         this.graphicRX.setValue((double)graphic.relativeX);
         this.graphicRY.setValue((double)graphic.relativeY);
         this.graphicRW.setValue((double)graphic.relativeW);
         this.graphicRH.setValue((double)graphic.relativeH);
         this.graphicAnchorX.setValue((double)(graphic instanceof TextGraphic ? ((TextGraphic)graphic).anchorX : graphic instanceof IconGraphic ? ((IconGraphic)graphic).anchorX : graphic.anchorX));
         this.graphicAnchorY.setValue((double)(graphic instanceof TextGraphic ? ((TextGraphic)graphic).anchorY : graphic instanceof IconGraphic ? ((IconGraphic)graphic).anchorY : graphic.anchorY));
         this.graphicPrimary.setValue((double)graphic.primary);
         this.graphicHover.toggled(graphic.hover);
         this.graphicSecondary.setValue((double)(graphic instanceof GradientGraphic ? ((GradientGraphic)graphic).secondary : graphic instanceof ShadowGraphic ? ((ShadowGraphic)graphic).secondary : 0));
         this.graphicHorizontal.toggled(graphic instanceof GradientGraphic && ((GradientGraphic)graphic).horizontal);
         this.graphicTextValue.setText(graphic instanceof TextGraphic ? ((TextGraphic)graphic).text : "");
         String iconId = graphic instanceof IconGraphic ? ((IconGraphic)graphic).id : "";
         this.graphicIconPicker.setSelected(iconId);
         this.graphicIconValue.setText(iconId);
         this.graphicOffset.setValue((double)(graphic instanceof ShadowGraphic ? ((ShadowGraphic)graphic).offset : 0));
      }
      this.fillingProperties = false;
   }

   private void refreshInspectorScroll() {
      if (this.propertyScroll.hasParent()) {
         this.propertyScroll.resize();
         this.propertyScroll.scroll.clamp();
      }
   }

   private void rebuildInspectorElements() {
      this.propertyScroll.removeAll();
      if (this.data == null) {
         this.refreshInspectorTabs();
         this.refreshInspectorScroll();
         return;
      }

      if (this.selected == null && this.inspectorTab != InspectorTab.FILE) {
         this.inspectorTab = InspectorTab.FILE;
      }
      if (this.inspectorTab == InspectorTab.GRAPHICS) {
         this.inspectorTab = InspectorTab.CONTENT;
      }

      if (this.inspectorTab == InspectorTab.FILE) {
         this.propertyScroll.add((IGuiElement[])this.fileProperties.toArray(new IGuiElement[0]));
      } else if (this.selected != null) {
         if (this.inspectorTab == InspectorTab.GENERAL) {
            int morphToggleIndex = this.baseComponentProperties.indexOf(this.morphUseTooltip);
            if (morphToggleIndex < 0) {
               this.propertyScroll.add((IGuiElement[])this.baseComponentProperties.toArray(new IGuiElement[0]));
            } else {
               this.propertyScroll.add((IGuiElement[])this.baseComponentProperties.subList(0, morphToggleIndex + 1).toArray(new IGuiElement[0]));
               if (this.selected.tooltipMorphEnabled) {
                  this.propertyScroll.add((IGuiElement[])this.morphTooltipProperties.toArray(new IGuiElement[0]));
               }
               this.propertyScroll.add((IGuiElement[])this.baseComponentProperties.subList(morphToggleIndex + 1, this.baseComponentProperties.size()).toArray(new IGuiElement[0]));
               this.propertyScroll.add((IGuiElement[])this.eventScriptProperties.toArray(new IGuiElement[0]));
            }
         } else if (this.inspectorTab == InspectorTab.ANIMATION) {
            this.propertyScroll.add((IGuiElement[])this.animationProperties.toArray(new IGuiElement[0]));
         } else {
            this.addComponentTypeProperties();
         }
      }

      for (GuiLabel label : this.propertyScroll.getChildren(GuiLabel.class)) {
         label.marginTop(6).marginBottom(3);
      }
      this.refreshInspectorTabs();
      this.refreshInspectorScroll();
   }

   private void addComponentTypeProperties() {
      if (this.selected instanceof UILabelBaseComponent) this.propertyScroll.add((IGuiElement[])this.labelProperties.toArray(new IGuiElement[0]));
      if (this.selected instanceof UILabelBaseComponent) this.propertyScroll.add((IGuiElement[])this.labelStyleProperties.toArray(new IGuiElement[0]));
      if (this.selected instanceof UIButtonComponent) this.propertyScroll.add((IGuiElement[])this.buttonProperties.toArray(new IGuiElement[0]));
      if (this.selected instanceof UIIconComponent) this.propertyScroll.add((IGuiElement[])this.iconProperties.toArray(new IGuiElement[0]));
      if (this.selected instanceof UILabelComponent || this.selected instanceof UITextComponent) this.propertyScroll.add((IGuiElement[])this.textProperties.toArray(new IGuiElement[0]));
      if (this.selected instanceof UITextboxComponent) this.propertyScroll.add((IGuiElement[])this.inputProperties.toArray(new IGuiElement[0]));
      if (this.selected instanceof UIToggleComponent) this.propertyScroll.add((IGuiElement[])this.toggleProperties.toArray(new IGuiElement[0]));
      if (this.selected instanceof UITrackpadComponent) this.propertyScroll.add((IGuiElement[])this.trackpadProperties.toArray(new IGuiElement[0]));
      if (this.selected instanceof UIColorComponent) this.propertyScroll.add((IGuiElement[])this.colorProperties.toArray(new IGuiElement[0]));
      if (this.selected instanceof UIStringListComponent) this.propertyScroll.add((IGuiElement[])this.stringListProperties.toArray(new IGuiElement[0]));
      if (this.selected instanceof UILayoutComponent) this.propertyScroll.add((IGuiElement[])this.layoutProperties.toArray(new IGuiElement[0]));
      if (this.selected instanceof UIMorphComponent) this.propertyScroll.add((IGuiElement[])this.morphProperties.toArray(new IGuiElement[0]));
      if (this.selected instanceof UIStackComponent) this.propertyScroll.add((IGuiElement[])this.stackProperties.toArray(new IGuiElement[0]));
      if (this.selected instanceof UIGraphicsComponent) this.propertyScroll.add((IGuiElement[])this.graphicsProperties.toArray(new IGuiElement[0]));
   }

   private void setVisible(List<GuiElement> elements, boolean visible) {
      for(GuiElement element : elements) {
         element.setVisible(visible);
      }
   }

   private GuiTrackpadElement componentNumber(Consumer<Double> callback) {
      return new GuiTrackpadElement(this.mc, (value) -> {
         if (this.selected != null && !this.fillingProperties) {
            callback.accept(value);
            this.rebuildPreview();
         }
      });
   }

   private void applyMoveTo() {
      if (this.selected != null && !this.fillingProperties) {
         this.selected.moveTo((float)this.moveToX.value, (float)this.moveToY.value, (int)this.moveToDuration.value, this.moveInterpolationValue);
         this.rebuildPreview();
      }
   }

   private void applyRotateTo() {
      if (this.selected != null && !this.fillingProperties) {
         this.selected.rotateTo((float)this.rotateToAngle.value, (int)this.rotateToDuration.value, this.rotateInterpolationValue);
         this.rebuildPreview();
      }
   }

   private GuiElement axisPair(GuiElement x, GuiElement y) {
      GuiElement pair = new GuiElement(this.mc).markContainer();
      pair.flex().h(20).row(2);
      pair.add(new IGuiElement[]{x, y});
      return pair;
   }

   private GuiElement axisTriple(GuiElement x, GuiElement y, GuiElement z) {
      GuiElement triple = new GuiElement(this.mc).markContainer();
      triple.flex().h(20).row(3);
      triple.add(new IGuiElement[]{x, y, z});
      return triple;
   }

   private void setSizeRelative(boolean relative) {
      if (this.selected == null || this.fillingProperties) {
         return;
      }
      this.setUnitRelative(this.selected.w, 2, relative);
      this.setUnitRelative(this.selected.h, 3, relative);
      this.fillProperties();
      this.rebuildPreview();
   }

   private void setPositionRelative(boolean relative) {
      if (this.selected == null || this.fillingProperties) {
         return;
      }
      this.setUnitRelative(this.selected.x, 0, relative);
      this.setUnitRelative(this.selected.y, 1, relative);
      this.fillProperties();
      this.rebuildPreview();
   }

   private void setUnitRelative(UIUnit unit, int index, boolean relative) {
      int size = this.unitExtent(index);
      if (relative && !unit.relative) {
         unit.value = (float)unit.offset / (float)size;
         unit.offset = 0;
         unit.relative = true;
      } else if (!relative && unit.relative) {
         unit.offset = Math.round(unit.value * (float)size);
         unit.value = 0.0F;
         unit.relative = false;
      }
   }

   private GuiTrackpadElement coordinate(int index) {
      return new GuiTrackpadElement(this.mc, (value) -> {
         if (this.selected != null && !this.fillingProperties) {
            UIUnit unit = this.unit(index);
            if (unit.relative) {
               unit.value = value.floatValue();
            } else {
               unit.offset = value.intValue();
            }
            this.rebuildPreview();
         }
      });
   }

   private GuiToggleElement relativeToggle(class_310 mc, IKey key, int index) {
      return new GuiToggleElement(mc, key, (button) -> {
         if (this.selected != null && !this.fillingProperties) {
            UIUnit unit = this.unit(index);
            int size = this.unitExtent(index);
            if (button.isToggled()) {
               unit.value = (float)unit.offset / (float)size;
               unit.offset = 0;
               unit.relative = true;
            } else {
               unit.offset = Math.round(unit.value * (float)size);
               unit.value = 0.0F;
               unit.relative = false;
            }
            this.fillProperties();
            this.rebuildPreview();
         }
      });
   }

   private GuiTrackpadElement anchorTrackpad(class_310 mc, boolean x) {
      GuiTrackpadElement element = new GuiTrackpadElement(mc, (value) -> {
         if (this.selected != null && !this.fillingProperties) {
            if (x) {
               this.selected.anchorX(value.floatValue());
            } else {
               this.selected.anchorY(value.floatValue());
            }
            this.rebuildPreview();
         }
      });
      element.limit(0.0D, 1.0D).strong = 0.05D;
      return element;
   }

   private UIUnit unit(int index) {
      if (index == 0) return this.selected.x;
      if (index == 1) return this.selected.y;
      if (index == 2) return this.selected.w;
      return this.selected.h;
   }

   private int unitExtent(int index) {
      return index == 0 || index == 2 ? 1280 : 720;
   }

   private GuiLabel wrappedInspectorLabel(String text) {
      GuiLabel label = new GuiLabel(this.mc, IKey.str(text)) {
         public void draw(GuiContext context) {
            String value = this.label.get();
            int maxWidth = Math.max(1, this.area.w);
            List<String> lines = GuiUIFilePanel.this.wrapInspectorText(value, maxWidth);

            for(int index = 0; index < lines.size(); ++index) {
               GuiDraw.drawString(this.font, (String)lines.get(index), this.area.x, this.area.y + index * 9, this.color, this.textShadow);
            }
         }
      };
      label.flex().h(28);
      return label;
   }

   private List<String> wrapInspectorText(String text, int maxWidth) {
      List<String> lines = new ArrayList();
      StringBuilder line = new StringBuilder();

      for(String word : text.split(" ")) {
         String candidate = line.length() == 0 ? word : line + " " + word;
         if (this.font.method_1727(candidate) <= maxWidth) {
            line.setLength(0);
            line.append(candidate);
         } else {
            if (line.length() > 0) {
               lines.add(line.toString());
               line.setLength(0);
            }

            while(this.font.method_1727(word) > maxWidth && word.length() > 1) {
               int end = word.length() - 1;
               while(end > 1 && this.font.method_1727(word.substring(0, end)) > maxWidth) {
                  --end;
               }
               lines.add(word.substring(0, end));
               word = word.substring(end);
            }
            line.append(word);
         }
      }

      if (line.length() > 0 || lines.isEmpty()) {
         lines.add(line.toString());
      }

      return lines;
   }

   private GuiLabel paletteCategory(String title) {
      GuiLabel label = Elements.label(IKey.str(title));
      

      label.flex().h(16);
      return label;
   }

   private GuiButtonElement componentButton(String type, String title) {
      GuiButtonElement button = new GuiUIComponentPaletteButton(this.mc, IKey.str(title), this.componentIcon(type), this.componentColor(type), (element) -> this.addComponent(type));
      button.tooltip(IKey.str("Добавить компонент: " + title));
      return button;
   }

   private mchorse.mclib.client.gui.utils.Icon componentIcon(String type) {
      if (type.equals("graphics")) return Icons.IMAGE;
      if (type.equals("button") || type.equals("clickarea")) return Icons.CURSOR;
      if (type.equals("label")) return Icons.FILE;
      if (type.equals("text")) return Icons.CODE;
      if (type.equals("icon")) return Icons.FAVORITE;
      if (type.equals("textbox") || type.equals("textarea")) return Icons.EDIT;
      if (type.equals("toggle")) return Icons.CHECKBOARD;
      if (type.equals("trackpad")) return Icons.MOVE_LEFT;
      if (type.equals("color")) return Icons.MATERIAL;
      if (type.equals("strings")) return Icons.MORE;
      if (type.equals("item")) return Icons.BLOCK;
      if (type.equals("morph")) return Icons.POSE;
      if (type.equals("layout")) return Icons.MAZE;
      return Icons.NONE;
   }

   private int componentColor(String type) {
      if (type.equals("graphics")) return 0xFFD67AFF;
      if (type.equals("button")) return 0xFF66AFFF;
      if (type.equals("label")) return 0xFF77D8FF;
      if (type.equals("text")) return 0xFFFFE46B;
      if (type.equals("icon")) return 0xFF71F2D8;
      if (type.equals("textbox") || type.equals("textarea")) return 0xFF9EEA70;
      if (type.equals("toggle")) return 0xFFFFC463;
      if (type.equals("trackpad")) return 0xFF8CA8FF;
      if (type.equals("color")) return 0xFFFF83C2;
      if (type.equals("strings")) return 0xFFE6A3FF;
      if (type.equals("item")) return 0xFFFFD36A;
      if (type.equals("morph")) return 0xFFFF7CCF;
      if (type.equals("clickarea")) return 0xFF73E7FF;
      if (type.equals("layout")) return 0xFFAC8CFF;
      return -1;
   }

   private void previewInGame() {
      if (this.data == null || !this.allowed) {
         return;
      }

      this.selectionPathBeforePreview = this.componentPath(this.selected);
      this.inspectorTabBeforePreview = this.inspectorTab;
      this.save();
      Dispatcher.sendToServer(new PacketUIPreview(this.data));
   }

   private List<Integer> componentPath(UIComponent target) {
      if (target == null || this.data == null) {
         return null;
      }

      List<Integer> path = new ArrayList();
      return this.findComponentPath(this.data.root, target, path) ? path : null;
   }

   private boolean findComponentPath(UIParentComponent parent, UIComponent target, List<Integer> path) {
      for(int index = 0; index < parent.children.size(); ++index) {
         UIComponent component = (UIComponent)parent.children.get(index);
         path.add(index);
         if (component == target) {
            return true;
         }
         if (component instanceof UIParentComponent && this.findComponentPath((UIParentComponent)component, target, path)) {
            return true;
         }
         path.remove(path.size() - 1);
      }
      return false;
   }

   private UIComponent componentAtPath(UIParentComponent root, List<Integer> path) {
      if (path == null || path.isEmpty()) {
         return null;
      }

      UIParentComponent parent = root;
      UIComponent component = null;
      for(int depth = 0; depth < path.size(); ++depth) {
         int index = (Integer)path.get(depth);
         if (index < 0 || index >= parent.children.size()) {
            return null;
         }

         component = (UIComponent)parent.children.get(index);
         if (depth + 1 < path.size()) {
            if (!(component instanceof UIParentComponent)) {
               return null;
            }
            parent = (UIParentComponent)component;
         }
      }

      return component;
   }

   private void addComponent(String type) {
      if (this.data == null) {
         return;
      }

      UIComponent component = (UIComponent)CommonProxy.getUiComponents().create(type);
      if (component == null) {
         return;
      }

      UIParentComponent parent = this.selected instanceof UIParentComponent ? (UIParentComponent)this.selected : this.data.root;
      component.id = this.nextId(type);
      this.applyDocumentedDefaults(component, type);
      if (component instanceof UILabelBaseComponent) {
         ((UILabelBaseComponent)component).label(titleFor(type));
      }

      this.saveSnapshot();
      parent.children.add(component);
      this.selected = component;
      this.inspectorTab = component instanceof UIGraphicsComponent ? InspectorTab.CONTENT : InspectorTab.GENERAL;
      this.refreshEditor();
   }

   

   private void applyDocumentedDefaults(UIComponent component, String type) {
      component.x(10).y(10);
      if (component instanceof UIStringListComponent) {
         UIStringListComponent list = (UIStringListComponent)component;
         list.w(100).h(240);
         list.values("Первая строка", "Вторая строка", "Третья строка").selected(0).background(0x88000000);
      } else if (component instanceof UITextareaComponent) {
         component.w(160).h(80);
      } else if (component instanceof UITextboxComponent) {
         component.w(160).h(20);
      } else if (component instanceof UIButtonComponent) {
         component.w(160).h(20);
      } else if (component instanceof UIIconComponent || component instanceof UIStackComponent) {
         component.w(20).h(20);
      } else if (component instanceof UIColorComponent) {
         component.w(160).h(20);
      } else if (component instanceof UITrackpadComponent) {
         UITrackpadComponent trackpad = (UITrackpadComponent)component;
         trackpad.w(160).h(20);
         trackpad.value(5.0D).limit(0.0D, 25.0D, true);
      } else if (component instanceof UIMorphComponent || component instanceof UIGraphicsComponent || component instanceof UILayoutComponent) {
         component.w(160).h(100);
      } else if (component instanceof UILabelBaseComponent || component instanceof UIToggleComponent || component instanceof UIClickComponent) {
         component.w(160).h(20);
      } else {
         component.w(100).h(20);
      }
   }

   private String nextId(String type) {
      int index = 1;
      while(this.findById(this.data.root, type + "_" + index) != null) {
         ++index;
      }

      return type + "_" + index;
   }

   private String titleFor(String type) {
      if (type.equals("button")) return "Кнопка";
      if (type.equals("label")) return "Надпись";
      if (type.equals("text")) return "Текст";
      if (type.equals("textbox")) return "Введите текст";
      if (type.equals("textarea")) return "Текст";
      if (type.equals("toggle")) return "Переключатель";
      if (type.equals("color")) return "Выбор цветов";
      return "";
   }

   private void pickComponent(String key) {
      this.pickCanvasComponent((UIComponent)this.hierarchyEntries.get(key));
   }

   private void pickCanvasComponent(UIComponent component) {
      this.selected = component;
      if (component != null && this.inspectorTab == InspectorTab.FILE) {
         this.inspectorTab = InspectorTab.GENERAL;
      }
      this.preview.setSelected(component);
      this.syncHierarchySelection();
      this.propertyScroll.scroll.scrollTo(0);
      this.fillProperties();
   }

   private void syncHierarchySelection() {
      if (this.selected == null) {
         this.hierarchy.setCurrent((String)null);
         return;
      }

      for (Map.Entry<String, UIComponent> entry : this.hierarchyEntries.entrySet()) {
         if (entry.getValue() == this.selected) {
            this.hierarchy.setCurrentScroll(entry.getKey());
            return;
         }
      }
   }

   private void canvasChanged() {
      


      this.fillProperties();
   }

   private void moveSelected(int direction) {
      if (this.data == null || this.selected == null) {
         return;
      }
      UIParentComponent parent = this.findParent(this.data.root, this.selected);
      if (parent == null) {
         return;
      }
      this.saveSnapshot();
      int index = parent.children.indexOf(this.selected);
      int target = index + direction;
      if (target >= 0 && target < parent.children.size()) {
         Collections.swap(parent.children, index, target);
         this.refreshEditor();
      }
   }

   private void indentSelected() {
      if (this.data == null || this.selected == null) {
         return;
      }
      UIParentComponent parent = this.findParent(this.data.root, this.selected);
      if (parent == null) {
         return;
      }
      int index = parent.children.indexOf(this.selected);
      if (index > 0 && parent.children.get(index - 1) instanceof UIParentComponent) {
         this.saveSnapshot();
         UIParentComponent container = (UIParentComponent)parent.children.get(index - 1);
         parent.children.remove(index);
         container.children.add(this.selected);
         this.refreshEditor();
      }
   }

   private void outdentSelected() {
      if (this.data == null || this.selected == null) {
         return;
      }
      UIParentComponent parent = this.findParent(this.data.root, this.selected);
      if (parent == null || parent == this.data.root) {
         return;
      }
      UIParentComponent grandParent = this.findParent(this.data.root, parent);
      if (grandParent != null) {
         this.saveSnapshot();
         int parentIndex = grandParent.children.indexOf(parent);
         parent.children.remove(this.selected);
         grandParent.children.add(parentIndex + 1, this.selected);
         this.refreshEditor();
      }
   }

   private void removeSelected() {
      if (this.data != null && this.selected != null && this.removeFromParent(this.data.root, this.selected)) {
         this.saveSnapshot();
         this.selected = null;
         this.inspectorTab = InspectorTab.FILE;
         this.refreshEditor();
      }
   }
   private UIParentComponent findParent(UIParentComponent parent, UIComponent target) {
      for(UIComponent child : parent.children) {
         if (child == target) {
            return parent;
         }
         if (child instanceof UIParentComponent) {
            UIParentComponent found = this.findParent((UIParentComponent)child, target);
            if (found != null) {
               return found;
            }
         }
      }
      return null;
   }

   private boolean removeFromParent(UIParentComponent parent, UIComponent target) {
      if (parent.children.remove(target)) {
         return true;
      }

      for(UIComponent child : parent.children) {
         if (child instanceof UIParentComponent && this.removeFromParent((UIParentComponent)child, target)) {
            return true;
         }
      }

      return false;
   }

   private UIComponent findById(UIParentComponent parent, String id) {
      for(UIComponent child : parent.children) {
         if (id.equals(child.id)) {
            return child;
         }
         if (child instanceof UIParentComponent) {
            UIComponent found = this.findById((UIParentComponent)child, id);
            if (found != null) return found;
         }
      }
      return null;
   }

   private void refreshEditor() {
      this.rebuildHierarchy();
      this.fillProperties();
      this.rebuildPreview();
      this.editor.resize();
      this.layoutHierarchyPanel();
   }

   private void layoutHierarchyPanel() {
      if (this.editor.area.w <= 0 || this.editor.area.h <= 0) {
         return;
      }

      int x = this.editor.area.x + 8;
      int bottom = this.editor.area.ey() - 8;
      this.hierarchyPanel.area.setPoints(x, bottom - 128, x + 150, bottom);
      this.hierarchyPanel.resize();
   }

   private void rebuildHierarchy() {
      this.hierarchyEntries.clear();
      List<String> names = new ArrayList();
      if (this.data != null) {
         this.collectHierarchy(this.data.root, "", names);
      }
      this.hierarchy.setList(names);
      this.hierarchyCaption.label = IKey.str("Иерархия (" + names.size() + ")");
      this.syncHierarchySelection();
   }

   private mchorse.mclib.client.gui.utils.Icon componentIcon(UIComponent component) {
      return this.componentIcon(this.componentType(component));
   }

   private int componentColor(UIComponent component) {
      return this.componentColor(this.componentType(component));
   }

   private String componentType(UIComponent component) {
      if (component instanceof UIGraphicsComponent) return "graphics";
      if (component instanceof UIButtonComponent) return "button";
      if (component instanceof UILabelComponent) return "label";
      if (component instanceof UITextComponent) return "text";
      if (component instanceof UIIconComponent) return "icon";
      if (component instanceof UITextboxComponent) return "textbox";
      if (component instanceof UITextareaComponent) return "textarea";
      if (component instanceof UIToggleComponent) return "toggle";
      if (component instanceof UITrackpadComponent) return "trackpad";
      if (component instanceof UIColorComponent) return "color";
      if (component instanceof UIStringListComponent) return "strings";
      if (component instanceof UIStackComponent) return "item";
      if (component instanceof UIMorphComponent) return "morph";
      if (component instanceof UILayoutComponent) return "layout";
      if (component instanceof UIClickComponent) return "clickarea";
      return "";
   }

   private void collectHierarchy(UIParentComponent parent, String indent, List<String> names) {
      for(UIComponent component : parent.children) {
         String id = component.id.isEmpty() ? "<без ID>" : component.id;
         String label = indent + id;
         this.hierarchyEntries.put(label, component);
         names.add(label);
         if (component instanceof UIParentComponent) {
            this.collectHierarchy((UIParentComponent)component, indent + "  ", names);
         }
      }
   }

   private void fillProperties() {
      this.fillingProperties = true;
      boolean hasData = this.data != null;
      boolean hasComponent = this.selected != null;
      this.script.setVisible(hasData);
      this.function.setVisible(hasData);
      this.background.setVisible(hasData);
      this.closable.setVisible(hasData);
      this.paused.setVisible(hasData);
      for(GuiElement element : this.componentProperties) {
         element.setVisible(hasComponent);
      }
      boolean hasLabel = hasComponent && this.selected instanceof UILabelBaseComponent;
      this.setVisible(this.labelProperties, hasLabel);
      this.setVisible(this.labelStyleProperties, hasLabel);
      this.setVisible(this.buttonProperties, hasComponent && this.selected instanceof UIButtonComponent);
      this.setVisible(this.iconProperties, hasComponent && this.selected instanceof UIIconComponent);
      this.setVisible(this.textProperties, hasComponent && (this.selected instanceof UILabelComponent || this.selected instanceof UITextComponent));
      this.setVisible(this.inputProperties, hasComponent && this.selected instanceof UITextboxComponent);
      this.setVisible(this.toggleProperties, hasComponent && this.selected instanceof UIToggleComponent);
      this.setVisible(this.trackpadProperties, hasComponent && this.selected instanceof UITrackpadComponent);
      this.setVisible(this.colorProperties, hasComponent && this.selected instanceof UIColorComponent);
      this.setVisible(this.stringListProperties, hasComponent && this.selected instanceof UIStringListComponent);
      this.setVisible(this.layoutProperties, hasComponent && this.selected instanceof UILayoutComponent);
      this.setVisible(this.morphTooltipProperties, hasComponent && this.selected.tooltipMorph != null);
      boolean hasMorph = hasComponent && this.selected instanceof UIMorphComponent;
      this.setVisible(this.morphProperties, hasMorph);
      this.setVisible(this.stackProperties, hasComponent && this.selected instanceof UIStackComponent);
      this.setVisible(this.visualAnimationProperties, hasComponent && (this.selected instanceof UIButtonComponent || this.selected instanceof UIIconComponent));
      this.setVisible(this.graphicsProperties, hasComponent && this.selected instanceof UIGraphicsComponent);
      this.rebuildInspectorElements();
      this.previewInGame.setVisible(hasData);
      this.previewInGame.setEnabled(hasData && this.allowed);
      if (hasData) {
         this.script.setText(this.data.script);
         this.function.setText(this.data.function);
         this.background.toggled(this.data.background);
         this.closable.toggled(this.data.closable);
         this.paused.toggled(this.data.paused);
      }
      if (hasComponent) {
         this.componentId.setText(this.selected.id);
         if (this.selected instanceof UILabelBaseComponent) {
            this.label.setText(((UILabelBaseComponent)this.selected).label);
         }
         this.x.setValue(this.unitDisplayValue(this.selected.x));
         this.y.setValue(this.unitDisplayValue(this.selected.y));
         this.w.setValue(this.unitDisplayValue(this.selected.w));
         this.h.setValue(this.unitDisplayValue(this.selected.h));
         this.relativeX.toggled(this.selected.x.relative);
         this.relativeY.toggled(this.selected.y.relative);
         this.relativePosition.toggled(this.selected.x.relative && this.selected.y.relative);
         this.relativeW.toggled(this.selected.w.relative);
         this.relativeH.toggled(this.selected.h.relative);
         this.anchorX.setValue((double)this.selected.x.anchor);
         this.anchorY.setValue((double)this.selected.y.anchor);
         boolean morphTooltipEnabled = this.selected.tooltipMorphEnabled;
         this.tooltip.setText(this.selected.tooltip);
         this.tooltip.setEnabled(!morphTooltipEnabled);
         this.morphTooltip.setText(morphTooltipEnabled ? this.selected.tooltipMorphText : "");
         this.morphUseTooltip.toggled(this.selected.tooltipMorphEnabled);
         this.callbackEvent.setText(this.selected.callbackEvent);
         this.hoverEvent.setText(this.selected.hoverEvent);
         this.hoverEnterEvent.setText(this.selected.hoverEnterEvent);
         this.hoverExitEvent.setText(this.selected.hoverExitEvent);
         this.morphTooltipSelect.setEnabled(hasComponent);
         this.fillMorphTooltipSettings(this.selected);
         if (this.selected instanceof UILabelBaseComponent) {
            class_2487 labelTag = this.selected.serializeNBT();
            this.textColor.setValue((double)(labelTag.method_10545("Color") ? labelTag.method_10550("Color") : 16777215));
            this.textShadow.toggled(!labelTag.method_10545("TextShadow") || labelTag.method_10577("TextShadow"));
            this.textBackground.toggled(!labelTag.method_10545("HasBackground") || labelTag.method_10577("HasBackground"));
         }
         if (this.selected instanceof UILabelComponent) {
            UILabelComponent component = (UILabelComponent)this.selected;
            this.labelAnchorX.setValue((double)component.anchorX);
            this.labelAnchorY.setValue((double)component.anchorY);
         }
         if (this.selected instanceof UITextComponent) {
            this.textAnchor.setValue((double)((UITextComponent)this.selected).textAnchor);
         }
         if (this.selected instanceof UIButtonComponent) {
            UIButtonComponent component = (UIButtonComponent)this.selected;
            class_2487 buttonTag = component.serializeNBT();
            this.buttonBackground.setValue((double)(buttonTag.method_10545("Background") ? buttonTag.method_10550("Background") : 0));
            this.buttonHover.toggled(component.hoverEvents);
            this.buttonUnhover.toggled(component.unhoverEvents);
         }
         if (this.selected instanceof UIIconComponent) {
            UIIconComponent component = (UIIconComponent)this.selected;
            this.iconName.setText(component.icon);
            this.iconPicker.setSelected(component.icon);
            this.iconBackground.setValue((double)(component.background == null ? 0 : component.background));
            this.iconColor.setValue((double)(component.color == null ? 16777215 : component.color));
         }
         if (this.selected instanceof UIButtonComponent || this.selected instanceof UIIconComponent) {
            class_2487 visualTag = this.selected.serializeNBT();
            if (visualTag.method_10573("ScaleTo", 10)) {
               class_2487 scale = visualTag.method_10562("ScaleTo");
               this.scaleToX.setValue((double)scale.method_10583("X"));
               this.scaleToY.setValue((double)scale.method_10583("Y"));
               this.scaleToDuration.setValue((double)scale.method_10550("Duration"));
               this.scaleInterpolationValue = scale.method_10545("Interpolation") ? scale.method_10558("Interpolation") : "sine_inout";
            } else {
               this.scaleToX.setValue(1.0D);
               this.scaleToY.setValue(1.0D);
               this.scaleToDuration.setValue(0.0D);
               this.scaleInterpolationValue = "sine_inout";
            }
            this.scaleToInterpolation.setText(this.scaleInterpolationValue);
            if (visualTag.method_10573("ColorTo", 10)) {
               class_2487 color = visualTag.method_10562("ColorTo");
               this.colorToColor.setValue((double)color.method_10550("Color"));
               this.colorToDuration.setValue((double)color.method_10550("Duration"));
               this.colorInterpolationValue = color.method_10545("Interpolation") ? color.method_10558("Interpolation") : "sine_inout";
            } else {
               this.colorToColor.setValue(16777215.0D);
               this.colorToDuration.setValue(0.0D);
               this.colorInterpolationValue = "sine_inout";
            }
            this.colorToInterpolation.setText(this.colorInterpolationValue);
         }
         if (this.selected instanceof UITextboxComponent) {
            this.inputMaxLength.setValue((double)((UITextboxComponent)this.selected).maxLength);
         }
         if (this.selected instanceof UIToggleComponent) {
            this.toggleState.toggled(((UIToggleComponent)this.selected).state);
         }
         if (this.selected instanceof UIColorComponent) {
            this.colorValue.picker.setValue(((UIColorComponent)this.selected).color);
         }
         if (this.selected instanceof UITrackpadComponent) {
            UITrackpadComponent component = (UITrackpadComponent)this.selected;
            this.trackValue.setValue(component.value == null ? 0.0D : component.value);
            this.trackMin.setValue(component.min == null ? 0.0D : component.min);
            this.trackMax.setValue(component.max == null ? 0.0D : component.max);
            this.trackInteger.toggled(component.integer);
            this.trackNormal.setValue(component.normal == null ? 1.0D : component.normal);
            this.trackWeak.setValue(component.weak == null ? 0.2D : component.weak);
            this.trackStrong.setValue(component.strong == null ? 5.0D : component.strong);
            this.trackIncrement.setValue(component.increment == null ? 0.0D : component.increment);
         }
         if (this.selected instanceof UIStringListComponent) {
            UIStringListComponent component = (UIStringListComponent)this.selected;
            this.stringsValues.setText(String.join("\n", component.values));
            this.stringsSelected.setValue((double)(component.selected == null ? 0 : component.selected));
            this.stringsBackground.setValue((double)(component.background == null ? 0 : component.background));
         }
         if (this.selected instanceof UILayoutComponent) {
            UILayoutComponent component = (UILayoutComponent)this.selected;
            this.layoutScroll.toggled(component.scroll);
            this.layoutScrollSize.setValue((double)(component.scrollSize == null ? 0 : component.scrollSize));
            this.layoutHorizontal.toggled(component.horizontal);
            this.layoutType.setValue((double)(component.layoutType == null ? -1 : component.layoutType.ordinal()));
            this.layoutMargin.setValue((double)component.margin);
            this.layoutPadding.setValue((double)component.padding);
            this.layoutWidth.setValue((double)(component.width == null ? 0 : component.width));
            this.layoutItems.setValue((double)(component.items == null ? 0 : component.items));
         }
         if (this.selected instanceof UIMorphComponent) {
            UIMorphComponent component = (UIMorphComponent)this.selected;
            this.morphNbt.setText(component.morph == null ? "" : component.morph.toString());
            this.morphSelect.setEnabled(true);
            this.morphEdit.setEnabled(component.morph != null);
            this.morphEditing.toggled(component.editing);
            this.morphPosX.setValue((double)(component.pos == null ? 0.0F : component.pos.x));
            this.morphPosY.setValue((double)(component.pos == null ? 0.0F : component.pos.y));
            this.morphPosZ.setValue((double)(component.pos == null ? 0.0F : component.pos.z));
            this.morphPitch.setValue((double)(component.rot == null ? 0.0F : component.rot.x));
            this.morphYaw.setValue((double)(component.rot == null ? 0.0F : component.rot.y));
            this.morphDistance.setValue((double)component.distance);
            this.morphFov.setValue((double)component.fov);
         }
         if (this.selected instanceof UIStackComponent) {
            this.stackNbt.setText(NbtCompat.write(((UIStackComponent)this.selected).stack).toString());
         }
         if (this.selected instanceof UIGraphicsComponent) {
            this.graphicsNbt.setText(this.selected.serializeNBT().toString());
            this.refreshGraphicsList();
         } else {
            this.selectedGraphic = null;
         }
         this.tooltipDirection.setValue((double)this.selected.tooltipDirection);
         this.marginTop.setValue((double)this.selected.marginTop);
         this.marginBottom.setValue((double)this.selected.marginBottom);
         this.marginLeft.setValue((double)this.selected.marginLeft);
         this.marginRight.setValue((double)this.selected.marginRight);
         this.updateDelay.setValue((double)this.selected.updateDelay);
         class_2487 animation = this.selected.serializeNBT();
         if (animation.method_10573("MoveTo", 10)) {
            class_2487 move = animation.method_10562("MoveTo");
            this.moveToX.setValue((double)move.method_10583("X"));
            this.moveToY.setValue((double)move.method_10583("Y"));
            this.moveToDuration.setValue((double)move.method_10550("Duration"));
            this.moveInterpolationValue = move.method_10545("Interpolation") ? move.method_10558("Interpolation") : "sine_inout";
         } else {
            this.moveToX.setValue(0.0D);
            this.moveToY.setValue(0.0D);
            this.moveToDuration.setValue(0.0D);
            this.moveInterpolationValue = "sine_inout";
         }
         this.moveToInterpolation.setText(this.moveInterpolationValue);
         if (animation.method_10573("RotateTo", 10)) {
            class_2487 rotate = animation.method_10562("RotateTo");
            this.rotateToAngle.setValue((double)rotate.method_10583("Angle"));
            this.rotateToDuration.setValue((double)rotate.method_10550("Duration"));
            this.rotateInterpolationValue = rotate.method_10545("Interpolation") ? rotate.method_10558("Interpolation") : "sine_inout";
         } else {
            this.rotateToAngle.setValue(0.0D);
            this.rotateToDuration.setValue(0.0D);
            this.rotateInterpolationValue = "sine_inout";
         }
         this.rotateToInterpolation.setText(this.rotateInterpolationValue);
         this.visible.toggled(this.selected.visible);
         this.enabled.toggled(this.selected.enabled);
      }
      this.fillingProperties = false;
   }

   private double unitDisplayValue(UIUnit unit) {
      return unit.relative ? (double)unit.value : (double)unit.offset;
   }

   private void rebuildPreview() {
      this.preview.set(this.data, this.selected);
      
      if (!this.isDragging) {
         this.saveSnapshot();
      }
   }

   public void saveSnapshot() {
      if (this.data == null) {
         return;
      }
      this.redoStack.clear();
      class_2487 snapshot = this.data.serializeNBT();
      this.undoStack.push(snapshot);
      if (this.undoStack.size() > 50) {
         this.undoStack.removeLast();
      }
   }

   private void undo() {
      if (this.undoStack.isEmpty() || this.data == null) {
         return;
      }
      this.redoStack.push(this.data.serializeNBT());
      class_2487 snapshot = this.undoStack.pop();
      this.data.deserializeNBT(snapshot);
      this.selected = null;
      this.inspectorTab = InspectorTab.FILE;
      this.refreshEditor();
   }

   private void redo() {
      if (this.redoStack.isEmpty() || this.data == null) {
         return;
      }
      this.undoStack.push(this.data.serializeNBT());
      class_2487 snapshot = this.redoStack.pop();
      this.data.deserializeNBT(snapshot);
      this.selected = null;
      this.inspectorTab = InspectorTab.FILE;
      this.refreshEditor();
   }

   
   @Override
   public boolean keyTyped(GuiContext context) {
      boolean ctrl = context.keyCode == 29 || context.keyCode == 157;  
      
      if (ctrl) {
         if (context.keyCode == 26) {  
            this.undo();
            return true;
         }
         if (context.keyCode == 21) {  
            this.redo();
            return true;
         }
         if (context.keyCode == 3) {  
            this.copy();
            return true;
         }
         if (context.keyCode == 22) {  
            this.paste();
            return true;
         }
         if (context.keyCode == 4) {  
            this.duplicate();
            return true;
         }
      }
      return super.keyTyped(context);
   }

   private void copy() {
      if (this.selected != null && this.data != null) {
         
         UIComponent copy = this.deepCopyComponent(this.selected);
         if (copy != null) {
            
            UIParentComponent tempParent = new UILayoutComponent();
            tempParent.children.add(copy);

            class_2487 tag = (class_2487)tempParent.serializeNBT();
            tag.method_10582("_ContentType", "UI");
            GuiUtils.setClipboardString(tag.toString());
         }
      }
   }

   private UIComponent deepCopyComponent(UIComponent original) {
      if (original == null) return null;

      
      String className = original.getClass().getSimpleName();
      String type = className.toLowerCase();
      if (type.startsWith("ui")) {
         type = type.substring(2);
      }
      if (type.endsWith("component")) {
         type = type.substring(0, type.length() - 9);
      }

      UIComponent copy = (UIComponent)CommonProxy.getUiComponents().create(type);
      if (copy == null) return original;

      copy.id = this.generateUniqueId();
      copy.visible = original.visible;
      copy.enabled = original.enabled;
      copy.updateDelay = original.updateDelay;
      copy.marginTop = original.marginTop;
      copy.marginBottom = original.marginBottom;
      copy.marginLeft = original.marginLeft;
      copy.marginRight = original.marginRight;
      copy.tooltip = original.tooltip;
      copy.tooltipMorph = original.tooltipMorph;

      
      class_2487 nbt = (class_2487)original.serializeNBT();
      copy.deserializeNBT(nbt);
      
      copy.id = this.generateUniqueId();

      return copy;
   }

   private String generateUniqueId() {
      return "comp_" + System.currentTimeMillis();
   }

   private void paste() {
      try {
         class_2487 tag = class_2522.method_10718(GuiUtils.getClipboardString());
         if (tag != null && tag.method_10558("_ContentType").equals("UI")) {
            
            UIParentComponent tempParent = new UILayoutComponent();
            tempParent.deserializeNBT(tag);

            if (!tempParent.children.isEmpty()) {
               this.saveSnapshot();
               UIComponent pastedComponent = tempParent.children.get(0);
               this.fixComponentIds(pastedComponent);
               UIParentComponent parent = this.selected instanceof UIParentComponent ? (UIParentComponent)this.selected : this.data.root;
               parent.children.add(pastedComponent);
               this.selected = pastedComponent;
               this.inspectorTab = InspectorTab.GENERAL;
               this.refreshEditor();
            }
         }
      } catch (Exception e) {
         Mappet.LOGGER.error("Error pasting UI component", e);
      }
   }

   private void fixComponentIds(UIComponent component) {
      if (component == null) return;

      component.id = this.generateUniqueId();
      if (component instanceof UIParentComponent) {
         for (UIComponent child : ((UIParentComponent)component).children) {
            this.fixComponentIds(child);
         }
      }
   }

   private void duplicate() {
      if (this.selected == null) return;

      UIComponent copy = this.deepCopyComponent(this.selected);
      if (copy == null) return;

      this.saveSnapshot();

      
      UIParentComponent parent = this.findParent(this.data.root, this.selected);
      if (parent != null) {
         int index = parent.children.indexOf(this.selected);
         this.fixComponentIds(copy);
         parent.children.add(index + 1, copy);
         this.selected = copy;
         this.inspectorTab = InspectorTab.GENERAL;
         this.refreshEditor();
      }
   }



   public ContentType getType() {
      return ContentType.UIS;
   }

   public String getTitle() {
      return "mappet.gui.panels.uis";
   }

   public void fill(UIFile data, boolean allowed) {
      super.fill(data, allowed);
      this.editor.setVisible(data != null);
      this.undoStack.clear();
      this.redoStack.clear();
      UIComponent restored = data == null ? null : this.componentAtPath(data.root, this.selectionPathBeforePreview);
      this.selectionPathBeforePreview = null;
      this.selected = restored;
      this.inspectorTab = restored == null ? InspectorTab.FILE : (this.inspectorTabBeforePreview == null ? InspectorTab.GENERAL : this.inspectorTabBeforePreview);
      this.inspectorTabBeforePreview = null;
      this.refreshEditor();
   }

   public void draw(GuiContext context) {
      if (this.editor.isVisible()) {
         this.layoutHierarchyPanel();
         this.refreshInspectorScroll();
         this.palette.area.draw(-2013265920);
         this.hierarchyPanel.area.draw(-2013265920);
         this.preview.area.draw(-1879048192);
         this.properties.area.draw(-2013265920);
         GuiDraw.drawStringWithShadow(this.font, IKey.lang("mappet.gui.ui_editor.preview").get(), this.preview.area.x, this.preview.area.y - 12, 16777215);
         GuiDraw.drawStringWithShadow(this.font, IKey.lang("mappet.gui.ui_editor.components").get(), this.palette.area.x, this.palette.area.y - 12, 16777215);
         GuiDraw.drawStringWithShadow(this.font, IKey.lang("mappet.gui.ui_editor.properties").get(), this.properties.area.x, this.properties.area.y - 12, 16777215);
      }
      this.morphTooltipPreview.beginFrame();
      super.draw(context);
      if (this.inspectorTab == InspectorTab.GENERAL && this.selected != null && this.selected.tooltipMorphEnabled && this.morphTooltipPreview.hasParent() && this.morphTooltipPreview.wasDrawnInCurrentFrame() && this.morphTooltipPreview.hasMorph()) {
         this.morphTooltipPreview.drawPhysical(context);
      }
   }
}
