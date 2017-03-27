package tcpExamples;

import java.util.LinkedList;

/**
 * Created by joshuasmith on 3/27/17.
 */
public class Message {

    public int src;
    public int dest;
    public String tag;

    private LinkedList<Object> messageBuf;

    public Message(int src, int dest, String tag, LinkedList<Object> messageBuf) {
        this.src = src;
        this.dest = dest;
        this.tag = tag;
        this.messageBuf = messageBuf;
    }

    public LinkedList<Object> getMessageBuf() { return this.messageBuf; }
    public int getMessageInt() { return (Integer) this.messageBuf.removeFirst(); }

    public String toString() {
        return String.valueOf(this.src) + " "
                + String.valueOf(this.dest) + " "
                + this.tag + " "
                + this.messageBuf.toString();
    }
}
