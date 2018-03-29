package it.uniroma2.dicii.bd.progetto.repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.user.User;

public class JDBCUserDAO implements UsersRepository {
	
	private static final String QUERY_SEARCH_USER = "SELECT * FROM UTENTI WHERE USERNAME = ? AND PASSWORD = ?";

	@Override
	public User findByUsernameAndPassword(String username, String password) throws ConfigurationError, DataAccessError {
		
		try {
			
			User user = null;
			
			// Per garantire maggiore coesione apertura e chiusura delle connessioni vengono delegate ad una classe apposita 
			JDBCConnectionManager connectionManager = JDBCConnectionManager.getInstance();
			Connection connection = connectionManager.openConnection(DBType.DB_USER);
			PreparedStatement statement = connection.prepareStatement(QUERY_SEARCH_USER);
			statement.setString(1, username);
			statement.setString(2, password);
			ResultSet resultSet = statement.executeQuery();
			
			if (resultSet.next()) {
				user = new User (resultSet.getString("username"), resultSet.getString("password"), 
						resultSet.getString("nome"), resultSet.getString("cognome"), resultSet.getString("email"),
						resultSet.getInt("tipo"));
			}
			
			return user;
		} catch (IOException | ClassNotFoundException | NullPointerException e) {
			throw new ConfigurationError(e.getMessage(), e.getCause());
		} catch (SQLException e) {
			throw new DataAccessError(e.getMessage(), e.getCause());
		}
	}
}

