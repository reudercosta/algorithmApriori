/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apriori;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

/**
 * Class that implements the data mining algorithm Apriori.
 *
 * @author Iago Machado
 */
public class AprioriAlgorithm {

    private static HashMap<TreeSet<Item>, Integer> frequents = new HashMap<>();
    private static int MIN_FREQUENCY;
    
    /**
     * Applies the Apriori algorithm to find frequent itemsets in the provided
     * transaction collection and generates association rules with the
     * transactions' items.
     *
     * @param transactions collection of transactions from witch frequent
     * itemsets are desired.
     * @param minSupport Minimum support to consider an itemset as frequent.
     * @param minConfidence Minimum confidence to the association rules.
     * @return A list of association rules that meet the minimum support and confidence provided.
     */
    public static List<AssociationRule> run(Collection<Transaction> transactions, double minSupport, double minConfidence) throws IOException {
     long initialTime = 0, finalTime = 0;
        MIN_FREQUENCY = (int)(minSupport*transactions.size());//(int) Math.round(minSupport * transactions.size());
       // System.out.println(minSupport+" / "+MIN_FREQUENCY);
       initialTime = System.currentTimeMillis();
       HashMap<TreeSet<Item>, Integer> frequentItemset = findFrequent1(transactions);
       finalTime = System.currentTimeMillis();
       long time = finalTime -initialTime;
       System.out.println("qtd ItemSets"+frequentItemset.size());
       System.out.println("1-itemsets"+time+"ms");
       frequents.putAll(frequentItemset);  
        for (int k = 2; !frequentItemset.isEmpty(); k++) {
            initialTime = 0; finalTime = 0;
            initialTime = System.currentTimeMillis();
            HashMap<TreeSet<Item>, Integer> candidates = generateCandidates(frequentItemset);
            for (TreeSet<Item> c : candidates.keySet()) {
                for (Transaction t : transactions) {
                    if (t.getItems().containsAll(c)) {    //Counting of occurrence of itemsets
                        int count = candidates.get(c);
                        candidates.put(c, ++count);
                    }
                }
                
            }
            frequents.putAll(frequentItemset);
            frequentItemset.clear();
            for (Entry<TreeSet<Item>, Integer> e : candidates.entrySet()) {
                if (e.getValue() > MIN_FREQUENCY) { //Checks if candidate has the minimum frequence to be frequent
                    frequentItemset.put(e.getKey(), e.getValue());
                    
                }
            }
            finalTime = System.currentTimeMillis();
            long times = finalTime -initialTime;
            System.out.println("qtd ItemSets"+frequentItemset.size());
            System.out.println(k+"-itemsets"+times+"ms");
        }
        List<AssociationRule> associations = generateAssociationRules(transactions.size(), minSupport, minConfidence);
        return associations;
    }

    /**
     * Find set of frequent items of size 1.
     *
     * @param transactions Collection of transaction containing items.
     * @return A set of all size 1 frequent items in transactions.
     */
    private static HashMap<TreeSet<Item>, Integer> findFrequent1(Collection<Transaction> transactions) throws IOException {
      HashMap<TreeSet<Item>, Integer> candidates = new HashMap<>();
        for (Transaction t : transactions) {
            for (Item i : t.getItems()) {
                TreeSet<Item> itemset = new TreeSet<>();
                itemset.add(i);
                if (candidates.containsKey(itemset)) {    //counting of occurrence of items
                    int qnt = candidates.get(itemset);
                    candidates.put(itemset, ++qnt);
                } else {
                    candidates.put(itemset, 1);
                }
            }
        }
        HashMap<TreeSet<Item>, Integer> frequentItems = new HashMap<>();
        for (Entry<TreeSet<Item>, Integer> e : candidates.entrySet()) {
            if (e.getValue() > MIN_FREQUENCY) { //Checks if candidate has the minimum support to be frequent
                frequentItems.put(e.getKey(), e.getValue());
            }
        }
        return frequentItems;
    }

    /**
     * Generates a set of candidates C(n) of a given F(n-1) frequent itemset.
     *
     * @param frequentItemset set of frequent items of size n-1.
     * @return Set of candidates itemsets of size n.
     */
    private static HashMap<TreeSet<Item>, Integer> generateCandidates(HashMap<TreeSet<Item>, Integer> frequentItemset) throws IOException {
    
        HashMap<TreeSet<Item>, Integer> candidates = new HashMap<>();
        for (TreeSet<Item> i1 : frequentItemset.keySet()) {
            for (TreeSet<Item> i2 : frequentItemset.keySet()) {
                /*Join step*/
                if (i1.subSet(i1.first(), i1.last()).equals(i2.subSet(i2.first(), i2.last())) && !i1.last().equals(i2.last())) {
                    TreeSet<Item> candidate = new TreeSet<>();
                    candidate.addAll(i1);
                    candidate.add(i2.last());
                    /*Prune step*/
                    if (!hasInfrequentSubset(candidate, frequentItemset)) {
                        candidates.put(candidate, 0);
                    }
                }
            }
        }
       
        return candidates;
    }

    /**
     * Check if the candidade set of size n has any infrequent subset of size
     * n-1.
     *
     * @param candidate Set of size n, candidate to be frequent.
     * @param frequentItemset Set of frequent items of size n-1.
     * @return true if the candidate set has any infrequent subset, false
     * otherwise.
     */
    private static boolean hasInfrequentSubset(TreeSet<Item> candidate, HashMap<TreeSet<Item>, Integer> frequentItemset) {
        List<TreeSet<Item>> subsets = getSubsets(candidate);
        for (TreeSet<Item> subset : subsets) {
            if (!frequentItemset.containsKey(subset)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all subsets of size n-1 of a given set of size n.
     *
     * @param set Set whose subsets are desired.
     * @return All subsets of size n-1 of the given set.
     */
    private static List<TreeSet<Item>> getSubsets(TreeSet<Item> set) {
        List<TreeSet<Item>> subsets = new ArrayList<>();
        for (Item item : set) {
            subsets.add((TreeSet<Item>) set.clone());
        }
        for (Iterator i = set.iterator(), j = subsets.iterator(); i.hasNext();) {
            ((TreeSet<Item>) j.next()).remove(i.next());
        }
        return subsets;
    }
    
    /**
     * Combines the frequent itemsets to generate association rules
     * based on the the minimum support and confidence provided.
     * 
     * @param numTransactions Number of transactions from the database
     * @param minSupport Minimum support to consider an itemset as frequent.
     * @param minConfidence Minimum confidence to the association rules.
     * @return A List with the Association Rules that meet the requirements of support and confidence.
     */
    private static List<AssociationRule> generateAssociationRules(int numTransactions, double minSupport, double minConfidence) {
        List<AssociationRule> associations = new LinkedList<>();

        for (TreeSet<Item> s1 : frequents.keySet()) {
            for (TreeSet<Item> s2 : frequents.keySet()) {

                TreeSet<Item> intersection = (TreeSet<Item>) s2.clone();
                //Get the intersection of the sets
                intersection.retainAll(s1);

                if (intersection.isEmpty()) {
                    TreeSet<Item> union = (TreeSet<Item>) s1.clone();
                    union.addAll(s2);
                    
                    if(frequents.containsKey(union)){
                        int frequence = frequents.get(union);
                        double support = ((double) frequence) / numTransactions;
                        double confidence = ((double) frequence) / frequents.get(s1);

                        if (support >= minSupport && confidence >= minConfidence) {
                            AssociationRule ar = new AssociationRule(s1, s2, confidence, support);
                            associations.add(ar);
                        }
                    }
                }
            }
        }
        return associations;
    }

}
