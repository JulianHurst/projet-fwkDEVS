package models;

import DEVSModel.DEVSAtomic;
import DEVSModel.Port;

public class Or extends DEVSAtomic{
	public Port in0, in1, out;
	public static final int x = 2;
	
	
	int a, b, y, y_next;
	
	float duration;
	
	public Or(String name){
		
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
		a = x;
		b = x;
		
		y = x;
		y_next = y;
		duration = Float.MAX_VALUE;
	}

	@Override
	public void deltaExt(Port p, Object ev, float e) {
		// TODO Auto-generated method stub
		if(p.equals(in0)){
			
			a = (int)ev;		
			
		}
		else {
			
			b = (int)ev;
			
		}
		duration = 0;
		//y_next = Math.max(a, b);
		if(a == 1 || b == 1){
			y_next = 1;
		}
		else if ( a == Or.x || b == Or.x){
			y_next = x;
		}
		else {
			y_next = 0;
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
		if(y_next < Or.x)
			System.out.print("--" + this.name +"("  + y_next + ")");
		else 
			System.out.print("--" + this.name +"(x)");
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

}
