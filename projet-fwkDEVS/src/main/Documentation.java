package main;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * Affichage de la documentation
 *
 */
public class Documentation extends Stage{
	
	public Documentation(){
		WebView webView = new WebView();
		WebEngine webEngine = webView.getEngine(); 
		webEngine.load("file://"+System.getProperty("user.dir")+"/userDoc/Index.html");
		
		//Pour le css
		//webEngine.setUserStyleSheetLocation();
        
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(webView);
        
        Scene secondScene = new Scene(secondaryLayout, 200, 100);

        setTitle("Documentation");
        setScene(secondScene);
        
	}
}
