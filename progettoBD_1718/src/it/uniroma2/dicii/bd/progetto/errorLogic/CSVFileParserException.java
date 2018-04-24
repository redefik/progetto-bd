package it.uniroma2.dicii.bd.progetto.errorLogic;

//Questo errore si presenta quando sono presenti problemi relativi al parsing di un file csv.
//e.g.: campi non validi, tipi non conformi...

public class CSVFileParserException extends Exception {

	public CSVFileParserException (String message, Throwable cause) {
		super(message, cause);
	}
	
}
