public class Token
{
    final public TokenType tokenType;
    final public String value;
    final public int startIndex;
    final public int endIndex;

    public Token(TokenType _tokenType, String _value, int start, int end)
    {
        tokenType = _tokenType;
        value = _value;
        startIndex = start;
        endIndex = end;
    }

    public int GetStartIndex()
    {
        return startIndex;
    }

    public int GetEndIndex()
    {
        return endIndex;
    }

    public String toString()
    {
        return "<"+tokenType+"> - <"+value+">";
    }
}
