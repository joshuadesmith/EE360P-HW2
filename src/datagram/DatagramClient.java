package datagram;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

/**
 * Created by joshuasmith on 2/20/17.
 */
public class DatagramClient {

    public static void main(String[] args) {
        String hostName;
        int port = 2018;
        int len = 1024;

        byte[] rBuffer = new byte[len];
        DatagramPacket sendPacket, receivePacket;

        if (args.length > 0) {
            hostName = args[0];
        } else {
            hostName = "localhost";
        }

        try {
            InetAddress iAddr = InetAddress.getByName(hostName);
            DatagramSocket socket = new DatagramSocket();
            Scanner scanner = new Scanner(System.in);

            while (true) {
                String echo = scanner.nextLine();

                if (echo.equals("done")) { break; }

                byte[] buf = new byte[echo.length()];
                buf = echo.getBytes();

                // Create a packet with contents of buf and send it to server
                sendPacket = new DatagramPacket(buf, buf.length, iAddr, port);
                socket.send(sendPacket);

                // Receive a packet from the server
                receivePacket = new DatagramPacket(rBuffer, rBuffer.length);
                socket.receive(receivePacket);

                String retStr = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received from server: " + retStr);
            }

            socket.close();

        } catch (UnknownHostException e) {
            System.err.println(e);
        } catch (SocketException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

}
