package hw3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by joshuasmith on 2/20/17.
 */
public class Server {

    private static HashMap<String, Integer> inventory;
    private static AtomicInteger orderCount = new AtomicInteger(1); // Order IDs count up from 1
    private static ArrayList<Order> orderHistory = new ArrayList<Order>();

    private static int protocol = 0; // 0 = TCP, 1 = UDP

    public static void main (String[] args) {
        /*int tcpPort;
        int udpPort;
        if (args.length != 3) {
            System.out.println("ERROR: Provide 3 arguments");
            System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
            System.out.println("\t(2) <udpPort>: the port number for UDP connection");
            System.out.println("\t(3) <file>: the file of inventory");

            System.exit(-1);
        }
        tcpPort = Integer.parseInt(args[0]);
        udpPort = Integer.parseInt(args[1]);
        String fileName = args[2];*/

        String fileName = "inventory.txt";

        // parse the inventory file
        initializeInventory(fileName);
        printInventory();

        // TODO: handle request from clients
    }

    /**
     * Parses the contents of a text file to initialize the Store inventory
     * @param fileName  Name of the file to be parsed
     */
    private static synchronized void initializeInventory(String fileName) {
        inventory = new HashMap<String, Integer>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            boolean keepReading = true;
            while (keepReading) {
                String line = reader.readLine();
                if (line != null) {
                    Scanner parser = new Scanner(line);
                    String token = parser.next();
                    int num = parser.nextInt();
                    parser.close();
                    inventory.put(token, num);
                } else {
                    keepReading = false;
                }
            }
            System.out.println("Finished initializing inventory.");
            if (inventory.size() == 0) {
                System.out.println("Warning: Inventory is empty");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.err.println("File " + fileName + " not found.");
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the current inventory contents to the console
     */
    private static synchronized void printInventory() {
        for (Map.Entry<String, Integer> invEntry : inventory.entrySet()) {
            System.out.println(invEntry.getKey() + ": " + invEntry.getValue());
        }
    }

    /**
     * Sets the data transfer protocol of the server
     * @param s String sent by client; u = UDP, t = TCP
     */
    private static synchronized void setProtocol(String s) {
        if (s.toLowerCase().equals("u")) {
            protocol = 0;
        } else if (s.toLowerCase().equals("t")) {
            protocol = 1;
        }
    }

    /**
     * Processes an order placed by a client
     * @param order Order information to be processed
     * @return      Result of order processing.
     */
    private static synchronized String processPurchase(Order order) {

        if (!inventory.containsKey(order.getProduct())) {
            return "Not Available - We do not sell this product.";
        }

        // Inventory contains the requested product
        int currentQuant = inventory.get(order.getProduct());
        if (currentQuant < order.getQuantity()) {
            return "Not Available - Not enough items.";
        }

        // Inventory has enough stock of requested product
        // Update inventory, order history, and order count
        inventory.put(order.getProduct(), currentQuant - order.getQuantity());
        orderHistory.add(order);
        orderCount.incrementAndGet();
        return "Your order has been placed " + order.toString();
    }

    private static synchronized String cancelOrder(int id) {
        Order order = getOrderByID(id);

        if (order == null) {
            return Integer.toString(id) + " not found, no such order";
        }

        // Order with id exists
        // Update inventory and order history
        int currentQuant = inventory.get(order.getProduct());
        currentQuant += order.getQuantity();
        inventory.put(order.getProduct(), currentQuant);
        orderHistory.remove(order);
        return "Order " + id + " has been cancelled.";
    }

    private static synchronized ArrayList<String> searchOrders(String user) {
        ArrayList<String> searchResults = new ArrayList<String>(1);
        ArrayList<Order> userOrders = queryOrdersByUser(user);

        if (userOrders.size() == 0) {
            searchResults.add("No order found for " + user);
        } else {
            for (Order order : userOrders) {
                searchResults.add(order.toStringNameless());
            }
        }

        return searchResults;
    }

    private static synchronized ArrayList<String> listInventory() {
        ArrayList<String> invList = new ArrayList<String>(0);

        for (Map.Entry<String, Integer> invEntry : inventory.entrySet()) {
            invList.add(invEntry.getKey() + " " + Integer.toString(invEntry.getValue()));
        }

        if (invList.size() == 0) {
            invList.add("The store is empty.");
        }

        return invList;
    }

    private static synchronized ArrayList<Order> queryOrdersByUser(String user) {
        ArrayList<Order> userOrders = new ArrayList<Order>(0);

        for (Order order : orderHistory) {
            if (order.getUser().equals(user)) {
                userOrders.add(order);
            }
        }

        return userOrders;
    }

    private static synchronized Order getOrderByID(int id) {
        Order result = null;

        for (Order order : orderHistory) {
            if (order.getId() == id) {
                result = order;
            }
        }

        return result;
    }

    private class Order {
        int id;
        String user;
        String product;
        int quantity;

        protected Order(String user, String product, int quantity) {
            this.id = orderCount.get();
            this.user = user;
            this.product = product;
            this.quantity = quantity;
        }

        public int getId() {
            return this.id;
        }

        public String getUser() {
            return this.user;
        }

        public String getProduct() {
            return this.product;
        }

        public int getQuantity() {
            return this.quantity;
        }

        public String toString() {
            return (Integer.toString(this.id) + " "
                    + this.user + " "
                    + this.product + " "
                    + Integer.toString(this.quantity));
        }

        public String toStringNameless() {
            return (Integer.toString(this.id) + ", "
                    + this.product + ", "
                    + Integer.toString(this.quantity));
        }
    }
}
