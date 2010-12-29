//$Revision: 1.5 $,$Date: 2003/05/26 12:42:17 $,$Author: toersel $

package boyer;

import de.fhstralsund.vinets.algorithm.*;
import de.fhstralsund.vinets.structure.*;
import de.fhstralsund.vinets.algorithm.dfs.*;

import java.util.*;

/** Linear time realisation of the planar embedding algorithm by John Boyer and
 * Wendy Myrvold. If the input graph is planar, a combinatorial embedding is
 * calculated. This means the cyclic edge order for every node is computed, so
 * that the graph can be embedded in the plane without edge crossings.
 * This algorithm only processes undirected graphs!
 */
public class BoyerPlanarEmbedder implements Algorithm {
   
   // Holds the vertices and the virtual vertices, received from inital DFS run
   private BESAbstractVertex[] vertices = null;
   
   // The node count of the graph
   private int graphSize = 0;
   
   // Indicator whether the graph is planar, set during execution
   private boolean planar = true;
   
   private LinkedList resultBicompRoots = new LinkedList();
   
   public Parameter execute(Parameter params) {
      // Reject directed/mixed graphs as input
      if (!accept(params)) {
         return params;
      }
      
      graphSize = params.getGraph().countNodes();
      
      /* Due to Euler a graph with more than two nodes can not be planar if it
       * has more than (3 * (node count) - 6) edges. If so, we can break
       * already here and avoid further costly calculations.
       */
      if ((graphSize > 2) &&
      (params.getGraph().countEdges() > ((3 * graphSize) - 6))) {
         params.setMessage("Graph is NOT planar");
         return params;
      }
      
      /* For the initialisation works a DFS run + lowpoint calculation on the
       * input graph is required. This starts the dfs which will send events to
       * the lowpoint calculation which will send events to the initialisation
       * (plus the events from the dfs itself)
       */
      DFS dfs = new DFS();
      DFSLowPointCalculation lpc = new DFSLowPointCalculation();
      InitialDFSRun dfsrun = new InitialDFSRun(graphSize);
      dfs.addListener(dfsrun);
      dfs.addListener(lpc);
      lpc.addListener(dfsrun);
      dfs.execute(params);
      
      // Get the prepared array with the vertices linked to singleton bicomps
      vertices = dfsrun.getVertexArray();
      
      // Perform main algorithm
      String result = performPlanarityCheck();
      
      /* If the graph is planar perform post processing on the embedding
       * structure and retrieve the cyclic ordering of the edges that form
       * the combinatorial planar embedding.
       */
      if (planar) {
         postProcessStructure();
         result += dumpEdgeOrdering();
      }
      
      // And return result
      params.setMessage(result);
      return params;
   }
   
   public boolean accept(Parameter params) {
      Graph graph = params.getGraph();
      if (graph != null && graph.isUndirected()) { 
         return true;
      } else {
         params.setMessage("Can only be used on undirected graphs");
         return false;
      }
   }
   
   public String getHint() { return "Planar embedding algorithm";}
   
   public String getName() { return "Boyer";}
   
   /** After the execution of the algorithm this indicates whether the
    * input graph was found to be planar. Make sure this is called AFTER
    * the execution of the algorithm!
    * @return True if the input graph is planar, false otherwise
    */
   public boolean isPlanar() {
      return planar;
   }
   
   /** After the execution of the algorithm if the input graph was planar this
    * returns a List containing the roots of the result bicomp. There is one
    * root entry for every connected component of the input graph. Note that
    * since the virtual vertices are finally merged into their vertices the
    * bicomps are rooted by BESVertex instances not BESVirtualVertex instances.
    * If the input graph was not planar the returned List is empty.
    * @return A List of BESVertex instances being the result bicomps roots
    */
   public List getResultBicompRoots() {
      if (planar) return resultBicompRoots;
      else return null;
   }
   
