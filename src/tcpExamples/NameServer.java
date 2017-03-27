package tcpExamples;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by joshuasmith on 2/20/17.
 */
public class NameServer {

    NameTable table = null;

    public NameServer() {
        table = new NameTable();
    }

    public static void main(String[] args) {
        NameServer server = new NameServer();
        System.out.println("Server started");

        try {
            ServerSocket listener = new ServerSocket(Symbols.ServerPort);
            System.out.println("Server port: " + listener.getLocalPort() + "\n");

            while (true) {
                Socket s = listener.accept();
                if (s != null) {
                    System.out.println("Got connection at port: " + s.getLocalPort());
                    Thread t = new ServerThread(server.table, s);
                    t.start();
                }
            }
        } catch (IOException e) {
            System.err.println("Server aborted: " + e);
        }
    }
}
