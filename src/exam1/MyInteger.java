package exam1;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by joshuasmith on 5/8/17.
 */
public class MyInteger {
    AtomicInteger integer;

    public MyInteger() {
        integer = new AtomicInteger();
    }

    public MyInteger(int val) {
        integer = new AtomicInteger(val);
    }

    /**
     * Atomically adds 1 to the current value and returns the updated value
     * @return updated integer value
     */
    public int incrAndGet() {
        int currentVal, newVal;
        while (true) {
            currentVal = integer.get();
            newVal = currentVal + 1;
            if (integer.compareAndSet(currentVal, newVal)) return newVal;
            Thread.yield();
        }
    }

    /**
     * Atomically sets the current value to one and tests whether the previous
     * value was nonzero
     * @return  True if previous value was non-zero
     *          False if previous value was zero
     */
    public boolean testAndSet() {
        boolean result;
        int currentVal;
        while (true) {
            currentVal = integer.get();
            result = (currentVal != 0);
            if (integer.compareAndSet(currentVal, 1)) return result;
            Thread.yield();
        }
    }
}
