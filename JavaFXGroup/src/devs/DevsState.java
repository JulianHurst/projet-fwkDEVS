package devs;

import java.util.LinkedHashSet;
import java.util.Set;
import util.Util;

import javafx.scene.text.Text;

/**
 * Un état DEVS.
 * @author Julian
 *
 */
public class DevsState extends DevsObject{

	/**
	 * Constructeur d'un état
	 * @param name Le nom de l'état.
	 * @param rect Le rectangle qui représente l'état.
	 */
	public DevsState(String name,StateRect rect){
		this.name=new Text(name);
		this.shape=rect;
		transitions=new LinkedHashSet<>();
		ports=new LinkedHashSet<>();
		ports.add(new Port(this,"in0",Port.Type.INPUT));
		ports.add(new Port(this,"out0",Port.Type.OUTPUT));
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
	
	
}