   /**
    * Returns the cyclic edge ordering of all input Nodes of the graph if
    * it was found to be planar. A HashMap is returned, containing for every
    * Node used as key an ArrayList containing the Edge references in the 
    * correct order.
    * @return A HashMap containing the edge ordering for every Node if the
    * input graph was planar, null otherwise.
    */
   public HashMap getCyclicEdgeOrdering() {
      HashMap result = new HashMap();
      
      for (int pos = 1; pos <= graphSize ; pos++) {
         BESVertex vertex = (BESVertex)vertices[pos];
         ArrayList edgeList = new ArrayList();
         
         Node node = vertex.getReferred();
         Iterator edges = vertex.enumEdges();
         while (edges.hasNext()) {
            BESEdge current = (BESEdge)edges.next(); 
            edgeList.add(current.getReferred());
         }
         
         result.put(node, edgeList);
      }
     
      return result;
   }
   
   /** Algorithm main loop. Steps through all vertices in descending DFI order,
    * collects the back edges TO this vertex, performs a walkup for every
    * back edge and performs a walkdown for every child bicomp of the vertex.
    * If, in a step, not all back edges could be embedded, then the graph is not
    * planar.
    */
   private String performPlanarityCheck() {
      
      // Holds the source vertices of the back edges to the current vertex
      LinkedList backEdgeSources = new LinkedList();
      
      // Holds all the DFS descendants of the currently processed vertex
      LinkedList treeEdgeDescendants = new LinkedList();
      
      /* Increased for every walkup done (=one back edge) in a step, decreased
       * for every back edge embedded. If this is larger than zero after the
       * walkdowns the graph is not planar, since the walkdown failed to embed
       * backedges
       */
      int numBackEdges = 0;
      
      for (int i = graphSize; i > 0; i--) {
         backEdgeSources.clear();
         treeEdgeDescendants.clear();
         numBackEdges = 0;
         BESVertex processed = ((BESVertex)vertices[i]);
         Node theNode = processed.getReferred();
         
         /* Determine back edges the lead to the currently processed vertex and
          * find all DFS descendants of it
          */
         Iterator edges = theNode.undirectedEdges();
         while (edges.hasNext()) {
            Edge edge = (Edge)edges.next();
            if (edge.getLabel(DFS.EDGE_TYPE_KEY) == DFS.BACK_EDGE
            && edge.getLabel(InitialDFSRun.FROM_KEY) != theNode) {
               Node other = edge.getEnds().getOther(theNode);
               backEdgeSources.add(
               (BESVertex)vertices[((Integer)other
               .getLabel(DFS.DFI_KEY)).intValue()]);
            }
            else if (edge.getLabel(DFS.EDGE_TYPE_KEY) == DFS.TREE_EDGE
            &&  edge.getLabel(InitialDFSRun.FROM_KEY) == theNode) {
               Node other = edge.getEnds().getOther(theNode);
               treeEdgeDescendants.add(
               (BESVertex)vertices[((Integer)other
               .getLabel(DFS.DFI_KEY)).intValue()]);
            }
         }
         
         // Perform walkup for every back edge
         Iterator walkups = backEdgeSources.iterator();
         while (walkups.hasNext()) {
            walkUp((BESVertex)walkups.next(), processed);
            numBackEdges++;
         }
         
         /* If there are no back edges to embed at this vertex skip to next
          * loop iteration
          */
         if (numBackEdges == 0) continue;
         
         // Perform walkdown for every dfs child
         Iterator walkdowns = treeEdgeDescendants.iterator();
         while (walkdowns.hasNext()) {
            BESVertex descendant = (BESVertex)walkdowns.next();
            numBackEdges -=
            walkDown((BESVirtualVertex)vertices[descendant.getID() + graphSize],
            processed);
         }
         // Check whether all back edges were embedded, break if not
         planar = (numBackEdges == 0);
         if (!planar) break;
      }
      
      if (planar) return ("Graph is planar");
      else return ("Graph is NOT planar");
   }
   
