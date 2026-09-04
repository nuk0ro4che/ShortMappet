package mchorse.mappet.network.common.scripts;

public class ScriptSearchResult
{
    public final String script;
    public final int line;
    public final int column;
    public final int length;
    public final String text;

    public ScriptSearchResult(String script, int line, int column, int length, String text)
    {
        this.script = script == null ? "" : script;
        this.line = Math.max(1, line);
        this.column = Math.max(1, column);
        this.length = Math.max(1, length);
        this.text = text == null ? "" : text;
    }

    public String getLabel()
    {
        return this.script + ":" + this.line + ":" + this.column + " — " + this.text;
    }
}
