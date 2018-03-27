package it.uniroma2.dicii.bd.progetto.gui;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class WindowManager {
	
    private static WindowManager instance;

    private WindowManager() {}

    public synchronized static WindowManager getInstance() {
        if (instance == null) {
            instance = new WindowManager();
        }
        return instance;
    }
	
	public void changeMenu (AnchorPane currentWindow, String nextMenu) throws IOException {
		Stage stage = (Stage) currentWindow.getScene().getWindow();
		Parent root = FXMLLoader.load(WindowManager.class.getResource(nextMenu));
		stage.hide();
		stage.setScene(new Scene(root));
		stage.show();
	}
}
