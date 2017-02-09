


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
            this.wait();
        } else {
            count = parties;
            notifyAll();
        }

        return index;
    }
}
