package mchorse.mappet.entities.utils;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.entities.EntityNpc;
import net.minecraft.class_18;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2520;
import net.minecraft.class_3218;

public class MappetNpcRespawnManager extends class_18 {
   public static final String DATA_NAME = "mappet_RespawnData";
   public final List<DiedNpcHolder> diedNpcHolders = new ArrayList();
   private class_1937 world;

   public void onTick() {
      List<DiedNpcHolder> ready = new ArrayList();

      for(DiedNpcHolder holder : this.diedNpcHolders) {
         class_2338 pos = class_2338.method_49637(holder.posX, holder.posY, holder.posZ);
         if (holder.respawnTime <= this.world.method_8510() && this.world.method_22340(pos)) {
            holder.spawn(this.world);
            ready.add(holder);
         }
      }

      if (!ready.isEmpty()) {
         this.diedNpcHolders.removeAll(ready);
         this.method_80();
      }

   }

   public void addDiedNpc(EntityNpc npc) {
      long time = npc.method_37908().method_8510() + (long)(Integer)npc.getState().respawnDelay.get();
      double x = (Boolean)npc.getState().respawnOnCoordinates.get() ? (Double)npc.getState().respawnPosX.get() : npc.method_23317();
      double y = (Boolean)npc.getState().respawnOnCoordinates.get() ? (Double)npc.getState().respawnPosY.get() : npc.method_23318();
      double z = (Boolean)npc.getState().respawnOnCoordinates.get() ? (Double)npc.getState().respawnPosZ.get() : npc.method_23321();
      this.diedNpcHolders.add(new DiedNpcHolder(npc, time, x, y, z));
      this.method_80();
   }

   public static MappetNpcRespawnManager fromNbt(class_2487 root) {
      MappetNpcRespawnManager data = new MappetNpcRespawnManager();

      for(class_2520 element : root.method_10554("DiedNPCHolders", 10)) {
         class_2487 tag = (class_2487)element;
         data.diedNpcHolders.add(new DiedNpcHolder(tag.method_10562("NBT"), tag.method_10558("UUID"), tag.method_10537("RespawnTime"), tag.method_10550("WorldID"), tag.method_10574("PosX"), tag.method_10574("PosY"), tag.method_10574("PosZ")));
      }

      return data;
   }

   public class_2487 method_75(class_2487 root) {
      class_2499 list = new class_2499();

      for(DiedNpcHolder holder : this.diedNpcHolders) {
         class_2487 tag = new class_2487();
         tag.method_10566("NBT", holder.nbt.method_10553());
         tag.method_10582("UUID", holder.uuid);
         tag.method_10544("RespawnTime", holder.respawnTime);
         tag.method_10569("WorldID", holder.worldID);
         tag.method_10549("PosX", holder.posX);
         tag.method_10549("PosY", holder.posY);
         tag.method_10549("PosZ", holder.posZ);
         list.add(tag);
      }

      root.method_10566("DiedNPCHolders", list);
      return root;
   }

   public static MappetNpcRespawnManager get(class_1937 world) {
      MappetNpcRespawnManager data = (MappetNpcRespawnManager)((class_3218)world).method_17983().method_17924(MappetNpcRespawnManager::fromNbt, MappetNpcRespawnManager::new, "mappet_RespawnData");
      data.world = world;
      return data;
   }
}
