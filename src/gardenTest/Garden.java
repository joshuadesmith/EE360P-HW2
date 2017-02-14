package gardenTest;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by joshuasmith on 2/8/17.
 */
public class Garden {

    /**
     * UNSEEDED:    Hole has been dug but not seeded or filled
     * SEEDED:      Hole has been dug and seeded, but not filled
     * FILLED:      Hole has been dug, seeded, and filled
     */
    private static final int UNSEEDED = 0;
    private static final int SEEDED = 1;
    private static final int FILLED = 2;
    private static AtomicInteger[] holeCount;

    private static boolean shovelFree;

    // Lock that makes sure gardenTest.Newton and gardenTest.Mary can't have the shovel at the same time
    private static final ReentrantLock gardenLock = new ReentrantLock();

    // Conditions required for gardenTest.Newton to dig
    //private static final Condition lessThan4Unseeded = gardenLock.newCondition(); // combined these two into one
    //private static final Condition lessThan8Unfilled = gardenLock.newCondition();
    private static final Condition lessThanMaxEmpty = gardenLock.newCondition();
    private static final Condition shovelIsFree = gardenLock.newCondition();

    // Conditions required for gardenTest.Benjamin to seed
    private static final Condition atLeast1Unseeded = gardenLock.newCondition();

    // Conditions required for gardenTest.Mary to fill
    private static final Condition atLeast1Seeded = gardenLock.newCondition();


    /**
     * Constructor
     */
    public Garden() {
        shovelFree = true;
        holeCount = new AtomicInteger[3];

        for (int i = 0; i < holeCount.length; i++) {
            holeCount[i] = new AtomicInteger();
        }
    }

    /**
     * Called when gardenTest.Newton wants to dig a hole.
     * Shovel is no longer free if he starts.
     */
    public void startDigging() {
        gardenLock.lock();

        try {
            while ((holeCount[UNSEEDED].get() >= 4) || (holeCount[UNSEEDED].get() + holeCount[SEEDED].get() >= 8)) {
                lessThanMaxEmpty.await();
            }
            while (!shovelFree) { shovelIsFree.await(); }
        } catch (Exception e) {
            e.printStackTrace();
        }

        shovelFree = false;
        //System.out.println("gardenTest.Newton now has the shovel");
        //System.out.println("gardenTest.Newton started digging.");

        gardenLock.unlock();
    }

    /**
     * Called when gardenTest.Newton is done digging a hole.
     * Number of UNSEEDED holes incremented.
     * Shovel is now free.
     * canSeed Condition is signalled.
     */
    public void doneDigging() {
        gardenLock.lock();

        holeCount[UNSEEDED].incrementAndGet();
        atLeast1Unseeded.signal();
        shovelFree = true;
        shovelIsFree.signal();
        //System.out.println("gardenTest.Newton finished digging.");
        //System.out.println("gardenTest.Newton no longer has the shovel");

        gardenLock.unlock();
    }

    /**
     * Called when gardenTest.Benjamin wants to seed a hole.
     */
    public void startSeeding() {
        gardenLock.lock();

        try {
            while (holeCount[UNSEEDED].get() < 1) { atLeast1Unseeded.await(); }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("gardenTest.Benjamin is now seeding");
        gardenLock.unlock();
    }

    /**
     * Called when gardenTest.Benjamin is done seeding the hole.
     * Number of UNSEEDED holes is decremented.
     * Number of SEEDED holes is incremented.
     * canFill Condition is signalled.
     */
    public void doneSeeding() {
        gardenLock.lock();

        holeCount[UNSEEDED].decrementAndGet();
        holeCount[SEEDED].incrementAndGet();
        atLeast1Seeded.signal();
        lessThanMaxEmpty.signal();
        //System.out.println("gardenTest.Benjamin finished seeding.");

        gardenLock.unlock();
    }

    /**
     * Called when gardenTest.Mary starts filling a hole.
     * Shovel is no longer free.
     */
    public void startFilling() {
        gardenLock.lock();

        try {
            while (holeCount[SEEDED].get() < 1) { atLeast1Seeded.await(); }
            while (!shovelFree) { shovelIsFree.await(); }
        } catch (Exception e) {
            e.printStackTrace();
        }

        shovelFree = false;
        //System.out.println("gardenTest.Mary has the shovel.");
        //System.out.println("gardenTest.Mary started filling");
        gardenLock.unlock();
    }

    /**
     * Called when gardenTest.Mary finishes filling a hole.
     * Number of SEEDED holes is decremented.
     * Number of FILLED holes is incremented.
     * Shovel is now free.
     * canDig and canSeed Conditions notified.
     */
    public void doneFilling() {
        gardenLock.lock();
        holeCount[SEEDED].decrementAndGet();
        holeCount[FILLED].incrementAndGet();
        lessThanMaxEmpty.signal();
        shovelFree = true;
        shovelIsFree.signal();

        //System.out.println("gardenTest.Mary finished filling.");
        //System.out.println("gardenTest.Mary no longer has the shovel.");
        gardenLock.unlock();
    }

    /*
    * The following methods return the total number of holes dug, seeded or
    * filled by gardenTest.Newton, gardenTest.Benjamin or gardenTest.Mary at the time the methods' are
    * invoked on the garden class. */

    /**
     * Calculates number of holes that have been dug
     * Total holes dug = holes unseeded + holes seeded + holes filled
     * @return  Number of holes dug
     */
    public int totalHolesDugByNewton() {
        int total = holeCount[UNSEEDED].get() + holeCount[SEEDED].get() + holeCount[FILLED].get();
        return total;
    }

    /**
     * Total holes seeded = holes seeded + holes filled
     * @return  Number of holes seeded
     */
    public int totalHolesSeededByBenjamin() {
        int total = holeCount[SEEDED].get() + holeCount[FILLED].get();
        return total;
    }

    /**
     * @return  Number of holes filled
     */
    public int totalHolesFilledByMary() {
        int total = holeCount[FILLED].get();
        return total;
    }

    /**
     * Determines whether gardenTest.Newton can currently dig a hole.
     * gardenTest.Newton has to wait for gardenTest.Benjamin if there are 4 holes that are UNSEEDED.
     * He also has to wait for gardenTest.Mary if there are 8 unfilled holes.
     * gardenTest.Newton cannot dig a hole if the shovel is not free.
     * @return
     */
    private boolean canDig() {
        // Check number of unseeded holes
        if (holeCount[UNSEEDED].get() >= 4) {
            return false;
        }

        // Check number of unfilled holes
        if ((holeCount[UNSEEDED].get() + holeCount[SEEDED].get()) >= 8) {
            return false;
        }

        // If proper number of unfilled/unseed holes, check if shovel is free.
        return true;
    }

    /**
     * Determines whether gardenTest.Benjamin can currently seed a hole.
     * gardenTest.Benjamin cannot plant a seed unless at least one UNSEEDED hole exists.
     * @return
     */
    private boolean canSeed() {
        return (holeCount[UNSEEDED].get() >= 0);
    }

    /**
     * Determines whether gardenTest.Mary can currently fill a hole.
     * gardenTest.Mary cannot fill a hole unless at least one hole that has been SEEDED.
     * gardenTest.Mary cannot fill a hole if the shovel is not free.
     * @return
     */
    private boolean canFIll() {
        if (holeCount[SEEDED].get() <= 0) {
            return false;
        } else {
            return true;
        }
    }
}
