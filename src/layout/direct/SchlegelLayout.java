package layout.direct;

import java.util.Arrays;
import java.util.Random;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.SSSRFinder;

public class SchlegelLayout {
    
    private final double PI2 = 2 * Math.PI;
    
    public void layout(IAtomContainer atomContainer) {
        layout(atomContainer, 100.0, 0.01);
    }
        
    public void layout(IAtomContainer atomContainer, double dimension, double eta) {
        if (PlanarityChecker.isPlanar(atomContainer)) {
            // arbitrarily select the first ring for the boundary
            IRingSet ringSet = new SSSRFinder(atomContainer).findSSSR();
            IAtomContainer boundary = ringSet.getAtomContainer(0);
            
            double radius = dimension / 2;
            placeBoundary(boundary, 0, 0, radius);
            placeInner(atomContainer, radius);
            int[] per = calculatePeriphericities(atomContainer, boundary);
            int maxPer = getMax(per);
            anneal(atomContainer, boundary, radius, per, maxPer, eta);
        }
    }
    
    private void placeInner(IAtomContainer atomContainer, double radius) {
        Random random = new Random();
        for (IAtom atom : atomContainer.atoms()) {
            if (atom.getPoint2d() == null) {
                double x = random.nextDouble() * (random.nextBoolean()? -1 : 1);
                double y = random.nextDouble() * (random.nextBoolean()? -1 : 1);
                atom.setPoint2d(new Point2d(x, y));
            }
        }
    }
    
    private void anneal(
            IAtomContainer atomContainer, IAtomContainer boundary, double radius, int[] per, int maxPer, double eta) {
        System.out.println(Arrays.toString(per));
        
        int step = 0;
        double maxdis = 0.0;
        int vertexCount = atomContainer.getAtomCount();
        int edgeCount = atomContainer.getBondCount();
        Vector2d[] edgeForces = new Vector2d[edgeCount];
        Vector2d[] vertexForces = new Vector2d[vertexCount];
        
        int TMP_BREAK_LOOP = 500;
        
        while ((maxdis == 0.0 || maxdis > eta) && step < TMP_BREAK_LOOP) {
            for (int vertexForceIndex = 0; vertexForceIndex < vertexCount; vertexForceIndex++) {
                vertexForces[vertexForceIndex] = new Vector2d();
            }
            double coolVal = cool(radius, step);
            
            // calculate the edge and vertex forces
            for (int edgeIndex = 0; edgeIndex < edgeCount; edgeIndex++) {
                IBond bond = atomContainer.getBond(edgeIndex);
                IAtom atomU = bond.getAtom(0);
                IAtom atomV = bond.getAtom(1);
                int uIndex = atomContainer.getAtomNumber(atomU);
                int vIndex = atomContainer.getAtomNumber(atomV);
                Point2d pointU = atomU.getPoint2d();
                Point2d pointV = atomV.getPoint2d();
                
                // create a new force for this edge
                edgeForces[edgeIndex] = new Vector2d(pointU);
                Vector2d current = edgeForces[edgeIndex]; 
                current.sub(pointV);
                double bandStrength = bandStrength(per[uIndex], per[vIndex], maxPer);
                double length = current.length();
                current.x = bandStrength * (Math.pow(current.x, 3) / length);
                current.y = bandStrength * (Math.pow(current.y, 3) / length);
                
                // update the vertex forces
                vertexForces[uIndex].add(current);
                vertexForces[vIndex].sub(current);
            }
            
            maxdis = 0;
            for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
                Vector2d forceVector = vertexForces[vertexIndex];
                double length = forceVector.length();
                double force = Math.min(length, coolVal);
                IAtom atom = atomContainer.getAtom(vertexIndex);
//                if (boundary.contains(atom)) continue;
                Point2d point = atom.getPoint2d();
                forceVector.normalize();
                point.x += force * forceVector.x;
                point.y += force * forceVector.y;
                if (maxdis == 0.0 || force > maxdis) {
                    maxdis = force;
                }
            }
            System.out.println("step " + step + " " + maxdis);
            step++;
        }
        System.out.println("NUMBER OF STEPS " + step);
    }
    
    private double bandStrength(int perU, int perV, int maxPer) {
        double strengthFactor = (1.4 + maxPer) / 24;
        return Math.exp(strengthFactor * ((2 * maxPer - perU - perV) / maxPer));
    }
    
    private double cool(double radius, int step) {
        return (radius / 15) * Math.exp(-(((double)step) / 60));
    }
    
    private void placeBoundary(IAtomContainer boundary, 
            double centerX, double centerY, double radius) {
        
        double angle = 0;
        double addAngle = (PI2) / boundary.getAtomCount(); 
        for (IAtom atom : boundary.atoms()) {
            angle += addAngle;
            if (angle >= PI2) {
                angle -= PI2;
            }
            double x = centerX + (Math.cos(angle) * radius);
            double y = centerY + (Math.sin(angle) * radius);
            atom.setPoint2d(new Point2d(x, y));
        }
    }
    
    private int getMax(int[] arr) {
        int max = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }
    
    public int[] calculatePeriphericities(
            IAtomContainer atomContainer, IAtomContainer boundary) {
        int atomCount = atomContainer.getAtomCount();
        int boundaryAtomCount = boundary.getAtomCount();
        
        // calculate all-v-all shortest path distances
        int[][] costMatrix = AdjacencyMatrix.getMatrix(atomContainer);
        int[][] distances = PathTools.computeFloydAPSP(costMatrix);
        
        // get the atom indices for the boundary atoms in the main atomcontainer
        int[] boundaryIndices = new int[boundaryAtomCount];
        for (int index = 0; index < boundaryAtomCount; index++) {
            IAtom atom = boundary.getAtom(index);
            boundaryIndices[index] = atomContainer.getAtomNumber(atom);
        }
        
        // find the minimum distance for each atom to a boundary atom
        int[] per = new int[atomCount];
        for (int index = 0; index < atomCount; index++) {
            IAtom atom = atomContainer.getAtom(index);
            if (boundary.contains(atom)) {
                per[index] = 0;
            } else {
                int minDistanceToBoundary = Integer.MAX_VALUE;
                for (int boundaryIndexIndex = 0; 
                         boundaryIndexIndex < boundaryAtomCount; boundaryIndexIndex++) {
                    int boundaryIndex = boundaryIndices[boundaryIndexIndex];
                    if (distances[boundaryIndex][index] < minDistanceToBoundary) {
                        minDistanceToBoundary = distances[boundaryIndex][index];
                    }
                }
                per[index] = minDistanceToBoundary;
            }
        }
        return per;
    }

}
