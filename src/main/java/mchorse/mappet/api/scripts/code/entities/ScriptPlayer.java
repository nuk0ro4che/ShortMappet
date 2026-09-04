package mchorse.mappet.api.scripts.code.entities;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;

import java.util.Locale;
import java.util.Set;
import mchorse.aperture.network.common.PacketCameraState;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.shaders.ShaderFile;
import mchorse.mappet.api.scripts.code.client.ClientClipboardCache;
import mchorse.mappet.client.ClientMovementLockState;
import mchorse.mappet.network.client.scripts.ClientHandlerSound;
import mchorse.mappet.network.client.scripts.ClientHandlerMousePosition;
import mchorse.mappet.network.client.scripts.ClientHandlerClipboard;
import mchorse.mappet.api.scripts.client.ClientScriptExecutor;
import mchorse.mappet.api.scripts.code.client.ScriptCameraShake;
import mchorse.mappet.api.scripts.code.client.ScriptGameSettings;
import mchorse.mappet.api.scripts.code.sounds.ManagedSoundRegistry;
import mchorse.mappet.api.scripts.code.sounds.ScriptManagedSound;
import mchorse.mappet.api.scripts.code.sounds.ScriptClientManagedSound;
import mchorse.mappet.client.sounds.ClientManagedSoundManager;
import mchorse.mappet.api.scripts.code.client.ScriptAllHud;
import mchorse.mappet.api.scripts.code.items.ScriptInventory;
import mchorse.mappet.api.scripts.code.render.ScriptHand;
import mchorse.mappet.api.scripts.code.mappet.MappetQuests;
import mchorse.mappet.api.scripts.code.mappet.MappetUIBuilder;
import mchorse.mappet.api.scripts.code.mappet.MappetUIContext;
import mchorse.mappet.api.scripts.code.nbt.ScriptNBTCompound;
import mchorse.mappet.api.scripts.user.client.ICameraShake;
import mchorse.mappet.api.scripts.user.client.IGameSettings;
import mchorse.mappet.api.scripts.user.client.IHudElement;
import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.api.scripts.user.entities.ILocalMorph;
import mchorse.mappet.api.scripts.user.entities.IScriptPlayer;
import mchorse.mappet.api.scripts.user.items.IScriptInventory;
import mchorse.mappet.api.scripts.user.items.IScriptItemStack;
import mchorse.mappet.api.scripts.user.mappet.IMappetQuests;
import mchorse.mappet.api.scripts.user.mappet.IMappetUIBuilder;
import mchorse.mappet.api.scripts.user.mappet.IMappetUIContext;
import mchorse.mappet.api.scripts.user.nbt.INBT;
import mchorse.mappet.api.scripts.user.nbt.INBTCompound;
import mchorse.mappet.api.scripts.user.render.IScriptHand;
import mchorse.mappet.api.scripts.user.sounds.IScriptManagedSound;
import mchorse.mappet.api.ui.UI;
import mchorse.mappet.api.ui.UIFile;
import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.compat.EntityDataHolder;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketClipboard;
import mchorse.mappet.network.common.scripts.PacketMovementLock;
import mchorse.mappet.network.common.scripts.PacketEntityRotations;
import mchorse.mappet.network.common.scripts.PacketOpenWeb;
import mchorse.mappet.network.common.scripts.PacketPlayModelAnimation;
import mchorse.mappet.network.common.scripts.PacketSound;
import mchorse.mappet.network.common.scripts.PacketShader;
import mchorse.mappet.network.common.scripts.PacketManagedSound;
import mchorse.mappet.network.common.scripts.PacketWorldMorph;
import mchorse.mappet.client.morphs.WorldMorph;
import mchorse.mappet.network.common.ui.PacketCloseUI;
import mchorse.mappet.network.common.ui.PacketUI;
import mchorse.mappet.utils.WorldUtils;
import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1657;
import net.minecraft.class_1934;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2522;
import net.minecraft.class_2527;
import net.minecraft.class_2561;
import net.minecraft.class_2616;
import net.minecraft.class_2735;
import net.minecraft.class_2743;
import net.minecraft.class_2770;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3222;
import net.minecraft.class_746;
import net.minecraft.class_1324;
import net.minecraft.class_5134;
import net.minecraft.class_3419;
import net.minecraft.class_329;
import net.minecraft.class_5894;
import net.minecraft.class_5903;
import net.minecraft.class_5904;
import net.minecraft.class_5905;
import net.minecraft.class_2561.class_2562;

public class ScriptPlayer extends ScriptEntity<class_1657> implements IScriptPlayer {
   private IMappetQuests quests;
   private IScriptInventory inventory;
   private IScriptInventory enderChest;

   public ScriptPlayer(class_1657 entity) {
      super(entity);
   }

   

   public class_3222 getMinecraftPlayer() {
      return this.entity instanceof class_3222 ? (class_3222)this.entity : null;
   }

   public IScriptHand getHand(int side) {
      return new ScriptHand(this.entity, side);
   }

   public boolean executeClientScript(String script) {
      return this.executeClientScript(script, "main");
   }

