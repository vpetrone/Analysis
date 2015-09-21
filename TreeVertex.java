package cs.rit;

import sun.reflect.generics.tree.Tree;

/**
 * Author: Vincent Petrone (vxp2993)
 */
public class TreeVertex {
    private int position;
    private double weight;
    private TreeVertex parent;
    private boolean marked;

    public TreeVertex(int position, double weight, TreeVertex parent) {
        this.position = position;
        this.weight = weight;
        this.parent = parent;
        this.marked = false;
    }

    public void setParent(TreeVertex tv) {
        parent = tv;
    }
    public void setWeight(double newWeight) {
        weight = newWeight;
    }

    public void mark() {
        marked = true;
    }

    public int getPos() {
        return position;
    }
    public double getWeight() {
        return weight;
    }
    public TreeVertex getParent() {
        return parent;
    }
    public boolean isMarked() {
        return marked;
    }
}
