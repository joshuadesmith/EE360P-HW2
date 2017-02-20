package tcpExample;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by joshuasmith on 2/20/17.
 */
public class NameTable {

    class NameEntry {
        public String procName = null;
        public InetSocketAddress address = null;

        public NameEntry(String procName, String host, int port) {
            this.procName = procName;
            this.address = new InetSocketAddress(host, port);
        }
    }

    ArrayList<NameEntry> table = null;

    public NameTable() {
        this(100);
    }

    public NameTable(int tableSize) {
        table = new ArrayList<NameEntry>(tableSize);
    }

    // Returns the socket address of a process
    // Returns null if the process isnt in the table
    public synchronized InetSocketAddress search(String procName) {
        for (NameEntry entry : table) {
            if (procName.equals(entry.procName)) { return entry.address; }
        }

        return null;
    }

    public synchronized int insert(String procName, String hostName, int port) {
        int retVal = 1;

        Iterator<NameEntry> iter = table.iterator();
        while (iter.hasNext()) {
            NameEntry entry = iter.next();
            if (procName.equals(entry.procName)) {
                iter.remove();
                retVal = 0;
            }
        }

        table.add(new NameEntry(procName, hostName, port));
        notifyAll();
        return retVal;
    }

    public synchronized InetSocketAddress blockingFind(String procName) {
        InetSocketAddress retAddr = search(procName);

        try {
            while (retAddr == null) {
                this.wait();
                retAddr = search(procName);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return retAddr;
    }

    public synchronized void clear() {
        table.clear();
    }
}
