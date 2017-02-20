package tcpExample;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by joshuasmith on 2/20/17.
 */
public class ServerThread extends Thread {
    NameTable table = null;
    Socket clientSocket = null;

    public ServerThread(NameTable table, Socket clientSocket) {
        this.table = table;
        this.clientSocket = clientSocket;
    }

    public void run() {
        Scanner scanner = null;

        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
            scanner = new Scanner(clientSocket.getInputStream());
            PrintWriter pout = new PrintWriter(clientSocket.getOutputStream());

            String command = inFromClient.readLine();
            System.out.println("Recieved command: " + command);
            Scanner tagScanner = new Scanner(command);
            String tag = tagScanner.next();

            if (tag.equals("search")) {
                InetSocketAddress addr = table.search(tagScanner.next());
                if (addr == null) {
                    pout.println(0 + " " + "nullhost");
                    System.out.println("Sent: " + 0 + " " + "nullhost");
                } else {
                    pout.println(addr.getPort() + " " + addr.getHostName());
                    System.out.println("Sent: " + addr.getPort() + " " + addr.getHostName());
                }
            }

            if (tag.equals("insert")) {
                String name = tagScanner.next();
                String hostName = tagScanner.next();
                int port = tagScanner.nextInt();
                int retVal = table.insert(name, hostName, port);
                pout.println(retVal);
                System.out.println("Sent: " + retVal + "\n");
            }

            if (tag.equals("blockingfind")) {
                InetSocketAddress addr = table.blockingFind(tagScanner.next());
                pout.println(addr.getPort() + " " + addr.getHostName());
                System.out.println("Sent: " + addr.getPort() + " " + addr.getHostName());
            }

            if (tag.equals("clear")) {
                table.clear();
                System.out.println("Cleared Name Table");
            }

            pout.flush();
            clientSocket.close();
            tagScanner.close();
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            scanner.close();
        }
    }
}
