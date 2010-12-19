package composer;

import java.util.List;

public class CompositionTree {
    
    public class Component {
        
        public List<Component> children;
        
        public List<Integer> vertexIndices;
        
    }
    
    private Component root;
    
    public CompositionTree() {
        root = new Component();
    }
    
    public Component getRoot() {
        return root;
    }

}
