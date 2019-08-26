/* Michael PÉRIN, Verimag / Univ. Grenoble Alpes, december 2017 */


public class State {
	private int id ;

	State(){
		this.id = Counter.next();
	}
	
	public int id() {
		return this.id;
	}
	
	public String toString() {
		return (" " + this.id + " ") ;
	}
}
