package it.uniroma2.dicii.bd.progetto.entry;
	
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	
	private static final String LOGINVIEW_FXML = "../gui/loginView.fxml";
	private static final String WINDOW_HEADER = "INAF";
	
	@Override
	public void start(Stage primaryStage) {
		 Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource(LOGINVIEW_FXML));
	        primaryStage.setTitle(WINDOW_HEADER);
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
