package gardenTest;

/**
 * Created by joshuasmith on 2/12/17.
 */
public class Benjamin implements Runnable {
    private static int count;
    private static int max;
    private static Garden garden;

    public int getSeeded() {
        return count;
    }

    public Benjamin(Garden g, int m) {
        count = 0;
        max = m;
        garden = g;
    }

    public void run() {
        while (count < max) {
            garden.startSeeding();
            seed();
            garden.doneSeeding();
        }

        System.out.println("Done seeding. Seeded" + count + " holes.");
    }

    public void seed() {
        count++;
    }
}
