package mchorse.mappet.network.server.scripts;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.scripts.Script;
import mchorse.mappet.api.scripts.ScriptManager;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketRequestScriptSearch;
import mchorse.mappet.network.common.scripts.PacketScriptSearchResults;
import mchorse.mappet.network.common.scripts.ScriptSearchResult;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.class_3222;

public class ServerHandlerRequestScriptSearch extends ServerMessageHandler<PacketRequestScriptSearch>
{
    private static final int MAX_RESULTS = 4096;

    @Override
    public void run(class_3222 player, PacketRequestScriptSearch message)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }

        String query = message.query.trim();
        List<ScriptSearchResult> results = new ArrayList<>();
        if (!query.isEmpty())
        {
            String needle = query.toLowerCase(Locale.ROOT);

            ScriptManager manager = message.clientScript ? Mappet.clientScripts : Mappet.scripts;
            if (manager == null) {
                return;
            }
            for (String id : manager.getKeys())
            {
                if (results.size() >= MAX_RESULTS)
                {
                    break;
                }

                Script script = manager.load(id);
                if (script == null || script.code == null)
                {
                    continue;
                }

                String[] lines = script.code.split("\\n", -1);
                for (int line = 0; line < lines.length && results.size() < MAX_RESULTS; line++)
                {
                    String lowerLine = lines[line].toLowerCase(Locale.ROOT);
                    int index = 0;
                    while (results.size() < MAX_RESULTS)
                    {
                        index = lowerLine.indexOf(needle, index);
                        if (index < 0)
                        {
                            break;
                        }

                        results.add(new ScriptSearchResult(id, line + 1, index + 1, query.length(), lines[line].trim()));
                        index += Math.max(needle.length(), 1);
                    }
                }
            }
        }

        results.sort((a, b) -> {
            int scripts = a.script.compareToIgnoreCase(b.script);
            if (scripts != 0)
            {
                return scripts;
            }

            int lines = Integer.compare(a.line, b.line);
            return lines != 0 ? lines : Integer.compare(a.column, b.column);
        });
        Dispatcher.sendTo(new PacketScriptSearchResults(results, message.clientScript), player);
    }
}
