import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

public class LZWCompressor {

    private static final int dictionnarySize = 256;

    public void compress(String filein, String fileout) {
        // Build the dictionary.
        int dictSize = dictionnarySize;
        HashMap<String,Integer> dictionary = new HashMap<String,Integer>();
        for (int i = 0; i < dictionnarySize; i++)
            dictionary.put(Character.toString((char)i), i);

        String w = "";
        List<Integer> result = new ArrayList<Integer>();
        File file = new File(filein);

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
            int singleCharInt;
            char singleChar;
            while((singleCharInt = bufferedInputStream.read()) != -1) {
                singleChar = (char) singleCharInt;
                String wc = w + singleChar;
                if (dictionary.containsKey(wc))
                    w = wc;
                else {
                    result.add(dictionary.get(w));
                    // Add wc to the dictionary.
                    dictionary.put(wc, dictSize++);
                    w = Character.toString(singleChar);
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

        // Output the code for w.
        if (!w.equals(""))
            result.add(dictionary.get(w));


        File file2 = new File(fileout);
        System.out.println(dictSize);
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2))) {
            for (int i : result) {
                DataOutputStream os = new DataOutputStream(bufferedOutputStream);
                os.writeInt(i);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void decompress(String filein, String fileout) {
        // Build the dictionary.
        int dictSize = dictionnarySize;
        HashMap<Integer,String> dictionary = new HashMap<Integer,String>();
        for (int i = 0; i < dictionnarySize; i++)
            dictionary.put(i, Character.toString((char)i));

        File file = new File(filein);
        File file2 = new File(fileout);
        String w = null;
        StringBuffer result = new StringBuffer();
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file2));
             DataInputStream is = new DataInputStream(bufferedInputStream); DataOutputStream os = new DataOutputStream(out);) {

            int k = 0;
            while (is.available() > 0) {
                k = is.readInt();
                String entry = dictionary.get(k);
                if(entry == null){
                    entry = w + w.charAt(0);
                }
                os.writeBytes(entry);
                if(w != null){
                    dictionary.put(dictSize++, w + entry.charAt(0));
                }
                w = entry;
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }
}
