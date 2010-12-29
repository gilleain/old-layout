//$Revision: 1.1 $,$Date: 2003/02/06 15:19:18 $,$Author: toersel $

package boyer;

/** A TraversalContext aggregates a position information in the embedding
 * plus a link indicator. This is needed because the embedding structure
 * elements are doubly-linked. Thus it is required to keep track which link was
 * used to enter or exit the current element to be able to select the other one 
 * to continue traversing the embedding structure or store this information.  
 */
public class TraversalContext {
   // The contexts position, an element of the embedding structure
   private BESElement position = null;
   
   // Indicating a direction information, assignable from BESElement constants 
   private int direction = 0;
   
   /** Creates a new TraversalContext
    * @param position The position in the embedding structure
    * @param direction An indicator which link was used to enter or exit the 
    * position. Use the constants provided by BESElement.
    */
   public TraversalContext(BESElement position, int direction) {
      this.position = position;
      setDirection(direction);
   }
   
   /** Sets the position of this TraversalContext
    * @param position The element being the position of this TraversalContext.
    */
   public void setPosition(BESElement position) {
      this.position = position;
   }
   
   /** Returns the position of this TraversalContext
    * @return The position
    */
   public BESElement getPosition() { return position; }
   
   /** Returns an indicator from which direction the position was entered or 
    * left.
    * @return The direction indicator (see constants in BESElement)
    */
   public int getDirection() { return direction; }
   
   /** Sets the indicator from which direction the position was entered or left.
    * @param direction The direction indicator (see constants in BESElement)
    */
   public void setDirection(int direction) {
      if (!(direction == BESElement.CCW || direction ==BESElement.CW)) {
         throw new IllegalArgumentException("Illegal link indicator");
      }
      this.direction = direction;
   } 
}