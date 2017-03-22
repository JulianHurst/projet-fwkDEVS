package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import DEVSModel.DEVSAtomic;
import DEVSModel.DEVSCoupled;
import DEVSSimulator.Root;
import devs.DevsCouple;
import devs.DevsEnclosing;
import devs.DevsModel;
import devs.DevsModule;
import devs.DevsObject;
import devs.Port;
import devs.Transition;

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
	 * Récupère les ports du couple spécifié.
	 * @param model Le nom de la classe qui réfère au couple.
	 * @return L'ensemble des ports.
	 * @throws InstantiationException Lancée si newInstance échoue.
	 * @throws IllegalAccessException Lancée si l'accès à une méthode particulière n'est pas authorisé.
	 * @throws IllegalArgumentException Lancée si l'argument du constructeur n'est pas valide.
	 * @throws InvocationTargetException Lancée si une exception est lancée dans le contructeur appelé.
	 * @throws NoSuchMethodException Lancée si le constructeur spécifié n'est pas trouvé.
	 * @throws SecurityException Lancée si la sécurité est violée (private).
	 * @throws ClassNotFoundException Lancée si la classe demandée n'est pas trouvée.
	 */
	public static Set<Port> getAtomicCouplePorts(DevsObject parent, String model) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException{ 
		Set<Port> result=new LinkedHashSet<>();
		Object thing=Class.forName("couples."+model).newInstance();
		DEVSCoupled a = (DEVSCoupled)thing;
		for(DEVSModel.Port P : a.getInPorts()){
			result.add(new Port(parent,P.getName(),Port.Type.INPUT));
		}
		for(DEVSModel.Port P : a.getOutPorts()){
			result.add(new Port(parent,P.getName(),Port.Type.OUTPUT));
		}
		return result;
	}
	
	/**
	 * Récupère les noms de tous les couples (contenus dans le package couples).
	 * @return Une liste des noms des classes.
	 */
	public static List<String> getAtomicCoupleNames(){
		List<String> result = new ArrayList<>();
		File pack = new File(System.getProperty("user.dir")+"/src/couples");
		if(!pack.exists())
			pack.mkdir();
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
	public static Set<DevsModel> getModels(Set<DevsObject> objects){
		Set<DevsModel> result=new LinkedHashSet<>();
		for(DevsObject obj : objects){
			if(obj.getClass().equals(DevsModel.class))
			result.add((DevsModel)obj);
		}
		return result;
	}
	
	/**
	 * Récupère les modules contenus dans un ensemble d'objets.
	 * @param objects L'ensemble d'objets.
	 * @return L'ensemble des modules.
	 */
	public static Set<DevsModule> getModules(Set<DevsObject> objects){
		Set<DevsModule> result=new LinkedHashSet<>();
		for(DevsObject obj : objects){
			if(obj.getClass().equals(DevsModule.class))
			result.add((DevsModule)obj);
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

	/**
	 * Efface le contenu du dossier output.
	 */
	public static void deleteOutput(){
		File outputFolder = new File(System.getProperty("user.dir")+"/output");
		if(!outputFolder.exists())
			outputFolder.mkdir();
		for(File f : outputFolder.listFiles())
			f.delete();
		System.out.println("output deleted");
	}
	
	/**
	 * Renvoie un liste de résultats obtenus à partir des fichiers Trans0...Transn.
	 * @return La liste des résultats.
	 * @throws IOException Lancée si il y a un problème de lecture du fichier.
	 */
	public static List<Integer> getResults() throws IOException{
		List<Integer> results = new ArrayList<Integer>();
		File outputFolder = new File(System.getProperty("user.dir")+"/output");
		if(!outputFolder.exists())
			outputFolder.mkdir();
		//for(String name : outputFolder.list())
			//while(!isCompletelyWritten(new File(outputFolder,name)));
		for(String name : outputFolder.list()){
			if(name.contains("Trans")){
				BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/output/"+name));
				String line;
				while ((line = reader.readLine()) != null){
					StringTokenizer stringTok = new StringTokenizer(line);
					results.add(Integer.parseInt(stringTok.nextToken()));
				}
				reader.close();
			}
		}
		return results;
	}

	/**
	 * Est censé vérifier que l'écriture d'un fichier soit finie.
	 * @param file Le fichier à vérifier.
	 * @return True si l'écriture du fichier est finie, false sinon.
	 */
	private static boolean isCompletelyWritten(File file) {
	    RandomAccessFile stream = null;
	    try {
	        stream = new RandomAccessFile(file, "rw");
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        if (stream != null) {
	            try {
	                stream.close();
	            } catch (IOException e) {
	            	e.printStackTrace();
	            }
	        }
	    }
	    return false;
	}	
	
	/**
	 * Vérifie si un couple d'un nom donné à été généré.
	 * @param couple Le nom du couple.
	 * @return True si un fichier java à été généré, false sinon.
	 */
	public static boolean isGenerated(String couple){
		File coupleClass = new File("src/couples/"+couple+".java");
		return coupleClass.exists();
	}
	
	/**
	 * Vérifie si un couple d'un nom donné à été compilé.
	 * @param couple Le nom du couple.
	 * @return True si un fichier class à été généré, false sinon.
	 */
	public static boolean isCompiled(String couple){
		File coupleClass = new File("bin/couples/"+couple+".class");
		return coupleClass.exists();
	}

	/**
	 * Compile le couple spécifié.
	 * @param coupleClass Le nom du couple.
	 */
	public static void compile(String coupleClass){
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if(compiler==null){
			System.out.println("Il faut le JDK 1.8 ou au dessus");
			return;
		}			
		compiler.run(null, null, null, "-d",System.getProperty("user.dir")+File.separator+"bin",System.getProperty("user.dir")+File.separator+"src"+File.separator+"couples"+File.separator+coupleClass+".java");
		System.out.println("Couple compiled");
	}
	
	/**
	 * Execute la simulation sur le couple spécifié.
	 * @param couple Le nom du couple.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static void execute(String couple,int simulationTime) throws InstantiationException, IllegalAccessException, ClassNotFoundException{ 
		//File bin = new File(System.getProperty("user.dir")+"/src");
		//URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { bin.toURI().toURL() });
		//DEVSCoupled execCouple = (DEVSCoupled)Class.forName("gen."+couple.getName().getText(),true,classLoader).newInstance();

		deleteOutput();
		DEVSCoupled execCouple = (DEVSCoupled)Class.forName("couples."+couple).newInstance();
		Root root = new Root(execCouple,simulationTime);
		root.startSimulation();
		System.out.println("Couple executed");
	}
	
	/**
	 * Vérifie si le couple donné est valide, c'est-à-dire que tous les ports sont reliés.
	 * @param couple Le couple à vérifier.
	 * @return True si le couple est valide, false sinon.
	 */
	public static boolean isValid(DevsCouple couple){
		Set<Port> ports=new LinkedHashSet<>();
		Set<Transition> transitions=new LinkedHashSet<>();
		boolean result=false;
		for(DevsObject obj : couple.getModels()){
			ports.addAll(obj.getPorts());
			transitions.addAll(obj.getTransitions());
		}
		for(Port port : ports){
			result=false;
			for(Transition transition : transitions){
				if(port.equals(transition.getSrc()) || port.equals(transition.getDest()))
					result=true;
			}
			if(!result)
				return false;
		}
		return result;
	}
}
