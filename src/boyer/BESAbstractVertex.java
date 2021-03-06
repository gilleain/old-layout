//$Revision: 1.1 $,$Date: 2003/02/06 15:19:18 $,$Author: toersel $

package boyer;

/** Abstract base class for all vertex elements of the embedding structure.
 */
public abstract class BESAbstractVertex extends BESElement {
   
   // VisitedTag property
   private BESVertex visitedTag = null;
   
   // ID Property, equals the position in the vertex array
   private int id = 0;
   
   /**Creates a new BESAbstractVertex
    * @param id A unique number being the position of this BESVertex in the
    * vertex array created by the initialisation process.
    */
   public BESAbstractVertex(int id) {
      this.id = id;
   }
   
   //overridden from BESElement to check structural constraints
   public void setTwinLink(BESElement target) {
      /* A (virtual) vertex can only be twin linked to another vertex - if we
       * don`t get one throw IllegalArgumentException
       */
      if (target == null || target instanceof BESVertex) {
         twinLink = target;
      } else {
         throw new IllegalArgumentException("Invalid structuring!");
      }
   }
   
   //overridden from BESElement to check structural constraints
   public void setLink(int which, BESElement target) {
      /* A vertex can only be linked to a BESEdge - if we don`t get one
       * throw IllegalArgumentException
       */
      if (target instanceof BESEdge) {
         link[which] = target;
      }
      else {
         throw new IllegalArgumentException("Invalid structuring!");
      }
   }
   
   /** Returns the next vertex on the external face.
    * @param direction A link indicator being the direction in which to search
    * for the next vertex on the external face
    * @return  A TraversalContext containing the next vertex on the external
    * face and the link used to enter it
    */
   public TraversalContext getNextOnExternalFace(int direction) {
      // Check direction parameter for validity
      if (direction == CCW || direction == CW) {
         /* Obtain the twin of the edge which is at the link of the passed
          * direction edge from which this vertex was entered
          */
         BESEdge twinEdge = (BESEdge)getLink(direction).getTwinLink();
         // Call helper method (see bottom)
         return getNextImpl(twinEdge, direction ^ 1);
      }
      else {
         throw new IllegalArgumentException("Illegal traversal context");
      }
   }
   
   /** Returns the next vertex on the external face.
    * @param context A TraversalContext that must contain the current vertex
    * and the link used to enter it (the vertex will be left from the opposite
    * link).
    * @return A TraversalContext containing the next vertex on the external
    * face and the link used to enter it
    */
   public TraversalContext getNextOnExternalFace(TraversalContext context) {
      if (context.getPosition() != this) {
         throw new IllegalArgumentException("Illegal traversal context");
      }
      
      /* Obtain the twin of the edge which we get if we select the opposite
       * edge from which this vertex was entered
       */
      BESEdge twinEdge =
      (BESEdge)getLink(context.getDirection() ^ 1).getTwinLink();
      //call helper method (see bottom)
      return getNextImpl(twinEdge, context.getDirection());
   }
   
   /** Marks this vertex as visited by a walkup so that a following walkup for
    * the same processed vertex can stop here.
    * @param vertex The currently processed vertex
    */
   public void setVisitedTag(BESVertex vertex) { visitedTag = vertex; }
   
   /** Returns the visited "tag" done by a previous walkup. If it is equal
    * to the processed vertex a following walkup for the same vertex can stop
    * here.
    * @return The last processed vertex for which this vertex has been in a
    * walkup path
    */
   public BESVertex getVisitedTag() { return visitedTag; }
   
   /** Returns the vertex id. This is unique for every vertex and equals the
    * position in the vertex array generated by the initial DFS run on the
    * input graph. For BESVertex instances this is also the DFI.
    * @return The ID
    */
   public int getID() {
      return id;
   }
   
   /** Flips the links of the vertex plus the links of all edges in its
    * adjacency list.
    */
   public void flipVertex() {
      /* Invert the vertex itself, calls method in BESElement which swaps links.
       * The context is advanced before the swapping so we do not have to
       * invert its direction due to the swapped links.
       */
      TraversalContext tc = getNextInCycle(new TraversalContext(this, CCW));
      this.invert();
      
      /* Invert all edges in the adjacency list. Again the context is
       * advanced before swapping the links.
       */
      while (tc.getPosition() != this) {
         BESElement element = tc.getPosition();
         tc = element.getNextInCycle(tc);
         element.invert();
         if (element instanceof BESEdge) {
            BESEdge current = (BESEdge)element;
            /* Toggle the sign of all tree edges to indicate the vertex 
             * was flipped.
             */ 
            if (current.getType() == BESEdge.TREE) {
               current.toggleSign();
            }
         }
      }
   }
   
   /** Helper method for traversing the external face. The challenge is that we
    * arrived at an edge following the twin link of an external face edge of
    * the previous vertex in traversal. Now we must determine from which
    * direction the next vertex is entered so we can deal with inconsistent
    * vertex orientations (due to time optimization) during the algorithm
    * progress
    */
   private TraversalContext getNextImpl(BESElement edge, int prevDirection){
      BESElement ccw = edge.getLink(CCW);
      BESElement cw = edge.getLink(CW);
      BESAbstractVertex vertex = null;
      
      /* Special case for singleton bicomps to maintain traversal direction.
       * If both links of the edge refer to the same vertex we assume this
       * is a singleton bicomp.
       */
      if (ccw == cw) {
         vertex = (BESAbstractVertex)edge.getLink(prevDirection);
         return new TraversalContext(vertex, prevDirection);
      }
      
      /* If we are not in a singleton bicomp we simply look which link
       * of the edge leads to a vertex and select it
       */
      if (ccw instanceof BESAbstractVertex) {
         vertex = (BESAbstractVertex)ccw;
      }
      else {
         vertex = (BESAbstractVertex)cw;
      }
      
      /* Finally we look from which direction the vertex was entered and augment
       * these information in a traversal context
       */
      if (vertex.getLink(CCW) == edge) {
         return new TraversalContext(vertex, CCW);
      }
      else {
         return new TraversalContext(vertex, CW);
      }
   }
}