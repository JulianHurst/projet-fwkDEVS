package devs;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 * Représente un port entrant ou sortant.
 * @author juju
 *
 */
public class Port {
	/**
	 * Le nom du port (in0,...,inn ou out0,...,outn).
	 */
	private Text name;
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
		this.name=new Text(name);
		this.type=type;
		circle=new Circle(10.0f);
		circle.setFill(Color.WHITE);
		circle.setStroke(Color.BLACK);
	}

	public Text getName() {
		return name;
	}

	public void setName(Text name) {
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
	
	public String toString(){
		return "name : "+name.getText()+" type : "+type;
	}
	
	/*@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name.getText() == null) ? 0 : name.getText().hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		System.out.println("equals");
		if (this == obj){
		System.out.println("equalsthis");
			return true;
		}
		if (obj == null){
		System.out.println("equalsnull");
			return false;
		}
		if (getClass() != obj.getClass()){
		System.out.println("equalsn");
			return false;
		}
		Port other = (Port) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.getText().equals(other.name.getText())){
			System.out.println("equals : "+name.getText()+" "+other.name.getText());
			return false;
		}
		if (type != other.type){
			System.out.println("equals : "+type+" "+other.type);
			return false;
		}
		System.out.println("equaltrue");
		return true;
	}*/
}
