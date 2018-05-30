package it.uniroma2.dicii.bd.progetto.repository;

import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.user.User;

// Interfaccia che incapsula la logica di persistenza relativa agli utenti
public interface UsersRepository {

	User findByUsernameAndPassword(String username, String password) throws ConfigurationError, DataAccessError;

	boolean existsUserWithUsername(String username) throws DataAccessError, ConfigurationError;
	
	void persist(User user) throws ConfigurationError, DataAccessError;

	void deleteUserWithUsername(String username) throws ConfigurationError, DataAccessError;

}
