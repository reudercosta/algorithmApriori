/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apriori;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeSet;
import jdk.nashorn.internal.ir.BreakNode;

/**
 * Class that implements the qualitative preference query algorithm skyline to
 * generate association rules according to its support and confidence.
 *
 * @author Iago Machado and Reuder Cerqueira
 */
public class PrefRuleSky {

    /**
     * Runs the qualitative preference query algorithm to generate preferential
     * association rules from the provided transactions.
     *
     * @param transactions Set of transactions in which will be generated the
     * association rules.
     * @return A list of preferential association rules.
     */
    public static List<AssociationRule> run(Collection<Transaction> transactions) throws IOException, FileNotFoundException, ClassNotFoundException {
        long initialTime = 0, finalTime = 0;
        initialTime = System.currentTimeMillis();
        // generation(transactions);
        // System.exit(0);
        File itemsets = generateItemsets1(transactions);
        finalTime = System.currentTimeMillis();
        long time = finalTime - initialTime;
        System.out.println("Execution time-: " + time + "ms");
        int k = 2;
        while (itemsets != null) {
            initialTime = 0;
            finalTime = 0;
            initialTime = System.currentTimeMillis();
            File candidates = generateCandidates(itemsets, k, transactions);
            itemsets = candidates;
            k++;
            finalTime = System.currentTimeMillis();
            long times = finalTime - initialTime;
            System.out.println("Execution time: " + times + "ms");
        }
        File associations = generateAssociationRules(transactions.size());
        return skylineQuery(associations);
    }

    private static File generateItemsets1(Collection<Transaction> transactions) throws FileNotFoundException, IOException, ClassNotFoundException {
        HashMap<TreeSet<Item>, Integer> itemsets1 = new HashMap<>();

        File dirItemsets = new File("Itemsets");
        dirItemsets.mkdir();

        File file1itemsets = new File(dirItemsets.getAbsolutePath() + "/1itemsets.dat");
        FileWriter fileos = new FileWriter(file1itemsets);
        try (BufferedWriter itemsets1Writer = new BufferedWriter(fileos)) {
            for (Transaction t : transactions) {
                for (Item i : t.getItems()) {
                    TreeSet<Item> itemset = new TreeSet<>();
                    itemset.add(i);
                    if (itemsets1.containsKey(itemset)) {    //counting of occurrence of items
                        int qnt = itemsets1.get(itemset);
                        itemsets1.put(itemset, ++qnt);
                    } else {
                        itemsets1.put(itemset, 1);
                    }
                }
            }

            for (Entry<TreeSet<Item>, Integer> e : itemsets1.entrySet()) {
                int frequency = e.getValue();
                // int freq = generation(transactions);
                if (frequency > 0) {
                    itemsets1Writer.write(String.valueOf(frequency));
                    for (Item i : e.getKey()) {
                        itemsets1Writer.write(",");
                        itemsets1Writer.write(Long.toString(i.getId()));
                    }
                    itemsets1Writer.newLine();
                }
            }

            itemsets1.clear();
        }
        return file1itemsets;
    }

