package it.uniroma2.dicii.bd.progetto.gui;

import java.io.File;
import java.io.IOException;
import java.util.Stack;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// Classe Singleton
public class WindowManager {
	
	
	
	private static final String DATA_ACCESS_ERROR_MSG = "Accesso ai dati non riuscito";
	private static final String CONFIGURATION_ERROR_MSG = "Configurazione di sistema errata";
	private static final String GUI_ERROR_MSG = "Errore nella gestione delle finestre";
	private static final String CSV_PARSING_ERROR_MSG = "Il contenuto del file selezionato non e' valido";
	
	private AnchorPane window;
	
	// La pila memorizza gli ultimi menu visitati: l'elemento in cima e' l'ultimo, quello sotto il penultimo e cosi' via...
	private Stack<String> previousMenu;
	private String currentMenu;
	
    public void setWindow(AnchorPane window) {
		this.window = window;
	}

	private static WindowManager instance;

    private WindowManager() {
    	this.previousMenu = new Stack<>();
    }

    public synchronized static WindowManager getInstance() {
        if (instance == null) {
            instance = new WindowManager();
        }
        return instance;
    }
    
    private void openMenu(String menuToOpen) throws GUIError {
    	try {
    		this.currentMenu = menuToOpen;
	    	Stage stage = (Stage) window.getScene().getWindow();
			Parent root = FXMLLoader.load(WindowManager.class.getResource(menuToOpen));
			stage.hide();
			stage.setScene(new Scene(root));
			stage.show();
    	} catch (IOException | NullPointerException e) {
			throw new GUIError(e.getMessage(), e.getCause());
		}
    }
    
	public void changeMenu (String nextMenu) throws GUIError {
		if (currentMenu != null) {
			// il menu attuale diventa l'ultimo visitato
			previousMenu.add(currentMenu);
		}
		// il menu da aprire e' quello passato come argomento
		openMenu(nextMenu);
	}
	
	public void openErrorWindow(ErrorType error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        switch (error) {
            case DATA_ACCESS:
                alert.setHeaderText(DATA_ACCESS_ERROR_MSG);
                break;
            case CONFIGURATION:
                alert.setHeaderText(CONFIGURATION_ERROR_MSG);
                break;
            case GUI:
                alert.setHeaderText(GUI_ERROR_MSG);
                break;
            case CSVFILE_PARSING:
            	alert.setHeaderText(CSV_PARSING_ERROR_MSG);
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
	
	public void openDetailedErrorWindow(String header, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }
	
	public void goToPreviousMenu() throws GUIError {
		if (!previousMenu.isEmpty()) {
			// il menu da aprire e' l'ultimo visitato (in cima allo stack)
			String menuToOpen = previousMenu.pop();
			openMenu(menuToOpen);
		}
	}
	
	public File getFileFromSystemExplorerWithFormat(String formatName, String formatExtension) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(formatName, formatExtension));
		File selectedFile = fileChooser.showOpenDialog(null);
		return selectedFile;
	}
}
