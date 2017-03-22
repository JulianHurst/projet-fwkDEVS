package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import util.Util;

/**
 * Le chronogramme obtenu à partir des résultats.
 * @author juju
 *
 */
public class Chronogram extends Stage{
	/**
	 * La liste des résultats;
	 */
	private List<Integer> results = new ArrayList<>();
	/**
	 * Le group dans lequel on dessine.
	 */
	private Group canvas;
	/**
	 * La longueur d'un segment.
	 */
	private static int LINELENGTH=100;
	/**
	 * La hauteur d'un segment (0->1 ou 1->0).
	 */
	private static int LINEHEIGHT=50;
	/**
	 * L'espace à réserver à gauche du chronogramme.
	 */
	private static int INITX=50;
	
	public Chronogram(){
		loadResults();
		canvas = new Group();
		double sceneWidth=Screen.getPrimary().getVisualBounds().getWidth();
		double sceneHeight=Screen.getPrimary().getVisualBounds().getHeight();
		Rectangle R = new Rectangle(0,0,sceneWidth,sceneHeight);
		R.setFill(Color.WHITE);
		canvas.getChildren().add(R);
		Group container = new Group();
		container.getChildren().add(canvas);

		ScrollPane scroller = new ScrollPane();
		scroller.setContent(container);
		Scene scene= new Scene(scroller, 600, 300, Color.BLACK);
		this.setTitle("Chronogram");
		this.setScene(scene);
		draw();
	}

	/**
	 * Charge les résultats à partir des fichiers dans le dossier output.
	 */
	public void loadResults(){
		try {
			results=Util.getResults();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Dessine le plan et le chronogramme.
	 */
	public void draw(){
		drawPlan();
		drawResults();
	}
	
	/**
	 * Dessine le plan.
	 */
	public void drawPlan(){
		Line planX=new Line();
		planX.setStartX(INITX);
		planX.setStartY(canvas.getBoundsInParent().getHeight()/2);
		planX.setEndX(canvas.getBoundsInParent().getWidth());
		planX.setEndY(canvas.getBoundsInParent().getHeight()/2);
		Line planY=new Line();
		planY.setStartX(INITX);
		planY.setStartY(0);
		planY.setEndX(INITX);
		planY.setEndY(canvas.getBoundsInParent().getHeight());
		Text zero = new Text("0");
		zero.setX(INITX/2-zero.getBoundsInParent().getWidth()/2);
		zero.setY(planX.getStartY()+zero.getBoundsInParent().getHeight()/2);
		
		Line oneNotch = new Line();
		oneNotch.setStartX(INITX);
		oneNotch.setStartY(planX.getStartY()-LINEHEIGHT);
		oneNotch.setEndX(INITX+10);
		oneNotch.setEndY(planX.getStartY()-LINEHEIGHT);
		
		Text one = new Text("1");
		one.setX(INITX/2-one.getBoundsInParent().getWidth()/2);
		one.setY(oneNotch.getStartY()+one.getBoundsInParent().getHeight()/2);
		
		canvas.getChildren().add(planX);
		canvas.getChildren().add(planY);
		canvas.getChildren().add(zero);
		canvas.getChildren().add(oneNotch);
		canvas.getChildren().add(one);
	}
	
	/**
	 * Dessine le chronogramme.
	 */
	public void drawResults(){
		int changes=0;
		for(int i=0;i<results.size();i++){
			Line changeLine=null;
			if(results.get(i).intValue()==0){
				if(i>0 && results.get(i-1).intValue()==1){
					changes++;
					changeLine = new Line();
					changeLine.setStartX(i*LINELENGTH+INITX);
					changeLine.setStartY(canvas.getBoundsInParent().getHeight()/2-LINEHEIGHT);
					changeLine.setEndX(i*LINELENGTH+INITX);
					changeLine.setEndY(canvas.getBoundsInParent().getHeight()/2);
					changeLine.setStroke(Color.RED);
				}
			}
			else{
				if(i==0)
					changes++;
				if(i>0 && results.get(i-1).intValue()==0){
					changes++;
					changeLine = new Line();
					changeLine.setStartX(i*LINELENGTH+INITX);
					changeLine.setStartY(canvas.getBoundsInParent().getHeight()/2);
					changeLine.setEndX(i*LINELENGTH+INITX);
					changeLine.setEndY(canvas.getBoundsInParent().getHeight()/2-LINEHEIGHT);
					changeLine.setStroke(Color.RED);
				}
			}
			if(changeLine!=null)
				canvas.getChildren().add(changeLine);
			Line resultLine = new Line();
			resultLine.setStartX(i*LINELENGTH+INITX);
			resultLine.setStartY(canvas.getBoundsInParent().getHeight()/2-((changes%2)*LINEHEIGHT));
			resultLine.setEndX(resultLine.getStartX()+LINELENGTH);
			resultLine.setEndY(canvas.getBoundsInParent().getHeight()/2-((changes%2)*LINEHEIGHT));
			resultLine.setStroke(Color.RED);
			canvas.getChildren().add(resultLine);
		}
	}
}
