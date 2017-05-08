package ch3problems;

import java.util.concurrent.Semaphore;

/**
 * Created by joshuasmith on 5/8/17.
 */
public class StarveFreeReaderWriter {
    int numReaders = 0;
    Semaphore mutex = new Semaphore(1);
    Semaphore wLock = new Semaphore(1);

    // To prevent starvation:
    boolean writeWaiting = false;
    Semaphore rLock = new Semaphore(1);

    public void startRead() throws InterruptedException {
        mutex.acquire();
        if (writeWaiting) rLock.acquire();
        numReaders++;
        if (numReaders == 1) wLock.acquire();
        mutex.release();
    }

    public void endRead() throws InterruptedException {
        mutex.acquire();
        numReaders--;
        if (numReaders == 0) wLock.release();
        mutex.release();
    }

    public void startWrite() throws InterruptedException {
        if (numReaders > 0) writeWaiting = true;
        rLock.acquire();
        wLock.acquire();
    }

    public void endWrite() {
        writeWaiting = false;
        rLock.release();
        wLock.release();
    }
}
