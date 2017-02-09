/**
 * Created by joshuasmith on 2/8/17.
 */
public class FairReadWriteLock {

    private int numReaders = 0;     // Tracks number of threads reading
    private int numWaitingToWrite = 0;     // Tracks number of threads waiting to write

    private boolean writeInProgress = false;

    public synchronized void beginRead() throws InterruptedException {
        while (writeInProgress || numWaitingToWrite > 0) {
            wait();
        }
        numReaders++;
    }

    public synchronized void endRead() {
        numReaders--;
        if (numReaders == 0) {
            notifyAll();
        }
    }

    public synchronized void beginWrite() throws InterruptedException {
        numWaitingToWrite++;
        while (numReaders > 0 || writeInProgress) {
            wait();
        }

        numWaitingToWrite--;
        writeInProgress = true;
    }

    public synchronized void endWrite() {
        writeInProgress = false;
        notifyAll();
    }

}
