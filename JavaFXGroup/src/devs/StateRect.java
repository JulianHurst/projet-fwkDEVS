package devs;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Un rectangle qui représente un état.
 * @author Julian
 *
 */
public class StateRect extends Rectangle {
	/**
	 * Largeur du rectangle par défaut.
	 */
	private double stateWidth=200;
	/**
	 * Hauteur du rectangle par défaut.
	 */
	private double stateHeight=100;
	/**
	 * Les vraies coordonnées qui prennent en compte les éventuelles translations (drag and drop). 
	 */
	private double trueX,trueY;
	/**
	 * La couleur du contour.
	 */
	private Color color;
	
	public StateRect(){
		setWidth(stateWidth);
		setHeight(stateHeight);
		color=Color.BLACK;
		trueX=getX();
		trueY=getY();
	}

	public double getStateWidth() {
		return stateWidth;
	}

	public void setStateWidth(double stateWidth) {
		this.stateWidth = stateWidth;
	}

	public double getStateHeight() {
		return stateHeight;
	}

	public void setStateHeight(double stateHeight) {
		this.stateHeight = stateHeight;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		setStroke(color);
	}

	public double getTrueX() {
		return trueX;
	}

	public void setTrueX(double trueX) {
		this.trueX = trueX;
	}

	public double getTrueY() {
		return trueY;
	}

	public void setTrueY(double trueY) {
		this.trueY = trueY;
	}


}
