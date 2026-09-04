package mchorse.mappet.client.gui.scripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import mchorse.mappet.Mappet;
import mchorse.mappet.client.gui.scripts.utils.documentation.DocClass;
import mchorse.mappet.client.gui.scripts.utils.documentation.DocDelegate;
import mchorse.mappet.client.gui.scripts.utils.documentation.DocEntry;
import mchorse.mappet.client.gui.scripts.utils.documentation.DocList;
import mchorse.mappet.client.gui.scripts.utils.documentation.DocMerger;
import mchorse.mappet.client.gui.scripts.utils.documentation.DocMethod;
import mchorse.mappet.client.gui.scripts.utils.documentation.DocPackage;
import mchorse.mappet.client.gui.scripts.utils.documentation.Docs;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlayPanel;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiSearchListElement;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1074;
import net.minecraft.class_310;

public class GuiDocumentationOverlayPanel extends GuiOverlayPanel {
   public static Docs docs;
   private static DocEntry top;
   private static DocEntry entry;
   public GuiDocEntrySearchList list;
   public GuiScrollElement documentation;
   public GuiIconElement javadocs;

   public static List<DocClass> search(String text) {
      List<DocClass> list = new ArrayList();

      for(DocClass docClass : getDocs().classes) {
         if (docClass.getMethod(text) != null) {
            list.add(docClass);
         }
      }

      return list;
   }

   public static Docs getDocs() {
      parseDocs();
      return docs;
   }

   private static void parseDocs() {
      boolean dev = FabricLoader.getInstance().isDevelopmentEnvironment();
      if (dev || docs == null) {
         docs = DocMerger.getMergedDocs();
         entry = null;
         if (docs == null) {
            docs = new Docs();
            return;
         }

         docs.copyMethods("UILabelBaseComponent", "UIButtonComponent", "UILabelComponent", "UITextComponent", "UITextareaComponent", "UITextboxComponent", "UIToggleComponent");
         docs.remove("UIParentComponent");
         docs.remove("UILabelBaseComponent");
         Map<String, DocList> docLists = new HashMap();
         DocList topPackage = new DocList();
         DocList scripting = new DocList();
         DocList entities = new DocList();
         DocList nbt = new DocList();
         DocList items = new DocList();
         DocList blocks = new DocList();
         DocList ui = new DocList();
         docLists.put("topPackage", topPackage);
         docLists.put("scripting", scripting);
         docLists.put("entities", entities);
         docLists.put("nbt", nbt);
         docLists.put("items", items);
         docLists.put("blocks", blocks);
         docLists.put("ui", ui);
         mixinsHook();
         topPackage.doc = docs.getPackage("mchorse.mappet.api.scripts.user.mappet").doc;
         scripting.name = "Scripting API";
         scripting.doc = docs.getPackage("mchorse.mappet.api.scripts.user").doc;
         scripting.parent = topPackage;
         ui.name = "UI API";
         ui.doc = docs.getPackage("mchorse.mappet.api.ui.components").doc;
         ui.parent = topPackage;
         boolean useNewStructure = (Boolean)Mappet.scriptDocsNewStructure.get();
         List<DocPackage> extraPackages = (List)docs.packages.stream().filter((docPackagex) -> docPackagex.name.startsWith("extra")).collect(Collectors.toList());
         List<DocList> extraDocLists = new ArrayList();

         for(DocPackage docPackage : extraPackages) {
            String firstPackage = docPackage.name.substring(0, docPackage.name.indexOf("."));
            DocList extra = new DocList();
            extra.name = docPackage.name.substring(docPackage.name.lastIndexOf(".") + 1);
            extra.doc = docPackage.doc;
            extra.parent = firstPackage.equals("extraScripting") ? scripting : (firstPackage.equals("extraUI") ? ui : topPackage);
            extra.source = docPackage.source;
            ((DocList)extra.parent).entries.add(extra);
            extraDocLists.add(extra);
         }

         if (useNewStructure) {
            entities.name = "/ Entities";
            entities.doc = docs.getPackage("mchorse.mappet.api.scripts.user.entities").doc;
            entities.parent = scripting;
            scripting.entries.add(entities);
            nbt.name = "/ NBT";
            nbt.doc = docs.getPackage("mchorse.mappet.api.scripts.user.nbt").doc;
            nbt.parent = scripting;
            scripting.entries.add(nbt);
            items.name = "/ Items";
            items.doc = docs.getPackage("mchorse.mappet.api.scripts.user.items").doc;
            items.parent = scripting;
            scripting.entries.add(items);
            blocks.name = "/ Blocks";
            blocks.doc = docs.getPackage("mchorse.mappet.api.scripts.user.blocks").doc;
            blocks.parent = scripting;
            scripting.entries.add(blocks);
         }

         for(DocClass docClass : docs.classes) {
            docClass.setup();
            if (docClass.name.startsWith("extra")) {
               String packages = docClass.name.substring(docClass.name.indexOf(".") + 1, docClass.name.lastIndexOf("."));

               try {
                  List<DocList> lists = (List)extraDocLists.stream().filter((docList) -> docList.name.equals(packages)).collect(Collectors.toList());
                  DocList list = (DocList)lists.get(0);
                  if (list != null) {
                     list.entries.add(docClass);
                     docClass.parent = list;
                  }
               } catch (Exception var20) {
               }
            } else if (!docClass.name.contains("ui.components") && !docClass.name.endsWith(".Graphic")) {
               if (useNewStructure) {
                  List<Callable<Boolean>> functions = new ArrayList();
                  boolean added = false;
                  functions.add((Callable)() -> addWithNewStructure((input) -> input.name.contains("entities"), docClass, (DocList)docLists.get("entities")));
                  functions.add((Callable)() -> addWithNewStructure((input) -> input.name.contains("nbt"), docClass, (DocList)docLists.get("nbt")));
                  functions.add((Callable)() -> addWithNewStructure((input) -> input.name.contains("items"), docClass, (DocList)docLists.get("items")));
                  functions.add((Callable)() -> addWithNewStructure((input) -> input.name.contains("blocks"), docClass, (DocList)docLists.get("blocks")));
                  mixinsHook();
                  functions.add((Callable)() -> addWithNewStructure((input) -> !input.name.endsWith("Graphic"), docClass, (DocList)docLists.get("scripting")));

                  for(Callable<Boolean> function : functions) {
                     if (added) {
                        break;
                     }

                     try {
                        added = (Boolean)function.call();
                     } catch (Exception e) {
                        throw new RuntimeException(e);
                     }
                  }
               } else if (!docClass.name.endsWith("Graphic")) {
                  scripting.entries.add(docClass);
                  docClass.parent = scripting;
               }
            } else {
               ui.entries.add(docClass);
               docClass.parent = ui;
            }
         }

         topPackage.entries.add(scripting);
         topPackage.entries.add(ui);
         top = topPackage;
      }

   }

