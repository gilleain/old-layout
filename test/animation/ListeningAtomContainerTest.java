package animation;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

public class ListeningAtomContainerTest extends BaseAnimationTest {
    
    @Test
    public void basicTest() {
        final List<Point2d> points = new ArrayList<Point2d>();
        IChemObjectBuilder builder = new ListeningChemObjectBuilder(
                new IFrameTrigger() {
                    
                    @Override
                    public void postLowLevelEvent(MicroEvent event) {
                        IChemObject chemObject = event.getChemObject();
                        if (chemObject instanceof IAtom) {
                            IAtom atom = (IAtom) chemObject;
                            points.add(new Point2d(atom.getPoint2d()));
                        }
                    }

                    @Override
                    public void addFrameListener(FrameListener listener) {
                        // do nothing in this test
                    }
            
        });
        IAtomContainer atomContainer = randomPointStructure(builder);
        dummyAlgorithm(atomContainer);
        for (Point2d point : points) {
            System.out.println(point);
        }
    }
    
}
