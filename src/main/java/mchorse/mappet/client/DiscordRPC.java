package mchorse.mappet.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.Mappet;
import net.minecraft.class_310;

public final class DiscordRPC
{
   private static final Gson GSON = new GsonBuilder().create();
   private static final File CONFIG = new File(CommonProxy.configFolder, "discordrpc.json");
   private static final ExecutorService WORKER = Executors.newSingleThreadExecutor((runnable) ->
   {
      Thread thread = new Thread(runnable, "ShortMappet-DiscordRPC");
      thread.setDaemon(true);
      return thread;
   });
   private static final AtomicBoolean UPDATE_QUEUED = new AtomicBoolean();
   private static final AtomicBoolean CLOSE_QUEUED = new AtomicBoolean();
   private static Settings settings;
   private static long startedAt;
   private static long nextUpdate;
   private static IpcConnection connection;
   private static boolean loggedUnavailable;

   private DiscordRPC()
   {
   }

   public static void initialize()
   {
      settings = load();
      startedAt = Instant.now().getEpochSecond();
      nextUpdate = 0;
      loggedUnavailable = false;
      Mappet.LOGGER.info("Discord RPC initialized: enabled={}, clientIdPresent={}", settings.enabled, hasClientId(settings));
   }

   public static Settings getSettings()
   {
      if (settings == null)
      {
         settings = load();
      }

      return settings;
   }

   public static void saveSettings(Settings value)
   {
      settings = value == null ? new Settings() : value.copy();
      startedAt = Instant.now().getEpochSecond();
      nextUpdate = 0;
      loggedUnavailable = false;
      applyToConfig(settings);

      try
      {
         CONFIG.getParentFile().mkdirs();
         Files.writeString(CONFIG.toPath(), GSON.toJson(settings));
      }
      catch (Exception error)
      {
         Mappet.LOGGER.warn("Couldn't save Discord RPC config", error);
      }

      queueClose();
   }

   public static void tick(class_310 client)
   {
      Settings current = settings;
      if (current == null)
      {
         initialize();
         current = settings;
      }

      if (!current.enabled || !hasClientId(current))
      {
         queueClose();
         nextUpdate = 0;
         return;
      }

      long now = System.currentTimeMillis();
      if (now < nextUpdate || !UPDATE_QUEUED.compareAndSet(false, true))
      {
         return;
      }

      nextUpdate = now + 5000L;
      Snapshot snapshot = Snapshot.create(current, client, startedAt);
      WORKER.execute(() ->
      {
         try
         {
            update(snapshot);
         }
         finally
         {
            UPDATE_QUEUED.set(false);
         }
      });
   }

   private static void update(Snapshot snapshot)
   {
      try
      {
         if (connection == null)
         {
            connection = connect(snapshot.clientId);
            loggedUnavailable = false;
         }

         connection.setActivity(snapshot);
      }
      catch (Exception error)
      {
         closeConnection();
         if (!loggedUnavailable)
         {
            Mappet.LOGGER.warn("Discord RPC is unavailable: {}", error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage());
            loggedUnavailable = true;
         }
      }
   }

   private static void queueClose()
   {
      if (!CLOSE_QUEUED.compareAndSet(false, true))
      {
         return;
      }

      WORKER.execute(() ->
      {
         try
         {
            closeConnection();
         }
         finally
         {
            CLOSE_QUEUED.set(false);
         }
      });
   }

   private static void closeConnection()
   {
      if (connection != null)
      {
         try
         {
            connection.clearActivity();
         }
         catch (Exception ignored)
         {
         }

         try
         {
            connection.close();
         }
         catch (Exception ignored)
         {
         }

         connection = null;
      }
   }

   private static boolean hasClientId(Settings value)
   {
      return value != null && value.clientId != null && !value.clientId.trim().isEmpty();
   }

   private static String replace(String value, String player, String world)
   {
      if (value == null)
      {
         return "";
      }

      return value.replace("${player}", player).replace("${world}", world);
   }

