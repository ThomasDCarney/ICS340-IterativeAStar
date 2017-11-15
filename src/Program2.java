/**
 * Class: ICS 340 - Data Structures and Algorithms <br>
 * Instructor: Michael Stein <br>
 * Description: Program 2, Iterative Deepening A* Search. <br>
 * Due: 04/01/2016 <br><br>
 * 
 * This program has us taking in three files, an adjacency matrix indicating directed
 * edges, a heuristic value for each vertex and a set of goals. We are to use them to
 * implement an iterative deepening A* search. 
 * 
 * @author Tom Carney
 * @version 1.0
 * @since 03/12/2016
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFileChooser;

public class Program2 {
    
    private JFileChooser chooser;           // Lets the user specify a location and/or file.
    private BufferedReader matrixReader;    // Used to pick apart the selected file once chosen.
    private BufferedReader goalReader;      // Used to pick apart the selected file once chosen.
    private BufferedReader heuristicReader; // Used to pick apart the selected file once chosen.
    private Graph<Integer> graph;           // Used to house and work with all the vertices and edges.
    
    
    /**
     * No Argument constructor for Program02.
     */
    public Program2() {
        
        chooser = new JFileChooser(new java.io.File("."));
        setGraph("Choose a graph file.");
        setGoals("Choose a goals file.");
        setHeuristics("Choose a heuristics file.");
        
    } // end Program2 constructor
    
    
    /**
     * This method is used to acquire the space separated graph file.
     * 
     * @param title - A title for the pop-up window telling the user what file to select.
     */
    public void setGraph(String title) {
        
        matrixReader = chooseFile(title);
        
    } // end getMatrix
    
    
    /**
     * This method is used to acquire the space separated goals file.
     * 
     * @param title - A title for the pop-up window telling the user what file to select.
     */
    public void setGoals(String title) {
        
        goalReader = chooseFile(title);
        
    } // end getMatrix
    
    
    /**
     * This method is used to acquire the space separated heuristic file.
     * 
     * @param title - A title for the pop-up window telling the user what file to select.
     */
    public void setHeuristics(String title) {
        
        heuristicReader = chooseFile(title);
        
    } // end getMatrix
    
    
    /**
     * This method is used to acquire a buffered file in which to process.
     * 
     * @param title - A title for the pop-up request window.
     * 
     * @return - A BufferedReader if the file can be loaded, null otherwise.
     */
    private BufferedReader chooseFile(String title) {
        
        File tempFile = null;
        BufferedReader tempReader = null;
        
        chooser.setDialogTitle(title);
        int returnValue = chooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          
            tempFile = chooser.getSelectedFile();
            
            try {
                
                tempReader = new BufferedReader(new FileReader(tempFile));
              
            }catch (FileNotFoundException e){
                
                System.out.println("File not found");
                
            } // end try/catch block
            
        } // end IF block
        
        return tempReader;
        
    } // end chooseFile
    
    
    /**
     * This method will take the currently loaded Matrix, Goal and Heuristic 
     * files and populate graph data from them.
     */
    public void buildGraph() {
        
        graph = new Graph<Integer>();
        addVertices();
        addEdges();
        addGoals();
        
        // Could be a better way but this works for now.
        graph.setRootVertex(graph.findVertexByName("S"));
        
    }
    
    
    /**
     * This method will use the goals file to set the boolean flag of each vertex
     * stating whether or not it is a goal vertex.
     * 
     * @precondition - You must first load a goals file.
     */
    private void addGoals() {
        
        Scanner scanner = null;
        boolean proceed = false;
        String line = null;
        
        // Must first load the goals file.
        try {
            
            proceed = goalReader.ready();
            
        } catch (IOException e) {
            
            System.out.println("Goal file is not ready!");
            
        }
        
        while(proceed) {
            
            try {
                
                line = goalReader.readLine();
                
            } catch (IOException e) {
                
                System.out.println("Goal file could not be read!");
                
            }
            
            if(line != null) {
                
                scanner = new Scanner(line);
                String tempName = scanner.next();
                int tempGoal = scanner.nextInt();
                
                if(tempGoal == 1) {
                    
                    graph.findVertexByName(tempName).setGoal(true);
                    
                }
                
                
            } else {
                
                proceed = false;
                
            } // end IF-ELSE
            
        }
        
    } // end setGoals
    
    
    /**
     * This method is used to add vertices to the graph based on the heuristic file.
     * The first column is used as the vertex name. The second, its heuristic information
     * column, is used to fill the vertex's data member.
     * 
     * @precondition - A heuristic file must be chosen first.
     */
    private void addVertices() {
        
        Scanner scanner = null;
        boolean proceed = false;
        String line = null;
        
        // Only start-proceed if heuristic file is good to go.
        try {
            
            proceed = heuristicReader.ready();
            
        } catch (IOException e) {
            
            System.out.println("Heuristic file is not ready!");
            
        } // end TRY-CATCH
        
        // As long as the file is good, loop through each line for vertex info.
        while(proceed) {
            
            try {
                
                line = heuristicReader.readLine();
                
            } catch (IOException e) {
                
                System.out.println("Heuristic file could not be read!");
                
            }
            
            if(line != null) {
                
                scanner = new Scanner(line);
                graph.addVertex(new Vertex<Integer>(scanner.next(), scanner.nextInt()));
                
            } else {
                
                proceed = false;
                
            } // end IF-ELSE
            
        } // end WHILE
        
    } // end addVertices
    
    
    /**
     * This method will use the matrix file to insert all the directed edges.
     * 
     * @precondition - You must first load a matrix file.
     */
    private void addEdges() {
        
        Scanner scanner = null;
        boolean proceed = false;
        String line = null;
        String tempFromName = null;
        String tempWeight = null;
        
        // Offset value for each column from 'A'.
        int offset;
        
        // Only start-proceed if matrix file is good to go.
        try {
            
            proceed = matrixReader.ready();
            
        } catch (IOException e) {
            
            System.out.println("Matrix file is not ready!");
            
        }
        
        // As long as the file is good, loop through each line for edge info.
        while(proceed) {
            
            try {
                
                line = matrixReader.readLine();
                
            } catch (IOException e) {
                
                System.out.println("Matrix file could not be read!");
                
            }
            
            if(line != null) {
                
                scanner = new Scanner(line);
                
                // Lines that don't start with a letter are just column headers.
                // This is of course specific to our project, would need to change
                // if the file's format changed.
                if(line.startsWith("-")) {
                    
                    continue; // Not a real line so just skips this loop.
                    
                }
                
                // Save the vertex the edge starts at.
                tempFromName = scanner.next();  
                
                
                offset = 0; // offset starts at 0 for A.
                
                while(scanner.hasNext()) {
                    
                    tempWeight = scanner.next();
                    
                    // Need to make sure it's a dash meaning "no edge" and not the
                    // start of a negative weighted edge.
                    if(tempWeight.compareTo("-") == 0) {
                        
                        // We just have a "no edge" dash so ignore it.
                        
                    } else {
                        
                        // It's either a pos/neg weight.
                        
                        String tempToName = (char) ('A' + offset) + "";
                        
                        graph.addEdge(graph.findVertexByName(tempFromName), 
                                      graph.findVertexByName(tempToName), 
                                      Integer.parseInt(tempWeight));
                        
                    }
                    
                    offset++;
                    
                }
                
            } else {
                
                proceed = false;
                
            } // end IF-ELSE
            
        } // end WHILE
        
    } // end addEdges
    
    
    /**
     * This will perform an A* Search on the current graph with no
     * specified depth limit.
     */
    public void aStarSearch() {
        
        // Just using 9999 as some huge depth we'd never hit in place of INF.
        graph.aStarSearch(graph.getRootVertex(), 9999);
        
    } // end aStarSearch
    
    
    /**
     * This will perform an A* search on the current graph with a
     * specified depth or cost limit.
     * 
     * @param depth - The specified depth or cost limit.
     */
    public void aStarSearch(int depth) {
        
        graph.aStarSearch(graph.getRootVertex(), depth);
        
    } // end aStarSearch
    
    
    /**
     * This will perform an Iterative Deepening A* Search on the 
     * current graph to a specified depth or cost.
     * 
     * @param depth - The specified depth or cost limit.
     */
    public void IDAStarSearch(int depth) {
        
        graph.IDAStarSearch(graph.getRootVertex(), depth);
        
    } // end IDAStarSearch
    
    
    public static void main(String[] args) {

        Program2 program2 = new Program2();
        program2.buildGraph();
        program2.IDAStarSearch(12);
        
    } // end main
    
}