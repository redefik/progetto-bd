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
	private static final String QUERY_SEARCH_USERNAME = "SELECT * FROM UTENTI WHERE USERNAME = ?";
	private static final String QUERY_INSERT_USER = "INSERT INTO UTENTI VALUES (?,?,?,?,?,?)";

	@Override
	public User findByUsernameAndPassword(String username, String password) throws ConfigurationError, DataAccessError {
		
		Connection connection = null;
		JDBCConnectionManager connectionManager = null;
		
		try {
			
			User user = null;
			
			// Per garantire maggiore coesione apertura e chiusura delle connessioni vengono delegate ad una classe apposita 
			connectionManager = JDBCConnectionManager.getInstance();
			connection = connectionManager.openConnection(DBType.DB_USER);
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
		} finally {
			if (connection != null && connectionManager != null) {
				try {
					connectionManager.closeConnection(connection);
				} catch (SQLException e) {
					throw new DataAccessError(e.getMessage(), e.getCause());
				}
			}
		}
	}

	@Override
	public boolean existsUserWithUsername(String username) throws DataAccessError, ConfigurationError {
		Connection connection = null;
		JDBCConnectionManager connectionManager = null;
		
		try {
			
			connectionManager = JDBCConnectionManager.getInstance();
			connection = connectionManager.openConnection(DBType.DB_USER);
			PreparedStatement statement = connection.prepareStatement(QUERY_SEARCH_USERNAME);
			statement.setString(1, username);
			ResultSet resultSet = statement.executeQuery();
			
			return resultSet.next();
		
		} catch (IOException | ClassNotFoundException | NullPointerException e) {
			throw new ConfigurationError(e.getMessage(), e.getCause());
		} catch (SQLException e) {
			throw new DataAccessError(e.getMessage(), e.getCause());
		} finally {
			if (connection != null && connectionManager != null) {
				try {
					connectionManager.closeConnection(connection);
				} catch (SQLException e) {
					throw new DataAccessError(e.getMessage(), e.getCause());
				}
			}
		}
	}

	@Override
	public void persist(User user) throws ConfigurationError, DataAccessError {
		
		Connection connection = null;
		JDBCConnectionManager connectionManager = null;
		
		try {
			
			connectionManager = JDBCConnectionManager.getInstance();
			connection = connectionManager.openConnection(DBType.DB_USER);
			PreparedStatement statement = connection.prepareStatement(QUERY_INSERT_USER);
			statement.setString(1, user.getUsername());
			statement.setString(2, user.getPassword());
			statement.setString(3, user.getFirstName());
			statement.setString(4, user.getLastName());
			statement.setString(5, user.getMail());
			statement.setInt(6, user.getType());
			
			// Si verifica che l'inserimento sia stato realmente effettuato
			if (statement.executeUpdate() != 1) {
				throw new DataAccessError(DataAccessError.INSERT_FAILED);
			}
			
			
		} catch (IOException | ClassNotFoundException | NullPointerException e) {
			throw new ConfigurationError(e.getMessage(), e.getCause());
		} catch (SQLException e) {
			throw new DataAccessError(e.getMessage(), e.getCause());
		
		} finally {
			if (connection != null && connectionManager != null) {
				try {
					connectionManager.closeConnection(connection);
				} catch (SQLException e) {
					throw new DataAccessError(e.getMessage(), e.getCause());
				}
			}
		}
	}
}