   private static void applyToConfig(Settings value)
   {
      if (Mappet.discordRpcEnabled == null)
      {
         return;
      }

      Mappet.discordRpcEnabled.setValue(value.enabled);
      Mappet.discordRpcClientId.setValue(value.clientId);
      Mappet.discordRpcDetails.setValue(value.details);
      Mappet.discordRpcState.setValue(value.state);
      Mappet.discordRpcTimestamp.setValue(value.showTimestamp);
      Mappet.discordRpcLargeImage.setValue(value.largeImage);
      Mappet.discordRpcLargeText.setValue(value.largeText);
      Mappet.discordRpcSmallImage.setValue(value.smallImage);
      Mappet.discordRpcSmallText.setValue(value.smallText);
      Mappet.discordRpcButton1Text.setValue(value.button1Text);
      Mappet.discordRpcButton1Url.setValue(value.button1Url);
      Mappet.discordRpcButton2Text.setValue(value.button2Text);
      Mappet.discordRpcButton2Url.setValue(value.button2Url);
      Mappet.discordRpcEnabled.saveLater();
   }

   private static Settings fromConfig()
   {
      if (Mappet.discordRpcEnabled == null)
      {
         return null;
      }

      Settings value = new Settings();
      value.enabled = Boolean.TRUE.equals(Mappet.discordRpcEnabled.get());
      value.clientId = stringValue(Mappet.discordRpcClientId.get());
      value.details = stringValue(Mappet.discordRpcDetails.get());
      value.state = stringValue(Mappet.discordRpcState.get());
      value.showTimestamp = Boolean.TRUE.equals(Mappet.discordRpcTimestamp.get());
      value.largeImage = stringValue(Mappet.discordRpcLargeImage.get());
      value.largeText = stringValue(Mappet.discordRpcLargeText.get());
      value.smallImage = stringValue(Mappet.discordRpcSmallImage.get());
      value.smallText = stringValue(Mappet.discordRpcSmallText.get());
      value.button1Text = stringValue(Mappet.discordRpcButton1Text.get());
      value.button1Url = stringValue(Mappet.discordRpcButton1Url.get());
      value.button2Text = stringValue(Mappet.discordRpcButton2Text.get());
      value.button2Url = stringValue(Mappet.discordRpcButton2Url.get());
      return value;
   }

   private static String stringValue(Object value)
   {
      return value == null ? "" : String.valueOf(value);
   }

   private static Settings load()
   {
      Settings value = fromConfig();
      if (value != null)
      {
         return value;
      }

      try
      {
         if (CONFIG.isFile())
         {
            value = GSON.fromJson(Files.readString(CONFIG.toPath()), Settings.class);
         }
      }
      catch (Exception error)
      {
         Mappet.LOGGER.warn("Couldn't read Discord RPC config", error);
      }

      if (value == null)
      {
         value = new Settings();
         try
         {
            CONFIG.getParentFile().mkdirs();
            Files.writeString(CONFIG.toPath(), GSON.toJson(value));
         }
         catch (Exception error)
         {
            Mappet.LOGGER.warn("Couldn't create Discord RPC config", error);
         }
      }

      return value;
   }

   private static IpcConnection connect(String clientId) throws IOException
   {
      boolean windows = System.getProperty("os.name", "").toLowerCase().contains("win");
      IOException lastError = null;

      for (int index = 0; index < 10; index++)
      {
         try
         {
            IpcConnection value = windows
               ? new WindowsIpc("\\\\?\\pipe\\discord-ipc-" + index)
               : openUnix(index);
            value.handshake(clientId);
            return value;
         }
         catch (IOException error)
         {
            lastError = error;
         }
      }

      throw lastError == null ? new IOException("Discord IPC socket was not found") : lastError;
   }

   private static IpcConnection openUnix(int index) throws IOException
   {
      List<String> directories = new ArrayList();
      addDirectory(directories, System.getenv("XDG_RUNTIME_DIR"));
      addDirectory(directories, System.getenv("TMPDIR"));
      addDirectory(directories, System.getenv("TMP"));
      addDirectory(directories, System.getenv("TEMP"));
      addDirectory(directories, "/tmp");

      IOException lastError = null;
      for (String directory : directories)
      {
         Path path = Path.of(directory, "discord-ipc-" + index);
         if (!Files.exists(path))
         {
            continue;
         }

         try
         {
            return new UnixIpc(path);
         }
         catch (IOException error)
         {
            lastError = error;
         }
      }

      throw lastError == null ? new IOException("Discord IPC socket was not found") : lastError;
   }

   private static void addDirectory(List<String> directories, String directory)
   {
      if (directory != null && !directory.isEmpty() && !directories.contains(directory))
      {
         directories.add(directory);
      }
   }

