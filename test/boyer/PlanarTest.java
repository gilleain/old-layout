package boyer;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;

import de.fhstralsund.vinets.algorithm.Parameter;
import de.fhstralsund.vinets.structure.SimpleGraph;

import base.BaseTest;

public class PlanarTest extends BaseTest {
    
    public void planarTest(IAtomContainer atomContainer) {
        SimpleGraph graph = AtomContainerGraphFactory.create(atomContainer);
        Parameter parameter = new Parameter(graph, "");
        BoyerPlanarEmbedder bpe = new BoyerPlanarEmbedder();
        bpe.execute(parameter);
        boolean isPlanar = bpe.isPlanar();
        System.out.println(parameter.getMessage() + " " +
                isPlanar + " " 
                + graph.countNodes() + " " + graph.countEdges());
        System.out.println(bpe.getCyclicEdgeOrdering());
        System.out.println(bpe.getResultBicompRoots());

    }
    
    @Test
    public void buckyballTest() {
        planarTest(getBuckyball());
    }
    
    @Test
    public void bowtieaneTest() {
        planarTest(makeBowtieane());
    }
    
    @Test
    public void cuneaneTest() {
        planarTest(makeCuneane());
    }
    
    @Test
    public void tetrahedraneTest() {
        planarTest(makeTetrahedrane());
    }
    
    @Test
    public void dodecTest() {
        planarTest(makeDodecahedrane());
    }

}
