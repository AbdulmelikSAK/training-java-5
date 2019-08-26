/* Michael PÉRIN, Verimag / Univ. Grenoble Alpes, december 2017 */

public final class Counter {
	static int counter = 0;
	
	public static int next() {
		int n = counter;
		counter++;
		return n;
	}
}