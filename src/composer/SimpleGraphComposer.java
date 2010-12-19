package composer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Use only the degree of each vertex in the graph to collect into components.
 * 
 * @author maclean
 *
 */
public class SimpleGraphComposer implements IGraphComposer {
    
    private IAtomContainer atomContainer;
    
    private Map<Integer, List<IAtom>> degreeMap;
    
    public SimpleGraphComposer(IAtomContainer atomContainer) {
        this.atomContainer = atomContainer;
        degreeMap = getDegreeMap(atomContainer);
    }
    
    private Map<Integer, List<IAtom>> getDegreeMap(IAtomContainer atomContainer) {
        Map<Integer, List<IAtom>> degreeMap = new HashMap<Integer, List<IAtom>>();
        for (IAtom atom : atomContainer.atoms()) {
            int degree = atomContainer.getConnectedAtomsCount(atom);
            List<IAtom> atomsWithDegree;
            if (degreeMap.containsKey(degree)) {
                atomsWithDegree = degreeMap.get(degree);
            } else {
                atomsWithDegree = new ArrayList<IAtom>();
                degreeMap.put(degree, atomsWithDegree);
            }
            atomsWithDegree.add(atom);
        }
        return degreeMap;
    }

    @Override
    public CompositionTree getNext() {
        // TODO Auto-generated method stub
        return null;
    }

}
