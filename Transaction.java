/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apriori;

import java.io.Serializable;
import java.util.TreeSet;

/**
 * Class that represents a transaction containing a set of items.
 * @author Iago Machado
 */
public class Transaction implements Serializable{
    private final long id;
    private TreeSet<Item> items;

    public Transaction(long id) {
        this.id = id;
        items = new TreeSet<>();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Transaction other = (Transaction) obj;
        return this.id == other.getId();
    }

    public long getId() {
        return id;
    }

    public TreeSet<Item> getItems() {
        return items;
    }
    
    public boolean addItem(Item i){
        return items.add(i);
    }

    @Override
    public String toString() {
        return items.toString();
    }
    
    
}
