package hw1;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by joshuasmith on 1/30/17.
 */
public class SortArrayTest {

    private static final int MAX_LEN = 10000;
    private static final int HEAD_LEN = 20;

    public static void main(String[] args) {
        int[] A = generateArray(0);
        verify(A);

        int[] B = generateArray(1);
        verify(B);

        int[] C = generateArray(2);
        verify(C);

        int[] D = generateArray(10);
        verify(D);

        int[] E = generateArray(100);
        verify(E);

        int[] F = generateArray(1000);
        verify(F);

        int[] G = generateArray(MAX_LEN);
        verify(G);
    }

    static void verify(int[] A) {
        long startTime, execTime;
        int[] B = new int[A.length];
        System.arraycopy(A, 0, B, 0, A.length);

        System.out.println("Verify Parallel Sort for array: ");
        printArray(A);

        Arrays.sort(A);

        startTime = System.currentTimeMillis();
        PSort.parallelSort(B, 0, B.length);

        execTime = System.currentTimeMillis() - startTime;
        System.out.println("Execution time for serial quicksort: " + execTime);

        boolean isSuccess = true;
        for (int i = 0; i < A.length; i++) {
            if (A[i] != B[i]) {
                System.out.println("Your parallel sorting algorithm is not correct");
                System.out.println("Expect:");
                printArray(A);
                System.out.println("Your results:");
                printArray(B);
                isSuccess = false;
                break;
            }
        }

        if (isSuccess) {
            System.out.println("Great, your sorting algorithm works for this test case");
        }
        System.out.println("=========================================================");
    }

    public static void printArrayHead(int[] A, int len) {
        int rem = A.length - len;
        for (int i = 0; i < len; i++) {
            if (i != len - 1) {
                System.out.print(A[i] + " ");
            } else {
                System.out.print(A[i] + " ... ");
            }
        }
        System.out.println("(" + rem + " elements not shown)\n");
    }

    public static void printArray(int[] A) {
        if (A.length > HEAD_LEN) {
            printArrayHead(A, HEAD_LEN);
        } else {
            for (int i = 0; i < A.length; i++) {
                if (i != A.length - 1) {
                    System.out.print(A[i] + " ");
                } else {
                    System.out.print(A[i]);
                }
            }
            System.out.println();
        }
    }

    public static int[] generateArray(int size) {
        int[] A = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            A[i] = random.nextInt(size);
        }
        return A;
    }
}
