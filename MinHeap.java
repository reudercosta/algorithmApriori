/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apriori;

/**
 *
 * @author Iago Machado
 */
public class MinHeap {
    private AssociationRule[] data;
    private Comparator comparator;
    private int size;
    
    public MinHeap(Comparator comparator){
        data = new AssociationRule[10];
        this.comparator = comparator;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public int size() {
        return size;
    }
    
    private void swap(int p1, int p2){
        AssociationRule temp = data[p1];
        data[p1] = data[p2];
        data[p2] = temp;
    }
    
    public void add(AssociationRule ar) {
        if(size == data.length){
            AssociationRule[] temp = new AssociationRule[size*2];
            System.arraycopy(data, 0, temp, 0, size);
            data = temp;
        }
        data[size] = ar;
        int parent = (size-1)/2;
        int child = size;
        while(child>0 && comparator.compare(data[parent], data[child])>0){
            swap(parent, child);
            child = parent;
            parent = (parent - 1)/2;
        }
        size++;
    }
    
    private int min(int i, int j){
        if(i<size && j<size){
            return comparator.compare(data[i], data[j])<0 ? i : j;
        } else if(i < size){
            return i;
        } else if(j < size){
            return j;
        } else return data.length;
    }
    
    public AssociationRule remove(){
        AssociationRule ret = data[0];
        data[0] = data[size-1];
        size--;
        int parent = 0;
        int child = min(parent*2+1, parent*2+2);
        while(child<size && comparator.compare(data[child], data[parent])<0){
            swap(parent, child);
            parent = child;
            child = min(parent*2+1, parent*2+2);
        }
        return ret;
    }
    
    public AssociationRule peek() {
        return data[0];
    }
}
