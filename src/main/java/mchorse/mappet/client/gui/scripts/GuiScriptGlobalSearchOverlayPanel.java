package mchorse.mappet.client.gui.scripts;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlayPanel;
import mchorse.mappet.network.common.scripts.ScriptSearchResult;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringSearchListElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiScriptGlobalSearchOverlayPanel extends GuiOverlayPanel
{
    private static final String EMPTY_LABEL = "Ничего не найдено";
    private static final long DOUBLE_CLICK_DELAY = 450L;

    private final GuiTextElement query;
    private final GuiStringSearchListElement results;
    private final Map<String, ScriptSearchResult> entries = new LinkedHashMap<>();
    private final Consumer<String> searchCallback;
    private final Consumer<ScriptSearchResult> selectCallback;
    private String lastSelected;
    private long lastSelectedAt;

    public GuiScriptGlobalSearchOverlayPanel(class_310 mc, Consumer<String> searchCallback, Consumer<ScriptSearchResult> selectCallback)
    {
        super(mc, IKey.str("Глобальный поиск по скриптам"));
        this.searchCallback = searchCallback;
        this.selectCallback = selectCallback;
        this.query = new GuiTextElement(mc, 256, (text) -> this.clearResults());
        this.query.tooltip(IKey.str("Текст для поиска во всех скриптах"));
        this.query.flex().relative(this.content).xy(0, 0).w(1.0F).h(18);

        GuiButtonElement search = new GuiButtonElement(mc, IKey.str("Искать во всех скриптах"), (button) -> this.search());
        search.tooltip(IKey.str("Искать без учёта регистра"));
        search.flex().relative(this.content).xy(0, 25).w(1.0F).h(18);

        this.results = new GuiStringSearchListElement(mc, (list) -> this.select((String)list.get(0)));
        this.results.label(IKey.str("Фильтр результатов"));
        this.results.flex().relative(this.content).xy(0, 50).w(1.0F).h(1.0F, -50);
        this.content.add(new IGuiElement[]{this.query, search, this.results});
    }

    public void showResults(Collection<ScriptSearchResult> found)
    {
        this.entries.clear();
        this.results.list.getList().clear();

        if (found == null || found.isEmpty())
        {
            this.results.list.add(EMPTY_LABEL);
        }
        else
        {
            Map<String, List<ScriptSearchResult>> grouped = new LinkedHashMap<>();
            for (ScriptSearchResult result : found)
            {
                grouped.computeIfAbsent(result.script, (key) -> new ArrayList<>()).add(result);
            }

            for (Map.Entry<String, List<ScriptSearchResult>> group : grouped.entrySet())
            {
                String header = "{} " + group.getKey() + " (" + group.getValue().size() + ")";
                this.results.list.add(header);
                for (ScriptSearchResult result : group.getValue())
                {
                    String label = "    [" + result.line + "]  " + result.text;
                    this.entries.put(label, result);
                    this.results.list.add(label);
                }
            }
        }

        this.results.filter("", true);
        this.lastSelected = null;
        this.lastSelectedAt = 0L;
    }

    private void search()
    {
        String text = this.query.field.getText();
        if (text == null || text.trim().isEmpty())
        {
            this.clearResults();
            return;
        }

        if (this.searchCallback != null)
        {
            this.searchCallback.accept(text.trim());
        }
    }

    private void select(String label)
    {
        ScriptSearchResult result = this.entries.get(label);
        if (result == null)
        {
            return;
        }

        long now = System.currentTimeMillis();
        if (label.equals(this.lastSelected) && now - this.lastSelectedAt <= DOUBLE_CLICK_DELAY)
        {
            this.lastSelected = null;
            this.lastSelectedAt = 0L;
            this.close();
            if (this.selectCallback != null)
            {
                this.selectCallback.accept(result);
            }

            return;
        }

        this.lastSelected = label;
        this.lastSelectedAt = now;
    }

    private void clearResults()
    {
        this.entries.clear();
        this.results.list.getList().clear();
        this.results.filter("", true);
        this.lastSelected = null;
        this.lastSelectedAt = 0L;
    }
}
