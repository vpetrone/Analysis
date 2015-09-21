package cs.rit;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Author: Vincent Petrone
 * Descr: Runs Prim's algorithm to find a minimal tour for our TSP.
 * Prim's algorithm requires a priority queue implemented as a min
 * heap which stores our vertices and their associated costs. Our
 * final solution is gauranteed to be no more than twice the optimal
 * path.
 */
public class MstTSP {

    private static TreeVertex[] mstPath;
    private static TreeVertex[] mstTour;
    private static double distance;
    private static double tourDist;
    private static PrintWriter delay;

    /* @param: args[0], number of vertices in our graph
     * @param: args[1], seed for random number generator
     * Descr: Verifies command line arguments and runs out implementation
     * of the algorithm and prints the results.
     */
    public static void main(String[] args) {
        int n = 1, seed = 0;
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
            System.out.println("Usage: java MstTSP n seed");
            System.exit(1);
        }
        Graph g = new Graph(n, seed);
        delay = new PrintWriter(System.out);
        //Initialize variables for TS algorithm.
        long startTime = System.currentTimeMillis();
        calculateMstDistance(g.getVertexList(), g);
        long runTime = System.currentTimeMillis() - startTime;
        DecimalFormat sigFormat = new DecimalFormat("0.00");
        if(n <= 10) {
            System.out.println("X-Y Coordinates:");
            for(int i=0; i<n; i++) {
                System.out.print("v" + i + ": " + g.printVertexByIndex(i));
            }
            System.out.println("\n");
            System.out.println("Adjacency matrix of graph weights:");
            g.printGraphWeights();
            System.out.println();
            printMSTGraph(mstPath);
            System.out.println("\nTotal weight of mst: " + sigFormat.format(distance));
            //Print pre-order traversal
            delay.flush();
        }

