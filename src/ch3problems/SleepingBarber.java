package ch3problems;

import java.util.concurrent.Semaphore;

/**
 * Created by joshuasmith on 5/8/17.
 */
public class SleepingBarber {

    public Semaphore sleep = new Semaphore(0);
    public Semaphore barber = new Semaphore(1);
    public int numChairs;

    public SleepingBarber(int numChairs) {
        this.numChairs = numChairs;
    }

    public void runBarber() {
        try {
            while (true) {
                sleep.acquire();
                System.out.println("Woke up - giving customer a cut");
                numChairs++;
                barber.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void hairCut() {
        try {
            if (numChairs > 0) {
                numChairs--;
                sleep.release();
                System.out.println("Customer sat in chair - " + numChairs + " open chairs left");
                barber.acquire();
                System.out.println("Customer left with a new doo");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SleepingBarber sb = new SleepingBarber(6);
        Thread barbThread = new Thread(sb.new Barber(sb));
        barbThread.start();
        for (int i = 0; i < 8; i++) {
            Thread custThread = new Thread(sb.new Customer(sb));
            custThread.start();
        }
    }

    class Barber implements Runnable {
        SleepingBarber sb;

        public Barber (SleepingBarber sb) {
            this.sb = sb;
        }

        @Override
        public void run() {
            sb.runBarber();
        }
    }

    class Customer implements Runnable {
        SleepingBarber sb;

        public Customer(SleepingBarber sb) {
            this.sb = sb;
        }

        @Override
        public void run() {
            sb.hairCut();
        }
    }
}
