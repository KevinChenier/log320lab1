import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LZWCompressor {

    private static final int dictionnarySize = 16384;

    public List<Integer> compress(String uncompressed) {
        // Build the dictionary.
        int dictSize = dictionnarySize;
        HashMap<String,Integer> dictionary = new HashMap<String,Integer>();
        for (int i = 0; i < dictionnarySize; i++)
            dictionary.put("" + (char)i, i);

        String w = "";
        List<Integer> result = new ArrayList<Integer>();
        for (char c : uncompressed.toCharArray()) {
            String wc = w + c;
            if (dictionary.containsKey(wc))
                w = wc;
            else {
                result.add(dictionary.get(w));
                // Add wc to the dictionary.
                dictionary.put(wc, dictSize++);
                w = Character.toString(c);
            }
        }

        // Output the code for w.
        if (!w.equals(""))
            result.add(dictionary.get(w));
        return result;
    }

    public String decompress(List<Integer> compressed) {
        // Build the dictionary.
        int dictSize = dictionnarySize;
        HashMap<Integer,String> dictionary = new HashMap<Integer,String>();
        for (int i = 0; i < dictionnarySize; i++)
            dictionary.put(i, "" + (char)i);

        String w = "" + (char)(int)compressed.remove(0);
        StringBuffer result = new StringBuffer(w);
        try {
            for (int k : compressed) {
                String entry = dictionary.containsKey(k) ? dictionary.get(k) : w + w.charAt(0);
                result.append(entry);

                dictionary.put(dictSize++, w + entry.charAt(0));
                w = entry;
            }
        } catch (NullPointerException e){
            System.err.println("Null exception was catched... LZW decompression failed.");
            return result.toString();
        } finally {
            return result.toString();
        }
    }
}
