


public class MonitorCyclicBarrier {

    private int parties;
    private int currentCount;

    private Object monitor;

    public MonitorCyclicBarrier(int parties) {
        this.parties = parties;
        this.currentCount = parties;
        this.monitor = new Object();
    }

    public synchronized int await() throws InterruptedException {
        synchronized (monitor) {
            int index = --currentCount;
            if (index > 0) {
                while (index > 0) {
                    monitor.wait();
                }
            } else {
                reset();
            }
            return index;
        }
    }

    public void reset() {
        currentCount = parties;
        monitor.notifyAll();
    }
}
