/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apriori;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Iago Machado
 */
public class Main {

    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Name of database file: ");
        String file = "ale.txt";//scanner.nextLine();

        try {
            scanner = new Scanner(new FileReader(file));
        } catch (FileNotFoundException ex) {
            System.out.println("File not found.");
            System.exit(1);
        }

        HashSet<Transaction> transactionSet = new HashSet<>();
        HashMap<Long, Item> items = new HashMap<>();

        while (scanner.hasNext()) {
            Scanner tokenizer = new Scanner(scanner.nextLine());
            tokenizer.useDelimiter(";|\\n");

            long transactionID = Long.parseLong(tokenizer.next());
            Transaction transaction = new Transaction(transactionID);

            while (tokenizer.hasNext()) {
                long itemID = Long.parseLong(tokenizer.next().replaceAll("\"",""));

                Item item;
                if (items.containsKey(itemID)) {
                    item = items.get(itemID);
                } else {
                    item = new Item(itemID);
                    items.put(itemID, item);
                }

                transaction.addItem(item);
            }
            transactionSet.add(transaction);
        }
        scanner.close();

        System.out.println("Transactions: " + transactionSet.size());
        System.out.println("Items: " + items.size());
        items = null;

        System.out.println(transactionSet);

        long initialTime = 0, finalTime = 0;
        List<AssociationRule> associationRules = null;

        System.out.println("Select the algorithm that will be used: ");
        System.out.println("1 - Apriori Algorithm");
        System.out.println("2 - PrefRuleSky");
        System.out.println("3 - PrefRuleTopK");
        scanner = new Scanner(System.in);
        for (int i = 0; i < 1; i++) {
            System.out.println("For do " + i);
            switch (2) {
                case 1:
                    initialTime = System.currentTimeMillis();
                    associationRules = AprioriAlgorithm.run(transactionSet, 0.002, 0.01);
                    finalTime = System.currentTimeMillis();
                    break;
                case 2:
                    initialTime = System.currentTimeMillis();
                    associationRules = PrefRuleSky.run(transactionSet);
                    finalTime = System.currentTimeMillis();
                    break;

                case 3:
                    System.out.println("K: ");
                    int k = 10;//scanner.nextInt();
                    System.out.println("Alpha: ");
                    double alpha = 0.1;//scanner.nextDouble();
                    initialTime = System.currentTimeMillis();
                    associationRules = PrefRuleTopK.run(transactionSet, k, alpha);
                    finalTime = System.currentTimeMillis();
                    break;
            }

            long time = finalTime - initialTime;

            System.out.println("Execution time: " + time + "ms");
            System.out.println("Number of generated associations rules: " + associationRules.size());
            for (AssociationRule al : associationRules) {
                System.out.println(al);
            }
        }

    }

}
