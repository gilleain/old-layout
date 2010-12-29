//$Revision: 1.1 $,$Date: 2003/02/06 15:19:18 $,$Author: toersel $

package boyer;

/** Base class for all embedding structure elements. It contains three
 * links to other BESElements. There are two links for the
 * adjacency list, one being the next element clockwise (CW) the other in 
 * counter-clockwise (CCW) direction. The third link is called the twin
 * link. For edges it connects to the egde representation in other vertex
 * the edge is incident to. For virtual vertices (BESVirtualVertex) 
 * the twin link points to appropriate vertex (BESVertex) object. In vertices
 * (BESVertex) the twin link is a self loop.    
 */
public abstract class BESElement {
   
   /** Holds the two adjacency list links.
    */
   protected BESElement[] link = new BESElement[2];
   
   /** The twin link.
    */
   protected BESElement twinLink = null;
   
   /** Counter clockwise direction
    */
   public static final int CCW = 0;
   
   /** Clockwise direction
    */
   public static final int CW = 1;
   
   /** Creates a new BESElement. The links point initially to the element 
    * itself. The twin link is null.
    */
   public BESElement() {
      link[CCW] = this;
      link[CW] = this;
   }
   
   /** Sets the specified link to the supplied target.
    * @param which Which link to set, either CCW or CW
    * @param target The link target 
    */   
   public void setLink(int which, BESElement target) {
      if (!(which == BESElement.CCW || which ==BESElement.CW)) {
         throw new IllegalArgumentException("Illegal link indicator");
      }
      link[which] = target;
   }
   
   /** Returns the element referenced by the twin link.
    * @return The target of the twin link
    */   
   public BESElement getTwinLink() { return twinLink; }
   
   /** Returns the element at the given link.
    * @param which A link indicator, either CCW for counter-clockwise or CW
    * for clockwise link.
    * @return The Element at the supplied link
    */   
   public BESElement getLink(int which) {
      if (!(which == BESElement.CCW || which ==BESElement.CW)) {
         throw new IllegalArgumentException("Illegal link indicator");
      }
      return link[which];
   }
   
   /** Swaps the links of this embbeding structure element. 
    */
   public void invert() {
      BESElement temp = link[CW];
      link[CW] = link[CCW];
      link[CCW] = temp;
   }
   
   /** Sets the target of the twin link.
    * @param target The target 
    */   
   public abstract void setTwinLink(BESElement target);
   
   /** Returns the next element in the adjacency list. The supplied 
    * TraversalContext indicates the prior movement direction.
    * @param context The TraversalContext containing this element and an
    * indicator from which direction it was entered
    * @return A TraversalContext containing the next element and an
    * indicator from which direction it was entered 
    */   
   public TraversalContext getNextInCycle(TraversalContext context) {
      BESElement position = context.getPosition();
      if (position != this) {
         throw new IllegalArgumentException("Illegal context!");
      }
      
      /* Choose the link opposing the link in the TraversalContext, as it 
       * denotes the link to enter the position - so we must use the other link 
       * to exit it. Create a new TraversalContext containing the next element
       * and return it.
       */ 
      BESElement next = link[context.getDirection() ^ 1];
      return new TraversalContext(next, context.getDirection());
   }
}