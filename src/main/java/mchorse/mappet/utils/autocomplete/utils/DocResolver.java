package mchorse.mappet.utils.autocomplete.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mchorse.mappet.client.gui.scripts.GuiDocumentationOverlayPanel;
import mchorse.mappet.client.gui.scripts.utils.documentation.DocClass;
import mchorse.mappet.client.gui.scripts.utils.documentation.DocMethod;
import mchorse.mappet.client.gui.scripts.utils.documentation.Docs;
import mchorse.mappet.utils.autocomplete.AutoCompleteConfig;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import net.minecraft.class_310;

public class DocResolver {
   private static final Map<String, String> HEAD_TYPES = new HashMap();
   private static final Map<String, String> KNOWN_RETURN_TYPES;
   private static final ThreadLocal<LinkedHashSet<String>> RESOLVING_VARS = ThreadLocal.withInitial(LinkedHashSet::new);

   private static Object resolveDocs() {
      try {
         Method m = GuiDocumentationOverlayPanel.class.getDeclaredMethod("getMergedDocs");
         m.setAccessible(true);
         Object result = m.invoke((Object)null);
         if (result instanceof Docs) {
            return result;
         }
      } catch (Exception var2) {
      }

      return GuiDocumentationOverlayPanel.getDocs();
   }

   public static List<AutoCompleteConfig.Suggestion> findMatchingMethods(String prefix) {
      List<AutoCompleteConfig.Suggestion> result = new ArrayList();
      if (prefix == null) {
         return result;
      } else {
         try {
            Object docs = resolveDocs();
            if (docs == null) {
               return result;
            }

            Field classesField = findField(docs.getClass(), "classes");
            classesField.setAccessible(true);
            List<?> classes = (List)classesField.get(docs);
            if (classes == null) {
               return result;
            }

            String lower = prefix.toLowerCase();
            LinkedHashSet<String> seen = new LinkedHashSet();

            for(Object classObj : classes) {
               DocClass docClass = (DocClass)classObj;
               if (docClass != null) {
                  Field methodsField = findField(docClass.getClass(), "methods");
                  methodsField.setAccessible(true);
                  List<?> methods = (List)methodsField.get(docClass);
                  if (methods != null) {
                     String className = getClassName(docClass);

                     for(Object methodObj : methods) {
                        DocMethod method = (DocMethod)methodObj;
                        String name = getEntryName(method);
                        if (name != null && !name.isEmpty() && (prefix.isEmpty() || name.toLowerCase().startsWith(lower)) && !seen.contains(name)) {
                           seen.add(name);
                           result.add(new AutoCompleteConfig.Suggestion(name, getReturnType(method), className));
                           if (result.size() >= AutoCompleteConfig.MAX_SUGGESTIONS) {
                              break;
                           }
                        }
                     }

                     if (result.size() >= AutoCompleteConfig.MAX_SUGGESTIONS) {
                        break;
                     }
                  }
               }
            }
         } catch (Exception var17) {
         }

         result.sort((a, b) -> a.methodName.compareToIgnoreCase(b.methodName));
         return result;
      }
   }

