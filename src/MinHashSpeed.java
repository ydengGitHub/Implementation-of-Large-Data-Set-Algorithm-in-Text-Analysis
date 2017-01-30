/**
 * This class tests whether it is faster to estimate Jaccard Similarities using
 * MinHash matrix than to compute the similarities exactly.
 * 
 * @author YAN DENG
 *
 */
public class MinHashSpeed {
	public static void main(String[] args) throws IllegalAccessException {
		if (args.length != 2) {
			throw new IllegalArgumentException("Invalid arguments, should be: <foldername> <number of permutations>");
		}

		String folderName = args[0].trim();
		int k = Integer.parseInt(args[1]);// number of permutations
		if (k < 1) {
			throw new IllegalArgumentException("Number of permutation should be greater or equal to 1.");
		}

		MinHash test = new MinHash(folderName, k);
		String[] files = test.allDocs();
		System.out.println("Number of Permutations: " + k);
		System.out.println();
		Helper timer1 = new Helper();
		double[][] exactJacMatrix;

		/*
		 * compute the exact similarities without using binary Frequency Matrix
		 */
//		System.out.println("Start to compute the exact similarities without the use of binary Frequency Matrix......");
//		timer1.startTimer();
//		// test.binaryFreqMatrix();
//		exactJacMatrix = new double[files.length][files.length];
//		for (int i = 0; i < files.length; i++) {
//			String file1 = files[i];
//			for (int j = i + 1; j < files.length; j++) {
//				String file2 = files[j];
//				exactJacMatrix[i][j] = test.exactJaccard(file1, file2);
//			}
//		}
//		timer1.stopTimer();
//		System.out.println("Mission complete. It takes " + timer1.getTime() / 1000
//				+ " seconds to compute the exact similarities.");
//		System.out.println();

		/*
		 * compute the exact similarities by using binary Frequency Matrix
		 */
		timer1 = new Helper();
		System.out.println("Start to compute the exact similarities by using the binaray Frequency Matrix......");
		timer1.startTimer();
		test.binaryFreqMatrix();
		timer1.stopTimer();
		System.out.println("It takes " + timer1.getTime() / 1000 + " seconds to create the binary Frequency Matrix.");
		timer1.startTimer();
		exactJacMatrix = new double[files.length][files.length];
		for (int i = 0; i < files.length; i++) {
			String file1 = files[i];
			for (int j = i + 1; j < files.length; j++) {
				String file2 = files[j];
				exactJacMatrix[i][j] = test.exactJaccard(file1, file2);
			}
		}
		timer1.stopTimer();
		System.out.println("Mission complete. It takes " + timer1.getTime() / 1000
				+ " seconds to compute the exact similarities.");
		System.out.println();

		/*
		 * compute the approximate similarities by using min Hash matrix
		 */
		Helper timer2 = new Helper();
		System.out.println("Start to compute the approximate similarities......");
		timer2.startTimer();
		test.minHashMatrix();
		timer2.stopTimer();
		System.out.println("It takes " + timer2.getTime() / 1000 + " seconds to create the MinHash Matrix.");
		timer2.startTimer();
		double[][] approxJacMatrix = new double[files.length][files.length];
		for (int i = 0; i < files.length; i++) {
			String file1 = files[i];
			for (int j = i + 1; j < files.length; j++) {
				String file2 = files[j];
				approxJacMatrix[i][j] = test.approximateJaccard(file1, file2);
			}
		}
		timer2.stopTimer();
		System.out.println("Mission complete. It takes " + timer2.getTime() / 1000
				+ " seconds to compute the approximate similarities.");
	}

}
