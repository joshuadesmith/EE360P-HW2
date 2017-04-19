package udpplus;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by joshuasmith on 4/18/17.
 */
public class ReceiverTest {

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
            ArrayList<byte[]> rec = null;
            while (true) {
                rec = udpp.recieve();
            }
        } catch (UnknownHostException e) {
            System.out.println("UknownHostException in main: " + e);
        }
    }
}
