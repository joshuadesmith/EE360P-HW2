package udpplus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class UDPplus {
	/*
	 * This is the main area of UDP plus. It's where we instantiate all the objects, and it's the thing
	 * that the user calls. These are where the public methods should be
	 * */

    /* variables*/
    private DatagramPacket out;
    private DatagramSocket sock;
    private InetAddress ia;
    private int port;
    private int count;
    private int numStored;
    private int packetLength;
    private ArrayList<byte[]> localPacketsSent = new ArrayList<byte[]>();
    private ArrayList<byte[]> localPacketsRecieved = new ArrayList<byte[]>();
    private final String ENDHEADER = "#";
    private final int defaultPacketLength = 2048;

    /* functions*/
    public UDPplus(InetAddress ia, int port, int numStored, int packetLength)
    {
        this.ia = ia;
        this.port = port;
        this.numStored = numStored;
        this.packetLength = packetLength;
    }
    /*
     * TODO: whenever I add these 2 constructions I get a medly of errors that don't make sense
     * I would like to add these two constructors without crashing everything
     * */
	/*
	public UDPplus(InetAddress ia, int port, int numStored)
	{
		this.ia = ia;
		this.port = port;
		this.numStored = numStored;
		packetLength = defaultPacketLength;
	}
	public UDPplus(InetAddress ia, int port, int packetLength)
	{
		this.ia = ia;
		this.port = port;
		this.packetLength = packetLength;
		numStored = 10;
	}
	*/
    public boolean send(String data)
    {
        String header = String.valueOf(count) + ENDHEADER;
        data = header + data;
        byte[] localBytes = new byte[data.length()];
        localBytes = data.getBytes();
        out = new DatagramPacket(localBytes, localBytes.length,ia,port);
        try {
            sock.send(out);
        } catch (IOException e) {
            return false;
        }
        if(count == 0){localPacketsSent.clear();}
        localPacketsSent.set(count, localBytes);
        count = (count + 1)% numStored;

        return true;
    }
    public ArrayList<byte[]> recieve()
    {
        ArrayList<byte[]> ret = new ArrayList<byte[]>();
        byte[] recieveBuffer;
        if(packetLength == -1)
        {
            recieveBuffer = new byte[defaultPacketLength];
            out = new DatagramPacket(recieveBuffer, defaultPacketLength);
        }
        else
        {
            recieveBuffer = new byte[packetLength];
            out = new DatagramPacket(recieveBuffer, packetLength);
            StringTokenizer st = new StringTokenizer(new String(out.getData()), "#");
            int packetNumber = Integer.valueOf(st.nextToken());
            if( packetNumber == -1)
            {
                resendPackets(st.nextToken());
            }
            else
            {
                boolean sendToUser = false;
                if(packetNumber == numStored)
                {
                    while(!sendToUser)
                    {
                        sendToUser = requestResend();
                    }
                }

            }
        }
        for(int i = 0; i <localPacketsRecieved.size(); i++)
        {
            ret.add(localPacketsRecieved.get(i));
        }

        return ret;
    }
    private boolean requestResend() {
        boolean ret = true;
        String message = "-1#";
        for(int i = 0; i <localPacketsRecieved.size();i++ )
        {
            if(localPacketsRecieved.get(i) == null)
            {
                ret = false;
                message += i + "#";
            }
        }
        byte[] dataBytes = message.getBytes();
        if(!message.equals("-1#")){
            DatagramPacket toSend = new DatagramPacket(dataBytes,dataBytes.length,ia,port);
            try {
                sock.send(toSend);
            } catch (IOException e) {
                return false;
            }
        }

        return ret;
    }
    private void resendPackets(String packetNum) {
        int pNum = Integer.valueOf(packetNum);
        @SuppressWarnings("unused")
        byte[] data = localPacketsSent.get(pNum);
        DatagramPacket toSend = new DatagramPacket(localPacketsSent.get(pNum),localPacketsSent.get(pNum).length,
                ia,port);
        try {
            sock.send(toSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //TODO: ADD HELPER METHODS (SETS/GETS)
}
