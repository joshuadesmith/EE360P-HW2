package datagram;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Created by joshuasmith on 2/20/17.
 */
public class DatagramServerThread extends Thread {

    public final int DEFAULT_PORT = 2018;
    public String FILE_PATH = "text.txt";

    protected DatagramSocket socket = null;
    protected BufferedReader reader = null;
    protected boolean keepRunning = true;
    private int counter = 5;

    public DatagramServerThread() throws IOException {
        this(DatagramServerThread.class.getSimpleName());
    }

    public DatagramServerThread(String name) throws IOException {
        super(name);

        this.socket = new DatagramSocket(DEFAULT_PORT);

        try {
            reader = new BufferedReader(new FileReader(FILE_PATH));
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found. Server will return current data instead.");
        }
    }

    public void run() {

        while (keepRunning && counter > 0) {
            try {
                byte[] buf = new byte[256];

                // receive request
                DatagramPacket rPacket = new DatagramPacket(buf, buf.length);
                socket.receive(rPacket);

                // determine response
                String dString = null;
                if (reader == null) {
                    dString = new Date().toString();
                    counter++;
                } else {
                    dString = getNextFileLine();
                }
                buf = dString.getBytes();

                // send response to client
                DatagramPacket sPacket = new DatagramPacket(buf, buf.length, rPacket.getAddress(), rPacket.getPort());
                socket.send(sPacket);

            } catch (IOException e) {
                e.printStackTrace();
                keepRunning = false;
            }
        }

        socket.close();
    }

    private String getNextFileLine() {
        String ret = null;

        try {
            ret = reader.readLine();
            if (ret == null) {
                reader.close();
                keepRunning = false;
                ret = "End of input file. Exiting.";
            }
        } catch (IOException e) {
            ret = "IOException occured in server.";
        }

        return ret;
    }
}
