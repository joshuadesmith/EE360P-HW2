package hw1;

import java.util.Arrays;

/**
 * Created by joshuasmith on 1/24/17.
 */
public class QSortSerialTest {
    public static void main(String[] args) {
        int[] A = {13, 59, 24, 18, 33, 20, 11, 11, 13, 50, 10999, 97};

        QSortSerial sorter = new QSortSerial(A, 0, A.length - 1);
        sorter.go();
        int[] result = sorter.getArr();

        System.out.println("Result:");
        for (int i = 0; i < result.length; i++) {
            System.out.print(result[i] + " ");
        }
        System.out.println("\n");


        Arrays.sort(A);
        System.out.println("Expected:");
        for (int i = 0; i < A.length; i++) {
            System.out.print(A[i] + " ");
        }
    }
}
