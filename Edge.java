package cs.rit;

import java.text.DecimalFormat;

/**
 * Author: Vincent Petrone (vxp2993)
 * Descr: A representative object of an edge in our graph.
 * Holds its 'to' and 'from' vertices in no particular order, a
 * value to represent its weight, and its position in the graph
 * table.
 */
public class Edge {
    private double value;
    //Vertex numerical Values
    private int row;
    private int col;
    //Vertex Object references
    private Vertex v1;
    private Vertex v2;

    public Edge(int row, int col, double value, Vertex v1, Vertex v2) {
        this.value = value;
        this.row = row;
        this.col = col;
        this.v1 = v1;
        this.v2 = v2;
    }
    //Format the weighted value so we keep a specified number of sig figs.
    public double getValue() {
        return value;
    }
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    /*
     * Descr: returns True if the given edge is of smaller percieved value than
     * the native edge, false otherwise.
     */
    public boolean isSmaller(Edge e) {
        if(e.getValue() < value) {
            return true;
        } else if (e.getValue() > value) {
            return false;
        }
        //Values are equal
        if(e.getRow() < row) {
            return true;
        } else if(e.getRow() > row) {
            return false;
        }
        //Rows are equal
        if(e.getCol() < col) {
            return true;
        } else {
            return false;
        }
    }
    //Builds a quick list to return of both vertices connected to this edge.
    public Vertex[] getVertices() {
        return new Vertex[]{v1, v2};
    }

    /*
     *
     *
     */
    public boolean canFollow(Vertex v) {
        return (v == v1 || v == v2);
    }

    /* @param Vertex v, The vertex we are coming from
     * Descr: 'follows' an edge from the given vertex to it's associated vertex.
     * Since our graph is undirected, each edge really represents two edges, therefore
     * a function like this must specify which vertex we are coming from. If we pass in
     * a vertex that is NOT associated with our edge, we throw a BadVertexException!
     * returns: The vertex we are going to
     */
    public Vertex follow(Vertex v) throws BadVertexException {
        if(v == v1) {
            v2.setParent(v1);
            return v2;
        } else if(v == v2) {
            v1.setParent(v2);
            return v1;
        } else {
            throw new BadVertexException("Improper vertex given, does not connect to specified edge.");
        }
    }

    public boolean equals(Edge other) {
        Vertex[] vertices = other.getVertices();
        return (vertices[0] == v1 && vertices[1] == v2);
    }
}