   /** Performs the walkup for the specified vertex.
    * @param from The back edge source
    * @param to The back edge target (the currently processed vertex)
    */
   private void walkUp(BESVertex from, BESVertex processed) {
      /* Mark the vertex as adjacent to the processed vertex. So the walkdown
       * knows that a back edge must be embedded when this vertex is
       * encountered.
       */
      from.setAdjacentTo(processed);
      
      /* Initialise two TraversalContexts since the bicomps are scanned in
       * both directions during the walkup process (on path may be a lot
       * shorter than the other side)
       */
      TraversalContext first = from.getNextOnExternalFace(BESElement.CCW);
      TraversalContext second = from.getNextOnExternalFace(BESElement.CW);
      BESVirtualVertex virtual = null;
      
      // While we have not arrived at the currently processed vertex.
      while (first.getPosition() != processed) {
         BESAbstractVertex firstVertex = (BESAbstractVertex)first.getPosition();
         BESAbstractVertex secondVertex = (BESAbstractVertex)second.getPosition();
         
         /* Break the while loop if we encounter a vertex that was on walkup
          * path before. The rest of the path is "shared".
          */
         if (firstVertex.getVisitedTag() == processed ||
         secondVertex.getVisitedTag() == processed) {
            break;
         }
         
         /* Mark the current position, so a following walkup for the
          * the currently processed vertex can stop here.
          */
         firstVertex.setVisitedTag(processed);
         secondVertex.setVisitedTag(processed);
         
         // Check if we reached the bicomp root
         if (firstVertex instanceof BESVirtualVertex) {
            virtual = (BESVirtualVertex)firstVertex;
         }
         else if (secondVertex instanceof BESVirtualVertex) {
            virtual = (BESVirtualVertex)secondVertex;
         }
         else virtual = null;
         
         // If we have arrived at the bicomp root
         if (virtual != null) {
            // Get the vertex entry for the virtual vertex (= bicomp root)
            BESVertex descendant =
            (BESVertex)vertices[virtual.getID() - graphSize];
            BESVertex vertex = descendant.getDFSParent();
            
            // Check if this vertex is the processed vertex.
            if (vertex != processed) {
               /* Check if the bicomp is externally active. If so the bicomp
                * is appended to the pertinent bicomp list of the vertex.
                * Otherwise it is prepended. This way, all internally active
                * bicomps are processed before the externally active ones, when
                * the list is processed from the front.
                */
               if (descendant.getLowPoint() < processed.getID()) {
                  vertex.appendPertinentBicomp(virtual);
               }
               else {
                  vertex.prependPertinentBicomp(virtual);
               }
               
               /* Start walking into both directions along the external
                * face in the new bicomp that was reached via the virtual
                * vertex.
                */
               first = vertex.getNextOnExternalFace(BESElement.CCW);
               second = vertex.getNextOnExternalFace(BESElement.CW);
            }
         }
         /* We don`t have reached the bicomp root so we continue travelling
          * on the bicomp`s external face in both directions
          */
         else {
            first = firstVertex.getNextOnExternalFace(first);
            second = secondVertex.getNextOnExternalFace(second);
         }
      }
   }
   
