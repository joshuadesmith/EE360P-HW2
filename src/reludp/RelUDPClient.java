package reludp;

import java.io.IOException;
import java.net.*;

/**
 * Created by joshuasmith on 4/10/17.
 */
public class RelUDPClient {
    private String hostname = null;
    private int portNum;
    private int bufLen;
    private int timeout;
    private DatagramSocket socket;
    private InetAddress addr;

    public RelUDPClient() {
        this.hostname = "localhost";
        this.portNum = 8025;
        this.bufLen = 1024;
        this.timeout = 100;
    }

    public RelUDPClient(String hostname, int portNum, int bufLen) {
        this.hostname = hostname;
        this.portNum = portNum;
        this.bufLen = bufLen;
    }

    public void connect() {
        try {
            addr = InetAddress.getByName(hostname);
            socket = new DatagramSocket();
            socket.setSoTimeout(timeout);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void send(String msg) {
        boolean ack = false;
        DatagramPacket outPacket, inPacket;
        byte[] buf = msg.getBytes();
        byte[] rBuf = new byte[bufLen];
        outPacket = new DatagramPacket(buf, buf.length, addr, portNum);
        inPacket = new DatagramPacket(rBuf, rBuf.length);
        while (!ack) {
            try {
                socket.send(outPacket);
                socket.receive(inPacket);
                String inStr = new String(inPacket.getData(), 0, inPacket.getLength());
                if (inStr.equals("ACK")) {
                    ack = true;
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
