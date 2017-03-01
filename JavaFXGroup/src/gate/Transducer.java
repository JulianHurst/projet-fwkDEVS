package gate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import DEVSModel.DEVSAtomic;
import DEVSModel.Port;


public class Transducer extends DEVSAtomic{
	
	private float time = 0;
	private PrintWriter trans;
	public Transducer(String name){
		
		this.name = name;
		this.addInPort(new Port(this, "in"));
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
		try {
			trans = new PrintWriter(new BufferedWriter(new FileWriter("output/"+ this.name)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void deltaExt(Port p, Object ev, float e) {
		// TODO Auto-generated method stub
		this.time += e;
		//System.out.println("receive " + ev);
		this.trans.println(ev + " " + this.time);
	}

	@Override
	public void deltaInt() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] lambda() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getDuration() {
		// TODO Auto-generated method stub
		return Float.POSITIVE_INFINITY;
	}
	
	public void endSim(){
		
		this.trans.close();
	}

}
