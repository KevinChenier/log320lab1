import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class Test {

	public static String defaultInPathName = "exemple.txt";
	public static String defaultOutPathName = "out.txt";
	public StringBuilder stringBuilder = new StringBuilder();

	public void byteRead(String inPathName) {
		// Quick read with Bytes
		try {
			byte[] fileBytes = Files.readAllBytes(new File(inPathName).toPath());
			char singleChar;
			for (byte b : fileBytes) {
				singleChar = (char) b;
				stringBuilder.append(singleChar);
			}
			System.out.println(stringBuilder.toString());
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

	public static void main(String[] args) throws IOException {
		long startTimeAll = System.currentTimeMillis();
		if(args.length != 4){
			throw new ExceptionInInitializerError();
		}
		defaultInPathName = args[2];
		defaultOutPathName = args[3];
		Test test = new Test();
		test.intRead(defaultInPathName);
		String stringToCompress = test.stringBuilder.toString();

		if((args[0].equals("-huff") || args[0].equals("-opt")) && args[1].equals("-c")) {
			HuffmanCompressor HUFFcompressor = new HuffmanCompressor();
			String s = HUFFcompressor.encode(stringToCompress);
			test.writeToFile(s);
		}
		else if((args[0].equals("-huff") || args[0].equals("-opt")) && args[1].equals("-d")) {
			HuffmanCompressor HUFFcompressor = new HuffmanCompressor();
			String stringDecompressed = HUFFcompressor.decode(defaultInPathName);
			test.fastWrite(stringDecompressed);
		}
		else if(args[0].equals("-lzw") && args[1].equals("-c")) {
			LZWCompressor LZWcompressor = new LZWCompressor();
			List<Integer> compressedArray = LZWcompressor.compress(stringToCompress);

		}
		else if(args[0].equals("-lzw") && args[1].equals("-d")) {
			LZWCompressor LZWcompressor = new LZWCompressor();
			//String stringDecompressed = LZWcompressor.decompress(compressedArray);
			//test.fastWrite(stringDecompressed);
		}

		long stopTimeAll = System.currentTimeMillis();
		System.out.println("Compression and Decompression time:" + (stopTimeAll - startTimeAll) + " ms");
	}
}