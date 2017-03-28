package distributed;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by joshuasmith on 3/28/17.
 */
public class RAClient {

    public static final String TAG = "cli";

    private Socket tcpSocket;
    private DataOutputStream outToServer;
    private DataInputStream inFromServer;
    private ArrayList<InetSocketAddress> serverNodes;

    private static final int NUM_SERVERS = 3;
    private static final int SERVER_ID = 0;
    private static final String IP = "127.0.0.1";
    private static final int[] PORTS = {8025, 8030, 8035};

    private RAClient() {
        this.serverNodes = new ArrayList<InetSocketAddress>();
        for (int i = 0; i < NUM_SERVERS; i++) {
            this.serverNodes.add(new InetSocketAddress(IP, PORTS[i]));
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RAClient myClient = new RAClient();

        while (sc.hasNextLine()) {
            String str = sc.nextLine();
            String resp = myClient.sendMessageToServer(str);
            System.out.println("[DEBUG]: Received message \"" + resp + "\"");
        }
    }

    private String sendMessageToServer(String message) {
        String response = null;

        try {
            if (connectToServer()) {
                System.out.println("[DEBUG]: Connected");
                outToServer.writeUTF(message);
                outToServer.flush();
                System.out.println("[DEBUG]: Sent message \"" + message + "\"");

                response = inFromServer.readUTF();
                closeStreams();
            }
        } catch (IOException e) {
            System.out.println("[ERROR]: IOException while sending message to Server");
            System.out.println("\t(" + e + ")");
        }

        return response;
    }

    private boolean connectToServer() {
        System.out.println("[DEBUG]: Attempting to connect to Server Node " + SERVER_ID);

        tcpSocket = new Socket();
        try {
            tcpSocket.connect(serverNodes.get(SERVER_ID));
            outToServer = new DataOutputStream(tcpSocket.getOutputStream());
            inFromServer = new DataInputStream(tcpSocket.getInputStream());
            return true;
        } catch (IOException e) {
            System.out.println("[ERROR]: IOException while connecting to Server");
            System.out.println("\t(" + e + ")");
            return false;
        }
    }

    private void closeStreams() throws IOException {
        outToServer.close();
        inFromServer.close();
        tcpSocket.close();
    }
}
