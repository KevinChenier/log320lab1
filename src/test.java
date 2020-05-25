import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class test {
    public static void main(String[] args) throws FileNotFoundException,IOException
    {
        ArrayList<Integer> tempArray = new ArrayList<Integer>();
        String fileName = "exemple.txt";
        try {
            final BufferedReader in = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8));
            int charactere;
            while ((charactere = in.read()) != -1) {
                tempArray.add(charactere);
            }
            in.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        for(int z = 0; z<300; z++) {
            System.out.println(tempArray.get(z));
        }
        System.out.println("size : "+ tempArray.size());
    }
}
