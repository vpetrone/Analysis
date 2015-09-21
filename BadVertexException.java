package cs.rit;

/**
 * Author: Vincent Petrone (vxp2993)
 * Descr: A Specified exception for the 'follow' method in Edge.
 * Lets the user know that they input an incorrect Vertex into
 * the 'follow' method so the operation cannot complete.
 */
public class BadVertexException extends Exception {
    public BadVertexException() { super(); }
    public BadVertexException(String message) { super(message); }
}
