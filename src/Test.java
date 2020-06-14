import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Test {

	public static String defaultInPathName;
	public static String defaultOutPathName;
	public StringBuilder stringBuilder = new StringBuilder();

	public void byteRead(String inPathName) {
		File file = new File(inPathName);

		try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
			int singleCharInt;
			char singleChar;
			while((singleCharInt = bufferedInputStream.read()) != -1) {
				singleChar = (char) singleCharInt;
				stringBuilder.append(singleChar);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void intRead(String inPathName) {
		// Quick read with Integer
		try {
			final BufferedReader in = new BufferedReader(
					new InputStreamReader(new FileInputStream(inPathName), StandardCharsets.UTF_8));
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

	public void fastWrite(String stringToWrite) {
		if (stringToWrite.isEmpty()) {
			System.err.println("String is empty");
			return;
		}
		// Fast Write
		FileWriter myWriter = null;
		try {
			myWriter = new FileWriter(defaultOutPathName);
			myWriter.write(stringToWrite);
			myWriter.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeToFile(String binaryString) throws IOException {
		BitOutputStream bos = new BitOutputStream(defaultOutPathName);
		for (int j = 0; j < binaryString.length(); j++) {
			bos.writeBit(Character.getNumericValue(binaryString.charAt(j)));
		}
	}

	public List<Integer> extractIntegerListFromFile(String pathName) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(pathName));
		scanner.useDelimiter("\\D+");
		List<Integer> list = new ArrayList<Integer>();
		while (scanner.hasNextInt()) {
			list.add(scanner.nextInt());
		}
		return list;
	}

	public static void main(String[] args) throws IOException {
		if(args.length != 4){
			throw new ExceptionInInitializerError();
		}
		defaultInPathName = args[2];
		defaultOutPathName = args[3];
		Test test = new Test();

		if((args[0].equals("-huff") || args[0].equals("-opt")) && args[1].equals("-c")) {
			test.byteRead(defaultInPathName);
			String stringToCompress = test.stringBuilder.toString();
			HuffmanCompressor HUFFcompressor = new HuffmanCompressor();
			String stringCompressed = HUFFcompressor.encode(stringToCompress);
			test.writeToFile(stringCompressed);
		}
		else if((args[0].equals("-huff") || args[0].equals("-opt")) && args[1].equals("-d")) {
			HuffmanCompressor HUFFcompressor = new HuffmanCompressor();
			String stringDecompressed = HUFFcompressor.decode(defaultInPathName);
			test.fastWrite(stringDecompressed);
		}
		else if(args[0].equals("-lzw") && args[1].equals("-c")) {
			//test.byteRead(defaultInPathName);
			//String stringToCompress = test.stringBuilder.toString();
			LZWCompressor LZWcompressor = new LZWCompressor();
			LZWcompressor.compress2(defaultInPathName,defaultOutPathName);
			//List<Integer> compressedArray = LZWcompressor.compress(stringToCompress);
			//test.fastWrite(compressedArray.toString());
		}
		else if(args[0].equals("-lzw") && args[1].equals("-d")) {
			LZWCompressor LZWcompressor = new LZWCompressor();
			LZWcompressor.decompress2(defaultInPathName,defaultOutPathName);
			//List<Integer> integerList = test.extractIntegerListFromFile(defaultInPathName);
			//String stringDecompressed = LZWcompressor.decompress(integerList);
			//test.fastWrite(stringDecompressed);
		}
	}
}
