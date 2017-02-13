/**
 * Created by joshuasmith on 2/13/17.
 */
public class FairReadWriteTest {
    private static final int NUM = 5;
    private static final FairReadWriteLock rwLock = new FairReadWriteLock();

    public static void main(String[] args) {
        Reader[] readers = new Reader[NUM];
        Writer[] writers = new Writer[NUM];
        Thread[] threads = new Thread[2*NUM];

        for (int i = 0; i < NUM; i++) {
            readers[i] = new Reader(rwLock, i);
            writers[i] = new Writer(rwLock, i);
        }

        int j = 0;
        for (int i = 0; i < 10; i += 2) {
            threads[i] = new Thread(readers[j]);
            threads[i+1] = new Thread(writers[j]);
            j++;
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
    }
}
