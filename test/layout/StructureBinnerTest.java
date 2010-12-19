package layout;

import java.io.IOException;
import java.util.List;

import layout.direct.CDKSingleMoleculeLayout;
import layout.direct.LayoutBin;
import layout.direct.StructureBinner;

import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

public class StructureBinnerTest extends BaseTest {
    
    public IMolecule makeConstrictedMol() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IMolecule mol = builder.newInstance(IMolecule.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(0, 4, IBond.Order.SINGLE);
        
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(1, 5, IBond.Order.SINGLE);
        mol.addBond(1, 6, IBond.Order.SINGLE);
        mol.addBond(1, 7, IBond.Order.SINGLE);
        
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(2, 8, IBond.Order.SINGLE);
        mol.addBond(2, 9, IBond.Order.SINGLE);
        mol.addBond(2, 10, IBond.Order.SINGLE);
        
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(3, 11, IBond.Order.SINGLE);
        mol.addBond(3, 12, IBond.Order.SINGLE);
        mol.addBond(3, 13, IBond.Order.SINGLE);
        
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(4, 14, IBond.Order.SINGLE);
        mol.addBond(4, 15, IBond.Order.SINGLE);
        mol.addBond(4, 16, IBond.Order.SINGLE);
        
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(14, 17, IBond.Order.SINGLE);
        mol.addBond(14, 18, IBond.Order.SINGLE);
        mol.addBond(17, 18, IBond.Order.SINGLE);
        
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(11, 19, IBond.Order.SINGLE);
        mol.addBond(11, 20, IBond.Order.SINGLE);
        mol.addBond(19, 20, IBond.Order.SINGLE);
        
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(6, 21, IBond.Order.SINGLE);
        mol.addBond(6, 22, IBond.Order.SINGLE);
        mol.addBond(21, 22, IBond.Order.SINGLE);
        
        return mol;
    }
    
    @Test
    public void test() throws IOException {
        IMolecule mol = makeConstrictedMol();
        setIDs(mol);
        List<LayoutBin> bins = 
            StructureBinner.run(mol, new CDKSingleMoleculeLayout(), 100);
        int binIndex = 0;
        for (LayoutBin bin : bins) {
            System.out.println(bin.size() + " ");
            IAtomContainer member0 = bin.getMember(0);
            GeometryTools.center(member0, super.getCanvasDimension());
            super.makeImage(member0, "bins", "bin" + binIndex + ".png");
            binIndex++;
        }
    }
    
    private void setIDs(IAtomContainer atomContainer) {
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            atomContainer.getAtom(i).setID(String.valueOf(i));
        }
    }

}
