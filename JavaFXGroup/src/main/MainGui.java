package main;

import java.util.LinkedHashSet;
import java.util.Set;

import devs.DevsState;
import devs.Port;
import devs.StateRect;
import devs.Transition;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainGui extends Application{
	/**
	 * L'ensemble des états.
	 */
	private LinkedHashSet<DevsState> states = new LinkedHashSet<>();
	/**
	 * L'ensemble des rectangles.
	 */
	private LinkedHashSet<StateRect> rectangles = new LinkedHashSet<>();
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
	private enum Action{RECT,LINE,NONE};
	/**
	 * L'action active.
	 */
	private Action currentAction=Action.NONE;
	/**
	 * L'état source attribué lors de la création de transitions.
	 */
	private DevsState src=null;
	/**
	 * Le port source attribué lors de la création de transitions.
	 */
	private Port srcPort=null;
	/**
	 * Les coordonnées nécessaires pour la mise à jour de la position lors du drag and drop.
	 */
	private double originX,originY,originTranslateX,originTranslateY;
	/**
	 * Initialisation et lancement de l'interface
	 * @param primaryStage La fenêtre principale
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox root = new VBox();	
		Button rectButton,lineButton,cleanButton;
		
		//Permet de dessiner des rectangles
		rectButton=new Button("Rect");
		//Permet de dessiner des liens entre rectangles
		lineButton=new Button("Line");
		//Permet d'effacer tout (rectangles et liens)
		cleanButton=new Button("Clean");
		
		rectButton.setOnAction(e->{
			src=null;
			if(srcPort!=null)
				srcPort.getCircle().setStroke(Color.BLACK);
			currentAction=Action.RECT;
			src=null;
			for(DevsState state : states)
				for(Port p : state.getPorts())
					p.getCircle().setOnMousePressed(event->{});
			setDragAndDropAll();
		});
		
		lineButton.setOnAction(e->{
			currentAction=Action.LINE;
			for(StateRect rect : rectangles){
				rect.setOnMousePressed(event->{});
				rect.setOnMouseDragged(event->{});
				
			}
			for(DevsState state : states){
				for(Port p : state.getPorts()){
					Circle c=p.getCircle();
					c.setOnMousePressed(event->{
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
					});
				}
			}
		});
		
		//Efface tout
		cleanButton.setOnAction(e->{
			currentAction=Action.NONE;
			canvas.getChildren().remove(1, canvas.getChildren().size());
			rectangles.clear();
			states.clear();
			src=null;
		});
		
		
		double sceneWidth=Screen.getPrimary().getVisualBounds().getWidth();
		Rectangle R = new Rectangle(0,0,sceneWidth,800);
		R.setFill(Color.WHITE);
		canvas.getChildren().add(R);
		canvas.setOnMousePressed(e->{
			switch(currentAction){
				case RECT:
					boolean superimposed=false;
					for(StateRect rect : rectangles){
						if(e.getX()>=rect.getTrueX() && e.getX()<=rect.getTrueX()+rect.getStateWidth() &&
								e.getY()>=rect.getTrueY() && e.getY()<=rect.getTrueY()+rect.getStateHeight())
							superimposed=true;
					}
					if(!superimposed)
						drawRect(e.getX(),e.getY());
					break;
				case LINE:
					break;
				default:
			}
				
		});
		
		ToolBar toolBar = new ToolBar(
				new Button("Run"),
				new Button("Compile"),
				new Button("Save"),
				rectButton,
				lineButton,
				new Separator(Orientation.VERTICAL),
				cleanButton,
				new Separator(Orientation.VERTICAL),
				new Button("Debug"),
				new Button("Profile")
		);
		s = new Scene(root, 1200, 600, Color.BLACK);
		ScrollPane scroller = new ScrollPane();
		scroller.setContent(canvas);
		root.getChildren().add(toolBar);
		root.getChildren().add(scroller);
		primaryStage.setTitle("fwkDEVS");
		primaryStage.setScene(s);
		primaryStage.setResizable(true);
		primaryStage.show();
	}
	
	/**
	 * Dessine un rectangle sur le Group.
	 * @param x La position abscisse où dessiner le rectangle.
	 * @param y La position ordonnée où dessiner le rectangle.
	 */
	public void drawRect(double x,double y){
		StateRect rect = new StateRect();
		rect.setX(x);
		rect.setY(y);
		rect.setStroke(rect.getColor());
		rect.setFill(Color.WHITE);
		rect.setTrueX(rect.getX());
		rect.setTrueY(rect.getY());
		DevsState Drect=new DevsState("état",rect);
		setDragAndDrop(Drect);
		setEditState(Drect);
		rectangles.add(rect);
		states.add(Drect);
		canvas.getChildren().add(rect);
		
		//Dessine le nom de l'état au centre du rectangle
		drawName(Drect);
		drawPorts(Drect);
	}
	
	/**
	 * Dessine le nom d'un état dans le centre du rectangle.
	 * @param state L'état concerné.
	 */
	public void drawName(DevsState state){
		StateRect rect=state.getRect();
		state.getName().setX((rect.getX()+rect.getStateWidth()/2)-state.getName().getBoundsInLocal().getWidth()/2);
		state.getName().setY(rect.getY()+rect.getStateHeight()/2);
		if(canvas.getChildren().contains(state.getName()))
			canvas.getChildren().remove(state.getName());
		canvas.getChildren().add(state.getName());
	}
	
	/**
	 * Dessine un lien entre deux états.
	 * @param src L'état source.
	 * @param dest L'état de déstination
	 */
	public void drawLine(DevsState src,DevsState dest){
		Point2D P = getJoin(src.getRect(),dest.getRect());
		Point2D P1 = getJoin(dest.getRect(),src.getRect());
		Line line=new Line();
		line.setStartX(P.getX());
		line.setStartY(P.getY());
		line.setEndX(P1.getX());
		line.setEndY(P1.getY());
		//if(dest.addTransition(new Transition(src,dest,line)))
			//canvas.getChildren().add(line);
		src.getRect().setColor(Color.BLACK);
		src=null;
	}
	
	/**
	 * Dessine une transition entre deux ports.
	 * @param state L'état concerné.
	 * @param src Le port source.
	 * @param dest Le port destinataire.
	 */
	public void drawLine(DevsState state,Port src,Port dest){
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
		for(DevsState state : states){
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
	public void drawPorts(DevsState state){
		drawInPorts(state);
		drawOutPorts(state);
	}
	
	/**
	 * Dessine les ports d'entrée sur un état donné.
	 * @param state L'état concerné.
	 */
	public void drawInPorts(DevsState state){
		StateRect rect = state.getRect();
		Set<Port> inputPorts=state.getInputPorts();
		double step = rect.getStateHeight()/(inputPorts.size()+1);
		int inc=1;
		Circle circle;
		for(Port p : inputPorts){
			circle=p.getCircle();
			circle.setCenterX(rect.getX());
			circle.setCenterY(rect.getY()+step*inc);
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
	public void drawOutPorts(DevsState state){
		StateRect rect = state.getRect();
		Set<Port> outputPorts=state.getOutputPorts();
		double step=rect.getStateHeight()/(outputPorts.size()+1);
		int inc=1;
		Circle circle;
		for(Port p : outputPorts){
			circle=p.getCircle();
			circle.setCenterX(rect.getX()+rect.getStateWidth());
			circle.setCenterY(rect.getY()+step*inc);
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
	public void removePorts(DevsState state){
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
		for(DevsState state : states){
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
		StateRect rect=state.getRect();
		for(Port p : state.getPorts()){
			p.getCircle().setTranslateX(rect.getTrueX()-rect.getX());
			p.getCircle().setTranslateY(rect.getTrueY()-rect.getY());
			p.getName().setTranslateX(rect.getTrueX()-rect.getX());
			p.getName().setTranslateY(rect.getTrueY()-rect.getY());
		}
	}
	
	/**
	 * Ajoute une action pour l'évènement drag and drop sur tous les rectangles.
	 */
	public void setDragAndDropAll(){
		for(DevsState state : states){
			setDragAndDrop(state);
		}
	}
	
	/**
	 * Ajoute une action pour l'évènement drag and drop pour le rectangle d'un état donné.
	 * @param state L'état sur lequel on veut ajouter l'action drag and drop.
	 */
	public void setDragAndDrop(DevsState state){
		StateRect rect = state.getRect();
		rect.setOnMouseDragged(e->{
			double offsetX = e.getSceneX() - originX;
			double offsetY = e.getSceneY() - originY;
			double newTranslateX = originTranslateX + offsetX;
			double newTranslateY = originTranslateY + offsetY;
			 
			rect.setTranslateX(newTranslateX);
			rect.setTranslateY(newTranslateY);
			rect.setTrueX(rect.getX()+rect.getTranslateX());
			rect.setTrueY(rect.getY()+rect.getTranslateY());
			
			state.getName().setTranslateX(newTranslateX);
			state.getName().setTranslateY(newTranslateY);
			
			for(Port p : state.getPorts()){
				p.getCircle().setTranslateX(newTranslateX);
				p.getCircle().setTranslateY(newTranslateY);
				p.getName().setTranslateX(newTranslateX);
				p.getName().setTranslateY(newTranslateY);
				for(DevsState s : states){
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
				originX=e.getSceneX();
				originY=e.getSceneY();
				originTranslateX = rect.getTranslateX();
				originTranslateY = rect.getTranslateY();
		});
	}
	
	/**
	 * Ajoute la possibilité de modifier un état en double cliquant.
	 * @param state L'état à modifier.
	 */
	public void setEditState(DevsState state){
		StateRect rect=state.getRect();
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
		x=rect1.getTrueX()-rect2.getTrueX();
		y=rect1.getTrueY()-rect2.getTrueY();
		if(Math.abs(x)>Math.abs(y)){
			if(x<0)
				return new Point2D(rect1.getTrueX()+rect1.getStateWidth(),rect1.getTrueY()+rect1.getStateHeight()/2);
			else
				return new Point2D(rect1.getTrueX(),rect1.getTrueY()+rect1.getStateHeight()/2);
		}
		else{
			if(y<0)
				return new Point2D(rect1.getTrueX()+rect1.getStateWidth()/2,rect1.getTrueY()+rect1.getStateHeight());
			else
				return new Point2D(rect1.getTrueX()+rect1.getStateWidth()/2,rect1.getTrueY());
		}
	}

	/**
	 * Main qui lance le GUI avec des arguments.
	 * @param args Arguments passés au programme.
	 */
	public static void main(String[] args){
		launch(args);
	}
}
