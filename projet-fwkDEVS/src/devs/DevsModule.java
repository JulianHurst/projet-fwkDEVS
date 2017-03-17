package devs;

import java.util.LinkedHashSet;
import java.util.Set;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import util.Util;

/**
 * Un module (couple incomplet).
 * @author juju
 *
 */
public class DevsModule extends DevsObject {

	/**
	 * Le nom de l'objet (Nom de la classe).
	 */
	private String objectName;	
	
	/**
	 * Constructeur d'un module (couple incomplet).
	 * @param name Le nom du module.
	 * @param rect Le rectangle qui représente le module.
	 */
	public DevsModule(String name,String objectName){
		this.name=new Text(name);
		this.objectName=objectName;
		this.shape=new Rectangle(200,100);
		shape.setStroke(Color.BLUE);
		shape.setFill(Color.WHITE);
		transitions=new LinkedHashSet<>();
		ports=new LinkedHashSet<>();
	}

	/**
	 * Ajoute une transition si elle n'existe pas déjà où si il n'y en a pas avec le même évènement.
	 * @param transition La transition à ajouter.
	 * @return True si l'ajout est effectué avec succès, false sinon.
	 */
	public boolean addTransition(Transition transition){
		/*for(Transition trans : transitions){
			/*
			if(transition.getEvent().toString().equals(trans.getEvent().toString())) 
				return false;
				
		}*/
		return transitions.add(transition);
	}
	

	/**
	 * Ajoute un port si il n'existe pas déjà pour ce module. 
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
	 * Met à jour les ports selon un autre ensemble de ports.
	 * @param ports Un ensemble de ports.
	 */
	public void updatePorts(Set<Port> ports){
		int size=this.ports.size();
		Set<Port> pendingPorts=new LinkedHashSet<>();
		if(size<ports.size()){
			for(Port port : ports){
				if(!Util.includes(this.ports,port))
					this.ports.add(port);
			}
		}
		else if(size>ports.size()){
			for(Port port : this.ports){
				if(!Util.includes(ports, port))
					pendingPorts.add(port);
			}
			this.ports.removeAll(pendingPorts);
		}
	}
	
	/**
	 * Met à jour les ports d'entrée selon un ensemble de ports d'entrée.
	 * @param ports Un ensemble de ports.
	 */
	public void updateInPorts(Set<Port> ports){
		Set<Port> inputPorts=Util.getInPorts(ports);
		int size=getInputPorts().size();
		Set<Port> pendingPorts=new LinkedHashSet<>();
		if(size<inputPorts.size()){
			for(Port port : inputPorts){
				if(!Util.includes(this.ports,port))
					this.ports.add(port);
			}
		}
		else if(size>inputPorts.size()){
			for(Port port : getInputPorts()){
				if(!Util.includes(inputPorts, port))
					pendingPorts.add(port);
			}
			this.ports.removeAll(pendingPorts);
		}
	}
	
	/**
	 * Met à jour les ports de sortie selon un ensemble de ports de sortie.
	 * @param ports Un ensemble de ports.
	 */
	public void updateOutPorts(Set<Port> ports){
		Set<Port> outputPorts=Util.getOutPorts(ports);
		int size=this.getOutputPorts().size();
		Set<Port> pendingPorts=new LinkedHashSet<>();
		if(size<outputPorts.size()){
			for(Port port : outputPorts){
				if(!Util.includes(this.ports,port))
					this.ports.add(port);
			}
		}
		else if(size>outputPorts.size()){
			for(Port port : getOutputPorts()){
				if(!Util.includes(outputPorts, port))
					pendingPorts.add(port);
			}
			this.ports.removeAll(pendingPorts);
		}
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

}
