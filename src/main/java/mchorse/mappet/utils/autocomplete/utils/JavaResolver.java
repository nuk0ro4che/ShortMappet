package mchorse.mappet.utils.autocomplete.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Vector;
import mchorse.mappet.utils.autocomplete.AutoCompleteConfig;
import net.minecraft.class_310;

public class JavaResolver {
   public static List<AutoCompleteConfig.Suggestion> findMatchingJavaNashorn(String prefix) {
      List<AutoCompleteConfig.Suggestion> result = new ArrayList();
      if (prefix == null) {
         prefix = "";
      }

      String lower = prefix.toLowerCase();

      for(String m : AutoCompleteConfig.JAVA_NASHORN_MEMBERS) {
         if (prefix.isEmpty() || m.toLowerCase().startsWith(lower)) {
            result.add(new AutoCompleteConfig.Suggestion(m, javaMethodReturnHint(m), "java"));
         }
      }

      result.sort((a, b) -> a.methodName.compareToIgnoreCase(b.methodName));
      return result;
   }

   private static String javaMethodReturnHint(String name) {
      if (name == null) {
         return "";
      } else {
         switch (name) {
            case "type":
               return "Class";
            case "from":
               return "Array";
            default:
               return "";
         }
      }
   }

   public static String extractJavaTypePrefix(String line, int cursorOffset) {
      if (line != null && !line.isEmpty() && cursorOffset > 0) {
         String sub = line.substring(0, Math.min(cursorOffset, line.length()));
         if (sub.isEmpty()) {
            return null;
         } else {
            int idx = sub.lastIndexOf("Java.type(\"");
            char quoteChar = '"';
            if (idx < 0) {
               idx = sub.lastIndexOf("Java.type('");
               quoteChar = '\'';
            }

            if (idx < 0) {
               return null;
            } else {
               int start = idx + 11;
               if (start > sub.length()) {
                  return null;
               } else {
                  String inner = sub.substring(start);
                  return inner.indexOf(34) < 0 && inner.indexOf(39) < 0 ? inner : null;
               }
            }
         }
      } else {
         return null;
      }
   }

   public static List<AutoCompleteConfig.Suggestion> findMatchingJavaClasses(String prefix) {
      List<AutoCompleteConfig.Suggestion> result = new ArrayList();
      if (prefix == null) {
         prefix = "";
      }

      LinkedHashSet<String> candidates = new LinkedHashSet();

      try {
         ClassLoader cl = Thread.currentThread().getContextClassLoader();
         collectClassesFromLoader(cl, prefix, candidates);
         ClassLoader mcl = class_310.class.getClassLoader();
         if (mcl != cl) {
            collectClassesFromLoader(mcl, prefix, candidates);
         }

         for(Package pkg : Package.getPackages()) {
            try {
               processCandidateName(pkg.getName() + ".", prefix, candidates);
            } catch (Exception var10) {
            }
         }
      } catch (Exception var11) {
      }

      for(String s : candidates) {
         boolean isPkg = s.endsWith(".");
         String display = isPkg ? s.substring(0, s.length() - 1) : s;
         result.add(new AutoCompleteConfig.Suggestion(display, isPkg ? "package" : "Class", isPkg ? "{}" : "java"));
         if (result.size() >= AutoCompleteConfig.MAX_SUGGESTIONS) {
            break;
         }
      }

      result.sort((a, b) -> {
         boolean ap = "package".equals(a.returnType);
         boolean bp = "package".equals(b.returnType);
         if (ap != bp) {
            return ap ? -1 : 1;
         } else {
            return a.methodName.compareToIgnoreCase(b.methodName);
         }
      });
      return result;
   }

   private static void collectClassesFromLoader(ClassLoader cl, String prefix, LinkedHashSet<String> out) {
      if (cl != null && out.size() <= 300) {
         try {
            Field f = findField(cl.getClass(), "classes");
            f.setAccessible(true);
            Object val = f.get(cl);
            if (val instanceof Vector) {
               for(Object c : (Vector)val) {
                  if (c instanceof Class) {
                     try {
                        processCandidateName(((Class)c).getName(), prefix, out);
                     } catch (Exception var8) {
                     }
                  }
               }
            }
         } catch (Exception var9) {
         }

         ClassLoader parent = cl.getParent();
         if (parent != null && parent != cl) {
            collectClassesFromLoader(parent, prefix, out);
         }

      }
   }

   private static void processCandidateName(String fqn, String prefix, LinkedHashSet<String> out) {
      if (fqn != null && !fqn.startsWith("$") && !fqn.contains("$$")) {
         String name = fqn.replace('$', '.');
         if (name.endsWith(".")) {
            name = name.substring(0, name.length() - 1);
         }

         if (name.toLowerCase().startsWith(prefix.toLowerCase())) {
            String rest = name.substring(prefix.length());
            if (!rest.isEmpty()) {
               int dot = rest.indexOf(46);
               if (dot >= 0) {
                  out.add(prefix + rest.substring(0, dot + 1));
               } else {
                  out.add(name);
               }

            }
         }
      }
   }

   private static Field findField(Class<?> c, String name) throws NoSuchFieldException {
      while(c != null) {
         try {
            return c.getDeclaredField(name);
         } catch (NoSuchFieldException var3) {
            c = c.getSuperclass();
         }
      }

      throw new NoSuchFieldException(name);
   }
}