   public boolean executeClientScript(String script, String function) {
      return this.entity instanceof class_3222 && ClientScriptExecutor.execute((class_3222)this.entity, script, function);
   }

   public void disableJump(boolean disabled) {
      if (this.entity instanceof class_3222) {
         Dispatcher.sendTo(new PacketMovementLock(PacketMovementLock.JUMP, disabled), (class_3222)this.entity);
      } else if (this.entity instanceof class_746) {
         ClientMovementLockState.setJumpDisabled(disabled);
      }
   }

   public void disableSprint(boolean disabled) {
      if (this.entity instanceof class_3222) {
         Dispatcher.sendTo(new PacketMovementLock(PacketMovementLock.SPRINT, disabled), (class_3222)this.entity);
      } else if (this.entity instanceof class_746) {
         ClientMovementLockState.setSprintDisabled(disabled);
      }
   }

   public IHudElement getAllHud(String element) {
      return new ScriptAllHud(this.entity).get(element);
   }

   
   public boolean applyShader(String id) {
      return this.applyShader(id, false, false);
   }

   



   public boolean applyShader(String id, boolean ui, boolean hud) {
      ShaderFile shader = this.getShader(id);
      if (shader == null) {
         return false;
      }

      Dispatcher.sendTo(PacketShader.apply(0, shader), this.getMinecraftPlayer());
      if (ui) {
         Dispatcher.sendTo(PacketShader.apply(1, shader), this.getMinecraftPlayer());
      }
      if (hud) {
         Dispatcher.sendTo(PacketShader.apply(2, shader), this.getMinecraftPlayer());
      }
      return true;
   }

   
   public void removeShader() {
      this.removeShader(false, false);
   }

   
   public void removeShader(boolean ui, boolean hud) {
      Dispatcher.sendTo(PacketShader.remove(0), this.getMinecraftPlayer());
      if (ui) {
         Dispatcher.sendTo(PacketShader.remove(1), this.getMinecraftPlayer());
      }
      if (hud) {
         Dispatcher.sendTo(PacketShader.remove(2), this.getMinecraftPlayer());
      }
   }

   
   public boolean applyUIShader(String id) {
      return this.applyShaderToTarget(id, 1);
   }

   
   public void removeUIShader() {
      Dispatcher.sendTo(PacketShader.remove(1), this.getMinecraftPlayer());
   }

   
   public boolean applyHUDShader(String id) {
      return this.applyShaderToTarget(id, 2);
   }

   
   public void removeHUDShader() {
      Dispatcher.sendTo(PacketShader.remove(2), this.getMinecraftPlayer());
   }

   private boolean applyShaderToTarget(String id, int target) {
      ShaderFile shader = this.getShader(id);
      if (shader == null) {
         return false;
      }

      Dispatcher.sendTo(PacketShader.apply(target, shader), this.getMinecraftPlayer());
      return true;
   }

   private ShaderFile getShader(String id) {
      if (Mappet.shaders == null || id == null || id.trim().isEmpty()) {
         return null;
      }
      return Mappet.shaders.load(id.trim());
   }

   public void setMotion(double x, double y, double z) {
      super.setMotion(x, y, z);
      if (this.entity instanceof class_3222) {
         ((class_3222)this.entity).field_13987.method_14364(new class_2743(this.entity));
      }
   }

   public void setRotations(float pitch, float yaw, float yawHead) {
      super.setRotations(pitch, yaw, yawHead);
      if (this.entity instanceof class_3222) {
         class_3222 player = (class_3222)this.entity;
         Dispatcher.sendTo(new PacketEntityRotations(player.method_5628(), yaw, yawHead, pitch), player);
      }
   }

   public void swingArm(int arm) {
      super.swingArm(arm);
      if (this.entity instanceof class_3222) {
         ((class_3222)this.entity).field_13987.method_14364(new class_2616(this.entity, (byte)(arm == 1 ? 3 : 0)));
      }
   }

   public int getGameMode() {
      return this.entity instanceof class_3222 ? ((class_3222)this.entity).field_13974.method_14257().method_8379() : 0;
   }

   public void setGameMode(int gameMode) {
      if (this.entity instanceof class_3222 && gameMode >= 0 && gameMode <= 3) {
         ((class_3222)this.entity).method_7336(class_1934.method_8384(gameMode));
      }
   }

   public void setLay(boolean lay) {
      EntityDataHolder holder = (EntityDataHolder)this.entity;

      if (holder.mappet$isLay() == lay) {
         return;
      }

      holder.mappet$getPersistentData().method_10556("MappetLay", lay);
      holder.mappet$setLay(lay);
   }

   public boolean getLay() {
      return ((EntityDataHolder)this.entity).mappet$isLay();
   }

   public IScriptInventory getInventory() {
      if (this.inventory == null) {
         this.inventory = new ScriptInventory(this.entity.method_31548());
      }
      return this.inventory;
   }

   public IScriptInventory getEnderChest() {
      if (!(this.entity instanceof class_3222)) {
         return null;
      }
      if (this.enderChest == null) {
         this.enderChest = new ScriptInventory(((class_3222)this.entity).method_7274());
      }
      return this.enderChest;
   }

