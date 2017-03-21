package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import util.Util;

/**
 * Affichage de la documentation
 *
 */
public class Documentation extends Stage{
	
	public Documentation(){
		Label secondLabel = new Label("Hello");
        
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(secondLabel);
        
        Scene secondScene = new Scene(secondaryLayout, 200, 100);

        Stage secondStage = new Stage();
        secondStage.setTitle("Documentation");
        secondStage.setScene(secondScene);
        
	}
}
