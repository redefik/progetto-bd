package it.uniroma2.dicii.bd.progetto.repository;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class UsersRepositoryFactory {
	
	private static UsersRepositoryFactory instance;
	
	protected UsersRepositoryFactory () {}
	
    public synchronized static UsersRepositoryFactory getInstance() {
        if (instance == null) {
            instance = new UsersRepositoryFactory();
        }
        return instance;
    }
    
    public UsersRepository createUsersRepository () throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    	Properties properties = new Properties();
    	FileInputStream inputStream = (FileInputStream) getClass().getResourceAsStream("config.properties");
    	properties.load(inputStream);
    	String className = properties.getProperty("usersrepository_type");
    	Class<?> c = Class.forName(className);
    	UsersRepository usersRepository = (UsersRepository) c.newInstance();
    	return usersRepository;
    }
}
