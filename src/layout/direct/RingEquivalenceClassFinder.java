package layout.direct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.signature.SignatureQuotientGraph;

import signature.SymmetryClass;

public class RingEquivalenceClassFinder {
    
    public static List<List<IAtomContainer>> getRingEquivalenceClasses(
            IAtomContainer atomContainer, IRingSet ringSet) {
        // partition the vertices into symmetry classes
        MoleculeSignature molSignature = new MoleculeSignature(atomContainer);
        SignatureQuotientGraph sqg = new SignatureQuotientGraph(atomContainer);
        System.out.println(sqg);
        List<SymmetryClass> symmetryClasses = molSignature.getSymmetryClasses();
        for (SymmetryClass sym : symmetryClasses) { System.out.println(sym); }
        int[] symmetryClassLookup = makeSymmetryClassLookup(atomContainer, symmetryClasses);
        System.out.println("Sym : " + Arrays.toString(symmetryClassLookup));
        // use these to partition the rings into classes
        Map<int[], List<IAtomContainer>> 
            ringSequenceMap = new HashMap<int[], List<IAtomContainer>>();
        int ringCount = 0;
        for (IAtomContainer ring : ringSet.atomContainers()) {
            int n = ring.getAtomCount();
            int[] classSequence = new int[n];
            for (int i = 0; i < n; i++) {
                IAtom atom = ring.getAtom(i);
                int atomNumber = atomContainer.getAtomNumber(atom);
                classSequence[i] = symmetryClassLookup[atomNumber];
            }
            System.out.println(ringCount++ + Arrays.toString(classSequence));
            // add the ring to the map
            List<IAtomContainer> ringEquivalenceClass = null;
            for (int[] sequenceRep : ringSequenceMap.keySet()) {
                if (cycliclyEqual(classSequence, sequenceRep)) {
                    ringEquivalenceClass = ringSequenceMap.get(sequenceRep);
                }
            }
            if (ringEquivalenceClass == null) {
                ringEquivalenceClass = new ArrayList<IAtomContainer>();
                ringSequenceMap.put(classSequence, ringEquivalenceClass);
            }
            ringEquivalenceClass.add(ring);
        }
        
        // convert the map into a simple list of lists
        List<List<IAtomContainer>> ringEquivalenceClasses = 
            new ArrayList<List<IAtomContainer>>();
        for (int[] sequenceRep : ringSequenceMap.keySet()) {
            ringEquivalenceClasses.add(ringSequenceMap.get(sequenceRep));
        }
        return ringEquivalenceClasses;
    }
    
    public static boolean cycliclyEqual(int[] sequenceA, int[] sequenceB) {
        if (sequenceA.length != sequenceB.length) return false;
        if (checkCyclicEquality(sequenceA, sequenceB)) {
            return true;
        } else {
            int length = sequenceB.length;
            int[] reverse = new int[length];
            for (int i = length; i > 0; i--) {
                int rIndex = length - i;
                reverse[i - 1] = sequenceB[rIndex]; 
            }
            return checkCyclicEquality(sequenceA, reverse);
        }
    }
    
    private static boolean checkCyclicEquality(int[] sequenceA, int[] sequenceB) {
        int indexB = 0;
        int length = sequenceA.length;
        for (int indexA = 0; indexA < length; indexA++) {
            // found a possible starting pos
            if (sequenceA[indexA] == sequenceB[indexB]) {
                int startInA = indexA;
                for (int subIndexA = indexA; subIndexA < length; subIndexA++) {
                    if (sequenceA[subIndexA] == sequenceB[indexB]) {
                        indexB++;
                    } else {
                        indexB = 0;
                        break;
                    }
                }
                // not a match
                if (indexB == 0) {
                    continue;
                } else {
                    // check the left over part
                    int remainderIndex = 0;
                    for ( ; remainderIndex < startInA; remainderIndex++) {
                        int a = sequenceA[remainderIndex];
                        int b = sequenceB[indexB];
                        if (a == b) {
                            indexB++;
                        } else {
                            break;
                        }
                    }
                    if (remainderIndex == startInA) return true;
                    indexB = 0;
                }
            }
        }
        return false;
    }

    private static int[] makeSymmetryClassLookup(
            IAtomContainer atomContainer, List<SymmetryClass> symmetryClasses) {
        int[] classLookup = new int[atomContainer.getAtomCount()];
        for (SymmetryClass symmetryClass : symmetryClasses) {
            for (int member : symmetryClass) {
                classLookup[member] = symmetryClasses.indexOf(symmetryClass);
            }
        }
        return classLookup;
    }

}
