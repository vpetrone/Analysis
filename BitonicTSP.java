package cs.rit;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Author: Vincent Petrone (vxp2993)
 * Descr: Calculates a bitonic path of the travelling salesman problem
 * using dynamic programming to keep track of previously computed distances
 * in a table.
 */
public class BitonicTSP {

    private static Vertex[] bitonicPath;
    private static PrintWriter delay;
    private static DecimalFormat sigFormat = new DecimalFormat("0.00");
    private static double distance;

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
        calculateBitonic(g.getVertexList(), g);
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
            delay.flush();
        }
        System.out.print("\n" + "Distance using bitonic: " + sigFormat.format(distance) + " for path");
        for(Vertex v: bitonicPath) {
            System.out.print(" " + v.getPosition());
        }
        System.out.println("\n" + "Runtime for bitonic TSP   : "
                + runTime + " milliseconds");
    }

    /**
     * @param vList, A list of our vertices
     * @param g, the graph that holds out weights and relations
     * Descr: Performs the calculations for a bitonic TSP. We sort each
     * vertex by the row value, and traverse each vertex to calculate relational
     * distances and keep them in a table. Then we assign a direction to each
     * vertex depending on whether it exists in the n-table, and find the optimal
     * tour by traversing out nodes in euclidean x-order.
     */
    private static void calculateBitonic(Vertex[] vList, Graph g) {
        Arrays.sort(vList); //Sorts by x coordinate
        delay.write("\nSorted X-Y Coordinates:\n");
        for(int i=0; i<vList.length; i++) {
            delay.write("v" + vList[i].getPosition() + ": " + vList[i].toString());
        }
        delay.write("\n");

        //Initialize tables
        int n = vList.length;
        double[][] lTable = new double[n][n]; //default double is 0
        int[][] nTable = new int[n][n];
        for(int[] arr: nTable) {
            Arrays.fill(arr, -1);
        }
        //Algorithm
        for(int j=1; j < n; j++) {
            for(int i=0; i < j; i++) {
                if(i == 0 && j == 1) {
                    lTable[i][j] = vList[i].getWeightedDistance(vList[j]);
                    nTable[i][j] = i;
                } else if(j > i+1) {
                    lTable[i][j] = lTable[i][j-1] + vList[j-1].getWeightedDistance(vList[j]);
                    nTable[i][j] = j-1;
                } else {
                    lTable[i][j] = Double.POSITIVE_INFINITY; //positive infinity!
                    for(int k=0; k < i; k++) {
                        double q = lTable[k][i] + vList[k].getWeightedDistance(vList[j]);
                        if(q < lTable[i][j]) {
                            lTable[i][j] = q;
                            nTable[i][j] = k;
                        }
                    }
                }
            }
        }
        if(vList.length <= 10) {
            //Print tables:
            delay.write("\nL-Table:\n");
            for (int q = 0; q < lTable.length; q++) {
                for (int w = 0; w < lTable.length; w++) {
                    delay.write(sigFormat.format(lTable[q][w]) + "  ");
                }
                delay.write("\n");
            }
            delay.write("\nN-Table:\n");
            for (int q = 0; q < nTable.length; q++) {
                for (int w = 0; w < nTable.length; w++) {
                    if (w != 0) {
                        if (nTable[q][w] == -1) {
                            delay.write(" " + nTable[q][w]);
                        } else {
                            delay.write("  " + nTable[q][w]);
                        }
                    } else {
                        delay.write(nTable[q][w] + "");
                    }
                }
                delay.write("\n");
            }
        }
        //Get Distance
        double longerComponent = lTable[lTable.length-2][lTable.length-1];
        double shorterComponent =
                vList[lTable.length-2].getWeightedDistance(vList[lTable.length-1]);
        distance = longerComponent + shorterComponent;

        //Mark True/False
        int nextNeighbor = nTable[nTable.length-2][nTable.length-1];
        vList[nTable.length-1].mark();
        while(nextNeighbor != -1) {
            vList[nextNeighbor].mark();
            int lowestAccessor = 0;
            for(int k=0; k < lTable.length; k++) {
                if(lTable[k][nextNeighbor] != 0 &&
                    lTable[k][nextNeighbor] < lTable[lowestAccessor][nextNeighbor]) {
                    lowestAccessor = k;
                }
            }
            nextNeighbor = nTable[lowestAccessor][nextNeighbor];
        }
        //Extrapolate Path
        int zeroIndex = -1;
        for(int h=0; h < vList.length; h++) {
            if(vList[h].getPosition() == 0) {
                zeroIndex = h;
                break;
            }
        }
        int currentIndex = zeroIndex;
        Vertex[] tempPath = new Vertex[n + 1];
        int pathAccessorInd = 0;
        boolean currentDirection = vList[zeroIndex].isMarked();
        do {
            if(currentIndex == 0 || currentIndex == vList.length - 1) {
                currentDirection = !currentDirection;
                tempPath[pathAccessorInd] = vList[currentIndex];
                pathAccessorInd++;
            } else if(currentDirection == vList[currentIndex].isMarked()) {
                tempPath[pathAccessorInd] = vList[currentIndex];
                pathAccessorInd++;
            }
            currentIndex = manipulateIndex(currentIndex, currentDirection, n);
        } while(pathAccessorInd < n);
        tempPath[pathAccessorInd] = vList[zeroIndex];
        bitonicPath = tempPath;
        return;

    }

    /**
     * @param curInd, The current, or previous index
     * @param curDir, the direction we trying to move in
     * @param n, the size of our list (for wrapping)
     * Descr: Finds the next index we are concerned with in our list
     * of vertices. We must increment in the desired direction and 'wrap'
     * the end and beginning of the list together.
     * @return the index of our next position in the path.
     */
    private static int manipulateIndex(int curInd, boolean curDir, int n) {
        if(curDir) {
            curInd += 1;
        } else {
            curInd -= 1;
        }
        if(curInd == n) {
            curInd = 0;
        } else if(curInd == -1) {
            curInd = n-1;
        }
        return curInd;
    }
}
