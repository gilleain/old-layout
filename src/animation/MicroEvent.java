package animation;

import org.openscience.cdk.interfaces.IChemObject;

public class MicroEvent {
    
    private final IChemObject source;
    
    public MicroEvent(IChemObject source) {
        this.source = source;
    }
    
    public IChemObject getChemObject() {
        return source;
    }

}
