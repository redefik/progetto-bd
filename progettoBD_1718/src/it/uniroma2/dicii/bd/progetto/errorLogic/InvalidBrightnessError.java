package it.uniroma2.dicii.bd.progetto.errorLogic;

// L'eccezione viene sollevata quando la luminosit� specificata dall'utente � minore di 0
public class InvalidBrightnessError extends Exception{
	
	private static final long serialVersionUID = 1L;

	public InvalidBrightnessError() {
		
	}
}
