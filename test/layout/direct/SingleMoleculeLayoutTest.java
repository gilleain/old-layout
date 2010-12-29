package layout.direct;

import java.io.IOException;

import layout.direct.CDKSingleMoleculeLayout;

import org.junit.Test;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

import base.BaseTest;

public class SingleMoleculeLayoutTest extends BaseTest {
    
    public static final String dir = "single_mol";
    
    @Test
    public void layoutMoleculeNotZoomToFit() throws IOException {
       IMolecule mol = MoleculeFactory.make123Triazole();
       CDKSingleMoleculeLayout layout = new CDKSingleMoleculeLayout();
       layout.layout(mol, imageCenter());
       makeImage(mol, dir, "triazole_natural.png");
    }
    
    @Test
    public void layoutMoleculeZoomToFit() throws IOException {
       IMolecule mol = MoleculeFactory.make123Triazole();
       CDKSingleMoleculeLayout layout = new CDKSingleMoleculeLayout();
       layout.layout(mol, getCanvas());
       makeImage(mol, dir, "triazole_zoomed.png");
    }

}
