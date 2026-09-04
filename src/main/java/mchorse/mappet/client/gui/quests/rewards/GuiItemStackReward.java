package mchorse.mappet.client.gui.quests.rewards;

import mchorse.mappet.api.quests.rewards.ItemStackReward;
import mchorse.mappet.client.gui.utils.GuiItemsElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiItemStackReward extends GuiReward<ItemStackReward> {
   public GuiItemsElement items;

   public GuiItemStackReward(class_310 mc, ItemStackReward reward) {
      super(mc, reward);
      this.items = new GuiItemsElement(mc, IKey.lang("mappet.gui.quests.reward_item.title"), reward.stacks);
      this.items.flex().relative(this).wh(1.0F, 1.0F);
      this.flex = this.items.flex();
      this.add(this.items);
   }
}
