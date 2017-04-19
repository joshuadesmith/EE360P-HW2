package udpplus;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by joshuasmith on 4/18/17.
 */
public class SenderTest {

    public static void main(String[] args) {
        int port = 8025;
        int packLen = 24;
        int numStored = 2;
        String hostname = "localhost";
        String echoStr = "example";

        InetAddress addr;
        UDPplus udpp;

        try {
            addr = InetAddress.getByName(hostname);
            udpp = new UDPplus(addr, port, numStored, packLen);
            while (!udpp.send(echoStr)) {}
            System.out.println("Message sent");
        } catch (UnknownHostException e) {
            System.err.println("UnKnownHostException in main: " + e);
        }
    }
}
