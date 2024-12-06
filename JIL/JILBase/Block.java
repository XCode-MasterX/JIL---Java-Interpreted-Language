package JILBase;

import java.util.ArrayList;

public class Block {
    protected ArrayList<Object> contents;
    int startPoint;
    private final int depth;
    private final StoreVariables owner;

    public Block(final int depth, final StoreVariables owner) {
        contents = new ArrayList<>();
        this.depth = depth;
        this.owner = owner;
    }

    public int[] createBlock(final ArrayList<Token> program, int index, final int depth) {
        Token current = program.get(index);
        Statement currStatement = null;
        startPoint = program.get(index).line;
        
        index += current.type == TokenType.NEWLINE ? 1 : 0;

        for(; index < program.size(); index++) {
            current = program.get(index);

            if(current.type == TokenType.EOF)
                jil.error(current.line, "Expected }, but encountered EOF.");
            
            if(current.type == TokenType.LEFT_BRACE) {
                Block subBlock = new Block(depth + 1, owner);
                index = subBlock.createBlock(program, index + 1, depth + 1)[0];
                contents.add(subBlock);
                continue;
            }
            else if(current.type == TokenType.RIGHT_BRACE) {
                currStatement = null;
                break;
            }
            
            if(currStatement == null)
                currStatement = new Statement(this);
            
            if(current.type == TokenType.NEWLINE || current.type == TokenType.SEMICOLON) {
                if(currStatement == null || currStatement.isEmpty()) continue;
                contents.add(currStatement);
                currStatement = null;
                continue;
            }
            
            currStatement.addToken(current);
        }

        return new int[]{index, depth};
    }

    public void print() {
        System.out.println(this);
    }

    public void breakdown() {
        for(Object o : contents) {
            if(o instanceof Statement statement) statement.breakdown();
            else if(o instanceof Block subBlock) subBlock.breakdown();
        }
    }

    public void run(int line) {
        for(Object o : contents) {
            if(o instanceof Statement statement) statement.run(line);
            else if(o instanceof Block subBlock) subBlock.run(line);
            else                                 jil.error(-1, "The fuck is this shit? " + contents.toString());
            
            line++;
        }
    }

    public Object getVariableValue(final Token search) { return owner.getVariableValue(search); }

    public String toString() {
        StringBuilder stringRep = new StringBuilder();

        stringRep.append("This block contains following contents: \n" + "-".repeat(50) + "\n");
        for(Object s : contents)
            stringRep.append("\t".repeat(depth) + s.toString());
        stringRep.append("-".repeat(19) + " BLOCK END " + "-".repeat(19) + "\n");

        return stringRep.toString();
    }
}