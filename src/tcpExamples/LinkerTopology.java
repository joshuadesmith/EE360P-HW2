package tcpExamples;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Created by joshuasmith on 3/27/17.
 */
public class LinkerTopology {

    private static final String fileName = "topology1.txt";

    public static boolean readNeighbors(int myID, List<Integer> neighbors) {
        System.out.println("Reading topology...");

        try {
            Scanner fileScanner = new Scanner(new FileReader(fileName));

            while (fileScanner.hasNext()) {
                neighbors.add(fileScanner.nextInt());
            }

            System.out.println("Process " + myID + " has neighbors: " + neighbors.toString());
            return true;

        } catch (FileNotFoundException e) {
            System.err.println("File \"" + fileName + "\" not found");
            return false;
        }
    }

    public static void setComplete(int myID, List<Integer> neighbors, int numProc) {
        for (int i = 0; i < numProc; i++) {
            if (i != myID) { neighbors.add(i); }
        }
    }

    public static void main(String[] args) {
        LinkedList<Integer> list = new LinkedList<Integer>();
        LinkerTopology.readNeighbors(Integer.parseInt(args[0]), list);
    }
}
