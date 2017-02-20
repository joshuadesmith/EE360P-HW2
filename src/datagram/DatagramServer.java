package datagram;

import java.io.IOException;
import java.net.DatagramSocket;

/**
 * Created by joshuasmith on 2/20/17.
 * Basic datagram echo server
 */
public class DatagramServer {

    public static void main(String[] args) throws IOException {
        new DatagramServerThread().start();
    }
}
