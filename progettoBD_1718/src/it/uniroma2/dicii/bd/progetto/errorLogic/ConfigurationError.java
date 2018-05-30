package it.uniroma2.dicii.bd.progetto.errorLogic;

// Questo errore si presenta quando sono presenti impostazioni di sistema errate. 
// e.g.: nomi dei file errati, nomi delle classi errati, accesso ai file non consentiti... 
public class ConfigurationError extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ConfigurationError(String message) {
		super(message);
	}
	
	public ConfigurationError(String message, Throwable cause) {
		super(message, cause);
	}
	
}