   public static List<AutoCompleteConfig.Suggestion> findMethodsOfClass(String classNames, String prefix) {
      List<AutoCompleteConfig.Suggestion> result = new ArrayList();
      if (classNames != null && !classNames.isEmpty()) {
         String[] classesArray = classNames.split(",");

         try {
            Object docs = resolveDocs();
            String lower = prefix == null ? "" : prefix.toLowerCase();
            LinkedHashSet<String> seen = new LinkedHashSet();

            for(String className : classesArray) {
               className = simpleName(className.trim());
               if (!className.isEmpty()) {
                  if (docs != null) {
                     Field classesField = findField(docs.getClass(), "classes");
                     classesField.setAccessible(true);
                     List<?> classes = (List)classesField.get(docs);
                     if (classes != null) {
                        LinkedHashSet<String> targetClasses = new LinkedHashSet();
                        targetClasses.add(className);
                        collectSuperTypes(className, docs, targetClasses);

                        for(Object classObj : classes) {
                           DocClass dc = (DocClass)classObj;
                           if (dc != null) {
                              String cn = getClassName(dc);
                              if (matchesTypeName(targetClasses, cn)) {
                                 Field methodsField = findField(dc.getClass(), "methods");
                                 methodsField.setAccessible(true);
                                 List<?> methods = (List)methodsField.get(dc);
                                 if (methods != null) {
                                    for(Object mo : methods) {
                                       DocMethod dm = (DocMethod)mo;
                                       String name = getEntryName(dm);
                                       if (name != null && !name.isEmpty() && (lower.isEmpty() || name.toLowerCase().startsWith(lower)) && !seen.contains(name)) {
                                          seen.add(name);
                                          result.add(new AutoCompleteConfig.Suggestion(name, getReturnType(dm), cn));
                                          if (result.size() >= AutoCompleteConfig.MAX_SUGGESTIONS) {
                                             break;
                                          }
                                       }
                                    }

                                    if (result.size() >= AutoCompleteConfig.MAX_SUGGESTIONS) {
                                       break;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (result.size() < AutoCompleteConfig.MAX_SUGGESTIONS) {
                     try {
                        Class<?> clazz = Class.forName(className);

                        for(Method m : clazz.getMethods()) {
                           String name = m.getName();
                           if ((lower.isEmpty() || name.toLowerCase().startsWith(lower)) && !seen.contains(name)) {
                              seen.add(name);
                              String returnType = m.getReturnType().getSimpleName();
                              result.add(new AutoCompleteConfig.Suggestion(name, returnType, className));
                              if (result.size() >= AutoCompleteConfig.MAX_SUGGESTIONS) {
                                 break;
                              }
                           }
                        }
                     } catch (ClassNotFoundException var24) {
                     }
                  }
               }
            }
         } catch (Exception var25) {
         }

         result.sort((a, b) -> a.methodName.compareToIgnoreCase(b.methodName));
         return result;
      } else {
         return result;
      }
   }

   public static List<String> collectSubTypes(String className) {
      LinkedHashSet<String> result = new LinkedHashSet();
      if (className != null && !className.isEmpty()) {
         try {
            Object docs = resolveDocs();
            if (docs == null) {
               return new ArrayList(result);
            }

            collectSubTypes(className, docs, result);
         } catch (Exception var3) {
         }

         return new ArrayList(result);
      } else {
         return new ArrayList(result);
      }
   }

   private static void collectSubTypes(String className, Object docs, LinkedHashSet<String> out) {
      try {
         Field classesField = findField(docs.getClass(), "classes");
         classesField.setAccessible(true);
         List<?> classes = (List)classesField.get(docs);
         if (classes == null) {
            return;
         }

         for(Object classObj : classes) {
            DocClass dc = (DocClass)classObj;
            if (dc != null) {
               String cn = getClassName(dc);
               if (!cn.equals(className) && !out.contains(cn) && hasTypeReference(dc, className)) {
                  out.add(cn);
                  collectSubTypes(cn, docs, out);
               }
            }
         }
      } catch (Exception var9) {
      }

   }

   private static void collectSuperTypes(String className, Object docs, LinkedHashSet<String> out) {
      try {
         Field classesField = findField(docs.getClass(), "classes");
         classesField.setAccessible(true);
         List<?> classes = (List)classesField.get(docs);
         if (classes == null) {
            return;
         }

         for(Object classObj : classes) {
            DocClass dc = (DocClass)classObj;
            if (dc != null) {
               String cn = getClassName(dc);
               if (className.equals(cn)) {
                  for(String fieldName : new String[]{"superclass", "parent", "interfaces", "extend"}) {
                     try {
                        Field f = findField(dc.getClass(), fieldName);
                        f.setAccessible(true);
                        Object val = f.get(dc);
                        if (val != null) {
                           if (val instanceof String) {
                              String superName = simpleName((String)val);
                              if (!superName.isEmpty() && !out.contains(superName)) {
                                 out.add(superName);
                                 collectSuperTypes(superName, docs, out);
                              }
                           } else if (val instanceof List) {
                              for(Object item : (List)val) {
                                 String superName = simpleName(item.toString());
                                 if (!superName.isEmpty() && !out.contains(superName)) {
                                    out.add(superName);
                                    collectSuperTypes(superName, docs, out);
                                 }
                              }
                           }
                        }
                     } catch (Exception var18) {
                     }
                  }
               }
            }
         }
      } catch (Exception var19) {
      }

   }

   private static String getClassName(DocClass dc) {
      try {
         Field f = findField(dc.getClass(), "name");
         f.setAccessible(true);
         Object n = f.get(dc);
         return n != null ? simpleName(n.toString()) : "";
      } catch (Exception var3) {
         return "";
      }
   }

   public static String resolveVarReturnClass(String varName, String fullText) {
      Pattern p = Pattern.compile("(?:var|let|const)\\s+" + Pattern.quote(varName) + "\\s*=(?!=)\\s*([\\w.()]+)\\.(\\w+)\\s*\\(", 8);
      Matcher m = p.matcher(fullText);
      String bestMatch = null;

      while(m.find()) {
         String ownerChain = m.group(1);
         String methodName = m.group(2);
         String ownerType = null;
         if (ownerChain.contains(".")) {
            ownerType = resolveChainTypeInternal(ownerChain, fullText);
         } else {
            ownerType = resolveHeadType(ownerChain, fullText);
         }

         String rt = null;
         if (ownerType != null && !ownerType.isEmpty()) {
            rt = findReturnTypeByMethodInClass(ownerType, methodName);
         }

         if (rt == null || rt.isEmpty()) {
            rt = findReturnTypeByMethodName(methodName);
         }

         if (rt != null && !rt.isEmpty()) {
            bestMatch = rt;
            break;
         }
      }

      if (bestMatch != null) {
         return bestMatch;
      } else {
         Pattern p2 = Pattern.compile("(?:var|let|const)\\s+" + Pattern.quote(varName) + "\\s*=(?!=)\\s*([\\w.]+(?:\\([^)]*\\))?)", 8);
         Matcher m2 = p2.matcher(fullText);
         if (m2.find()) {
            String expression = m2.group(1);
            String ownerType = expression.contains(".")
               ? resolveChainTypeInternal(expression, fullText)
               : resolveHeadType(expression, fullText);
            String propOrMethod = expression.substring(expression.lastIndexOf('.') + 1);
            int paren = propOrMethod.indexOf('(');
            if (paren >= 0) {
               propOrMethod = propOrMethod.substring(0, paren);
            }
            String rt = findReturnTypeByMethodInClass(ownerType, propOrMethod);
            if (rt != null && !rt.isEmpty()) {
               return rt;
            }

            rt = findReturnTypeByMethodName(propOrMethod);
            if (rt != null && !rt.isEmpty()) {
               return rt;
            }
         }

         return null;
      }
   }

   private static String resolveChainTypeInternal(String chain, String fullText) {
      if (chain != null && !chain.isEmpty()) {
         List<String> segments = new ArrayList();
         int depth = 0;
         int start = 0;

         for(int i = 0; i < chain.length(); ++i) {
            char c = chain.charAt(i);
            if (c == '(') {
               ++depth;
            } else if (c == ')') {
               --depth;
            } else if (c == '.' && depth == 0) {
               String seg = chain.substring(start, i).trim();
               if (!seg.isEmpty()) {
                  segments.add(seg);
               }

               start = i + 1;
            }
         }

         String last = chain.substring(start).trim();
         if (!last.isEmpty()) {
            segments.add(last);
         }

         if (segments.isEmpty()) {
            return null;
         } else {
            String head = (String)segments.get(0);
            if (head.endsWith(")")) {
               head = head.substring(0, head.indexOf(40));
            }

            String currentType = resolveHeadType(head, fullText);
            if (currentType != null && !currentType.isEmpty()) {
               for(int i = 1; i < segments.size(); ++i) {
                  String seg = (String)segments.get(i);
                  int parenIdx = seg.indexOf(40);
                  String methodName = parenIdx >= 0 ? seg.substring(0, parenIdx) : seg;
                  if (methodName.isEmpty()) {
                     return null;
                  }

                  String next = findReturnTypeByMethodInClass(currentType, methodName);
                  if (next == null || next.isEmpty()) {
                     String getter = resolveAliasMethodName(methodName);
                     if (!getter.equals(methodName)) {
                        next = findReturnTypeByMethodInClass(currentType, getter);
                     }
                  }

                  if (next == null || next.isEmpty()) {
                     next = findReturnTypeByMethodName(resolveAliasMethodName(methodName));
                  }

                  if (next == null || next.isEmpty()) {
                     return null;
                  }

                  currentType = next;
               }

               return currentType;
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private static String resolveHeadType(String varName, String fullText) {
      if (varName == null || varName.isEmpty()) {
         return null;
      }
      String known = (String)HEAD_TYPES.get(varName);
      if (known != null) {
         return known;
      }
      LinkedHashSet<String> resolving = RESOLVING_VARS.get();
      if (!resolving.add(varName)) {
         return null;
      }
      try {
         return resolveVarReturnClass(varName, fullText);
      } finally {
         resolving.remove(varName);
      }
   }

   public static String resolveVarClassFromJavaType(String varName, String fullText) {
      Pattern p = Pattern.compile("(?:var|let|const)\\s+" + Pattern.quote(varName) + "\\s*=\\s*Java\\.type\\(\"([^\"]+)\"\\)", 8);
      Matcher m = p.matcher(fullText);
      if (m.find()) {
         return m.group(1);
      } else {
         p = Pattern.compile("(?:var|let|const)\\s+" + Pattern.quote(varName) + "\\s*=\\s*Java\\.type\\('([^']+)'\\)", 8);
         m = p.matcher(fullText);
         return m.find() ? m.group(1) : null;
      }
   }

   public static String findReturnTypeByMethodInClass(String className, String methodName) {
      if (className != null && !className.isEmpty() && methodName != null && !methodName.isEmpty()) {
         methodName = resolveAliasMethodName(methodName);
         String[] classes = className.split(",");

         try {
            Object docs = resolveDocs();
            if (docs != null) {
               Field classesField = findField(docs.getClass(), "classes");
               classesField.setAccessible(true);
               List<?> classesList = (List)classesField.get(docs);
               if (classesList != null) {
                  for(String singleClass : classes) {
                     singleClass = singleClass.trim();
                     LinkedHashSet<String> targets = new LinkedHashSet();
                     targets.add(singleClass);
                     collectSuperTypes(singleClass, docs, targets);
                     String rt = findReturnTypeInTargets(classesList, targets, methodName);
                     if (rt != null && !rt.isEmpty()) {
                        return rt;
                     }

                     LinkedHashSet<String> subTargets = new LinkedHashSet(collectSubTypes(singleClass));
                     if (!subTargets.isEmpty()) {
                        rt = findReturnTypeInTargets(classesList, subTargets, methodName);
                        if (rt != null && !rt.isEmpty()) {
                           return rt;
                        }
                     }
                  }
               }
            }
         } catch (Exception var13) {
         }

         return (String)KNOWN_RETURN_TYPES.get(methodName);
      } else {
         return null;
      }
   }

   private static String findReturnTypeInTargets(List<?> classes, Set<String> targets, String methodName) {
      try {
         for(Object classObj : classes) {
            DocClass dc = (DocClass)classObj;
            if (dc != null) {
               String cn = getClassName(dc);
               if (targets.contains(cn)) {
                  Field methodsField = findField(dc.getClass(), "methods");
                  methodsField.setAccessible(true);
                  List<?> methods = (List)methodsField.get(dc);
                  if (methods != null) {
                     for(Object mo : methods) {
                        DocMethod dm = (DocMethod)mo;
                        if (methodName.equals(getEntryName(dm))) {
                           String rt = getReturnType(dm);
                           if (rt != null && !rt.isEmpty()) {
                              return rt;
                           }
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception var13) {
      }

      return null;
   }

   public static String findReturnTypeByMethodName(String methodName) {
      try {
         Object docs = resolveDocs();
         if (docs != null) {
            Field classesField = findField(docs.getClass(), "classes");
            classesField.setAccessible(true);
            List<?> classes = (List)classesField.get(docs);
            if (classes != null) {
               for(Object classObj : classes) {
                  DocClass dc = (DocClass)classObj;
                  if (dc != null) {
                     Field methodsField = findField(dc.getClass(), "methods");
                     methodsField.setAccessible(true);
                     List<?> methods = (List)methodsField.get(dc);
                     if (methods != null) {
                        for(Object mo : methods) {
                           DocMethod dm = (DocMethod)mo;
                           if (methodName.equals(getEntryName(dm))) {
                              String rt = getReturnType(dm);
                              if (rt != null && !rt.isEmpty()) {
                                 return rt;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception var13) {
      }

      String fb = (String)KNOWN_RETURN_TYPES.get(methodName);
      return fb != null ? fb : "";
   }

   public static GuiScrollElement buildDocPanel(class_310 mc, AutoCompleteConfig.Suggestion s) {
      if (s != null && mc != null) {
         String type = s.className != null ? s.className : "";
         if (!type.equals("kw") && !type.equals("var") && !type.equals("[]") && !type.equals("{}") && !type.equals("fn") && !type.equals("value")) {
            Object dm = findDocMethod(type, s.methodName);
            if (dm == null) {
               return null;
            } else {
               GuiScrollElement panel = new GuiScrollElement(mc) {
                  protected void preDraw(GuiContext ctx) {
                     GuiDraw.drawRect(this.area.x - 1, this.area.y - 1, this.area.x + this.area.w + 1, this.area.y + this.area.h + 1, -15064525);
                     GuiDraw.drawRect(this.area.x, this.area.y, this.area.x + this.area.w, this.area.y + this.area.h, -16777216);
                  }
               };
               panel.flex().column(4).vertical().stretch().scroll().padding(8);
               return panel;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static void populateDocPanel(class_310 mc, AutoCompleteConfig.Suggestion s, GuiScrollElement panel) {
      if (s != null && mc != null && panel != null) {
         String type = s.className != null ? s.className : "";
         if (!type.equals("kw") && !type.equals("var") && !type.equals("param") && !type.equals("[]") && !type.equals("{}") && !type.equals("fn") && !type.equals("java") && !type.equals("value")) {
            Object dm = findDocMethod(type, s.methodName);
            if (dm != null) {
               try {
                  Method fillIn = findMethodUp(dm.getClass(), "fillIn");
                  if (fillIn != null) {
                     fillIn.setAccessible(true);
                     fillIn.invoke(dm, mc, panel);
                  }
               } catch (Exception var6) {
               }

               panel.resize();
            }
         }
      }
   }

   private static Object findDocMethod(String className, String methodName) {
      try {
         Object docs = resolveDocs();
         if (docs == null) {
            return null;
         }

         Field classesField = findField(docs.getClass(), "classes");
         classesField.setAccessible(true);
         List<?> classes = (List)classesField.get(docs);
         if (classes == null) {
            return null;
         }

         for(int pass = 0; pass < 2; ++pass) {
            for(Object classObj : classes) {
               DocClass dc = (DocClass)classObj;
               if (dc != null) {
                  if (pass == 0 && !className.isEmpty()) {
                     String cn = getClassName(dc);
                     if (!className.equals(cn)) {
                        continue;
                     }
                  }

                  Field methodsField = findField(dc.getClass(), "methods");
                  methodsField.setAccessible(true);
                  List<?> methods = (List)methodsField.get(dc);
                  if (methods != null) {
                     for(Object mo : methods) {
                        DocMethod dm = (DocMethod)mo;
                        if (methodName.equals(getEntryName(dm))) {
                           return dm;
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception var14) {
      }

      return null;
   }

   private static Method findMethodUp(Class<?> cls, String name) {
      while(cls != null) {
         for(Method m : cls.getDeclaredMethods()) {
            if (m.getName().equals(name)) {
               return m;
            }
         }

         cls = cls.getSuperclass();
      }

      return null;
   }

   public static List<String> getDocForSuggestion(AutoCompleteConfig.Suggestion s) {
      if (s == null) {
         return null;
      } else {
         String type = s.className != null ? s.className : "";
         if (!type.equals("kw") && !type.equals("var") && !type.equals("[]") && !type.equals("{}") && !type.equals("fn") && !type.equals("value")) {
            try {
               Object docs = resolveDocs();
               if (docs == null) {
                  return null;
               }

               Field classesField = findField(docs.getClass(), "classes");
               classesField.setAccessible(true);
               List<?> classes = (List)classesField.get(docs);
               if (classes == null) {
                  return null;
               }

               label105:
               for(int pass = 0; pass < 2; ++pass) {
                  Iterator var6 = classes.iterator();

                  while(true) {
                     DocClass dc;
                     while(true) {
                        if (!var6.hasNext()) {
                           continue label105;
                        }

                        Object classObj = var6.next();
                        dc = (DocClass)classObj;
                        if (dc != null) {
                           if (pass != 0 || type.isEmpty()) {
                              break;
                           }

                           String cn = getClassName(dc);
                           if (type.equals(cn)) {
                              break;
                           }
                        }
                     }

                     Field methodsField = findField(dc.getClass(), "methods");
                     methodsField.setAccessible(true);
                     List<?> methods = (List)methodsField.get(dc);
                     if (methods != null) {
                        for(Object mo : methods) {
                           DocMethod dm = (DocMethod)mo;
                           String name = getEntryName(dm);
                           if (s.methodName.equals(name)) {
                              String rawDoc = "";

                              try {
                                 Field fd = findField(dm.getClass(), "doc");
                                 fd.setAccessible(true);
                                 Object v = fd.get(dm);
                                 if (v != null) {
                                    rawDoc = v.toString();
                                 }
                              } catch (Exception var20) {
                              }

                              List<Object> args = new ArrayList();

                              try {
                                 Field fa = findField(dm.getClass(), "arguments");
                                 fa.setAccessible(true);
                                 Object v = fa.get(dm);
                                 if (v instanceof List) {
                                    args = (List)v;
                                 }
                              } catch (Exception var19) {
                              }

                              String retType = getReturnType(dm);
                              return buildTooltip(type, s.methodName, retType, args, rawDoc);
                           }
                        }
                     }
                  }
               }
            } catch (Exception var21) {
            }

            return null;
         } else {
            return null;
         }
      }
   }

   private static List<String> buildTooltip(String className, String methodName, String returnType, List<Object> args, String rawDoc) {
      List<String> out = new ArrayList();
      StringBuilder sig = new StringBuilder();
      sig.append("§b").append(className).append("§r.§f").append(methodName).append("§r(");

      for(int i = 0; i < args.size(); ++i) {
         if (i > 0) {
            sig.append("§r, ");
         }

         String argType = getArgField(args.get(i), "type");
         String argName = getArgField(args.get(i), "name");
         sig.append("§6").append(simpleName(argType)).append(" §f").append(argName);
      }

      sig.append("§r)");
      if (!returnType.isEmpty()) {
         sig.append(" §7: §a").append(returnType);
      }

      out.add(sig.toString());
      int preStart = rawDoc.indexOf("<pre>");
      String descPart = preStart >= 0 ? rawDoc.substring(0, preStart) : rawDoc;
      String codePart = preStart >= 0 ? rawDoc.substring(preStart) : "";
      String desc = cleanHtml(descPart).trim();
      if (!desc.isEmpty()) {
         out.add("");

         for(String line : wordWrap(desc, 34)) {
            out.add("§7" + line);
         }
      }

      boolean hasArgDocs = false;

      for(Object arg : args) {
         String argDoc = getArgField(arg, "doc");
         if (!argDoc.isEmpty()) {
            hasArgDocs = true;
            break;
         }
      }

      if (hasArgDocs) {
         out.add("");
         out.add("§eArguments:");

         for(Object arg : args) {
            String argType = simpleName(getArgField(arg, "type"));
            String argName = getArgField(arg, "name");
            String argDoc = cleanHtml(getArgField(arg, "doc")).trim();
            if (argDoc.isEmpty()) {
               out.add("  §6" + argType + " §f" + argName);
            } else {
               out.add("  §6" + argType + " §f" + argName + " §7— " + argDoc);
            }
         }
      }

      Pattern prePattern = Pattern.compile("<pre>\\s*\\{@code\\s*([\\s\\S]*?)\\}\\s*</pre>", 2);
      Matcher preMatcher = prePattern.matcher(codePart);
      boolean firstCode = true;

      while(preMatcher.find()) {
         if (firstCode) {
            out.add("");
            out.add("§eExample:");
            firstCode = false;
         }

         String code = dedent(preMatcher.group(1));

         for(String cl : code.split("\n")) {
            out.add("§7" + cl.replaceAll("\\s+$", ""));
         }
      }

      return out;
   }

   private static boolean hasTypeReference(DocClass dc, String className) {
      for(String fieldName : new String[]{"superclass", "parent", "interfaces", "extend"}) {
         try {
            Field f = findField(dc.getClass(), fieldName);
            f.setAccessible(true);
            Object val = f.get(dc);
            if (val != null) {
               if (val instanceof String) {
                  if (className.equals(simpleName(val.toString()))) {
                     return true;
                  }
               } else if (val instanceof List) {
                  for(Object item : (List)val) {
                     if (item != null && className.equals(simpleName(item.toString()))) {
                        return true;
                     }
                  }
               }
            }
         } catch (Exception var10) {
         }
      }

      return false;
   }

   private static String resolveAliasMethodName(String name) {
      if (name != null && !name.isEmpty()) {
         for(String[] alias : AutoCompleteConfig.PROPERTY_ALIASES) {
            if (alias.length >= 2 && name.equals(alias[0])) {
               return alias[1];
            }
         }

         return name;
      } else {
         return name;
      }
   }

   private static boolean matchesTypeName(Set<String> targets, String actual) {
      if (actual == null || actual.isEmpty()) {
         return false;
      }

      for (String target : targets) {
         if (target != null && (target.equals(actual) || simpleName(target).equals(simpleName(actual)))) {
            return true;
         }
      }

      return false;
   }

   private static String simpleName(String fqn) {
      if (fqn == null) {
         return "";
      } else {
         int dot = fqn.lastIndexOf(46);
         return dot >= 0 ? fqn.substring(dot + 1) : fqn;
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

   private static String getEntryName(Object obj) {
      try {
         Field f = findField(obj.getClass(), "name");
         f.setAccessible(true);
         Object v = f.get(obj);
         return v != null ? v.toString() : "";
      } catch (Exception var3) {
         return "";
      }
   }

   private static String getReturnType(Object obj) {
      try {
         Field f = findField(obj.getClass(), "returnType");
         f.setAccessible(true);
         Object v = f.get(obj);
         return v != null ? simpleName(v.toString()) : "";
      } catch (Exception var5) {
         try {
            Field f = findField(obj.getClass(), "type");
            f.setAccessible(true);
            Object v = f.get(obj);
            return v != null ? simpleName(v.toString()) : "";
         } catch (Exception var4) {
            return "";
         }
      }
   }

   private static String getArgField(Object arg, String fieldName) {
      try {
         Field f = findField(arg.getClass(), fieldName);
         f.setAccessible(true);
         Object v = f.get(arg);
         return v != null ? v.toString() : "";
      } catch (Exception var4) {
         return "";
      }
   }

   private static String cleanHtml(String html) {
      return html == null ? "" : html.replaceAll("<[^>]*>", "").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
   }

   private static List<String> wordWrap(String text, int width) {
      List<String> bits = new ArrayList();
      String[] words = text.split("\\s+");
      StringBuilder line = new StringBuilder();

      for(String w : words) {
         if (line.length() + w.length() + 1 > width) {
            bits.add(line.toString());
            line = new StringBuilder(w);
         } else {
            if (line.length() > 0) {
               line.append(" ");
            }

            line.append(w);
         }
      }

      if (line.length() > 0) {
         bits.add(line.toString());
      }

      return bits;
   }

   private static String dedent(String text) {
      if (text == null) {
         return "";
      } else {
         String[] lines = text.split("\n");
         int minIndent = Integer.MAX_VALUE;

         for(String l : lines) {
            if (!l.trim().isEmpty()) {
               int indent;
               for(indent = 0; indent < l.length() && Character.isWhitespace(l.charAt(indent)); ++indent) {
               }

               if (indent < minIndent) {
                  minIndent = indent;
               }
            }
         }

         if (minIndent == Integer.MAX_VALUE) {
            minIndent = 0;
         }

         StringBuilder sb = new StringBuilder();

         for(String l : lines) {
            if (l.length() >= minIndent) {
               sb.append(l.substring(minIndent)).append("\n");
            } else {
               sb.append("\n");
            }
         }

         return sb.toString();
      }
   }

   static {
      HEAD_TYPES.put("c", "IScriptEvent");
      HEAD_TYPES.put("event", "IScriptEvent");
      HEAD_TYPES.put("mappet", "IScriptFactory");
      HEAD_TYPES.put("factory", "IScriptFactory");
      KNOWN_RETURN_TYPES = new HashMap();
      KNOWN_RETURN_TYPES.put("createUI", "IMappetUIBuilder");
      KNOWN_RETURN_TYPES.put("createUIFromFile", "IMappetUIBuilder");
      KNOWN_RETURN_TYPES.put("createForm", "IMappetForm");
      KNOWN_RETURN_TYPES.put("createNBT", "INBTCompound");
      KNOWN_RETURN_TYPES.put("createNBTList", "INBTList");
      KNOWN_RETURN_TYPES.put("createMorph", "AbstractMorph");
      KNOWN_RETURN_TYPES.put("createLocalMorph", "ILocalMorph");
      KNOWN_RETURN_TYPES.put("createItemStack", "IScriptItemStack");
      KNOWN_RETURN_TYPES.put("getEntity", "IScriptEntity");
      KNOWN_RETURN_TYPES.put("getNPC", "IScriptNpc");
      KNOWN_RETURN_TYPES.put("getSubject", "IScriptPlayer,IScriptEntity,IScriptNpc");
      KNOWN_RETURN_TYPES.put("getObject", "IScriptEntity");
      KNOWN_RETURN_TYPES.put("getPlayer", "IScriptPlayer,IScriptEntity");
      KNOWN_RETURN_TYPES.put("getWorld", "IScriptWorld");
      KNOWN_RETURN_TYPES.put("getServer", "IScriptServer");
      KNOWN_RETURN_TYPES.put("getValues", "Map");
      KNOWN_RETURN_TYPES.put("getUIContext", "IMappetUIContext");
      KNOWN_RETURN_TYPES.put("getInventory", "IScriptInventory");
      KNOWN_RETURN_TYPES.put("getStates", "IMappetStates");
      KNOWN_RETURN_TYPES.put("getQuests", "IMappetQuests");
      KNOWN_RETURN_TYPES.put("getMorph", "AbstractMorph");
      KNOWN_RETURN_TYPES.put("getMount", "IScriptEntity");
      KNOWN_RETURN_TYPES.put("getTarget", "IScriptEntity");
      KNOWN_RETURN_TYPES.put("getMainItem", "IScriptItemStack");
      KNOWN_RETURN_TYPES.put("getOffItem", "IScriptItemStack");
      KNOWN_RETURN_TYPES.put("getFullData", "INBTCompound");
      KNOWN_RETURN_TYPES.put("getEntityData", "INBTCompound");
      KNOWN_RETURN_TYPES.put("getPosition", "ScriptVector");
      KNOWN_RETURN_TYPES.put("getMotion", "ScriptVector");
      KNOWN_RETURN_TYPES.put("getRotations", "ScriptVector");
      KNOWN_RETURN_TYPES.put("getLook", "ScriptVector");
      KNOWN_RETURN_TYPES.put("getSettings", "IGameSettings");
      KNOWN_RETURN_TYPES.put("getHand", "IHandSettings");
      KNOWN_RETURN_TYPES.put("getBlock", "IScriptBlockState");
      KNOWN_RETURN_TYPES.put("getTileEntity", "IScriptTileEntity");
      KNOWN_RETURN_TYPES.put("getRayTrace", "IScriptRayTrace");
      KNOWN_RETURN_TYPES.put("rayTrace", "IScriptRayTrace");
      KNOWN_RETURN_TYPES.put("getAllPlayers", "List");
      KNOWN_RETURN_TYPES.put("layout", "IMappetUIBuilder");
      KNOWN_RETURN_TYPES.put("getManagedSound", "IScriptManagedSound");
      KNOWN_RETURN_TYPES.put("playManagedSound", "IScriptManagedSound");
      KNOWN_RETURN_TYPES.put("playManagedStaticSound", "IScriptManagedSound");
      KNOWN_RETURN_TYPES.put("getCurrent", "UIComponent");
   }
}
