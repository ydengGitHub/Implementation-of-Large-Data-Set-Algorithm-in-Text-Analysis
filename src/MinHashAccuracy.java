import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Tests how accurately MinHash matrix can be used to estimate Jaccard
 * Similarity.
 * 
 * @author YAN DENG
 *
 */
public class MinHashAccuracy {

	public static void main(String[] args) {
		if (args.length != 3) {
			throw new IllegalArgumentException(
					"Invalid arguments, should be: <foldername> <number of permutations> <error parameter>");
		}

		String folderName = args[0].trim();
		// System.out.println(folderName);
		int k = Integer.parseInt(args[1]);// number of permutations
		if (k < 1) {
			throw new IllegalArgumentException("Number of permutation should be greater or equal to 1.");
		}
		double epsilon = Double.parseDouble(args[2]);
		if (epsilon < 0 || epsilon >= 1) {
			throw new IllegalArgumentException("error parameter should range between 0 and 1");
		}

		MinHash test = new MinHash(folderName, k);
		test.binaryFreqMatrix();
		test.minHashMatrix();
		String[] files = test.allDocs();
		execute(test, files, epsilon);
		
		/*
		 * For each of k=400, 600, 800, and epsilon=0.04, 0.07, 0.09, run the test 10 times and output the results.
		 */
//		File outputFile=new File("minHashAccuracy_result.txt");
//		try{
//			PrintWriter writer=new PrintWriter(outputFile);
//			writer.append("NumPermutations\\ErrorParameter; 0.04;0.07;0.09\n");
//			for(int i=0;i<3;i++){
//				int numPerm=400+200*i;
//				for(int j=0;j<10;j++){
//					test = new MinHash(folderName, numPerm);
//					test.binaryFreqMatrix();
//					test.minHashMatrix();
//					files = test.allDocs();
//					writer.append(execute(test, files, epsilon));
//				}
//				writer.append("\n\n");
//			}
//			writer.close();			
//		}catch(FileNotFoundException e){
//			e.printStackTrace();
//		}
	}

	/**
	 * Helper Method.
	 * @param minHash
	 * @param files
	 * @param epsilon
	 * @return
	 */
	private static String execute(MinHash minHash,String[] files,double epsilon){
		int count = 0;
		int count004 = 0;
		int count007 = 0;
		int count009 = 0;
		for (int i = 0; i < files.length; i++) {
			String file1 = files[i];
			if (file1.equals(".DS_Store"))
				continue;
			for (int j = i + 1; j < files.length; j++) {
				String file2 = files[j];
				double exactJaccard = minHash.exactJaccard(file1, file2);
				double approxJaccard = minHash.approximateJaccard(file1, file2);
				if (Math.abs(exactJaccard - approxJaccard) > epsilon) {
					count++;
				}
				if (Math.abs(exactJaccard - approxJaccard) > 0.04)
					count004++;
				if (Math.abs(exactJaccard - approxJaccard) > 0.07)
					count007++;
				if (Math.abs(exactJaccard - approxJaccard) > 0.09)
					count009++;
			}
		}

		System.out.println("Number of Permutataions: " + minHash.numPermutations() + "; given error parameter: "
				+ epsilon);
		System.out.println("Number of pairs |exactJac-approximateJac|>given error parameter: " + count);
		String result=minHash.numPermutations()+";"+count004+";"+count007+";"+count009+"\n";
		return result;
	}
}
