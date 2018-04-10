package it.uniroma2.dicii.bd.progetto.repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.satellite.Agency;
import it.uniroma2.dicii.bd.progetto.user.User;

public class JDBCSatelliteDAO {
	
	private static final String QUERY_FIND_ALL_AGENCIES = "SELECT * FROM SATELLITE";

	public ArrayList<Agency> findAllAgencies() throws ConfigurationError, DataAccessError, SQLException{
		
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			
			Agency agency = null;
			ArrayList<Agency> agencies = new ArrayList<Agency>();
			
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			PreparedStatement statement = connection.prepareStatement(QUERY_FIND_ALL_AGENCIES);
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				agency = new Agency (resultSet.getString("name"));
				agencies.add(agency);
			}
			
			return agencies;
			
		} catch (IOException | ClassNotFoundException | NullPointerException e) {
			throw new ConfigurationError(e.getMessage(), e.getCause());
		} catch (SQLException e) {
			throw new DataAccessError(e.getMessage(), e.getCause());
		} finally {
			jdbcConnectionPool.releaseConnection(connection);
		}
	
	}
	
}
