package modDecomp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import modularDecomposition.Graph;
import modularDecomposition.MDTree;
import modularDecomposition.MDTreeNode;
import modularDecomposition.Vertex;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

public class TestMDTree {
    
    public class AtomGraph extends Graph {
        
        public AtomGraph(IAtomContainer atomContainer) {
            int i = 0;
            for (IAtom atom : atomContainer.atoms()) {
                String label = String.valueOf(i);
                Vertex vertex;
                if (vertices.containsKey(label)) {
                    vertex = vertices.get(label);
                } else {
                    vertex = new Vertex(label);
                    vertices.put(label, vertex);
                }
                
                for (IAtom neighbour : atomContainer.getConnectedAtomsList(atom)) {
                    String neighbourLabel = String.valueOf(atomContainer.getAtomNumber(neighbour));
                    Vertex neighbourVertex;
                    if (vertices.containsKey(neighbourLabel)) {
                        neighbourVertex = vertices.get(neighbourLabel);
                    } else {
                        neighbourVertex = new Vertex(neighbourLabel);
                        vertices.put(neighbourLabel, neighbourVertex);
                    }
                    vertex.addNeighbour(neighbourVertex);
                }
                i++;
            }
        }
    }
    
    public void testMol(String name, IMolecule mol) {
        AtomGraph atomGraph = new AtomGraph(mol);
        MDTree mdTree = new MDTree(atomGraph);
//        System.out.println(mdTree.toString());
        MDTreeNode root = (MDTreeNode)mdTree.getRoot(); 
        int children = root.getNumChildren();
        boolean childrenEqualToAtomCount = (children == mol.getAtomCount());
        if (childrenEqualToAtomCount) {
            System.out.println(name + " IS " + root.getType());
        } else {
            System.out.print(name + " IS " + root.getType());
            MDTreeNode current = (MDTreeNode) root.getFirstChild();
            if (current != null) { 
                System.out.print(current.isALeaf());
                current  = (MDTreeNode) current.getRightSibling(); 
            }
            while (current != null) {
                System.out.print(current.isALeaf());
                current = (MDTreeNode) current.getRightSibling();
            }
            System.out.println();
        }
    }
    
    @Test
    public void diamantaneTest() {
        IMolecule diamantane = MoleculeFactory.makeDiamantane();
        testMol("Diamantane", diamantane);
    }
    
    @Test
    public void testAll() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        for (Method method : MoleculeFactory.class.getMethods()) {
            if (method.getParameterTypes().length > 0) continue;
            IMolecule molecule = (IMolecule) method.invoke(MoleculeFactory.class);
            testMol(method.getName().substring(4), molecule);
        }
    }

}
