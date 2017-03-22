package devs;

import java.util.LinkedHashSet;
import java.util.Set;

import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

/**
 * La classe qu'étendent les objets essentiels à la simulation DEVS.
 * @author juju
 *
 */
public abstract class DevsObject {
	/**
	 * Le nom de l'objet.
	 */
	protected Text name;
	/**
	 * L'ensemble des transitions sans répétitions.
	 */
	protected Set<Transition> transitions;
	/**
	 * La forme qui représente l'objet graphiquement.
	 */
	protected Shape shape;
	/**
	 * Les ports d'entrée et/ou de sortie de l'objet.
	 */
	protected Set<Port> ports;
	
	public Text getName() {
		return name;
	}
	public void setName(Text name) {
		this.name = name;
	}
	public void setName(String name){
		this.name.setText(name);
	}
	public Set<Transition> getTransitions() {
		return transitions;
	}
	public void setTransitions(Set<Transition> transitions) {
		this.transitions = transitions;
	}
	public Shape getShape() {
		return shape;
	}
	public void setShape(Shape shape) {
		this.shape = shape;
	}
	public Set<Port> getPorts() {
		return ports;
	}
	public void setPorts(Set<Port> ports) {
		this.ports = ports;
	}
	
	/**
	 * Récupère les ports d'entrée.
	 * @return Un Set contenant les ports d'entrée.
	 */
	public Set<Port> getInputPorts(){
		Set<Port> inputPorts=new LinkedHashSet<>();
		for(Port p : ports){
			if(p.getType().equals(Port.Type.INPUT)){
				inputPorts.add(p);
			}
		}
		return inputPorts;
	}

	/**
	 * Récupère les ports de sortie.
	 * @return Un Set contenant les ports de sortie.
	 */
	public Set<Port> getOutputPorts(){
		Set<Port> outputPorts=new LinkedHashSet<>();
		for(Port p : ports){
			if(p.getType().equals(Port.Type.OUTPUT)){
				outputPorts.add(p);
			}
		}
		return outputPorts;
	}
	
	public abstract boolean addTransition(Transition transition); 
	
	
	
	
}
