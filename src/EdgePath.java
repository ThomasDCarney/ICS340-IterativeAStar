/**
 * Class: ICS 340 - Data Structures and Algorithms <br>
 * Instructor: Michael Stein <br>
 * Description: Program 2, Iterative Deepening A* Search. <br>
 * Due: 04/01/2016 <br><br>
 * 
 * This class represents a path of edges along a graph. The cost of which
 * is associated with an A* search which is the current path cost + the
 * end vertices heuristic value. 
 * 
 * @author Tom Carney
 * @version 1.0
 * @since 03/12/2016
 */

import java.util.LinkedList;

public class EdgePath<T> implements Comparable<EdgePath<T>>, Cloneable {

    private Integer cost;
    private LinkedList<Edge<T>> edges;
    
    
    /**
     * A private constructor used by the clone function.
     */
    private EdgePath() {
        
        // I just want a constructor for the clone, not public
        
        edges = new LinkedList<Edge<T>>();
        
    } // end EdgePath constructor
    
    
    /**
     * A constructor for the EdgePath class.
     * 
     * @param startEdge - An initial edge to start the path.
     */
    public EdgePath(Edge<T> startEdge) {
        
        edges = new LinkedList<Edge<T>>();
        edges.add(startEdge);
        cost = startEdge.getCost() + ((Integer)startEdge.getTo().getData()).intValue();
        
        // At the beginning, this will be the shortest path so update.
        startEdge.getTo().setMinPathCost(cost);
        
    } // end EdgePath constructor
    
    
    /**
     * This will return the paths current cost.
     * 
     * @return - The paths current cost.
     */
    public int getCost() {
        
        return cost;
        
    } // end getCost
    
    
    /**
     * This method is used to extend the path by one more edge.
     * 
     * @param newEdge - The path by which to extend it.
     */
    public void extendPath(Edge<T> newEdge) {
        
        // COULD MAKE SURE THE NEW EDGE ACTUALLY BELONGS AT THE END OF THE CURRENT PATH
        // BUT FOR NOW GLAZING OVER THAT DETAIL SINCE IT'S DONE BY THE PROGRAM AND NOT
        // MANUALLLY.
        
        edges.add(newEdge);
        
        // remove the previous heuristic data.
        cost = cost - ((Integer)newEdge.getFrom().getData()).intValue(); 
        
        // add the new cost and heuristic.
        cost = cost + newEdge.getCost() + ((Integer)newEdge.getTo().getData()).intValue(); 
        
        // If the new path gets to a node for less cost, update that nodes min path cost.
        if(newEdge.getTo().getMinPathCost() > cost) {
            
            newEdge.getTo().setMinPathCost(cost);
            
        }
        
    } // end extendPath
    
    
    /**
     * This method peeks at the last edge without removing it.
     * 
     * @return - The last edge in the path.
     */
    public Edge<T> peekLastEdge() {
        
        return edges.peekLast();
        
    } // end peekLastEdge
    
    
    /**
     * This method peeks at the first edge without removing it.
     * 
     * @return - The first edge in the path.
     */
    public Edge<T> peekFirstEdge() {
        
        return edges.peekFirst();
        
    } // end peekFirstEdge
    
    
    /**
     * This method gets the last edge and removes it.
     * 
     * @return - The last edge in the path.
     */
    public Edge<T> removeLastEdge() {
        
        return edges.pollLast();
        
    } // end removeLastEdge
    
    
    /**
     * This method gets the first edge and removes it.
     * 
     * @return - The first edge in the path.
     */
    public Edge<T> removeFirstEdge() {
        
        return edges.pollFirst();
        
    } // end removeFirstEdge
    
    
    /**
     * This method returns a cloned copy of the current path.
     * 
     * @return - A copy of the current path.
     */
    public EdgePath<T> clone() {
        
        EdgePath<T> pathClone = new EdgePath<T>();
        
        pathClone.cost = cost;
        for (Edge<T> e : edges)
            pathClone.edges.add(e);
        
        return pathClone;
        
    } // end clone
    
    
    /**
     * This method is used to compare to paths to determine which way to 
     * order them by cost.
     */
    @Override
    public int compareTo(EdgePath<T> path2) {
        
        return cost - path2.getCost();
        
    } // end compareTo
    
    
    /**
     * This method will return a string representation of the path.
     * 
     * @return - A string representation of the path.
     */
    @Override
    public String toString() {
        
        String tmp = "";
        
        for (Edge<T> e : edges)
            tmp += (e + "\n");
        
        return tmp;
        
    } // end toString
    
} // end EdgePath