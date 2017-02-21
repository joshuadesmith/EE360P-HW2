package hw3;

import java.util.ArrayList;

/**
 * Created by joshuasmith on 2/21/17.
 */
public class InventoryTest {

    public static String file = "inventory.txt";

    public static void main(String[] args) {
        Server server = new Server();
        server.initializeInventory(file);
        server.printInventory();

        ArrayList<String> joshSearch;
        ArrayList<String> benSearch;

        Server.Order testOrder1 = server.generateOrder("josh", "camera", 10);
        String result1 = server.processPurchase(testOrder1);
        Server.Order testOrder2 = server.generateOrder("josh", "ps4", 5);
        String result2 = server.processPurchase(testOrder2);
        Server.Order testOrder3 = server.generateOrder("ben", "camera", 1);
        String result3 = server.processPurchase(testOrder3);

        System.out.println("");
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3 + "\n");

        joshSearch = server.searchOrders("josh");
        benSearch = server.searchOrders("ben");

        System.out.println("Results for Josh:");
        for (String s : joshSearch) {
            System.out.println(s);
        }
        System.out.println("");

        System.out.println("Results for Ben:");
        for (String s : benSearch) {
            System.out.println(s);
        }
        System.out.println("");

        String result4 = server.cancelOrder(2);
        System.out.println(result4 + "\n");

        ArrayList<String> inv = server.listInventory();
        for (String s : inv) {
            System.out.println(s);
        }
    }
}
