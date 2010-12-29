//$Revision: 1.2 $,$Date: 2003/05/26 12:42:17 $,$Author: toersel $

package boyer;

import de.fhstralsund.vinets.structure.*;
import de.fhstralsund.vinets.algorithm.dfs.*;

import java.util.*;

/** * This class performs all initialisation and preparation works needed for the
 * BoyerPlanarEmbedder which are performed in a depth first search.
 * Technically this class listens to DFS events sent by a DFS implementation.
 */
public class InitialDFSRun extends DFSAdapter
implements DFSLowPointListener {
   
   // This array holds the vertex references
   private BESAbstractVertex[] vertices = null;
   
   /* The last tree edge discovered must be held until the target node is found.
    * Then a singleton bicomp will be created with the BESEdge instances
    * referring to this edge from the input graph.
    */
   private Edge current = null;
   
   // Used to perform a bucket sort of the vertices according to their lowpoints
   private LinkedList[] buckets = null;
   
   /* This is passed to the construtor so a vertex array with
    * proper size can be created
    */
   private int graphSize = 0;

   public final static Object FROM_KEY = new Object();
   
   /** Creates a new InitialDSFSRun.
    * @param graphSize The size of the graph (number of the nodes)
    */
   public InitialDFSRun(int graphSize) {
      this.graphSize = graphSize;
      /* Create the vertex array. See method getVertexArray() for explanation
       * of the array size
       */
      vertices = new BESAbstractVertex[2 * graphSize + 1];
      /* Create the sort buckets. Size is node count plus one again to be
       * able to start at postion one, not zero.
       */
      buckets = new LinkedList[graphSize + 1];
   }
   
   /** After the DFS is finished this returns an array filled with vertices and
    * virtual vertices of the initial embedding structure, with singleton
    * bicomps being preconnected, twin links set etc.
    * The position of the vertices in the array can be calculated as follows.
    * For vertices the position in the array equals their DFS discovery number
    * (DFI). The virtual vertices are stored at the DFI of the vertex they are
    * the root copy for plus the graphs node count.
    * The total size of the array is two times the node count of the graph
    * plus one, since arrays are zero index based, but the first DFI is one.
    * @return An array containing Vertex and VirtualVertex instances of the
    * embedding structure.
    */
   public BESAbstractVertex[] getVertexArray() {
      return vertices;
   }
   
   public void foundEdge(Edge edge, Node from, Object edgetype) {
      /* This is a hack :-) Since the input graph is undirected knowing that
       * it is a tree edge is not enough. We must also know what was the
       * source node and what the target. One way would be to compare the
       * DFI´s but tagging the edge with the source node is more comfortable.
       */
      edge.setLabel(FROM_KEY, from);
      
      // Found tree edge
      if (edgetype == DFS.TREE_EDGE) {
         // See variable declaration for "current"
         current = edge;
      }
      // Found back edge
      else if (edgetype == DFS.BACK_EDGE) {
         NodePair np = edge.getEnds();
         Node to = np.getOther(from);
         
         /* Figure out the DFI of target of this back edge. Than set this
          * as the lowest ancestor of the source vertex - if it is the
          * lowest yet.
          */
         int fromDFI = ((Integer)from.getLabel(DFS.DFI_KEY)).intValue();
         int toDFI = ((Integer)to.getLabel(DFS.DFI_KEY)).intValue();
         BESVertex vertex = (BESVertex)vertices[fromDFI];
         vertex.setLeastAncestorIfSmaller(toDFI);
         
         /* Store the Edge reference in the source Node of the back edge,
          * using the target Node as key. This way it can be retrieved 
          * later in linear time
          */
         
         from.setLabel(to, edge);
      }
   }
   
   /* When a node is discovered create a BESVertex instance, set the
    * DFSParent property (if the node is not DFS root). If the node has
    * a DFS parent create a BESVirtualVertex for it. Finally call a method
    * that links these two vertices to a singleton bicomp.
    */
   public void discoveredNode(Node node, Node parent, int timestamp) {
      // Get the Node DFI and create the BESVertex instance
      int nodeDFI = ((Integer)node.getLabel(DFS.DFI_KEY)).intValue();
      BESVertex vertex = new BESVertex(node, nodeDFI);
      vertices[nodeDFI] = vertex;
      
      // Check to see whether the Node has a DFS parent
      if (parent != null) {
         // Get DFI of parent vertex and set DFSParent property of the vertex
         int parentDFI = ((Integer)parent.getLabel(DFS.DFI_KEY)).intValue();
         BESVertex parentVertex = (BESVertex)vertices[parentDFI];
         vertex.setDFSParent(parentVertex);
         
         /* Since a DFS parent vertex was found we must create a virtual vertex
          * for the singleton bicomp
          */
         BESVirtualVertex virtual =
         new BESVirtualVertex(nodeDFI + graphSize);
         vertices[nodeDFI + graphSize] = virtual;
         virtual.setTwinLink(parentVertex);
         
         // Now construct the singleton bicomp
         createSingletonBicomp(vertex, virtual);
      }
   }
   
   /* When the DFS is finished the vertices are sorted according to their
    * low point values. This is done using the buckets.
    */
   public void finishDFS() {
      // Loop over all buckets in ascending order of the lowpoint they represent
      for (int i = 1; i < buckets.length; i++) {
         // Skip if no bucket exists for this number
         LinkedList list = buckets[i];
         if (list == null) continue;
         
         // Iterate over the bucket content
         Iterator it = list.iterator();
         while (it.hasNext()) {
            BESVertex vertex = (BESVertex)it.next();
            
            /* If this vertex has a parent append it to the parents` separated
             * DFS child list
             */
            BESVertex parent = vertex.getDFSParent();
            if (parent != null) {
               parent.addLastToSeparatedDFSChildList(vertex);
            }
         }
      }
   }
   
   /* Once the lowpoint for a node in the input graph has been calculated the
    * representing vertex is put into the bucket for this lowpoint and its
    * LowPoint property is set. When the DFS is finished the vertices can then
    * be sorted according to their lowpoints in linear time using the buckets.
    */
   public void lowPointCalculated(Node node, Integer lowpoint) {
      int lp = lowpoint.intValue();
      
      /* Using the DFI retrieve the vertex from the vertex array and set the
       * lowpoint property
       */
      BESVertex vertex =
      (BESVertex)vertices[((Integer)node.getLabel(DFS.DFI_KEY)).intValue()];
      vertex.setLowPoint(lp);
      
      // If a bucket for this lowpoint does not exist yet create one.
      if (buckets[lp] == null) {
         buckets[lp] = new LinkedList();
      }
      // Add the vertex to the bucket with its lowpoint
      buckets[lp].addLast(vertex);
   }
   
   /** This method links the vertex and the virtual vertex to a
    * singleton bicomp. The necessary BESEdge instances are created.
    */
   private void createSingletonBicomp(BESVertex vertex,
   BESVirtualVertex virtual) {
      // Create the two edge instances, one for each adjacency list
      BESEdge virtualEdge = new BESEdge(current, BESEdge.TREE);
      BESEdge vertexEdge = new BESEdge(current, BESEdge.TREE);
      
      // Link the virtual vertex with its edge and set the edge neighbour
      virtual.setLink(BESElement.CCW, virtualEdge);
      virtualEdge.setLink(BESElement.CCW, virtual);
      virtual.setLink(BESElement.CW, virtualEdge);
      virtualEdge.setLink(BESElement.CW, virtual);
      virtualEdge.setNeighbour(vertex);
      
      // Link the vertex with its edge and set the edge neighbour
      vertex.setLink(BESElement.CCW, vertexEdge);
      vertexEdge.setLink(BESElement.CCW, vertex);
      vertex.setLink(BESElement.CW, vertexEdge);
      vertexEdge.setLink(BESElement.CW, vertex);
      vertexEdge.setNeighbour(virtual);
      
      /* Set the twin link of the virtual vertex`s edge to the vertex`s edge.
       * This automatically sets the reverse twin link!
       */
      virtualEdge.setTwinLink(vertexEdge);
   }
}