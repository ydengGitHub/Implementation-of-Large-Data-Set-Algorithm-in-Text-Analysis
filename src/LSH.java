import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements locality sensitive hashing to detect near duplicates of
 * a document.
 * 
 * @author YAN DENG
 *
 */
public class LSH {

	private String[] docNames;
	private int[][] minHashMatrix;
	private int bands;
	private int r;// rows
	private ArrayList<HashMap<Integer, HashSet<String>>> tables;

	/**
	 * Constructs an in- stance of LSH
	 * 
	 * @param minHashMatrix
	 *            MinHash matrix of the document collection
	 * @param docNames
	 *            an array of Strings consisting of names of documents/files in
	 *            the document collection
	 * @param bands
	 *            the number of bands to be used to perform locality sensitive
	 *            hashing
	 */
	public LSH(int[][] minHashMatrix, String[] docNames, int bands) {
		this.minHashMatrix = minHashMatrix;
		this.docNames = docNames;
		this.bands = bands;
		if (bands == 0)
			throw new IllegalArgumentException("number of bands can not be 0.");
		this.r = minHashMatrix[0].length / bands;
		if (minHashMatrix[0].length % bands != 0) {
			System.out.printf("There are %d minhash signatures not being used.", minHashMatrix.length % bands);
		}
		this.tables = computeTables();
	}

	/**
	 * Takes name of a document as parameter and returns an array list of names
	 * of the near duplicate documents(may contain false positive)
	 * 
	 * @param docName
	 * @return an array list of names of the near duplicate documents
	 */
	public String[] nearDuplicatesOf(String docName) {
		fileOrder(docName);// throw IllegalArgument exception if docName does
							// not exist
		HashSet<String> similarDocuments = new HashSet<String>();
		for (int i = 0; i < bands; i++) {
			HashMap<Integer, HashSet<String>> ti = tables.get(i);
			Set<Integer> keys = ti.keySet();
			for (Integer k : keys) {
				if (ti.get(k).contains(docName)) {
					HashSet<String> docs = ti.get(k);
					for (String s : docs) {
						similarDocuments.add(s);
					}
					break;
				}
			}
		}
		similarDocuments.remove(docName);
		return Arrays.copyOf(similarDocuments.toArray(), similarDocuments.size(), String[].class);
	}

	/**
	 * Compute b hash Tables, T1,......Tb
	 * 
	 * @return the b hash Tables in an ArrayList
	 */
	private ArrayList<HashMap<Integer, HashSet<String>>> computeTables() {
		ArrayList<HashMap<Integer, HashSet<String>>> tables = new ArrayList<HashMap<Integer, HashSet<String>>>();
		for (int i = 0; i < bands; i++) {
			tables.add(computeTablei(i));
		}
		return tables;
	}

	/**
	 * Compute hash Table Ti
	 * 
	 * @param bandIndex
	 * @return hash Table Ti
	 */
	private HashMap<Integer, HashSet<String>> computeTablei(int bandIndex) {
		HashMap<Integer, HashSet<String>> table = new HashMap<Integer, HashSet<String>>();
		int prime = Helper.nextPrime(docNames.length);
		int index = r * bandIndex;
		int upperBound = r * (bandIndex + 1);
		for (int i = 0; i < docNames.length; i++) {
			if (docNames[i].equals(".DS_Store"))
				continue;
			String file = docNames[i];
			StringBuilder sb = new StringBuilder();
			index = r * bandIndex;
			for (; index < upperBound; index++) {
				sb.append(minHashMatrix[i][index]);
				sb.append(",");
			}
			Integer key = sb.toString().hashCode() % prime;
//			Integer key = sb.hashCode() % prime;//StringBuilder.hashCode() doesn't work.
			if (!table.containsKey(key)) {
				HashSet<String> hashSet = new HashSet<String>();
				hashSet.add(file);
				table.put(key, hashSet);
			} else {
				table.get(key).add(file);
			}
		}
		return table;
	}
	
	/**
	 * Return the index of the given file in the files array
	 * 
	 * @param file
	 * @return the index of the given file in the files array
	 */
	private int fileOrder(String file) {
		int index = -1;
		for (int i = 0; i < docNames.length; i++) {
			if (docNames[i].equals(file)) {
				index = i;
				break;
			}
		}
		if (index == -1) {
			throw new IllegalArgumentException("File: "+file+ " does not exist.");
		}
		return index;
	}
}
