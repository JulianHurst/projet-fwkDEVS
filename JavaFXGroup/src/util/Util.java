package util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import DEVSModel.DEVSAtomic;
import devs.DevsEnclosing;
import devs.DevsObject;
import devs.DevsState;
import devs.Port;

public final class Util {
	
	public static Set<Port> getInPorts(Set<Port> ports){
		Set<Port> inputPorts=new LinkedHashSet<>();
		for(Port p : ports){
			if(p.getType().equals(Port.Type.INPUT))
				inputPorts.add(p);
		}
		return inputPorts;
	}
	
	public static Set<Port> getOutPorts(Set<Port> ports){
		Set<Port> outputPorts=new LinkedHashSet<>();
		for(Port p : ports){
			if(p.getType().equals(Port.Type.OUTPUT))
				outputPorts.add(p);
		}
		return outputPorts;
	}
	
	public static boolean includes(Set<Port> ports, Port port){
		for(Port p : ports){
			if(port.getName().getText().equals(p.getName().getText()) && 
					port.getType().equals(p.getType()))
				return true;
		}
		return false;
	}

	/**
	 * Récupère les ports du modèle atomique spécifié.
	 * @param model Le nom de la classe qui réfère au modèle atomique.
	 * @return L'ensemble des ports.
	 * @throws InstantiationException Lancée si newInstance échoue.
	 * @throws IllegalAccessException Lancée si l'accès à une méthode particulière n'est pas authorisé.
	 * @throws IllegalArgumentException Lancée si l'argument du constructeur n'est pas valide.
	 * @throws InvocationTargetException Lancée si une exception est lancée dans le contructeur appelé.
	 * @throws NoSuchMethodException Lancée si le constructeur spécifié n'est pas trouvé.
	 * @throws SecurityException Lancée si la sécurité est violée (private).
	 * @throws ClassNotFoundException Lancée si la classe demandée n'est pas trouvée.
	 */
	public static Set<Port> getAtomicModelPorts(DevsObject parent, String model) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException{ 
		Set<Port> result=new LinkedHashSet<>();
		Object thing=Class.forName("models."+model).getDeclaredConstructor(String.class).newInstance(model);
		DEVSAtomic a = (DEVSAtomic)thing;
		for(DEVSModel.Port P : a.inPorts){
			result.add(new Port(parent,P.getName(),Port.Type.INPUT));
		}
		for(DEVSModel.Port P : a.outPorts){
			result.add(new Port(parent,P.getName(),Port.Type.OUTPUT));
		}
		return result;
	}
	
	/**
	 * Récupère les noms de tous les modèles atomiques (contenus dans le package models).
	 * @return Une liste des noms des classes.
	 */
	public static List<String> getAtomicModelNames(){
		List<String> result = new ArrayList<>();
		File pack = new File(System.getProperty("user.dir")+"/src/models");
		for(String file : pack.list())
			result.add(file.replace(".java", ""));
		return result;
	}

	/**
	 * Récupère les générateurs et/ou transducers contenus dans un ensemble de DevsObjects.
	 * @param objects L'ensemble d'objets. 
	 * @return L'ensemble des générateurs et/ou transducers.
	 */
	public static Set<DevsEnclosing> getEnclosing(Set<DevsObject> objects){
		Set<DevsEnclosing> result=new LinkedHashSet<>();
		for(DevsObject obj : objects){
			if(obj.getClass().equals(DevsEnclosing.class))
			result.add((DevsEnclosing)obj);
		}
		return result;
	}
	
	/**
	 * Récupère les modèles atomiques contenus dans un ensemble d'objets.
	 * @param objects L'ensemble d'objets.
	 * @return L'ensemble des modèles atomiques.
	 */
	public static Set<DevsState> getModels(Set<DevsObject> objects){
		Set<DevsState> result=new LinkedHashSet<>();
		for(DevsObject obj : objects){
			if(obj.getClass().equals(DevsState.class))
			result.add((DevsState)obj);
		}
		return result;
	}
	
	/**
	 * Récupère le numéro du port spécifié pour un objet contenu dans un ensemble d'objets spécifié.
	 * @param p Le port.
	 * @param objects L'ensemble d'objets.
	 * @return Le numéro du port s'il est trouvé, -1 sinon.
	 */
	public static int findPortId(Port p,Set<DevsObject> objects){
		int result=0;
		for(DevsObject obj : objects){
			Set<Port> ports;
			if(p.getType().equals(Port.Type.INPUT))
				ports=obj.getInputPorts();
			else
				ports=obj.getOutputPorts();
			Iterator<Port> it = ports.iterator();
			while(it.hasNext()){
				if(p.equals(it.next()))
					return result;
				result++;
			}
			result=0;
		}
		return -1;
	}
	
}
