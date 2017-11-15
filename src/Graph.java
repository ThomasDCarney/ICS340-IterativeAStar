/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/**
 * This has been modified by Thomas Carney as part of Program 2 for 
 * ICS 340, Fall 2016. 
 */

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * A directed graph data structure.
 * 
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class Graph<T> {
  /** Color used to mark unvisited nodes */
  public static final int VISIT_COLOR_WHITE = 1;

  /** Color used to mark nodes as they are first visited in DFS order */
  public static final int VISIT_COLOR_GREY = 2;

  /** Color used to mark nodes after descendants are completely visited */
  public static final int VISIT_COLOR_BLACK = 3;

  /** Vector<Vertex> of graph verticies */
  private List<Vertex<T>> verticies;

  /** Vector<Edge> of edges in the graph */
  private List<Edge<T>> edges;

  /** The vertex identified as the root of the graph */
  private Vertex<T> rootVertex;
  
  /** Used for the A* search */
  private PriorityQueue<EdgePath<T>> priorityQueue;
  
  /** Used to write to the output files */
  private PrintWriter pWriter;
  

  /**
   * Construct a new graph without any vertices or edges
   */
  public Graph() {
    verticies = new ArrayList<Vertex<T>>();
    edges = new ArrayList<Edge<T>>();
  }

  /**
   * Are there any verticies in the graph
   * 
   * @return true if there are no verticies in the graph
   */
  public boolean isEmpty() {
    return verticies.size() == 0;
  }

  /**
   * Add a vertex to the graph
   * 
   * @param v
   *          the Vertex to add
   * @return true if the vertex was added, false if it was already in the graph.
   */
  public boolean addVertex(Vertex<T> v) {
    boolean added = false;
    if (verticies.contains(v) == false) {
      added = verticies.add(v);
    }
    return added;
  }

  /**
   * Get the vertex count.
   * 
   * @return the number of verticies in the graph.
   */
  public int size() {
    return verticies.size();
  }

  /**
   * Get the root vertex
   * 
   * @return the root vertex if one is set, null if no vertex has been set as
   *         the root.
   */
  public Vertex<T> getRootVertex() {
    return rootVertex;
  }

  /**
   * Set a root vertex. If root does no exist in the graph it is added.
   * 
   * @param root -
   *          the vertex to set as the root and optionally add if it does not
   *          exist in the graph.
   */
  public void setRootVertex(Vertex<T> root) {
    this.rootVertex = root;
    if (verticies.contains(root) == false)
      this.addVertex(root);
  }

  /**
   * Get the given Vertex.
   * 
   * @param n
   *          the index [0, size()-1] of the Vertex to access
   * @return the nth Vertex
   */
  public Vertex<T> getVertex(int n) {
    return verticies.get(n);
  }

  /**
   * Get the graph verticies
   * 
   * @return the graph verticies
   */
  public List<Vertex<T>> getVerticies() {
    return this.verticies;
  }

  /**
   * Insert a directed, weighted Edge<T> into the graph.
   * 
   * @param from -
   *          the Edge<T> starting vertex
   * @param to -
   *          the Edge<T> ending vertex
   * @param cost -
   *          the Edge<T> weight/cost
   * @return true if the Edge<T> was added, false if from already has this Edge<T>
   * @throws IllegalArgumentException
   *           if from/to are not verticies in the graph
   */
  public boolean addEdge(Vertex<T> from, Vertex<T> to, int cost) throws IllegalArgumentException {
    if (verticies.contains(from) == false)
      throw new IllegalArgumentException("from is not in graph");
    if (verticies.contains(to) == false)
      throw new IllegalArgumentException("to is not in graph");

    Edge<T> e = new Edge<T>(from, to, cost);
    if (from.findEdge(to) != null)
      return false;
    else {
      from.addEdge(e);
      to.addEdge(e);
      edges.add(e);
      return true;
    }
  }

  /**
   * Insert a bidirectional Edge<T> in the graph
   * 
   * @param from -
   *          the Edge<T> starting vertex
   * @param to -
   *          the Edge<T> ending vertex
   * @param cost -
   *          the Edge<T> weight/cost
   * @return true if edges between both nodes were added, false otherwise
   * @throws IllegalArgumentException
   *           if from/to are not verticies in the graph
   */
  public boolean insertBiEdge(Vertex<T> from, Vertex<T> to, int cost)
      throws IllegalArgumentException {
    return addEdge(from, to, cost) && addEdge(to, from, cost);
  }

  /**
   * Get the graph edges
   * 
   * @return the graph edges
   */
  public List<Edge<T>> getEdges() {
    return this.edges;
  }

  /**
   * Remove a vertex from the graph
   * 
   * @param v
   *          the Vertex to remove
   * @return true if the Vertex was removed
   */
  public boolean removeVertex(Vertex<T> v) {
    if (!verticies.contains(v))
      return false;

    verticies.remove(v);
    if (v == rootVertex)
      rootVertex = null;

    // Remove the edges associated with v
    for (int n = 0; n < v.getOutgoingEdgeCount(); n++) {
      Edge<T> e = v.getOutgoingEdge(n);
      v.remove(e);
      Vertex<T> to = e.getTo();
      to.remove(e);
      edges.remove(e);
    }
    for (int n = 0; n < v.getIncomingEdgeCount(); n++) {
      Edge<T> e = v.getIncomingEdge(n);
      v.remove(e);
      Vertex<T> predecessor = e.getFrom();
      predecessor.remove(e);
    }
    return true;
  }

  /**
   * Remove an Edge<T> from the graph
   * 
   * @param from -
   *          the Edge<T> starting vertex
   * @param to -
   *          the Edge<T> ending vertex
   * @return true if the Edge<T> exists, false otherwise
   */
  public boolean removeEdge(Vertex<T> from, Vertex<T> to) {
    Edge<T> e = from.findEdge(to);
    if (e == null)
      return false;
    else {
      from.remove(e);
      to.remove(e);
      edges.remove(e);
      return true;
    }
  }

  /**
   * Clear the mark state of all verticies in the graph by calling clearMark()
   * on all verticies.
   * 
   * @see Vertex#clearMark()
   */
  public void clearMark() {
    for (Vertex<T> w : verticies)
      w.clearMark();
  }

  /**
   * Clear the mark state of all edges in the graph by calling clearMark() on
   * all edges.
   */
  public void clearEdges() {
    for (Edge<T> e : edges)
      e.clearMark();
  }

  /**
   * Perform a depth first serach using recursion.
   * 
   * @param v -
   *          the Vertex to start the search from
   * @param visitor -
   *          the vistor to inform prior to
   * @see Visitor#visit(Graph, Vertex)
   */
  public void depthFirstSearch(Vertex<T> v, final Visitor<T> visitor) {
    VisitorEX<T, RuntimeException> wrapper = new VisitorEX<T, RuntimeException>() {
      public void visit(Graph<T> g, Vertex<T> v) throws RuntimeException {
        if (visitor != null)
          visitor.visit(g, v);
      }
    };
    this.depthFirstSearch(v, wrapper);
  }

  /**
   * Perform a depth first serach using recursion. The search may be cut short
   * if the visitor throws an exception.
   * 
   * @param <E>
   * 
   * @param v -
   *          the Vertex to start the search from
   * @param visitor -
   *          the vistor to inform prior to
   * @see Visitor#visit(Graph, Vertex)
   * @throws E
   *           if visitor.visit throws an exception
   */
  public <E extends Exception> void depthFirstSearch(Vertex<T> v, VisitorEX<T, E> visitor) throws E {
    if (visitor != null)
      visitor.visit(this, v);
    v.visit();
    for (int i = 0; i < v.getOutgoingEdgeCount(); i++) {
      Edge<T> e = v.getOutgoingEdge(i);
      if (!e.getTo().visited()) {
        depthFirstSearch(e.getTo(), visitor);
      }
    }
  }

  /**
   * Perform a breadth first search of this graph, starting at v.
   * 
   * @param v -
   *          the search starting point
   * @param visitor -
   *          the vistor whose vist method is called prior to visting a vertex.
   */
  public void breadthFirstSearch(Vertex<T> v, final Visitor<T> visitor) {
    VisitorEX<T, RuntimeException> wrapper = new VisitorEX<T, RuntimeException>() {
      public void visit(Graph<T> g, Vertex<T> v) throws RuntimeException {
        if (visitor != null)
          visitor.visit(g, v);
      }
    };
    this.breadthFirstSearch(v, wrapper);
  }

  /**
   * Perform a breadth first search of this graph, starting at v. The vist may
   * be cut short if visitor throws an exception during a vist callback.
   * 
   * @param <E>
   * 
   * @param v -
   *          the search starting point
   * @param visitor -
   *          the vistor whose vist method is called prior to visting a vertex.
   * @throws E
   *           if vistor.visit throws an exception
   */
  public <E extends Exception> void breadthFirstSearch(Vertex<T> v, VisitorEX<T, E> visitor)
      throws E {
    LinkedList<Vertex<T>> q = new LinkedList<Vertex<T>>();

    q.add(v);
    if (visitor != null)
      visitor.visit(this, v);
    v.visit();
    while (q.isEmpty() == false) {
      v = q.removeFirst();
      for (int i = 0; i < v.getOutgoingEdgeCount(); i++) {
        Edge<T> e = v.getOutgoingEdge(i);
        Vertex<T> to = e.getTo();
        if (!to.visited()) {
          q.add(to);
          if (visitor != null)
            visitor.visit(this, to);
          to.visit();
        }
      }
    }
  }

  /**
   * Find the spanning tree using a DFS starting from v.
   * 
   * @param v -
   *          the vertex to start the search from
   * @param visitor -
   *          visitor invoked after each vertex is visited and an edge is added
   *          to the tree.
   */
  public void dfsSpanningTree(Vertex<T> v, DFSVisitor<T> visitor) {
    v.visit();
    if (visitor != null)
      visitor.visit(this, v);

    for (int i = 0; i < v.getOutgoingEdgeCount(); i++) {
      Edge<T> e = v.getOutgoingEdge(i);
      if (!e.getTo().visited()) {
        if (visitor != null)
          visitor.visit(this, v, e);
        e.mark();
        dfsSpanningTree(e.getTo(), visitor);
      }
    }
  }

  /**
   * Search the verticies for one with name.
   * 
   * @param name -
   *          the vertex name
   * @return the first vertex with a matching name, null if no matches are found
   */
  public Vertex<T> findVertexByName(String name) {
    Vertex<T> match = null;
    for (Vertex<T> v : verticies) {
      if (name.equals(v.getName())) {
        match = v;
        break;
      }
    }
    return match;
  }

  /**
   * Search the verticies for one with data.
   * 
   * @param data -
   *          the vertex data to match
   * @param compare -
   *          the comparator to perform the match
   * @return the first vertex with a matching data, null if no matches are found
   */
  public Vertex<T> findVertexByData(T data, Comparator<T> compare) {
    Vertex<T> match = null;
    for (Vertex<T> v : verticies) {
      if (compare.compare(data, v.getData()) == 0) {
        match = v;
        break;
      }
    }
    return match;
  }

  /**
   * Search the graph for cycles. In order to detect cycles, we use a modified
   * depth first search called a colored DFS. All nodes are initially marked
   * white. When a node is encountered, it is marked grey, and when its
   * descendants are completely visited, it is marked black. If a grey node is
   * ever encountered, then there is a cycle.
   * 
   * @return the edges that form cycles in the graph. The array will be empty if
   *         there are no cycles.
   */
  public Edge<T>[] findCycles() {
    ArrayList<Edge<T>> cycleEdges = new ArrayList<Edge<T>>();
    // Mark all verticies as white
    for (int n = 0; n < verticies.size(); n++) {
      Vertex<T> v = getVertex(n);
      v.setMarkState(VISIT_COLOR_WHITE);
    }
    for (int n = 0; n < verticies.size(); n++) {
      Vertex<T> v = getVertex(n);
      visit(v, cycleEdges);
    }

    Edge<T>[] cycles = new Edge[cycleEdges.size()];
    cycleEdges.toArray(cycles);
    return cycles;
  }

  private void visit(Vertex<T> v, ArrayList<Edge<T>> cycleEdges) {
    v.setMarkState(VISIT_COLOR_GREY);
    int count = v.getOutgoingEdgeCount();
    for (int n = 0; n < count; n++) {
      Edge<T> e = v.getOutgoingEdge(n);
      Vertex<T> u = e.getTo();
      if (u.getMarkState() == VISIT_COLOR_GREY) {
        // A cycle Edge<T>
        cycleEdges.add(e);
      } else if (u.getMarkState() == VISIT_COLOR_WHITE) {
        visit(u, cycleEdges);
      }
    }
    v.setMarkState(VISIT_COLOR_BLACK);
  }

  
  /**
   * This method will return a string containing header information.
   * Delineating the column for the vertex being processed and vertex 
   * each column pertains to.
   * 
   * @return - A string with header information.
   */
  public String printHeader() {
      
      String returnString = "Process\t";
      
      for (Vertex<T> v : verticies)
          returnString += ("\t" + v.getName());
      
      return returnString;
      
  } // end printHeader
  
  
  /**
   * This method prints off the minimum path costs for any vertex
   * currently on the frontier. The spacing will match that of the 
   * header.
   * 
   * @return - A string of minimum path costs for the frontier vertices.
   */
  public String printFrontier() {
      
      String returnString = "";
      
      for (Vertex<T> v : verticies) {
          
          if(v.getMarkState() == VISIT_COLOR_GREY) {
              
              returnString += ("\t" + v.getMinPathCost());
              
          } else {
              
              returnString += ("\t-");
              
          }
          
      }    
      
      return returnString;
      
  } // end printFrontier
  
  
  /**
   * This method will perform an Iterative Deepening A* Search on the 
   * graph based on the provided start vertex and max depth/cost.
   * 
   * @param startVertex - The vertex to start at.
   * 
   * @param maxDepth - The maximum cost to which the search will go.
   */
  public void IDAStarSearch(Vertex<T> startVertex, int maxDepth) {
      
      // First take care of the special case.
      if(maxDepth == 0) {
          
          Integer data = (Integer)startVertex.getData();
          
          if(data.intValue() == 0) {
              
              System.out.println("Start node is the goal!");
              
          } else {
              
              System.out.println("Start node is not the goal!");
              
          }
          
      }
      
      // Essentially just call A* search for various depths.
      for(int i = 4 ; i <= maxDepth ; i++) {
          
          // I want to allow for max costs other than multiples of 4
          // but still start at and increment in 4's.
          if(i % 4 == 0 || i == maxDepth) {
              
              aStarSearch(startVertex, i);
              
          }
          
      }
      
  } // end IDAStarSearch
  
  
  /**
   * This will perform an A* Search on the graph to specified depth.
   * 
   * @param startVertex  - The vertex to start at.
   * 
   * @param maxDepth - The maximum cost to which the search will go.
   */
  public void aStarSearch(Vertex<T> startVertex, int maxDepth) {
      
      // This will hold all the paths that need processing.
      priorityQueue = new PriorityQueue<EdgePath<T>>();
      
      setupWriter(createOutputPath(maxDepth));
      
      // This collects data for the output file.
      pWriter.println(printHeader());
      
      // This will put the initial paths in our queue based on the startVertex
      List<Edge<T>> outgoingEdges = startVertex.getOutgoingEdges();
      for (Edge<T> e: outgoingEdges) {
          
          // Mark vertex as on the frontier.
          e.getTo().setMarkState(VISIT_COLOR_GREY); 
          EdgePath<T> newPath = new EdgePath<T>(e);
          priorityQueue.add(newPath);
          
      }
      
      // Now the start vertex has been processed, mark it as such.
      startVertex.setMarkState(VISIT_COLOR_BLACK);
      
      pWriter.println("   S   \t" + printFrontier());
      
      boolean loopAgain = true;
      while(loopAgain) {
          
          // Gets the current minimum path from our priority queue.
          EdgePath<T> cheapPath = priorityQueue.poll();
          
          // If we've surpassed our max cost, go no further
          if(cheapPath.getCost() > maxDepth) {
              
              pWriter.println("");
              pWriter.println("No goal reached at a cost of " + maxDepth);
              loopAgain = false;
              continue;
              
          }
          
          // Check to see if we've hit a goal.
          if(cheapPath.peekLastEdge().getTo().isGoal()) {
              
              loopAgain = false;
              pWriter.println("Goal Reached in " + cheapPath.getCost());
              pWriter.println("");
              pWriter.println(cheapPath);
              
          } else {
              
              // No goal so process the path.
              Vertex<T> endVertex = cheapPath.peekLastEdge().getTo();
              endVertex.setMarkState(VISIT_COLOR_BLACK);
              pWriter.print("   " + endVertex.getName() + "   \t");
              
              List<Edge<T>> outgoingEdges2 = endVertex.getOutgoingEdges();
              
              for (Edge<T> e : outgoingEdges2) {
                  
                  EdgePath<T> newPath = cheapPath.clone();
                  newPath.extendPath(e);
                  
                  // Put the end vertex on the frontier
                  e.getTo().setMarkState(VISIT_COLOR_GREY);
                  priorityQueue.add(newPath);
                  
              }
              
              // Add the new frontier after processing the end vertex.
              pWriter.println(printFrontier());
              
          }
          
      }
      
      // Reset vertices for whatever kind of next search.
      for (Vertex<T> v : verticies)
          v.setMarkState(VISIT_COLOR_WHITE);
      
      // Have to close it or else file isn't written to.
      pWriter.close();
      
  } // end aStarSearch
  
  
  /**
   * This method will create a new file in which to write line sums to.
   * 
   * @return A boolean true if creation goes smoothly, false otherwise.
   */
  private Boolean setupWriter(String outputPath) {
      
      try {
          
          pWriter = new PrintWriter(new FileWriter(outputPath));
          return true;
      
      } catch(IOException e1) {
          
          System.out.println("Error creating print writer.");
          
      }
      
      return false;
      
  } // end setupWriter
  
  
  /**
   * This method will determine the output path and name based on specifications
   * in the project doc and current depth-cost value.
   * 
   * @return The absolute path to the output file as a string.
   */
  private String createOutputPath(int maxDepth) {
      
      String outputPath = "IC_" + maxDepth + ".txt";
      
      return outputPath;
      
  } // end createOutputPath
  
  
  /**
   * This method will provide a String representation of the graph.
   */
  public String toString() {
    StringBuffer tmp = new StringBuffer("Graph[");
    for (Vertex<T> v : verticies)
      tmp.append("\n" + v);
    tmp.append(']');
    return tmp.toString();
  }

} // end Graph