   public static void mixinsHook() {
   }

   public static boolean addWithNewStructure(Function<DocClass, Boolean> predicate, DocClass docClass, DocList list) {
      if (!(Boolean)predicate.apply(docClass)) {
         return false;
      } else {
         list.entries.add(docClass);
         docClass.parent = list;
         return true;
      }
   }

   public GuiDocumentationOverlayPanel(class_310 mc) {
      this(mc, (DocEntry)null);
   }

   public GuiDocumentationOverlayPanel(class_310 mc, DocEntry entry) {
      super(mc, IKey.lang("mappet.gui.scripts.documentation.title"));
      this.list = new GuiDocEntrySearchList(mc, (l) -> this.pick((DocEntry)l.get(0)));
      this.list.label(IKey.lang("mappet.gui.search"));
      this.documentation = new GuiScrollElement(mc);
      this.list.flex().relative(this.content).w(240).h(1.0F);
      this.documentation.flex().relative(this.content).x(240).w(1.0F, -240).h(1.0F).column(4).vertical().stretch().scroll().padding(10);
      this.content.add(new IGuiElement[]{this.list, this.documentation});
      this.javadocs = new GuiIconElement(mc, Icons.SERVER, (b) -> this.openJavadocs());
      this.javadocs.tooltip(IKey.lang("mappet.gui.scripts.documentation.javadocs")).flex().wh(16, 16);
      this.icons.flex().row(0).reverse().resize().width(32).height(16);
      this.icons.addAfter(this.close, this.javadocs);
      this.setupDocs(entry);
   }

   private void pick(DocEntry entryIn) {
      if (entryIn instanceof DocList && "Keycodes".equals(entryIn.getName())) {
         return;
      }

      boolean isMethod = entryIn instanceof DocMethod;
      entryIn = entryIn.getEntry();
      List<DocEntry> entries = entryIn.getEntries();
      boolean wasSame = this.list.list.getList().size() >= 2 && ((DocEntry)this.list.list.getList().get(1)).parent == entryIn.parent;
      if (entry == entryIn || !wasSame) {
         this.list.list.clear();
         if (entryIn.parent != null) {
            this.list.list.add(new DocDelegate(entryIn.parent));
         }

         this.list.list.add(entries);
         this.list.list.sort();
         if (isMethod) {
            this.list.list.setCurrentScroll(entryIn);
         }
      }

      this.fill(entryIn);
   }

   private void fill(DocEntry entryIn) {
      if (!(entryIn instanceof DocMethod)) {
         entry = entryIn;
      }

      this.documentation.scroll.scrollTo(0);
      this.documentation.removeAll();
      entryIn.fillIn(this.mc, this.documentation);
      this.resize();
   }

   private void setupDocs(DocEntry in) {
      parseDocs();
      if (in != null) {
         entry = in;
      } else if (entry == null) {
         entry = top;
      }

      this.pick(entry);
   }

   private void openJavadocs() {
      GuiUtils.openWebLink(class_1074.method_4662("mappet.gui.scripts.documentation.javadocs_url", new Object[0]));
   }

   public static class GuiDocEntrySearchList extends GuiSearchListElement<DocEntry> {
      public GuiDocEntrySearchList(class_310 mc, Consumer<List<DocEntry>> callback) {
         super(mc, callback);
      }

      protected GuiListElement<DocEntry> createList(class_310 minecraft, Consumer<List<DocEntry>> consumer) {
         return new GuiDocEntryList(minecraft, consumer);
      }
   }

   public static class GuiDocEntryList extends GuiListElement<DocEntry> {
      public GuiDocEntryList(class_310 mc, Consumer<List<DocEntry>> callback) {
         super(mc, callback);
         this.scroll.scrollItemSize = 16;
         ScrollArea var10000 = this.scroll;
         var10000.scrollSpeed *= 2;
      }

      protected boolean sortElements() {
         this.list.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
         return true;
      }

      protected String elementToString(DocEntry element) {
         return element.getName();
      }
   }
}
