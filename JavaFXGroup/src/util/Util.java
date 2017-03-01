package util;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Set;

import DEVSModel.DEVSAtomic;
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
	public static Set<Port> getAtomicModelPorts(String model) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException{ 
		Set<Port> result=new LinkedHashSet<>();
		Object thing=Class.forName("models."+model).getDeclaredConstructor(String.class).newInstance(model);
		DEVSAtomic a = (DEVSAtomic)thing;
		for(DEVSModel.Port P : a.inPorts){
			result.add(new Port(P.getName(),Port.Type.INPUT));
		}
		for(DEVSModel.Port P : a.outPorts){
			result.add(new Port(P.getName(),Port.Type.OUTPUT));
		}
		return result;
	}
	
}