   public static class Settings
   {
      public boolean enabled = false;
      public String clientId = "";
      public String details = "Играет в Mappet";
      public String state = "Игрок: ${player}";
      public boolean showTimestamp = true;
      public String largeImage = "";
      public String largeText = "Mappet";
      public String smallImage = "";
      public String smallText = "";
      public String button1Text = "";
      public String button1Url = "";
      public String button2Text = "";
      public String button2Url = "";

      public Settings copy()
      {
         Settings value = new Settings();
         value.enabled = this.enabled;
         value.clientId = this.clientId;
         value.details = this.details;
         value.state = this.state;
         value.showTimestamp = this.showTimestamp;
         value.largeImage = this.largeImage;
         value.largeText = this.largeText;
         value.smallImage = this.smallImage;
         value.smallText = this.smallText;
         value.button1Text = this.button1Text;
         value.button1Url = this.button1Url;
         value.button2Text = this.button2Text;
         value.button2Url = this.button2Url;
         return value;
      }
   }

   private static class Snapshot
   {
      private final String clientId;
      private final String details;
      private final String state;
      private final boolean showTimestamp;
      private final String largeImage;
      private final String largeText;
      private final String smallImage;
      private final String smallText;
      private final String button1Text;
      private final String button1Url;
      private final String button2Text;
      private final String button2Url;
      private final String player;
      private final String world;
      private final long startedAt;

      private Snapshot(Settings settings, class_310 client, long startedAt)
      {
         this.clientId = settings.clientId.trim();
         this.details = settings.details;
         this.state = settings.state;
         this.showTimestamp = settings.showTimestamp;
         this.largeImage = settings.largeImage;
         this.largeText = settings.largeText;
         this.smallImage = settings.smallImage;
         this.smallText = settings.smallText;
         this.button1Text = settings.button1Text;
         this.button1Url = settings.button1Url;
         this.button2Text = settings.button2Text;
         this.button2Url = settings.button2Url;
         this.player = client.field_1724 == null ? "" : client.field_1724.method_5477().getString();
         this.world = client.field_1687 == null ? "Главное меню" : client.field_1687.method_27983().toString();
         this.startedAt = startedAt;
      }

      private static Snapshot create(Settings settings, class_310 client, long startedAt)
      {
         return new Snapshot(settings.copy(), client, startedAt);
      }
   }

   private interface IpcConnection extends Closeable
   {
      void handshake(String clientId) throws IOException;
      void setActivity(Snapshot snapshot) throws IOException;
      void clearActivity() throws IOException;
   }

   private abstract static class BaseIpc implements IpcConnection
   {
      protected abstract void writeFrame(int opcode, String payload) throws IOException;
      protected abstract Frame readFrame() throws IOException;

      @Override
      public void handshake(String clientId) throws IOException
      {
         JsonObject payload = new JsonObject();
         payload.addProperty("v", 1);
         payload.addProperty("client_id", clientId);
         writeFrame(0, GSON.toJson(payload));

         Frame ready = readFrame();
         if (ready.opcode != 1 || !ready.payload.contains("READY"))
         {
            throw new IOException("Discord rejected handshake");
         }
      }

      @Override
      public void setActivity(Snapshot snapshot) throws IOException
      {
         JsonObject activity = new JsonObject();
         String details = replace(snapshot.details, snapshot.player, snapshot.world);
         String state = replace(snapshot.state, snapshot.player, snapshot.world);
         if (!details.isEmpty()) activity.addProperty("details", details);
         if (!state.isEmpty()) activity.addProperty("state", state);

         if (snapshot.showTimestamp)
         {
            JsonObject timestamps = new JsonObject();
            timestamps.addProperty("start", snapshot.startedAt);
            activity.add("timestamps", timestamps);
         }

         JsonArray assets = new JsonArray();
         JsonObject assetObject = new JsonObject();
         if (!empty(snapshot.largeImage)) assetObject.addProperty("large_image", snapshot.largeImage);
         if (!empty(snapshot.largeText)) assetObject.addProperty("large_text", snapshot.largeText);
         if (!empty(snapshot.smallImage)) assetObject.addProperty("small_image", snapshot.smallImage);
         if (!empty(snapshot.smallText)) assetObject.addProperty("small_text", snapshot.smallText);
         if (assetObject.size() > 0) activity.add("assets", assetObject);

         JsonArray buttons = new JsonArray();
         addButton(buttons, snapshot.button1Text, snapshot.button1Url);
         addButton(buttons, snapshot.button2Text, snapshot.button2Url);
         if (buttons.size() > 0) activity.add("buttons", buttons);

         JsonObject args = new JsonObject();
         args.addProperty("pid", ProcessHandle.current().pid());
         args.add("activity", activity);
         sendCommand("SET_ACTIVITY", args);
      }

