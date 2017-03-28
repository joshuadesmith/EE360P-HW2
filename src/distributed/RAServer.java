package distributed;

import hw4.Clock;
import hw4.TimeStamp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by joshuasmith on 3/28/17.
 */
public class RAServer {
    public static final String TAG = "serv";

    private static final int NUM_SERVERS = 3;
    private static final String IP = "127.0.0.1";
    private static final int[] PORTS = {8025, 8030, 8035};

    private static ArrayList<InetSocketAddress> serverList;

    private int thisServerID;
    private int numOkays;
    private boolean wantCS;
    private Clock clock;
    private PriorityQueue<TimeStamp> pendingQ;
    private ServerSocket socket;

    private RAServer(int thisServerID) {
        this.thisServerID = thisServerID;
        this.numOkays = 0;
        this.wantCS = false;
        this.clock = new Clock();
        this.pendingQ = new PriorityQueue<TimeStamp>();
    }

    private void setSocket() throws IOException {
        this.socket = new ServerSocket(serverList.get(this.thisServerID).getPort());
    }

    public static void main(String[] args) {
        RAServer thisServer;

        serverList = new ArrayList<InetSocketAddress>();
        for (int i = 0; i < NUM_SERVERS; i++) {
            serverList.add(new InetSocketAddress(IP, PORTS[i]));
        }

        if (args.length < 1) {
            System.out.println("[ERROR]: Requires 1 argument - Server ID");
        }

        thisServer = new RAServer(Integer.parseInt(args[0]));
        ExecutorService threadPool = Executors.newCachedThreadPool();

        try {
            thisServer.setSocket();
            Socket clientSocket = null;
            while ((clientSocket = thisServer.socket.accept()) != null) {

            }
        } catch (IOException e) {
            System.out.println("[ERROR]: IOException while connecting to client");
        }
    }

    private synchronized void sendMsgToOtherServers(String type, String msg, int ts, int pid) {
        String msgToOthers = TAG + " " + type + " " + Integer.toString(ts) + " " + Integer.toString(pid);
        Socket s;
        DataOutputStream dout;

        for (int i = 0; i < NUM_SERVERS; i++) {
            if (i != thisServerID) {
                InetSocketAddress addr = serverList.get(i);
                s = new Socket();

                try {
                    s.connect(addr);
                    dout = new DataOutputStream(s.getOutputStream());
                    dout.writeUTF(msgToOthers);
                    dout.flush();
                    dout.close();
                } catch (IOException e) {
                    System.out.println("[ERROR]: IOException while sending msg to others");
                }
            }
        }
    }

    public synchronized void handleMsgFromOtherServer(String msg) {
        String[] tokens = msg.split(" ");
        String msgBody = msg.substring(msg.indexOf(" ") + 1);

        int otherTS = Integer.parseInt(tokens[tokens.length-2]);
        int otherID = Integer.parseInt(tokens[tokens.length-1]);

    }

    public synchronized void requestCS(String msg) {
        sendMsgToOtherServers("request", msg, clock.getClock(), thisServerID);
        numOkays = 0;
        wantCS = true;
        System.out.println("[DEBUG]: CS requested - numOkays = " + numOkays);

        try {
            while (numOkays < NUM_SERVERS - 1) {
                wait();
                System.out.println("[DEBUG]: CS requested - numOkays = " + numOkays);
            }
        } catch (InterruptedException e) {
            System.out.println("[ERROR]: InterruptedException while waiting for okays");
        }
    }

    public void releaseCS() {}
}
