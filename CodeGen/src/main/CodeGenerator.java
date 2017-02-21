package main;

import java.io.File;
import java.io.IOException;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;

import DEVSModel.DEVSAtomic;
import DEVSModel.DEVSCoupled;
import DEVSModel.Port;

public class CodeGenerator {
	JCodeModel codeModel = new JCodeModel();
	JPackage jp;
	JDefinedClass stateMachine;
	
	CodeGenerator(){
		jp = codeModel._package("gen");
	}
	
	public void generateCouple(String coupleName) throws JClassAlreadyExistsException{
		JDefinedClass couple = jp._class(coupleName);
		couple._extends(DEVSCoupled.class);
		couple.field(JMod.PUBLIC, Generator.class, "generator");
		couple.field(JMod.PUBLIC, stateMachine, "stateMachine");
		couple.field(JMod.PUBLIC, Transducer.class, "transducer");
		
		JMethod constructor = couple.constructor(JMod.PUBLIC);
		JBlock constructorBody = constructor.body();
		constructorBody.assign(JExpr.ref("generator"), JExpr._new(codeModel._ref(Generator.class)).arg("in0"));
		constructorBody.assign(JExpr.ref("stateMachine"), JExpr._new(codeModel.ref(stateMachine.name())).arg("stateMachine"));
		constructorBody.assign(JExpr.ref("transducer"), JExpr._new(codeModel._ref(Transducer.class)).arg("Yellow"));
		JInvocation addGen = constructorBody.invoke(JExpr._this().ref("subModels"),"add");
		JInvocation addSm = constructorBody.invoke(JExpr._this().ref("subModels"),"add");
		JInvocation addTrans = constructorBody.invoke(JExpr._this().ref("subModels"),"add");
		addGen.arg(JExpr.ref("generator"));
		addSm.arg(JExpr.ref("stateMachine"));
		addTrans.arg(JExpr.ref("transducer"));
		
		JInvocation linkGenToSm = constructorBody.invoke(JExpr._this(),"addIC");
		JInvocation linkSmToTrans = constructorBody.invoke(JExpr._this(),"addIC");
		linkGenToSm.arg(JExpr.ref("generator").invoke("getOutPorts").invoke("get").arg(JExpr.lit(0)));
		linkGenToSm.arg(JExpr.ref("stateMachine").invoke("getInPorts").invoke("get").arg(JExpr.lit(0)));
		linkSmToTrans.arg(JExpr.ref("stateMachine").invoke("getOutPorts").invoke("get").arg(JExpr.lit(0)));
		linkSmToTrans.arg(JExpr.ref("transducer").invoke("getInPorts").invoke("get").arg(JExpr.lit(0)));
		
		JMethod setSelectPriority = couple.method(JMod.PUBLIC, void.class, "setSelectPriority");
		setSelectPriority.annotate(Override.class);
		setSelectPriority.body()._return();
	}
	
	public void generateStates(String stateName) throws JClassAlreadyExistsException, IOException{
		stateMachine = jp._class(stateName);
		stateMachine.javadoc().add("La classe "+stateName+".");
		stateMachine._extends(DEVSAtomic.class);
		//JEnumConstant states = state.enumConstant("states");
		//states.arg(JExpr.lit("state"));
		JDefinedClass states = stateMachine._enum("states");
		states.enumConstant("state");
		JFieldVar currentState = stateMachine.field(JMod.PRIVATE, states, "currentState");
		JFieldVar inPort = stateMachine.field(JMod.PRIVATE, Port.class, "inport");
		JFieldVar outPort = stateMachine.field(JMod.PRIVATE, Port.class, "outport");
		JFieldVar DELAY = stateMachine.field(JMod.PRIVATE | JMod.FINAL, float.class, "DELAY",JExpr.lit(10.0f));
		
		
		JMethod constructor=stateMachine.constructor(JMod.PUBLIC);
		constructor.param(String.class, "name");
		constructor.javadoc().add("Crée un nouveau "+stateMachine.name()+".");
			
		JBlock constructorBody=constructor.body();
		constructorBody.directStatement("super();");
		constructorBody.invoke("init");
		constructorBody.assign(JExpr._this().ref("name"),JExpr.ref("name"));
		constructorBody.assign(JExpr._this().ref(inPort.name()),JExpr._new(codeModel._ref(Port.class)).arg(JExpr._this()).arg("inport"));
		constructorBody.assign(JExpr._this().ref(outPort.name()),JExpr._new(codeModel._ref(Port.class)).arg(JExpr._this()).arg("outport"));
		
		JMethod init = stateMachine.method(JMod.PUBLIC, void.class, "init");
		init.annotate(Override.class);
		init.body()._return();
		
		JMethod deltaExt = stateMachine.method(JMod.PUBLIC, void.class, "deltaExt");
		deltaExt.annotate(Override.class);
		deltaExt.param(Port.class, "arg0");
		deltaExt.param(Object.class, "arg1");
		deltaExt.param(float.class, "arg2");
		deltaExt.body()._return();
		
		JMethod getDuration = stateMachine.method(JMod.PUBLIC, float.class, "getDuration");
		getDuration.annotate(Override.class);
		getDuration.body()._return(JExpr.lit(0));
		
		JMethod deltaInt = stateMachine.method(JMod.PUBLIC, void.class, "deltaInt");
		deltaInt.annotate(Override.class);
		deltaInt.body()._return();
		
		JMethod lambda = stateMachine.method(JMod.PUBLIC, Object[].class, "lambda");
		lambda.annotate(Override.class);
		lambda.body()._return(JExpr._null());
		
	}
	
	public static void main(String[] args){
		CodeGenerator G=new CodeGenerator();
		try {
			G.generateStates("Test"+"State");
			G.generateCouple("TestCouple");

            //Mettre le chemin où créer le/les fichiers 
			G.codeModel.build(new File("/home/juju/Documents/TCPSpecLog/src/"));

		} catch (JClassAlreadyExistsException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
}
