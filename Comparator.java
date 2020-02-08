/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apriori;

import java.io.Serializable;

/**
 *
 * @author Iago Machado
 */
public class Comparator implements Serializable{
    
    private double alfa;
    
    public Comparator(double alfa){
        this.alfa = alfa;
    }
    
    public int compare(AssociationRule ar, AssociationRule ar2){
        double scoreAr = score(ar);
        double scoreAr2 = score(ar2);
        if(scoreAr > scoreAr2){
            return 1;
        } else if(scoreAr < scoreAr2){
            return -1;
        } else{
            return 0;
        }
    }
    
    private double score(AssociationRule ar){
        //(1-alfa)*ar.getSupport() + alfa*ar.getConfidence();
        double result = (1-alfa)*ar.getSupport() + alfa*ar.getConfidence();
        return result;
    }
}
