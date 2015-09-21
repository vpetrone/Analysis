package cs.rit;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Author: Vincent Petrone (vxp2993)
 */
public class GreedyTSP {

    private static int partCall = 0;
    private static ArrayList<Edge> greedyPath;
    private static int n = 1;

    /* @args0: number of vertices for the TSP
     * @args1: seed value for our random number generator
     * Descr: Greedy TSP sorts all of the weighted edges and chooses our path using
     * the union-find algorithm to find the minimum spanning tree amongst all vertices.
     * We utilize the path compression heuristic to speed our search for the MST.
     * A list of our path and weights are printed as well as a separate graph of greedy
     * weights with a low enough n value.
     * note: runs significantly faster than OptimalTSP (and scales much better)
     */
    public static void main(String[] args) {
        int seed = 0;
        try {
            n = Integer.parseInt(args[0]);
            seed = Integer.parseInt(args[1]);
            if(n < 1) {
                System.out.println("Number of vertices must be greater than 1");
                System.exit(1);
            }
        } catch(NumberFormatException nfe) {
            System.out.println("Command line args must be integers");
            System.exit(1);
        } catch(Exception e) {
            System.out.println("Usage: java GreedyTSP n seed");
            System.exit(1);
        }
        Graph g = new Graph(n, seed);
        greedyPath = new ArrayList<Edge>();
        if(n <= 10) {
            System.out.println("X-Y Coordinates:");
            for(int i=0; i<n; i++) {
                System.out.print("v" + i + ": " + g.printVertexByIndex(i));
            }
            System.out.println("\n");
            System.out.println("Adjacency matrix of graph weights:");
            g.printGraphWeights();
            System.out.println();
        }
        //Initialize variables for TS algorithm.
        long startTime = System.currentTimeMillis();
        sortWeights(g.getEdgeList());
        long sortTime = System.currentTimeMillis() - startTime;
        //Pick Edges Greedily
        double greedyDist = findGreedyPath(g.getEdgeList());
        //Set direct path
        Iterator<Integer> it = null;
        try {
            it = setGreedyPath(g.getVertexList()).iterator();
        } catch (BadVertexException e) {
            System.err.println("Finding our path went badly, Incorrect vertices used!");
            e.printStackTrace();
        }
        long runTime = System.currentTimeMillis() - startTime;
        if(n <= 10) {
            //Print greedy Graph
            System.out.println("Greedy  graph:");
            System.out.println("Adjacency matrix of graph weights:");
            printGreedyGraph();
            System.out.println("\nEdges of tour from greedy graph:");
            for (Edge e : greedyPath) {
                System.out.println(e.getRow() + " " + e.getCol() + " weight = " + e.getValue());
            }
        }
        DecimalFormat sigFormat = new DecimalFormat("0.00");
        greedyDist = Double.parseDouble(sigFormat.format(greedyDist));
        System.out.print("\n" + "Distance using greedy: " + greedyDist + " for path");
        while(it.hasNext()) {
            System.out.print(" " + it.next().intValue());
        }

        System.out.println("\n" + "Runtime for greedy TSP   : "
                + runTime + " milliseconds");

    }
    /* @param: Edge[] edgeList, the list of all edges in our graph (sorted)
     * Descr: Selects edges from our sorted edge list based on some criteria to
     * create our minimum spanning tree of all nodes.
     * 1. We select lowest cost edges first.
     * 2. The selected edge must not make a cycle in our MST.
     * 3. We can only visit each node once (no third edge from a vertex).
     * We merge our selected subtrees together to make the final product.
     * Also accumulates and returns the total path cost of our tree.
     * returns: Accumulation of edge weights (double)
     */
    private static double findGreedyPath(Edge[] edgeList) {
        double totalWeight = 0;
        for(int i=0; i < edgeList.length; i++) {
            Edge e = edgeList[i];
            Vertex[] vList = e.getVertices();
            Vertex v1 = vList[0];
            Vertex v2 = vList[1];
            if(isPathCandidate(greedyPath, v1, v2)) {
                //Merge the subtrees
                if (v1.isRoot() && v2.isRoot()) {
                    v1.setParent(v2);
                } else if (v1.isRoot()) {
                    v1.setParent(v2.getParent());
                } else if (v2.isRoot()) {
                    v2.setParent(v1.getParent());
                } else {
                    v1.getParent().setParent(v2.getParent());
                }
                greedyPath.add(e);
                totalWeight += e.getValue();
            }
        }
        return totalWeight;
    }
    /* @param Vertex[] vList, List of all vertices in our graph
     * Descr: Follows the path laid by our 'findGreedyPath' function to build a list
     * of integers corresponding to vertices so that we may print our complete
     * path.
     */
    private static ArrayList<Integer> setGreedyPath(Vertex[] vList) throws BadVertexException {
        ArrayList<Integer> pathTraversal = new ArrayList<Integer>();
        Vertex v = vList[0];
        ArrayList<Edge> alreadySeen = new ArrayList<Edge>();
        do {
            for(Edge e: v.getChildren()) {
                if (greedyPath.contains(e) && !alreadySeen.contains(e)) {
                    alreadySeen.add(e);
                    //Log the current vertex
                    pathTraversal.add(v.getPosition());
                    //Follow the edge to the next vertex
                    v = e.follow(v);
                    break;
                }
            }
        } while(v.getPosition() != 0);
        pathTraversal.add(0);
        return pathTraversal;
    }
    /* @param ArrayList<Obj> path, list of edges in our greedy path
     * @param Vertex v1, 'from' vertex as a possible candidate
     * @param Vertex v2, 'to' vertex as a possible candidate
     * Descr: Determines whether our specified vertices will be connected
     * in our new path of greedy edges. Mainly determines the 3 conditions noted in
     * 'findGreedyPath'
     */
    private static boolean isPathCandidate(ArrayList<Edge> path, Vertex v1, Vertex v2) {
        //If we have one more edge to connect, we make sure it connects the path.
        if((path.size() < n-1 && v1.findRoot() != v2.findRoot()) ||
                (path.size() == n-1 && v1.findRoot() == v2.findRoot())) {
            int v1Counter = 0, v2Counter = 0; //Keeps track of edges from the vertices
            for(Edge e : path) {
                if(v1.getPosition() == e.getRow() || v1.getPosition() == e.getCol())
                    v1Counter++;
                if(v2.getPosition() == e.getRow() || v2.getPosition() == e.getCol())
                    v2Counter++;
            }
            if(v1Counter < 2 && v2Counter < 2) {
                return true;
            }
        }
        return false;
    }

