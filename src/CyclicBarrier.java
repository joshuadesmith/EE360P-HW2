/*
 * Joshua Smith - jds5228
 * Taewhan Ko -
 */
import java.util.concurrent.Semaphore; // for implementation using Semaphores

public class CyclicBarrier {

    private int parties;
    private int count;

    private Semaphore indexCheckLock;
    private Semaphore waitLock;
    private Semaphore resetLock;


    public CyclicBarrier(int parties) {
        this.parties = parties;
        this.count = parties;
        this.indexCheckLock = new Semaphore(1);
        this.waitLock = new Semaphore(1);
        this.resetLock = new Semaphore(1);
    }

    /**
     * Waits until all parties have invoked await on this barrier.
     * If the current thread is not the last to arrive then it is
     * disabled for thread scheduling purposes and lies dormant until
     * the last thread arrives.
     * @return The arrival index of the current thread, where index
     * (parties - 1) indicates the first to arrive and 0 indicates
     * the last to arrive.
     * @throws InterruptedException
     */
    public synchronized int await() throws InterruptedException {
        // Solution without using Java Semaphores
        /*if (Thread.interrupted()) {
            reset();
            throw new InterruptedException();
        }

        int index = --currentCount;

        if (index > 0) {
            wait();
        } else {
            reset();
        }

        return index;*/

        // Solution with Java Semaphores
        int index = 0;

        acquire(indexCheckLock);


        return index;
    }

    private void reset() {
        acquire(indexCheckLock);
        count = parties;
        release(indexCheckLock);
        notifyAll();
    }

    private void acquire(Semaphore s) {
        try {
            s.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void acquire(Semaphore s, int num) {
        try {
            s.acquire(num);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void release(Semaphore s) {
        s.release();
    }

    private void release(Semaphore s, int num) {
        s.release(num);
    }
}