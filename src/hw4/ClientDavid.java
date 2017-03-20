package hw4;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientDavid {

    private Socket tcpSocket;
    private DataOutputStream outToServer;
    private DataInputStream inFromServer;

    public static void main (String[] args) {
        Scanner sc = new Scanner(System.in);
        int numServer = sc.nextInt();
        InetSocketAddress[] servers = new InetSocketAddress[numServer];
        ClientDavid client = new ClientDavid();

        for (int i = 0; i < numServer; i++) {
            // TODO: parse inputs to get the ips and ports of servers
            String ip_port[] = sc.nextLine().trim().split(":");
            String ip = ip_port[0];
            int port = Integer.parseInt(ip_port[1]);
            servers[i] = new InetSocketAddress(ip, port);
        }

        while(sc.hasNextLine()) {
            String cmd = sc.nextLine();
            String[] tokens = cmd.split(" ");
            String response = null;

            if (tokens[0].equals("purchase")) {
                String command = tokens[0] + " " + tokens[1] + " " + tokens[2] + " " + tokens[3];
                response = client.issueCommand(command,servers,numServer);
                System.out.println("Response received: \n" + response);
            }

            else if (tokens[0].equals("cancel")) {
                String command = tokens[0] + " " + tokens[1];
                response = client.issueCommand(command,servers,numServer);
                System.out.println("Response received: \n" + response);
            }

            else if (tokens[0].equals("search")) {
                String command = tokens[0] + " " + tokens[1];
                response = client.issueCommand(command,servers,numServer);
                System.out.println("Response received: \n" + response);
            }

            else if (tokens[0].equals("list")) {
                response = client.issueCommand(tokens[0],servers,numServer);
                System.out.println("Response received: \n" + response);
            }
            else {
                System.out.println("ERROR: No such command");
            }
        }
    }

    private String issueCommand(String command,InetSocketAddress[] servers,int numServer) {
        String response = null;

        try {
            setUpTCPSocket(servers,numServer);
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

    private void setUpTCPSocket(InetSocketAddress[] servers,int numServer) {
        boolean server_found = false;
        InetSocketAddress currentServer;
        int index = 0;
        while(!server_found){
            tcpSocket = new Socket();
            currentServer = servers[index];
            try {
                /* handle TCP connection to server */
                tcpSocket.setSoTimeout(100);
                try {
                    tcpSocket.connect(currentServer);
                } catch (Exception e) {
                    // TODO: check how we handle running out of servers to check
                    index = (index + 1) % numServer;
                    continue;
                }
                outToServer = new DataOutputStream(tcpSocket.getOutputStream());
                inFromServer = new DataInputStream(tcpSocket.getInputStream());
                server_found = true;
            }catch(IOException e){
                System.err.println("IOException in Client.setUpTCPSocket finding server: " + e);
            }
        }
    }
}