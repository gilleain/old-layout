package layout.direct;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.reactionblast.graphics.direct.Params;

import base.BaseTest;

public class SchlegelLayoutTest extends BaseTest {
    
    @Test
    public void periphTest() {
        IAtomContainer mol = makeDodecahedrane();
        IRingSet ringSet = new SSSRFinder(mol).findSSSR();
        // arbitrarily select the first ring for the boundary
        IAtomContainer boundary = ringSet.getAtomContainer(0);
        SchlegelLayout layout = new SchlegelLayout();
        int[] per = layout.calculatePeriphericities(mol, boundary);
        System.out.println(Arrays.toString(per));
    }
    
    public void layout(IAtomContainer mol, String name) throws IOException {
        SchlegelLayout layout = new SchlegelLayout();
//        layout.layout(mol);
        layout.layout(mol, 1000, 0.5);
        for (IAtom atom : mol.atoms()) {
            System.out.println(atom.getPoint2d());
        }
        GeometryTools.scaleMolecule(mol, getCanvasDimension(), 0.75);
        GeometryTools.center(mol, getCanvasDimension());
        Params params = new Params();
        params.drawAtomID = false;
        makeImage(mol, "schlegel", name + ".png");
    }
    
    @Test
    public void fullerene26LayoutTest() throws IOException {
        layout(makeFullerene26(), "f26");
    }
    
    @Test
    public void buckyLayoutTest() throws IOException {
        layout(getBuckyball(), "bucky");
    }
    
    @Test
    public void dodecLayoutTest() throws IOException {
        layout(makeDodecahedrane(), "dodec");
    }
    
    @Test
    public void cubaneLayoutTest() throws IOException {
        layout(makeCubane(), "cubane");
    }

}
