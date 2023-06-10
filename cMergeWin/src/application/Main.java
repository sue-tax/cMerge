package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
//	public static final float VERSION = 0.13f;
	// フォルダ指定を可能にする
//	public static final float VERSION = 0.20f;
	// 比較機能の追加、ファイル名の除外文字列の対応
//	public static final float VERSION = 0.21f;
	// 比較機能のフォルダ指定（カラーを保存）
//	public static final float VERSION = 0.22f;
	// 除外指定の有無で処理を変更
	public static final float VERSION = 0.23f;

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
