import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class test {
    public static void main(String[] args) throws FileNotFoundException,IOException
    {
        //Quick read with Integer
        ArrayList<Integer> tempArray = new ArrayList<Integer>();
        String fileName = "exemple.txt";
        try {
            final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8));
            int charactere;
            while ((charactere = in.read()) != -1) {
                tempArray.add(charactere);
            }
            in.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        for(int z = 0; z<300; z++) {
            System.out.println((char)(int)tempArray.get(z));
        }
        System.out.println("size : "+ tempArray.size());


        //Quick read with Bytes
        byte [] fileBytes = Files.readAllBytes(new File("exemple.txt").toPath());
        char singleChar;
        for(byte b : fileBytes) {
            singleChar = (char) b;
            System.out.print(singleChar);
        }



        //Fast Write
        RandomAccessFile out = null;
        try {
            out = new RandomAccessFile("out.txt", "rw");
            FileChannel file = out.getChannel();
            ByteBuffer buf = file.map(FileChannel.MapMode.READ_WRITE, 0, 4 * tempArray.size());
            for (int i : tempArray) {
                buf.allocate(4).putInt(i).array();
            }
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
