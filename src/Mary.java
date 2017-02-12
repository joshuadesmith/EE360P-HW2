/**
 * Created by joshuasmith on 2/12/17.
 */
public class Mary implements Runnable {
    private static int count;
    private static int max;
    private static Garden garden;

    public int getFilled() {
        return count;
    }

    public Mary(Garden g, int m) {
        count = 0;
        max = m;
        garden = g;
    }

    public void run() {
        while (count < max) {
            garden.startFilling();
            fill();
            garden.doneFilling();
        }
    }

    public void fill() {
        count++;
    }
}
