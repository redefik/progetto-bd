package it.uniroma2.dicii.bd.progetto.errorLogic;

//Questo errore si presenta quando sono presenti problemi relativi all'esecuzione di un batch update.
//e.g.: duplicazione di chiavi, vincoli di integrità violati...

public class BatchError extends Exception {

	public BatchError(String message, Throwable cause) {
		super(message, cause);
	}
}
