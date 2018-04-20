package it.uniroma2.dicii.bd.progetto.administration;

import java.io.File;
import java.util.ArrayList;

import it.uniroma2.dicii.bd.progetto.errorLogic.CSVFileParserException;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.filament.BorderPointBean;
import it.uniroma2.dicii.bd.progetto.filament.FilamentBean;
import it.uniroma2.dicii.bd.progetto.satellite.SatelliteBean;

public abstract class CSVFileParser {
	
	//Si specifica l'header che tutti i file csv contenenti filamenti devono rispettare
	protected String[] FILAMENTS_FILE_HEADERS = 
		{"IDFIL", "NAME", "TOTAL_FLUX", "MEAN_DENS", "MEAN_TEMP", "ELLIPTICITY", "CONTRAST", "SATELLITE", "INSTRUMENT"};
	
	//Si specifica l'header che tutti i file csv contenenti punti del contorno devono rispettare
	protected String[] BORDER_POINTS_FILE_HEADERS = {"IDFIL", "GLON_CONT", "GLAT_CONT"};

	public abstract ArrayList<FilamentBean> getFilamentBeans(File importedFile) throws CSVFileParserException;
	
	public abstract ArrayList<BorderPointBean> getBorderPointBeans(File importedFile, SatelliteBean satelliteBean) 
			throws CSVFileParserException, ConfigurationError, DataAccessError;
}
