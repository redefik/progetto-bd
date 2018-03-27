package it.uniroma2.dicii.bd.progetto.repository;

import it.uniroma2.dicii.bd.progetto.user.User;

public interface UsersRepository {

	User findByUsernameAndPassword(String username, String password);
	
}
