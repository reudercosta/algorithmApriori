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
public class ComparatorTopk implements Serializable{
    
    private double alfa;
    
    public ComparatorTopk(double alfa){
        this.alfa = alfa;
    }
    
    public int compare(double ar, double ar2){
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
    
    private double score(double ar){
        //
        double result = ((1-alfa)*ar);
        return result;
    }
}
