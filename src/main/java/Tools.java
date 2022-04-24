import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Tools {

    static Map<String, String> sortByValue(Map<String, String> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, String>> list =
                new LinkedList<Map.Entry<String, String>>((Collection<? extends Map.Entry<String, String>>) unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        list.sort(new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                return -1 * Integer.compare(Integer.parseInt(o1.getValue()), Integer.parseInt(o2.getValue()));
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }


    // Stores yeaCount to a file
   /* public static void writeYea() {
        Main.yeaFileProperties.putAll(Main.yeaCount);
        try {
            Main.yeaFileProperties.store(new FileOutputStream("usr/app/yea.txt"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    // Scrambles a string and returns it
    public static String stringScramble(String input) {
        List<Character> list = new ArrayList<Character>();
        for (char c : input.toCharArray()) list.add(c);
        Collections.shuffle(list);
        StringBuilder builder = new StringBuilder();
        for (char c : list) builder.append(c);
        return builder.toString();
    }
}