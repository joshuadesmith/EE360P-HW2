package hw1;
//UT-EID 1 = jds5228
//UT-EID 2 =


import java.util.*;
import java.util.concurrent.*;


public class PSearch implements Callable {

    public static int val;
    private int[] arr;
    private int iLeft, iRight;

    public PSearch(int k, int[] arr, int iLeft, int iRight) {
        this.val = k;
        this.arr = arr;
        this.iLeft = iLeft;
        this.iRight = iRight;
    }

    public static int parallelSearch(int k, int[] A, int numThreads){

        // Check for invalid number of threads
        if (numThreads <= 0) { return -1; }

        // Check for empty array
        if (A.length < 1) { return -1; }

        // Check for single-element array
        if (A.length == 1) {
            return (A[0] == k) ? 0 : -1;
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
        ArrayList<Future<Integer>> futures = new ArrayList<Future<Integer>>();

        // Search sequentially if single thread
        if (numThreads == 1) {
            Future<Integer> resultSingle = threadPool.submit(new PSearch(k, A, 0, A.length));
            try {
                return resultSingle.get();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else { // Spawn threads if multiple threads

            int partitionLength = A.length / numThreads;
            int left = 0; // Keeps track of left index
            for (int i = 0; i < numThreads; i++) {
                if ((i == numThreads - 1) && (A.length % numThreads != 0)) {
                    futures.add(threadPool.submit(new PSearch(k, A, left, A.length - 1)));
                } else {
                    futures.add(threadPool.submit(new PSearch(k, A, left, left + partitionLength - 1)));
                }
                left += partitionLength;
            }

            try {
                for (Future<Integer> f : futures) {
                    Integer result = f.get();
                    if (!result.equals(-1)) {
                        return result;
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        return -1; // if not found
    }

    @Override
    public Object call() throws Exception {
        return search();
    }

    /**
     * Searches this.arr for the value this.k
     * @return  The index of the value in the array if found
     *          -1 if the value is not found
     */
    public int search() {
        for (int i = iLeft; i <= iRight; i++) {
            if (arr[i] == val) { return i; }
        }
        return -1;
    }

}
