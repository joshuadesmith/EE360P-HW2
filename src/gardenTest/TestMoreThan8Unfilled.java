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
        // Now 4 unseeded, 4 seeded

        waitForTick(4);

        // Now try to dig another hole - should block until hole is filled
        //int unfilled = RunGardenTest.garden.totalHolesDugByNewton() - RunGardenTest.garden.totalHolesFilledByMary();
        //System.out.println("There are " + unfilled + " unfilled holes*****************");
        RunGardenTest.garden.startDigging();
        assertTick(6);
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
        // Now 0 unseeded, 4 seeded

        waitForTick(3);
        RunGardenTest.garden.startSeeding();
        RunGardenTest.garden.doneSeeding();
        // Now 3 unseeded, 5 seeded

        waitForTick(5);
        RunGardenTest.garden.startSeeding();
        RunGardenTest.garden.doneSeeding();
        // Now 2 unseeded, 6 seeded
    }

    public void thread3() throws InterruptedException {
        waitForTick(6);
        RunGardenTest.garden.startFilling();
        RunGardenTest.garden.doneFilling();
        // Now 2 unseeded, 5 seeded

    }
}
