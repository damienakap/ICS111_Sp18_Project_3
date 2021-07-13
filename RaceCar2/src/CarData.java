
/**
 * @author Damien Apilando
 * e-mail: damienakap@gmail.com
 * 
 * ICS111
 * lecturer Jason Leigh
 * 
 * Note:
 * 	This is an object that stores a cars' data.
 * 
 */

public class CarData{
	
	int place, bestLapTime;
	String name;
	
	public CarData(String n, int p, int blt) {
		this.place = p;
		this.bestLapTime = blt;
		this.name = n;
	}
}
