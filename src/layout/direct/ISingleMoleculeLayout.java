package layout.direct;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface ISingleMoleculeLayout {
    
    public void layout(IAtomContainer atomContainer, Point2d center);

}
