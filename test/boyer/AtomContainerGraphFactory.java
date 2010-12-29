package boyer;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import de.fhstralsund.vinets.structure.GraphType;
import de.fhstralsund.vinets.structure.Node;
import de.fhstralsund.vinets.structure.SimpleGraph;

public class AtomContainerGraphFactory {
    
    public static SimpleGraph create(IAtomContainer atomContainer) {
        SimpleGraph graph = new SimpleGraph(GraphType.UNDIRECTED);
        
        List<Node> nodes = new ArrayList<Node>();
        int atomNumber = 0;
        for (IAtom atom : atomContainer.atoms()) {
            String id = String.valueOf(atomNumber) + atom.getSymbol();
            nodes.add(graph.createNode(id));
            atomNumber++;
        }
        
        for (IBond bond : atomContainer.bonds()) {
            IAtom a0 = bond.getAtom(0);
            IAtom a1 = bond.getAtom(1);
            int a0n = atomContainer.getAtomNumber(a0);
            int a1n = atomContainer.getAtomNumber(a1);
            graph.createEdge(nodes.get(a0n), nodes.get(a1n));
        }
        
        return graph;
    }

}
