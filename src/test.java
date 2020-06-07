import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class test {

    public static final String defaultInPathName = "exemple.txt";
    public static final String defaultOutPathName = "out.txt";
    public StringBuilder stringBuilder = new StringBuilder();

    public void byteRead(String inPathName) {
        //Quick read with Bytes
        try {
            byte [] fileBytes = Files.readAllBytes(new File(inPathName).toPath());
            char singleChar;
            for(byte b : fileBytes) {
                singleChar = (char) b;
                stringBuilder.append(singleChar);
            }
            System.out.println(stringBuilder.toString());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void intRead(String inPathName) {
        //Quick read with Integer
        try {
            final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(inPathName), StandardCharsets.UTF_8));
            int singleCharInt;
            char singleChar;
            while ((singleCharInt = in.read()) != -1) {
                singleChar = (char) singleCharInt;
                stringBuilder.append(singleChar);
            }
            in.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void fastWrite(String stringToWrite, String outPathName) {
        if(stringToWrite.isEmpty())
        {
            System.err.println("String is empty");
            return;
        }
        //Fast Write
        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter(outPathName);
            myWriter.write(stringToWrite);
            myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        test Test = new test();
        Test.intRead(defaultInPathName);
        String stringToCompress = Test.stringBuilder.toString();

        LZWCompressor LZWcompressor = new LZWCompressor();

        long startTimeAll = System.currentTimeMillis();
        long startTimeCompressed = System.currentTimeMillis();
        List<Integer> compressedArray = LZWcompressor.compress(stringToCompress);
        long stopTimeCompressed = System.currentTimeMillis();
        long startTimeDecompressed = System.currentTimeMillis();
        String stringDecompressed = LZWcompressor.decompress(compressedArray);
        long stopTimeDecompressed = System.currentTimeMillis();
        long stopTimeAll = System.currentTimeMillis();


        System.out.println("Compression time:" + (stopTimeCompressed - startTimeCompressed) + " ms");
        System.out.println("Decompression time:" + (stopTimeDecompressed - startTimeDecompressed) + " ms");
        System.out.println("Compression and Decompression time:" + (stopTimeAll - startTimeAll) + " ms");

        Test.fastWrite(stringDecompressed, defaultOutPathName);
    }
}


