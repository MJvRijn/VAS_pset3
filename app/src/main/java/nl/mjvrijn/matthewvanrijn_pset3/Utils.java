package nl.mjvrijn.matthewvanrijn_pset3;

import java.io.InputStream;
import java.util.Scanner;

public class Utils {
    /* For the purposes of this assignment, it is far more practical to work with JSONObjects and
     * not with streaming JSONReaders. Therefore it is desirable to get the entire input stream as
     * a string. This method of doing so is from
     * http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string */
    public static String inputSteamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
