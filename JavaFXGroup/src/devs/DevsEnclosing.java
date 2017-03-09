package devs;

import java.util.LinkedHashSet;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class DevsEnclosing extends DevsObject {
	public enum Type{GEN,TRANS}
	private Type type;
	public static int GEN_QUANTITY=0;
	public static int TRANS_QUANTITY=0;
	
	public DevsEnclosing(Type T){
		name=new Text();
		transitions=new LinkedHashSet<>();
		ports=new LinkedHashSet<>();
		shape=new Circle(50,Color.WHITE);
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