    private static File generateCandidates(File itemsetsFile, int k, Collection<Transaction> transactions) throws FileNotFoundException, IOException, ClassNotFoundException {
        int countCandidates = 0;
        long initialTime = 0, finalTime = 0;

        File dirItemsets = new File("Itemsets");

        BufferedReader fileReader = new BufferedReader(new FileReader(itemsetsFile));

        File fileCandidates = new File(dirItemsets.getAbsolutePath() + "/" + String.valueOf(k) + "itemsets.dat");

        try (BufferedWriter objWriter = new BufferedWriter(new FileWriter(fileCandidates))) {
            String line1 = fileReader.readLine();
            while (line1 != null) {
                TreeSet<Item> itemset1 = buildItemset(line1);
                line1 = fileReader.readLine();

                BufferedReader fileReader2 = new BufferedReader(new FileReader(itemsetsFile));

                /*This part is just to put the fileReader2 just after the fileReader position in the file
                avoiding to generate duplicated candidates in the candidates file*/
                TreeSet<Item> aux = buildItemset(fileReader2.readLine());
                while (!aux.equals(itemset1)) {
                    aux = buildItemset(fileReader2.readLine());
                }
                aux = null;

                String line2 = fileReader2.readLine();
                while (line2 != null) {
                    TreeSet<Item> i2 = buildItemset(line2);
                    line2 = fileReader2.readLine();

                    /*Join step*/
                    if (itemset1.subSet(itemset1.first(), itemset1.last()).equals(i2.subSet(i2.first(), i2.last())) && !itemset1.last().equals(i2.last())) {
                        TreeSet<Item> candidate = new TreeSet<>();
                        candidate.addAll(itemset1);
                        candidate.add(i2.last());

                        int countFrequency = 0;
                        for (Transaction t : transactions) {
                            if (t.getItems().containsAll(candidate)) {    //Counting of occurrence of the candidate
                                countFrequency++;
                            }
                        }
                        // int freq = generation(transactions);
                        if (countFrequency > 0) {
                            objWriter.write(String.valueOf(countFrequency));
                            for (Item i : candidate) {
                                objWriter.write(",");
                                objWriter.write(Long.toString(i.getId()));
                            }
                            objWriter.newLine();
                            countCandidates++;
                        }
                    }
                }
            }
        }

        if (countCandidates == 0) {
            fileCandidates = null;
        }

        System.out.println(k + "-itemsets =" + countCandidates);
        return fileCandidates;
    }

    private static TreeSet<Item> buildItemset(String line) {
        StringTokenizer builder = new StringTokenizer(line, ",");

        Integer.valueOf(builder.nextToken());

        TreeSet<Item> itemset = new TreeSet<>();
        while (builder.hasMoreTokens()) {
            itemset.add(new Item(Long.parseLong(builder.nextToken())));
        }
        return itemset;
    }

    /**
     * Generate association rules from the set of itemsets "kItemsets" generated
     * before.
     *
     * @param numTransactions number of database transactions.
     * @return a file of association rules.
     */
    private static File generateAssociationRules(int numTransactions) throws FileNotFoundException, IOException, ClassNotFoundException {
        File dirItemsets = new File("Itemsets");

        File associationRulesFile = new File("Association Rules.dat");
        try (BufferedWriter associationRulesWriter = new BufferedWriter(new FileWriter(associationRulesFile))) {
            for (File itemsetsFile1 : dirItemsets.listFiles()) {
                BufferedReader objReader = new BufferedReader(new FileReader(itemsetsFile1));
                String line = objReader.readLine();
                while (line != null) {
                    StringTokenizer tokenizer = new StringTokenizer(line, ",");
                    int s1Frequency = Integer.valueOf(tokenizer.nextToken());
                    TreeSet<Item> s1 = buildItemset(line);

                    for (File itemsetsFile2 : dirItemsets.listFiles()) {
                        BufferedReader objReader2 = new BufferedReader(new FileReader(itemsetsFile2));
                        String line2 = objReader2.readLine();
                        while (line2 != null) {
                            tokenizer = new StringTokenizer(line2, ",");
                            int s2Frequency = Integer.valueOf(tokenizer.nextToken());
                            TreeSet<Item> s2 = buildItemset(line2);

                            TreeSet<Item> intersection = new TreeSet<>();
                            for (Item i : s1) {
                                if (s2.contains(i)) {
                                    intersection.add(i);
                                }
                            }

                            if (intersection.isEmpty()) {
                                TreeSet<Item> union = (TreeSet<Item>) s1.clone();
                                union.addAll(s2);

                                int frequency = findItemsetCount(union, dirItemsets.getAbsolutePath());
                                if (frequency != 0) {
                                    double support = ((double)frequency) / numTransactions;
                                    double confidence;
                                    confidence = (s1Frequency == 0) ? 0 : ((double)frequency )/ s1Frequency;
                                    AssociationRule ar = new AssociationRule(s1, s2, confidence, support);
                                    associationRulesWriter.write(s1.toString() + "->" + s2.toString() + ";" + String.valueOf(confidence) + ";" + String.valueOf(support));
                                    associationRulesWriter.newLine();

                                }
                            }
                            line2 = objReader2.readLine();
                        }
                    }
                    line = objReader.readLine();
                }
            }
        }
        return associationRulesFile;
    }

