package layout.direct;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.SSSRFinder;

public class SchlegelLayout {
    
    public void layout(IAtomContainer atomContainer) {
        IRingSet ringSet = new SSSRFinder(atomContainer).findSSSR();
        check(atomContainer, ringSet);
        
        
    }
    
    public void check(IAtomContainer atomContainer, IRingSet rings) {
        // simple checks for planarity
        int v = atomContainer.getAtomCount();
        int e = atomContainer.getBondCount();
        boolean isNonPlanar = false;
        if (v > 2 && e > ((3 * v) - 6)) {
            isNonPlanar = true;
        }
        
        if (v > 3 && !checkForRingsOfSize3(rings) && (e > ((2 * v) - 4))) {
            isNonPlanar = true;
        }
        
        // ? throw error? or warning?
        if (isNonPlanar) {
            
        }
    }
    
    private boolean checkForRingsOfSize3(IRingSet rings) {
        for (IAtomContainer ring : rings.atomContainers()) {
            if (ring.getAtomCount() == 3) {
                return true;
            }
        }
        return false;
    }

}
