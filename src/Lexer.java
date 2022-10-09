import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Lexer {

    private List<TokenDefenition> _tokenDefenitions;

    public Lexer()
    {
        _tokenDefenitions = new ArrayList<TokenDefenition>();
        FillAllTokenDefenitions();
    }

    public List<Token> Tokenize(String filePath)
    {
        List<Token> tokenizedProgram = new ArrayList<Token>();

        BufferedReader reader;
        try
        {
            reader = new BufferedReader(new FileReader(filePath));
            String codeLine = reader.readLine();
            while (codeLine != null)
            {
                var tokenTypesForLexemas = GetTokenTypesForLexemas(codeLine);
                int lastEndIndex = -1;
                for(var tokensForLexema : tokenTypesForLexemas.entrySet())
                {
                    Token rightToken = null;
                    for(var token : tokensForLexema.getValue())
                    {
                        if (lastEndIndex >= token.endIndex)
                            continue;
                        if(rightToken == null || token.endIndex > rightToken.endIndex
                                || (token.endIndex == rightToken.endIndex && token.tokenType.compareTo(rightToken.tokenType) < 0))
                        {
                            rightToken = token;
                        }
                    }
                    if (rightToken != null)
                    {
                        lastEndIndex = rightToken.endIndex;
                        tokenizedProgram.add(rightToken);
                    }
                }
                codeLine = reader.readLine();
            }
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return tokenizedProgram;
    }
    private Map<Integer,List<Token>> GetTokenTypesForLexemas(String line)
    {
        var lineMatches = new ArrayList<Token>();
        for (TokenDefenition tokenDefinition : _tokenDefenitions)
        {
            lineMatches.addAll(tokenDefinition.FindMatches(line));
        }
        Map<Integer,List<Token>> tokensByStartIndex = new TreeMap<Integer,List<Token>>();
        tokensByStartIndex.putAll(lineMatches.stream().collect(Collectors.groupingBy(Token:: GetStartIndex)));
        return  tokensByStartIndex;
    }

    private void FillAllTokenDefenitions()
    {
        _tokenDefenitions.add(new TokenDefenition(new String("^[{$](SAFEFPUEXCEPTIONS|IMPLICITEXEPTIONS|WEAKPACKAGEUNIT|VARSTRINGCHECKS|MAXFPUREGISTERS|"+
        "EXTENDEDSYNTAX|PASCALMAINNAME|OVERFLOWCHECKS|MINFPCONSTPREC|WRITEABLECONST|VARPROPSETTER|FRAMEWORKPATH|W|STACKFRAMES|TYPEDADDDRESS|LINKFRAMEWORK|"+
        "REFERENCEINFOMINSTACKSIZE|MAXSTACKSIZE|LOCALSYMBOLS|STRINGCHECKS|OPTIMIZATION|OBJECTCHECKS|CHECKPOINTER|POINTERMATH|OPENSTRINGS|LIBRARYPATH|INCLUDEPATH|"+
                "DESCRIPTION|SCOPEDENUMS|RANGECHECKS|PACKRECORDS|MINEMUMSIZE|LONGSTRINGS|EXTERNALSYM|EXTENDEDSYM|THREADNAME|SETPEFLAGS|SCREENNAME|OBJECTPATH|MODESWITCH|SATURATION|"+
                "IEEEERRORS|INTERFACES|COPERATORS|BITPACKING|ASSERTIONS|SMARTLINK|LIBSUFFIX|LIBPREFIX|IMAGEBASE|EXTENSION|DEBUGINFO|COPYRIGHT|LIBEXPORT|ENDREGION|CODEALIGN|"+
                "UNITPATH|SYSCALLS|CODEPAGE|WARNINGS|RESOURSE|PACKENUM|NODEFINE|TYPEINFO|BOOLEVAL|VERSION|PROFILE|APPTYPE|APPNAME|WARNING|MESSAGE|LINKLIB|INCLUDE|IOCHECK|HPPEMIT|"+
                "FPUTYPE|DEFINEC|CALLING|ASMMODE|MEMORY|UNDEFC|REGION|PACKET|INLINE|ERRORC|ELSEIF|DEFINE|APPID|UNDEF|NOTES|MACRO|IFOPT|IFNEF|IFDEF|HINTS|FATAL|ERROR|ENDIF|ELIFC|"+
                "ELSEC|ALIGN|MODE|WARN|WAIT|STOP|SETC|PUSH|NOTE|LINK|INFO|HINT|GOTO|ENDC|ELSE|PIC|POP|MMX|IFC|Z4|Z2|Z1|OV|IF|A8|A4|A2|A1|Y|X|P|O|N|M|L|G|E|D|V|T|S|R|R|Q|Z|M|L|"+
                "J|I|I|H|F|C|B|A)}$"), TokenType.CompilerDirective));

            _tokenDefenitions.add(new TokenDefenition("^[\\(\\*](.*)[\\*\\)]$", TokenType.Comment));

            _tokenDefenitions.add(new TokenDefenition("(implementation|resourcestring|initialization|unimplemented|saveregisters|alias|"+
            "dispinterface|nostackframe|experimental|finalization|constructor|reintroduce|reintroduce|destructor|specialize|oldfpccall|implements|enumerator|"+
            "deprecated|unaligned|softfloat|published|protected|otherwise|nodefault|interrupt|forward|continue|bitpacked|assembler|threadvar|procedure|"+
            "interface|inherited|function|absolute|safecall|register|platform|override|overload|noreturn|external|abstract|absolute|property|operator|boolean|"+
            "virtual|varargs|stdcall|private|message|iocheck|generic|dynamic|default|cppdecl|library|finally|exports|program|integer|downto|readln|winapi|strict|"+
            "stored|static|result|public|pascal|helper|export|packed|inline|except|string|repeat|record|packed|object|inline|const|begin|array|write|local|index|"+
            "far16|cdecl|break|raise|class|while|until|label|goto|file|case|char|real|read|near|name|cvar|with|uses|unit|type|then|self|for|end|else|div|asm|and|"+
            "far|try|out|xor|var|shr|shl|set|not|nil|mod|is|to|if|as|of|do|in|on|or)", TokenType.ReservedWord));

            _tokenDefenitions.add(new TokenDefenition("([+]|[-]|[\\*]|/|=|<|>|[\\[]|[\\]]|[/^]|@|[\\$]|#|&|%|[\\*\\*]|:=)", TokenType.Operator));

            _tokenDefenitions.add(new TokenDefenition("-?(\\d|[a-f]|[A-F])+(([\\.]|[,])?\\d)?", TokenType.Number));

            _tokenDefenitions.add(new TokenDefenition("'(.*)'", TokenType.StringOrCharacter));

            _tokenDefenitions.add(new TokenDefenition("(\\{|\\}|,|[(]|[)]|;|:|[\\.])", TokenType.Separator));

            _tokenDefenitions.add(new TokenDefenition("([a-z]|[A-Z]|\\d|[_])+", TokenType.Identificator));

            _tokenDefenitions.add(new TokenDefenition("(\"|~|`)+", TokenType.Invalid));
}
}
