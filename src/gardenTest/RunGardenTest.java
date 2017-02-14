package gardenTest;

import edu.umd.cs.mtc.TestFramework;

/**
 * Created by joshuasmith on 2/14/17.
 */
public class RunGardenTest {
    final static Garden garden = new Garden();

    public static void main(String[] args) throws Throwable {
        //TestFramework.runOnce(new Test4Unseeded());
        //TestFramework.runOnce(new TestMoreThan8Unfilled());
        TestFramework.runOnce(new TestNoUnseeded());

    }
}
