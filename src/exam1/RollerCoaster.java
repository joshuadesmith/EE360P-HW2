package exam1;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by joshuasmith on 5/8/17.
 */
public class RollerCoaster {
    int carCap;
    int numPass;
    int numBoarded;
    boolean loading;
    boolean unloading;

    ReentrantLock lock = new ReentrantLock();
    Condition invokedLoad = lock.newCondition();
    Condition invokedUnload = lock.newCondition();
    Condition carEmpty = lock.newCondition();
    Condition carFull = lock.newCondition();

    public RollerCoaster(int carCap, int numPass) {
        this.carCap = carCap;
        this.numPass = numPass;
        this.numBoarded = 0;
        this.loading = false;
        this.unloading = false;
    }

    public void board() throws InterruptedException {
        lock.lock();
        try {
            while (!loading) invokedLoad.await();
            numBoarded++;
            carFull.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void unboard() throws InterruptedException {
        lock.lock();
        try {
            while (!unloading) invokedUnload.await();
            numBoarded--;
            carEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void load() throws InterruptedException {
        lock.lock();
        try {
            loading = true;
            invokedLoad.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void unload() throws InterruptedException {
        lock.lock();
        try {
            unloading = true;
            invokedUnload.signalAll();
            while (numBoarded > 0) carEmpty.await();
            unloading = false;
            carEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void runCar() throws InterruptedException {
        lock.lock();
        try {
            while (numBoarded < carCap) carFull.await();
            loading = false;
        } finally {
            lock.unlock();
        }
    }

}
