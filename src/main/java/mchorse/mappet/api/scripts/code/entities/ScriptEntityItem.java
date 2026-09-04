package mchorse.mappet.api.scripts.code.entities;

import java.util.UUID;
import mchorse.mappet.api.scripts.code.items.ScriptItemStack;
import mchorse.mappet.api.scripts.user.entities.IScriptEntityItem;
import mchorse.mappet.api.scripts.user.items.IScriptItemStack;
import mchorse.mappet.compat.EntityData;
import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_1799;
import net.minecraft.class_2487;

public class ScriptEntityItem extends ScriptEntity<class_1542> implements IScriptEntityItem {
   public ScriptEntityItem(class_1542 entity) {
      super(entity);
   }

   public int getAge() {
      return (this.entity).field_6012;
   }

   public void setAge(int age) {
      (this.entity).field_6012 = age;
   }

   public int getPickupDelay() {
      class_2487 tag = new class_2487();
      ((class_1542)this.entity).method_5647(tag);
      return tag.method_10568("PickupDelay");
   }

   public void setPickupDelay(int delay) {
      ((class_1542)this.entity).method_6982(delay);
   }

   public int getLifespan() {
      return EntityData.get(this.entity).method_10545("Lifespan") ? EntityData.get(this.entity).method_10550("Lifespan") : 6000;
   }

   public void setLifespan(int lifespan) {
      EntityData.get(this.entity).method_10569("Lifespan", lifespan);
   }

   public String getOwner() {
      class_1297 owner = ((class_1542)this.entity).method_24921();
      return owner == null ? EntityData.get(this.entity).method_10558("OwnerName") : owner.method_5477().getString();
   }

   public void setOwner(String owner) {
      String name = owner == null ? "" : owner;
      EntityData.get(this.entity).method_10582("OwnerName", name);
      if (name.isEmpty()) {
         ((class_1542)this.entity).method_48349((UUID)null);
      } else if (((class_1542)this.entity).method_5682() != null) {
         ((class_1542)this.entity).method_5682().method_3793().method_14515(name).ifPresent((profile) -> ((class_1542)this.entity).method_48349(profile.getId()));
      }

   }

   public String getThrower() {
      return EntityData.get(this.entity).method_10558("ThrowerName");
   }

   public void setThrower(String thrower) {
      String name = thrower == null ? "" : thrower;
      EntityData.get(this.entity).method_10582("ThrowerName", name);
      if (name.isEmpty()) {
         ((class_1542)this.entity).method_6981((UUID)null);
      } else if (((class_1542)this.entity).method_5682() != null) {
         ((class_1542)this.entity).method_5682().method_3793().method_14515(name).ifPresent((profile) -> ((class_1542)this.entity).method_6981(profile.getId()));
      }

   }

   public IScriptItemStack getItem() {
      return ScriptItemStack.create(((class_1542)this.entity).method_6983());
   }

   public void setItem(IScriptItemStack stack) {
      ((class_1542)this.entity).method_6979(stack == null ? class_1799.field_8037 : stack.getMinecraftItemStack());
   }

   public void setInfinitePickupDelay() {
      ((class_1542)this.entity).method_6989();
   }

   public void setDefaultPickupDelay() {
      ((class_1542)this.entity).method_6988();
   }

   public void setNoDespawn() {
      ((class_1542)this.entity).method_35190();
   }

   public boolean canPickup() {
      return !((class_1542)this.entity).method_6977();
   }
}
