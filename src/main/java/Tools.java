import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Tools {

     // Scrambles a string and returns it
    public static String stringScramble(String input) {
        List<Character> list = new ArrayList<Character>();
        for (char c : input.toCharArray()) list.add(c);
        Collections.shuffle(list);
        StringBuilder builder = new StringBuilder();
        for (char c : list) builder.append(c);
        return builder.toString().toLowerCase(Locale.ROOT);
    }

    //checks to see if message is a valid "yea"
    public static boolean yeaCheck(String input) {
        input = input.replaceAll("[^\\w\\s]", "");
        return (input.equalsIgnoreCase("yea") || input.equalsIgnoreCase("yeah"));
    }
}