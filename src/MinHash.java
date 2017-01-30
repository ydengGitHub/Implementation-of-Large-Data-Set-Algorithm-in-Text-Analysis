import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Construct a K × N minhash matrix that can be used to estimate similarity of
 * any two documents, where K is the number of random permutations.
 * 
 * @author YAN DENG
 *
 */
public class MinHash {

	private String folderName;
	private String[] files;
	private int numPermutations;
	private HashSet<String> terms; // for quick query
	private String[] termsArray; // original order of the terms, range between
									// [0,numTerms-1]
	private int numTerms;
	private int numDocuments;
	private int prime; // numTerms<=primer<=2*numTerms
	private int[][] coefficients; // store the k pairs of a,b values
	private int[][] minHashMatrix;
	private boolean[][] bfMatrix; // binary frequency matrix for calculating the
									// exact similarities
	private int[][] termsOrders; // [i'th permutation] [j'th term]'s position

	/**
	 * Constructor, initialize a new MinHash instance.
	 * 
	 * @param folder
	 *            The name of a folder containing our document collection for
	 *            which we wish to construct MinHash matrix
	 * @param numPermutations
	 *            Denotes the number of permutations to be used in creating the
	 *            MinHash matrix
	 */
	public MinHash(String folder, int numPermutations) {
		this.folderName = folder;
		this.files = allDocs();
		this.numPermutations = numPermutations;
		this.terms = new HashSet<String>();
		getTerms(); // initialize the 'terms', 'numDocuments' variable
		this.numTerms = terms.size();
		System.out.println("Number of terms: " + numTerms);
		this.termsArray = Arrays.copyOf(terms.toArray(), terms.size(), String[].class);
		this.prime = Helper.nextPrime(numTerms);
		System.out.println("Chosen Prime is: " + prime);
		this.coefficients = getCoefficients(numPermutations);
		this.binaryFreqMatrix();
		this.minHashMatrix();
	}

