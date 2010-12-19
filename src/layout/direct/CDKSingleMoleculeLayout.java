package layout.direct;

import java.awt.geom.Rectangle2D;

import javax.vecmath.Point2d;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;

public class CDKSingleMoleculeLayout implements ISingleMoleculeLayout {
    
    private StructureDiagramGenerator sdg;
    
    private int bondLengthOnScreen = 40;
    
    public CDKSingleMoleculeLayout() {
        sdg = new StructureDiagramGenerator();
    }
    
    private void atomLayout(IAtomContainer atomContainer) {
        IMolecule mol = atomContainer.getBuilder().newInstance(
                IMolecule.class, atomContainer);
        sdg.setMolecule(mol, false);
        try {
            sdg.generateCoordinates();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void scaleAndCenter(
            IAtomContainer atomContainer, double scaleFactor, Point2d center) {
        GeometryTools.translate2DCenterTo(atomContainer, new Point2d(0,0));
        GeometryTools.scaleMolecule(atomContainer, scaleFactor);
        GeometryTools.translate2DCenterTo(atomContainer, center);
    }
    
    public void layout(IAtomContainer atomContainer, Point2d center) {
        atomLayout(atomContainer);
        double scaleFactor = bondLengthOnScreen / 
                             GeometryTools.getBondLengthAverage(atomContainer);
        scaleAndCenter(atomContainer, scaleFactor, center);
    }
    
    public void layout(IAtomContainer atomContainer, Rectangle2D canvas) {
        atomLayout(atomContainer);
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        Rectangle2D molBounds = GeometryTools.getRectangle2D(atomContainer);
        double molWidth = molBounds.getWidth();
        double molHeight = molBounds.getHeight();
        double scaleFactor = 
            Math.min(canvasWidth / molWidth, canvasHeight / molHeight);
        Point2d center = new Point2d(canvas.getCenterX(), canvas.getCenterY());
        scaleAndCenter(atomContainer, scaleFactor, center);
    }

}
