package mchorse.mappet.entities.utils;

import java.util.UUID;
import mchorse.mappet.Mappet;
import mchorse.mappet.entities.EntityNpc;
import net.minecraft.class_1309;
import net.minecraft.class_1937;
import net.minecraft.class_2487;

public class DiedNpcHolder {
   public class_2487 nbt;
   public long respawnTime;
   public String uuid;
   public int worldID;
   public double posX;
   public double posY;
   public double posZ;

   public DiedNpcHolder(EntityNpc npc, long respawnTime, double posX, double posY, double posZ) {
      this.nbt = npc.method_5647(new class_2487());
      this.uuid = npc.method_5667().toString();
      this.respawnTime = respawnTime;
      this.worldID = npc.method_37908().method_27983() == class_1937.field_25180 ? -1 : (npc.method_37908().method_27983() == class_1937.field_25181 ? 1 : 0);
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
   }

   public DiedNpcHolder(class_2487 nbt, String uuid, long respawnTime, int worldID, double posX, double posY, double posZ) {
      this.nbt = nbt;
      this.uuid = uuid;
      this.respawnTime = respawnTime;
      this.worldID = worldID;
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
   }

   public void spawn(class_1937 world) {
      EntityNpc npc = new EntityNpc(Mappet.npcEntity, world);
      npc.method_5651(this.nbt);
      npc.method_5814(this.posX, this.posY, this.posZ);
      npc.method_6033(npc.method_6063());
      if ((Boolean)npc.getState().respawnSaveUUID.get()) {
         npc.method_5826(UUID.fromString(this.uuid));
      }

      world.method_8649(npc);
      npc.getState().triggerRespawn.trigger((class_1309)npc);
   }
}
