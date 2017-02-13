/**
 * Created by joshuasmith on 2/13/17.
 */
public class Reader {
    static FairReadWriteLock rwLock;

    public Reader(FairReadWriteLock rwLock) {
        this.rwLock = rwLock;
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
        System.out.println("A Thread is reading.");
    }
}
