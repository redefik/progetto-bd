package it.uniroma2.dicii.bd.progetto.errorLogic;

// Questo errore si presenta quando sono presenti problemi relativi alla gestione dello strato di persistenza
// e.g.: query di un database errate, nomi errati nelle tabelle di un database

public class DataAccessError extends Exception {
	
	private static final long serialVersionUID = 1L;
	public static final String INSERT_FAILED = "Inserimento non effettuato";
	public static final String DELETE_FAILED = "Cancellazione non effettuata";
	
	public DataAccessError (String message, Throwable cause) {
		super(message, cause);
	}
	
	public DataAccessError (String message) {
		super(message);
	}
	
	
}
