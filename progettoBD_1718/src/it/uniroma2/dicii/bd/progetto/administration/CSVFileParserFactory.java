package it.uniroma2.dicii.bd.progetto.administration;

import java.io.IOException;
import java.util.Properties;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;

//La classe incapsula la logica di creazione per un oggetto che implementa CSVFileParser. La classe concreta usata in questa 
//versione del programma potrebbe cambiare in futuro.

//Classe singleton
public class CSVFileParserFactory {
	
	private static CSVFileParserFactory instance;
	private static final String CONFIGURATION_FILE = "/config.properties";
	private static final String CSVFILEPARSER_TYPE_KEY = "csvfileparser_type";
	
	protected CSVFileParserFactory() {
		
	}
	
	public synchronized static CSVFileParserFactory getInstance() {
		if (instance == null) {
			instance = new CSVFileParserFactory();
		}
		return instance;
	}
	
	public CSVFileParser createCSVFileParser() throws ConfigurationError {
		try {
    		// Legge da un file .properties l'implementazione di CSVFileParser da instanziare
    		Properties properties = new Properties();
        	properties.load(getClass().getResourceAsStream(CONFIGURATION_FILE));
        	String className = properties.getProperty(CSVFILEPARSER_TYPE_KEY);
        	Class<?> c = Class.forName(className);
        	CSVFileParser parserInstance = (CSVFileParser) c.newInstance();
        	return parserInstance;
        	
    	} catch(IOException | IllegalAccessException | InstantiationException | 
    			ClassNotFoundException | NullPointerException e) {
    		throw new ConfigurationError(e.getMessage(), e.getCause());
    	}	
	}
	
}
