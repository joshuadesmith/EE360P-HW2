package tcpExamples;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Created by joshuasmith on 3/27/17.
 */
public class ProcessConnector {

    private ServerSocket listener;
    private Socket[] link;
    private NameClient myNameClient;

    public ObjectInputStream[] dataIn;
    public ObjectOutputStream[] dataOut;

    public void connect(String baseName, int myID, LinkedList<Integer> neighbors) throws Exception {
        int numNeighbors = neighbors.size();
        int localPort = getLocalPort(myID);

        myNameClient = new NameClient();
        link = new Socket[numNeighbors];
        dataIn = new ObjectInputStream[numNeighbors];
        dataOut = new ObjectOutputStream[numNeighbors];
        listener = new ServerSocket(localPort);

        myNameClient.insertName(baseName + myID, InetAddress.getLocalHost().getHostName(), localPort);

        for (int pid : neighbors) {
            if (pid < myID) {
                Socket sock = listener.accept();
                ObjectInputStream din = new ObjectInputStream(sock.getInputStream());
                Integer otherID = (Integer) din.readObject();
                int index = neighbors.indexOf(otherID);
                String tag = (String) din.readObject();
                if (tag.equals("hello")) {
                    link[index] = sock;
                    dataIn[index] = din;
                    dataOut[index] = new ObjectOutputStream(sock.getOutputStream());
                }
            }
        }

        for (Integer pid : neighbors) {
            if (pid > myID) {
                InetSocketAddress addr = myNameClient.searchName(baseName + pid, true);
                int index = neighbors.indexOf(pid);

                link[index] = new Socket(addr.getHostName(), addr.getPort());
                dataOut[index] = new ObjectOutputStream(link[index].getOutputStream());

                dataOut[index].writeObject(myID);
                dataOut[index].writeObject("hello");
                dataOut[index].flush();

                dataIn[index] = new ObjectInputStream(link[index].getInputStream());
            }
        }

    }

    public void closeSockets() {
        try {
            listener.close();
            for (Socket sock : link) { sock.close(); }
            myNameClient.clear();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private int getLocalPort(int myID) {
        return (Symbols.ServerPort + 20 + myID);
    }
}