   /** Perform the walkdown on the given virtual vertex.
    * @param start The virtual vertex of a child bicomp of the currently
    * processed vertex
    * @param processed The currently processed vertex
    * @return The number of back edges that were embedded
    */
   private int walkDown(BESVirtualVertex start, BESVertex processed) {
      /* Stores the TraversalContexts when leaving/entering bicomps while
       * descending into child bicomps for later merging
       */
      MergeQueue mq = new MergeQueue();
      
      // Used to count number of embedded backedges
      int embedded = 0;
      
      /* Check if short cut edges can be embedded. For this, the bicomp must
       * be externally active so that if at some time the bicomp has a degree
       * two face it will be bisected through merging in of another
       * bicomp later.
       */
      BESVertex descendant = (BESVertex)vertices[start.getID() - graphSize];
      BESVertex vertexEntry = (BESVertex)start.getTwinLink();
      boolean couldEmbedShortCircuit = false;
      if (descendant.getLowPoint() < vertexEntry.getID()) {
         couldEmbedShortCircuit = true;
      }
      
      // Go counter-clockwise, then clockwise around the child bicomp
      for (int out = 0; out <= 1; out++) {
         TraversalContext next =
         start.getNextOnExternalFace(new TraversalContext(start, out ^1));
         
         /* Walk around the bicomp (and descend into child bicomps)
          * until the root is reached again
          */
         while (next.getPosition() != start) {
            BESVertex position = (BESVertex)next.getPosition();
            
            /* Check if the the vertex has a back edge to the currently
             * processed vertex that must be embedded
             */
            if (position.getAdjacentTo() == processed) {
               mergeBicomps(mq);
               
               /* The reference to the original back edge was set in the
                * initial DFS run
                */
               Edge original = (Edge)
                  position.getReferred().getLabel(processed.getReferred());
               
               embedEdge(new TraversalContext(start, out), next, BESEdge.BACK, original);
               position.setAdjacentTo(null);
               embedded++;
            }
            
            // Check if the vertex has a pertinent bicomp
            if (position.hasPertinentBicomp() == true) {
               /* Save the vertex and information from which direction it was
                * entered in the merge queue
                */
               mq.push(next);
               
               // Get the first pertinent bicomp
               BESVirtualVertex bicompRoot = position.retrievePertinentBicomp();
               
               /* Scan the new bicomp in both directions from the root to find
                * the first active vertex
                */
               TraversalContext one = bicompRoot.getNextActive(
               new TraversalContext(bicompRoot, BESElement.CW), processed);
               TraversalContext two = bicompRoot.getNextActive(
               new TraversalContext(bicompRoot, BESElement.CCW), processed);
               
               /* Preferably select internally active vertices, pertinent
                * vertices otherwise. If both are externally active but not
                * pertinent (stopping vertices) this is a halting condition
                * in the next iteration - therefore choice is arbitrary then.
                */
               if (((BESVertex)one.getPosition()).
               getActivityStatus(processed) == BESVertex.INTERNAL) {
                  next = one;
               }
               else if (((BESVertex)two.getPosition()).
               getActivityStatus(processed) == BESVertex.INTERNAL) {
                  next = two;
               }
               else if (((BESVertex)one.getPosition()).isPertinent(processed)) {
                  next = one;
               }
               else {
                  next = two;
               }
               
               /* After choosing a direction push information about the
                * bicomp root and the direction it was left into the merge queue
                */
               TraversalContext direction = new TraversalContext(bicompRoot,
               (next == one) ? BESElement.CCW : BESElement.CW);
               mq.push(direction);
            }
            
            /* If the current position is not active then the next vertex on the
             * external face is obtained (while keeping the direction). If
             * possible, a shortcut edge is embedded
             */
            else if (!position.isActive(processed)) {
               next = position.getNextOnExternalFace(next);
               if (couldEmbedShortCircuit &&
               ((BESVertex)next.getPosition()).getAdjacentTo() != vertexEntry) {
                  embedEdge(new TraversalContext(start, out),
                  next, BESEdge.SHORTCUT, null);
               }
            }
            else {
               // A stopping vertex was encountered, break therefore
               break;
            }
         }
         // If bicomps remained unmerged, break (graph not planar, k33 subgraph)
         if (!mq.isEmpty()) break;
      }
      return embedded;
   }
   
