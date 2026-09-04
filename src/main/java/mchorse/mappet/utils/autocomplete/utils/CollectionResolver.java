package mchorse.mappet.utils.autocomplete.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mchorse.mappet.utils.autocomplete.AutoCompleteConfig;

public class CollectionResolver {
   public static List<AutoCompleteConfig.Suggestion> findMatchingFromCollection(String varName, String prefix, List<String> allLines) {
      List<AutoCompleteConfig.Suggestion> result = new ArrayList();
      if (allLines != null && !allLines.isEmpty()) {
         String fullText = ScopeAnalyzer.joinLines(allLines);
         String lower = prefix != null ? prefix.toLowerCase() : "";
         List<String> arrayItems = extractArrayItems(varName, fullText);
         if (!arrayItems.isEmpty()) {
            for(String item : arrayItems) {
               if (prefix == null || prefix.isEmpty() || item.toLowerCase().startsWith(lower)) {
                  result.add(new AutoCompleteConfig.Suggestion(item, "", "[]"));
               }
            }

            result.sort((a, b) -> a.methodName.compareToIgnoreCase(b.methodName));
            return result;
         } else {
            for(String key : extractObjectKeys(varName, fullText)) {
               if (prefix == null || prefix.isEmpty() || key.toLowerCase().startsWith(lower)) {
                  result.add(new AutoCompleteConfig.Suggestion(key, "", "{}"));
               }
            }

            result.sort((a, b) -> a.methodName.compareToIgnoreCase(b.methodName));
            return result;
         }
      } else {
         return result;
      }
   }

   private static List<String> extractArrayItems(String varName, String fullText) {
      List<String> items = new ArrayList();
      Pattern p = Pattern.compile("(?:var|let|const)\\s+" + Pattern.quote(varName) + "\\s*=\\s*\\[([\\s\\S]*?)\\]", 8);
      Matcher m = p.matcher(fullText);
      if (m.find()) {
         Matcher sm = Pattern.compile("[\"'](\\w+)[\"']").matcher(m.group(1));

         while(sm.find()) {
            items.add(sm.group(1));
         }
      }

      return items;
   }

   private static List<String> extractObjectKeys(String varName, String fullText) {
      List<String> keys = new ArrayList();
      Pattern p = Pattern.compile("(?:var|let|const)\\s+" + Pattern.quote(varName) + "\\s*=\\s*\\{([\\s\\S]*?)\\}", 8);
      Matcher m = p.matcher(fullText);
      if (m.find()) {
         Matcher km = Pattern.compile("(?:^|,)\\s*(\\w+)\\s*:", 8).matcher(m.group(1));

         while(km.find()) {
            keys.add(km.group(1));
         }
      }

      return keys;
   }
}
