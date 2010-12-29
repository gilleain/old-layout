//$Revision: 1.2 $,$Date: 2003/05/22 09:39:46 $,$Author: toersel $

package boyer;

import de.fhstralsund.vinets.structure.*;

import java.util.*;

/** Representation of a vertex in the embedding structure. There is exactly one
 * BESVertex for each Node of the input graph.
 */
public class BESVertex extends BESAbstractVertex {
   // Referred property (pointing to the represented Node of the input graph)
   private Node ref = null;
   
   /* Holds the separated DFS childs. Using a LinkedHashSet here to have a
    * data strucure with:
    * - stable (user defined) element iteration order
    * - constant time seeks and deletes
    * The drawback is that this requires at least JDK 1.4!
    */
   private LinkedHashSet separatedDFSChilds = new LinkedHashSet();
   
   // Holds the pertinent bicomps
   private LinkedList pertinentBicomps = new LinkedList();
   
   // LeastAncestor property
   private int leastAncestor = Integer.MAX_VALUE;
   
   // LowPoint property
   private int lowPoint = 0;
   
   // AdjacentTo property
   private BESVertex adjacentTo = null;
   
   // DFSParent property 
   private BESVertex dfsParent = null;
   
   /** Flag for externally active nodes.
    */
   public static final int EXTERNAL = 0;
   
   /** Flag for internally active nodes.
    */
   public static final int INTERNAL = 1;
   
   /** Flag for inactive nodes.
    */
   public static final int INACTIVE = 2;
   
   /** Creates a new BESVertex.
    * @param ref The Node of the input graph this vertex represents.
    * @param id A unique number being the position of this BESVertex in the
    * vertex array created by the initialisation process.
    */
   public BESVertex(Node ref, int id) {
      super(id);
      this.ref = ref;
      setTwinLink(this);
   }
   
   /** Returns the Node of the input graph this vertex represents.
    * @return The Node of the input graph represented by this vertex
    */
   public Node getReferred() {
      return ref;
   }
   
   /** Removes the passed BESVertex from the separated DFS child list.
    * @param entry The BESVertex to remove from the list 
    */
   public void removeFromSeparatedDFSChildList(BESVertex entry) {
      separatedDFSChilds.remove(entry);
   };
   
   /** Appends the passed BESVertex to the separated DFS child list.
    * @param entry The BESVertex to append to the list 
    */
   public void addLastToSeparatedDFSChildList(BESVertex entry) {
      /* LinkedHashSet ensures that the order of add operations equals the
       * iteration order. Therefore calling add makes sure, the element will
       * be presented by the Iterator after all elemenmts that were inserted 
       * before. 
       */ 
      separatedDFSChilds.add(entry);
   };
   
   /** Returns the first entry form the separated DFS childs list.
    * @return The first element
    */ 
   public BESVertex getFirstSeparatedDFSChild() {
      if (separatedDFSChilds.isEmpty()) return null;
      else return (BESVertex)separatedDFSChilds.iterator().next();
   }
   
   /** Checks whether this BESVertex has separated DFS childs.
    * @return True if the BESVertex has separated DFS childs, false otherwise
    */ 
   public boolean hasSeparatedDFSChild() {
      return (!separatedDFSChilds.isEmpty());
   }
   
   /** Appends the passed BESVirtualVertex (which is a bicomp root) to the end
    * of the pertinent bicomp list. This is done if the bicomp is externally 
    * active to ensure all internally active bicomps, that are prepended, will
    * be processed before - given that the list is processed from front to back.
    * @param bicompRoot A BESVirtualVertex being root of an externally active
    * pertinent bicomp.  
    */
   public void appendPertinentBicomp(BESVirtualVertex bicompRoot) {
      if (bicompRoot.getTwinLink() != this ) {
         throw new IllegalArgumentException("Invalid bicomp or null!");
      }
      pertinentBicomps.addLast(bicompRoot);
   }
   
   /** Prepends the passed BESVirtualVertex (which is a bicomp root) to the 
    * front of the pertinent bicomp list. This is done if the bicomp is 
    * internally active to ensure all externally active bicomps, that are 
    * appended, will be processed afterwards - given that the list is processed 
    * from front to back.
    * @param bicompRoot A BESVirtualVertex being root of an internally active
    * pertinent bicomp.  
    */
   public void prependPertinentBicomp(BESVirtualVertex bicompRoot) {
      if (bicompRoot.getTwinLink() != this ) {
         throw new IllegalArgumentException("Invalid bicomp or null!");
      }
      pertinentBicomps.addFirst(bicompRoot);
   }
   
   /** Returns and removes the first element from the pertinent bicomp list.
    * @return The first element from the pertinent bicomp list
    */
   public BESVirtualVertex retrievePertinentBicomp() {
      if (pertinentBicomps.isEmpty()) return null;
      return (BESVirtualVertex) pertinentBicomps.removeFirst();
   }
   
