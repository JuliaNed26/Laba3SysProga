public class Main {
    public static void main(String[] args)
    {
        Lexer lexer = new Lexer();
        var tokenizedProgram = lexer.Tokenize("C:\\Users\\nedav\\IdeaProjects\\Laba3\\in.txt");
        for (var token : tokenizedProgram)
        {
            if (token.tokenType != TokenType.Comment)
            {
                System.out.println(token);
            }
        }
    }
}