        System.out.print("\n" + "Distance using mst: " + sigFormat.format(tourDist) + " for path");
        for(TreeVertex v: mstTour) {
            System.out.print(" " + v.getPos());
        }
        System.out.println("\n" + "Runtime for Mst TSP   : "
                + runTime + " milliseconds");
    }

    /* @param: vList, list of all vertices in our graph in order with
     * associated connections.
     * Descr: Finds our MST using Prim, generates the tour from the MST
     * and calculates the distance of each operation.
     * returns: final distance of our tour.
     */
    private static void calculateMstDistance(Vertex[] vList, Graph g) {
        mstPath = buildMST(vList);
        mstTour = findTour(mstPath);
        //Retrieves second-to-last elem in tour
        TreeVertex lastConnectedElem = mstTour[mstTour.length-2];
        double[][] weightAcess = g.getWeightList();
        for(int i=0; i < mstTour.length - 1; i++) {
            tourDist += weightAcess[mstTour[i].getPos()][mstTour[i+1].getPos()];
        }
    }
    /* @param: mst, Our mst path in order.
     * Descr: Performs DFS in an iterative manner to compute our
     * Prim's generated tour of the vertices. The final connection weight
     * is calculated in the outside calling function. Simultaneously, we
     * store the output for a pre-order traversal into a buffer that prints
     * to System.out so we can delay the output until the proper point in
     * the program.
     * returns: Our traversal in order for the TSP.
     */
    private static TreeVertex[] findTour(TreeVertex[] mst) {
        TreeVertex[] tour = new TreeVertex[mst.length+1];
        TreeVertex[] sortedMST = reverseSortByPos(mst);
        Stack<TreeVertex> s = new Stack<TreeVertex>();
        s.push(mst[mst.length-1]);
        int index = 0;
        delay.write("\nPre-order traversal:");
        while(!s.isEmpty()) {
            TreeVertex tv = s.pop();
            if(!tv.isMarked()) {
                tour[index] = tv;
                tv.mark();
                TreeVertex parent = tv.getParent();
                if(parent == null) {
                    delay.write("\nParent of " + tv.getPos() + " is -1");
                } else {
                    delay.write("\nParent of " + tv.getPos() + " is " + parent.getPos());
                }
                for(TreeVertex vertex: sortedMST) { // visit in numerical order
                    if(vertex.getParent() == tv) {
                        s.push(vertex);
                    }
                }
                index++;
            }
        }
        tour[index] = mst[mst.length-1];
        delay.write("\n");
        return tour;
    }

    private static TreeVertex[] reverseSortByPos(TreeVertex[] mstOG) {
        for(int i=1; i < mstOG.length; i++) {
            int j = i;
            while (j > 0 && mstOG[j-1].getPos() < mstOG[j].getPos()) {
                TreeVertex temp = mstOG[j];
                mstOG[j] = mstOG[j-1];
                mstOG[j-1] = temp;
                j = j-1;
            }
        }
        return mstOG;
    }

    /* @param: bh, our priority queue of vertices.
     * @param: position, The position of the node we are searching for.
     * Descr: Searches the minHeap for a node with the given position,
     * returns first occurrence node when found.
     * returns: index of desired object, -1 if no such vertex exists in
     * our priority queue.
     */
    private static int findVertexIndexByPos(BinaryHeap bh, int position) {
        for(int i=1; i < bh.array.length; i++) {
            TreeVertex tv = bh.array[i];
            if(tv.getPos() == position)
                return i;
        }
        return -1;
    }

    /* @param: vertices, The list of all vertices and their connections in
     * numerical ordering
     * Descr: Builds the Minimum Spanning Tree (MST) via Primm's Algorithm
     * concerned with each vertex in our graph, build a fringe and place each
     * associated weight in a minimum priority queue. Repeatedly choose the
     * minimum vertex (starting with node 0) and update the fringe with that
     * node's edge if it has a smaller weight than the one established until
     * we have each node in our mst.
     * returns: A representative list of our mst in order of visitation.
     */
    private static TreeVertex[] buildMST(Vertex[] vertices) {
        TreeVertex[] mst = new TreeVertex[vertices.length];
        BinaryHeap fringe = new BinaryHeap();
        //fringe.add(new TreeVertex(0, 0, null));
        TreeVertex start = new TreeVertex(0, 0, null);
        mst[0] = start;
        for(int i=1; i < vertices.length; i++) {
            fringe.add(new TreeVertex(vertices[i].getPosition(), -1, null));
        }
        vertices[start.getPos()].mark();
        List<Edge> edges = vertices[start.getPos()].getChildren();
        for(Edge e: edges) {
            try {
                Vertex fringeVertex = e.follow(vertices[start.getPos()]);
                if(!fringeVertex.isMarked()) {
                    updateHeap(fringe, fringeVertex, e.getValue(), start);
                }
            } catch (BadVertexException e1) {
                e1.printStackTrace();
            }
        }
        fringe.heapify();
        int index = 1;
        while(!fringe.isEmpty()) {
            TreeVertex tVert = fringe.remove();
            mst[index] = tVert;
            distance += tVert.getWeight();
            //Update the fringe
            vertices[tVert.getPos()].mark();
            List<Edge> eList = vertices[tVert.getPos()].getChildren();
            for(Edge e: eList) {
                try {
                    Vertex fringeVertex = e.follow(vertices[tVert.getPos()]);
                    if(!fringeVertex.isMarked()) {
                        updateHeap(fringe, fringeVertex, e.getValue(), tVert);
                    }
                } catch (BadVertexException e1) {
                    e1.printStackTrace();
                }
            }
            //swim up our modified nodes (HOW DO WE HEAPIFY?)
            //I think we look at each possible subtree (all non-leaf nodes)
            //and call 'sink' on that node.
            fringe.heapify();
            index++;
        }
        return mst;
    }

    /* @param: bh, The binary heap to be modified.
     * @param: v, the fringe vertex we are concerned with possibly changing.
     * @param: newWeight, the new and possibly lower edge value.
     * @param: newParent, our new treeVertex we have just added.
     * Descr: Updates the connections in the BinaryHeap based on a new vertex
     * being added. If the new connecting edges are strictly less than the
     * current 'best' edges than we replace them and set a new parent. Otherwise,
     * leave them alone!
     */
    private static void updateHeap(BinaryHeap bh, Vertex v, double newWeight, TreeVertex newParent)
            throws BadVertexException {
        //Only put the shortest of all the connecting edges in our graph!
        int index = findVertexIndexByPos(bh, v.getPosition());
        TreeVertex tv = bh.array[index];
        if(tv != null && (v.getBestWeight() == -1
                || v.getBestWeight() > newWeight)) {
            tv.setParent(newParent);
            tv.setWeight(newWeight);
            v.setBestWeight(newWeight);
        }
    }

    /* Descr: Prints our graph of MST edges only that we are using
     * in our solution. Similar to our original graph but with many
     * edges removed. Aids in visualization!
     */
    private static void printMSTGraph(TreeVertex[] mstList) {
        System.out.println("Minimum Spanning Tree:");
        System.out.println("Adjacency matrix of graph weights:");
        DecimalFormat sigFormat = new DecimalFormat("0.00");
        int n = mstList.length;
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
                for(TreeVertex tv: mstList) {
                    if(tv.getParent() != null) {
                        if ((tv.getPos() == i && tv.getParent().getPos() == j) ||
                                (tv.getPos() == j && tv.getParent().getPos() == i)) {
                            matchedWeight = true;
                            double val = Double.parseDouble(sigFormat.format(tv.getWeight()));
                            System.out.print(val);
                        }
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