   /** Processes the contents of a MergeQueue and merges the contained bicomps.
    * @param mq The MergeQueue
    */
   private void mergeBicomps(MergeQueue mq) {
      // Proceed as long as the merge queue is not empty
      while (!mq.isEmpty()) {
         /* Pull two TraversalContexts from the merge queue. The first (in)
          * contains the vertex that had a pertinent bicomp into that was
          * descended plus information about from which direction it was
          * entered. The second context contains the root of the pertinent
          * bicomp plus the information in which direction it was left.
          */
         TraversalContext in = mq.pull();
         TraversalContext out = mq.pull();
         
         /* If the directions of in and out are equal then out must be flipped.
          * Since the direction of "in" means the direction of entering and the
          * direction of "out" means the direction of leaving the equality of
          * those indicates that a flip must occur to make sure the back edge
          * is embedded consistently along one "side".
          */
         if (in.getDirection() == out.getDirection()) {
            BESVirtualVertex virtual = (BESVirtualVertex)out.getPosition();
            //invert the virtual vertex
            virtual.flipVertex();
            
            //invert out since the virtual vertex was flipped
            out.setDirection(out.getDirection() ^ 1);
         }
         
         mergeVertices(in, out);
      }
   }
   
   /** Merges a virtual vertex into a vertex. After merging:
    * 1) The virtual vertex is completely unlinked from the structure
    * 2) The edges from which the vertex was entered and to which the
    * virtual vertex was left are joined
    * 3) The other edge directly incident in the adjacency list of the
    * virtual vertex is linked with the vertex from the direction it was
    * entered from
    * @param in A TraversalContext containing the vertex plus the direction
    * it was entered from
    * @param out A TraversalContext containing the virtual vertex plus the
    * direction it was left
    */
   private void mergeVertices(TraversalContext in, TraversalContext out) {
      BESVertex vertex = (BESVertex)in.getPosition();
      BESVirtualVertex virtual = (BESVirtualVertex)out.getPosition();
      
      // Remove this descendant from the separated DFS childs list
      BESVertex desc = (BESVertex)vertices[virtual.getID() - graphSize];
      vertex.removeFromSeparatedDFSChildList(desc);
      
      // Set the twin link to null to mark this virtual vertex as merged
      virtual.setTwinLink(null);
      
      /* Change the Neighbour property of the virtual vertex`s edges` twins to
       * indicate incidence to the vertex
       */
      TraversalContext context =
      virtual.getNextInCycle(new TraversalContext(virtual, BESElement.CCW));
      do {
         BESEdge current = (BESEdge)context.getPosition();
         BESEdge twin = (BESEdge)current.getTwinLink();
         twin.setNeighbour(vertex);
         context = current.getNextInCycle(context);
      }
      while (context.getPosition() != virtual);
      
      /* Get the elements from which the vertex was entered and to which the
       * virtual vertex was left and connect these. This also works
       * for vertices that have empty adjacency lists (vertices for dfs root
       * nodes) since the links point to the element self. Thus they can be
       * merged with virtual vertices of their child bicomps.
       */
      TraversalContext inFrom = vertex.getNextInCycle(
      new TraversalContext(vertex, in.getDirection() ^ 1));
      TraversalContext outTo = virtual.getNextInCycle(
      new TraversalContext(virtual, out.getDirection() ^ 1));
      BESElement inFromElem = inFrom.getPosition();
      BESElement outToElem = outTo.getPosition();
      inFromElem.setLink(inFrom.getDirection(), outToElem);
      outToElem.setLink(outTo.getDirection(), inFromElem);
      
      /* Connect the other edge directly incident in the adjacency list of the
       * virtual vertex with the link of the vertex from which it was entered
       */
      TraversalContext other =
      virtual.getNextInCycle(new TraversalContext(virtual, out.getDirection()));
      vertex.setLink(in.getDirection(), other.getPosition());
      other.getPosition().setLink(other.getDirection(), vertex);
   }
   
