package it.uniroma2.dicii.bd.progetto.gui;

import java.io.IOException;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

// Classe Singleton
public class WindowManager {
	
	private AnchorPane window;
	
    public void setWindow(AnchorPane window) {
		this.window = window;
	}

	private static WindowManager instance;

    private WindowManager() {}

    public synchronized static WindowManager getInstance() {
        if (instance == null) {
            instance = new WindowManager();
        }
        return instance;
    }
    
	public void changeMenu (String nextMenu) throws GUIError {
	try {
		Stage stage = (Stage) window.getScene().getWindow();
		Parent root = FXMLLoader.load(WindowManager.class.getResource(nextMenu));
		stage.hide();
		stage.setScene(new Scene(root));
		stage.show();
	} catch (IOException | NullPointerException e) {
		throw new GUIError(e.getMessage(), e.getCause());
	}
}
	
	public void openErrorWindow(ErrorType error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        switch (error) {
            case DATA_ACCESS:
                alert.setHeaderText("Accesso ai dati non riuscito");
                break;
            case CONFIGURATION:
                alert.setHeaderText("Configurazione di sistema errata");
                break;
            case GUI:
                alert.setHeaderText("Errore nella gestione delle finestre");
                break;
        }
        alert.showAndWait();
    }
	
	public void openInfoWindow(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
