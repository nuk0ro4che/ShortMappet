package mchorse.mappet.client.gui.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import java.util.function.Function;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mappet.compat.client.LegacyGlStateManager;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.utils.Icons;
import net.minecraft.class_310;





public class GuiStringFolderList extends GuiStringListElement
{
    private final List<String> hierarchy = new ArrayList();
    private final Set<String> expandedFolders = new HashSet();
    private final Map<String, TreeEntry> entries = new HashMap();
    private String activeFolder = "";
    private Icon fileIcon;
    private Function<String, Integer> diagnosticStatusResolver;
    private Consumer<String> middleClickCallback;
    private Consumer<List<String>> fileSelectionCallback;
    private BiConsumer<String, String> fileDropCallback;
    private String draggingFile;
    private int dragStartY;
    private int dragStartX;
    private int dragMouseOffsetX;
    private int dragMouseOffsetY;
    private boolean visualDragging;
    private boolean contextFile;
    private static final int DIAGNOSTIC_WARNING_COLOR = -16128;
    private static final int DIAGNOSTIC_ERROR_COLOR = -65536;

    public GuiStringFolderList(class_310 mc, Consumer<List<String>> callback)
    {
        super(mc, callback);
        this.fileIcon = Icons.FILE;
        this.fileSelectionCallback = callback;
        this.callback = (values) -> this.fileCallback(callback, values);
    }

    private void fileCallback(Consumer<List<String>> callback, List<String> values)
    {
        if (values.isEmpty())
        {
            return;
        }

        TreeEntry entry = this.entries.get(values.get(0));
        if (entry == null)
        {
            return;
        }

        if (entry.folder)
        {
            

            this.activeFolder = entry.path;
            return;
        }

        values.clear();
        values.add(entry.path);
        callback.accept(values);
    }

    public void setFileIcon(Icon icon)
    {
        this.fileIcon = icon;
    }

    
    public void setDiagnosticStatusResolver(Function<String, Integer> resolver)
    {
        this.diagnosticStatusResolver = resolver;
    }

    
    public void onMiddleClick(Consumer<String> callback)
    {
        this.middleClickCallback = callback;
    }

    
    public void onFileDrop(BiConsumer<String, String> callback)
    {
        this.fileDropCallback = callback;
    }

    
    public boolean isContextFile()
    {
        return this.contextFile;
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (context.mouseButton == 1)
        {
            this.contextFile = false;
        }
        if (this.scroll.isInside(context))
        {
            int index = this.scroll.getIndex(context.mouseX, context.mouseY);
            if (this.exists(index))
            {
                TreeEntry entry = this.entries.get(this.list.get(index));
                if (context.mouseButton == 1 && entry != null)
                {
                    this.contextFile = !entry.folder;
                }
                if (context.mouseButton == 0 && entry != null && entry.folder && this.isFolderArrow(entry, context.mouseX))
                {
                    this.toggleFolder(entry.path);
                    return true;
                }

                if (context.mouseButton == 0 && entry != null && !entry.folder)
                {
                    this.draggingFile = entry.path;
                    this.dragStartX = context.mouseX;
                    this.dragStartY = context.mouseY;
                    this.dragMouseOffsetX = 12;
                    this.dragMouseOffsetY = 8;
                    return true;
                }

                if (context.mouseButton == 2)
                {
                    if (entry != null && !entry.folder && this.middleClickCallback != null)
                    {
                        this.middleClickCallback.accept(entry.path);
                    }
                    return true;
                }
            }
        }

        return super.mouseClicked(context);
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        if (this.draggingFile == null)
        {
            return;
        }
        String source = this.draggingFile;
        boolean dragged = Math.abs(context.mouseX - this.dragStartX) >= 4 || Math.abs(context.mouseY - this.dragStartY) >= 4;
        this.draggingFile = null;
        if (!dragged)
        {
            int index = this.scroll.getIndex(context.mouseX, context.mouseY);
            if (this.exists(index) && this.fileSelectionCallback != null)
            {
                TreeEntry entry = this.entries.get(this.list.get(index));
                if (entry != null && !entry.folder)
                {
                    this.fileSelectionCallback.accept(Collections.singletonList(entry.path));
                }
            }
            return;
        }
        if (source == null)
        {
            return;
        }
        if (!this.scroll.isInside(context))
        {
            return;
        }
        int index = this.scroll.getIndex(context.mouseX, context.mouseY);
        String destination = "";

        if (this.exists(index))
        {
            TreeEntry target = this.entries.get(this.list.get(index));
            if (target != null)
            {
                if (target.path.equals(source))
                {
                    return;
                }

                destination = target.folder ? target.path : parent(target.path);
            }
        }

        

        if (this.fileDropCallback != null && !destination.equals(parent(source)))
        {
            this.fileDropCallback.accept(source, destination);
        }
    }

    
    public String getPath()
    {
        return this.activeFolder;
    }

