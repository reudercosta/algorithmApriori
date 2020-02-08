/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apriori;

import java.io.Serializable;

/**
 * Class that represents an item that is a component of a transaction.
 * @author Iago Machado
 */
public class Item implements Comparable<Item>, Serializable{
    private final long id;

    public Item(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Item){
            Item item = (Item)obj;
            return this.id == item.id;
        }
        return false;
    }

    @Override
    public String toString() {
        return Long.toString(id);
    }

    @Override
    public int compareTo(Item t) {
        return Long.compare(id, t.getId());
    }

    
}
