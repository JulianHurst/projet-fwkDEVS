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
	private static String htmlPath="/userDoc/";
	private static String cssPath=htmlPath+"css/";
	
	public Documentation(){
		WebView webView = new WebView();
		WebEngine webEngine = webView.getEngine(); 
		webEngine.load("file://"+System.getProperty("user.dir")+htmlPath+"Index.html");
		
		//Pour le css
		webEngine.setUserStyleSheetLocation("file://"+System.getProperty("user.dir")+cssPath+"devs.css");
        
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(webView);
        
        Scene secondScene = new Scene(secondaryLayout, 200, 100);

        setTitle("Documentation");
        setScene(secondScene);
        
	}
}
