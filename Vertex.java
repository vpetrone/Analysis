package cs.rit;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Vincent Petrone (vxp2993)
 * Descr: A single node on our Euclidean plane.
 * Contains a position on the plane, its relative position
 * of weights, parent vertex, and list of children edges.
 */
public class Vertex implements Comparable<Vertex> {
    private int position;
    private boolean marked;
    private int xCoord;
    private int yCoord;
    private Vertex parent;
    private List<Edge> edges;
    private double bestWeight;

    DecimalFormat sigFormat = new DecimalFormat("0.00");

    public Vertex(int x, int y, int position) {
        this.xCoord = x;
        this.yCoord = y;
        this.position = position;
        this.marked = false;
        this.parent = null;
        this.bestWeight = -1;
        edges = new ArrayList<Edge>();
    }
    //Adds a child edge to the vertex
    public void addEdge(Edge e) {
        edges.add(e);
    }
    /* @param a: The given x coordinate
     * @param b: The given y coordinate
     * Descr: Utilizes Euclidian distance formula to generate the distance
     * on a Euclidian plane between the stored x,y coordinates and a given
     * pair.
     */
    public double getWeightedDistance(Vertex v) {
        DecimalFormat sigFormat = new DecimalFormat("0.00");
        double rawAnswer = Math.sqrt(Math.abs(
                Math.pow(xCoord - v.getX(), 2) + Math.pow(yCoord - v.getY(), 2)));
        return Double.parseDouble(sigFormat.format(rawAnswer));
    }
    /* Descr: our find(u) method using our path compression heuristic. Takes
     * no parameter and is structured differently than the given pseudocode
     * (mainly due to my choice to make a root have a null parent) However the
     * function operates with the same logic in the same manner.
     */
    public Vertex findRoot() {
        if(parent != null) {
            parent = parent.findRoot();
        }
        if(isRoot()) {
            return this;
        }
        return parent;
    }
    public void mark() {
        marked = true;
    }

    //Root nodes have a null parent
    public boolean isRoot() {
        return parent == null;
    }

    public void setBestWeight(double d) {
        this.bestWeight = d;
    }
    public void setParent(Vertex v) { parent = v; }
    public Vertex getParent() { return parent; }
    public double getBestWeight() { return bestWeight; }
    public int getPosition() { return position; }
    public int getX() { return xCoord; }
    public int getY() { return yCoord; }
    public boolean isMarked() {
        return marked;
    }
    public List<Edge> getChildren() {
        return edges;
    }

    public String toString() {
        return "(" + xCoord + "," + yCoord + ") ";
    }
    @Override
    public int compareTo(Vertex o) {
        return xCoord - o.getX();
    }
}
