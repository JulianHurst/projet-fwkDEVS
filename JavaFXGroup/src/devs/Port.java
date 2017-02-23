package devs;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Représente un port entrant ou sortant.
 * @author juju
 *
 */
public class Port {
	/**
	 * Le nom du port (in0,...,inn ou out0,...,outn).
	 */
	private String name;
	/**
	 * Un type de port (entrée ou sortie).
	 * @author juju
	 *
	 */
	public enum Type{INPUT,OUTPUT};
	/**
	 * Le type du port. 
	 */
	private Type type;
	/**
	 * Le cercle qui représente le port graphiquement.
	 */
	private Circle circle;
	
	public Port(String name,Type type){
		this.name=name;
		this.type=type;
		circle=new Circle(10.0f);
		circle.setFill(Color.WHITE);
		circle.setStroke(Color.BLACK);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Circle getCircle() {
		return circle;
	}

	public void setCircle(Circle circle) {
		this.circle = circle;
	}
	
}
