package distributed;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by joshuasmith on 2/20/17.
 * Basic datagram echo server
 */
public class DatagramServer {

    public static void main(String[] args) {
        DatagramPacket dataPacket, returnPacket;

        int port = 2018;
        int len = 1024;

        try {
            DatagramSocket dataSocket = new DatagramSocket(port);
            byte[] buffer = new byte[len];

            while(true) {
                dataPacket = new DatagramPacket(buffer, buffer.length);
                dataSocket.receive(dataPacket);

                returnPacket = new DatagramPacket(dataPacket.getData(), dataPacket.getLength(),
                                                    dataPacket.getAddress(), dataPacket.getPort());
                dataSocket.send(returnPacket);
            }
        } catch (SocketException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
