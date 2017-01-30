import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class testExactJac {

	public static void main(String[] args) throws FileNotFoundException {
		MinHash test = new MinHash("space", 800);
		test.binaryFreqMatrix();
		String[] files = test.allDocs();
		File outputFile=new File("exactJacsW");
//		File outputFile=new File("exactJacsS");
		PrintWriter writer=new PrintWriter(outputFile);
		int compare=0;
		int count=0;
		for (int i = 0; i < files.length; i++) {			
			String file1 = files[i];
			if (file1.equals(".DS_Store"))
				continue;
			for (int j = i + 1; j < files.length; j++) {
				compare++;
				String file2 = files[j];
				double exactJaccard = test.exactJaccard(file1, file2);
				if(exactJaccard>0.1 && exactJaccard<0.105){
					//System.out.println(file1+" "+file2+" "+exactJaccard);
					writer.println(file1+" "+file2+" "+exactJaccard);
					count++;
				}
			}
		}
		writer.close(); 
		System.out.println(count+"/"+compare);
	}
}
