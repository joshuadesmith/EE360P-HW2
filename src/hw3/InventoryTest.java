package hw3;

import java.util.ArrayList;

/**
 * Created by joshuasmith on 2/21/17.
 */
public class InventoryTest {

    public static String file = "inventory.txt";

    /* EXPECTED RESULTS:
    Finished initializing inventory.
    ps4: 17
    phone: 20
    xbox: 8
    laptop: 15
    camera: 10

    Your order has been placed 1 josh camera 10
    Your order has been placed 2 josh ps4 5
    Not Available - Not enough items.

    Results for Josh:
    1, camera, 10
    2, ps4, 5

    Results for Ben:
    No order found for ben

    Order 2 has been cancelled.

    ps4 17
    phone 20
    xbox 8
    laptop 15
    camera 0
     */

    public static void main(String[] args) {
        Server server = new Server();
        Server.initializeInventory(file);
        Server.printInventory();

        String result1 = server.handleCommand("purchase josh camera 10");
        String result2 = server.handleCommand("purchase josh ps4 5");
        String result3 = server.handleCommand("purchase ben camera 1");

        System.out.println("");
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3 + "\n");

        String joshSearch = server.searchOrders("josh");
        String benSearch = server.searchOrders("ben");

        System.out.println("Results for Josh:");
        System.out.println(joshSearch);
        System.out.println("Results for Ben:");
        System.out.println(benSearch);


        String result4 = server.handleCommand("cancel 2");
        System.out.println(result4 + "\n");

        String inv = server.handleCommand("list");
        System.out.println(inv);
    }
}
