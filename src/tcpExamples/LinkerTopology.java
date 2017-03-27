package tcpExamples;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * Created by joshuasmith on 3/27/17.
 */
public class LinkerTopology {

    private static final String fileName = "topology_info.txt";

    public static boolean readNeighbors(HashMap<Integer, LinkedList<Integer>> neighbors) {
        System.out.println("Reading topology...");

        try {
            Scanner fileScanner = new Scanner(new FileReader(fileName));

            // Get total number of processes
            int numProc = Integer.parseInt(fileScanner.nextLine());

            // Get a list of neighbors for each process
            for (int i = 1; i <= numProc; i++) {
                String[] procs = fileScanner.nextLine().trim().split(" ");
                LinkedList<Integer> procNeighbors = new LinkedList<Integer>();

                for (String str : procs) {
                    procNeighbors.add(Integer.parseInt(str));
                }

                neighbors.put(i, procNeighbors);
            }

            printNeighborLists(neighbors, numProc);
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

    public static void printNeighborLists(HashMap<Integer, LinkedList<Integer>> neighbors, int numProc) {
        System.out.println(numProc + " processes::");
        for (int i = 1; i <= numProc; i++) {
            System.out.print("Neighbors of P" + i + ": ");
            if (neighbors.containsKey(i)) {
                System.out.print(neighbors.get(i).toString() + "\n");
            } else {
                System.out.print("*none*");
            }
        }
    }

    public static void main(String[] args) {
        HashMap<Integer, LinkedList<Integer>> neighborLists = new HashMap<Integer, LinkedList<Integer>>();
        LinkerTopology.readNeighbors(neighborLists);
    }
}
