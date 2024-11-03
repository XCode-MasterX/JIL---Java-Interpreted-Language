package JILBase;

import java.util.ArrayList;

public class Block {
    private ArrayList<Object> statements;

    public Block() {
        statements = new ArrayList<>();
    }

    public int[] createBlock(final ArrayList<Token> program, int index, int depth) {
        final int startDepth = depth;
        Token current = program.get(index);
        Statement currStatement = null;
        
        index += current.type == TokenType.NEWLINE ? 1 : 0;

        for(; index < program.size(); index++) {
            current = program.get(index);

            if(current.type == TokenType.EOF)
                jil.error(current.line, "Expected }, but encountered EOF.");

            if(currStatement == null) currStatement = new Statement();
            
            if(current.type == TokenType.LEFT_BRACE) {
                Block subBlock = new Block();
                index = subBlock.createBlock(program, index + 1, depth + 1)[0];
                statements.add(subBlock);
                continue;
            }
            else if(current.type == TokenType.RIGHT_BRACE) {
                depth--;
                currStatement = null;
                if(startDepth > depth) break;
            }
            
            if(current.type == TokenType.NEWLINE || current.type == TokenType.SEMICOLON) {
                if(currStatement == null || currStatement.isEmpty()) continue;
                statements.add(currStatement);
                currStatement = null;
            }
            else {
                currStatement.addToken(current);
            }
        }

        return new int[]{index, depth};
    }

    public String toString() {
        StringBuilder stringRep = new StringBuilder();

        for(Object s : statements)
            stringRep.append(s.toString());

        return stringRep.toString();
    }
}