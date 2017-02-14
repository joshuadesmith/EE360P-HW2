package gardenTest;

import edu.umd.cs.mtc.MultithreadedTestCase;

/**
 * Created by joshuasmith on 2/14/17.
 */
public class Test4Unseeded extends MultithreadedTestCase{

    public void thread1() throws InterruptedException {
        // Dig 4 holes
        for (int i = 0; i < 4; i++) {
            RunGardenTest.garden.startDigging();
            RunGardenTest.garden.doneDigging();
        }

        // Try to dig 5th hole - should block until hole is seeded
        RunGardenTest.garden.startDigging();
        assertTick(1);
        RunGardenTest.garden.doneDigging();

    }

    public void thread2() throws InterruptedException {
        waitForTick(1);
        RunGardenTest.garden.startSeeding();
        RunGardenTest.garden.doneSeeding();
    }
}
