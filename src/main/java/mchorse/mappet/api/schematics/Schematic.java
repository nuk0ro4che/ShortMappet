package mchorse.mappet.api.schematics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import mchorse.mappet.api.utils.AbstractData;
import mchorse.mappet.compat.NbtCompat;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_7923;

public class Schematic extends AbstractData {
   private class_2680[][][] blockStates;
   private byte[][][] metadata;
   private final List<class_2586> tileEntities;
   private final List<class_1297> entities;
   private int width;
   private int height;
   private int length;

   public Schematic() {
      this(1, 1, 1);
   }

   public Schematic(int width, int height, int length) {
      this.tileEntities = new ArrayList();
      this.entities = new ArrayList();
      this.width = width;
      this.height = height;
      this.length = length;
      this.init();
   }

   public void init() {
      this.blockStates = new class_2680[this.width][this.height][this.length];
      this.tileEntities.clear();
      this.entities.clear();
   }

   public boolean setBlockState(class_2338 pos, class_2680 blockState) {
      if (this.isInvalid(pos)) {
         return false;
      } else {
         int x = pos.method_10263();
         int y = pos.method_10264();
         int z = pos.method_10260();
         this.blockStates[x][y][z] = blockState;
         return true;
      }
   }

   public class_2680 getBlockState(class_2338 pos) {
      if (this.isInvalid(pos)) {
         return class_2246.field_10124.method_9564();
      } else {
         int x = pos.method_10263();
         int y = pos.method_10264();
         int z = pos.method_10260();
         return this.blockStates[x][y][z];
      }
   }

   public void setBlockEntity(class_2338 pos, class_2586 tileEntity) {
      if (!this.isInvalid(pos)) {
         this.removeBlockEntity(pos);
         if (tileEntity != null) {
            this.tileEntities.add(tileEntity);
         }

      }
   }

   public class_2586 getBlockEntity(class_2338 pos) {
      for(class_2586 tileEntity : this.tileEntities) {
         if (tileEntity.method_11016().equals(pos)) {
            return tileEntity;
         }
      }

      return null;
   }

   public void removeBlockEntity(class_2338 pos) {
      this.tileEntities.removeIf((tileEntity) -> tileEntity.method_11016().equals(pos));
   }

   public List<class_2586> getTileEntities() {
      return this.tileEntities;
   }

   private boolean isInvalid(class_2338 pos) {
      int x = pos.method_10263();
      int y = pos.method_10264();
      int z = pos.method_10260();
      return x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.height || z >= this.length;
   }

   public void loadFromWorld(class_1937 world, int x1, int y1, int z1, int x2, int y2, int z2) {
      int minX = Math.min(x1, x2);
      int minY = Math.min(y1, y2);
      int minZ = Math.min(z1, z2);
      int maxX = Math.max(x1, x2);
      int maxY = Math.max(y1, y2);
      int maxZ = Math.max(z1, z2);
      this.width = maxX - minX + 1;
      this.height = maxY - minY + 1;
      this.length = maxZ - minZ + 1;
      this.init();
      this.copyBlocks(world, minX, minY, minZ);
      this.copyEntities(world, minX, minY, minZ, maxX, maxY, maxZ);
   }

   public void place(class_1937 world, int x, int y, int z, boolean replaceBlocks, boolean placeAir) {
      for(int localX = 0; localX < this.width; ++localX) {
         for(int localY = 0; localY < this.height; ++localY) {
            for(int localZ = 0; localZ < this.length; ++localZ) {
               class_2338 blockPos = new class_2338(x + localX, y + localY, z + localZ);
               class_2338 localPos = new class_2338(localX, localY, localZ);
               class_2680 blockState = this.getBlockState(localPos);
               if ((replaceBlocks || world.method_22347(blockPos)) && (placeAir || !blockState.equals(class_2246.field_10124.method_9564()))) {
                  world.method_8501(blockPos, blockState);
                  class_2586 tileEntity = world.method_8321(blockPos);
                  if (tileEntity != null) {
                     class_2586 source = this.getBlockEntity(localPos);
                     if (source != null) {
                        class_2487 data = source.method_38243();
                        data.method_10569("x", blockPos.method_10263());
                        data.method_10569("y", blockPos.method_10264());
                        data.method_10569("z", blockPos.method_10260());
                        tileEntity.method_11014(data);
                        tileEntity.method_5431();
                     }
                  }
               }
            }
         }
      }

      for(class_1297 entity : this.entities) {
         entity.method_5826(UUID.randomUUID());
         world.method_8649(entity);
      }

   }