   public void executeCommand(String command) {
      if (this.entity instanceof class_746) {
         return;
      }
      if (this.entity instanceof class_3222) {
         ((class_3222)this.entity).method_5682().method_3734().method_44252(((class_3222)this.entity).method_5671(), command == null ? "" : command);
      }
   }

   public void setSpawnPoint(double x, double y, double z) {
      if (this.entity instanceof class_3222) {
         ((class_3222)this.entity).method_26284(((class_3222)this.entity).method_37908().method_27983(), class_2338.method_49637(x, y, z), 0.0F, true, false);
      }
   }

   public ScriptVector getSpawnPoint() {
      class_2338 pos = ((class_3222)this.entity).method_26280();
      if (pos == null) {
         pos = ((class_3222)this.entity).method_51469().method_43126();
      }

      return new ScriptVector((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260());
   }

   public boolean isFlying() {
      return this.entity instanceof class_3222 && ((class_3222)this.entity).method_31549().field_7479;
   }

   public float getWalkSpeed() {
      return this.entity instanceof class_3222 ? (float)((class_3222)this.entity).method_26825(class_5134.field_23719) : 0.1F;
   }


   public void setFlyingEnabled(boolean enabled) {
      if (this.entity instanceof class_3222) {
         ((class_3222)this.entity).method_31549().field_7478 = enabled;
         ((class_3222)this.entity).method_7355();
      }
   }

   public float getFlySpeed() {
      return this.entity instanceof class_3222 ? ((class_3222)this.entity).method_31549().method_7252() : 0.05F;
   }

   public void setFlySpeed(float speed) {
      if (this.entity instanceof class_3222) {
         ((class_3222)this.entity).method_31549().method_7248(speed);
         ((class_3222)this.entity).method_7355();
      }
   }

   public void resetFlySpeed() {
      this.setFlySpeed(0.05F);
   }

   public void setWalkSpeed(float speed) {
      if (!(this.entity instanceof class_3222) || Float.isNaN(speed) || Float.isInfinite(speed)) {
         return;
      }
      speed = Math.max(0.0F, speed);
      class_1324 attribute = ((class_3222)this.entity).method_5996(class_5134.field_23719);
      if (attribute != null) {
         attribute.method_6192((double)speed);
      }
   }


   public void resetWalkSpeed() {
      this.setWalkSpeed(0.1F);
   }

   public float getCooldown(IScriptItemStack item) {
      return this.entity instanceof class_3222 && item != null ? ((class_3222)this.entity).method_7357().method_7905(item.getMinecraftItemStack().method_7909(), 0.0F) : 0.0F;
   }

   public float getCooldown(int inventorySlot) {
      return this.getCooldown(this.getInventory().getStack(inventorySlot));
   }

   public void setCooldown(IScriptItemStack item, int ticks) {
      if (this.entity instanceof class_3222 && item != null) {
         ((class_3222)this.entity).method_7357().method_7906(item.getMinecraftItemStack().method_7909(), ticks);
      }
   }

   public void setCooldown(int inventorySlot, int ticks) {
      this.setCooldown(this.getInventory().getStack(inventorySlot), ticks);
   }

   public void resetCooldown(IScriptItemStack item) {
      if (this.entity instanceof class_3222 && item != null) {
         ((class_3222)this.entity).method_7357().method_7900(item.getMinecraftItemStack().method_7909());
      }
   }

   public void resetCooldown(int inventorySlot) {
      this.resetCooldown(this.getInventory().getStack(inventorySlot));
   }

   public int getHotbarIndex() {
      return this.entity.method_31548().field_7545;
   }

   public void setHotbarIndex(int slot) {
      if (slot >= 0 && slot < 9) {
         this.entity.method_31548().field_7545 = slot;
         if (this.entity instanceof class_3222) {
            ((class_3222)this.entity).field_13987.method_14364(new class_2735(slot));
         }
      }
   }

   public void send(String message) {
      class_2561 component = class_2561.method_43470(message == null ? "" : message);
      if (this.entity instanceof class_3222) {
         ((class_3222)this.entity).method_43496(component);
      } else if (this.entity instanceof class_746) {
         ((class_746)this.entity).method_7353(component, false);
      }
   }

   public void sendRaw(INBT message) {
      class_2561 component = message == null ? null : class_2562.method_10877(message.stringify());
      if (component != null) {
         if (this.entity instanceof class_3222) {
            ((class_3222)this.entity).method_43496(component);
         } else if (this.entity instanceof class_746) {
            ((class_746)this.entity).method_7353(component, false);
         }
      }

   }

   



   public String getSkin(String source) {
      String type = source == null ? "" : source.toLowerCase(Locale.ROOT);
      String name = this.getMinecraftPlayer().method_5477().getString();
      String skin = fetchSkinUrl(name, type);
      return skin == null ? "minecraft:textures/entity/steve.png" : skin;
   }

   
   public ILocalMorph createLocalMorph(String id, AbstractMorph morph, double x, double y, double z) {
      
      WorldMorph worldMorph = new WorldMorph();
      worldMorph.id = id == null ? "" : id;
      worldMorph.morph = morph;
      worldMorph.x = x;
      worldMorph.y = y;
      worldMorph.z = z;
      worldMorph.expiration = Integer.MAX_VALUE;
      worldMorph.scale = true;
      

      
      class_1657 targetPlayer = null;
      if (this.entity instanceof class_746) {
         
         targetPlayer = this.entity;
      } else if (this.entity instanceof class_3222) {
         
         targetPlayer = this.entity;
      }
      

      return new ScriptWorldMorph(worldMorph, targetPlayer);
   }

   



   private class ScriptWorldMorph implements ILocalMorph {
      private final WorldMorph morph;
      private final class_1657 targetPlayer;

      public ScriptWorldMorph(WorldMorph morph, class_1657 targetPlayer) {
         this.morph = morph;
         this.targetPlayer = targetPlayer;
         send();
      }

      @Override
      public ILocalMorph seeThrough(boolean value) {
         this.morph.seeThrough = value;
         send();
         return this;
      }

      @Override
      public ILocalMorph scale(boolean value) {
         this.morph.scale = value;
         send();
         return this;
      }

      @Override
      public boolean isFar(double distance) {
         return this.morph.distanceTo(this.targetPlayer) >= Math.max(0.0D, distance);
      }

      @Override
      public boolean isNear(double distance) {
         return this.morph.distanceTo(this.targetPlayer) < Math.max(0.0D, distance);
      }

      @Override
      public void remove() {
         this.morph.remove = true;
         send();
      }

      private void send() {
         if (targetPlayer != null && targetPlayer instanceof class_3222) {
            
            Dispatcher.sendTo(new PacketWorldMorph(this.morph), (class_3222) targetPlayer);
         } else {
            
            if (this.morph.remove) {
               mchorse.mappet.client.RenderingHandler.removeWorldMorph(this.morph.id);
            } else if (this.morph.morph != null) {
               mchorse.mappet.client.RenderingHandler.addOrReplaceWorldMorph(this.morph);
            }
         }
      }
   }

   public void removeLocalMorph(String id) {
      String localId = id == null ? "" : id;
      if (this.entity instanceof class_3222) {
         WorldMorph marker = new WorldMorph();
         marker.id = localId;
         marker.remove = true;
         Dispatcher.sendTo(new PacketWorldMorph(marker), (class_3222)this.entity);
      } else {
         mchorse.mappet.client.RenderingHandler.removeWorldMorph(localId);
      }
   }

   public ILocalMorph createLocalMorph(String id, String nbt, double x, double y, double z) {
      try {
         AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(class_2522.method_10718(nbt));
         return this.createLocalMorph(id, morph, x, y, z);
      } catch (Exception ignored) {
         return null;
      }
   }

   public ILocalMorph createLocalMorph(String id, INBTCompound nbt, double x, double y, double z) {
      AbstractMorph morph = nbt == null ? null : MorphManager.INSTANCE.morphFromNBT(nbt.getNbtCompound());
      return this.createLocalMorph(id, morph, x, y, z);
   }

   public String getType() {
      try {
         String name = this.getMinecraftPlayer().method_5477().getString();
         JsonObject profile = readJson("https://api.mojang.com/users/profiles/minecraft/" + name);
         if (profile != null && profile.has("id")) {
            JsonObject session = readJson("https://sessionserver.mojang.com/session/minecraft/profile/" + profile.get("id").getAsString());
            if (session != null && session.has("properties") && session.get("properties").isJsonArray()) {
               JsonArray properties = session.getAsJsonArray("properties");
               if (properties.size() > 0 && properties.get(0).isJsonObject()) {
                  JsonObject property = properties.get(0).getAsJsonObject();
                  if (property.has("value")) {
                     String decoded = new String(Base64.getDecoder().decode(property.get("value").getAsString()), StandardCharsets.UTF_8);
                     JsonObject textures = new JsonParser().parse(decoded).getAsJsonObject().getAsJsonObject("textures");
                     if (textures != null && textures.has("SKIN") && textures.get("SKIN").isJsonObject()) {
                        JsonObject skin = textures.getAsJsonObject("SKIN");
                        if (skin.has("metadata") && skin.get("metadata").isJsonObject()
                                && "slim".equals(skin.getAsJsonObject("metadata").get("model").getAsString())) {
                           return "alex";
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception ignored) {
         
      }

      return "fred";
   }

   private static String fetchSkinUrl(String name, String type) {
      try {
         JsonObject skin = null;
         if ("ely".equals(type)) {
            JsonObject response = readJson("https://skinsystem.ely.by/textures/" + name);
            if (response != null && response.has("SKIN") && response.get("SKIN").isJsonObject()) {
               skin = response.getAsJsonObject("SKIN");
            }
         } else if ("tl".equals(type)) {
            JsonObject response = readJson("https://auth.tlauncher.org/skin/profile/texture/login/" + name);
            if (response != null) {
               if (response.has("SKIN") && response.get("SKIN").isJsonObject()) {
                  skin = response.getAsJsonObject("SKIN");
               } else if (response.has("textures") && response.get("textures").isJsonObject()) {
                  JsonObject textures = response.getAsJsonObject("textures");
                  if (textures.has("SKIN") && textures.get("SKIN").isJsonObject()) {
                     skin = textures.getAsJsonObject("SKIN");
                  }
               }
            }
         } else if ("lic".equals(type)) {
            JsonObject profile = readJson("https://api.mojang.com/users/profiles/minecraft/" + name);
            if (profile != null && profile.has("id")) {
               JsonObject session = readJson("https://sessionserver.mojang.com/session/minecraft/profile/" + profile.get("id").getAsString());
               if (session != null && session.has("properties") && session.get("properties").isJsonArray()) {
                  JsonArray properties = session.getAsJsonArray("properties");
                  if (properties.size() > 0 && properties.get(0).isJsonObject()) {
                     JsonObject property = properties.get(0).getAsJsonObject();
                     if (property.has("value")) {
                        String decoded = new String(Base64.getDecoder().decode(property.get("value").getAsString()), StandardCharsets.UTF_8);
                        JsonObject textures = new JsonParser().parse(decoded).getAsJsonObject().getAsJsonObject("textures");
                        if (textures != null && textures.has("SKIN") && textures.get("SKIN").isJsonObject()) {
                           skin = textures.getAsJsonObject("SKIN");
                        }
                     }
                  }
               }
            }
         }

         if (skin != null && skin.has("url") && !skin.get("url").isJsonNull()) {
            return skin.get("url").getAsString().replaceFirst("^http:", "https:");
         }
      } catch (Exception ignored) {
         
      }

      return null;
   }

   private static JsonObject readJson(String address) {
      HttpURLConnection connection = null;
      try {
         connection = (HttpURLConnection)new URL(address).openConnection();
         connection.setConnectTimeout(3000);
         connection.setReadTimeout(3000);
         connection.setRequestProperty("User-Agent", "Mappet");
         if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return null;
         }

         StringBuilder result = new StringBuilder();
         try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
               result.append(line);
            }
         }

         JsonElement parsed = new JsonParser().parse(result.toString());
         return parsed.isJsonObject() ? parsed.getAsJsonObject() : null;
      } catch (Exception ignored) {
         return null;
      } finally {
         if (connection != null) {
            connection.disconnect();
         }
      }
   }

   public void sendTitleDurations(int fadeIn, int idle, int fadeOut) {
      if (this.entity instanceof class_3222) {
         class_5905 packet = new class_5905(fadeIn, idle, fadeOut);
         this.getMinecraftPlayer().field_13987.method_14364(packet);
      } else if (this.entity instanceof class_746) {
         class_310 client = class_310.method_1551();
         if (client != null && client.field_1705 != null) {
            client.field_1705.method_34001(fadeIn, idle, fadeOut);
         }
      }
   }

   public void sendTitle(String title) {
      if (this.entity instanceof class_3222) {
         class_5904 packet = new class_5904(class_2561.method_43470(title));
         this.getMinecraftPlayer().field_13987.method_14364(packet);
      } else if (this.entity instanceof class_746) {
         class_310 client = class_310.method_1551();
         if (client != null && client.field_1705 != null) {
            client.field_1705.method_34004(class_2561.method_43470(title));
         }
      }
   }

   public void sendSubtitle(String title) {
      if (this.entity instanceof class_3222) {
         class_5903 packet = new class_5903(class_2561.method_43470(title));
         this.getMinecraftPlayer().field_13987.method_14364(packet);
      } else if (this.entity instanceof class_746) {
         class_310 client = class_310.method_1551();
         if (client != null && client.field_1705 != null) {
            client.field_1705.method_34002(class_2561.method_43470(title));
         }
      }
   }

   public void sendActionBar(String title) {
      if (this.entity instanceof class_3222) {
         class_5894 packet = new class_5894(class_2561.method_43470(title));
         this.getMinecraftPlayer().field_13987.method_14364(packet);
      } else if (this.entity instanceof class_746) {
         class_310 client = class_310.method_1551();
         if (client != null && client.field_1705 != null) {
            client.field_1705.method_1758(class_2561.method_43470(title), false);
         }
      }
   }

   public void setXp(int level, int points) {
      if (this.entity instanceof class_3222) {
         ((class_3222)this.entity).method_7316(-this.getXpLevel() - 1);
         ((class_3222)this.entity).method_7316(level);
         ((class_3222)this.entity).method_7255(points);
      }
   }

   public void addXp(int points) {
      if (this.entity instanceof class_3222) {
         ((class_3222)this.entity).method_7255(points);
      }
   }

   public int getXpLevel() {
      return (this.entity).field_7520;
   }

   public int getXpPoints() {
      return this.entity instanceof class_3222 ? (int)(this.entity.field_7510 * (float)((class_3222)this.entity).method_7349()) : 0;
   }

   public void setHunger(int value) {
      if (this.entity instanceof class_3222) {
         ((class_3222)this.entity).method_7344().method_7580(value);
      }
   }

   public int getHunger() {
      return this.entity instanceof class_3222 ? ((class_3222)this.entity).method_7344().method_7586() : 0;
   }

   public void setSaturation(float value) {
      if (this.entity instanceof class_3222) {
         ((class_3222)this.entity).method_7344().method_7581(value);
      }
   }

   public float getSaturation() {
      return this.entity instanceof class_3222 ? ((class_3222)this.entity).method_7344().method_7589() : 0.0F;
   }

   public void playSound(String event, double x, double y, double z, float volume, float pitch) {
      this.playSound(event, "master", x, y, z, volume, pitch);
   }

   public void playSound(String event, String soundCategory, double x, double y, double z, float volume, float pitch) {
      if (this.entity instanceof class_3222) {
         WorldUtils.playSound((class_3222)this.entity, event, soundCategory, x, y, z, volume, pitch);
      } else {
         
         ClientHandlerSound.handle(new PacketSound(event, soundCategory, volume, pitch));
      }
   }

   public void playSound(String event, String soundCategory, double x, double y, double z) {
      this.playSound(event, soundCategory, x, y, z, 1.0F, 1.0F);
   }

   public IScriptManagedSound playManagedSound(String id, String event, String category, double x, double y, double z, float volume, float pitch) {
      String specialId = this.requireManagedSoundId(id);
      String soundName = this.requireManagedSoundName(event);
      String soundCategory = category == null || category.isEmpty() ? "master" : category;
      float soundVolume = Math.max(0.0F, volume);
      PacketManagedSound packet = PacketManagedSound.play(specialId, soundName, soundCategory, false, x, y, z, soundVolume, pitch);
      if (this.entity instanceof class_3222) {
         ManagedSoundRegistry.play((class_3222)this.entity, specialId, soundName, false, x, y, z, soundVolume, pitch);
         Dispatcher.sendTo(packet, (class_3222)this.entity);
         return new ScriptManagedSound((class_3222)this.entity, specialId);
      }
      ClientManagedSoundManager.handle(packet);
      return new ScriptClientManagedSound(specialId, soundName, x, y, z, soundVolume);
   }

   public IScriptManagedSound playManagedSound(String id, String event, String category, mchorse.mappet.api.scripts.user.entities.IScriptEntity target, float volume, float pitch) {
      if (!(target instanceof ScriptEntity<?> scriptEntity) || scriptEntity.getMinecraftEntity() == null) {
         throw new IllegalArgumentException("Entity-bound managed sound requires a valid entity");
      }

      String specialId = this.requireManagedSoundId(id);
      String soundName = this.requireManagedSoundName(event);
      String soundCategory = category == null || category.isEmpty() ? "master" : category;
      float soundVolume = Math.max(0.0F, volume);
      net.minecraft.class_1297 minecraftEntity = scriptEntity.getMinecraftEntity();
      double x = minecraftEntity.method_23317();
      double y = minecraftEntity.method_23318();
      double z = minecraftEntity.method_23321();
      PacketManagedSound packet = PacketManagedSound.playEntity(specialId, soundName, soundCategory, false, minecraftEntity.method_5628(), x, y, z, soundVolume, pitch);
      if (this.entity instanceof class_3222) {
         class_3222 player = (class_3222)this.entity;
         ManagedSoundRegistry.play(player, specialId, soundName, false, x, y, z, soundVolume, pitch, minecraftEntity.method_5628());
         Dispatcher.sendTo(packet, player);
         return new ScriptManagedSound(player, specialId);
      }
      ClientManagedSoundManager.handle(packet);
      return new ScriptClientManagedSound(specialId, soundName, x, y, z, soundVolume);
   }

   public IScriptManagedSound playManagedStaticSound(String id, String event, String category, float volume, float pitch) {
      String specialId = this.requireManagedSoundId(id);
      String soundName = this.requireManagedSoundName(event);
      String soundCategory = category == null || category.isEmpty() ? "master" : category;
      float soundVolume = Math.max(0.0F, volume);
      PacketManagedSound packet = PacketManagedSound.play(specialId, soundName, soundCategory, true, 0.0D, 0.0D, 0.0D, soundVolume, pitch);
      if (this.entity instanceof class_3222) {
         ManagedSoundRegistry.play((class_3222)this.entity, specialId, soundName, true, 0.0D, 0.0D, 0.0D, soundVolume, pitch);
         Dispatcher.sendTo(packet, (class_3222)this.entity);
         return new ScriptManagedSound((class_3222)this.entity, specialId);
      }
      ClientManagedSoundManager.handle(packet);
      return new ScriptClientManagedSound(specialId, soundName, 0.0D, 0.0D, 0.0D, soundVolume);
   }

   public IScriptManagedSound getManagedSound(String id) {
      String specialId = this.requireManagedSoundId(id);
      if (this.entity instanceof class_3222) {
         return ManagedSoundRegistry.has((class_3222)this.entity, specialId) ? new ScriptManagedSound((class_3222)this.entity, specialId) : null;
      }
      return ClientManagedSoundManager.has(specialId) ? new ScriptClientManagedSound(specialId, ClientManagedSoundManager.getName(specialId), 0.0D, 0.0D, 0.0D, 1.0F) : null;
   }

   private String requireManagedSoundId(String id) {
      if (id == null || id.trim().isEmpty()) {
         throw new IllegalArgumentException("Special sound ID can't be empty");
      }

      return id.trim();
   }

   private String requireManagedSoundName(String event) {
      if (event == null || event.trim().isEmpty()) {
         throw new IllegalArgumentException("Sound event name can't be empty");
      }

      return event.trim();
   }

   public void stopSound(String event, String category) {
      class_2960 id = event != null && !event.isEmpty() ? class_2960.method_12829(event) : null;
      class_3419 soundCategory = category != null && !category.isEmpty() ? class_3419.valueOf(category.toUpperCase(Locale.ROOT)) : null;
      if (this.entity instanceof class_3222) {
         ((class_3222)this.entity).field_13987.method_14364(new class_2770(id, soundCategory));
      }
   }

   public void playStaticSound(String event, float volume, float pitch) {
      this.playStaticSound(event, "master", volume, pitch);
   }

   public void playStaticSound(String event, String soundCategory, float volume, float pitch) {
      PacketSound packet = new PacketSound(event, soundCategory, volume, pitch);
      if (this.entity instanceof class_3222) {
         Dispatcher.sendTo(packet, (class_3222)this.entity);
      } else {
         ClientHandlerSound.handle(packet);
      }
   }

   public void playLoopSound(String event, String category, float volume, float pitch) {
      PacketSound packet = PacketSound.loop(event, category, volume, pitch);
      if (this.entity instanceof class_3222) {
         Dispatcher.sendTo(packet, (class_3222)this.entity);
      } else {
         ClientHandlerSound.handle(packet);
      }
   }

   public void stopLoopSound(String event, String category) {
      PacketSound packet = PacketSound.stopLoop(event, category);
      if (this.entity instanceof class_3222) {
         Dispatcher.sendTo(packet, (class_3222)this.entity);
      } else {
         ClientHandlerSound.handle(packet);
      }
   }

   public IMappetQuests getQuests() {
      if (this.quests == null) {
         this.quests = new MappetQuests(Character.get((class_1657)this.entity).getQuests(), (class_1657)this.entity);
      }

      return this.quests;
   }

   public AbstractMorph getMorph() {
      IMorphing cap = Morphing.get((class_1657)this.entity);
      return cap != null ? cap.getCurrentMorph() : super.getMorph();
   }

   public boolean setMorph(AbstractMorph morph) {
      if (morph == null) {
         MorphAPI.demorph((class_1657)this.entity);
      } else {
         MorphAPI.morph((class_1657)this.entity, morph, true);
      }

      return true;
   }

   public void playAnimation(String animation) {
      PacketPlayModelAnimation packet = new PacketPlayModelAnimation(animation, this.entity.method_5628());
      Dispatcher.sendToTracked(this.entity, packet);
      Dispatcher.sendTo(packet, (class_3222)this.entity);
   }

   public void setClipboard(String text) {
      String clipboard = PacketClipboard.sanitize(text);
      if (this.entity instanceof class_3222) {
         ClientClipboardCache.set(this.entity.method_5667(), clipboard);
         Dispatcher.sendTo(new PacketClipboard(PacketClipboard.SET, clipboard), (class_3222)this.entity);
      } else {
         ClientHandlerClipboard.set(clipboard);
      }
   }

   public void setMousePosition(double x, double y) {
      mchorse.mappet.network.common.scripts.PacketMousePosition packet = new mchorse.mappet.network.common.scripts.PacketMousePosition(x, y, 0, "linear");
      if (this.entity instanceof class_3222) {
         Dispatcher.sendTo(packet, (class_3222)this.entity);
      } else {
         ClientHandlerMousePosition.apply(packet);
      }
   }

   public mchorse.mappet.api.scripts.user.entities.IScriptPlayer moveMouseTo(String interpolation, int durationTicks, double x, double y) {
      mchorse.mappet.network.common.scripts.PacketMousePosition packet = new mchorse.mappet.network.common.scripts.PacketMousePosition(x, y, durationTicks, interpolation);
      if (this.entity instanceof class_3222) {
         Dispatcher.sendTo(packet, (class_3222)this.entity);
      } else {
         ClientHandlerMousePosition.apply(packet);
      }
      return this;
   }

   public mchorse.mappet.api.scripts.user.entities.IScriptPlayer moveMouseBy(String interpolation, int durationTicks, double dx, double dy) {
      mchorse.mappet.network.common.scripts.PacketMousePosition packet = new mchorse.mappet.network.common.scripts.PacketMousePosition(dx, dy, durationTicks, interpolation, true);
      if (this.entity instanceof class_3222) {
         Dispatcher.sendTo(packet, (class_3222)this.entity);
      } else {
         ClientHandlerMousePosition.apply(packet);
      }
      return this;
   }

   public String getClipboard() {
      if (this.entity instanceof class_3222) {
         Dispatcher.sendTo(new PacketClipboard(PacketClipboard.REQUEST), (class_3222)this.entity);
         return ClientClipboardCache.get(this.entity.method_5667());
      }
      return ClientHandlerClipboard.get();
   }

   public IGameSettings getSettings() {
      return new ScriptGameSettings(this.entity);
   }

   public ICameraShake getCameraShake() {
      return new ScriptCameraShake(this.entity);
   }

   public void openWeb(String url) {
      Dispatcher.sendTo(new PacketOpenWeb(url), (class_3222)this.entity);
   }

   public boolean openUI(IMappetUIBuilder in, boolean defaultData) {
      if (!(in instanceof MappetUIBuilder builder)) {
         return false;
      } else {
         ICharacter character = Character.get((class_1657)this.entity);
         boolean noContext = character.getUIContext() == null;
         if (!noContext) {
            character.getUIContext().close();
         }

         UI ui = builder.getUI();
         UIContext context = new UIContext(ui, (class_1657)this.entity, builder.getScript(), builder.getFunction());
         character.setUIContext(context);
         Dispatcher.sendTo(new PacketUI(ui), this.getMinecraftPlayer());
         if (defaultData) {
            context.populateDefaultData();
         }

         context.clearChanges();
         return noContext;
      }
   }

   public boolean openUI(String id, boolean defaultData) {
      if (Mappet.uis == null || id == null || id.trim().isEmpty()) {
         return false;
      }

      UIFile ui = (UIFile)Mappet.uis.load(id.trim());
      if (ui == null) {
         return false;
      }

      ICharacter character = Character.get((class_1657)this.entity);
      boolean noContext = character.getUIContext() == null;
      if (!noContext) {
         character.getUIContext().close();
      }

      UIContext context = new UIContext(ui, (class_1657)this.entity, ui.script, ui.function);
      character.setUIContext(context);
      Dispatcher.sendTo(new PacketUI(ui), this.getMinecraftPlayer());
      if (defaultData) {
         context.populateDefaultData();
      }

      context.clearChanges();
      return noContext;
   }

   




   public IScriptPlayer closeUI() {
      Dispatcher.sendTo(new PacketCloseUI(false), this.getMinecraftPlayer());
      return this;
   }

   
   public IScriptPlayer closeMappetUI() {
      Dispatcher.sendTo(new PacketCloseUI(true), this.getMinecraftPlayer());
      return this;
   }

   public IMappetUIContext getUIContext() {
      ICharacter character = Character.get((class_1657)this.entity);
      UIContext context = character.getUIContext();
      return context == null ? null : new MappetUIContext(context);
   }

   public Set<String> getFactions() {
      Set<String> factions = new HashSet();
      ICharacter character = Character.get((class_1657)this.entity);
      if (character != null) {
         factions = character.getStates().getFactionNames();
      }

      return factions;
   }

   public boolean setupHUD(String id) {
      return Character.get((class_1657)this.entity).setupHUD(id, true);
   }

   public void changeHUDMorph(String id, int index, AbstractMorph morph) {
      if (morph != null) {
         Character.get((class_1657)this.entity).changeHUDMorph(id, index, MorphUtils.toNBT(morph));
      }
   }

   public void changeHUDMorph(String id, int index, INBTCompound morph) {
      if (morph != null) {
         Character.get((class_1657)this.entity).changeHUDMorph(id, index, morph.getNbtCompound());
      }
   }

   
   @Override
   public void rotateBy(String interpolation, int durationTicks, float pitch, float yaw, float yawHead, boolean disableAI) {
      int duration = Math.max(0, durationTicks);
      if (duration == 0) {
         this.setRotations(this.getPitch() + pitch, this.getYaw() + yaw, this.getYawHead() + yawHead);
         return;
      }

      



      this.sendEntityTransition(false, true, interpolation, duration, this.entity.method_23317(), this.entity.method_23318(), this.entity.method_23321(), pitch, yaw, yawHead, true);
   }

   public boolean setHUDWorldLighting(String id, boolean enabled) {
      return Character.get((class_1657)this.entity).setHUDWorldLighting(id, enabled);
   }

   public boolean setHUDWorldLighting(String id, boolean enabled, float intensity) {
      return Character.get((class_1657)this.entity).setHUDWorldLighting(id, enabled, intensity);
   }

   public void closeHUD(String id) {
      Character.get((class_1657)this.entity).closeHUD(id);
   }

   public void closeAllHUD() {
      Character.get((class_1657)this.entity).closeAllHUD();
   }

   public INBTCompound getDisplayedHUDs() {
      ICharacter character = Character.get((class_1657)this.entity);
      class_2487 tag = ((Character)character).getDisplayedHUDsTag();
      return new ScriptNBTCompound(tag);
   }

   public INBTCompound getGlobalDisplayedHUDs() {
      ICharacter character = Character.get((class_1657)this.entity);
      class_2487 tag = ((Character)character).getGlobalDisplayedHUDsTag();
      return new ScriptNBTCompound(tag);
   }

   public void playScene(String sceneName) {
      if (FabricLoader.getInstance().isModLoaded("aperture")) {
         this.playApertureScene(sceneName, true);
      }

   }

   public void stopScene() {
      if (FabricLoader.getInstance().isModLoaded("aperture")) {
         this.playApertureScene("", false);
      }

   }

   private void playApertureScene(String sceneName, boolean toPlay) {
      if (this.entity instanceof class_3222) {
         mchorse.aperture.network.Dispatcher.sendTo(new PacketCameraState(sceneName, toPlay), (class_3222)this.entity);
      }
   }
}
