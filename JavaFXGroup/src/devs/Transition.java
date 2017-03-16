package devs;


import javafx.scene.shape.Line;

/**
 * Une transition entre deux objets.
 * @author Julian
 *
 */
public class Transition {
	/**
	 * Les ports de source et de destination.
	 */
	private Port src,dest;
	/**
	 * La ligne qui représente la transition graphiquement.
	 */
	private Line line;
	
	public Transition(Port src,Port dest,Line line){
		this.src=src;
		this.dest=dest;
		this.line=line;
	}
	
	public Port getSrc() {
		return src;
	}

	public void setSrc(Port src) {
		this.src = src;
	}

	public Port getDest() {
		return dest;
	}

	public void setDest(Port dest) {
		this.dest = dest;
	}

	public Line getLine() {
		return line;
	}

	public void setLine(Line line) {
		this.line = line;
	}
	
	public String toString(){
		return "src : "+src.toString()+" dest : "+dest.toString();
	}
	
}
