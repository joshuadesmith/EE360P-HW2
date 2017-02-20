package hw1;
//UT-EID 1 = jds5228
//UT-EID 2 =


import java.util.concurrent.*;

public class PSort implements Runnable {

    public static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();
    public static int GIVEN_THRESHOLD = 4;
    public static ExecutorService threadPool;

    private int[] arr;
    private int iLeft;
    private int iRight;

    public PSort(int[] arr, int iLeft, int iRight) {
        this.arr = arr;
        this.iLeft = iLeft;
        this.iRight = iRight;
    }

    /**
     * Starting point for parallel quicksort algorithm.
     * Initializes CachedThreadPool to handle threads.
     *
     * @param A     Array to be sorted
     * @param begin Index of first element
     * @param end   Index of final element
     */
    public static void parallelSort(int[] A, int begin, int end) {

        // Cases for null array, 1 element array, and invalid indices
        if (end - begin <= 0) { return; }

        // Case for array with only 2 elements
        if (end - begin == 1) {
            return;
        }

        threadPool = Executors.newCachedThreadPool();
        Future<?> result = threadPool.submit(new PSort(A, begin, end - 1));
                                            //end - 1 accounts for test file passing A.length
        try {
            result.get();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void run() {
        sort(iLeft, iRight);
    }

    /**
     * Sorts the elements from indices iLeft to iRight.
     * The method will sort recusively in the current thread if the number of elements is below a set threshold.
     * Otherwise, it will spawn threads that will sort smaller partitions of the array.
     *
     * The sorting algorithm is based on Lomuto's partition scheme. The psuedocode for which can be found
     * on the Quicksort Wikipedia page.
     * @param iLeft     Index of the leftmost element to be sorted
     * @param iRight    Index of the rightmost element to be sorted
     */
    private void sort(int iLeft, int iRight) {

        // Case for 1-element partition or invalid indices
        if (iRight - iLeft <= 0) { return; }

        // Case for 2-element partition
        if (arr.length == 2) {
            if (arr[0] > arr[1]) {
                swap(0, 1);
                return;
            }
        }

        if (iLeft < iRight) {
            int index = partition(iLeft, iRight);

            // Set threshold to either one given or one thread per processor
            // int thresh = (arr.length/MAX_THREADS == 0) ? GIVEN_THRESHOLD : arr.length/MAX_THREADS;

            // Recursively sort in current thread if < 4 elements to be sorted
            int thresh = GIVEN_THRESHOLD;
            if ((iRight - iLeft) <= thresh) {
                sort(iLeft, index - 1);
                sort(index + 1, iRight);
            } else { // Otherwise, create new threads with new partitions of the array
                Future<?> result1 = threadPool.submit(new PSort(arr, iLeft, index - 1));
                Future<?> result2 = threadPool.submit(new PSort(arr, index + 1, iRight));
                try {
                    result1.get();
                    result2.get();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    /**
     * Partitions arr between indices left and right. Pivots all values less than
     * the value at the pivot index to the left of it.
     * @param iLeft  The left index
     * @param iRight The right index
     * @return          The final index of the pivot position
     */
    private int partition(int iLeft, int iRight) {
        int pivot = arr[iRight];
        int j = iLeft;

        for (int i = iLeft; i < iRight; i++) {
            if (arr[i] < pivot) {
                swap(i, j);
                j++;
            }
        }

        swap(j, iRight);
        return j;
    }

    /**
     * Swaps the values at two indices
     * @param left  The index of the first val to be swapped
     * @param right The index of the second val to be swapped
     */
    private void swap(int left, int right) {
        int temp = arr[left];
        arr[left] = arr[right];
        arr[right] = temp;
    }

}
