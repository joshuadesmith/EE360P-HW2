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

    public CyclicBarrier(int parties) {
        this.parties = parties;
        this.count = parties;
        this.indexCheckLock = new Semaphore(1);
        this.waitLock = new Semaphore(1);
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
    public int await() throws InterruptedException {
        int index;

        acquire(indexCheckLock);

        if (count == parties) {         //First to arrive sets up a block
            acquire(waitLock);
        }

        index = --count;                //Update count and get index

        if (index > 0) {
            release(indexCheckLock);    //Allow other threads to update count
            acquire(waitLock);          //Block until all parties arrive
        }

        release(waitLock);              //First thread through releases block
        count = parties;                //Reset the count
        release(indexCheckLock);        //Allow more threads to enter barrier

        return index;
    }


    // Helper methods to clean things up
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