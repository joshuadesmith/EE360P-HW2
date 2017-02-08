/*
 * Joshua Smith - jds5228
 * Taewhan Ko -
 */
import java.util.concurrent.Semaphore; // for implementation using Semaphores

public class CyclicBarrier {

    private int parties;
    private int currentCount;

    private Semaphore isFull;
    private Semaphore incCap;

    public CyclicBarrier(int parties) {
        this.parties = parties;
        this.currentCount = parties;
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
        if (Thread.interrupted()) {
            reset();
            throw new InterruptedException();
        }

        int index = --currentCount;

        if (index > 0) {
            wait();
        } else {
            reset();
        }

        return index;
    }

    private void reset() {
        currentCount = parties;
        notifyAll();
    }

    private void acquire(Semaphore s) {
        try {
            s.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void release(Semaphore s) {
        s.release();
    }
}