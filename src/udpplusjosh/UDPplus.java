package udpplusjosh;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * Created by joshuasmith on 4/18/17.
 */
public class UDPplus {
    /* VARIABLES */
    private int port;
    private int count;
    private int packLen;
    private int numRec;
    private int sendCount;
    private InetAddress addr;
    private String hostname;
    private ArrayList<byte[]> recPackList = new ArrayList<byte[]>();
    private ArrayList<byte[]> senPackList = new ArrayList<byte[]>();

    private static final String END_HEADER = "#";
    private static final int DEFAULT_PACK_LEN = 2048;
    private static final int MAX_PACK_COUNT = 256;

    public UDPplus(String hostname, int port, int packLen) {
        this.port = port;
        this.packLen = packLen;
        this.numRec = 0;
        this.count = 0;
        this.sendCount = 0;

        try {
            this.addr = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            System.err.println("Uknown host: " + "\"" + hostname + "\"");
            this.addr = null;
        }
    }

    /*
        Sends String data.
        Blocks until the string has been fully recieved by other end
    */
    public boolean blockingSend(String data) {
        byte[] rBuf = new byte[DEFAULT_PACK_LEN];
        byte[] sBuf = data.getBytes();
        DatagramSocket socket;
        DatagramPacket sendPack;
        DatagramPacket recPack;
        try {
            socket = new DatagramSocket();
            sendPack = new DatagramPacket(sBuf, sBuf.length, addr, port);
            socket.send(sendPack);

            // get a response
            recPack = new DatagramPacket(rBuf, rBuf.length);
            String resp = new String(recPack.getData());
            System.out.println("Response: " + resp);
            return true;
        } catch (SocketException e) {
            System.err.println("SocketException in blockingSend: " + e);
            return false;
        } catch (IOException e) {
            System.err.println("IOException in blockingSend: " + e);
            return false;
        }
    }

    public boolean blockingReceive() {
        byte[] rBuf = new byte[DEFAULT_PACK_LEN];
        byte[] sBuf;
        DatagramSocket socket;
        DatagramPacket sendPack;
        DatagramPacket recPack;
        try {
            socket = new DatagramSocket();
            recPack = new DatagramPacket(rBuf, rBuf.length);
            socket.receive(recPack);
            System.out.println("Received: " + new String(recPack.getData()));

            String resp = String.valueOf(recPack.getData().length);
            sBuf = resp.getBytes();

            sendPack = new DatagramPacket(sBuf, sBuf.length, recPack.getAddress(), recPack.getPort());
            socket.send(sendPack);
            System.out.println("Response: " + resp);
            return true;
        } catch (SocketException e) {
            System.err.println("SocketException in blockingSend: " + e);
            return false;
        } catch (IOException e) {
            System.err.println("IOException in blockingSend: " + e);
            return false;
        }
    }

    /*
        Splits up a String message into multiple Strings to be sent
    */
    private ArrayList<String> splitData(String data) {
        return null;
    }
}