    /**
     * Applies the skyline preference query to select the bests association
     * rules.
     *
     * @param associationRules Association rules generated from the database.
     * @return the association rules that are not dominated for any other in the
     * database.
     */
    private static List<AssociationRule> skylineQuery(File associationRulesFile) throws FileNotFoundException, IOException, ClassNotFoundException {
        List<AssociationRule> skyAssociationRules = new LinkedList<>();
        boolean dominate;

        try (BufferedReader arReader = new BufferedReader(new FileReader(associationRulesFile))) {
            String line = arReader.readLine();
            if (line != null) {
                AssociationRule ar = createAssociationRule(line);

                skyAssociationRules.add(ar);

                line = arReader.readLine();
                while (line != null) {
                    ar = createAssociationRule(line);
                    dominate = false;
                    Iterator<AssociationRule> iter = skyAssociationRules.iterator();
                    while (iter.hasNext()) {
                        AssociationRule ar2 = iter.next();
                        //verifica se a regra r1 é dominada por r2
                        if (dominated(ar, ar2)) { //|| ar2.getSupport() > ar.getSupport() && ar2.getConfidence() >= ar.getConfidence())
                            dominate = true;
                            break;
                        } else if (dominated(ar2, ar)) {
                            //verifica se a regra r2 é dominada por r1
                            iter.remove();
                        }
                    }
                    if (!dominate) {
                        skyAssociationRules.add(ar);
                    }

                    line = arReader.readLine();
                }
            }
        }
        return skyAssociationRules;
    }

    private static AssociationRule createAssociationRule(String line) {
        StringTokenizer builder = new StringTokenizer(line, "->|;");
        String s1String = builder.nextToken();
        String s2String = builder.nextToken();
        String confidence = builder.nextToken();
        String support = builder.nextToken();

        builder = new StringTokenizer(s1String, "[|]");
        s1String = builder.nextToken();

        s1String = s1String.trim();
        builder = new StringTokenizer(s1String, ",");
        TreeSet s1 = new TreeSet<>();
        while (builder.hasMoreTokens()) {
            Long id = Long.parseLong(builder.nextToken().trim());
            s1.add(new Item(id));
        }

        builder = new StringTokenizer(s2String, "[|]");
        s2String = builder.nextToken();

        s2String = s2String.trim();
        builder = new StringTokenizer(s2String, ",");
        TreeSet s2 = new TreeSet<>();
        while (builder.hasMoreTokens()) {
            Long id = Long.parseLong(builder.nextToken().trim());
            s2.add(new Item(id));
        }
        AssociationRule ar = new AssociationRule(s1, s2, Double.parseDouble(confidence), Double.parseDouble(support));
        return ar;
    }

    private static int findItemsetCount(TreeSet<Item> itemset, String absolutePath) throws FileNotFoundException, IOException, ClassNotFoundException {
        int k = itemset.size();
        try (BufferedReader objReader = new BufferedReader(new FileReader(new File(absolutePath + "/" + String.valueOf(k) + "itemsets.dat")))) {

            String line = objReader.readLine();
            while (line != null) { //Checks if the reader reaches the end of the file
                StringTokenizer builder = new StringTokenizer(line, ",");
                int count = Integer.valueOf(builder.nextToken());
                TreeSet<Item> readSet = new TreeSet<>();
                while (builder.hasMoreTokens()) {
                    readSet.add(new Item(Long.parseLong(builder.nextToken())));
                }

                if (readSet.equals(itemset)) {
                    return count;
                }
                line = objReader.readLine();
            }
        } catch (FileNotFoundException ex) {
            return 0;
        }
        return 0;
    }

    private static boolean dominated(AssociationRule ar1, AssociationRule ar2) {
        return (ar1.getSupport() < ar2.getSupport() && ar1.getConfidence() <= ar2.getConfidence())
                || (ar1.getSupport() <= ar2.getSupport() && ar1.getConfidence() < ar2.getConfidence());
    }

