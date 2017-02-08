/**
 * Created by joshuasmith on 2/8/17.
 */
public class FairReadWriteLock {

    private int numReaders = 0;
    private int numWaiting = 0;

    private boolean writeInProgress = false;
    private boolean readEnable = false;

    public synchronized void beginRead() throws InterruptedException {
        while (writeInProgress || (numWaiting > 0 && !readEnable)) {
            wait();
        }
        numReaders++;
    }

    public synchronized void endRead() {
        numReaders--;
        readEnable = false;
        if (numReaders == 0) {
            notifyAll();
        }
    }

    public synchronized void beginWrite() throws InterruptedException {
        numWaiting++;
        while (numReaders > 0 || writeInProgress) {
            wait();
        }

        numWaiting--;
        writeInProgress = true;
    }

    public synchronized void endWrite() {
        writeInProgress = false;
        readEnable = true;
        notifyAll();
    }

}
