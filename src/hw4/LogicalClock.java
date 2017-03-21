package hw4;

/**
 * Created by joshuasmith on 3/20/17.
 */
public class LogicalClock {
    int val;

    public LogicalClock() {
        val = 1;
    }

    public synchronized int getVal() {
        return val;
    }

    public void internalTick() {
        val += 1;
    }

    public synchronized int sendTick() {
        this.internalTick();
        return val;
    }

    public synchronized void receiveTick(int rec) {
        val = Math.max(val, rec);
    }
}