    /**
     * Applies the skyline preference query to select the bests association
     *
     *
     * @param frequencyItens list of itemsets generated from the database.
     * @return a list of items that are not dominated for any other item in the
     * database.
     */
    private static List<String> skylineQueryFrequecy(List<String> frequencyItens) throws FileNotFoundException, IOException, ClassNotFoundException {
        List<String> skyAssociationRules = new ArrayList<>();
        String token[] = null;
        String token1[] = null;
        boolean dominate;
        Iterator<String> it = frequencyItens.iterator();
        token = it.next().split(",");
        while (it.hasNext()) {
            int FrequencyItem = Integer.parseInt(token[0]);
            skyAssociationRules.add(token[0]);
            dominate = false;
            Iterator<String> its = skyAssociationRules.iterator();
            while (its.hasNext()) {
                token1 = its.next().split(",");
                int FrequencyItem1 = Integer.parseInt(token1[0]);
                //verifica se a regra r1 é dominada por r2
                if (dominatedFreq(FrequencyItem, FrequencyItem1)) { //|| ar2.getSupport() > ar.getSupport() && ar2.getConfidence() >= ar.getConfidence())
                    dominate = true;
                    break;
                } else if (dominatedFreq(FrequencyItem1, FrequencyItem)) {
                    //verifica se a regra r2 é dominada por r1
                    its.remove();
                    //  skyAssociationRules.clear();
                }
            }
            if (!dominate) {
                skyAssociationRules.add(token[0]);
            }
            token = it.next().split(",");
            //line = arReader.readLine();
        }
        Iterator<String> ite = skyAssociationRules.iterator();
        int smaller = 0;
        while (ite.hasNext()) {
            String token2[] = ite.next().split(",");
            int large = Integer.parseInt(token2[0]);
            if (large > smaller) {
                smaller = large;

            } else {
                ite.remove();
            }
        }
        return skyAssociationRules;
    }

    /**
     * Applies the skyline preference query to select the bests frequent
     *
     * @param item two itemsets frequents generated from the
     * generateItemsetsRestric.
     * @return se dominate true or if no dominate return false.
     */
    private static boolean dominatedFreq(int ar1, int ar2) {
        return ((ar1 <= ar2));
    }

    /**
     * @param transaction generated the transactions of database.
     * @return a list as the bests supports appling the skyline preference
     * query.
     */
    private static int generation(Collection<Transaction> transactions) throws IOException, FileNotFoundException, ClassNotFoundException {
        List<Object> list = new LinkedList<>();
        int restrictSupp = 0;
        for (Object key : skylineQueryFrequecy(generateItemsetsRestrict(transactions))) {
            list.add(key);
        }

        restrictSupp = Integer.parseInt((String) list.get(0));

        return restrictSupp;
    }

    /**
     * @param transaction list of itemsets frequents generated from the
     * database.
     * @return a list of 1-itemsets in the database.
     */
    private static List<String> generateItemsetsRestrict(Collection<Transaction> transactions) throws FileNotFoundException, IOException, ClassNotFoundException {
        HashMap<TreeSet<Item>, Integer> itemsets1 = new HashMap<>();
        List<String> list = new LinkedList<>();

        for (Transaction t : transactions) {
            for (Item i : t.getItems()) {
                TreeSet<Item> itemset = new TreeSet<>();
                itemset.add(i);
                if (itemsets1.containsKey(itemset)) {    //counting of occurrence of items
                    int qnt = itemsets1.get(itemset);
                    itemsets1.put(itemset, ++qnt);
                } else {
                    itemsets1.put(itemset, 1);
                }
            }
        }

        for (Entry<TreeSet<Item>, Integer> e : itemsets1.entrySet()) {
            int frequency = e.getValue();
            if (frequency > 0) {
                // System.out.print(String.valueOf(frequency));
                String a = String.valueOf(frequency);
                for (Item i : e.getKey()) {
                    a += ",";
                    // System.out.print(",");
                    a += Long.toString(i.getId());
                    //   System.out.print(Long.toString(i.getId()));
                }
                list.add(a);
                // System.out.println("");
            }
        }

        itemsets1.clear();
        return list;
    }

}
