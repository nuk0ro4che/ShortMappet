package mchorse.mappet.api.scripts.user.entities;

import java.util.Set;
import mchorse.mappet.api.scripts.user.client.ICameraShake;
import mchorse.mappet.api.scripts.user.client.IGameSettings;
import mchorse.mappet.api.scripts.user.client.IHudElement;
import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.api.scripts.user.items.IScriptInventory;
import mchorse.mappet.api.scripts.user.items.IScriptItemStack;
import mchorse.mappet.api.scripts.user.mappet.IMappetQuests;
import mchorse.mappet.api.scripts.user.mappet.IMappetUIBuilder;
import mchorse.mappet.api.scripts.user.mappet.IMappetUIContext;
import mchorse.mappet.api.scripts.user.nbt.INBT;
import mchorse.mappet.api.scripts.user.nbt.INBTCompound;
import mchorse.mappet.api.scripts.user.render.IScriptHand;
import mchorse.mappet.api.scripts.user.sounds.IScriptManagedSound;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.class_3222;

public interface IScriptPlayer extends IScriptEntity {
   class_3222 getMinecraftPlayer();

   IScriptHand getHand(int side);

   
   boolean executeClientScript(String script);

   
   boolean executeClientScript(String script, String function);

   
boolean executeClientScript(String script, String function, Object... args);

   
    boolean executeClientScript(Object code);

   
    boolean executeClientScript(Object code, Object... args);

   
    void disableJump(boolean disabled);

   
   void disableSprint(boolean disabled);


   void playAnimation(String animation);

   void setClipboard(String text);

   
   void setMousePosition(double x, double y);

   
   IScriptPlayer moveMouseTo(String interpolation, int durationTicks, double x, double y);

   
   IScriptPlayer moveMouseBy(String interpolation, int durationTicks, double dx, double dy);

   String getClipboard();

   IGameSettings getSettings();

   ICameraShake getCameraShake();

   IHudElement getAllHud(String element);

   



   boolean applyShader(String id);

   



   boolean applyShader(String id, boolean ui, boolean hud);

   
   void removeShader();

   
   void removeShader(boolean ui, boolean hud);

   



   boolean applyUIShader(String id);

   
   void removeUIShader();

   



   boolean applyHUDShader(String id);

   
   void removeHUDShader();

   void openWeb(String url);

   void disconnect(String reason);

   void quitMinecraft();

   void quitWorld();

   void openSettings();

   int getGameMode();

   void setGameMode(int gameMode);

   void setLay(boolean lay);

   boolean getLay();

   IScriptInventory getInventory();

   IScriptInventory getEnderChest();

   void executeCommand(String command);

   void setSpawnPoint(double x, double y, double z);

   ScriptVector getSpawnPoint();

   boolean isFlying();

   float getWalkSpeed();


   void setFlyingEnabled(boolean var1);

   float getFlySpeed();

   void setWalkSpeed(float var1);


   void setFlySpeed(float var1);

   void resetFlySpeed();

   void resetWalkSpeed();

   float getCooldown(int var1);

   float getCooldown(IScriptItemStack var1);

   void setCooldown(int var1, int var2);

   void setCooldown(IScriptItemStack var1, int var2);

   void resetCooldown(int var1);

   void resetCooldown(IScriptItemStack var1);

   int getHotbarIndex();

   void setHotbarIndex(int var1);

   void send(String var1);

   void sendRaw(INBT var1);

   String getSkin(String var1);

   String getType();

   ILocalMorph createLocalMorph(String var1, mchorse.metamorph.api.morphs.AbstractMorph var2, double var3, double var5, double var7);
   void removeLocalMorph(String var1);

   void sendTitleDurations(int var1, int var2, int var3);

   void sendTitle(String var1);

   void sendSubtitle(String var1);

   void sendActionBar(String var1);

   void setXp(int var1, int var2);

   void addXp(int var1);

   int getXpLevel();

   int getXpPoints();

   void setHunger(int var1);

   int getHunger();

   void setSaturation(float var1);

   float getSaturation();

   default void playSound(String event, double x, double y, double z) {
      this.playSound(event, x, y, z, 1.0F, 1.0F);
   }

   void playSound(String var1, String var2, double var3, double var5, double var7);

   void playSound(String var1, String var2, double var3, double var5, double var7, float var9, float var10);

   void playSound(String var1, double var2, double var4, double var6, float var8, float var9);

   default void stopAllSounds() {
      this.stopSound("", "");
   }

   default void stopSound(String event) {
      this.stopSound(event, "");
   }

   void stopSound(String var1, String var2);

   
   IScriptManagedSound playManagedSound(String id, String event, String category, double x, double y, double z, float volume, float pitch);
   
   IScriptManagedSound playManagedSound(String id, String event, String category, IScriptEntity entity, float volume, float pitch);

   
   IScriptManagedSound playManagedStaticSound(String id, String event, String category, float volume, float pitch);

   
   IScriptManagedSound getManagedSound(String id);

   void playStaticSound(String var1, float var2, float var3);

   void playStaticSound(String var1, String var2, float var3, float var4);

   default void playLoopSound(String event) {
      this.playLoopSound(event, "master", 1.0F, 1.0F);
   }

   default void playLoopSound(String event, float volume, float pitch) {
      this.playLoopSound(event, "master", volume, pitch);
   }

   void playLoopSound(String event, String category, float volume, float pitch);

   default void stopLoopSound() {
      this.stopLoopSound("", "");
   }

   default void stopLoopSound(String event) {
      this.stopLoopSound(event, "");
   }

   void stopLoopSound(String event, String category);

   IMappetQuests getQuests();

   default void openUI(IMappetUIBuilder builder) {
      this.openUI(builder, false);
   }

   boolean openUI(IMappetUIBuilder var1, boolean var2);

   default boolean openUI(String id) {
      return this.openUI(id, true);
   }

   boolean openUI(String id, boolean defaultData);

   




   IScriptPlayer closeUI();

   
   IScriptPlayer closeMappetUI();

   IMappetUIContext getUIContext();

   Set<String> getFactions();

   boolean setupHUD(String var1);

   void changeHUDMorph(String var1, int var2, AbstractMorph var3);

   void changeHUDMorph(String var1, int var2, INBTCompound var3);

   default void closeAllHUD() {
      this.closeHUD((String)null);
   }

   
   boolean setHUDWorldLighting(String id, boolean enabled);
   
   boolean setHUDWorldLighting(String id, boolean enabled, float intensity);

   void closeHUD(String var1);

   INBTCompound getDisplayedHUDs();

   INBTCompound getGlobalDisplayedHUDs();

   void playScene(String var1);

   void stopScene();
}