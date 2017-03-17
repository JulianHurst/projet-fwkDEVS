package models;

import DEVSModel.DEVSAtomic;
import DEVSModel.Port;

public class And extends DEVSAtomic{

	private static final int x = 2;
	Port in0, in1, out;
	private final float falltime = 2, risetime = 2;
	
	int a, b, y, y_next;
	
	float duration;

	public And(String name){
		
		super();
		this.name = name;
		
		this.in0 = new Port(this, "in0");
		this.in1 = new Port(this, "in1");
		this.out = new Port(this, "out");
		this.addInPort(this.in0);
		this.addInPort(this.in1);
		this.addOutPort(this.out);
		
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
		a = 0;
		b = 0;
		
		y = 3;
		y_next = a*b;
		duration = Float.MAX_VALUE;
	}

	@Override
	public void deltaExt(Port p, Object ev, float e) {
		// TODO Auto-generated method stub
		if(p.equals(this.in0)){
			
			a = (int)ev;		
			
		}
		else {
			
			b = (int)ev;
			
		}
		
		y_next = a*b;
		
		if(y != y_next){
			duration = 0;
		}
		else {
			duration = Float.MAX_VALUE;
		}
		
	}

	@Override
	public void deltaInt() {
		// TODO Auto-generated method stub
		y = y_next;
		duration = Float.MAX_VALUE;
	}

	@Override
	public Object[] lambda() {
		// TODO Auto-generated method stub
		Object[] output = new Object[2];
		
		
		output[0] = this.out;
		output[1] = y_next;
		
		//if(y_next < And.x)
		//	System.out.print("--" + this.getName() +"(out="  + y_next + ")");
		//else 
		//	System.out.print("--" + this.getName() +"(out=x)");
		return output;
	}

	@Override
	public float getDuration() {
		// TODO Auto-generated method stub
		return duration;
	}

	

	public void deltaConfluent(Port p, Object ev, float e){
		
		//this.InternalTransition();
		this.deltaExt(p, ev, e);
	}
	
	
	public String toString(){
		
		return this.getName();
	}
}