    public String getPath(String name)
    {
        return this.activeFolder.isEmpty() ? name : this.activeFolder + "/" + name;
    }

    public void fill(Collection<String> files)
    {
        this.hierarchy.clear();
        this.hierarchy.addAll(files);
        this.activeFolder = "";
        this.filter("");
        this.rebuildTree();
    }

    public boolean hasInHierarchy(String path)
    {
        return this.hierarchy.contains(path);
    }

    
    public void keepFolder(String path)
    {
        String folder = normalize(path);
        if (folder.isEmpty())
        {
            return;
        }

        String marker = folder + "/";
        if (this.hierarchy.add(marker))
        {
            this.expandParents(folder);
        }
    }

    public void addFile(String path)
    {
        if (path == null || path.isEmpty() || this.hierarchy.contains(path))
        {
            return;
        }

        this.hierarchy.add(path);
        this.expandParents(path);
        this.activeFolder = parent(path);
        this.rebuildTree();
        this.setCurrentFile(path);
    }

    public void removeFile(String path)
    {
        if (this.hierarchy.remove(path))
        {
            this.rebuildTree();
            this.setIndex(-1);
        }
    }

    
    public void setCurrentFile(String path)
    {
        if (path == null || path.isEmpty())
        {
            return;
        }

        this.expandParents(path);
        this.activeFolder = parent(path);
        this.rebuildTree();
        

        this.setCurrent(entryId(path));
    }

    
    private boolean isFolderArrow(TreeEntry entry, int mouseX)
    {
        int left = this.scroll.x + entry.depth * 12;
        return mouseX >= left && mouseX < left + 10;
    }

    
    private void toggleFolder(String path)
    {
        String selected = (String)this.getCurrentFirst();
        if (!this.expandedFolders.add(path))
        {
            this.expandedFolders.remove(path);
        }
        this.rebuildTree();
        if (selected != null)
        {
            this.setCurrent(selected);
        }
    }

    private void expandParents(String path)
    {
        String folder = parent(path);
        while (!folder.isEmpty())
        {
            this.expandedFolders.add(folder);
            folder = parent(folder);
        }
    }

    private void rebuildTree()
    {
        this.entries.clear();
        this.list.clear();
        this.appendChildren("", 0);
        this.update();
    }

