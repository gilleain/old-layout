//$Revision: 1.1 $,$Date: 2003/02/06 15:19:18 $,$Author: toersel $

package boyer;

import de.fhstralsund.vinets.structure.*;

 /** Representation of an edge in the embedding structure. Note that for every
 * edge of the input graph there are two BESEdge instances - one in each
 * adjacency list of the incident nodes. These two instances are linked through
 * their twin link. Furthermore each BESEdge has a property called Neighbour,
 * which is a reference to the vertex in the adjacency list of the twin. It can
 * thus be obtained easily.
 * Besides tree and backedges there is a third type of BESEdge objects. The
 * shortcut edges are used during the embedding process. Sometimes large paths
 * on the external face of bicomps may become inactive. So they could be
 * traversed repeatedly without having any impact on the result beside wasting
 * execution time. Those paths are therefore "bridged" by a shortcut edge. These
 * are removed by a post processing of the embedding structure.
 */
public class BESEdge extends BESElement {

   // Referred property
   private Edge ref = null;

   // Neighbour property
   private BESAbstractVertex neighbour = null;

   // Type property
   private int type;

   // Edge sign
   private int sign = NOFLIP;

   /** Represents a DFS tree edge of the input graph
    */
   public static final int TREE = 0;

   /** Represents a DFS back edge of the input graph
    */
   public static final int BACK = 1;

   /** Represents a short cut edge in the embedding which is temporarily
    * inserted into structure to bypass inactive regions and save execution
    * time.
    */
   public static final int SHORTCUT = 2;

   /** The sign of a tree edge whose neighbour vertex is not required to be 
    * flipped to recover a consistent embedding.
    */
   public static final int NOFLIP = 1;

   /** The sign of a tree edge whose neighbour vertex must be flipped to recover
    * a consistent embedding.
    */
   public static final int NEEDFLIP = -1;

   /** Creates a new BESEdge
    * @param ref A reference to the appropriate edge in the input graph to
    * transfrom the embedding back. Can be null if this is a shortcut edge.
    * @param type The type of the edge, either TREE, BACK or SHORTCUT
    */
   public BESEdge(Edge ref, int type) {
      this.ref = ref;
      if (type == TREE || type == BACK || type == SHORTCUT) {
         this.type = type;
      }
      else throw new IllegalArgumentException();
   }

   /* Overridden from BESElement to check structural constraints and create
    * the reverse link automatically
    */
   public void setTwinLink(BESElement target) {
      /* A edge can only be twin linked to another edge - if we
       * don`t get one throw IllegalArgumentException
       */
      if (target instanceof BESEdge) {
         twinLink = target;
         target.twinLink = this;
      }
      else {
         throw new IllegalArgumentException("Invalid structuring!");
      }
   }

   /** Returns the edge of the input graph represented by this BESEdge.
    * return An edge of the input graph represented by this BESEdge
    */
   public Edge getReferred() {
      return ref;
   }

   /** Returns the neighbour vertex of this BESEdge. This is defined to be the
    * vertex that is in the adjacency list of the twin edge of this BESEdge.
    * @return The neighbour vertex.
    */
   public BESAbstractVertex getNeighbour() {
      return neighbour;
   }

   /** Sets the neighbour vertex of this BESEdge. This is defined to be the
    * vertex that is in the adjacency list of the twin edge of this BESEdge.
    * @param neighbour The neighbour vertex.
    */
   public void setNeighbour(BESAbstractVertex neighbour) {
      this.neighbour = neighbour;
   }

   /** Returns the type of this BESEdge.
    * @return The type code, either TREE, BACK or SHORTCUT
    */
   public int getType() {
      return type;
   }

   /** Returns the flip sign of this BESEdge.
    * @return The flip sign, either NOFLIP or NEEDFLIP
    */
   public int getSign() {
      return sign;
   }

   /** Toggles the flip sign of this BESEdge. If it is NOFLIP it will become
    * NEEDFLIP and vice versa.
    */
   public void toggleSign() {
      sign = sign * NEEDFLIP;
   }
}