package mchorse.mappet.client.gui.scripts.utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mchorse.mappet.utils.autocomplete.AutoCompleteConfig;
import mchorse.mappet.utils.autocomplete.AutoCompleteEngine;
import mchorse.mappet.utils.autocomplete.utils.CompletionHelper;







public final class JavaScriptNavigation
{
    private static final Pattern DECLARATION = Pattern.compile("\\b(?:var|let|const)\\s+([A-Za-z_$][\\w$]*)");
    private static final Pattern FUNCTION = Pattern.compile("\\bfunction\\s+([A-Za-z_$][\\w$]*)\\s*\\(");
    private static final Pattern ASSIGNMENT = Pattern.compile("(?m)(?<![=!<>])\\b([A-Za-z_$][\\w$]*)\\s*=(?!=)");
    private static final Pattern FUNCTION_PARAMETERS = Pattern.compile("\\bfunction\\s*(?:[A-Za-z_$][\\w$]*)?\\s*\\(([^)]*)\\)");
    private static final Pattern UI_BUILDER_ASSIGNMENT = Pattern.compile("\\b([A-Za-z_$][\\w$]*)\\s*=\\s*mappet\\s*\\.\\s*createUI\\s*\\(");
    private static final Pattern UI_CONTEXT_ASSIGNMENT = Pattern.compile("\\b([A-Za-z_$][\\w$]*)\\s*=\\s*[^;{}]*?\\.\\s*getUIContext\\s*\\(");
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_$][\\w$]*");

    private JavaScriptNavigation()
    {}

    public static Target resolve(String source, int offset)
    {
        if (source == null || source.isEmpty())
        {
            return null;
        }

        Token token = findToken(source, offset);
        if (token == null)
        {
            return null;
        }

        Target api = findApiTarget(source, token);
        return api != null ? api : findLocalTarget(source, token);
    }

    private static Target findApiTarget(String source, Token token)
    {
        int dot = previousNonWhitespace(source, token.start - 1);
        if (dot < 0 || source.charAt(dot) != '.')
        {
            return null;
        }

        int lineStart = source.lastIndexOf('\n', token.start - 1) + 1;
        String line = source.substring(lineStart, token.start);
        String[] context = CompletionHelper.extractContext(line + token.name, line.length() + token.name.length());
        if (context == null || !".".equals(context[0]) || !token.name.equals(context[1]))
        {
            return null;
        }

        String chain = context[2];
        if (chain == null || chain.isEmpty())
        {
            return null;
        }

        String owner = getChainHead(chain);
        if (owner != null && isLocalOutsideCurrentScope(source, owner, token.start))
        {
            return null;
        }

        String type = AutoCompleteEngine.resolveChainType(chain, source);
        String uiType = inferUiChainType(chain, source);
        if (uiType != null && (type == null || type.isEmpty() || type.equals("UIComponent") || type.endsWith(".UIComponent")))
        {
            type = uiType;
        }
        List<AutoCompleteConfig.Suggestion> suggestions;
        if (type != null && !type.isEmpty())
        {
            suggestions = AutoCompleteEngine.findMethodsOfClass(type, token.name);
        }
        else
        {
            suggestions = AutoCompleteEngine.findMethodsForKnownVar(chain, token.name, Arrays.asList(source.split("\\n", -1)));
        }

        for (AutoCompleteConfig.Suggestion suggestion : suggestions)
        {
            if (token.name.equals(suggestion.methodName))
            {
                return Target.api(token, suggestion);
            }
        }

        AutoCompleteConfig.Suggestion reflected = findMappetApiMethod(type, token.name);
        return reflected == null ? null : Target.api(token, reflected);
    }

    



    private static boolean isLocalOutsideCurrentScope(String source, String name, int useOffset)
    {
        Token owner = new Token(name, useOffset, useOffset + name.length());
        if (findLocalTarget(source, owner) != null)
        {
            return false;
        }

        return containsName(DECLARATION, source, name) || containsName(FUNCTION, source, name)
            || containsName(ASSIGNMENT, source, name) || containsFunctionParameter(source, name);
    }

    


    private static String inferUiChainType(String chain, String source)
    {
        if (chain == null || chain.isEmpty())
        {
            return null;
        }

        String head = getChainHead(chain);
        if (head != null && isUiContextVariable(head, source))
        {
            return chain.contains(".get(") ? "UIComponent" : "IMappetUIContext";
        }

        if (chain.contains(".button(")) return "UIButtonComponent";
        if (chain.contains(".icon(")) return "UIIconComponent";
        if (chain.contains(".label(")) return "UILabelComponent";
        if (chain.contains(".text(")) return "UITextComponent";
        if (chain.contains(".textbox(")) return "UITextboxComponent";
        if (chain.contains(".textarea(")) return "UITextareaComponent";
        if (chain.contains(".toggle(")) return "UIToggleComponent";
        if (chain.contains(".trackpad(")) return "UITrackpadComponent";
        if (chain.contains(".graphics(")) return "UIGraphicsComponent";
        if (chain.contains(".layout(") || chain.equals("layout")) return "IMappetUIBuilder";

        if (head != null)
        {
            Matcher assignment = UI_BUILDER_ASSIGNMENT.matcher(source);
            while (assignment.find())
            {
                if (head.equals(assignment.group(1)))
                {
                    return "IMappetUIBuilder";
                }
            }
        }

        return null;
    }

    private static boolean isUiContextVariable(String name, String source)
    {
        Matcher assignment = UI_CONTEXT_ASSIGNMENT.matcher(source);
        while (assignment.find())
        {
            if (name.equals(assignment.group(1)))
            {
                return true;
            }
        }
        return false;
    }

    private static String getChainHead(String chain)
    {
        Matcher matcher = IDENTIFIER.matcher(chain);
        return matcher.find() ? matcher.group() : null;
    }

    private static boolean containsName(Pattern pattern, String source, String name)
    {
        Matcher matcher = pattern.matcher(source);
        while (matcher.find())
        {
            if (name.equals(matcher.group(1)))
            {
                return true;
            }
        }

        return false;
    }

    private static boolean containsFunctionParameter(String source, String name)
    {
        Matcher matcher = FUNCTION_PARAMETERS.matcher(source);
        while (matcher.find())
        {
            for (String parameter : matcher.group(1).split(","))
            {
                if (name.equals(parameter.trim()))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private static Target findLocalTarget(String source, Token token)
    {
        int declaration = findNearestBefore(DECLARATION, source, token.name, token.start);
        int function = findNearestBefore(FUNCTION, source, token.name, token.start);
        int assignment = findNearestBefore(ASSIGNMENT, source, token.name, token.start);
        int parameter = findParameterBefore(source, token.name, token.start);
        int target = Math.max(Math.max(declaration, function), Math.max(assignment, parameter));

        return target < 0 ? null : Target.local(token, target, target + token.name.length());
    }

    private static int findNearestBefore(Pattern pattern, String source, String name, int offset)
    {
        int best = -1;
        Matcher matcher = pattern.matcher(source);
        while (matcher.find())
        {
            if (matcher.start(1) >= offset)
            {
                break;
            }
            if (name.equals(matcher.group(1)) && isVisibleFromDeclaration(source, matcher.start(1), offset))
            {
                best = matcher.start(1);
            }
        }

        return best;
    }

    private static int findParameterBefore(String source, String name, int offset)
    {
        int best = -1;
        Matcher matcher = FUNCTION_PARAMETERS.matcher(source);
        while (matcher.find())
        {
            if (matcher.end() >= offset)
            {
                break;
            }

            int body = source.indexOf('{', matcher.end());
            int bodyEnd = body < 0 ? -1 : findMatchingBrace(source, body);
            if (body < 0 || body >= offset || (bodyEnd >= 0 && bodyEnd < offset))
            {
                continue;
            }

            String parameters = matcher.group(1);
            int search = 0;
            for (String parameter : parameters.split(","))
            {
                String trimmed = parameter.trim();
                if (name.equals(trimmed))
                {
                    int local = parameters.indexOf(trimmed, search);
                    if (local >= 0)
                    {
                        best = matcher.start(1) + local;
                    }
                }
                search += parameter.length() + 1;
            }
        }

        return best;
    }

    private static AutoCompleteConfig.Suggestion findMappetApiMethod(String type, String methodName)
    {
        if (type == null || type.isEmpty())
        {
            return null;
        }

        for (String rawType : type.split(","))
        {
            String simpleName = rawType.trim();
            int dot = simpleName.lastIndexOf('.');
            if (dot >= 0)
            {
                simpleName = simpleName.substring(dot + 1);
            }

            






            if (isMappetApiType(simpleName))
            {
                return new AutoCompleteConfig.Suggestion(methodName, "", simpleName);
            }
        }

        return null;
    }

    private static boolean isMappetApiType(String type)
    {
        return type.startsWith("IScript") || type.startsWith("IUI") || type.startsWith("INBT")
            || type.startsWith("IItem") || type.startsWith("IBlock") || type.startsWith("IEntity")
            || type.startsWith("IPlayer") || type.startsWith("IHand") || type.startsWith("ICommand")
            || type.startsWith("UI");
    }

    private static boolean isVisibleFromDeclaration(String source, int declaration, int use)
    {
        int scope = findEnclosingOpenBrace(source, declaration);
        if (scope < 0)
        {
            return true;
        }

        int closing = findMatchingBrace(source, scope);
        return closing < 0 || closing >= use;
    }

    private static int findEnclosingOpenBrace(String source, int position)
    {
        int depth = 0;
        for (int index = position - 1; index >= 0; --index)
        {
            char character = source.charAt(index);
            if (character == '}')
            {
                ++depth;
            }
            else if (character == '{')
            {
                if (depth == 0)
                {
                    return index;
                }
                --depth;
            }
        }

        return -1;
    }

    private static int findMatchingBrace(String source, int opening)
    {
        int depth = 0;
        for (int index = opening; index < source.length(); ++index)
        {
            char character = source.charAt(index);
            if (character == '{')
            {
                ++depth;
            }
            else if (character == '}' && --depth == 0)
            {
                return index;
            }
        }

        return -1;
    }

    private static Token findToken(String source, int offset)
    {
        int safe = Math.max(0, Math.min(offset, source.length()));
        if (safe == source.length() || !isIdentifierPart(source.charAt(safe)))
        {
            --safe;
        }
        if (safe < 0 || !isIdentifierPart(source.charAt(safe)))
        {
            return null;
        }

        int start = safe;
        int end = safe + 1;
        while (start > 0 && isIdentifierPart(source.charAt(start - 1)))
        {
            --start;
        }
        while (end < source.length() && isIdentifierPart(source.charAt(end)))
        {
            ++end;
        }

        return new Token(source.substring(start, end), start, end);
    }

    private static boolean isIdentifierPart(char character)
    {
        return Character.isLetterOrDigit(character) || character == '_' || character == '$';
    }

    private static int previousNonWhitespace(String source, int index)
    {
        while (index >= 0 && Character.isWhitespace(source.charAt(index)))
        {
            --index;
        }
        return index;
    }

    public static final class Target
    {
        public final int start;
        public final int end;
        public final int declarationStart;
        public final int declarationEnd;
        public final AutoCompleteConfig.Suggestion suggestion;

        private Target(int start, int end, int declarationStart, int declarationEnd, AutoCompleteConfig.Suggestion suggestion)
        {
            this.start = start;
            this.end = end;
            this.declarationStart = declarationStart;
            this.declarationEnd = declarationEnd;
            this.suggestion = suggestion;
        }

        public static Target local(Token token, int declarationStart, int declarationEnd)
        {
            return new Target(token.start, token.end, declarationStart, declarationEnd, null);
        }

        public static Target api(Token token, AutoCompleteConfig.Suggestion suggestion)
        {
            return new Target(token.start, token.end, -1, -1, suggestion);
        }

        public boolean isApi()
        {
            return this.suggestion != null;
        }
    }

    private static final class Token
    {
        public final String name;
        public final int start;
        public final int end;

        private Token(String name, int start, int end)
        {
            this.name = name;
            this.start = start;
            this.end = end;
        }
    }
}
