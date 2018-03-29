package it.uniroma2.dicii.bd.progetto.errorLogic;

// Questo errore si presenta quando ci sono problemi relativi all'interfaccia utente
// e.g.: nome dei file fxml errati...
public class GUIError extends Exception {

	public GUIError(String message, Throwable cause) {
		super(message, cause);
	}
}
