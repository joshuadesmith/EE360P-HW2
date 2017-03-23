package hw4;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by joshuasmith on 2/20/17.
 */
public class Server {

    // TODO: Implement timestamps
    // TODO: Implement lamport request
    // TODO: Implement lamport release
    // TODO: Use lamport's mutex to phase out synchronized methods

    private static HashMap<String, Integer> inventory;
    private static AtomicInteger orderCount = new AtomicInteger(1); // Order IDs count up from 1
    private static ArrayList<Order> orderHistory = new ArrayList<Order>();
    private static ServerSocket tcpSocket = null;
    private static ExecutorService threadPool = null;

    private static HashMap<Integer, InetSocketAddress> serverList;
    private static boolean[] serverStatus;
    private int numAcks = 0;
    private int ID;
    private int numServers;
    private boolean lastServer = false;

    public static final String TAG = "serv";
    public static final boolean FROM_CONIFIG_FILE = true;

    public Server() {}

    public Server(int numServers, int ID) {
        this.numServers = numServers;
        this.ID = ID;
    }

    public static void main (String[] args) {

        Server thisServer;
        serverList = new HashMap<Integer, InetSocketAddress>();

        if (FROM_CONIFIG_FILE) {
            System.out.println("Enter config file name:");
            Scanner sc = new Scanner(System.in);
            String fileName = sc.nextLine().trim();
            thisServer = new Server();

            initiliazeServerParams(fileName, thisServer);
        } else {
            Scanner sc = new Scanner(System.in);
            int myID = sc.nextInt();
            int numServer = sc.nextInt();
            String inventoryPath = sc.next();
            sc.nextLine();

            System.out.println("[DEBUG] my id: " + myID);
            System.out.println("[DEBUG] numServer: " + numServer);
            System.out.println("[DEBUG] inventory path: " + inventoryPath);

            for (int i = 0; i < numServer; i++) {
                String[] str = sc.nextLine().trim().split(":");
                System.out.println("Address for server " + i + ": " + str[0] + " (Port " + str[1] + ")");
                serverList.put(i + 1, new InetSocketAddress(str[0], Integer.parseInt(str[1])));
                serverStatus[i] = true;
            }

            // parse the inventory file
            initializeInventory(inventoryPath);

            // for debugging
            printInventory();

            thisServer = new Server(numServer, myID);
        }

        if (thisServer.numServers < 2) { thisServer.lastServer = true; }

        threadPool = Executors.newCachedThreadPool();

        try {
            ServerSocket serverSocket = new ServerSocket(serverList.get(thisServer.ID).getPort());

            Socket s = null;
            while ((s = serverSocket.accept()) != null) {
                threadPool.submit(new ServerCommandInterpreter(thisServer, s));
            }
            System.out.println("TCP Server Runnable ending.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Parses a command string and executes the command
     * @param command   Command string received from client
     * @return          String that contains the server's response
     */
    private String handleCommand(String command) {
        String[] tokens = command.trim().split(" ");

        String response = null;


        if (tokens[0].toLowerCase().equals("purchase")) {
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

        System.out.println("[DEBUG]: Executed command \"" + command + "\"\n");
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

    private static synchronized void initiliazeServerParams(String fileName, Server server) {
        System.out.println("Initializing server from config file " + fileName);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            String firstLine = reader.readLine();
            String[] firstLineTokens = firstLine.trim().split(" ");
            server.ID = Integer.parseInt(firstLineTokens[0]);
            server.numServers = Integer.parseInt(firstLineTokens[1]);

            for (int i = 0; i < server.numServers; i++) {
                String[] str = reader.readLine().trim().split(":");
                System.out.println("Address for server " + i + ": " + str[0] + " (Port " + str[1] + ")");
                serverList.put(i + 1, new InetSocketAddress(str[0], Integer.parseInt(str[1])));
            }

            initializeInventory(firstLineTokens[2]);
            printInventory();

        } catch (FileNotFoundException e) {
            System.err.println("File not found");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IOException while reading file");
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
    private Order generateOrder(String user, String product, int quantity) {
        return new Order(user, product, quantity);
    }

    /**
     * Processes an order placed by a client
     * @param order     Order information to be processed
     * @return          Result of order processing.
     */
    private String processPurchase(Order order) {

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
    private String cancelOrder(int id) {
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
    private String searchOrders(String user) {
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
    private String listInventory() {
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
    private ArrayList<Order> queryOrdersByUser(String user) {
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
    private Order getOrderByID(int id) {
        Order result = null;

        for (Order order : orderHistory) {
            if (order.getId() == id) {
                result = order;
            }
        }

        return result;
    }


    // NEW FOR HW4
    // TODO: implement logical clock

    /**
     * Sends a message to all running servers
     * Message sent has form: "serv <type> <id> <command>"
     * @param id        ID of server sending command
     * @param command   Has form: "<type> <command>"
     */
    public void notifyServers(String type, int id, int clock, String command) {
        String message = TAG + " " + type + " " + Integer.toString(id) + " " + Integer.toString(clock) + " " + command;
        Socket sock;
        DataOutputStream outToServer;

        for (int i = 1; i <= numServers; i++) {

            // Check to see whether this server node is the last one running
            lastServer = (serverList.size() < 2);

            if (i != this.ID && serverList.containsKey(i)) {
                System.out.println("[DEBUG]: Attempting to connect to Server " + i);
                InetSocketAddress addr = serverList.get(i);
                sock = new Socket();

                try {
                    sock.connect(addr, 100);
                    outToServer = new DataOutputStream(sock.getOutputStream());
                    outToServer.writeUTF(message);
                    outToServer.flush();
                    outToServer.close();
                } catch (IOException e) {
                    System.err.println("IOException in Server.notifyServers:");
                    System.err.println("Unable to connect to Server " + i + "\n");
                    serverList.remove(i);
                }
            }
        }

//        for (Map.Entry<Integer, InetSocketAddress> entry : serverList.entrySet()) {
//            if (serverList.size() < 2) {
//                System.out.println("[DEBUG]: Only one server node is running");
//                lastServer = true;
//                return;
//            }
//            if (entry.getKey() != this.ID) {
//                System.out.println("[DEBUG]: Attempting to connect to Server " + entry.getKey());
//                sock = new Socket();
//                try {
//                    sock.connect(entry.getValue(), 100);
//                    DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
//
//                    outToServer.writeUTF(message);
//                    outToServer.flush();
//
//                    System.out.println("[DEBUG]: Sent message \"" + message + "\" to Server " + entry.getKey());
//                } catch (SocketTimeoutException e) {
//                    System.err.println("Timed out while connecting to Server " + entry.getKey());
//                    serverList.remove(entry.getKey());
//                    sock = new Socket();
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    System.err.println("IOException while connecting to Server " + entry.getKey());
//                    serverList.remove(entry.getKey());
//                    sock = new Socket();
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    public synchronized void sendRequest(String command) {
        notifyServers("request", this.ID, 0, command);

        // Wait for acknowledgements if not last server node
        if (!lastServer) {
            numAcks = 0;
            System.out.println("[DEBUG]: CS Requested - numAcks = " + numAcks);
            try {
                while (numAcks < serverList.size() - 1) {
                    wait();
                    System.out.println("[DEBUG]: CS Requested - numAcks = " + numAcks);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("[DEBUG]: Server " + ID + " is the last running server - no need for handshake ");
        }
    }

    public synchronized String releaseAndGetResponse(String command) {
        notifyAll();
        notifyServers("release", this.ID, 0, command);
        return handleCommand(command);
    }

    public synchronized void processCommandFromServerNode(String command) {
        // First need to remove parameter tokens from command string
        String[] tokens = command.split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 3; i < tokens.length; i++) {
            builder.append(tokens[i]);
            builder.append(" ");
        }

        if (tokens[0].equals("request")) {
            notifyServers("acknowledge", this.ID, 0, builder.toString().trim());
        }

        else if (tokens[0].equals("acknowledge")) {
            numAcks++;
        }

        else if (tokens[0].equals("release")) {
            handleCommand(builder.toString().trim());
        }

        notifyAll();
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
