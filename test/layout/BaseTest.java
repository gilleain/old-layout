package layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.reactionblast.graphics.direct.DirectMoleculeDrawer;

public class BaseTest {
    
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
        Image image = new BufferedImage(
                IMAGE_WIDTH, IMAGE_HEIGHT,
                BufferedImage.TYPE_INT_ARGB);
        DirectMoleculeDrawer drawer = new DirectMoleculeDrawer();
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

}
