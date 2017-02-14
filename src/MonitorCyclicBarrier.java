/*
 * EID's of group members:
 * TK8375
 * JDS5228
 */

public class MonitorCyclicBarrier {

    private int parties;
    private int count;

    public MonitorCyclicBarrier(int parties) {
        this.parties = parties;
        this.count = parties;
    }

    public synchronized int await() throws InterruptedException {
        int index = --count;

        if (index > 0) {
            this.wait();        // Wait for all parties
        } else {
            count = parties;    // Reset count to wait for parties again
            notifyAll();        // Wake up waiting threads
        }

        return index;           // First to arrive returns parties-1; last returns 0
    }
}
