package hw2;

/**
 * Created by joshuasmith on 2/13/17.
 */
public class Reader implements Runnable {
    static FairReadWriteLock rwLock;
    private int UUID;

    public Reader(FairReadWriteLock rwLock, int UUID) {
        this.rwLock = rwLock;
        this.UUID = UUID;
    }

    public void run() {
        try {
            rwLock.beginRead();
            read();
            rwLock.endRead();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void read() {
        System.out.println("hw2.Reader " + UUID + " is reading.");
    }

    public int getID() {
        return UUID;
    }
}