   /** Embeds backedges and shortcut edges between a vertex and a virtual
    * vertex.
    * @param to A TraversalContext containing the edge target plus the link
    * of the virtual vertex to which the new edge should connect
    * @param from A TraversalContext containing the edge source plus the link
    * of the vertex to which the new edge should connect
    * @param type The edge type, either BACK or SHORTCUT
    */
   private void embedEdge(TraversalContext to, TraversalContext from, int type, Edge original){
      BESVirtualVertex processed = (BESVirtualVertex)to.getPosition();
      BESVertex source = (BESVertex)from.getPosition();
      
      // Create the edge instances, set the Neighbour property and twin links
      BESEdge edgeprocessed = new BESEdge(original, type);
      edgeprocessed.setNeighbour(source);
      BESEdge edgesource = new BESEdge(original, type);
      edgesource.setNeighbour(processed);
      edgeprocessed.setTwinLink(edgesource);
      
      /* Insert the virtual vertex`s edge into the adjacency list between the
       * virtual vertex and the element that was linked to the specified
       * link before
       */
      BESElement processedprevious = processed.getLink(to.getDirection());
      processed.setLink(to.getDirection(), edgeprocessed);
      edgeprocessed.setLink(to.getDirection() ^ 1, processed);
      edgeprocessed.setLink(to.getDirection(), processedprevious);
      processedprevious.setLink(to.getDirection() ^ 1, edgeprocessed);
      
      /* Insert vertex`s edge into the adjacency list between the vertex and
       * the element that was linked to the specified link before
       */
      BESElement sourceprevious = source.getLink(from.getDirection());
      source.setLink(from.getDirection(), edgesource);
      edgesource.setLink(from.getDirection() ^ 1, source);
      edgesource.setLink(from.getDirection(), sourceprevious);
      sourceprevious.setLink(from.getDirection() ^ 1, edgesource);
   }
   
   /* Collect all remaining unmerged virtual vertices. These are either
    * cut vertices or dfs tree roots of the input graph. The bicomps
    * rooted by these virtual vertices are post processed (removal of
    * shortcut edges and reorientation of vertices to form a consistent
    * embedding). When this is finished the virtual vertex is merged into its
    * vertex.
    */
   private void postProcessStructure() {
      // Run through the array positions containing virtual vertices
      for (int pos = graphSize + 1 ; pos < vertices.length ; pos++) {
         BESVirtualVertex virtual = (BESVirtualVertex)vertices[pos];
         
         /* If the array position contains a virtual vertex and it has not
          * been merged into its vertex before (twin link is not null)
          */
         if (virtual !=null && virtual.getTwinLink() != null) {
            /* Perform the post processing on the bicomp rooted by this
             * virtual vertex
             */
            virtual.postProcessBicomp();
            
            // Merge the virtual vertex in its vertex
            BESVertex vertex = (BESVertex)virtual.getTwinLink();
            mergeVertices(new TraversalContext(vertex, BESElement.CCW),
            new TraversalContext(virtual, BESElement.CW));
            
            /* If the vertex is now the root of a result bicomp add it to the
             * result list
             */
            if (vertex.getDFSParent() == null) {
               resultBicompRoots.addLast(vertex);
            }
         }
      }
   }
   
   /** Dumps the cyclic ordering of the vertex edges to a String. After
    * algorithm execution, if the graph is planar, the ordering is a
    * combinatorial planar embedding of the graph.
    * @return The edge ordering textually expressed in a long String
    */
   private String dumpEdgeOrdering() {
      StringBuffer sb = new StringBuffer();
      sb.append('\n');
      sb.append("Cyclic edge order forming a combinatorial planar embedding :");
      sb.append('\n');
      sb.append("***** Begin of edge list ******");
      sb.append('\n');
      
      /* Run through the vertices and obtain Strings that contain the edge
       * ordering of these. Append them to the result StringBuffer
       */
      for (int pos = 1; pos <= graphSize ; pos++) {
         BESVertex vertex = (BESVertex)vertices[pos];
         sb.append('\n');
         sb.append("Edge list for Node "+vertex.getReferred().getName());
         sb.append('\n');
         Iterator edges = vertex.enumEdges();
         while (edges.hasNext()) {
            BESEdge current = (BESEdge)edges.next();
            sb.append("Edge to "+
            ((BESVertex)current.getNeighbour()).getReferred().getName());
            sb.append('\n');
         }
      }
      
      sb.append("***** End of list ******");
      return sb.toString();
   }
}