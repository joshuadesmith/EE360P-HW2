/**
 * Created by joshuasmith on 2/13/17.
 */
public class Writer implements Runnable {

    static FairReadWriteLock rwLock;

    public Writer(FairReadWriteLock rwLock) {
        this.rwLock = rwLock;
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
        System.out.println("A Thread is writing.");
    }
}
