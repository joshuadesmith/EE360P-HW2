package hw1;

/**
 * Created by joshuasmith on 1/24/17.
 * Implementation of QuickSort Algorithm based on the Lomuto partition scheme.
 * Psuedocode can be found on the Quicksort Wikipedia page
 */
public class QSortSerial {

    private final int[] arr;
    private final int left;
    private final int right;

    public QSortSerial(int[] arr, int left, int right) {
        this.arr = arr;
        this.left = left;
        this.right = right;
    }

    public int[] getArr() {
        return arr;
    }

    public void go() {
        sort(left, right);
    }

    private void sort(int iLeft, int iRight) {
        if (iLeft < iRight) {
            int storeIndex = partition(iLeft, iRight);
            sort(iLeft, storeIndex - 1);
            sort(storeIndex + 1, iRight);
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
