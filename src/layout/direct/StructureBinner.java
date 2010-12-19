package layout.direct;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Repeatedly lays out a structure, then 'bins' the resulting coordinates
 * to sample the spread of possible layouts and orientations. 
 * 
 * @author maclean
 *
 */
public class StructureBinner {
    
    private static final Point2d ORIGIN = new Point2d(0, 0);

    public static List<LayoutBin> run(IAtomContainer molecule, 
            ISingleMoleculeLayout layout, int numberOfRuns) {
        List<LayoutBin> bins = new ArrayList<LayoutBin>();
        
        for (int i = 0; i < numberOfRuns; i++) {
            try {
                IAtomContainer clonedContainer = (IAtomContainer) molecule.clone();
                layout.layout(clonedContainer, ORIGIN);
                addToBins(bins, clonedContainer);
            } catch (CloneNotSupportedException cnse) {
                cnse.printStackTrace();
            }
        }
        
        return bins;
    }
    
    public static void addToBins(List<LayoutBin> bins, IAtomContainer atomContainer) {
        for (LayoutBin bin : bins) {
            if (bin.contains(atomContainer)) {
                bin.add(atomContainer);
                return;
            }
        }
        bins.add(new LayoutBin(atomContainer));
    }
}
