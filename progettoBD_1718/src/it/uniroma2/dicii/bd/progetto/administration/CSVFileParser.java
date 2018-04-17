package it.uniroma2.dicii.bd.progetto.administration;

import java.io.File;
import java.util.ArrayList;

import it.uniroma2.dicii.bd.progetto.errorLogic.CSVFileParserException;
import it.uniroma2.dicii.bd.progetto.filament.FilamentBean;

public abstract class CSVFileParser {
	
	//Si specifica l'header che tutti i file csv contenenti filamenti devono rispettare
	protected String[] FILAMENTS_FILE_HEADERS = 
		{"IDFIL", "NAME", "TOTAL_FLUX", "MEAN_DENS", "MEAN_TEMP", "ELLIPTICITY", "CONTRAST", "SATELLITE", "INSTRUMENT"};

	public abstract ArrayList<FilamentBean> getFilamentBeans(File importedFile) throws CSVFileParserException;
	
}
