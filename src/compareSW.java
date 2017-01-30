import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

public class compareSW {

	public static void main(String[] args) throws FileNotFoundException {
		File fileS=new File("exactJacsS");
		File fileW=new File("exactJacsW");
		Scanner scan1=new Scanner(fileS);
		HashSet<String> setS=new HashSet<String>();
		while(scan1.hasNextLine()){
			String[] elements=scan1.nextLine().split(" ");
			setS.add(elements[0]+","+elements[1]);
		}
		scan1.close();
		Scanner scan2=new Scanner(fileW);
		int count=0;
		while(scan2.hasNextLine()){
			String[] elements=scan2.nextLine().split(" ");
			String files=elements[0]+","+elements[1];
			if(setS.contains(files)){
				System.out.println(files);
				count++;
			}
		}
		scan2.close();
		System.out.println(count);
	}

}
