import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

public class HuffmanCompressor {

	private static Map<Character, String> charPrefixHashMap = new HashMap<>();
	static HuffmanNode root;

	private static HuffmanNode buildTree(Map<Character, Integer> freq) {

		PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>();
		Set<Character> keySet = freq.keySet();
		for (Character c : keySet) {
			HuffmanNode huffmanNode = new HuffmanNode();
			huffmanNode.data = c;
			huffmanNode.frequency = freq.get(c);
			huffmanNode.left = null;
			huffmanNode.right = null;
			priorityQueue.offer(huffmanNode);
		}

		while (priorityQueue.size() > 1) {

			HuffmanNode x = priorityQueue.peek();
			priorityQueue.poll();

			HuffmanNode y = priorityQueue.peek();
			priorityQueue.poll();

			HuffmanNode sum = new HuffmanNode();

			sum.frequency = x.frequency + y.frequency;
			sum.data = '-';

			sum.left = x;

			sum.right = y;
			root = sum;

			priorityQueue.offer(sum);
		}

		return priorityQueue.poll();
	}

	private static void setPrefixCodes(HuffmanNode node, StringBuilder prefix) {
		if (node != null) {
			if (node.left == null && node.right == null) {
				charPrefixHashMap.put(node.data, prefix.toString());
			} else {
				prefix.append('0');
				setPrefixCodes(node.left, prefix);
				prefix.deleteCharAt(prefix.length() - 1);

				prefix.append('1');
				setPrefixCodes(node.right, prefix);
				prefix.deleteCharAt(prefix.length() - 1);
			}
		}
	}

	public String encode(String test) {
		HashMap<Character, Integer> freq = new HashMap<>();
		HashMap<Character, Integer> freqSorted = new HashMap<>();

		for (int i = 0; i < test.length(); i++) {
			if (!freq.containsKey(test.charAt(i))) {
				freq.put(test.charAt(i), 0);
			}
			freq.put(test.charAt(i), freq.get(test.charAt(i)) + 1);
		}
		freqSorted = sortByValue(freq);

		root = buildTree(freqSorted);

		System.out.println("Character Frequency Map = " + freqSorted+ "\n");

		setPrefixCodes(root, new StringBuilder());
		System.out.println("Character Prefix Map = " + charPrefixHashMap);
		StringBuilder s = new StringBuilder();

		for (int i = 0; i < test.length(); i++) {
			char c = test.charAt(i);
			s.append(charPrefixHashMap.get(c));
		}

		return s.toString();
	}

	public String decode(String file) {

		StringBuilder stringBuilder = new StringBuilder();
		BitInputStream bis = new BitInputStream(file);
		StringBuilder sBuilder = new StringBuilder();

		int bit = 0;

		while (bit != -1) {
			bit = bis.readBit();
			if (bit == 0 || bit == 1) {
				sBuilder.append(bit);
			}
		}
		bis.close();

		String s = sBuilder.toString();

		HuffmanNode temp = root;

		for (int i = 0; i < s.length(); i++) {
			int j = Integer.parseInt(String.valueOf(s.charAt(i)));
			if (j == 0) {
				temp = temp.left;
				if (temp.left == null && temp.right == null) {
					stringBuilder.append(temp.data);
					temp = root;
				}
			}
			if (j == 1) {
				temp = temp.right;
				if (temp.left == null && temp.right == null) {
					stringBuilder.append(temp.data);
					temp = root;
				}
			}
		}

		// System.out.println("Decoded string is " + stringBuilder.toString());
		return stringBuilder.toString();
	}

	static class HuffmanNode implements Comparable<HuffmanNode> {
		int frequency;
		char data;
		HuffmanNode left, right;

		public int compareTo(HuffmanNode node) {
			return frequency - node.frequency;
		}
	}

	public static HashMap<Character, Integer> sortByValue(HashMap<Character, Integer> hm) {
		List<Entry<Character, Integer>> list = new LinkedList<Map.Entry<Character, Integer>>(hm.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<Character, Integer>>() {
			public int compare(Map.Entry<Character, Integer> map1, Map.Entry<Character, Integer> map2) {
				return (map1.getValue()).compareTo(map2.getValue());
			}
		});

		HashMap<Character, Integer> sortedMap = new LinkedHashMap<Character, Integer>();
		for (Map.Entry<Character, Integer> temp : list) {
			sortedMap.put(temp.getKey(), temp.getValue());
		}
		
		return sortedMap;
	}
}
