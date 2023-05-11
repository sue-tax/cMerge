package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
	static final float VERSION = 0.12f;

	public static Stage stage;

	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		try {
    		Parent root = FXMLLoader.load(
	    			getClass().
	    			getResource("cMerge.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().
					getResource("application.css").
					toExternalForm());
	    	primaryStage.setTitle(String.format(
	    			"cMergeWin(version %s)",
	    			VERSION));
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
