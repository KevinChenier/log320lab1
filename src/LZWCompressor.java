import java.io.*;
import java.util.*;

public class LZWCompressor {

    private static final int dictionnarySize = 256;

    public List<Integer> compress(String uncompressed) {
        // Build the dictionary.
        int dictSize = dictionnarySize;
        HashMap<String,Integer> dictionary = new HashMap<String,Integer>();
        for (int i = 0; i < dictionnarySize; i++)
            dictionary.put(Character.toString((char)i), i);

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

    public void compress3(String filein, String fileout) {
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

        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2))) {
            for (int i : result)
                bufferedOutputStream.write(i);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private String fil(int inp, int btsz) {
        String ret = "", r1 = "";
        if (inp == 0)
            ret = "0";
        int i;
        while (inp != 0) {
            if ((inp % 2) == 1)
                ret += "1";
            else
                ret += "0";
            inp /= 2;
        }
        for (i = ret.length() - 1; i >= 0; i--) {
            r1 += ret.charAt(i);
        }
        while (r1.length() != btsz) {
            r1 = "0" + r1;
        }
        return r1;
    }

    public Byte strtobt(String in) {

        int i, n = in.length();
        byte ret = 0;
        for (i = 0; i < n; i++) {
            ret *= 2.;
            if (in.charAt(i) == '1')
                ret++;
        }
        for (; n < 8; n++)
            ret *= 2.;
        Byte r = ret;
        return r;
    }


    public void compress2(String filein, String fileout) {
        Map<String, Integer> dictionary = new HashMap<String, Integer>();
        int dictSize = 256;
        String big = "";
        for (int i = 0; i < 256; i++)
            dictionary.put("" + (char) i, i);
        int mpsz = 256;
        String w = "";
        int maxByteSize = precalc(filein);

        try {
            BitInputStream input = new BitInputStream(filein);
            BitOutputStream output = new BitOutputStream(fileout);

            output.writeInt(maxByteSize);
            Byte c;
            int ch;
            while (true) {
                c = input.readByte();
                if(c == null){
                    break;
                }
                ch = Byte.toUnsignedInt(c);
                String wc = w + (char) ch;
                if (dictionary.containsKey(wc))
                    w = wc;
                else {
                    big += fil(dictionary.get(w),maxByteSize);
                    while (big.length() >= 8) {
                        output.writeByte(strtobt(big.substring(0, 8)));
                        big = big.substring(8, big.length());
                    }
                    if (mpsz < 100000) {
                        dictionary.put(wc, dictSize++);
                        mpsz += wc.length();
                    }
                    w = "" + (char) ch;
                }
            }


            if (!w.equals("")) {
                big += fil(dictionary.get(w),maxByteSize);
                while (big.length() >= 8) {
                    output.writeByte(big.substring(0, 8).getBytes("UTF-8")[0]);
                    big = big.substring(8);
                }
                if (big.length() >= 1) {
                    output.writeByte(big.getBytes("UTF-8")[0]);
                }
            }



        } catch(IOException e){
            System.out.println("IO exception = " + e);
        }
    }

    private int precalc(String fileis) {
        Map<String, Integer> dictionary = new HashMap<String, Integer>();
        int dictSize = 256;
        for (int i = 0; i < 256; i++)
            dictionary.put("" + (char) i, i);
        int mpsz = 256;
        String w = "";

        File filei = null;
        filei = new File(fileis);

        try {
            BitInputStream input = new BitInputStream(fileis);

            Byte c;
            int ch;
            while (true) {
                c = input.readByte();
                if(c == null)
                    break;
                ch = Byte.toUnsignedInt(c);
                String wc = w + (char) ch;
                if (dictionary.containsKey(wc))
                    w = wc;
                else {
                    if (mpsz < 100000) {
                        dictionary.put(wc, dictSize++);
                        mpsz += wc.length();
                    }
                    w = "" + (char) ch;
                }
            }
        } catch (IOException e) {
            System.out.println("IO exception = " + e);
        }

        // if empty file
        int btsz = 0;
        long i = 1;
        while (i < dictSize) {
            i *= 2;
            btsz++;
        }
        return btsz;
    }

    public String decompress(List<Integer> compressed) {
        // Build the dictionary.
        int dictSize = dictionnarySize;
        HashMap<Integer,String> dictionary = new HashMap<Integer,String>();
        for (int i = 0; i < dictionnarySize; i++)
            dictionary.put(i, Character.toString((char)i));

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

    public static int stoi(String s) {
        int ret = 0, i;
        for (i = 0; i < s.length(); i++) {
            ret *= 2;
            if (s.charAt(i) == '1')
                ret++;
        }
        return ret;
    }


    public void decompress2(String filein, String fileout) {
        int k;
        int dictSize = 256;
        int mpsz = 256;
        String big = "";
        String ts;
        String[] stringRepresentation = pre();
        Map<Integer, String> dictionary = new HashMap<Integer, String>();
        for (int i = 0; i < 256; i++)
            dictionary.put(i, "" + (char) i);

        try {
            BitInputStream input = new BitInputStream(filein);
            BitOutputStream output = new BitOutputStream(fileout);

            Byte c;
            int maxByteSize = input.readInt();

            while (true) {
                try {
                    c = input.readByte();
                    big += stringRepresentation[Byte.toUnsignedInt(c)];
                    if (big.length() >= maxByteSize)
                        break;
                } catch (EOFException eof) {
                    System.out.println("End of File");
                    break;
                }
            }

            if (big.length() >= maxByteSize) {
                k = stoi(big.substring(0, maxByteSize));
                big = big.substring(maxByteSize, big.length());
            } else {
                return;
            }

            String w = "" + (char) k;

            output.writeBytes(w);
            // System.out.println(w);

            while (true) {
                try {
                    while (big.length() < maxByteSize) {
                        c = input.readByte();
                        big += stringRepresentation[Byte.toUnsignedInt(c)];
                    }
                    k = stoi(big.substring(0, maxByteSize));
                    big = big.substring(maxByteSize, big.length());

                    String entry = "";
                    if (dictionary.containsKey(k)) {

                        entry = dictionary.get(k);
                    } else if (k == dictSize) {
                        entry = w + w.charAt(0);

                    }
                    output.writeBytes(entry);

                    if (mpsz < 100000) {
                        ts = w + entry.charAt(0);
                        dictionary.put(dictSize++, ts);
                        mpsz += ts.length();
                    }
                    w = entry;
                } catch (EOFException eof) {
                    System.out.println("End of File");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("IO exception = " + e);
        }
    }

    public String[] pre() {
        String[] bttost = new String[256];
        int i, j;
        String r1;
        bttost[0] = "0";
        for (i = 0; i < 256; i++) {
            r1 = "";
            j = i;
            if (i != 0)
                bttost[i] = "";
            while (j != 0) {
                if ((j % 2) == 1)
                    bttost[i] += "1";
                else
                    bttost[i] += "0";
                j /= 2;
            }
            for (j = bttost[i].length() - 1; j >= 0; j--) {
                r1 += bttost[i].charAt(j);
            }
            while (r1.length() < 8) {
                r1 = "0" + r1;
            }
            bttost[i] = r1;
        }

        return bttost;
    }

}
