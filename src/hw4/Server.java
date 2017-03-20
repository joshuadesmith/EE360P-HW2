package hw4;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by joshuasmith on 2/20/17.
 */
public class Server {

    private static final int TEMP_TCP_PORT = 2018;
    private static final int TEMP_UDP_PORT = 4036;
    private static final int DATA_BUFFER_SIZE = 512;
    private static final String TEMP_HOST_NAME = "localhost";
    private static final String TEMP_FILE_NAME = "inventory.txt";

    private static HashMap<String, Integer> inventory;
    private static AtomicInteger orderCount = new AtomicInteger(1); // Order IDs count up from 1
    private static ArrayList<Order> orderHistory = new ArrayList<Order>();

    private static ServerSocket tcpSocket = null;
    private static DatagramSocket udpSocket = null;
    private static ExecutorService threadPool = null;

    private int tcpPortNum;
    private int udpPortNum;

    public static void main (String[] args) {

        Scanner sc = new Scanner(System.in);
        int myID = sc.nextInt();
        int numServer = sc.nextInt();
        String inventoryPath = sc.next();
        sc.nextLine();

        System.out.println("[DEBUG] my id: " + myID);
        System.out.println("[DEBUG] numServer: " + numServer);
        System.out.println("[DEBUG] inventory path: " + inventoryPath);

        for (int i = 0; i < numServer; i++) {
            // TODO: parse inputs to get the ips and ports of servers
            String str = sc.next();
            System.out.println("address for server " + i + ": " + str);
        }

        int tcpPort;

        tcpPort = Integer.parseInt(args[0]);
        String fileName = args[2];
        
        Server server = new Server();
        server.tcpPortNum = tcpPort;

        // parse the inventory file
        initializeInventory(TEMP_FILE_NAME);
        
        // for debugging
        printInventory();

        try {
            // For TCP
            //tcpPort = TEMP_TCP_PORT;
            InetSocketAddress socketAddress = new InetSocketAddress(TEMP_HOST_NAME, tcpPort);
            tcpSocket = new ServerSocket(tcpPort);


            // Thread handling
            threadPool = Executors.newCachedThreadPool();
            threadPool.submit(server.new TCPServerRunnable());
        } catch (IOException e) {
            System.err.println("IOException in Server.main");
            e.printStackTrace();
        }
    }

