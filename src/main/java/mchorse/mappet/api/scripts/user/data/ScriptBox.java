package mchorse.mappet.api.scripts.user.data;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3d;
import mchorse.mappet.api.scripts.code.ScriptWorld;
import mchorse.mappet.api.scripts.code.blocks.ScriptBlockState;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2680;

public class ScriptBox {
   public double minX;
   public double minY;
   public double minZ;
   public double maxX;
   public double maxY;
   public double maxZ;

   public ScriptBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      this.minX = Math.min(minX, maxX);
      this.minY = Math.min(minY, maxY);
      this.minZ = Math.min(minZ, maxZ);
      this.maxX = Math.max(minX, maxX);
      this.maxY = Math.max(minY, maxY);
      this.maxZ = Math.max(minZ, maxZ);
   }

   public String toString() {
      return "ScriptBox(" + this.minX + ", " + this.minY + ", " + this.minZ + ", " + this.maxX + ", " + this.maxY + ", " + this.maxZ + ")";
   }

   public boolean isColliding(ScriptBox box) {
      return this.minX < box.maxX && this.maxX > box.minX && this.minY < box.maxY && this.maxY > box.minY && this.minZ < box.maxZ && this.maxZ > box.minZ;
   }

   public void offset(double x, double y, double z) {
      this.minX += x;
      this.minY += y;
      this.minZ += z;
      this.maxX += x;
      this.maxY += y;
      this.maxZ += z;
   }

   public boolean contains(double x, double y, double z) {
      return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ;
   }

   public boolean contains(ScriptVector vector) {
      return this.contains(vector.x, vector.y, vector.z);
   }

   public boolean contains(Vector3d vector) {
      return this.contains(vector.x, vector.y, vector.z);
   }

   public List<ScriptVector> getBlocksPositions(ScriptWorld scriptWorld, ScriptBlockState state) {
      class_1937 world = scriptWorld.getMinecraftWorld();
      class_2680 blockState = state.getMinecraftBlockState();
      List<ScriptVector> blocks = new ArrayList();

      for(double x = this.minX; x <= this.maxX; ++x) {
         for(double y = this.minY; y <= this.maxY; ++y) {
            for(double z = this.minZ; z <= this.maxZ; ++z) {
               class_2338 pos = class_2338.method_49637(x, y, z);
               if (world.method_8320(pos).equals(blockState)) {
                  blocks.add(new ScriptVector(x, y, z));
               }
            }
         }
      }

      return blocks;
   }
}
