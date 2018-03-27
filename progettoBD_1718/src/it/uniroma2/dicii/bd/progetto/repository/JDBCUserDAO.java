package it.uniroma2.dicii.bd.progetto.repository;

import it.uniroma2.dicii.bd.progetto.user.User;

public class JDBCUserDAO implements UsersRepository{

	@Override
	public User findByUsernameAndPassword(String username, String password) {
		return null;
	}

}