   private void copyBlocks(class_1937 world, int minX, int minY, int minZ) {
      this.init();

      for(int localX = 0; localX <= this.width; ++localX) {
         for(int localY = 0; localY <= this.height; ++localY) {
            for(int localZ = 0; localZ <= this.length; ++localZ) {
               class_2338 blockPos = new class_2338(minX + localX, minY + localY, minZ + localZ);
               class_2338 localPos = new class_2338(localX, localY, localZ);
               class_2680 blockState = world.method_8320(blockPos);
               class_2248 block = blockState.method_26204();
               boolean success = this.setBlockState(localPos, blockState);
               if (success && blockState.method_31709()) {
                  this.copyBlockEntity(world, blockPos, localPos);
               }
            }
         }
      }

   }

   private void copyEntities(class_1937 world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      class_238 axisAlignedBB = new class_238((double)minX, (double)minY, (double)minZ, (double)maxX, (double)maxY, (double)maxZ);
      List<class_1297> entityList = (List)world.method_8335((class_1297)null, axisAlignedBB).stream().filter((entityx) -> !(entityx instanceof class_1657)).collect(Collectors.toList());
      this.entities.clear();

      for(class_1297 entity : entityList) {
         this.addEntity(entity);
      }

   }

   private void copyBlockEntity(class_1937 world, class_2338 blockPos, class_2338 localPos) {
      class_2586 tileEntity = world.method_8321(blockPos);
      if (tileEntity != null) {
         try {
            this.setBlockEntity(localPos, tileEntity);
         } catch (Exception var6) {
            this.setBlockState(localPos, class_2246.field_9987.method_9564());
         }

      }
   }

   public void addEntity(class_1297 entity) {
      if (entity != null && !(entity instanceof class_1657)) {
         for(class_1297 e : this.entities) {
            if (entity.method_5667().equals(e.method_5667())) {
               return;
            }
         }

         this.entities.add(entity);
      }
   }

   public void removeEntity(class_1297 entity) {
      if (entity != null) {
         this.entities.removeIf((e) -> entity.method_5667().equals(e.method_5667()));
      }
   }

