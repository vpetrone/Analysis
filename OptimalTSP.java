package cs.rit;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Author: Vincent Petrone (vxp2993)
 * Class: Analysis of Algorithms, Section-01
 * Descr: Part 1 of the class project. Solves the travelling salesman
 * problem (TSP) with the optimal path. Straightforward approach.
 */
public class OptimalTSP {

    private static int[] bestPath;

    /* @param: int n, # of vertices (n)
     * @param: int seed, value for the random number generator
     * Descr: Checks command line arguments and runs through the
     * TSP using the most optimal route to solve the problem.
     * Utilizes Graph class as an abstraction to hold vertices
     * and the adjacency matrix.
     */
    public static void main(String[] args) {
        int n = 1, seed = 0;
        try {
            n = Integer.parseInt(args[0]);
            seed = Integer.parseInt(args[1]);
            if(n < 1 || n > 13) {
                System.out.println("Number of vertices must be between 1 and 13");
                System.exit(1);
            }
        } catch(NumberFormatException nfe) {
            System.out.println("Command line args must be integers");
            System.exit(1);
        } catch(Exception e) {
            System.out.println("Usage: java OptimalTSP n seed");
            System.exit(1);
        }
        Graph g = new Graph(n, seed);
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
        double optimalDist = calculateOptimalDistance(g.getWeightList(), n);
        long runTime = System.currentTimeMillis() - startTime;

        System.out.print("\n" + "Optimal Distance: " + optimalDist + " for path");
        for(int i=bestPath.length-1; i>=0; i--) {
            System.out.print(" " + bestPath[i]);
        }
        System.out.println("\n" + "Runtime for optimal TSP   : "
                + runTime + " milliseconds");
    }

    /* @param: double[][] weightList,
     * @param: int n, the # of vertices
     * Descr:
     */
    public static double calculateOptimalDistance(double[][] weightList, int n) {
        //Graphs cannot have negative weights, so we can check if bestWeight has been set
        double bestWeight = -1;
        //Build initial Path
        int[] currentPath = new int[n+1];
        currentPath[n] = 0; //set last element to zero
        for(int i=0; i<n; i++) {
            currentPath[i] = i; // Fill array with initial ordering
        }
        while(currentPath != null) {
            //calculate weights
            double curWeight = 0;
            for(int i=1; i<currentPath.length; i++) {
                int fromNode = currentPath[i-1];
                int toNode = currentPath[i];
                curWeight += weightList[fromNode][toNode];
            }
            //Format the curWeight double to rid minute decimals
            DecimalFormat sigFormat = new DecimalFormat("0.00");
            curWeight = Double.parseDouble(sigFormat.format(curWeight));

            if(curWeight < bestWeight || bestWeight == -1) {
                bestWeight = curWeight;
                bestPath = currentPath.clone();
            }
            //print
            if(n <= 5) {
                System.out.print("Path: ");
                for (int i = 0; i < currentPath.length; i++) {
                    System.out.print(currentPath[i] + " ");
                }
                System.out.println(" distance = " + curWeight);
            }
            //modify permutation (method returns null if we are done)
            currentPath = modifyPermutation(currentPath);
        }
        return bestWeight;
    }

    /* @param: int[] curPerm, The current permutation we are need to modify.
     * Descr: Creates the next permutation in the array from the given
     * permutation. Generates the next permutation in lexicographic order.
     * returns null when we cannot generate a further permutation.
     */
    private static int[] modifyPermutation(int[] curPerm) {
        if(curPerm.length <= 3) return null;
        //We ignore leading and trailing zeros
        for(int i=curPerm.length-2; i>=2; i--) {
            if(curPerm[i-1] < curPerm[i]) {
                int swapDigit = curPerm[i-1]; //The digit we are swapping into the subList
                int swapLocationIndex = i;
                //determine which digit to swap with
                for(int j=i; j< curPerm.length; j++) {
                    if(curPerm[j] < curPerm[i] && curPerm[j] > swapDigit) {
                        swapLocationIndex = j;
                    }
                }//Swap location
                curPerm[i-1] = curPerm[swapLocationIndex];
                curPerm[swapLocationIndex] = swapDigit;
                //Order the sublist
                Arrays.sort(curPerm, i, curPerm.length - 1);
                return curPerm;
            }
        }
        return null;
    }
}