    /* @param: Edge[] edgeList, list of all edges in out graph (unsorted)
     * Descr: Uses quicksort to sort each edge in our graph by lowest cost.
     */
    private static void sortWeights(Edge[] edgeList) {
        if(edgeList == null || edgeList.length == 0) {
            return;
        }
        //Perform the KNUTH SHUFFLE
        long shuffleTime = System.currentTimeMillis();
        shuffleKnuth(edgeList);
        shuffleTime = System.currentTimeMillis() - shuffleTime;
        //Sort edges via Quicksort
        long sortTime = System.currentTimeMillis();
        quicksort(edgeList, 0, edgeList.length - 1);
        sortTime = System.currentTimeMillis() - sortTime;
    }
    /* @param: Edge[] edgeList,
     * @param: int lowIndex,
     * @param: int highIndex
     * Descr: Takes a well shuffled array of Edges and two index bounds to sort
     * the array within. First, partitions all elements to the left, or right of
     * a single pivot value, then recursively performs that operation on each
     * half of the list until sorted.
     */
    private static void quicksort(Edge[] edgeList, int lowIndex, int highIndex) {
        if (lowIndex < highIndex) {
            int partition = partition(edgeList, lowIndex, highIndex);
            quicksort(edgeList, lowIndex, partition - 1);
            quicksort(edgeList, partition + 1, highIndex);
        }
    }
    /* @param: Edge[] edgeList,
     * @param: int lowIndex,
     * @param: int highIndex
     * Descr: The partition section of quicksort that uses the highest Index value
     * as our pivot value. At the end of sorting, we can swap our value in to its
     * correct location via the 'tempIndex'
     */
    private static int partition(Edge[] edgeList, int lowIndex, int highIndex) {
        partCall++;
        int tempIndex = lowIndex;
        for(int i=lowIndex; i < highIndex; i++) {
            if(edgeList[highIndex].isSmaller(edgeList[i])) {
                swap(edgeList, i, tempIndex);
                tempIndex++;
            }
        }
        swap(edgeList, tempIndex, highIndex);
        return tempIndex;
    }
    // Performs a simple swap operation on a list of edges at the specified indices.
    private static void swap(Edge[] edgeList, int index1, int index2 ) {
        Edge temp = edgeList[index1];
        edgeList[index1] = edgeList[index2];
        edgeList[index2] = temp;
    }
    /* @param: Edge[] edgeList
     * Descr: Performs the simple Knuth shuffle on our array as a
     * precursor to quick-sorting.
     */
    private static void shuffleKnuth(Edge[] edgeList) {
        Random shuffler = new Random();
        for(int i=0; i < edgeList.length - 1; i++) {
            int j = shuffler.nextInt(edgeList.length);
            swap(edgeList, j, i);
        }
    }
    /* Descr: Prints our graph of greedy edges only that we are using
     * in our solution. Similar to our original graph but with many
     * edges removed. Aids in visualization!
     */
    private static void printGreedyGraph() {
        DecimalFormat sigFormat = new DecimalFormat("0.00");
        System.out.println();
        for(int i=0; i<n; i++) {
            System.out.print("      " + i);
        }
        for(int i=0; i<n; i++) {
            System.out.println("\n");
            System.out.print(i);
            for(int j=0; j<n; j++) {
                boolean matchedWeight = false;
                System.out.print("   ");
                for(Edge e: greedyPath) {
                    if((e.getRow() == i && e.getCol() == j) ||
                            (e.getRow() == j && e.getCol() == i)) {
                        matchedWeight = true;
                        double val = Double.parseDouble(sigFormat.format(e.getValue()));
                        System.out.print(val);
                    }
                }
                if(!matchedWeight) {
                    System.out.print("0.00");
                }
            }
        }
        System.out.println();
    }
}
