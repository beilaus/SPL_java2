package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        this.orientation=orientation;
        this.vector=vector;
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
        if(index<0 || index>=vector.length)
            throw new IndexOutOfBoundsException("[Sharedvector:Get]: Index out of bounds");
        readLock();
        try {
            double output = vector[index];
            return output;
        }
        finally {
            readUnlock();
        }
    }

    public int length() {
        return vector.length;
    }

    public VectorOrientation getOrientation() {
        readLock();
        try{
            VectorOrientation orient=orientation;
            return orient;
        }
        finally{readUnlock();}
    }

    public void writeLock() {
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        lock.writeLock().unlock();
    }

    public void readLock() {
        lock.readLock().lock();
    }

    public void readUnlock() {
        lock.readLock().unlock();
    }

    public void transpose() {
        writeLock();
        try{
            VectorOrientation opposite = (this.orientation == VectorOrientation.ROW_MAJOR) ? VectorOrientation.COLUMN_MAJOR : VectorOrientation.ROW_MAJOR;      
            this.orientation=opposite;
        }
        
        finally{writeUnlock();}
        
        
    }

    public void add(SharedVector other) {
        if(vector.length!=other.vector.length)
            throw new IllegalArgumentException("[Add]: Cannot add vectors with diferent sizes");
        
        writeLock();
        other.readLock();
        try{
            if (orientation!=other.orientation)
             throw new IllegalArgumentException("[Add]: Cannot add vectors with diferent orientations");
        for (int i=0;i<vector.length;i++){
            vector[i]+=other.vector[i];
            }  
        }
        finally{
            writeUnlock();
            other.readUnlock();
        }
    } 

    public void negate() {
        // TODO: negate vector
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        return 0;
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
    }
}
