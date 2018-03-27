package it.uniroma2.dicii.bd.progetto.entry;
	
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		 Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("../gui/loginView.fxml"));
	        primaryStage.setTitle("INAF");
	        primaryStage.setScene(new Scene(root, 600, 600));
	        primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
