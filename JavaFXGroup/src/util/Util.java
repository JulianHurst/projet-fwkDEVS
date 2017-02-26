package util;

import java.util.LinkedHashSet;
import java.util.Set;

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

}
