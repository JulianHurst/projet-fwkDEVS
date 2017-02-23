package devs;

import java.util.LinkedHashSet;

import java.util.Set;

import javafx.scene.text.Text;

/**
 * Un état DEVS.
 * @author Julian
 *
 */
public class DevsState{
	/**
	 * Le nom de l'état
	 */
	private Text name;
	/**
	 * L'ensemble des transitions sans répétitions.
	 */
	private Set<Transition> transitions;
	/**
	 * Le rectangle qui représente l'état graphiquement.
	 */
	private StateRect rect;
	/**
	 * Les ports d'entrée et de sortie de l'état.
	 */
	private Set<Port> ports;

	/**
	 * Constructeur d'un état
	 * @param name Le nom de l'état.
	 * @param rect Le rectangle qui représente l'état.
	 */
	public DevsState(String name,StateRect rect){
		this.name=new Text(name);
		this.rect=rect;
		transitions=new LinkedHashSet<>();
		ports=new LinkedHashSet<>();
		ports.add(new Port("in0",Port.Type.INPUT));
		ports.add(new Port("out0",Port.Type.OUTPUT));
	}

	/**
	 * Ajoute une transition si elle n'existe pas déjà où si il n'y en a pas avec le même évènement.
	 * @param transition La transition à ajouter.
	 * @return True si l'ajout est effectué avec succès, false sinon.
	 */
	public boolean addTransition(Transition transition){
		for(Transition trans : transitions)
			if(transition.getEvent().toString().equals(trans.getEvent().toString()))
				return false;
		return transitions.add(transition);
	}
	
	public Text getName() {
		return name;
	}

	public void setName(Text name) {
		this.name = name;
	}
	
	public void setName(String name){
		this.name.setText(name);
	}

	public StateRect getRect() {
		return rect;
	}

	public void setRect(StateRect rect) {
		this.rect = rect;
	}

	public Set<Transition> getTransitions() {
		return transitions;
	}

	public void setTransitions(Set<Transition> transitions) {
		this.transitions = transitions;
	}

	/**
	 * Ajoute un port si il n'existe pas déjà pour cet état.
	 * @param port Le port à ajouter.
	 * @return True si l'ajout est effectué avec succès, false sinon.
	 */
	public boolean addPort(Port port){
		for(Port p : ports){
			if(p.getName().equals(port.getName()))
				return false;
		}
		return ports.add(port);
	}
	
	public Set<Port> getPorts() {
		return ports;
	}
	
	/**
	 * Récupère les ports d'entrée.
	 * @return Un Set contenant les ports d'entrée.
	 */
	public Set<Port> getInputPorts(){
		Set<Port> inputPorts=new LinkedHashSet<>();
		for(Port p : ports){
			if(p.getType()==Port.Type.INPUT){
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
			if(p.getType()==Port.Type.OUTPUT){
				outputPorts.add(p);
			}
		}
		return outputPorts;
	}

	public void setPorts(Set<Port> ports) {
		this.ports = ports;
	}
}
