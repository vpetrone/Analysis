package cs.rit;

import java.util.Arrays;

/**
 * Author: Vincent Petrone
 * Descr:
 */
public class BinaryHeap {
    
    protected TreeVertex[] array;
    protected int size;

    /**
     * Descr: Constructs a new BinaryHeap.
     */
    public BinaryHeap() {
        array = new TreeVertex[10];
        size = 0;
    }
    public BinaryHeap(int size) {
        array = new TreeVertex[size];
        this.size = 0;
    }

    /**
     * Descr: Adds a value to the min-heap.
     */
    public void add(TreeVertex value) {
        //Check for resizing
        if (size >= array.length - 1) {
            array = this.resize();
        }
        //put into the end of the heap
        size++;
        int index = size;
        array[index] = value;
        //reposition
        swim(this.size);
    }

    public TreeVertex[] resize() {
        return Arrays.copyOf(array, array.length * 2);
    }

    //Returns true if the heap has no elements, false otherwise.
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Descr: Returns (but does not remove) the minimum element in the heap.
     */
    public TreeVertex peek() {
        if (this.isEmpty()) {
            throw new IllegalStateException();
        }
        return array[1];
    }

    /**
     * Descr: Removes and returns the minimum element in the heap.
     */
    public TreeVertex remove() {
        TreeVertex result = peek();
        //swap end leaf with root and remove root
        array[1] = array[size];
        array[size] = null;
        size--;
        sink(1);
        return result;
    }

    public void swap(int index1, int index2) {
        TreeVertex temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
    }

    /**
     * Performs the "sink" operation to place the element that is at the
     * root of the heap in its correct place so that the heap maintains the
     * min-heap order property.
     */
    private void sink(int idx) {
        int index = idx;
        while (hasLeftChild(index)) {
            //find smallest child
            int smallerChild = leftIndex(index);
                //sink with smaller child, if I have a smaller child
                if (hasRightChild(index)
                        && array[leftIndex(index)].getWeight() > array[rightIndex(index)].getWeight()) {
                    smallerChild = rightIndex(index);
                }
                if (array[index].getWeight() != -1 &&
                        array[index].getWeight() > array[smallerChild].getWeight()) {
                    swap(index, smallerChild);
                } else {
                    break;
                }
            index = smallerChild;
        }
    }

    public void heapify() {
        for(int i=size-1; i > 0; i--) {
            if(hasLeftChild(i)) {
                sink(i);
            }
        }
    }

    /**
     * Performs the "swim" operation to place a newly inserted element
     * (i.e. the element that is at the size index) in its correct place so
     * that the heap maintains the min-heap order property.
     */
    public void swim(int idx) {
        int index = idx;

        while (array[index].getWeight() != -1 && hasParent(index) &&
              (parent(index).getWeight() == -1 ||
               parent(index).getWeight() > array[index].getWeight())) {
            //parent and child are out of order
            swap(index, parentIndex(index));
            index = parentIndex(index);
        }
    }

    public boolean hasParent(int i) {
        return i > 1;
    }

    public TreeVertex parent(int i) {
        return array[parentIndex(i)];
    }

    public int parentIndex(int i) {
        return i / 2;
    }

    public int leftIndex(int i) {
        return i * 2;
    }

    public int rightIndex(int i) {
        return i * 2 + 1;
    }

    public boolean hasLeftChild(int i) {
        return leftIndex(i) <= size;
    }

    public boolean hasRightChild(int i) {
        return rightIndex(i) <= size;
    }

    public int getSize() {
        return size;
    }

    public TreeVertex[] toArray() {
        return array;
    }
}
