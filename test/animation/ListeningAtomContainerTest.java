package animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point2d;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import base.BaseTest;

public class ListeningAtomContainerTest extends BaseTest {
    
    private Random random = new Random();
    
    public void dummyAlgorithm(IAtomContainer atomContainer) {
        int numberOfSteps = 10;
        
        for (int step = 0; step < numberOfSteps; step++) {
            for (IAtom atom : atomContainer.atoms()) {
                Point2d point = atom.getPoint2d();
                point.x += random.nextDouble() * (random.nextBoolean()? 1 : -1);
                point.y += random.nextDouble() * (random.nextBoolean()? 1 : -1);
                atom.setPoint2d(point);
            }
        }
    }
    
    public Point2d randomPoint() {
        return new Point2d(random.nextDouble(), random.nextDouble());
    }
    
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
            
        });
        IAtomContainer atomContainer = builder.newInstance(IAtomContainer.class);
        atomContainer.addAtom(builder.newInstance(IAtom.class, "C", randomPoint()));
        atomContainer.addAtom(builder.newInstance(IAtom.class, "C", randomPoint()));
        atomContainer.addAtom(builder.newInstance(IAtom.class, "C", randomPoint()));
        atomContainer.addAtom(builder.newInstance(IAtom.class, "C", randomPoint()));
        atomContainer.addAtom(builder.newInstance(IAtom.class, "C", randomPoint()));
        dummyAlgorithm(atomContainer);
        for (Point2d point : points) {
            System.out.println(point);
        }
    }

}
