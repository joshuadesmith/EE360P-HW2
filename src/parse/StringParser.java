package parse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by joshuasmith on 4/11/17.
 */
public class StringParser {

    public static ArrayList<String> getLines(String filename) {
        ArrayList<String> lines = new ArrayList<String>();

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(filename));
            String str;
            while ((str = fileReader.readLine()) != null) {
                lines.add(str);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File \"" + filename + "\" not found");
        } catch (IOException e) {
            System.err.println("IOException while reading file");
        } finally {
            return lines;
        }
    }

    public static String[] parseLineIntoTokens(String str) {
        String line = str.trim().toLowerCase().replaceAll("[^A-Za-z0-9]", " ");
        return line.split("\\s+");
    }
}