    /**
     * Parses a command string and executes the command
     * @param command   Command string received from client
     * @return          String that contains the server's response
     */
    synchronized String handleCommand(String command) {
        String[] tokens = command.trim().split(" ");

        String response = null;

        if (tokens[0].toLowerCase().equals("setmode")) {

        }

        else if (tokens[0].toLowerCase().equals("purchase")) {
            response = processPurchase(new Order(tokens[1], tokens[2], Integer.parseInt(tokens[3])));
        }

        else if (tokens[0].toLowerCase().equals("cancel")) {
            response = cancelOrder(Integer.parseInt(tokens[1]));
        }

        else if (tokens[0].toLowerCase().equals("search")) {
            response = searchOrders(tokens[1]);
        }

        else if (tokens[0].toLowerCase().equals("list")) {
            response = listInventory();
        }

        else {
            response = "Invalid Command: " + command;
        }

        return response;
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
            // For debugging
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
     * Prints the current inventory contents to the Server's console
     * FOR DEBUGGING
     */
    private static synchronized void printInventory() {
        System.out.println("Current Inventory:");
        for (Map.Entry<String, Integer> invEntry : inventory.entrySet()) {
            System.out.println(invEntry.getKey() + ": " + invEntry.getValue());
        }
        System.out.println("");
    }

    /**
     * Creates an Order object from the input strings
     * Assigns the order an ID
     * @param user      Name of the user the order was made by
     * @param product   Name of the product that the user ordered
     * @param quantity  Number of units of the product
     * @return          The resulting Order object
     */
    private synchronized Order generateOrder(String user, String product, int quantity) {
        return new Order(user, product, quantity);
    }

    /**
     * Processes an order placed by a client
     * @param order     Order information to be processed
     * @return          Result of order processing.
     */
    private synchronized String processPurchase(Order order) {

        if (!inventory.containsKey(order.getProduct())) {
            return "Not Available - We do not sell this product.\n";
        }

        // Inventory contains the requested product
        int currentQuant = inventory.get(order.getProduct());
        if (currentQuant < order.getQuantity()) {
            return "Not Available - Not enough items.\n";
        }

        // Inventory has enough stock of requested product
        // Update inventory, order history, and order count
        inventory.put(order.getProduct(), currentQuant - order.getQuantity());
        orderHistory.add(order);
        orderCount.incrementAndGet();
        return "Your order has been placed " + order.toString() + "\n";
    }

    /**
     * Cancels an order by removing it from the list of past orders
     * and then updates current inventory
     * @param id        ID of order to be cancelled
     * @return          Response to be sent back to client
     */
    private synchronized String cancelOrder(int id) {
        Order order = getOrderByID(id);

        if (order == null) {
            return Integer.toString(id) + " not found, no such order\n";
        }

        // Order with id exists
        // Update inventory and order history
        int currentQuant = inventory.get(order.getProduct());
        currentQuant += order.getQuantity();
        inventory.put(order.getProduct(), currentQuant);
        orderHistory.remove(order);
        return "Order " + id + " has been cancelled.\n";
    }

    /**
     * Searches past orders for all orders made by a user
     * @param user      Username used to search orders
     * @return          Response to be sent back to client
     */
    private synchronized String searchOrders(String user) {
        StringBuilder builder = new StringBuilder();
        ArrayList<Order> userOrders = queryOrdersByUser(user);

        if (userOrders.size() == 0) {
            builder.append("No order found for " + user + "\n");
        } else {
            for (Order order : userOrders) {
                builder.append(order.toStringNameless());
                builder.append("\n");
            }
        }

        //builder.append("\n");
        return builder.toString();
    }

    /**
     * Lists the current inventory of the store
     * @return          A string containing all store inventory
     */
    private synchronized String listInventory() {
        ArrayList<String> invList = new ArrayList<String>(0);
        StringBuilder builder = new StringBuilder();
        String list = null;

        for (Map.Entry<String, Integer> invEntry : inventory.entrySet()) {
            builder.append(invEntry.getKey());
            builder.append(" ");
            builder.append(invEntry.getValue());
            builder.append("\n");
        }

        if (inventory.isEmpty()) {
            builder.append("The store is empty.\n");
        }

        //builder.append("\n");
        return builder.toString();
    }

    /**
     * Gets an a list of all the orders made by a specified user
     * @param user      Name of the user
     * @return          ArrayList of all the orders that the user made
     */
    private synchronized ArrayList<Order> queryOrdersByUser(String user) {
        ArrayList<Order> userOrders = new ArrayList<Order>(0);

        for (Order order : orderHistory) {
            if (order.getUser().equals(user)) {
                userOrders.add(order);
            }
        }

        return userOrders;
    }

    /**
     * Gets the order associated with a specific order ID
     * @param id        Order ID to search for
     * @return          Order object associated with ID
     *                  Null if no order found
     */
    private synchronized Order getOrderByID(int id) {
        Order result = null;

        for (Order order : orderHistory) {
            if (order.getId() == id) {
                result = order;
            }
        }

        return result;
    }

    /**
     * Runnable that constantly checks for TCP connections
     */
    private class TCPServerRunnable implements Runnable {
        @Override
        public void run() {
            try {
                Socket s = null;
                while ((s = tcpSocket.accept()) != null) {
                    threadPool.submit(new TCPCommandHandler(s));
                }
                System.out.println("TCP Server Runnable ending.");
            } catch (IOException e) {
                System.err.println("IOException in TCPServerRunnable.run");
            }
        }
    }

    /**
     * Handles a single command via TCP protocol
     */
    private class TCPCommandHandler implements Runnable {
        Socket clientSocket;

        TCPCommandHandler(Socket s) {
            this.clientSocket = s;
        }

        @Override
        public void run() {
            try {
                DataInputStream inFromClient = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
                String command = inFromClient.readUTF();
                System.out.println("Received command via TCP: " + command);

                String response = handleCommand(command);
                outToClient.writeUTF(response);
                outToClient.flush();

            } catch (IOException e) {
                System.err.println("IOException in TCPCommandHandler.run");
            }
        }
    }


    /**
     * Contains information for an order made
     */
    class Order {
        int id;
        String user;
        String product;
        int quantity;

        Order(String user, String product, int quantity) {
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
