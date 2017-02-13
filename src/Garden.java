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
    private static int[] holeCount;

    private static int holesDug, holesSeeded, holesFilled;

    private final ReentrantLock updateLock = new ReentrantLock();
    private final ReentrantLock shovelLock = new ReentrantLock();
    private final ReentrantLock seedLock = new ReentrantLock();

    private final Condition notTooManyUnseeded = shovelLock.newCondition();
    private final Condition notTooManyUnfilled = shovelLock.newCondition();
    private final Condition atLeastOneSeeded = shovelLock.newCondition();
    private final Condition newtonHasShovel = shovelLock.newCondition();
    private final Condition maryHasShovel = shovelLock.newCondition();
    private static boolean shovelFree;

    private final Condition atLeastOneUnseeded = seedLock.newCondition();

    /**
     * Constructor
     */
    public Garden() {
        shovelFree = true;
        holeCount = new int[3];
        holesDug = holesSeeded = holesFilled = 0;
    }

    /**
     * Called when Newton wants to dig a hole.
     * Shovel is no longer free if he starts.
     */
    public void startDigging() {
        System.out.println("Newton wants to dig a hole.");
        shovelLock.lock();

        try {
            while (holeCount[UNSEEDED] >= 4) { notTooManyUnseeded.await(); }
            while (holeCount[UNSEEDED] + holeCount[SEEDED] >= 8) { notTooManyUnfilled.await(); }
            while (!shovelFree) { newtonHasShovel.await(); }

            shovelFree = false;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //updateLock.unlock();
        }
    }

    /**
     * Called when Newton is done digging a hole.
     * Number of UNSEEDED holes incremented.
     * Shovel is now free.
     * canSeed Condition is signalled.
     */
    public void doneDigging() {
        updateLock.lock();

        holeCount[UNSEEDED]++;
        holesDug++;
        shovelFree = true;
        System.out.println("Newton dug a hole");
        atLeastOneUnseeded.signal();
        maryHasShovel.signal();

        updateLock.unlock();
        shovelLock.unlock();
    }

    /**
     * Called when Benjamin wants to seed a hole.
     */
    public void startSeeding() {
        seedLock.lock();
        System.out.println("Benjamin wants to seed a hole.");
        try {
            while (holeCount[UNSEEDED] <= 0) { atLeastOneUnseeded.await(); }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * Called when Benjamin is done seeding the hole.
     * Number of UNSEEDED holes is decremented.
     * Number of SEEDED holes is incremented.
     * canFill Condition is signalled.
     */
    public void doneSeeding() {
        updateLock.lock();

        holeCount[UNSEEDED]--;
        holeCount[SEEDED]++;
        holesSeeded++;
        System.out.println("Benjamin seeded a hole.");
        atLeastOneSeeded.signalAll();

        updateLock.unlock();
        seedLock.unlock();
    }

    /**
     * Called when Mary starts filling a hole.
     * Shovel is no longer free.
     */
    public void startFilling() {
        System.out.println("Mary wants to fill a hole.");
        shovelLock.lock();
        try {
            while (holeCount[SEEDED] <= 0) { atLeastOneSeeded.await(); }
            while (!shovelFree) { maryHasShovel.await(); }

            shovelFree = false;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

    }

    /**
     * Called when Mary finishes filling a hole.
     * Number of SEEDED holes is decremented.
     * Number of FILLED holes is incremented.
     * Shovel is now free.
     * canDig and canSeed Conditions notified.
     */
    public void doneFilling() {
        updateLock.lock();

        holeCount[SEEDED]--;
        holeCount[FILLED]++;
        holesFilled++;
        shovelFree = true;
        System.out.println("Mary filled a hole.");
        notTooManyUnfilled.signalAll();
        newtonHasShovel.signalAll();

        updateLock.unlock();
        shovelLock.unlock();
    }

    /*
    * The following methods return the total number of holes dug, seeded or
    * filled by Newton, Benjamin or Mary at the time the methods' are
    * invoked on the garden class. */

    /**
     * Calculates number of holes that have been dug
     * Total holes dug = holes unseeded + holes seeded + holes filled
     * @return  Number of holes dug
     */
    public int totalHolesDugByNewton() {
        int total = holeCount[UNSEEDED] + holeCount[SEEDED] + holeCount[FILLED];
        return total;
    }

    /**
     * Total holes seeded = holes seeded + holes filled
     * @return  Number of holes seeded
     */
    public int totalHolesSeededByBenjamin() {
        int total = holeCount[SEEDED] + holeCount[FILLED];
        return total;
    }

    /**
     * @return  Number of holes filled
     */
    public int totalHolesFilledByMary() {
        int total = holeCount[FILLED];
        return total;
    }

    /**
     * Determines whether Newton can currently dig a hole.
     * Newton has to wait for Benjamin if there are 4 holes that are UNSEEDED.
     * He also has to wait for Mary if there are 8 unfilled holes.
     * Newton cannot dig a hole if the shovel is not free.
     * @return
     */
    private boolean canDig() {
        // Check number of unseeded holes
        if (holeCount[UNSEEDED] >= 4) {
            return false;
        }

        // Check number of unfilled holes
        if ((holeCount[UNSEEDED] + holeCount[SEEDED]) >= 8) {
            return false;
        }

        // If proper number of unfilled/unseed holes, check if shovel is free.
        return shovelFree;
    }

    /**
     * Determines whether Benjamin can currently seed a hole.
     * Benjamin cannot plant a seed unless at least one UNSEEDED hole exists.
     * @return
     */
    private boolean canSeed() {
        return (holeCount[UNSEEDED] >= 0);
    }

    /**
     * Determines whether Mary can currently fill a hole.
     * Mary cannot fill a hole unless at least one hole that has been SEEDED.
     * Mary cannot fill a hole if the shovel is not free.
     * @return
     */
    private boolean canFIll() {
        if (holeCount[SEEDED] <= 0) {
            return false;
        } else {
            return shovelFree;
        }
    }
}
