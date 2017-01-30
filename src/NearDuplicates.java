import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class puts together MinHash and LSH to detect near duplicates in a
 * document collection.
 * 
 * @author YAN
 *
 */
public class NearDuplicates {

	public static void main(String[] args) {
		if (args.length != 5) {
			throw new IllegalArgumentException(
					"Invalid arguments. Should be: <folder name> <number of permutations> <number of bands> <similarity threshold> <name of a document from the collection>");
		}

		String folderName = args[0].trim();
		int numPermutations = Integer.parseInt(args[1]);// number of
														// permutations
		if (numPermutations < 1) {
			throw new IllegalArgumentException("Number of permutations should be greater or equal to 1.");
		}
		int bands = Integer.parseInt(args[2]);
		if (bands < 1 || bands > numPermutations) {
			throw new IllegalArgumentException(
					"Number of bands should be greater or equal to 1 and less or equal to number of permutations.");
		}

		double threshold = Double.parseDouble(args[3]);
		String fileName = args[4];

		MinHash minHash = new MinHash(folderName, numPermutations);
		execute(minHash, numPermutations, bands, threshold, fileName);

		/*
		 * Randomly pick 20 files, run with the bands 10, 25, 50, 100 and output
		 * the result
		 */
//		File outputFile = new File("nearDuplicate_result.txt");
//		try {
//			PrintWriter writer = new PrintWriter(outputFile);
//			writer.append(
//					"FileName\\Number of Bands;10 Candidate;10 Similar;25 Candidate;25Similar;50 Candidate;50 Similar;100 Candidate;100 Similar\n");
//			for (int i = 0; i < 20; i++) {
//				Random ran = new Random();
//				int index = ran.nextInt(1000);
//				String testFile = "space-" + index + ".txt";
//				writer.append(testFile + ";" + execute(minHash, numPermutations, 10, threshold, testFile) + ";");
//				writer.append(execute(minHash, numPermutations, 25, threshold, testFile) + ";");
//				writer.append(execute(minHash, numPermutations, 50, threshold, testFile) + ";");
//				writer.append(execute(minHash, numPermutations, 100, threshold, testFile) + "\n");
//			}
//			writer.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * Helper Method
	 * 
	 * @param minHash
	 * @param numPermutations
	 * @param bands
	 * @param threshold
	 * @param fileName
	 * @return
	 */
	private static String execute(MinHash minHash, int numPermutations, int bands, double threshold, String fileName) {
		int[][] minHashMatrix = minHash.minHashMatrix();
		String[] docNames = minHash.allDocs();
		System.out.println("Number of docs: " + docNames.length);
		// for(int i=0;i<docNames.length;i++){
		// if(i%8==0) System.out.println();
		// System.out.print(docNames[i]+", ");
		// }

		LSH lsh = new LSH(minHashMatrix, docNames, bands);
		String[] candidateFiles = lsh.nearDuplicatesOf(fileName);
		System.out.println();
		System.out.println(numPermutations + " permutations and " + bands
				+ " bands are used to find near duplicate files for " + fileName + ".");
		System.out.println("There are " + candidateFiles.length + " candidate files.");
		ArrayList<String> similarFiles = new ArrayList<String>();
		for (String s : candidateFiles) {
			double jacValue = minHash.approximateJaccard(fileName, s);
			// System.out.println(s + ", " + jacValue);
			if (jacValue >= threshold) {
				similarFiles.add(s);
			}
		}
		System.out.println("There are " + similarFiles.size() + " files which are near duplicate to " + fileName
				+ " with threshold: " + threshold + ":");
		System.out.println(similarFiles);
		String result = candidateFiles.length + ";" + similarFiles.size();
		return result;
	}
}
