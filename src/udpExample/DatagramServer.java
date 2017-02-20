package udpExample;

import java.io.IOException;

/**
 * Created by joshuasmith on 2/20/17.
 * Basic udpExample echo server
 */
public class DatagramServer {

    public static void main(String[] args) throws IOException {
        new DatagramServerThread().start();
    }
}
