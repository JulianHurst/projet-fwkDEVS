package main;

import java.util.LinkedHashSet;

import devs.DevsState;
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
	 * Les coordonnées nécessaires pour la mise à jour de la position lors du drag and drop.
	 */
	private double originX,originY,originTranslateX,originTranslateY;
	//private int inc=0;

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
			currentAction=Action.RECT;
			src=null;
			setDragAndDropAll();
		});
		
		lineButton.setOnAction(e->{
			currentAction=Action.LINE;
			for(StateRect rect : rectangles){
				rect.setOnMousePressed(event->{});
				rect.setOnMouseDragged(event->{});
				
			}
			for(DevsState state : states){
				//Associe un état source (redessiné en rouge) si il y en a pas. Sinon on calcule les coordonnés, ajoute la transition, dessine le lien et remet src à null. 
				state.getRect().setOnMousePressed(event->{
					if(src==null){
						src=state;
						state.getRect().setColor(Color.RED);
					}
					else{
						//Récupère coordonnées de départ de la ligne  
						Point2D P = getJoin(src.getRect(),state.getRect());
						//Récupère coordonnées de fin de la ligne 
						Point2D P1 = getJoin(state.getRect(),src.getRect());
						Line line=new Line();
						line.setStartX(P.getX());
						line.setStartY(P.getY());
						line.setEndX(P1.getX());
						line.setEndY(P1.getY());
						if(src.addTransition(new Transition(src,state,line)))
							canvas.getChildren().add(line);
						src.getRect().setColor(Color.BLACK);
						src=null;
					}
				});
			}
		});
		
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
		s = new Scene(root, 300, 300, Color.BLACK);
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
		//inc++;
		rectangles.add(rect);
		states.add(Drect);
		canvas.getChildren().add(rect);
		
		//Dessine le nom de l'état au centre du rectangle
		Drect.getName().setX((rect.getX()+rect.getStateWidth()/2)-Drect.getName().getBoundsInLocal().getWidth()/2);
		Drect.getName().setY(rect.getY()+rect.getStateHeight()/2);
		canvas.getChildren().add(Drect.getName());
	}
	
	/**
	 * Dessine un lien entre deux états.
	 * @param src L'état source.
	 * @param state L'état de déstination
	 */
	public void drawLine(DevsState src,DevsState dest){
		Point2D P = getJoin(src.getRect(),dest.getRect());
		Point2D P1 = getJoin(dest.getRect(),src.getRect());
		Line line=new Line();
		line.setStartX(P.getX());
		line.setStartY(P.getY());
		line.setEndX(P1.getX());
		line.setEndY(P1.getY());
		if(dest.addTransition(new Transition(src,dest,line)))
			canvas.getChildren().add(line);
		src.getRect().setColor(Color.BLACK);
		src=null;
	}
	
	/**
	 * Ajoute une action pour l'évènement drag and drop sur tous les rectangles.
	 */
	public void setDragAndDropAll(){
		for(DevsState state : states){
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
				
				for(Transition transition : state.getTransitions()){
					Point2D P = getJoin(rect,transition.getDest().getRect());
					Point2D P1 = getJoin(transition.getDest().getRect(),rect);
					transition.getLine().setStartX(P.getX());
					transition.getLine().setStartY(P.getY());
					transition.getLine().setEndX(P1.getX());
					transition.getLine().setEndY(P1.getY());
				}
				
				for(DevsState src : states){
					for(Transition transition : src.getTransitions())
						if(transition.getDest().equals(state)){
							Point2D P = getJoin(rect,src.getRect());
							Point2D P1 = getJoin(src.getRect(),rect);
							transition.getLine().setStartX(P1.getX());
							transition.getLine().setStartY(P1.getY());
							transition.getLine().setEndX(P.getX());
							transition.getLine().setEndY(P.getY());
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
			
			for(Transition transition : state.getTransitions()){
				Point2D P = getJoin(rect,transition.getDest().getRect());
				transition.getLine().setStartX(P.getX());
				transition.getLine().setStartY(P.getY());
			}
			
			for(DevsState src : states){
				for(Transition transition : src.getTransitions())
					if(transition.getDest().equals(state)){
						Point2D P = getJoin(rect,src.getRect());
						transition.getLine().setEndX(P.getX());
						transition.getLine().setEndY(P.getY());
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
