/**
 * Created by joshuasmith on 2/12/17.
 */
public class Newton implements Runnable {

    private static int count;
    private static int max;
    private static Garden garden;

    public Newton(Garden g, int m) {
        count = 0;
        max = m;
        garden = g;
    }

    public void run() {
        while (count < max) {
            garden.startDigging();
            dig();
            garden.doneDigging();
        }
    }

    private void dig() {
        count++;
    }
}
