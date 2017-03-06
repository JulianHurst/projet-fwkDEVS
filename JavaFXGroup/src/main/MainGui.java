package main;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Set;

import devs.DevsEnclosing;
import devs.DevsObject;
import devs.DevsState;
import devs.Port;
import devs.StateRect;
import devs.Transition;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import util.Util;

public class MainGui extends Application{
	/**
	 * L'ensemble des états.
	 */
	private LinkedHashSet<DevsObject> states = new LinkedHashSet<>();
	/**
	 * Le group auquel on ajoute les objets à dessiner.
	 */
	private Group canvas = new Group();
	/**
	 * La scène principale.
	 */
	private Scene s;
	/**
	 * Les actions possibles
	 */
	private enum Action{RECT,LINE,MODEL,GEN,TRANS,NONE};
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
	 * Les coordonnées nécessaires pour la mise à jour de la position lors du drag and drop.
	 */
	private double originX,originY,originTranslateX,originTranslateY;
	/**
	 * Le nom du modèle atomique à dessiner.
	 */
	private String modelName;
	/**
	 * Le scale qui permet de zoomer sur le canvas.
	 */
	private Scale zoomer;
	private ListView<String> modelsList;
	/**
	 * Initialisation et lancement de l'interface
	 * @param primaryStage La fenêtre principale
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox root = new VBox();	
		Button rectButton,lineButton,cleanButton,zoomButton,unzoomButton,reloadButton,genButton,transButton;
		
		//Permet de dessiner des rectangles
		rectButton=new Button("Rect");
		//Permet de dessiner des generateurs
		genButton=new Button("Gen");
		//Permet de dessiner des transducers
		transButton=new Button("Trans");
		//Permet de dessiner des liens entre rectangles
		lineButton=new Button("Line");
		//Permet d'effacer tout (rectangles et liens)
		cleanButton=new Button("Clear");
		
		zoomButton=new Button("Zoom in");
		unzoomButton=new Button("Zoom out");
		
		reloadButton=new Button("Reload models");

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
		
		rectButton.setOnAction(e->{
			src=null;
			if(srcPort!=null)
				srcPort.getCircle().setStroke(Color.BLACK);
			currentAction=Action.RECT;
			for(DevsObject state : states)
				for(Port p : state.getPorts())
					p.getCircle().setOnMousePressed(event->{});
		});
		
		genButton.setOnAction(e->{
			src=null;
			if(srcPort!=null)
				srcPort.getCircle().setStroke(Color.BLACK);
			currentAction=Action.GEN;
			for(DevsObject state : states)
				for(Port p : state.getPorts())
					p.getCircle().setOnMousePressed(event->{});
			
		});

		transButton.setOnAction(e->{
			src=null;
			if(srcPort!=null)
				srcPort.getCircle().setStroke(Color.BLACK);
			currentAction=Action.TRANS;
			for(DevsObject state : states)
				for(Port p : state.getPorts())
					p.getCircle().setOnMousePressed(event->{});
			
		});
		
		lineButton.setOnAction(e->{
			currentAction=Action.LINE;
			for(DevsObject state : states){
				for(Port p : state.getPorts()){
					Circle c=p.getCircle();
					c.setOnMousePressed(event->{
						if(event.getButton().equals(MouseButton.PRIMARY)){
							if(src==null){
								src=state;
								srcPort=p;
								c.setStroke(Color.RED);
							}
							else if(!state.equals(src) && !srcPort.getType().equals(p.getType())){
								if(srcPort.getType().equals(Port.Type.INPUT)){
									drawLine(src,srcPort,p);
								}
								else
									drawLine(state,p,srcPort);
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
			currentAction=Action.NONE;
			canvas.getChildren().remove(1, canvas.getChildren().size());
			states.clear();
			src=null;
		});
		
		reloadButton.setOnAction(e->{
			reloadModels();
		});
		
		
		double sceneWidth=Screen.getPrimary().getVisualBounds().getWidth();
		double sceneHeight=Screen.getPrimary().getVisualBounds().getHeight();
		Rectangle R = new Rectangle(0,0,sceneWidth,sceneHeight);
		R.setFill(Color.WHITE);
		canvas.getChildren().add(R);
		canvas.setOnMousePressed(e->{
			if(e.getButton().equals(MouseButton.PRIMARY)){
				boolean superimposed=false;
				switch(currentAction){
					case RECT:
						for(DevsObject obj : states){
							Shape rect = obj.getShape();
							if(e.getX()>=rect.getBoundsInParent().getMinX() && e.getX()<=rect.getBoundsInParent().getMinX()+rect.getBoundsInLocal().getWidth() &&
									e.getY()>=rect.getBoundsInParent().getMinY() && e.getY()<=rect.getBoundsInParent().getMinY()+rect.getBoundsInLocal().getHeight())
								superimposed=true;
						}
						if(!superimposed)
							drawRect(e.getX(),e.getY(),"état");
						break;
					case LINE:
						break;
					case MODEL:
						superimposed=false;
						for(DevsObject obj : states){
							Shape rect = obj.getShape();
							if(e.getX()>=rect.getBoundsInParent().getMinX() && e.getX()<=rect.getBoundsInParent().getMinX()+rect.getBoundsInLocal().getWidth() &&
									e.getY()>=rect.getBoundsInParent().getMinY() && e.getY()<=rect.getBoundsInParent().getMinY()+rect.getBoundsInLocal().getHeight())
								superimposed=true;
						}
						if(!superimposed)
							drawRect(e.getX(),e.getY(),modelName);
						break;
					case GEN:
						superimposed=false;
						for(DevsObject obj : states){
							Shape rect = obj.getShape();
							if(e.getX()>=rect.getBoundsInParent().getMinX() && e.getX()<=rect.getBoundsInParent().getMinX()+rect.getBoundsInLocal().getWidth() &&
									e.getY()>=rect.getBoundsInParent().getMinY() && e.getY()<=rect.getBoundsInParent().getMinY()+rect.getBoundsInLocal().getHeight())
								superimposed=true;
						}
						if(!superimposed)
							drawGen(e.getX(),e.getY());
						break;
					case TRANS:
						superimposed=false;
						for(DevsObject obj : states){
							Shape rect = obj.getShape();
							if(e.getX()>=rect.getBoundsInParent().getMinX() && e.getX()<=rect.getBoundsInParent().getMinX()+rect.getBoundsInLocal().getWidth() &&
									e.getY()>=rect.getBoundsInParent().getMinY() && e.getY()<=rect.getBoundsInParent().getMinY()+rect.getBoundsInLocal().getHeight())
								superimposed=true;
						}
						if(!superimposed)
							drawTrans(e.getX(),e.getY());
						break;
					default:
				}
			}	
		});
		
		ToolBar toolBar = new ToolBar(
				new Button("Run"),
				new Button("Compile"),
				new Button("Save"),
				new Separator(Orientation.VERTICAL),
				rectButton,
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

		modelsList = new ListView<>();
		modelsList.getItems().addAll(Util.getAtomicModelNames());
		
		//Ajoute la création de modèles atomiques avec leurs ports.
		modelsList.setOnMouseClicked(e->{
			src=null;
			if(srcPort!=null)
				srcPort.getCircle().setStroke(Color.BLACK);
			currentAction=Action.MODEL;
			for(DevsObject state : states)
				for(Port p : state.getPorts())
					p.getCircle().setOnMousePressed(event->{});
			modelName=modelsList.getSelectionModel().getSelectedItem().toString();
		});
		
		Text modelTitle = new Text("Models");
		VBox titleBox = new VBox();
		titleBox.getChildren().add(modelTitle);
		titleBox.setAlignment(Pos.CENTER);

		VBox modelBox = new VBox();
		modelBox.getChildren().addAll(titleBox,modelsList);
		modelBox.setMaxWidth(300);
		
		Group container = new Group();
		container.getChildren().add(canvas);

		ScrollPane scroller = new ScrollPane();
		scroller.setContent(container);

		zoomer = new Scale(1,1,0,0);
		canvas.getTransforms().add(zoomer);
		
		SplitPane split = new SplitPane();
		split.setOrientation(Orientation.HORIZONTAL);
		split.getItems().addAll(modelBox,scroller);

		root.getChildren().add(toolBar);
		root.getChildren().add(split);
		primaryStage.setTitle("fwkDEVS");
		primaryStage.setScene(s);
		primaryStage.setResizable(true);
		primaryStage.show();
	}
	
	/**
	 * Dessine un rectangle sur le Group.
	 * @param x La position abscisse où dessiner le rectangle.
	 * @param y La position ordonnée où dessiner le rectangle.
	 * @param name Le nom du modèle atomique.
	 */
	public void drawRect(double x,double y,String name){
		StateRect rect = new StateRect();
		rect.setX(x);
		rect.setY(y);
		DevsState Drect=new DevsState(name,rect);
		try {
			Drect.setPorts(Util.getAtomicModelPorts(modelName));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		setDragAndDrop(Drect);
		setEditState(Drect);
		states.add(Drect);
		canvas.getChildren().add(rect);
		
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
		circ.setCenterX(x);
		circ.setCenterY(y);
		setDragAndDrop(Gen);
		states.add(Gen);
		canvas.getChildren().add(circ);
		drawName(Gen);
		drawPorts(Gen);
	}

	/**
	 * Dessine un transducer.
	 * @param x La position abscisse où dessiner le transducer.
	 * @param y La position ordonnée où dessiner le transducer.
	 */
	public void drawTrans(double x,double y){
		DevsEnclosing Trans=new DevsEnclosing(DevsEnclosing.Type.TRANS);
		Circle circ = (Circle)Trans.getShape();
		circ.setCenterX(x);
		circ.setCenterY(y);
		setDragAndDrop(Trans);
		states.add(Trans);
		canvas.getChildren().add(circ);
		drawName(Trans);
		drawPorts(Trans);
	}
	
	/**
	 * Rafraichit la liste des modèles atomiques en cas de rajout/suppression de modèles.
	 */
	public void reloadModels(){
		modelsList.getItems().clear();
		modelsList.getItems().addAll(Util.getAtomicModelNames());
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
		line.setStartX(src.getCircle().getCenterX()+src.getCircle().getTranslateX());
		line.setStartY(src.getCircle().getCenterY()+src.getCircle().getTranslateY());
		line.setEndX(dest.getCircle().getCenterX()+dest.getCircle().getTranslateX());
		line.setEndY(dest.getCircle().getCenterY()+dest.getCircle().getTranslateY());
		Transition T=new Transition(src,dest,line);
		if(oneTransition(T)){
			state.addTransition(T);
			if(canvas.getChildren().contains(line))
				canvas.getChildren().remove(line);
			canvas.getChildren().add(line);
		}
	}
	
	/**
	 * Vérifie s'il y a une ou plusieurs transitions sur un même port après l'ajout d'une transition.
	 * @param trans La transition à ajouter.
	 * @return True s'il n'y a qu'une transition venant ou allant d'un port, false sinon.
	 */
	public boolean oneTransition(Transition trans){
		for(DevsObject state : states){
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
	public void removeInPorts(DevsState state){
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
	public void removeOutPorts(DevsState state){
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
		for(DevsObject state : states){
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
	public void updatePortTranslations(DevsState state){
		Shape rect=state.getShape();
		for(Port p : state.getPorts()){
			p.getCircle().setTranslateX(rect.getBoundsInParent().getMinX()-rect.getBoundsInLocal().getMinX());
			p.getCircle().setTranslateY(rect.getBoundsInParent().getMinY()-rect.getBoundsInLocal().getMinY());
			p.getName().setTranslateX(rect.getBoundsInParent().getMinX()-rect.getBoundsInLocal().getMinX());
			p.getName().setTranslateY(rect.getBoundsInParent().getMinY()-rect.getBoundsInLocal().getMinY());
		}
	}
	
	/**
	 * Ajoute une action pour l'évènement drag and drop sur tous les rectangles.
	 */
	public void setDragAndDropAll(){
		for(DevsObject state : states){
			setDragAndDrop(state);
		}
	}
	
	/**
	 * Ajoute une action pour l'évènement drag and drop pour le rectangle d'un état donné.
	 * @param state L'état sur lequel on veut ajouter l'action drag and drop.
	 */
	public void setDragAndDrop(DevsObject state){
		Shape rect = state.getShape();
		rect.setOnMouseDragged(e->{
			double offsetX = e.getSceneX() - originX;
			double offsetY = e.getSceneY() - originY;
			double newTranslateX = originTranslateX + offsetX;
			double newTranslateY = originTranslateY + offsetY;
			 
			rect.setTranslateX(newTranslateX);
			rect.setTranslateY(newTranslateY);

			state.getName().setTranslateX(newTranslateX);
			state.getName().setTranslateY(newTranslateY);
			
			for(Port p : state.getPorts()){
				p.getCircle().setTranslateX(newTranslateX);
				p.getCircle().setTranslateY(newTranslateY);
				p.getName().setTranslateX(newTranslateX);
				p.getName().setTranslateY(newTranslateY);
				for(DevsObject s : states){
					for(Transition transition : s.getTransitions()){
						if(p.equals(transition.getSrc())){
							transition.getLine().setStartX(transition.getSrc().getCircle().getCenterX()+transition.getSrc().getCircle().getTranslateX());
							transition.getLine().setStartY(transition.getSrc().getCircle().getCenterY()+transition.getSrc().getCircle().getTranslateY());
						}
						else{
							transition.getLine().setEndX(transition.getDest().getCircle().getCenterX()+transition.getDest().getCircle().getTranslateX());
							transition.getLine().setEndY(transition.getDest().getCircle().getCenterY()+transition.getDest().getCircle().getTranslateY());
						}
					}
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
				states.remove(state);
			}
			else{
				originX=e.getSceneX();
				originY=e.getSceneY();
				originTranslateX = rect.getTranslateX();
				originTranslateY = rect.getTranslateY();
			}
		});
	}
	
	/**
	 * Ajoute la possibilité de modifier un état en double cliquant.
	 * @param state L'état à modifier.
	 */
	public void setEditState(DevsState state){
		Shape rect=state.getShape();
		rect.setOnMouseClicked(e->{
			if(e.getClickCount()==2){
				EditStateStage stage=new EditStateStage(this,state);
				stage.show();
			}
		});
		
	}
	
	
	/**
	 * Récupère la position de départ d'une ligne en fonction de deux rectangles.
	 * @param rect1 Le premier rectangle.
	 * @param rect2 Le deuxième rectangle.
	 * @return Les coordonnées de départ de la ligne.
	 */
	public Point2D getJoin(StateRect rect1, StateRect rect2){
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
