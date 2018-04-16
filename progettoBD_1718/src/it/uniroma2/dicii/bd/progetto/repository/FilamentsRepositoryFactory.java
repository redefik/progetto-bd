package it.uniroma2.dicii.bd.progetto.repository;

import java.io.IOException;
import java.util.Properties;

import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;

public class FilamentsRepositoryFactory {
	
private static FilamentsRepositoryFactory instance;
	
	private static final String CONFIGURATION_FILE = "/config.properties";
	private static final String FILAMENTSREPOSITORY_TYPE_KEY = "filamentsrepository_type";
	
	protected FilamentsRepositoryFactory () {}
	
    public synchronized static FilamentsRepositoryFactory getInstance() {
        if (instance == null) {
            instance = new FilamentsRepositoryFactory();
        }
        return instance;
    }
    
    public FilamentsRepository createSatellitesRepository () throws ConfigurationError{
    	
    	try {
    		// Legge da un file .properties l'implementazione di SatellitesRepository da instanziare
    		Properties properties = new Properties();
        	properties.load(getClass().getResourceAsStream(CONFIGURATION_FILE));
        	String className = properties.getProperty(FILAMENTSREPOSITORY_TYPE_KEY);
        	Class<?> c = Class.forName(className);
        	FilamentsRepository filamentsRepository = (FilamentsRepository) c.newInstance();
        	return filamentsRepository;
        	
    	} catch(IOException | IllegalAccessException | InstantiationException | 
    			ClassNotFoundException | NullPointerException e) {
    		throw new ConfigurationError(e.getMessage(), e.getCause());
    	}	
    	
    }
	
}
