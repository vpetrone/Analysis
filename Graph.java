package cs.rit;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Author: Vincent Petrone (vxp2993)
 * Descr: A graph of our Euclidean plane. Holds a list of our
 * vertices as well as an adjacency matrix of weights between points.
 * Also includes various useful functions. Used for all parts of the
 * project.
 */
public class Graph {

    DecimalFormat sigFormat = new DecimalFormat("0.00");
    private Vertex[] vertexList; //List of points
    private double[][] weightList; //Adjacency matrix of weights
    private Edge[] edgeList; //List of edges

    /* @param: int n, the number of vertices in the graph.
     * @param: int seed, Used to generate random numbers.
     * Descr: Constructor. Initializes our vertex list
     * and generates the weights between points.
     */
    public Graph( int n, int seed ) {
        //sigFormat.setMinimumFractionDigits(2);
        vertexList = new Vertex[n]; //Create the graph
        weightList = new double[n][n];
        edgeList = new Edge[(n*(n-1))/2];
        //Generate Points
        Random randX = new Random(seed);
        Random randY = new Random(seed*2);
        //Fill the graph
        int count = 0;
        while( count < n ) {
            int x = randX.nextInt(n);
            int y = randY.nextInt(n);
            //if the x is not unique, trash points.
            boolean isUniqueX = true;
            for(int i=0; i < n; i++) {
                if(vertexList[i] != null && vertexList[i].getX() == x) {
                    isUniqueX = false;
                    break;
                }
            }
            if(isUniqueX) {
                vertexList[count] = new Vertex(x, y, count);
                count++;
            }
        }
        generateWeights();
    }
    //Accessor for matrix of weighted edges
    public double[][] getWeightList() {
        return weightList;
    }
    //Accessor for list of weighted edges
    public Edge[] getEdgeList() { return edgeList; }
    //Accessor for list of Vertices
    public Vertex[] getVertexList() { return vertexList; }
    //Utility function for printing vertices
    public String printVertexByIndex(int i) {
        Vertex v = vertexList[i];
        return "(" + v.getX() + "," + v.getY() + ") ";
    }
    /* Descr: Generates the weights of all edges in our graph by comparing
     * each node to each other node and itself. Also generates all edges in the graph
     * by connecting the vertices to each other.
     */
    private void generateWeights() {
        int count = 0;
        for(int i=0; i<weightList.length; i++) {
            for(int j=0; j<weightList.length; j++) {
                double distance = vertexList[i].getWeightedDistance(vertexList[j]);
                weightList[i][j] = distance;
                if(j > i) {
                    Edge e = new Edge(i, j, distance, vertexList[i], vertexList[j]);
                    vertexList[i].addEdge(e);
                    vertexList[j].addEdge(e);
                    edgeList[count] = e;
                    count++;
                }
            }
        }
    }
    // Utility function for printing the graph of all edges.
    public void printGraphWeights() {
        System.out.println();
        for(int i=0; i<weightList.length; i++) {
            System.out.print("      " + i);
        }
        for(int i=0; i<weightList.length; i++) {
            System.out.println("\n");
            System.out.print(i);
            for(int j=0; j<vertexList.length; j++) {
                double val = Double.parseDouble(sigFormat.format(weightList[i][j]));
                System.out.print("   " + val);
            }
        }
        System.out.println();
    }
}
