package mchorse.mappet.client.gui;

import java.util.List;
import java.util.function.Consumer;
import mchorse.mappet.Mappet;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.npc.PacketNpcList;
import mchorse.mappet.network.common.npc.PacketNpcTool;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import net.minecraft.class_1074;
import net.minecraft.class_1799;
import net.minecraft.class_2487;
import net.minecraft.class_310;
import net.minecraft.class_332;

public class GuiNpcToolScreen extends GuiBase {
   public GuiStringListElement npcs;
   public GuiStringListElement states;

   public GuiNpcToolScreen(class_310 mc, List<String> npcs, List<String> states) {
      this.npcs = new GuiStringListElement(mc, (l) -> this.queryStates((String)l.get(0)));
      this.npcs.background().setList(npcs);
      this.npcs.sort();
      this.states = new GuiStringListElement(mc, (Consumer)null);
      this.states.background().setList(states);
      this.states.sort();
      this.npcs.flex().relative(this.viewport).x(0.5F, -10).y(0.5F).w(100).h(200).anchor(1.0F, 0.5F);
      this.states.flex().relative(this.viewport).x(0.5F, 10).y(0.5F).w(100).h(200).anchor(0.0F, 0.5F);
      this.root.add(new IGuiElement[]{this.npcs, this.states});
      class_1799 stack = mc.field_1724.method_6047();
      if (stack.method_7909() != Mappet.npcTool) {
         stack = mc.field_1724.method_6079();
      }

      class_2487 tag = stack.method_7969();
      if (tag != null && tag.method_10545("Npc") && tag.method_10545("State")) {
         this.npcs.setCurrentScroll(tag.method_10558("Npc"));
         this.states.setCurrentScroll(tag.method_10558("State"));
      }

   }

   private void queryStates(String s) {
      PacketNpcList packet = new PacketNpcList();
      packet.npcs.add(s);
      Dispatcher.sendToServer(packet);
   }

   public boolean method_25421() {
      return false;
   }

   protected void closeScreen() {
      super.closeScreen();
      String npc = this.npcs.isDeselected() ? "" : (String)this.npcs.getCurrentFirst();
      String state = this.states.isDeselected() ? "" : (String)this.states.getCurrentFirst();
      Dispatcher.sendToServer(new PacketNpcTool(npc, state));
   }

   public void method_25394(class_332 drawContext, int mouseX, int mouseY, float partialTicks) {
      this.method_25420(drawContext);
      String title = class_1074.method_4662("mappet.gui.npc_tool.title", new Object[0]);
      GuiDraw.drawTextBackground(this.field_22793, title, this.viewport.mx(this.field_22793.method_1727(title)), this.viewport.y + 20, 16777215, -2013265920);
      GuiDraw.drawStringWithShadow(this.field_22793, class_1074.method_4662("mappet.gui.npc_tool.npc", new Object[0]), this.npcs.area.x, this.npcs.area.y - 12, 16777215);
      GuiDraw.drawStringWithShadow(this.field_22793, class_1074.method_4662("mappet.gui.npc_tool.state", new Object[0]), this.states.area.x, this.states.area.y - 12, 16777215);
      super.method_25394(drawContext, mouseX, mouseY, partialTicks);
   }
}
