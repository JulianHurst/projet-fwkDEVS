package models;

import DEVSModel.DEVSAtomic;
import DEVSModel.Port;

public class Not extends DEVSAtomic {

	Port in, out;
	int a;
	float duration;
	final float  delay = 2;
	
	
	public Not(String name){
		super();
		this.name = name;
		this.in = new Port(this, "in");
		this.out = new Port(this, "out");
		this.addInPort(this.in);
		this.addOutPort(this.out);		
	}
	@Override
	public void init() {
		// TODO Auto-generated method stub
		duration = Float.MAX_VALUE;
		a = Or.x;
	}

	@Override
	public void deltaExt(Port p, Object ev, float e) {
		// TODO Auto-generated method stub
		int b;
		a = (int)ev;
		if(a < Or.x){
			b = (a+1)%2;
			a = b; 
		}
		duration = delay;
	}

	@Override
	public void deltaInt() {
		// TODO Auto-generated method stub
		duration = Float.MAX_VALUE;
	}

	@Override
	public Object[] lambda() {
		// TODO Auto-generated method stub
		Object[] output = new Object[2];
		
		
		output[0] = this.out;
		output[1] = a;
		
		
		return output;
	}

	@Override
	public float getDuration() {
		// TODO Auto-generated method stub
		return duration;
	}

}
