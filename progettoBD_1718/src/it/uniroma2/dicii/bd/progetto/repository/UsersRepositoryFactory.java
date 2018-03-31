package it.uniroma2.dicii.bd.progetto.repository;

import java.io.IOException;
import java.util.Properties;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;

// Classe Singleton 
public class UsersRepositoryFactory {
	
	private static UsersRepositoryFactory instance;
	
	private static final String CONFIGURATION_FILE = "/config.properties";
	private static final String USERSREPOSITORY_TYPE_KEY = "usersrepository_type";
	
	protected UsersRepositoryFactory () {}
	
    public synchronized static UsersRepositoryFactory getInstance() {
        if (instance == null) {
            instance = new UsersRepositoryFactory();
        }
        return instance;
    }
    
    public UsersRepository createUsersRepository () throws ConfigurationError{
    	
    	try {
    		// Legge da un file .properties l'implementazione di UsersRepository da istanziare
    		Properties properties = new Properties();
        	properties.load(getClass().getResourceAsStream(CONFIGURATION_FILE));
        	String className = properties.getProperty(USERSREPOSITORY_TYPE_KEY);
        	Class<?> c = Class.forName(className);
        	UsersRepository usersRepository = (UsersRepository) c.newInstance();
        	return usersRepository;
        	
    	} catch(IOException | IllegalAccessException | InstantiationException | 
    			ClassNotFoundException | NullPointerException e) {
    		throw new ConfigurationError(e.getMessage(), e.getCause());
    	}	
    	
    }
}