    private void appendChildren(String parent, int depth)
    {
        List<String> folders = this.collectFolders(parent);
        List<String> files = this.collectFiles(parent);
        Collections.sort(folders, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(files, String.CASE_INSENSITIVE_ORDER);

        for (String folder : folders)
        {
            TreeEntry entry = new TreeEntry(folder, true, depth, name(folder));
            this.entries.put(entry.id, entry);
            this.list.add(entry.id);
            if (this.expandedFolders.contains(folder))
            {
                this.appendChildren(folder, depth + 1);
            }
        }

        for (String file : files)
        {
            TreeEntry entry = new TreeEntry(file, false, depth, name(file));
            this.entries.put(entry.id, entry);
            this.list.add(entry.id);
        }
    }

    private List<String> collectFolders(String parent)
    {
        Set<String> folders = new HashSet();
        String prefix = parent.isEmpty() ? "" : parent + "/";

        for (String raw : this.hierarchy)
        {
            String path = normalize(raw);
            if (path.isEmpty() || !path.startsWith(prefix))
            {
                continue;
            }

            String remainder = path.substring(prefix.length());
            int slash = remainder.indexOf('/');
            if (slash >= 0)
            {
                folders.add(prefix + remainder.substring(0, slash));
            }
            else if (raw.endsWith("/"))
            {
                folders.add(path);
            }
        }

        return new ArrayList(folders);
    }

    private List<String> collectFiles(String parent)
    {
        List<String> files = new ArrayList();
        for (String raw : this.hierarchy)
        {
            String path = normalize(raw);
            if (!raw.endsWith("/") && parent(path).equals(parent))
            {
                files.add(path);
            }
        }

        return files;
    }

    private static String normalize(String path)
    {
        if (path == null)
        {
            return "";
        }

        return path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    }

    private static String parent(String path)
    {
        int index = path.lastIndexOf('/');
        return index < 0 ? "" : path.substring(0, index);
    }

    private static String name(String path)
    {
        int index = path.lastIndexOf('/');
        return index < 0 ? path : path.substring(index + 1);
    }

    private static String entryId(String path)
    {
        return "\u0001" + path;
    }

    @Override
    public void draw(GuiContext context)
    {
        this.visualDragging = this.draggingFile != null && this.isVisualDrag(context);
        super.draw(context);
        if (this.draggingFile == null || !this.isVisualDrag(context))
        {
            return;
        }

        String label = name(this.draggingFile);
        int width = Math.max(96, Math.min(260, this.font.method_1727(label) + 38));
        int windowWidth = this.mc.method_22683().method_4489();
        int windowHeight = this.mc.method_22683().method_4506();
        int left = Math.max(2, Math.min(context.mouseX - this.dragMouseOffsetX, windowWidth - width - 2));
        int top = Math.max(2, Math.min(context.mouseY - this.dragMouseOffsetY, windowHeight - 18));
        GuiDraw.drawRect(left, top, left + width, top + 16, -10066330);
        this.fileIcon.render(left + 4, top);
        GuiDraw.drawString(this.font, label, left + 20, top + 5, -1, false);
    }

    private boolean isVisualDrag(GuiContext context)
    {
        return Math.abs(context.mouseX - this.dragStartX) >= 4 || Math.abs(context.mouseY - this.dragStartY) >= 4;
    }

    @Override
    protected void drawElementPart(String element, int index, int x, int y, boolean hover, boolean selected)
    {
        TreeEntry entry = this.entries.get(element);
        if (entry == null)
        {
            return;
        }

        int entryX = x + entry.depth * 12;
        LegacyGlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (entry.folder)
        {
            if (this.draggingFile != null && hover)
            {
                GuiDraw.drawRect(this.area.x, y, this.area.ex(), y + 16, -10053172);
            }
            this.drawFolderArrow(entryX, y, this.expandedFolders.contains(entry.path));
            Icons.FOLDER.render(entryX + 10, y);
            super.drawElementPart(entry.name, index, entryX + 22, y, hover, selected);
            return;
        }

        if (entry.path.equals(this.draggingFile))
        {
            if (this.visualDragging)
            {
                return;
            }
            GuiDraw.drawRect(this.area.x, y, this.area.ex(), y + 16, -11513776);
        }
        this.fileIcon.render(entryX + 10, y);
        super.drawElementPart(entry.name, index, entryX + 22, y, hover, selected);
        if (this.diagnosticStatusResolver != null)
        {
            Integer status = this.diagnosticStatusResolver.apply(entry.path);
            if (status != null && status > 0)
            {
                GuiDraw.drawRect(this.area.ex() - 12, y + 2, this.area.ex() - 9, y + 14, status == 2 ? DIAGNOSTIC_ERROR_COLOR : DIAGNOSTIC_WARNING_COLOR);
            }
        }
    }

    
    private void drawFolderArrow(int x, int y, boolean expanded)
    {
        int centerX = x + 7;
        int centerY = y + 6;

        for (int offset = 0; offset <= 2; ++offset)
        {
            if (expanded)
            {
                int pixelY = centerY + 2 - offset;
                GuiDraw.drawRect(centerX - offset, pixelY, centerX - offset + 1, pixelY + 1, -1);
                GuiDraw.drawRect(centerX + offset, pixelY, centerX + offset + 1, pixelY + 1, -1);
            }
            else
            {
                int pixelX = centerX + 2 - offset;
                GuiDraw.drawRect(pixelX, centerY - offset, pixelX + 1, centerY - offset + 1, -1);
                GuiDraw.drawRect(pixelX, centerY + offset, pixelX + 1, centerY + offset + 1, -1);
            }
        }
    }

    
    @Override
    protected boolean sortElements()
    {
        return true;
    }

    private static class TreeEntry
    {
        public final String id;
        public final String path;
        public final boolean folder;
        public final int depth;
        public final String name;

        public TreeEntry(String path, boolean folder, int depth, String name)
        {
            this.id = entryId(path);
            this.path = path;
            this.folder = folder;
            this.depth = depth;
            this.name = name;
        }
    }
}
