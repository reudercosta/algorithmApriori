/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apriori;

import java.io.Serializable;
import java.util.Objects;
import java.util.TreeSet;

/**
 * Class that represent an Association Rule between two sets
 * with confidence and support.
 * @author Iago Machado
 */
public class AssociationRule implements Serializable{
    private TreeSet<Item> s1;
    private TreeSet<Item> s2;
    private double confidence;
    private double support;

    public AssociationRule(TreeSet s1, TreeSet s2, double confidence, double support) {
        this.s1 = s1;
        this.s2 = s2;
        this.confidence = confidence;
        this.support = support;
    }

    public double getConfidence() {
        return confidence;
    }

    public double getSupport() {
        return support;
    }

    @Override
    public String toString() {
        return s1.toString() + "->" + s2.toString() + ", confidence = " + String.valueOf(confidence) + "%, support = " + String.valueOf(support)+"%";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.s1);
        hash = 97 * hash + Objects.hashCode(this.s2);
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
        final AssociationRule other = (AssociationRule) obj;
        if (!Objects.equals(this.s1, other.s1)) {
            return false;
        }
        if (!Objects.equals(this.s2, other.s2)) {
            return false;
        }
        return true;
    }
    
    
    
}
