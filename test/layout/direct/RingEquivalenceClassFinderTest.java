package layout.direct;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.templates.MoleculeFactory;

import base.BaseTest;

public class RingEquivalenceClassFinderTest extends BaseTest {
    
    @Test
    public void cyclicSequenceForwardSucceedTest() {
        int[] seqA = new int[] {1, 2, 1, 0, 0, 1};
        int[] seqB = new int[] {0, 1, 2, 1, 1, 0};
        Assert.assertTrue(RingEquivalenceClassFinder.cycliclyEqual(seqA, seqB));
    }
    
    @Test
    public void cyclicSequenceForwardFailTest() {
        int[] seqA = new int[] {0, 1, 1, 2};
        int[] seqB = new int[] {1, 1, 1, 0};
        Assert.assertFalse(RingEquivalenceClassFinder.cycliclyEqual(seqA, seqB));
    }
    
    @Test
    public void cyclicSequenceBackwardSucceedTest() {
        int[] seqA = new int[] {0, 1, 1, 2};
        int[] seqB = new int[] {1, 1, 2, 0};
        Assert.assertTrue(RingEquivalenceClassFinder.cycliclyEqual(seqA, seqB));
    }
    
    @Test
    public void cyclicSequenceBackwardFailTest() {
        int[] seqA = new int[] {0, 1, 1, 2};
        int[] seqB = new int[] {1, 1, 2, 1};
        Assert.assertFalse(RingEquivalenceClassFinder.cycliclyEqual(seqA, seqB));
    }
    
    public void ringEqClTest(IAtomContainer atomContainer) {
        IRingSet ringSet = new SSSRFinder(atomContainer).findSSSR();
        List<List<IAtomContainer>> ringEqClasses = 
            RingEquivalenceClassFinder.getRingEquivalenceClasses(atomContainer, ringSet);
        print(atomContainer, ringEqClasses);
    }
    
    public void sssrFinderEqClTest(IAtomContainer atomContainer) {
        List<IRingSet> eqCl = 
            new SSSRFinder(atomContainer).findEquivalenceClasses();
        for (int cl = 0; cl < eqCl.size(); cl++) {
            System.out.println(cl);
            print(atomContainer, (IRingSet)eqCl.get(cl));
        }
    }
    
    @Test
    public void bowtieaneRingEqClTest() {
//        ringEqClTest(makeBowtieane());
        sssrFinderEqClTest(makeBowtieane());
    }
    
    @Test
    public void fullerene26RingEqClTest() {
//        ringEqClTest(makeFullerene26());
        sssrFinderEqClTest(makeFullerene26());
    }
    
    @Test
    public void alphaPineneRingEqClTest() {
        ringEqClTest(MoleculeFactory.makeAlphaPinene());
//        sssrFinderEqClTest(MoleculeFactory.makeAlphaPinene());
    }
    
    @Test
    public void dodecahedraneRingEqClTest() {
//        ringEqClTest(makeDodecahedrane());
        sssrFinderEqClTest(makeDodecahedrane());
    }
    
    @Test
    public void buckballRingEqClTest() {
        ringEqClTest(getBuckyball());
    }
    
    @Test
    public void twistaneRingEquivalenceClassTest() {
        ringEqClTest(makeTwistane());
//        sssrFinderEqClTest(makeTwistane());
    }
    
    @Test
    public void cuneaneRingEquivalenceClassTest() {
        ringEqClTest(makeCuneane());
    }
    
    @Test
    public void bauersGraphRingEquivalenceClassTest() {
        ringEqClTest(makeBauersGraph());
//        sssrFinderEqClTest(makeBauersGraph());
    }
    
    public void print(IAtomContainer mol, IRingSet rings) {
        for (IAtomContainer ring : rings.atomContainers()) {
            print(mol, ring);
        }
    }
    
    public void print(IAtomContainer mol, IAtomContainer ring) {
        int ringSize = ring.getAtomCount();
        int[] indices = new int[ringSize];
        for (int ringIndex = 0; ringIndex < ringSize; ringIndex++) {
            IAtom atom = ring.getAtom(ringIndex);
            int index = mol.getAtomNumber(atom);
            indices[ringIndex] = index;
        }
        System.out.println(Arrays.toString(indices));
    }
    
    public void print(IAtomContainer mol, List<List<IAtomContainer>> ringEqClasses) {
        System.out.println(ringEqClasses.size());
        for (List<IAtomContainer> ringEqCl : ringEqClasses) {
            System.out.println(ringEqCl.size());
            for (IAtomContainer ring : ringEqCl) {
                print(mol, ring);
            }
        }
    }

}
