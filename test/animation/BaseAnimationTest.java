package animation;

import java.util.Random;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import base.BaseTest;

public class BaseAnimationTest extends BaseTest {
    
    private Random random = new Random();
    
    public void dummyAlgorithm(IAtomContainer atomContainer) {
        dummyAlgorithm(atomContainer, 10);
    }
    
    public void dummyAlgorithm(IAtomContainer atomContainer, int numberOfSteps) {
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
    
    public IAtomContainer randomPointStructure(IChemObjectBuilder builder) {
        return randomPointStructure(builder, 5);
    }
    
    public IAtomContainer randomPointStructure(IChemObjectBuilder builder, int numberOfAtoms) {
        IAtomContainer atomContainer = builder.newInstance(IAtomContainer.class);
        for (int i = 0; i < numberOfAtoms; i++) {
            atomContainer.addAtom(builder.newInstance(IAtom.class, "C", randomPoint()));
        }
        return atomContainer;
    }

}
