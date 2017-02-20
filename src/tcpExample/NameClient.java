package tcpExample;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by joshuasmith on 2/20/17.
 */
public class NameClient {

    Scanner din;
    PrintWriter pout;
    Socket serverSocket;

    public void getSocket() throws IOException {
        serverSocket = new Socket(Symbols.HostName, Symbols.ServerPort);
        din = new Scanner(serverSocket.getInputStream());
        pout = new PrintWriter(serverSocket.getOutputStream());
    }

    public int insertName(String procName, String hostName, int portNum) throws IOException {
        getSocket();
        pout.println("insert " + procName + " " + hostName + " " + portNum);
        System.out.println("Command Sent: insert " + procName + " " + hostName + " " + portNum);
        pout.flush();

        int retVal = din.nextInt();
        System.out.println("Received: " + retVal + "\n");
        serverSocket.close();
        return retVal;
    }

    public InetSocketAddress searchName(String procName, boolean isBlocking) throws IOException {
        getSocket();
        if (isBlocking) {
            pout.println("blockingFind " + procName);
            System.out.println("Command Sent: blockingFind " + procName);
        } else {
            pout.println("search " + procName);
            System.out.println("Command Sent: search " + procName);
        }

        pout.flush();

        String result = din.nextLine();
        System.out.println("Received: " + result);

        Scanner resultScanner = new Scanner(result);
        serverSocket.close();

        int portNum = resultScanner.nextInt();
        String hostName = resultScanner.next();
        if (portNum == 0) {
            return null;
        } else {
            return new InetSocketAddress(hostName, portNum);
        }

    }

    public void clear() throws IOException {
        getSocket();
        pout.println("clear ");
        pout.flush();
        serverSocket.close();
    }

    public static void main(String[] args) {
        NameClient client = new NameClient();

        try {
            int val = client.insertName("hello1", Symbols.HostName, Symbols.ServerPort);
            InetSocketAddress pa = client.searchName("hello1", false);
            System.out.println("\n" + pa.getHostName() + ":" + pa.getPort());
        } catch (IOException e) {
            System.err.println("Server aborted: " + e);
        }
    }
}
