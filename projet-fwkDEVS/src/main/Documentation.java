package main;

import java.io.File;
import java.net.MalformedURLException;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * Affichage de la documentation.
 *
 */
public class Documentation extends Stage{
	/**
	 * Le chemin vers les fichiers html.
	 */
	private static String htmlPath="/userDoc/";
	/**
	 * Le chemin vers les fichiers css.
	 */
	private static String cssPath=htmlPath+"css/";
	
	public Documentation(){
		WebView webView = new WebView();
		WebEngine webEngine = webView.getEngine();
		
		File htmlFile = new File(System.getProperty("user.dir")+htmlPath+"Index.html");
		File cssFile = new File(System.getProperty("user.dir")+cssPath+"devs.css");
		
		
		try {
			webEngine.load(htmlFile.toURI().toURL().toString());
			webEngine.setUserStyleSheetLocation(cssFile.toURI().toURL().toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(webView);
        
        Scene secondScene = new Scene(secondaryLayout, 1200,800);

        setTitle("Documentation");
        setScene(secondScene);
        
	}
}
