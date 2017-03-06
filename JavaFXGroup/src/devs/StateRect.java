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
	private final static double stateWidth=200;
	/**
	 * Hauteur du rectangle par défaut.
	 */
	private final static double stateHeight=100;
	
	public StateRect(){
		setWidth(stateWidth);
		setHeight(stateHeight);
		setStroke(Color.BLACK);
		setFill(Color.WHITE);
	}

}
