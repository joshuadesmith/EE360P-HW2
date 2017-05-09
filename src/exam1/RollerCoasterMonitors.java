package exam1;

/**
 * Created by joshuasmith on 5/8/17.
 */
public class RollerCoasterMonitors {
    int num = 0;                // number of passengers in car
    final int LOADING = 0;
    final int RUNNING = 1;
    final int UNLOADING = 2;
    final int C = 5;            // capacity of the car
    int state = LOADING;

    public synchronized void board() throws InterruptedException {
        while ((state != LOADING) || (num >= C)) wait();
        num++;
        notifyAll();
    }

    public synchronized void unboard() throws InterruptedException {
        while (state != UNLOADING) wait();
        num--;
        notifyAll();
    }

    public synchronized void load() throws InterruptedException {
        state = LOADING;
        notifyAll();
    }

    public synchronized void runCar() throws InterruptedException {
        while (num < C) wait();
        state = RUNNING;
    }

    public synchronized void unload() throws InterruptedException {
        state = UNLOADING;
        notifyAll();
        while (num > 0) wait();
    }
}
