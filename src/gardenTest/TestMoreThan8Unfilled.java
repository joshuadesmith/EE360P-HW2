package gardenTest;

import edu.umd.cs.mtc.MultithreadedTestCase;

/**
 * Created by joshuasmith on 2/14/17.
 */
public class TestMoreThan8Unfilled extends MultithreadedTestCase {

    public void thread1() throws InterruptedException {
        // Dig 4 holes
        for (int i = 0; i < 4; i++) {
            RunGardenTest.garden.startDigging();
            RunGardenTest.garden.doneDigging();
        }

        // Wait for seeder
        waitForTick(2);

        // Dig 4 more holes
        for (int i = 0; i < 4; i++) {
            RunGardenTest.garden.startDigging();
            RunGardenTest.garden.doneDigging();
        }

        waitForTick(4);

        // Now try to dig another hole - should block until hole is filled
        int unfilled = RunGardenTest.garden.totalHolesDugByNewton() - RunGardenTest.garden.totalHolesFilledByMary();
        System.out.println("There are " + unfilled + " unfilled holes*****************");
        RunGardenTest.garden.startDigging();
        assertTick(5);
        RunGardenTest.garden.doneDigging();
    }

    public void thread2() throws InterruptedException {
        // Wait until 4 unseeded holes
        waitForTick(1);

        // Seed 4 holes
        for (int i = 0; i < 4; i++) {
            RunGardenTest.garden.startSeeding();
            RunGardenTest.garden.doneSeeding();
        }

        waitForTick(3);
        RunGardenTest.garden.startSeeding();
        RunGardenTest.garden.doneSeeding();
    }

    public void thread3() throws InterruptedException {
        waitForTick(5);
        RunGardenTest.garden.startFilling();
        RunGardenTest.garden.doneFilling();
    }
}
