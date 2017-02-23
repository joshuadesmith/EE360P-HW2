package hw3;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by joshuasmith on 2/20/17.
 */
public class Client {

    private static final int TEMP_TCP_PORT = 2018;
    private static final String TEMP_HOST_NAME = "localhost";

    private Socket tcpSocket;
    private DataOutputStream outToServer;
    private DataInputStream inFromServer;

    public static void main (String[] args) {

        String hostAddress = TEMP_HOST_NAME;
        int tcpPort = TEMP_TCP_PORT;
        int udpPort;
        int protocol = 1; //1 = TCP, 0 = UDP

        /*
        if (args.length != 3) {
            System.out.println("ERROR: Provide 3 arguments");
            System.out.println("\t(1) <hostAddress>: the address of the server");
            System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
            System.out.println("\t(3) <udpPort>: the port number for UDP connection");
            System.exit(-1);
        }

        hostAddress = args[0];
        tcpPort = Integer.parseInt(args[1]);
        udpPort = Integer.parseInt(args[2]);
        */

        Client client = new Client();

        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()) {
            String cmd = sc.nextLine();
            String[] tokens = cmd.split(" ");
            String response = null;

            if (tokens[0].equals("setmode")) {
                // Set the mode of communication for sending commands to the server
                // and display the name of the protocol that will be used in future
                if (tokens[1].toLowerCase().equals("u")) {
                    protocol = 0;
                    System.out.println("Communication protocol set: UDP");
                } else if (tokens[1].toLowerCase().equals("t")) {
                    protocol = 1;
                    System.out.println("Communication protocol set: TCP");
                }
            }

            else if (tokens[0].equals("purchase")) {
                // TODO: send appropriate command to the server and display the
                // appropriate responses form the server
                String command = tokens[0] + " " + tokens[1] + " " + tokens[2] + " " + tokens[3];
                response = client.issueCommand(command, protocol);
                System.out.println("Response received: \n" + response);
            }

            else if (tokens[0].equals("cancel")) {
                // TODO: send appropriate command to the server and display the
                // appropriate responses form the server
                String command = tokens[0] + " " + tokens[1];
                response = client.issueCommand(command, protocol);
                System.out.println("Response received: \n" + response);
            }

            else if (tokens[0].equals("search")) {
                // TODO: send appropriate command to the server and display the
                // appropriate responses form the server
                String command = tokens[0] + " " + tokens[1];
                response = client.issueCommand(command, protocol);
                System.out.println("Response received: \n" + response);
            }

            else if (tokens[0].equals("list")) {
                // TODO: send appropriate command to the server and display the
                // appropriate responses form the server
                response = client.issueCommand(tokens[0], protocol);
                System.out.println("Response received: \n" + response);
            }

            else {
                System.out.println("ERROR: No such command");
            }
        }
    }

    private String issueCommand(String command, int protocol) {
        String response = null;

        try {

            // UDP protocol
            if (protocol == 0) {
                // UDP CODE HERE
            }

            // TCP protocol
            else if (protocol == 1) {
                setUpTCPSocket();
                outToServer.writeUTF(command);
                System.out.println("Issued Command: " + command);
                outToServer.flush();

                response = inFromServer.readUTF();
                tcpSocket.close();
            }

        } catch (IOException e) {
            System.err.println("IOException in Client.issueCommand: " + e);
        }

        return response;
    }

    private void setUpTCPSocket() {
        try {
            tcpSocket = new Socket(TEMP_HOST_NAME, TEMP_TCP_PORT);
            outToServer = new DataOutputStream(tcpSocket.getOutputStream());
            inFromServer = new DataInputStream(tcpSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("IOException in Client.setUpTCPSocket: " + e);
        }
    }
}
