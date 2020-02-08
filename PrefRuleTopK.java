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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 *
 * @author Iago Machado
 */
public class PrefRuleTopK {

    private static HashMap<TreeSet<Item>, Integer> kItemsets = new HashMap<>();

    /**
     * Runs the quantitative top-K preference query algorithm to generate
     * preferential association rules from the provided transactions.
     *
     * @param transactions Set of transactions in which will be generated the
     * association rules.
     * @param k Number of rules to be generated. The k best rules will be
     * returned.
     * @param alpha Double that will be used to balance the values between
     * support and confidence.
     * @return A list of the k best preferential association rules.
     */
    public static List<AssociationRule> run(Collection<Transaction> transactions, int k, double alpha) throws IOException, FileNotFoundException, ClassNotFoundException {
        long initialTime = 0, finalTime = 0;
        initialTime = System.currentTimeMillis();
      //  generation(transactions, k, alpha);
        File itemsets = generateItemsets1(transactions);
        finalTime = System.currentTimeMillis();
        long time = finalTime - initialTime;
        System.out.println("Execution time-: " + time + "ms");
        int count = 2;
        while (itemsets != null) {
            initialTime = 0;
            finalTime = 0;
            initialTime = System.currentTimeMillis();
            File candidates = generateCandidates(itemsets, count, transactions);
            itemsets = candidates;
            count++;
            finalTime = System.currentTimeMillis();
            long times = finalTime - initialTime;
            System.out.println("Execution time: " + times + "ms");
        }
        File associations = generateAssociationRules(transactions.size());
        List<AssociationRule> topKRules = topKRules(associations, k, alpha);
        return topKRules;
    }
    
    /**
     * Generates a set of itemsets with just one item for all the transactions
     * items.
     *
     * @param transactions A collection of all the database transactions.
     * @return A set of itemsets of size 1.
     */
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

