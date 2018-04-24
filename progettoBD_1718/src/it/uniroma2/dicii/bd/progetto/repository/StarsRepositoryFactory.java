package it.uniroma2.dicii.bd.progetto.repository;

import java.io.IOException;
import java.util.Properties;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;

// Classe singleton
public class StarsRepositoryFactory {

	private static StarsRepositoryFactory instance;
	
	private static final String CONFIGURATION_FILE = "/config.properties";
	private static final String STARSREPOSITORY_TYPE_KEY = "starsrepository_type";
	
	protected StarsRepositoryFactory () {}
	
    public synchronized static StarsRepositoryFactory getInstance() {
        if (instance == null) {
            instance = new StarsRepositoryFactory();
        }
        return instance;
    }
    
    public StarsRepository createStarsRepository () throws ConfigurationError{
    	
    	try {
    		// Legge da un file .properties l'implementazione di StarsRepository da instanziare
    		Properties properties = new Properties();
        	properties.load(getClass().getResourceAsStream(CONFIGURATION_FILE));
        	String className = properties.getProperty(STARSREPOSITORY_TYPE_KEY);
        	Class<?> c = Class.forName(className);
        	StarsRepository starsRepository = (StarsRepository) c.newInstance();
        	return starsRepository;
        	
    	} catch(IOException | IllegalAccessException | InstantiationException | 
    			ClassNotFoundException | NullPointerException e) {
    		throw new ConfigurationError(e.getMessage(), e.getCause());
    	}	
    	
    }
	
}