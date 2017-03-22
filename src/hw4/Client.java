package hw4;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

/**
 * Created by joshuasmith on 2/20/17.
 */
public class Client {

    private Socket tcpSocket;
    private DataOutputStream outToServer;
    private DataInputStream inFromServer;
    private int currentServ = 0;

    public static final String CLIENT_TAG = "cli";

    public static void main (String[] args) {

        Scanner sc = new Scanner(System.in);
        int numServer = sc.nextInt();

        InetSocketAddress[] servers = new InetSocketAddress[numServer];

        for (int i = 0; i < numServer; i++) {
            String ip_port[] = sc.nextLine().trim().split(":");
            servers[i] = new InetSocketAddress(ip_port[0], Integer.parseInt(ip_port[1]));
        }

        Client client = new Client();

        while(sc.hasNextLine()) {
            String cmd = sc.nextLine();
            String[] tokens = cmd.split(" ");
            String response = null;

            if (tokens[0].equals("purchase")) {
                String command = CLIENT_TAG + " " + tokens[0] + " " + tokens[1] + " " + tokens[2] + " " + tokens[3];
                response = client.issueCommand(command, servers, numServer);
                System.out.println("Response received: \n" + response);
            }

            else if (tokens[0].equals("cancel")) {
                String command = CLIENT_TAG + " " + tokens[0] + " " + tokens[1];
                response = client.issueCommand(command, servers, numServer);
                System.out.println("Response received: \n" + response);
            }

            else if (tokens[0].equals("search")) {
                String command = CLIENT_TAG + " " + tokens[0] + " " + tokens[1];
                response = client.issueCommand(command, servers, numServer);
                System.out.println("Response received: \n" + response);
            }

            else if (tokens[0].equals("list")) {
                String command = CLIENT_TAG + " " + tokens[0];
                response = client.issueCommand(command, servers, numServer);
                System.out.println("Response received: \n" + response);
            }

            else {
                System.out.println("ERROR: No such command");
            }
        }
    }

    private String issueCommand(String command, InetSocketAddress[] servers, int numServers) {
        String response = null;

        try {
            setUpTCPSocket(servers, numServers);
            outToServer.writeUTF(command);
            System.out.println("Issued Command: " + command);
            outToServer.flush();

            response = inFromServer.readUTF();
            tcpSocket.close();

        } catch (IOException e) {
            System.err.println("IOException in Client.issueCommand: " + e);
        }

        return response;
    }

    private void setUpTCPSocket(InetSocketAddress[] servers, int numServers) {

        if (currentServ >= numServers) {
            System.err.println("All servers are currently down.");
            return;
        }

        InetSocketAddress currentServer;
        for (int i = currentServ; i < numServers; i++) {
            tcpSocket = new Socket();
            currentServer = servers[i];
            try {
                /* handle TCP connection to server */
                tcpSocket.setSoTimeout(100);
                try {
                    tcpSocket.connect(currentServer);
                    outToServer = new DataOutputStream(tcpSocket.getOutputStream());
                    inFromServer = new DataInputStream(tcpSocket.getInputStream());
                    break;
                } catch (SocketTimeoutException e) {
                    currentServ++; //TODO: not sure if this works
                    e.printStackTrace();
                }

            } catch (IOException e) {
                System.err.println("IOException in Client.setUpTCPSocket finding server: " + e);
            }
        }
    }
}
