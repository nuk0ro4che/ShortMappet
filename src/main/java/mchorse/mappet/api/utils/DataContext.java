package mchorse.mappet.api.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import mchorse.mappet.Mappet;
import mchorse.mappet.entities.EntityNpc;
import mchorse.mappet.utils.ExpressionRewriter;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2168;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2514;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2522;
import net.minecraft.server.MinecraftServer;

public class DataContext {
   public static final ExpressionRewriter REWRITER = new ExpressionRewriter();
   public MinecraftServer server;
   public class_1937 world;
   public class_2338 pos;
   public class_1297 subject;
   public class_1297 object;
   private boolean canceled;
   private boolean client;
   private TriggerSender sender;
   private Map<String, Object> values;

   public DataContext(class_1297 subject, class_1297 object) {
      this(subject.method_37908());
      this.subject = subject;
      this.object = object;
      this.setup();
   }

   public DataContext(class_1297 subject) {
      this(subject.method_37908());
      this.subject = subject;
      this.setup();
   }

   public DataContext(class_1937 world) {
      this(world.method_8503());
      this.world = world;
   }

   public DataContext(class_1937 world, class_2338 pos) {
      this(world.method_8503());
      this.world = world;
      this.pos = pos;
   }

   public DataContext(MinecraftServer server) {
      this.values = new HashMap();
      this.server = server;
      this.world = server.method_3847(class_1937.field_25179);
      this.setup();
   }

   

   public static DataContext client(class_1297 subject) {
      DataContext context = new DataContext();
      context.subject = subject;
      context.world = subject == null ? null : subject.method_37908();
      context.client = true;
      context.setup();
      return context;
   }

   private DataContext() {
      this.values = new HashMap();
   }

   public boolean isClient() {
      return this.client;
   }

   public void cancel() {
      this.cancel(true);
   }

   public void cancel(boolean canceled) {
      this.canceled = canceled;
   }

   public boolean isCanceled() {
      return this.canceled;
   }

   private void setup() {
      class_1657 player = this.getPlayer();
      EntityNpc npc = this.getNpc();
      this.set("subject", this.subject == null ? "" : this.subject.method_5845());
      this.set("subject_name", this.subject == null ? "" : this.subject.method_5477().getString());
      this.set("object", this.object == null ? "" : this.object.method_5845());
      this.set("object_name", this.object == null ? "" : this.object.method_5477().getString());
      this.set("player", player == null ? "" : player.method_5845());
      this.set("player_name", player == null ? "" : player.method_5477().getString());
      this.set("npc", npc == null ? "" : npc.method_5845());
      this.set("npc_name", npc == null ? "" : npc.method_5477().getString());
   }

   public DataContext set(String key, double value) {
      this.values.put(key, value);
      return this;
   }

   public DataContext set(String key, String value) {
      this.values.put(key, value);
      return this;
   }

   public DataContext set(String key, boolean value) {
      this.values.put(key, value);
      return this;
   }

   public DataContext parse(String nbt) {
      try {
         this.parse(class_2522.method_10718(nbt));
      } catch (Exception var3) {
      }

      return this;
   }

   public DataContext parse(class_2487 tag) {
      for(String key : tag.method_10541()) {
         class_2520 value = tag.method_10580(key);
         if (value instanceof class_2514) {
            this.set(key, ((class_2514)value).method_10697());
         } else if (value instanceof class_2519) {
            this.set(key, ((class_2519)value).method_10714());
         }
      }

      return this;
   }

   public Map<String, Object> getValues() {
      return this.values;
   }

   public Object getValue(String key) {
      return this.values.get(key);
   }

   public int execute(String command) {
      if (this.server == null) {
         
         return 0;
      }
      command = this.process(command == null ? "" : command);
      return this.server.method_3734().method_44252(this.getSender(), command);
   }

   public String process(String text) {
      if (text.startsWith("/")) {
         text = text.substring(1);
      }

      return !text.contains("${") ? text : REWRITER.set(this).rewrite(text);
   }

   public class_1657 getPlayer() {
      if (this.subject instanceof class_1657) {
         return (class_1657)this.subject;
      } else {
         return this.object instanceof class_1657 ? (class_1657)this.object : null;
      }
   }

   public EntityNpc getNpc() {
      if (this.subject instanceof EntityNpc) {
         return (EntityNpc)this.subject;
      } else {
         return this.object instanceof EntityNpc ? (EntityNpc)this.object : null;
      }
   }

   public Set<String> getKeys() {
      return this.values.keySet();
   }

   public class_2168 getSender() {
      if (this.server == null) {
         return null;
      }
      if ((Boolean)Mappet.eventUseServerForCommands.get()) {
         return this.server.method_3739();
      } else {
         if (this.sender == null) {
            this.sender = new TriggerSender();
         }

         if (this.subject == null) {
            this.sender.set(this.server, this.world, this.pos);
         } else {
            this.sender.set(this.subject);
         }

         return this.sender.getSource();
      }
   }

   public DataContext copy() {
      DataContext context = this.server == null ? new DataContext() : new DataContext(this.server);
      context.subject = this.subject;
      context.object = this.object;
      context.server = this.server;
      context.world = this.world;
      context.client = this.client;
      context.values.putAll(this.values);
      context.setup();
      return context;
   }
}
