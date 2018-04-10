package it.uniroma2.dicii.bd.progetto.administration;

import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.repository.UsersRepository;
import it.uniroma2.dicii.bd.progetto.repository.UsersRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.user.User;
import it.uniroma2.dicii.bd.progetto.user.UserBean;

// Classe singleton
public class AdministrationSession {
		
		private static AdministrationSession instance;
		
		protected AdministrationSession() {}

	    public synchronized static AdministrationSession getInstance() {
	        if (instance == null) {
	            instance = new AdministrationSession();
	        }
	        return instance;
	    }
	    
	    // Il metodo verifica l'assenza di un utente con lo username specificato
	   public boolean isAvailableUsername(String username) throws ConfigurationError, DataAccessError {
		   
		   UsersRepositoryFactory usersRepositoryFactory = UsersRepositoryFactory.getInstance();
		   UsersRepository usersRepository = usersRepositoryFactory.createUsersRepository();
		   return (!(usersRepository.existsUserWithUsername(username)));
		   
	   }
	   
	   // Il metodo delega a un oggetto userRepository l'inserimento di un nuovo utente 
	   public void registerUser(UserBean userBean) throws ConfigurationError, DataAccessError {
		
		   User user = new User(userBean);
		   UsersRepositoryFactory usersRepositoryFactory = UsersRepositoryFactory.getInstance();
		   UsersRepository usersRepository = usersRepositoryFactory.createUsersRepository();
		   usersRepository.persist(user);
	   } 
		    
	    
	    
}
