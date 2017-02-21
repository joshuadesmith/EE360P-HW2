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

    private static Socket tcpSocket;

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

        try {
            tcpSocket = new Socket(TEMP_HOST_NAME, TEMP_TCP_PORT);
        } catch (IOException e) {
            System.err.println("IOException in Client.main");
        }


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
                response = issueCommand(command, protocol);
                System.out.println(response);
            } else if (tokens[0].equals("cancel")) {
                // TODO: send appropriate command to the server and display the
                // appropriate responses form the server
                response = issueCommand(cmd, protocol);
                System.out.println(response);
            } else if (tokens[0].equals("search")) {
                // TODO: send appropriate command to the server and display the
                // appropriate responses form the server
                response = issueCommand(cmd, protocol);
                System.out.println(response);
            } else if (tokens[0].equals("list")) {
                // TODO: send appropriate command to the server and display the
                // appropriate responses form the server
                response = issueCommand(cmd, protocol);
                System.out.println(response);
            } else {
                System.out.println("ERROR: No such command");
            }
        }
    }

    private static String issueCommand(String command, int protocol) {
        String response = null;

        try {
            if (protocol == 0) {
                // UDP CODE HERE
            } else if (protocol == 1) {
                System.out.println("Attempting to issue command");
                BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(tcpSocket.getOutputStream()));
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));

                outToServer.write(command);
                response = inFromServer.readLine();
                outToServer.flush();
                outToServer.close();
                inFromServer.close();
            }
        } catch (IOException e) {
            System.err.println("IOException in Client.issueCommand");
        }

        return response;
    }
}
