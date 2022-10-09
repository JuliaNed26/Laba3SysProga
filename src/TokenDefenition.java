import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenDefenition
{
    final String regularExpression;
    final TokenType tokenType;

    public TokenDefenition(String regex, TokenType type)
    {
        regularExpression = regex;
        tokenType = type;
    }

    public List<Token> FindMatches(String codeLine)
    {
        List<Token> allMatches = new ArrayList<Token>();
        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher(codeLine);
        while(matcher.find())
        {
            allMatches.add(new Token(tokenType, matcher.group(), matcher.start(), matcher.end()));
        }
        return  allMatches;
    }
}