   public List<class_1297> getEntities() {
      return this.entities;
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10575("Width", (short)this.width);
      tag.method_10575("Height", (short)this.height);
      tag.method_10575("Length", (short)this.length);
      int size = this.width * this.length * this.height;
      byte[] localBlocks = new byte[size];
      byte[] localMetadata = new byte[size];
      byte[] extraBlocks = new byte[size];
      byte[] extraBlocksNibble = new byte[(int)Math.ceil((double)size / (double)2.0F)];
      boolean extra = false;
      Map<String, Short> mappings = new HashMap();

      for(int x = 0; x < this.width; ++x) {
         for(int y = 0; y < this.height; ++y) {
            for(int z = 0; z < this.length; ++z) {
               int index = x + (y * this.length + z) * this.width;
               class_2680 blockState = this.getBlockState(new class_2338(x, y, z));
               class_2248 block = blockState.method_26204();
               int blockId = class_7923.field_41175.method_10206(block);
               localBlocks[index] = (byte)blockId;
               localMetadata[index] = (byte)block.method_9595().method_11662().indexOf(blockState);
               if ((extraBlocks[index] = (byte)(blockId >> 8)) > 0) {
                  extra = true;
               }

               String name = String.valueOf(class_7923.field_41175.method_10221(block));
               if (!mappings.containsKey(name)) {
                  mappings.put(name, (short)blockId);
               }
            }
         }
      }

      class_2499 tileEntitiesList = new class_2499();

      for(class_2586 tileEntity : this.getTileEntities()) {
         try {
            class_2487 tileEntityTagCompound = NbtCompat.write(tileEntity);
            tileEntitiesList.add(tileEntityTagCompound);
         } catch (Exception var17) {
            class_2338 tePos = tileEntity.method_11016();
            int index = tePos.method_10263() + (tePos.method_10264() * this.length + tePos.method_10260()) * this.width;
            localBlocks[index] = (byte)class_7923.field_41175.method_10206(class_2246.field_9987);
            localMetadata[index] = 0;
            extraBlocks[index] = 0;
         }
      }

      for(int i = 0; i < extraBlocksNibble.length; ++i) {
         if (i * 2 + 1 < extraBlocks.length) {
            extraBlocksNibble[i] = (byte)(extraBlocks[i * 2] << 4 | extraBlocks[i * 2 + 1]);
         } else {
            extraBlocksNibble[i] = (byte)(extraBlocks[i * 2] << 4);
         }
      }

      class_2499 entityList = new class_2499();

      for(class_1297 entity : this.getEntities()) {
         class_2487 entityCompound = NbtCompat.write(entity);
         entityList.add(entityCompound);
      }

      tag.method_10582("Materials", "Alpha");
      tag.method_10570("Blocks", localBlocks);
      tag.method_10570("Data", localMetadata);
      if (extra) {
         tag.method_10570("AddBlocks", extraBlocksNibble);
      }

      tag.method_10566("Entities", entityList);
      tag.method_10566("TileEntities", tileEntitiesList);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      short width = tag.method_10568("Width");
      short length = tag.method_10568("Length");
      short height = tag.method_10568("Height");
      this.width = width;
      this.length = length;
      this.height = height;
      this.init();
      byte[] blocks = tag.method_10547("Blocks");
      byte[] metadata = tag.method_10547("Data");
      boolean extra = false;
      byte[] extraBlocks = null;
      if (tag.method_10545("AddBlocks")) {
         extra = true;
         byte[] extraBlocksNibble = tag.method_10547("AddBlocks");
         extraBlocks = new byte[extraBlocksNibble.length * 2];

         for(int i = 0; i < extraBlocksNibble.length; ++i) {
            extraBlocks[i * 2] = (byte)(extraBlocksNibble[i] >> 4 & 15);
            extraBlocks[i * 2 + 1] = (byte)(extraBlocksNibble[i] & 15);
         }
      }

      for(int x = 0; x < width; ++x) {
         for(int y = 0; y < height; ++y) {
            for(int z = 0; z < length; ++z) {
               int index = x + (y * length + z) * width;
               int blockID = blocks[index] & 255 | (extra ? (extraBlocks[index] & 255) << 8 : 0);
               int meta = metadata[index] & 255;
               class_2248 block = (class_2248)class_7923.field_41175.method_10200(blockID);
               class_2338 blockPos = new class_2338(x, y, z);
               List<class_2680> states = block.method_9595().method_11662();
               class_2680 blockState = meta < states.size() ? (class_2680)states.get(meta) : block.method_9564();
               this.setBlockState(blockPos, blockState);
            }
         }
      }

      class_2499 tileEntities = tag.method_10554("TileEntities", 10);

      for(int i = 0; i < tileEntities.size(); ++i) {
         class_2487 data = tileEntities.method_10602(i);
         class_2338 pos = new class_2338(data.method_10550("x"), data.method_10550("y"), data.method_10550("z"));
         class_2586 tileEntity = class_2586.method_11005(pos, this.getBlockState(pos), data);
         if (tileEntity != null) {
            this.setBlockEntity(tileEntity.method_11016(), tileEntity);
         }
      }

   }
}
