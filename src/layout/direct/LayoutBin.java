package layout.direct;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * A bin of layouts, based on some comparison measure.
 * 
 * @author maclean
 *
 */
public class LayoutBin {
    
    private final double TOLERANCE = 0.01;
    
    private List<IAtomContainer> members;

    public LayoutBin(IAtomContainer atomContainer) {
        members = new ArrayList<IAtomContainer>();
        members.add(atomContainer);
    }

    public boolean contains(IAtomContainer atomContainer) {
        for (IAtomContainer member : members) {
            double d = distance(member, atomContainer);
//            System.out.println("DISTANCE " + d);
            if (d > TOLERANCE) {
                return false;
            }
        }
        return true;
    }
    
//    private void printAtomContainerPoints(IAtomContainer atomContainer) {
//        System.out.print("|");
//        for (IAtom a : atomContainer.atoms()) {
//            Point2d p = a.getPoint2d();
//            System.out.print(String.format("%2.2f %2.2f", p.x, p.y) + "|");
//        }
//        System.out.println("|");
//    }
    
    private double distance(IAtomContainer a, IAtomContainer b) {
        double dSum = 0.0;
        int n = a.getAtomCount();
        for (int i = 0; i < n; i++) {
            IAtom aa = a.getAtom(i);
            IAtom bb = b.getAtom(i);
            double d = aa.getPoint2d().distance(bb.getPoint2d());
            dSum += Math.pow(d, 2);
//            System.out.println("d = " + d + " dSum= " + dSum);
        }
        return Math.sqrt(dSum / n);
    }

    public int size() {
        return members.size();
    }

    public void add(IAtomContainer atomContainer) {
        members.add(atomContainer);
    }

    public IAtomContainer getMember(int i) {
        return members.get(i);
    }

}
