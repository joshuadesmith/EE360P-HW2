


public class MonitorCyclicBarrier {

    private int parties;
    private int count;

    private final Object indexMonitor;

    public MonitorCyclicBarrier(int parties) {
        this.parties = parties;
        this.count = parties;
        this.indexMonitor = new Object();
    }

    public int await() throws InterruptedException {
        synchronized (indexMonitor) {

        }
        return 0;
    }
}
