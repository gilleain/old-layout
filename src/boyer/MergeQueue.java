//$Revision: 1.1 $,$Date: 2003/02/06 15:19:18 $,$Author: toersel $

package boyer;

import java.util.*;

/** Simple queue data structure (FIFO) to store the TraversalContexts of exit 
 * and entry during descending into child bicomps in the walkdown process.
 */
public class MergeQueue {
   
   // Holding the TraversalContexts, this is basically wrapped to be a queue 
   private LinkedList queue = new LinkedList();

   /** Pushes a TraversalContext into the end of the MergeQueue.
    * @param context The TraversalContext to be pushed into the MergeQueue.
    */
   public void push(TraversalContext context) {
      queue.addLast(context);
   }
   
   /** Returns and removes the first TraversalContext from the MergeQueue.
    * @return The first TraversalContext from the queue
    */
   public TraversalContext pull() {
      return (TraversalContext)queue.removeFirst();
   }
   
   /** Checks whether the MergeQueue is empty or not.
    * @return True if the queue is empty, false otherwise
    */
   public boolean isEmpty() {
      return queue.isEmpty();
   }
}