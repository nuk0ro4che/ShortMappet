package mchorse.mappet.network.server.scripts;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.scripts.Script;
import mchorse.mappet.api.scripts.ScriptManager;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketRequestScriptDiagnostic;
import mchorse.mappet.network.common.scripts.PacketScriptDiagnosticCode;
import mchorse.mappet.utils.autocomplete.utils.ScopeAnalyzer;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.class_3222;


public class ServerHandlerRequestScriptDiagnostic extends ServerMessageHandler<PacketRequestScriptDiagnostic> {
   private static final Pattern REMOTE_IMPORT = Pattern.compile("(?m)^\\s*import\\s+(?:([\\\"'])([^\\\"']+)\\1|([^;\\s]+))\\s*;?\\s*$");
   public void run(class_3222 player, PacketRequestScriptDiagnostic message) {
      if (!OpHelper.isPlayerOp(player) || message.script == null || message.script.isEmpty()) {
         return;
      }

      ScriptManager manager = message.clientScript ? Mappet.clientScripts : Mappet.scripts;
      Script script = manager == null ? null : manager.load(message.script);
      String code = script == null || script.code == null ? "" : script.code;
      boolean library = script != null && script.globalLibrary;
      Dispatcher.sendTo(new PacketScriptDiagnosticCode(message.script, code, getLibraryFunctionNames(manager, script), message.clientScript, library), player);
   }

   private static Set<String> getLibraryFunctionNames(ScriptManager manager, Script script) {
      if (manager == null) {
         return new HashSet();
      }
      Set<String> libraryIds = new HashSet(manager.globalLibraries.keySet());
      if (script != null && script.libraries != null) {
         libraryIds.addAll(script.libraries);
      }

      List<String> libraryCodes = new ArrayList();
      for(String id : libraryIds) {
         Script library = manager.load(id);
         if (library != null && library.code != null && !library.code.isEmpty()) {
            libraryCodes.add(library.code);
         }
      }

      Set<String> checkedUrls = new HashSet();
      List<String> importedCodes = new ArrayList(libraryCodes);
      if (script != null && script.code != null) {
         collectRemoteImportCodes(script.code, importedCodes, checkedUrls);
      }
      for (int index = 0; index < importedCodes.size(); ++index) {
         collectRemoteImportCodes(importedCodes.get(index), importedCodes, checkedUrls);
      }
      return ScopeAnalyzer.getLibraryFunctionNames(importedCodes);
   }

   private static void collectRemoteImportCodes(String source, List<String> codes, Set<String> checkedUrls) {
      if (source == null) return;
      Matcher imports = REMOTE_IMPORT.matcher(source);
      while (imports.find()) {
         String name = imports.group(2) != null ? imports.group(2) : imports.group(3);
         String url = resolveRemoteImport(name);
         if (url == null || !checkedUrls.add(url)) continue;
         try {
            HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "Mappet-Diagnostics/1.0");
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
               try (InputStream input = connection.getInputStream(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                  byte[] buffer = new byte[8192];
                  int total = 0;
                  int read;
                  while ((read = input.read(buffer)) >= 0 && total <= 1024 * 1024) {
                     output.write(buffer, 0, read);
                     total += read;
                  }
                  codes.add(new String(output.toByteArray(), StandardCharsets.UTF_8));
               }
            }
            connection.disconnect();
         } catch (Exception ignored) {
         }
      }
   }

   private static String resolveRemoteImport(String name) {
      if (name == null || name.isEmpty()) return null;
      if (name.startsWith("http://") || name.startsWith("https://")) return name;
      if (name.startsWith("github:")) {
         String value = name.substring("github:".length());
         int first = value.indexOf(':');
         int last = value.lastIndexOf(':');
         if (first <= 0 || last <= first || last >= value.length() - 1) return null;
         String user = value.substring(0, first);
         String repoPath = value.substring(first + 1, last);
         String file = value.substring(last + 1);
         String[] parts = repoPath.split("/", -1);
         if (parts.length == 0 || parts[0].isEmpty()) return null;
         StringBuilder path = new StringBuilder();
         for (int i = 1; i < parts.length; ++i) {
            if (!parts[i].isEmpty()) path.append(parts[i]).append('/');
         }
         path.append(file);
         return "https://raw.githubusercontent.com/" + user + "/" + parts[0] + "/main/" + path;
      }
      if (name.contains("/") || name.contains("\\")) return null;
      return "https://raw.githubusercontent.com/nuk0ro4che/myLib/main/" + name;
   }
}
