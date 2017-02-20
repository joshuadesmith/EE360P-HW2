package hw2;

/**
 * Created by joshuasmith on 2/13/17.
 */
public class Writer implements Runnable {

    static FairReadWriteLock rwLock;
    private int UUID;

    public Writer(FairReadWriteLock rwLock, int UUID) {
        this.rwLock = rwLock;
        this.UUID = UUID;
    }

    public void run() {
        try {
            rwLock.beginWrite();
            write();
            rwLock.endWrite();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void write() {
        System.out.println("hw2.Writer " + UUID + " is writing.");
    }

    public int getID() {
        return UUID;
    }
}
