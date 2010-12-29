package layout.direct;

import org.openscience.cdk.interfaces.IAtomContainer;

import boyer.AtomContainerGraphFactory;
import boyer.BoyerPlanarEmbedder;
import de.fhstralsund.vinets.algorithm.Parameter;
import de.fhstralsund.vinets.structure.SimpleGraph;

public class PlanarityChecker {
    
    public static boolean isPlanar(IAtomContainer atomContainer) {
        SimpleGraph graph = AtomContainerGraphFactory.create(atomContainer);
        Parameter parameter = new Parameter(graph, "");
        BoyerPlanarEmbedder bpe = new BoyerPlanarEmbedder();
        bpe.execute(parameter);
        return bpe.isPlanar();
    }

}
