package JILBase;

public class ConditionalBlock extends Block{
    ConditionalStatement condition;
    
    public ConditionalBlock(ConditionalStatement condition, final int depth, final StoreVariables owner){
        super(depth, owner);
        this.condition = condition;
    }

    public void run(int line) {
        condition.run();
        if(!condition.evaluation) return;
            
        for(Object o : contents) {
            if(o instanceof Statement statement) statement.run(line);
            else if(o instanceof Block subBlock) subBlock.run(line);
            else jil.error(-1, contents.toString());
            
            line++;
        }
    }
}
