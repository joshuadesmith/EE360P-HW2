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
    private static int[] holeCount = new int[3];

    private ReentrantLock actionLock;
    private ReentrantLock countLock;


    /**
     * Constructor
     */
    public Garden() {   }


    public void startDigging() {   }


    public void doneDigging() {   }


    public void startSeeding() {   }


    public void doneSeeding() {   }


    public void startFilling() {   }


    public void doneFilling() {   }

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
        int total = 0;
        for (int i : holeCount) {
            total += i;
        }
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
}