      @Override
      public void clearActivity() throws IOException
      {
         JsonObject args = new JsonObject();
         args.add("activity", com.google.gson.JsonNull.INSTANCE);
         sendCommand("SET_ACTIVITY", args);
      }

      private void sendCommand(String command, JsonObject args) throws IOException
      {
         JsonObject payload = new JsonObject();
         payload.addProperty("cmd", command);
         payload.addProperty("nonce", Long.toHexString(System.nanoTime()));
         payload.add("args", args);
         writeFrame(1, GSON.toJson(payload));
         readFrame();
      }

      private static boolean empty(String value)
      {
         return value == null || value.isEmpty();
      }

      private static void addButton(JsonArray buttons, String label, String url)
      {
         if (!empty(label) && url != null && url.startsWith("https://"))
         {
            JsonObject button = new JsonObject();
            button.addProperty("label", label);
            button.addProperty("url", url);
            buttons.add(button);
         }
      }
   }

   private static class Frame
   {
      private final int opcode;
      private final String payload;

      private Frame(int opcode, String payload)
      {
         this.opcode = opcode;
         this.payload = payload;
      }
   }

   private static class WindowsIpc extends BaseIpc
   {
      private final RandomAccessFile file;

      private WindowsIpc(String path) throws IOException
      {
         this.file = new RandomAccessFile(path, "rw");
      }

      @Override
      protected synchronized void writeFrame(int opcode, String payload) throws IOException
      {
         byte[] data = payload.getBytes(java.nio.charset.StandardCharsets.UTF_8);
         ByteBuffer header = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
         header.putInt(opcode).putInt(data.length);
         file.write(header.array());
         file.write(data);
      }

      @Override
      protected synchronized Frame readFrame() throws IOException
      {
         int opcode = Integer.reverseBytes(file.readInt());
         int length = Integer.reverseBytes(file.readInt());
         if (length < 0 || length > 4 * 1024 * 1024) throw new IOException("Invalid Discord IPC frame");
         byte[] data = new byte[length];
         file.readFully(data);
         return new Frame(opcode, new String(data, java.nio.charset.StandardCharsets.UTF_8));
      }

      @Override
      public void close() throws IOException
      {
         file.close();
      }
   }

   private static class UnixIpc extends BaseIpc
   {
      private final SocketChannel channel;

      private UnixIpc(Path path) throws IOException
      {
         this.channel = SocketChannel.open(StandardProtocolFamily.UNIX);
         this.channel.connect(UnixDomainSocketAddress.of(path));
      }

      @Override
      protected synchronized void writeFrame(int opcode, String payload) throws IOException
      {
         byte[] data = payload.getBytes(java.nio.charset.StandardCharsets.UTF_8);
         ByteBuffer buffer = ByteBuffer.allocate(8 + data.length).order(ByteOrder.LITTLE_ENDIAN);
         buffer.putInt(opcode).putInt(data.length).put(data).flip();
         writeFully(buffer);
      }

      @Override
      protected synchronized Frame readFrame() throws IOException
      {
         ByteBuffer header = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
         readFully(header);
         header.flip();
         int opcode = header.getInt();
         int length = header.getInt();
         if (length < 0 || length > 4 * 1024 * 1024) throw new IOException("Invalid Discord IPC frame");
         ByteBuffer body = ByteBuffer.allocate(length);
         readFully(body);
         return new Frame(opcode, new String(body.array(), java.nio.charset.StandardCharsets.UTF_8));
      }

      private void writeFully(ByteBuffer buffer) throws IOException
      {
         while (buffer.hasRemaining()) channel.write(buffer);
      }

      private void readFully(ByteBuffer buffer) throws IOException
      {
         while (buffer.hasRemaining())
         {
            if (channel.read(buffer) < 0) throw new IOException("Discord IPC closed");
         }
      }

      @Override
      public void close() throws IOException
      {
         channel.close();
      }
   }
}
