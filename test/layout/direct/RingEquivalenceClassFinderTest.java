package layout.direct;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.SSSRFinder;

public class RingEquivalenceClassFinderTest {
    
    private IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
    
    public void addSingleBonds(int from, IAtomContainer mol, int... toArr) {
        for (int to : toArr) {
            mol.addBond(to, from, IBond.Order.SINGLE);
        }
    }
    
    public IAtomContainer makeCuneane() {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        for (int i = 0; i < 8; i++) {
            mol.addAtom(builder.newInstance(IAtom.class, "C"));
        }
        addSingleBonds(0, mol, 1, 3, 5);
        addSingleBonds(1, mol, 2, 7);
        addSingleBonds(2, mol, 3, 7);
        addSingleBonds(3, mol, 4);
        addSingleBonds(4, mol, 5, 6);
        addSingleBonds(5, mol, 6);
        addSingleBonds(6, mol, 7);
        return mol;
    }
    
    // the example used by Ulrich Bauer in the SimpleCycleBasis test
    public IAtomContainer makeBauersGraph() {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        for (int i = 0; i < 8; i++) {
            mol.addAtom(builder.newInstance(IAtom.class, "C"));
        }
        addSingleBonds(0, mol, 1, 2);
        addSingleBonds(1, mol, 2, 3, 4, 6);
        addSingleBonds(2, mol, 3, 4, 5, 7);
        addSingleBonds(3, mol, 4, 6);
        addSingleBonds(4, mol, 5, 7);
        addSingleBonds(5, mol, 7);
        return mol;
    }
    
    public IAtomContainer makeTwistane() {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        for (int i = 0; i < 10; i++) {
            mol.addAtom(builder.newInstance(IAtom.class, "C"));
        }
        addSingleBonds(0, mol, 1, 5);
        addSingleBonds(1, mol, 2, 7);
        addSingleBonds(2, mol, 3);
        addSingleBonds(3, mol, 4, 9);
        addSingleBonds(4, mol, 5, 6);
        addSingleBonds(6, mol, 7);
        addSingleBonds(7, mol, 8);
        addSingleBonds(8, mol, 9);
        return mol;
    }
    
    
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
    
    @Test
    public void twistaneRingEquivalenceClassTest() {
        IAtomContainer twistane = makeTwistane();
        IRingSet ringSet = new SSSRFinder(twistane).findSSSR();
        List<List<IAtomContainer>> ringEqClasses = 
            RingEquivalenceClassFinder.getRingEquivalenceClasses(twistane, ringSet);
        print(twistane, ringEqClasses);
    }
    
    @Test
    public void cuneaneRingEquivalenceClassTest() {
        IAtomContainer cuneane = makeCuneane();
        IRingSet ringSet = new SSSRFinder(cuneane).findSSSR();
        List<List<IAtomContainer>> ringEqClasses = 
            RingEquivalenceClassFinder.getRingEquivalenceClasses(cuneane, ringSet);
        print(cuneane, ringEqClasses);
    }
    
    @Test
    public void bauersGraphRingEquivalenceClassTest() {
        IAtomContainer bauersGraph = makeBauersGraph();
        IRingSet ringSet = new SSSRFinder(bauersGraph).findSSSR();
        List<List<IAtomContainer>> ringEqClasses = 
            RingEquivalenceClassFinder.getRingEquivalenceClasses(bauersGraph, ringSet);
//        print(bauersGraph, ringEqClasses);
        print(bauersGraph, ringSet);
        List<IAtomContainer> eqCl = new SSSRFinder(bauersGraph).findEquivalenceClasses();
        for (int cl = 0; cl < eqCl.size(); cl++) {
            System.out.println(cl);
//            print(bauersGraph, (IRingSet)eqCl.get(cl));
        }
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
