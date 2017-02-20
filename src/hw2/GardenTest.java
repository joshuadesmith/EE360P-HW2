package hw2;

import gardenTest.Benjamin;
import gardenTest.Garden;
import gardenTest.Mary;
import gardenTest.Newton;

/**
 * Created by joshuasmith on 2/12/17.
 */
public class GardenTest {
    private static final int NUM_HOLES = 10;
    private static final Garden testGarden = new Garden();

    public static void main(String[] args) {
        System.out.println("Starting test...");

        Thread newt = new Thread(new Newton(testGarden, NUM_HOLES));
        Thread benj = new Thread(new Benjamin(testGarden, NUM_HOLES));
        Thread mary = new Thread(new Mary(testGarden, NUM_HOLES));

        newt.start();
        benj.start();
        mary.start();


    }
}
