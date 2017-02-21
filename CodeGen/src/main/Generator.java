package main;

import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.StringTokenizer;
import java.util.Vector;

import DEVSModel.DEVSAtomic;
import DEVSModel.Port;

public class Generator extends DEVSAtomic {

	Port out;
	float duration, time_init = 3;
	Vector<float[]> Out_Gen; // Out Generator
	//float[] out = new float[2];
	float TimeLast, time;
	String name_file;
	
	public Generator() {
		
		
	}
	
	public Generator(String name){
		
		float a, c_time;
		boolean y;
		String out_str; // out_string
		StringTokenizer str_tok;
	
		
		this.name = name;
		Out_Gen = new Vector<float[]>();
		float[] a_b ;
		
		
		this.name_file = "input/" + name + ".gate";
		//System.out.println("file:" + this.name_file);
		out = new Port(this, "out");		
		this.addOutPort(out);
		
		try{
			  BufferedReader entree = new BufferedReader(new
					FileReader (name_file));
			  
			  out_str = entree.readLine();//lit une ligne;fin de fichier->null
			  while(out_str != null) {				  
				  str_tok = new StringTokenizer(out_str);
				  //  y = Boolean.parseBoolean(str_tok.nextToken());
				  a = Float.parseFloat(str_tok.nextToken());
				  c_time = Float.parseFloat(str_tok.nextToken());
				  a_b = new float[2];
				  a_b[0] = a;				  
				  a_b[1] = c_time;
				  Out_Gen.add(a_b);
				  out_str = entree.readLine();
			  }
			  
			  entree.close();
			}
			catch (FileNotFoundException e){
			  System.err.println("fichier non trouvé !");
			} 
			catch (IOException e){
			    //IOException englobe FileNotFoundException
			    // on la capte donc apr�s dans l'ordre des clause catch 
			}
			
			
			
		
			for(int i = 0; i < Out_Gen.size(); i++) {
				
				//System.out.println(" a - b " + Out_Gen.elementAt(i) [0] + " " +
				//		Out_Gen.elementAt(i) [1]);
			}

		
	}
	
	public Generator(String name_file, String name){
		float a, c_time;
		boolean y;
		String out_str; // out_string
		StringTokenizer str_tok;
	
		
		this.name = name;
		Out_Gen = new Vector<float[]>();
		float[] a_b ;
		
		this.name_file = name_file;
		
		out = new Port(this, "out");		
		this.addOutPort(out);
		
		try{
			  BufferedReader entree = new BufferedReader(new
					FileReader (name_file));
			  
			  out_str = entree.readLine();//lit une ligne;fin de fichier->null
			  while(out_str != null) {				  
				  str_tok = new StringTokenizer(out_str);
				  //  y = Boolean.parseBoolean(str_tok.nextToken());
				  a = Float.parseFloat(str_tok.nextToken());
				  c_time = Float.parseFloat(str_tok.nextToken());
				  a_b = new float[2];
				  a_b[0] = a;				  
				  a_b[1] = c_time;
				  Out_Gen.add(a_b);
				  out_str = entree.readLine();
			  }
			  
			  entree.close();
			}
			catch (FileNotFoundException e){
			  System.err.println("fichier non trouvé !");
			} 
			catch (IOException e){
			    //IOException englobe FileNotFoundException
			    // on la capte donc apr�s dans l'ordre des clause catch 
			}
			
			
			
		
			for(int i = 0; i < Out_Gen.size(); i++) {
				
				System.out.println(" a - b " + Out_Gen.elementAt(i) [0] + " " +
						Out_Gen.elementAt(i) [1]);
			}
		
	}
	@Override
	public void deltaExt(Port p, Object ev, float e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deltaInt() {
		// TODO Auto-generated method stub
		
		
		
		
		TimeLast = ((float[])Out_Gen.get(0))[1];
		
		Out_Gen.remove(0);
		if(Out_Gen.size() != 0){			
			duration = ((float[])Out_Gen.get(0))[1] - TimeLast ;
		}
		else {
			duration = Float.MAX_VALUE;
		}
		
	}

	@Override
	public Object[] lambda() {
		// TODO Auto-generated method stub
		Object[] output = new Object[2];
		output[0] = out;
		
		float a1, b1, b2;
		
		output[0] = out;
		output[1] = (int)((float[])Out_Gen.get(0))[0];
		/*
		if (((float[])Out_Gen.get(0))[0] != 0){
			
			output[1] = true;
		}
		else{
			output[1] = false;
		}
		*/
		
		/*
		if(name_file.equals("scenario_1.txt"))
			System.out.print("--sr(in0="  + output[1] + ")" );
		else 
			System.out.print("--sr(in1="  + output[1] + ")" );
		*/
		return output;
	}

	@Override
	public float getDuration() {
		// TODO Auto-generated method stub
		return duration;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		duration = ((float[])Out_Gen.get(0))[1];
		//System.out.println(this.toString());
	}

	public void set_time_init(float time) {
		this.time_init = time;
	}
	public String toString(){
		return (this.getClass().getSimpleName() + this.hashCode() + " - " + this.name_file);
	}
}
