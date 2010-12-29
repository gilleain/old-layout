package base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.vecmath.Point2d;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.reactionblast.graphics.direct.DirectMoleculeDrawer;
import org.openscience.reactionblast.graphics.direct.Params;

public class BaseTest {
    
    private IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
    
    public static final int IMAGE_WIDTH = 400;
    
    public static final int IMAGE_HEIGHT = 400;
    
    public static final String imageDir = "images";
    
    public Point2d imageCenter() {
        return new Point2d(IMAGE_WIDTH / 2, IMAGE_HEIGHT / 2);
    }
    
    public Rectangle2D getCanvas() {
        return new Rectangle2D.Double(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
    }
    
        
    public void makeImage(IAtomContainer atomContainer, String subDirPath, String filename) throws IOException {
        makeImage(atomContainer, subDirPath, filename, new Params());
    }
    
    public void makeImage(IAtomContainer atomContainer, String subDirPath, String filename, Params params) throws IOException {
        Image image = new BufferedImage(
                IMAGE_WIDTH, IMAGE_HEIGHT,
                BufferedImage.TYPE_INT_ARGB);
        DirectMoleculeDrawer drawer = new DirectMoleculeDrawer(params);
        drawer.getParams().drawAtomID = true;
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        drawer.drawMolecule(atomContainer, g);
        g.dispose();
        File dir = new File(imageDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File subDir = new File(dir, subDirPath);
        if (!subDir.exists()) {
            subDir.mkdir();
        }
        File file = new File(subDir, filename);
        ImageIO.write((RenderedImage)image, "PNG", file);
    }

    public Dimension getCanvasDimension() {
        return new Dimension(IMAGE_HEIGHT, IMAGE_WIDTH);
    }

    
    public void addAtoms(int count, String elementSymbol, IAtomContainer atomContainer) {
        for (int i = 0; i < count; i++) {
            atomContainer.addAtom(builder.newInstance(IAtom.class, elementSymbol));
        }
    }
    
    public void addSingleBonds(int from, IAtomContainer mol, int... toArr) {
        for (int to : toArr) {
            mol.addBond(to, from, IBond.Order.SINGLE);
        }
    }
    
    public void isKConnected(IAtomContainer ac, int k) {
        for (int i = 0; i < ac.getAtomCount(); i++) {
            IAtom atom = ac.getAtom(i);
            if (ac.getConnectedAtomsCount(atom) < k) {
                System.out.println("atom " + i + " not " + k + "-connected");
            }
        }
    }
    
    public void isKBondOrder(IAtomContainer ac, int k) {
        for (int i = 0; i < ac.getAtomCount(); i++) {
            IAtom atom = ac.getAtom(i);
            if (ac.getBondOrderSum(atom) < k) {
                System.out.println("atom " + i + " not " + k + "-bondorder");
            }
        }
    }
    
    public IAtomContainer getBuckyball() {
        String filename = "data/mdl/buckyball.mol";
        InputStream ins =
            this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IMolecule molecule;
        try {
            molecule = (IMolecule)reader.read(new Molecule());
            for (IBond bond : molecule.bonds()) {
                bond.setOrder(IBond.Order.SINGLE);
            }
        } catch (CDKException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return molecule;
    }
    

    public IAtomContainer makeDodecahedrane() {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        addAtoms(20, "C", mol);
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(0, 4, IBond.Order.SINGLE);
        mol.addBond(0, 5, IBond.Order.DOUBLE);
        mol.addBond(1, 2, IBond.Order.DOUBLE);
        mol.addBond(1, 6, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.addBond(2, 7, IBond.Order.SINGLE);
        mol.addBond(3, 4, IBond.Order.SINGLE);
        mol.addBond(3, 8, IBond.Order.DOUBLE);
        mol.addBond(4, 9, IBond.Order.DOUBLE);
        mol.addBond(5, 10, IBond.Order.SINGLE);
        mol.addBond(5, 14, IBond.Order.SINGLE);
        mol.addBond(6, 10, IBond.Order.SINGLE);
        mol.addBond(6, 11, IBond.Order.DOUBLE);
        mol.addBond(7, 11, IBond.Order.SINGLE);
        mol.addBond(7, 12, IBond.Order.DOUBLE);
        mol.addBond(8, 12, IBond.Order.SINGLE);
        mol.addBond(8, 13, IBond.Order.SINGLE);
        mol.addBond(9, 13, IBond.Order.SINGLE);
        mol.addBond(9, 14, IBond.Order.SINGLE);
        mol.addBond(10, 16, IBond.Order.DOUBLE);
        mol.addBond(11, 17, IBond.Order.SINGLE);
        mol.addBond(12, 18, IBond.Order.SINGLE);
        mol.addBond(13, 19, IBond.Order.DOUBLE);
        mol.addBond(14, 15, IBond.Order.DOUBLE);
        mol.addBond(15, 16, IBond.Order.SINGLE);
        mol.addBond(15, 19, IBond.Order.SINGLE);
        mol.addBond(16, 17, IBond.Order.SINGLE);
        mol.addBond(17, 18, IBond.Order.DOUBLE);
        mol.addBond(18, 19, IBond.Order.SINGLE);
//        addSingleBonds(0, mol, 1, 4);
//        addSingleBonds(1, mol, 6);
//        addSingleBonds(2, mol, 3, 7);
//        addSingleBonds(3, mol, 4);
//        addSingleBonds(5, mol, 10, 11);
//        addSingleBonds(6, mol, 11);
//        addSingleBonds(7, mol, 12);
//        addSingleBonds(8, mol, 14);
//        addSingleBonds(9, mol, 10, 14);
//        addSingleBonds(12, mol, 17);
//        addSingleBonds(13, mol, 18);
//        addSingleBonds(15, mol, 16, 19);
//        addSingleBonds(16, mol, 17);
//        addSingleBonds(18, mol, 19);
//        
//        mol.addBond(0, 5, IBond.Order.DOUBLE);
//        mol.addBond(1, 2, IBond.Order.DOUBLE);
//        mol.addBond(3, 8, IBond.Order.DOUBLE);
//        mol.addBond(4, 9, IBond.Order.DOUBLE);
//        mol.addBond(6, 12, IBond.Order.DOUBLE);
//        mol.addBond(7, 13, IBond.Order.DOUBLE);
//        mol.addBond(8, 13, IBond.Order.DOUBLE);
//        mol.addBond(10, 15, IBond.Order.DOUBLE);
//        mol.addBond(11, 16, IBond.Order.DOUBLE);
//        mol.addBond(14, 19, IBond.Order.DOUBLE);
//        mol.addBond(17, 18, IBond.Order.DOUBLE);
        isKConnected(mol, 3);
        isKBondOrder(mol, 4);
        return mol;
    }
    
    public IAtomContainer makeCubane() {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        for (int i = 0; i < 8; i++) {
            mol.addAtom(builder.newInstance(IAtom.class, "C"));
        }
        addSingleBonds(0, mol, 1, 3, 5);
        addSingleBonds(1, mol, 2, 6);
        addSingleBonds(2, mol, 3, 7);
        addSingleBonds(3, mol, 4);
        addSingleBonds(4, mol, 5, 6);
        addSingleBonds(5, mol, 6);
        addSingleBonds(6, mol, 7);
        return mol;
    }
    
    public IAtomContainer makeCuneane() {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        for (int i = 0; i < 8; i++) {
            mol.addAtom(builder.newInstance(IAtom.class, "C"));
        }
        addSingleBonds(0, mol, 1, 3, 5);
        addSingleBonds(1, mol, 2, 7);
        addSingleBonds(2, mol, 3, 7);
        addSingleBonds(3, mol, 4);
        addSingleBonds(4, mol, 5, 6);
        addSingleBonds(5, mol, 6);
        addSingleBonds(6, mol, 7);
        return mol;
    }
    
    // the example used by Ulrich Bauer in the SimpleCycleBasis test
    public IAtomContainer makeBauersGraph() {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        addAtoms(8, "C", mol);
        addSingleBonds(0, mol, 1, 2);
        addSingleBonds(1, mol, 2, 3, 4, 6);
        addSingleBonds(2, mol, 3, 4, 5, 7);
        addSingleBonds(3, mol, 4, 6);
        addSingleBonds(4, mol, 5, 7);
        addSingleBonds(5, mol, 7);
        return mol;
    }
    
    public IAtomContainer makeTwistane() {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        addAtoms(10, "C", mol);
        addSingleBonds(0, mol, 1, 5);
        addSingleBonds(1, mol, 2, 7);
        addSingleBonds(2, mol, 3);
        addSingleBonds(3, mol, 4, 9);
        addSingleBonds(4, mol, 5, 6);
        addSingleBonds(6, mol, 7);
        addSingleBonds(7, mol, 8);
        addSingleBonds(8, mol, 9);
        return mol;
    }
    
    public IAtomContainer makeFullerene26() {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        addAtoms(26, "C", mol);
        addSingleBonds(0, mol, 1, 4, 5);
        addSingleBonds(1, mol, 2, 6);
        addSingleBonds(2, mol, 3, 7);
        addSingleBonds(3, mol, 4, 8);
        addSingleBonds(4, mol, 9);
        addSingleBonds(5, mol, 10, 15);
        addSingleBonds(6, mol, 10, 11);
        addSingleBonds(7, mol, 11, 12);
        addSingleBonds(8, mol, 13, 14);
        addSingleBonds(9, mol, 14, 15);
        addSingleBonds(10, mol, 16);
        addSingleBonds(11, mol, 17);
        addSingleBonds(12, mol, 13, 18);
        addSingleBonds(13, mol, 19);
        addSingleBonds(14, mol, 20);
        addSingleBonds(15, mol, 21);
        addSingleBonds(16, mol, 21, 22);
        addSingleBonds(17, mol, 18, 22);
        addSingleBonds(18, mol, 25);
        addSingleBonds(19, mol, 20, 25);
        addSingleBonds(20, mol, 23);
        addSingleBonds(21, mol, 23);
        addSingleBonds(22, mol, 24);
        addSingleBonds(23, mol, 24);
        addSingleBonds(24, mol, 25);
        isKConnected(mol, 3);
        return mol;
    }
    
    public IAtomContainer makeBowtieane() {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        addAtoms(10, "C", mol);
        addSingleBonds(0, mol, 1, 7);
        addSingleBonds(1, mol, 2, 8);
        addSingleBonds(2, mol, 3, 6);
        addSingleBonds(3, mol, 4, 9);
        addSingleBonds(4, mol, 5);
        addSingleBonds(5, mol, 6, 9);
        addSingleBonds(6, mol, 7);
        addSingleBonds(7, mol, 8);
        addSingleBonds(8, mol, 9);
        return mol;
    }

    public IAtomContainer makeTetrahedrane() {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        addAtoms(4, "C", mol);
        addSingleBonds(0, mol, 1, 2, 3);
        addSingleBonds(1, mol, 2, 3);
        addSingleBonds(2, mol, 3);
        return mol;
    }
}
