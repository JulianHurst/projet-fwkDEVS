package main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.sun.codemodel.JClassAlreadyExistsException;

import DEVSModel.DEVSCoupled;
import DEVSSimulator.Root;
import codegen.CodeGenerator;
import devs.DevsCouple;
import devs.DevsEnclosing;
import devs.DevsModel;
import devs.DevsModule;
import devs.DevsObject;
import devs.Port;
import devs.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import util.Util;

public class MainGui extends Application{
	/**
	 * L'ensemble des états.
	 */
	//private LinkedHashSet<DevsObject> couple.getModels() = new LinkedHashSet<>();
	/**
	 * Le group auquel on ajoute les objets à dessiner.
	 */
	private Group canvas = new Group();
	/**
	 * La scène principale.
	 */
	private Scene s;
	private Stage primaryStage;
	private DevsCouple couple;
	/**
	 * Les actions possibles
	 */
	private enum Action{LINE,MODEL,COUPLE,GEN,TRANS,NONE};
	/**
	 * L'action active.
	 */
	private Action currentAction=Action.NONE;
	/**
	 * L'état source attribué lors de la création de transitions.
	 */
	private DevsObject src=null;
	/**
	 * Le port source attribué lors de la création de transitions.
	 */
	private Port srcPort=null;
	/**
	 * Le nom du modèle atomique à dessiner.
	 */
	private String modelName;
	/**
	 * Le scale qui permet de zoomer sur le canvas.
	 */
	private Scale zoomer;
	/**
	 * La liste de tous les modèles atomiques.
	 */
	private ListView<String> modelsList;
	private ListView<String> couplesList;
	/**
	 * Les propriétés de la souris pour le drag and drop.
	 */
	final ObjectProperty<Point2D> mousePosition = new SimpleObjectProperty<>();
	/**
	 * Initialisation et lancement de l'interface
	 * @param primaryStage La fenêtre principale
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox root = new VBox();	
		Button lineButton,cleanButton,zoomButton,unzoomButton,reloadButton,genButton,transButton;
		
		//Permet de dessiner des generateurs
		genButton=new Button("Generator");
		//Permet de dessiner des transducers
		transButton=new Button("Transducer");
		//Permet de dessiner des liens entre rectangles
		lineButton=new Button("Transition");
		//Permet d'effacer tout (rectangles et liens)
		cleanButton=new Button("Clear");
		
		zoomButton=new Button("Zoom in");
		unzoomButton=new Button("Zoom out");
		
		reloadButton=new Button("Reload lists");

		zoomButton.setOnAction(e->{
			if(zoomer.getX()<50){
				zoomer.setX(zoomer.getX()+0.5);
				zoomer.setY(zoomer.getY()+0.5);
			}
		});
		
		unzoomButton.setOnAction(e->{
			if(zoomer.getX()>1){
				zoomer.setX(zoomer.getX()-0.5);
				zoomer.setY(zoomer.getY()-0.5);
			}
		});
		
		
		genButton.setOnAction(e->{
			src=null;
			if(srcPort!=null)
				srcPort.getCircle().setStroke(Color.BLACK);
			currentAction=Action.GEN;
			for(DevsObject state : couple.getModels())
				for(Port p : state.getPorts())
					p.getCircle().setOnMousePressed(event->{});
			
		});

		transButton.setOnAction(e->{
			src=null;
			if(srcPort!=null)
				srcPort.getCircle().setStroke(Color.BLACK);
			currentAction=Action.TRANS;
			for(DevsObject state : couple.getModels())
				for(Port p : state.getPorts())
					p.getCircle().setOnMousePressed(event->{});
			
		});

		
		lineButton.setOnAction(e->{
			currentAction=Action.LINE;
			for(DevsObject state : couple.getModels()){
				for(Port p : state.getPorts()){
					Circle c=p.getCircle();
					c.setOnMousePressed(event->{
						if(event.getButton().equals(MouseButton.PRIMARY)){
							if(src==null){
								src=state;
								srcPort=p;
								c.setStroke(Color.RED);
							}
							else if(!state.equals(src) && (!srcPort.getType().equals(p.getType())|| src.equals(couple) || state.equals(couple))){
								if(srcPort.getType().equals(Port.Type.INPUT)){
									drawLine(state,p,srcPort);
								}
								else
									drawLine(src,srcPort,p);
								srcPort.getCircle().setStroke(Color.BLACK);
								src=null;
							}
						}
					});
				}
			}
		});
		
		//Efface tout
		cleanButton.setOnAction(e->{
			Set<DevsObject> pending = new LinkedHashSet<>();
			currentAction=Action.NONE;
			canvas.getChildren().remove(2, canvas.getChildren().size());
			for(DevsObject obj : couple.getModels()){
				if(!obj.getClass().equals(DevsCouple.class))
					pending.add(obj);
			}
			couple.getModels().removeAll(pending);
			drawPorts(couple);
			src=null;
			DevsEnclosing.GEN_QUANTITY=0;
			DevsEnclosing.TRANS_QUANTITY=0;
		});
		
		reloadButton.setOnAction(e->{
			reloadAll();
		});
		
		MenuBar menuBar = new MenuBar();
		
		Menu file = new Menu("File");
		MenuItem newItem = new MenuItem("New");
		MenuItem genItem = new MenuItem("Generate");
		MenuItem compileItem = new MenuItem("Compile");
		MenuItem execItem = new MenuItem("Execute");
		MenuItem closeItem = new MenuItem("Close");
		
		newItem.setOnAction(e->{
			try {
				new MainGui().start(new Stage());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		genItem.setOnAction(e->{
			generate();
		});
		
		compileItem.setOnAction(e->{
			generate();
			compile(couple.getName().getText());
		});
		
		execItem.setOnAction(e->{
			generate();
			compile(couple.getName().getText());
			try {
				execute();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException
					| MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		
		closeItem.setOnAction(e->{
			Platform.exit();
		});
		
		file.getItems().add(newItem);
		file.getItems().add(genItem);
		file.getItems().add(compileItem);
		file.getItems().add(execItem);
		file.getItems().add(closeItem);
		
		Menu edit = new Menu("Edit");
		MenuItem coupleItem = new MenuItem("Edit couple");
		
		coupleItem.setOnAction(e->{
			EditStateStage stage=new EditStateStage(this,couple);
			stage.show();
		});
		
		edit.getItems().add(coupleItem);

		menuBar.getMenus().add(file);
		menuBar.getMenus().add(edit);
		
		
		double sceneWidth=Screen.getPrimary().getVisualBounds().getWidth();
		double sceneHeight=Screen.getPrimary().getVisualBounds().getHeight();
		Rectangle R = new Rectangle(0,0,sceneWidth,sceneHeight);
		R.setFill(Color.WHITE);
		canvas.getChildren().add(R);
		
		couple=new DevsCouple("");
		Rectangle rootRect = (Rectangle)couple.getShape();
		rootRect.setOnMousePressed(e->{
			if(e.getButton().equals(MouseButton.PRIMARY)){
				boolean superimposed=false;
				switch(currentAction){
					case LINE:
						break;
					case MODEL:
						superimposed=false;
						for(DevsObject obj : couple.getModels()){
							Shape rect = obj.getShape();
							if(!obj.getClass().equals(DevsCouple.class) && e.getX()>=rect.getBoundsInParent().getMinX() && e.getX()<=rect.getBoundsInParent().getMinX()+rect.getBoundsInLocal().getWidth() &&
									e.getY()>=rect.getBoundsInParent().getMinY() && e.getY()<=rect.getBoundsInParent().getMinY()+rect.getBoundsInLocal().getHeight())
								superimposed=true;
						}
						if(!superimposed){
							int inc=0;
							for(DevsObject obj : couple.getModels()){
								if(obj.getName().getText().matches(modelName+"[0-9]+"))
									inc++;
							}
							drawRect(e.getX(),e.getY(),modelName+inc);
						}
						break;
					case COUPLE:
						superimposed=false;
						for(DevsObject obj : couple.getModels()){
							Shape rect = obj.getShape();
							if(!obj.getClass().equals(DevsCouple.class) && e.getX()>=rect.getBoundsInParent().getMinX() && e.getX()<=rect.getBoundsInParent().getMinX()+rect.getBoundsInLocal().getWidth() &&
									e.getY()>=rect.getBoundsInParent().getMinY() && e.getY()<=rect.getBoundsInParent().getMinY()+rect.getBoundsInLocal().getHeight())
								superimposed=true;
						}
						if(!superimposed){
							int inc=0;
							for(DevsObject obj : couple.getModels()){
								if(obj.getName().getText().matches(modelName+"[0-9]+"))
									inc++;
							}
							drawRect(e.getX(),e.getY(),modelName+inc);
						}
						break;
					case GEN:
						superimposed=false;
						for(DevsObject obj : couple.getModels()){
							Shape rect = obj.getShape();
							if(!obj.getClass().equals(DevsCouple.class) && e.getX()>=rect.getBoundsInParent().getMinX() && e.getX()<=rect.getBoundsInParent().getMinX()+rect.getBoundsInLocal().getWidth() &&
									e.getY()>=rect.getBoundsInParent().getMinY() && e.getY()<=rect.getBoundsInParent().getMinY()+rect.getBoundsInLocal().getHeight())
								superimposed=true;
						}
						if(!superimposed)
							drawGen(e.getX(),e.getY());
						break;
					case TRANS:
						superimposed=false;
						for(DevsObject obj : couple.getModels()){
							Shape rect = obj.getShape();
							if(!obj.getClass().equals(DevsCouple.class) && e.getX()>=rect.getBoundsInParent().getMinX() && e.getX()<=rect.getBoundsInParent().getMinX()+rect.getBoundsInLocal().getWidth() &&
									e.getY()>=rect.getBoundsInParent().getMinY() && e.getY()<=rect.getBoundsInParent().getMinY()+rect.getBoundsInLocal().getHeight())
								superimposed=true;
						}
						if(!superimposed)
							drawTrans(e.getX(),e.getY());
						break;
					default:
				}
			}
			if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount()==2){
				EditStateStage stage=new EditStateStage(this,couple);
				stage.show();
			}
			if(e.getButton().equals(MouseButton.SECONDARY))
				mousePosition.set(new Point2D(e.getSceneX(),e.getSceneY()));
		});
		
		ToolBar toolBar = new ToolBar(
				genButton,
				transButton,
				lineButton,
				new Separator(Orientation.VERTICAL),
				cleanButton,
				new Separator(Orientation.VERTICAL),
				zoomButton,
				unzoomButton,
				new Separator(Orientation.VERTICAL),
				reloadButton
		);
		s = new Scene(root, 1200, 600, Color.BLACK);
		
		TabPane tabPane = new TabPane();
		Tab modelsTab = new Tab();
		Tab couplesTab = new Tab();
		modelsTab.setText("Models");
		couplesTab.setText("Couples");

		modelsList = new ListView<>();
		couplesList = new ListView<>();
		modelsList.getItems().addAll(Util.getAtomicModelNames());
		for(String couple : Util.getAtomicCoupleNames()){
			if(Util.getAtomicCouplePorts(new DevsCouple("tmp"), couple).size()>0)
				couplesList.getItems().add(couple);
		}
		
		//Ajoute la création de modèles atomiques avec leurs ports.
		modelsList.setOnMouseClicked(e->{
			src=null;
			if(srcPort!=null)
				srcPort.getCircle().setStroke(Color.BLACK);
			currentAction=Action.MODEL;
			for(DevsObject state : couple.getModels())
				for(Port p : state.getPorts())
					p.getCircle().setOnMousePressed(event->{});
			modelName=modelsList.getSelectionModel().getSelectedItem().toString();
		});
		
		couplesList.setOnMouseClicked(e->{
			src=null;
			if(srcPort!=null)
				srcPort.getCircle().setStroke(Color.BLACK);
			currentAction=Action.COUPLE;
			for(DevsObject state : couple.getModels())
				for(Port p : state.getPorts())
					p.getCircle().setOnMousePressed(event->{});
			modelName=couplesList.getSelectionModel().getSelectedItem().toString();
		});
		
		modelsTab.setContent(modelsList);
		couplesTab.setContent(couplesList);

		tabPane.getTabs().add(modelsTab);
		tabPane.getTabs().add(couplesTab);
		tabPane.setMaxWidth(300);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		Group container = new Group();
		container.getChildren().add(canvas);

		ScrollPane scroller = new ScrollPane();
		scroller.setContent(container);

		zoomer = new Scale(1,1,0,0);
		canvas.getTransforms().add(zoomer);
		
		SplitPane split = new SplitPane();
		split.setOrientation(Orientation.HORIZONTAL);
		split.getItems().addAll(tabPane,scroller);

		root.getChildren().add(menuBar);
		root.getChildren().add(toolBar);
		root.getChildren().add(split);
		primaryStage.setTitle("fwkDEVS (unnamed couple)");
		primaryStage.setScene(s);
		primaryStage.setResizable(true);
		primaryStage.show();
		
		//rootRect.setX(sceneWidth/2-rootRect.getWidth()/2);
		//rootRect.setY(sceneHeight/2-rootRect.getHeight()/2);
		rootRect.setX(0);
		rootRect.setY(0);
		
		canvas.getChildren().add(couple.getShape());
		couple.getModels().add(couple);
		rootRect.setOnMouseDragged(e->{
			if(e.getButton().equals(MouseButton.SECONDARY)){
				double offsetX = e.getSceneX()-mousePosition.get().getX();
				double offsetY = e.getSceneY()-mousePosition.get().getY();
				
				rootRect.setWidth(rootRect.getWidth()+offsetX);
				rootRect.setHeight(rootRect.getHeight()+offsetY);

				mousePosition.set(new Point2D(e.getSceneX(), e.getSceneY()));
				Set<Transition> transitions=new LinkedHashSet<>();
				transitions.addAll(couple.getTransitions());
				for(DevsObject obj : couple.getModels()){
					for(Transition transition : obj.getTransitions()){
						if(couple.getPorts().contains(transition.getSrc()) || couple.getPorts().contains(transition.getDest()))
							transitions.add(transition);
					}
				}
				this.removeInPorts(couple);
				this.removeOutPorts(couple);
				this.drawInPorts(couple);
				this.drawOutPorts(couple);

				for(DevsObject obj : couple.getModels()){
					if(!rootRect.getBoundsInParent().contains(obj.getShape().getBoundsInParent())){
						rootRect.setWidth(rootRect.getWidth()-offsetX);
						rootRect.setHeight(rootRect.getHeight()-offsetY);
					}
				}
				if(rootRect.getWidth()<500 || rootRect.getHeight()<500){
					rootRect.setWidth(rootRect.getWidth()-offsetX);
					rootRect.setHeight(rootRect.getHeight()-offsetY);
				}
				for(Transition transition : transitions){
					this.drawLine(transition.getSrc().getParent(), transition.getSrc(), transition.getDest());
				}
			}
		});
		this.primaryStage=primaryStage;
	}
	
	/**
	 * Génère la classe du couple. 
	 */
	public void generate(){
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Couple name");
		dialog.setHeaderText("Couple name");
		dialog.setContentText("Choose a name for the couple :");
		Optional<String> result=dialog.showAndWait();
		result.ifPresent(name->{
			CodeGenerator C = new CodeGenerator();
			couple.setName(name);
			setTitle(name);
			try {
				C.generateCouple(name, couple.getModels(),couple);
			} catch (JClassAlreadyExistsException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}
	
	/**
	 * Compile le couple spécifié.
	 * @param coupleClass Le nom du couple.
	 */
	public void compile(String coupleClass){
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(null, null, null, "-d",System.getProperty("user.dir")+"/bin",System.getProperty("user.dir")+"/src/couples/"+coupleClass+".java");
	}
	
	/**
	 * Execute la simulation sur le couple courant.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws MalformedURLException
	 */
	public void execute() throws InstantiationException, IllegalAccessException, ClassNotFoundException, MalformedURLException{ 
		//File bin = new File(System.getProperty("user.dir")+"/src");
		//URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { bin.toURI().toURL() });

		//DEVSCoupled execCouple = (DEVSCoupled)Class.forName("gen."+couple.getName().getText(),true,classLoader).newInstance();
		DEVSCoupled execCouple = (DEVSCoupled)Class.forName("couples."+couple.getName().getText()).newInstance();
		Root root = new Root(execCouple,150);
		root.startSimulation();
	}
	
	/**
	 * Dessine un rectangle sur le Group.
	 * @param x La position abscisse où dessiner le rectangle.
	 * @param y La position ordonnée où dessiner le rectangle.
	 * @param name Le nom du modèle atomique.
	 */
	public void drawRect(double x,double y,String name){
		DevsObject Drect;
		if(currentAction.equals(Action.COUPLE)){
			Drect=new DevsModule(name,modelName);
		}
		else{
			Drect=new DevsModel(name,modelName);
		}
		Rectangle rect=(Rectangle)Drect.getShape();
		rect.setX(x);
		rect.setY(y);
		try {
			if(currentAction.equals(Action.COUPLE)){
				Drect.setPorts(Util.getAtomicCouplePorts(Drect,modelName));
			}
			else
				Drect.setPorts(Util.getAtomicModelPorts(Drect,modelName));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		setDragAndDrop(Drect);
		couple.getModels().add(Drect);
		canvas.getChildren().add(rect);

		if(rect.getBoundsInParent().getMaxX()>couple.getShape().getBoundsInParent().getMaxX()){
			Rectangle r=(Rectangle)couple.getShape();
			r.setWidth(rect.getBoundsInLocal().getMaxX()-r.getX());
			//couple.getShape().resize(rect.getBoundsInParent().getMaxX(), couple.getShape().getBoundsInParent().getMaxY());
		}
		if(rect.getBoundsInParent().getMaxY()>couple.getShape().getBoundsInParent().getMaxY()){
			Rectangle r=(Rectangle)couple.getShape();
			r.setHeight(rect.getBoundsInLocal().getMaxY()-r.getY());
			//couple.getShape().resize(couple.getShape().getBoundsInParent().getMaxX(), rect.getBoundsInParent().getMaxY());
		}
		
		//Dessine le nom de l'état au centre du rectangle
		drawName(Drect);
		drawPorts(Drect);
	}
	
	/**
	 * Dessine un générateur.
	 * @param x La position abscisse où dessiner le générateur.
	 * @param y La position ordonnée où dessiner le générateur.
	 */
	public void drawGen(double x,double y){
		DevsEnclosing Gen=new DevsEnclosing(DevsEnclosing.Type.GEN);
		Circle circ = (Circle)Gen.getShape();
		circ.setCenterX(x+circ.getRadius());
		circ.setCenterY(y+circ.getRadius());
		setDragAndDrop(Gen);
		couple.getModels().add(Gen);
		canvas.getChildren().add(circ);
		drawName(Gen);
		drawPorts(Gen);
		if(circ.getBoundsInParent().getMaxX()>couple.getShape().getBoundsInParent().getMaxX()){
			Rectangle r=(Rectangle)couple.getShape();
			r.setWidth(circ.getBoundsInLocal().getMaxX()-r.getX());
			//couple.getShape().resize(circ.getBoundsInParent().getMaxX(), couple.getShape().getBoundsInParent().getMaxY());
		}
		if(circ.getBoundsInParent().getMaxY()>couple.getShape().getBoundsInParent().getMaxY()){
			Rectangle r=(Rectangle)couple.getShape();
			r.setHeight(circ.getBoundsInLocal().getMaxY()-r.getY());
			//couple.getShape().resize(couple.getShape().getBoundsInParent().getMaxX(), circ.getBoundsInParent().getMaxY());
		}
	}

	/**
	 * Dessine un transducer.
	 * @param x La position abscisse où dessiner le transducer.
	 * @param y La position ordonnée où dessiner le transducer.
	 */
	public void drawTrans(double x,double y){
		DevsEnclosing Trans=new DevsEnclosing(DevsEnclosing.Type.TRANS);
		Circle circ = (Circle)Trans.getShape();
		circ.setCenterX(x+circ.getRadius());
		circ.setCenterY(y+circ.getRadius());
		setDragAndDrop(Trans);
		couple.getModels().add(Trans);
		canvas.getChildren().add(circ);
		drawName(Trans);
		drawPorts(Trans);
		if(circ.getBoundsInParent().getMaxX()>couple.getShape().getBoundsInParent().getMaxX()){
			Rectangle r=(Rectangle)couple.getShape();
			r.setWidth(circ.getBoundsInLocal().getMaxX()-r.getX());
			//couple.getShape().resize(circ.getBoundsInParent().getMaxX(), couple.getShape().getBoundsInParent().getMaxY());
		}
		if(circ.getBoundsInParent().getMaxY()>couple.getShape().getBoundsInParent().getMaxY()){
			Rectangle r=(Rectangle)couple.getShape();
			r.setHeight(circ.getBoundsInLocal().getMaxY()-r.getY());
			//couple.getShape().resize(couple.getShape().getBoundsInParent().getMaxX(), circ.getBoundsInParent().getMaxY());
		}
	}
	
	/**
	 * Rafraichit la liste des modèles atomiques en cas de rajout/suppression de modèles.
	 */
	public void reloadModels(){
		modelsList.getItems().clear();
		modelsList.getItems().addAll(Util.getAtomicModelNames());
	}
	
	public void reloadCouples() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException{
		couplesList.getItems().clear();
		for(String couple : Util.getAtomicCoupleNames()){
			compile(couple);
			if(Util.getAtomicCouplePorts(new DevsCouple("tmp"), couple).size()>0)
				couplesList.getItems().add(couple);
		}
	}
	
	public void reloadAll(){
		reloadModels();
		try {
			reloadCouples();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Dessine le nom d'un état dans le centre du rectangle.
	 * @param state L'état concerné.
	 */
	public void drawName(DevsObject state){
		Shape rect=state.getShape();
		state.getName().setX((rect.getBoundsInLocal().getMinX()+rect.getBoundsInLocal().getWidth()/2)-state.getName().getBoundsInLocal().getWidth()/2);
		state.getName().setY(rect.getBoundsInLocal().getMinY()+rect.getBoundsInLocal().getHeight()/2);
		if(canvas.getChildren().contains(state.getName()))
			canvas.getChildren().remove(state.getName());
		canvas.getChildren().add(state.getName());
	}
	
	/**
	 * Dessine un lien entre deux états.
	 * @param src L'état source.
	 * @param dest L'état de déstination
	 */
	/*public void drawLine(DevsState src,DevsState dest){
		Point2D P = getJoin(src.getShape(),dest.getShape());
		Point2D P1 = getJoin(dest.getShape(),src.getShape());
		Line line=new Line();
		line.setStartX(P.getX());
		line.setStartY(P.getY());
		line.setEndX(P1.getX());
		line.setEndY(P1.getY());
		//if(dest.addTransition(new Transition(src,dest,line)))
			//canvas.getChildren().add(line);
		src.getShape().setColor(Color.BLACK);
		src=null;
	}*/
	
	/**
	 * Dessine une transition entre deux ports.
	 * @param state L'état concerné.
	 * @param src Le port source.
	 * @param dest Le port destinataire.
	 */
	public void drawLine(DevsObject state,Port src,Port dest){
		Line line = new Line();
		line.setStartX(src.getCircle().getCenterX()+src.getCircle().getLayoutX());
		line.setStartY(src.getCircle().getCenterY()+src.getCircle().getLayoutY());
		line.setEndX(dest.getCircle().getCenterX()+dest.getCircle().getLayoutX());
		line.setEndY(dest.getCircle().getCenterY()+dest.getCircle().getLayoutY());
		Transition T=new Transition(src,dest,line);
		
		state.addTransition(T);
		if(canvas.getChildren().contains(line))
			canvas.getChildren().remove(line);
		canvas.getChildren().add(line);
	}
	
	/**
	 * Vérifie s'il y a une ou plusieurs transitions sur un même port après l'ajout d'une transition.
	 * @param trans La transition à ajouter.
	 * @return True s'il n'y a qu'une transition venant ou allant d'un port, false sinon.
	 */
	public boolean oneTransition(Transition trans){
		for(DevsObject state : couple.getModels()){
			for(Transition transition : state.getTransitions()){
				if(isSrcOrDest(trans,transition))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Le test nécessaire pour déterminer la présence d'une transition venant de ou allant vers un port.
	 * @param trans La transition à ajouter.
	 * @param transition Une transition existante.
	 * @return True si un des ports utilisé par la transition à ajouter est déjà utilisé par l'autre transition. 
	 */
	public boolean isSrcOrDest(Transition trans,Transition transition){
		return trans.getSrc().equals(transition.getSrc()) || trans.getDest().equals(transition.getDest()) ||
		trans.getSrc().equals(transition.getDest()) || trans.getDest().equals(transition.getSrc());
	}
	
	/**
	 * Dessine les ports sur un état donné.
	 * @param state L'état concerné.
	 */
	public void drawPorts(DevsObject state){
		drawInPorts(state);
		drawOutPorts(state);
	}
	
	/**
	 * Dessine les ports d'entrée sur un état donné.
	 * @param state L'état concerné.
	 */
	public void drawInPorts(DevsObject state){
		Shape rect = state.getShape();
		Set<Port> inputPorts=state.getInputPorts();
		double step = rect.getBoundsInLocal().getHeight()/(inputPorts.size()+1);
		int inc=1;
		Circle circle;
		for(Port p : inputPorts){
			circle=p.getCircle();
			circle.setCenterX(rect.getBoundsInLocal().getMinX());
			circle.setCenterY(rect.getBoundsInLocal().getMinY()+step*inc);
			canvas.getChildren().add(circle);
			p.getName().setX(circle.getCenterX()-(circle.getRadius()+p.getName().getBoundsInLocal().getWidth()));
			p.getName().setY(circle.getCenterY()-circle.getRadius());
			canvas.getChildren().add(p.getName());
			inc++;
		}
	}
		
	/**
	 * Dessine les ports de sortie sur un état donné.
	 * @param state L'état concerné.
	 */
	public void drawOutPorts(DevsObject state){
		Shape rect = state.getShape();
		Set<Port> outputPorts=state.getOutputPorts();
		double step=rect.getBoundsInLocal().getHeight()/(outputPorts.size()+1);
		int inc=1;
		Circle circle;
		for(Port p : outputPorts){
			circle=p.getCircle();
			circle.setCenterX(rect.getBoundsInLocal().getMinX()+rect.getBoundsInLocal().getWidth());
			circle.setCenterY(rect.getBoundsInLocal().getMinY()+step*inc);
			canvas.getChildren().add(circle);
			p.getName().setX(circle.getCenterX()+(circle.getRadius()));
			p.getName().setY(circle.getCenterY()-circle.getRadius());
			canvas.getChildren().add(p.getName());
			inc++;
		}
	}
	
	
	/**
	 * Efface les ports d'un état.
	 * @param state L'état concerné.
	 */
	public void removePorts(DevsObject state){
		for(Port p : state.getPorts()){
			if(canvas.getChildren().contains(p.getCircle())){
				canvas.getChildren().remove(p.getCircle());
				canvas.getChildren().remove(p.getName());
				removeTransitions(p);
			}
		}
	}
	
	/*public void removePorts(DevsState state, Set<Port> changedPorts){
		for(Port p : state.getPorts()){
			if(canvas.getChildren().contains(p.getCircle())){
				canvas.getChildren().remove(p.getCircle());
				canvas.getChildren().remove(p.getName());
				removeTransitions(p);
			}
		}
	}*/
	
	/**
	 * Efface les ports d'un état.
	 * @param state L'état concerné.
	 */
	public void removeInPorts(DevsCouple state){
		for(Port p : state.getInputPorts()){
			if(canvas.getChildren().contains(p.getCircle())){
				canvas.getChildren().remove(p.getCircle());
				canvas.getChildren().remove(p.getName());
				removeTransitions(p);
			}
		}
	}
	
	/**
	 * Efface les ports d'un état.
	 * @param state L'état concerné.
	 */
	public void removeOutPorts(DevsCouple state){
		for(Port p : state.getOutputPorts()){
			if(canvas.getChildren().contains(p.getCircle())){
				canvas.getChildren().remove(p.getCircle());
				canvas.getChildren().remove(p.getName());
				removeTransitions(p);
			}
		}
	}
	
	
	/**
	 * Efface toutes les transitions concernant un port donné.
	 * @param p Le port concerné.
	 */
	public void removeTransitions(Port p){
		Set<Transition> pendingTransitions;
		for(DevsObject state : couple.getModels()){
			pendingTransitions=new LinkedHashSet<>();
			for(Transition transition : state.getTransitions()){
				if(transition.getSrc().equals(p) || transition.getDest().equals(p)){
					canvas.getChildren().remove(transition.getLine());
					pendingTransitions.add(transition);
				}
			}
			state.getTransitions().removeAll(pendingTransitions);
		}
	}
	
	/**
	 * Met à jour les données de translation pour les ports.
	 * @param state L'état contenant les ports concernés.
	 */
	public void updatePortTranslations(DevsCouple state){
		Shape rect=state.getShape();
		for(Port p : state.getPorts()){
			p.getCircle().setTranslateX(rect.getBoundsInParent().getMinX()-rect.getBoundsInLocal().getMinX());
			p.getCircle().setTranslateY(rect.getBoundsInParent().getMinY()-rect.getBoundsInLocal().getMinY());
			p.getName().setTranslateX(rect.getBoundsInParent().getMinX()-rect.getBoundsInLocal().getMinX());
			p.getName().setTranslateY(rect.getBoundsInParent().getMinY()-rect.getBoundsInLocal().getMinY());
		}
	}

	/**
	 * Permet de changer le titre de la fenêtre.
	 * @param title Le nouveau titre.
	 */
	public void setTitle(String title){
		primaryStage.setTitle(title);
	}
	
	/**
	 * Ajoute une action pour l'évènement drag and drop sur tous les rectangles.
	 */
	public void setDragAndDropAll(){
		for(DevsObject state : couple.getModels()){
			setDragAndDrop(state);
		}
	}
	
	/**
	 * Ajoute une action pour l'évènement drag and drop pour le rectangle d'un état donné.
	 * @param state Le modèle sur lequel on veut ajouter l'action drag and drop.
	 */
	public void setDragAndDrop(DevsObject state){
		Shape rect = state.getShape();
		rect.setOnMouseDragged(e->{

			double offsetX = e.getSceneX() - mousePosition.get().getX();
			double offsetY = e.getSceneY() - mousePosition.get().getY();

			rect.setLayoutX(rect.getLayoutX()+offsetX);
			rect.setLayoutY(rect.getLayoutY()+offsetY);
			mousePosition.set(new Point2D(e.getSceneX(), e.getSceneY()));

			state.getName().setLayoutX(state.getName().getLayoutX()+offsetX);
			state.getName().setLayoutY(state.getName().getLayoutY()+offsetY);
			
			for(Port p : state.getPorts()){
				p.getCircle().setLayoutX(p.getCircle().getLayoutX()+offsetX);
				p.getCircle().setLayoutY(p.getCircle().getLayoutY()+offsetY);
				p.getName().setLayoutX(p.getName().getLayoutX()+offsetX);
				p.getName().setLayoutY(p.getName().getLayoutY()+offsetY);
				for(DevsObject s : couple.getModels()){
					for(Transition transition : s.getTransitions()){
						if(p.equals(transition.getSrc())){
							transition.getLine().setStartX(transition.getSrc().getCircle().getCenterX()+transition.getSrc().getCircle().getLayoutX());
							transition.getLine().setStartY(transition.getSrc().getCircle().getCenterY()+transition.getSrc().getCircle().getLayoutY());
						}
						else{
							transition.getLine().setEndX(transition.getDest().getCircle().getCenterX()+transition.getDest().getCircle().getLayoutX());
							transition.getLine().setEndY(transition.getDest().getCircle().getCenterY()+transition.getDest().getCircle().getLayoutY());
						}
					}
				}
			}
			
			
			if(!couple.getShape().getBoundsInParent().contains(rect.getBoundsInParent())){
				rect.setLayoutX(rect.getLayoutX()-offsetX);
				rect.setLayoutY(rect.getLayoutY()-offsetY);
				state.getName().setLayoutX(state.getName().getLayoutX()-offsetX);
				state.getName().setLayoutY(state.getName().getLayoutY()-offsetY);
				for(Port p : state.getPorts()){
					p.getCircle().setLayoutX(p.getCircle().getLayoutX()-offsetX);
					p.getCircle().setLayoutY(p.getCircle().getLayoutY()-offsetY);
					p.getName().setLayoutX(p.getName().getLayoutX()-offsetX);
					p.getName().setLayoutY(p.getName().getLayoutY()-offsetY);
				}
			}
		});
		
		rect.setOnMousePressed(e->{
			if(e.getButton()==MouseButton.SECONDARY){
				if(state.getClass().equals(DevsEnclosing.class)){
					if(state.getName().getText().contains("Gen"))
						DevsEnclosing.GEN_QUANTITY--;
					else
						DevsEnclosing.TRANS_QUANTITY--;
				}
				canvas.getChildren().remove(rect);
				canvas.getChildren().remove(state.getName());
				removePorts(state);
				couple.getModels().remove(state);
			}
			else{
				mousePosition.set(new Point2D(e.getSceneX(),e.getSceneY()));
			}
		});
	}
	/**
	 * Ajoute la possibilité de modifier un état en double cliquant.
	 * @param state L'état à modifier.
	 */
	/*public void setEditState(DevsState state){
		Shape rect=state.getShape();
		rect.setOnMouseClicked(e->{
			if(e.getClickCount()==2){
				EditStateStage stage=new EditStateStage(this,state);
				stage.show();
			}
		});
		
	}*/
	
	
	/**
	 * Récupère la position de départ d'une ligne en fonction de deux rectangles.
	 * @param rect1 Le premier rectangle.
	 * @param rect2 Le deuxième rectangle.
	 * @return Les coordonnées de départ de la ligne.
	 */
	public Point2D getJoin(Rectangle rect1, Rectangle rect2){
		double x,y;
		x=rect1.getBoundsInParent().getMinX()-rect2.getBoundsInParent().getMinX();
		y=rect1.getBoundsInParent().getMinY()-rect2.getBoundsInParent().getMinY();
		if(Math.abs(x)>Math.abs(y)){
			if(x<0)
				return new Point2D(rect1.getBoundsInParent().getMinX()+rect1.getBoundsInLocal().getWidth(),rect1.getBoundsInParent().getMinY()+rect1.getBoundsInLocal().getHeight()/2);
			else
				return new Point2D(rect1.getBoundsInParent().getMinX(),rect1.getBoundsInParent().getMinY()+rect1.getBoundsInLocal().getHeight()/2);
		}
		else{
			if(y<0)
				return new Point2D(rect1.getBoundsInParent().getMinX()+rect1.getBoundsInLocal().getWidth()/2,rect1.getBoundsInParent().getMinY()+rect1.getBoundsInLocal().getHeight());
			else
				return new Point2D(rect1.getBoundsInParent().getMinX()+rect1.getBoundsInLocal().getWidth()/2,rect1.getBoundsInParent().getMinY());
		}
	}

	/**
	 * Main qui lance le GUI avec des arguments.
	 * @param args Arguments passés au programme.
	 */
	public static void main(String[] args){
		launch(args);
	
		//Test de getAtomicModelPorts
		/*Set<Port> port;
		try {
			port = Util.getAtomicModelPorts("And");
			for(Port p : port)
				System.out.println(p.toString());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
