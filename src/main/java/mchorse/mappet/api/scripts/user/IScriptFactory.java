package mchorse.mappet.api.scripts.user;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import mchorse.mappet.api.scripts.user.blocks.IScriptBlockState;
import mchorse.mappet.api.scripts.user.data.ScriptBox;
import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import mchorse.mappet.api.scripts.user.items.IScriptItemStack;
import mchorse.mappet.api.scripts.user.logs.IMappetLogger;
import mchorse.mappet.api.scripts.user.mappet.IMappetUIBuilder;
import mchorse.mappet.api.scripts.user.nbt.INBTCompound;
import mchorse.mappet.api.scripts.user.nbt.INBTList;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.class_1291;
import net.minecraft.class_1297;
import net.minecraft.class_2396;

public interface IScriptFactory {
   IScriptBlockState createBlockState(String var1, int var2);

   IScriptBlockState createBlockState(String var1);

   default INBTCompound createCompound() {
      return this.createCompound((String)null);
   }

   INBTCompound createCompound(String var1);

   INBTCompound createCompoundFromJS(Object var1);

   default INBTList createList() {
      return this.createList((String)null);
   }

   INBTList createList(String var1);

   INBTList createListFromJS(Object var1);

   default IScriptItemStack createItemNBT(String nbt) {
      return this.createItem(this.createCompound(nbt));
   }

   IScriptItemStack createItem(INBTCompound var1);

   default IScriptItemStack createItem(String itemId) {
      return this.createItem(itemId, 1);
   }

   default IScriptItemStack createItem(String itemId, int count) {
      return this.createItem(itemId, count, 0);
   }

   IScriptItemStack createItem(String var1, int var2, int var3);

   default IScriptItemStack createBlockItem(String blockId) {
      return this.createItem(blockId, 1);
   }

   default IScriptItemStack createBlockItem(String blockId, int count) {
      return this.createItem(blockId, count, 0);
   }

   IScriptItemStack createBlockItem(String var1, int var2, int var3);

   class_2396 getParticleType(String var1);

   class_1291 getPotion(String var1);

   default AbstractMorph createMorph(String nbt) {
      return this.createMorph(this.createCompound(nbt));
   }

   AbstractMorph createMorph(INBTCompound var1);

   default IMappetUIBuilder createUI() {
      return this.createUI("", "");
   }

   default IMappetUIBuilder createUI(IScriptEvent event, String function) {
      return this.createUI(event.getScript(), function);
   }

   IMappetUIBuilder createUI(String var1, String var2);

   IMappetUIBuilder createUIFromFile(String var1);

   IMappetUIBuilder createUIFromFile(String var1, String var2, String var3);

   Object get(String var1);

   void set(String var1, Object var2);

   default String dump(Object object) {
      return this.dump(object, true);
   }

   String dump(Object var1, boolean var2);

   double random(double var1);

   double random(double var1, double var3);

   double random(double var1, double var3, long var5);

   String style(String... var1);

   IMappetLogger getLogger();

   IScriptEntity getMappetEntity(class_1297 var1);

   default ScriptVector vector(double x, double y, double z) {
      return new ScriptVector(x, y, z);
   }

   default Vector2d vector2() {
      return new Vector2d();
   }

   default Vector2d vector2(double x, double y) {
      return new Vector2d(x, y);
   }

   default Vector2d vector2(Vector2d v) {
      return new Vector2d(v);
   }

   default Vector3d vector3() {
      return new Vector3d();
   }

   default Vector3d vector3(double x, double y, double z) {
      return new Vector3d(x, y, z);
   }

   default Vector3d vector3(Vector3d v) {
      return new Vector3d(v);
   }

   default Vector4d vector4() {
      return new Vector4d();
   }

   default Vector4d vector4(double x, double y, double z, double w) {
      return new Vector4d(x, y, z, w);
   }

   default Vector4d vector4(Vector4d v) {
      return new Vector4d(v);
   }

   default Matrix3d matrix3() {
      Matrix3d m = new Matrix3d();
      m.setIdentity();
      return m;
   }

   default Matrix3d matrix3(Matrix3d m) {
      return new Matrix3d(m);
   }

   default Matrix4d matrix4() {
      Matrix4d m = new Matrix4d();
      m.setIdentity();
      return m;
   }

   default Matrix4d matrix4(Matrix4d m) {
      return new Matrix4d(m);
   }

   default ScriptBox box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      return new ScriptBox(minX, minY, minZ, maxX, maxY, maxZ);
   }

   boolean isPointInBounds(Object var1, Object var2, Object var3);

   INBTCompound toNBT(Object var1);

   String format(String var1, Object... var2);
}
