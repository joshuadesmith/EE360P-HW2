// EID1 = jds5228
// EID2 =

/**
 * Created by joshuasmith on 2/8/17.
 * An instance of this class should provide the following properties:
 *  (a) There is no read-write or write-write conflict.
 *  (b) A writer thread that invokes beginWrite() will be blocked until all preceding reader
 *      and writer threads have acquired and released the lock.
 *  (c) A reader thread that invokes beginRead() will be blocked until all preceding writer
 *      threads have acquired and released the lock.
 *  (d) A reader thread cannot be blocked if any preceding writer thread has acquired and released the lock.
 *  The precedence of threads is determined by the timestamp (sequence number) that threads obtain on arrival.
 */
public class FairReadWriteLock {

    private int numReaders = 0;                 // Tracks number of threads reading
    private int numWaitingToWrite = 0;          // Tracks number of threads waiting to write
    private boolean writeInProgress = false;    // Determines whether a thread is writing

    public synchronized void beginRead() throws InterruptedException {
        // Block if a thread is writing or there are threads waiting to write
        while (writeInProgress || numWaitingToWrite > 0) {
            wait();
        }

        // If no threads writing or waiting to write, inc reader count
        numReaders++;
    }

    public synchronized void endRead() {
        // One less thread is reading
        numReaders--;

        // If all threads done reading, notify other threads
        if (numReaders == 0) {
            notifyAll();
        }
    }

    public synchronized void beginWrite() throws InterruptedException {
        // Increment number of threads waiting to write in case thread gets blocked
        numWaitingToWrite++;

        // Block if any threads are reading or if a thread is writing
        while (numReaders > 0 || writeInProgress) {
            wait();
        }

        // A thread is now writing (decrement number of waiting threads)
        numWaitingToWrite--;
        writeInProgress = true;
    }

    public synchronized void endWrite() {
        // Thread is no longer writing
        writeInProgress = false;
        notifyAll();
    }

}
