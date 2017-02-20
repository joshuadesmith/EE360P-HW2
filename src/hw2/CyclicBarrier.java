package hw2;/*
 * EID's of group members:
 * TK8375
 * JDS5228
 */
import java.util.concurrent.Semaphore; // for implementation using Semaphores

public class CyclicBarrier {

    private int parties;
    private int count;

    Semaphore barrier = new Semaphore(0);
    Semaphore barrier2= new Semaphore(1);
    Semaphore mutex = new Semaphore(1);

    public CyclicBarrier(int parties) {
        this.parties = parties;
        this.count = 0;
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
        mutex.acquire();
        count++;
        int index = this.parties - this.count;
        if(count == parties){   //awaits for the last thread
            barrier.release();
            barrier2.acquire();
        }
        mutex.release();


        barrier.acquire();      //await for all threads to get here
        barrier.release();      //resume execution


        mutex.acquire();
        count--;
        if(count == 0){	        //awaits for the last thread
            barrier2.release();
            barrier.acquire();  //relock the barrier for the next set of threads
        }
        mutex.release();


        barrier2.acquire();
        barrier2.release();
        return index;
    }


    // Helper methods to clean things up
    // Didn't end up using these
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