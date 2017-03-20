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

    private static final int DATA_BUFFER_SIZE = 512;

    private Socket tcpSocket;
    private DataOutputStream outToServer;
    private DataInputStream inFromServer;
    private int tcpPortNum;

    private String hostAddr;

    public static void main (String[] args) {

        String hostAddress;
        int tcpPort;
        int protocol = 1; //1 = TCP

        Scanner sc = new Scanner(System.in);
        int numServer = sc.nextInt();

        for (int i = 0; i < numServer; i++) {
            // TODO: parse inputs to get the ips and ports of servers
        }

        hostAddress = args[0];
        tcpPort = Integer.parseInt(args[1]);

        Client client = new Client();
        client.tcpPortNum = tcpPort;
        client.hostAddr = hostAddress;

        while(sc.hasNextLine()) {
            String cmd = sc.nextLine();
            String[] tokens = cmd.split(" ");
            String response = null;

            if (tokens[0].equals("setmode")) {
                if (tokens[1].toLowerCase().equals("u")) {
                    protocol = 0;
                    System.out.println("Communication protocol set: UDP\n");
                } else if (tokens[1].toLowerCase().equals("t")) {
                    protocol = 1;
                    System.out.println("Communication protocol set: TCP\n");
                }
            }

            else if (tokens[0].equals("purchase")) {
                String command = tokens[0] + " " + tokens[1] + " " + tokens[2] + " " + tokens[3];
                response = client.issueCommand(command, protocol);
                System.out.println("Response received: \n" + response);
            }

            else if (tokens[0].equals("cancel")) {
                String command = tokens[0] + " " + tokens[1];
                response = client.issueCommand(command, protocol);
                System.out.println("Response received: \n" + response);
            }

            else if (tokens[0].equals("search")) {
                String command = tokens[0] + " " + tokens[1];
                response = client.issueCommand(command, protocol);
                System.out.println("Response received: \n" + response);
            }

            else if (tokens[0].equals("list")) {
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
            // TCP protocol
            if (protocol == 1) {
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
            tcpSocket = new Socket(hostAddr, tcpPortNum);
            outToServer = new DataOutputStream(tcpSocket.getOutputStream());
            inFromServer = new DataInputStream(tcpSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("IOException in Client.setUpTCPSocket: " + e);
        }
    }
}
