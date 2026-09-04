package mchorse.mappet.api.scripts.code.entities.ai.rotations;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.class_18;
import net.minecraft.class_1937;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_3218;

public class RotationDataStorage extends class_18 {
   public static final String ROTATION_DATA_KEY = "mappet_rotation_data";
   private Map<UUID, RotationData> rotationDataMap = new HashMap();

   public static RotationDataStorage getRotationDataStorage(class_1937 world) {
      if (world instanceof class_3218 serverWorld) {
         return (RotationDataStorage)serverWorld.method_17983().method_17924(RotationDataStorage::fromNbt, RotationDataStorage::new, "mappet_rotation_data");
      } else {
         throw new IllegalArgumentException("Rotation data is server-side");
      }
   }

   public void readFromNBT(class_2487 nbt) {
      this.rotationDataMap.clear();
      class_2499 rotationDataList = nbt.method_10554("rotationDataList", 10);

      for(int i = 0; i < rotationDataList.size(); ++i) {
         class_2487 rotationDataCompound = rotationDataList.method_10602(i);
         UUID entityId = UUID.fromString(rotationDataCompound.method_10558("entityId"));
         float yaw = rotationDataCompound.method_10583("yaw");
         float pitch = rotationDataCompound.method_10583("pitch");
         float yawHead = rotationDataCompound.method_10583("yawHead");
         this.rotationDataMap.put(entityId, new RotationData(yaw, pitch, yawHead));
      }

   }

   public class_2487 method_75(class_2487 nbt) {
      class_2499 rotationDataList = new class_2499();

      for(Map.Entry<UUID, RotationData> entry : this.rotationDataMap.entrySet()) {
         class_2487 rotationDataCompound = new class_2487();
         rotationDataCompound.method_10582("entityId", ((UUID)entry.getKey()).toString());
         rotationDataCompound.method_10548("yaw", ((RotationData)entry.getValue()).yaw);
         rotationDataCompound.method_10548("pitch", ((RotationData)entry.getValue()).pitch);
         rotationDataCompound.method_10548("yawHead", ((RotationData)entry.getValue()).yawHead);
         rotationDataList.add(rotationDataCompound);
      }

      nbt.method_10566("rotationDataList", rotationDataList);
      return nbt;
   }

   private static RotationDataStorage fromNbt(class_2487 nbt) {
      RotationDataStorage storage = new RotationDataStorage();
      storage.readFromNBT(nbt);
      return storage;
   }

   public void addRotationData(UUID entityId, float yaw, float pitch, float yawHead) {
      this.rotationDataMap.put(entityId, new RotationData(yaw, pitch, yawHead));
      this.method_80();
   }

   public void removeRotationData(UUID entityId) {
      this.rotationDataMap.remove(entityId);
      this.method_80();
   }

   public RotationData getRotationData(UUID entityId) {
      return (RotationData)this.rotationDataMap.get(entityId);
   }

   public static class RotationData {
      public final float yaw;
      public final float pitch;
      public final float yawHead;

      public RotationData(float yaw, float pitch, float yawHead) {
         this.yaw = yaw;
         this.pitch = pitch;
         this.yawHead = yawHead;
      }
   }
}