            for (Map.Entry<TreeSet<Item>, Integer> e : itemsets1.entrySet()) {
                int frequency = e.getValue();
              //  int freq = generation(transactions,10, 0.5);
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

    /**
     * Generates a set of candidates C(n) of a given C(n-1) itemset.
     *
     * @return Set of candidate itemsets of size n.
     */
    private static File generateCandidates(File itemsetsFile, int k, Collection<Transaction> transactions) throws FileNotFoundException, IOException, ClassNotFoundException {
        int countCandidates = 0;

        File dirItemsets = new File("Itemsets");

        BufferedReader fileReader = new BufferedReader(new FileReader(itemsetsFile));

        File fileCandidates = new File(dirItemsets.getAbsolutePath() + "/" + String.valueOf(k) + "itemsets.dat");
        try (BufferedWriter objWriter = new BufferedWriter(new FileWriter(fileCandidates))) {

            String line1 = fileReader.readLine();
            while (line1 != null) {
                StringTokenizer builder = new StringTokenizer(line1, ",");
                Integer.valueOf(builder.nextToken());
                TreeSet<Item> i1 = new TreeSet<>();
                while (builder.hasMoreTokens()) {
                    i1.add(new Item(Long.parseLong(builder.nextToken())));
                }
                line1 = fileReader.readLine();

                BufferedReader fileReader2 = new BufferedReader(new FileReader(itemsetsFile));

                /*This part is just to put the objReader2 just after the objReader position in the file
                avoiding to generate duplicated candidates in the candidates file*/
                builder = new StringTokenizer(fileReader2.readLine(), ",");
                Integer.valueOf(builder.nextToken());
                TreeSet<Item> aux = new TreeSet<>();
                while (builder.hasMoreTokens()) {
                    aux.add(new Item(Long.parseLong(builder.nextToken())));
                }
                while (!aux.equals(i1)) {
                    builder = new StringTokenizer(fileReader2.readLine(), ",");
                    Integer.valueOf(builder.nextToken());
                    aux = new TreeSet<>();
                    while (builder.hasMoreTokens()) {
                        aux.add(new Item(Long.parseLong(builder.nextToken())));
                    }
                }
                aux = null;

                String line2 = fileReader2.readLine();
                while (line2 != null) {
                    builder = new StringTokenizer(line2, ",");
                    Integer.valueOf(builder.nextToken());
                    TreeSet<Item> i2 = new TreeSet<>();
                    while (builder.hasMoreTokens()) {
                        i2.add(new Item(Long.parseLong(builder.nextToken())));
                    }
                    line2 = fileReader2.readLine();

                    /*Join step*/
                    if (i1.subSet(i1.first(), i1.last()).equals(i2.subSet(i2.first(), i2.last())) && !i1.last().equals(i2.last())) {
                        TreeSet<Item> candidate = new TreeSet<>();
                        candidate.addAll(i1);
                        candidate.add(i2.last());

                        int countFrequency = 0;
                        for (Transaction t : transactions) {
                            if (t.getItems().containsAll(candidate)) {    //Counting of occurrence of the candidate
                                countFrequency++;
                            }
                        }
                       // int freq = generation(transactions,10, 0.5);
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

                            TreeSet<Item> intersection = new TreeSet<Item>();
                            for (Item i : s1) {
                                if (s2.contains(i)) {
                                    intersection.add(i);
                                }
                            }

                            if (intersection.size() == 0) {
                                TreeSet<Item> union = (TreeSet<Item>) s1.clone();
                                union.addAll(s2);

                                int frequency = findItemsetCount(union, dirItemsets.getAbsolutePath());
                                if (frequency != 0) {
                                    double support = ((double) frequency) / numTransactions;
                                    double confidence;
                                    confidence = (s1Frequency == 0) ? 0 : ((double) frequency) / s1Frequency;

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

    private static List<AssociationRule> topKRules(File associationRules, int k, double alfa) throws FileNotFoundException, IOException {
        Comparator comparator = new Comparator(alfa);
        MinHeap heap = new MinHeap(comparator);

        try (BufferedReader reader = new BufferedReader(new FileReader(associationRules))) {
            String line = reader.readLine();
            while (line != null) {
                AssociationRule ar = createAssociationRule(line);
                heap.add(ar);
                if (heap.size() > k) {
                    heap.remove();
                }
                line = reader.readLine();
            }

        }
        List<AssociationRule> topKRules = new LinkedList<>();
        if (heap.size() != 0) {
            for (int i = 0; i < k; i++) {
                topKRules.add(heap.remove());
            }
        }
        return topKRules;
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
     * @param transaction list of itemsets frequents generated from the
     * database.
     * @return a list of 1-itemsets in the database.
     */
    private static List generateItemsetsRestrict(Collection<Transaction> transactions) throws FileNotFoundException, IOException, ClassNotFoundException {
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

        for (Map.Entry<TreeSet<Item>, Integer> e : itemsets1.entrySet()) {
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

    private static List topKRulesRestric(List<String> frequentItems, int k, double alfa) throws FileNotFoundException, IOException {
        ComparatorTopk comparator = new ComparatorTopk(alfa);
        MinHeapTopk heap = new MinHeapTopk(comparator);

        for (String key : frequentItems) {
            String token[] = key.split(",");
            int freqItem = Integer.parseInt(token[0]);
            heap.add(freqItem);
            if (heap.size() > k) {
                heap.remove();
            }

        }

        List<String> topKRules = new LinkedList<>();
        if (heap.size() != 0) {
            for (int i = 0; i < k; i++) {
                topKRules.add("" + heap.remove());
            }
        }
        return topKRules;
    }

    /**
     * @param transaction generated the transactions of database.
     * @return a list as the bests supports appling the skyline preference
     * query.
     */
    public static int generation(Collection<Transaction> transactions, int k, double alfa) throws IOException, FileNotFoundException, ClassNotFoundException {
        List<Object> list = new LinkedList<>();
        int restrictSupp = 0;
        int largeValue = 0;
        for (Object key : topKRulesRestric(generateItemsetsRestrict(transactions), k, alfa)) {
            list.add(key);
        }
        for (Object value : list) {
            int v = Integer.parseInt((String) value);
            if (v > largeValue) {
                largeValue = Integer.parseInt((String) value);
            }

        }
        return largeValue;
    } 
    

   

    
}
