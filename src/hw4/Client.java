package hw4;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Created by joshuasmith on 2/20/17.
 */
public class Client {

    private Socket tcpSocket;
    private DataOutputStream outToServer;
    private DataInputStream inFromServer;
    private int currentServIndex = 0;
    private boolean connected = false;

    public static final String TAG = "cli";
    private static final boolean FROM_CONFIG_FILE = false;
    private static final boolean DEBUG = false;

    public static void main (String[] args) {

        InetSocketAddress[] servers;
        int numServer;
        Scanner sc = new Scanner(System.in);

        if (FROM_CONFIG_FILE) {
            System.out.println("Enter client configuration file:");
            String configFileName = sc.nextLine().trim();
            servers = initializeClientParams(configFileName);
            numServer = servers.length;
        } else {
            numServer = sc.nextInt();
            servers = new InetSocketAddress[numServer];

            sc.nextLine();
            for (int i = 0; i < numServer; i++) {
                String ip_port[] = sc.nextLine().trim().split(":");
                servers[i] = new InetSocketAddress(ip_port[0], Integer.parseInt(ip_port[1]));
            }
        }

        Client client = new Client();

        while(sc.hasNextLine()) {
            String cmd = sc.nextLine();
            String[] tokens = cmd.split(" ");
            String response = null;

            if (tokens[0].equals("purchase")) {
                String command = TAG + " " + tokens[0] + " " + tokens[1] + " " + tokens[2] + " " + tokens[3];
                response = client.issueCommand(command, servers, numServer);
                System.out.println(response);
            }

            else if (tokens[0].equals("cancel")) {
                String command = TAG + " " + tokens[0] + " " + tokens[1];
                response = client.issueCommand(command, servers, numServer);
                System.out.println(response);
            }

            else if (tokens[0].equals("search")) {
                String command = TAG + " " + tokens[0] + " " + tokens[1];
                response = client.issueCommand(command, servers, numServer);
                System.out.println(response);
            }

            else if (tokens[0].equals("list")) {
                String command = TAG + " " + tokens[0];
                response = client.issueCommand(command, servers, numServer);
                System.out.println(response);
            }

            else {
                System.out.println("ERROR: No such command");
            }
        }
    }

    private static InetSocketAddress[] initializeClientParams(String fileName) {
        InetSocketAddress[] result;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            int n = Integer.parseInt(reader.readLine().trim());

            if (DEBUG) System.out.println(n + " Servers::");

            result = new InetSocketAddress[n];

            for (int i = 0; i < n; i++) {
                String[] str = reader.readLine().trim().split(":");
                if (DEBUG) System.out.println("Server " + (i+1) + ": " + str[0] + "(Port " + str[1] + ")");
                result[i] = new InetSocketAddress(str[0], Integer.parseInt(str[1]));
            }

            System.out.println("");
            return result;
        } catch (FileNotFoundException e) {
            if (DEBUG) {
                System.err.println("File not found:");
                e.printStackTrace();
            }
        } catch (IOException e) {
            if (DEBUG) {
                System.err.println("IOException while reading file:");
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Sends a command to the first running server
     * @param command       Command in form "cli <command>"
     * @param servers       List of all server addresses
     * @param numServers    The number of servers
     * @return              Response from the server
     */
    private String issueCommand(String command, InetSocketAddress[] servers, int numServers) {
        String response = null;

        try {
            setUpTCPSocket(servers, numServers);
            if (connected) {

                outToServer.writeUTF(command);
                if (DEBUG) System.out.println("Issued Command: " + command);
                outToServer.flush();

                response = inFromServer.readUTF();
                tcpSocket.close();
            }

        } catch (IOException e) {
            if (DEBUG) {
                System.err.println("IOException in Client.issueCommand: ");
                System.err.println("Could not connect to Server " + currentServIndex);
            }
        }

        return response;
    }

    /**
     * Attempts to connect to the first running server node
     * @param servers       List of all server addresses
     * @param numServers    Number of total servers
     */
    private void setUpTCPSocket(InetSocketAddress[] servers, int numServers) {
        connected = false;

        if (currentServIndex >= numServers) {
            if (DEBUG) System.err.println("All servers are currently down.");
        } else {
            InetSocketAddress currentServer;
            for (int i = currentServIndex; i < numServers; i++) {
                if (DEBUG) System.out.println("[DEBUG]: Attempting to connect to server " + i);

                tcpSocket = new Socket();
                currentServer = servers[i];
                try {
                /* handle TCP connection to server */
                    tcpSocket.setSoTimeout(100);
                    tcpSocket.connect(currentServer);
                    outToServer = new DataOutputStream(tcpSocket.getOutputStream());
                    inFromServer = new DataInputStream(tcpSocket.getInputStream());
                    connected = true;
                    currentServIndex = i;
                    return;
                } catch (SocketTimeoutException e) {
                    if (DEBUG) {
                        System.err.println("SocketTimeoutException in Client.setUpTCPSocket finding server:");
                        System.err.println("Could not connect to Server " + i);
                    }
                    connected = false;
                } catch (IOException e) {
                    if (DEBUG) {
                        System.err.println("IOException in Client.setUpTCPSocket finding server:");
                        System.err.println("Could not connect to Server " + i);
                    }
                    connected = false;
                }
            }
        }
    }


}
