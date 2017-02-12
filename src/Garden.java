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

    private static ReentrantLock actionLock;
    private static ReentrantLock countLock;
    private Condition canDig;
    private Condition canSeed;
    private Condition canFill;
    private static boolean shovelFree;

    /**
     * Constructor
     */
    public Garden() {
        shovelFree = true;
        holeCount = new int[3];
        actionLock = new ReentrantLock();
        countLock = new ReentrantLock();
        canDig = actionLock.newCondition();
        canSeed = actionLock.newCondition();
        canFill = actionLock.newCondition();
    }

    /**
     * Called when Newton wants to dig a hole.
     * Shovel is no longer free if he starts.
     */
    public void startDigging() {
        actionLock.lock();

        try {
            while (!canDig()) { canDig.await(); }
            shovelFree = false;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            actionLock.unlock();
        }
    }

    /**
     * Called when Newton is done digging a hole.
     * Number of UNSEEDED holes incremented.
     * Shovel is now free.
     * canSeed Condition is signalled.
     */
    public void doneDigging() {
        actionLock.lock();

        try {
            holeCount[UNSEEDED]++;
            shovelFree = true;
            canSeed.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            actionLock.unlock();
        }

    }

    /**
     * Called when Benjamin wants to seed a hole.
     */
    public void startSeeding() {
        actionLock.lock();
        try {
            while (!canSeed()) { canSeed.await(); }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            actionLock.unlock();
        }
    }

    /**
     * Called when Benjamin is done seeding the hole.
     * Number of UNSEEDED holes is decremented.
     * Number of SEEDED holes is incremented.
     * canFill Condition is signalled.
     */
    public void doneSeeding() {
        actionLock.lock();
        try {
            holeCount[UNSEEDED]--;
            holeCount[SEEDED]++;
            canFill.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            actionLock.unlock();
        }
    }

    /**
     * Called when Mary starts filling a hole.
     * Shovel is no longer free.
     */
    public void startFilling() {
        actionLock.lock();
        try {
            while (!canFIll()) { canFill.await(); }
            shovelFree = false;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            actionLock.unlock();
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
        actionLock.lock();
        try {
            holeCount[SEEDED]--;
            holeCount[FILLED]++;
            shovelFree = true;
            canDig.signal();
            canSeed.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            actionLock.unlock();
        }

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
        countLock.lock();
        int total = holeCount[UNSEEDED] + holeCount[SEEDED] + holeCount[FILLED];
        countLock.unlock();
        return total;
    }

    /**
     * Total holes seeded = holes seeded + holes filled
     * @return  Number of holes seeded
     */
    public int totalHolesSeededByBenjamin() {
        countLock.lock();
        int total = holeCount[SEEDED] + holeCount[FILLED];
        countLock.unlock();
        return total;
    }

    /**
     * @return  Number of holes filled
     */
    public int totalHolesFilledByMary() {
        countLock.lock();
        int total = holeCount[FILLED];
        countLock.unlock();
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
        if (holeCount[UNSEEDED] > 4) {
            return false;
        }

        // Check number of unfilled holes
        if ((holeCount[UNSEEDED] + holeCount[SEEDED]) > 8) {
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
        return (holeCount[UNSEEDED] <= 0);
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
