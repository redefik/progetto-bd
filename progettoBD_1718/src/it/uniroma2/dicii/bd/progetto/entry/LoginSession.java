package it.uniroma2.dicii.bd.progetto.entry;

import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.repository.UsersRepository;
import it.uniroma2.dicii.bd.progetto.repository.UsersRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.user.User;
import it.uniroma2.dicii.bd.progetto.user.UserBean;

// Classe Singleton
public class LoginSession {
	
	private static LoginSession instance;
	
	protected LoginSession() {}

    public synchronized static LoginSession getInstance() {
        if (instance == null) {
            instance = new LoginSession();
        }
        return instance;
    }
    
    public UserBean findUser (String username, String password) throws ConfigurationError, DataAccessError  {
    	
    	UserBean userBean = null;
    	
    	// Istanzia tramite factory un oggetto repository che incapsula la logica di persistenza relativa agli utenti
    	UsersRepository usersRepository = UsersRepositoryFactory.getInstance().createUsersRepository();
    	User user = usersRepository.findByUsernameAndPassword(username, password);
    	// Se l'utente viene trovato allora viene wrappato con un bean ritornato all'interfaccia
    	if (user != null) {
    		userBean = new UserBean (user);
    	}
    	
    	return userBean;
    }
	
}