	/**
	 * 
	 * @return Returns an array of String consisting of all the names of files
	 *         in the document collection
	 */
	public String[] allDocs() {
		File[] files = new File(folderName).listFiles();
		if (files.length == 0)
			return new String[] {};
		String[] docs = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			docs[i] = files[i].getName();
		}
		return docs;
	}

	/**
	 * 
	 * @param fileName
	 * @return Returns the binary Frequency of the given file with respect to
	 *         the whole terms
	 */
	public boolean[] binaryFreq(String fileName) {
		File file = new File(folderName + "/" + fileName);
		HashSet<String> hashset = readFile(file);
		boolean[] binaryFreq = new boolean[numTerms];
		for (int j = 0; j < numTerms; j++) {
			if (hashset.contains(termsArray[j])) {
				binaryFreq[j] = true;
			}
		}
		return binaryFreq;
	}

	/**
	 * Calculate the binary frequency matrix for all documents.
	 * 
	 * @return the binary frequency matrix
	 */
	public boolean[][] binaryFreqMatrix() {
		bfMatrix = new boolean[files.length][numTerms];
		for (int i = 0; i < files.length; i++) {
			if (files[i].equals(".DS_Store"))
				continue;
			bfMatrix[i] = binaryFreq(files[i]);

		}
		return bfMatrix;
	}

	/**
	 * Calculate the binary frequencies of the two given files and then
	 * calculate the exact Jaccard Simility by the formula: Intersection/Union
	 * 
	 * @param file1
	 * @param file2
	 * @return Returns the exact Jaccard Similarity of the given two files.
	 */
	public double exactJaccard(String file1, String file2) {
		int intersection = 0;
		int union = 0;
		boolean[] binaryFreq1;
		boolean[] binaryFreq2;
		if (bfMatrix == null || bfMatrix.length == 0) {
			binaryFreq1 = binaryFreq(file1);
			binaryFreq2 = binaryFreq(file2);
		} else {
			binaryFreq1 = bfMatrix[fileOrder(file1)];
			binaryFreq2 = bfMatrix[fileOrder(file2)];
		}
		for (int i = 0; i < numTerms; i++) {
			if (binaryFreq1[i] == true && binaryFreq2[i] == true) {
				intersection++;
				union++;
			} else if (binaryFreq1[i] == true || binaryFreq2[i] == true) {
				union++;
			}
		}
		// System.out.println("File1: " + file1 + ", File2: " + file2 + ",
		// intersection: " + intersection + ", union: "
		// + union + ",exactJaccard: " + ((double) intersection) / union);
		return ((double) intersection) / union;
	}

	/**
	 * Calculate the min hash value for each permutation and return the values
	 * in an array
	 * 
	 * @param fileName
	 * @return Returns the MinHash signature (an array of int) of the given
	 *         document
	 */
	public int[] minHashSig(String fileName) {
		File file = new File(folderName + "//" + fileName);
		int[] signature = new int[numPermutations];
		HashSet<String> hashset = readFile(file);
		for (int i = 0; i < numPermutations; i++) {
			int minHash = numTerms;
			for (int j = 0; j < numTerms; j++) {
				if (hashset.contains(termsArray[termsOrders[i][j]])) {
					minHash = j;
					break;
				}
			}
			signature[i] = minHash;
		}
		return signature;
	}

	/**
	 * Estimate and returns the Jaccard similarity of documents file1 and file2
	 * by comparing the MinHash signatures of file1 and file2
	 * 
	 * @param file1
	 * @param file2
	 * @return the jaccard similarity between file1 and file2
	 */
	public double approximateJaccard(String file1, String file2) {
		int count = 0;
		int[] sig1;
		int[] sig2;
		if (minHashMatrix == null || minHashMatrix.length == 0) {
			sig1 = minHashSig(file1);
			sig2 = minHashSig(file2);
		} else {
			sig1 = minHashMatrix[fileOrder(file1)];
			sig2 = minHashMatrix[fileOrder(file2)];
		}
		for (int i = 0; i < numPermutations; i++) {
			if (sig1[i] == sig2[i]) {
				count++;
			}
		}
		// System.out.println("File1: " + file1 + ", File2: " + file2 +
		// ",approxJaccard: "
		// + ((double) count) / numPermutations);
		return ((double) count) / numPermutations;
	}

	/**
	 * 
	 * @return Returns the MinHash Matrix of the collection.
	 */
	public int[][] minHashMatrix() {
		minHashMatrix = new int[numDocuments][numPermutations];
		computeTermsOrders();
		for (int i = 0; i < numDocuments; i++) {
			if (files[i].equals(".DS_Store"))
				continue;
			minHashMatrix[i] = minHashSig(files[i]);

		}
		return minHashMatrix;
	}

	/**
	 * Compute and store the orders of each permutations
	 */
	private void computeTermsOrders() {
		termsOrders = new int[numPermutations][numTerms];
		for (int i = 0; i < numPermutations; i++) {
			for (int j = 0; j < numTerms; j++) {
				int index = ((coefficients[i][0] * j + coefficients[i][1]) % prime) % numTerms;
				termsOrders[i][index] = j;
			}
		}
	}

	/**
	 * 
	 * @return Returns the number of terms in the document collection
	 */
	public int numTerms() {
		return numTerms;
	}

	/**
	 * 
	 * @return Returns the number of permutations used to construct the MinHash
	 *         matrix
	 */
	public int numPermutations() {
		return numPermutations;
	}

	/**
	 * Go through all the documents in the given folder and collect all terms
	 * and store them in the terms hashset.
	 */
	private void getTerms() {
		File[] files = new File(folderName).listFiles();
		if (files.length == 0)
			throw new IllegalArgumentException("The folder is empty.");
		numDocuments = files.length;
		System.out.println("Number of files:" + numDocuments);// include
																// .DS_Store if
																// using Mac
		for (int i = 0; i < numDocuments; i++) {
			// System.out.println(files[i].getName());
			if (files[i].getName().equals(".DS_Store"))
				continue;
			terms.addAll(readFile(files[i]));
		}
	}

	/**
	 * Go through the given file, and add the non-repeated terms to a hashset
	 * hashset
	 * 
	 * @param file
	 * @return Returns the hashset of terms in the given file
	 */
	private HashSet<String> readFile(File file) {
		HashSet<String> hashset = new HashSet<String>();
		try {
			Scanner scan = new Scanner(file);
			String[] line;
			while (scan.hasNextLine()) {
				//line = scan.nextLine().split("\\W+"); // it will split the
														// string based upon
														// non-word character
				line=scan.nextLine().split("[,.:;\\s\\']+");
				for (int i = 0; i < line.length; i++) {
					String word = line[i].toLowerCase();
					if (!(word.length() < 3 || word.equals("the"))) {
						hashset.add(word);
					}
				}
			}
			scan.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("File: " + file.getName() + ", terms: " +
		// hashset.size());
		return hashset;
	}

	/**
	 * Generate k random values for a in the range [1,..., p-1] and k random
	 * values for b in the range [0,...,p-1]
	 * 
	 * @param numPermutations
	 * @return K pairs of a,b values
	 */
	private int[][] getCoefficients(int numPermutations) {
		int[][] cefs = new int[numPermutations][2];
		for (int i = 0; i < numPermutations; i++) {
			cefs[i][0] = Helper.generateRan(prime - 1) + 1; // Generate
															// a;
			cefs[i][1] = Helper.generateRan(prime); // Generate b;
			// System.out.println("a= " + cefs[i][0] + " b= " + cefs[i][1]);
		}
		return cefs;
	}

	/**
	 * Return the index of the given file in the files array
	 * 
	 * @param file
	 * @return the index of the given file in the files array
	 */
	private int fileOrder(String file) {
		int index = -1;
		for (int i = 0; i < files.length; i++) {
			if (files[i].equals(file)) {
				index = i;
				break;
			}
		}
		if (index == -1) {
			throw new IllegalArgumentException("File " + file + " does not exist.");
		}
		return index;
	}
}