   /** Returns true if this vertex has pertinent bicomps.
    * @return True if this vertex has at least one pertinent bicomp, 
    * false otherwise.
    */
   public boolean hasPertinentBicomp() {
      return (!pertinentBicomps.isEmpty());
   }
   
   /** Returns the LeastAncestor property of this BESVertex. This is the Node 
    * with the lowest DFI the Node this BESVertex represents has a back edge
    * to. 
    * @return The DFI of the ancestor with the smallest DFI
    */
   public int getLeastAncestor() {
      return leastAncestor;
   }
   
   /** Sets the LeastAncestor property of this BESVertex. This is the Node 
    * with the lowest DFI the Node this BESVertex represents has a back edge
    * to. The property is set only if the parameter is smaller than the 
    * current value.
    * @param dfi The DFI of an ancestor
    */
   public void setLeastAncestorIfSmaller(int dfi) {
      if (dfi > 0 && dfi < leastAncestor) {
         leastAncestor = dfi;
      }
   }
   
   /** Stores the DFS lowpoint of the Node this BESVertex represents.
    * @param lowPoint The lowpoint
    */
   public void setLowPoint(int lowPoint) { this.lowPoint = lowPoint; }
   
   /** Returns the DFS lowpoint of the Node this BESVertex represents.
    * @return The lowpoint
    */
   public int getLowPoint() { return lowPoint; }
   
   /** Sets the AjacentTo property of this BESVertex. This is set during
    * walkup to the currently processed vertex, so the walkdown knows a back
    * edge must be embedded from this vertex.
    * @param vertex The BESVertex this BESVertex is adjacent to.
    */ 
   public void setAdjacentTo(BESVertex vertex) { adjacentTo = vertex; }
   
   /** Returns the AjacentTo property of this BESVertex. This is set during
    * walkup to the currently processed vertex, so the walkdown knows a back
    * edge must be embedded from this vertex. 
    * @return The vertex this BESVertex is adjacent to.   
    */
   public BESVertex getAdjacentTo() { return adjacentTo; }
   
   /** Sets a reference to the BESVertex representing the DFS parent node of
    * this vertex.
    * @param parent The BESVertex representing the DFS parent node of this
    * BESVertex.
    */   
   public void setDFSParent(BESVertex parent) { dfsParent = parent; }
   
   /** Returns the BESVertex representing the DFS parent.
    * @return The BESVertex representing the DFS parent
    */   
   public BESVertex getDFSParent() { return dfsParent; }
   
   /** Checks whether this vertex is pertinent for the currently processed 
    * vertex.
    * @param processed The currently processed vertex
    * @return True if this vertex is pertinent, false otherwise.
    */
   public boolean isPertinent(BESVertex processed) {
      return (adjacentTo == processed || hasPertinentBicomp() == true);
   }
   
   /** Returns the activity level of this vertex. The return value is one
    * of the constants NONE, INTERNAL or EXTERNAL.
    * @param processed The vertex which is currently processed by the algorithm.
    * @return the activity level. 
    */
   public int getActivityStatus(BESVertex processed) {
      /* The vertex is externally active if: 
       * a) It has a back edge to a vertex with a lower DFI than the currently 
       * processed vertex 
       * OR
       * b) It has a dfs child in a separate bicomp which has a back edge to 
       * vertex with a lower DFI than the currently processed vertex.
       */ 
      if (leastAncestor < processed.getID() || (hasSeparatedDFSChild() &&
         getFirstSeparatedDFSChild().lowPoint < processed.getID())) {
         return EXTERNAL;
      }
      
      /* If the vertex is not externally active but is pertinent (see method
       * isPertinent) then it is considered to be internally active
       */ 
      if (isPertinent(processed) == true) {
         return INTERNAL;
      }
      
      // Inactive otherwise
      else return INACTIVE;
   }
   
   /** Checks whether this vertex is active, either internally or externally.
    * @param processed The vertex which is currently processed by the algorithm
    * @return True if the vertex is active in any kind, false otherwise 
    */
   public boolean isActive(BESVertex processed) {
      int status = getActivityStatus(processed);
      return (status == EXTERNAL || status == INTERNAL);
   }
   
   /** Returns the cyclic order of this vertex adjacent edges in an Iterator.
    * @return An Iterator over the adjacent edges of this vertex in correct cyclic order. 
    */
   public Iterator enumEdges() {
      List list = new LinkedList();
     
      TraversalContext tc =
      this.getNextInCycle(new TraversalContext(this, CCW));
      /* Step through the vertex`s adjacency list appending the edge target
       * names to the result string
       */ 
      while (tc.getPosition() != this) {
         BESEdge edge = (BESEdge)tc.getPosition();
         list.add(edge);
         tc = tc.getPosition().getNextInCycle(tc);
      }
      
      return list.iterator();
   }
}
