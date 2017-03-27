package codegen;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;

import DEVSModel.DEVSCoupled;
import devs.DevsCouple;
import devs.DevsEnclosing;
import devs.DevsModel;
import devs.DevsModule;
import devs.DevsObject;
import devs.Port;
import devs.Transition;
import gate.Generator;
import gate.Transducer;
import util.Util;

public class CodeGenerator {
	JCodeModel codeModel = new JCodeModel();
	JPackage jp;
	
	public CodeGenerator(){
		jp = codeModel._package("couples");
	}
	
	/**
	 * Génère le code du couple représenté par un ensemble d'objets.
	 * @param coupleName Le nom du couple.
	 * @param objects L'ensemble d'objets.
	 * @throws JClassAlreadyExistsException Lancée si la classe existe déjà.
	 * @throws IOException Lancée si un problème survient lors de l'écriture.
	 * @throws ClassNotFoundException Lancée si une classe n'est pas trouvée.
	 */
	public void generateCouple(String coupleClassName,Set<DevsObject> objects,DevsCouple couple) throws JClassAlreadyExistsException, IOException, ClassNotFoundException{
		JDefinedClass coupleClass = jp._class(coupleClassName);
		coupleClass._extends(DEVSCoupled.class);
		
		JMethod constructor = coupleClass.constructor(JMod.PUBLIC);
		JBlock constructorBody = constructor.body();
		
		for(DevsModel model : Util.getModels(objects)){
			coupleClass.field(JMod.PRIVATE, Class.forName("models."+model.getObjectName()), model.getName().getText());
			constructorBody.assign(JExpr.ref(model.getName().getText()), JExpr._new(codeModel._ref(Class.forName("models."+model.getObjectName()))).arg(model.getName().getText()));
			constructorBody.invoke(JExpr._this().invoke("getSubModels"),"add").arg(JExpr.ref(model.getName().getText()));
		}
		for(DevsEnclosing enclosing : Util.getEnclosing(objects)){
			if(enclosing.getType().equals(DevsEnclosing.Type.GEN)){
				coupleClass.field(JMod.PRIVATE, Generator.class, enclosing.getName().getText());
				constructorBody.assign(JExpr.ref(enclosing.getName().getText()), JExpr._new(codeModel._ref(Generator.class)).arg(enclosing.getName().getText()));
				constructorBody.invoke(JExpr._this().invoke("getSubModels"),"add").arg(JExpr.ref(enclosing.getName().getText()));
			}
			else{
				coupleClass.field(JMod.PRIVATE, Transducer.class, enclosing.getName().getText());
				constructorBody.assign(JExpr.ref(enclosing.getName().getText()), JExpr._new(codeModel._ref(Transducer.class)).arg(enclosing.getName().getText()));
				constructorBody.invoke(JExpr._this().invoke("getSubModels"),"add").arg(JExpr.ref(enclosing.getName().getText()));
			}
		}
		for(DevsModule module : Util.getModules(objects)){
			coupleClass.field(JMod.PRIVATE, Class.forName("couples."+module.getObjectName()), module.getName().getText());
			constructorBody.assign(JExpr.ref(module.getName().getText()), JExpr._new(codeModel._ref(Class.forName("couples."+module.getObjectName()))));
			constructorBody.invoke(JExpr._this().invoke("getSubModels"),"add").arg(JExpr.ref(module.getName().getText()));
		}
		
		
		
		for(DevsEnclosing enclosing : Util.getEnclosing(objects)){
			for(Transition transition : enclosing.getTransitions()){
				if(enclosing.getType().equals(DevsEnclosing.Type.GEN)){
					JInvocation linkGenToSm = constructorBody.invoke(JExpr._this(),"addIC");
					DevsObject dest = transition.getDest().getParent();
					linkGenToSm.arg(JExpr.ref(enclosing.getName().getText()).invoke("getOutPorts").invoke("get").arg(JExpr.lit(0)));
					linkGenToSm.arg(JExpr.ref(dest.getName().getText()).invoke("getInPorts").invoke("get").arg(JExpr.lit(Util.findPortId(transition.getDest(),objects))));
				}
			}
		}
	
		Set<DevsObject> modelsModules=new LinkedHashSet<>();
		modelsModules.addAll(Util.getModels(objects));
		modelsModules.addAll(Util.getModules(objects));
		
		for(DevsObject model : modelsModules){
			for(Transition transition : model.getTransitions()){
				if(!transition.getDest().getParent().getClass().equals(DevsCouple.class)){
					JInvocation linkGenToSm = constructorBody.invoke(JExpr._this(),"addIC");
					linkGenToSm.arg(JExpr.ref(model.getName().getText()).invoke("getOutPorts").invoke("get").arg(JExpr.lit(Util.findPortId(transition.getSrc(), objects))));
					linkGenToSm.arg(JExpr.ref(transition.getDest().getParent().getName().getText()).invoke("getInPorts").invoke("get").arg(JExpr.lit(Util.findPortId(transition.getDest(), objects))));
				}
			}
		}

		for(Port p : couple.getInputPorts()){
			coupleClass.field(JMod.PRIVATE, DEVSModel.Port.class, p.getName().getText());
			constructorBody.assign(JExpr.ref(p.getName().getText()), JExpr._new(codeModel._ref(DEVSModel.Port.class)).arg(JExpr._this()).arg(p.getName().getText()));
			constructorBody.invoke(JExpr._this(),"addInPort").arg(JExpr.ref(p.getName().getText()));
		}
		for(Port p : couple.getOutputPorts()){
			coupleClass.field(JMod.PRIVATE, DEVSModel.Port.class, p.getName().getText());
			constructorBody.assign(JExpr.ref(p.getName().getText()), JExpr._new(codeModel._ref(DEVSModel.Port.class)).arg(JExpr._this()).arg(p.getName().getText()));
			constructorBody.invoke(JExpr._this(),"addOutPort").arg(JExpr.ref(p.getName().getText()));
		}
		for(DevsObject obj : objects){
			for(Transition transition : obj.getTransitions()){
				if(couple.getPorts().contains(transition.getSrc())){
					if(transition.getSrc().getType().equals(Port.Type.INPUT)){
						JInvocation linkGenToSm = constructorBody.invoke(JExpr._this(),"addEIC");
						linkGenToSm.arg(JExpr.invoke("getInPort").arg(transition.getSrc().getName().getText()));
						linkGenToSm.arg(JExpr.ref(transition.getDest().getParent().getName().getText()).invoke("getInPorts").invoke("get").arg(JExpr.lit(Util.findPortId(transition.getDest(), objects))));						
					}
					else{
						JInvocation linkGenToSm = constructorBody.invoke(JExpr._this(),"addEOC");
						linkGenToSm.arg(JExpr.ref(transition.getDest().getParent().getName().getText()).invoke("getOutPorts").invoke("get").arg(JExpr.lit(Util.findPortId(transition.getDest(), objects))));
						linkGenToSm.arg(JExpr.invoke("getOutPort").arg(transition.getSrc().getName().getText()));						
					}
				}
				if(couple.getPorts().contains(transition.getDest())){
					if(transition.getSrc().getType().equals(Port.Type.INPUT)){
						JInvocation linkGenToSm = constructorBody.invoke(JExpr._this(),"addEIC");
						linkGenToSm.arg(JExpr.invoke("getInPort").arg(transition.getDest().getName().getText()));
						linkGenToSm.arg(JExpr.ref(transition.getSrc().getParent().getName().getText()).invoke("getInPorts").invoke("get").arg(JExpr.lit(Util.findPortId(transition.getSrc(), objects))));						
					}
					else{
						JInvocation linkGenToSm = constructorBody.invoke(JExpr._this(),"addEOC");
						linkGenToSm.arg(JExpr.ref(transition.getSrc().getParent().getName().getText()).invoke("getOutPorts").invoke("get").arg(JExpr.lit(Util.findPortId(transition.getSrc(), objects))));
						linkGenToSm.arg(JExpr.invoke("getOutPort").arg(transition.getDest().getName().getText()));						
					}
				}
					
					
					
			}
		}
		
		
		JMethod setSelectPriority = coupleClass.method(JMod.PUBLIC, void.class, "setSelectPriority");
		setSelectPriority.annotate(Override.class);
		setSelectPriority.body()._return();
		codeModel.build(new File(System.getProperty("user.dir")+"/src"));
	}
	
}
