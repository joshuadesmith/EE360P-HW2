package parse;

import java.util.ArrayList;

/**
 * Created by joshuasmith on 4/11/17.
 */
public class StringParserTest {

    private static String filename = "strings.txt";

    public static void main(String[] args) {
        ArrayList<String> lines = StringParser.getLines(filename);

        int i = 1;
        for (String line : lines) {
            String[] tokens = StringParser.parseLineIntoTokens(line);
            System.out.println("Tokens for line " + i + ":");
            for (String token : tokens) {
                System.out.println("\t\"" + token + "\"");
            }
            System.out.println("");
            i++;
        }
    }
}
