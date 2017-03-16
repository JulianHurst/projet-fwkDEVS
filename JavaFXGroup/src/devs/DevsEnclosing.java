package devs;

import java.util.LinkedHashSet;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 * Un générateur ou transduceur.
 * @author juju
 *
 */
public class DevsEnclosing extends DevsObject {
	/**
	 * Un type d'objet enclosing.
	 * @author juju
	 *
	 */
	public enum Type{GEN,TRANS}
	/**
	 * Le type du DevsEnclosing
	 */
	private Type type;
	/**
	 * Le nombre de générateurs.
	 */
	public static int GEN_QUANTITY=0;
	/**
	 * Le nombre de transduceurs.
	 */
	public static int TRANS_QUANTITY=0;
	
	public DevsEnclosing(Type T){
		name=new Text();
		transitions=new LinkedHashSet<>();
		ports=new LinkedHashSet<>();
		shape=new Circle(80,Color.WHITE);
		shape.setStroke(Color.BLACK);
		type=T;
		if(type.equals(Type.GEN)){
			ports.add(new Port(this,"out",Port.Type.OUTPUT));
			name.setText("Gen"+GEN_QUANTITY);
			GEN_QUANTITY++;
		}
		else{
			ports.add(new Port(this,"in",Port.Type.INPUT));
			name.setText("Trans"+TRANS_QUANTITY);
			TRANS_QUANTITY++;
		}
	}

	@Override
	public boolean addTransition(Transition transition) {
		return transitions.add(transition);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
