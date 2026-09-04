package mchorse.mappet.client.gui.panels;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import mchorse.mappet.api.utils.logs.LoggerLevel;
import mchorse.mappet.api.utils.logs.MappetLogger;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.utils.GuiLoggingLevelList;
import mchorse.mappet.client.gui.utils.GuiScrollLogsElement;
import mchorse.mappet.client.gui.utils.GuiTextLabeledElement;
import mchorse.mappet.client.gui.utils.text.GuiText;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.logs.PacketRequestLogs;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.mclib.GuiDashboardPanel;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiLogPanel extends GuiDashboardPanel<GuiMappetDashboard> {
   String lastLogTime;
   List<String> logLines;
   String search;
   GuiScrollLogsElement text;
   GuiLoggingLevelList levelFlags;
   GuiTextLabeledElement searchBar;
   boolean searchIgnoreCase;
   boolean searchRegex;
   boolean searchOnlyMessage;
   GuiToggleElement toggleIgnoreCase;
   GuiToggleElement toggleRegex;
   GuiToggleElement toggleOnlyMessage;
   private static final Pattern replacePattern = Pattern.compile("\\r(?=[^\\[\\n])");

   public GuiLogPanel(class_310 mc, GuiMappetDashboard dashboard) {
      super(mc, dashboard);
      this.lastLogTime = LocalDateTime.of(1, 1, 1, 0, 0, 0).format(MappetLogger.dtf);
      this.logLines = new ArrayList();
      this.search = "";
      this.searchIgnoreCase = false;
      this.searchRegex = false;
      this.searchOnlyMessage = false;
      this.context(() -> {
         GuiSimpleContextMenu menu = new GuiSimpleContextMenu(mc);
         menu.action(Icons.REFRESH, IKey.lang("mappet.gui.logs.context.update"), this::sendRequestPacket);
         return menu.shadow();
      });
      this.text = new GuiScrollLogsElement(mc);
      this.text.background().flex().relative(this).xy(10, 10).w(0.75F, -10).h(1.0F, -50).column(5).vertical().stretch().scroll().padding(10);
      this.searchBar = (new GuiTextLabeledElement(mc, (s) -> {
         this.search = s;
         this.createTextElements();
      })).label(IKey.lang("mappet.gui.search"));
      this.searchBar.flex().relative(this).anchorY(1.0F).w(0.75F, -10).x(10).h(20).y(1.0F, -10);
      this.searchBar.field.setMaxStringLength(Integer.MAX_VALUE);
      this.toggleIgnoreCase = new GuiToggleElement(mc, IKey.lang("mappet.gui.logs.toggle.ignore_case"), (b) -> {
         this.searchIgnoreCase = b.isToggled();
         this.createTextElements();
      });
      this.toggleIgnoreCase.flex().relative(this).anchorY(1.0F).anchorX(1.0F).x(1.0F, -20).y(1.0F, -10).w(0.2F, -20).h(20);
      this.toggleRegex = new GuiToggleElement(mc, IKey.lang("mappet.gui.logs.toggle.regex"), (b) -> {
         this.searchRegex = b.isToggled();
         this.toggleIgnoreCase.setEnabled(!b.isToggled());
         this.createTextElements();
      });
      this.toggleRegex.flex().relative(this.toggleIgnoreCase).anchorY(1.0F).anchorX(1.0F).wh(1.0F, 1.0F).y(-1.0F, 10).x(1.0F);
      this.toggleOnlyMessage = new GuiToggleElement(mc, IKey.lang("mappet.gui.logs.toggle.onlyMessage"), (b) -> {
         this.searchOnlyMessage = b.isToggled();
         this.createTextElements();
      });
      this.toggleOnlyMessage.flex().relative(this.toggleRegex).anchorY(1.0F).anchorX(1.0F).wh(1.0F, 1.0F).y(-1.0F, 10).x(1.0F);
      this.levelFlags = new GuiLoggingLevelList(mc, (l) -> this.createTextElements());
      this.levelFlags.background().flex().relative(this).anchorX(1.0F).x(1.0F, -10).y(10).w(0.2F).h(0.5F, -20).column(10).vertical().stretch().scroll().padding(10);
      this.levelFlags.resize();
      this.add(this.text);
      this.add(this.levelFlags);
      this.add(this.searchBar);
      this.add(this.toggleIgnoreCase);
      this.add(this.toggleRegex);
      this.add(this.toggleOnlyMessage);
      this.resize();
   }

   public void appear() {
      super.appear();
      this.sendRequestPacket();
   }

   public void update(String data) {
      this.fillList(data);
      this.updateLastTime();
      this.createTextElements();
   }

   public void createTextElements() {
      this.text.removeAll();

      for(String line : this.logLines) {
         LoggerLevel level = this.getLineLevel(line);
         if ((Boolean)this.levelFlags.flags.get(level) && this.isMatchesSearch(line)) {
            this.text.add((new GuiText(this.mc)).text(line).color(level.color, false).context(() -> {
               GuiSimpleContextMenu menu = new GuiSimpleContextMenu(this.mc);
               menu.action(Icons.COPY, IKey.lang("mappet.gui.logs.context.copy"), () -> {
                  int secondCloseBracket = line.indexOf("]", line.indexOf("]") + 1);
                  GuiUtils.setClipboardString(line.substring(secondCloseBracket + 2));
               });
               return menu.shadow();
            }));
         }
      }

      this.text.resize();
   }

   public void fillList(String data) {
      data = replacePattern.matcher(data).replaceAll("\n");
      String[] lines = data.split("\r");

      for(String line : lines) {
         if (!line.equals("")) {
            this.logLines.add(line);
         }
      }

   }

   public void updateLastTime() {
      String last = (String)this.logLines.get(this.logLines.size() - 1);
      this.lastLogTime = last.substring(1, last.indexOf("]"));
   }

   public boolean isMatchesSearch(String line) {
      if (this.searchOnlyMessage) {
         int secondIndex = line.indexOf(93, line.indexOf(93) + 1);
         line = line.substring(secondIndex + 1);
      }

      if (this.searchRegex) {
         try {
            Pattern searchPattern = Pattern.compile(this.search);
            return searchPattern.matcher(line).find();
         } catch (Exception var3) {
            return false;
         }
      } else {
         return this.searchIgnoreCase ? line.toLowerCase().contains(this.search.toLowerCase()) : line.contains(this.search);
      }
   }

   public LoggerLevel getLineLevel(String line) {
      int secondOpenBracket = line.indexOf(91, line.indexOf(91) + 1);
      int secondClosedBracket = line.indexOf(93, secondOpenBracket);
      LoggerLevel level = null;
      if (secondClosedBracket != -1) {
         level = LoggerLevel.valueOf(line.substring(secondOpenBracket + 1, secondClosedBracket));
      }

      return level;
   }

   public void sendRequestPacket() {
      Dispatcher.sendToServer((new PacketRequestLogs()).setLastDate(this.lastLogTime));
   }
}
