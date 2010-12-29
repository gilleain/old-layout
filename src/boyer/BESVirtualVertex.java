//$Revision: 1.1 $,$Date: 2003/02/06 15:19:18 $,$Author: toersel $

package boyer;

import java.util.*;

/** The representation of a virtual vertex of the embedding strucure.
 */
public class BESVirtualVertex extends BESAbstractVertex {

   /** Creates a new BESVirtualVertex.
    * @param id A unique number being the position of this BESVirtualVertex in 
    * the vertex array created by the initialisation process.
    */
   public BESVirtualVertex(int id) {
      super(id);
   }
   
   /** Starting at this vertex, the root of a bicomp, this methods walks along
    * the external face of the bicomp and finds the next active vertex.
    * @param context A TraversalContext to determine the direction of walking.
    * @param processed A Vertex instance which is the currently processed
    * vertex. This is needed since activity is defined dynamically.
    * @return A TraversalContext containing the next active vertex found plus
    * the direction from which it was entered
    */
   public TraversalContext getNextActive(
   TraversalContext context, BESVertex processed) {
      if (context.getPosition() != this) {
         throw new IllegalArgumentException("Illegal traversal context");
      }

      /* Continue walking on the external face until an active vertex is found. 
       * Before checking this it must be ensured that we have not arrived at the 
       * bicomp root, since activity rules do not apply for virtual vertices.
       */
      do {
         context = ((BESAbstractVertex)context.getPosition())
            .getNextOnExternalFace(context);
      }  
      while (! (context.getPosition() instanceof BESVertex && 
         ((BESVertex)context.getPosition()).isActive(processed)));
      
      return context;
   }
   
   /** Performs post processing operations on the vertices in the bicomp rooted 
    * by this virtual vertex. Shortcut edges are removed from the adjacency 
    * lists. Also if a vertex has an orientation inconsistent to the bicomp 
    * root this vertex is flipped. Inconsistent orientations arise due to the
    * deferred flipping strategy of the linear time optimiziation.  
    */
   public void postProcessBicomp() {
      /* Practically what is done in this method is a DFS. But
       * the fact that the bicomp contains a complete tree edge skeleton from
       * the initalisation is used. This tree is traversed, collecting all
       * vertices encountered. For every vertex discovered all its tree edge
       * targets are added to a stack - except the DFS successor. The stack is
       * processed until it is empty.
       */
      
      // Used as stack to accumulate vertices to be processed DFS like
      LinkedList dfsStack = new LinkedList();     
      /* Composite used to push a position in the bicomp plus the product of
       * tree edge signs on the path to it together onto the processing stack
       */ 
      class StackEntry {
         public int product = 0;
         public BESAbstractVertex vertex = null;
         
         public StackEntry(BESAbstractVertex vertex, int product) {
            this.vertex = vertex;
            this.product = product;
         }
      }
      
      /* Start by pushing the bicomp root (this) into the result list and 
       * on top of the processing stack.
       */
      dfsStack.addFirst(new StackEntry(this, BESEdge.NOFLIP));
      
      while (!(dfsStack.isEmpty())) {
         /* Retrieve the next (virtual) vertex from the processing stack.
          * Also get the product of tree edge sign on the path to it.
          */
         StackEntry entry = (StackEntry)dfsStack.removeFirst(); 
         BESAbstractVertex pos = entry.vertex; 
         int product = entry.product;
         
         TraversalContext tc = 
            pos.getNextInCycle(new TraversalContext(pos, CCW));
         // Step through the adjacency list of the current (virtual) vertex
         while (tc.getPosition() != pos) {     
            BESEdge edge = (BESEdge)tc.getPosition();
            tc = tc.getPosition().getNextInCycle(tc);  
            
            // Only process tree edges here.
            if (edge.getType() == BESEdge.TREE) {        
               BESAbstractVertex neighbour = edge.getNeighbour();
               
               /* If the current position is the bicomp root add the tree edge
                * target to the processing stack immediately
                */
               if (pos == this) {
                  dfsStack.addFirst
                     (new StackEntry(neighbour, product * edge.getSign()));
                  continue;
               }
               
               /* If the edge does not lead to the dfs parent vertex or to the
                * bicomp root add the target to the processing stack
                */
               BESVertex parent = ((BESVertex)pos).getDFSParent();
               if (!(neighbour == parent || neighbour == this)) {
                  dfsStack.addFirst
                  (new StackEntry(neighbour, product * edge.getSign())); 
               }
            }
            // Remove shortcut edge if encountered
            else if (edge.getType() == BESEdge.SHORTCUT) {
               BESElement neighbourOne = edge.getLink(BESElement.CCW);
               BESElement neighbourTwo = edge.getLink(BESElement.CW);
               neighbourOne.setLink(BESElement.CW, neighbourTwo);
               neighbourTwo.setLink(BESElement.CCW, neighbourOne);
            }
         }
         
         /* Finally flip the vertex if the prodcut of the tree egde signs on 
          * the path to the vertex is BESEdge.NEEDFLIP. This must be done at
          * last because the flipping will toggle the tree edges signs again!
          */
         if (product == BESEdge.NEEDFLIP) {
            pos.flipVertex();
         }
      }
   }
}