package exam1;

import java.util.concurrent.CountDownLatch;

/**
 * Created by joshuasmith on 5/8/17.
 */
public class MyLatch {
    CountDownLatch latch;
    int numWaiting;

    public MyLatch(int initVal) {
        latch = new CountDownLatch(initVal);
        numWaiting = 0;
    }

    public void await() throws InterruptedException {
        synchronized (this) {
            numWaiting++;
        }
        latch.await();
        synchronized (this) {
            numWaiting--;
        }
    }

    public synchronized void countDown() {
        latch.countDown();
    }

    public synchronized int getNumWaiting() {
        return numWaiting;
    }
}
