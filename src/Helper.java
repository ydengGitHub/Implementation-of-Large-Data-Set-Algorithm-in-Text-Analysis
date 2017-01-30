import java.util.Random;

/**
 * Including the static Helper methods, such as isPrime(), nextPrime(),
 * nextRan()
 * 
 * @author YAN DENG
 *
 */
public class Helper {
	
	private long startTime;
	private long endTime;
	private boolean ended = false;
	private long usedTime;
		
	/**
	 * Check if the given integer is a prime or not.
	 * 
	 * @param num
	 * @return true: is a prime; false: else.
	 */
	public static boolean isPrime(int num) {
		if (num == 2)
			return true;
		if (num % 2 == 0)
			return false;
		for (int i = 3; i * i <= num; i += 2)
			if (num % i == 0)
				return false;
		return true;
	}

	/**
	 * Find the first prime bigger than the given integer.
	 * 
	 * @param num
	 * @return a prime number
	 */
	public static int nextPrime(int num) {
		int nextPrime = num;
		if (nextPrime % 2 == 0)
			nextPrime += 1;

		while (!isPrime(nextPrime)) {
			nextPrime += 2;
		}
		// System.out.println("Next prime is " + nextPrime);
		return nextPrime;
	}

	/**
	 * Generate a random integer between[1,...,p-1] for the random function's a
	 * and b.
	 * 
	 * @return a random generated integer between[1,...,p-1];
	 */
	public static int generateRan(int num) {
		Random rand = new Random();
		int par = rand.nextInt(num);
		return par;
	}
	
	public void startTimer() {
		ended = false;
		startTime = System.currentTimeMillis();
	}

	public void stopTimer() {
		ended = true;
		endTime = System.currentTimeMillis();
		usedTime = endTime - startTime;
	}

	public long getTime() throws IllegalAccessException {
		if (!ended) {
			throw new IllegalAccessException("Timer is still running.");
		}
		return usedTime;
	}
	
	public static int calculateB(int k, double s){
		int b=1;
		int optimalB=-1;
		double value;
		double bestValue=1;
		for(;b<Math.sqrt(k);b++){
			value=Math.abs(s-(Math.pow(1.0/b, ((double) b)/k)));
			if(value<bestValue){
				optimalB=b;
				bestValue=value;				
			}
		}
		return optimalB;
	}
}
