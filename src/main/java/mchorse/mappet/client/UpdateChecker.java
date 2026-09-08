package mchorse.mappet.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mchorse.mappet.Mappet;
import net.minecraft.class_2561;
import net.minecraft.class_310;

public class UpdateChecker {
   private static final String VERSION = "1.0.0";
   private static final String API = "https://api.github.com/repos/nuk0ro4che/ShortMappet/releases/latest";
   private static final String RELEASES = "https://github.com/nuk0ro4che/ShortMappet/releases";
   private static final Pattern TAG = Pattern.compile("\\\"tag_name\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");
   private static final Pattern URL = Pattern.compile("\\\"html_url\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");
   private static boolean checked;

   private UpdateChecker() {
   }

   public static void check() {
      if (checked) {
         return;
      }

      checked = true;
      Thread thread = new Thread(UpdateChecker::load, "ShortMappet update check");
      thread.setDaemon(true);
      thread.start();
   }

   private static void load() {
      HttpURLConnection connection = null;

      try {
         connection = (HttpURLConnection) new URL(API).openConnection();
         connection.setRequestMethod("GET");
         connection.setRequestProperty("User-Agent", "ShortMappet");
         connection.setConnectTimeout(5000);
         connection.setReadTimeout(5000);

         if (connection.getResponseCode() != 200) {
            return;
         }

         String json = read(connection);
         Matcher tag = TAG.matcher(json);

         if (!tag.find()) {
            return;
         }

         String version = tag.group(1);
         if (compare(version, VERSION) <= 0) {
            return;
         }

         Matcher link = URL.matcher(json);
         String release = link.find() ? link.group(1) : RELEASES;
         showMessage(version, release);
      } catch (Exception error) {
         Mappet.LOGGER.debug("Couldn't check for updates", error);
      } finally {
         if (connection != null) {
            connection.disconnect();
         }
      }
   }

   private static String read(HttpURLConnection connection) throws Exception {
      StringBuilder result = new StringBuilder();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
         String line;
         while ((line = reader.readLine()) != null) {
            result.append(line);
         }
      }

      return result.toString();
   }

   private static void showMessage(String version, String link) {
    class_310 client = class_310.method_1551();
    client.execute(() -> {
    if (client.field_1724 != null) {
        client.field_1724.method_7353(
                class_2561.method_43470(
                    "§eДоступно обновление ShortMappet: §f" + version + " §e" + link
                ),
                false
            );
        }
    });

   }

   private static int compare(String first, String second) {
      int[] a = numbers(first);
      int[] b = numbers(second);
      int length = Math.max(a.length, b.length);

      for (int i = 0; i < length; ++i) {
         int x = i < a.length ? a[i] : 0;
         int y = i < b.length ? b[i] : 0;

         if (x != y) {
            return x > y ? 1 : -1;
         }
      }

      return 0;
   }

   private static int[] numbers(String version) {
      String value = version.replaceFirst("^[^0-9]*", "");
      String[] parts = value.split("[^0-9]+");
      int[] result = new int[parts.length];

      for (int i = 0; i < parts.length; ++i) {
         try {
            result[i] = Integer.parseInt(parts[i]);
         } catch (NumberFormatException error) {
            result[i] = 0;
         }
      }

      return result;
   }
}
