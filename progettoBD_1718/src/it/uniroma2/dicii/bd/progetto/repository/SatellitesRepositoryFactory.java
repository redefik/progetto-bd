package it.uniroma2.dicii.bd.progetto.repository;

import java.io.IOException;
import java.util.Properties;

import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;

public class SatellitesRepositoryFactory {
	
	private static SatellitesRepositoryFactory instance;
	
	private static final String CONFIGURATION_FILE = "/config.properties";
	private static final String SATELLITESREPOSITORY_TYPE_KEY = "satellitesrepository_type";
	
	protected SatellitesRepositoryFactory () {}
	
    public synchronized static SatellitesRepositoryFactory getInstance() {
        if (instance == null) {
            instance = new SatellitesRepositoryFactory();
        }
        return instance;
    }
    
    public SatellitesRepository createSatellitesRepository () throws ConfigurationError{
    	
    	try {
    		// Legge da un file .properties l'implementazione di SatellitesRepository da instanziare
    		Properties properties = new Properties();
        	properties.load(getClass().getResourceAsStream(CONFIGURATION_FILE));
        	String className = properties.getProperty(SATELLITESREPOSITORY_TYPE_KEY);
        	Class<?> c = Class.forName(className);
        	SatellitesRepository satellitesRepository = (SatellitesRepository) c.newInstance();
        	return satellitesRepository;
        	
    	} catch(IOException | IllegalAccessException | InstantiationException | 
    			ClassNotFoundException | NullPointerException e) {
    		throw new ConfigurationError(e.getMessage(), e.getCause());
    	}	
    	
    }
}
