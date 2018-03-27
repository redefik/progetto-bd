package it.uniroma2.dicii.bd.progetto.entry;

import it.uniroma2.dicii.bd.progetto.repository.UsersRepository;
import it.uniroma2.dicii.bd.progetto.repository.UsersRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.user.UserBean;

import java.io.IOException;

public class LoginSession {
	
	private static LoginSession instance;
	
	protected LoginSession() {}

    public synchronized static LoginSession getInstance() {
        if (instance == null) {
            instance = new LoginSession();
        }
        return instance;
    }
    
    public UserBean findUser (String username, String password) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, IOException {
    	UsersRepository usersRepository = UsersRepositoryFactory.getInstance().createUsersRepository();
    	return null;
    }
	
}
