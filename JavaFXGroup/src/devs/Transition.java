package devs;


import javafx.scene.shape.Line;

/**
 * Une transition entre deux états.
 * @author Julian
 *
 */
public class Transition {
	/**
	 * Les états de source et de destination.
	 */
	private DevsState src,dest;
	/**
	 * L'évènement associé à la transition.
	 */
	private Object event;
	/**
	 * L'action associé à la transition.
	 */
	private Object action;
	/**
	 * La ligne qui représente la transition graphiquement.
	 */
	private Line line;
	
	public Transition(DevsState src,DevsState dest,Line line){
		this.src=src;
		this.dest=dest;
		this.line=line;
		event=new String();
		action=new String();
	}
	

	public Object getEvent() {
		return event;
	}


	public Object getAction() {
		return action;
	}


	public void setEvent(Object events) {
		this.event = events;
	}


	public void setAction(Object actions) {
		this.action = actions;
	}


	public DevsState getSrc() {
		return src;
	}

	public void setSrc(DevsState src) {
		this.src = src;
	}

	public DevsState getDest() {
		return dest;
	}

	public void setDest(DevsState dest) {
		this.dest = dest;
	}

	public Line getLine() {
		return line;
	}

	public void setLine(Line line) {
		this.line = line;
	}
	
}
