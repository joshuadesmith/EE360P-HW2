package gardenTest;

import edu.umd.cs.mtc.MultithreadedTestCase;

/**
 * Created by joshuasmith on 2/14/17.
 */
public class TestNoUnseeded extends MultithreadedTestCase {

    // Newton
    public void thread1() throws InterruptedException {
        waitForTick(1);
        RunGardenTest.garden.startDigging();
        RunGardenTest.garden.doneDigging();
    }

    // Benjamin
    public void thread2() throws InterruptedException {
        RunGardenTest.garden.startSeeding();
        assertTick(1);
        RunGardenTest.garden.doneSeeding();
    }

    // Mary
    public void thread3() throws InterruptedException {

    }
}
